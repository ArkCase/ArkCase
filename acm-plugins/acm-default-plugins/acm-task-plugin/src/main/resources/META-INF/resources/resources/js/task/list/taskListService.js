/**
 * TaskList.Service
 *
 * manages all service call to application server
 *
 * @author jwu
 */
TaskList.Service = {
    initialize : function() {
    }

    ,API_LIST_TASK             : "/api/latest/plugin/task/list/"
    ,API_LIST_TASK             : "/api/latest/plugin/task/forUser/"
    ,API_RETRIEVE_DETAIL       : "/api/latest/plugin/task/byId/"
    ,API_COMPLETE_TASK         : "/api/latest/plugin/task/completeTask/"
    ,API_SIGN_TASK         	   : "/api/latest/plugin/signature/confirm/"
    ,API_FIND_BYTASKBYID_TASK_SIGNATURE : "/api/latest/plugin/signature/find/"


    ,listTaskAll : function() {
            Acm.Ajax.asyncGet(App.getContextPath() + this.API_LIST_TASK + "all"
                ,TaskList.Callback.EVENT_LIST_RETRIEVED
            );
        }

   	,listTask : function(user) {
        Acm.Ajax.asyncGet(App.getContextPath() + this.API_LIST_TASK + user
            ,TaskList.Callback.EVENT_LIST_RETRIEVED
        );
    }
    ,retrieveDetail : function(taskId) {
        Acm.Ajax.asyncGet(App.getContextPath() + this.API_RETRIEVE_DETAIL + taskId
            ,TaskList.Callback.EVENT_DETAIL_RETRIEVED
        );
    }
    ,completeTask : function(taskId) {
        Acm.Ajax.asyncPost(App.getContextPath() + this.API_COMPLETE_TASK + taskId
            ,"{}"
            ,TaskList.Callback.EVENT_TASK_COMPLETED
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
}