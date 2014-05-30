/**
 * Task.Callback
 *
 * Callback handlers for server responses
 *
 * @author jwu
 */
Task.Callback = {
    initialize : function() {
//        Acm.Dispatcher.addCallbackListener(this.CallbackResultReceived, this.onResultReceived);
    }

    ,CallbackResultReceived		: "task-result-received"

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
