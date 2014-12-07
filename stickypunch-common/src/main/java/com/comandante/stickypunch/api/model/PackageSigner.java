package com.comandante.stickypunch.api.model;

public interface PackageSigner {
    public byte[] sign(byte[] data) throws Exception;
}
