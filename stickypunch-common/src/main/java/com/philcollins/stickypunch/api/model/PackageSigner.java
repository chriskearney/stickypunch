package com.philcollins.stickypunch.api.model;

public interface PackageSigner {
    public byte[] sign(byte[] data) throws Exception;
}
