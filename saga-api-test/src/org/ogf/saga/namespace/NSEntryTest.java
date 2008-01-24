package org.ogf.saga.namespace;

import org.ogf.saga.URL;
import org.ogf.saga.namespace.abstracts.AbstractNSEntryTest;

/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   NSEntryTest
* Author: Sylvain Reynaud (sreynaud@in2p3.fr)
* Date:   2 juil. 2007
* ***************************************************
* Description:                                      */
/**
 *
 */
public class NSEntryTest extends AbstractNSEntryTest {
    public NSEntryTest(String protocol) throws Exception {
        super(protocol);
    }

    public void test_getURL() throws Exception {
        URL expected = m_fileUrl;
        assertEquals(
                expected.toString(),
                m_file.getURL().toString());
    }

    public void test_getCWD() throws Exception {
        URL expected = new URL(m_dirUrl.toString());
        expected.setFragment(null);
        assertEquals(
                expected.toString(),
                m_file.getCWD().toString());
    }

    public void test_getName() throws Exception {
        assertEquals(
                DEFAULT_FILENAME,
                m_file.getName().getPath());
    }
}
