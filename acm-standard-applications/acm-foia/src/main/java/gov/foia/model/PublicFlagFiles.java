package gov.foia.model;

import java.util.HashSet;
import java.util.Set;

public class PublicFlagFiles {

    private Set<Long> fileIds = new HashSet<>();
    private Set<Long> folderIds = new HashSet<>();

    public Set<Long> getFileIds() {
        return fileIds;
    }

    public void setFileIds(Set<Long> fileIds) {
        this.fileIds = fileIds;
    }

    public Set<Long> getFolderIds() {
        return folderIds;
    }

    public void setFolderIds(Set<Long> folderIds) {
        this.folderIds = folderIds;
    }
}
