package org.ogf.saga.error;

import org.ogf.saga.SagaObject;

/**
 * This exception indicates that a remote operation did not complete
 * successfully because the network communication or the remote service
 * timed out.
 */
public class Timeout extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a Timeout exception.
     */
    public Timeout() {
        super(TIMEOUT);
    }

    /**
     * Constructs a Timeout exception with the specified detail
     * message.
     * @param message the detail message.
     */
    public Timeout(String message) {
        super(TIMEOUT, message);
    }

    /**
     * Constructs a Timeout exception with the specified cause.
     * @param cause the cause.
     */
    public Timeout(Throwable cause) {
        super(TIMEOUT, cause);
    }

    /**
     * Constructs a Timeout exception with the specified detail
     * message and cause.
     * @param message the detail message.
     * @param cause the cause.
     * 
     */
    public Timeout(String message, Throwable cause) {
        super(TIMEOUT, message, cause);
    }
    
    /**
     * Constructs a Timeout exception with the specified detail
     * message and associated SAGA object.
     * @param message the detail message.
     * @param object the associated SAGA object.
     */
    public Timeout(String message, SagaObject object) {
        super(TIMEOUT, message, object);
    }
}
