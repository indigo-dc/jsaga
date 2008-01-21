package org.ogf.saga.error;

import org.ogf.saga.SagaObject;

/**
 * This exception indicates that a method fails because none of the available
 * session contexts could succesfully be used for authentication.
 */
public class AuthenticationFailed extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs an AuthenticationFailed exception.
     */
    public AuthenticationFailed() {
        super(AUTHENTICATION_FAILED);
    }

    /**
     * Constructs an AuthenticationFailed exception with the specified detail
     * message.
     * @param message the detail message.
     */
    public AuthenticationFailed(String message) {
        super(AUTHENTICATION_FAILED, message);
    }

    /**
     * Constructs an AuthenticationFailed exception with the specified cause.
     * @param cause the cause.
     */
    public AuthenticationFailed(Throwable cause) {
        super(AUTHENTICATION_FAILED, cause);
    }

    /**
     * Constructs an AuthenticationFailed exception with the specified detail
     * message and cause. 
     * @param message the detail message.
     * @param cause the cause.
     */
    public AuthenticationFailed(String message, Throwable cause) {
        super(AUTHENTICATION_FAILED, message, cause);
    }

    /**
     * Constructs an AuthenticationFailed exception with the specified detail
     * message and associated SAGA object.
     * @param message the detail message.
     * @param object the associated SAGA object.
     */
    public AuthenticationFailed(String message, SagaObject object) {
        super(AUTHENTICATION_FAILED, message, object);
    }
}
