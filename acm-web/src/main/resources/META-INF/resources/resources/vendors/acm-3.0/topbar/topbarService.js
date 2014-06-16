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

    ,API_GET_SEARCH_TERMS       : "/api/latest/plugin/complaint/types"


    ,getSearchTerms : function() {
        var searchTerms = Topbar.Object.getSearchTerms();
        if (Acm.isEmpty()) {
            Acm.Ajax.asyncGet(Acm.getContextPath() + this.API_GET_SEARCH_TERMS
                ,Topbar.Callback.EVENT_SEARCH_TERMS_RETRIEVED
            );
        } else {
            Topbar.Callback.onSearchTermsRetrieved(Acm.Dispatcher, searchTerms);
        }

    }

};

