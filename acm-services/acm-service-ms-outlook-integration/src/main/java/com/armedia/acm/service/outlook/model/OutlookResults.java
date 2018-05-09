package com.armedia.acm.service.outlook.model;

import java.util.List;

/**
 * Created by armdev on 4/21/15.
 */
public class OutlookResults<T extends OutlookItem>
{
    private List<T> items;
    private int totalItems;
    private boolean moreItemsAvailable;
    private int currentStartIndex;
    private int currentMaxItems;
    private Integer nextStartIndex;
    private String currentSortField;
    private boolean currentSortAscending;

    public int getTotalItems()
    {
        return totalItems;
    }

    public void setTotalItems(int totalItems)
    {
        this.totalItems = totalItems;
    }

    public boolean isMoreItemsAvailable()
    {
        return moreItemsAvailable;
    }

    public void setMoreItemsAvailable(boolean moreItemsAvailable)
    {
        this.moreItemsAvailable = moreItemsAvailable;
    }

    public int getCurrentStartIndex()
    {
        return currentStartIndex;
    }

    public void setCurrentStartIndex(int currentStartIndex)
    {
        this.currentStartIndex = currentStartIndex;
    }

    public int getCurrentMaxItems()
    {
        return currentMaxItems;
    }

    public void setCurrentMaxItems(int currentMaxItems)
    {
        this.currentMaxItems = currentMaxItems;
    }

    public Integer getNextStartIndex()
    {
        return nextStartIndex;
    }

    public void setNextStartIndex(Integer nextStartIndex)
    {
        this.nextStartIndex = nextStartIndex;
    }

    public String getCurrentSortField()
    {
        return currentSortField;
    }

    public void setCurrentSortField(String currentSortField)
    {
        this.currentSortField = currentSortField;
    }

    public boolean isCurrentSortAscending()
    {
        return currentSortAscending;
    }

    public void setCurrentSortAscending(boolean currentSortAscending)
    {
        this.currentSortAscending = currentSortAscending;
    }

    public List<T> getItems()
    {
        return items;
    }

    public void setItems(List<T> items)
    {
        this.items = items;
    }

    @Override
    public String toString()
    {
        return "OutlookResults{" +
                "items=" + items +
                ", totalItems=" + totalItems +
                ", moreItemsAvailable=" + moreItemsAvailable +
                ", currentStartIndex=" + currentStartIndex +
                ", currentMaxItems=" + currentMaxItems +
                ", nextStartIndex=" + nextStartIndex +
                ", currentSortField='" + currentSortField + '\'' +
                ", currentSortAscending=" + currentSortAscending +
                '}';
    }
}
