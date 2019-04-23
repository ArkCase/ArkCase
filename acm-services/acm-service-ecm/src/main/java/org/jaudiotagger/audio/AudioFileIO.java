//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jaudiotagger.audio;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import org.jaudiotagger.audio.asf.AsfFileReader;
import org.jaudiotagger.audio.asf.AsfFileWriter;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.flac.FlacFileReader;
import org.jaudiotagger.audio.flac.FlacFileWriter;
import org.jaudiotagger.audio.generic.AudioFileModificationListener;
import org.jaudiotagger.audio.generic.AudioFileReader;
import org.jaudiotagger.audio.generic.AudioFileWriter;
import org.jaudiotagger.audio.generic.ModificationHandler;
import org.jaudiotagger.audio.generic.Utils;
import org.jaudiotagger.audio.mp3.MP3FileReader;
import org.jaudiotagger.audio.mp3.MP3FileWriter;
import org.jaudiotagger.audio.mp4.Mp4FileReader;
import org.jaudiotagger.audio.mp4.Mp4FileWriter;
import org.jaudiotagger.audio.ogg.OggFileReader;
import org.jaudiotagger.audio.ogg.OggFileWriter;
import org.jaudiotagger.audio.real.RealFileReader;
import org.jaudiotagger.audio.wav.WavFileReader;
import org.jaudiotagger.audio.wav.WavFileWriter;
import org.jaudiotagger.logging.ErrorMessage;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class AudioFileIO
{
    public static Logger logger = Logger.getLogger("org.jaudiotagger.audio");
    private static AudioFileIO defaultInstance;
    private final ModificationHandler modificationHandler = new ModificationHandler();
    private Map<String, AudioFileReader> readers = new HashMap();
    private Map<String, AudioFileWriter> writers = new HashMap();

    public static void delete(AudioFile f) throws CannotReadException, CannotWriteException
    {
        getDefaultAudioFileIO().deleteTag(f);
    }

    public static AudioFileIO getDefaultAudioFileIO()
    {
        if (defaultInstance == null)
        {
            defaultInstance = new AudioFileIO();
        }

        return defaultInstance;
    }

    public static AudioFile read(File f)
            throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException
    {
        return getDefaultAudioFileIO().readFile(f);
    }

    public static void write(AudioFile f) throws CannotWriteException
    {
        getDefaultAudioFileIO().writeFile(f);
    }

    public AudioFileIO()
    {
        this.prepareReadersAndWriters();
    }

    public void addAudioFileModificationListener(AudioFileModificationListener listener)
    {
        this.modificationHandler.addAudioFileModificationListener(listener);
    }

    public void deleteTag(AudioFile f) throws CannotReadException, CannotWriteException
    {
        String ext = Utils.getExtension(f.getFile());
        Object afw = this.writers.get(ext);
        if (afw == null)
        {
            throw new CannotWriteException(ErrorMessage.NO_DELETER_FOR_THIS_FORMAT.getMsg(new Object[] { ext }));
        }
        else
        {
            ((AudioFileWriter) afw).delete(f);
        }
    }

    private void prepareReadersAndWriters()
    {
        this.readers.put(SupportedFileFormat.OGG.getFilesuffix(), new OggFileReader());
        this.readers.put(SupportedFileFormat.FLAC.getFilesuffix(), new FlacFileReader());
        this.readers.put(SupportedFileFormat.MP3.getFilesuffix(), new MP3FileReader());
        this.readers.put(SupportedFileFormat.MP4.getFilesuffix(), new Mp4FileReader());
        this.readers.put(SupportedFileFormat.M4A.getFilesuffix(), new Mp4FileReader());
        this.readers.put(SupportedFileFormat.M4P.getFilesuffix(), new Mp4FileReader());
        this.readers.put(SupportedFileFormat.M4B.getFilesuffix(), new Mp4FileReader());
        this.readers.put(SupportedFileFormat.WAV.getFilesuffix(), new WavFileReader());
        this.readers.put(SupportedFileFormat.WMA.getFilesuffix(), new AsfFileReader());
        // ArkCase changes BEGIN
        this.readers.put(SupportedFileFormat.TMP.getFilesuffix(), new WavFileReader());
        // ArkCase changes END
        RealFileReader realReader = new RealFileReader();
        this.readers.put(SupportedFileFormat.RA.getFilesuffix(), realReader);
        this.readers.put(SupportedFileFormat.RM.getFilesuffix(), realReader);
        this.writers.put(SupportedFileFormat.OGG.getFilesuffix(), new OggFileWriter());
        this.writers.put(SupportedFileFormat.FLAC.getFilesuffix(), new FlacFileWriter());
        this.writers.put(SupportedFileFormat.MP3.getFilesuffix(), new MP3FileWriter());
        this.writers.put(SupportedFileFormat.MP4.getFilesuffix(), new Mp4FileWriter());
        this.writers.put(SupportedFileFormat.M4A.getFilesuffix(), new Mp4FileWriter());
        this.writers.put(SupportedFileFormat.M4P.getFilesuffix(), new Mp4FileWriter());
        this.writers.put(SupportedFileFormat.M4B.getFilesuffix(), new Mp4FileWriter());
        this.writers.put(SupportedFileFormat.WAV.getFilesuffix(), new WavFileWriter());
        this.writers.put(SupportedFileFormat.WMA.getFilesuffix(), new AsfFileWriter());
        Iterator<AudioFileWriter> it = this.writers.values().iterator();
        Iterator i$ = this.writers.values().iterator();

        while (i$.hasNext())
        {
            AudioFileWriter curr = (AudioFileWriter) i$.next();
            curr.setAudioFileModificationListener(this.modificationHandler);
        }

    }

    public AudioFile readFile(File f)
            throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException
    {
        this.checkFileExists(f);
        String ext = Utils.getExtension(f);
        AudioFileReader afr = (AudioFileReader) this.readers.get(ext);
        if (afr == null)
        {
            throw new CannotReadException(ErrorMessage.NO_READER_FOR_THIS_FORMAT.getMsg(new Object[] { ext }));
        }
        else
        {
            return afr.read(f);
        }
    }

    public void checkFileExists(File file) throws FileNotFoundException
    {
        logger.info("Reading file:path" + file.getPath() + ":abs:" + file.getAbsolutePath());
        if (!file.exists())
        {
            logger.severe("Unable to find:" + file.getPath());
            throw new FileNotFoundException(ErrorMessage.UNABLE_TO_FIND_FILE.getMsg(new Object[] { file.getPath() }));
        }
    }

    public void removeAudioFileModificationListener(AudioFileModificationListener listener)
    {
        this.modificationHandler.removeAudioFileModificationListener(listener);
    }

    public void writeFile(AudioFile f) throws CannotWriteException
    {
        String ext = Utils.getExtension(f.getFile());
        AudioFileWriter afw = (AudioFileWriter) this.writers.get(ext);
        if (afw == null)
        {
            throw new CannotWriteException(ErrorMessage.NO_WRITER_FOR_THIS_FORMAT.getMsg(new Object[] { ext }));
        }
        else
        {
            afw.write(f);
        }
    }
}
