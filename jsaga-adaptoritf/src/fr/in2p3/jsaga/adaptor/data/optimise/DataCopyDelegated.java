package fr.in2p3.jsaga.adaptor.data.optimise;

import fr.in2p3.jsaga.adaptor.data.DataAdaptor;
import org.ogf.saga.url.URL;
import org.ogf.saga.error.*;

/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   DataCopyDelegated
* Author: Sylvain Reynaud (sreynaud@in2p3.fr)
* Date:   14 juin 2007
* ***************************************************
* Description:                                      */
/**
 * Delegate file transfer to a remote server
 */
public interface DataCopyDelegated extends DataAdaptor {
    /**
     * Set the server.
     * @param host host of the server.
     * @param port port of the server.
     */
    public void setTransferManagementServer(String host, int port);

    /**
     * Request a transfer task.
     * @param sourceAbsoluteUrl the path of the file to be copied.
     * @param targetAbsoluteUrl the URL of the file to copy to.
     * @param overwrite if true, then target is overwrited if it exists.
     * @param additionalArgs adaptor specific arguments
     * @throws AlreadyExistsException if targetAbsoluteUrl already exists and overwrite is false.
     * @throws DoesNotExistException if sourceAbsoluteUrl does not exist.
     */
    public void requestTransfer(URL sourceAbsoluteUrl, URL targetAbsoluteUrl, boolean overwrite, String additionalArgs) throws DoesNotExistException, AlreadyExistsException;

    /**
     * Monitor the requested transfer task.
     * @throws BadParameterException if sourceAbsoluteURI is not a file.
     */
    public void monitorTransfer() throws BadParameterException;
}
