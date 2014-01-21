package com.philcollins.pushpackage;

import com.philcollins.stickypunch.api.model.PackageCreator;
import com.philcollins.stickypunch.api.model.PackageZipEntry;

import java.util.UUID;

public class PackageZipCreator implements PackageCreator {
    private final PackageZipBuilder packageZipBuilder;

    public PackageZipCreator(PackageZipBuilder packageZipBuilder) {
        this.packageZipBuilder = packageZipBuilder;
    }

    @Override
    public PackageZipEntry getPackage() throws Exception {
        String newUserId = UUID.randomUUID().toString();
        byte[] data = packageZipBuilder.createPackage(newUserId);
        return new PackageZipEntry(data, newUserId);
    }
}
