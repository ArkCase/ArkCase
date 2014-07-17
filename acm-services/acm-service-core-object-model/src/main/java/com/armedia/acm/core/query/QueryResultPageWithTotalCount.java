package com.armedia.acm.core.query;

import java.util.List;

/**
 * Created by armdev on 7/10/14.
 */
public class QueryResultPageWithTotalCount<T>
{
    private int totalCount;
    private int startRow;
    private int maxRows;
    private List<T> resultPage;

    public int getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }

    public int getStartRow()
    {
        return startRow;
    }

    public void setStartRow(int startRow)
    {
        this.startRow = startRow;
    }

    public int getMaxRows()
    {
        return maxRows;
    }

    public void setMaxRows(int maxRows)
    {
        this.maxRows = maxRows;
    }

    public List<T> getResultPage()
    {
        return resultPage;
    }

    public void setResultPage(List<T> resultPage)
    {
        this.resultPage = resultPage;
    }
}
