package fr.in2p3.jsaga.impl.task.impl;

import fr.in2p3.jsaga.impl.AbstractSagaObjectImpl;

import fr.in2p3.jsaga.impl.task.AbstractThreadedTask;
import org.ogf.saga.SagaObject;
import org.ogf.saga.error.*;
import org.ogf.saga.task.Async;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   AsyncTest
* Author: Sylvain Reynaud (sreynaud@in2p3.fr)
* Date:   11 janv. 2008
* ***************************************************
* Description:                                      */
/**
 *
 */
public class AsyncTest extends AbstractSagaObjectImpl implements SagaObject, Async {
    public String getHello(String name) {
//        try {Thread.currentThread().sleep(100);} catch (InterruptedException e) {/*ignore*/}
        return "Hello "+name+" !";
    }

    public Task<AsyncTest, String> getHello(TaskMode mode, final String name) throws NotImplementedException {
        return new AbstractThreadedTask<AsyncTest,String>(mode) {
            public String invoke() throws NotImplementedException, IncorrectURLException, AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException, BadParameterException, IncorrectStateException, AlreadyExistsException, DoesNotExistException, TimeoutException, NoSuccessException {
                return AsyncTest.this.getHello(name);
            }
        };
    }
}
