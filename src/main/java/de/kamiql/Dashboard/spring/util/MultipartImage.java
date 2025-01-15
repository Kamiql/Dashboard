package de.kamiql.Dashboard.spring.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class MultipartImage implements MultipartFile {
    private final byte[] content;
    private final String fileName;
    private final String contentType;

    public MultipartImage(byte[] content, String fileName, String contentType) {
        this.content = content;
        this.fileName = fileName;
        this.contentType = contentType;
    }

    @Override
    public String getName() {
        return fileName;
    }

    @Override
    public String getOriginalFilename() {
        return fileName;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return content.length == 0;
    }

    @Override
    public long getSize() {
        return content.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return content;
    }

    @Override
    public ByteArrayInputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(content);
    }

    @Override
    public void transferTo(java.io.File dest) throws IOException, IllegalStateException {
        // Implement this if needed, but it's not required for this case
    }
}
