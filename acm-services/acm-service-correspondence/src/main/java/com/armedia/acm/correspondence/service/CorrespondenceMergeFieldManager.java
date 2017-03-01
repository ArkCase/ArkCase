package com.armedia.acm.correspondence.service;

import static com.armedia.acm.correspondence.service.CorrespondenceMapper.mapConfigurationFromMergeField;
import static com.armedia.acm.correspondence.service.CorrespondenceMapper.mapConfigurationFromMergeFieldVersion;
import static com.armedia.acm.correspondence.service.CorrespondenceMapper.mapMergeFieldFromConfiguration;
import static com.armedia.acm.correspondence.service.CorrespondenceMapper.mapMergeFieldVersionFromConfiguration;

import com.armedia.acm.correspondence.model.CorrespondenceMergeField;
import com.armedia.acm.correspondence.model.CorrespondenceMergeFieldConfiguration;
import com.armedia.acm.correspondence.model.CorrespondenceMergeFieldVersion;
import com.armedia.acm.correspondence.model.CorrespondenceMergeFieldVersionConfiguration;
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
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sasko.tanaskoski
 *
 */
public class CorrespondenceMergeFieldManager implements ApplicationListener<ContextRefreshedEvent>
{

    private Resource correspondenceMergeFieldsVersionConfiguration;
    private Resource correspondenceMergeFieldsConfiguration;

    private List<CorrespondenceMergeField> mergeFields = new ArrayList<>();
    private List<CorrespondenceMergeFieldVersion> mergeFieldsVersions = new ArrayList<>();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event)
    {
        try
        {
            File file = correspondenceMergeFieldsVersionConfiguration.getFile();
            String resource = FileUtils.readFileToString(file);

            ObjectMapper mapper = new ObjectMapper();

            List<CorrespondenceMergeFieldVersionConfiguration> mergeFieldsVersionConfigurations = mapper.readValue(resource,
                    new TypeReference<List<CorrespondenceMergeFieldVersionConfiguration>>()
                    {
                    });

            mergeFieldsVersions = new ArrayList<CorrespondenceMergeFieldVersion>(mergeFieldsVersionConfigurations.stream()
                    .map(configuration -> mapMergeFieldVersionFromConfiguration(configuration)).collect(Collectors.toList()));

            file = correspondenceMergeFieldsConfiguration.getFile();
            resource = FileUtils.readFileToString(file);

            mapper = new ObjectMapper();

            List<CorrespondenceMergeFieldConfiguration> mergeFieldsConfigurations = mapper.readValue(resource,
                    new TypeReference<List<CorrespondenceMergeFieldConfiguration>>()
                    {
                    });

            mergeFields = new ArrayList<CorrespondenceMergeField>(mergeFieldsConfigurations.stream()
                    .map(configuration -> mapMergeFieldFromConfiguration(configuration)).collect(Collectors.toList()));
        } catch (IOException ioe)
        {
            throw new IllegalStateException(ioe);
        }
    }

    public List<CorrespondenceMergeField> getMergeFields()
    {
        return mergeFields;
    }

    public List<CorrespondenceMergeFieldVersion> getMergeFieldVersions()
    {
        return mergeFieldsVersions;
    }

    public List<CorrespondenceMergeFieldVersion> getMergeFieldVersionsByType(String objectType)
    {
        return mergeFieldsVersions.stream().filter(version -> version.getMergingType().equals(objectType)).collect(Collectors.toList());
    }

    public List<CorrespondenceMergeField> getActiveVersionMergeFieldsByType(String objectType)
    {
        return mergeFields.stream()
                .filter(mergeField -> mergeField.getFieldVersion().equals(getActiveMergingVersionByType(objectType).getMergingVersion()))
                .filter(mergeField -> mergeField.getFieldType().equals(objectType)).collect(Collectors.toList());
    }

    public CorrespondenceMergeFieldVersion getActiveMergingVersionByType(String objectType)
    {
        return mergeFieldsVersions.stream().filter(mergeFieldVersion -> mergeFieldVersion.isMergingActiveVersion())
                .filter(mergeFieldVersion -> mergeFieldVersion.getMergingType().equals(objectType)).findFirst().get();
    }

    /**
     * @param mergeFields
     * @throws IOException
     */
    private void updateMergeFieldConfiguration(Collection<CorrespondenceMergeField> mergeFields) throws IOException
    {
        List<CorrespondenceMergeFieldConfiguration> configurations = mergeFields.stream()
                .map(mergeField -> mapConfigurationFromMergeField(mergeField)).collect(Collectors.toList());

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String configurationsOutput = mapper.writeValueAsString(configurations);

        File file = correspondenceMergeFieldsConfiguration.getFile();
        FileUtils.writeStringToFile(file, configurationsOutput);

    }

    /**
     * @param mergeFieldsVersion
     * @throws IOException
     */
    private void updateMergeFieldVersionConfiguration(Collection<CorrespondenceMergeFieldVersion> mergeFieldsVersions) throws IOException
    {
        List<CorrespondenceMergeFieldVersionConfiguration> configurations = mergeFieldsVersions.stream()
                .map(mergeFieldVersion -> mapConfigurationFromMergeFieldVersion(mergeFieldVersion)).collect(Collectors.toList());

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String configurationsOutput = mapper.writeValueAsString(configurations);

        File file = correspondenceMergeFieldsVersionConfiguration.getFile();
        FileUtils.writeStringToFile(file, configurationsOutput);

    }

    public void setCorrespondenceMergeFieldsVersionConfiguration(Resource correspondenceMergeFieldsVersionConfiguration)
    {
        this.correspondenceMergeFieldsVersionConfiguration = correspondenceMergeFieldsVersionConfiguration;
    }

    public void setCorrespondenceMergeFieldsConfiguration(Resource correspondenceMergeFieldsConfiguration)
    {
        this.correspondenceMergeFieldsConfiguration = correspondenceMergeFieldsConfiguration;
    }

}
