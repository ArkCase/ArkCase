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
        var success = false;
        if (response) {
            Acm.Object.setApprovers(response);
            success = true;
        }

        if (!success) {
            Acm.Dialog.error("Failed to retrieve approvers");
        }
    }
    ,onComplaintTypesRetrieved : function(Callback, response) {
        var success = false;
        if (response) {
            Acm.Object.setComplaintTypes(response);
            success = true;
        }

        if (!success) {
            Acm.Dialog.error("Failed to retrieve complaint types");
        }
    }
    ,onPrioritiesRetrieved : function(Callback, response) {
        var success = false;
        if (response) {
            Acm.Object.setPriorities(response);
            success = true;
        }

        if (!success) {
            Acm.Dialog.error("Failed to retrieve priorities");
        }
    }

};
