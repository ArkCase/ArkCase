package com.armedia.acm.plugins.dashboard.service;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.armedia.acm.plugins.dashboard.dao.DashboardDao;
import com.armedia.acm.plugins.dashboard.dao.WidgetDao;
import com.armedia.acm.plugins.dashboard.model.Dashboard;
import com.armedia.acm.plugins.dashboard.model.DashboardDto;
import com.armedia.acm.plugins.dashboard.model.widget.Widget;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.service.AcmUserRoleService;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

/**
 * Created by marst on 5/23/16.
 */
public class DashboardServiceTest extends EasyMockSupport
{

    private DashboardService unit;

    private DashboardDao mockDashboardDao;
    private UserDao mockUserDao;
    private WidgetDao mockWidgetDao;
    private AcmUserRoleService mockUserRoleService;
    private DashboardPropertyReader mockDashboardPropertyReader;

    @Before
    public void setUp() throws Exception
    {

        unit = new DashboardService();

        mockDashboardDao = createMock(DashboardDao.class);
        mockUserDao = createMock(UserDao.class);
        mockWidgetDao = createMock(WidgetDao.class);
        mockDashboardPropertyReader = createMock(DashboardPropertyReader.class);
        mockUserRoleService = createMock(AcmUserRoleService.class);

        unit.setDashboardDao(mockDashboardDao);
        unit.setUserDao(mockUserDao);
        unit.setWidgetDao(mockWidgetDao);
        unit.setDashboardPropertyReader(mockDashboardPropertyReader);
        unit.setUserRoleService(mockUserRoleService);
    }

    @Test
    public void prepareDashboardStringBasedOnUserRolesTest() throws Exception
    {
        String dashboardConfigString = "{\"title\":\"Dashboard\",\"structure\":\"6-6\",\"rows\":[{\"columns\":[{\"styleClass\":\"col-md-6\",\"widgets\":[{\"type\":\"myTasks\",\"config\":{},\"title\":\"My Tasks\",\"titleTemplateUrl\":\"../src/templates/widget-title.html\",\"wid\":3}],\"cid\":5},{\"styleClass\":\"col-md-6\",\"widgets\":[{\"type\":\"newComplaints\",\"config\":{},\"title\":\"New Complaints\",\"titleTemplateUrl\":\"../src/templates/widget-title.html\",\"wid\":10},{\"type\":\"myComplaints\",\"config\":{},\"title\":\"My Complaints\",\"titleTemplateUrl\":\"../src/templates/widget-title.html\",\"wid\":9},{\"type\":\"myCases\",\"config\":{},\"title\":\"My Cases\",\"titleTemplateUrl\":\"../src/templates/widget-title.html\",\"wid\":4}],\"cid\":6}]}],\"titleTemplateUrl\":\"modules/dashboard/views/dashboard-title.client.view.html\"}";
        String expectedDashboardConfigStringAfterRemovingWidgets = "{\"titleTemplateUrl\":\"modules/dashboard/views/dashboard-title.client.view.html\",\"title\":\"Dashboard\",\"rows\":[{\"columns\":[{\"styleClass\":\"col-md-6\",\"widgets\":[],\"cid\":5},{\"styleClass\":\"col-md-6\",\"widgets\":[],\"cid\":6}]}],\"structure\":\"6-6\"}";

        AcmUser user = new AcmUser();
        user.setUserId("marst");

        Dashboard dashboard = new Dashboard();
        dashboard.setDashboardConfig(dashboardConfigString);
        dashboard.setDashboardOwner(user);
        dashboard.setCollapsed(false);

        Dashboard expectedDashboard = new Dashboard();
        expectedDashboard.setDashboardConfig(expectedDashboardConfigStringAfterRemovingWidgets);
        expectedDashboard.setDashboardOwner(user);
        expectedDashboard.setCollapsed(false);

        DashboardDto expectedDashboardDto = new DashboardDto();
        expectedDashboardDto.setDashboardConfig(expectedDashboard.getDashboardConfig());
        expectedDashboardDto.setCollapsed(false);
        expectedDashboardDto.setModule("DASHBOARD");
        expectedDashboardDto.setUserId(user.getUserId());

        Set<String> roles = new HashSet<>(Arrays.asList("ACM_MARST"));

        Widget news = new Widget();
        news.setWidgetId(1201L);
        news.setWidgetName("news");

        Widget teamWorkload = new Widget();
        teamWorkload.setWidgetId(1212L);
        teamWorkload.setWidgetName("teamWorkload");

        Widget casesByQueue = new Widget();
        casesByQueue.setWidgetId(1209L);
        casesByQueue.setWidgetName("casesByQueue");

        List<Widget> widgetsByUserRoles = new ArrayList<>();
        widgetsByUserRoles.add(news);
        widgetsByUserRoles.add(teamWorkload);
        widgetsByUserRoles.add(casesByQueue);

        List<Widget> listOfDashboardWidgetOnly = new ArrayList<>();
        listOfDashboardWidgetOnly.add(news);
        listOfDashboardWidgetOnly.add(teamWorkload);
        listOfDashboardWidgetOnly.add(casesByQueue);

        expect(mockUserDao.findByUserId(user.getUserId())).andReturn(user);
        expect(mockDashboardDao.getDashboardConfigForUserAndModuleName(user, "DASHBOARD")).andReturn(dashboard).anyTimes();
        expect(mockUserRoleService.getUserRoles(user.getUserId())).andReturn(roles);
        expect(mockWidgetDao.getAllWidgetsByRoles(roles)).andReturn(widgetsByUserRoles);
        expect(mockDashboardPropertyReader.getDashboardWidgetsOnly()).andReturn(listOfDashboardWidgetOnly);
        expect(mockDashboardDao.setDashboardConfigForUserAndModule(isA(AcmUser.class), isA(DashboardDto.class), isA(String.class)))
                .andReturn(1);

        replayAll();

        Dashboard result = unit.prepareDashboardStringBasedOnUserRoles(user.getUserId(), "DASHBOARD");

        verifyAll();

        assertNotNull(result);
        assertEquals(result.getDashboardConfig(), expectedDashboardConfigStringAfterRemovingWidgets);

    }

}
