/**
 * Search.Callback
 *
 * Callback handlers for server responses
 *
 * @author jwu
 */
Search.Callback = {
    initialize : function() {
        Acm.Dispatcher.addEventListener(this.EVENT_RESULT_RETRIEVED, this.onResultRetrieved);
    }

    ,EVENT_RESULT_RETRIEVED		: "search-result-retrieved"

    //for demo
    ,onResultRetrieved : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to retrieve search results:" + response.errorMsg);
        } else {
            var data = [response];
            Search.Page.fillResults(data);
        }
    }
    ,onResultRetrieved_save : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to retrieve search results:" + response.errorMsg);
        } else {
            Search.Page.fillResults(response);
        }
    }
};


