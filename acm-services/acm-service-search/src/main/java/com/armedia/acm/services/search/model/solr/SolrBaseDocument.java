package com.armedia.acm.services.search.model.solr;

/*-
 * #%L
 * ACM Service: Search
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
 * Created by armdev on 10/23/14.
 */
public interface SolrBaseDocument
{
    String getId();

    void setId(String id);

    void setDeny_user_ls(List<Long> deny_user_ls);

    void setAllow_user_ls(List<Long> allow_user_ls);

    void setDeny_group_ls(List<Long> deny_group_ls);

    void setAllow_group_ls(List<Long> allow_group_ls);

    void setAllowUser_ls(List<Long> allow_user_ls);

    void setDenyGroup_ls(List<Long> deny_group_ls);

    void setAllowGroup_ls(List<Long> allow_group_ls);

    void setPublic_doc_b(boolean public_doc_b);

    void setProtected_object_b(boolean protected_object_b);
}
