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
    ,onInitialized: function() {
        if (Topbar.Controller.QuickSearch.onInitialized) {Topbar.Controller.QuickSearch.onInitialized();}
        if (Topbar.Controller.Suggestion.onInitialized)  {Topbar.Controller.Suggestion.onInitialized();}
        if (Topbar.Controller.Asn.onInitialized)         {Topbar.Controller.Asn.onInitialized();}
    }

    ,QuickSearch: {
        create : function() {
        }
        ,onInitialized: function() {
        }

        ,VIEW_CHANGED_QUICK_SEARCH_TERM        : "topbar-view-changed-quick-serach-term"       //param: term
        ,viewChangedQuickSearchTerm: function(term) {
            Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_QUICK_SEARCH_TERM, term);
        }
    }

    ,Suggestion: {
        create : function() {
        }
        ,onInitialized: function() {
        }
        ,MODEL_CHANGED_SUGGESTION               : "topbar-model-changed-suggestion"             //param: process
        ,modelChangedSuggestion: function(process) {
            Acm.Dispatcher.fireEvent(this.MODEL_CHANGED_SUGGESTION, process);
        }
    }

    ,Asn: {
        create : function() {
        }
        ,onInitialized: function() {
        }

        ,MODEL_RETRIEVED_ASN_LIST                 : "topbar-model-retrieved-asn-list"                    //param: asnList
        ,MODEL_SAVED_ASN                          : "topbar-model-saved-asn"                             //param: asn
        ,MODEL_UPDATED_ASN_ACTION                 : "topbar-model-updated-asn-action"                    //param: asnId, action
        ,MODEL_UPDATED_ASN_STATUS                 : "topbar-model-updated-asn-status"                    //param: asnId, status
        ,MODEL_DELETED_ASN                        : "topbar-model-deleted-asn"                           //param: asnId
        ,VIEW_CHANGED_ASN_ACTION                  : "topbar-view-changed-asn-action"                     //param: asnId, action
        ,VIEW_CHANGED_ASN_STATUS                  : "topbar-view-changed-asn-status"                     //param: asnId, status
        ,VIEW_DELETED_ASN                         : "topbar-view-deleted-asn"                            //param: asnId

        ,modelRetrievedAsnList: function(asnList) {
            Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_ASN_LIST, asnList);
        }
        ,modelSavedAsn: function(asn) {
            Acm.Dispatcher.fireEvent(this.MODEL_SAVED_ASN, asn);
        }
        ,modelUpdatedAsnAction: function(asnId, action) {
            Acm.Dispatcher.fireEvent(this.MODEL_UPDATED_ASN_ACTION, asnId, action);
        }
        ,modelUpdatedAsnStatus: function(asnId, action) {
            Acm.Dispatcher.fireEvent(this.MODEL_UPDATED_ASN_STATUS, asnId, action);
        }
        ,modelDeletedAsn: function(asnId) {
            Acm.Dispatcher.fireEvent(this.MODEL_DELETED_ASN, asnId);
        }


        ,viewChangedAsnAction: function(asnId, action) {
            Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_ASN_ACTION, asnId, action);
        }
        ,viewChangedAsnStatus: function(asnId, status) {
            Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_ASN_STATUS, asnId, status);
        }
        ,viewDeletedAsn: function(asnId) {
            Acm.Dispatcher.fireEvent(this.VIEW_DELETED_ASN, asnId);
        }


    }
};

