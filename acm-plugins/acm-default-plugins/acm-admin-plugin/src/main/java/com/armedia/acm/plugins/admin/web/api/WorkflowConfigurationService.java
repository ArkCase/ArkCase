package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.activiti.model.AcmProcessDefinition;
import com.armedia.acm.activiti.services.AcmBpmnService;
import com.armedia.acm.plugins.admin.exception.AcmWorkflowConfigurationException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.InputStream;
import java.util.*;

/**
 * Created by admin on 6/9/15.
 */
public class WorkflowConfigurationService {
    public static final String PROP_ID = "id";
    public static final String PROP_KEY = "key";
    public static final String PROP_NAME = "name";
    public static final String PROP_ACTIVE = "active";
    public static final String PROP_VERSION = "version";
    public static final String PROP_DESCRIPTION = "description";
    public static final String PROP_CREATED = "created";
    public static final String PROP_CREATOR = "creator";
    public static final String PROP_MODIFIED = "modified";
    public static final String PROP_MODIFIER = "modifier";

    private final Set<String> ORDERABLE_PROPERTIES = Collections.unmodifiableSet(new HashSet<String>(){{
        add(PROP_NAME);
        add(PROP_DESCRIPTION);
        add(PROP_CREATED);
        add(PROP_CREATOR);
        add(PROP_MODIFIED);
        add(PROP_MODIFIER);
    }});

    private String temporaryFolder;
    private AcmBpmnService acmBpmnService;

    /**
     * Retrieve workflows paginated list
     * @param start
     * @param length
     * @param orderBy
     * @param isAsc
     * @return
     */
    public List<AcmProcessDefinition> retrieveWorkflows(int start, int length, String orderBy, boolean isAsc) throws AcmWorkflowConfigurationException {
        if (!validateOrderByParam(orderBy)) {
            throw new AcmWorkflowConfigurationException(String.format("Wrong 'OrderBy' parameter: '%s'", orderBy));
        }

        return acmBpmnService.listPage(start, length, orderBy, isAsc);
    }


    /**
     * Return Workflow process history
     * @param key
     * @param version
     * @return
     */
    public List<AcmProcessDefinition> retrieveHistory(String key, int version) {
        AcmProcessDefinition processDefinition = acmBpmnService.getByKeyAndVersion(key, version);
        List<AcmProcessDefinition> history =  acmBpmnService.getVersionHistory(processDefinition);
        return history;
    }

    /**
     * Return Bpmn file of selected workflow
     * @param key
     * @param version
     * @return
     */
    public InputStream retrieveBpmnFile(String key, int version) {
        AcmProcessDefinition processDefinition = acmBpmnService.getByKeyAndVersion(key, version);
        return acmBpmnService.getBpmnFileStream(processDefinition);
    }

    /**
     * Replace workflow file
     * @param fileInputStream
     */
    public void uploadBpmnFile(InputStream fileInputStream) throws AcmWorkflowConfigurationException {
        // Create temp dir if required
        File tempDir = new File(temporaryFolder);
        tempDir.mkdirs();

        File tmpFile = null;
        try {
            tmpFile = File.createTempFile("bpmn-", ".xml", tempDir);
            FileUtils.copyInputStreamToFile(fileInputStream, tmpFile);
            acmBpmnService.deploy(tmpFile, false, true);
        } catch (Exception e) {
            throw new AcmWorkflowConfigurationException("Can't replace bpmn file", e);
        } finally {
            if (tmpFile != null) {
                FileUtils.deleteQuietly(tmpFile);
            }
        }

    }

    public void makeActive(String key, int version) {
        AcmProcessDefinition processDefinition = acmBpmnService.getByKeyAndVersion(key, version);
        acmBpmnService.makeActive(processDefinition);
    }


    public boolean validateOrderByParam(String orderByParam) {
        return ORDERABLE_PROPERTIES.contains(orderByParam);
    }

    public void setAcmBpmnService(AcmBpmnService acmBpmnService) {
        this.acmBpmnService = acmBpmnService;
    }

    public void setTemporaryFolder(String temporaryFolder) {
        this.temporaryFolder = temporaryFolder;
    }
}
