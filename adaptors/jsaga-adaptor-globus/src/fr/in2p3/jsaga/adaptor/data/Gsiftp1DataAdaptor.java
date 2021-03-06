package fr.in2p3.jsaga.adaptor.data;

import fr.in2p3.jsaga.adaptor.base.defaults.Default;
import fr.in2p3.jsaga.adaptor.base.usage.UOptional;
import fr.in2p3.jsaga.adaptor.base.usage.Usage;
import fr.in2p3.jsaga.adaptor.data.read.FileAttributes;
import fr.in2p3.jsaga.helpers.EntryPath;
import org.globus.ftp.FileInfo;
import org.globus.ftp.GridFTPSession;
import org.globus.ftp.exception.UnexpectedReplyCodeException;
import org.ogf.saga.error.*;

import java.util.Map;
import java.util.Vector;

/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   Gsiftp1DataAdaptor
* Author: Sylvain Reynaud (sreynaud@in2p3.fr)
* Date:   20 aout 2007
* ***************************************************
* Description:                                      */
/**
 *
 */
public class Gsiftp1DataAdaptor extends GsiftpDataAdaptorAbstract {
    public String getType() {
        return "gsiftp-v1";
    }

    /** setting protection level is not supported */
    public Usage getUsage() {
        return new UOptional(TCP_BUFFER_SIZE);
    }

    /** setting protection level is not supported */
    public Default[] getDefaults(Map attributes) throws IncorrectStateException {
        return null;
    }

    /** MLSD command is not supported */
    public FileAttributes getAttributes(String absolutePath, String additionalArgs) throws PermissionDeniedException, DoesNotExistException, TimeoutException, NoSuccessException {
        EntryPath path = new EntryPath(absolutePath);
        String entryName = path.getEntryName();
        FileAttributes[] list = this.listAttributes(path.getBaseDir(), additionalArgs);
        for (int i=0; i<list.length; i++) {
            if (list[i].getName().equals(entryName)) {
                return list[i];
            }
        }
        throw new DoesNotExistException("Entry does not exist: "+entryName);
    }

    /** MLSD command is not supported */
    public FileAttributes[] listAttributes(String absolutePath, String additionalArgs) throws PermissionDeniedException, DoesNotExistException, TimeoutException, NoSuccessException {
        Vector v;
        try {
            m_client.setMode(GridFTPSession.MODE_STREAM);
            m_client.setPassiveMode(true);
            String sav = m_client.getCurrentDir();
            m_client.changeDir(absolutePath);
            v = m_client.list();
            m_client.changeDir(sav);
        } catch (Exception e) {
            try {
                throw rethrowException(e);
            } catch (BadParameterException badParameter) {
                throw new NoSuccessException("Unexpected exception", e);
            }
        }
        FileAttributes[] ret = new FileAttributes[v.size()];
        for (int i=0; i<v.size(); i++) {
            FileInfo entry = (FileInfo) v.get(i);
            ret[i] = new Gsiftp1FileAttributes(entry);
        }
        return ret;
    }

    protected void rethrowParsedException(UnexpectedReplyCodeException e) throws DoesNotExistException, AlreadyExistsException, PermissionDeniedException, NoSuccessException {
        String message = e.getReply().getMessage();
        if (message.indexOf("not a plain file") > -1) {
            throw new DoesNotExistException(e);
        } else if (message.indexOf("exists") > -1) {
            throw new AlreadyExistsException(e);
        } else if (message.indexOf("Permission denied") > -1) {
            throw new PermissionDeniedException(e);
        } else {
            throw new NoSuccessException(e);
        }
    }
}
