/**
 * Topbar.Service
 *
 * manages all service call to application server
 *
 * @author jwu
 */
Topbar.Service = {
    initialize : function() {
    }

    ,API_TYPEAHEAD_SUGGESTION       : "/resources/ctrs.json"
    ,API_GET_TYPEAHEAD_TERMS       : "/api/latest/plugin/complaint/types"


    ,getTypeAheadTerms : function() {
        var typeAheadTerms = Topbar.Object.getTypeAheadTerms();
        if (Acm.isEmpty()) {
            Acm.Ajax.asyncGet(Acm.getContextPath() + this.API_GET_TYPEAHEAD_TERMS
                ,Topbar.Callback.EVENT_TYPEAHEAD_TERMS_RETRIEVED
            );
        } else {
            Topbar.Callback.onTypeAheadTermsRetrieved(Acm.Dispatcher, typeAheadTerms);
        }

    }

};

