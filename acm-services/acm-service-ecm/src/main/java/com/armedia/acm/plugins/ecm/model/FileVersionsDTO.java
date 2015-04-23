package com.armedia.acm.plugins.ecm.model;

import java.util.List;

/**
 * Created by marjan.stefanoski on 22.04.2015.
 */
public class FileVersionsDTO {

    private String fileName;
    private Long fileId;
    private String acctiveVersion;
    private List<String> versions;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public List<String> getVersions() {
        return versions;
    }

    public void setVersions(List<String> versions) {
        this.versions = versions;
    }

    public String getAcctiveVersion() {
        return acctiveVersion;
    }

    public void setAcctiveVersion(String acctiveVersion) {
        this.acctiveVersion = acctiveVersion;
    }
}
