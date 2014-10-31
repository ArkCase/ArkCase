/**
 * CaseFile.Page
 *
 * manages all dynamic created page element
 *
 * @author jwu
 */
CaseFile.Page = {
    create : function() {
    }

    ,URL_TASK_DETAIL: "/plugin/task/"
    ,URL_NEW_TASK:    "/plugin/task/wizard?parentType=CASE_FILE&parentId="


    ,fillReportSelection: function() {
        var html = "<span>"
            + "<select class='input-sm form-control input-s-sm inline v-middle'>"
            + "<option value='roi'>Report of Investigation</option>"
            + "</select>"
            + "</span>";
        CaseFile.Object.beforeSpanAddRoi(html);
    }

};

