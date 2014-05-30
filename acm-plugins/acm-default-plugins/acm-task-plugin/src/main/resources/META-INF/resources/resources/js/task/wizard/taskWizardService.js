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

    ,API_GET_APPROVERS        : "/api/latest/users/withPrivilege/acm-complaint-approve"
    ,API_CREATE_TASK          : "/api/latest/plugin/complaint"
    ,API_UPLOAD_TASK_FILE     : "/api/latest/plugin/complaint/file"


    ,getApprovers : function() {
        Acm.Ajax.asyncGet(Acm.getContextPath() + this.API_GET_APPROVERS
            ,TaskWizard.Callback.EVENT_APPROVERS_RETRIEVED
        );
    }
    ,createTask : function(data) {
        Acm.Ajax.asyncPost(Acm.getContextPath() + this.API_CREATE_TASK
            ,JSON.stringify(data)
            ,TaskWizard.Callback.EVENT_CREATE_RETURNED
        );
    }
};

