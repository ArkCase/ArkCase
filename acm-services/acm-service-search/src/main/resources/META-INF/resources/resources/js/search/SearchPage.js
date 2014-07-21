/**
 * Search.Page
 *
 * manages all dynamic created page element
 *
 * @author jwu
 */
Search.Page = {
    initialize : function() {
    }

    ,fillResults: function(data) {
        Search.Object.resetTableResults();
//        $.each(data, function(idx, val) {
//            if (10 == idx) {
//                return false;
//            }
//            var a = val;
//            var z = 1;
//
//            var urlBase = App.getContextPath() + "/plugin/complaint/";
//            var row = "<tr>"
//                + "<td style='width:20%'><a href='" + urlBase + val.complaintId + "'>" + val.complaintId  + "</a></td>"
//                + "<td>" + val.complaintTitle + "</td>"
//                + "</tr>";
//            Search.Object.addRowTableResults(row);
//        })
    }
};

