/**
 * TaskWizard.Service
 *
 * manages all service call to application server
 *
 * @author jwu
 */
TaskWizard.Service = {
    create : function() {
    }

    ,API_CREATE_ADHOC_TASK          : "/api/latest/plugin/task/adHocTask"
    ,API_RETRIEVE_USERS             : "/api/latest/plugin/search/USER"

    ,API_TYPEAHEAD_SUGGESTION_BEGIN_      : "/api/latest/plugin/search/quickSearch?q=name:*"
    ,API_TYPEAHEAD_SUGGESTION_END         : "*%20AND%20(object_type_s:COMPLAINT%20OR%20object_type_s:CASE)&start=0&n=16"


    ,getAssignees : function() {
        Acm.Ajax.asyncGet(App.getContextPath() + this.API_RETRIEVE_USERS
            ,TaskWizard.Callback.EVENT_ASSIGNEES_RETRIEVED
        );
    }
    ,createAdhocTask : function(data) {
        Acm.Ajax.asyncPost(App.getContextPath() + this.API_CREATE_ADHOC_TASK
            ,JSON.stringify(data)
            ,TaskWizard.Callback.EVENT_TASK_CREATED
        );
    }
};

