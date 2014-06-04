/**
 * ComplaintList is namespace component for Complaint Wizard
 *
 * @author jwu
 */
var ComplaintList = ComplaintList || {
    initialize: function() {
        ComplaintList.Object.initialize();
        ComplaintList.Event.initialize();
        ComplaintList.Page.initialize();
        ComplaintList.Rule.initialize();
        ComplaintList.Service.initialize();
        ComplaintList.Callback.initialize();

        Acm.deferred(ComplaintList.Event.onPostInit);
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

};

