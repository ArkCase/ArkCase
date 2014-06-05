/**
 * ComplaintDetail.Object
 *
 * manages screen objects
 *
 * @author jwu
 */
ComplaintDetail.Object = {
    initialize : function() {
        var items = $(document).items();
        var complaintId = items.properties("complaintId").itemValue();
        Complaint.setComplaintId(complaintId);

        this.$lnkTitle = $("#caseTitle");
        this.$h4ComplaintNumber = $("#caseTitle").parent();

        this.$lnkIncident       = $("#incident");
        this.$lnkPriority       = $("#priority");
        this.$lnkAssigned       = $("#assigned");
        this.$lnkComplaintType  = $("#type");
        this.$lnkStatus         = $("#status");

        this.$divDetails = $(".complaintDetails");

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

};




