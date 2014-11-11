/**
 * Audit is namespace component for Audit plugin
 *
 * @author jwu
 */
var Audit = Audit || {
    create: function() {
        Audit.Object.create();
        Audit.Event.create();
        Audit.Page.create();
        Audit.Rule.create();
        Audit.Service.create();
        Audit.Callback.create();
    }

};

