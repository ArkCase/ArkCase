package com.armedia.acm.correspondence.service;

import com.armedia.acm.correspondence.model.CorrespondenceQuery;
import com.armedia.acm.correspondence.model.CorrespondenceTemplate;
import com.armedia.acm.correspondence.model.CorrespondenceTemplateConfiguration;

import java.util.Map;
import java.util.function.Function;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jan 30, 2017
 *
 */
public class TemplateMapper
{

    public static CorrespondenceTemplate mapTemplateFromConfiguration(CorrespondenceTemplateConfiguration configuration,
            Map<String, CorrespondenceQuery> correspondenceQueryBeansMap)
    {
        CorrespondenceTemplate template = new CorrespondenceTemplate();

        template.setDocumentType(configuration.getDocumentType());
        template.setTemplateFilename(configuration.getTemplateFilename());
        template.setQuery(correspondenceQueryBeansMap.get(configuration.getCorrespondenceQueryBeanId()));
        template.setTemplateSubstitutionVariables(configuration.getTemplateSubstitutionVariables());
        template.setDateFormatString(configuration.getDateFormatString());
        template.setNumberFormatString(configuration.getNumberFormatString());

        return template;
    }

    public static CorrespondenceTemplateConfiguration mapConfigurationFromTemplate(CorrespondenceTemplate template,
            Function<CorrespondenceQuery, String> queryIdLookup)
    {
        CorrespondenceTemplateConfiguration configuration = new CorrespondenceTemplateConfiguration();

        configuration.setDocumentType(template.getDocumentType());
        configuration.setTemplateFilename(template.getTemplateFilename());
        configuration.setCorrespondenceQueryBeanId(queryIdLookup.apply(template.getQuery()));
        configuration.setTemplateSubstitutionVariables(template.getTemplateSubstitutionVariables());
        configuration.setDateFormatString(template.getDateFormatString());
        configuration.setNumberFormatString(template.getNumberFormatString());

        return configuration;
    }

    /**
     * @param toUpdate
     * @param updateFrom
     */
    public static void updateTemplateState(CorrespondenceTemplate toUpdate, CorrespondenceTemplate updateFrom)
    {
        toUpdate.setDocumentType(updateFrom.getDocumentType());
        toUpdate.setTemplateFilename(updateFrom.getTemplateFilename());
        toUpdate.setQuery(updateFrom.getQuery());
        toUpdate.setTemplateSubstitutionVariables(updateFrom.getTemplateSubstitutionVariables());
        toUpdate.setDateFormatString(updateFrom.getDateFormatString());
        toUpdate.setNumberFormatString(updateFrom.getNumberFormatString());
    }

}
