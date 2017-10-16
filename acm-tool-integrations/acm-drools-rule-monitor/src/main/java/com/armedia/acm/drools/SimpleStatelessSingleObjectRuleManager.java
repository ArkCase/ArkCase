package com.armedia.acm.drools;

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

    private KieBase kieBase;
    private String ruleFileLocation;

    public KieBase getKieBase() {
		return kieBase;
	}

	public void setKieBase(KieBase kieBase) {
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

        if ( ruleFile.exists() && ruleFile.isFile())
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

    public synchronized void updateRulesFromFile(File configFile)
    {
        SpreadsheetCompiler sc = new SpreadsheetCompiler();

        Resource xls = new FileSystemResource(configFile);

        try
        {
            String drl = sc.compile(xls.getInputStream(), InputType.XLS);


            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
            dtconf.setInputType(DecisionTableInputType.XLS);
            kbuilder.add( ResourceFactory.newInputStreamResource(xls.getInputStream()), ResourceType.DTABLE, dtconf );

            if ( kbuilder.hasErrors() )
            {
                log.error("DRL with errors: {}", drl);

                for (KnowledgeBuilderError error : kbuilder.getErrors() )
                {
                    log.error("Error building rules: {}", error);
                }

                throw new RuntimeException(String.format("Could not build rules from %s", configFile.getAbsolutePath()));
            }

            KieBase base = kbuilder.newKnowledgeBase();

            setKieBase(base);

            log.debug("Updated business rules from file '{}'",  getRuleSpreadsheetFilename());

        } catch (IOException e)
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


    public void setRuleFileLocation(String ruleFileLocation)
    {
        this.ruleFileLocation = ruleFileLocation;
    }

    public String getRuleFileLocation()
    {
        return ruleFileLocation;
    }
}
