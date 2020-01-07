package com.armedia.acm.services.mediaengine.editor;

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

import java.beans.PropertyEditorSupport;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com>
 */
public class BooleanEditor extends PropertyEditorSupport
{
    @Override
    public String getAsText()
    {
        return getValue() instanceof Boolean ? String.valueOf(getValue()) : "";
    }

    @Override
    public void setAsText(String value) throws IllegalArgumentException
    {
        setValue(new Boolean(Boolean.parseBoolean(value)));
    }

    @Override
    public String[] getTags()
    {
        return new String[] { String.valueOf(true), String.valueOf(false) };
    }
}
