package fr.in2p3.jsaga.adaptor.cream.job;

import eu.emi.security.canl.axis2.CANLAXIS2SocketFactory;
import fr.in2p3.jsaga.adaptor.ClientAdaptor;
import fr.in2p3.jsaga.adaptor.base.defaults.Default;
import fr.in2p3.jsaga.adaptor.base.usage.UOptional;
import fr.in2p3.jsaga.adaptor.base.usage.Usage;
import fr.in2p3.jsaga.adaptor.security.SecurityCredential;
import fr.in2p3.jsaga.adaptor.security.impl.GSSCredentialSecurityCredential;

import org.apache.axis2.AxisFault;
import org.apache.axis2.databinding.types.URI;
import org.apache.axis2.databinding.types.URI.MalformedURIException;
import org.apache.commons.httpclient.protocol.Protocol;
import org.glite.ce.creamapi.ws.cream2.CREAMStub;
import org.glite.ce.creamapi.ws.cream2.CREAMStub.JobFilter;
import org.glite.ce.creamapi.ws.cream2.CREAMStub.JobId;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ogf.saga.context.Context;
import org.ogf.saga.error.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   CreamJobAdaptorAbstract
* Author: Sylvain Reynaud (sreynaud@in2p3.fr)
* Date:   10 mars 2008
* ***************************************************
* Description:                                      */
/**
 *
 */
public class CreamJobAdaptorAbstract implements ClientAdaptor {


    private static final String DELEGATION_ID = "delegationId";

    protected GSSCredential m_credential;
    protected String m_vo;
    protected File m_certRepository;

    protected String m_delegationId;
    protected CREAMStub m_creamStub;
    // TODO: check if URL can be retrieved from Stub
    protected URL m_creamUrl;

    protected String m_creamVersion = "";
    
    public String getType() {
        return "cream";
    }

    public Class[] getSupportedSecurityCredentialClasses() {
        return new Class[]{GSSCredentialSecurityCredential.class};
    }

    public void setSecurityCredential(SecurityCredential credential) {
        m_credential = ((GSSCredentialSecurityCredential) credential).getGSSCredential();
        try {
            m_vo = credential.getAttribute(Context.USERVO);
        } catch (Exception e) {
            /* ignore */
        }
        try {
            m_certRepository = ((GSSCredentialSecurityCredential) credential).getCertRepository();
        } catch (Exception e) {
            /* ignore */
        }
    }

    public int getDefaultPort() {
        return 8443;
    }

    public Usage getUsage() {
        return new UOptional(DELEGATION_ID);
    }

    public Default[] getDefaults(Map attributes) throws IncorrectStateException {
        return null;    // no default
    }

    public void connect(String userInfo, String host, int port, String basePath, Map attributes) throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException, BadParameterException, TimeoutException, NoSuccessException {
        Protocol.registerProtocol("https", new Protocol("https", new CANLAXIS2SocketFactory(), 8443));
        
        Properties sslConfig = new Properties();
        sslConfig.put("truststore", m_certRepository.getPath());
        sslConfig.put("crlcheckingmode", "ifvalid");
        // TODO: hardcoded
        sslConfig.put("proxy", "/home/schwarz/.jsaga/tmp/voms_cred.txt");
        CANLAXIS2SocketFactory.setCurrentProperties(sslConfig);

        // set DELEGATION_ID
        if (attributes.containsKey(DELEGATION_ID)) {
            m_delegationId = (String) attributes.get(DELEGATION_ID);
        } else {
            try {
                String dn = m_credential.getName().toString();
                // TODO: add vo name 
                m_delegationId = "delegation-"+Math.abs(dn.hashCode());
            } catch (GSSException e) {
                throw new NoSuccessException(e);
            }
        }
    	try {
    		m_creamUrl = new URL("https", host, port, "/ce-cream/services/CREAM2");
    		m_creamStub = new CREAMStub(m_creamUrl.toString());
		} catch (MalformedURLException e) {
            throw new BadParameterException(e.getMessage(), e);
		} catch (AxisFault e) {
            throw new BadParameterException(e.getMessage(), e);
		}
    }

    public void disconnect() throws NoSuccessException {
        m_creamStub = null;
        m_creamUrl = null;
    }
    
	protected JobFilter getJobFilter(String nativeJobId) throws NoSuccessException {
        JobId jobId = new JobId();
        jobId.setId(nativeJobId);
        try {
			jobId.setCreamURL(new URI(m_creamUrl.toString()));
		} catch (MalformedURIException e) {
			throw new NoSuccessException(e);
		}
        JobFilter filter = new JobFilter();
        filter.setDelegationId(m_delegationId);
        filter.setJobId(new JobId[]{jobId});
        return filter;
    }


}
