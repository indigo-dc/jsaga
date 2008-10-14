package fr.in2p3.jsaga.adaptor.data;

import fr.in2p3.jsaga.adaptor.base.defaults.Default;
import fr.in2p3.jsaga.adaptor.base.usage.Usage;
import fr.in2p3.jsaga.adaptor.data.optimise.DataCopy;
import fr.in2p3.jsaga.adaptor.data.optimise.DataRename;
import fr.in2p3.jsaga.adaptor.data.read.FileAttributes;
import fr.in2p3.jsaga.adaptor.data.read.FileReaderGetter;
import fr.in2p3.jsaga.adaptor.data.write.FileWriterPutter;
import fr.in2p3.jsaga.adaptor.security.SecurityAdaptor;
import org.globus.ftp.FeatureList;
import org.globus.ftp.exception.ServerException;
import org.ogf.saga.error.*;

import java.io.*;
import java.util.Map;

/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   GsiftpDataAdaptor
* Author: Sylvain Reynaud (sreynaud@in2p3.fr)
* Date:   20 juil. 2007
* ***************************************************
* Description:                                      */
/**
 *
 */
public class GsiftpDataAdaptor implements FileReaderGetter, FileWriterPutter, DataCopy, DataRename {
    private GsiftpDataAdaptorAbstract m_adaptor;

    public GsiftpDataAdaptor() {
        m_adaptor = new GsiftpDefaultDataAdaptor();
    }

    public String getType() {
        return "gsiftp";
    }

    public Usage getUsage() {
        return m_adaptor.getUsage();
    }

    public Default[] getDefaults(Map attributes) throws IncorrectState {
        return m_adaptor.getDefaults(attributes);
    }

    public Class[] getSupportedSecurityAdaptorClasses() {
        return m_adaptor.getSupportedSecurityAdaptorClasses();
    }

    public void setSecurityAdaptor(SecurityAdaptor securityAdaptor) {
        m_adaptor.setSecurityAdaptor(securityAdaptor);
    }

    public BaseURL getBaseURL() throws IncorrectURL {
        return m_adaptor.getBaseURL();
    }

    public void connect(String userInfo, String host, int port, String basePath, Map attributes) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, BadParameter, Timeout, NoSuccess {
        // connect
        m_adaptor.connect(userInfo, host, port, null, attributes);

        // check version
        FeatureList fl;
        try {
            fl = m_adaptor.m_client.getFeatureList();
        } catch (IOException e) {
            throw new NoSuccess(e);
        } catch (ServerException e) {
            throw new NoSuccess(e);
        }

        // replace implementation
        GsiftpDataAdaptorAbstract sav = m_adaptor;
        if (fl.contains("PARALLEL") && fl.contains("SIZE") && fl.contains("ERET") && fl.contains("ESTO")) {
            // <*>              = PARALLEL, SIZE, ERET, ESTO
            if (fl.contains("DCAU") && fl.contains("MDTM") && fl.contains("REST STREAM")) {
                // <globus>     = <*> + DCAU, MDTM, "REST STREAM"
                if (fl.contains("SPAS") && fl.contains("SPOR")) {
                    // <new>    = <globus> + MLST..., SPAS, SPOR, UTF8, "LANG EN"
                    m_adaptor = new Gsiftp2DataAdaptor();
                } else {
                    // <old>    = <globus>
                    m_adaptor = new Gsiftp1DataAdaptor();
                }
            } else if (fl.contains("SBUF") && fl.contains("EOF")) {
                // <oldDCache>  = <*> + SBUF + EOF
                // <newDCache>  = <*> + SBUF + EOF + GETPUT, CKSM, SCKS, MODEX
                m_adaptor = new GsiftpDCacheDataAdaptor();
            }
        } else {
            throw new NotImplemented("Unsupported server implementation");
        }
        m_adaptor.m_client = sav.m_client;
        m_adaptor.m_credential = sav.m_credential;
    }

    public void disconnect() throws NoSuccess {
        m_adaptor.disconnect();
    }

    public boolean exists(String absolutePath, String additionalArgs) throws PermissionDenied, Timeout, NoSuccess {
        return m_adaptor.exists(absolutePath, additionalArgs);
    }

    public boolean isDirectory(String absolutePath, String additionalArgs) throws PermissionDenied, DoesNotExist, Timeout, NoSuccess {
        return m_adaptor.isDirectory(absolutePath, additionalArgs);
    }

    public boolean isEntry(String absolutePath, String additionalArgs) throws PermissionDenied, DoesNotExist, Timeout, NoSuccess {
        return m_adaptor.isEntry(absolutePath, additionalArgs);
    }

    public long getSize(String absolutePath, String additionalArgs) throws PermissionDenied, BadParameter, DoesNotExist, Timeout, NoSuccess {
        return m_adaptor.getSize(absolutePath, additionalArgs);
    }

    public void getToStream(String absolutePath, String additionalArgs, OutputStream stream) throws PermissionDenied, BadParameter, DoesNotExist, Timeout, NoSuccess {
        m_adaptor.getToStream(absolutePath, additionalArgs, stream);
    }

    public void putFromStream(String absolutePath, boolean append, String additionalArgs, InputStream stream) throws PermissionDenied, BadParameter, AlreadyExists, ParentDoesNotExist, Timeout, NoSuccess {
        m_adaptor.putFromStream(absolutePath, append, additionalArgs, stream);
    }

    public void copy(String sourceAbsolutePath, String targetHost, int targetPort, String targetAbsolutePath, boolean overwrite, String additionalArgs) throws AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, AlreadyExists, DoesNotExist, ParentDoesNotExist, Timeout, NoSuccess {
        m_adaptor.copy(sourceAbsolutePath, targetHost, targetPort, targetAbsolutePath, overwrite, additionalArgs);
    }

    public void copyFrom(String sourceHost, int sourcePort, String sourceAbsolutePath, String targetAbsolutePath, boolean overwrite, String additionalArgs) throws AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        m_adaptor.copyFrom(sourceHost, sourcePort, sourceAbsolutePath, targetAbsolutePath, overwrite, additionalArgs);
    }

    public void rename(String sourceAbsolutePath, String targetAbsolutePath, boolean overwrite, String additionalArgs) throws PermissionDenied, BadParameter, DoesNotExist, AlreadyExists, Timeout, NoSuccess {
        m_adaptor.rename(sourceAbsolutePath, targetAbsolutePath, overwrite, additionalArgs);
    }

    public void removeFile(String parentAbsolutePath, String fileName, String additionalArgs) throws PermissionDenied, BadParameter, DoesNotExist, Timeout, NoSuccess {
        m_adaptor.removeFile(parentAbsolutePath, fileName, additionalArgs);
    }

    public FileAttributes getAttributes(String absolutePath, String additionalArgs) throws PermissionDenied, DoesNotExist, Timeout, NoSuccess {
        return m_adaptor.getAttributes(absolutePath, additionalArgs);
    }

    public FileAttributes[] listAttributes(String absolutePath, String additionalArgs) throws PermissionDenied, DoesNotExist, Timeout, NoSuccess {
        return m_adaptor.listAttributes(absolutePath, additionalArgs);
    }

    public void makeDir(String parentAbsolutePath, String directoryName, String additionalArgs) throws PermissionDenied, BadParameter, AlreadyExists, ParentDoesNotExist, Timeout, NoSuccess {
        m_adaptor.makeDir(parentAbsolutePath, directoryName, additionalArgs);
    }

    public void removeDir(String parentAbsolutePath, String directoryName, String additionalArgs) throws PermissionDenied, BadParameter, DoesNotExist, Timeout, NoSuccess {
        m_adaptor.removeDir(parentAbsolutePath, directoryName, additionalArgs);
    }
}
