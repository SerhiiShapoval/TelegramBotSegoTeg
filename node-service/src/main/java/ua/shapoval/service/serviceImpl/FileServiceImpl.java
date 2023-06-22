package ua.shapoval.service.serviceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import ua.shapoval.entity.AppDocument;
import ua.shapoval.entity.BinaryContent;
import ua.shapoval.exeptions.UnloadFileException;
import ua.shapoval.repository.AppDocumentRepository;
import ua.shapoval.repository.BinaryContentRepository;
import ua.shapoval.service.FileService;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

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
    private final AppDocumentRepository appDocumentRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public AppDocument processDoc(Message message) {
        String fieldId = message.getDocument().getFileId();
        ResponseEntity<String> response = getFieldPath(fieldId);

            if (response.getStatusCode().equals(HttpStatus.OK)){
                JSONObject jsonObject = new JSONObject(response.getBody());
                String filePath = String.valueOf(jsonObject
                        .getJSONObject("result")
                        .getString("file_path"));
                byte[] fileInByte = downloadFile(filePath);
                BinaryContent transientBinaryContent = BinaryContent.builder()
                        .fileAsArrayBytes(fileInByte)
                        .build();
                BinaryContent persistentBinaryContent = binaryContentRepository.save(transientBinaryContent);
                Document telegramDoc = message.getDocument();
                AppDocument transientAppDoc = buildTransientAppDoc(telegramDoc, persistentBinaryContent);
                return appDocumentRepository.save(transientAppDoc);
            }else {
                throw new UnloadFileException("Bad response from telegram service " + response);
            }

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

    private byte[] downloadFile(String filePath) {
        String fullUri = fileStorageUri.replace("{token}", token)
                .replace("{filePath}",filePath);
        URL urlObj = null;
        try {
            urlObj = new URL(fullUri);
        } catch (MalformedURLException e) {
            throw new UnloadFileException(e.getMessage());
        }
        try(InputStream inputStream = urlObj.openStream()) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new UnloadFileException(e.getMessage());
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
