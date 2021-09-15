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

/**
 * Created by Aleksandar Acevski <aleksandar.acevski@armedia.com> on April, 2021
 */
public interface ZylabLoadFileColumns
{

    String ZYLAB_ID = "ZyLAB_ID";
    String NAME = "Name";
    String PRODUCED_PAGES = "Produced_Pages";
    String PRODUCTION_CREATE_DATE = "Production_CreateDate";
    String CONTAINS_REDACTION = "Contains_Redaction";
    String REDACTION_CODE_1 = "RedactionCode1";
    String REDACTION_CODE_2 = "RedactionCode2";
    String REDACTION_JUSTIFICATION = "RedactionJustification";
    String CUSTODIAN = "Custodian";
    String DOC_NAME = "Doc_Name";
    String DOC_PAGE_COUNT = "Doc_PageCount";
    String DOC_DATE = "Doc_Date";
    String DOC_EXT = "Doc_Ext";
    String DOC_SIZE = "Doc_Size";
    String HAS_ATTACHMENT = "Has_Attachment";
    String IS_ATTACHMENT = "Is_Attachment";
    String EMAIL_FROM = "Email_From";
    String EMAIL_RECIPIENT = "Email_Recipient";
    String MULTIMEDIA_DURATION_SEC = "Multimedia_Duration(Sec)";
    String MULTIMEDIA_PROPERTIES = "Multimedia_properties";
    String REVIEWED_ANALYSIS = "Reviewed_Analysis";
    String LAST_REVIEWED_BY = "LastReviewedBy";
    String SOURCE = "Source";
    String EXEMPT_WITHHELD_REASON = "Exempt_Withheld_Reason";
    String EXEMPT_WITHHELD = "ExemptWithheld";

}
