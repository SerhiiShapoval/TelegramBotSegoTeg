package ua.shapoval.service;

import org.springframework.core.io.FileSystemResource;
import ua.shapoval.entity.AppDocument;
import ua.shapoval.entity.AppPhoto;
import ua.shapoval.entity.BinaryContent;

public interface FileService {

    AppDocument getDocument(String id);
    AppPhoto getPhoto(String id);
    FileSystemResource getFileSystemResource(BinaryContent binaryContent);
}
