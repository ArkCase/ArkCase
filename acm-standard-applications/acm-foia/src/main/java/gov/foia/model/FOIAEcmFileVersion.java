package gov.foia.model;

import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("gov.foia.model.EcmFileVersion")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class FOIAEcmFileVersion extends EcmFileVersion
{
    @Column(name = "fo_review_status")
    private String reviewStatus;

    @Column(name = "fo_redaction_status")
    private String redactionStatus;

    public String getReviewStatus()
    {
        return reviewStatus;
    }

    public void setReviewStatus(String reviewStatus)
    {
        this.reviewStatus = reviewStatus;
    }

    public String getRedactionStatus()
    {
        return redactionStatus;
    }

    public void setRedactionStatus(String redactionStatus)
    {
        this.redactionStatus = redactionStatus;
    }
}
