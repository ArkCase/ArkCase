package com.armedia.acm.services.transcribe.model;

/*-
 * #%L
 * ACM Service: Transcribe
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

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/05/2018
 */
public interface TranscribeConstants
{
    String OBJECT_TYPE = "TRANSCRIBE";
    String OBJECT_TYPE_ITEM = "TRANSCRIBE_ITEM";

    String MEDIA_TYPE_VIDEO_RECOGNITION_KEY = "video/";
    String MEDIA_TYPE_AUDIO_RECOGNITION_KEY = "audio/";

    String TEMP_FILE_PREFIX = "transcribe-";
    String TEMP_FILE_SUFFIX = ".docx";
    String WORD_MIME_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    String FILE_CATEGORY = "Document";

    String TRANSCRIBE_SYSTEM_USER = "TRANSCRIBE_SERVICE";
    String TRANSCRIBE_SYSTEM_IP_ADDRESS = "127.0.0.1";

    String TRANSCRIBE_CREATED_EVENT = "com.armedia.acm.transcribe.created";
    String TRANSCRIBE_UPDATED_EVENT = "com.armedia.acm.transcribe.updated";
    String TRANSCRIBE_QUEUED_EVENT = "com.armedia.acm.transcribe.queued";
    String TRANSCRIBE_PROCESSING_EVENT = "com.armedia.acm.transcribe.processing";
    String TRANSCRIBE_COMPLETED_EVENT = "com.armedia.acm.transcribe.completed";
    String TRANSCRIBE_FAILED_EVENT = "com.armedia.acm.transcribe.failed";
    String TRANSCRIBE_CANCELLED_EVENT = "com.armedia.acm.transcribe.cancelled";
    String TRANSCRIBE_COMPILED_EVENT = "com.armedia.acm.transcribe.compiled";
    String TRANSCRIBE_ROLLBACK_EVENT = "com.armedia.acm.transcribe.rollback";
    String TRANSCRIBE_PROVIDER_FAILED_EVENT = "com.armedia.acm.transcribe.provider.failed";
}
