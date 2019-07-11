package com.armedia.acm.correspondence.service;

/*-
 * #%L
 * ACM Service: Correspondence Library
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

import com.armedia.acm.correspondence.model.CorrespondenceTemplate;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.spring.SpringContextHolder;
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

import static com.armedia.acm.correspondence.service.CorrespondenceMapper.mapTemplateFromConfiguration;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jan 26, 2017
 */
public class CorrespondenceTemplateManager implements ApplicationListener<ContextRefreshedEvent>
{

    private SpringContextHolder springContextHolder;

    private Resource correspondenceTemplatesConfiguration;

    private Map<String, Map<String, CorrespondenceTemplate>> templates = new ConcurrentHashMap<>();

    private Pattern camelCase = Pattern.compile("[A-Za-z].*?(?=([A-Z]|\\.))");

    private ObjectConverter objectConverter;

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
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event)
    {
        try
        {
            File file = correspondenceTemplatesConfiguration.getFile();
            if (!file.exists())
            {
                file.createNewFile();
            }
            String resource = FileUtils.readFileToString(file);
            if (resource.isEmpty())
            {
                resource = "[]";
            }

            List<CorrespondenceTemplate> templateConfigurations = getObjectConverter().getJsonUnmarshaller()
                    .unmarshallCollection(resource, List.class, CorrespondenceTemplate.class);

            templateConfigurations.stream().forEach(configuration -> {
                CorrespondenceTemplate template = mapTemplateFromConfiguration(configuration);
                if (templates.containsKey(template.getTemplateId()))
                {
                    templates.get(template.getTemplateId()).put(template.getTemplateVersion(), template);
                }
                else
                {
                    templates.put(template.getTemplateId(), getVersionToTemplateMap(template));
                }
            });
        }
        catch (IOException ioe)
        {
            throw new IllegalStateException(ioe);
        }

    }

    /**
     * @return the templates
     */
    List<CorrespondenceTemplate> getActiveVersionTemplates()
    {
        List<CorrespondenceTemplate> list = new ArrayList<>();

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
        List<CorrespondenceTemplate> list = new ArrayList<>();

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
        List<CorrespondenceTemplate> list = new ArrayList<>();

        templates.values().stream().forEach(versionMap -> {
            list.addAll(versionMap.values());
        });

        return list;
    }

    /**
     * @param template
     * @return
     * @throws IOException
     */
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
        }
        else
        {
            templates.put(template.getTemplateId(), getVersionToTemplateMap(template));
        }

        updateConfiguration(getAllTemplates());
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
            template.setTemplateModelProvider(existingTemplate.getTemplateModelProvider());
        }
        else
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

    /**
     * @param templateId
     * @param templateVersion
     * @return
     */
    private Optional<CorrespondenceTemplate> findTemplateByIdAndVersion(String templateId, String templateVersion)
    {
        return Optional.ofNullable(templates.get(templateId).get(templateVersion));
    }

    /**
     * @param templateId
     * @return
     */
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
        List<CorrespondenceTemplate> configurations = templates.stream()
                .map(template -> mapTemplateFromConfiguration(template)).collect(Collectors.toList());

        String configurationsOutput = getObjectConverter().getIndentedJsonMarshaller().marshal(configurations);

        File file = correspondenceTemplatesConfiguration.getFile();
        FileUtils.writeStringToFile(file, configurationsOutput);

    }

    private Map<String, CorrespondenceTemplate> getVersionToTemplateMap(CorrespondenceTemplate correspondenceTemplate)
    {
        Map<String, CorrespondenceTemplate> versionToTempateMap = new HashMap<>();
        versionToTempateMap.put(correspondenceTemplate.getTemplateVersion(), correspondenceTemplate);
        return versionToTempateMap;
    }

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }

}
