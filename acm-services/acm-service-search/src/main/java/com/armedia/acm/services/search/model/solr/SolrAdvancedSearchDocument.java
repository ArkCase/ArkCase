package com.armedia.acm.services.search.model.solr;

/*-
 * #%L
 * ACM Service: Search
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

import com.armedia.acm.services.search.model.SearchConstants;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Property names must be identical to the desired SOLR field names.
 */
public class SolrAdvancedSearchDocument extends SolrAbstractDocument implements Serializable
{
    private static final long serialVersionUID = 1L;

    {
        getAdditionalProperties().put("hidden_b", false);
        getAdditionalProperties().put("adhocTask_b", false);
    }
    ///////////////////// fields for all documents ///////////////////////////
    private String id;
    private String object_id_s;
    private Long object_id_i;
    private String object_type_s;
    private String name;
    private String name_lcs;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = SearchConstants.SOLR_DATE_FORMAT, timezone = SearchConstants.TIME_ZONE_UTC)
    private Date create_date_tdt;
    private String author;
    private String creator_lcs;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = SearchConstants.SOLR_DATE_FORMAT, timezone = SearchConstants.TIME_ZONE_UTC)
    private Date modified_date_tdt;
    private String modifier_lcs;

    // access control fields
    private boolean public_doc_b;
    private boolean protected_object_b;

    private List<Long> deny_group_ls;
    private List<Long> allow_group_ls;

    private List<Long> deny_user_ls;
    private List<Long> allow_user_ls;

    private List<Long> parent_deny_group_ls;
    private List<Long> parent_allow_group_ls;

    private List<Long> parent_deny_user_ls;
    private List<Long> parent_allow_user_ls;

    /////////////////// for docs with an incident date ////////////
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = SearchConstants.SOLR_DATE_FORMAT, timezone = SearchConstants.TIME_ZONE_UTC)
    private Date incident_date_tdt;

    /////////////////// for docs with a due date////////////////////
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = SearchConstants.SOLR_DATE_FORMAT, timezone = SearchConstants.TIME_ZONE_UTC)
    private Date dueDate_tdt;

    @Override
    public String getId()
    {
        return id;
    }

    @Override
    public void setId(String id)
    {
        this.id = id;
    }

    public String getObject_id_s()
    {
        return object_id_s;
    }

    public void setObject_id_s(String object_id_s)
    {
        this.object_id_s = object_id_s;
    }

    public String getObject_type_s()
    {
        return object_type_s;
    }

    public void setObject_type_s(String object_type_s)
    {
        this.object_type_s = object_type_s;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Date getCreate_date_tdt()
    {
        return create_date_tdt;
    }

    public void setCreate_date_tdt(Date create_date_tdt)
    {
        this.create_date_tdt = create_date_tdt;
    }

    public String getCreator_lcs()
    {
        return creator_lcs;
    }

    public void setCreator_lcs(String creator_lcs)
    {
        this.creator_lcs = creator_lcs;
    }

    public Date getModified_date_tdt()
    {
        return modified_date_tdt;
    }

    public void setModified_date_tdt(Date modified_date_tdt)
    {
        this.modified_date_tdt = modified_date_tdt;
    }

    public String getModifier_lcs()
    {
        return modifier_lcs;
    }

    public void setModifier_lcs(String modifier_lcs)
    {
        this.modifier_lcs = modifier_lcs;
    }

    public boolean isProtected_object_b()
    {
        return protected_object_b;
    }

    @Override
    public void setProtected_object_b(boolean protected_object_b)
    {
        this.protected_object_b = protected_object_b;
    }

    public List<Long> getDeny_group_ls()
    {
        return deny_group_ls;
    }

    @Override
    public void setDeny_group_ls(List<Long> deny_group_ls)
    {
        this.deny_group_ls = deny_group_ls;
    }

    public List<Long> getAllow_group_ls()
    {
        return allow_group_ls;
    }

    @Override
    public void setAllow_group_ls(List<Long> allow_group_ls)
    {
        this.allow_group_ls = allow_group_ls;
    }

    public List<Long> getDeny_user_ls()
    {
        return deny_user_ls;
    }

    @Override
    public void setDeny_user_ls(List<Long> deny_user_ls)
    {
        this.deny_user_ls = deny_user_ls;
    }

    public List<Long> getAllow_user_ls()
    {
        return allow_user_ls;
    }

    @Override
    public void setAllow_user_ls(List<Long> allow_user_ls)
    {
        this.allow_user_ls = allow_user_ls;
    }

    public List<Long> getParent_deny_group_ls()
    {
        return parent_deny_group_ls;
    }

    @Override
    public void setParent_deny_group_ls(List<Long> parent_deny_group_ls)
    {
        this.parent_deny_group_ls = parent_deny_group_ls;
    }

    public List<Long> getParent_allow_group_ls()
    {
        return parent_allow_group_ls;
    }

    @Override
    public void setParent_allow_group_ls(List<Long> parent_allow_group_ls)
    {
        this.parent_allow_group_ls = parent_allow_group_ls;
    }

    public List<Long> getParent_deny_user_ls()
    {
        return parent_deny_user_ls;
    }

    @Override
    public void setParent_deny_user_ls(List<Long> parent_deny_user_ls)
    {
        this.parent_deny_user_ls = parent_deny_user_ls;
    }

    public List<Long> getParent_allow_user_ls()
    {
        return parent_allow_user_ls;
    }

    @Override
    public void setParent_allow_user_ls(List<Long> parent_allow_user_ls)
    {
        this.parent_allow_user_ls = parent_allow_user_ls;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public Long getObject_id_i()
    {
        return object_id_i;
    }

    public void setObject_id_i(Long object_id_i)
    {
        this.object_id_i = object_id_i;
    }

    public boolean isPublic_doc_b()
    {
        return public_doc_b;
    }

    @Override
    public void setPublic_doc_b(boolean public_doc_b)
    {
        this.public_doc_b = public_doc_b;
    }

    public Date getIncident_date_tdt()
    {
        return incident_date_tdt;
    }

    public void setIncident_date_tdt(Date incident_date_tdt)
    {
        this.incident_date_tdt = incident_date_tdt;
    }

    public Date getDueDate_tdt()
    {
        return dueDate_tdt;
    }

    public void setDueDate_tdt(Date dueDate_tdt)
    {
        this.dueDate_tdt = dueDate_tdt;
    }

    public String getName_lcs()
    {
        return name_lcs;
    }

    public void setName_lcs(String name_lcs)
    {
        this.name_lcs = name_lcs;
    }

    @Override
    public String toString()
    {
        return "SolrAdvancedSearchDocument{" +
                "id='" + id + '\'' +
                ", object_id_s='" + object_id_s + '\'' +
                ", object_id_i=" + object_id_i +
                ", object_type_s='" + object_type_s + '\'' +
                ", name='" + name + '\'' +
                ", name_lcs='" + name_lcs + '\'' +
                ", create_date_tdt=" + create_date_tdt +
                ", author='" + author + '\'' +
                ", creator_lcs='" + creator_lcs + '\'' +
                ", modified_date_tdt=" + modified_date_tdt +
                ", modifier_lcs='" + modifier_lcs + '\'' +
                ", public_doc_b=" + public_doc_b +
                ", protected_object_b=" + protected_object_b +
                ", deny_group_ls=" + deny_group_ls +
                ", allow_group_ls=" + allow_group_ls +
                ", deny_user_ls=" + deny_user_ls +
                ", allow_user_ls=" + allow_user_ls +
                ", parent_deny_group_ls=" + parent_deny_group_ls +
                ", parent_allow_group_ls=" + parent_allow_group_ls +
                ", parent_deny_user_ls=" + parent_deny_user_ls +
                ", parent_allow_user_ls=" + parent_allow_user_ls +
                ", incident_date_tdt=" + incident_date_tdt +
                ", dueDate_tdt=" + dueDate_tdt +
                '}';
    }
}
