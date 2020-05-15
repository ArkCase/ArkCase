package com.armedia.acm.services.comprehendmedical;


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
