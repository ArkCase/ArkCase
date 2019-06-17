package com.armedia.acm.convertfolder;

/*-
 * #%L
 * ACM Service: Folder Converting Service
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

import com.armedia.acm.plugins.ecm.model.EcmFile;

import org.apache.commons.io.FileUtils;
import org.docx4j.Docx4J;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.model.fields.FieldUpdater;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Apr 26, 2018
 *
 */
public class DocxToPdfConverter extends PdfConverterBase implements FileConverter
{

    private static final List<String> SUPPORTED_TYPES_EXTENSIONS = Collections.unmodifiableList(Arrays.asList("docx"));

    /**
     * Logger instance.
     */
    private Logger log = LogManager.getLogger(getClass());

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.convertfolder.FileConverter#getTypeExtensions()
     */
    @Override
    public List<String> getSupportedTypesExtensions()
    {
        return SUPPORTED_TYPES_EXTENSIONS;
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.convertfolder.PdfConverterBase#performConversion(java.lang.String, java.io.File,
     * java.io.File)
     */
    @Override
    protected void performConversion(EcmFile file, File tempOriginFile, File tempPdfFile)
            throws ConversionException
    {
        try (InputStream fileByteStream = new FileInputStream(tempOriginFile))
        {
            WordprocessingMLPackage wordProcessor = WordprocessingMLPackage.load(fileByteStream);
            FieldUpdater updater = new FieldUpdater(wordProcessor);
            updater.update(true);

            try (OutputStream fos = new FileOutputStream(tempPdfFile))
            {
                FOSettings settings = Docx4J.createFOSettings();
                settings.setWmlPackage(wordProcessor);
                Docx4J.toFO(settings, fos, Docx4J.FLAG_EXPORT_PREFER_XSL);
            }

        }
        catch (IOException | Docx4JException e)
        {
            FileUtils.deleteQuietly(tempPdfFile);
            String fileName = file.getFileName() + "." + file.getFileExtension();
            log.warn("Failed to convert file [{}] with id [{}] of type [{}].", fileName, file.getId(), file.getFileExtension(), e);
            throw new ConversionException(String.format("Failed to convert file [%s] with id [%s] of type [%s].", fileName, file.getId(),
                    file.getFileExtension()), e);
        }
        finally
        {
            FileUtils.deleteQuietly(tempOriginFile);
        }
    }

}
