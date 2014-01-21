package com.philcollins.pushpackage;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.philcollins.pushpackage.jackson.WebsiteObjectMapper;
import com.philcollins.pushpackage.jackson.model.Website;
import com.philcollins.pushpackage.jackson.model.WebsiteBuilder;
import com.philcollins.stickypunch.api.model.PackageSigner;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class PackageZipBuilder {

    private PackageZipConfiguration packageZipConfiguration;
    private PackageSigner signer;

    private ObjectMapper objectMapper = WebsiteObjectMapper.getInstance();

    public static final ImmutableSet<String> IMAGE_NAMES = ImmutableSet.of(
            "icon_128x128.png",
            "icon_128x128@2x.png",
            "icon_16x16.png",
            "icon_16x16@2x.png",
            "icon_32x32.png",
            "icon_32x32@2x.png");

    public PackageZipBuilder(PackageZipConfiguration packageZipConfiguration, PackageSigner signer) {
        this.packageZipConfiguration = packageZipConfiguration;
        this.signer = signer;
    }

    public byte[] createPackage(String userID) throws Exception {
        validatePushPackageFiles(packageZipConfiguration.pushPackageFiles);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(byteArrayOutputStream);
        HashMap<String, String> sha1s = new HashMap<String, String>();
        for (String imageFileName : IMAGE_NAMES) {
            createImage(imageFileName, sha1s, zos);
        }
        createWebsiteJson(userID, sha1s, zos);
        InputStream is = createManifestJson(sha1s, zos);
        createSignature(is, zos);
        zos.flush();
        zos.close();
        return byteArrayOutputStream.toByteArray();
    }

    private void createImage(String fileName, HashMap<String, String> sha1s, ZipOutputStream zos) throws IOException {
        HashFunction hf = Hashing.sha1();
        File imageFile = new File(packageZipConfiguration.pushPackageFiles + "/icon.iconset/" + fileName);
        Preconditions.checkArgument(imageFile.canRead(), "File cannot be read: %s", imageFile);
        HashCode hc = hf.newHasher().putBytes(FileUtils.readFileToByteArray(imageFile)).hash();
        sha1s.put("icon.iconset/" + fileName, hc.toString());
        addToZipFile(new FileInputStream(imageFile), "icon.iconset/" + fileName, zos);
    }

    private void createWebsiteJson(String userId, HashMap<String, String> sha1s, ZipOutputStream zos) throws IOException {
        HashFunction hf = Hashing.sha1();
        InputStream is = buildWebsiteJsonFile(userId);
        HashCode hc = hf.newHasher().putBytes(IOUtils.toByteArray(is)).hash();
        sha1s.put("website.json", hc.toString());
        is.reset();
        addToZipFile(is, "website.json", zos);
    }

    private InputStream createManifestJson(HashMap<String, String> sha1s, ZipOutputStream zos) throws Exception {
        HashFunction hf = Hashing.sha1();
        InputStream is = buildManifestJsonFile(sha1s);
        addToZipFile(is, "manifest.json", zos);
        return is;
    }

    private void createSignature(InputStream is, ZipOutputStream zos) throws Exception {
        is.reset();
        byte[] signedBytes = signer.sign(IOUtils.toByteArray(is));
        addToZipFile(new ByteArrayInputStream(signedBytes), "signature", zos);
    }

    private InputStream buildWebsiteJsonFile(String userId) throws IOException {
        List<String> allowedDomains = Lists.transform(packageZipConfiguration.allowedDomains, new Function<Object, String>() {
            @Override
            public String apply(Object arg0) {
                if (arg0 != null)
                    return arg0.toString();
                else
                    return "null";
            }
        });
        Website website = new WebsiteBuilder()
                .setWebsitePushId(packageZipConfiguration.websitePushID)
                .setWebServiceUrl(packageZipConfiguration.webServiceUrl)
                .setAllowedDomains(allowedDomains)
                .setUrlFormatString(packageZipConfiguration.urlFormatString)
                .setAuthenticationToken(userId)
                .setWebsiteName(packageZipConfiguration.websiteName)
                .build();

        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(website);
        return new ByteArrayInputStream(json.getBytes());
    }

    private InputStream buildManifestJsonFile(Map<String, String> manifestSha1s) throws IOException {
        String manifestJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(manifestSha1s);
        return new ByteArrayInputStream(manifestJson.getBytes());
    }

    public static void addToZipFile(InputStream inputStream, String zipEntity, ZipOutputStream zos) throws IOException {
        ZipEntry zipEntry = new ZipEntry(zipEntity);
        zos.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = inputStream.read(bytes)) >= 0) {
            zos.write(bytes, 0, length);
        }
        zos.closeEntry();
        inputStream.close();
    }

    private void isValidDirectory(File file) {
        Preconditions.checkNotNull(file);
        Preconditions.checkArgument(file.exists(), "Directory does not exist: %s.", file);
        Preconditions.checkArgument(file.isDirectory(), "Should be a directory: %s", file);
        Preconditions.checkArgument(file.canRead(), "File cannot be written: %s", file);
    }

    private void isValidPushPackage(File file) {
        File imageDir = new File(file + "/icon.iconset");
        isValidDirectory(imageDir);
        for (String imageFile : IMAGE_NAMES) {
            Preconditions.checkArgument(new File(imageDir + "/" + imageFile).canRead(), "");
        }
    }

    private void validatePushPackageFiles(String fileName) {
        File file = new File(fileName);
        isValidDirectory(file);
        isValidPushPackage(file);
    }
}
