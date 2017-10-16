package com.armedia.acm.plugins.dashboard.service;


import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class DashboardPropertyReaderTest
{
    private static final String WIDGET_TO_REMOVE = "correspondence";
    private DashboardPropertyReader dashboardPropertyReader;

    @Before
    public void setUp()
    {
        dashboardPropertyReader = new DashboardPropertyReader();
    }

    @Test
    public void testRemoveWidgetFromDashboardConfigWhenEmptyColumns()
    {

        JSONObject jsonConfig = new JSONObject();

        JSONArray rows = new JSONArray();
        JSONArray columns = new JSONArray();
        JSONObject row1 = new JSONObject();
        row1.put("columns", columns);
        JSONObject row2 = new JSONObject();
        row2.put("columns", columns);
        rows.put(0, row1);
        rows.put(1, row2);
        jsonConfig.put("rows", rows);

        String updatedConfig = dashboardPropertyReader.removeWidgetFromDashboardConfig(jsonConfig.toString(),
                WIDGET_TO_REMOVE);

        testIfWidgetIsRemoved(updatedConfig);
    }

    @Test
    public void testRemoveWidgetFromDashboardConfigWhenNoColumns()
    {

        JSONObject jsonConfig = new JSONObject();

        JSONArray rows = new JSONArray();
        JSONObject row1 = new JSONObject();
        row1.put("style", "");
        JSONObject row2 = new JSONObject();
        row2.put("style", "");
        rows.put(0, row1);
        rows.put(1, row2);
        jsonConfig.put("rows", rows);

        // test if exception is properly handled
        dashboardPropertyReader.removeWidgetFromDashboardConfig(jsonConfig.toString(),
                WIDGET_TO_REMOVE);
    }

    @Test
    public void testRemoveWidgetFromDashboardConfig()
    {
        JSONArray widgets = new JSONArray();
        JSONObject referenceWidget = new JSONObject();
        referenceWidget.put("type", "references");
        JSONObject correspondenceWidget = new JSONObject();
        correspondenceWidget.put("type", WIDGET_TO_REMOVE);
        JSONObject costWidget = new JSONObject();
        costWidget.put("type", "costsheet");
        widgets.put(0, referenceWidget);
        widgets.put(1, correspondenceWidget);
        widgets.put(2, costWidget);

        JSONObject jsonConfig = new JSONObject();

        JSONArray columns = new JSONArray();
        JSONObject column1 = new JSONObject();
        column1.put("widgets", widgets);
        columns.put(0, column1);
        JSONObject column2 = new JSONObject();
        column2.put("widgets", widgets);
        columns.put(1, column2);

        JSONObject row1 = new JSONObject();
        row1.put("columns", columns);
        JSONObject row2 = new JSONObject();
        row2.put("columns", columns);

        JSONArray rows = new JSONArray();
        rows.put(0, row1);
        rows.put(1, row2);

        jsonConfig.put("rows", rows);

        String updatedConfig = dashboardPropertyReader.removeWidgetFromDashboardConfig(jsonConfig.toString(),
                WIDGET_TO_REMOVE);

        testIfWidgetIsRemoved(updatedConfig);

    }

    public void testIfWidgetIsRemoved(String config)
    {
        JSONObject updatedJsonConfig = new JSONObject(config);
        JSONArray updatedRows = updatedJsonConfig.getJSONArray("rows");
        for (int i = 0; i < updatedRows.length(); i++)
        {
            JSONArray updatedColumns = updatedRows.getJSONObject(i).getJSONArray("columns");
            for (int j = 0; j < updatedColumns.length(); j++)
            {
                JSONArray updatedWidgets = updatedColumns.getJSONObject(j).getJSONArray("widgets");
                for (int k = 0; k < updatedWidgets.length(); k++)
                {
                    JSONObject widget = updatedWidgets.getJSONObject(k);
                    assertFalse(widget.has(WIDGET_TO_REMOVE));
                }
            }
        }
    }
}
