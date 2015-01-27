/**
 * Complaint.Page
 *
 * manages all dynamic created page element
 *
 * @author jwu
 */
Complaint.Page = {
    create : function() {
    }

    ,URL_TASK_DETAIL: "/plugin/task/"
    ,URL_NEW_TASK:    "/plugin/task/wizard?parentType=COMPLAINT&reference="




    ,fillReportSelection: function() {
    	var formDocuments = null;
    	try {
    		formDocuments = JSON.parse(Complaint.Object.getFormDocuments());
    	}catch(e) {
    		
    	}
        var html = "<span>"
            + "<select class='input-sm form-control input-s-sm inline v-middle'>"
            + "<option value=''>Document Type</option>";
        
        if (formDocuments != null && formDocuments.length > 0) {
        	for (var i = 0; i < formDocuments.length; i ++) {
        		html += "<option value='" + formDocuments[i]["value"] + "'>" + formDocuments[i]["label"] + "</option>"
        	}
        }
        
        html += "</select>"
            + "</span>";
        Complaint.Object.beforeSpanAddDocument(html);
    }

};

