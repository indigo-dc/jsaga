package org.ogf.saga.job;

import org.ogf.saga.bootstrap.ImplementationBootstrapLoader;
import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.IncorrectURLException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.session.Session;
import org.ogf.saga.session.SessionFactory;
import org.ogf.saga.task.TaskMode;
import org.ogf.saga.task.Task;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;

/**
 * Factory for objects from the job package.
 */
public abstract class JobFactory {

    private static JobFactory factory;

    private static synchronized void initializeFactory()
            throws NotImplementedException, NoSuccessException {
        if (factory == null) {
            factory = ImplementationBootstrapLoader.createJobFactory();
        }
    }

    /**
     * Creates a job description. To be provided by the implementation.
     * 
     * @return the job description.
     */
    protected abstract JobDescription doCreateJobDescription()
            throws NotImplementedException, NoSuccessException;

    /**
     * Creates a job service. To be provided by the implementation.
     * 
     * @param session
     *            the session handle.
     * @param rm
     *            contact string for the resource manager.
     * @return the job service.
     */
    protected abstract JobService doCreateJobService(Session session, URL rm)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException;

    /**
     * Creates a task that creates a job service. To be provided by the
     * implementation.
     * 
     * @param mode
     *            the task mode.
     * @param session
     *            the session handle.
     * @param rm
     *            contact string for the resource manager.
     * @return the task.
     * @exception NotImplementedException
     *                is thrown when the task version of this method is not
     *                implemented.
     */
    protected abstract Task<JobFactory, JobService> doCreateJobService(
            TaskMode mode, Session session, URL rm)
            throws NotImplementedException;

    /**
     * Creates a job description.
     * 
     * @return
     *      the job description.
     * @exception NotImplementedException
     *      is thrown when this method is not implemented.
     * @throws NoSuccessException
     *      is thrown when the Saga factory could not be created.
     */
    public static JobDescription createJobDescription()
            throws NotImplementedException, NoSuccessException {
        initializeFactory();
        return factory.doCreateJobDescription();
    }

    /**
     * Creates a job service.
     * 
     * @param session
     *      the session handle.
     * @param rm
     *      contact string for the resource manager.
     * @return
     *      the job service.
     * @exception NotImplementedException
     *      is thrown if the implementation does not provide an
     *      implementation of this method.
     * @exception PermissionDeniedException
     *      is thrown when the method failed because the identity used did
     *      not have sufficient permissions to perform the operation
     *      successfully.
     * @exception AuthorizationFailedException
     *      is thrown when none of the available contexts of the
     *      used session could be used for successful authorization.
     *      This error indicates that the resource could not be accessed
     *      at all, and not that an operation was not available due to
     *      restricted permissions.
     * @exception AuthenticationFailedException
     *      is thrown when operation failed because none of the available
     *      session contexts could successfully be used for authentication.
     * @exception TimeoutException
     *      is thrown when a remote operation did not complete successfully
     *      because the network communication or the remote service timed
     *      out.
     * @exception IncorrectURLException
     *      is thrown when an implementation cannot handle the specified
     *      protocol, or that access to the specified entity via the
     *      given protocol is impossible.
     * @exception NoSuccessException
     *      is thrown when the operation was not successfully performed,
     *      and none of the other exceptions apply.
     */
    public static JobService createJobService(Session session, URL rm)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        initializeFactory();
        return factory.doCreateJobService(session, rm);
    }

    /**
     * Creates a job service using the default contact string.
     * 
     * @param session
     *      the session handle.
     * @return
     *      the job service.
     * @exception NotImplementedException
     *      is thrown if the implementation does not provide an
     *      implementation of this method.
     * @exception PermissionDeniedException
     *      is thrown when the method failed because the identity used did
     *      not have sufficient permissions to perform the operation
     *      successfully.
     * @exception AuthorizationFailedException
     *      is thrown when none of the available contexts of the
     *      used session could be used for successful authorization.
     *      This error indicates that the resource could not be accessed
     *      at all, and not that an operation was not available due to
     *      restricted permissions.
     * @exception AuthenticationFailedException
     *      is thrown when operation failed because none of the available
     *      session contexts could successfully be used for authentication.
     * @exception TimeoutException
     *      is thrown when a remote operation did not complete successfully
     *      because the network communication or the remote service timed
     *      out.
     * @exception IncorrectURLException
     *      is thrown when an implementation cannot handle the specified
     *      protocol, or that access to the specified entity via the
     *      given protocol is impossible.
     * @exception NoSuccessException
     *      is thrown when the operation was not successfully performed,
     *      and none of the other exceptions apply.
     */
    public static JobService createJobService(Session session)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        URL url;
        try {
            url = URLFactory.createURL("");
        } catch (Throwable e) {
            throw new NoSuccessException("Should not happen", e);
        }
        initializeFactory();
        return factory.doCreateJobService(session, url);
    }

    /**
     * Creates a job service, using the default session.
     * 
     * @param rm
     *      contact string for the resource manager.
     * @return
     *      the job service.
     * @exception NotImplementedException
     *      is thrown if the implementation does not provide an
     *      implementation of this method.
     * @exception PermissionDeniedException
     *      is thrown when the method failed because the identity used did
     *      not have sufficient permissions to perform the operation
     *      successfully.
     * @exception AuthorizationFailedException
     *      is thrown when none of the available contexts of the
     *      used session could be used for successful authorization.
     *      This error indicates that the resource could not be accessed
     *      at all, and not that an operation was not available due to
     *      restricted permissions.
     * @exception AuthenticationFailedException
     *      is thrown when operation failed because none of the available
     *      session contexts could successfully be used for authentication.
     * @exception TimeoutException
     *      is thrown when a remote operation did not complete successfully
     *      because the network communication or the remote service timed
     *      out.
     * @exception IncorrectURLException
     *      is thrown when an implementation cannot handle the specified
     *      protocol, or that access to the specified entity via the
     *      given protocol is impossible.
     * @exception NoSuccessException
     *      is thrown when the operation was not successfully performed,
     *      and none of the other exceptions apply.
     */
    public static JobService createJobService(URL rm)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        Session session = SessionFactory.createSession();
        initializeFactory();
        return factory.doCreateJobService(session, rm);
    }

    /**
     * Creates a job service, using the default session and default contact
     * string.
     * 
     * @return
     *      the job service.
     * @exception NotImplementedException
     *      is thrown if the implementation does not provide an
     *      implementation of this method.
     * @exception PermissionDeniedException
     *      is thrown when the method failed because the identity used did
     *      not have sufficient permissions to perform the operation
     *      successfully.
     * @exception AuthorizationFailedException
     *      is thrown when none of the available contexts of the
     *      used session could be used for successful authorization.
     *      This error indicates that the resource could not be accessed
     *      at all, and not that an operation was not available due to
     *      restricted permissions.
     * @exception AuthenticationFailedException
     *      is thrown when operation failed because none of the available
     *      session contexts could successfully be used for authentication.
     * @exception TimeoutException
     *      is thrown when a remote operation did not complete successfully
     *      because the network communication or the remote service timed
     *      out.
     * @exception IncorrectURLException
     *      is thrown when an implementation cannot handle the specified
     *      protocol, or that access to the specified entity via the
     *      given protocol is impossible.
     * @exception NoSuccessException
     *      is thrown when the operation was not successfully performed,
     *      and none of the other exceptions apply.
     */
    public static JobService createJobService() throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            TimeoutException, NoSuccessException {
        URL url;
        try {
            url = URLFactory.createURL("");
        } catch (Throwable e) {
            throw new NoSuccessException("Should not happen", e);
        }
        Session session = SessionFactory.createSession();
        initializeFactory();
        return factory.doCreateJobService(session, url);
    }

    /**
     * Creates a task that creates a job service.
     * 
     * @param mode
     *            the task mode.
     * @param session
     *            the session handle.
     * @param rm
     *            contact string for the resource manager.
     * @return the task.
     * @exception NotImplementedException
     *                is thrown when the task version of this method is not
     *                implemented.
     * @throws NoSuccessException
     *             is thrown when the Saga factory could not be created.
     */
    public static Task<JobFactory, JobService> createJobService(TaskMode mode,
            Session session, URL rm) throws NotImplementedException,
            NoSuccessException {
        initializeFactory();
        return factory.doCreateJobService(mode, session, rm);
    }

    /**
     * Creates a task that creates a job service, using a default contact
     * string.
     * 
     * @param mode
     *            the task mode.
     * @param session
     *            the session handle.
     * @return the task.
     * @exception NotImplementedException
     *                is thrown when the task version of this method is not
     *                implemented.
     * @throws NoSuccessException
     *             is thrown when the Saga factory could not be created.
     */
    public static Task<JobFactory, JobService> createJobService(TaskMode mode,
            Session session) throws NotImplementedException, NoSuccessException {
        URL url;
        try {
            url = URLFactory.createURL("");
        } catch (Throwable e) {
            throw new NoSuccessException("Should not happen", e);
        }
        initializeFactory();
        return factory.doCreateJobService(mode, session, url);
    }

    /**
     * Creates a task that creates a job service, using the default session.
     * 
     * @param mode
     *            the task mode.
     * @param rm
     *            contact string for the resource manager.
     * @return the task.
     * @exception NotImplementedException
     *                is thrown when the task version of this method is not
     *                implemented.
     * @exception NoSuccessException
     *                is thrown when the Saga factory could not be created or
     *                when the default session could not be created.
     */
    public static Task<JobFactory, JobService> createJobService(TaskMode mode,
            URL rm) throws NotImplementedException, NoSuccessException {
        Session session = SessionFactory.createSession();
        initializeFactory();
        return factory.doCreateJobService(mode, session, rm);
    }

    /**
     * Creates a task that creates a job service, using the default session and
     * default contact string.
     * 
     * @param mode
     *            the task mode.
     * @return the task.
     * @exception NotImplementedException
     *                is thrown when the task version of this method is not
     *                implemented.
     * @exception NoSuccessException
     *                is thrown when the Saga factory could not be created or
     *                when the default session could not be created.
     */
    public static Task<JobFactory, JobService> createJobService(TaskMode mode)
            throws NotImplementedException, NoSuccessException {
        URL url;
        try {
            url = URLFactory.createURL("");
        } catch (Throwable e) {
            throw new NoSuccessException("Should not happen", e);
        }
        Session session = SessionFactory.createSession();
        initializeFactory();
        return factory.doCreateJobService(mode, session, url);
    }
}
