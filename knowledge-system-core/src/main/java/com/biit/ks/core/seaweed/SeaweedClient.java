package com.biit.ks.core.seaweed;

import com.biit.ks.core.exceptions.SeaweedClientException;
import com.biit.ks.core.models.Chunk;
import com.biit.ks.logger.KnowledgeSystemLogger;
import com.biit.ks.logger.SeaweedLogger;
import io.grpc.StatusRuntimeException;
import org.apache.tomcat.util.http.fileupload.util.Streams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import seaweedfs.client.FilerClient;
import seaweedfs.client.FilerProto;
import seaweedfs.client.SeaweedInputStream;
import seaweedfs.client.SeaweedOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class SeaweedClient {
    private static final int DEFAULT_SEAWEED_PORT = 8888;
    private static final int DEFAULT_BUFFER_SIZE = 8192;

    private static final int DIRECTORY_PERMISSIONS = 0755;

    private FilerClient filerClient;

    @Value("${seaweed.max.file.memory:8388608}")
    private int maxFileSizeOnMemory;

    public SeaweedClient(@Value("${seaweed.server.url}") String serverUrl,
                         @Value("${seaweed.server.port}") String serverPort) {

        SeaweedLogger.debug(this.getClass(), "Connecting to server '{}' with port '{}'.", serverUrl, serverPort);

        int convertedPort;
        if (serverPort != null) {
            try {
                convertedPort = Integer.parseInt(serverPort);
            } catch (NumberFormatException e) {
                SeaweedLogger.severe(this.getClass(), "Invalid port number '{}'", serverPort);
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
        } catch (FileNotFoundException e) {
            SeaweedLogger.severe(this.getClass(), "File not found: {}", fullPath);
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

    public void addBytes(String seaweedPath, byte[] bytes) throws IOException {
        final File tempFile = File.createTempFile("byte", ".seaweed");
        tempFile.deleteOnExit();
        try (FileOutputStream stream = new FileOutputStream(tempFile)) {
            stream.write(bytes);
        }
        addFile(seaweedPath, tempFile);
        tempFile.delete();
    }


    public void addFile(String seaweedPath, MultipartFile file) throws IOException {
        if (file != null) {
            try (SeaweedOutputStream seaweedOutputStream = new SeaweedOutputStream(filerClient, seaweedPath)) {
                SeaweedLogger.debug(this.getClass(), "Storing file '{}'.", seaweedPath);
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
        SeaweedLogger.info(this.getClass(), "Creating folder '{}' with permissions '{}'.", fullPath, permissions);
        filerClient.mkdirs(fullPath, permissions);
    }

    public void deleteFolder(String fullPath) {
        removeFolder(fullPath, true, true);
    }

    /**
     * Removes a folder.
     *
     * @param fullPath             Path of the folder.
     * @param recursive            Remove content recursively.
     * @param ignoreRecursiveError Do not stop removing if something fails.
     */
    public void removeFolder(String fullPath, boolean recursive, boolean ignoreRecursiveError) {
        SeaweedLogger.info(this.getClass(), "Deleting folder '{}'.", fullPath);
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
            if (offset < fileSize) {
                seaweedInputStream.seek(offset);
                final long remaining = seaweedInputStream.available();
                KnowledgeSystemLogger.debug(this.getClass(), "Position set to '{}'.", offset);
                final byte[] chunk = new byte[size];
                KnowledgeSystemLogger.debug(this.getClass(), "Reading chunk of size '{}'.", size);
                seaweedInputStream.readNBytes(chunk, 0, (int) Math.min(size, remaining));
                KnowledgeSystemLogger.debug(this.getClass(), "Chunk read.");
                return new Chunk(chunk, fileSize);
            }
            return null;
        }
    }

    public byte[] getBytes(String fullPath, long offset, int size) throws IOException {
        long readBytes = 0;
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while (readBytes < size) {
            final Chunk chunk = getChunk(fullPath, offset + readBytes, size);
            if (chunk != null) {
                byteArrayOutputStream.writeBytes(chunk.getData());
                readBytes += chunk.getFileSize();
            } else {
                break;
            }
        }
        return byteArrayOutputStream.toByteArray();
    }


    public byte[] getBytes(String folderPath, String entryName) throws IOException {
        if (entryName == null) {
            SeaweedLogger.warning(this.getClass(), "Provided entryName is null.");
            return null;
        }
        final File tempFile = File.createTempFile(entryName, ".seaweed");
        tempFile.deleteOnExit();
        getFile(folderPath + File.separator + entryName, tempFile);
        if (tempFile.length() > maxFileSizeOnMemory) {
            throw new SeaweedClientException(this.getClass(), "File length '" + tempFile.length()
                    + "' exceeds the maximum size allowed on memory '" + maxFileSizeOnMemory + "'.");
        }
        final byte[] byteArray = new byte[(int) tempFile.length()];
        try (FileInputStream inputStream = new FileInputStream(tempFile)) {
            inputStream.read(byteArray);
        }
        tempFile.delete();
        return byteArray;
    }


    public void cleanFolder(String folderPath) {
        filerClient.rm(folderPath, true, true);
    }

    public void wipeOut() {
        filerClient.listEntries("/").forEach(entry -> {
            cleanFolder("/" + entry.getName());
        });
    }

}
