package com.armedia.acm.services.email.smtp;

/*-
 * #%L
 * ACM Service: Email SMTP
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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.activation.DataSource;
import javax.activation.MimetypesFileTypeMap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class InputStreamDataSource implements DataSource
{

    private final Logger LOG = LogManager.getLogger(getClass());
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private final String name;

    public InputStreamDataSource(InputStream inputStream, String name)
    {
        this.name = name;
        try
        {
            int nRead;
            byte[] data = new byte[16384];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1)
            {
                buffer.write(data, 0, nRead);
            }
            inputStream.close();
            buffer.flush();
        }
        catch (IOException e)
        {
            LOG.error("Problem while adding attachment...", e);
        }

    }

    @Override
    public String getContentType()
    {
        return new MimetypesFileTypeMap().getContentType(name);
    }

    @Override
    public InputStream getInputStream() throws IOException
    {
        return new ByteArrayInputStream(buffer.toByteArray());
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public OutputStream getOutputStream() throws IOException
    {
        throw new IOException("Read-only data");
    }

}
