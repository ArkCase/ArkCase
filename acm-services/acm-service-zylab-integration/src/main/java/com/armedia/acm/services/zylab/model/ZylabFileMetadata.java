package com.armedia.acm.services.zylab.model;

/*-
 * #%L
 * ACM Service: Arkcase ZyLAB Integration
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
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

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import com.armedia.acm.data.converter.LocalDateTimeConverter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

@Entity
@Table(name = "acm_zylab_file_metadata")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "className", defaultImpl = ZylabFileMetadata.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "cm_class_name", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("com.armedia.acm.services.zylab.model.ZylabFileMetadata")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class ZylabFileMetadata
{
    @Id
    @TableGenerator(name = "acm_zylab_file_metadata_gen", table = "acm_zylab_file_metadata_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_zylab_file_metadata", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_zylab_file_metadata_gen")
    @Column(name = "cm_zylab_metadata_id")
    private Long id;

    @Column(name = "cm_class_name")
    private String className = this.getClass().getName();

    @Column(name = "cm_matter_id")
    private Long matterId;

    @Column(name = "cm_production_key")
    private String productionKey;

    @Column(name = "cm_zylab_id")
    private Long zylabId;

    @Column(name = "cm_name")
    private String name;

    @Column(name = "cm_produced_pages")
    private Integer producedPages;

    @Column(name = "cm_production_create_date")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime productionCreateDate;

    @Column(name = "cm_contains_redaction")
    private Boolean containsRedaction;

    @Column(name = "cm_redaction_code_1")
    private String redactionCode1;

    @Column(name = "cm_redaction_code_2")
    private String redactionCode2;

    @Column(name = "cm_redaction_justification")
    private String redactionJustification;

    @Column(name = "cm_custodian")
    private String custodian;

    @Column(name = "cm_doc_name")
    private String docName;

    @Column(name = "cm_doc_page_count")
    private Integer docPageCount;

    @Column(name = "cm_doc_date")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime docDate;

    @Column(name = "cm_doc_ext")
    private String docExt;

    @Column(name = "cm_doc_size")
    private Long docSize;

    @Column(name = "cm_has_attachment")
    private Boolean hasAttachment;

    @Column(name = "cm_is_attachment")
    private Boolean isAttachment;

    @Column(name = "cm_email_from")
    private String emailFrom;

    @Column(name = "cm_email_recipient")
    private String emailRecipient;

    @Column(name = "cm_multimedia_duration_sec")
    private Integer multimediaDurationSec;

    @Column(name = "cm_multimedia_properties")
    private String multimediaProperties;

    @Column(name = "cm_reviewed_analysis")
    private String reviewedAnalysis;

    @Column(name = "cm_last_reviewed_by")
    private String lastReviewedBy;

    @Column(name = "cm_source")
    private String source;

    @Column(name = "cm_exempt_withheld_reason")
    private String exemptWithheldReason;

    @Column(name = "cm_exempt_withheld")
    private Boolean exemptWithheld;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public Long getMatterId()
    {
        return matterId;
    }

    public void setMatterId(Long matterId)
    {
        this.matterId = matterId;
    }

    public String getProductionKey()
    {
        return productionKey;
    }

    public void setProductionKey(String productionKey)
    {
        this.productionKey = productionKey;
    }

    public Long getZylabId()
    {
        return zylabId;
    }

    public void setZylabId(Long zylabId)
    {
        this.zylabId = zylabId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getProducedPages()
    {
        return producedPages;
    }

    public void setProducedPages(Integer producedPages)
    {
        this.producedPages = producedPages;
    }

    public LocalDateTime getProductionCreateDate()
    {
        return productionCreateDate;
    }

    public void setProductionCreateDate(LocalDateTime productionCreateDate)
    {
        this.productionCreateDate = productionCreateDate;
    }

    public Boolean getContainsRedaction()
    {
        return containsRedaction;
    }

    public void setContainsRedaction(Boolean containsRedaction)
    {
        this.containsRedaction = containsRedaction;
    }

    public String getRedactionCode1()
    {
        return redactionCode1;
    }

    public void setRedactionCode1(String redactionCode1)
    {
        this.redactionCode1 = redactionCode1;
    }

    public String getRedactionCode2()
    {
        return redactionCode2;
    }

    public void setRedactionCode2(String redactionCode2)
    {
        this.redactionCode2 = redactionCode2;
    }

    public String getRedactionJustification()
    {
        return redactionJustification;
    }

    public void setRedactionJustification(String redactionJustification)
    {
        this.redactionJustification = redactionJustification;
    }

    public String getCustodian()
    {
        return custodian;
    }

    public void setCustodian(String custodian)
    {
        this.custodian = custodian;
    }

    public String getDocName()
    {
        return docName;
    }

    public void setDocName(String docName)
    {
        this.docName = docName;
    }

    public Integer getDocPageCount()
    {
        return docPageCount;
    }

    public void setDocPageCount(Integer docPageCount)
    {
        this.docPageCount = docPageCount;
    }

    public LocalDateTime getDocDate()
    {
        return docDate;
    }

    public void setDocDate(LocalDateTime docDate)
    {
        this.docDate = docDate;
    }

    public String getDocExt()
    {
        return docExt;
    }

    public void setDocExt(String docExt)
    {
        this.docExt = docExt;
    }

    public Long getDocSize()
    {
        return docSize;
    }

    public void setDocSize(Long docSize)
    {
        this.docSize = docSize;
    }

    public Boolean getHasAttachment()
    {
        return hasAttachment;
    }

    public void setHasAttachment(Boolean hasAttachment)
    {
        this.hasAttachment = hasAttachment;
    }

    public Boolean getAttachment()
    {
        return isAttachment;
    }

    public void setAttachment(Boolean attachment)
    {
        isAttachment = attachment;
    }

    public String getEmailFrom()
    {
        return emailFrom;
    }

    public void setEmailFrom(String emailFrom)
    {
        this.emailFrom = emailFrom;
    }

    public String getEmailRecipient()
    {
        return emailRecipient;
    }

    public void setEmailRecipient(String emailRecipient)
    {
        this.emailRecipient = emailRecipient;
    }

    public Integer getMultimediaDurationSec()
    {
        return multimediaDurationSec;
    }

    public void setMultimediaDurationSec(Integer multimediaDurationSec)
    {
        this.multimediaDurationSec = multimediaDurationSec;
    }

    public String getMultimediaProperties()
    {
        return multimediaProperties;
    }

    public void setMultimediaProperties(String multimediaProperties)
    {
        this.multimediaProperties = multimediaProperties;
    }

    public String getReviewedAnalysis()
    {
        return reviewedAnalysis;
    }

    public void setReviewedAnalysis(String reviewedAnalysis)
    {
        this.reviewedAnalysis = reviewedAnalysis;
    }

    public String getLastReviewedBy()
    {
        return lastReviewedBy;
    }

    public void setLastReviewedBy(String lastReviewedBy)
    {
        this.lastReviewedBy = lastReviewedBy;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public String getExemptWithheldReason() {
        return exemptWithheldReason;
    }

    public void setExemptWithheldReason(String exemptWithheldReason) {
        this.exemptWithheldReason = exemptWithheldReason;
    }

    public Boolean getExemptWithheld() {
        return exemptWithheld;
    }

    public void setExemptWithheld(Boolean exemptWithheld) {
        this.exemptWithheld = exemptWithheld;
    }

    @Override
    public String toString() {
        return "ZylabFileMetadata{" +
                "id=" + id +
                ", matterId=" + matterId +
                ", productionKey='" + productionKey + '\'' +
                ", zylabId=" + zylabId +
                ", name=" + name +
                ", producedPages=" + producedPages +
                ", productionCreateDate=" + productionCreateDate +
                ", containsRedaction=" + containsRedaction +
                ", redactionCode1='" + redactionCode1 + '\'' +
                ", redactionCode2='" + redactionCode2 + '\'' +
                ", redactionJustification='" + redactionJustification + '\'' +
                ", custodian='" + custodian + '\'' +
                ", docName='" + docName + '\'' +
                ", docPageCount=" + docPageCount +
                ", docDate=" + docDate +
                ", docExt='" + docExt + '\'' +
                ", docSize=" + docSize +
                ", hasAttachment=" + hasAttachment +
                ", isAttachment=" + isAttachment +
                ", emailFrom='" + emailFrom + '\'' +
                ", emailRecipient='" + emailRecipient + '\'' +
                ", multimediaDurationSec=" + multimediaDurationSec +
                ", multimediaProperties='" + multimediaProperties + '\'' +
                ", reviewedAnalysis='" + reviewedAnalysis + '\'' +
                ", lastReviewedBy='" + lastReviewedBy + '\'' +
                ", source='" + source + '\'' +
                ", exemptWithheldReason='" + exemptWithheldReason + '\'' +
                ", exemptWithheld=" + exemptWithheld +
                '}';
    }
}
