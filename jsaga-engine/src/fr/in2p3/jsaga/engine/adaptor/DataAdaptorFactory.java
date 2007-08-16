package fr.in2p3.jsaga.engine.adaptor;

import fr.in2p3.jsaga.adaptor.data.DataAdaptor;
import fr.in2p3.jsaga.adaptor.security.SecurityAdaptor;
import fr.in2p3.jsaga.engine.config.Configuration;
import fr.in2p3.jsaga.engine.config.ConfigurationException;
import fr.in2p3.jsaga.engine.config.adaptor.DataAdaptorDescriptor;
import fr.in2p3.jsaga.helpers.StringArray;
import org.ogf.saga.URI;
import org.ogf.saga.error.*;

import java.lang.Exception;

/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   DataAdaptorFactory
* Author: Sylvain Reynaud (sreynaud@in2p3.fr)
* Date:   15 juin 2007
* ***************************************************
* Description:                                      */
/**
 * Create and manage data adaptors
 */
public class DataAdaptorFactory {
    private static DataAdaptorFactory _instance = null;

    private DataAdaptorDescriptor m_descriptor;

    public static synchronized DataAdaptorFactory getInstance() throws ConfigurationException {
        if (_instance == null) {
            _instance = new DataAdaptorFactory();
        }
        return _instance;
    }
    private DataAdaptorFactory() throws ConfigurationException {
        m_descriptor = Configuration.getInstance().getDescriptors().getDataDesc();
    }

    /**
     * Create a new instance of data adaptor for URI <code>service</code> and connect to service.
     * <br>Note: only 'scheme' is needed for creation, but 'userid' and 'hostname' might be useful
     * for caching data adaptor instances.
     * @param service the URI of the service
     * @param securityAdaptor the security adaptor
     * @param type the security context type
     * @return the data adaptor instance
     */
    public DataAdaptor getDataAdaptor(URI service, SecurityAdaptor securityAdaptor, String type) throws AuthenticationFailed, AuthorizationFailed, PermissionDenied, Timeout, NoSuccess {
        // create instance
        Class clazz = m_descriptor.getClass(service.getScheme());
        DataAdaptor dataAdaptor;
        try {
            dataAdaptor = (DataAdaptor) clazz.newInstance();
        } catch (Exception e) {
            throw new NoSuccess(e);
        }

        // set security
        if (securityAdaptor != null) {
            if (StringArray.arrayContains(dataAdaptor.getSupportedContextTypes(), type)) {
                dataAdaptor.setSecurityAdaptor(securityAdaptor);
            } else {
                throw new AuthenticationFailed("Security context type '"+type+"' not supported for protocol: "+service.getScheme());
            }
        }

        // connect
        dataAdaptor.connect(service.getUserInfo(), service.getHost(), service.getPort());
        return dataAdaptor;
    }
}
