package integration;

import junit.framework.Test;
import org.ogf.saga.job.*;

/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   LocalExecutionTestSuite
* Author: Nicolas DEMESY (nicolas.demesy@bt.com)
* Date:   15 avril 2008
****************************************************/

public class LocalExecutionTestSuite extends JSAGATestSuite {
    /** create test suite */
    public static Test suite() throws Exception {return new LocalExecutionTestSuite();}
    /** index of test cases */
    public static class index extends IndexTest {public index(){super(LocalExecutionTestSuite.class);}}

    // test cases
    public static class LocalJobDescriptionTest extends JobDescriptionTest {
        public LocalJobDescriptionTest() throws Exception {super("local");}
        public void test_spmdVariation() { super.ignore("not supported"); }
        public void test_totalCPUCount() { super.ignore("not supported"); }
        public void test_numberOfProcesses() { super.ignore("not supported"); }
        public void test_processesPerHost() { super.ignore("not supported"); }
        public void test_threadsPerProcess() { super.ignore("not supported"); }
        public void test_input() { super.ignore("not supported"); }
        public void test_output() { super.ignore("not supported"); }
        public void test_error() { super.ignore("not supported"); }
        public void test_fileTransfer() { super.ignore("not supported"); }
        public void test_cleanup() { super.ignore("not supported"); }
        public void test_totalCPUTime() { super.ignore("not supported"); }
        public void test_totalPhysicalMemory() { super.ignore("not supported"); }
        public void test_cpuArchitecture() { super.ignore("not supported"); }
        public void test_operatingSystemType() { super.ignore("not supported"); }
        public void test_candidateHosts() { super.ignore("not supported"); }
        public void test_queue() { super.ignore("not supported"); }
     }
    
    // test cases
    public static class LocalJobRunMinimalTest extends JobRunMinimalTest {
        public LocalJobRunMinimalTest() throws Exception {super("local");}
    }
    
    // test cases
    public static class LocalJobRunRequiredTest extends JobRunRequiredTest {
        public LocalJobRunRequiredTest() throws Exception {super("local");}
    }

    // test cases
    public static class LocalJobRunSandboxTest extends JobRunSandboxTest {
        public LocalJobRunSandboxTest() throws Exception {super("local");}

        // additional tests to/from remote
        public void test_input_local_to_remote() throws Exception {
            super.runJobInput(true, getLocal("input"), getRemote("input"));
        }
        public void test_input_remote_to_remote() throws Exception {
            super.runJobInput(true, getRemote("input_source"), getRemote("input_target"));
        }
        public void test_output_local_from_remote() throws Exception {
            super.runJobOutput(true, getLocal("output"), getRemote("output"));
        }
        public void test_output_remote_from_remote() throws Exception {
            super.runJobOutput(true, getRemote("output"), getRemote("output_target"));
        }
    }
    
    // test cases
    public static class LocalJobRunOptionalTest extends JobRunOptionalTest {
        public LocalJobRunOptionalTest() throws Exception {super("local");}
        public void test_resume_done() { super.ignore("not supported"); }
        public void test_resume_running() { super.ignore("not supported"); }
        public void test_suspend_done() { super.ignore("not supported"); }
        public void test_suspend_running() { super.ignore("not supported"); }
        public void test_listJob() { super.ignore("not supported but MUST BE REACTIVATED when the jsaga-engine will support this"); }
    }
    
 	// test cases
    public static class LocalJobRunDescriptionTest extends JobRunDescriptionTest {
        public LocalJobRunDescriptionTest() throws Exception {super("local");}
        public void test_run_queueRequirement() { super.ignore("not supported"); }
        public void test_run_cpuTimeRequirement() { super.ignore("not supported"); }
        public void test_run_memoryRequirement() { super.ignore("not supported"); }
    }
    
    // test cases
    public static class LocalJobRunInteractiveTest extends JobRunInteractiveTest {
        public LocalJobRunInteractiveTest() throws Exception {super("local");}
    }
}