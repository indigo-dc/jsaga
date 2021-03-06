package fr.in2p3.jsaga.adaptor.ssh3.job;


import fr.in2p3.jsaga.adaptor.job.BadResource;
import fr.in2p3.jsaga.adaptor.job.control.JobControlAdaptor;
import fr.in2p3.jsaga.adaptor.job.control.advanced.CleanableJobAdaptor;
import fr.in2p3.jsaga.adaptor.job.control.description.JobDescriptionTranslator;
import fr.in2p3.jsaga.adaptor.job.control.description.JobDescriptionTranslatorXSLT;
import fr.in2p3.jsaga.adaptor.job.control.interactive.StreamableJobInteractiveSet;
import fr.in2p3.jsaga.adaptor.job.control.staging.StagingJobAdaptorOnePhase;
import fr.in2p3.jsaga.adaptor.job.control.staging.StagingJobAdaptorTwoPhase;
import fr.in2p3.jsaga.adaptor.job.control.staging.StagingTransfer;
import fr.in2p3.jsaga.adaptor.job.local.LocalAdaptorAbstract;
import fr.in2p3.jsaga.adaptor.job.local.LocalJobProcess;
import fr.in2p3.jsaga.adaptor.job.monitor.JobMonitorAdaptor;
import fr.in2p3.jsaga.adaptor.ssh3.SSHAdaptorAbstract;
import fr.in2p3.jsaga.adaptor.ssh3.SSHExecutionChannel;

import org.ogf.saga.error.*;

import ch.ethz.ssh2.SFTPException;
import ch.ethz.ssh2.SFTPv3Client;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.sftp.AttribPermissions;
import ch.ethz.ssh2.sftp.ErrorCodes;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Matcher;

/* ***************************************************
 * *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
 * ***             http://cc.in2p3.fr/             ***
 * ***************************************************
 * File:   SSHJobControlAdaptor
 * Author: Lionel Schwarz (lionel.schwarz@in2p3.fr)
 * Date:   16 juillet 2013
 * ***************************************************/

public class SSHJobControlAdaptor extends SSHAdaptorAbstract implements
		JobControlAdaptor, CleanableJobAdaptor, StagingJobAdaptorTwoPhase {

    private static final String ROOTDIR = "RootDir";
    protected static final String TYPE = "ssh";
    
    public String getType() {
		return TYPE;
	}

	public JobMonitorAdaptor getDefaultJobMonitor() {
		return new SSHJobMonitorAdaptor();
	}

    public JobDescriptionTranslator getJobDescriptionTranslator() throws NoSuccessException {
        JobDescriptionTranslator translator = new JobDescriptionTranslatorXSLT("xsl/job/ssh.xsl");
        translator.setAttribute(ROOTDIR, SSHJobProcess.getRootDir());
        return translator;
    }

    public void connect(String userInfo, String host, int port, String basePath, Map attributes) 
    		throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException, 
    		BadParameterException, TimeoutException, NoSuccessException {
    	super.connect(userInfo, host, port, basePath, attributes);
		String fullUrl = "";
    	SFTPv3Client sftp;
		try {
			sftp = new SFTPv3Client(m_conn);
		} catch (IOException e1) {
			throw new NoSuccessException(e1);
		}
		for (String d: SSHJobProcess.getRootDir().split("/")) {
			fullUrl += d + "/";
			try {
				sftp.mkdir(fullUrl, AttribPermissions.S_IRUSR	| AttribPermissions.S_IWUSR	| AttribPermissions.S_IXUSR);
			} catch (SFTPException e) {
				if (e.getServerErrorCode() == ErrorCodes.SSH_FX_FAILURE) { // Already Exists
					// ignore
				} else if (e.getServerErrorCode() == ErrorCodes.SSH_FX_PERMISSION_DENIED) {
					sftp.close();
					throw new AuthorizationFailedException(e);
				} else {
					sftp.close();
					throw new NoSuccessException(e); 
				}
			} catch (IOException e) {
				sftp.close();
				throw new NoSuccessException(e); 
			}
		}
		sftp.close();
    }
    
    private SSHJobProcess submit(String jobDesc, String uniqId)
			throws PermissionDeniedException, TimeoutException, BadResource, NoSuccessException {
    	SSHJobProcess sjp = null;
    	SFTPv3Client sftp = null;
		try {
			sftp = new SFTPv3Client(m_conn);
	    	sjp = new SSHJobProcess(uniqId, jobDesc, m_conn.getHostname(), m_conn.getPort(), sftp.canonicalPath("."));
	    	sjp.checkResources();
	    	// create user working directory only if not specified
			if (!sjp.isUserWorkingDirectory()) {
				sftp.mkdir(sjp.getWorkingDirectory(), 
						AttribPermissions.S_IRUSR
						| AttribPermissions.S_IWUSR
						| AttribPermissions.S_IXUSR);
			}
		} catch (SFTPException e) {
			if (e.getServerErrorCode() == ErrorCodes.SSH_FX_FAILURE) { // Already Exists
				// ignore
			} else if (e.getServerErrorCode() == ErrorCodes.SSH_FX_PERMISSION_DENIED) {
				throw new PermissionDeniedException(e);
			} else {
				throw new NoSuccessException(e); 
			}
		} catch (IOException e) {
			throw new NoSuccessException(e);
		} finally {
			if (sftp != null) sftp.close();
		}
        sjp.setCreated(new Date());
		try {
            store(sjp, uniqId);
		} catch (Exception e) {
			throw new NoSuccessException(e);
		}
		return sjp;
	}

	public String submit(String jobDesc, boolean checkMatch, String uniqId)
			throws PermissionDeniedException, TimeoutException, BadResource, NoSuccessException {
    	return this.submit(jobDesc, uniqId).getJobId();
    }

	public void cancel(String nativeJobId) throws PermissionDeniedException, TimeoutException,
            NoSuccessException {
		SSHExecutionChannel channelCancel=null;
		try {
			channelCancel = new SSHExecutionChannel(m_conn);
			channelCancel.execute("kill `cat " + new SSHJobProcess(nativeJobId).getPidFile() + "`;");
			int error = channelCancel.getExitStatus();
			if (error > 0) {
				throw new Exception("Cancel command failed with error code: " + error);
			}
		} catch (Exception e) {
			throw new NoSuccessException(e);
		} finally {
			if (channelCancel != null) channelCancel.close();
		}
	}

	public void clean(String nativeJobId) throws PermissionDeniedException, TimeoutException,
            NoSuccessException {
		SSHExecutionChannel cleanChannel=null;
		try {
			SSHJobProcess sshjp = new SSHJobProcess(nativeJobId);
			cleanChannel = new SSHExecutionChannel(m_conn);
			String cmd = "rm -rf " + sshjp.getGeneratedWorkingDirectory() + "*";
			cleanChannel.execute(cmd);
		} catch (Exception e) {
			throw new NoSuccessException(e);
		} finally {
			if (cleanChannel != null) cleanChannel.close();
		}
	}

	private class ShellScriptBuffer  {
		private String m_script = "";
		private final String EOL = "\n";
		public void append(String s) {
			m_script += s + EOL;
		}
		public String toString() {
			return m_script;
		}
	}

	public String getStagingDirectory(String nativeJobId)
			throws PermissionDeniedException, TimeoutException,
			NoSuccessException {
		// The staging directory is built at connect
		return null;
	}

	public StagingTransfer[] getInputStagingTransfer(String nativeJobId)
			throws PermissionDeniedException, TimeoutException,
			NoSuccessException {
		try {
			SSHJobProcess sshjp = restore(nativeJobId);
			return sshjp.getInputStaging();
		} catch (IOException e) {
			throw new NoSuccessException(e);
		} catch (ClassNotFoundException e) {
			throw new NoSuccessException(e);
		} catch (InterruptedException e) {
			throw new NoSuccessException(e);
		}
	}

	public StagingTransfer[] getOutputStagingTransfer(String nativeJobId)
			throws PermissionDeniedException, TimeoutException,
			NoSuccessException {
		try {
			SSHJobProcess sshjp = restore(nativeJobId);
			return sshjp.getOutputStaging();
		} catch (IOException e) {
			throw new NoSuccessException(e);
		} catch (ClassNotFoundException e) {
			throw new NoSuccessException(e);
		} catch (InterruptedException e) {
			throw new NoSuccessException(e);
		}
	}

	public void start(String nativeJobId) throws PermissionDeniedException,
			TimeoutException, NoSuccessException {
		try {
			SSHExecutionChannel channel = new SSHExecutionChannel(m_conn);
	    	
	    	ShellScriptBuffer command = new ShellScriptBuffer();
			Properties jobProps = new Properties();
			SSHJobProcess sshjp = restore(nativeJobId);
			jobProps.load(new ByteArrayInputStream(sshjp.getJobDesc().getBytes()));
			String _exec = null;
	        Enumeration e = jobProps.propertyNames();
			Hashtable _envParams = new Hashtable();
	        while (e.hasMoreElements()){
	               String key = (String)e.nextElement();
	               String val = (String)jobProps.getProperty(key);
	               if (key.equals("_Executable")) {
	            	   // The following line does not work -> indexArrayOutOfBoundException
	            	   // _exec = val.replaceAll("\\$", "\\\\$");
	            	   _exec = Matcher.quoteReplacement(val);
	               } else {
	            	   // channel.setEnv does not work
	            	   _envParams.put(key, val);
	               }
	        }
	 	    // channel.setEnv does not work
	        Iterator i = _envParams.entrySet().iterator();
	
	        while(i.hasNext()){
	          Map.Entry me = (Map.Entry)i.next();
	          command.append(me.getKey() + "=" + me.getValue());
	        }

	        command.append(_exec);
			String cde = "cat << EOS | bash -s \n" + command.toString() + "EOS\n";
			//System.out.println("NEW command="+cde);
			channel.execute(cde, 1000);
			if (channel.isClosed()) {
				int error = channel.getExitStatus();
				// It is possible that the wrapper script failed without writing any .endcode file
				// Here this is simulated by storing returnCode into the serialized object
				sshjp.setReturnCode(error);
				store(sshjp, nativeJobId);
	            //System.out.println("channel is closed and return = " + error);
			}
		} catch (IOException e) {
			throw new NoSuccessException(e);
		} catch (ClassNotFoundException e) {
			throw new NoSuccessException(e);
		} catch (InterruptedException e) {
			throw new NoSuccessException(e);
		}
	
	}
	
}
