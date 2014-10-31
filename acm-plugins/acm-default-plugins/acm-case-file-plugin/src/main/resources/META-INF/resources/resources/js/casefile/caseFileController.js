/**
 * CaseFile.Controller
 *
 * @author jwu
 */
CaseFile.Controller = {
    create : function() {
    }
    ,initialize: function() {
        var treeInfo = CaseFile.Model.Tree.Config.getTreeInfo();
        if (0 < treeInfo.caseFileId) { //single caseFile
            CaseFile.Model.setCaseFileId(treeInfo.caseFileId);
            CaseFile.Service.Detail.retrieveCaseFile(treeInfo.caseFileId);
        } else {
            CaseFile.Service.List.retrieveCaseFileList(treeInfo);
        }
    }

    ,ME_CASE_FILE_LIST_RETRIEVED		: "case-file-case-file-list-retrieved"     //param: key
    ,ME_CASE_FILE_RETRIEVED		        : "case-file-detail-retrieved"             //param: caseFile
    ,ME_CASE_FILE_SAVED		            : "case-file-detail-saved"                 //param: caseFile

    ,VE_PREV_PAGE_CLICKED		        : "case-file-prev-page-clicked"            //param: none
    ,VE_NEXT_PAGE_CLICKED		        : "case-file-next-page-clicked"            //param: none
    ,VE_CASE_FILE_SELECTED		        : "case-file-case-file-selected"           //param: caseFileId
    ,VE_TREE_NODE_SELECTED		        : "case-file-tree-node-selected"           //param: node key

    ,VE_CASE_TITLE_CHANGED              : "case-file-case-title-changed"           //param: caseFileId, caseTitle
    ,VE_INCIDENT_DATE_CHANGED           : "case-file-incident-date-changed"        //param: caseFileId, incidentDate



    ,modelRetrievedCaseFileList: function(key) {
        Acm.Dispatcher.fireEvent(this.ME_CASE_FILE_LIST_RETRIEVED, key);
    }
    ,modelRetrievedCaseFile: function(caseFile) {
        Acm.Dispatcher.fireEvent(this.ME_CASE_FILE_RETRIEVED, caseFile);
    }
    ,modelSavedCaseFile : function(caseFile) {
        Acm.Dispatcher.fireEvent(this.ME_CASE_FILE_SAVED, caseFile);
    }
    ,viewClickedPrevPage: function() {
        Acm.Dispatcher.fireEvent(this.VE_PREV_PAGE_CLICKED);
    }
    ,viewClickedNextPage: function() {
        Acm.Dispatcher.fireEvent(this.VE_NEXT_PAGE_CLICKED);
    }
    ,viewSelectedCaseFile: function(caseFileId) {
        Acm.Dispatcher.fireEvent(this.VE_CASE_FILE_SELECTED, caseFileId);
    }
    ,viewSelectedTreeNode: function(nodeKey) {
        Acm.Dispatcher.fireEvent(this.VE_TREE_NODE_SELECTED, nodeKey);
    }
    ,viewChangedCaseTitle: function(caseFileId, caseTitle) {
        Acm.Dispatcher.fireEvent(this.VE_CASE_TITLE_CHANGED, caseFileId, caseTitle);
    }
    ,viewChangedIncidentDate: function(caseFileId, incidentDate) {
        Acm.Dispatcher.fireEvent(this.VE_INCIDENT_DATE_CHANGED, caseFileId, incidentDate);
    }


};

