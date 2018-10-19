package gov.foia.service;

import com.armedia.acm.plugins.ecm.model.AcmFolder;

import java.util.function.Predicate;

/**
 * A predicate that tests for equality of a folder name with a configured string value.
 *
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Nov 22, 2016
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
