package com.armedia.acm.correspondence.service;

import com.armedia.acm.correspondence.model.CorrespondenceQuery;
import com.armedia.acm.correspondence.model.CorrespondenceTemplate;
import com.armedia.acm.correspondence.model.CorrespondenceTemplateConfiguration;
import com.armedia.acm.spring.SpringContextHolder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jan 26, 2017
 *
 */
public class CorrespondenceTemplateManager implements InitializingBean
{

    private SpringContextHolder springContextHolder;

    private Resource correspondenceTemplatesConfiguration;

    private List<CorrespondenceTemplate> templates = new ArrayList<>();

    Map<String, CorrespondenceQuery> correspondenceQueryBeansMap;

    /**
     * @param springContextHolder the springContextHolder to set
     */
    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }

    /**
     * @param correspondenceTemplatesConfigurationPath the correspondenceTemplatesConfigurationPath to set
     */
    public void setCorrespondenceTemplatesConfigurationPath(Resource correspondenceTemplatesConfigurationPath)
    {
        correspondenceTemplatesConfiguration = correspondenceTemplatesConfigurationPath;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception
    {
        File file = correspondenceTemplatesConfiguration.getFile();
        String resource = FileUtils.readFileToString(file);

        ObjectMapper mapper = new ObjectMapper();

        List<CorrespondenceTemplateConfiguration> templateConfigurations = mapper.readValue(resource,
                new TypeReference<List<CorrespondenceTemplateConfiguration>>()
                {
                });

        correspondenceQueryBeansMap = springContextHolder.getAllBeansOfType(CorrespondenceQuery.class);

        templates.addAll(templateConfigurations.stream().map(this::mapTemplateFromConfiguration).collect(Collectors.toList()));

        correspondenceQueryBeansMap = null;
    }

    private CorrespondenceTemplate mapTemplateFromConfiguration(CorrespondenceTemplateConfiguration configuration)
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

    /**
     * @return the templates
     */
    public List<CorrespondenceTemplate> getTemplates()
    {
        return new ArrayList<>(templates);
    }

    public CorrespondenceTemplate updateTemplate(CorrespondenceTemplate template) throws IOException
    {
        Optional<CorrespondenceTemplate> existing = findTemplate(template.getTemplateFilename());
        if (existing.isPresent())
        {
            CorrespondenceTemplate existingTemplate = existing.get();
            updateTemplate(existingTemplate, template);
        } else
        {
            templates.add(template);
        }

        updateConfiguration(templates);

        return template;
    }

    /**
     * @param existingTemplate
     * @param template
     */
    private void updateTemplate(CorrespondenceTemplate existingTemplate, CorrespondenceTemplate template)
    {
        existingTemplate.setDocumentType(template.getDocumentType());
        existingTemplate.setTemplateFilename(template.getTemplateFilename());
        existingTemplate.setQuery(template.getQuery());
        existingTemplate.setTemplateSubstitutionVariables(template.getTemplateSubstitutionVariables());
        existingTemplate.setDateFormatString(template.getDateFormatString());
        existingTemplate.setNumberFormatString(template.getNumberFormatString());
    }

    /**
     * @param templates2
     * @throws IOException
     */
    private void updateConfiguration(List<CorrespondenceTemplate> templates) throws IOException
    {
        List<CorrespondenceTemplateConfiguration> configurations = templates.stream().map(this::mapConfigurationFromTemplate)
                .collect(Collectors.toList());

        ObjectMapper mapper = new ObjectMapper();
        String configurationsOutput = mapper.writeValueAsString(configurations);

        File file = correspondenceTemplatesConfiguration.getFile();
        FileUtils.writeStringToFile(file, configurationsOutput);

        // should update the complaintCorrespondenceForms.json and caseCorrespondenceForms.json as well

    }

    private CorrespondenceTemplateConfiguration mapConfigurationFromTemplate(CorrespondenceTemplate template)
    {
        CorrespondenceTemplateConfiguration configuration = new CorrespondenceTemplateConfiguration();

        configuration.setDocumentType(template.getDocumentType());
        configuration.setTemplateFilename(configuration.getTemplateFilename());
        configuration.setCorrespondenceQueryBeanId(null);
        configuration.setTemplateSubstitutionVariables(template.getTemplateSubstitutionVariables());
        configuration.setDateFormatString(template.getDateFormatString());
        configuration.setNumberFormatString(template.getNumberFormatString());

        return configuration;
    }

    /**
     * @param templateFileName
     * @return
     */
    public CorrespondenceTemplate getTemplateByFileName(String templateFileName)
    {
        return findTemplate(templateFileName).orElse(null);
    }

    /**
     * @param templateFileName
     * @return
     * @throws IOException
     */
    public CorrespondenceTemplate deleteTemplate(String templateFileName) throws IOException
    {
        Optional<CorrespondenceTemplate> result = findTemplate(templateFileName);
        if (result.isPresent())
        {
            CorrespondenceTemplate template = result.get();
            if (templates.remove(template))
            {
                updateConfiguration(templates);
            }
        }
        return null;
    }

    private Optional<CorrespondenceTemplate> findTemplate(String templateFileName)
    {
        return templates.stream().filter(t -> t.getTemplateFilename().equals(templateFileName)).findAny();
    }

}
