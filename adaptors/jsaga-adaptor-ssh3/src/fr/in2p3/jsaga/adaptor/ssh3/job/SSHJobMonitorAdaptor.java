package fr.in2p3.jsaga.adaptor.ssh3.job;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import fr.in2p3.jsaga.adaptor.job.control.manage.ListableJobAdaptor;
import fr.in2p3.jsaga.adaptor.job.monitor.JobInfoAdaptor;
import fr.in2p3.jsaga.adaptor.job.monitor.JobStatus;
import fr.in2p3.jsaga.adaptor.job.monitor.QueryIndividualJob;
import fr.in2p3.jsaga.adaptor.ssh3.SSHAdaptorAbstract;
import fr.in2p3.jsaga.adaptor.ssh3.data.SFTPFileAttributes;

import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.TimeoutException;

import ch.ethz.ssh2.SFTPException;
import ch.ethz.ssh2.SFTPv3Client;
import ch.ethz.ssh2.SFTPv3DirectoryEntry;
import ch.ethz.ssh2.SFTPv3FileHandle;
import ch.ethz.ssh2.sftp.ErrorCodes;


/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   SSHJobMonitorAdaptor
* Author: Lionel Schwarz (lionel.schwarz@in2p3.fr)
* Date:   16 juillet 2013
* ***************************************************/

public class SSHJobMonitorAdaptor extends SSHAdaptorAbstract implements QueryIndividualJob, ListableJobAdaptor, JobInfoAdaptor {

	private String m_host; // needs to keep it for getExecutionHosts
	
    public String getType() {
        return "ssh";
    }

    public void connect(String userInfo, String host, int port, String basePath, Map attributes) throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException, BadParameterException, TimeoutException, NoSuccessException {
    	super.connect(userInfo, host, port, basePath, attributes);
    	m_host = host;
    }
    
    public JobStatus getStatus(String nativeJobId) throws TimeoutException, NoSuccessException {
		Integer rc = getReturnCode(nativeJobId);
    	return new SSHJobStatus(nativeJobId, rc);
    }        

	public String[] list() throws PermissionDeniedException, TimeoutException, NoSuccessException {
		SFTPv3Client sftp = null;
		try {
			sftp = new SFTPv3Client(m_conn);
			List<SFTPv3DirectoryEntry> v = sftp.ls(SSHJobProcess.getRootDir());
			List<String> processes = new ArrayList<String>();
			String suffix = "." + SSHJobProcess.PROCESS_SUFFIX;
			for (int i=0; i<v.size(); i++) {
				String file = ((SFTPv3DirectoryEntry)v.get(i)).filename;
				if (file.endsWith(suffix)) {
					processes.add(file.substring(0, file.length()-suffix.length()));
				}
			}
			String[] listOfJobs = new String[processes.size()];
			return processes.toArray(listOfJobs);
			
		} catch (SFTPException e) {
			if (e.getServerErrorCode() == ErrorCodes.SSH_FX_NO_SUCH_FILE)
				throw new NoSuccessException(e);
			if (e.getServerErrorCode() == ErrorCodes.SSH_FX_PERMISSION_DENIED)
				throw new PermissionDeniedException(e);
    		throw new NoSuccessException(e);
		} catch (IOException e) {
    		throw new NoSuccessException(e);
		} finally {
			if (sftp!=null) sftp.close();
		}
	}

	public Integer getExitCode(String nativeJobId)	throws NotImplementedException, NoSuccessException {
		Integer rc = getReturnCode(nativeJobId);
		if (rc == SSHJobProcess.PROCESS_RUNNING)
			throw new NoSuccessException("Process not finished, exit code not available");
		return rc;
	}
	
	public Date getCreated(String nativeJobId) throws NotImplementedException, 	NoSuccessException {
		try {
			SSHJobProcess sshjp = restore(nativeJobId);
			return sshjp.getCreated();
		} catch (IOException e) {
			throw new NoSuccessException(e);
		} catch (ClassNotFoundException e) {
			throw new NoSuccessException(e);
		} catch (InterruptedException e) {
			throw new NoSuccessException(e);
		}
	}

	public Date getStarted(String nativeJobId) throws NotImplementedException, NoSuccessException {
		String filename = new SSHJobProcess(nativeJobId).getPidFile();
		SFTPv3Client sftp = null;
		try {
			sftp = new SFTPv3Client(m_conn);
			SFTPFileAttributes attrs = new SFTPFileAttributes(filename, sftp.stat(filename));
			return new Date(attrs.getLastModified());
		} catch (IOException e) {
			throw new NoSuccessException(e);
		} finally {
			if (sftp!=null) sftp.close();
		}
	}

	public Date getFinished(String nativeJobId) throws NotImplementedException, NoSuccessException {
		String filename = new SSHJobProcess(nativeJobId).getEndcodeFile();
		SFTPv3Client sftp = null;
		try {
			sftp = new SFTPv3Client(m_conn);
			SFTPFileAttributes attrs = new SFTPFileAttributes(filename, sftp.stat(filename));
			return new Date(attrs.getLastModified());
		} catch (IOException e) {
			throw new NoSuccessException(e);
		} finally {
			if (sftp!=null) sftp.close();
		}
	}

	public String[] getExecutionHosts(String nativeJobId) throws NotImplementedException, NoSuccessException {
		return new String[]{m_host};
	}

	private Integer getReturnCode(String nativeJobId) throws NoSuccessException {
		SFTPv3Client sftp = null;
    	try {    	
			sftp = new SFTPv3Client(m_conn);
			SFTPv3FileHandle f = sftp.openFileRO(new SSHJobProcess(nativeJobId).getEndcodeFile());
	    	byte[] buf = new byte[4];
			int len = sftp.read(f, 0, buf, 0, buf.length);
			sftp.closeFile(f);
    		return new Integer(new String(buf).trim());
    	} catch (SFTPException sftpe) {
    		// try first to get serialized object
    		try {
				SSHJobProcess sjp = this.restore(nativeJobId);
				return sjp.getReturnCode();
			} catch (IOException e) {
			} catch (ClassNotFoundException e) {
			} catch (InterruptedException e) {
			}
    		return SSHJobProcess.PROCESS_RUNNING;
    	} catch (IOException e) {
    		throw new NoSuccessException(e);
		} finally {
			if (sftp!=null) sftp.close();
		}
	}

}
