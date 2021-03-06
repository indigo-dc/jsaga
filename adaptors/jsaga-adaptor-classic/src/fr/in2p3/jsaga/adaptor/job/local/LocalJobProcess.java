package fr.in2p3.jsaga.adaptor.job.local;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.ogf.saga.error.NoSuccessException;

import fr.in2p3.jsaga.adaptor.job.BadResource;
import fr.in2p3.jsaga.adaptor.job.control.staging.StagingTransfer;
import fr.in2p3.jsaga.adaptor.job.monitor.JobStatus;

/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   LocalJobProcess
* Author: Lionel Schwarz (lionel.schwarz@in2p3.fr)
* Date:   15 avril 2011
* ***************************************************/

public class LocalJobProcess implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 442420832799282097L;
	protected String m_pid;
	protected String m_jobId;
	protected int m_returnCode;
	protected Date m_created;
	protected String m_jobDesc;
	
	protected static final String _rootDir = System.getProperty("java.io.tmpdir") + "/jsaga/adaptor/local";
    public static final String STAGING_IN = "In";
    public static final String STAGING_OUT = "Out";
    
	
	public static final int PROCESS_DONE_OK = 0;
	public static final int PROCESS_RUNNING = -1;
	public static final int PROCESS_STOPPED = -2;
	public static final int PROCESS_UNKNOWNSTATE = -99;

	public static final String PID_SUFFIX = "pid";
	public static final String ENDCODE_SUFFIX = "endcode";
	public static final String PROCESS_SUFFIX = "process";
	
	public LocalJobProcess(String jobId, String jobDesc) {
		m_jobId = jobId;
		m_jobDesc = jobDesc;
		m_returnCode = -1;
		m_pid = null;
	}
	public LocalJobProcess(String jobId) {
		this(jobId, null);
	}
	
    public static String getRootDir_Bash() {
        if (System.getProperty("os.name").startsWith("Windows")) {
            String path = new File(_rootDir).toURI().getPath();
            return "/cygdrive/"+path.charAt(1)+path.substring(3);
        } else {
            return _rootDir;
        }
    }
    public static String getRootDir() {
    	return _rootDir;
    }
    public String getWorkingDirectory() throws IOException {
    	if (isUserWorkingDirectory()) {
			return getValue("_WorkingDirectory");
		} else {
			return getGeneratedWorkingDirectory();
		}
    }
    public String getGeneratedWorkingDirectory() {
		return getRootDir() + "/" + m_jobId;
    }
    public String getGeneratedFilename() {
		return getGeneratedWorkingDirectory();
    }
    
    // Create working directory only if not specified in job description
	public void createWorkingDirectory() throws IOException {
		if (!isUserWorkingDirectory()) {
	    	new File(getWorkingDirectory()).mkdirs();
		}
	}
    
	public boolean isUserWorkingDirectory() throws IOException {
		try {
			getValue("_WorkingDirectory");
			return true;
		} catch (NoSuchElementException e) {
	    	return false;
		}
	}
	
    public String getFile(String suffix) {
    	return getGeneratedFilename() + "." + suffix;
    }

    public String getPidFile() {
    	return getFile(PID_SUFFIX);
    }
    
    public String getEndcodeFile() {
    	return getFile(ENDCODE_SUFFIX);
    }

    public String getSerializeFile() {
    	return getFile(PROCESS_SUFFIX);
    }
    

    public String getJobId() {
    	return m_jobId;
    }
    public String getJobDesc() {
    	return m_jobDesc;
    }

    public StagingTransfer[] getStaging(String InOrOut) throws NoSuccessException {
    	StagingTransfer[] st = new StagingTransfer[]{};
        try {
        	ArrayList transfersArrayList = new ArrayList();
        	String valDS = getValue("_DataStaging");
        	String [] transfers = valDS.substring(0, valDS.length()-1).split(",");
     	   	for (int i=0; i<transfers.length; i++) {
     	   		String[] fromTo = transfers[i].split("[<>]");
     	   		if (transfers[i].contains(">") && InOrOut.equals(STAGING_IN)) {
     	   			transfersArrayList.add(new StagingTransfer(fromTo[0], toURL(fromTo[1]), false));
     	   		} else if (transfers[i].contains("<") && InOrOut.equals(STAGING_OUT)) {
     	   			transfersArrayList.add(new StagingTransfer(toURL(fromTo[1]), fromTo[0], false));
     	   		}
     	   	}
     	   	return (StagingTransfer[]) transfersArrayList.toArray(st);
        } catch (IOException e) {
            throw new NoSuccessException(e);
        } catch (NoSuchElementException e) {
	        return st;
        }
	}
	
    public StagingTransfer[] getInputStaging() throws NoSuccessException {
    	return getStaging(STAGING_IN);
    }
    
    public StagingTransfer[] getOutputStaging() throws NoSuccessException {
    	return getStaging(STAGING_OUT);
    }
    
    public String getPid() throws NoSuccessException {
		if (m_pid != null) return m_pid;
    	File f = new File(getPidFile());
    	FileInputStream fis;
		try {
			fis = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			throw new NoSuccessException(e);
		}
    	byte[] buf = new byte[(int)f.length()];
    	try {
			int len = fis.read(buf);
        	fis.close();
		} catch (IOException e) {
			throw new NoSuccessException(e);
		}
		// Get PID reading yyy.pid file minus last character (carriage return)
		m_pid = new String(buf).trim();
		return m_pid;
	}

	public void setReturnCode(int returnCode) {
		this.m_returnCode = returnCode;
	}

	public int getReturnCode() throws NoSuccessException {
		if (m_returnCode >= 0) return m_returnCode; // final state
    	File f = new File(getEndcodeFile());
    	FileInputStream fis;
		try {
			fis = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			// PROCESS Is not finished
			return m_returnCode;
		}
    	byte[] buf = new byte[(int)f.length()];
    	try {
			int len = fis.read(buf);
        	fis.close();
		} catch (IOException e) {
			throw new NoSuccessException(e);
		}
		// Get return code reading yyy.endcode file minus last character (carriage return)
		m_returnCode = Integer.valueOf(new String(buf).trim());
		return m_returnCode;
	}
	
	public JobStatus getJobStatus() throws NoSuccessException {
		int status = getReturnCode();
		if (status <0) { // either running or suspended
			try {
				status = getProcessStatus();
			} catch (Exception e) {
				return new LocalJobStatus(m_jobId, LocalJobProcess.PROCESS_UNKNOWNSTATE);						
			}
		}
		return new LocalJobStatus(m_jobId, status);						
	}
	
	
    public void setCreated(Date created) {
		this.m_created = created;
	}

	public Date getCreated() {
		return m_created;
	}

	public Date getStarted() throws NoSuccessException {
		long startTime = new File(this.getPidFile()).lastModified();
		if (startTime == 0) {
			return null;
		}
		return new Date(startTime);
	}

	public Date getFinished() throws NoSuccessException {
		long endTime = new File(this.getEndcodeFile()).lastModified();
		if (endTime == 0) {
			return null;
		}
		return new Date(endTime);
	}
	
    public void clean() {
    	try {
    		new File(getPidFile()).delete();
    	} catch (Exception e) {
    		// ignore
    	}
    	try {
    		new File(getEndcodeFile()).delete();
    	} catch (Exception e) {
    		// ignore
    	}
    	try {
    		new File(getSerializeFile()).delete();
    	} catch (Exception e) {
    		// ignore
    	}
    	try {
    		new File(getWorkingDirectory()).delete();
    	} catch (Exception e){
    		// ignore
    	}
    }

	public int getProcessStatus() throws NoSuccessException {
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("/proc/"+getPid()+"/stat"))));
			String status = br.readLine().split(" ")[2];
			if (status.startsWith("S") || status.startsWith("R")) { return PROCESS_RUNNING;}
			if (status.startsWith("T")) { return PROCESS_STOPPED;}
			throw new NoSuccessException("Unknown status: "+status);
		} catch (FileNotFoundException e) {
			throw new NoSuccessException(e);
		} catch (IOException e) {
			throw new NoSuccessException(e);
		}
	}
	
	public void checkResources() throws BadResource, NoSuccessException {
		try {
			File wd = new File(getValue("_WorkingDirectory"));
			if (!wd.exists() || !wd.isDirectory()) {
				throw new BadResource("Directory does not exist:");
			}
		} catch (NoSuchElementException e) {
			// ignore
		} catch (IOException e) {
			throw new NoSuccessException(e);
		}
	}

    protected String getValue(String searchedKey) throws IOException, NoSuchElementException {
    	Properties jobProps = new Properties();
    	jobProps.load(new ByteArrayInputStream(m_jobDesc.getBytes()));
    	Enumeration e = jobProps.propertyNames();
        while (e.hasMoreElements()){
           String key = (String)e.nextElement();
           String val = (String)jobProps.getProperty(key);
           if (key.equals(searchedKey)) {
        	   return val;
           }
        }
        throw new NoSuchElementException();
    }

	protected String toURL(String filename) throws NoSuccessException {
		if (filename.startsWith("file:")) {
			return filename;
		} else {
			try {
				return "file://" + getWorkingDirectory() + "/" + filename;
			} catch (IOException e) {
				throw new NoSuccessException(e);
			}
		}
	}

}
