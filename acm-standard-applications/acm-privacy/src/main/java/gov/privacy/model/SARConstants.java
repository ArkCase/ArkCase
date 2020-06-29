/**
 *
 */
package gov.privacy.model;

/*-
 * #%L
 * ACM Privacy: Subject Access Request
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 *
 */
public interface SARConstants
{

    String DATA_ACCESS_REQUEST = "Data Access Request";
    String RIGHT_TO_BE_FORGOTTEN_REQUEST = "Right to be Forgotten Request";
    String DATA_MODIFICATION_REQUEST = "Data Modification Request";
    String OTHER_REQUEST = "Other";

    String SIMPLE_REQUEST_TRACK = "simple";

    String REQ = "REQ";
    String ACK = "ACK";
    String RECEIVE_ACK = "RECEIVE_ACK";
    String REQ_DELETE = "REQ_DELETE";
    String DENIAL = "DENIAL";
    String REQ_EXTENSION = "REQ_EXTENSION";

    String MIME_TYPE_PDF = "application/pdf";
    String MIME_TYPE_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    String NEW_FILE = "NEW_FILE";
    String FILE_ID = "FILE_ID";

    String EMAIL_HEADER_SUBJECT = "Privacy Extension Notification";
    String EMAIL_HEADER_ATTACHMENT = "Hello,";
    String EMAIL_BODY_ATTACHMENT = "Please find the attached document(s) for your review.";
    String EMAIL_FOOTER_ATTACHMENT = "Powered by ArkCase, Enterprise Case Management platform, http://www.arkcase.com";

    String EMAIL_RELEASE_SUBJECT = "Request Complete";

    String EMAIL_RESPONSE_FOLDER_ZIP = "New installment of documents is now available for your Subject Acces Request";

    String PRIVACY_PIPELINE_EXTENSION_PROPERTY_KEY = "privacy_request_extension";

    String FULFILL_QUEUE = "Fulfill";
    String INTAKE_QUEUE = "Intake";
}
