/**
 * Dashboard.Page
 *
 * manages all dynamic created page element
 *
 * @author jwu
 */
Dashboard.Page = {
    initialize : function() {
    }

    ,fillMyTasks: function(data) {
        Dashboard.Object.resetTableMyTasks();
        $.each(data, function(idx, val) {
            if (10 == idx) {
                return false;
            }
            var a = val;
            var z = 1;

            var row = "<tr>"
                + "<td>" + val.complaintId + "</td>"
                + "<td>" + val.complaintTitle + "</td>"
                + "<td>" + val.priority + "</td>"
                + "<td>" + "due" + idx + "</td>"
                + "<td>" + val.status + "</td>"
                + "</tr>";
            Dashboard.Object.addRowTableMyTasks(row);
        })
    }
};

