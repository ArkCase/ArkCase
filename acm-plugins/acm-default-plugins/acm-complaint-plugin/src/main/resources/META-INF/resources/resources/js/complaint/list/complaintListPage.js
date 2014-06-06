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
                    + Acm.getDateFromDatetime(c.created) + "</small><strong class='block'>"
                    + c.complaintTitle + "</strong><small>"
                    + c.creator + "</small></a><input type='hidden' value='" + c.complaintId + "' /> </li>";
            }
        }

        ComplaintList.Object.setHtmlUlComplaints(html);
        ComplaintList.Object.registerClickListItemEvents();

        ComplaintList.Event.doClickLnkListItem();
    }

    ,buildTableIncident: function(c) {
        ComplaintList.Object.resetTableIncident();
        var originator = c.originator;
        if (Acm.isEmpty(originator)) {
            return;
        }
        var contactMethods = originator.contactMethods;
        if (Acm.isEmpty(contactMethods)) {
            contactMethods = {};
        }
        var addresses = originator.addresses;
        if (Acm.isEmpty(addresses)) {
            addresses = {};
        }
        var row = "<tr class='odd gradeA'>"
            + "<td><input type='hidden' value='" + originator.id + "' />" + originator.givenName + "</td>"
            + "<td>" + originator.familyName + "</td>"
            + "<td>" + addresses.type + "</td>"
            + "<td>" + contactMethods.value + "</td>"
//            + "<td>" + addresses. + "</td>"
//            + "<td>" + addresses. + "</td>"
//            + "<td>" + addresses. + "</td>"
//            + "<td>" + addresses. + "</td>"
            + "<td><a href='javascript:;' class='edit'>Edit</a></td>"
            + "<td><a href='javascript:;' class='edit'>Delete</a></td>"
            + "</tr>";
        ComplaintList.Object.addRowTableMyTasks(row);
    }
//<tr class="odd gradeA">
//    <td>[First]</td>
//    <td>[Last]</td>
//    <td>[Type]</td>
//    <td>[Phone]</td>
//    <td>[Address]</td>
//    <td>[City]</td>
//    <td>[State]</td>
//    <td>[ZIP]</td>
//    <td><a href="javascript:;" class="edit">Edit</a></td>
//    <td><a href="javascript:;" class="delete">Delete</a></td>
//</tr>
};

