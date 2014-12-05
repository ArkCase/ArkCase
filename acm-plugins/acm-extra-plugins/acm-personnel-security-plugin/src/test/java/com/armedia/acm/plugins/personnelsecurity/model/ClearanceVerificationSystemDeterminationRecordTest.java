package com.armedia.acm.plugins.personnelsecurity.model;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Created by armdev on 12/5/14.
 */
public class ClearanceVerificationSystemDeterminationRecordTest
{

    private static final int RECORD_LENGTH = 170;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Test
    public void toString_correctLength()
    {
        ClearanceVerificationSystemDeterminationRecord record = new ClearanceVerificationSystemDeterminationRecord();
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
        record.setSubjectDateOfBirth(new Date());
        record.setSubjectUSPlaceOfBirth("IL");

        String cvsRecord = record.toString();

        log.debug(">" + cvsRecord + "<");

        assertEquals("000000000", cvsRecord.substring(0, 9));

        assertEquals("03", cvsRecord.substring(167, 169));

        assertEquals(RECORD_LENGTH, record.toString().length());

    }
}
