package gov.foia.service;

public interface FOIAEcmFileService
{
    void setReviewStatus(Long fileId, String fileVersion, String reviewStatus);

    void setRedactionStatus(Long fileId, String fileVersion, String redactionStatus);
}
