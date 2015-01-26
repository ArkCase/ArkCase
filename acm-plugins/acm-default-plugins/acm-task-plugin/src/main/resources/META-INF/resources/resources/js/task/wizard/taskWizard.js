/**
 * TaskWizard is namespace component for Task Wizard
 *
 * @author jwu
 */
var TaskWizard = TaskWizard || {
    create: function() {
        TaskWizard.Object.create();
        TaskWizard.Event.create();
        TaskWizard.Page.create();
        TaskWizard.Rule.create();
        TaskWizard.Service.create();
        TaskWizard.Callback.create();

        Acm.deferred(TaskWizard.Event.onPostInit);
    }
    //get assignees
    ,getAssignees: function() {
        var data = sessionStorage.getItem("TaskAssignees");
        var item = ("null" === data)? null : JSON.parse(data);
        return item;
    }
    ,setAssignees: function(data) {
        var item = (Acm.isEmpty(data))? null : JSON.stringify(data);
        sessionStorage.setItem("TaskAssignees", item);
    }
    ,Object: {}
    ,Event:{}
    ,Page: {}
    ,Rule: {}
    ,Service: {}
    ,Callback: {}

};
