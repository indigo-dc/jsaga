package org.ogf.saga.logicalfile;

import org.ogf.saga.URL;
import org.ogf.saga.bootstrap.ImplementationBootstrapLoader;
import org.ogf.saga.error.AlreadyExists;
import org.ogf.saga.error.AuthenticationFailed;
import org.ogf.saga.error.AuthorizationFailed;
import org.ogf.saga.error.BadParameter;
import org.ogf.saga.error.DoesNotExist;
import org.ogf.saga.error.IncorrectURL;
import org.ogf.saga.error.NoSuccess;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.error.PermissionDenied;
import org.ogf.saga.error.Timeout;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.session.Session;
import org.ogf.saga.session.SessionFactory;
import org.ogf.saga.task.TaskMode;
import org.ogf.saga.task.Task;

/**
 * Factory for objects from the logicalfile package.
 */
public abstract class LogicalFileFactory {
    
    private static LogicalFileFactory factory;

    private static synchronized void initializeFactory()
        throws NotImplemented {
        if (factory == null) {
            factory = ImplementationBootstrapLoader.createLogicalFileFactory();
        }
    }

    /**
     * Creates a LogicalFile. To be provided by the implementation.
     * @param session the session handle.
     * @param name location of the file.
     * @param flags the open mode.
     * @return the file instance.
     */
    protected abstract LogicalFile doCreateLogicalFile(Session session,
            URL name, int flags)
        throws NotImplemented, IncorrectURL, 
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, AlreadyExists, DoesNotExist,
            Timeout, NoSuccess;
    
    /**
     * Creates a Directory. To be provided by the implementation.
     * @param session the session handle.
     * @param name location of directory.
     * @param flags the open mode.
     * @return the directory instance.
     */
    protected abstract LogicalDirectory doCreateLogicalDirectory(
            Session session, URL name, int flags)
        throws NotImplemented, IncorrectURL, 
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, AlreadyExists, DoesNotExist, Timeout, NoSuccess;

    /**
     * Creates a Task that creates a LogicalFile.
     * To be provided by the implementation.
     * @param mode the task mode.
     * @param session the session handle.
     * @param name location of the file.
     * @param flags the open mode.
     * @return the task.
     * @exception NotImplemented is thrown when the task version of this
     *     method is not implemented.
     */
    protected abstract Task<LogicalFile> doCreateLogicalFile(
            TaskMode mode, Session session, URL name, int flags)
        throws NotImplemented;
    
    /**
     * Creates a Task that creates a LogicalDirectory.
     * To be provided by the implementation.
     * @param mode the task mode.
     * @param session the session handle.
     * @param name location of directory.
     * @param flags the open mode.
     * @return the task.
     * @exception NotImplemented is thrown when the task version of this
     *     method is not implemented.
     */
    protected abstract Task<LogicalDirectory>
            doCreateLogicalDirectory(
                    TaskMode mode, Session session, URL name, int flags)
            throws NotImplemented;
    
    /**
     * Creates a LogicalFile.
     * @param session the session handle.
     * @param name location of the file.
     * @param flags the open mode.
     * @return the file instance.
     */
    public static LogicalFile createLogicalFile(Session session, URL name,
            int flags)
        throws NotImplemented, IncorrectURL, 
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, AlreadyExists, DoesNotExist,
            Timeout, NoSuccess {
        initializeFactory();
        return factory.doCreateLogicalFile(session, name, flags);
    }

    /**
     * Creates a LogicalDirectory.
     * @param session the session handle.
     * @param name location of the directory.
     * @param flags the open mode.
     * @return the directory instance.
     */
    public static LogicalDirectory createLogicalDirectory(Session session,
            URL name, int flags)
        throws NotImplemented, IncorrectURL,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        initializeFactory();
        return factory.doCreateLogicalDirectory(session, name, flags);
    }
    
    /**
     * Creates a Task that creates a LogicalFile.
     * @param mode the task mode.
     * @param session the session handle.
     * @param name location of the file.
     * @param flags the open mode.
     * @return the task.
     * @exception NotImplemented is thrown when the task version of this
     *     method is not implemented.
     */
    public static Task<LogicalFile> createLogicalFile(
            TaskMode mode, Session session, URL name, int flags)
        throws NotImplemented {
        initializeFactory();
        return factory.doCreateLogicalFile(mode, session, name, flags);
    }

    /**
     * Creates a Task that creates a LogicalDirectory.
     * @param mode the task mode.
     * @param session the session handle.
     * @param name location of the directory.
     * @param flags the open mode.
     * @return the task.
     * @exception NotImplemented is thrown when the task version of this
     *     method is not implemented.
     */
    public static Task<LogicalDirectory> createLogicalDirectory(
            TaskMode mode, Session session, URL name, int flags)
        throws NotImplemented {
        initializeFactory();
        return factory.doCreateLogicalDirectory(mode, session, name, flags);
    }
    
    /**
     * Creates a LogicalFile using READ open mode.
     * @param session the session handle.
     * @param name location of the file.
     * @return the file instance.
     */
    public static LogicalFile createLogicalFile(Session session, URL name)
        throws NotImplemented, IncorrectURL, 
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, AlreadyExists, DoesNotExist,
            Timeout, NoSuccess {
        initializeFactory();
        return factory.doCreateLogicalFile(session, name, Flags.READ.getValue());
    }

    /**
     * Creates a LogicalDirectory using READ open mode.
     * @param session the session handle.
     * @param name location of the directory.
     * @return the directory instance.
     */
    public static LogicalDirectory createLogicalDirectory(Session session,
            URL name)
        throws NotImplemented, IncorrectURL,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        initializeFactory();
        return factory.doCreateLogicalDirectory(session, name, Flags.READ.getValue());
    }
    
    /**
     * Creates a Task that creates a LogicalFile using READ open mode.
     * @param mode the task mode.
     * @param session the session handle.
     * @param name location of the file.
     * @return the task.
     * @exception NotImplemented is thrown when the task version of this
     *     method is not implemented.
     */
    public static Task<LogicalFile> createLogicalFile(
            TaskMode mode, Session session, URL name)
        throws NotImplemented {
        initializeFactory();
        return factory.doCreateLogicalFile(mode, session, name, Flags.READ.getValue());
    }

    /**
     * Creates a Task that creates a LogicalDirectory using READ open mode.
     * @param mode the task mode.
     * @param session the session handle.
     * @param name location of the directory.
     * @return the task.
     * @exception NotImplemented is thrown when the task version of this
     *     method is not implemented.
     */
    public static Task<LogicalDirectory> createLogicalDirectory(
            TaskMode mode, Session session, URL name)
        throws NotImplemented {
        initializeFactory();
        return factory.doCreateLogicalDirectory(mode, session, name, Flags.READ.getValue());
    }
    
    /**
     * Creates a LogicalFile using the default session.
     * @param name location of the file.
     * @param flags the open mode.
     * @return the file instance.
     */
    public static LogicalFile createLogicalFile(URL name,int flags)
        throws NotImplemented, IncorrectURL, 
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, AlreadyExists, DoesNotExist,
            Timeout, NoSuccess {
        Session session = SessionFactory.createSession();
        initializeFactory();
        return factory.doCreateLogicalFile(session, name, flags);
    }

    /**
     * Creates a LogicalDirectory using the default session.
     * @param name location of the directory.
     * @param flags the open mode.
     * @return the directory instance.
     */
    public static LogicalDirectory createLogicalDirectory(URL name, int flags)
        throws NotImplemented, IncorrectURL,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        Session session = SessionFactory.createSession();
        initializeFactory();
        return factory.doCreateLogicalDirectory(session, name, flags);
    }
    
    /**
     * Creates a Task that creates a LogicalFile using the default session.
     * @param mode the task mode.
     * @param name location of the file.
     * @param flags the open mode.
     * @return the task.
     * @exception NotImplemented is thrown when the task version of this
     *     method is not implemented.
     * @exception NoSuccess is thrown when the default session could not be
     *     created.
     */
    public static Task<LogicalFile> createLogicalFile(
            TaskMode mode, URL name, int flags)
        throws NotImplemented, NoSuccess {
        Session session = SessionFactory.createSession();
        initializeFactory();
        return factory.doCreateLogicalFile(mode, session, name, flags);
    }

    /**
     * Creates a Task that creates a LogicalDirectory using the default session.
     * @param mode the task mode.
     * @param name location of the directory.
     * @param flags the open mode.
     * @return the task.
     * @exception NotImplemented is thrown when the task version of this
     *     method is not implemented.
     * @exception NoSuccess is thrown when the default session could not be
     *     created.
     */
    public static Task<LogicalDirectory> createLogicalDirectory(
            TaskMode mode, URL name, int flags)
        throws NotImplemented, NoSuccess {
        Session session = SessionFactory.createSession();
        initializeFactory();
        return factory.doCreateLogicalDirectory(mode, session, name, flags);
    }
    
    /**
     * Creates a LogicalFile using READ open mode, using the default session .
     * @param name location of the file.
     * @return the file instance.
     */
    public static LogicalFile createLogicalFile(URL name)
        throws NotImplemented, IncorrectURL, 
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, AlreadyExists, DoesNotExist,
            Timeout, NoSuccess {
        Session session = SessionFactory.createSession();
        initializeFactory();
        return factory.doCreateLogicalFile(session, name, Flags.READ.getValue());
    }

    /**
     * Creates a LogicalDirectory using READ open mode, using the default session.
     * @param name location of the directory.
     * @return the directory instance.
     */
    public static LogicalDirectory createLogicalDirectory(URL name)
        throws NotImplemented, IncorrectURL,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        Session session = SessionFactory.createSession();
        initializeFactory();
        return factory.doCreateLogicalDirectory(session, name, Flags.READ.getValue());
    }
    
    /**
     * Creates a Task that creates a LogicalFile using READ open mode,
     * using the default session.
     * @param mode the task mode.
     * @param name location of the file.
     * @return the task.
     * @exception NotImplemented is thrown when the task version of this
     *     method is not implemented.
     * @exception NoSuccess is thrown when the default session could not be
     *     created.
     */
    public static Task<LogicalFile> createLogicalFile(TaskMode mode, URL name)
        throws NotImplemented, NoSuccess {
        Session session = SessionFactory.createSession();
        initializeFactory();
        return factory.doCreateLogicalFile(mode, session, name, Flags.READ.getValue());
    }

    /**
     * Creates a Task that creates a LogicalDirectory using READ open mode,
     * using the default session.
     * @param mode the task mode.
     * @param name location of the directory.
     * @return the task.
     * @exception NotImplemented is thrown when the task version of this
     *     method is not implemented.
     * @exception NoSuccess is thrown when the default session could not be
     *     created.
     */
    public static Task<LogicalDirectory> createLogicalDirectory(TaskMode mode, URL name)
        throws NotImplemented, NoSuccess {
        Session session = SessionFactory.createSession();
        initializeFactory();
        return factory.doCreateLogicalDirectory(mode, session, name, Flags.READ.getValue());
    }
}
