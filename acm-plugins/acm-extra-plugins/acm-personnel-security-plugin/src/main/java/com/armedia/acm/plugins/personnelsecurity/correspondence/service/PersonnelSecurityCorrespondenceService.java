package com.armedia.acm.plugins.personnelsecurity.correspondence.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A stub service for now until the correspondence service is ready.
 */
public class PersonnelSecurityCorrespondenceService
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    public void generateClearanceDeniedCorrespondence(String subjectLastName)
    {
        log.info("Clearance was denied for subject last name '" + subjectLastName + "'");
    }

    public void generateClearanceGrantedCorrespondence(String subjectLastName)
    {
        log.info("Clearance was granted for subject last name '" + subjectLastName + "'");
    }
}
