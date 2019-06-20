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
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.imageio.ImageIO;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity May 15, 2018
 *
 */
public class PptxToPdfConverter extends PdfConverterBase implements FileConverter
{

    private static final List<String> SUPPORTED_TYPES_EXTENSIONS = Collections.unmodifiableList(Arrays.asList("pptx"));

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
        return SUPPORTED_TYPES_EXTENSIONS;
    }

    @Override
    protected void performConversion(EcmFile file, File tempOriginFile, File tempPdfFile)
            throws ConversionException
    {
        try (XMLSlideShow ppt = new XMLSlideShow(new FileInputStream(tempOriginFile)); PDDocument doc = new PDDocument())
        {
            Dimension pgsize = ppt.getPageSize();
            List<XSLFSlide> slides = ppt.getSlides();

            for (XSLFSlide slide : slides)
            {
                PDPage page = new PDPage(new PDRectangle((float) pgsize.getWidth(), (float) pgsize.getHeight()));
                doc.addPage(page);

                BufferedImage img = new BufferedImage(pgsize.width, pgsize.height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D graphics = img.createGraphics();
                // clear the drawing area
                // graphics.setPaint(Color.white);
                graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width, pgsize.height));

                // render
                slide.draw(graphics);

                String tempUploadFolderPath = FileUtils.getTempDirectoryPath();
                File tempImageFile = new File(tempUploadFolderPath + File.separator + "slide_" + Thread.currentThread().getName());
                try (FileOutputStream fos = new FileOutputStream(tempImageFile))
                {
                    ImageIO.write(img, "png", fos);
                }
                catch (IOException e)
                {
                    FileUtils.deleteQuietly(tempImageFile);
                    continue;
                }

                PDImageXObject image = PDImageXObject.createFromFileByContent(tempImageFile, doc);

                try (PDPageContentStream contents = new PDPageContentStream(doc, page))
                {
                    contents.drawImage(image, 0, 0);
                }

                FileUtils.deleteQuietly(tempImageFile);

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
