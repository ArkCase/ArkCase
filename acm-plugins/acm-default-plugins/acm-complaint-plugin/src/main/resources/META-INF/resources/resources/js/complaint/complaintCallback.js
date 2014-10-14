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
        Acm.Dispatcher.addEventListener(this.EVENT_COMPLAINT_PERSON_LIST_RETRIEVED, this.onComplaintPersonListRetrieved);
        Acm.Dispatcher.addEventListener(this.EVENT_PERSON_SAVED, this.onPersonSaved);

    }

    ,EVENT_LIST_RETRIEVED		: "complaint-list-retrieved"
    ,EVENT_DETAIL_RETRIEVED		: "complaint-detail-retrieved"
    ,EVENT_COMPLAIN_SAVED		: "complaint-complaint-saved"
    ,EVENT_COMPLAINT_PERSON_LIST_RETRIEVED : "complaint-person-list-retrieved"
    ,EVENT_PERSON_SAVED         : "complaint-person-saved"
    ,onListRetrieved : function(Callback, response) {   	
        if (response.hasError) {
            Acm.Dialog.error("Failed to retrieve complaint list:" + response.errorMsg);
        } else {
            if (response && response.response && response.responseHeader) {
                var responseData = response.response;

                var treeInfo = Complaint.Object.getTreeInfo();
                //todo: compare treeInfo with response, if not match do nothing (user click something else before result)
                //if (treeInfo.start != response start) {
                //  return;
                //}



                treeInfo.total = responseData.numFound;  //= response total

                var complaints = responseData.docs;
                var start = treeInfo.start;
                Complaint.cachePage.put(start, complaints);

                var key = treeInfo.initKey;
                if (null == key) {
                    if (0 < complaints.length) {
                        var complaintId = parseInt(complaints[0].object_id_s);
                        if (0 < complaintId) {
                            key = start + "." + complaintId;
                        }
                    }
                } else {
                    treeInfo.initKey = null;
                }
                Complaint.Object.refreshTree(key);
            }
        } //end outer else
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
                    var complaintSolr = {};
                    complaintSolr.author = complaint.creator;
                    complaintSolr.author_s = complaint.creator;
                    complaintSolr.create_dt = complaint.created;
                    complaintSolr.last_modified = complaint.modified;
                    complaintSolr.modifier_s = complaint.modifier;
                    complaintSolr.name = complaint.complaintNumber;
                    complaintSolr.object_id_s = complaint.complaintId;
                    complaintSolr.object_type_s = App.OBJTYPE_COMPLAINT;
                    complaintSolr.owner_s = complaint.creator;
                    complaintSolr.status_s = complaint.status;
                    complaintSolr.title_t = complaint.complaintTitle;



                    var complaints = [complaintSolr];
                    Complaint.cachePage.put(pageId, complaints);

                    var key = pageId + "." + treeInfo.complaintId.toString();
                    Complaint.Object.refreshTree(key);
                }


                Complaint.cacheComplaint.put(complaintId, complaint);

                var people = Complaint.cachePersonList.get(complaintId);
                if (people) {
                    Complaint.Object.populateComplaint(complaint);
                } else {
                    Complaint.Service.retrievePersonListComplaint(complaintId);


                }

                Complaint.Object.populateComplaint(complaint);
            }
        }
    }
    ,onComplaintSaved : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to create or save complaint:" + response.errorMsg);
        } else
        {
            Complaint.Callback.onDetailRetrieved(Callback, response);
        }
    }
    ,onPersonSaved: function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to create or save person:" + response.errorMsg);
//        } else {
//            if (Acm.isNotEmpty(response.complaintId)) {
//            }
        }
    }

    ,onComplaintPersonListRetrieved : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to create or retrieve person list:" + response.errorMsg);
        } else {
            if (Acm.isNotEmpty(response)) {
                var c = Complaint.getComplaint();
                if(response){
                    Complaint.cachePersonList.put(c.complaintId,response);
                    Complaint.Object.refreshJTablePeople();
                }

            }
        }
    }
};
