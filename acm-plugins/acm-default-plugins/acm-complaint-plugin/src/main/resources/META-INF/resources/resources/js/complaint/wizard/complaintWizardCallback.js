/**
 * ComplaintWizard.Callback
 *
 * Callback handlers for server responses
 *
 * @author jwu
 */
ComplaintWizard.Callback = {
    initialize : function() {
//        Acm.Dispatcher.addEventListener(this.EVENT_APPROVERS_RETRIEVED, this.onApproversRetrieved);
//        Acm.Dispatcher.addEventListener(this.EVENT_COMPLAINT_TYPES_RETRIEVED, this.onComplaintTypesRetrieved);
//        Acm.Dispatcher.addEventListener(this.EVENT_PRIORIES_RETRIEVED, this.onPrioritiesRetrieved);
        Acm.Dispatcher.addEventListener(this.EVENT_COMPLAIN_SAVED, this.onComplaintSaved);
        Acm.Dispatcher.addEventListener(this.EVENT_COMPLAIN_SUBMITTED, this.onComplaintSubmitted);
    }

//    ,EVENT_APPROVERS_RETRIEVED        : "complaint-wizard-approvers-retrieved"
//    ,EVENT_COMPLAINT_TYPES_RETRIEVED  : "complaint-wizard-complaint-types-retrieved"
//    ,EVENT_PRIORIES_RETRIEVED         : "complaint-wizard-priorities-retrieved"
    ,EVENT_COMPLAIN_SAVED		      : "complaint-wizard-complaint-saved"
    ,EVENT_COMPLAIN_SUBMITTED         : "complaint-wizard-complaint-submitted"


//    ,onApproversRetrieved : function(Callback, response) {
//        var success = false;
//        if (response) {
//            ComplaintWizard.Object.initApprovers(response);
//            success = true;
//        }
//
//        if (!success) {
//            Acm.Dialog.error("Failed to retrieve approvers");
//        }
//    }
//    ,onComplaintTypesRetrieved : function(Callback, response) {
//        var success = false;
//        if (response) {
//            ComplaintWizard.Object.initComplaintTypes(response);
//            success = true;
//        }
//
//        if (!success) {
//            Acm.Dialog.error("Failed to retrieve complaint types");
//        }
//    }
//    ,onPrioritiesRetrieved : function(Callback, response) {
//        var success = false;
//        if (response) {
//            ComplaintWizard.Object.initPriorities(response);
//            success = true;
//        }
//
//        if (!success) {
//            Acm.Dialog.error("Failed to retrieve priorities");
//        }
//    }
    ,onComplaintSaved : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to create or save complaint:" + response.errorMsg);
        } else {
            if (Acm.isNotEmpty(response.complaintId)) {
                ComplaintWizard.Object.setComplaintData(response);
                ComplaintWizard.Object.setTextH3Title(Acm.goodValue(response.complaintNumber));
                Acm.Dialog.info("Complaint data saved");
            }
        }
    }
    ,onComplaintSubmitted : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Error occurred for complaint approval submission:" + response.errorMsg);
        } else {
                //validate response.length == Complaint.getComplaint().approvers.length
            App.gotoPage(ComplaintWizard.Page.URL_DASHBOARD);
        }
    }
};
