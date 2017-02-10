package com.armedia.acm.correspondence.service;

import static com.armedia.acm.correspondence.service.TemplateMapper.mapConfigurationFromTemplate;
import static com.armedia.acm.correspondence.service.TemplateMapper.mapTemplateFromConfiguration;
import static com.armedia.acm.correspondence.service.TemplateMapper.updateTemplateState;

import com.armedia.acm.correspondence.model.CorrespondenceQuery;
import com.armedia.acm.correspondence.model.CorrespondenceTemplate;
import com.armedia.acm.correspondence.model.CorrespondenceTemplateConfiguration;
import com.armedia.acm.correspondence.model.QueryType;
import com.armedia.acm.services.config.model.AcmConfig;
import com.armedia.acm.services.config.model.JsonConfig;
import com.armedia.acm.spring.SpringContextHolder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.apache.commons.io.FileUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jan 26, 2017
 */
public class CorrespondenceTemplateManager implements ApplicationListener<ContextRefreshedEvent>
{

    private SpringContextHolder springContextHolder;

    private Resource correspondenceTemplatesConfiguration;

    private Resource caseCorrespondenceForms;

    private Resource complaintCorrespondenceForms;

    private Map<String, CorrespondenceTemplate> templates = new HashMap<>();

    private Pattern camelCase = Pattern.compile("[A-Za-z].*?(?=([A-Z]|\\.))");

    private List<AcmConfig> configList;

    /**
     * @param springContextHolder
     *            the springContextHolder to set
     */
    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }

    /**
     * @param correspondenceTemplatesConfiguration
     *            the correspondenceTemplatesConfiguration to set
     */
    public void setCorrespondenceTemplatesConfiguration(Resource correspondenceTemplatesConfiguration)
    {
        this.correspondenceTemplatesConfiguration = correspondenceTemplatesConfiguration;
    }

    /**
     * @param caseCorrespondenceForms
     *            the caseCorrespondenceForms to set
     */
    public void setCaseCorrespondenceForms(Resource caseCorrespondenceForms)
    {
        this.caseCorrespondenceForms = caseCorrespondenceForms;
    }

    /**
     * @param complaintCorrespondenceForms
     *            the complaintsCorrespondenceForms to set
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
    public void onApplicationEvent(ContextRefreshedEvent event)
    {
        try
        {
            File file = correspondenceTemplatesConfiguration.getFile();
            String resource = FileUtils.readFileToString(file);

            ObjectMapper mapper = new ObjectMapper();

            List<CorrespondenceTemplateConfiguration> templateConfigurations = mapper.readValue(resource,
                    new TypeReference<List<CorrespondenceTemplateConfiguration>>()
                    {
                    });

            Map<String, CorrespondenceQuery> correspondenceQueryBeansMap = springContextHolder.getAllBeansOfType(CorrespondenceQuery.class);

            templates.putAll(templateConfigurations.stream().map(c -> mapTemplateFromConfiguration(c, correspondenceQueryBeansMap))
                    .collect(Collectors.toMap(CorrespondenceTemplate::getTemplateFilename, Function.identity())));

        } catch (IOException ioe)
        {
            throw new IllegalStateException(ioe);
        }

    }

    /**
     * @return the templates
     */
    List<CorrespondenceTemplate> getTemplates()
    {
        return new ArrayList<>(templates.values());
    }

    /**
     * @param query
     * @return
     */
    String getQueryId(CorrespondenceQuery query)
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

    Optional<CorrespondenceTemplate> updateTemplate(CorrespondenceTemplate template) throws IOException
    {
        Optional<CorrespondenceTemplate> existing = findTemplate(template.getTemplateFilename());
        if (existing.isPresent())
        {
            CorrespondenceTemplate existingTemplate = existing.get();
            updateTemplateState(existingTemplate, template);
        } else
        {
            templates.put(template.getTemplateFilename(), template);
        }

        updateConfiguration(templates.values());
        updateLabels(template, templateLabels -> {
            Optional<TemplateLabel> labelHolder = templateLabels.stream()
                    .filter(tl -> tl.getTemplate().equals(template.getTemplateFilename())).findAny();
            if (labelHolder.isPresent())
            {
                TemplateLabel label = labelHolder.get();
                label.setLabel(template.getDisplayName());
                label.setActivated(template.isActivated());
            } else
            {
                templateLabels.add(new TemplateLabel(template.getTemplateFilename(), template.getDocumentType(), template.isActivated()));
            }
        });

        return Optional.of(template);
    }

    /**
     * @param templateFileName
     * @return
     * @throws IOException
     */
    Optional<CorrespondenceTemplate> deleteTemplate(String templateFileName) throws IOException
    {
        Optional<CorrespondenceTemplate> result = findTemplate(templateFileName);
        if (result.isPresent())
        {
            CorrespondenceTemplate template = result.get();
            templates.remove(template.getTemplateFilename());
            updateConfiguration(templates.values());
            updateLabels(template, templateLabels -> {
                Optional<TemplateLabel> label = templateLabels.stream()
                        .filter(tl -> tl.getTemplate().equals(template.getTemplateFilename())).findAny();
                if (label.isPresent())
                {
                    templateLabels.remove(label.get());
                }
            });
            return result;
        }
        return Optional.empty();
    }

    /**
     * @param templateFileName
     * @return
     */
    Optional<CorrespondenceTemplate> getTemplateByFileName(String templateFileName)
    {
        return Optional.of(findTemplate(templateFileName).orElseGet(() -> {
            CorrespondenceTemplate template = new CorrespondenceTemplate();
            template.setTemplateFilename(templateFileName);
            template.setDocumentType(generateDocumentTypeFromFilename(templateFileName));
            template.setDateFormatString("MM/dd/yyyy");
            template.setNumberFormatString("###,###,###");
            template.setTemplateSubstitutionVariables(new HashMap<>());
            return template;
        }));
    }

    private Optional<CorrespondenceTemplate> findTemplate(String templateFileName)
    {
        return Optional.ofNullable(templates.get(templateFileName));
    }

    /**
     * @param documentType
     * @return
     */
    private String generateDocumentTypeFromFilename(String documentType)
    {
        Matcher matcher = camelCase.matcher(documentType);
        List<String> matches = new LinkedList<>();
        while (matcher.find())
        {
            matches.add((matcher.group()));
        }
        return matches.stream().collect(Collectors.joining(" "));
    }

    /**
     * @param templates
     * @throws IOException
     */
    private void updateConfiguration(Collection<CorrespondenceTemplate> templates) throws IOException
    {
        List<CorrespondenceTemplateConfiguration> configurations = templates.stream()
                .map(template -> mapConfigurationFromTemplate(template, this::getQueryId)).collect(Collectors.toList());

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String configurationsOutput = mapper.writeValueAsString(configurations);

        File file = correspondenceTemplatesConfiguration.getFile();
        FileUtils.writeStringToFile(file, configurationsOutput);

    }

    @FunctionalInterface
    private static interface LabelsUpdater
    {
        void updateLabels(List<TemplateLabel> templateLabels);
    }

    /**
     * @param template
     * @throws IOException
     */
    private void updateLabels(CorrespondenceTemplate template, LabelsUpdater updater) throws IOException
    {
        File file;
        switch (template.getQuery().getType())
        {
        case CASE_FILE:
            file = caseCorrespondenceForms.getFile();
            break;
        case COMPLAINT:
            file = complaintCorrespondenceForms.getFile();
            break;
        default:
            throw new IllegalArgumentException();
        }
        String resource = FileUtils.readFileToString(file);

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        List<TemplateLabel> templateLabels = mapper.readValue(resource, new TypeReference<List<TemplateLabel>>()
        {
        });

        updater.updateLabels(templateLabels);

        String configValueAsString = mapper.writeValueAsString(templateLabels);
        FileUtils.writeStringToFile(file, configValueAsString);
        updateConfig(template.getQuery().getType(), configValueAsString);
    }

    public void setConfigList(List<AcmConfig> configList)
    {
        this.configList = configList;
    }

    /**
     * updates configList with for given queryType and value
     *
     * @param queryType
     * @param configValue
     */
    private void updateConfig(QueryType queryType, String configValue)
    {
        if (configList == null || configList.isEmpty())
        {
            // couldn't find case and complaints configs
            return;
        }
        AcmConfig casesConfig = configList.stream().filter(config -> "caseCorrespondenceForms".equals(config.getConfigName())).findFirst()
                .orElse(null);
        AcmConfig complaintsConfig = configList.stream().filter(config -> "complaintCorrespondenceForms".equals(config.getConfigName()))
                .findFirst().orElse(null);
        switch (queryType)
        {
        case CASE_FILE:
            if (casesConfig instanceof JsonConfig)
            {
                ((JsonConfig) casesConfig).setJson(configValue);
            }
            break;
        case COMPLAINT:
            if (complaintsConfig instanceof JsonConfig)
            {
                ((JsonConfig) complaintsConfig).setJson(configValue);
            }
            break;
        default:
            throw new IllegalArgumentException();
        }
    }
}
