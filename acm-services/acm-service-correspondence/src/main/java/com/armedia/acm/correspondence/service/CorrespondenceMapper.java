package com.armedia.acm.correspondence.service;

import com.armedia.acm.correspondence.model.CorrespondenceMergeField;
import com.armedia.acm.correspondence.model.CorrespondenceMergeFieldConfiguration;
import com.armedia.acm.correspondence.model.CorrespondenceMergeFieldVersion;
import com.armedia.acm.correspondence.model.CorrespondenceMergeFieldVersionConfiguration;
import com.armedia.acm.correspondence.model.CorrespondenceTemplate;
import com.armedia.acm.correspondence.model.CorrespondenceTemplateConfiguration;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jan 30, 2017
 *
 */
public class CorrespondenceMapper
{

    public static CorrespondenceTemplate mapTemplateFromConfiguration(CorrespondenceTemplateConfiguration configuration)
    {
        CorrespondenceTemplate template = new CorrespondenceTemplate();

        template.setTemplateId(configuration.getTemplateId());
        template.setTemplateVersion(configuration.getTemplateVersion());
        template.setTemplateVersionActive(configuration.isTemplateVersionActive());
        template.setLabel(configuration.getLabel());
        template.setDocumentType(configuration.getDocumentType());
        template.setTemplateFilename(configuration.getTemplateFilename());
        template.setObjectType(configuration.getObjectType());
        template.setDateFormatString(configuration.getDateFormatString());
        template.setNumberFormatString(configuration.getNumberFormatString());
        template.setActivated(configuration.isActivated());
        template.setModifier(configuration.getModifier());
        template.setModified(configuration.getModified());

        return template;
    }

    public static CorrespondenceTemplateConfiguration mapConfigurationFromTemplate(CorrespondenceTemplate template)
    {
        CorrespondenceTemplateConfiguration configuration = new CorrespondenceTemplateConfiguration();

        configuration.setTemplateId(template.getTemplateId());
        configuration.setTemplateVersion(template.getTemplateVersion());
        configuration.setTemplateVersionActive(template.isTemplateVersionActive());
        configuration.setLabel(template.getLabel());
        configuration.setDocumentType(template.getDocumentType());
        configuration.setTemplateFilename(template.getTemplateFilename());
        configuration.setObjectType(template.getObjectType());
        configuration.setDateFormatString(template.getDateFormatString());
        configuration.setNumberFormatString(template.getNumberFormatString());
        configuration.setActivated(template.isActivated());
        configuration.setModifier(template.getModifier());
        configuration.setModified(template.getModified());

        return configuration;
    }

    /**
     * @param toUpdate
     * @param updateFrom
     */
    public static void updateTemplateState(CorrespondenceTemplate toUpdate, CorrespondenceTemplate updateFrom)
    {

        toUpdate.setTemplateId(updateFrom.getTemplateId());
        toUpdate.setTemplateVersion(updateFrom.getTemplateVersion());
        toUpdate.setTemplateVersionActive(updateFrom.isTemplateVersionActive());
        toUpdate.setLabel(updateFrom.getLabel());
        toUpdate.setDocumentType(updateFrom.getDocumentType());
        toUpdate.setTemplateFilename(updateFrom.getTemplateFilename());
        toUpdate.setObjectType(updateFrom.getObjectType());
        toUpdate.setDateFormatString(updateFrom.getDateFormatString());
        toUpdate.setNumberFormatString(updateFrom.getNumberFormatString());
        toUpdate.setActivated(updateFrom.isActivated());
        toUpdate.setModifier(updateFrom.getModifier());
        toUpdate.setModified(updateFrom.getModified());
    }

    public static CorrespondenceMergeField mapMergeFieldFromConfiguration(CorrespondenceMergeFieldConfiguration configuration)
    {
        CorrespondenceMergeField mergeField = new CorrespondenceMergeField();

        mergeField.setFieldId(configuration.getFieldId());
        mergeField.setFieldValue(configuration.getFieldValue());
        mergeField.setFieldDescription(configuration.getFieldDescription());
        mergeField.setFieldType(configuration.getFieldType());
        mergeField.setFieldVersion(configuration.getFieldVersion());

        return mergeField;
    }

    public static CorrespondenceMergeFieldConfiguration mapConfigurationFromMergeField(CorrespondenceMergeField mergeField)
    {
        CorrespondenceMergeFieldConfiguration configuration = new CorrespondenceMergeFieldConfiguration();

        configuration.setFieldId(mergeField.getFieldId());
        configuration.setFieldValue(mergeField.getFieldValue());
        configuration.setFieldDescription(mergeField.getFieldDescription());
        configuration.setFieldType(mergeField.getFieldType());
        configuration.setFieldVersion(mergeField.getFieldVersion());

        return configuration;
    }

    public static CorrespondenceMergeFieldVersion mapMergeFieldVersionFromConfiguration(
            CorrespondenceMergeFieldVersionConfiguration configuration)
    {
        CorrespondenceMergeFieldVersion mergeFieldVersion = new CorrespondenceMergeFieldVersion();

        mergeFieldVersion.setMergingVersion(configuration.getMergingVersion());
        mergeFieldVersion.setMergingActiveVersion(configuration.isMergingActiveVersion());
        mergeFieldVersion.setMergingType(configuration.getMergingType());
        mergeFieldVersion.setModified(configuration.getModified());
        mergeFieldVersion.setModifier(configuration.getModifier());

        return mergeFieldVersion;
    }

    public static CorrespondenceMergeFieldVersionConfiguration mapConfigurationFromMergeFieldVersion(
            CorrespondenceMergeFieldVersion mergeFieldVersion)
    {
        CorrespondenceMergeFieldVersionConfiguration configuration = new CorrespondenceMergeFieldVersionConfiguration();

        configuration.setMergingVersion(mergeFieldVersion.getMergingVersion());
        configuration.setMergingActiveVersion(mergeFieldVersion.isMergingActiveVersion());
        configuration.setMergingType(mergeFieldVersion.getMergingType());
        configuration.setModified(mergeFieldVersion.getModified());
        configuration.setModifier(mergeFieldVersion.getModifier());

        return configuration;
    }

}
