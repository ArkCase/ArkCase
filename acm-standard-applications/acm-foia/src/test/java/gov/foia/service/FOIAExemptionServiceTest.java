package gov.foia.service;

import static org.junit.Assert.assertEquals;

import com.armedia.acm.services.exemption.model.ExemptionCode;

import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import gov.foia.dao.FOIAExemptionCodeDao;

/**
 * Created by ana.serafimoska
 */
@RunWith(EasyMockRunner.class)
public class FOIAExemptionServiceTest extends EasyMockSupport
{
    private FOIAExemptionService foiaExemptionService;
    private FOIAExemptionCodeDao foiaExemptionCodeDao;

    @Before
    public void setUp()
    {
        foiaExemptionService = new FOIAExemptionService();
        foiaExemptionCodeDao = createMock(FOIAExemptionCodeDao.class);
        foiaExemptionService.setFoiaExemptionCodeDao(foiaExemptionCodeDao);
    }

    @Test
    public void testFilterExemptionCodes()
    {
        List<ExemptionCode> exemptionCodeList = new ArrayList<>();
        ExemptionCode exemptionCode = new ExemptionCode();
        exemptionCode.setExemptionCode("Ex.1");
        exemptionCode.setExemptionStatus("APPROVED");

        ExemptionCode exemptionCode2 = new ExemptionCode();
        exemptionCode2.setExemptionCode("Ex.2");
        exemptionCode2.setExemptionStatus("MANUAL");

        ExemptionCode exemptionCode3 = new ExemptionCode();
        exemptionCode3.setExemptionCode("Ex.2");
        exemptionCode3.setExemptionStatus("APPROVED");

        ExemptionCode exemptionCode4 = new ExemptionCode();
        exemptionCode4.setExemptionCode("Ex.1");
        exemptionCode4.setExemptionStatus("MANUAL");

        ExemptionCode exemptionCode5 = new ExemptionCode();
        exemptionCode5.setExemptionCode("Ex.5");
        exemptionCode5.setExemptionStatus("MANUAL");

        ExemptionCode exemptionCode6 = new ExemptionCode();
        exemptionCode6.setExemptionCode("Ex.6");
        exemptionCode6.setExemptionStatus("APPROVED");

        exemptionCodeList.add(exemptionCode);
        exemptionCodeList.add(exemptionCode2);
        exemptionCodeList.add(exemptionCode3);
        exemptionCodeList.add(exemptionCode4);
        exemptionCodeList.add(exemptionCode5);
        exemptionCodeList.add(exemptionCode6);

        List<ExemptionCode> returnedList = foiaExemptionService.filterExemptionCodes(exemptionCodeList);

        assertEquals(4, returnedList.size());

        assertEquals("Ex.1", returnedList.get(0).getExemptionCode());
        assertEquals("APPROVED", returnedList.get(0).getExemptionStatus());

        assertEquals("Ex.2", returnedList.get(1).getExemptionCode());
        assertEquals("APPROVED", returnedList.get(1).getExemptionStatus());

        assertEquals("Ex.5", returnedList.get(2).getExemptionCode());
        assertEquals("MANUAL", returnedList.get(2).getExemptionStatus());

        assertEquals("Ex.6", returnedList.get(3).getExemptionCode());
        assertEquals("APPROVED", returnedList.get(3).getExemptionStatus());
        
    }

}