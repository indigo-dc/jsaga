package fr.in2p3.jsaga.adaptor.job.control;

import fr.in2p3.jsaga.adaptor.job.JobAdaptor;
import org.ogf.saga.error.*;

/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   JobControlAdaptor
* Author: Sylvain Reynaud (sreynaud@in2p3.fr)
* Date:   18 juin 2007
* ***************************************************
* Description:                                      */
/**
 *
 */
public interface JobControlAdaptor extends JobAdaptor {
    public static final String MONITOR_SERVICE_URL = "MonitorServiceUrl";

    /**
     * submit a job
     * @param jobDesc the job description in the language supported by the targeted grid
     * @param checkMatch if true then check if job description matches job service before submitting job
     * @return the identifier of the job in the grid
     */
    public String submit(String jobDesc, boolean checkMatch) throws PermissionDenied, Timeout, NoSuccess;

    /**
     * cancel a job
     * @param nativeJobId the identifier of the job in the grid
     */
    public void cancel(String nativeJobId) throws PermissionDenied, Timeout, NoSuccess;
}
