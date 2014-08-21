/**
 * Complaint.Callback
 *
 * Callback handlers for server responses
 *
 * @author jwu
 */
Complaint.Callback = {
    initialize : function() {
        Acm.Dispatcher.addEventListener(this.EVENT_LIST_RETRIEVED, this.onListRetrieved);
        Acm.Dispatcher.addEventListener(this.EVENT_DETAIL_RETRIEVED, this.onDetailRetrieved);
        Acm.Dispatcher.addEventListener(this.EVENT_COMPLAIN_SAVED, this.onComplaintSaved);
    }

    ,EVENT_LIST_RETRIEVED		: "complaint-list-retrieved"
    ,EVENT_DETAIL_RETRIEVED		: "complaint-detail-retrieved"
    ,EVENT_COMPLAIN_SAVED		: "complaint-complaint-saved"

    ,onListRetrieved : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to retrieve complaint list:" + response.errorMsg);
        } else {
            var treeInfo = Complaint.Object.getTreeInfo();
            //todo: compare treeInfo with response, if not match do nothing (user click something else before result)
            //if (treeInfo.start != response start) {
            //  return;
            //}
            treeInfo.total = 32;  //= response total

            var complaints = response;
            var start = treeInfo.start;
            Complaint.cachePage.put(start, complaints);

            var key = treeInfo.initKey;
            if (null == key) {
                if (0 < complaints.length) {
                    var complaintId = complaints[0].complaintId;
                    if (0 < complaintId) {
                        key = start + "." + complaintId;
                    }
                }
            } else {
                treeInfo.initKey = null;
            }
            Complaint.Object.refreshTree(key);
        }
    }

    ,onDetailRetrieved : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to retrieve complaint detail:" + response.errorMsg);
        } else {
            if (Acm.isNotEmpty(response.complaintId)) {
                var complaint = response;
                var complaintId = Complaint.getComplaintId();
                if (complaintId != complaint.complaintId) {
                    return;         //user clicks another complaint before callback, do nothing
                }

                //handle single complaint situation
                var treeInfo = Complaint.Object.getTreeInfo();
                if (0 < treeInfo.complaintId) {
                    treeInfo.total = 1;

                    var pageId = treeInfo.start;
                    var complaints = [complaint];
                    Complaint.cachePage.put(pageId, complaints);

                    var key = pageId + "." + treeInfo.complaintId.toString();
                    Complaint.Object.refreshTree(key);
                }

                Complaint.cacheComplaint.put(complaintId, complaint);
                Complaint.Object.populateComplaint(complaint);
            }
        }
    }
    ,onComplaintSaved : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to create or save complaint:" + response.errorMsg);
//        } else {
//            if (Acm.isNotEmpty(response.complaintId)) {
//            }
        }
    }

};
