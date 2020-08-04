package gov.privacy.service;

/*-
 * #%L
 * ACM Privacy: Subject Access Request
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

import com.armedia.acm.plugins.ecm.model.AcmFolder;

import java.util.function.Predicate;

/**
 * A predicate that tests for equality of a folder name with a configured string value.
 *
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 *
 */
public class FolderNameEqualsPredicate implements Predicate<AcmFolder>
{

    /**
     * A string that is used for testing for equality with a name of a <code>AcmFolder</code>.
     */
    private String folderName;

    /*
     * (non-Javadoc)
     * @see java.util.function.Predicate#test(java.lang.Object)
     */
    @Override
    public boolean test(AcmFolder folder)
    {
        return folder.getName().equals(folderName);
    }

    /**
     * @param folderName
     *            the folderName to set
     */
    public void setFolderName(String folderName)
    {
        this.folderName = folderName;
    }

}
