package fr.in2p3.jsaga.impl.url;

import org.ogf.saga.url.URL;
import org.ogf.saga.error.*;
import org.ogf.saga.url.URLFactory;

/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   URLFactoryImpl
* Author: Sylvain Reynaud (sreynaud@in2p3.fr)
* Date:   20 oct. 2008
* ***************************************************
* Description:                                      */
/**
 *
 */
public class URLFactoryImpl extends URLFactory {
    protected URL doCreateURL(String url) throws BadParameter, NoSuccess, NotImplemented {
        return new URLImpl(url, true);  // encode = true
    }

    public static URL createUnencodedURL(String url) throws BadParameter {
        return new URLImpl(url, false); // encode = false
    }
}
