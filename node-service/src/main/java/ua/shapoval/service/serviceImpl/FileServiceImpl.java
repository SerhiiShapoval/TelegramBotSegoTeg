package ua.shapoval.service.serviceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import ua.shapoval.CryptoTool;
import ua.shapoval.entity.AppDocument;
import ua.shapoval.entity.AppPhoto;
import ua.shapoval.entity.BinaryContent;
import ua.shapoval.exeptions.UploadFileException;
import ua.shapoval.repository.AppDocumentRepository;
import ua.shapoval.repository.AppPhotoRepository;
import ua.shapoval.repository.BinaryContentRepository;
import ua.shapoval.service.FileService;
import ua.shapoval.service.enums.LinkType;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j
public class FileServiceImpl implements FileService {

   @Value("${token}")
    private String token;
   @Value("${service.file_info.uri}")
    private String fileInfoUri;
    @Value("${service.file_storage.uri}")
    private String fileStorageUri;
    @Value("${link.address}")
    private String linkAddress;
    private final AppDocumentRepository appDocumentRepository;
    private final BinaryContentRepository binaryContentRepository;

    private final AppPhotoRepository appPhotoRepository;
    private final CryptoTool cryptoTool;

    @Override
    public AppDocument processDoc(Message message) {
        Document telegramDoc = message.getDocument();
        String fieldId = telegramDoc.getFileId();
        ResponseEntity<String> response = getFieldPath(fieldId);

            if (response.getStatusCode().equals(HttpStatus.OK)){
                BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);
                AppDocument transientAppDoc = buildTransientAppDoc(telegramDoc, persistentBinaryContent);
                return appDocumentRepository.save(transientAppDoc);
            }else {
                throw new UploadFileException("Bad response from telegram service " + response);
            }

    }

    private BinaryContent getPersistentBinaryContent(ResponseEntity<String> response) {
        String filePath = getFilePath(response);
        byte[] fileInByte = downloadFile(filePath);
        BinaryContent transientBinaryContent = BinaryContent.builder()
                .fileAsArrayBytes(fileInByte)
                .build();
        return binaryContentRepository.save(transientBinaryContent);
    }

    private String getFilePath(ResponseEntity<String> response) {
        JSONObject jsonObject = new JSONObject(response.getBody());
        return String.valueOf(jsonObject
                .getJSONObject("result")
                .getString("file_path"));
    }



    @Override
    public AppPhoto processPhoto(Message message) {
        var photoSizeCount = message.getPhoto().size();
        var photoIndex = photoSizeCount > 1 ? message.getPhoto().size() - 1 : 0; //take original size photo
        PhotoSize telegramPhoto = message.getPhoto().get(photoIndex);

        String fieldId = telegramPhoto.getFileId();
        ResponseEntity<String> response = getFieldPath(fieldId);
        if (response.getStatusCode().equals(HttpStatus.OK)){
            BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);
            AppPhoto transientAppPhoto = buildTransientAppPhoto(telegramPhoto, persistentBinaryContent);
            return appPhotoRepository.save(transientAppPhoto);
        }else {
            throw new UploadFileException("Bad response from telegram service " + response);
        }
    }

    private AppPhoto buildTransientAppPhoto(PhotoSize telegramPhoto, BinaryContent persistentBinaryContent) {
        return AppPhoto.builder()
                .telegramFieldId(telegramPhoto.getFileId())
                .binaryContent(persistentBinaryContent)
                .fileSize(Long.valueOf(telegramPhoto.getFileSize()))
                .build();
    }

    private AppDocument buildTransientAppDoc(Document telegramDoc, BinaryContent persistentBinaryContent) {
        return AppDocument.builder()
                .telegramFieldId(telegramDoc.getFileId())
                .documentName(telegramDoc.getFileName())
                .binaryContent(persistentBinaryContent)
                .type(telegramDoc.getMimeType())
                .fileSize(telegramDoc.getFileSize())
                .build();
    }

    @Override
    public String generateLink(Long id, LinkType linkType) {
        var hash = cryptoTool.hashOf(id);
        return "http://" + linkAddress + linkType + "?id=" + hash;
    }
    private byte[] downloadFile(String filePath) {
        String fullUri = fileStorageUri.replace("{token}", token)
                .replace("{filePath}",filePath);
        URL urlObj = null;
        try {
            urlObj = new URL(fullUri);
        } catch (MalformedURLException e) {
            throw new UploadFileException(e.getMessage());
        }
        try(InputStream inputStream = urlObj.openStream()) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new UploadFileException(e.getMessage());
        }

    }

    private ResponseEntity<String> getFieldPath(String fieldId) {
        HttpEntity<String> request = new HttpEntity<>(new HttpHeaders());
        return new RestTemplate().exchange(
                fileInfoUri,
                HttpMethod.GET,
                request,
                String.class,
                token,
                fieldId
                );
    }
}
