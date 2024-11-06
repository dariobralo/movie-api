package com.movieapp.movie_api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService{

    private final Path storagePath;

    public FileServiceImpl(@Value("${project.uploads-dir}") String uploadDir){
        this.storagePath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.storagePath);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo crear el directorio de carga de imagenes", e);
        }
    }

    /*
     *
     */
    @Override
    public String uploadFile(String movieTitle, MultipartFile file) {

        String uuid = UUID.randomUUID().toString();

        String fileName = file.getOriginalFilename();
        String extension = "";
        if (fileName != null && fileName.contains(".")){
            extension = fileName.substring(fileName.lastIndexOf("."));
        }
        String newFileName = movieTitle.replaceAll("\\s+", "_") + "+" + uuid + extension;

        try {
            Path targetLocation = this.storagePath.resolve(newFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return newFileName;
        } catch (IOException e){
            throw new RuntimeException("Error al copiar archivo.", e);
        }
    }

    @Override
    public String deleteFile(String filename) throws FileNotFoundException {
        try {
            Path targetLocation = loadFile(filename);
            Files.delete(targetLocation);
        } catch (IOException e) {
            throw new FileNotFoundException();
        }

        return "Archivo Eliminado";
    }

    @Override
    public Resource getResourceFile(String filename) throws IOException {
        try {
            Path filePath = loadFile(filename);
            Resource image = new UrlResource(filePath.toUri());

            if (!image.exists() || !image.isReadable()){
                throw new FileNotFoundException("Fallo en generar el recurso. La imagen solicitada no existe o esta el archivo dañado");
            }

            return image;
        } catch (IOException e){
            System.out.println("Error al generar el recurso en FileService");
            throw new IOException("Error al generar el recurso");
        }
    }

    @Override
    public Path loadFile(String fileName) throws FileNotFoundException {
        return this.storagePath.resolve(fileName).normalize();
    }

}
