package ru.gadzhiev.mirea_project.repositories.interfaces;

import ru.gadzhiev.mirea_project.models.Status;

import java.util.List;

public interface StatusRepository {
    int getInitialStatus();
    Status getStatus(int code, String lang);
}
