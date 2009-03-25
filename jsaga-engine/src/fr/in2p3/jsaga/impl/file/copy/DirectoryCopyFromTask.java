package fr.in2p3.jsaga.impl.file.copy;

import fr.in2p3.jsaga.impl.file.DirectoryImpl;
import org.ogf.saga.error.*;
import org.ogf.saga.session.Session;
import org.ogf.saga.url.URL;

/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   DirectoryCopyFromTask
* Author: Sylvain Reynaud (sreynaud@in2p3.fr)
* Date:   21 f�vr. 2009
* ***************************************************
* Description:                                      */
/**
 *
 */
public class DirectoryCopyFromTask<T,E> extends AbstractCopyFromTask<T,E> {
    private DirectoryImpl m_targetDir;

    /** constructor */
    public DirectoryCopyFromTask(Session session, DirectoryImpl targetDir, URL target, int flags) throws NotImplementedException {
        super(session, target, flags);
        m_targetDir = targetDir;
    }

    public void doCopyFrom(URL source, int flags) throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException, BadParameterException, IncorrectStateException, DoesNotExistException, AlreadyExistsException, TimeoutException, NoSuccessException, IncorrectURLException {
        m_targetDir.copyFrom(source, flags);
    }
}
