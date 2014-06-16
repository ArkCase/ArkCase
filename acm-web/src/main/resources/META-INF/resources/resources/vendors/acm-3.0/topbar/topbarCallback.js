/**
 * Topbar.Callback
 *
 * Callback handlers for server responses
 *
 * @author jwu
 */
Topbar.Callback = {
    initialize : function() {
        Acm.Dispatcher.addEventListener(this.EVENT_SEARCH_TERMS_RETRIEVED, this.onSearchTermsRetrieved);
    }

    ,EVENT_SEARCH_TERMS_RETRIEVED  : "topbar-search-terms-retrieved"

    ,onSearchTermsRetrieved : function(Callback, response) {
        var success = false;
        if (response) {
            var searchTerms = response;
            Topbar.Object.setSearchTerms(searchTerms);
            Topbar.Object.useTypeAheadSearch(searchTerms);
            success = true;
        }

        if (!success) {
            Acm.Dialog.error("Failed to retrieve search terms");
        }
    }
};
