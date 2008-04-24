package fr.in2p3.jsaga.adaptor.data;

import fr.in2p3.jsaga.adaptor.base.defaults.Default;
import fr.in2p3.jsaga.adaptor.base.usage.Usage;
import fr.in2p3.jsaga.adaptor.data.optimise.DataCopy;
import fr.in2p3.jsaga.adaptor.data.optimise.DataRename;
import fr.in2p3.jsaga.adaptor.data.read.FileAttributes;
import fr.in2p3.jsaga.adaptor.data.read.FileReaderGetter;
import fr.in2p3.jsaga.adaptor.data.write.FileWriterPutter;
import fr.in2p3.jsaga.adaptor.security.SecurityAdaptor;
import fr.in2p3.jsaga.adaptor.security.impl.GSSCredentialSecurityAdaptor;
import org.globus.common.ChainedIOException;
import org.globus.ftp.*;
import org.globus.ftp.exception.*;
import org.globus.gsi.gssapi.GlobusGSSException;
import org.globus.gsi.gssapi.auth.HostAuthorization;
import org.ietf.jgss.GSSCredential;
import org.ogf.saga.error.*;

import java.io.*;
import java.lang.Exception;
import java.util.Map;

/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   GsiftpDataAdaptorAbstract
* Author: Sylvain Reynaud (sreynaud@in2p3.fr)
* Date:   20 juil. 2007
* ***************************************************
* Description:                                      */
/**
 *
 */
public abstract class GsiftpDataAdaptorAbstract implements FileReaderGetter, FileWriterPutter, DataCopy, DataRename {
    protected GSSCredential m_credential;
    protected GridFTPClient m_client;

    public abstract String getType();
    public abstract Usage getUsage();
    public abstract Default[] getDefaults(Map attributes) throws IncorrectState;
    public abstract boolean isDirectory(String absolutePath, String additionalArgs) throws PermissionDenied, DoesNotExist, Timeout, NoSuccess;
    public abstract FileAttributes[] listAttributes(String absolutePath, String additionalArgs) throws PermissionDenied, DoesNotExist, Timeout, NoSuccess;

    public Class[] getSupportedSecurityAdaptorClasses() {
        return new Class[]{GSSCredentialSecurityAdaptor.class};
    }

    public void setSecurityAdaptor(SecurityAdaptor securityAdaptor) {
        m_credential = ((GSSCredentialSecurityAdaptor) securityAdaptor).getGSSCredential();
    }

    public BaseURL getBaseURL() throws IncorrectURL {
        return new BaseURL(2811);
    }

    public void connect(String userInfo, String host, int port, String basePath, Map attributes) throws AuthenticationFailed, AuthorizationFailed, BadParameter, Timeout, NoSuccess {
        try {
            m_client = new GridFTPClient(host, port);
            m_client.setAuthorization(HostAuthorization.getInstance());
            m_client.authenticate(m_credential);
        } catch (ChainedIOException e) {
            try {
                throw e.getException();
            } catch (GlobusGSSException gssException) {
                throw new AuthenticationFailed(gssException);
            } catch (Throwable throwable) {
                throw new Timeout(throwable);
            }
        } catch (IOException e) {
            if (e.getMessage()!=null && e.getMessage().indexOf("Authentication") > -1) {
                throw new AuthenticationFailed(e);
            } else {
                throw new Timeout(e);
            }
        } catch (ServerException e) {
            switch(e.getCode()) {
                case ServerException.SERVER_REFUSED:
                    try {
                        throw e.getRootCause();
                    } catch (UnexpectedReplyCodeException unexpectedReplyCode) {
                        switch(unexpectedReplyCode.getReply().getCode()) {
                            case 530:
                                throw new AuthorizationFailed(unexpectedReplyCode);
                            default:
                                throw new NoSuccess(unexpectedReplyCode);
                        }
                    } catch (Exception e1) {
                        throw new NoSuccess(e1);
                    }
            }
        }
    }

    public void disconnect() throws NoSuccess {
        try {
            m_client.close();
        } catch (Exception e) {
            throw new NoSuccess(e);
        }
    }

    public boolean exists(String absolutePath, String additionalArgs) throws PermissionDenied, Timeout, NoSuccess {
        try {
            return m_client.exists(absolutePath);
        } catch (Exception e) {
            try {
                throw rethrowException(e);
            } catch (DoesNotExist doesNotExist) {
                throw new NoSuccess(e);
            } catch (BadParameter badParameter) {
                throw new NoSuccess("Unexpected exception", e);
            }
        }
    }

    public boolean isEntry(String absolutePath, String additionalArgs) throws PermissionDenied, DoesNotExist, Timeout, NoSuccess {
        return !isDirectory(absolutePath, additionalArgs);
    }

    public long getSize(String absolutePath, String additionalArgs) throws PermissionDenied, BadParameter, DoesNotExist, Timeout, NoSuccess {
        try {
            return m_client.getSize(absolutePath);
        } catch (Exception e) {
            throw rethrowException(e);
        }
    }

    public void getToStream(String absolutePath, String additionalArgs, OutputStream stream) throws PermissionDenied, BadParameter, DoesNotExist, Timeout, NoSuccess {
        final boolean autoFlush = false;
        final boolean ignoreOffset = true;
        try {
            m_client.setType(GridFTPSession.TYPE_IMAGE);
            m_client.setMode(GridFTPSession.MODE_STREAM); //MODE_EBLOCK induce error: "451 refusing to store with active mode"
            m_client.setPassive();
            m_client.setLocalActive();
            m_client.get(
                    absolutePath,
                    new DataSinkStream(stream, autoFlush, ignoreOffset),
                    null);
        } catch (Exception e) {
            throw rethrowException(e);
        }
    }

    public void putFromStream(String absolutePath, boolean append, String additionalArgs, InputStream stream) throws PermissionDenied, BadParameter, AlreadyExists, ParentDoesNotExist, Timeout, NoSuccess {
        final int DEFAULT_BUFFER_SIZE = 16384;
        try {
            m_client.setType(GridFTPSession.TYPE_IMAGE);
            m_client.setMode(GridFTPSession.MODE_EBLOCK);
            m_client.setPassive();
            m_client.setLocalActive();
            m_client.put(
                absolutePath,
                new DataSourceStream(stream, DEFAULT_BUFFER_SIZE),
                    null,
                    append);
        } catch (Exception e) {
            try {
                throw rethrowExceptionFull(e);
            } catch (DoesNotExist e2) {
                throw new ParentDoesNotExist(e);
            }
        }
    }

    public void copy(String sourceAbsolutePath, String targetHost, int targetPort, String targetAbsolutePath, boolean overwrite, String additionalArgs) throws AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, AlreadyExists, DoesNotExist, ParentDoesNotExist, Timeout, NoSuccess {
        // connect to peer server
        GsiftpDataAdaptorAbstract targetAdaptor = new Gsiftp1DataAdaptor();
        targetAdaptor.m_credential = m_credential;
        targetAdaptor.connect(null, targetHost, targetPort, null, null);

        //todo: remove this block when overwriting target file will work (it only works with UrlCopy)
        if (overwrite && targetAdaptor.exists(targetAbsolutePath, additionalArgs)) {
            try {
                targetAdaptor.m_client.deleteFile(targetAbsolutePath);
            } catch (Exception e) {
                throw new PermissionDenied("Failed to overwrite target file", e);
            }
        }

        // need to check existence of target explicitely, else exception is never thrown
        if (!overwrite && targetAdaptor.exists(targetAbsolutePath, additionalArgs)) {
            throw new AlreadyExists("File already exists");
        }

        try {
            // for compatibility with VDT-1.6, .NET implementation
            m_client.setDataChannelAuthentication(DataChannelAuthentication.NONE);
            targetAdaptor.m_client.setDataChannelAuthentication(DataChannelAuthentication.NONE);

            // transfer file
            m_client.setType(GridFTPSession.TYPE_IMAGE);
            targetAdaptor.m_client.setType(GridFTPSession.TYPE_IMAGE);
            m_client.setMode(GridFTPSession.MODE_EBLOCK);
            targetAdaptor.m_client.setMode(GridFTPSession.MODE_EBLOCK);
            m_client.setStripedActive(targetAdaptor.m_client.setStripedPassive());
            m_client.extendedTransfer(sourceAbsolutePath, targetAdaptor.m_client, targetAbsolutePath, null);
        } catch (Exception e) {
            throw rethrowExceptionFull(e);
        } finally {
            // disconnect from peer server
            targetAdaptor.disconnect();
        }
    }

    public void copyFrom(String sourceHost, int sourcePort, String sourceAbsolutePath, String targetAbsolutePath, boolean overwrite, String additionalArgs) throws AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        // connect to peer server
        GsiftpDataAdaptorAbstract sourceAdaptor = new Gsiftp1DataAdaptor();
        sourceAdaptor.m_credential = m_credential;
        sourceAdaptor.connect(null, sourceHost, sourcePort, null, null);

        //todo: remove this block when overwriting target file will work (it only works with UrlCopy)
        if (overwrite && this.exists(targetAbsolutePath, additionalArgs)) {
            try {
                m_client.deleteFile(targetAbsolutePath);
            } catch (Exception e) {
                throw new PermissionDenied("Failed to overwrite target file", e);
            }
        }

        // need to check existence of target explicitely, else exception is never thrown
        if (!overwrite && this.exists(targetAbsolutePath, additionalArgs)) {
            throw new AlreadyExists("File already exists");
        }

        try {
            // for compatibility with VDT-1.6, .NET implementation
            sourceAdaptor.m_client.setDataChannelAuthentication(DataChannelAuthentication.NONE);
            m_client.setDataChannelAuthentication(DataChannelAuthentication.NONE);

            // transfer file
            sourceAdaptor.m_client.setType(GridFTPSession.TYPE_IMAGE);
            m_client.setType(GridFTPSession.TYPE_IMAGE);
            sourceAdaptor.m_client.setMode(GridFTPSession.MODE_EBLOCK);
            m_client.setMode(GridFTPSession.MODE_EBLOCK);
            sourceAdaptor.m_client.setStripedActive(m_client.setStripedPassive());
            sourceAdaptor.m_client.extendedTransfer(sourceAbsolutePath, m_client, targetAbsolutePath, null);
        } catch (Exception e) {
            throw rethrowExceptionFull(e);
        } finally {
            // disconnect from peer server
            sourceAdaptor.disconnect();
        }
    }

    public void rename(String sourceAbsolutePath, String targetAbsolutePath, boolean overwrite, String additionalArgs) throws PermissionDenied, BadParameter, DoesNotExist, AlreadyExists, Timeout, NoSuccess {
        try {
            m_client.rename(sourceAbsolutePath, targetAbsolutePath);
        } catch (Exception e) {
            throw rethrowExceptionFull(e);
        }
    }

    public void removeFile(String parentAbsolutePath, String fileName, String additionalArgs) throws PermissionDenied, BadParameter, DoesNotExist, Timeout, NoSuccess {
        try {
            m_client.deleteFile(parentAbsolutePath+"/"+fileName);
        } catch (Exception e) {
            throw rethrowException(e);
        }
    }

    public void makeDir(String parentAbsolutePath, String directoryName, String additionalArgs) throws PermissionDenied, BadParameter, AlreadyExists, ParentDoesNotExist, Timeout, NoSuccess {
        try {
            m_client.makeDir(parentAbsolutePath+"/"+directoryName);
        } catch (Exception e) {
            try {
                throw rethrowExceptionFull(e);
            } catch (DoesNotExist e2) {
                throw new ParentDoesNotExist(e);
            }
        }
    }

    public void removeDir(String parentAbsolutePath, String directoryName, String additionalArgs) throws PermissionDenied, BadParameter, DoesNotExist, Timeout, NoSuccess {
        try {
            m_client.deleteDir(parentAbsolutePath+"/"+directoryName);
        } catch (Exception e) {
            throw rethrowException(e);
        }
    }

    protected NoSuccess rethrowException(Exception exception) throws PermissionDenied, BadParameter, DoesNotExist, Timeout, NoSuccess {
        try {
            throw rethrowExceptionFull(exception);
        } catch (AlreadyExists e) {
            throw new NoSuccess(e);
        }
    }

    private NoSuccess rethrowExceptionFull(Exception exception) throws PermissionDenied, BadParameter, DoesNotExist, AlreadyExists, Timeout, NoSuccess {
        try {
            throw exception;
        } catch (IllegalArgumentException e) {
            throw new BadParameter(e);
        } catch (IOException e) {
            throw new Timeout(e);
        } catch (ServerException e) {
            switch(e.getCode()) {
                case ServerException.SERVER_REFUSED:
                    try {
                        throw e.getRootCause();
                    } catch (UnexpectedReplyCodeException unexpectedReplyCode) {
                        switch(unexpectedReplyCode.getReply().getCode()) {
                            case 112:
                                throw new Timeout(e);
                            case 500:
                            case 521:
                            case 550:
                                this.rethrowParsedException(unexpectedReplyCode);
                            default:
                                throw new NoSuccess(e);
                        }
                    } catch (Exception e1) {
                        throw new PermissionDenied(e1);
                    }
                case ServerException.REPLY_TIMEOUT:             throw new Timeout(e);
                default:                                        throw new NoSuccess(e);
            }
        } catch (ClientException e) {
            switch(e.getCode()) {
                case ClientException.NOT_AUTHORIZED:            throw new PermissionDenied(e);
                case ClientException.REPLY_TIMEOUT:             throw new Timeout(e);
                default:                                        throw new NoSuccess(e);
            }
        } catch (Exception e) {
            throw new NoSuccess(e);
        }
    }
    protected abstract void rethrowParsedException(UnexpectedReplyCodeException e) throws DoesNotExist, AlreadyExists, PermissionDenied, NoSuccess;
}
