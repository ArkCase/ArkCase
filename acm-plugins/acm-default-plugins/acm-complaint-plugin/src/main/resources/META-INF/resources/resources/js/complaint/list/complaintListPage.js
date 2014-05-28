/**
 * ComplaintList.Page
 *
 * manages all dynamic created page element
 *
 * @author jwu
 */
ComplaintList.Page = {
    initialize : function() {
    }

    ,buildComplaintList: function(arr) {
        var html = "";
        if (Acm.isNotEmpty(arr)) {
            var len = arr.length;
            for (var i = 0; i < len; i++) {
                var c = arr[i];
                if (0 == i) {
                    Complaint.setComplaintId(c.complaintId);
                }

                html += "<li class='list-group-item'> <a href='#' class='thumb-sm pull-left m-r-sm'> <img src='"
                    + Acm.getContextPath() + "/resources/images/a0.png'" + "class='img-circle'> </a> "
                    + "<a href='#' class='clear text-ellipsis'> <small class='pull-right'>"
                    + ComplaintList.getDateFromDatetime(c.created) + "</small><strong class='block'>"
                    + c.complaintNumber + "</strong><small>"
                    + c.complaintTitle + "</small></a><input type='hidden' value='" + c.complaintId + "' /> </li>";
            }
        }

        ComplaintList.Object.setHtmlUlComplaints(html);
        ComplaintList.Object.registerClickListItemEvents();

        ComplaintList.Event.doClickLnkListItem();
    }
};

