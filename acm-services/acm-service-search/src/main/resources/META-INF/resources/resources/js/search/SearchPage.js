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

    ,buildPanel: function(searchDef) {
        var html = "<div class='line line-dashed b-b line-lg pull-in'></div>"
            + "<div class='form-group'>"
            + "    <label class='col-sm-6 control-label'>Complaints</label>"
            + "    <div class='col-sm-4'>"
            + "    <label class='switch'>"
            + "        <input type='checkbox' id='chkComplaints'>"
            + "            <span></span>"
            + "        </label>"
            + "    </div>"

            + "    <div class='col-sm-12' id='complaintFields'>"
            + "    <label class='label'>Complaint Title22222</label>"
            + "    <input type='text' id='edtComplaintTitle' class='form-control' placeholder='Enter Complaint Title'>"
            + "    <label class='label'>Complaint ID</label>"
            + "    <input type='text' id='edtComplaintID' class='form-control' placeholder='Enter Complaint ID'>"
            + "    <label class='label'>Incident Date</label>"
            + "    <div class='clear'></div>"
            + "    <label class='label col-sm-3'>From</label>"
            + "    <div class='col-sm-9'><input class='datepicker-input form-control' id='edtComplaintDateStartRange' type='text' value='' data-date-format='dd-mm-yyyy' ></div>"
            + "        <label class='label col-sm-3'>To</label>"
            + "        <div class='col-sm-9'> <input class='datepicker-input form-control ' id='edtComplaintDateEndRange' type='text' value='' data-date-format='dd-mm-yyyy' ></div>"
            + "            <div class='clear'></div>"
            + "            <label for='priority'  class='label' >Priority</label>"
            + "            <select name='priority' class='form-control' id='selComplaintPriority'>"
            + "                <option>Choose Priority</option>"
            + "                <option selected>Low</option>"
            + "                <option>Medium</option>"
            + "                <option>High</option>"
            + "                <option>Expedited</option>"
            + "            </select>"
            + "            <label class='label' >Assigned To</label>"
            + "            <input type='text' class='form-control' id='edtComplaintAssignee' placeholder='Enter Assigned To'>"
            + "                <label for='subjectType'  class='label'>Subject Type</label>"
            + "                <select name='subjectType' class='form-control' id='selComplaintSubjectType' >"
            + "                    <option>Choose Subject Type</option>"
            + "                </select>"
            + "                <label for='complaintStatus'  class='label'>Status</label>"
            + "                <select name='complaintStatus' class='form-control' id='selComplaintStatus' >"
            + "                    <option>Choose Status</option>"
            + "                </select>"
            + "            </div>"
            + "        </div>"
        ;

        Search.Object.appendHtmlDivSearchQuery(html);
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

