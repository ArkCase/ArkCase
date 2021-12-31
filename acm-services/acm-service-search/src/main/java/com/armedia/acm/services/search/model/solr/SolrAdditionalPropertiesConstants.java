package com.armedia.acm.services.search.model.solr;

/*-
 * #%L
 * ACM Service: Search
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

public final class SolrAdditionalPropertiesConstants
{
    private SolrAdditionalPropertiesConstants()
    {
        // restrict instantiation
    }

    public static final String ACTION_LCS = "action_lcs";
    public static final String DATA_LCS = "data_lcs";
    public static final String CREATOR_FULL_NAME_LCS = "creator_full_name_lcs";
    public static final String MODIFIER_FULL_NAME_LCS = "modifier_full_name_lcs";

    /////////////////// for complaints, case files, consultations other objects with a title or description ////////////
    public static final String TITLE_PARSEABLE = "title_parseable";
    public static final String DESCRIPTION_PARSEABLE = "description_parseable";
    public static final String SUMMARY_PARSEABLE_LCS = "summary_parseable_lcs";

    // for sorting//
    public static final String TITLE_PARSEABLE_LCS = "title_parseable_lcs";

    /////////////////// for complaints, case files, tasks we introduce description and for personAssociation we
    /////////////////// introduce notes ////////////
    public static final String DESCRIPTION_NO_HTML_TAGS_PARSEABLE = "description_no_html_tags_parseable";

    /////////////////// for docs with a priority ////////////
    public static final String PRIORITY_LCS = "priority_lcs";

    /////////////////// for docs with an assignee ////////////
    public static final String ASSIGNEE_ID_LCS = "assignee_id_lcs";
    public static final String ASSIGNEE_FIRST_NAME_LCS = "assignee_first_name_lcs";
    public static final String ASSIGNEE_LAST_NAME_LCS = "assignee_last_name_lcs";
    public static final String ASSIGNEE_FULL_NAME_LCS = "assignee_full_name_lcs";
    public static final String INCIDENT_TYPE_LCS = "incident_type_lcs";

    ////////////// associated tags ////////////////////
    public static final String TAG_TOKEN_LCS = "tag_token_lcs";

    /////////////////// for docs with a status date ////////////
    public static final String STATUS_LCS = "status_lcs";

    /////////////////// for person records //////////////////////
    public static final String PERSON_TITLE_LCS = "person_title_lcs";
    public static final String FIRST_NAME_LCS = "first_name_lcs";
    public static final String LAST_NAME_LCS = "last_name_lcs";
    public static final String FULL_NAME_LCS = "full_name_lcs";

    /////////////////// for acm users ///////////////
    public static final String EMAIL_LCS = "email_lcs";

    /////////////////// for orgs, contact methods, and other objects with a type and a value /////////////
    public static final String TYPE_LCS = "type_lcs";
    public static final String VALUE_PARSEABLE = "value_parseable";

    ////////////////// for postal addresses /////////////////////
    public static final String LOCATION_STREET_ADDRESS_LCS = "location_street_address_lcs";
    public static final String LOCATION_CITY_LCS = "location_city_lcs";
    public static final String LOCATION_STATE_LCS = "location_state_lcs";
    public static final String LOCATION_POSTAL_CODE_LCS = "location_postal_code_sdo";

    ///////////////////// for objects in a parent-child relationship, or association objects /////////////
    public static final String CHILD_ID_S = "child_id_s";
    public static final String CHILD_TYPE_S = "child_type_s";
    public static final String PARENT_ID_S = "parent_id_s";
    public static final String PARENT_OBJECT_ID_I = "parent_object_id_i";
    public static final String PARENT_TYPE_S = "parent_type_s";
    public static final String PARENT_NAME_T = "parent_name_t";
    public static final String PARENT_NUMBER_LCS = "parent_number_lcs";

    ////////////////// for objects that own organizations, e.g. persons /////////////////////
    public static final String ORGANIZATION_ID_SS = "organization_id_ss";

    ////////////////// for objects that own postal addresses, e.g. persons /////////////////////
    public static final String POSTAL_ADDRESS_ID_SS = "postal_address_id_ss";

    ////////////////// for objects that own contact methods, e.g. persons /////////////////////
    public static final String CONTACT_METHOD_SS = "contact_method_ss";

    ///////////////// for objects that have multiple children, supervisor, members ... e.g. group ///////////////////
    public static final String SUPERVISOR_ID_S = "supervisor_id_s";
    public static final String CHILD_ID_SS = "child_id_ss";
    public static final String MEMBER_ID_SS = "member_id_ss";
    public static final String ADHOC_TASK_B = "adhocTask_b";
    public static final String OWNER_LCS = "owner_lcs";

    ////////////// for business process tasks /////////
    public static final String BUSINESS_PROCESS_NAME_LCS = "business_process_name_lcs";
    public static final String BUSINESS_PROCESS_ID_I = "business_process_id_i";

    /////////////////////// for content files /////////////////////////////////////////
    public static final String CONTENT_TYPE = "content_type";
    public static final String ECM_FILE_ID = "ecmFileId";

    public static final String CMIS_VERSION_SERIES_ID_S = "cmis_version_series_id_s";

    /////////////////////// for notification /////////////////////////////////////////
    public static final String STATE_LCS = "state_lcs";

    public static final String PARENT_REF_S = "parent_ref_s";
    public static final String HIDDEN_B = "hidden_b";

    public static final String PARENT_FOLDER_ID_I = "parent_folder_id_i";

    public static final String EXT_S = "ext_s";
    public static final String MIME_TYPE_S = "mime_type_s";
    public static final String ACM_PARTICIPANTS_LCS = "acm_participants_lcs";


}
