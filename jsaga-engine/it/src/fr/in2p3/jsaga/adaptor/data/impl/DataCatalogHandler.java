package fr.in2p3.jsaga.adaptor.data.impl;

import fr.in2p3.jsaga.Base;
import fr.in2p3.jsaga.adaptor.schema.data.catalog.*;
import fr.in2p3.jsaga.adaptor.schema.data.catalog.File;
import org.exolab.castor.util.LocalConfiguration;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.NoSuccessException;

import java.io.*;
import java.util.*;

/* ***************************************************
* *** Centre de Calcul de l'IN2P3 - Lyon (France) ***
* ***             http://cc.in2p3.fr/             ***
* ***************************************************
* File:   DataCatalogHandler
* Author: Sylvain Reynaud (sreynaud@in2p3.fr)
* Date:   19 juil. 2007
* ***************************************************
* Description:                                      */
/**
 *
 */
public class DataCatalogHandler {
    private static final java.io.File FILE = new java.io.File(Base.JSAGA_VAR, "data-catalog.xml");
    private static DataCatalogHandler _instance = null;
    private DataCatalog m_catalogRoot;

    public static synchronized DataCatalogHandler getInstance() throws NoSuccessException {
        if (_instance == null) {
            _instance = new DataCatalogHandler();
        }
        return _instance;
    }
    private DataCatalogHandler() throws NoSuccessException {
        if (FILE.exists()) {
            try {
                m_catalogRoot = (DataCatalog) Unmarshaller.unmarshal(DataCatalog.class, new InputStreamReader(new FileInputStream(FILE)));
            } catch (Exception e) {
                throw new NoSuccessException(e);
            }
        } else {
            m_catalogRoot = new DataCatalog();
            m_catalogRoot.setName("/");
        }
    }

    /**
     * commit modification made on catalog
     */
    public void commit() throws NoSuccessException {
        try {
            // marshal
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            Properties prop = LocalConfiguration.getInstance().getProperties();
            prop.setProperty("org.exolab.castor.indent", "true");
            Marshaller.marshal(m_catalogRoot, new OutputStreamWriter(buffer));
            // dump to file if marshaling did not throw any exception
            OutputStream out = new FileOutputStream(FILE);
            out.write(buffer.toByteArray());
            out.close();
        } catch(Exception e) {
            throw new NoSuccessException(e);
        }
    }

    ////////////////////////////////////////// public methods /////////////////////////////////////////

    // add
    public Directory addDirectory(String absolutePath) throws DoesNotExistException {
        return addDirectory(getParentDirectory(absolutePath), getEntryName(absolutePath));
    }
    public Directory addDirectory(DirectoryType parent, String name) {
        Directory dir = new Directory();
        dir.setName(name);
        parent.addDirectory(dir);
        return dir;
    }
    public File addFile(String absolutePath) throws DoesNotExistException {
        return addFile(getParentDirectory(absolutePath), getEntryName(absolutePath));
    }
    public File addFile(DirectoryType parent, String name) {
        File file = new File();
        file.setName(name);
        parent.addFile(file);
        return file;
    }

    // remove
    public void removeDirectory(String absolutePath) throws DoesNotExistException, NoSuccessException {
        removeDirectory(getParentDirectory(absolutePath), getEntryName(absolutePath));
    }
    public void removeDirectory(DirectoryType parent, String name) throws DoesNotExistException, NoSuccessException {
        Directory dir = getDirectory(parent, name);
        if (dir.getDirectoryCount()>0 || dir.getFileCount()>0) {
            throw new NoSuccessException("Directory is not empty: "+name);
        }
        parent.removeDirectory(dir);
    }
    public void removeFile(String absolutePath) throws DoesNotExistException {
        removeFile(getParentDirectory(absolutePath), getEntryName(absolutePath));
    }
    public void removeFile(DirectoryType parent, String name) throws DoesNotExistException {
        parent.removeFile(getFile(parent, name));
    }

    // get
    public DirectoryType getDirectory(String absolutePath) throws DoesNotExistException {
        DirectoryType parent = getParentDirectory(absolutePath);
        String name = getEntryName(absolutePath);
        if (name != null) {
            return getDirectory(parent, name);
        } else {
            return parent;
        }
    }
    public Directory getDirectory(DirectoryType parent, String entryName) throws DoesNotExistException {
        for (int i=0; i<parent.getDirectoryCount(); i++) {
            if (parent.getDirectory(i).getName().equals(entryName)) {
                return parent.getDirectory(i);
            }
        }
        throw new DoesNotExistException("Directory does not exist: "+entryName);
    }
    public File getFile(String absolutePath) throws DoesNotExistException {
        return getFile(getParentDirectory(absolutePath), getEntryName(absolutePath));
    }
    public File getFile(DirectoryType parent, String entryName) throws DoesNotExistException {
        for (int i=0; i<parent.getFileCount(); i++) {
            if (parent.getFile(i).getName().equals(entryName)) {
                return parent.getFile(i);
            }
        }
        throw new DoesNotExistException("File does not exist: "+entryName);
    }
    public EntryType getEntry(String absolutePath) throws DoesNotExistException {
        DirectoryType parentDir = getParentDirectory(absolutePath);
        String entryName = getEntryName(absolutePath);
        return getEntry(parentDir, entryName);
    }
    public EntryType getEntry(DirectoryType parent, String entryName) throws DoesNotExistException {
        if (entryName == null) {
            return parent;
        }
        try {
            return getFile(parent, entryName);
        } catch(DoesNotExistException e) {
            return getDirectory(parent, entryName);
        }
    }

    // list
    public EntryType[] listEntries(String absolutePath) throws DoesNotExistException {
        EntryType entry = this.getEntry(absolutePath);
        if (entry instanceof DirectoryType) {
            return listEntries((DirectoryType)entry);
        } else {
            return new EntryType[]{entry};
        }
    }
    public EntryType[] listEntries(DirectoryType parent) {
        List list = new ArrayList();
        for (int i=0; i<parent.getDirectoryCount(); i++) {
            list.add(parent.getDirectory(i));
        }
        for (int i=0; i<parent.getFileCount(); i++) {
            list.add(parent.getFile(i));
        }
        return (EntryType[]) list.toArray(new EntryType[list.size()]);
    }

    ////////////////////////////////////////// friend methods /////////////////////////////////////////

    public DirectoryType getParentDirectory(String absolutePath) throws DoesNotExistException {
        String[] entryNames = toArray(absolutePath);
        DirectoryType parent = m_catalogRoot;
        for (int i=0; i<entryNames.length-1; i++) {
            parent = getDirectory(parent, entryNames[i]);
        }
        return parent;
    }

    public String getEntryName(String absolutePath) {
        String[] entryNames = toArray(absolutePath);
        if (entryNames.length > 0) {
            return entryNames[entryNames.length-1];
        } else {
            return null;
        }
    }

    private static String[] toArray(String absolutePath) {
        List list = new ArrayList();
        String[] array = absolutePath.split("/");
        for (int i=0; i<array.length; i++) {
            if (!array[i].equals("")) {
                list.add(array[i]);
            }
        }
        return (String[]) list.toArray(new String[list.size()]);
    }
}
