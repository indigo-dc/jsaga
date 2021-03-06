package fr.in2p3.jsaga.impl.job.staging.mgr;

import fr.in2p3.jsaga.adaptor.job.control.staging.StagingJobAdaptor;
import fr.in2p3.jsaga.adaptor.job.control.staging.StagingTransfer;
import fr.in2p3.jsaga.impl.job.instance.AbstractSyncJobImpl;
import org.ogf.saga.error.*;
import org.ogf.saga.file.*;
import org.ogf.saga.job.JobDescription;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.session.Session;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;

/* ***************************************************
 * *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
 * ***             http://cc.in2p3.fr/             ***
 * ***************************************************
 * File:   DataStagingManagerThroughSandbox
 * Author: Sylvain Reynaud (sreynaud@in2p3.fr)
 * Date:   15 mars 2010
 * ***************************************************
 * Description:                                      */
/**
 *
 */
public class DataStagingManagerThroughSandbox implements DataStagingManager {
    protected StagingJobAdaptor m_adaptor;

    public DataStagingManagerThroughSandbox(StagingJobAdaptor adaptor, String uniqId) throws NotImplementedException, BadParameterException, NoSuccessException {
        m_adaptor = adaptor;
    }

    public JobDescription modifyJobDescription(final JobDescription jobDesc) throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException, BadParameterException, TimeoutException, NoSuccessException {
        return jobDesc;
    }

    public void postStaging(AbstractSyncJobImpl job, String nativeJobId) throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException, BadParameterException, DoesNotExistException, TimeoutException, IncorrectStateException, NoSuccessException {
        // for each output file
        for (StagingTransfer transfer : m_adaptor.getOutputStagingTransfer(nativeJobId)) {
            transfer(job.getSession(), transfer);
        }
    }

    public Directory cleanup(AbstractSyncJobImpl job, String nativeJobId) throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException, BadParameterException, DoesNotExistException, TimeoutException, IncorrectStateException, NoSuccessException {
        // for each input file
        for (StagingTransfer transfer : m_adaptor.getInputStagingTransfer(nativeJobId)) {
            remove(job.getSession(), transfer.getTo());
        }

        // for each output file
        for (StagingTransfer transfer : m_adaptor.getOutputStagingTransfer(nativeJobId)) {
            remove(job.getSession(), transfer.getFrom());
        }

        // remove base directory
        String stagingDir = m_adaptor.getStagingDirectory(nativeJobId);
        if (stagingDir != null) {
            URL url = URLFactory.createURL(JSAGA_FACTORY, stagingDir);
            Directory dir = null;
            try {
                dir = FileFactory.createDirectory(JSAGA_FACTORY, job.getSession(), url);
                return dir;
            } catch (IncorrectURLException | AlreadyExistsException e) {
                throw new NoSuccessException(e);
            }
        }
        return null;
    }

	public StagingTransfer[] getOutputStagingTransfer(String nativeJobId) throws PermissionDeniedException, TimeoutException, NoSuccessException {
		return m_adaptor.getOutputStagingTransfer(nativeJobId);
	}

    protected static void transfer(Session session, StagingTransfer transfer) throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException, BadParameterException, DoesNotExistException, TimeoutException, IncorrectStateException, NoSuccessException {
        int append = (transfer.isAppend() ? Flags.APPEND : Flags.NONE).getValue();
        File file = null;
        try {
            URL from = URLFactory.createURL(JSAGA_FACTORY, pathToURL(transfer.getFrom()));
            URL to = URLFactory.createURL(JSAGA_FACTORY, pathToURL(transfer.getTo()));
            file = FileFactory.createFile(JSAGA_FACTORY, session, from, Flags.NONE.getValue());
            file.copy(to, append);
        } catch (AlreadyExistsException | IncorrectURLException e) {
            throw new NoSuccessException(e);
        } finally{
        	if(file != null){
	        	try{
	        		file.close();
	        	}catch (Exception e) {
					// Ignore it: A problem during the close should not be a problem.
				}
        	}
        }
    }
    private static String pathToURL(String path) {
        boolean isWindowsAbsolutePath = (path.length()>2
                && Character.isLetter(path.charAt(0))
                && path.charAt(1)==':'
                && (path.charAt(2)=='\\' || path.charAt(2)=='/')
                && path.charAt(3)!='/');
        if (path.contains(":/") && !isWindowsAbsolutePath) {
            // returns URI
            return path;
        } else {
            // convert path to URI
            return new java.io.File(path).toURI().toString();
        }
    }

    private static void remove(Session session, String url) throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException, BadParameterException, DoesNotExistException, TimeoutException, IncorrectStateException, NoSuccessException {
    	File file = null;
    	try {
            URL urlToRemove = URLFactory.createURL(JSAGA_FACTORY, url);
            file = FileFactory.createFile(JSAGA_FACTORY, session, urlToRemove, Flags.NONE.getValue());
            file.remove();
        } catch (AlreadyExistsException | IncorrectURLException e) {
            throw new NoSuccessException(e);
        } finally{
        	if(file != null){
	        	try{
	        		file.close();
	        	}catch (Exception e) {
					// Ignore it: A problem during the close should not be a problem.
				}
        	}
        }
    }
}
