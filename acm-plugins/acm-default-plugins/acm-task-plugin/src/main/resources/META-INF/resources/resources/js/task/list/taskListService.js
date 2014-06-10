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

    ,API_LIST_TASK       : "/api/latest/plugin/task/forUser/"
    ,API_COMPLETE_TASK         : "/api/latest/plugin/task/completeTask/"


    ,listTask : function(user) {
        Acm.Ajax.asyncGet(Acm.getContextPath() + this.API_LIST_TASK + user
            ,TaskList.Callback.EVENT_LIST_RETURNED
        );
    }
    ,completeTask : function(taskId) {
        Acm.Ajax.asyncPost(Acm.getContextPath() + this.API_COMPLETE_TASK + taskId
            ,"{}"
            ,TaskList.Callback.EVENT_TASK_COMPLETED
        );
    }

}