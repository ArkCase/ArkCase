/**
 * CaseFile.Controller
 *
 * @author jwu
 */
CaseFile.Controller = {
    create : function() {
    }
    ,initialize: function() {
    }

    ,ME_ASSIGNEES_FOUND                 : "case-file-assignees"                    //param: assignees
    ,ME_SUBJECT_TYPES_FOUND             : "case-file-subject-types"                //param: subjectTypes
    ,ME_PRIORITIES_FOUND                : "case-file-priorities"                   //param: priorities

    ,ME_CASE_FILE_LIST_RETRIEVED        : "case-file-case-file-list-retrieved"     //param: key
    ,ME_CASE_FILE_RETRIEVED             : "case-file-detail-retrieved"             //param: caseFile
    ,ME_CASE_FILE_SAVED                 : "case-file-detail-saved"                 //param: caseFile

    ,ME_CASE_TITLE_SAVED                : "case-file-case-title-saved"             //param: caseFileId, caseTitle
    ,ME_INCIDENT_DATE_SAVED             : "case-file-incident-date-saved"          //param: caseFileId, incidentDate
    ,ME_ASSIGNEE_SAVED                  : "case-file-assignee-saved"               //param: caseFileId, assignee
    ,ME_SUBJECT_TYPE_SAVED              : "case-file-subject-type-saved"           //param: caseFileId, caseType
    ,ME_PRIORITY_SAVED                  : "case-file-priority-saved"               //param: caseFileId, priority
    ,ME_DUE_DATE_SAVED                  : "case-file-due-date-saved"               //param: caseFileId, dueDate
    ,ME_DETAIL_SAVED                    : "case-file-detail-saved"                 //param: caseFileId, details

    ,ME_NOTE_SAVED                      : "case-file-note-saved"                   //param: note
    ,ME_NOTE_DELETED                    : "case-file-note-deleted"                 //param: noteId

    ,VE_PREV_PAGE_CLICKED               : "case-file-prev-page-clicked"            //param: none
    ,VE_NEXT_PAGE_CLICKED		        : "case-file-next-page-clicked"            //param: none
    ,VE_CASE_FILE_SELECTED		        : "case-file-case-file-selected"           //param: caseFileId
    ,VE_TREE_NODE_SELECTED		        : "case-file-tree-node-selected"           //param: node key

    ,VE_CASE_TITLE_CHANGED              : "case-file-case-title-changed"           //param: caseFileId, title
    ,VE_INCIDENT_DATE_CHANGED           : "case-file-incident-date-changed"        //param: caseFileId, incidentDate
    ,VE_ASSIGNEE_CHANGED                : "case-file-assignee-changed"             //param: caseFileId, assignee
    ,VE_SUBJECT_TYPE_CHANGED            : "case-file-subject-type-changed"         //param: caseFileId, caseType
    ,VE_PRIORITY_CHANGED                : "case-file-priority-changed"             //param: caseFileId, priority
    ,VE_DUE_DATE_CHANGED                : "case-file-due-date-changed"             //param: caseFileId, dueDate
    ,VE_DETAIL_CHANGED                  : "case-file-detail-changed"               //param: caseFileId, details

    ,VE_CASE_FILE_CLOSED                : "case-file-case-file-closed"             //param: caseFileId
    ,VE_DOCUMENT_ADDED                  : "case-file-document-added"               //param: caseFileId
    ,VE_CHILD_OBJECT_CHANGED            : "case-file-child-object-changed"         //param: caseFileId, idx, childObject; childObject format: {Record: {title:"xxx", status="xxx"}}


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
    ,modelSavedIncidentDate : function(caseFileId, incidentDate) {
        Acm.Dispatcher.fireEvent(this.ME_INCIDENT_DATE_SAVED, caseFileId, incidentDate);
    }
    ,modelSavedAssignee : function(caseFileId, assignee) {
        Acm.Dispatcher.fireEvent(this.ME_ASSIGNEE_SAVED, caseFileId, assignee);
    }
    ,modelSavedSubjectType : function(caseFileId, caseType) {
        Acm.Dispatcher.fireEvent(this.ME_SUBJECT_TYPE_SAVED, caseFileId, caseType);
    }
    ,modelSavedPriority : function(caseFileId, priority) {
        Acm.Dispatcher.fireEvent(this.ME_PRIORITY_SAVED, caseFileId, priority);
    }
    ,modelSavedDueDate : function(caseFileId, dueDate) {
        Acm.Dispatcher.fireEvent(this.ME_DUE_DATE_SAVED, caseFileId, dueDate);
    }
    ,modelSavedDetail : function(caseFileId, details) {
        Acm.Dispatcher.fireEvent(this.ME_DETAIL_SAVED, caseFileId, details);
    }

    ,modelSavedNote : function(note) {
        Acm.Dispatcher.fireEvent(this.ME_NOTE_SAVED, note);
    }
    ,modelDeletedNote : function(noteId) {
        Acm.Dispatcher.fireEvent(this.ME_NOTE_DELETED, noteId);
    }


    ,viewClickedPrevPage: function() {
        Acm.Dispatcher.fireEvent(this.VE_PREV_PAGE_CLICKED);
    }
    ,viewClickedNextPage: function() {
        Acm.Dispatcher.fireEvent(this.VE_NEXT_PAGE_CLICKED);
    }
    ,viewSelectedCaseFile: function(caseFileId) {
        //CaseFile.Model.setCaseFileId(caseFileId);
        Acm.Dispatcher.fireEvent(this.VE_CASE_FILE_SELECTED, caseFileId);
    }
    ,viewSelectedTreeNode: function(nodeKey) {
        Acm.Dispatcher.fireEvent(this.VE_TREE_NODE_SELECTED, nodeKey);
    }
    ,viewChangedCaseTitle: function(caseFileId, title) {
        Acm.Dispatcher.fireEvent(this.VE_CASE_TITLE_CHANGED, caseFileId, title);
    }
    ,viewChangedIncidentDate: function(caseFileId, incidentDate) {
        Acm.Dispatcher.fireEvent(this.VE_INCIDENT_DATE_CHANGED, caseFileId, incidentDate);
    }
    ,viewChangedAssignee: function(caseFileId, assignee) {
        Acm.Dispatcher.fireEvent(this.VE_ASSIGNEE_CHANGED, caseFileId, assignee);
    }
    ,viewChangedSubjectType: function(caseFileId, caseType) {
        Acm.Dispatcher.fireEvent(this.VE_SUBJECT_TYPE_CHANGED, caseFileId, caseType);
    }
    ,viewChangedPriority: function(caseFileId, priority) {
        Acm.Dispatcher.fireEvent(this.VE_PRIORITY_CHANGED, caseFileId, priority);
    }
    ,viewChangedDueDate: function(caseFileId, dueDate) {
        Acm.Dispatcher.fireEvent(this.VE_DUE_DATE_CHANGED, caseFileId, dueDate);
    }
    ,viewChangedDetail: function(caseFileId, details) {
        Acm.Dispatcher.fireEvent(this.VE_DETAIL_CHANGED, caseFileId, details);
    }
    ,viewClosedCaseFile: function(caseFileId) {
        Acm.Dispatcher.fireEvent(this.VE_CASE_FILE_CLOSED, caseFileId);
    }
    ,viewAddedDocument: function(caseFileId) {
        Acm.Dispatcher.fireEvent(this.VE_DOCUMENT_ADDED, caseFileId);
    }
    ,viewChangedChildObject: function(caseFileId, idx, childObject) {
        Acm.Dispatcher.fireEvent(this.VE_CHILD_OBJECT_CHANGED, caseFileId, idx, childObject);
    }


};

