/**
 * TaskList.Service
 *
 * manages all service call to application server
 *
 * @author jwu
 */
TaskList.Service = {
    create : function() {
    }

    ,API_LIST_TASK             : "/api/latest/plugin/search/" + Task.getObjectType()
    ,API_RETRIEVE_DETAIL       : "/api/latest/plugin/task/byId/"
    ,API_COMPLETE_TASK         : "/api/latest/plugin/task/completeTask/"
    ,API_COMPLETE_TASK_WITH_OUTCOME         : "/api/latest/plugin/task/completeTask"
    ,API_DELETE_TASK         : "/api/latest/plugin/task/deleteTask/"
    ,API_SIGN_TASK         	   : "/api/latest/plugin/signature/confirm/"
    ,API_FIND_BYTASKBYID_TASK_SIGNATURE : "/api/latest/plugin/signature/find/"
    ,API_SAVE_DETAIL       				: "/api/latest/plugin/task/save/"
    ,API_RETRIEVE_COMPLAINT_DETAIL        : "/api/latest/plugin/complaint/byId/"
    ,API_SAVE_NOTE               : "/api/latest/plugin/note"
    ,API_DELETE_NOTE             : "/api/latest/plugin/note/"
    ,API_LIST_NOTES              : "/api/latest/plugin/note/"
    ,API_DOWNLOAD_DOCUMENT      : "/api/v1/plugin/ecm/download/byId/"
    ,API_RETRIEVE_WORKFLOW_HISTORY       : "/api/latest/plugin/task/history/"


    ,listTaskSaveDetail : function(taskId, data) {
        Acm.Ajax.asyncPost(App.getContextPath() + this.API_SAVE_DETAIL + taskId
                ,JSON.stringify(data)
                ,TaskList.Callback.EVENT_DETAIL_SAVED
            );    	
    }
    ,listTaskAll : function(treeinfo,user) {
            /*Acm.Ajax.asyncGet(App.getContextPath() + this.API_LIST_TASK
                ,TaskList.Callback.EVENT_LIST_RETRIEVED
            );*/
        var taskId = treeinfo.taskId;
        var initKey = treeinfo.initKey;
        var start = treeinfo.start;
        var n = treeinfo.n;
        var s = treeinfo.s;
        var q = treeinfo.q;

        var url = App.getContextPath() + this.API_LIST_TASK  + "?assignee=" + user;
        url += "&start=" + treeinfo.start;
        url += "&n=" + treeinfo.n;
        Acm.Ajax.asyncGet(url
            ,TaskList.Callback.EVENT_LIST_RETRIEVED
        );
    }

   	,listTask : function(user) {
        var url =App.getContextPath() + this.API_LIST_TASK + "?assignee=" + user;
        Acm.Ajax.asyncGet(App.getContextPath() + this.API_LIST_TASK + "?assignee=" + user
            ,TaskList.Callback.EVENT_LIST_RETRIEVED
        );
    }
    ,retrieveDetail : function(taskId) {
        Acm.Ajax.asyncGet(App.getContextPath() + this.API_RETRIEVE_DETAIL + taskId
            ,TaskList.Callback.EVENT_DETAIL_RETRIEVED
        );
    }
    ,retrieveComplaintDetail : function(complaintId) {
        Acm.Ajax.asyncGet(App.getContextPath() + this.API_RETRIEVE_COMPLAINT_DETAIL + complaintId
            ,TaskList.Callback.EVENT_COMPLAINT_DETAIL_RETRIEVED
        );
    }
    ,completeTask : function(taskId) {
        Acm.Ajax.asyncPost(App.getContextPath() + this.API_COMPLETE_TASK + taskId
            ,"{}"
            ,TaskList.Callback.EVENT_TASK_COMPLETED
        );
    }
    ,completeTaskWithOutcome : function(data) {
        Acm.Ajax.asyncPost(App.getContextPath() + this.API_COMPLETE_TASK_WITH_OUTCOME
            ,JSON.stringify(data)
            ,TaskList.Callback.EVENT_TASK_COMPLETED_WITH_OUTCOME
        );
    }
    ,deleteTask : function(taskId) {
        Acm.Ajax.asyncPost(App.getContextPath() + this.API_DELETE_TASK + taskId
            ,"{}"
            ,TaskList.Callback.EVENT_TASK_DELETED
        );
    }
    ,signTask : function(taskId) {
    	var formURL = App.getContextPath() + this.API_SIGN_TASK + Task.getObjectType() + "/" + taskId;
    	var theForm = TaskList.Object.getSignatureForm();
    	
    	Acm.Ajax.asyncPostForm(formURL, theForm, TaskList.Callback.EVENT_TASK_SIGNED);
    }
    ,findSignatureByTypeById : function(taskId) {
    	var url = App.getContextPath() + this.API_FIND_BYTASKBYID_TASK_SIGNATURE + Task.getObjectType() + "/" + taskId;
    	
        Acm.Ajax.asyncGet(url, TaskList.Callback.EVENT_LIST_BYTYPEBYID_RETRIEVED);
    }
    ,saveNote : function(data) {
        Acm.Ajax.asyncPost(App.getContextPath() + this.API_SAVE_NOTE
            ,JSON.stringify(data)
            ,TaskList.Callback.EVENT_NOTE_SAVED
        );
    }
    ,deleteNoteById: function(noteId){
        var url = (App.getContextPath() + this.API_DELETE_NOTE + noteId);
        Acm.Ajax.asyncDelete(App.getContextPath() + this.API_DELETE_NOTE + noteId
            ,TaskList.Callback.EVENT_NOTE_DELETED
        );
    }
    ,retrieveNotes : function(parentId, parentType) {
        var url = (App.getContextPath() + this.API_LIST_NOTES + parentType + "/" + parentId);
        Acm.Ajax.asyncGet(url ,TaskList.Callback.EVENT_NOTE_LIST_RETRIEVED);
    }
    ,retrieveWorkflowHistory : function(id, adhoc) {
    	var url = App.getContextPath() + this.API_RETRIEVE_WORKFLOW_HISTORY + id + '/' + adhoc;
        Acm.Ajax.asyncGet(url, TaskList.Callback.EVENT_WORKFLOW_HISTORY_RETRIEVED);
    }
}