package com.armedia.acm.correspondence.utils;

/*-
 * #%L
 * ACM Service: Correspondence Library
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

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author darko.dimitrievski
 */
public interface SpELWordEvaluator
{
    /**
     * Generating correspondence template, where the merge fields values are mapped by the SPEL expressions generated results
     *
     * @param wordTemplate - the correspondence template
     * @param targetStream
     * @param objectType
     * @param parentObjectId
     *
     * @throws IOException
     */
    void generate(Resource wordTemplate, OutputStream targetStream, String objectType, Long parentObjectId) throws IOException;
}
