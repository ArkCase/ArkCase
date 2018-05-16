
package org.apache.chemistry.opencmis.commons.enums.transformers;

/*-
 * #%L
 * ACM Mule CMIS Connector
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

import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.mule.api.transformer.DiscoverableTransformer;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;
import org.mule.transformer.types.DataTypeFactory;

import javax.annotation.Generated;

@Generated(value = "Mule DevKit Version 3.4.0", date = "2014-05-13T04:20:32-03:00", comments = "Build 3.4.0.1555.8df15c1")
public class UnfileObjectEnumTransformer
        extends AbstractTransformer
        implements DiscoverableTransformer
{

    private int weighting = DiscoverableTransformer.DEFAULT_PRIORITY_WEIGHTING;

    public UnfileObjectEnumTransformer()
    {
        registerSourceType(DataTypeFactory.create(String.class));
        setReturnClass(UnfileObject.class);
        setName("UnfileObjectEnumTransformer");
    }

    protected Object doTransform(Object src, String encoding)
            throws TransformerException
    {
        UnfileObject result = null;
        result = Enum.valueOf(UnfileObject.class, ((String) src));
        return result;
    }

    public int getPriorityWeighting()
    {
        return weighting;
    }

    public void setPriorityWeighting(int weighting)
    {
        this.weighting = weighting;
    }

}
