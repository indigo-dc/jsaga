package org.ogf.saga.stream;

import org.ogf.saga.SagaObject;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.task.Async;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

/**
 * Since Java programmers are used to streams, the Java language bindings of
 * SAGA provide them. In contrast to everything else in the language bindings,
 * this is an abstract class, not an interface, because it is supposed to be a
 * java.io.InputStream (which is a class, not an interface). Implementations
 * should redefine methods of java.io.InputStream. These streams can be obtained
 * through the
 * {@link org.ogf.saga.stream.Stream#getInputStream() Stream.getInputStream()}
 * method.
 */
public abstract class StreamInputStream extends java.io.InputStream implements
        SagaObject, Async {

    /**
     * Clone is mentioned here because the inherited
     * {@link java.lang.Object#clone()} cannot hide the public version in
     * {@link SagaObject#clone()}.
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    // Task versions of java.io.InputStream methods.

    /**
     * Creates a task that reads a byte from this stream.
     * See {@link java.io.InputStream#read()}. 
     * @param mode
     *      the task mode.
     * @exception NotImplementedException
     *      is thrown if the implementation does not provide an
     *      implementation of this method.
     */
    public abstract Task<StreamInputStream, Integer> read(TaskMode mode)
            throws NotImplementedException;

    /**
     * Creates task that reads (part of) a buffer from this stream.
     * See {@link java.io.InputStream#read(byte[], int, int)}. 
     * @param mode
     *      the task mode.
     * @exception NotImplementedException
     *      is thrown if the implementation does not provide an
     *      implementation of this method.
     */
    public abstract Task<StreamInputStream, Integer> read(TaskMode mode,
            byte[] buf, int off, int len) throws NotImplementedException;

    /**
     * Creates a task that reads a buffer from this stream.
     * See {@link java.io.InputStream#read(byte[])}. 
     * @param mode
     *      the task mode.
     * @exception NotImplementedException
     *      is thrown if the implementation does not provide an
     *      implementation of this method.
     */
    public Task<StreamInputStream, Integer> read(TaskMode mode, byte[] buf)
            throws NotImplementedException {
        return read(mode, buf, 0, buf.length);
    }

    /**
     * Creates a task that skips the specified number of bytes from this stream.
     * See {@link java.io.InputStream#skip(long)}. 
     * @param mode
     *      the task mode.
     * @exception NotImplementedException
     *      is thrown if the implementation does not provide an
     *      implementation of this method.
     */
    public abstract Task<StreamInputStream, Long> skip(TaskMode mode, long n)
            throws NotImplementedException;

    /**
     * Creates a task that determines how many bytes are available from this
     * stream.
     * See {@link java.io.InputStream#available()}. 
     * @param mode
     *      the task mode.
     * @exception NotImplementedException
     *      is thrown if the implementation does not provide an
     *      implementation of this method.
     */
    public abstract Task<StreamInputStream, Integer> available(TaskMode mode)
            throws NotImplementedException;

    /**
     * Creates a task that closes this stream.
     * See {@link java.io.InputStream#close()}. 
     * @param mode
     *      the task mode.
     * @exception NotImplementedException
     *      is thrown if the implementation does not provide an
     *      implementation of this method.
     */
    public abstract Task<StreamInputStream, Void> close(TaskMode mode)
            throws NotImplementedException;

    /**
     * Creates a task that marks the current position in this stream.
     * See {@link java.io.InputStream#mark(int)}. 
     * @param mode
     *      the task mode.
     * @exception NotImplementedException
     *      is thrown if the implementation does not provide an
     *      implementation of this method.
     */
    public abstract Task<StreamInputStream, Void> mark(TaskMode mode,
            int readlimit) throws NotImplementedException;

    /**
     * Creates a task that resets the position to the position last marked.
     * See {@link java.io.InputStream#reset()}. 
     * @param mode
     *      the task mode.
     * @exception NotImplementedException
     *      is thrown if the implementation does not provide an
     *      implementation of this method.
     */
    public abstract Task<StreamInputStream, Void> reset(TaskMode mode)
            throws NotImplementedException;

    /**
     * Creates a task that determines if {@link java.io.InputStream#mark(int)}
     * is supported.
     * See {@link java.io.InputStream#markSupported()}. 
     * @param mode
     *      the task mode.
     * @exception NotImplementedException
     *      is thrown if the implementation does not provide an
     *      implementation of this method.
     */
    public abstract Task<StreamInputStream, Boolean> markSupported(TaskMode mode)
            throws NotImplementedException;
}
