/**
 * SimpleSearch.Callback
 *
 * Callback handlers for server responses
 *
 * @author jwu
 */
SimpleSearch.Callback = {
    initialize : function() {
        Acm.Dispatcher.addEventListener(this.EVENT_RESULT_RETRIEVED, this.onResultRetrieved);
    }

    ,EVENT_RESULT_RETRIEVED		: "search-result-retrieved"

    ,onResultRetrieved : function(Callback, response) {
        var success = false;
        if (response) {
            SimpleSearch.Page.fillResults(response);
            success = true;
        }

        if (!success) {
            Acm.Dialog.error("Failed to retrieve my tasks");
        }
    }
};


