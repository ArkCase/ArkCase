package org.jaudiotagger.audio;

/**
 * Created by Vladimir Cherepnalkovski
 */
public enum SupportedFileFormat
{
    OGG("ogg"),
    MP3("mp3"),
    FLAC("flac"),
    MP4("mp4"),
    M4A("m4a"),
    M4P("m4p"),
    WMA("wma"),
    WAV("wav"),
    RA("ra"),
    RM("rm"),
    M4B("m4b"),
    // ArkCase changes BEGIN
    TMP("tmp");
    // ArkCase changes END

    private String filesuffix;

    private SupportedFileFormat(String filesuffix)
    {
        this.filesuffix = filesuffix;
    }

    public String getFilesuffix()
    {
        return this.filesuffix;
    }
}