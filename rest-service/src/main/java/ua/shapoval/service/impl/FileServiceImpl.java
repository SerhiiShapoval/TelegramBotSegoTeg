package ua.shapoval.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import ua.shapoval.CryptoTool;
import ua.shapoval.entity.AppDocument;
import ua.shapoval.entity.AppPhoto;
import ua.shapoval.entity.BinaryContent;
import ua.shapoval.repository.AppDocumentRepository;
import ua.shapoval.repository.AppPhotoRepository;
import ua.shapoval.service.FileService;

import java.io.File;
import java.io.IOException;

@Service
@Log4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    public final AppDocumentRepository appDocumentRepository;
    private final AppPhotoRepository appPhotoRepository;
    private final CryptoTool cryptoTool;

    @Override
    public AppDocument getDocument(String hash) {
        var id = cryptoTool.idOf(hash);
        if (id == null){
            return null;
        }
        return appDocumentRepository.findById(id).orElse(null);
    }

    @Override
    public AppPhoto getPhoto(String hash) {
        var id = cryptoTool.idOf(hash);
        if (id == null){
            return null;
        }
        return appPhotoRepository.findById(id).orElse(null);
    }

    @Override
    public FileSystemResource getFileSystemResource(BinaryContent binaryContent) {
        try {
            File temp = File.createTempFile("tempFile", ".bin");
            temp.deleteOnExit();
            FileUtils.writeByteArrayToFile(temp, binaryContent.getFileAsArrayBytes());
            return new FileSystemResource(temp);

        } catch (IOException e) {
            log.error(e);
            return null;
        }

    }
}
