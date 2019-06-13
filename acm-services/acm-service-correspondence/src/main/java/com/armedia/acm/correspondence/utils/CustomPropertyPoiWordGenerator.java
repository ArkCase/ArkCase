package com.armedia.acm.correspondence.utils;

/*-
 * #%L
 * ACM Service: Correspondence Library
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

import org.apache.poi.POIXMLProperties;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.openxmlformats.schemas.officeDocument.x2006.customProperties.CTProperty;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by armdev on 12/11/14.
 */
public class CustomPropertyPoiWordGenerator implements PoiWordGenerator
{
    private transient final Logger log = LogManager.getLogger(getClass());

    /**
     * Generate the Word document via setting custom Word property values. The template must have custom
     * properties, one property for each template substitution variable. Unfortunately the user must be prompted to
     * refresh document fields before these new property values are visible. If they are not prompted, or if they
     * choose to click "no" instead of "yes", they see the variable names, not the values. This is a bad user
     * experience. So even though this approach maintains all formatting, we can't use it.
     *
     */
    @Override
    public void generate(Resource wordTemplate, OutputStream targetStream, Map<String, String> substitutions) throws IOException
    {
        XWPFDocument template;

        template = new XWPFDocument(wordTemplate.getInputStream());
        POIXMLProperties xmlProps = template.getProperties();

        for (Map.Entry<String, String> sub : substitutions.entrySet())
        {
            if (xmlProps.getCustomProperties().contains(sub.getKey()))
            {
                List<CTProperty> props = xmlProps.getCustomProperties().getUnderlyingProperties().getPropertyList();
                for (CTProperty prop : props)
                {
                    if (prop.getName().equals(sub.getKey()))
                    {
                        prop.setLpwstr(sub.getValue());
                    }
                }
            }
        }

        // need this line to enforce that fields are updated when user opens the doc. This is a sub-optimal UI.
        template.enforceUpdateFields();

        log.debug("writing correspondence to stream: " + targetStream);

        try
        {
            template.write(targetStream);
            targetStream.flush();
        }
        finally
        {
            try
            {
                targetStream.close();
            }
            catch (IOException e)
            {
                // could not close the file, not the end of the world
            }
        }

    }
}
