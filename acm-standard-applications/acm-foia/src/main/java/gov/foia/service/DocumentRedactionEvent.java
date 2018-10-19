/**
 *
 */
package gov.foia.service;

import com.armedia.acm.core.model.AcmEvent;
import com.armedia.acm.plugins.ecm.model.EcmFile;

import java.util.Date;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Nov 14, 2016
 */
public class DocumentRedactionEvent extends AcmEvent
{

    private static final long serialVersionUID = 8688351069368877191L;
    private String exemptionCode;

    /**
     * @param source
     * @param user
     * @param redactionType
     * @param exemptionCode
     */
    public DocumentRedactionEvent(EcmFile source, String user, RedactionType redactionType, String exemptionCode)
    {
        super(source);

        setObjectId(source.getId());
        setEventDate(new Date());
        setUserId(user);
        setObjectType(source.getObjectType());
        setEventType(redactionType.type());
        this.exemptionCode = exemptionCode;
    }

    /**
     * @return the exemptionCode
     */
    public String getExemptionCode()
    {
        return exemptionCode;
    }

    /**
     * @param exemptionCodes
     *            the exemptionCode to set
     */
    public void setExemptionCode(String exemptionCode)
    {
        this.exemptionCode = exemptionCode;
    }

    public enum RedactionType
    {
        REMOVED("gov.foia.service.exemption.type.removed"), ADDED("gov.foia.service.exemption.type.added");

        private String redactionType;

        /**
         *
         */
        private RedactionType(String redactionType)
        {
            this.redactionType = redactionType;
        }

        public String type()
        {
            return redactionType;
        }
    }

}
