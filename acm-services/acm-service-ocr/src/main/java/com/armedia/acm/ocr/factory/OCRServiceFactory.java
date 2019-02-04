package com.armedia.acm.ocr.factory;

/*-
 * #%L
 * ACM Services: Optical character recognition via Tesseract
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

import com.armedia.acm.ocr.exception.OCRServiceProviderNotFoundException;
import com.armedia.acm.ocr.model.OCRServiceProvider;
import com.armedia.acm.ocr.service.OCRService;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class OCRServiceFactory
{
    private OCRService tesseractOCRService;

    public OCRService getService(OCRServiceProvider provider) throws OCRServiceProviderNotFoundException
    {
        switch (provider)
        {
        case TESSERACT:
            return tesseractOCRService;
        }

        throw new OCRServiceProviderNotFoundException(
                String.format("Provider [%s] not found.", provider != null ? provider.toString() : null));
    }

    public void setTesseractOCRService(OCRService tesseractOCRService)
    {
        this.tesseractOCRService = tesseractOCRService;
    }
}
