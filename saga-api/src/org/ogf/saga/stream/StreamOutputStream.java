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
 * java.io.OutputStream (which is a class, not an interface). Implementations
 * should redefine methods of java.io.OutputStream. These streams can be
 * obtained through the
 * {@link org.ogf.saga.stream.Stream#getOutputStream() Stream.getOutputStream()}
 * method.
 */
public abstract class StreamOutputStream extends java.io.OutputStream implements
        SagaObject, Async {

    /**
     * Clone is mentioned here because the inherited
     * {@link java.lang.Object#clone()} cannot hide the public version in
     * {@link SagaObject#clone()}.
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    // Task versions of java.io.OutputStream methods.

    /**
     * Creates a task that writes a byte to this stream.
     * See {@link java.io.OutputStream#write(int)}. 
     * @param mode
     *      the task mode.
     * @exception NotImplementedException
     *      is thrown if the implementation does not provide an
     *      implementation of this method.
     */
    public abstract Task<StreamOutputStream, Void> write(TaskMode mode, int b)
            throws NotImplementedException;

    /**
     * Creates a task that writes (part of) a buffer to this stream.
     * See {@link java.io.OutputStream#write(byte[], int, int)}. 
     * @param mode
     *      the task mode.
     * @exception NotImplementedException
     *      is thrown if the implementation does not provide an
     *      implementation of this method.
     */
    public abstract Task<StreamOutputStream, Void> write(TaskMode mode,
            byte[] buf, int off, int len) throws NotImplementedException;

    /**
     * Creates a task that writes a buffer to this stream.
     * See {@link java.io.OutputStream#write(byte[])}. 
     * @param mode
     *      the task mode.
     * @exception NotImplementedException
     *      is thrown if the implementation does not provide an
     *      implementation of this method.
     */
    public Task<StreamOutputStream, Void> write(TaskMode mode, byte[] buf)
            throws NotImplementedException {
        return write(mode, buf, 0, buf.length);
    }

    /**
     * Creates a task that flushes this stream.
     * See {@link java.io.OutputStream#flush()}. 
     * @param mode
     *      the task mode.
     * @exception NotImplementedException
     *      is thrown if the implementation does not provide an
     *      implementation of this method.
     */
    public abstract Task<StreamOutputStream, Void> flush(TaskMode mode)
            throws NotImplementedException;

    /**
     * Creates a task that closes this stream.
     * See {@link java.io.OutputStream#close()}. 
     * @param mode
     *      the task mode.
     * @exception NotImplementedException
     *      is thrown if the implementation does not provide an
     *      implementation of this method.
     */
    
    public abstract Task<StreamOutputStream, Void> close(TaskMode mode)
            throws NotImplementedException;
}