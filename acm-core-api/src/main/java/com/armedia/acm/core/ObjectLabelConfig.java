package com.armedia.acm.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;

public class ObjectLabelConfig
{
    @JsonProperty("object.CASE_FILE.label")
    @Value("${object.CASE_FILE.label}")
    private String caseFileLabel;

    @JsonProperty("object.COMPLAINT.label")
    @Value("${object.COMPLAINT.label}")
    private String complaintLabel;

    @JsonProperty("object.FILE.label")
    @Value("${object.FILE.label}")
    private String fileLabel;

    @JsonProperty("object.TASK.label")
    @Value("${object.TASK.label}")
    private String taskLabel;

    @JsonProperty("object.PARTICIPANT.label")
    @Value("${object.PARTICIPANT.label}")
    private String participantLabel;

    @JsonProperty("object.NOTE.label")
    @Value("${object.NOTE.label}")
    private String noteLabel;

    @JsonProperty("object.PERSON-ASSOCIATION.label")
    @Value("${object.PERSON-ASSOCIATION.label}")
    private String personAssociationLabel;

    @JsonProperty("object.DOC_REPO.label")
    @Value("${object.DOC_REPO.label}")
    private String docRepoLabel;

    public String getLabelForObjectType(String objectType)
    {
        switch (objectType)
        {
        case "TASK":
            return taskLabel;
        case "CASE_FILE":
            return caseFileLabel;
        case "COMPLAINT":
            return complaintLabel;
        case "FILE":
            return fileLabel;
        case "PARTICIPANT":
            return participantLabel;
        case "NOTE":
            return noteLabel;
        case "PERSON-ASSOCIATION":
            return personAssociationLabel;
        case "DOC_REPO":
            return docRepoLabel;
        default:
            return objectType;
        }
    }

    public String getCaseFileLabel()
    {
        return caseFileLabel;
    }

    public void setCaseFileLabel(String caseFileLabel)
    {
        this.caseFileLabel = caseFileLabel;
    }

    public String getComplaintLabel()
    {
        return complaintLabel;
    }

    public void setComplaintLabel(String complaintLabel)
    {
        this.complaintLabel = complaintLabel;
    }

    public String getFileLabel()
    {
        return fileLabel;
    }

    public void setFileLabel(String fileLabel)
    {
        this.fileLabel = fileLabel;
    }

    public String getTaskLabel()
    {
        return taskLabel;
    }

    public void setTaskLabel(String taskLabel)
    {
        this.taskLabel = taskLabel;
    }

    public String getParticipantLabel()
    {
        return participantLabel;
    }

    public void setParticipantLabel(String participantLabel)
    {
        this.participantLabel = participantLabel;
    }

    public String getNoteLabel()
    {
        return noteLabel;
    }

    public void setNoteLabel(String noteLabel)
    {
        this.noteLabel = noteLabel;
    }

    public String getPersonAssociationLabel()
    {
        return personAssociationLabel;
    }

    public void setPersonAssociationLabel(String personAssociationLabel)
    {
        this.personAssociationLabel = personAssociationLabel;
    }

    public String getDocRepoLabel()
    {
        return docRepoLabel;
    }

    public void setDocRepoLabel(String docRepoLabel)
    {
        this.docRepoLabel = docRepoLabel;
    }
}
