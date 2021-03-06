package integration;

import junit.framework.Test;
import org.ogf.saga.job.JobRunMinimalTest;

/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   SSHExecutionTestSuiteMinimal
* Author: Lionel Schwarz (lionel.schwarz@in2p3.fr)
* Date:   16 Nov 2011
****************************************************/

public class SSHExecutionTestSuiteMinimal extends JSAGATestSuite {
    /** create test suite */
    public static Test suite() throws Exception {return new SSHExecutionTestSuiteMinimal();}
    /** index of test cases */
    public static class index extends IndexTest {public index(){super(SSHExecutionTestSuiteMinimal.class);}}

    /** test cases */
    public static class SSHJobRunMinimalTest extends JobRunMinimalTest {
        public SSHJobRunMinimalTest() throws Exception {super("ssh2");}
     }
}