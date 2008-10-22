package fr.in2p3.jsaga.helpers.xslt;

import junit.framework.TestCase;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;

import java.io.ByteArrayInputStream;
import java.util.Properties;

/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   XSLTransformerTest
* Author: Sylvain Reynaud (sreynaud@in2p3.fr)
* Date:   17 avr. 2008
* ***************************************************
* Description:                                      */
/**
 *
 */
public class XSLTransformerTest extends TestCase {
    public void test_wrapper_1_generate_URL() throws Exception {
        final URL[] urls = new URL[] {
                URLFactory.createURL("gsiftp://host:2811/absolute/path/to/file.txt?query#grid"),
                URLFactory.createURL("gsiftp://host:2811/absolute/path/to/file.txt#grid"),
                URLFactory.createURL("gsiftp://host:2811?query#grid"),
                URLFactory.createURL("gsiftp://host:2811#grid"),
                URLFactory.createURL("gsiftp://host:2811"),
                URLFactory.createURL("gsiftp://host:2811/absolute/path/to/file.txt"),
                URLFactory.createURL("gsiftp://host:2811/./relative/path/to/file.txt"),
                URLFactory.createURL("srb://user@host/absolute/path/to/file.txt"),
                URLFactory.createURL("srb:///absolute/path/to/file.txt"),
                URLFactory.createURL("file:///C:/absolute/path/to/dir/"),
                URLFactory.createURL("file:///absolute/path/to/dir/"),
                URLFactory.createURL("file://./relative/path/to/file.txt"),
                URLFactory.createURL("file://$PWD/relative/path/to/file.txt")
        };
        XSLTransformer t = XSLTransformerFactory.getInstance().create("xsl/execution/wrapper_1-generate.xsl");
        for (int i=0; i<urls.length; i++) {
            byte[] xml = ("<test>"+urls[i]+"</test>").getBytes();
            Properties prop = new Properties();
            prop.load(new ByteArrayInputStream(t.transform(xml)));
            assertEquals(
                    "Bad scheme for URL: "+prop.getProperty("URL"),
                    urls[i].getScheme()!=null ? urls[i].getScheme() : "",
                    prop.getProperty("SCHEME"));
            assertEquals(
                    "Bad userInfo for URL: "+prop.getProperty("URL"),
                    urls[i].getUserInfo()!=null ? urls[i].getUserInfo() : "",
                    prop.getProperty("USER"));
            assertEquals(
                    "Bad host for URL: "+prop.getProperty("URL"),
                    urls[i].getHost()!=null ? urls[i].getHost() : "",
                    prop.getProperty("HOST"));
            assertEquals(
                    "Bad port for URL: "+prop.getProperty("URL"),
                    urls[i].getPort()>-1 ? urls[i].getPort()+"" : "",
                    prop.getProperty("PORT"));
            assertEquals(
                    "Bad path for URL: "+prop.getProperty("URL"),
                    urls[i].getPath()!=null ? urls[i].getPath() : "",
                    prop.getProperty("PATH"));
            assertEquals(
                    "bad query for URL: "+prop.getProperty("URL"),
                    urls[i].getQuery()!=null ? urls[i].getQuery() : "",
                    prop.getProperty("QUERY"));
            assertEquals(
                    "bad fragment for URL: "+prop.getProperty("URL"),
                    urls[i].getFragment()!=null ? urls[i].getFragment() : "",
                    prop.getProperty("FRAGMENT"));
        }
    }
}
