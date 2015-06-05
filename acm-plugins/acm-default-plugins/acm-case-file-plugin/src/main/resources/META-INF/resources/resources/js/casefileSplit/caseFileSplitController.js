/**
 * CaseFileSplit.Controller
 *
 * @author jwu
 */
CaseFileSplit.Controller = CaseFileSplit.Controller || {
    create : function() {
    }
    ,onInitialized: function() {
    }

    ,MODEL_RETRIEVED_PARENT_CASE_FILE      : "casefile-split-model-retrieved-parent-case-file"
    ,modelRetrievedParentCaseFile: function(parentCaseFile){
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_PARENT_CASE_FILE, parentCaseFile);
    }

    ,MODEL_RETRIEVED_GROUPS                 : "casefile-split-model-retrieved-groups"
    ,modelRetrievedGroups: function(groups) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_GROUPS, groups);
    }
    
    ,MODEL_RETRIEVED_USERS                 : "casefile-split-model-retrieved-users"
    ,modelRetrievedUsers: function(users) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_USERS, users);
    }
    ,MODEL_FOUND_ASSIGNEES                 : "casefile-split-model-found-assignees"
    ,modelFoundAssignees: function(assignees) {
        Acm.Dispatcher.fireEvent(this.MODEL_FOUND_ASSIGNEES, assignees);
    }

    ,VIEW_RETRIEVED_SUMMARY               : "casefile-split-view-retrieved-summary"
    ,viewRetrievedSummary: function(summary){
        Acm.Dispatcher.fireEvent(this.VIEW_RETRIEVED_SUMMARY, summary);
    }
    ,MODEL_SPLIT_CASE_FILE                  : "casefile-split-model-split-casefile"
    ,modelSplitCaseFile: function(splitCaseFile){
        Acm.Dispatcher.fireEvent(this.MODEL_SPLIT_CASE_FILE, splitCaseFile);
    }
};

