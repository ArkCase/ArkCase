/**
 *
 */
package gov.foia.model;

import com.armedia.acm.data.converter.BooleanToStringConverter;
import com.armedia.acm.plugins.ecm.model.EcmFile;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;

import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jun 21, 2016
 */
@Entity
@DiscriminatorValue("gov.foia.model.FOIAFile")
public class FOIAFile extends EcmFile
{

    private static final long serialVersionUID = 3364794434531371617L;

    @ElementCollection
    @CollectionTable(name = "foia_file_exemption_code", joinColumns = @JoinColumn(name = "ecm_file_id", referencedColumnName = "cm_file_id"))
    @Column(name = "fo_exemption_code")
    private List<String> exemptionCodes;

    @Column(name = "fo_public_flag")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean publicFlag;

    /**
     * @return the exemptionCodes
     */
    public List<String> getExemptionCodes()
    {
        return exemptionCodes;
    }

    /**
     * @param exemptionCodes
     *            the exemptionCodes to set
     */
    public void setExemptionCodes(List<String> exemptionCodes)
    {
        this.exemptionCodes = exemptionCodes;
    }

    public Boolean getPublicFlag()
    {
        return publicFlag;
    }

    public void setPublicFlag(Boolean publicFlag)
    {
        this.publicFlag = publicFlag;
    }
}
