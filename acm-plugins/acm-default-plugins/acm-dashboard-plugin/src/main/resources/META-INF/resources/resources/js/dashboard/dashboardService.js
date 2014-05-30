/**
 * Dashboard.Service
 *
 * manages all service call to application server
 *
 * @author jwu
 */
Dashboard.Service = {
    initialize : function() {
    }

    ,API_RETRIEVE_MY_TASKS       : "/api/latest/plugin/complaint/list"


    ,retrieveMyTasks : function() {
        Acm.Ajax.asyncGet(Acm.getContextPath() + this.API_RETRIEVE_MY_TASKS
            ,Dashboard.Callback.EVENT_MY_TASKS_RETRIEVED
        );
    }
};

