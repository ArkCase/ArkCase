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

import com.armedia.acm.files.AbstractConfigurationFileEvent;
import com.armedia.acm.files.ConfigurationFileAddedEvent;
import com.armedia.acm.files.ConfigurationFileChangedEvent;

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
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationListener;

import java.io.File;
import java.io.FileInputStream;
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
    private String ruleFileLocation;

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
        String ruleFileName = getRuleFileLocation() + "/" + getRuleSpreadsheetFilename();
        log.debug("Loading rules from {}", ruleFileName);

        File ruleFile = new File(ruleFileName);

        if (ruleFile.exists() && ruleFile.isFile())
        {
            updateRulesFromFile(ruleFile);
        }
        else
        {
            log.warn("No such file: {}", ruleFileName);
        }

    }

    @Override
    public void onApplicationEvent(AbstractConfigurationFileEvent fileEvent)
    {
        if (fileEvent != null &&
                fileEvent.getConfigFile() != null &&
                getRuleSpreadsheetFilename().equals(fileEvent.getConfigFile().getName()))
        {
            if (fileEvent instanceof ConfigurationFileAddedEvent ||
                    fileEvent instanceof ConfigurationFileChangedEvent)
            {
                updateRulesFromFile(fileEvent.getConfigFile());
            }
        }
    }

    public synchronized void updateRulesFromFile(File configFile)
    {
        SpreadsheetCompiler sc = new SpreadsheetCompiler();

        try
        {

            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
            dtconf.setInputType(DecisionTableInputType.XLS);
            kbuilder.add(ResourceFactory.newFileResource(configFile), ResourceType.DTABLE, dtconf);

            if (kbuilder.hasErrors())
            {
                try (InputStream drlStream = new FileInputStream(configFile))
                {
                    String drl = sc.compile(drlStream, InputType.XLS);
                    log.error("DRL with errors: {}", drl);

                    for (KnowledgeBuilderError error : kbuilder.getErrors())
                    {
                        log.error("Error building rules: {}", error);
                    }
                }

                throw new RuntimeException(String.format("Could not build rules from %s", configFile.getAbsolutePath()));
            }

            KieBase base = kbuilder.newKnowledgeBase();

            setKieBase(base);

            log.debug("Updated business rules from file '{}'", getRuleSpreadsheetFilename());

        }
        catch (IOException e)
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

    public String getRuleFileLocation()
    {
        return ruleFileLocation;
    }

    public void setRuleFileLocation(String ruleFileLocation)
    {
        this.ruleFileLocation = ruleFileLocation;
    }
}
