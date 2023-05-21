package ru.gadzhiev.mirea_project.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.gadzhiev.mirea_project.models.Appeal;
import ru.gadzhiev.mirea_project.models.Category;
import ru.gadzhiev.mirea_project.repositories.interfaces.AppealRepository;
import ru.gadzhiev.mirea_project.repositories.interfaces.ClassificationRepository;
import ru.gadzhiev.mirea_project.repositories.interfaces.FileRepository;
import ru.gadzhiev.mirea_project.utils.AppealUtils;

import java.io.IOException;
import java.util.List;

/**
 * Основной контроллер API для обращений
 */
@RestController
public class AppealController {

    @Autowired
    private AppealRepository appealRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private ClassificationRepository classificationRepository;

    @PostMapping(path = "/api/appeal", produces = MediaType.APPLICATION_JSON_VALUE)
    public String createAppeal(@RequestBody String payload, HttpServletResponse response) {
        AppealUtils.AppealCreatePayload appealCreatePayload = AppealUtils.getAppealCreatePayload(payload);
        if(appealCreatePayload == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "";
        }
        try {
            int appealId = appealRepository.createAppeal(appealCreatePayload);
            return "{ \"appeal_id\" : " + appealId + " }";
        } catch (DataAccessException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return "";
    }

    @GetMapping(path = "/api/appeal", produces = MediaType.APPLICATION_JSON_VALUE)
    public Appeal getAppeal(@RequestParam @NonNull Integer id, @RequestParam @NonNull String lang,
                            HttpServletResponse response) throws MissingRequestValueException {
        try {
            return appealRepository.getAppeal(id, lang);
        } catch (EmptyResultDataAccessException e) {
            throw new MissingRequestValueException("Incorrect appeal id");
        } catch (DataAccessException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
    }

    @PatchMapping(path = "/api/appeal/{id}")
    public void updateAppeal(@PathVariable @NonNull Integer id, @RequestBody String payload, HttpServletResponse response) throws MissingRequestValueException {
        AppealUtils.AppealUpdatePayload appealUpdatePayload = AppealUtils.getAppealUpdatePayload(payload);
        if(appealUpdatePayload == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        appealUpdatePayload.setId(id);
        if(appealUpdatePayload.getId() <= 0)
            throw new MissingRequestValueException("Incorrect appeal id");

        try {
            appealRepository.updateAppeal(appealUpdatePayload);
        } catch (DataAccessException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @PostMapping(path = "/api/appeal/{id}/comm", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addComment(@PathVariable @NonNull Integer id, @RequestBody @NonNull String payload, HttpServletResponse response) throws MissingRequestValueException {
        AppealUtils.AppealCommPayload appealCommPayload = AppealUtils.getAppealCommPayload(payload);
        if(appealCommPayload == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        appealCommPayload.setAppealId(id);
        try {
            appealRepository.addComm(appealCommPayload);
        } catch (DataAccessException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @PostMapping(path = "/api/appeal/{id}/attachment")
    public void uploadAttachments(@PathVariable @NonNull Integer id, @RequestParam("files") @Nullable MultipartFile[] payload, HttpServletResponse response) {
        if(id <= 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if(payload == null)
            return;

        int attachNum = 0;
        for(MultipartFile file : payload) {
            String path = fileRepository.save(id, file);
            try {
                appealRepository.addAttach(id, attachNum, file.getOriginalFilename(), path);
            } catch (DataAccessException e) {
                e.printStackTrace();
            }
            attachNum++;
        }
    }

    @GetMapping(path = "/api/appeal/{id}/attachment/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable @NonNull Integer id, @PathVariable @NonNull String fileName) throws IOException {
        Resource resource = fileRepository.load(id + "/" + fileName);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment");
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @GetMapping(path = "/api/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Category> getCategories() {
        return classificationRepository.getCategories();
    }

    @GetMapping(path = "/api/appeals", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Appeal> getAppeals(@RequestParam @Nullable Integer start) {
        if(start == null)
            start = 0;
        return appealRepository.getAppeals(start, 10);
    }
}
