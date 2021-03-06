package fr.in2p3.jsaga.adaptor.job.monitor;

import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.TimeoutException;

/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   QueryListJob
* Author: Sylvain Reynaud (sreynaud@in2p3.fr)
* Date:   14 juin 2007
* ***************************************************
* Description:                                      */
/**
 *
 */
public interface QueryListJob extends QueryJob {
    /**
     * Get the status of the jobs, which identifier is contained in nativeJobIdArray.
     * @param nativeJobIdArray the array of identifiers of the jobs in the grid
     * @return the status of listed jobs.
     */
    public JobStatus[] getStatusList(String[] nativeJobIdArray) throws TimeoutException, NoSuccessException;
}
