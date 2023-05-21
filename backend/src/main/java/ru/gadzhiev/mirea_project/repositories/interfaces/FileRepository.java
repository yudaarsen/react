package ru.gadzhiev.mirea_project.repositories.interfaces;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileRepository {
    void init();
    String save(int appealId, MultipartFile file);
    Resource load(String path);
}
