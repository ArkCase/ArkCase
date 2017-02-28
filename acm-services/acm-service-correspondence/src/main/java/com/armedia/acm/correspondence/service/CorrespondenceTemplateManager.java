package com.armedia.acm.correspondence.service;

import static com.armedia.acm.correspondence.service.CorrespondenceMapper.mapConfigurationFromTemplate;
import static com.armedia.acm.correspondence.service.CorrespondenceMapper.mapTemplateFromConfiguration;

import com.armedia.acm.correspondence.model.CorrespondenceTemplate;
import com.armedia.acm.correspondence.model.CorrespondenceTemplateConfiguration;
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
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
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

    private Map<String, Map<String, CorrespondenceTemplate>> templates = new ConcurrentHashMap<>();

    private Pattern camelCase = Pattern.compile("[A-Za-z].*?(?=([A-Z]|\\.))");

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

            templateConfigurations.stream().forEach(configuration -> {
                CorrespondenceTemplate template = mapTemplateFromConfiguration(configuration);
                if (templates.containsKey(template.getTemplateId()))
                {
                    templates.get(template.getTemplateId()).put(template.getTemplateVersion(), template);
                } else
                {
                    templates.put(template.getTemplateId(), getVersionToTemplateMap(template));
                }
            });
            /*
             * for (CorrespondenceTemplateConfiguration configuration : templateConfigurations) { CorrespondenceTemplate
             * template = mapTemplateFromConfiguration(configuration); if
             * (templates.containsKey(template.getTemplateId())) {
             * templates.get(template.getTemplateId()).put(template.getTemplateVersion(), template); } else {
             * templates.put(template.getTemplateId(), getVersionToTemplateMap(template)); } }
             */

        } catch (IOException ioe)
        {
            throw new IllegalStateException(ioe);
        }

    }

    /**
     * @return the templates
     */
    List<CorrespondenceTemplate> getActiveVersionTemplates()
    {
        List<CorrespondenceTemplate> list = new ArrayList<CorrespondenceTemplate>();

        templates.values().stream().forEach(versionMap -> {
            Optional<CorrespondenceTemplate> template = versionMap.values().stream().filter(ct -> ct.isTemplateVersionActive()).findFirst();
            if (template.isPresent())
            {
                list.add(template.get());
            }
        });

        return list;
    }

    /**
     * @param objectType
     * @return the templates
     */
    List<CorrespondenceTemplate> getActivatedActiveVersionTemplatesByObjectType(String objectType)
    {
        List<CorrespondenceTemplate> list = new ArrayList<CorrespondenceTemplate>();

        templates.values().stream().forEach(versionMap -> {
            Optional<CorrespondenceTemplate> template = versionMap.values().stream().filter(ct -> ct.isTemplateVersionActive())
                    .filter(ct -> ct.getObjectType().equals(objectType)).filter(ct -> ct.isActivated()).findFirst();
            if (template.isPresent())
            {
                list.add(template.get());
            }
        });

        return list;
    }

    /**
     * @return the templates
     */
    List<CorrespondenceTemplate> getAllTemplates()
    {
        List<CorrespondenceTemplate> list = new ArrayList<CorrespondenceTemplate>();

        templates.values().stream().forEach(versionMap -> {
            list.addAll(versionMap.values());
        });

        return list;
    }

    Optional<CorrespondenceTemplate> updateTemplate(CorrespondenceTemplate template) throws IOException
    {
        Optional<CorrespondenceTemplate> optExisting = findActiveVersionTemplate(template.getTemplateId());
        if (optExisting.isPresent())
        {
            CorrespondenceTemplate existing = optExisting.get();
            templates.get(existing.getTemplateId()).put(template.getTemplateVersion(), template);
            if (!existing.getTemplateVersion().equals(template.getTemplateVersion()))
            {
                existing.setTemplateVersionActive(false);
                existing.setActivated(false);
            }
            // updateTemplateState(existing, template);
        } else
        {
            templates.put(template.getTemplateId(), getVersionToTemplateMap(template));
        }

        updateConfiguration(getAllTemplates());
        /*
         * updateLabels(template, templateLabels -> { Optional<TemplateLabel> labelHolder = templateLabels.stream()
         * .filter(tl -> tl.getTemplate().equals(template.getTemplateFilename())).findAny(); if
         * (labelHolder.isPresent()) { TemplateLabel label = labelHolder.get(); label.setLabel(template.getLabel());
         * label.setActivated(template.isActivated()); } else { templateLabels.add(new
         * TemplateLabel(template.getTemplateFilename(), template.getDocumentType(), template.isActivated())); } });
         */

        return Optional.of(template);
    }

    /**
     * @param templateId
     * @return
     * @throws IOException
     */
    Optional<CorrespondenceTemplate> deleteActiveVersionTemplate(String templateId) throws IOException
    {
        Optional<CorrespondenceTemplate> optTemplate = findActiveVersionTemplate(templateId);
        if (optTemplate.isPresent())
        {
            CorrespondenceTemplate template = optTemplate.get();
            templates.get(template.getTemplateId()).remove(template.getTemplateVersion());
            if (templates.get(template.getTemplateId()).isEmpty())
            {
                templates.remove(template.getTemplateId());
            }
            updateConfiguration(getAllTemplates());
            /*
             * updateLabels(template, templateLabels -> { Optional<TemplateLabel> label = templateLabels.stream()
             * .filter(tl -> tl.getTemplate().equals(template.getTemplateFilename())).findAny(); if (label.isPresent())
             * { templateLabels.remove(label.get()); } });
             */
            return optTemplate;
        }

        return Optional.empty();
    }

    /**
     * @param templateId
     * @param templateVersion
     * @return
     * @throws IOException
     */
    Optional<CorrespondenceTemplate> deleteTemplateByIdAndVersion(String templateId, String templateVersion) throws IOException
    {
        Optional<CorrespondenceTemplate> optTemplate = findTemplateByIdAndVersion(templateId, templateVersion);
        if (optTemplate.isPresent())
        {
            CorrespondenceTemplate template = optTemplate.get();
            templates.get(template.getTemplateId()).remove(template.getTemplateVersion());
            if (templates.get(template.getTemplateId()).isEmpty())
            {
                templates.remove(template.getTemplateId());
            }
            updateConfiguration(getAllTemplates());
            /*
             * updateLabels(template, templateLabels -> { Optional<TemplateLabel> label = templateLabels.stream()
             * .filter(tl -> tl.getTemplate().equals(template.getTemplateFilename())).findAny(); if (label.isPresent())
             * { templateLabels.remove(label.get()); } });
             */
            return optTemplate;
        }

        return Optional.empty();
    }

    /**
     * @param templateId
     * @param templateFilename
     * @return
     */
    Optional<CorrespondenceTemplate> getTemplateByIdAndFilename(String templateId, String templateFilename)
    {
        CorrespondenceTemplate template = new CorrespondenceTemplate();
        Optional<CorrespondenceTemplate> optExistingTemplate = findActiveVersionTemplate(templateId);
        if (optExistingTemplate.isPresent())
        {
            CorrespondenceTemplate existingTemplate = optExistingTemplate.get();
            template.setTemplateId(existingTemplate.getTemplateId());
            template.setTemplateVersion(Double.toString(templates.get(templateId).keySet().stream()
                    .mapToDouble(key -> Double.parseDouble(key)).reduce(0, (a, b) -> Double.max(a, b) + 1)));
            template.setTemplateVersionActive(true);
            template.setActivated(true);
            template.setTemplateFilename(templateFilename);
            template.setDateFormatString(existingTemplate.getDateFormatString());
            template.setNumberFormatString(existingTemplate.getNumberFormatString());
            template.setDocumentType(existingTemplate.getDocumentType());
            template.setObjectType(existingTemplate.getObjectType());
        } else
        {
            template.setTemplateId(Integer
                    .toString(templates.keySet().stream().mapToInt(id -> Integer.parseInt(id)).reduce(0, (a, b) -> Integer.max(a, b)) + 1));
            template.setTemplateVersion("1.0");
            template.setTemplateVersionActive(true);
            template.setActivated(true);
            template.setTemplateFilename(templateFilename);
            template.setDocumentType(generateDocumentTypeFromFilename(templateFilename));
            template.setDateFormatString("MM/dd/yyyy");
            template.setNumberFormatString("###,###,###");
        }

        return Optional.of(template);
        /*
         * CorrespondenceTemplate template = new CorrespondenceTemplate();
         * 
         * if (null == templateId || templateId.equals("0") || templateId.equals("undefined")) {
         * template.setTemplateId(Integer .toString(templates.keySet().stream().mapToInt(id ->
         * Integer.parseInt(id)).reduce(0, (a, b) -> Integer.max(a, b)) + 1)); template.setTemplateVersion("1.0");
         * template.setTemplateVersionActive(true); template.setActivated(true);
         * template.setTemplateFilename(templateFilename);
         * template.setDocumentType(generateDocumentTypeFromFilename(templateFilename));
         * template.setDateFormatString("MM/dd/yyyy"); template.setNumberFormatString("###,###,###"); } else {
         * CorrespondenceTemplate existingTemplate = findActiveVersionTemplate(templateId);
         * 
         * template.setTemplateId(existingTemplate.getTemplateId());
         * template.setTemplateVersion(Double.toString(templates.get(templateId).keySet().stream() .mapToDouble(key ->
         * Double.parseDouble(key)).reduce(0, (a, b) -> Double.max(a, b) + 1)));
         * template.setTemplateVersionActive(true); template.setActivated(true);
         * template.setTemplateFilename(templateFilename);
         * template.setDateFormatString(existingTemplate.getDateFormatString());
         * template.setNumberFormatString(existingTemplate.getNumberFormatString());
         * template.setDocumentType(existingTemplate.getDocumentType());
         * template.setObjectType(existingTemplate.getObjectType());
         * 
         * } return template;
         */
    }

    /**
     * @param templateId
     * @return
     */
    Optional<CorrespondenceTemplate> getActiveTemplateById(String templateId)
    {
        return findActiveVersionTemplate(templateId);
    }

    /**
     * @param templateId
     * @return
     */
    List<CorrespondenceTemplate> getTemplateVersionsById(String templateId)
    {
        return new ArrayList<>(templates.get(templateId).values());
    }

    /**
     * @param templateId
     * @param templateVersion
     * @return
     */
    Optional<CorrespondenceTemplate> getTemplateByIdAndVersion(String templateId, String templateVersion)
    {
        return findTemplateByIdAndVersion(templateId, templateVersion);
    }

    private Optional<CorrespondenceTemplate> findTemplateByIdAndVersion(String templateId, String templateVersion)
    {
        return Optional.ofNullable(templates.get(templateId).get(templateVersion));
    }

    private Optional<CorrespondenceTemplate> findActiveVersionTemplate(String templateId)
    {
        if (templates.get(templateId) != null)
        {
            return templates.get(templateId).values().stream().filter(ct -> ct.isTemplateVersionActive()).findFirst();

        }
        return Optional.empty();

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
            matches.add((matcher.group().trim()));
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
                .map(template -> mapConfigurationFromTemplate(template)).collect(Collectors.toList());

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String configurationsOutput = mapper.writeValueAsString(configurations);

        File file = correspondenceTemplatesConfiguration.getFile();
        FileUtils.writeStringToFile(file, configurationsOutput);

    }

    public Map<String, CorrespondenceTemplate> getVersionToTemplateMap(CorrespondenceTemplate correspondenceTemplate)
    {
        Map<String, CorrespondenceTemplate> versionToTempateMap = new HashMap<String, CorrespondenceTemplate>();
        versionToTempateMap.put(correspondenceTemplate.getTemplateVersion(), correspondenceTemplate);
        return versionToTempateMap;
    }

}
