/**
 * ComplaintWizard is namespace component for Complaint Wizard
 *
 * @author jwu
 */
var ComplaintWizard = ComplaintWizard || {
    initialize: function() {
        ComplaintWizard.Object.initialize();
        ComplaintWizard.Event.initialize();
        ComplaintWizard.Page.initialize();
        ComplaintWizard.Rule.initialize();
        ComplaintWizard.Service.initialize();
        ComplaintWizard.Callback.initialize();

        ComplaintWizard.Event.onPostInit();
    }

    ,Object: {}
    ,Event:{}
    ,Page: {}
    ,Rule: {}
    ,Service: {}
    ,Callback: {}
};

