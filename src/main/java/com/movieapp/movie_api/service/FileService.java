package com.movieapp.movie_api.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public interface FileService {

    String uploadFile(String movieTitle, MultipartFile file);

    Path loadFile(String fileName) throws FileNotFoundException;

    Resource getResourceFile(String filename) throws IOException;

    String deleteFile(String filename) throws FileNotFoundException;

}
