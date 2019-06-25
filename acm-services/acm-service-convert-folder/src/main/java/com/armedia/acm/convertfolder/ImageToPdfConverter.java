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
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity May 8, 2018
 *
 */
public class ImageToPdfConverter extends PdfConverterBase implements FileConverter
{

    private static class BoundingBox
    {
        private final float width;
        private final float height;

        BoundingBox(float width, float height)
        {
            this.width = width;
            this.height = height;
        }

        /**
         * @return the width
         */
        public float getWidth()
        {
            return width;
        }

        /**
         * @return the height
         */
        public float getHeight()
        {
            return height;
        }

        public static BoundingBox scaleIfNecessary(BoundingBox container, BoundingBox contained)
        {
            if (container.getWidth() >= contained.getWidth() && container.getHeight() >= contained.getHeight())
            {
                return contained;
            }
            else if (contained.getWidth() - container.getWidth() > contained.getHeight() - container.getHeight())
            {
                float scale = container.getWidth() / contained.getWidth();
                return new BoundingBox(contained.getWidth() * scale, contained.getHeight() * scale);
            }
            else
            {
                float scale = container.getHeight() / contained.getHeight();
                return new BoundingBox(contained.getWidth() * scale, contained.getHeight() * scale);
            }
        }

    }

    private static final List<String> SUPPORTED_TYPES_EXTENSION = Collections
            .unmodifiableList(Arrays.asList("jpg", "jpeg", "tif", "tiff", "gif", "bmp", "png"));

    /**
     * Logger instance.
     */
    private Logger log = LogManager.getLogger(getClass());

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.convertfolder.FileConverter#getSupportedTypesExtensions()
     */
    @Override
    public List<String> getSupportedTypesExtensions()
    {
        return SUPPORTED_TYPES_EXTENSION;
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
        try (PDDocument doc = new PDDocument())
        {
            PDPage page = new PDPage();
            doc.addPage(page);

            PDImageXObject image = PDImageXObject.createFromFileByContent(tempOriginFile, doc);

            try (PDPageContentStream contents = new PDPageContentStream(doc, page))
            {
                BoundingBox size = BoundingBox.scaleIfNecessary(
                        new BoundingBox(page.getMediaBox().getWidth(), page.getMediaBox().getHeight()),
                        new BoundingBox(image.getWidth(), image.getHeight()));
                contents.drawImage(image, 0, page.getMediaBox().getHeight() - size.getHeight(), size.getWidth(), size.getHeight());
            }
            doc.save(tempPdfFile);
        }
        catch (IOException e)
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
