/**
 *
 */
package com.armedia.acm.services.costsheet.service;

import com.armedia.acm.services.costsheet.model.AcmCostsheet;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Properties;

/**
 * @author riste.tutureski
 */
public interface CostsheetService
{

    public Properties getProperties();

    public AcmCostsheet save(AcmCostsheet costsheet);

    public AcmCostsheet save(AcmCostsheet costsheet, String submissionName);

    public AcmCostsheet get(Long id);

    public List<AcmCostsheet> getByObjectIdAndType(Long objectId, String objectType, int startRow, int maxRows, String sortParams);

    public String getObjectsFromSolr(String objectType, Authentication authentication, int startRow, int maxRows, String sortParams, String searchQuery, String userId);

    public String getObjectsFromSolr(String objectType, Authentication authentication, int startRow, int maxRows, String sortParams, String userId);

    public boolean checkWorkflowStartup(String type);

    public String createName(AcmCostsheet costsheet);
}
