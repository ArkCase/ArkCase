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

import static com.armedia.acm.correspondence.service.CorrespondenceMapper.mapConfigurationFromMergeField;
import static com.armedia.acm.correspondence.service.CorrespondenceMapper.mapConfigurationFromMergeFieldVersion;
import static com.armedia.acm.correspondence.service.CorrespondenceMapper.mapMergeFieldFromConfiguration;
import static com.armedia.acm.correspondence.service.CorrespondenceMapper.mapMergeFieldVersionFromConfiguration;

import com.armedia.acm.core.exceptions.CorrespondenceMergeFieldVersionException;
import com.armedia.acm.correspondence.model.CorrespondenceMergeField;
import com.armedia.acm.correspondence.model.CorrespondenceMergeFieldConfiguration;
import com.armedia.acm.correspondence.model.CorrespondenceMergeFieldVersion;
import com.armedia.acm.correspondence.model.CorrespondenceMergeFieldVersionConfiguration;
import com.armedia.acm.correspondence.model.CorrespondenceQuery;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.spring.SpringContextHolder;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author sasko.tanaskoski
 *
 */
public class CorrespondenceMergeFieldManager implements ApplicationListener<ContextRefreshedEvent>
{

    private static final String DEFAULT_MERGE_FIELD_VERSION = "1.0";
    private static final String DEFAULT_MERGE_FIELD_MODIFIER = "System User";
    private Resource correspondenceMergeFieldsVersionConfiguration;
    private Resource correspondenceMergeFieldsConfiguration;
    private SpringContextHolder springContextHolder;
    private ObjectConverter objectConverter;
    private List<CorrespondenceMergeField> mergeFields = new ArrayList<>();
    private List<CorrespondenceMergeFieldVersion> mergeFieldsVersions = new ArrayList<>();

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event)
    {
        try
        {
            File file = correspondenceMergeFieldsVersionConfiguration.getFile();
            if (!file.exists())
            {
                file.createNewFile();
            }
            String resource = FileUtils.readFileToString(file);
            if (resource.isEmpty())
            {
                resource = "[]";
            }

            List<CorrespondenceMergeFieldVersionConfiguration> mergeFieldsVersionConfigurations = getObjectConverter().getJsonUnmarshaller()
                    .unmarshallCollection(resource, List.class, CorrespondenceMergeFieldVersionConfiguration.class);

            mergeFieldsVersions = new ArrayList<>(mergeFieldsVersionConfigurations.stream()
                    .map(configuration -> mapMergeFieldVersionFromConfiguration(configuration)).collect(Collectors.toList()));

            if (mergeFieldsVersions.isEmpty())
            {
                createDefaultMergeFieldVersionRecords();
            }

            file = correspondenceMergeFieldsConfiguration.getFile();
            if (!file.exists())
            {
                file.createNewFile();
            }
            resource = FileUtils.readFileToString(file);
            if (resource.isEmpty())
            {
                resource = "[]";
            }

            List<CorrespondenceMergeFieldConfiguration> mergeFieldsConfigurations = getObjectConverter().getJsonUnmarshaller()
                    .unmarshallCollection(resource, List.class, CorrespondenceMergeFieldConfiguration.class);

            mergeFields = new ArrayList<>(mergeFieldsConfigurations.stream()
                    .map(configuration -> mapMergeFieldFromConfiguration(configuration)).collect(Collectors.toList()));

            if (mergeFields.isEmpty())
            {
                createDefaultMergeFieldRescords();
            }
        }
        catch (IOException ioe)
        {
            throw new IllegalStateException(ioe);
        }
    }

    /**
     * @return mergeFields
     */
    public List<CorrespondenceMergeField> getMergeFields()
    {
        return mergeFields;
    }

    /**
     * @return mergeFieldVersions
     */
    public List<CorrespondenceMergeFieldVersion> getMergeFieldVersions()
    {
        return mergeFieldsVersions;
    }

    /**
     * @param objectType
     * @return mergeFieldVersions
     */
    public List<CorrespondenceMergeFieldVersion> getMergeFieldVersionsByType(String objectType)
    {
        return mergeFieldsVersions.stream().filter(version -> version.getMergingType().equals(objectType)).collect(Collectors.toList());
    }

    /**
     * @param objectType
     * @return mergeFields
     */
    public List<CorrespondenceMergeField> getActiveVersionMergeFieldsByType(String objectType)
            throws CorrespondenceMergeFieldVersionException
    {
        List<CorrespondenceMergeField> mergeFieldsInActiveVersion = new ArrayList<>();
        for (CorrespondenceMergeField mergeField : mergeFields)
        {
            if (mergeField.getFieldVersion().equals(getActiveMergingVersionByType(objectType).getMergingVersion()))
            {
                mergeFieldsInActiveVersion.add(mergeField);
            }
        }
        return mergeFieldsInActiveVersion.stream()
                .filter(mergeField -> mergeField.getFieldType().equals(objectType)).collect(Collectors.toList());
    }

    /**
     * @param objectType
     * @return mergeFieldVersion
     */
    public CorrespondenceMergeFieldVersion getActiveMergingVersionByType(String objectType) throws CorrespondenceMergeFieldVersionException
    {
        Optional<CorrespondenceMergeFieldVersion> correspondenceMergeFieldVersion = mergeFieldsVersions.stream()
                .filter(mergeFieldVersion -> mergeFieldVersion.isMergingActiveVersion())
                .filter(mergeFieldVersion -> mergeFieldVersion.getMergingType().equals(objectType)).findFirst();
        if (correspondenceMergeFieldVersion.isPresent())
            return correspondenceMergeFieldVersion.get();
        else
            throw new CorrespondenceMergeFieldVersionException("CorrespondenceMergeFieldVersionNotFoundException");
    }

    /**
     * @param objectType
     * @return version
     */
    private Double getNewVersionByType(String objectType)
    {
        return mergeFieldsVersions.stream().filter(mergeFieldVersion -> mergeFieldVersion.getMergingType().equals(objectType))
                .map(mergeFieldVersion -> mergeFieldVersion.getMergingVersion()).mapToDouble(version -> Double.parseDouble(version))
                .reduce(0, (a, b) -> Double.max(a, b) + 1);
    }

    /**
     * @param newMergeFields
     * @param auth
     * @return mergeFields
     * @throws IOException
     */
    public List<CorrespondenceMergeField> saveMergeFieldsData(List<CorrespondenceMergeField> newMergeFields, Authentication auth)
            throws IOException, CorrespondenceMergeFieldVersionException
    {
        List<CorrespondenceMergeField> result = new ArrayList<>();
        String objectType = newMergeFields.get(0).getFieldType();
        String newVersion = getNewVersionByType(objectType).toString();

        getActiveMergingVersionByType(objectType).setMergingActiveVersion(false);

        CorrespondenceMergeFieldVersion newMergeFieldVersion = new CorrespondenceMergeFieldVersion();
        newMergeFieldVersion.setMergingVersion(newVersion);
        newMergeFieldVersion.setMergingActiveVersion(true);
        newMergeFieldVersion.setMergingType(objectType);
        newMergeFieldVersion.setModifier(auth.getName());
        newMergeFieldVersion.setModified(new Date());
        mergeFieldsVersions.add(newMergeFieldVersion);
        updateMergeFieldVersionConfiguration(mergeFieldsVersions);

        newMergeFields.forEach(mergeField -> {
            CorrespondenceMergeField newMergeField = new CorrespondenceMergeField();
            newMergeField.setFieldVersion(newVersion);
            newMergeField.setFieldId(mergeField.getFieldId());
            newMergeField.setFieldDescription(mergeField.getFieldDescription());
            newMergeField.setFieldType(mergeField.getFieldType());
            newMergeField.setFieldValue(mergeField.getFieldValue());
            result.add(newMergeField);
            mergeFields.add(newMergeField);
        });
        updateMergeFieldConfiguration(mergeFields);

        return result;
    }

    /**
     * @param mergeFieldVersion
     * @param auth
     * @return mergeFieldVersion
     * @throws IOException
     */
    public CorrespondenceMergeFieldVersion setActiveMergingVersion(CorrespondenceMergeFieldVersion mergeFieldVersion, Authentication auth)
            throws IOException, CorrespondenceMergeFieldVersionException
    {
        String objectType = mergeFieldVersion.getMergingType();
        getActiveMergingVersionByType(objectType).setMergingActiveVersion(false);

        Optional<CorrespondenceMergeFieldVersion> optionalCorrespondenceMergeFieldVersion = mergeFieldsVersions.stream()
                .filter(version -> version.getMergingVersion().equals(mergeFieldVersion.getMergingVersion()))
                .filter(version -> version.getMergingType().equals(objectType)).findFirst();
        CorrespondenceMergeFieldVersion activeVersion;
        if (!optionalCorrespondenceMergeFieldVersion.isPresent())
        {
            throw new CorrespondenceMergeFieldVersionException("CorrespondenceMergeFieldVersion not found");
        }
        else
        {
            activeVersion = optionalCorrespondenceMergeFieldVersion.get();
            activeVersion.setModified(new Date());
            activeVersion.setModifier(auth.getName());
            activeVersion.setMergingActiveVersion(true);
            updateMergeFieldVersionConfiguration(mergeFieldsVersions);
            return activeVersion;
        }

    }

    /**
     * @param mergeFields
     * @throws IOException
     */
    private void updateMergeFieldConfiguration(Collection<CorrespondenceMergeField> mergeFields) throws IOException
    {
        List<CorrespondenceMergeFieldConfiguration> configurations = mergeFields.stream()
                .map(mergeField -> mapConfigurationFromMergeField(mergeField)).collect(Collectors.toList());

        String configurationsOutput = getObjectConverter().getIndentedJsonMarshaller().marshal(configurations);

        File file = correspondenceMergeFieldsConfiguration.getFile();
        FileUtils.writeStringToFile(file, configurationsOutput);

    }

    /**
     * @param mergeFieldsVersions
     * @throws IOException
     */
    private void updateMergeFieldVersionConfiguration(Collection<CorrespondenceMergeFieldVersion> mergeFieldsVersions) throws IOException
    {
        List<CorrespondenceMergeFieldVersionConfiguration> configurations = mergeFieldsVersions.stream()
                .map(mergeFieldVersion -> mapConfigurationFromMergeFieldVersion(mergeFieldVersion)).collect(Collectors.toList());

        String configurationsOutput = getObjectConverter().getIndentedJsonMarshaller().marshal(configurations);

        File file = correspondenceMergeFieldsVersionConfiguration.getFile();
        FileUtils.writeStringToFile(file, configurationsOutput);

    }

    private void createDefaultMergeFieldRescords() throws IOException
    {
        Map<String, CorrespondenceQuery> correspondenceQueryBeansMap = springContextHolder.getAllBeansOfType(CorrespondenceQuery.class);
        correspondenceQueryBeansMap.values().stream().forEach(cq -> {
            if (!cq.getFieldNames().isEmpty())
            {
                for (String fieldName : cq.getFieldNames())
                {
                    CorrespondenceMergeField defaultMergeField = new CorrespondenceMergeField();
                    defaultMergeField.setFieldVersion("1.0");
                    defaultMergeField.setFieldId(fieldName);
                    defaultMergeField.setFieldDescription(
                            StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(fieldName), ' ') + " Place Holder");
                    defaultMergeField.setFieldType(cq.getType().name());
                    defaultMergeField.setFieldValue(fieldName);
                    mergeFields.add(defaultMergeField);
                }
            }
        });
        updateMergeFieldConfiguration(mergeFields);
    }

    private void createDefaultMergeFieldVersionRecords() throws IOException
    {
        Map<String, CorrespondenceQuery> correspondenceQueryBeansMap = springContextHolder.getAllBeansOfType(CorrespondenceQuery.class);
        correspondenceQueryBeansMap.values().stream().forEach(cq -> {
            if (cq.getType() != null)
            {
                CorrespondenceMergeFieldVersion defaultMergeFieldVersion = new CorrespondenceMergeFieldVersion();
                defaultMergeFieldVersion.setMergingActiveVersion(true);
                defaultMergeFieldVersion.setMergingVersion(DEFAULT_MERGE_FIELD_VERSION);
                defaultMergeFieldVersion.setMergingType(cq.getType().name());
                defaultMergeFieldVersion.setModified(new Date());
                defaultMergeFieldVersion.setModifier(DEFAULT_MERGE_FIELD_MODIFIER);
                mergeFieldsVersions.add(defaultMergeFieldVersion);
            }
        });
        updateMergeFieldVersionConfiguration(mergeFieldsVersions);
    }

    /**
     * @param correspondenceMergeFieldsVersionConfiguration
     *            the correspondenceMergeFieldsVersionConfiguration to set
     */
    public void setCorrespondenceMergeFieldsVersionConfiguration(Resource correspondenceMergeFieldsVersionConfiguration)
    {
        this.correspondenceMergeFieldsVersionConfiguration = correspondenceMergeFieldsVersionConfiguration;
    }

    /**
     * @param correspondenceMergeFieldsConfiguration
     *            the correspondenceMergeFieldsConfiguration to set
     */
    public void setCorrespondenceMergeFieldsConfiguration(Resource correspondenceMergeFieldsConfiguration)
    {
        this.correspondenceMergeFieldsConfiguration = correspondenceMergeFieldsConfiguration;
    }

    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }

    /**
     * @param springContextHolder
     *            the springContextHolder to set
     */
    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
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
