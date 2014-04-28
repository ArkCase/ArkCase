/**
 * ComplaintWizard.Callback
 *
 * Callback handlers for server responses
 *
 * @author jwu
 */
ComplaintWizard.Callback = {
    initialize : function() {
//        Acm.Dispatcher.addCallbackListener(this.CallbackResultReceived, this.onResultReceived);
    }

    ,CALLBACK_SOME		: "complaint-wizard-result-received"

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
