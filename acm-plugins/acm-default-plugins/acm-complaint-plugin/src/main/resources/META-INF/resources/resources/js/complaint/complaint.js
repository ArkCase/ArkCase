/**
 * Complaint is namespace component for Complaint
 *
 * @author jwu
 */
var Complaint = Complaint || {
    initialize: function() {
        Complaint.Object.initialize();
        Complaint.Event.initialize();
        Complaint.Page.initialize();
        Complaint.Rule.initialize();
        Complaint.Service.initialize();
        Complaint.Callback.initialize();

        Acm.deferred(Complaint.Event.onPostInit);
    }

    ,Object: {}
    ,Event:{}
    ,Page: {}
    ,Rule: {}
    ,Service: {}
    ,Callback: {}


    ,_complaintList: []
    ,getComplaintList: function() {
        return this._complaintList;
    }
    ,setComplaintList: function(list) {
        return this._complaintList = list;
    }
    ,findComplaint: function(complaintId) {
        var found = null;
        if (Acm.isNotEmpty(this._complaintList)) {
            var len = this._complaintList.length;
            for (var i = 0; i < len; i++) {
                var c = this._complaintList[i];
                if (complaintId == c.complaintId) {
                    found = c;
                    break;
                }
            }//end for
        }
        return found;
    }

    ,_singleObject: false
    ,isSingleObject: function() {
        return this._singleObject;
    }
    ,setSingleObject: function(single) {
        this._singleObject = single;
    }

};

