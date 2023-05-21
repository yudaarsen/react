package ru.gadzhiev.mirea_project.repositories.interfaces;

import org.springframework.dao.DataAccessException;
import ru.gadzhiev.mirea_project.models.Appeal;
import ru.gadzhiev.mirea_project.models.AppealComm;
import ru.gadzhiev.mirea_project.models.Attachment;
import ru.gadzhiev.mirea_project.utils.AppealUtils;

import java.util.List;

public interface AppealRepository {
    int createAppeal(AppealUtils.AppealCreatePayload appealCreatePayload) throws DataAccessException;
    Appeal getAppeal(int appealId, String lang) throws DataAccessException;
    void updateAppeal(AppealUtils.AppealUpdatePayload payload) throws DataAccessException;
    List<AppealComm> getAppealComms(int appealId, String lang) throws DataAccessException;
    List<Attachment> getAttachments(int appealId) throws DataAccessException;
    void addComm(AppealUtils.AppealCommPayload payload) throws DataAccessException;
    void addAttach(int appealId, int attachNum, String name, String path) throws DataAccessException;
    List<Appeal> getAppeals(int start, int limit) throws DataAccessException;
}
