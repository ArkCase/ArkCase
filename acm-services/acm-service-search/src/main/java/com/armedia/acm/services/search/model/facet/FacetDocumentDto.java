package com.armedia.acm.services.search.model.facet;

import java.util.List;

/**
 * Created by marjan.stefanoski on 19.12.2014.
 */
public class FacetDocumentDto implements IFacetDto {

    private String createDateStart;
    private String createDateEnd;
    private String modifyDateStart;
    private String modifyDateEnd;
    private List<String> createUserList;
    private List<String> modifyUserList;
    private List<String> fileStatusList;
    private List<String> parentTypeList;

    public String getCreateDateStart() {
        return createDateStart;
    }

    public void setCreateDateStart(String createDateStart) {
        this.createDateStart = createDateStart;
    }

    public String getCreateDateEnd() {
        return createDateEnd;
    }

    public void setCreateDateEnd(String createDateEnd) {
        this.createDateEnd = createDateEnd;
    }

    public String getModifyDateStart() {
        return modifyDateStart;
    }

    public void setModifyDateStart(String modifyDateStart) {
        this.modifyDateStart = modifyDateStart;
    }

    public String getModifyDateEnd() {
        return modifyDateEnd;
    }

    public void setModifyDateEnd(String modifyDateEnd) {
        this.modifyDateEnd = modifyDateEnd;
    }

    public List<String> getCreateUserList() {
        return createUserList;
    }

    public void setCreateUserList(List<String> createUserList) {
        this.createUserList = createUserList;
    }

    public List<String> getModifyUserList() {
        return modifyUserList;
    }

    public void setModifyUserList(List<String> modifyUserList) {
        this.modifyUserList = modifyUserList;
    }

    public List<String> getFileStatusList() {
        return fileStatusList;
    }

    public void setFileStatusList(List<String> fileStatusList) {
        this.fileStatusList = fileStatusList;
    }

    public List<String> getParentTypeList() {
        return parentTypeList;
    }

    public void setParentTypeList(List<String> parentTypeList) {
        this.parentTypeList = parentTypeList;
    }
}
