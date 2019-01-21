package com.armedia.acm.plugins.ecm.model;

public class ProgressbarDetails {

    private String uuid;
    private int stage;
    private int currentProgress;
    private boolean isProgressbar;
    private boolean success;
    private String objectType;
    private Long objectId;
    private String objectNumber;
    private String fileName;

    //status of the file. inprogress, finished, failed.
    private enum UploadFileStatus
    {
        READY,
        IN_PROGRESS,
        FAILED,
        FINISHED
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
    }

    public boolean isProgressbar() {
        return isProgressbar;
    }

    public void setProgressbar(boolean progressbar) {
        isProgressbar = progressbar;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public String getObjectNumber() {
        return objectNumber;
    }

    public void setObjectNumber(String objectNumber) {
        this.objectNumber = objectNumber;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
