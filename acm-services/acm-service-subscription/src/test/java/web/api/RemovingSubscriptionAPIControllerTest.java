package web.api;

import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.services.subscription.dao.SubscriptionDao;
import com.armedia.acm.services.subscription.service.SubscriptionEventPublisher;
import com.armedia.acm.services.subscription.web.api.RemovingSubscriptionAPIController;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

/**
 * Created by marjan.stefanoski on 12.02.2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-subscription-web-api-test.xml"
})
public class RemovingSubscriptionAPIControllerTest extends EasyMockSupport {
    private MockMvc mockMvc;
    private Authentication mockAuthentication;
    private RemovingSubscriptionAPIController mockRemovingSubscriptionAPIController;
    private SubscriptionDao mockSubscriptionDao;
    private AcmPlugin mockSubscriptionPlugin;
    private SubscriptionEventPublisher mockSubscriptionEventPublisher;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception {

        mockRemovingSubscriptionAPIController = new RemovingSubscriptionAPIController();

        mockSubscriptionDao = createMock(SubscriptionDao.class);
        mockSubscriptionPlugin = createMock(AcmPlugin.class);
        mockSubscriptionEventPublisher = createMock(SubscriptionEventPublisher.class);

        mockRemovingSubscriptionAPIController.setSubscriptionDao(mockSubscriptionDao);
        mockRemovingSubscriptionAPIController.setSubscriptionPlugin(mockSubscriptionPlugin);
        mockRemovingSubscriptionAPIController.setSubscriptionEventPublisher(mockSubscriptionEventPublisher);

        mockMvc = MockMvcBuilders.standaloneSetup(mockRemovingSubscriptionAPIController).setHandlerExceptionResolvers(exceptionResolver).build();
        mockAuthentication = createMock(Authentication.class);
    }

    @Test
    @Ignore
    public void removeSubscription() throws Exception {

    }
    public MockMvc getMockMvc() {
        return mockMvc;
    }

    public void setMockMvc(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    public Authentication getMockAuthentication() {
        return mockAuthentication;
    }

    public void setMockAuthentication(Authentication mockAuthentication) {
        this.mockAuthentication = mockAuthentication;
    }

    public ExceptionHandlerExceptionResolver getExceptionResolver() {
        return exceptionResolver;
    }

    public void setExceptionResolver(ExceptionHandlerExceptionResolver exceptionResolver) {
        this.exceptionResolver = exceptionResolver;
    }
}
