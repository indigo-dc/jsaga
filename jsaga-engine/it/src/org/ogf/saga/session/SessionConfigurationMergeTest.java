package org.ogf.saga.session;

import fr.in2p3.jsaga.engine.session.SessionConfiguration;
import org.junit.Test;
import org.ogf.saga.JSAGABaseTest;

import java.net.URL;

/* ***************************************************
 * *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
 * ***             http://cc.in2p3.fr/             ***
 * ***************************************************
 * File:   SessionConfigurationMergeTest
 * Author: Sylvain Reynaud (sreynaud@in2p3.fr)
 * ***************************************************
 * Description:                                      */

/**
 *
 */
public class SessionConfigurationMergeTest extends JSAGABaseTest {
    private static final String CONFIG = "/home/user-default-contexts.xml";

    public SessionConfigurationMergeTest() throws Exception {
        super();
    }

    @Test
    public void test_dumpXML() throws Exception {
        URL configUrl = SessionConfigurationMergeTest.class.getResource(CONFIG);
        SessionConfiguration config = new SessionConfiguration(configUrl);
        String expected = SessionConfigurationTest.getResourceAsString("/home/expected.xml");
        //TODO: remove this workaround when castor will be replaced with JAXB
        expected = expected.replaceAll("\\r\\n", "\n");
        assertTrue(configUrl != null);
        assertEquals(expected, config.toXML());
    }
}
