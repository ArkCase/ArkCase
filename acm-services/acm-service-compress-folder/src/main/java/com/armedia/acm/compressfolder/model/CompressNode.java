package com.armedia.acm.compressfolder.model;

import java.util.ArrayList;
import java.util.List;

public class CompressNode
{
    private Long rootFolderId;
    private List<FileFolderNode> selectedNodes = new ArrayList<>();

    public Long getRootFolderId()
    {
        return rootFolderId;
    }

    public void setRootFolderId(Long rootFolderId)
    {
        this.rootFolderId = rootFolderId;
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
