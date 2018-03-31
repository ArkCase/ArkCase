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
}
