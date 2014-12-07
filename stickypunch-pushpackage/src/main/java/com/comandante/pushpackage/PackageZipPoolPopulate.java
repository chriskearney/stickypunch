package com.comandante.pushpackage;

import com.comandante.stickypunch.api.model.PackageZipEntry;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;

public class PackageZipPoolPopulate implements Runnable {
    private static final Logger log = LogManager.getLogger(PackageZipPoolPopulate.class);

    private final ArrayBlockingQueue<PackageZipEntry> packagesQueue;
    private final PackageZipBuilder packageZipBuilder;

    public PackageZipPoolPopulate(PackageZipPool packageZipPool, PackageZipBuilder packageZipBuilder) {
        this.packagesQueue = packageZipPool.packagesQueue;
        this.packageZipBuilder = packageZipBuilder;
    }

    @Override
    public void run() {
        while (true) {
            String newUserId = UUID.randomUUID().toString();
            try {
                final long startTime = System.currentTimeMillis();
                PackageZipEntry packageZipEntry = new PackageZipEntry(packageZipBuilder.createPackage(newUserId), newUserId);
                final long endTime = System.currentTimeMillis();
                packagesQueue.put(packageZipEntry);
                log.info(String.format("Added a new package zip to pool. Generation Time: %dms", endTime - startTime));
            } catch (Exception e) {
                log.error("Unable to populate package zip pool!", e);
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e1) {
                }
            }
        }
    }
}
