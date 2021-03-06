package fr.in2p3.jsaga.adaptor.job.monitor;

import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.TimeoutException;

/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   QueryIndividualJob
* Author: Sylvain Reynaud (sreynaud@in2p3.fr)
* Date:   14 juin 2007
* ***************************************************
* Description:                                      */
/**
 *
 */
public interface QueryIndividualJob extends QueryJob {
    /**
     * Get the status of the job matching identifier nativeJobId.
     * @param nativeJobId the identifier of the job in the grid
     * @return the status of the job.
     */
    public JobStatus getStatus(String nativeJobId) throws TimeoutException, NoSuccessException;
}
