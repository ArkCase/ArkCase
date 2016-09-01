/**
 *
 */
package com.armedia.acm.services.timesheet.service;

import com.armedia.acm.services.timesheet.model.AcmTimesheet;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Properties;

/**
 * @author riste.tutureski
 */
public interface TimesheetService
{

    public Properties getProperties();

    public AcmTimesheet save(AcmTimesheet timesheet);

    public AcmTimesheet save(AcmTimesheet timesheet, String submissionName);

    public AcmTimesheet get(Long id);

    public List<AcmTimesheet> getByObjectIdAndType(Long objectId, String objectType, int startRow, int maxRows, String sortParams);

    public String getObjectsFromSolr(String objectType, Authentication authentication, int startRow, int maxRows, String sortParams, String searchQuery, String userId);

    public String getObjectsFromSolr(String objectType, Authentication authentication, int startRow, int maxRows, String sortParams, String userId);

    public boolean checkWorkflowStartup(String type);

    public String createName(AcmTimesheet timesheet);
}
