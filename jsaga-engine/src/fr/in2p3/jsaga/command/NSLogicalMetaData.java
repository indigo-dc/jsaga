package fr.in2p3.jsaga.command;

import fr.in2p3.jsaga.impl.logicalfile.LogicalDirectoryImpl;
import org.apache.commons.cli.*;
import org.ogf.saga.attributes.Attributes;
import org.ogf.saga.logicalfile.LogicalFileFactory;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.namespace.NSEntry;
import org.ogf.saga.session.Session;
import org.ogf.saga.session.SessionFactory;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;

/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   NSLogicalMetaData
* Author: Sylvain Reynaud (sreynaud@in2p3.fr)
* Date:   4 nov. 2008
* ***************************************************
* Description:                                      */
/**
 *
 */
public class NSLogicalMetaData extends AbstractCommand {
    private static final String OPT_HELP = "h", LONGOPT_HELP = "help";
    private static final String OPT_GET = "g", LONGOPT_GET = "get";
    private static final String OPT_SET = "s", LONGOPT_SET = "set";
    private static final String OPT_REMOVE = "r", LONGOPT_REMOVE = "remove";
    private static final String OPT_LIST = "l", LONGOPT_LIST = "list";
    private static final String OPT_LIST_ALL_KEYS = "a", LONGOPT_LIST_ALL_KEYS = "list-all-keys";

    public NSLogicalMetaData() {
        super("jsaga-logical-metadata", new String[]{"URL"}, new String[]{OPT_HELP, LONGOPT_HELP});
    }

    public static void main(String[] args) throws Exception {
        NSLogicalMetaData command = new NSLogicalMetaData();
        CommandLine line = command.parse(args);
        if (line.hasOption(OPT_HELP))
        {
            command.printHelpAndExit(null);
        }
        else
        {
            // get URL and pattern from arguments
            String arg = command.m_nonOptionValues[0];
            URL url = URLFactory.createURL(arg);

            // open connection
            Session session = SessionFactory.createSession(true);
            Attributes entry;
            if (url.getPath().endsWith("/")) {
                entry = LogicalFileFactory.createLogicalDirectory(session, url, Flags.NONE.getValue());
            } else {
                entry = LogicalFileFactory.createLogicalFile(session, url, Flags.NONE.getValue());
            }

            // operation
            if (line.hasOption(OPT_GET)) {
                String key = line.getOptionValue(OPT_GET);
                System.out.println(entry.getAttribute(key));
            } else if (line.hasOption(OPT_SET)) {
                String[] array = line.getOptionValues(OPT_SET);
                String key = array[0];
                String value = array[1];
                entry.setAttribute(key, value);
            } else if (line.hasOption(OPT_REMOVE)) {
                String key = line.getOptionValue(OPT_REMOVE);
                entry.removeAttribute(key);
            } else if (line.hasOption(OPT_LIST)) {
                String[] keys = entry.listAttributes();
                for (int i=0; i<keys.length; i++) {
                    System.out.println(keys[i]+" = "+entry.getAttribute(keys[i]));
                }
            } else if (line.hasOption(OPT_LIST_ALL_KEYS)) {
                String[] keys = ((LogicalDirectoryImpl)entry).listAttributesRecursive();
                for (int i=0; i<keys.length; i++) {
                    System.out.println(keys[i]);
                }
            }

            // close connection
            ((NSEntry)entry).close();
        }
    }

    protected Options createOptions() {
        Options opt = new Options();

        // command group
        OptionGroup group = new OptionGroup();
        group.setRequired(true);
        {
            group.addOption(OptionBuilder.withDescription("Display this help and exit")
                    .withLongOpt(LONGOPT_HELP)
                    .create(OPT_HELP));
            group.addOption(OptionBuilder.withDescription("Get meta-data <key>")
                    .hasArg()
                    .withArgName("key")
                    .withLongOpt(LONGOPT_GET)
                    .create(OPT_GET));
            group.addOption(OptionBuilder.withDescription("Set meta-data <key> with value <value>")
                    .hasArgs(2)
                    .withArgName("key value")
                    .withLongOpt(LONGOPT_SET)
                    .create(OPT_SET));
            group.addOption(OptionBuilder.withDescription("Remove meta-data <key>")
                    .hasArg()
                    .withArgName("key")
                    .withLongOpt(LONGOPT_REMOVE)
                    .create(OPT_REMOVE));
            group.addOption(OptionBuilder.withDescription("List meta-data <key>-<value> pairs")
                    .withLongOpt(LONGOPT_LIST)
                    .create(OPT_LIST));
            group.addOption(OptionBuilder.withDescription("List all meta-data keys")
                    .withLongOpt(LONGOPT_LIST_ALL_KEYS)
                    .create(OPT_LIST_ALL_KEYS));
        }
        opt.addOptionGroup(group);
        
        return opt;
    }
}
