package fr.in2p3.jsaga.engine.data.copy;

import fr.in2p3.jsaga.engine.data.flags.FlagsBytes;
import fr.in2p3.jsaga.engine.data.flags.FlagsBytesPhysical;
import org.ogf.saga.url.URL;
import org.ogf.saga.error.*;
import org.ogf.saga.logicalfile.LogicalFile;
import org.ogf.saga.logicalfile.LogicalFileFactory;
import org.ogf.saga.namespace.*;
import org.ogf.saga.session.Session;

import java.util.List;

/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   SourceLogicalFile
* Author: Sylvain Reynaud (sreynaud@in2p3.fr)
* Date:   28 ao�t 2007
* ***************************************************
* Description:                                      */
/**
 * TODO: remove this dead code
 */
public class SourceLogicalFile {
    private LogicalFile m_sourceFile;

    public SourceLogicalFile(LogicalFile sourceFile) {
        m_sourceFile = sourceFile;
    }

    public void putToPhysicalFile(Session session, URL target, FlagsBytes targetFlags) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, DoesNotExist, AlreadyExists, Timeout, NoSuccess, IncorrectURL {
        // get location of source entry (may be logical or physical
        List<URL> sourceLocations = m_sourceFile.listLocations();
        if (sourceLocations!=null && sourceLocations.size()>0) {
            // open source entry
            URL source = sourceLocations.get(0);
            NSEntry sourceEntry = createSourceNSEntry(session, source);

            // copy
            sourceEntry.copy(target, targetFlags.remove(Flags.NONE));

            // close source entry (but not the source logical file)
            sourceEntry.close();
        } else {
            throw new NoSuccess("No location found for logical file: "+m_sourceFile.getURL());
        }
    }

    public void putToLogicalFile(Session session, URL target, FlagsBytes targetFlags) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, Timeout, NoSuccess, IncorrectURL {
        // get location of source physical file
        List<URL> sourceLocations = m_sourceFile.listLocations();
        if (sourceLocations!=null && sourceLocations.size()>0) {
            // open target logical file
            LogicalFile targetLogicalFile = createTargetLogicalFile(session, target, targetFlags);
            try {
                // copy
                if (targetFlags.contains(Flags.OVERWRITE)) {
                    // remove all target locations
                    try {
                        List<URL> targetLocations = targetLogicalFile.listLocations();
                        for (int i=0; targetLocations !=null && i< targetLocations.size(); i++) {
                            targetLogicalFile.removeLocation(targetLocations.get(i));
                        }
                    } catch(IncorrectState e) {
                        // ignore if target logical file does not exist
                    }
                }
                // add all source locations
                for (int i=0; sourceLocations!=null && i<sourceLocations.size(); i++) {
                    targetLogicalFile.addLocation(sourceLocations.get(i));
                }
            } catch (DoesNotExist e) {
                throw new NoSuccess("Unexpected exception: DoesNotExist", e);
            } finally {
                // close target
                targetLogicalFile.close();
            }
        }
    }

    public static NSEntry createSourceNSEntry(Session session, URL source) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, Timeout, NoSuccess, IncorrectURL {
        try {
            return NSFactory.createNSEntry(session, source, Flags.NONE.getValue());
        } catch (AlreadyExists e) {
            throw new NoSuccess("Unexpected exception: AlreadyExists");
        } catch (DoesNotExist doesNotExist) {
            throw new IncorrectState("Source physical file does not exist: "+source, doesNotExist);
        }
    }

    public static LogicalFile createTargetLogicalFile(Session session, URL target, FlagsBytes flags) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, Timeout, NoSuccess, IncorrectURL {
        FlagsBytes correctedBytes = flags.or(FlagsBytesPhysical.WRITE).or(FlagsBytes.CREATE);
        int correctedFlags =
                (correctedBytes.contains(Flags.OVERWRITE)
                        ? correctedBytes.remove(Flags.OVERWRITE)
                        : correctedBytes.add(Flags.EXCL));
        try {
            return LogicalFileFactory.createLogicalFile(session, target, correctedFlags);
        } catch (DoesNotExist e) {
            throw new NoSuccess("Unexpected exception: DoesNotExist", e);
        } catch (AlreadyExists alreadyExists) {
            throw new AlreadyExists("Target entry already exists: "+target, alreadyExists.getCause());
        }
    }
}
