/**
 * ComplaintList.Object
 *
 * manages screen objects
 *
 * @author jwu
 */
ComplaintList.Object = {
    initialize : function() {
        this.$ulComplaints      = $("#ulComplaints");
        this.$lnkTitle          = $("#caseTitle");
        this.$h4ComplaintNumber = $("#caseTitle").parent();

        this.$lnkIncident       = $("#incident");
        this.$lnkPriority       = $("#priority");
        this.$lnkAssigned       = $("#assigned");
        this.$lnkComplaintType  = $("#type");
        this.$lnkStatus         = $("#status");

        this.$divDetails        = $(".complaintDetails");
        this.$secIncident       = $("#secIncident");
        this.$tableIncident     = $("#secIncident>div>table");

        this.$tableDocDocuments     = $("#secDocDocuments>div>table");

        this.$tableRefDocuments     = $("#secRefDocuments>div>table");


    }

    ,hiliteSelectedItem: function() {
        var cur = Complaint.getComplaintId();
        this.$ulComplaints.find("li").each(function(index) {
            var cid = $(this).find("input[type='hidden']").val();
            if (cid == cur) {
                $(this).addClass("active");
            } else {
                $(this).removeClass("active");
            }
        });
    }


    ,getHtmlUlComplaints: function() {
        return Acm.Object.getHtml(this.$ulComplaints);
    }
    ,setHtmlUlComplaints: function(val) {
        return Acm.Object.setHtml(this.$ulComplaints, val);
    }
    ,registerClickListItemEvents: function() {
        this.$ulComplaints.find("a.thumb-sm").click(function(e) {ComplaintList.Event.onClickLnkListItemImage(this);});
        this.$ulComplaints.find("a.text-ellipsis").click(function(e) {ComplaintList.Event.onClickLnkListItem(this);});
    }
    ,getHiddenComplaintId: function(e) {
        var $hidden = $(e).siblings("input[type='hidden']");
        return $hidden.val();
    }
    ,updateDetail: function(c) {
        this.setTextLnkTitle(c.complaintTitle);
        this.setTextH4ComplaintNumber(" (" + c.complaintNumber + ")");
        this.setTextLnkIncident(Acm.getDateFromDatetime(c.created));
        this.setTextLnkPriority(c.priority);
        this.setTextLnkAssigned(c.assignee);
        this.setTextLnkComplaintType(c.complaintType);
        this.setTextLnkStatus(c.status);

        this.setHtmlDetails(c.details);

        //ComplaintList.Page.buildTableIncident(c);
        ComplaintList.Page.buildTableDocDocuments(c);
        //ComplaintList.Page.buildTableRefDocuments(c);



//todo: jasmine test
//        var $c = $("<h4>beg<a>mid</a>end</h4>");
//        var c1 = Acm.Object.getTextNodeText($c);
//        var c2 = Acm.Object.getTextNodeText($c, 0);
//        var c3 = Acm.Object.getTextNodeText($c, 1);
//        Acm.Object.setTextNodeText($c, "last", -1);
//        var c4 = Acm.Object.getTextNodeText($c);
//
//todo:
// test Acm.setXxx (null, undefined, ""),
//
    }
    ,setTextLnkTitle: function(txt) {
        Acm.Object.setText(this.$lnkTitle, txt);
    }
    ,setTextH4ComplaintNumber: function(txt) {
        Acm.Object.setTextNodeText(this.$h4ComplaintNumber, txt, 1);
    }

    ,setTextLnkIncident: function(txt) {
        Acm.Object.setText(this.$lnkIncident, txt);
    }
    ,setTextLnkPriority: function(txt) {
        Acm.Object.setText(this.$lnkPriority, txt);
    }
    ,setTextLnkAssigned: function(txt) {
        Acm.Object.setText(this.$lnkAssigned, txt);
    }
    ,setTextLnkComplaintType: function(txt) {
        Acm.Object.setText(this.$lnkComplaintType, txt);
    }
    ,setTextLnkStatus: function(txt) {
        Acm.Object.setText(this.$lnkStatus, txt);
    }


    ,setHtmlDetails: function(html) {
        Acm.Object.setHtml(this.$divDetails, html);
    }


    ,resetTableDocDocuments: function() {
        this.$tableDocDocuments.find("tbody > tr").remove();
    }
    ,addRowTableDocDocuments: function(row) {
        this.$tableDocDocuments.find("tbody:last").append(row);
    }

    ,resetTableRefDocuments: function() {
        this.$tableRefDocuments.find("tbody > tr").remove();
    }
    ,addRowTableRefDocuments: function(row) {
        this.$tableRefDocuments.find("tbody:last").append(row);
    }

};




