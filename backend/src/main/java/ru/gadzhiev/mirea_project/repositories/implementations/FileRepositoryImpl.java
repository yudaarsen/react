package ru.gadzhiev.mirea_project.repositories.implementations;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import ru.gadzhiev.mirea_project.repositories.interfaces.FileRepository;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Реализация репозитория для работы с файлами (приложениями к обращениям)
 */
@Repository
public class FileRepositoryImpl implements FileRepository {

    private final Path root = Paths.get("uploads");

    @PostConstruct
    @Override
    public void init() {
        try {
            Files.createDirectory(root);
        } catch (FileAlreadyExistsException e) {
          // dummy
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot create root directory!");
        }
    }

    @Override
    public String save(int appealId, MultipartFile file) {
        Path target = null;
        try {
            Path appealDir = root.resolve(String.valueOf(appealId));
            if(!Files.exists(appealDir))
                Files.createDirectory(appealDir);
            target = appealDir.resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), target);
            return target.toString();
        } catch (FileAlreadyExistsException e) {
            return target.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Resource load(String path) {
        try {
            Path filePath = root.resolve(path);
            Resource resource = new UrlResource(filePath.toUri());

            if(resource.exists() || resource.isReadable())
                return resource;
            else
                return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
