package com.armedia.acm.plugins.task.model;

import java.io.Serializable;
import java.util.List;

public class BuckslipHistory implements Serializable
{
    private boolean nonConcurEndsApprovalProcess;
    List<PastTask> pastTasks;

    public boolean isNonConcurEndsApprovalProcess() {
        return nonConcurEndsApprovalProcess;
    }

    public void setNonConcurEndsApprovalProcess(boolean nonConcurEndsApprovalProcess) {
        this.nonConcurEndsApprovalProcess = nonConcurEndsApprovalProcess;
    }

    public List<PastTask> getPastTasks() {
        return pastTasks;
    }

    public void setPastTasks(List<PastTask> pastTasks) {
        this.pastTasks = pastTasks;
    }
}
