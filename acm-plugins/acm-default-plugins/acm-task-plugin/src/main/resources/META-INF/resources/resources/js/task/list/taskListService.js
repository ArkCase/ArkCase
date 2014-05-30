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

    ,API_LIST_TASK       : "/api/latest/plugin/complaint/list"


    ,listTask : function() {
        Acm.Ajax.asyncGet(Acm.getContextPath() + this.API_LIST_TASK
            ,TaskList.Callback.EVENT_LIST_RETURNED
        );
    }
};

