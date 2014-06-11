package com.armedia.acm.plugins.complaint.web.api;

import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.service.ComplaintEventPublisher;
import com.armedia.acm.plugins.complaint.service.SaveComplaintTransaction;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * Created by armdev on 4/29/14.
 */
public class SaveComplaintDetailsAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;

    private SaveComplaintDetailsAPIController unit;
    private ComplaintEventPublisher mockEventPublisher;
    private ComplaintDao mockComplaintDao;
    private SaveComplaintTransaction mockSaveComplaintTransaction;
    private Authentication mockAuthentication;

    private Logger log = LoggerFactory.getLogger(getClass());

    ObjectMapper dateMapper = new ObjectMapper();

    @Before
    public void setUp() throws Exception
    {
        unit = new SaveComplaintDetailsAPIController();

        dateMapper.setSerializationConfig(dateMapper.getSerializationConfig().without(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS));

        MappingJacksonHttpMessageConverter messageConverter = new MappingJacksonHttpMessageConverter();
        messageConverter.setObjectMapper(dateMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(unit).setMessageConverters(messageConverter).build();

        mockEventPublisher = createMock(ComplaintEventPublisher.class);
        mockComplaintDao = createMock(ComplaintDao.class);
        mockSaveComplaintTransaction = createMock(SaveComplaintTransaction.class);
        mockAuthentication = createMock(Authentication.class);

        unit.setComplaintDao(mockComplaintDao);
        unit.setEventPublisher(mockEventPublisher);
        unit.setComplaintTransaction(mockSaveComplaintTransaction);
    }

    @Test
    public void updateComplaintDetails() throws Exception
    {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

        // truncate time from today's date
        Date today = df.parse(df.format(new Date()));

        Complaint in = new Complaint();
        in.setComplaintType("complaintType");
        in.setIncidentDate(today);
        in.setPriority("expedite");
        in.setDetails("<strong>html text</strong");
        in.setComplaintTitle("title");

        Long complaintId = 500L;

        Complaint fromDao = new Complaint();
        fromDao.setComplaintId(complaintId);
        fromDao.setComplaintNumber("20140429_500");
        fromDao.setEcmFolderId("ecmFolderId");

        Complaint toMuleFlow = new Complaint();
        toMuleFlow.setComplaintId(fromDao.getComplaintId());
        toMuleFlow.setComplaintType(in.getComplaintType());
        toMuleFlow.setComplaintNumber(fromDao.getComplaintNumber());
        toMuleFlow.setPriority(in.getPriority());
        toMuleFlow.setComplaintTitle(in.getComplaintTitle());
        toMuleFlow.setDetails(in.getDetails());
        toMuleFlow.setEcmFolderId(fromDao.getEcmFolderId());
        toMuleFlow.setIncidentDate(in.getIncidentDate());

        Capture<Complaint> found = new Capture<>();

        expect(mockComplaintDao.find(Complaint.class, complaintId)).andReturn(fromDao);
        expect(mockSaveComplaintTransaction.saveComplaint(capture(found), eq(mockAuthentication))).andReturn(toMuleFlow);
        mockEventPublisher.publishComplaintEvent(eq(toMuleFlow), eq(mockAuthentication), eq(false), eq(true));
        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        replayAll();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(df);
        String inJson = objectMapper.writeValueAsString(in);

        log.debug("Incoming JSON: " + inJson);

        MvcResult result = mockMvc.perform(
                post("/api/latest/plugin/complaint/details/" + complaintId)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .principal(mockAuthentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inJson))
                .andReturn();

        log.info("results: " + result.getResponse().getContentAsString());

        verifyAll();

        Complaint sentToMule = found.getValue();
        assertEquals(toMuleFlow.getComplaintId(), sentToMule.getComplaintId());
        assertEquals(toMuleFlow.getComplaintNumber(), sentToMule.getComplaintNumber());
        assertEquals(toMuleFlow.getComplaintTitle(), sentToMule.getComplaintTitle());
        assertEquals(toMuleFlow.getComplaintType(), sentToMule.getComplaintType());
        assertEquals(toMuleFlow.getIncidentDate(), sentToMule.getIncidentDate());
        assertEquals(toMuleFlow.getDetails(), sentToMule.getDetails());
        assertEquals(toMuleFlow.getPriority(), sentToMule.getPriority());
        assertEquals(toMuleFlow.getComplaintId(), sentToMule.getComplaintId());

    }
}
