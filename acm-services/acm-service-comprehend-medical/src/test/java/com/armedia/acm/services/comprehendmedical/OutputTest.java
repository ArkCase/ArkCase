package com.armedia.acm.services.comprehendmedical;

/*-
 * #%L
 * ACM Service: Comprehend Medical
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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


import com.armedia.acm.services.comprehendmedical.model.ComprehendMedicalEntity;
import com.armedia.acm.services.comprehendmedical.utils.ComprehendMedicalUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class OutputTest
{
    @Test
    public void ParseOutput() throws Exception
    {
        String output = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("aws/output/comprehendMedicalOutput.txt"));

        List<ComprehendMedicalEntity> entities = ComprehendMedicalUtils.getEntitiesFromOutput(output);

        Assert.assertNotNull(entities);
        Assert.assertEquals(1, entities.size());
        Assert.assertNotNull(entities.get(0).getAttributes());
        Assert.assertEquals(5, entities.get(0).getAttributes().size());
        Assert.assertEquals("MEDICATION", entities.get(0).getCategory());
        Assert.assertEquals("2 times", entities.get(0).getAttributes().get(3).getText());
    }
}
