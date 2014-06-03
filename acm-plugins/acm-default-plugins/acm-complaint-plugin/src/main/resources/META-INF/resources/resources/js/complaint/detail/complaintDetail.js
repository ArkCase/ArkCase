/**
 * ComplaintDetail is namespace component for Complaint Wizard
 *
 * @author jwu
 */
var ComplaintDetail = ComplaintDetail || {
    initialize: function() {
        ComplaintDetail.Object.initialize();
        ComplaintDetail.Event.initialize();
        ComplaintDetail.Page.initialize();
        ComplaintDetail.Rule.initialize();
        ComplaintDetail.Service.initialize();
        ComplaintDetail.Callback.initialize();

        Acm.deferred(ComplaintDetail.Event.onPostInit);
    }

    ,Object: {}
    ,Event:{}
    ,Page: {}
    ,Rule: {}
    ,Service: {}
    ,Callback: {}

//    ,_complaintList: []
//    ,getComplaintDetail: function() {
//        return this._complaintList;
//    }
//    ,setComplaintDetail: function(list) {
//        return this._complaintList = list;
//    }
//    ,findComplaint: function(complaintId) {
//        var found = null;
//        if (Acm.isNotEmpty(this._complaintList)) {
//            var len = this._complaintList.length;
//            for (var i = 0; i < len; i++) {
//                var c = this._complaintList[i];
//                if (complaintId == c.complaintId) {
//                    found = c;
//                    break;
//                }
//            }//end for
//        }
//        return found;
//    }

    //datetime format: "2014-04-30T16:51:33.914+0000"
    ,getDateFromDatetime: function(dt) {
        var d = "";
        if (Acm.isNotEmpty(dt)) {
            d = dt.substr(0, 10);
        }
        return d;
    }
};

