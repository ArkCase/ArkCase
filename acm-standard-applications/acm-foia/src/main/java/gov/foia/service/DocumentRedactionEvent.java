/**
 *
 */
package gov.foia.service;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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
