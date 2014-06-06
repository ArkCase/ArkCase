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

        this.$divDetails        = $(".complaintDetails");
        this.$secIncident       = $("#secIncident");
        this.$tableIncident     = $("#secIncident>div>table");

        this.$tableDocDocuments     = $("#secDocDocuments>div>table");

        this.$tableRefDocuments     = $("#secRefDocuments>div>table");
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


        //ComplaintDetail.Page.buildTableIncident(c);
        ComplaintDetail.Page.buildTableDocDocuments(c);
        //ComplaintDetail.Page.buildTableRefDocuments(c);

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




