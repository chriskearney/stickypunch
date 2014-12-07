package com.comandante.stickypunch.api.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class PackageZipEntry {
    private final byte[] packageZipData;
    private final String id;

    public PackageZipEntry(byte[] packageZipData, String id) {
        this.packageZipData = packageZipData;
        this.id = id;
    }

    public byte[] getPackageZipData() {
        return packageZipData;
    }

    public String getId() {
        return id;
    }

    public InputStream getInputStream() {
        return new ByteArrayInputStream(packageZipData);
    }
}
