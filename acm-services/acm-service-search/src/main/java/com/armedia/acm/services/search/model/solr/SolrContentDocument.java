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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SolrContentDocument extends SolrAdvancedSearchDocument
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private List<String> skipAdditionalPropertiesInURL;

    private String cmis_version_series_id_s;

    protected void listToUrlValues(Map<String, Object> values, List<? extends Object> list, String key)
    {
        if (list != null)
        {
            for (int a = 0; a < list.size(); a++)
            {
                values.put("literal." + key + "." + a, list.get(a));
            }
        }
    }

    /**
     * 
     * @return a map suitable for use in the Spring RestTemplate postForEntity method. The partner method
     *         buildUrlTemlpate() provides the template needed by Spring.
     */
    public Map<String, Object> buildUrlValues()
    {
        Map<String, Object> values = new HashMap<>();

        listToUrlValues(values, getAllowGroup_ls(), "allow_group_ls");
        listToUrlValues(values, getDenyGroup_ls(), "deny_group_ls");
        listToUrlValues(values, getAllowUser_ls(), "allow_user_ls");
        listToUrlValues(values, getDenyUser_ls(), "deny_user_ls");
        listToUrlValues(values, getAllowRole_ss(), "allow_role_ss");
        listToUrlValues(values, getDenyRole_ss(), "deny_role_ss");

        values.put("literal.hidden_b", isHidden_b());
        values.put("literal.parent_ref_s", getParent_ref_s());
        values.put("literal.status_lcs", getStatus_lcs());
        values.put("literal.protected_object_b", isProtected_object_b());
        values.put("literal.public_doc_b", isPublic_doc_b());
        values.put("literal.id", getId());
        values.put("literal.object_type_s", getObject_type_s());
        values.put("literal.object_id_s", getObject_id_s());
        values.put("literal.modified_date_tdt", getModified_date_tdt());
        values.put("literal.modifier_lcs", getModifier_lcs());
        values.put("literal.create_date_tdt", getCreate_date_tdt());
        values.put("literal.creator_lcs", getCreator_lcs());
        values.put("literal.name", getName());
        values.put("literal.parent_id_s", getParent_id_s());
        values.put("literal.parent_type_s", getParent_type_s());
        values.put("literal.parent_number_lcs", getParent_number_lcs());
        values.put("literal.title_parseable", getTitle_parseable());
        values.put("literal.title_parseable_lcs", getTitle_parseable_lcs());
        values.put("literal.assignee_full_name_lcs", getAssignee_full_name_lcs());
        values.put("literal.type_lcs", getType_lcs());
        values.put("literal.ext_s", getExt_s());
        values.put("literal.mime_type_s", getMime_type_s());

        if (getAdditionalProperties() != null)
        {
            for (Map.Entry<String, Object> entry : getAdditionalProperties().entrySet())
            {
                if (getSkipAdditionalPropertiesInURL() != null && !getSkipAdditionalPropertiesInURL().contains(entry.getKey()))
                {
                    values.put("literal." + entry.getKey(), entry.getValue());
                }
            }
        }

        return values;
    }

    /**
     * 
     * @return a URL template for use by the Spring Rest Template. You must also provide the values, via
     *         buildUrlValues().
     */
    public String buildUrlTemplate()
    {
        final List<String> multivalueProperties = Arrays.asList("literal.allow_acl_ss", "literal.deny_acl_ss");
        return buildUrlValues().keySet().stream().
        // Solr multivalued elements are represented in the buildUrlValues map as e.g. "literal.allow_acl_ss.0",
        // "literal.deny_acl_ss.1".
        // We want the URL template to be e.g. "literal.allow_acl_ss={literal.allow_acl_ss.0}",
        // "literal.deny_acl_ss={literal.deny_acl_ss.1"
                map(k -> String.format("%s={%s}",
                        multivalueProperties.contains(StringUtils.substringBeforeLast(k, ".")) ? StringUtils.substringBeforeLast(k, ".")
                                : k,
                        k))
                .collect(Collectors.joining("&"));
    }

    /**
     * This method is no longer used in the content-file-to-Solr flow. Kept for backwards compatibility.
     * 
     * @deprecated use buildUrlTemplate and buildUrlValues
     * @return
     */
    @Deprecated
    public String getUrl()
    {
        StringBuilder url = new StringBuilder(
                "literal.allow_role_ss="
                        + (getAllowRole_ss() == null ? null : String.join("&literal.allow_role_ss=", getAllowRole_ss()))
                        + "&literal.deny_role_ss="
                        + (getDenyRole_ss() == null ? null : String.join("&literal.deny_role_ss=", getDenyRole_ss()))
                        + "&literal.allow_user_ls="
                        + (getAllowUser_ls() == null ? null: getAllowUser_ls().stream().map(Object::toString).collect(Collectors.joining("&literal.allow_user_ls=")))
                        +"&literal.deny_user_ls="
                        +(getDenyUser_ls() == null ? null : getDenyUser_ls().stream().map(Object::toString).collect(Collectors.joining("&literal.deny_user_ls=")))
                        +"&literal.allow_group_ls="
                        +(getAllowGroup_ls() == null ? null : getAllowGroup_ls().stream().map(Object::toString).collect(Collectors.joining("&literal.allow_group_ls")))
                        +"&literal.deny_group_ls="
                        + (getDenyGroup_ls() == null ? null : getDenyGroup_ls().stream().map(Object::toString).collect(Collectors.joining("&literal.deny_group_ls")))
                        +
                        "&literal.hidden_b=" + isHidden_b() +
                        "&literal.parent_ref_s=" + encode(getParent_ref_s()) +
                        "&literal.status_lcs=" + encode(getStatus_lcs()) +
                        "&literal.protected_object_b=" + isProtected_object_b() +
                        "&literal.public_doc_b=" + isPublic_doc_b() +
                        "&literal.id=" + encode(getId()) +
                        "&literal.object_type_s=" + encode(getObject_type_s()) +
                        "&literal.object_id_s=" + encode(getObject_id_s()) +
                        "&literal.modified_date_tdt=" + getModified_date_tdt() +
                        "&literal.modifier_lcs=" + encode(getModifier_lcs()) +
                        "&literal.create_date_tdt=" + getCreate_date_tdt() +
                        "&literal.creator_lcs=" + encode(getCreator_lcs()) +
                        "&literal.name=" + encode(getName()) +
                        "&literal.parent_id_s=" + encode(getParent_id_s()) +
                        "&literal.parent_type_s=" + encode(getParent_type_s()) +
                        "&literal.parent_number_lcs=" + encode(getParent_number_lcs()) +
                        "&literal.title_parseable=" + encode(getTitle_parseable()) +
                        "&literal.title_parseable_lcs=" + encode(getTitle_parseable_lcs()) +
                        "&literal.assignee_full_name_lcs=" + encode(getAssignee_full_name_lcs()) +
                        "&literal.type_lcs=" + encode(getType_lcs()) +
                        "&literal.ext_s=" + encode(getExt_s()) +
                        "&literal.mime_type_s=" + encode(getMime_type_s()));

        if (getAdditionalProperties() != null)
        {
            for (Map.Entry<String, Object> entry : getAdditionalProperties().entrySet())
            {
                if (getSkipAdditionalPropertiesInURL() != null && !getSkipAdditionalPropertiesInURL().contains(entry.getKey()))
                {

                    url.append("&literal.").append(entry.getKey()).append("=")
                            .append(entry.getValue() instanceof String ? encode((String) entry.getValue()) : entry.getValue());
                }
            }
        }

        return url.toString();
    }

    private String encode(String str)
    {
        String encodedStr = "";

        if (str == null)
        {
            return encodedStr;
        }

        try
        {
            encodedStr = URLEncoder.encode(str, "UTF-8");
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

    @Override
    public String getCmis_version_series_id_s()
    {
        return cmis_version_series_id_s;
    }

    @Override
    public void setCmis_version_series_id_s(String cmis_version_series_id_s)
    {
        this.cmis_version_series_id_s = cmis_version_series_id_s;
    }
}
