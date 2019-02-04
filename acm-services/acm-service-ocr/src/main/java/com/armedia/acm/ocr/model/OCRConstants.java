package com.armedia.acm.ocr.model;

/*-
 * #%L
 * ACM Service: OCR
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
 * Created by Vladimir Cherepnalkovski
 */
public interface OCRConstants {

    String OBJECT_TYPE = "OCR";
    String SERVICE = "OCR";
    String TESSERACT_SERVICE = "TESSERACT_OCR";

    String TESSERACT_COMMAND_PREFIX = "tesseract";
    String QPDF = "qpdf";
    String IMAGE_MAGICK = "magick convert";

    String MEDIA_TYPE_PDF_RECOGNITION_KEY = "application/pdf";
    String MEDIA_TYPE_IMAGE_RECOGNITION_KEY = "image/";

    String OCR_SYSTEM_USER = "OCR_SERVICE";
    String OCR_SYSTEM_IP_ADDRESS = "127.0.0.1";

    String TEMP_FILE_PREFIX = "ocr-";
    String TEMP_FILE_PDF_SUFFIX = ".pdf";
    String TEMP_FILE_PNG_SUFFIX = ".png";

    String QPDF_TMP = "QPDF_TMP";
    String TESSERACT_TMP = "TESSERACT_TMP";
    String MAGICK_TMP = "MAGICK_TMP";
    String UPLOADED_TMP = "UPLOADED_TMP";

    String OCR_CREATED_EVENT = "com.armedia.acm.ocr.created";
    String OCR_UPDATED_EVENT = "com.armedia.acm.ocr.updated";
    String OCR_QUEUED_EVENT = "com.armedia.acm.ocr.queued";
    String OCR_PROCESSING_EVENT = "com.armedia.acm.ocr.processing";
    String OCR_COMPLETED_EVENT = "com.armedia.acm.ocr.completed";
    String OCR_FAILED_EVENT = "com.armedia.acm.ocr.failed";
    String OCR_CANCELLED_EVENT = "com.armedia.acm.ocr.cancelled";
    String OCR_COMPILED_EVENT = "com.armedia.acm.ocr.compiled";
    String OCR_ROLLBACK_EVENT = "com.armedia.acm.ocr.rollback";
    String OCR_PROVIDER_FAILED_EVENT = "com.armedia.acm.ocr.provider.failed";
}
