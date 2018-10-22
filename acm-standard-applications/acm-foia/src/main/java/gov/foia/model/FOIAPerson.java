package gov.foia.model;

import com.armedia.acm.plugins.person.model.Person;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author sasko.tanaskoski
 *
 */

@Entity
@DiscriminatorValue("gov.foia.model.FOIAPerson")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class FOIAPerson extends Person
{

    private static final long serialVersionUID = -5025159640218061551L;

    @Column(name = "fo_position")
    private String position;

    /**
     * @return the position
     */
    public String getPosition()
    {
        return position;
    }

    /**
     * @param position
     *            the position to set
     */
    public void setPosition(String position)
    {
        this.position = position;
    }

}
