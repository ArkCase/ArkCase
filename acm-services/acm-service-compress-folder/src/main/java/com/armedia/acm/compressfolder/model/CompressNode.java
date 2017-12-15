package com.armedia.acm.compressfolder.model;

import java.util.ArrayList;
import java.util.List;

public class CompressNode
{
    private Long rootFolder;
    private List<FileFolderNode> selectedNodes = new ArrayList<>();

    public Long getRootFolder()
    {
        return rootFolder;
    }

    public void setRootFolder(Long rootFolder)
    {
        this.rootFolder = rootFolder;
    }

    public List<FileFolderNode> getSelectedNodes()
    {
        return selectedNodes;
    }

    public void setSelectedNodes(List<FileFolderNode> selectedNodes)
    {
        this.selectedNodes = selectedNodes;
    }
}
