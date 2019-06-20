package com.armedia.acm.plugins.personnelsecurity.cvs.model;

/*-
 * #%L
 * ACM Personnel Security
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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Date;

/**
 * Created by armdev on 12/5/14.
 */
public class ClearanceVerificationSystemDeterminationRecordTest
{

    private static final int RECORD_LENGTH = 170;

    private final Logger log = LogManager.getLogger(getClass());

    @Test
    public void toString_correctRecordFormat()
    {
        ClearanceVerificationSystemDeterminationRecord record = new ClearanceVerificationSystemDeterminationRecord("Garcia", new Date(),
                true, new Date());
        record.setClearanceType("F");

        record.setAccessReported("SAPSCI");
        record.setAdjudicationStartDate(new Date());
        record.setClearanceException("W");
        record.setClearanceLevel("T");
        record.setContactGrantingAuthority("Y");
        record.setEligibilityDate(new Date());
        record.setEligibilityLevel("C");
        record.setExceptionType("W");
        record.setGrantedOrDeniedDate(new Date());
        record.setGrantingAuthoritySoi("SOI");
        record.setNonUSImmediateFamilyMembers("N");
        record.setRecordType("C");
        record.setStandardUsedToGrantClearance("A");
        record.setStatusDate(new Date());
        record.setStatusUpdate("N");
        record.setSubjectUSPlaceOfBirth("IL");

        String cvsRecord = record.toString();

        log.debug(">" + cvsRecord + "<");

        assertEquals("000000000", cvsRecord.substring(0, 9));

        assertEquals("03", cvsRecord.substring(167, 169));

        assertEquals(RECORD_LENGTH, record.toString().length());

    }
}
