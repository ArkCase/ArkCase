/**
 *
 */
package com.armedia.acm.service.outlook.service.impl;

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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.armedia.acm.plugins.ecm.dao.AcmContainerDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 31, 2017
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class OutlookRecreatableCalendarServiceImplTest
{

    private static final long OBJECT_ID = 101L;

    private static final String OBJECT_TYPE = "CASE_FILE";

    @Mock
    private AcmContainerDao mockedContainerDao;

    @InjectMocks
    private OutlookRecreatableCalendarServiceImpl calendarService;

    @Mock
    private AcmContainer mockedContainer;

    /**
     * Test method for
     * {@link com.armedia.acm.service.outlook.service.impl.OutlookRecreatableCalendarServiceImpl#clearFolderRecreatedFlag(java.lang.String, java.lang.Long)}.
     *
     * @throws Exception
     */
    @Test
    public void testClearFolderRecreatedFlag() throws Exception
    {
        // given
        when(mockedContainerDao.findFolderByObjectTypeAndId(any(String.class), any(long.class))).thenReturn(mockedContainer);

        ArgumentCaptor<Boolean> captor = ArgumentCaptor.forClass(Boolean.class);

        // when
        AcmContainer container = calendarService.clearFolderRecreatedFlag(OBJECT_TYPE, OBJECT_ID);

        // then
        verify(mockedContainerDao).findFolderByObjectTypeAndId(OBJECT_TYPE, 101L);
        verify(mockedContainer).setCalendarFolderRecreated(captor.capture());
        assertThat(captor.getValue(), is(false));
        assertThat(container.isCalendarFolderRecreated(), is(false));
    }

}
