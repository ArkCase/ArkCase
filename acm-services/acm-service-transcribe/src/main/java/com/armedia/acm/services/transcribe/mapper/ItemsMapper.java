package com.armedia.acm.services.transcribe.mapper;

/*-
 * #%L
 * ACM Service: Transcribe
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

import com.armedia.acm.objectonverter.ArkCaseBeanUtils;
import com.armedia.acm.services.transcribe.model.TranscribeItem;
import com.armedia.acm.tool.transcribe.model.TranscribeItemDTO;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class ItemsMapper
{
    private final Logger LOG = LogManager.getLogger(getClass());

    public List<TranscribeItemDTO> TranscribeItemToDTOS(List<TranscribeItem> transcribeItems)
    {
        List<TranscribeItemDTO> transcribeItemDTOS = new ArrayList<>();
        try
        {
            List<String> excludeFields = new ArrayList<>();
            excludeFields.add("transcribe");
            excludeFields.add("className");

            ArkCaseBeanUtils utils = new ArkCaseBeanUtils();
            utils.setExcludeFields(excludeFields);

            for (TranscribeItem item : transcribeItems)
            {
                TranscribeItemDTO itemDTO = new TranscribeItemDTO();
                utils.copyProperties(itemDTO, item);

                transcribeItemDTOS.add(itemDTO);
            }
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            LOG.warn("Could not copy items for Transcribe with ID=[{}]. REASON=[{}]",
                    transcribeItems.get(0).getTranscribe().getId() != null ? transcribeItems.get(0).getTranscribe().getId() : null,
                    e.getMessage());
        }

        return transcribeItemDTOS;
    }

    public List<TranscribeItem> DTOStoTranscribeItem(List<TranscribeItemDTO> transcribeItemDTOS)
    {
        List<TranscribeItem> transcribeItems = new ArrayList<>();

        try
        {
            List<String> excludeFields = new ArrayList<>();
            excludeFields.add("transcribe");
            excludeFields.add("className");

            ArkCaseBeanUtils utils = new ArkCaseBeanUtils();
            utils.setExcludeFields(excludeFields);

            for (TranscribeItemDTO itemDTO : transcribeItemDTOS)
            {
                TranscribeItem item = new TranscribeItem();
                utils.copyProperties(item, itemDTO);

                transcribeItems.add(item);
            }
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            LOG.warn("Could not copy items for Transcribe with ID=[{}]. REASON=[{}]",
                    transcribeItemDTOS.get(0).getTranscribe().getId() != null ? transcribeItemDTOS.get(0).getTranscribe().getId() : null,
                    e.getMessage());
        }

        return transcribeItems;
    }
}
