package com.armedia.acm.web.api;

/*-
 * #%L
 * ACM Shared Web Artifacts
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

import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;
import org.springframework.web.servlet.resource.VersionedResource;

import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.List;

public class PathResourceResolverWithETag extends PathResourceResolver
{

    @Override
    public Resource resolveResource(HttpServletRequest request, String requestPath, List<? extends Resource> locations,
            ResourceResolverChain chain)
    {
        return new HashVersionedResource(super.resolveResource(request, requestPath, locations, chain));
    }

    public static class HashVersionedResource implements VersionedResource
    {

        private Resource resource;

        public HashVersionedResource(Resource resource)
        {
            this.resource = resource;
        }

        @Override
        public String getVersion()
        {
            try (InputStream is = resource.getInputStream())
            {
                return org.apache.commons.codec.digest.DigestUtils.md5Hex(is);

            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }

        @Override
        public boolean exists()
        {
            return resource.exists();
        }

        @Override
        public boolean isReadable()
        {
            return resource.isReadable();
        }

        @Override
        public boolean isOpen()
        {
            return resource.isOpen();
        }

        @Override
        public URL getURL() throws IOException
        {
            return resource.getURL();
        }

        @Override
        public URI getURI() throws IOException
        {
            return resource.getURI();
        }

        @Override
        public File getFile() throws IOException
        {
            return resource.getFile();
        }

        @Override
        public long contentLength() throws IOException
        {
            return resource.contentLength();
        }

        @Override
        public long lastModified() throws IOException
        {
            return resource.lastModified();
        }

        @Override
        public Resource createRelative(String s) throws IOException
        {
            return resource.createRelative(s);
        }

        @Override
        public String getFilename()
        {
            return resource.getFilename();
        }

        @Override
        public String getDescription()
        {
            return resource.getDescription();
        }

        @Override
        public InputStream getInputStream() throws IOException
        {
            return resource.getInputStream();
        }
    }
}
