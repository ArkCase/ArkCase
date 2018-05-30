package com.armedia.acm.service.outlook.model;

/*-
 * #%L
 * ACM Service: MS Outlook integration
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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
