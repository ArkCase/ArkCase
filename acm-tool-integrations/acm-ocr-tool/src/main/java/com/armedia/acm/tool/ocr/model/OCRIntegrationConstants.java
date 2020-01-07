package com.armedia.acm.tool.ocr.model;

/*-
 * #%L
 * acm-ocr-tool
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

/**
 * Created by Vladimir Cherepnalkovski
 */
public interface OCRIntegrationConstants
{
    String OBJECT_TYPE = "OCR";
    String SERVICE = "OCR";
    String TESSERACT_SERVICE = "TESSERACT_OCR";
    String OCR_SYSTEM_USER = "OCR_SERVICE";

    String TMP_DIR = "java.io.tmpdir";

    String TESSERACT_COMMAND_PREFIX = "tesseract";
    String QPDF = "qpdf";
    String IMAGE_MAGICK = "magick convert";

    String MEDIA_TYPE_PDF_RECOGNITION_KEY = "application/pdf";

    String OCR_FILE_PREFIX = "ocr-";
    String TEMP_FILE_PDF_SUFFIX = ".pdf";
    String TEMP_FILE_PNG_SUFFIX = ".png";

    String MAP_PROP_FILE_ID = "fileId";

    String QPDF_TMP = "QPDF_TMP";
    String TESSERACT_TMP = "TESSERACT_TMP";
    String MAGICK_TMP = "MAGICK_TMP";
    String UPLOADED_TMP = "UPLOADED_TMP";
    String PROCESS_STATUS_TMP = "PROCESS_STATUS_TMP";
}
