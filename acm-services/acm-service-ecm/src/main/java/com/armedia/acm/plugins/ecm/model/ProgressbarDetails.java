package com.armedia.acm.plugins.ecm.model;

public class ProgressbarDetails {

    private int stage;
    private int currentProgress;
    private boolean isProgressbar;

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
}
