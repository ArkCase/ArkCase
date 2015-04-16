package com.armedia.acm.activiti.services;

import com.armedia.acm.activiti.model.AcmProcessDefinition;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * Created by nebojsha on 09.04.2015.
 */
public interface ProcessDefinitionManagementService {


    public List<AcmProcessDefinition> listPage(int start, int length, String orderBy, boolean isAsc);

    public InputStream getProcessDefinitionFile(AcmProcessDefinition wfFile);

    public void removeProcessDefinition(AcmProcessDefinition processDefinition);

    public void makeActive(AcmProcessDefinition processDefinition);

    public List<AcmProcessDefinition> getVersionHistory(AcmProcessDefinition processDefinition);

    public long count();

    public AcmProcessDefinition deployProcessDefinition(File processDefinitionFile, boolean makeWorkingVersion, boolean deleteFileAfterDeploy);

    public AcmProcessDefinition getActive(String processDefinitionKey);
}
