package integration;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.ogf.saga.file.*;
import org.ogf.saga.namespace.*;

/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   SFTPDataTestSuite
* Author: Nicolas DEMESY (nicolas.demesy@bt.com)
* Date:   15 avril 2008
****************************************************/

public class SFTPDataTestSuite extends TestSuite {
    // test cases
    public static class SFTPNSEntryTest extends NSEntryTest {
        public SFTPNSEntryTest() throws Exception {super("sftp");}
    }
    public static class SFTPDirectoryListTest extends DirectoryListTest {
        public SFTPDirectoryListTest() throws Exception {super("sftp");}
    }
    public static class SFTPDirectoryMakeTest extends DirectoryMakeTest {
        public SFTPDirectoryMakeTest() throws Exception {super("sftp");}
    }
    public static class SFTPDirectoryTest extends DirectoryTest {
        public SFTPDirectoryTest() throws Exception {super("sftp");}
    }
    public static class SFTPFileReadTest extends FileReadTest {
        public SFTPFileReadTest() throws Exception {super("sftp");}
    }
    public static class SFTPFileWriteTest extends FileWriteTest {
        public SFTPFileWriteTest() throws Exception {super("sftp");}
    }
    public static class SFTPNSCopyTest extends NSCopyTest {
        public SFTPNSCopyTest() throws Exception {super("sftp", "sftp");}
    }
    public static class SFTPNSCopyFromTest extends NSCopyFromTest {
        public SFTPNSCopyFromTest() throws Exception {super("sftp", "sftp");}
    }
    public static class SFTPNSCopyRecursiveTest extends NSCopyRecursiveTest {
        public SFTPNSCopyRecursiveTest() throws Exception {super("sftp", "sftp");}
    }
    public static class SFTPNSMoveTest extends NSMoveTest {
        public SFTPNSMoveTest() throws Exception {super("sftp", "sftp");}
    }
    public static class SFTP_to_EmulatorNSCopyTest extends NSCopyTest {
        public SFTP_to_EmulatorNSCopyTest() throws Exception {super("sftp", "test");}
    }
    public static class SFTP_to_EmulatorNSCopyFromTest extends NSCopyFromTest {
        public SFTP_to_EmulatorNSCopyFromTest() throws Exception {super("sftp", "test");}
    }
    public static class SFTP_to_EmulatorNSCopyRecursiveTest extends NSCopyRecursiveTest {
        public SFTP_to_EmulatorNSCopyRecursiveTest() throws Exception {super("sftp", "test");}
    }
    public static class SFTP_to_EmulatorNSMoveTest extends NSMoveTest {
        public SFTP_to_EmulatorNSMoveTest() throws Exception {super("sftp", "test");}
    }

    public SFTPDataTestSuite() throws Exception {
        super();
        // test cases
        this.addTestSuite(SFTPNSEntryTest.class);
        this.addTestSuite(SFTPDirectoryListTest.class);
        this.addTestSuite(SFTPDirectoryMakeTest.class);
        this.addTestSuite(SFTPDirectoryTest.class);
        this.addTestSuite(SFTPFileReadTest.class);
        this.addTestSuite(SFTPFileWriteTest.class);
        this.addTestSuite(SFTPNSCopyTest.class);
        this.addTestSuite(SFTPNSCopyFromTest.class);
        this.addTestSuite(SFTPNSCopyRecursiveTest.class);
        this.addTestSuite(SFTPNSMoveTest.class);
    }

    public static Test suite() throws Exception {
        return new SFTPDataTestSuite();
    }
}