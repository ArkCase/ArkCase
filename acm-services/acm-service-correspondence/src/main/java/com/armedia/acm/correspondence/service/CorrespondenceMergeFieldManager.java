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

import com.armedia.acm.correspondence.model.CorrespondenceMergeField;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.spring.SpringContextHolder;
import org.apache.commons.io.FileUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.armedia.acm.correspondence.service.CorrespondenceMapper.mapMergeFieldFromConfiguration;

/**
 * @author sasko.tanaskoski
 *
 */
public class CorrespondenceMergeFieldManager implements ApplicationListener<ContextRefreshedEvent>
{
    private Resource correspondenceMergeFieldsConfiguration;
    private SpringContextHolder springContextHolder;
    private ObjectConverter objectConverter;
    private List<CorrespondenceMergeField> mergeFields = new ArrayList<>();

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event)
    {
        try
        {
            File file = correspondenceMergeFieldsConfiguration.getFile();
            if (!file.exists())
            {
                file.createNewFile();
            }
            String resource = FileUtils.readFileToString(file);
            if (resource.isEmpty())
            {
                resource = "[]";
            }

            List<CorrespondenceMergeField> mergeFieldsConfigurations = getObjectConverter().getJsonUnmarshaller()
                    .unmarshallCollection(resource, List.class, CorrespondenceMergeField.class);

            mergeFields = new ArrayList<>(mergeFieldsConfigurations.stream()
                    .map(configuration -> mapMergeFieldFromConfiguration(configuration)).collect(Collectors.toList()));

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
     * @param fieldObjectType
     * @return mergeFields
     */
    public List<CorrespondenceMergeField> getMergeFieldsByType(String fieldObjectType)
    {
        List<CorrespondenceMergeField> mergeFieldsByType = new ArrayList<>();
        for (CorrespondenceMergeField mergeField : mergeFields)
        {
            mergeFieldsByType.add(mergeField);
        }
        return mergeFieldsByType.stream()
                .filter(mergeField -> mergeField.getFieldObjectType().equals(fieldObjectType)).collect(Collectors.toList());
    }


    /**
     * @param newMergeFields
     * @param auth
     * @return mergeFields
     * @throws IOException
     */
    public List<CorrespondenceMergeField> saveMergeFieldsData(List<CorrespondenceMergeField> newMergeFields, Authentication auth)
            throws IOException
    {
        List<CorrespondenceMergeField> result = new ArrayList<>();

        for (int numNewMergeFields=0; numNewMergeFields<newMergeFields.size(); numNewMergeFields++)
        {
            for (int numExistingMergeField=0; numExistingMergeField<mergeFields.size(); numExistingMergeField++)
            {
                CorrespondenceMergeField newMergeField = newMergeFields.get(numNewMergeFields);
                CorrespondenceMergeField existingMergeField = mergeFields.get(numExistingMergeField);

                //Adding new merge field
                if (!newMergeField.getFieldId().equalsIgnoreCase(existingMergeField.getFieldId()) &&
                        !newMergeField.getFieldId().equalsIgnoreCase(mergeFields.get(mergeFields.size()-1).getFieldId()) &&
                        newMergeField.getFieldObjectType().equalsIgnoreCase(existingMergeField.getFieldObjectType()))
                {
                    newMergeFields.forEach(mergeField -> {
                        CorrespondenceMergeField addMergeField = new CorrespondenceMergeField();
                        addMergeField.setFieldId(mergeField.getFieldId());
                        addMergeField.setFieldDescription(mergeField.getFieldDescription());
                        addMergeField.setFieldObjectType(mergeField.getFieldObjectType());
                        addMergeField.setFieldValue(mergeField.getFieldValue());
                        result.add(addMergeField);
                        mergeFields.add(addMergeField);
                    });
                }
            }
        }

        updateMergeFieldConfiguration(mergeFields);

        return result;
    }
    /**
     * @param mergeFields
     * @throws IOException
     */
    private void updateMergeFieldConfiguration(Collection<CorrespondenceMergeField> mergeFields) throws IOException
    {
        List<CorrespondenceMergeField> configurations = mergeFields.stream()
                .map(mergeField -> mapMergeFieldFromConfiguration(mergeField)).collect(Collectors.toList());

        String configurationsOutput = getObjectConverter().getIndentedJsonMarshaller().marshal(configurations);

        File file = correspondenceMergeFieldsConfiguration.getFile();
        FileUtils.writeStringToFile(file, configurationsOutput);

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
