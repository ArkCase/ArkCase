/**
 *
 */
package com.armedia.acm.form.cost.service;

/*-
 * #%L
 * ACM Forms: Cost
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

import com.armedia.acm.form.cost.model.CostForm;
import com.armedia.acm.form.cost.model.CostItem;
import com.armedia.acm.frevvo.config.FrevvoFormChargeAbstractService;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.frevvo.model.UploadedFiles;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.services.costsheet.dao.AcmCostsheetDao;
import com.armedia.acm.services.costsheet.model.AcmCostsheet;
import com.armedia.acm.services.costsheet.model.CostsheetConstants;
import com.armedia.acm.services.costsheet.service.CostsheetEventPublisher;
import com.armedia.acm.services.costsheet.service.CostsheetService;
import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.users.model.AcmUser;

import org.json.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * @author riste.tutureski
 */
public class CostService extends FrevvoFormChargeAbstractService
{

    private Logger LOG = LogManager.getLogger(getClass());

    private CostsheetService costsheetService;
    private AcmCostsheetDao acmCostsheetDao;
    private CostFactory costFactory;
    private CostsheetEventPublisher costsheetEventPublisher;

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
        }
        catch (Exception e)
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
            LOG.warn("Unmarshalling CostForm failed!");
            return false;
        }

        AcmCostsheet costsheet = getCostFactory().asAcmCostsheet(form);

        // Create costsheet folder (if not exist)
        String rootFolder = (String) getCostsheetService().getProperties().get(CostsheetConstants.ROOT_FOLDER_KEY);
        String folderName = getCostsheetService().createName(costsheet);
        String uniqueFolderName = getFolderAndFilesUtils().createUniqueFolderName(folderName);
        AcmContainer container = createContainer(rootFolder, costsheet.getUser().getUserId(), costsheet.getId(),
                CostsheetConstants.OBJECT_TYPE, uniqueFolderName);
        costsheet.setContainer(container);
        costsheet.setTitle(folderName);

        AcmCostsheet saved = getCostsheetService().save(costsheet, getAuthentication(), submissionName);

        form = getCostFactory().asFrevvoCostForm(saved);

        // Take user id and ip address
        String userId = getAuthentication().getName();
        String ipAddress = (String) getRequest().getSession().getAttribute("acm_ip_address");

        boolean startWorkflow = getCostsheetService()
                .checkWorkflowStartup(CostsheetConstants.EVENT_TYPE + "." + submissionName.toLowerCase());

        UploadedFiles uploadedFiles = saveAttachments(attachments, saved.getContainer().getFolder().getCmisFolderId(),
                FrevvoFormName.COSTSHEET.toUpperCase(), saved.getId());

        getCostsheetEventPublisher().publishEvent(saved, userId, ipAddress, true, submissionName.toLowerCase(), uploadedFiles,
                startWorkflow);

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
        List<String> types = getStandardLookupEntries("costsheetTypes");
        form.setObjectTypeOptions(types);

        // Init Statuses
        form.setStatusOptions(getStandardLookupEntries("costsheetStatuses"));

        // Init Titles
        CostItem item = new CostItem();
        item.setTitleOptions(getStandardLookupEntries("costsheetTitles"));
        form.setItems(Arrays.asList(item));

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

    public void setCostsheetEventPublisher(CostsheetEventPublisher costsheetEventPublisher)
    {
        this.costsheetEventPublisher = costsheetEventPublisher;
    }

    @Override
    public Object convertToFrevvoForm(Object obj, Object form)
    {
        // Implementation no needed so far
        return null;
    }
}
