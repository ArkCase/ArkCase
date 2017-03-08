package com.armedia.acm.correspondence.model;

import java.util.Date;

/**
 * This POJO stores the parameters to generate a new Word document based on a Word template.
 */
public class CorrespondenceTemplate
{

    /**
     * The template id of the generated correspondence
     */
    private String templateId;

    /**
     * The template version of the generated correspondence
     */
    private String templateVersion;

    /**
     * The active version flag of the generated correspondence
     */
    private boolean templateVersionActive;
    /**
     * The label for the template, by default should be set to the value of <code>documentType</code>.
     */
    private String label;

    /**
     * The document type of the generated correspondence, e.g. &quot;Report of Investigation&quot;
     */
    private String documentType;

    /**
     * Must match a filename in the $HOME/.acm/correspondenceTemplates folder. This file must be a Microsoft Word .docx
     * file, in the Office XML file format first introduced by Word 2007.
     */
    private String templateFilename;

    /**
     * The type of the template for generated correspondence, CASE_FILE, COMPLAINT etc.
     */
    private String objectType;

    /**
     * The date format that should be applied to any date. All date columns will be displayed in the generated
     * correspondence in this format. Must be a valid date format according to Java DateFormat rules.
     */
    private String dateFormatString;

    /**
     * The number format that should be applied to any number. All number columns will be displayed in the generated
     * correspondence in this format. Must be a valid number format according to Java NumberFormat rules.
     */
    private String numberFormatString;

    private boolean activated;

    private String modifier;

    private Date modified;

    /**
     * @return the templateId
     */
    public String getTemplateId()
    {
        return templateId;
    }

    /**
     * @param templateId
     *            the templateId to set
     */
    public void setTemplateId(String templateId)
    {
        this.templateId = templateId;
    }

    /**
     * @return the templateVersion
     */
    public String getTemplateVersion()
    {
        return templateVersion;
    }

    /**
     * @param templateVersion
     *            the templateVersion to set
     */
    public void setTemplateVersion(String templateVersion)
    {
        this.templateVersion = templateVersion;
    }

    /**
     * @return the templateVersionActive
     */
    public boolean isTemplateVersionActive()
    {
        return templateVersionActive;
    }

    /**
     * @param templateVersionActive
     *            the templateVersionActive to set
     */
    public void setTemplateVersionActive(boolean templateVersionActive)
    {
        this.templateVersionActive = templateVersionActive;
    }

    /**
     * @return the label
     */
    public String getLabel()
    {
        return label;
    }

    /**
     * @param label
     *            the label to set
     */
    public void setLabel(String label)
    {
        this.label = label;
    }

    /**
     * @return the documentType
     */
    public String getDocumentType()
    {
        return documentType;
    }

    /**
     * @param documentType
     *            the documentType to set
     */
    public void setDocumentType(String documentType)
    {
        this.documentType = documentType;
    }

    /**
     * @return the templateFilename
     */
    public String getTemplateFilename()
    {
        return templateFilename;
    }

    /**
     * @param templateFilename
     *            the templateFilename to set
     */
    public void setTemplateFilename(String templateFilename)
    {
        this.templateFilename = templateFilename;
    }

    /**
     * @return the objectType
     */
    public String getObjectType()
    {
        return objectType;
    }

    /**
     * @param objectType
     *            the objectType to set
     */
    public void setObjectType(String objectType)
    {
        this.objectType = objectType;
    }

    /**
     * @return the dateFormatString
     */
    public String getDateFormatString()
    {
        return dateFormatString;
    }

    /**
     * @param dateFormatString
     *            the dateFormatString to set
     */
    public void setDateFormatString(String dateFormatString)
    {
        this.dateFormatString = dateFormatString;
    }

    /**
     * @return the numberFormatString
     */
    public String getNumberFormatString()
    {
        return numberFormatString;
    }

    /**
     * @param numberFormatString
     *            the numberFormatString to set
     */
    public void setNumberFormatString(String numberFormatString)
    {
        this.numberFormatString = numberFormatString;
    }

    /**
     * @return the activated
     */
    public boolean isActivated()
    {
        return activated;
    }

    /**
     * @param activated
     *            the activated to set
     */
    public void setActivated(boolean activated)
    {
        this.activated = activated;
    }

    /**
     * @return the modifier
     */
    public String getModifier()
    {
        return modifier;
    }

    /**
     * @param modifier
     *            the modifier to set
     */
    public void setModifier(String modifier)
    {
        this.modifier = modifier;
    }

    /**
     * @return the modified
     */
    public Date getModified()
    {
        return modified;
    }

    /**
     * @param modified
     *            the modified to set
     */
    public void setModified(Date modified)
    {
        this.modified = modified;
    }

}
