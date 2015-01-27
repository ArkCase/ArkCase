package com.armedia.acm.drools;

import com.armedia.acm.files.AbstractConfigurationFileEvent;
import com.armedia.acm.files.ConfigurationFileAddedEvent;
import com.armedia.acm.files.ConfigurationFileChangedEvent;

import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatelessKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;

/**
 * Created by armdev on 4/17/14.
 */
public abstract class SimpleStatelessSingleObjectRuleManager<T>
        implements ApplicationListener<AbstractConfigurationFileEvent>
{

    private String ruleSpreadsheetFilename;

    private transient Logger log = LoggerFactory.getLogger(getClass());



    private StatelessKnowledgeSession rulesSession;

    public T applyRules(T businessObject)
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("Applying rules: " + businessObject);
        }

        getRulesSession().execute(businessObject);

        if ( log.isDebugEnabled() )
        {
            log.debug("Done applying rules: " + businessObject);
        }

        return businessObject;
    }

    @Override
    public void onApplicationEvent(AbstractConfigurationFileEvent fileEvent)
    {
        if ( fileEvent != null &&
                fileEvent.getConfigFile() != null &&
                getRuleSpreadsheetFilename().equals(fileEvent.getConfigFile().getName()) )
        {
            if ( fileEvent instanceof ConfigurationFileAddedEvent ||
                    fileEvent instanceof ConfigurationFileChangedEvent)
            {
                updateRulesFromFile(fileEvent.getConfigFile());
            }
        }
    }

    public void updateRulesFromFile(File configFile)
    {
        SpreadsheetCompiler sc = new SpreadsheetCompiler();

        Resource xls = new FileSystemResource(configFile);

        try
        {
            String drl = sc.compile(xls.getInputStream(), InputType.XLS);

            if ( log.isDebugEnabled() )
            {
                log.debug("DRL: " + drl);
            }

            /*
            Handling transformer type
             */
            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
            dtconf.setInputType(DecisionTableInputType.XLS);
            kbuilder.add( ResourceFactory.newInputStreamResource(xls.getInputStream()), ResourceType.DTABLE, dtconf );

            if ( kbuilder.hasErrors() )
            {
                for (KnowledgeBuilderError error : kbuilder.getErrors() )
                {
                    log.error("Error building rules: " + error);
                }

                throw new RuntimeException("Could not build rules from " + configFile.getAbsolutePath());
            }

            StatelessKnowledgeSession workingMemory = kbuilder.newKnowledgeBase().newStatelessKnowledgeSession();

            setRulesSession(workingMemory);

            if ( log.isDebugEnabled() )
            {
                log.debug("Updated business rules from file '" + getRuleSpreadsheetFilename() + "'.");
            }

        } catch (IOException e)
        {
            log.error("Could not update rules: " + e.getMessage(), e);
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

    public StatelessKnowledgeSession getRulesSession()
    {
        return rulesSession;
    }

    public void setRulesSession(StatelessKnowledgeSession rulesSession)
    {
        this.rulesSession = rulesSession;
    }

}
