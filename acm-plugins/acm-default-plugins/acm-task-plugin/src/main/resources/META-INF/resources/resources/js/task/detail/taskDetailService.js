/**
 * TaskDetail.Service
 *
 * manages all service call to application server
 *
 * @author jwu
 */
TaskDetail.Service = {
    initialize : function() {
    }

    ,API_RETRIEVE_DETAIL       : "/api/latest/plugin/task/byId/"
    ,API_COMPLETE_TASK         : "/api/latest/plugin/task/completeTask/"


    ,retrieveDetail : function(taskId) {
        Acm.Ajax.asyncGet(Acm.getContextPath() + this.API_RETRIEVE_DETAIL + taskId
            ,TaskDetail.Callback.EVENT_DETAIL_RETRIEVED
        );
    }
    ,completeTask : function(taskId) {
        Acm.Ajax.asyncPost(Acm.getContextPath() + this.API_COMPLETE_TASK + taskId
            ,"{}"
            ,TaskDetail.Callback.EVENT_TASK_COMPLETED
        );
    }
};

