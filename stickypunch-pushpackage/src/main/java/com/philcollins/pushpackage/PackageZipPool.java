package com.philcollins.pushpackage;

import com.philcollins.stickypunch.api.model.PackageCreator;
import com.philcollins.stickypunch.api.model.PackageZipEntry;

import java.util.concurrent.ArrayBlockingQueue;

public class PackageZipPool implements PackageCreator {

    public final ArrayBlockingQueue<PackageZipEntry> packagesQueue;
    private final PackageZipConfiguration packageZipConfiguration;

    public PackageZipPool(PackageZipConfiguration packageZipConfiguration) {
        this.packageZipConfiguration = packageZipConfiguration;
        this.packagesQueue = new ArrayBlockingQueue<PackageZipEntry>(packageZipConfiguration.packageZipQueueSize);
    }

    @Override
    public PackageZipEntry getPackage() throws Exception {
        return packagesQueue.take();
    }

}
