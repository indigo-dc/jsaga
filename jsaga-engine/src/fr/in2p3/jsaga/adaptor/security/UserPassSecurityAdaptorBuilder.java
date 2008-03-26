package fr.in2p3.jsaga.adaptor.security;

import fr.in2p3.jsaga.adaptor.base.defaults.Default;
import fr.in2p3.jsaga.adaptor.base.usage.*;
import fr.in2p3.jsaga.adaptor.security.impl.UserPassSecurityAdaptor;
import fr.in2p3.jsaga.engine.config.attributes.FilePropertiesAttributesParser;
import org.ogf.saga.context.Context;
import org.ogf.saga.error.IncorrectState;
import org.ogf.saga.error.NoSuccess;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Properties;

/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   UserPassSecurityAdaptorBuilder
* Author: Sylvain Reynaud (sreynaud@in2p3.fr)
* Date:   19 juin 2007
* ***************************************************
* Description:                                      */
/**
 *
 */
public class UserPassSecurityAdaptorBuilder implements ExpirableSecurityAdaptorBuilder {
    private static final String USERPASSCRYPTED = "UserPassCrypted";
    private static final int USAGE_INIT = 1;
    private static final int USAGE_VOLATILE = 2;
    private static final int USAGE_LOAD = 3;

    public String getType() {
        return "UserPass";
    }

    public Class getSecurityAdaptorClass() {
        return UserPassSecurityAdaptor.class;
    }

    public Usage getUsage() {
        return new UAnd(new Usage[]{
                new U(Context.USERID),
                new UOr(new Usage[]{
                        new UAnd(USAGE_INIT, new Usage[]{new UHidden(Context.USERPASS), new U(Context.LIFETIME)}),
                        new U(USAGE_VOLATILE, Context.USERPASS),
                        new U(USAGE_LOAD, USERPASSCRYPTED)
                })
        });
    }

    public Default[] getDefaults(Map attributes) throws IncorrectState {
        return new Default[]{
                new Default(Context.USERID, "anonymous"),
                new Default(Context.USERPASS, "anon")
        };
    }

    public SecurityAdaptor createSecurityAdaptor(int usage, Map attributes, String contextId) throws IncorrectState, NoSuccess {
        try {
            switch(usage) {
                case USAGE_INIT:
                {
                    // get attributes
                    String name = (String) attributes.get(Context.USERID);
                    String password = (String) attributes.get(Context.USERPASS);
                    int lifetime = (attributes.containsKey(Context.LIFETIME)
                            ? UDuration.toInt(attributes.get(Context.LIFETIME))
                            : 12*3600);

                    // encrypt password
                    PasswordEncrypterSingleton crypter = PasswordEncrypterSingleton.getInstance(lifetime);
                    String cryptedPassword = crypter.encrypt(password);
                    int expiryDate = PasswordEncrypterSingleton.getExpiryDate(lifetime);

                    // write to user properties file
                    Properties prop = new Properties();
                    prop.load(new FileInputStream(FilePropertiesAttributesParser.FILE));
                    prop.setProperty(contextId+"."+Context.USERID, name);
                    prop.setProperty(contextId+"."+USERPASSCRYPTED, cryptedPassword);
                    prop.store(new FileOutputStream(FilePropertiesAttributesParser.FILE), "JSAGA user attributes");
                    return new UserPassExpirableSecurityAdaptor(name, password, expiryDate);
                }
                case USAGE_VOLATILE:
                {
                    return new UserPassSecurityAdaptor(
                            (String) attributes.get(Context.USERID),
                            (String) attributes.get(Context.USERPASS));
                }
                case USAGE_LOAD:
                {
                    // get attributes
                    String name = (String) attributes.get(Context.USERID);
                    String cryptedPassword = (String) attributes.get(USERPASSCRYPTED);

                    // decrypt password
                    PasswordDecrypterSingleton decrypter = PasswordDecrypterSingleton.getInstance();
                    int expiryDate = decrypter.getExpiryDate();
                    int currentDate = (int) (System.currentTimeMillis()/1000);
                    String password;
                    if (currentDate > expiryDate) {
                        password = null;
                    } else {
                        password = decrypter.decrypt(cryptedPassword);
                    }
                    return new UserPassExpirableSecurityAdaptor(name, password, expiryDate);
                }
                default:
                    throw new NoSuccess("INTERNAL ERROR: unexpected exception");
            }
        } catch(IncorrectState e) {
            throw e;
        } catch(NoSuccess e) {
            throw e;
        } catch(Exception e) {
            throw new NoSuccess(e);
        }
    }

    public void destroySecurityAdaptor(Map attributes, String contextId) throws Exception {
        Properties prop = new Properties();
        prop.load(new FileInputStream(FilePropertiesAttributesParser.FILE));
        prop.remove(contextId+"."+USERPASSCRYPTED);
        prop.store(new FileOutputStream(FilePropertiesAttributesParser.FILE), "JSAGA user attributes");
    }
}
