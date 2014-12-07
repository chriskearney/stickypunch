package com.comandante.pushpackage;

import com.comandante.stickypunch.api.model.PackageSigner;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;
import sun.security.rsa.RSAPrivateKeyImpl;
import sun.security.util.DerValue;

import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;

public class PackageZipSigner implements PackageSigner {

    private final PackageZipConfiguration packageZipConfiguration;

    public PackageZipSigner(PackageZipConfiguration packageZipConfiguration) {
        this.packageZipConfiguration = packageZipConfiguration;
    }

    @Override
    public byte[] sign(byte[] data) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        KeyStore inStore = KeyStore.getInstance("PKCS12");
        inStore.load(new FileInputStream(packageZipConfiguration.pushPackageSignerCertPath), packageZipConfiguration.pushPackageSignerCertPassword.toCharArray());

        Key key = inStore.getKey(packageZipConfiguration.pushPackageSignerCertName, packageZipConfiguration.pushPackageSignerCertPassword.toCharArray());
        PrivateKey privateKey = RSAPrivateKeyImpl.parseKey(new DerValue(key.getEncoded()));
        Certificate certificate = inStore.getCertificate(packageZipConfiguration.pushPackageSignerCertName);
        X509CertificateHolder certificateHolder = new X509CertificateHolder(certificate.getEncoded());

        List certList = new ArrayList();
        CMSTypedData msg = new CMSProcessableByteArray(data); //Data to sign

        certList.add(certificateHolder); //Adding the X509 Certificate

        Store certs = new JcaCertStore(certList);

        CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
        //Initializing the the BC's Signer
        ContentSigner sha1Signer = new JcaContentSignerBuilder("SHA1withRSA").setProvider("BC").build(privateKey);

        gen.addSignerInfoGenerator(
                new JcaSignerInfoGeneratorBuilder(
                        new JcaDigestCalculatorProviderBuilder().setProvider("BC").build())
                        .build(sha1Signer, certificateHolder));
        //adding the certificate
        gen.addCertificates(certs);
        //Getting the signed data
        CMSSignedData sigData = gen.generate(msg, false);
        return sigData.getEncoded();
    }
}
