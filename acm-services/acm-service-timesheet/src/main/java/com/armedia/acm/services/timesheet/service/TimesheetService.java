package com.armedia.acm.services.timesheet.service;

import com.armedia.acm.services.timesheet.model.AcmTimesheet;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Properties;


public interface TimesheetService
{

    Properties getProperties();

    AcmTimesheet save(AcmTimesheet timesheet);

    AcmTimesheet save(AcmTimesheet timesheet, String submissionName);

    AcmTimesheet get(Long id);

    List<AcmTimesheet> getByObjectIdAndType(Long objectId, String objectType, int startRow, int maxRows, String sortParams);

    String getObjectsFromSolr(String objectType, Authentication authentication, int startRow, int maxRows, String sortParams, String searchQuery, String userId);

    String getObjectsFromSolr(String objectType, Authentication authentication, int startRow, int maxRows, String sortParams, String userId);

    boolean checkWorkflowStartup(String type);

    String createName(AcmTimesheet timesheet);
}
