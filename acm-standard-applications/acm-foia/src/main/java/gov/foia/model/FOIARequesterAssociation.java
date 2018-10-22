/**
 *
 */
package gov.foia.model;

import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jun 22, 2016
 */
@Entity
@DiscriminatorValue("gov.foia.model.FOIARequesterAssociation")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class FOIARequesterAssociation extends PersonAssociation
{

    private static final long serialVersionUID = -6645831984920786486L;

    @Column(name = "fo_requester_source")
    private String requesterSource;

    /**
     * @return the requesterSource
     */
    public String getRequesterSource()
    {
        return requesterSource;
    }

    /**
     * @param requesterSource
     *            the requesterSource to set
     */
    public void setRequesterSource(String requesterSource)
    {
        this.requesterSource = requesterSource;
    }

}
