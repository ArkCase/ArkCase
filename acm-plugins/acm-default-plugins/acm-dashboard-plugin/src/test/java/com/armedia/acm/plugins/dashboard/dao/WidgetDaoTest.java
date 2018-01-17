package com.armedia.acm.plugins.dashboard.dao;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import com.armedia.acm.plugins.dashboard.model.widget.RolesGroupByWidgetDto;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by armdev on 1/20/16.
 */
public class WidgetDaoTest extends EasyMockSupport
{
    private WidgetDao unit;

    private EntityManager mockEntityManager;
    private Query mockQuery;

    @Before
    public void setUp() throws Exception
    {
        unit = new WidgetDao();

        mockEntityManager = createMock(EntityManager.class);
        mockQuery = createMock(Query.class);

        unit.setEntityManager(mockEntityManager);
    }

    @Test
    public void getRolesGroupByWidget_noWidgetRolesExistYet() throws Exception
    {
        expect(mockEntityManager.createQuery(anyObject(String.class))).andReturn(mockQuery);

        Object[] widgetWithNoRoles = new Object[2];
        widgetWithNoRoles[0] = "WIDGET";

        List<Object[]> widgets = new ArrayList<>();
        widgets.add(widgetWithNoRoles);

        expect(mockQuery.getResultList()).andReturn(widgets);

        replayAll();

        List<RolesGroupByWidgetDto> rolesByWidget = unit.getRolesGroupByWidget();

        verifyAll();

        assertEquals(1, rolesByWidget.size());
        assertEquals(0, rolesByWidget.get(0).getWidgetAuthorizedRoles().size());
        assertEquals("WIDGET", rolesByWidget.get(0).getWidgetName());
    }
}
