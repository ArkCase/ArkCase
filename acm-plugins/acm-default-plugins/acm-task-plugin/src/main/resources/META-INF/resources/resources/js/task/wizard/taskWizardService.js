/**
 * TaskWizard.Service
 *
 * manages all service call to application server
 *
 * @author jwu
 */
TaskWizard.Service = {
    initialize : function() {
    }

//    ,API_GET_ASSIGNEES              : "/api/latest/users/withPrivilege/acm-complaint-approve"
    ,API_CREATE_ADHOC_TASK          : "/api/latest/plugin/task/adHocTask"


//    ,getAssignees : function() {
//        Acm.Ajax.asyncGet(Acm.getContextPath() + this.API_GET_ASSIGNEES
//            ,TaskWizard.Callback.EVENT_ASSIGNEES_RETRIEVED
//        );
//    }
    ,createAdhocTask : function(data) {
        Acm.Ajax.asyncPost(Acm.getContextPath() + this.API_CREATE_ADHOC_TASK
            ,JSON.stringify(data)
            ,TaskWizard.Callback.EVENT_TASK_CREATED
        );
    }
};

