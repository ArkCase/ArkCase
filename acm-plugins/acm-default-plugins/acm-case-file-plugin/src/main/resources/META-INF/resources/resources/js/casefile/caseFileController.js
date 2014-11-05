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

    ,ME_ASSIGNEES_FOUND                 : "case-file-assignees"                    //param: assignees
    ,ME_SUBJECT_TYPES_FOUND             : "case-file-subject-types"                //param: subjectTypes
    ,ME_PRIORITIES_FOUND                : "case-file-priorities"                   //param: priorities

    ,ME_CASE_FILE_LIST_RETRIEVED        : "case-file-case-file-list-retrieved"     //param: key
    ,ME_CASE_FILE_RETRIEVED             : "case-file-detail-retrieved"             //param: caseFile
    ,ME_CASE_FILE_SAVED                 : "case-file-detail-saved"                 //param: caseFile

    ,ME_CASE_TITLE_SAVED                : "case-file-case-title-saved"             //param: caseFileId, caseTitle
    ,ME_INCIDENT_DATE_SAVED             : "case-file-incident-date-saved"          //param: caseFileId, created
    ,ME_PRIORITY_SAVED                  : "case-file-priority-saved"               //param: caseFileId, priority

    ,VE_PREV_PAGE_CLICKED               : "case-file-prev-page-clicked"            //param: none
    ,VE_NEXT_PAGE_CLICKED		        : "case-file-next-page-clicked"            //param: none
    ,VE_CASE_FILE_SELECTED		        : "case-file-case-file-selected"           //param: caseFileId
    ,VE_TREE_NODE_SELECTED		        : "case-file-tree-node-selected"           //param: node key

    ,VE_CASE_TITLE_CHANGED              : "case-file-case-title-changed"           //param: caseFileId, title
    ,VE_INCIDENT_DATE_CHANGED           : "case-file-incident-date-changed"        //param: caseFileId, created
    ,VE_ASSIGNEE_CHANGED                : "case-file-assignee-changed"             //param: caseFileId, assignee
    ,VE_SUBJECT_TYPE_CHANGED            : "case-file-subject-type-changed"         //param: caseFileId, subjectType
    ,VE_PRIORITY_CHANGED                : "case-file-priority-changed"             //param: caseFileId, priority

    ,modelFoundAssignees: function(assignees) {
        Acm.Dispatcher.fireEvent(this.ME_ASSIGNEES_FOUND, assignees);
    }
    ,modelFoundSubjectTypes: function(subjectTypes) {
        Acm.Dispatcher.fireEvent(this.ME_SUBJECT_TYPES_FOUND, subjectTypes);
    }
    ,modelFoundPriorities: function(priorities) {
        Acm.Dispatcher.fireEvent(this.ME_PRIORITIES_FOUND, priorities);
    }
    ,modelRetrievedCaseFileList: function(key) {
        Acm.Dispatcher.fireEvent(this.ME_CASE_FILE_LIST_RETRIEVED, key);
    }
    ,modelRetrievedCaseFile: function(caseFile) {
        Acm.Dispatcher.fireEvent(this.ME_CASE_FILE_RETRIEVED, caseFile);
    }
    ,modelSavedCaseFile : function(caseFile) {
        Acm.Dispatcher.fireEvent(this.ME_CASE_FILE_SAVED, caseFile);
    }
    ,modelSavedCaseTitle : function(caseFileId, title) {
        Acm.Dispatcher.fireEvent(this.ME_CASE_TITLE_SAVED, caseFileId, title);
    }
    ,modelSavedIncidentDate : function(caseFileId, created) {
        Acm.Dispatcher.fireEvent(this.ME_INCIDENT_DATE_SAVED, caseFileId, created);
    }
    ,modelSavedPriority : function(caseFileId, priority) {
        Acm.Dispatcher.fireEvent(this.ME_PRIORITY_SAVED, caseFileId, priority);
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
    ,viewChangedCaseTitle: function(caseFileId, title) {
        Acm.Dispatcher.fireEvent(this.VE_CASE_TITLE_CHANGED, caseFileId, title);
    }
    ,viewChangedIncidentDate: function(caseFileId, created) {
        Acm.Dispatcher.fireEvent(this.VE_INCIDENT_DATE_CHANGED, caseFileId, created);
    }
    ,viewChangedAssignee: function(caseFileId, assignee) {
        Acm.Dispatcher.fireEvent(this.VE_ASSIGNEE_CHANGED, caseFileId, assignee);
    }
    ,viewChangedSubjectType: function(caseFileId, subjectType) {
        Acm.Dispatcher.fireEvent(this.VE_SUBJECT_TYPE_CHANGED, caseFileId, subjectType);
    }
    ,viewChangedPriority: function(caseFileId, priority) {
        Acm.Dispatcher.fireEvent(this.VE_PRIORITY_CHANGED, caseFileId, priority);
    }



};

