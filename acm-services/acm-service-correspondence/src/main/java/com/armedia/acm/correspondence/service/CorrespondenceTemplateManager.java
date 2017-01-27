package com.armedia.acm.correspondence.service;

import com.armedia.acm.correspondence.model.CorrespondenceQuery;
import com.armedia.acm.correspondence.model.CorrespondenceTemplate;
import com.armedia.acm.correspondence.model.CorrespondenceTemplateConfiguration;
import com.armedia.acm.spring.SpringContextHolder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

    private Resource caseCorrespondenceForms;

    private Resource complaintCorrespondenceForms;

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
     * @param correspondenceTemplatesConfiguration the correspondenceTemplatesConfiguration to set
     */
    public void setCorrespondenceTemplatesConfiguration(Resource correspondenceTemplatesConfiguration)
    {
        this.correspondenceTemplatesConfiguration = correspondenceTemplatesConfiguration;
    }

    /**
     * @param caseCorrespondenceForms the caseCorrespondenceForms to set
     */
    public void setCaseCorrespondenceForms(Resource caseCorrespondenceForms)
    {
        this.caseCorrespondenceForms = caseCorrespondenceForms;
    }

    /**
     * @param complaintCorrespondenceForms the complaintsCorrespondenceForms to set
     */
    public void setComplaintCorrespondenceForms(Resource complaintCorrespondenceForms)
    {
        this.complaintCorrespondenceForms = complaintCorrespondenceForms;
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
        updateLabels(template);

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
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String configurationsOutput = mapper.writeValueAsString(configurations);

        File file = correspondenceTemplatesConfiguration.getFile();
        FileUtils.writeStringToFile(file, configurationsOutput);

    }

    private CorrespondenceTemplateConfiguration mapConfigurationFromTemplate(CorrespondenceTemplate template)
    {
        CorrespondenceTemplateConfiguration configuration = new CorrespondenceTemplateConfiguration();

        configuration.setDocumentType(template.getDocumentType());
        configuration.setTemplateFilename(template.getTemplateFilename());
        configuration.setCorrespondenceQueryBeanId(getQueryId(template.getQuery()));
        configuration.setTemplateSubstitutionVariables(template.getTemplateSubstitutionVariables());
        configuration.setDateFormatString(template.getDateFormatString());
        configuration.setNumberFormatString(template.getNumberFormatString());

        return configuration;
    }

    /**
     * @param query
     * @return
     */
    private String getQueryId(CorrespondenceQuery query)
    {
        Map<String, CorrespondenceQuery> queryBeans = springContextHolder.getAllBeansOfType(CorrespondenceQuery.class);
        if (queryBeans.values().contains(query))
        {
            Optional<Entry<String, CorrespondenceQuery>> searchResult = queryBeans.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(query)).findFirst();
            if (searchResult.isPresent())
            {
                return searchResult.get().getKey();
            }
        }
        return null;
    }

    /**
     * @param template
     * @throws IOException
     */
    private void updateLabels(CorrespondenceTemplate template) throws IOException
    {
        File file;
        switch (template.getQuery().getType())
        {
        case CASE_FILE:
            file = caseCorrespondenceForms.getFile();
            break;
        case COMPLAINT:
            file = complaintCorrespondenceForms.getFile();
        default:
            throw new IllegalArgumentException();
        }
        String resource = FileUtils.readFileToString(file);

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        List<TemplateLabel> templateLabels = mapper.readValue(resource, new TypeReference<List<TemplateLabel>>()
        {
        });

        Optional<TemplateLabel> label = templateLabels.stream().filter(tl -> tl.getTemplate().equals(template.getTemplateFilename()))
                .findAny();
        if (label.isPresent())
        {
            label.get().setLabel(template.getDocumentType());
        } else
        {
            templateLabels.add(new TemplateLabel(template.getTemplateFilename(), template.getDocumentType()));
        }
        FileUtils.writeStringToFile(file, mapper.writeValueAsString(templateLabels));

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
                updateLabels(template);
            }
            return template;
        }
        return null;
    }

    private Optional<CorrespondenceTemplate> findTemplate(String templateFileName)
    {
        return templates.stream().filter(t -> t.getTemplateFilename().equals(templateFileName)).findAny();
    }

}
