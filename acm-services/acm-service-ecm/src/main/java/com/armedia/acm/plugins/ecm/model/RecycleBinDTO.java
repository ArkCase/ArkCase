package com.armedia.acm.plugins.ecm.model;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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
 * @author darko.dimitrievski
 */

public class RecycleBinDTO
{
    private int numRecycleBinItems;
    private List<RecycleBinItemDTO> recycleBinItems;

    public RecycleBinDTO ()
    {

    }

    public RecycleBinDTO(int numRecycleBinItems, List<RecycleBinItemDTO> recycleBinItems)
    {
        this.numRecycleBinItems = numRecycleBinItems;
        this.recycleBinItems = recycleBinItems;
    }

    public int getNumRecycleBinItems()
    {
        return numRecycleBinItems;
    }

    public void setNumRecycleBinItems(int numRecycleBinItems)
    {
        this.numRecycleBinItems = numRecycleBinItems;
    }

    public List<RecycleBinItemDTO> getRecycleBinItems()
    {
        return recycleBinItems;
    }

    public void setRecycleBinItems(List<RecycleBinItemDTO> recycleBinItems)
    {
        this.recycleBinItems = recycleBinItems;
    }
}
