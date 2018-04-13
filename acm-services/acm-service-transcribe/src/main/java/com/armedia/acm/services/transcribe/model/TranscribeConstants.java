package com.armedia.acm.services.transcribe.model;

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
