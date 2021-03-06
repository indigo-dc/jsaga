package integration;

import java.net.URL;

import org.glite.ce.creamapi.ws.cream2.CREAMStub;
import org.glite.ce.creamapi.ws.cream2.CREAMStub.JobFilter;
import org.glite.ce.creamapi.ws.cream2.CREAMStub.JobPurgeRequest;
import org.glite.ce.creamapi.ws.cream2.CREAMStub.Result;
import org.glite.ce.security.delegation.DelegationServiceStub;
import org.glite.ce.security.delegation.DelegationServiceStub.Destroy;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.job.Job;
import org.ogf.saga.job.JobDescription;
import org.ogf.saga.job.ListTest;
import org.ogf.saga.job.description.DescriptionTest;
import org.ogf.saga.job.run.InfoTest;
import org.ogf.saga.job.run.InteractiveTest;
import org.ogf.saga.job.run.MinimalTest;
import org.ogf.saga.job.run.OptionalTest;
import org.ogf.saga.job.run.RequiredTest;
import org.ogf.saga.job.run.RequirementsTest;
import org.ogf.saga.job.run.SandboxTest;
import org.ogf.saga.task.State;

/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   CreamExecutionTestSuite
* Author:
* Date:
* ***************************************************
* Description:                                      */
/**
 *
 */
@RunWith(Suite.class)
@SuiteClasses({
    CreamExecutionTestSuite.CreamJobRunRequiredTest.class,
    CreamExecutionTestSuite.CreamJobRunOptionalTest.class,
    CreamExecutionTestSuite.CreamJobDescriptionTest.class,
    CreamExecutionTestSuite.CreamJobRunDescriptionTest.class,
    CreamExecutionTestSuite.CreamJobRunSandboxTest.class,
    CreamExecutionTestSuite.CreamJobRunInteractiveTest.class,
    CreamExecutionTestSuite.CreamJobRunInfoTest.class
    })

public class CreamExecutionTestSuite {

    private final static String TYPE = "cream";

    /** test cases */
    public static class CreamJobRunMinimalTest extends MinimalTest {
        public CreamJobRunMinimalTest() throws Exception {super(TYPE);}
    }

    // test cases
    public static class CreamJobRunRequiredTest extends RequiredTest {
        public CreamJobRunRequiredTest() throws Exception {super(TYPE);}
    }
    
   public static class CreamJobDescriptionTest extends DescriptionTest {
        public CreamJobDescriptionTest() throws Exception {super(TYPE);}
        
        @Test @Ignore("Not supported")
        public void test_totalCPUCount() throws Exception {}
        @Test @Ignore("Not supported")
        public void test_workingDirectory() throws Exception { }
        @Test @Ignore("Not supported")
        public void test_threadsPerProcess() throws Exception { }
        @Test @Ignore("Not supported")
        public void test_cleanup() throws Exception { }
        @Test @Ignore("Not supported")
        public void test_candidateHosts() throws Exception {}
        @Test @Ignore("SAGA queue is override by Queue in URL")
        public void test_queue() throws Exception {}
     }

    public static class CreamJobRunDescriptionTest extends RequirementsTest {
        public CreamJobRunDescriptionTest() throws Exception {super(TYPE);}
        @Test @Ignore("Not supported")
        public void test_run_inWorkingDirectory() { }
        @Test @Ignore("SAGA queue is override by Queue in URL")
        public void test_run_queueRequirement() throws Exception { }
    }

    public static class CreamJobRunOptionalTest extends OptionalTest {
        public CreamJobRunOptionalTest() throws Exception {super(TYPE);}
        
        /**
         * Runs a long job, waits for done state and resumes it
         * Cream send UnknownJob instead of Invalid
         */
        @Override
        @Test(expected=IncorrectStateException.class)
        public void test_resume_done() throws Exception {
            super.test_resume_done();
        }
        
        /**
         * Runs a long job, waits for running state and suspends it
         * Cream send UnknownJob instead of Invalid
         */
        @Override
        @Test(expected=IncorrectStateException.class)
        public void test_suspend_done() throws Exception {
            super.test_suspend_done();
        }

    }

    public static class CreamJobRunSandboxTest extends SandboxTest {
        public CreamJobRunSandboxTest() throws Exception {super(TYPE);}
        @Test @Ignore("Not supported")
        public void test_output_workingDirectory() throws Exception {  }
        
        @Test
        public void rfc3820_proxy_delegation() throws Exception {
            super.test_output_only_implicit();
        }
    }
    
    public static class CreamJobRunInteractiveTest extends InteractiveTest {
        public CreamJobRunInteractiveTest() throws Exception {super(TYPE);}
        @Test @Ignore("Not supported")
        public void test_run_environnement() throws Exception { }
    }

    public static class CreamJobRunInfoTest extends InfoTest {
        public CreamJobRunInfoTest() throws Exception {super(TYPE);}
    }

    public static class CreamJobListTest extends ListTest {
        public CreamJobListTest() throws Exception {super("cream");}
    }

    public static class CreamAlternativeTest extends CreamAbstractTest {
        public CreamAlternativeTest() throws Exception {  super();}
        
        // TODO: use CreamClient to do this
        @Test
        public void test_purge_jobs() throws Exception {
            // set filter
            JobFilter filter = new JobFilter();
            if (m_delegationId != null) {
                filter.setDelegationId(m_delegationId);
            }
            JobPurgeRequest jpr = new JobPurgeRequest();
            jpr.setJobPurgeRequest(filter);
            String url = new java.net.URL("https", m_url.getHost(), m_url.getPort(), "/ce-cream/services/CREAM2").toString();
            CREAMStub creamStub = new CREAMStub(url);
            try {
                Result[] resultArray = creamStub.jobPurge(jpr).getJobPurgeResponse().getResult();
                System.out.println(resultArray.length+" have been purged!");
            } catch (NullPointerException npe) {
                System.out.println("no jobs have been purged!");
            }
        }
        @Test
        public void test_destroy_delegation() throws Exception {
            DelegationServiceStub stub = new DelegationServiceStub(new URL("https", m_url.getHost(), m_url.getPort(), "/ce-cream/services/gridsite-delegation").toString());
            Destroy destroy = new Destroy();
            destroy.setDelegationID(m_delegationId);
            stub.destroy(destroy);
        }
    }
}
