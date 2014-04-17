package com.armedia.acm.drools;

import com.armedia.acm.files.AbstractConfigurationFileEvent;
import com.armedia.acm.files.ConfigurationFileAddedEvent;
import com.armedia.acm.files.ConfigurationFileChangedEvent;
import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.compiler.PackageBuilder;
import org.drools.core.RuleBase;
import org.drools.core.RuleBaseFactory;
import org.drools.core.StatelessSession;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

/**
 * Created by armdev on 4/17/14.
 */
public abstract class SimpleStatelessSingleObjectRuleManager<T>
        implements ApplicationListener<AbstractConfigurationFileEvent>
{

    private String ruleSpreadsheetFilename;

    private transient Logger log = LoggerFactory.getLogger(getClass());

    private StatelessSession rulesSession;

    public T applyRules(T businessObject)
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("Applying rules: " + businessObject);
        }

        getRulesSession().execute(businessObject);

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

            PackageBuilder builder = new PackageBuilder();
            builder.addPackageFromDrl(new StringReader(drl));

            RuleBase ruleBase = RuleBaseFactory.newRuleBase();
            ruleBase.addPackage(builder.getPackage());

            StatelessSession workingMemory = ruleBase.newStatelessSession();

            setRulesSession(workingMemory);

            if ( log.isDebugEnabled() )
            {
                log.debug("Updated business rules from file '" + getRuleSpreadsheetFilename() + "'.");
            }

        } catch (IOException | DroolsParserException e)
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

    public StatelessSession getRulesSession()
    {
        return rulesSession;
    }

    public void setRulesSession(StatelessSession rulesSession)
    {
        this.rulesSession = rulesSession;
    }
}
