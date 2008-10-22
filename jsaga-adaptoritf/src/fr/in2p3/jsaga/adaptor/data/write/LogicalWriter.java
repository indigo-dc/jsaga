package fr.in2p3.jsaga.adaptor.data.write;

import org.ogf.saga.url.URL;
import org.ogf.saga.error.*;

/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   LogicalWriter
* Author: Sylvain Reynaud (sreynaud@in2p3.fr)
* Date:   14 juin 2007
* ***************************************************
* Description:                                      */
/**
 *
 */
public interface LogicalWriter extends DataWriterAdaptor {
    /**
     * Add a replica location to the replica set.
     * Note: does never throw an <code>AlreadyExists</code> exception!
     * @param logicalEntry absolute path of the logical entry.
     * @param replicaEntry location to add to set.
     * @param additionalArgs adaptor specific arguments
     * @throws IncorrectState if <code>logicalEntry</code> does not exist.
     */
    public void addLocation(String logicalEntry, URL replicaEntry, String additionalArgs)
        throws PermissionDenied, IncorrectState, Timeout, NoSuccess;

    /**
     * Remove a replica location from the replica set.
     * @param logicalEntry absolute path of the logical entry.
     * @param replicaEntry replica to remove from set.
     * @param additionalArgs adaptor specific arguments
     * @throws IncorrectState if <code>logicalEntry</code> does not exist.
     * @throws DoesNotExist if the location is not in the set of replicas.
     */
    public void removeLocation(String logicalEntry, URL replicaEntry, String additionalArgs)
        throws PermissionDenied, IncorrectState, DoesNotExist, Timeout, NoSuccess;
}
