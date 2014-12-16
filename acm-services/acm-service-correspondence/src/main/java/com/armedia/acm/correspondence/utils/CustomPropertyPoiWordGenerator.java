package com.armedia.acm.correspondence.utils;

import org.apache.poi.POIXMLProperties;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.openxmlformats.schemas.officeDocument.x2006.customProperties.CTProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Generate the Word document via setting custom Word property values.  The template must have custom
     * properties, one property for each template substitution variable.  Unfortunately the user must be prompted to
     * refresh document fields before these new property values are visible.  If they are not prompted, or if they
     * choose to click "no" instead of "yes", they see the variable names, not the values.  This is a bad user
     * experience.  So even though this approach maintains all formatting, we can't use it.
     *
     */
    @Override
    public void generate(Resource wordTemplate, OutputStream targetStream, Map<String, String> substitutions) throws IOException
    {
        XWPFDocument template;

        template = new XWPFDocument(wordTemplate.getInputStream());
        POIXMLProperties xmlProps = template.getProperties();

        for ( Map.Entry<String, String> sub : substitutions.entrySet() )
        {
            if ( xmlProps.getCustomProperties().contains(sub.getKey()) )
            {
                List<CTProperty> props = xmlProps.getCustomProperties().getUnderlyingProperties().getPropertyList();
                for ( CTProperty prop : props )
                {
                    if ( prop.getName().equals(sub.getKey()))
                    {
                        prop.setLpwstr(sub.getValue());
                    }
                }
            }
        }

        // need this line to enforce that fields are updated when user opens the doc.  This is a sub-optimal UI.
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
