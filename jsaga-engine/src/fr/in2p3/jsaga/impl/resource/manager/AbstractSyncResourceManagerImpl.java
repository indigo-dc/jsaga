package fr.in2p3.jsaga.impl.resource.manager;

import fr.in2p3.jsaga.adaptor.resource.ResourceAdaptor;
import fr.in2p3.jsaga.adaptor.resource.compute.ComputeResourceAdaptor;
import fr.in2p3.jsaga.adaptor.resource.network.NetworkResourceAdaptor;
import fr.in2p3.jsaga.adaptor.resource.storage.StorageResourceAdaptor;
import fr.in2p3.jsaga.helpers.SAGAId;
import fr.in2p3.jsaga.impl.AbstractSagaObjectImpl;
import fr.in2p3.jsaga.impl.resource.description.ComputeDescriptionImpl;
import fr.in2p3.jsaga.impl.resource.description.NetworkDescriptionImpl;
import fr.in2p3.jsaga.impl.resource.description.StorageDescriptionImpl;
import fr.in2p3.jsaga.impl.resource.instance.ComputeImpl;
import fr.in2p3.jsaga.impl.resource.instance.NetworkImpl;
import fr.in2p3.jsaga.impl.resource.instance.StorageImpl;
import fr.in2p3.jsaga.impl.resource.task.IndividualResourceStatusPoller;
import fr.in2p3.jsaga.impl.resource.task.ResourceMonitorCallback;
import fr.in2p3.jsaga.impl.resource.task.StateListener;
import fr.in2p3.jsaga.sync.resource.SyncResourceManager;
import org.ogf.saga.SagaObject;
import org.ogf.saga.error.*;
import org.ogf.saga.resource.Type;
import org.ogf.saga.resource.description.ComputeDescription;
import org.ogf.saga.resource.description.NetworkDescription;
import org.ogf.saga.resource.description.ResourceDescription;
import org.ogf.saga.resource.description.StorageDescription;
import org.ogf.saga.resource.instance.Compute;
import org.ogf.saga.resource.instance.Network;
import org.ogf.saga.resource.instance.Resource;
import org.ogf.saga.resource.instance.Storage;
import org.ogf.saga.session.Session;
import org.ogf.saga.url.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/* ***************************************************
 * *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
 * ***             http://cc.in2p3.fr/             ***
 * ***************************************************/
public abstract class AbstractSyncResourceManagerImpl extends AbstractSagaObjectImpl implements SyncResourceManager, StateListener {
    protected URL m_url;
    protected ResourceAdaptor m_adaptor;
    private IndividualResourceStatusPoller m_poller;
    
    public AbstractSyncResourceManagerImpl(Session session, URL rm, ResourceAdaptor adaptor) {
        super(session);
        m_url = rm;
        m_adaptor = adaptor;
        m_poller = new IndividualResourceStatusPoller(m_adaptor);
    }

    public URL getURL() {
        return this.m_url;
    }
    
    /** clone */
    public SagaObject clone() throws CloneNotSupportedException {
        AbstractSyncResourceManagerImpl clone = (AbstractSyncResourceManagerImpl) super.clone();
        clone.m_url = m_url;
        clone.m_adaptor = m_adaptor;
        return clone;
    }

    //----------------------------------------------------------------

    public List<String> listResourcesSync(Type type) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, TimeoutException,
            NoSuccessException {
        if (Type.COMPUTE.equals(type) && ! (m_adaptor instanceof ComputeResourceAdaptor)) {
            throw new NotImplementedException("This adaptor does not handle compute resources");
        }
        if (Type.STORAGE.equals(type) && ! (m_adaptor instanceof StorageResourceAdaptor)) {
            throw new NotImplementedException("This adaptor does not handle storage resources");
        }
        if (Type.NETWORK.equals(type) && ! (m_adaptor instanceof NetworkResourceAdaptor)) {
            throw new NotImplementedException("This adaptor does not handle network resources");
        }
        List<String> list = new ArrayList<String>();
        if ((m_adaptor instanceof ComputeResourceAdaptor && type == null) || Type.COMPUTE.equals(type)) {
            list = addArrayOfSagaIdsToList(list, ((ComputeResourceAdaptor)m_adaptor).listComputeResources());
        }
        if ((m_adaptor instanceof StorageResourceAdaptor && type == null) || Type.STORAGE.equals(type)) {
            list = addArrayOfSagaIdsToList(list, ((StorageResourceAdaptor)m_adaptor).listStorageResources());
        }
        if ((m_adaptor instanceof NetworkResourceAdaptor && type == null) || Type.NETWORK.equals(type)) {
            list = addArrayOfSagaIdsToList(list, ((NetworkResourceAdaptor)m_adaptor).listNetworkResources());
        }
        return list;
    }

    public List<String> listTemplatesSync(Type type) throws NotImplementedException,
            TimeoutException, NoSuccessException {
        if (Type.COMPUTE.equals(type) && ! (m_adaptor instanceof ComputeResourceAdaptor)) {
            throw new NotImplementedException("This adaptor does not handle compute resources");
        }
        if (Type.STORAGE.equals(type) && ! (m_adaptor instanceof StorageResourceAdaptor)) {
            throw new NotImplementedException("This adaptor does not handle storage resources");
        }
        if (Type.NETWORK.equals(type) && ! (m_adaptor instanceof NetworkResourceAdaptor)) {
            throw new NotImplementedException("This adaptor does not handle network resources");
        }
        List<String> list = new ArrayList<String>();
        if ((m_adaptor instanceof ComputeResourceAdaptor && type == null) || Type.COMPUTE.equals(type)) {
            list = addArrayOfSagaIdsToList(list, ((ComputeResourceAdaptor)m_adaptor).listComputeTemplates());
        }
        if ((m_adaptor instanceof StorageResourceAdaptor && type == null) || Type.STORAGE.equals(type)) {
            list = addArrayOfSagaIdsToList(list, ((StorageResourceAdaptor)m_adaptor).listStorageTemplates());
        }
        if ((m_adaptor instanceof NetworkResourceAdaptor && type == null) || Type.NETWORK.equals(type)) {
            list = addArrayOfSagaIdsToList(list, ((NetworkResourceAdaptor)m_adaptor).listNetworkTemplates());
        }
        return list;
    }
    
    private List<String> addArrayOfSagaIdsToList(List<String> list, String[] array) {
        for (int i=0; array!=null && i<array.length; i++) {
            list.add(SAGAId.idToSagaId(m_url, array[i]));
        }
        return list;
    }

    public ResourceDescription getTemplateSync(String id) throws NotImplementedException,
            BadParameterException, DoesNotExistException, TimeoutException, NoSuccessException {
        // Extract internalId from sagaId
        Properties properties = m_adaptor.getTemplate(SAGAId.idFromSagaId(id));
        String typeString = properties.getProperty(Resource.RESOURCE_TYPE);
        if (typeString != null) {
            Type type = Type.valueOf(typeString);
            switch (type) {
                case COMPUTE:
                    return new ComputeDescriptionImpl(properties);
                case NETWORK:
                    return new NetworkDescriptionImpl(properties);
                case STORAGE:
                    return new StorageDescriptionImpl(properties);
                default:
                    throw new BadParameterException("Unexpected resource type: "+type.name());
            }
        } else {
            throw new BadParameterException("Template is missing required property: "+Resource.RESOURCE_TYPE);
        }
    }

    //----------------------------------------------------------------

    public Compute acquireComputeSync(ComputeDescription description) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, BadParameterException,
            TimeoutException, NoSuccessException {
        try {
            return new ComputeImpl(m_session, (ResourceManagerImpl) this, m_adaptor, description);
        } catch (PermissionDeniedException e) {
            throw new AuthorizationFailedException(e);
        } catch (IncorrectStateException e) {
            throw new NoSuccessException(e);
        } catch (DoesNotExistException e) {
            throw new BadParameterException(e);
        }
    }
    public Compute acquireComputeSync(String id) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, BadParameterException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return new ComputeImpl(m_session, (ResourceManagerImpl) this, m_adaptor, id);
    }
    public void releaseComputeSync(String id) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, BadParameterException,
            DoesNotExistException, TimeoutException, NoSuccessException, IncorrectStateException {
        m_adaptor.release(SAGAId.idFromSagaId(id));
    }
    public void releaseComputeSync(String id, boolean drain) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, BadParameterException,
            DoesNotExistException, TimeoutException, NoSuccessException, IncorrectStateException {
        ((ComputeResourceAdaptor)m_adaptor).release(SAGAId.idFromSagaId(id), drain);
    }

    //----------------------------------------------------------------

    public Network acquireNetworkSync(NetworkDescription description) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, BadParameterException,
            TimeoutException, NoSuccessException {
        try {
            return new NetworkImpl(m_session, (ResourceManagerImpl) this, m_adaptor, description);
        } catch (PermissionDeniedException e) {
            throw new AuthorizationFailedException(e);
        } catch (IncorrectStateException e) {
            throw new NoSuccessException(e);
        } catch (DoesNotExistException e) {
            throw new BadParameterException(e);
        }
    }
    public Network acquireNetworkSync(String id) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, BadParameterException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return new NetworkImpl(m_session, (ResourceManagerImpl) this, m_adaptor, id);
    }
    public void releaseNetworkSync(String id) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, BadParameterException,
            DoesNotExistException, TimeoutException, NoSuccessException, IncorrectStateException {
        m_adaptor.release(SAGAId.idFromSagaId(id));
    }

    //----------------------------------------------------------------

    public Storage acquireStorageSync(StorageDescription description) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, BadParameterException,
            TimeoutException, NoSuccessException {
        try {
            return new StorageImpl(m_session, (ResourceManagerImpl) this, m_adaptor, description);
        } catch (PermissionDeniedException e) {
            throw new AuthorizationFailedException(e);
        } catch (IncorrectStateException e) {
            throw new NoSuccessException(e);
        } catch (DoesNotExistException e) {
            throw new BadParameterException(e);
        }
    }
    public Storage acquireStorageSync(String id) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, BadParameterException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return new StorageImpl(m_session, (ResourceManagerImpl) this, m_adaptor, id);
    }
    public void releaseStorageSync(String id) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, BadParameterException,
            DoesNotExistException, TimeoutException, NoSuccessException, IncorrectStateException {
        m_adaptor.release(SAGAId.idFromSagaId(id));
    }

    //----------------------------------------------------------------

    /** This method is specific to JSAGA implementation */
    @Override
    public void startListening(String nativeResourceId, ResourceMonitorCallback callback) {
        m_poller.subscribeResource(nativeResourceId, callback);
    }
    
    @Override
    /** This method is specific to JSAGA implementation */
    public void stopListening(String nativeResourceId) {
        m_poller.unsubscribeResource(nativeResourceId);
    }

}
