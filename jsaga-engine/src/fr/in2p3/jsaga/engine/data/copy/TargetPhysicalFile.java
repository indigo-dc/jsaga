package fr.in2p3.jsaga.engine.data.copy;

import fr.in2p3.jsaga.engine.data.flags.FlagsBytes;
import fr.in2p3.jsaga.impl.file.FileImpl;
import org.ogf.saga.url.URL;
import org.ogf.saga.error.*;
import org.ogf.saga.file.*;
import org.ogf.saga.logicalfile.LogicalFile;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.session.Session;

import java.io.IOException;
import java.lang.Exception;
import java.util.List;

/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   TargetPhysicalFile
* Author: Sylvain Reynaud (sreynaud@in2p3.fr)
* Date:   28 ao�t 2007
* ***************************************************
* Description:                                      */
/**
 * TODO: remove this dead code
 */
public class TargetPhysicalFile {
    private static final int DEFAULT_BUFFER_SIZE = 16384;
    private FileImpl m_targetFile;

    public TargetPhysicalFile(FileImpl targetFile) {
        m_targetFile = targetFile;
    }

    public void getFromPhysicalFile(Session session, URL source, FlagsBytes sourceFlags) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, DoesNotExist, Timeout, NoSuccess, IncorrectURL {
        IOException closingException = null;

        // open source file if it exists
        FileImpl sourceFile = createSourceFile(session, source);
        FileInputStream in = sourceFile.getFileInputStream();

        // start to read if it exists
        byte[] data = new byte[DEFAULT_BUFFER_SIZE];
        int readlen;
        try {
            readlen = in.read(data, 0, data.length);
        } catch (IOException e) {
            try {
                sourceFile.close();
            } catch (Exception e1) {/*ignore*/}
            throw new DoesNotExist("Source file does not exist: "+source, e);
        }

        // open target file and copy
        try {
            // sourceFlags may contains OVERWRITE flag for target
            boolean overwrite = sourceFlags.contains(Flags.OVERWRITE);
            FileOutputStream out = m_targetFile.newFileOutputStream(overwrite);
            try {
                while (readlen > 0) {
                    int writelen;
                    for (int total=0; total<readlen; total+=writelen) {
                        writelen = readlen - total;
                        if (total > 0) {
                            byte[] dataBis = new byte[writelen];
                            System.arraycopy(data, total, dataBis, 0, writelen);
                            out.write(dataBis, 0, writelen);
                        } else {
                            out.write(data, 0, readlen);
                        }
                    }
                    readlen = in.read(data,0, data.length);
                }
            } catch (IOException e) {
                throw new Timeout(e);
            } finally {
                try {
                    out.close();
                } catch (IOException e) {
                    closingException = e;
                }
            }
        } catch (AlreadyExists alreadyExists) {
            throw new IncorrectState("Target file already exists: "+m_targetFile.getURL(), alreadyExists);
        } finally {
            try {
                sourceFile.close();
            } catch (Exception e) {
                closingException = new IOException(e.getClass().getName()+": "+e.getMessage());
            }
        }
        if (closingException != null) {
            throw new IncorrectState(closingException);
        }
    }

    public void getFromLogicalFile(Session session, URL source, FlagsBytes sourceFlags) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, DoesNotExist, Timeout, NoSuccess, IncorrectURL {
        // get location of source physical file
        LogicalFile sourceLogicalFile = TargetLogicalFile.createSourceLogicalFile(session, source, sourceFlags);
        List<URL> sourceLocations = sourceLogicalFile.listLocations();
        if (sourceLocations!=null && sourceLocations.size()>0) {
            // get source physical file
            URL sourcePhysicalUrl = sourceLocations.get(0);
            // copy
            m_targetFile.copyFrom(sourcePhysicalUrl, sourceFlags.remove(Flags.NONE));
            // close source logical file
            sourceLogicalFile.close();
        } else {
            throw new NoSuccess("No location found for logical file: "+source);
        }
    }

    public static FileImpl createSourceFile(Session session, URL source) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, DoesNotExist, Timeout, NoSuccess, IncorrectURL {
        try {
            return (FileImpl) FileFactory.createFile(session, source, Flags.READ.getValue());
        } catch (AlreadyExists e) {
            throw new NoSuccess("Unexpected exception: DoesNotExist", e);
        } catch (DoesNotExist doesNotExist) {
            throw new DoesNotExist("Source file does not exist: "+source, doesNotExist.getCause());
        }
    }
}
