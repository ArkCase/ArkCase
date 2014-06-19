package com.armedia.acm.services.search;

import com.armedia.acm.services.search.service.AcmQuickSearchJpaSolrGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-search-plugin-test.xml",
        "/spring/spring-library-activemq.xml"
})
public class SearchDocumentFactoryTest
{

    @Autowired
    @Qualifier("testComplaintQuicksearchGenerator")
    private AcmQuickSearchJpaSolrGenerator complaintGenerator;

    @Test
    public void complaintGenerator() throws Exception
    {
        //complaintGenerator.batchSolrUpdate();

        // wait a while, see if the batch scheduled update runs
        Thread.sleep(60000);

    }
}
