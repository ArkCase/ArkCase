/**
 * Topbar.Controller
 *
 * @author jwu
 */
Topbar.Controller = {
    create : function() {
        if (Topbar.Controller.Asn.create) {Topbar.Controller.Asn.create();}
    }
    ,initialize: function() {
        if (Topbar.Controller.Asn.initialize) {Topbar.Controller.Asn.initialize();}
    }

    ,Suggestion: {
        create : function() {
        }
        ,initialize: function() {
        }

        ,onModelChangeSuggestion: function(process) {
            Topbar.View.Suggestion.ctrlUpdateSuggestion(process);
        }
    }

    ,Asn: {
        create : function() {
        }
        ,initialize: function() {
            Acm.Timer.startWorker(App.getContextPath() + "/resources/js/acmTimer.js");
            Acm.Timer.registerListener("AsnWatch"
                ,16
                ,function() {
                    Topbar.Model.Asn.ctrlRetrieveAsnList(App.getUserName());
                    return true;
                }
            );
        }

        ,onModelChangedAsnList: function(asnList) {
            Topbar.View.Asn.ctrlUpdateAsnList(asnList);
        }
        ,onModelChangedAsnListError: function(errorMsg) {
            Topbar.View.Asn.ctrlNotifyAsnListError(errorMsg);
        }
        ,onModelChangedAsnListUpdateError: function(errorMsg) {
            Topbar.View.Asn.ctrlNotifyAsnListUpdateError(errorMsg);
        }
        ,onModelChangedAsnListUpdateSuccess: function() {
            Topbar.View.Asn.ctrlNotifyAsnListUpdateSuccess();
        }

        ,onViewChangedAsnAction: function(asnId, action) {
            Topbar.Model.Asn.ctrlUpdateAsnAction(asnId, action);
        }
    }
};

