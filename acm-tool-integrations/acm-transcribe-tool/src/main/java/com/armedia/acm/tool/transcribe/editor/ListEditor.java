package com.armedia.acm.tool.transcribe.editor;

/*-
 * #%L
 * ACM Service: Media engine
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

import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyEditorSupport;
import java.util.List;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/01/2018
 */
public class ListEditor extends PropertyEditorSupport
{
    @Override
    public String getAsText()
    {
        if (getValue() instanceof List)
        {
            return StringUtils.join((List) getValue(), ", ");
        }

        return "";
    }

    /*
     * @Override
     * public void setAsText(String text) throws IllegalArgumentException
     * {
     * if (text != null)
     * {
     * List<String> items = Arrays.asList(text.split("\\s*,\\s*"));
     * List<MediaEngineServices> providers = items.stream().map(item -> MediaEngineServices.valueOf(item))
     * .collect(Collectors.toList());
     * setValue(providers);
     * return;
     * }
     * throw new IllegalArgumentException(text);
     * }
     */
}
