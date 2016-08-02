package fr.in2p3.jsaga.adaptor.job;

import fr.in2p3.jsaga.adaptor.job.monitor.JobStatus;
import fr.in2p3.jsaga.adaptor.job.monitor.QueryListJob;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.TimeoutException;

/* ***************************************************
 * *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
 * ***             http://cc.in2p3.fr/             ***
 * ***************************************************
 * File:   QueryListJobAdaptor
 * Author: Sylvain Reynaud (sreynaud@in2p3.fr)
 * ***************************************************/
public class QueryListJobAdaptor extends JobAdaptorAbstract implements QueryListJob {
    public String getType() {
        return "query-list";
    }

    public JobStatus[] getStatusList(String[] nativeJobIdArray) throws TimeoutException, NoSuccessException {
        return new JobStatus[] {
                new JobStatus(nativeJobIdArray[0], null, null){
                    public String getModel() {return "TEST";}
                    public SubState getSubState() {return SubState.DONE;}
                }
        };
    }
}
