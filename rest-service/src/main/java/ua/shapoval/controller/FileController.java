package ua.shapoval.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.shapoval.service.FileService;

@RestController
@Log4j
@RequestMapping("/api/file/")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping(value = "get-doc")
    private ResponseEntity<?> document(@RequestParam("id") String id){
        var document = fileService.getDocument(id);

        if (document == null){
            return ResponseEntity
                    .badRequest()
                    .build();
        }
        var fileSystemResources = fileService.getFileSystemResource(document.getBinaryContent());
        if (fileSystemResources == null){
            return ResponseEntity
                    .internalServerError()
                    .build();
        }
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(document.getType()))
                .header("Content-disposition", "attachment; filename" + document.getDocumentName())
                .body(fileSystemResources);

    }

    @GetMapping(value = "get-photo")
    private ResponseEntity<?> photo(@RequestParam("id") String id){
        var photo = fileService.getPhoto(id);
        if (photo == null){
            return ResponseEntity
                    .badRequest()
                    .build();
        }
        var fileSystemResources = fileService.getFileSystemResource(photo.getBinaryContent());
        if (fileSystemResources == null){
            return ResponseEntity
                    .internalServerError()
                    .build();
        }
        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header("Content-disposition", "attachment;")
                .body(fileSystemResources);

    }
}
