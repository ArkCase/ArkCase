package com.armedia.acm.services.tag.model;

/*-
 * #%L
 * ACM Service: Tag
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

/**
 * Created by marjan.stefanoski on 31.03.2015.
 */
public interface TagConstants
{

    String OBJECT_TYPE = "TAG";

    String TAG_NAME = "name";
    String TAG_VALUE = "value";
    String TAG_DESC = "desc";

    int FIRST_ROW = 0;
    int MAX_ROWS = 100;
    String SORT = "";

    String SOLR_RESPONSE_BODY = "response";
    String SOLR_RESPONSE_DOCS = "docs";
    String SOLR_ID = "id";
    String SOLR_ID_SPLITER = "-";

    int ZERO = 0;

    String SOLR_PLACEHOLDER_PARENT_TYPE = "$[parentType]";
    String SOLR_PLACEHOLDER_PARENT_ID = "$[parentId]";

}
