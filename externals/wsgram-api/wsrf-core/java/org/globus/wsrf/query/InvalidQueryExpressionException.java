/*
 * Portions of this file Copyright 1999-2005 University of Chicago
 * Portions of this file Copyright 1999-2005 The University of Southern California.
 *
 * This file or a portion of this file is licensed under the
 * terms of the Globus Toolkit Public License, found at
 * http://www.globus.org/toolkit/download/license.html.
 * If you redistribute this file, with or without
 * modifications, you must include this notice in the file.
 */
package org.globus.wsrf.query;

/**
 * This exception is raised when the query expression is invalid.
 */
public class InvalidQueryExpressionException extends QueryException {

    /**
     * Creates a InvalidQueryExpressionException without error message.
     */
    public InvalidQueryExpressionException() {
    }

    /**
     * Creates a InvalidQueryExpressionException with a given error message.
     *
     * @param message error message
     */
    public InvalidQueryExpressionException(String message) {
        super(message);
    }

    /**
     * Creates a InvalidQueryExpressionException with a given error message
     * and nested exception.
     *
     * @param message error message
     * @param exception nested exception/
     */
    public InvalidQueryExpressionException(String message,
                                           Throwable exception) {
        super(message, exception);
    }

    /**
     * Creates a InvalidQueryExpressionException from a nested exception.
     *
     * @param exception nested exception
     */
    public InvalidQueryExpressionException(Throwable exception) {
        super("", exception);
    }

}
