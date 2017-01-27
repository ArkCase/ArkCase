package com.armedia.acm.correspondence.model;

import java.util.Map;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jan 26, 2017
 *
 */
public class CorrespondenceTemplateConfiguration
{

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
     * Text of the JPA query to retrieve the data needed by the template. This query must return a column for each
     * template substitution variable. It must be a valid JPA query.
     */
    private String correspondenceQueryBeanId;

    /**
     * Each entry must exactly match a field names form the query (key in this map) and substitution variable in the
     * Word template (value in this map);
     * <p/>
     * Suppose the Word template includes the substitution variable text &quot;${Create Date}&quot; and &quot;${Subject
     * Name}&quot;. In this case, the jpaQuery should return both the Create Date and the Subject Name. If &quot;Subject
     * Name&quot; is the first column returned, and &quot;Create Date&quot; is the second column, then this list
     * <strong>must</strong> include ${Subject Name} and ${Create Date} in that order.
     *
     */
    private Map<String, String> templateSubstitutionVariables;

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

    /**
     * @return the documentType
     */
    public String getDocumentType()
    {
        return documentType;
    }

    /**
     * @param documentType the documentType to set
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
     * @param templateFilename the templateFilename to set
     */
    public void setTemplateFilename(String templateFilename)
    {
        this.templateFilename = templateFilename;
    }

    /**
     * @return the correspondenceQueryBeanId
     */
    public String getCorrespondenceQueryBeanId()
    {
        return correspondenceQueryBeanId;
    }

    /**
     * @param correspondenceQueryBeanId the correspondenceQueryBeanId to set
     */
    public void setCorrespondenceQueryBeanId(String correspondenceQueryBeanId)
    {
        this.correspondenceQueryBeanId = correspondenceQueryBeanId;
    }

    /**
     * @return the templateSubstitutionVariables
     */
    public Map<String, String> getTemplateSubstitutionVariables()
    {
        return templateSubstitutionVariables;
    }

    /**
     * @param templateSubstitutionVariables the templateSubstitutionVariables to set
     */
    public void setTemplateSubstitutionVariables(Map<String, String> templateSubstitutionVariables)
    {
        this.templateSubstitutionVariables = templateSubstitutionVariables;
    }

    /**
     * @return the dateFormatString
     */
    public String getDateFormatString()
    {
        return dateFormatString;
    }

    /**
     * @param dateFormatString the dateFormatString to set
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
     * @param numberFormatString the numberFormatString to set
     */
    public void setNumberFormatString(String numberFormatString)
    {
        this.numberFormatString = numberFormatString;
    }

}
