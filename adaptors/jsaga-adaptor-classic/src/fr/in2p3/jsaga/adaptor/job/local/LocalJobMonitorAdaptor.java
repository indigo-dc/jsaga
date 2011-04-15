package fr.in2p3.jsaga.adaptor.job.local;


import fr.in2p3.jsaga.adaptor.base.defaults.Default;
import fr.in2p3.jsaga.adaptor.base.usage.Usage;
import fr.in2p3.jsaga.adaptor.job.control.manage.ListableJobAdaptor;
import fr.in2p3.jsaga.adaptor.job.monitor.JobInfoAdaptor;
import fr.in2p3.jsaga.adaptor.job.monitor.JobStatus;
import fr.in2p3.jsaga.adaptor.job.monitor.QueryIndividualJob;
import org.ogf.saga.error.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.Exception;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   LocalJobMonitorAdaptor
* Author: Nicolas DEMESY (nicolas.demesy@bt.com)
* Date:   29 avril 2008
* ***************************************************/

public class LocalJobMonitorAdaptor extends LocalAdaptorAbstract implements QueryIndividualJob, ListableJobAdaptor, JobInfoAdaptor {

	public Usage getUsage() {
    	return null;
    }

    public Default[] getDefaults(Map attributes) throws IncorrectStateException {
        return null;
    }

    public JobStatus getStatus(String nativeJobId) throws TimeoutException, NoSuccessException {
    	// TODO: use LocalJobProcess.getStatus()
    	try {
			LocalJobProcess ljp = LocalAdaptorAbstract.restore(nativeJobId);
			return ljp.getJobStatus();
		} catch (IOException e) {
			throw new NoSuccessException(e);
		} catch (ClassNotFoundException e) {
			throw new NoSuccessException(e);
		}
    	/*
    	File f = new File(getEndcodeFile(nativeJobId));
    	FileInputStream fis;
		try {
			fis = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			// PROCESS Is not finished
			return new LocalJobStatus(nativeJobId, -1);
		}
    	byte[] buf = new byte[(int)f.length()];
    	try {
			int len = fis.read(buf);
        	fis.close();
		} catch (IOException e) {
			throw new NoSuccessException(e);
		}
		// Get return code reading xxx.endcode file minus last character (carriage return)
		return new LocalJobStatus(nativeJobId, Integer.valueOf(new String(buf).substring(0, buf.length-1)));	
		*/					
    }

	public String[] list() throws PermissionDeniedException, TimeoutException,	NoSuccessException {
		// Find all local files named *.pid: running jobs
		// Find all local files named *.endcode: finished jobs
		// TODO: search Process Files *.process
		String filter = "(\\w*).process";
		Pattern p = Pattern.compile(filter);
		String[] files = new File(LocalJobProcess.getRootDir()).list();
		List<String> listeFichiers = new ArrayList<String>();
		for (int i=0; i<files.length;i++) {
			Matcher m = p.matcher(files[i]);
			if ( m.matches()) {
				listeFichiers.add(files[i].substring(0, files[i].lastIndexOf('.')));
				//files[i] = files[i].substring(0, files[i].lastIndexOf('.'));
			}
		}
		return (String[]) listeFichiers.toArray(new String[]{});
		//return files;
	}

	public Integer getExitCode(String nativeJobId)
			throws NotImplementedException, NoSuccessException {
    	try {
			LocalJobProcess ljp = LocalAdaptorAbstract.restore(nativeJobId);
			return ljp.getReturnCode();
		} catch (IOException e) {
			throw new NoSuccessException(e);
		} catch (ClassNotFoundException e) {
			throw new NoSuccessException(e);
		}
	}

	public Date getCreated(String nativeJobId) throws NotImplementedException,
			NoSuccessException {
    	try {
			LocalJobProcess ljp = LocalAdaptorAbstract.restore(nativeJobId);
			return ljp.getCreated();
		} catch (IOException e) {
			throw new NoSuccessException(e);
		} catch (ClassNotFoundException e) {
			throw new NoSuccessException(e);
		}
	}

	public Date getStarted(String nativeJobId) throws NotImplementedException,
			NoSuccessException {
    	try {
			LocalJobProcess ljp = LocalAdaptorAbstract.restore(nativeJobId);
			return ljp.getStarted();
		} catch (IOException e) {
			throw new NoSuccessException(e);
		} catch (ClassNotFoundException e) {
			throw new NoSuccessException(e);
		}
	}

	public Date getFinished(String nativeJobId) throws NotImplementedException,
			NoSuccessException {
    	try {
			LocalJobProcess ljp = LocalAdaptorAbstract.restore(nativeJobId);
			return ljp.getFinished();
		} catch (IOException e) {
			throw new NoSuccessException(e);
		} catch (ClassNotFoundException e) {
			throw new NoSuccessException(e);
		}
	}

	public String[] getExecutionHosts(String nativeJobId)
			throws NotImplementedException, NoSuccessException {
		return new String[] {"localhost"};
	}

}
