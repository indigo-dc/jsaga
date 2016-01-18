package org.ogf.saga.resource.description;

import org.ogf.saga.resource.description.ResourceDescription;

public interface StorageDescription extends ResourceDescription {
    /**
     * Attribute name: size in bytes /kB/MB/...
     */
    public static final String SIZE = "Size";

    /**
     * Attribute name: mount point or provisioning URL
     */
    public static final String ACCESS = "Access";
}
