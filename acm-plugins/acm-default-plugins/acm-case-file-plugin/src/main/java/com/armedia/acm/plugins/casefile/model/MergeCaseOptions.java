package com.armedia.acm.plugins.casefile.model;

/**
 * Created by nebojsha on 28.05.2015.
 */
public class MergeCaseOptions {
    private Long sourceCaseFileId;
    private Long targetCaseFileId;

    public Long getSourceCaseFileId() {
        return sourceCaseFileId;
    }

    public void setSourceCaseFileId(Long sourceCaseFileId) {
        this.sourceCaseFileId = sourceCaseFileId;
    }

    public Long getTargetCaseFileId() {
        return targetCaseFileId;
    }

    public void setTargetCaseFileId(Long targetCaseFileId) {
        this.targetCaseFileId = targetCaseFileId;
    }
}
