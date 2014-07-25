/**
 * ComplaintList.Callback
 *
 * Callback handlers for server responses
 *
 * @author jwu
 */
ComplaintList.Callback = {
    initialize : function() {
        Acm.Dispatcher.addEventListener(this.EVENT_LIST_RETRIEVED, this.onListRetrieved);
        Acm.Dispatcher.addEventListener(this.EVENT_DETAIL_RETRIEVED, this.onDetailRetrieved);
        Acm.Dispatcher.addEventListener(this.EVENT_TASKS_RETRIEVED, this.onTasksRetrieved);
    }

    ,EVENT_LIST_RETRIEVED		: "complaint-list-list-retrieved"
    ,EVENT_DETAIL_RETRIEVED		: "complaint-list-detail-retrieved"
    ,EVENT_TASKS_RETRIEVED		: "complaint-list-tasks-retrieved"

    ,onListRetrieved : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to retrieve complaint list:" + response.errorMsg);
        } else {
            ComplaintList.setComplaintList(response);
            ComplaintList.Page.buildComplaintList(response);
            //ComplaintList.Event.doClickLnkListItem();
        }
    }
    ,onDetailRetrieved : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to retrieve complaint detail:" + response.errorMsg);
        } else {
            if (Acm.isNotEmpty(response.complaintId)) {
                var curId = Complaint.getComplaintId();
                if (curId != response.complaintId) {
                    return;         //user clicks another complaint before callback, do nothing
                }

                var complaint = response;
                Complaint.setComplaint(complaint);
                ComplaintList.Object.updateDetail(complaint);
            }
        }
    }
    ,onTasksRetrieved : function(Callback, data) {
        var success = false;
        var err = "Invalid task list data";
        if (data.hasError) {
            err += ":" + response.errorMsg;
        } else {
            if (Acm.isNotEmpty(data.responseHeader)) {
                var responseHeader = data.responseHeader;
                if (Acm.isNotEmpty(responseHeader.status)) {
                    if (0 == responseHeader.status) {
                        var response = data.response;
                        ComplaintList.Object.updateTasks(response);
                        success = true;
                    } else {
                        if (Acm.isNotEmpty(data.error)) {
                            err = data.error.msg + "(" + data.error.code + ")";
                        }
                    }
                }
            }
        }

        if (!success) {
            Acm.Dialog.error(err);
        }
    }
    ,onTasksRetrieved0 : function(Callback, data) {
        var success = false;
        var err = "Invalid task list data";
        if (data.hasError) {
            err += ":" + response.errorMsg;
        } else {
            if (Acm.isNotEmpty(data.responseHeader)) {
                var responseHeader = data.responseHeader;
                if (Acm.isNotEmpty(responseHeader.status)) {
                    if (0 == responseHeader.status) {
                        var response = data.response;
                        ComplaintList.Page.buildTableTasks(response);
                        success = true;
                    } else {
                        if (Acm.isNotEmpty(data.error)) {
                            err = data.error.msg + "(" + data.error.code + ")";
                        }
                    }
                }
            }
        }

        if (!success) {
            Acm.Dialog.error(err);
        }
    }


};
