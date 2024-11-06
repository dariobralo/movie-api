package com.movieapp.movie_api.presentation.controller;

import com.movieapp.movie_api.service.FileService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/movie/image")
public class ImageController {

    private final FileService fileService;

    public ImageController(FileService fileService) {
        this.fileService = fileService;
    }

    /*
     *
     */
    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) throws IOException {
        try {
            Path filePath = fileService.loadFile(fileName);
            Resource image = new UrlResource(filePath.toUri());

            if (!image.exists() || !image.isReadable()){
                throw new FileNotFoundException("Fallo en generar el recurso. La imagen solicitada no existe o esta el archivo dañado");
            }

            String contentType = Files.probeContentType(filePath);
            if (contentType == null){
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + image.getFilename() + "\"")
                    .body(image);

        } catch (IOException e){
            throw new IOException("Error al generar recurso.");
        }
    }


}
