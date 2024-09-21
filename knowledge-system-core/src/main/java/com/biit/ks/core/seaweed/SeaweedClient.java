package com.biit.ks.core.seaweed;

import com.biit.ks.core.models.Chunk;
import com.biit.ks.logger.KnowledgeSystemLogger;
import com.biit.ks.logger.SeaweedLogger;
import com.biit.ks.logger.SolrLogger;
import io.grpc.StatusRuntimeException;
import org.apache.tomcat.util.http.fileupload.util.Streams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import seaweedfs.client.FilerClient;
import seaweedfs.client.FilerProto;
import seaweedfs.client.SeaweedInputStream;
import seaweedfs.client.SeaweedOutputStream;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class SeaweedClient {
    private static final int DEFAULT_SEAWEED_PORT = 8888;
    private static final int DEFAULT_BUFFER_SIZE = 8192;

    private static final int DIRECTORY_PERMISSIONS = 0755;

    private FilerClient filerClient;

    public SeaweedClient(@Value("${seaweed.server.url}") String serverUrl,
                         @Value("${seaweed.server.port}") String serverPort) {

        SeaweedLogger.debug(this.getClass(), "Connecting to server '{}' with port '{}'.", serverUrl, serverPort);

        int convertedPort;
        if (serverPort != null) {
            try {
                convertedPort = Integer.parseInt(serverPort);
            } catch (NumberFormatException e) {
                SolrLogger.severe(this.getClass(), "Invalid port number '{}'", serverPort);
                convertedPort = DEFAULT_SEAWEED_PORT;
            }
        } else {
            convertedPort = DEFAULT_SEAWEED_PORT;
        }
        try {
            filerClient = new FilerClient(serverUrl, convertedPort);
        } catch (StatusRuntimeException e) {
            SeaweedLogger.warning(this.getClass(), "Connection to Seaweed failed!");
            filerClient = null;
        }
    }

    public void getFile(String fullPath, File destination) throws IOException {
        try (SeaweedInputStream seaweedInputStream = new SeaweedInputStream(filerClient, fullPath)) {
            copyInputStreamToFile(seaweedInputStream, destination);
        }
    }

    public SeaweedInputStream getFile(String fullPath) throws IOException {
        try (SeaweedInputStream seaweedInputStream = new SeaweedInputStream(filerClient, fullPath)) {
            return seaweedInputStream;
        }
    }

    private static void copyInputStreamToFile(InputStream inputStream, File file) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
            int read;
            final byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }

    }

    public void addFile(String seaweedPath, MultipartFile file) throws IOException {
        if (file != null) {
            try (SeaweedOutputStream seaweedOutputStream = new SeaweedOutputStream(filerClient, seaweedPath)) {
                Streams.copy(file.getInputStream(), seaweedOutputStream, true);
            }
        }
    }

    public void addFile(String fullPath, File file) throws IOException {
        try (SeaweedOutputStream seaweedOutputStream = new SeaweedOutputStream(filerClient, fullPath)) {
            final Path path = file.toPath();
            Files.copy(path, seaweedOutputStream);
            seaweedOutputStream.flush();
        }
    }

    public void removeFile(String fullPath) {
        filerClient.rm(fullPath, false, true);
    }

    /***
     * Creates a new folder where you can store new files.
     * @param fullPath Path to the folder.
     */
    public void createFolder(String fullPath) {
        filerClient.mkdirs(fullPath, DIRECTORY_PERMISSIONS);
    }

    /***
     * Creates a new folder where you can store new files.
     * @param fullPath Path to the folder.
     * @param permissions Permissions as unix filesystem (i.e. 0755).
     */
    public void createFolder(String fullPath, int permissions) {
        filerClient.mkdirs(fullPath, permissions);
    }

    /**
     * Removes a folder.
     *
     * @param fullPath             Path of the folder.
     * @param recursive            Remove content recursively.
     * @param ignoreRecursiveError Do not stop removing if something fails.
     */
    public void removeFolder(String fullPath, boolean recursive, boolean ignoreRecursiveError) {
        filerClient.rm(fullPath, recursive, ignoreRecursiveError);
    }

    public List<FilerProto.Entry> listEntries(String fullPath) {
        return filerClient.listEntries(fullPath);
    }

    public FilerProto.Entry getEntry(String folderPath, String entryName) {
        return filerClient.lookupEntry(folderPath, entryName);
    }

    /***
     * Changes the permissions from a file or folder.
     * @param fullPath Path to the folder.
     * @param permissions Permissions as unix filesystem (i.e. 0755).
     */
    public void changePermissions(String fullPath, int permissions) {
        filerClient.touch(fullPath, permissions);
    }

    public FilerProto.FuseAttributes.Builder changeAttribute(FilerProto.Entry entry) {
        final FilerProto.Entry.Builder entryBuilder = FilerProto.Entry.newBuilder(entry);
        return FilerProto.FuseAttributes.newBuilder(entry.getAttributes());
    }

    public void saveChunk(String fullPath, long skip, int size, File destination) throws IOException {
        try (SeaweedInputStream seaweedInputStream = new SeaweedInputStream(filerClient, fullPath)) {
            seaweedInputStream.seek(skip);
            final byte[] chunk = new byte[size];
            //NOTICE: Using readNBytes(length) produces an exception in the SeaweedInputStream
            seaweedInputStream.readNBytes(chunk, 0, size);
            copyInputStreamToFile(new ByteArrayInputStream(chunk), destination);
        }
    }

    public Chunk getChunk(String fullPath, long offset, int size) throws IOException {
        KnowledgeSystemLogger.debug(this.getClass(), "Getting chunk from '{}'.", fullPath);
      try (SeaweedInputStream seaweedInputStream = new SeaweedInputStream(filerClient, fullPath)) {
        KnowledgeSystemLogger.debug(this.getClass(), "Connected to '{}'.", fullPath);
        final long fileSize = seaweedInputStream.available();
        seaweedInputStream.seek(offset);
        final long remaining = seaweedInputStream.available();
        KnowledgeSystemLogger.debug(this.getClass(), "Position set to '{}'.", offset);
        final byte[] chunk = new byte[size];
        KnowledgeSystemLogger.debug(this.getClass(), "Reading chunk of size '{}'.", size);
        seaweedInputStream.readNBytes(chunk, 0, (int) Math.min(size, remaining));
        KnowledgeSystemLogger.debug(this.getClass(), "Chunk read.");
        return new Chunk(chunk, fileSize);
      }
    }

}
