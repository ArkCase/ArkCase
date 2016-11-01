/**
 *
 */
package com.armedia.acm.form.cost.service;

import com.armedia.acm.form.cost.model.CostForm;
import com.armedia.acm.form.cost.model.CostItem;
import com.armedia.acm.frevvo.config.FrevvoFormChargeAbstractService;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.frevvo.model.Details;
import com.armedia.acm.frevvo.model.FrevvoUploadedFiles;
import com.armedia.acm.frevvo.model.Options;
import com.armedia.acm.frevvo.model.OptionsAndDetailsByType;
import com.armedia.acm.pluginmanager.service.AcmPluginManager;
import com.armedia.acm.plugins.ecm.dao.AcmContainerDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.services.costsheet.dao.AcmCostsheetDao;
import com.armedia.acm.services.costsheet.model.AcmCostsheet;
import com.armedia.acm.services.costsheet.model.CostsheetConstants;
import com.armedia.acm.services.costsheet.service.CostsheetEventPublisher;
import com.armedia.acm.services.costsheet.service.CostsheetService;
import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.search.service.SearchResults;
import com.armedia.acm.services.users.model.AcmUser;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author riste.tutureski
 */
public class CostService extends FrevvoFormChargeAbstractService
{

    private Logger LOG = LoggerFactory.getLogger(getClass());

    private CostsheetService costsheetService;
    private AcmCostsheetDao acmCostsheetDao;
    private CostFactory costFactory;
    private CostsheetEventPublisher costsheetEventPublisher;
    private SearchResults searchResults;
    private AcmPluginManager acmPluginManager;
    private AcmContainerDao AcmContainerDao;

    @Override
    public Object init()
    {
        Object result = "";

        if (getDocUriParameters() == null || "".equals(getDocUriParameters()))
        {
            return result;
        }

        String costsheetId = getDocUriParameter("costsheetId");

        try
        {
            Long costsheetIdLong = Long.parseLong(costsheetId);
            AcmCostsheet costsheet = getCostsheetService().get(costsheetIdLong);

            if (costsheet != null)
            {
                CostForm form = getCostFactory().asFrevvoCostForm(costsheet);
                form = (CostForm) populateEditInformation(form, costsheet.getContainer(), getFormName());
                form.setDocUriParameters(getDocUriParameters());
                form.setBalanceTable(Arrays.asList(new String()));

                result = convertFromObjectToXML(form);
            }
        } catch (Exception e)
        {
            LOG.error("Cannot parse {} to Long type. Empty form will be created.", costsheetId, e);
        }

        return result;
    }

    @Override
    public Object get(String action)
    {
        Object result = null;

        if (action != null)
        {
            if ("init-form-data".equals(action))
            {
                result = initFormData();
            }
        }

        return result;
    }

    @Override
    public boolean save(String xml, MultiValueMap<String, MultipartFile> attachments) throws Exception
    {
        // Get submission name - Save or Submit
        String submissionName = getRequest().getParameter("submission_name");

        // Unmarshall XML to object
        CostForm form = (CostForm) convertFromXMLToObject(cleanXML(xml), getFormClass());

        if (form == null)
        {
            LOG.warn("Cannot unmarshall Time Form.");
            return false;
        }

        AcmCostsheet costsheet = getCostFactory().asAcmCostsheet(form);

        // Create timesheet folder (if not exist)
        String rootFolder = (String) getCostsheetService().getProperties().get(CostsheetConstants.ROOT_FOLDER_KEY);
        String folderName = getCostsheetService().createName(costsheet);
        String uniqueFolderName = getFolderAndFilesUtils().createUniqueFolderName(folderName);
        AcmContainer container = createContainer(rootFolder, costsheet.getUser().getUserId(), costsheet.getId(), CostsheetConstants.OBJECT_TYPE, uniqueFolderName);
        costsheet.setContainer(container);
        costsheet.setTitle(folderName);

        AcmCostsheet saved = getCostsheetService().save(costsheet, submissionName);

        form = getCostFactory().asFrevvoCostForm(saved);

        // Take user id and ip address
        String userId = getAuthentication().getName();
        String ipAddress = (String) getRequest().getSession().getAttribute("acm_ip_address");

        boolean startWorkflow = getCostsheetService().checkWorkflowStartup(CostsheetConstants.EVENT_TYPE + "." + submissionName.toLowerCase());

        FrevvoUploadedFiles uploadedFiles = null;
        uploadedFiles = saveAttachments(attachments, saved.getContainer().getFolder().getCmisFolderId(), FrevvoFormName.COSTSHEET.toUpperCase(), saved.getId());

        getCostsheetEventPublisher().publishEvent(saved, userId, ipAddress, true, submissionName.toLowerCase(), uploadedFiles, startWorkflow);

        return true;
    }

    public Object initFormData()
    {
        String userId = getAuthentication().getName();
        AcmUser user = getUserDao().findByUserId(userId);

        CostForm form = new CostForm();

        // Set user
        form.setUser(userId);
        form.setUserOptions(Arrays.asList(userId + "=" + user.getFullName()));

        // Init Types
        List<String> types = convertToList((String) getProperties().get(getFormName() + ".types"), ",");
        form.setObjectTypeOptions(types);

        // Init Statuses
        form.setStatusOptions(convertToList((String) getProperties().get(getFormName() + ".statuses"), ","));

        // Init Titles
        CostItem item = new CostItem();
        item.setTitleOptions(convertToList((String) getProperties().get(getFormName() + ".titles"), ","));
        form.setItems(Arrays.asList(item));

        // Set charge codes for each type and details for w
        OptionsAndDetailsByType optionsAndDetailsByType = getCodeOptionsAndDetails(getFormName(), types);

        Map<String, Options> codeOptions = optionsAndDetailsByType.getOptionsByType();
        Map<String, Map<String, Details>> codeOptionsDetails = optionsAndDetailsByType.getOptionsDetailsByType();

        form.setCodeOptions(codeOptions);
        form.setCodeDetails(codeOptionsDetails);

        // Create JSON and back to the Frevvo form
        JSONObject json = createResponse(form);

        return json;
    }

    @Override
    public String getSolrResponse(String objectType)
    {
        String jsonResults = getCostsheetService().getObjectsFromSolr(objectType, getAuthentication(), 0, 25,
                SearchConstants.PROPERTY_NAME + " " + SearchConstants.SORT_DESC, "*", null);

        return jsonResults;
    }

    @Override
    public String getFormName()
    {
        return FrevvoFormName.COSTSHEET;
    }

    @Override
    public Class<?> getFormClass()
    {
        return CostForm.class;
    }

    public CostsheetService getCostsheetService()
    {
        return costsheetService;
    }

    public void setCostsheetService(CostsheetService costsheetService)
    {
        this.costsheetService = costsheetService;
    }

    public AcmCostsheetDao getAcmCostsheetDao()
    {
        return acmCostsheetDao;
    }

    public void setAcmCostsheetDao(AcmCostsheetDao acmCostsheetDao)
    {
        this.acmCostsheetDao = acmCostsheetDao;
    }

    public CostFactory getCostFactory()
    {
        return costFactory;
    }

    public void setCostFactory(CostFactory costFactory)
    {
        this.costFactory = costFactory;
    }

    public CostsheetEventPublisher getCostsheetEventPublisher()
    {
        return costsheetEventPublisher;
    }

    public void setCostsheetEventPublisher(
            CostsheetEventPublisher costsheetEventPublisher)
    {
        this.costsheetEventPublisher = costsheetEventPublisher;
    }

    public SearchResults getSearchResults()
    {
        return searchResults;
    }

    public void setSearchResults(SearchResults searchResults)
    {
        this.searchResults = searchResults;
    }

    public AcmPluginManager getAcmPluginManager()
    {
        return acmPluginManager;
    }

    public void setAcmPluginManager(AcmPluginManager acmPluginManager)
    {
        this.acmPluginManager = acmPluginManager;
    }

    public AcmContainerDao getAcmContainerDao()
    {
        return AcmContainerDao;
    }

    public void setAcmContainerDao(AcmContainerDao acmContainerDao)
    {
        AcmContainerDao = acmContainerDao;
    }

    @Override
    public Object convertToFrevvoForm(Object obj, Object form)
    {
        // Implementation no needed so far
        return null;
    }
}
