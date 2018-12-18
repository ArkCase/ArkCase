package gov.foia.service;

public interface FOIAEcmFileVersionService
{
    void setReviewStatus(Long fileId, String fileVersion, String reviewStatus);

    void setRedactionStatus(Long fileId, String fileVersion, String redactionStatus);
}
