package com.armedia.acm.plugins.casefile.dao;

/*-
 * #%L
 * ACM Default Plugin: Case File
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import static org.easymock.EasyMock.expect;

import com.armedia.acm.services.templateconfiguration.model.CorrespondenceMergeField;
import com.armedia.acm.services.templateconfiguration.service.CorrespondenceMergeFieldManager;
import com.armedia.acm.correspondence.utils.ParagraphRunPoiWordGenerator;
import com.armedia.acm.plugins.casefile.model.CaseFile;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

@RunWith(MockitoJUnitRunner.class)
public class SpelTest extends EasyMockSupport
{

    CorrespondenceMergeFieldManager manager;

    private ParagraphRunPoiWordGenerator generator;

    @Before
    public void setUp()
    {
        generator = new ParagraphRunPoiWordGenerator();

        manager = createMock(CorrespondenceMergeFieldManager.class);
        generator.setMergeFieldManager(manager);

    }

    @Test
    public void spelTest() throws Exception
    {

        CaseFile caseFile = new CaseFile();

        expect(manager.getMergeFields()).andReturn(new ArrayList<CorrespondenceMergeField>()).anyTimes();
        String evaluation = null;
        replayAll();

        System.out.println(caseFile.getCreated());
        evaluation = String.valueOf(generator.evaluateSpelExpression(caseFile, "'FY'+(created.year+1900+(created.month+1>=10?1:0))", null));
        System.out.println(evaluation);

        caseFile.setCreated(new Date("2020/03/10"));
        System.out.println(caseFile.getCreated());
        evaluation = String.valueOf(generator.evaluateSpelExpression(caseFile, "'FY'+(created.year+1900+(created.month+1>=10?1:0))", null));
        System.out.println(evaluation);

        caseFile.setCreated(new Date("2020/10/01"));
        System.out.println(caseFile.getCreated());
        evaluation = String.valueOf(generator.evaluateSpelExpression(caseFile, "'FY'+(created.year+1900+(created.month+1>=10?1:0))", null));
        System.out.println(evaluation);

        caseFile.setCreated(new Date("2020/09/30"));
        System.out.println(caseFile.getCreated());
        evaluation = String.valueOf(generator.evaluateSpelExpression(caseFile, "'FY'+(created.year+1900+(created.month+1>=10?1:0))", null));
        System.out.println(evaluation);

        // 'FY'+(receivedDate.year+(receivedDate.month.value+1>=10?1:0))
        caseFile.setQueueEnterDate(LocalDateTime.of(2020, 12, 24, 10, 10));
        System.out.println(caseFile.getQueueEnterDate());
        evaluation = String.valueOf(generator.evaluateSpelExpression(caseFile, "'FY'+(queueEnterDate.year+(queueEnterDate.month.value>=10?1:0))",
                null));
        System.out.println(evaluation);

        caseFile.setQueueEnterDate(LocalDateTime.of(2020, 10, 1, 10, 10));
        System.out.println(caseFile.getQueueEnterDate());
        evaluation = String.valueOf(generator.evaluateSpelExpression(caseFile, "'FY'+(queueEnterDate.year+(queueEnterDate.month.value>=10?1:0))",
                null));
        System.out.println(evaluation);

        caseFile.setQueueEnterDate(LocalDateTime.of(2020, 9, 30, 10, 10));
        System.out.println(caseFile.getQueueEnterDate());
        evaluation = String.valueOf(generator.evaluateSpelExpression(caseFile, "'FY'+(queueEnterDate.year+(queueEnterDate.month.value>=10?1:0))",
                null));
        System.out.println(evaluation);

        caseFile.setQueueEnterDate(LocalDateTime.of(2020, 2, 13, 10, 10));
        System.out.println(caseFile.getQueueEnterDate());
        evaluation = String.valueOf(generator.evaluateSpelExpression(caseFile, "'FY'+(queueEnterDate.year+(queueEnterDate.month.value>=10?1:0))",
                null));
        System.out.println(evaluation);
    }
}
