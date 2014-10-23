/**
 * Complaint.Callback
 *
 * Callback handlers for server responses
 *
 * @author jwu
 */
Complaint.Callback = {
    create : function() {
        Acm.Dispatcher.addEventListener(this.EVENT_LIST_RETRIEVED, this.onListRetrieved);
        Acm.Dispatcher.addEventListener(this.EVENT_DETAIL_RETRIEVED, this.onDetailRetrieved);
        Acm.Dispatcher.addEventListener(this.EVENT_COMPLAIN_SAVED, this.onComplaintSaved);
        /*Acm.Dispatcher.addEventListener(this.EVENT_COMPLAINT_PERSON_LIST_RETRIEVED, this.onComplaintPersonListRetrieved);
        Acm.Dispatcher.addEventListener(this.EVENT_PERSON_SAVED, this.onPersonSaved);*/
        Acm.Dispatcher.addEventListener(this.EVENT_PERSON_ASSOCIATION_SAVED, this.onPersonAssociationSaved);
        Acm.Dispatcher.addEventListener(this.EVENT_PERSON_DELETED, this.onPersonDeleted);
        Acm.Dispatcher.addEventListener(this.EVENT_NOTE_SAVED, this.onNoteSaved);
        Acm.Dispatcher.addEventListener(this.EVENT_NOTE_DELETED, this.onNoteDeleted);
        Acm.Dispatcher.addEventListener(this.EVENT_NOTE_LIST_RETRIEVED, this.onNotesListRetrieved);




    }

    ,EVENT_LIST_RETRIEVED		: "complaint-list-retrieved"
    ,EVENT_DETAIL_RETRIEVED		: "complaint-detail-retrieved"
    ,EVENT_COMPLAIN_SAVED		: "complaint-complaint-saved"
    ,EVENT_PERSON_ASSOCIATION_SAVED : "person-association-saved"
    ,EVENT_PERSON_DELETED       : "person-record-deleted"
    ,EVENT_NOTE_SAVED           : "object-note-saved"
    ,EVENT_NOTE_DELETED         : "object-note-deleted"
    ,EVENT_NOTE_LIST_RETRIEVED  : "object-note-listed"
    /*,EVENT_COMPLAINT_PERSON_LIST_RETRIEVED : "complaint-person-list-retrieved"
    ,EVENT_PERSON_SAVED         : "complaint-person-saved"*/
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

                var notes = Complaint.cacheNoteList.get(complaintId);
                if (notes) {
                    Complaint.Object.populateComplaint(complaint);
                } else {
                    Complaint.Service.retrieveNotes(complaintId);
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
            Complaint.cacheComplaint.put(response.complaintId, response);
            /*Complaint.Object.refreshJTablePeople();
            Complaint.Object.refreshJTableInitiator();*/
        }
    }
/*    ,onPersonSaved: function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to create or save person:" + response.errorMsg);
        } else {
            Acm.Dialog.info("Able to create or save person");

        }
    }*/
    ,onPersonAssociationSaved: function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to create or save person:" + response.errorMsg);
        } else {
            //Acm.Dialog.info("Able to create or save person");
        }
    }
    ,onPersonDeleted: function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to delete person:" + response.errorMsg);
        } else {
            //Acm.Dialog.info("Able to delete person");
        }
    }
    ,onNoteSaved: function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to create or save note:" + response.errorMsg);
        } else {
            //update the note list cache manually instead of calling service
            //next refresh will update the cache anyway
            var complaintId = Complaint.getComplaintId();
            var oldNotesList = Complaint.cacheNoteList.get(complaintId);
            var updatedNotesList = oldNotesList;
            updatedNotesList.push(response);
            Complaint.cacheNoteList.put(complaintId, updatedNotesList);
        }
    }
    ,onNoteDeleted : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to delete note:" + response.errorMsg);
        } else {
            var complaintId = Complaint.getComplaintId();
            Complaint.Service.retrieveNotes(complaintId);
        }
    }
    ,onNotesListRetrieved : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to list notes:" + response.errorMsg);
        } else {
//            Acm.Dialog.info("Able to list notes");
            Complaint.cacheNoteList.put(Complaint.getComplaintId(),response)
            Complaint.Object.refreshJTableNotes();
        }
    }

 /*   ,onComplaintPersonListRetrieved : function(Callback, response) {
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
    }*/
};
