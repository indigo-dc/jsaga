package org.ogf.saga.session;

import org.ogf.saga.SagaObject;
import org.ogf.saga.context.Context;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.TimeoutException;

/**
 * A session isolates independent sets of SAGA objects from each other, and
 * supports management of security contexts.
 */
public interface Session extends SagaObject {
    /**
     * Attaches a deep copy of the specified security context to the session.
     * 
     * @param context
     *      the context to be added.
     * @exception NoSuccessException
     *      is thrown when the implementation is not able to initialize
     *      the context, and cannot use the context as-is.
     * @exception TimeoutException
     *      is thrown if the context initialization implies a remote operation,
     *      and that operation times out.
     */
    public void addContext(Context context) throws NoSuccessException, TimeoutException;

    /**
     * Detaches the specified security context from the session.
     * 
     * @param context
     *      the context to be removed.
     * @exception DoesNotExistException
     *      is thrown when the session does not contain the specified
     *      context.
     */
    public void removeContext(Context context) throws DoesNotExistException;

    /**
     * Retrieves all contexts attached to the session. An empty array is
     * returned if no context is attached. The contexts in the returned list
     * are deep copies of the session's context.
     * 
     * @return
     *      a list of contexts.
     */
    public Context[] listContexts();

    /**
     * Closes a SAGA session. Deviation from the SAGA API, which does not have
     * this method. However, middleware may for instance have threads which may
     * need to be terminated, or the application will hang. This may not be the
     * right place for it, but there is no other place ...
     */
    public void close();

    /**
     * Closes a SAGA session. Deviation from the SAGA API, which does not have
     * this method. However, middleware may for instance have threads which may
     * need to be terminated, or the application will hang.
     * 
     * @param timeoutInSeconds
     *      the timeout in seconds.
     */
    public void close(float timeoutInSeconds);
}
