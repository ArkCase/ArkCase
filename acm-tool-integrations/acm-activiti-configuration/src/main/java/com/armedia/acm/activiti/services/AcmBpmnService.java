package com.armedia.acm.activiti.services;

import com.armedia.acm.activiti.model.AcmProcessDefinition;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * Created by nebojsha on 09.04.2015.
 */
public interface AcmBpmnService {


    public List<AcmProcessDefinition> listPage(int start, int length, String orderBy, boolean isAsc);

    public InputStream getBpmnFileStream(AcmProcessDefinition wfFile);

    public void remove(AcmProcessDefinition processDefinition, boolean cascade);

    public void makeActive(AcmProcessDefinition processDefinition);

    public List<AcmProcessDefinition> getVersionHistory(AcmProcessDefinition processDefinition);

    public long count();

    public AcmProcessDefinition deploy(File processDefinitionFile, boolean makeActive, boolean deleteFileAfterDeploy);

    public AcmProcessDefinition getActive(String processDefinitionKey);

}
