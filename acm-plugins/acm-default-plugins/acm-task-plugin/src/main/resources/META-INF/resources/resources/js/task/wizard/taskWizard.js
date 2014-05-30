/**
 * TaskWizard is namespace component for Task Wizard
 *
 * @author jwu
 */
var TaskWizard = TaskWizard || {
    initialize: function() {
        TaskWizard.Object.initialize();
        TaskWizard.Event.initialize();
        TaskWizard.Page.initialize();
        TaskWizard.Rule.initialize();
        TaskWizard.Service.initialize();
        TaskWizard.Callback.initialize();

        Acm.deferred(TaskWizard.Event.onPostInit);
    }

    ,Object: {}
    ,Event:{}
    ,Page: {}
    ,Rule: {}
    ,Service: {}
    ,Callback: {}

};
