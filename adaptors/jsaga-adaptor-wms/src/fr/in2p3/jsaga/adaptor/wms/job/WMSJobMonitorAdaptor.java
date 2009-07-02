package fr.in2p3.jsaga.adaptor.wms.job;

import fr.in2p3.jsaga.adaptor.base.defaults.Default;
import fr.in2p3.jsaga.adaptor.base.usage.*;
import fr.in2p3.jsaga.adaptor.job.monitor.JobStatus;
import fr.in2p3.jsaga.adaptor.job.monitor.*;
import holders.StringArrayHolder;
import org.apache.axis.SimpleTargetedChain;
import org.apache.axis.configuration.SimpleProvider;
import org.apache.axis.transport.http.HTTPSender;
import org.glite.lb.LoggingAndBookkeepingLocatorClient;
import org.glite.wsdl.services.lb.LoggingAndBookkeepingLocator;
import org.glite.wsdl.services.lb.LoggingAndBookkeepingPortType;
import org.glite.wsdl.types.lb.*;
import org.glite.wsdl.types.lb.holders.JobStatusArrayHolder;
import org.globus.axis.transport.HTTPSSender;
import org.globus.axis.util.Util;
import org.ietf.jgss.GSSCredential;
import org.ogf.saga.error.*;

import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Map;

/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   WMSJobMonitorAdaptor
* Author: Nicolas DEMESY (nicolas.demesy@bt.com)
* Date:   18 fev. 2008
* ***************************************************/

public class WMSJobMonitorAdaptor extends WMSJobAdaptorAbstract implements QueryIndividualJob, QueryFilteredJob {
    private String m_wmsServerUrl;
    private String m_lbHost;
	private int m_lbPort;

	// Should never be invoked 
	public int getDefaultPort() {
		return 9003;
	}

    public String getType() {
        return "wms";
    }

    public Usage getUsage() {
        return new UAnd(new Usage[]{new U(MONITOR_PORT)});
    }

    public Default[] getDefaults(Map attributes) throws IncorrectStateException {
    	return new Default[]{
    			new Default(MONITOR_PORT, "9003")};
    }
  
    public void connect(String userInfo, String host, int port, String basePath, Map attributes) throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException, BadParameterException, TimeoutException, NoSuccessException {
        m_wmsServerUrl = "https://"+host+":"+port+basePath;
        m_lbHost = WMStoLB.getInstance().getLBHost(m_wmsServerUrl);
        // jobIdUrl port can not be used for invoking web service, use default port instead...
        m_lbPort = Integer.parseInt((String) attributes.get(MONITOR_PORT));
    }

    public void disconnect() throws NoSuccessException {
        m_wmsServerUrl = null;
        m_lbHost = null;
        m_lbPort = -1;
	}
    
    /**
	 * Get one job status
	 */
    public JobStatus getStatus(String nativeJobId) throws TimeoutException, NoSuccessException {
    	
    	try {

    		// get stub
	        LoggingAndBookkeepingPortType stub = getLBStub(m_credential);
	        
	        // get job Status
	        JobFlagsValue[] jobFlagsValue = new JobFlagsValue[1];
	        jobFlagsValue[0] = JobFlagsValue.CLASSADS;
	        JobFlags jobFlags = new JobFlags(jobFlagsValue);
	        org.glite.wsdl.types.lb.JobStatus jobState = stub.jobStatus(nativeJobId,jobFlags );
	        if(jobState == null) {
	            throw new NoSuccessException("Unable to get status for job:"+nativeJobId);
	        }
	        return new WMSJobStatus(nativeJobId,jobState.getState(), jobState.getState().getValue());
    	}
    	catch (MalformedURLException e) {
    		throw new NoSuccessException(e);
    	} catch (ServiceException e) {
    		throw new NoSuccessException(e);
		} catch (GenericFault e) {
			throw new NoSuccessException(e);
		} catch (RemoteException e) {
			throw new NoSuccessException(e);
		}
    }

	/**
	 * Get all jobs for authenticated user 
	 */
	public JobStatus[] getFilteredStatus(Object[] filters) throws TimeoutException, NoSuccessException {
		try {
			
			// get stub
			LoggingAndBookkeepingPortType stub = getLBStub(m_credential);
			
	        // get Jobs Status
            JobFlagsValue[] jobFlagsValue = new JobFlagsValue[1];
            jobFlagsValue[0] = JobFlagsValue.CLASSADS;
            JobFlags jobFlags = new JobFlags(jobFlagsValue);
	        
            JobStatusArrayHolder jobStatusResult = new JobStatusArrayHolder();
	        StringArrayHolder jobNativeIdResult = new StringArrayHolder();
	       
	        QueryConditions[] queryConditions = new  QueryConditions[1];
	        queryConditions[0] = new QueryConditions();
	        queryConditions[0].setAttr(QueryAttr.JOBID);
	        
	        QueryRecord[] qR = new QueryRecord[1];
	        QueryRecValue value1 = new QueryRecValue();
	        value1.setC("https://"+m_lbHost+"/");
	        qR[0] = new QueryRecord(QueryOp.UNEQUAL, value1, null );	        
	        queryConditions[0].setRecord(qR);	        
	        // Cannot use stub.userJobs() because not yet implemented (version > 1.8 needed) 
	        stub.queryJobs(queryConditions, jobFlags, jobNativeIdResult, jobStatusResult);
	        
	        if(jobNativeIdResult != null && jobNativeIdResult.value != null) {
	        	WMSJobStatus[] filterJobs = new WMSJobStatus[jobNativeIdResult.value.length];	
	        	for (int i = 0; i < filterJobs.length; i++) {
	        		filterJobs[i] = new WMSJobStatus(jobNativeIdResult.value[i], jobStatusResult.value[i].getState(), jobStatusResult.value[i].getState().getValue());
				}
		        return filterJobs;
	        }
	        // TODO : exception or null ?
	        return null;
    	} catch (Exception e) {
    		throw new NoSuccessException(e);
    	}
	}

	private LoggingAndBookkeepingPortType getLBStub(GSSCredential m_credential) throws MalformedURLException, ServiceException, NoSuccessException {
        // set LB url
        if (m_lbHost == null) {
            // second chance to get the lbHost
            m_lbHost = WMStoLB.getInstance().getLBHost(m_wmsServerUrl);

            // if still null then fails
            if (m_lbHost == null) {
                throw new NoSuccessException("No LB found for WMS: "+m_wmsServerUrl);
            }
        }
        URL lbURL = new URL("https", m_lbHost, m_lbPort , "/");

		// Set provider
        SimpleProvider provider = new SimpleProvider();
        SimpleTargetedChain c = null;
        c = new SimpleTargetedChain(new HTTPSSender());
        provider.deployTransport("https",c);
        c = new SimpleTargetedChain(new HTTPSender());
        provider.deployTransport("http",c);
        Util.registerTransport();
        
        // get LB Stub
        LoggingAndBookkeepingLocator loc = new LoggingAndBookkeepingLocatorClient(provider, m_credential);
        return loc.getLoggingAndBookkeeping(lbURL);
	}

}
