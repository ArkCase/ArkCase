package com.armedia.acm.plugins.casefile.model;

import java.util.List;

/**
 * Created by nebojsha on 01.06.2015.
 */
public class SplitCaseOptions {
    private Long caseFileId;
    private List<AttachmentDTO> attachments;
    private boolean preserveFolderStructure;

    public boolean isPreserveFolderStructure() {
        return preserveFolderStructure;
    }

    public void setPreserveFolderStructure(boolean preserveFolderStructure) {
        this.preserveFolderStructure = preserveFolderStructure;
    }

    public Long getCaseFileId() {
        return caseFileId;
    }

    public void setCaseFileId(Long caseFileId) {
        this.caseFileId = caseFileId;
    }

    public List<AttachmentDTO> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentDTO> attachments) {
        this.attachments = attachments;
    }

    public static class AttachmentDTO {
        private Long id;
        private String type;

        public AttachmentDTO(Long id, String type) {
            this.id = id;
            this.type = type;
        }

        public AttachmentDTO() {
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
