package com.armedia.acm.services.search.model.solr;

import com.armedia.acm.services.search.model.SearchConstants;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SolrContentDocument extends SolrAbstractDocument implements Serializable
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private String id;
    private String ecmFileId;
    private String content_type;
    private String object_id_s;
    private String object_type_s;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = SearchConstants.SOLR_DATE_FORMAT, timezone = SearchConstants.TIME_ZONE_UTC)
    private Date create_date_tdt;
    private String creator_lcs;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = SearchConstants.SOLR_DATE_FORMAT, timezone = SearchConstants.TIME_ZONE_UTC)
    private Date modified_date_tdt;
    private String modifier_lcs;
    private String name;
    private String parent_id_s;
    private String parent_type_s;
    private String parent_number_lcs;
    private boolean public_doc_b;
    private boolean protected_object_b;
    private List<String> deny_acl_ss;
    private List<String> allow_acl_ss;
    private String status_lcs;
    private String parent_ref_s;
    private boolean hidden_b;
    private String title_parseable;
    private String title_parseable_lcs;
    private String assignee_full_name_lcs;
    private String type_lcs;
    private String ext_s;
    private String mime_type_s;
    private String url;
    private List<String> skipAdditionalPropertiesInURL;

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

    public String getEcmFileId()
    {
        return ecmFileId;
    }

    public void setEcmFileId(String ecmFileId)
    {
        this.ecmFileId = ecmFileId;
    }

    public List<String> getDeny_acl_ss()
    {
        return deny_acl_ss;
    }

    @Override
    public void setDeny_acl_ss(List<String> deny_acl_ss)
    {
        this.deny_acl_ss = deny_acl_ss;
    }

    public List<String> getAllow_acl_ss()
    {
        return allow_acl_ss;
    }

    @Override
    public void setAllow_acl_ss(List<String> allow_acl_ss)
    {
        this.allow_acl_ss = allow_acl_ss;
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

    public boolean isProtected_object_b()
    {
        return protected_object_b;
    }

    @Override
    public void setProtected_object_b(boolean protected_object_b)
    {
        this.protected_object_b = protected_object_b;
    }

    public String getContent_type()
    {
        return content_type;
    }

    public void setContent_type(String content_type)
    {
        this.content_type = content_type;
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

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getParent_id_s()
    {
        return parent_id_s;
    }

    public void setParent_id_s(String parent_id_s)
    {
        this.parent_id_s = parent_id_s;
    }

    public String getParent_type_s()
    {
        return parent_type_s;
    }

    public void setParent_type_s(String parent_type_s)
    {
        this.parent_type_s = parent_type_s;
    }

    public String getParent_number_lcs()
    {
        return parent_number_lcs;
    }

    public void setParent_number_lcs(String parent_number_lcs)
    {
        this.parent_number_lcs = parent_number_lcs;
    }

    public String getStatus_lcs()
    {
        return status_lcs;
    }

    public void setStatus_lcs(String status_lcs)
    {
        this.status_lcs = status_lcs;
    }

    public String getParent_ref_s()
    {
        return parent_ref_s;
    }

    public void setParent_ref_s(String parent_ref_s)
    {
        this.parent_ref_s = parent_ref_s;
    }

    public boolean isHidden_b()
    {
        return hidden_b;
    }

    public void setHidden_b(boolean hidden_b)
    {
        this.hidden_b = hidden_b;
    }

    public String getTitle_parseable()
    {
        return title_parseable;
    }

    public void setTitle_parseable(String title_parseable)
    {
        this.title_parseable = title_parseable;
    }

    public String getTitle_parseable_lcs()
    {
        return title_parseable_lcs;
    }

    public void setTitle_parseable_lcs(String title_parseable_lcs)
    {
        this.title_parseable_lcs = title_parseable_lcs;
    }

    public String getAssignee_full_name_lcs()
    {
        return assignee_full_name_lcs;
    }

    public void setAssignee_full_name_lcs(String assignee_full_name_lcs)
    {
        this.assignee_full_name_lcs = assignee_full_name_lcs;
    }

    public String getType_lcs()
    {
        return type_lcs;
    }

    public void setType_lcs(String type_lcs)
    {
        this.type_lcs = type_lcs;
    }

    public String getExt_s()
    {
        return ext_s;
    }

    public void setExt_s(String ext_s)
    {
        this.ext_s = ext_s;
    }

    public String getMime_type_s()
    {
        return mime_type_s;
    }

    public void setMime_type_s(String mime_type_s)
    {
        this.mime_type_s = mime_type_s;
    }

    private String encode(String str)
    {
        String encodedStr = "";
        try
        {
            encodedStr = URLEncoder.encode(str,"UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            log.warn("Unsupported 'UTF-8' encoding for text [{}]. Empty string will be used instead.", str);
        }

        return encodedStr;
    }

    public String getUrl()
    {
        String _url = "literal.allow_acl_ss=" + (allow_acl_ss == null ? null : String.join("&literal.allow_acl_ss=", allow_acl_ss)) +
                        "&literal.deny_acl_ss=" + (deny_acl_ss == null ? null : String.join("&literal.deny_acl_ss=", deny_acl_ss)) +
                        "&literal.hidden_b=" + hidden_b +
                        "&literal.parent_ref_s=" + parent_ref_s +
                        "&literal.status_lcs=" + status_lcs +
                        "&literal.protected_object_b=" + protected_object_b +
                        "&literal.public_doc_b=" + public_doc_b +
                        "&literal.id=" + id +
                        "&literal.object_type_s=" + object_type_s +
                        "&literal.object_id_s=" + object_id_s +
                        "&literal.modified_date_tdt=" + modified_date_tdt +
                        "&literal.modifier_lcs=" + modifier_lcs +
                        "&literal.create_date_tdt=" + create_date_tdt +
                        "&literal.creator_lcs=" + creator_lcs +
                        "&literal.name=" + encode(name) +
                        "&literal.parent_id_s=" + parent_id_s +
                        "&literal.parent_type_s=" + parent_type_s +
                        "&literal.parent_number_lcs=" + parent_number_lcs +
                        "&literal.title_parseable=" + encode(title_parseable) +
                        "&literal.title_parseable_lcs=" + encode(title_parseable_lcs) +
                        "&literal.assignee_full_name_lcs=" + assignee_full_name_lcs +
                        "&literal.type_lcs=" + type_lcs +
                        "&literal.ext_s=" + ext_s +
                        "&literal.mime_type_s=" + encode(mime_type_s);

        if (getAdditionalProperties() != null)
        {
            for (Map.Entry<String, Object> entry : getAdditionalProperties().entrySet())
            {
                if (getSkipAdditionalPropertiesInURL() != null && !getSkipAdditionalPropertiesInURL().contains(entry.getKey()))
                {
                    _url += "&literal." + entry.getKey() + "=" + entry.getValue();
                }
            }
        }

        setUrl(_url);

        return this.url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public List<String> getSkipAdditionalPropertiesInURL()
    {
        return skipAdditionalPropertiesInURL;
    }

    public void setSkipAdditionalPropertiesInURL(List<String> skipAdditionalPropertiesInURL)
    {
        this.skipAdditionalPropertiesInURL = skipAdditionalPropertiesInURL;
    }
}
