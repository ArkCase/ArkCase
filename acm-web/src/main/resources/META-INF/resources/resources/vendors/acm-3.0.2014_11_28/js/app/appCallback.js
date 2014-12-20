/**
 * App.Callback
 *
 * Callback handlers for server responses
 *
 * @author jwu
 */
App.Callback = {
    create : function() {
        Acm.Dispatcher.addEventListener(this.EVENT_APPROVERS_RETRIEVED, this.onApproversRetrieved);
        Acm.Dispatcher.addEventListener(this.EVENT_COMPLAINT_TYPES_RETRIEVED, this.onComplaintTypesRetrieved);
        Acm.Dispatcher.addEventListener(this.EVENT_PRIORIES_RETRIEVED, this.onPrioritiesRetrieved);
    }

    ,EVENT_APPROVERS_RETRIEVED        : "app-approvers-retrieved"
    ,EVENT_COMPLAINT_TYPES_RETRIEVED  : "app-complaint-types-retrieved"
    ,EVENT_PRIORIES_RETRIEVED         : "app-priorities-retrieved"


    ,onApproversRetrieved : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to retrieve approvers:" + response.errorMsg);
        } else {
            App.Object.setApprovers(response);
        }
    }
    ,onComplaintTypesRetrieved : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to retrieve complaint types:" + response.errorMsg);
        } else {
            App.Object.setComplaintTypes(response);
        }
    }
    ,onPrioritiesRetrieved : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to retrieve priorities:" + response.errorMsg);
        } else {
            App.Object.setPriorities(response);
        }
    }

};
