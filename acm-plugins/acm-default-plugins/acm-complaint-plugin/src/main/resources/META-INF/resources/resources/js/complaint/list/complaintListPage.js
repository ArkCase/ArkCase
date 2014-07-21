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

    ,URL_TASK_DETAIL: "/plugin/task/"
    ,URL_NEW_TASK:    "/plugin/task/wizard?parentType=COMPLAINT&parentId="

    ,buildComplaintList: function(arr) {
        var html = "";
        if (Acm.isNotEmpty(arr)) {
            var len = arr.length;
            if (0 < len) {
                var initId = ComplaintList.Object.getInitId();
                if (Acm.isNotEmpty(initId)) {
                    Complaint.setComplaintId(parseInt(initId));
                    ComplaintList.Object.setInitId("");
                } else {
                    Complaint.setComplaintId(arr[0].complaintId);
                }
            }

            for (var i = 0; i < len; i++) {
                var c = arr[i];
                html += "<li class='list-group-item'> <a href='#' class='thumb-sm pull-left m-r-sm'> <img src='"
                    + App.getContextPath() + "/resources/vendors/acm-3.0/themes/basic/images/a0.png" + "' class='img-circle'> </a> "
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

    ,buildTableDocDocuments: function(c) {
        ComplaintList.Object.resetTableDocDocuments();
        var urlBase = App.getContextPath() + ComplaintList.Service.API_DOWNLOAD_DOCUMENT;

        var childObjects = c.childObjects;
        if (Acm.isNotEmpty(childObjects)) {
            var len = childObjects.length;
            for (var i = 0; i < len; i++) {
                var obj = childObjects[i];
                if (obj.targetType == "FILE") {
                    var row = "<tr class='odd gradeA'>"
                        + "<td><a href='" + urlBase + obj.targetId + "'>" + obj.targetId + "</a></td>"
                        + "<td>" + obj.targetName + "</td>"
                        + "<td>" + Acm.getDateFromDatetime(obj.created) + "</td>"
                        + "<td>" + obj.creator + "</td>"
                        + "<td>" + obj.status + "</td>"
                        + "</tr>";
                    ComplaintList.Object.addRowTableDocDocuments(row);
                }
            } //for
        }
    }

    ,buildTableTasks: function(response) {
        ComplaintList.Object.resetTableTasks();
        var urlBase = App.getContextPath() + this.URL_TASK_DETAIL;

        for (var i = 0; i < response.docs.length; i++) {
            var obj = response.docs[i];
            var row = "<tr class='odd gradeA'>"
                + "<td><a href='" + urlBase + obj.object_id_s + "'>" + obj.object_id_s + "</a></td>"
                + "<td>" + obj.name + "</td>"    //? or obj.title_t
                + "<td>" + Acm.getDateFromDatetime(obj.create_dt) + "</td>"
                + "<td>" + "[priority]" + "</td>"
                + "<td>" + "[due]" + "</td>"
                + "<td>" + obj.status_s + "</td>"
                + "<td><select class='input-sm form-control input-s-sm inline v-middle'>"
                +     "<option value='0'>Choose Action</option>"
                +     "<option value='1'>Assign</option>"
                +     "<option value='2'>Unassign</option>"
                + "</select></td>"
                + "</tr>";
            ComplaintList.Object.addRowTableTasks(row);
        }

        ComplaintList.Object.registerChangeSelTasksEvents();
    }

};

