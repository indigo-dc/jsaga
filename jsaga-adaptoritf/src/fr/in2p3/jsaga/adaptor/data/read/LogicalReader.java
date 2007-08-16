package fr.in2p3.jsaga.adaptor.data.read;

import org.ogf.saga.error.*;

/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   LogicalReader
* Author: Sylvain Reynaud (sreynaud@in2p3.fr)
* Date:   15 juin 2007
* ***************************************************
* Description:                                      */
/**
 *
 */
public interface LogicalReader extends DataReaderAdaptor {
    /**
     * List the locations in the location set.
     * @param logicalEntry absolute path of the logical entry.
     * @return array of locations in set.
     * @throws IncorrectState if <code>absolutePath</code> does not exist.
     */
    public String[] listLocations(String logicalEntry)
        throws AuthenticationFailed, AuthorizationFailed, PermissionDenied, IncorrectState, Timeout, NoSuccess;
}
