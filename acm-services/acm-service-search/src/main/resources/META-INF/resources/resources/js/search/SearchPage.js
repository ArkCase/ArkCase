/**
 * Search.Page
 *
 * manages all dynamic created page element
 *
 * @author jwu
 */
Search.Page = {
    create : function() {
    }

    ,buildPanel: function(searchEx) {
        if (searchEx) {
            var html = "";
            for (var i = 0; i < searchEx.length; i++) {
                var pluginEx = searchEx[i]
                if (pluginEx.name && pluginEx.fields) {
                    html += "<div class='line line-dashed b-b line-lg pull-in'></div>"
                        + "<div class='form-group'>"
                        + "    <label class='col-sm-6 control-label'>" + Acm.goodValue(pluginEx.desc, pluginEx.name) + "</label>"
                        + "    <div class='col-sm-4'>"
                        + "        <label class='switch'>"
                        + "            <input type='checkbox'><span></span>"
                        + "        </label>"
                        + "    </div>"
                        + "    <div class='col-sm-12' id='complaintFields' style='display:none;'>";

                    for (var j = 0; j < pluginEx.fields.length; j++) {
                        var field = pluginEx.fields[j];
                        var value = Acm.goodValue(field.value);
                        if (field.name && field.desc && field.type && field.term) {
                            if ("date" == field.type) {
                                html += "<label class='label'>" + field.desc + "</label>"
                                   // + "<div class='col-sm-9'>"
                                    + "<input class='datepicker-input form-control' type='text' data-date-format='mm/dd/yyyy"
                                    + "' name='" + field.name
                                    + "' term='" + field.term
                                    + "' value='" + Acm.goodValue(field.value)
                                    + "' placeholder='" + Acm.goodValue(field.placeholder)
                                    + "' >"
                                    //+ "</div>"
                                    ;

                            } else if ("select" == field.type) {
                                html += "<label class='label'>" + field.desc + "</label>"
                                    + "<select class='form-control"
                                    + "' name='" + field.name
                                    + "' term='" + field.term
                                    + "' value='" + Acm.goodValue(field.value)
                                    + "' placeholder='" + Acm.goodValue(field.placeholder)
                                    + "' >";
                                    if (field.options) {
                                        for (var k = 0; k < field.options.length; k++) {
                                            var option = field.options[k];
                                            html += "<option";
                                            if (option.value) {
                                                html += " value='" + Acm.goodValue(option.value) + "'";
                                            }
                                            if (option.selected) {
                                                html += " selected";
                                            }
                                            html += ">" + Acm.goodValue(option.text) + "</option>";
                                        }
                                    }
                                html += "</select>";
                            } else { //if ("text" == field.type) {
                                html += "<label class='label'>" + field.desc + "</label>"
                                    + "<input type='text' class='form-control"
                                    + "' name='" + field.name
                                    + "' term='" + "dataTitle"
                                    + "' value='" + Acm.goodValue(field.value)
                                    + "' placeholder='" + Acm.goodValue(field.placeholder)
                                    + "' >";
                            }
                        }
                    } //end inner for j
                    //html += "</div></div>";
                    html += "<br/></div></div>";
                }
            } //end outer for i
            if (Acm.isNotEmpty(html)) {
                Search.Object.appendHtmlDivSearchQuery(html);
            }
        }

//        var html = "<div class='line line-dashed b-b line-lg pull-in'></div>"
//            + "<div class='form-group'>"
//            + "    <label class='col-sm-6 control-label'>Complaints</label>"
//            + "    <div class='col-sm-4'>"
//            + "    <label class='switch'>"
//            + "        <input type='checkbox'>"
//            + "            <span></span>"
//            + "        </label>"
//            + "    </div>"
//
//            + "    <div class='col-sm-12' id='complaintFields'>"
//            + "    <label class='label'>Complaint Title111</label>"
//            + "    <input type='text' class='form-control' placeholder='Enter Complaint Title' term='dataTitle'>"
//
//            + "    <label class='label'>Complaint ID</label>"
//            + "    <input type='text' class='form-control' placeholder='Enter Complaint ID' term='dataId'>"
//
//            + "    <label class='label'>Incident Date</label>"
//            + "    <div class='clear'></div>"
//            + "    <label class='label col-sm-3'>From</label>"
//            + "    <div class='col-sm-9'><input class='datepicker-input form-control' type='text' value='' data-date-format='dd-mm-yyyy' ></div>"
//
//            + "        <label class='label col-sm-3'>To</label>"
//            + "        <div class='col-sm-9'> <input class='datepicker-input form-control ' type='text' value='' data-date-format='dd-mm-yyyy' ></div>"
//            + "            <div class='clear'></div>"
//
//            + "            <label for='priority'  class='label' >Priority</label>"
//            + "            <select name='priority' class='form-control'>"
//            + "                <option>Choose Priority</option>"
//            + "                <option selected>Low</option>"
//            + "                <option>Medium</option>"
//            + "                <option>High</option>"
//            + "                <option>Expedited</option>"
//            + "            </select>"
//
//            + "            <label class='label' >Assigned To</label>"
//            + "            <input type='text' class='form-control' placeholder='Enter Assigned To'>"
//
//            + "                <label for='subjectType'  class='label'>Subject Type</label>"
//            + "                <select name='subjectType' class='form-control'>"
//            + "                    <option>Choose Subject Type</option>"
//            + "                </select>"
//
//            + "                <label for='complaintStatus'  class='label'>Status</label>"
//            + "                <select name='complaintStatus' class='form-control'>"
//            + "                    <option>Choose Status</option>"
//            + "                </select>"
//
//            + "            </div>"
//            + "        </div>"
//        ;
//
//        Search.Object.appendHtmlDivSearchQuery(html);
//        Search.Object.setupSwitch("chkComplaints");

    }

};

