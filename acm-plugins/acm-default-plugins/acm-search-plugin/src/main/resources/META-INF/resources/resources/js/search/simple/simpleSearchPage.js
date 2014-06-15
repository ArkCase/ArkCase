/**
 * SimpleSearch.Page
 *
 * manages all dynamic created page element
 *
 * @author jwu
 */
SimpleSearch.Page = {
    initialize : function() {
    }

    ,fillResults: function(data) {
        SimpleSearch.Object.resetTableMyTasks();
        $.each(data, function(idx, val) {
            if (10 == idx) {
                return false;
            }
            var a = val;
            var z = 1;

            var urlBase = Acm.getContextPath() + "/plugin/complaint/";
            var row = "<tr>"
                + "<td style='width:20%'><a href='" + urlBase + val.complaintId + "'>" + val.complaintId  + "</a></td>"
                + "<td>" + val.complaintTitle + "</td>"
                + "</tr>";
            SimpleSearch.Object.addRowTableMyTasks(row);
        })
    }
};

