package com.armedia.acm.plugins.consultation.model;

/*-
 * #%L
 * ACM Default Plugin: Consultation
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
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on May, 2020
 */
public interface ChangeConsultationStateContants
{
    String MIME_TYPE_PDF = "application/pdf";
    String NEW_FILE = "NEW_FILE";
    String FILE_ID = "FILE_ID";
    String FILE_VERSION = "FILE_VERSION";

    String CHANGE_CONSULTATION_STATUS_STYLESHEET = System.getProperty("user.home")
            + "/.arkcase/acm/pdf-stylesheets/change-consultation-state-document.xsl";
    String CHANGE_CONSULTATION_STATUS_DOCUMENT = "CHANGE_CONSULTATION";
    String CHANGE_CONSULTATION_STATUS_FILENAMEFORMAT = "Change Consultation Status.pdf";
    String CHANGE_CONSULTATION_STATUS = "CHANGE_CONSULTATION_STATUS";
}
