/**
 * Complaint.Page
 *
 * manages all dynamic created page element
 *
 * @author jwu
 */
Complaint.Page = {
    initialize : function() {
    }

    ,URL_TASK_DETAIL: "/plugin/task/"
    ,URL_NEW_TASK:    "/plugin/task/wizard?parentType=COMPLAINT&parentId="


    ,fillReportSelection: function() {
        var html = "<span>"
            + "<select class='input-sm form-control input-s-sm inline v-middle'>"
            + "<option value='0'>Report of Investigation</option>"
            + "</select>"
            + "</span>";
        Complaint.Object.beforeSpanAddDocument(html);
    }

};

