/**
 * Topbar.Callback
 *
 * Callback handlers for server responses
 *
 * @author jwu
 */
Topbar.Callback = {
    initialize : function() {
        Acm.Dispatcher.addEventListener(this.EVENT_TYPEAHEAD_TERMS_RETRIEVED, this.onTypeAheadTermsRetrieved);
    }

    ,EVENT_TYPEAHEAD_TERMS_RETRIEVED  : "topbar-typeahead-terms-retrieved"

    ,onTypeAheadTermsRetrieved : function(Callback, response) {
        var success = false;
        if (response) {
            var typeAheadTerms = response;
            Topbar.Object.setTypeAheadTerms(typeAheadTerms);
            Topbar.Object.useTypeAheadSearch(typeAheadTerms);
            success = true;
        }

        if (!success) {
            Acm.Dialog.error("Failed to retrieve typeAhead terms");
        }
    }
};
