/**
 * Complaint.Callback
 *
 * Callback handlers for server responses
 *
 * @author jwu
 */
Complaint.Callback = {
    initialize : function() {
//        ACM.Dispatcher.addCallbackListener(this.CallbackResultReceived, this.onResultReceived);
    }

    ,CallbackResultReceived		: "complaint-result-received"

    ,onResultReceived : function(Callback, response) {
        if (response) {
            if(response.success) {
                if (response.data) {
//                    doSomething();
                }
            }
        }
    }
};
