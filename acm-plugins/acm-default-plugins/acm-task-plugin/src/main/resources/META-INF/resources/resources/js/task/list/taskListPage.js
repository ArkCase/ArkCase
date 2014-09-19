/**
 * TaskList.Page
 *
 * manages all dynamic created page element
 *
 * @author jwu
 */
TaskList.Page = {
    initialize : function() {
    }

    ,buildTaskList: function(arr) {
        var html = "";
        if (!Acm.isArrayEmpty(arr)) {
            var len = arr.length;
            for (var i = 0; i < len; i++) {
                var t = arr[i];
                if (0 == i) {
                    Task.setTaskId(t.object_id_s);
                }

//                html += "<li class='list-group-item'><a href='#' class='clear text-ellipsis'> <small class='pull-right'>"
//                    + Acm.getDateFromDatetime(t.dueDate) + "</small><strong class='block'>"
//                    + t.title + "</strong></small></a><input type='hidden' value='" + t.taskId + "' /> </li>";

                html += "<li class='list-group-item'><a href='#' class='thumb-sm pull-left m-r-sm'> <img src='"
                    + App.getContextPath() + "/resources/vendors/acm-3.0/themes/basic/images/a1.png" + "' class='img-circle'>"
                    + "</a>"
                    + "<a href='#' class='clear text-ellipsis'>"
                    + "<strong class='block'>"
                    + t.name + "</strong>"
                    + "<small>"
                    + "Created Date: " + Acm.getDateFromDatetime(t.create_dt)
                    + "</small></br>" 
                    + "<small>"
                    + "Created By: " + t.owner_s + "</small>"
                    + "</a>"
                    + "<input type='hidden' value='" + t.object_id_s + "' /> </li>";
            }
        }

        TaskList.Object.setHtmlUlTasks(html);
        TaskList.Object.registerClickListItemEvents();

        TaskList.Event.doClickLnkListItem();
    }
    ,buildSignatureList: function(arr) {
        var html = "";
        if (!Acm.isArrayEmpty(arr)) {
            var len = arr.length;
            for (var i = 0; i < len; i++) {
                var t = arr[i];         
                html += '<tr class="odd gradeA"><td>' + t.signedBy + '</td><td>' + Acm.getDateFromDatetime(t.signedDate) +'</td></tr>';
            }
        }
        else {
        	html += '<tr class="odd gradeA"><td>None</td><td>&nbsp;</td></tr>';
        }
        
        TaskList.Object.setSignatureList(html);
    }
};
