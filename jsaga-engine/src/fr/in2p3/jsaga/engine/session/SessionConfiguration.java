package fr.in2p3.jsaga.engine.session;

import fr.in2p3.jsaga.Base;
import fr.in2p3.jsaga.engine.config.ConfigurationException;
import fr.in2p3.jsaga.generated.session.Attribute;
import fr.in2p3.jsaga.generated.session.JsagaDefault;
import fr.in2p3.jsaga.helpers.XMLFileParser;
import fr.in2p3.jsaga.helpers.xslt.XSLTransformerFactory;
import org.exolab.castor.util.LocalConfiguration;
import org.exolab.castor.xml.*;
import org.ogf.saga.context.Context;
import org.ogf.saga.context.ContextFactory;
import org.ogf.saga.error.*;
import org.ogf.saga.session.Session;
import org.w3c.dom.Document;

import java.io.*;
import java.net.URL;

/* ***************************************************
 * *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
 * ***             http://cc.in2p3.fr/             ***
 * ***************************************************
 * File:   SessionConfiguration
 * Author: Sylvain Reynaud (sreynaud@in2p3.fr)
 * ***************************************************
 * Description:                                      */

/**
 *
 */
public class SessionConfiguration {
    private static final String JSAGA_FACTORY = Base.getSagaFactory();
    private static final String XSD = "schema/jsaga-default-contexts.xsd";
    protected static final String MERGE = "xsl/jsaga-default-contexts-merge.xsl";
    protected static final String XSL = "xsl/jsaga-default-contexts.xsl";
    private static final File DEBUG = new File(Base.JSAGA_VAR, "jsaga-default-contexts.xml");

    protected JsagaDefault m_config;

    public SessionConfiguration(URL sessionCfgUrl) throws ConfigurationException {
        try {
            if (sessionCfgUrl != null) {
                // merge xinclude
                InputStream sessionCfgStream = sessionCfgUrl.openStream();
                byte[] data = new XMLFileParser(null).xinclude(sessionCfgStream);

                // parse xml
                XMLFileParser parser = new XMLFileParser(new String[]{XSD});
                Document rawConfig = parser.parse(new ByteArrayInputStream(data), DEBUG);

                // transform config
                XSLTransformerFactory tFactory = XSLTransformerFactory.getInstance();
                Document merged = tFactory.create(MERGE).transformToDOM(rawConfig);
                Document doc = tFactory.create(XSL).transformToDOM(merged);

                // unmarshall config
                Unmarshaller unmarshaller = new Unmarshaller(JsagaDefault.class);
                unmarshaller.setIgnoreExtraAttributes(false);
                unmarshaller.setValidation(true);
                m_config = (JsagaDefault) unmarshaller.unmarshal(doc);
            } else {
                m_config = new JsagaDefault();
            }
        } catch (Exception e) {
            throw new ConfigurationException(e);
        }
    }

    /** WARNING: use this method for debugging purpose only */
    public String toXML() throws MarshalException, ValidationException, IOException {
        StringWriter writer = new StringWriter();
        this.dump(writer);
        return writer.toString();
    }
    protected void dump(Writer writer) throws MarshalException, ValidationException, IOException {
        LocalConfiguration.getInstance().getProperties().setProperty("org.exolab.castor.indent", "true");
        Marshaller marshaller = new Marshaller(writer);
        marshaller.setValidation(true);
        marshaller.marshal(m_config);
    }

    public void setDefaultContext(Context context) throws NotImplementedException, NoSuccessException {
        fr.in2p3.jsaga.generated.session.Context contextCfg = this.findContextCfg(context);
        if (contextCfg != null) {
            // set CONFIGURATION defaults (/jsaga-defaults/contexts)
            setDefaultContext(context, contextCfg);
        }
    }

    public void setDefaultSession(Session session) throws IncorrectStateException, NoSuccessException, TimeoutException {
        fr.in2p3.jsaga.generated.session.Session sessionCfg = m_config.getSession();
        if (sessionCfg != null) {
            for (fr.in2p3.jsaga.generated.session.Context contextCfg : sessionCfg.getContext()) {
                Context context = ContextFactory.createContext(JSAGA_FACTORY, contextCfg.getType());
                // set CONFIGURATION defaults (/jsaga-defaults/session)
                setDefaultContext(context, contextCfg);
                session.addContext(context);
            }
        }
    }

    public fr.in2p3.jsaga.generated.session.Context[] getSessionContextsCfg() {
        fr.in2p3.jsaga.generated.session.Session sessionCfg = m_config.getSession();
        if (sessionCfg != null) {
            return sessionCfg.getContext();
        }
        return new fr.in2p3.jsaga.generated.session.Context[]{};
    }

    public fr.in2p3.jsaga.generated.session.Context findContextCfg(Context context) throws NotImplementedException, NoSuccessException {
        // get type
        String type;
        try {
            type = context.getAttribute(Context.TYPE);
        } catch (NotImplementedException | NoSuccessException e) {
            throw e;
        } catch (SagaException e) {
            throw new NoSuccessException(e);
        }

        // find config by type
        fr.in2p3.jsaga.generated.session.Contexts contextsCfg = m_config.getContexts();
        if (contextsCfg != null) {
            for (fr.in2p3.jsaga.generated.session.Context contextCfg : contextsCfg.getContext()) {
                if (contextCfg.getType().equals(type)) {
                    return contextCfg;
                }
            }
        }
        return null;
    }

    public static void setDefaultContext(Context context, fr.in2p3.jsaga.generated.session.Context config) throws NoSuccessException {
        try {
            for (Attribute attributeCfg : config.getAttribute()) {
                if (attributeCfg.getValue() != null) {
                    context.setAttribute(attributeCfg.getName(), attributeCfg.getValue());
                } else if (attributeCfg.getItemCount() > 0) {
                    context.setVectorAttribute(attributeCfg.getName(), attributeCfg.getItem());
                }
            }
        } catch (NoSuccessException e) {
            throw e;
        } catch (SagaException e) {
            throw new NoSuccessException(e);
        }
    }
}
