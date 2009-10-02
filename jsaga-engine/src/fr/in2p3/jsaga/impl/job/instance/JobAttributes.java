package fr.in2p3.jsaga.impl.job.instance;

import fr.in2p3.jsaga.impl.attributes.AttributeImpl;
import fr.in2p3.jsaga.impl.attributes.VectorAttributeImpl;
import fr.in2p3.jsaga.impl.monitoring.MetricMode;
import fr.in2p3.jsaga.impl.monitoring.MetricType;
import org.ogf.saga.error.*;
import org.ogf.saga.job.Job;

import java.util.Date;

/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   JobAttributes
* Author: Sylvain Reynaud (sreynaud@in2p3.fr)
* Date:   18 janv. 2008
* ***************************************************
* Description:                                      */
/**
 *
 */
public class JobAttributes implements Cloneable {
    AttributeImpl<String> m_JobId;
    VectorAttributeImpl<String> m_ExecutionHosts;
    AttributeImpl<Date> m_Created;
    AttributeImpl<Date> m_Started;
    AttributeImpl<Date> m_Finished;
    AttributeImpl<Integer> m_ExitCode;
    /** deviation from SAGA specification */
    AttributeImpl<String> m_NativeJobDescription;

    /** constructor */
    JobAttributes(final AbstractSyncJobImpl job) {
        m_JobId = job._addAttribute(new AttributeImpl<String>(
                Job.JOBID,
                "SAGA representation of the job identifier",
                MetricMode.ReadOnly,
                MetricType.String,
                null));
        m_ExecutionHosts = job._addVectorAttribute(new VectorAttributeImpl<String>(
                Job.EXECUTIONHOSTS,
                "list of host names or IP addresses allocated to run this job",
                MetricMode.ReadOnly,
                MetricType.String,
                null) {
            public String[] getValues() throws NotImplementedException, IncorrectStateException, NoSuccessException {
                String[] result = job.getJobInfoAdaptor().getExecutionHosts(job.getNativeJobId());
                if (result != null) {
                    return result;
                } else {
                    throw new IncorrectStateException("Attribute may not be initialized yet: "+Job.EXECUTIONHOSTS);
                }
            }
        });
        m_Created = job._addAttribute(new AttributeImpl<Date>(
                Job.CREATED,
                "time stamp of the job creation in the resource manager",
                MetricMode.ReadOnly,
                MetricType.Time,
                new Date(System.currentTimeMillis())) {
            public String getValue() throws NotImplementedException, IncorrectStateException, NoSuccessException {
                Date result = job.getJobInfoAdaptor().getCreated(job.getNativeJobId());
                if (result != null) {
                    return result.toString();
                } else {
                    throw new IncorrectStateException("Attribute may not be initialized yet: "+Job.CREATED);
                }
            }
        });
        m_Started = job._addAttribute(new AttributeImpl<Date>(
                Job.STARTED,
                "time stamp indicating when the job started running",
                MetricMode.ReadOnly,
                MetricType.Time,
                null) {
            public String getValue() throws NotImplementedException, IncorrectStateException, NoSuccessException {
                Date result = job.getJobInfoAdaptor().getStarted(job.getNativeJobId());
                if (result != null) {
                    return result.toString();
                } else {
                    throw new IncorrectStateException("Attribute may not be initialized yet: "+Job.STARTED);
                }
            }
        });
        m_Finished = job._addAttribute(new AttributeImpl<Date>(
                Job.FINISHED,
                "time stamp indicating when the job completed",
                MetricMode.ReadOnly,
                MetricType.Time,
                null) {
            public String getValue() throws NotImplementedException, IncorrectStateException, NoSuccessException {
                Date result = job.getJobInfoAdaptor().getFinished(job.getNativeJobId());
                if (result != null) {
                    return result.toString();
                } else {
                    throw new IncorrectStateException("Attribute may not be initialized yet: "+Job.FINISHED);
                }
            }
        });
        m_ExitCode = job._addAttribute(new AttributeImpl<Integer>(
                Job.EXITCODE,
                "process exit code",
                MetricMode.ReadOnly,
                MetricType.Int,
                null) {
            public String getValue() throws NotImplementedException, IncorrectStateException, NoSuccessException {
                Integer result = job.getJobInfoAdaptor().getExitCode(job.getNativeJobId());
                if (result != null) {
                    return result.toString();
                } else {
                    throw new IncorrectStateException("Attribute may not be initialized yet: "+Job.EXITCODE);
                }
            }
        });
        m_NativeJobDescription = job._addAttribute(new AttributeImpl<String>(
                AbstractSyncJobImpl.NATIVEJOBDESCRIPTION,
                "job description understood by the job service (deviation from SAGA specification)",
                MetricMode.ReadOnly,
                MetricType.String,
                null));
    }

    /** clone */
    public JobAttributes clone() throws CloneNotSupportedException {
        JobAttributes clone = (JobAttributes) super.clone();
        clone.m_JobId = m_JobId.clone();
        clone.m_ExecutionHosts = m_ExecutionHosts.clone();
        clone.m_Created = m_Created.clone();
        clone.m_Started = m_Started.clone();
        clone.m_Finished = m_Finished.clone();
        clone.m_ExitCode = m_ExitCode.clone();
        clone.m_NativeJobDescription = m_NativeJobDescription.clone();
        return clone;
    }
}
