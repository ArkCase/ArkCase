/**
 * Topbar.Controller
 *
 * @author jwu
 */
Topbar.Controller = {
    create : function() {
        if (Topbar.Controller.QuickSearch.create) {Topbar.Controller.QuickSearch.create();}
        if (Topbar.Controller.Suggestion.create)  {Topbar.Controller.Suggestion.create();}
        if (Topbar.Controller.Asn.create)         {Topbar.Controller.Asn.create();}
    }
    ,initialize: function() {
        if (Topbar.Controller.QuickSearch.initialize) {Topbar.Controller.QuickSearch.initialize();}
        if (Topbar.Controller.Suggestion.initialize)  {Topbar.Controller.Suggestion.initialize();}
        if (Topbar.Controller.Asn.initialize)         {Topbar.Controller.Asn.initialize();}
    }

    ,QuickSearch: {
        create : function() {
        }
        ,initialize: function() {
        }

        ,VIEW_CHANGED_QUICK_SEARCH_TERM        : "topbar-view-changed-quick-serach-term"       //param: term
        ,viewChangedQuickSearchTerm: function(term) {
            Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_QUICK_SEARCH_TERM, term);
        }
    }

    ,Suggestion: {
        create : function() {
        }
        ,initialize: function() {
        }
        ,MODEL_CHANGED_SUGGESTION               : "topbar-model-changed-suggestion"             //param: process
        ,modelChangedSuggestion: function(process) {
            Acm.Dispatcher.fireEvent(this.MODEL_CHANGED_SUGGESTION, process);
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

        ,MODEL_RETRIEVED_ASN_LIST                 : "topbar-model-retrieved-asn-list"                    //param: asnList
        ,MODEL_UPDATED_ASN_LIST                   : "topbar-model-updated-asn-list"                      //param: asnList
        ,VIEW_CHANGED_ASN_ACTION                  : "topbar-view-changed-asn-action"                     //param: asnId, action

        ,modelRetrievedAsnList: function(asnList) {
            Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_ASN_LIST, asnList);
        }
        ,modelUpdatedAsnList: function(asnList) {
            Acm.Dispatcher.fireEvent(this.MODEL_UPDATED_ASN_LIST, asnList);
        }
        ,viewChangedAsnAction: function(asnId, action) {
            Acm.Dispatcher.fireEvent(this.MODEL_UPDATED_ASN_LIST, asnId, action);
        }



//        ,onModelChangedAsnList: function(asnList) {
//            Topbar.View.Asn.ctrlUpdateAsnList(asnList);
//        }
//        ,onModelChangedAsnListError: function(errorMsg) {
//            Topbar.View.Asn.ctrlNotifyAsnListError(errorMsg);
//        }
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

