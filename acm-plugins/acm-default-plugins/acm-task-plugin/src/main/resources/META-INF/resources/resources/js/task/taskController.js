/**
 * Task.Controller
 *
 * @author jwu
 */
Task.Controller = Task.Controller || {
    create : function() {
    }
    ,onInitialized: function() {
    }


    ,VIEW_CHANGED_TITLE                                     : "task-view-changed-title"
    ,viewChangedTitle: function(nodeType, taskId, title) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_TITLE, nodeType, taskId, title);
    }
    ,MODEL_SAVED_TITLE                                      : "task-model-saved-title"
    ,modelSavedTitle: function(nodeType, taskId, title) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_TITLE, nodeType, taskId, title);
    }
    ,VIEW_CHANGED_PERCENT_COMPLETED                         : "task-view-changed-percent-completed"
    ,viewChangedPercentCompleted: function(nodeType, taskId, percent) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_PERCENT_COMPLETED, nodeType, taskId, percent);
    }
    ,MODEL_SAVED_PERCENT_COMPLETED                             : "task-model-saved--percent-completed"
    ,modelSavedPercentCompleted: function(nodeType, taskId, percent) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_PERCENT_COMPLETED, nodeType, taskId, percent);
    }
    ,VIEW_CHANGED_ASSIGNEE                                      : "task-view-changed-assignee"
    ,viewChangedAssignee: function(nodeType, taskId, assignee) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_ASSIGNEE, nodeType, taskId, assignee);
    }
    ,MODEL_SAVED_ASSIGNEE                                       : "task-model-saved-assignee"
    ,modelSavedAssignee: function(nodeType, taskId, assignee) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_ASSIGNEE, nodeType, taskId, assignee);
    }
    ,VIEW_CHANGED_PRIORITY                                      : "task-view-changed-priority"
    ,viewChangedPriority: function(nodeType, taskId, priority) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_PRIORITY, nodeType, taskId, priority);
    }
    ,MODEL_SAVED_PRIORITY                                       : "task-model-saved-priority"
    ,modelSavedPriority: function(nodeType, taskId, priority) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_PRIORITY, nodeType, taskId, priority);
    }
    ,VIEW_CHANGED_START_DATE                                    : "task-view-changed-start-date"
    ,viewChangedStartDate: function(nodeType, taskId, startDate) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_START_DATE, nodeType, taskId, startDate);
    }
    ,MODEL_SAVED_START_DATE                                     : "task-model-saved-start-date"
    ,modelSavedStartDate: function(nodeType, taskId, startDate) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_START_DATE, nodeType, taskId, startDate);
    }
    ,VIEW_CHANGED_DUE_DATE                                      : "task-view-changed-due-date"
    ,viewChangedDueDate: function(taskId, dueDate) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_DUE_DATE, nodeType, taskId, dueDate);
    }
    ,MODEL_SAVED_DUE_DATE                                       : "task-model-saved-due-date"
    ,modelSavedDueDate: function(taskId, dueDate) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_DUE_DATE, nodeType, taskId, dueDate);
    }

    ,VIEW_CHANGED_DETAIL                                        : "task-view-changed-detail"
    ,viewChangedDetail: function(nodeType, taskId, details) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_DETAIL, nodeType, taskId, details);
    }
    ,MODEL_SAVED_DETAIL                             : "task-model-changed-detail"
    ,modelSavedDetail: function(nodeType, taskId, details) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_DETAIL, taskId, details);
    }
    ,VIEW_CHANGED_REWORK_DETAILS                           : "task-view-changed-rework-detail"
    ,viewChangedReworkDetails: function(nodeType, taskId, reworkDetails) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_REWORK_DETAILS, nodeType, taskId, reworkDetails);
    }
    ,MODEL_SAVED_REWORK_DETAILS                             : "task-model-changed-rework-detail"
    ,modelSavedReworkDetails: function(nodeType, taskId, reworkDetails) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_REWORK_DETAILS, taskId, reworkDetails);
    }

//    ,VIEW_POPULATED_TASK_DETAILS                    : "task-view-populated-task-details"
//    ,viewPopulatedTaskDetails: function(task) {
//        Acm.Dispatcher.fireEvent(this.VIEW_POPULATED_TASK_DETAILS, task);
//    }
    ,VIEW_COMPLETED_TASK               : "task-view-completed-task"
    ,viewCompletedTask: function(task) {
        Acm.Dispatcher.fireEvent(this.VIEW_COMPLETED_TASK, task);
    }
    ,MODEL_COMPLETED_TASK                         : "task-model-completed-task"
    ,modelCompletedTask: function(taskId) {
        Acm.Dispatcher.fireEvent(this.MODEL_COMPLETED_TASK, taskId);
    }
    ,VIEW_DELETED_TASK               : "task-view-deleted-task"
    ,viewDeletedTask: function(taskId) {
        Acm.Dispatcher.fireEvent(this.VIEW_DELETED_TASK, taskId);
    }
    ,MODEL_DELETED_TASK              : "task-model-deleted"
    ,modelDeletedTask: function(taskId) {
        Acm.Dispatcher.fireEvent(this.MODEL_COMPLETED_TASK, taskId);
    }

    ,VIEW_ADDED_NOTE                                : "task-view-added-note"
    ,viewAddedNote: function(note){
        Acm.Dispatcher.fireEvent(this.VIEW_ADDED_NOTE, note);
    }
    ,MODEL_ADDED_NOTE                               : "task-model-added-note"
    ,modelAddedNote: function(note) {
        Acm.Dispatcher.fireEvent(this.MODEL_ADDED_NOTE, note);
    }
    ,VIEW_UPDATED_NOTE                              : "task-view-updated-note"
    ,viewUpdatedNote: function(note){
        Acm.Dispatcher.fireEvent(this.VIEW_UPDATED_NOTE, note);
    }
    ,MODEL_UPDATED_NOTE                             : "task-model-updated-note"
    ,modelUpdatedNote: function(note) {
        Acm.Dispatcher.fireEvent(this.MODEL_UPDATED_NOTE, note);
    }
    ,VIEW_DELETED_NOTE                              : "task-view-deleted-note"
    ,viewDeletedNote: function(deletedNoteId){
        Acm.Dispatcher.fireEvent(this.VIEW_DELETED_NOTE, deletedNoteId);
    }
    ,MODEL_DELETED_NOTE                             : "task-model-deleted-note"
    ,modelDeletedNote: function(deletedNoteId) {
        Acm.Dispatcher.fireEvent(this.MODEL_DELETED_NOTE, deletedNoteId);
    }
    ,MODEL_SAVED_NOTE                               : "task-model-saved-note"
    ,modelSavedNote: function(note) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_NOTE, note);
    }

    ,MODEL_RETRIEVED_WORKFLOW_OVERVIEW              : "task-model-retrieved-workflow-overview"
    ,modelRetrievedWorkflowOverview: function(workflowOverview) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_WORKFLOW_OVERVIEW, workflowOverview);
    }
    ,MODEL_UPLOADED_ATTACHMENTS                     : "task-model-uploaded-attachments"
    ,modelUploadedAttachments: function(attachments) {
        Acm.Dispatcher.fireEvent(this.MODEL_UPLOADED_ATTACHMENTS, attachments);
    }
    ,MODEL_RETRIEVED_REJECT_COMMENTS                : "task-model-retrieved-reject-comments"
    ,modelRetrievedRejectComments: function(rejectComments) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_REJECT_COMMENTS, rejectComments);
    }

    ,MODEL_RETRIEVED_PARENT_OBJECT                : "task-model-parent-object-retrieved-detail"
    ,modelRetrievedParentObject: function(parentObjData) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_PARENT_OBJECT, parentObjData);
    }
    ,MODEL_RETRIEVED_PARENT_OBJECT_ERROR          : "task-model-parent-object-retrieved-detail-error"
    ,modelRetrievedParentObjectError: function(error) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_PARENT_OBJECT_ERROR, error);
    }

    ,VIEW_RETRIEVED_USERS          : "task-view-retrieved-users"
    ,viewRetrievedUsers: function(start, n, sortDirection, searchKeyword, exclude) {
        Acm.Dispatcher.fireEvent(this.VIEW_RETRIEVED_USERS, start, n, sortDirection, searchKeyword, exclude);
    }
    ,MODEL_RETRIEVED_USERS          : "task-model-retrieved-users"
    ,modelRetrievedUsers: function(response) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_USERS, response);
    }
    ,MODEL_RETRIEVED_ASSIGNEES          : "task-model-retrieved-assignees"
    ,modelRetrievedAssignees: function(assignees){
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_ASSIGNEES, assignees);
    }
    ,MODEL_RETRIEVED_PRIORITIES          : "task-model-retrieved-priorities"
    ,modelRetrievedPriorities: function(priorities){
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_PRIORITIES, priorities);
    }

};

