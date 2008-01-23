package fr.in2p3.jsaga.impl.jobcollection;

import fr.in2p3.jsaga.impl.AbstractSagaObjectImpl;
import fr.in2p3.jsaga.jobcollection.*;
import org.ogf.saga.ObjectType;
import org.ogf.saga.SagaObject;
import org.ogf.saga.error.*;
import org.ogf.saga.session.Session;

import java.util.List;

/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   JobCollectionManagerImpl
* Author: Sylvain Reynaud (sreynaud@in2p3.fr)
* Date:   26 oct. 2007
* ***************************************************
* Description:                                      */
/**
 *
 */
public class JobCollectionManagerImpl extends AbstractSagaObjectImpl implements JobCollectionManager {
    /** constructor */
    public JobCollectionManagerImpl(Session session) {
        super(session);
    }

    /** clone */
    public SagaObject clone() throws CloneNotSupportedException {
        JobCollectionManagerImpl clone = (JobCollectionManagerImpl) super.clone();
        return clone;
    }

    public ObjectType getType() {
        return ObjectType.UNKNOWN;
    }

    public JobCollection createJobCollection(JobCollectionDescription description) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, Timeout, NoSuccess {
        return new JobCollectionImpl(m_session, description);
    }

    public List<String> list() throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, Timeout, NoSuccess {
        return null;  //todo
    }

    public JobCollection getJobCollection(String jobCollectionId) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, DoesNotExist, Timeout, NoSuccess {
        return null;  //todo
    }
}
