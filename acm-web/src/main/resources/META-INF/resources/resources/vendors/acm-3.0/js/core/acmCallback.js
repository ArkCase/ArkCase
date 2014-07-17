/**
 * Acm.Callback
 *
 * Callback handlers for server responses
 *
 * @author jwu
 */
Acm.Callback = {
    initialize : function() {
        Acm.Dispatcher.addEventListener(this.EVENT_APPROVERS_RETRIEVED, this.onApproversRetrieved);
        Acm.Dispatcher.addEventListener(this.EVENT_COMPLAINT_TYPES_RETRIEVED, this.onComplaintTypesRetrieved);
        Acm.Dispatcher.addEventListener(this.EVENT_PRIORIES_RETRIEVED, this.onPrioritiesRetrieved);
    }

    ,EVENT_APPROVERS_RETRIEVED        : "acm-approvers-retrieved"
    ,EVENT_COMPLAINT_TYPES_RETRIEVED  : "acm-complaint-types-retrieved"
    ,EVENT_PRIORIES_RETRIEVED         : "acm-priorities-retrieved"


    ,onApproversRetrieved : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to retrieve approvers:" + response.errorMsg);
        } else {
            Acm.Object.setApprovers(response);
        }
    }
    ,onComplaintTypesRetrieved : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to retrieve complaint types:" + response.errorMsg);
        } else {
            Acm.Object.setComplaintTypes(response);
        }
    }
    ,onPrioritiesRetrieved : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to retrieve priorities:" + response.errorMsg);
        } else {
            Acm.Object.setPriorities(response);
        }
    }

};
