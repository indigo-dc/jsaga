package integration;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.ogf.saga.file.*;
import org.ogf.saga.namespace.*;

/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   GlobusIntegrationTestSuite
* Author: Sylvain Reynaud (sreynaud@in2p3.fr)
* Date:   31 juil. 2007
* ***************************************************
* Description:                                      */
/**
 *
 */
public class GlobusDataTestSuite extends TestSuite {
    // test cases
    public static class GsiftpNSEntryTest extends NSEntryTest {
        public GsiftpNSEntryTest() throws Exception {super("gsiftp");}
    }
    public static class GsiftpDirectoryListTest extends DirectoryListTest {
        public GsiftpDirectoryListTest() throws Exception {super("gsiftp");}
    }
    public static class GsiftpDirectoryMakeTest extends DirectoryMakeTest {
        public GsiftpDirectoryMakeTest() throws Exception {super("gsiftp");}
    }
    public static class GsiftpDirectoryTest extends DirectoryTest {
        public GsiftpDirectoryTest() throws Exception {super("gsiftp");}
    }
    public static class GsiftpFileReadTest extends FileReadTest {
        public GsiftpFileReadTest() throws Exception {super("gsiftp");}
    }
    public static class GsiftpFileWriteTest extends FileWriteTest {
        public GsiftpFileWriteTest() throws Exception {super("gsiftp");}
    }
    public static class GsiftpNSCopyTest extends NSCopyTest {
        public GsiftpNSCopyTest() throws Exception {super("gsiftp", "gsiftp");}
    }
    public static class GsiftpNSCopyFromTest extends NSCopyFromTest {
        public GsiftpNSCopyFromTest() throws Exception {super("gsiftp", "gsiftp");}
    }
    public static class GsiftpNSCopyRecursiveTest extends NSCopyRecursiveTest {
        public GsiftpNSCopyRecursiveTest() throws Exception {super("gsiftp", "gsiftp");}
    }
    public static class GsiftpNSMoveTest extends NSMoveTest {
        public GsiftpNSMoveTest() throws Exception {super("gsiftp", "gsiftp");}
    }
    public static class Gsiftp_to_EmulatorNSCopyTest extends NSCopyTest {
        public Gsiftp_to_EmulatorNSCopyTest() throws Exception {super("gsiftp", "test");}
    }
    public static class Gsiftp_to_EmulatorNSCopyFromTest extends NSCopyFromTest {
        public Gsiftp_to_EmulatorNSCopyFromTest() throws Exception {super("gsiftp", "test");}
    }
    public static class Gsiftp_to_EmulatorNSCopyRecursiveTest extends NSCopyRecursiveTest {
        public Gsiftp_to_EmulatorNSCopyRecursiveTest() throws Exception {super("gsiftp", "test");}
    }
    public static class Gsiftp_to_EmulatorNSMoveTest extends NSMoveTest {
        public Gsiftp_to_EmulatorNSMoveTest() throws Exception {super("gsiftp", "test");}
    }

    public GlobusDataTestSuite() throws Exception {
        super();
        // test cases
        this.addTestSuite(GsiftpNSEntryTest.class);
        this.addTestSuite(GsiftpDirectoryListTest.class);
        this.addTestSuite(GsiftpDirectoryMakeTest.class);
        this.addTestSuite(GsiftpDirectoryTest.class);
        this.addTestSuite(GsiftpFileReadTest.class);
        this.addTestSuite(GsiftpFileWriteTest.class);

        this.addTestSuite(GsiftpNSCopyTest.class);
        this.addTestSuite(GsiftpNSCopyFromTest.class);
        this.addTestSuite(GsiftpNSCopyRecursiveTest.class);
        this.addTestSuite(GsiftpNSMoveTest.class);

        this.addTestSuite(Gsiftp_to_EmulatorNSCopyTest.class);
        this.addTestSuite(Gsiftp_to_EmulatorNSCopyFromTest.class);
        this.addTestSuite(Gsiftp_to_EmulatorNSCopyRecursiveTest.class);
        this.addTestSuite(Gsiftp_to_EmulatorNSMoveTest.class);
    }

    public static Test suite() throws Exception {
        return new GlobusDataTestSuite();
    }
}