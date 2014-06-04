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
        this.$h4TitleDate = $("#caseTitle").parent();
        this.$divDetails = $(".complaintDetails");

    }

    ,updateDetail: function(c) {
        this.setTextTitle(c.complaintTitle);
        this.setTextTitleDate(" (" + Acm.getDateFromDatetime(c.created) + ")");
        this.setHtmlDetails(c.details);
    }
    ,setTextTitle: function(txt) {
        Acm.Object.setText(this.$lnkTitle, txt);
    }
    ,setTextTitleDate: function(txt) {
        Acm.Object.setTextNodeText(this.$h4TitleDate, txt, 1);
    }
    ,setHtmlDetails: function(html) {
        Acm.Object.setHtml(this.$divDetails, html);
    }

};




