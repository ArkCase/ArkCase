package com.armedia.acm.drools;

/*-
 * #%L
 * Tool Integrations: Drools Business Rule Monitor
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

import com.armedia.acm.configuration.service.FileConfigurationService;
import com.armedia.acm.files.AbstractConfigurationFileEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.springframework.context.ApplicationListener;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by armdev on 4/17/14.
 */
public abstract class SimpleStatelessSingleObjectRuleManager<T>
        implements ApplicationListener<AbstractConfigurationFileEvent>
{

    private String ruleSpreadsheetFilename;

    private transient Logger log = LogManager.getLogger(getClass());

    private KieBase kieBase;

    private FileConfigurationService fileConfigurationService;

    public KieBase getKieBase()
    {
        return kieBase;
    }

    public void setKieBase(KieBase kieBase)
    {
        this.kieBase = kieBase;
    }

    public T applyRules(T businessObject)
    {
        log.trace("Applying rules: {}", businessObject);

        try
        {
            synchronized (this)
            {
                getKieBase().newStatelessKieSession().execute(businessObject);
            }
        }
        catch (NullPointerException e)
        {
            log.error("{} NPE: {}", getClass().getName(), e.getMessage(), e);
        }

        log.trace("Done applying rules: {}", businessObject);

        return businessObject;
    }

    public void afterPropertiesSet() throws Exception
    {
        log.debug("Getting rules from {}", "rules/" + getRuleSpreadsheetFilename());

        try (InputStream stream = fileConfigurationService.getInputStreamFromConfiguration("rules/" + getRuleSpreadsheetFilename());)
        {
            updateRulesFromStream(stream);
        }
        catch (IOException e)
        {
            log.debug("Error getting rules {}", e.getMessage(), e);
        }
    }

    @Override
    public void onApplicationEvent(AbstractConfigurationFileEvent fileEvent)
    {
        // TODO Make watching mechanism to handle events from config server, not from local files.
    }

    private void updateRulesFromStream(InputStream stream)
    {
        SpreadsheetCompiler sc = new SpreadsheetCompiler();

        try
        {

            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
            dtconf.setInputType(DecisionTableInputType.XLS);
            kbuilder.add(ResourceFactory.newInputStreamResource(stream), ResourceType.DTABLE, dtconf);

            if (kbuilder.hasErrors())
            {
                String drl = sc.compile(stream, InputType.XLS);
                log.error("DRL with errors: {}", drl);

                for (KnowledgeBuilderError error : kbuilder.getErrors())
                {
                    log.error("Error building rules: {}", error);
                }

                throw new RuntimeException(String.format("Could not build rules from %s", getRuleSpreadsheetFilename()));
            }

            KieBase base = kbuilder.newKieBase();

            setKieBase(base);

            log.debug("Updated business rules from file '{}'", getRuleSpreadsheetFilename());

        }
        catch (Exception e)
        {
            log.error("Could not update rules: {}", e.getMessage(), e);
        }
    }

    public String getRuleSpreadsheetFilename()
    {
        return ruleSpreadsheetFilename;
    }

    public void setRuleSpreadsheetFilename(String ruleSpreadsheetFilename)
    {
        this.ruleSpreadsheetFilename = ruleSpreadsheetFilename;
    }

    public FileConfigurationService getFileConfigurationService()
    {
        return fileConfigurationService;
    }

    public void setFileConfigurationService(FileConfigurationService fileConfigurationService)
    {
        this.fileConfigurationService = fileConfigurationService;
    }
}
