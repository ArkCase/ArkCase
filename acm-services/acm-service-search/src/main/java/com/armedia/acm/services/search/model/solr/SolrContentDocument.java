package com.armedia.acm.services.search.model.solr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public class SolrContentDocument extends SolrAdvancedSearchDocument
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private List<String> skipAdditionalPropertiesInURL;

    public String getUrl()
    {
        String url = "literal.allow_acl_ss=" + (getAllow_acl_ss() == null ? null : String.join("&literal.allow_acl_ss=", getAllow_acl_ss())) +
                        "&literal.deny_acl_ss=" + (getDeny_acl_ss() == null ? null : String.join("&literal.deny_acl_ss=", getDeny_acl_ss())) +
                        "&literal.hidden_b=" + isHidden_b() +
                        "&literal.parent_ref_s=" + getParent_ref_s() +
                        "&literal.status_lcs=" + getStatus_lcs() +
                        "&literal.protected_object_b=" + isProtected_object_b() +
                        "&literal.public_doc_b=" + isPublic_doc_b() +
                        "&literal.id=" + getId() +
                        "&literal.object_type_s=" + getObject_type_s() +
                        "&literal.object_id_s=" + getObject_id_s() +
                        "&literal.modified_date_tdt=" + getModified_date_tdt() +
                        "&literal.modifier_lcs=" + getModifier_lcs() +
                        "&literal.create_date_tdt=" + getCreate_date_tdt() +
                        "&literal.creator_lcs=" + getCreator_lcs() +
                        "&literal.name=" + encode(getName()) +
                        "&literal.parent_id_s=" + getParent_id_s() +
                        "&literal.parent_type_s=" + getParent_type_s() +
                        "&literal.parent_number_lcs=" + getParent_number_lcs() +
                        "&literal.title_parseable=" + encode(getTitle_parseable()) +
                        "&literal.title_parseable_lcs=" + encode(getTitle_parseable_lcs()) +
                        "&literal.assignee_full_name_lcs=" + getAssignee_full_name_lcs() +
                        "&literal.type_lcs=" + getType_lcs() +
                        "&literal.ext_s=" + getExt_s() +
                        "&literal.mime_type_s=" + encode(getMime_type_s());

        if (getAdditionalProperties() != null)
        {
            for (Map.Entry<String, Object> entry : getAdditionalProperties().entrySet())
            {
                if (getSkipAdditionalPropertiesInURL() != null && !getSkipAdditionalPropertiesInURL().contains(entry.getKey()))
                {
                    url += "&literal." + entry.getKey() + "=" + entry.getValue();
                }
            }
        }

        return url;
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

    public List<String> getSkipAdditionalPropertiesInURL()
    {
        return skipAdditionalPropertiesInURL;
    }

    public void setSkipAdditionalPropertiesInURL(List<String> skipAdditionalPropertiesInURL)
    {
        this.skipAdditionalPropertiesInURL = skipAdditionalPropertiesInURL;
    }
}
