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

    ,Object: {}
    ,Event:{}
    ,Page: {}
    ,Rule: {}
    ,Service: {}
    ,Callback: {}

};
