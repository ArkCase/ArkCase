/**
 * TaskList.Page
 *
 * manages all dynamic created page element
 *
 * @author jwu
 */
TaskList.Page = {
    create : function() {
    }

	/**
	 * Build the task list on the left panel with attributes like task title, created date, and created by. register each task
	 * object for the click event. Populate the right side task details panel. If no task was listed, hide the right side
	 * panel and display a no task was found message.
	 */
    ,buildTaskList: function(arr) {
        var html = "";
        if (!Acm.isArrayEmpty(arr)) {
            var len = arr.length;

            for (var i = 0; i < len; i++) {
                var t = arr[i];
                var taskId = t.object_id_s;
                if (0 == i) {
                    Task.setTaskId(taskId);
                }

                html += "<li class='list-group-item'><a href='#' class='thumb-sm pull-left m-r-sm'> <img src='"
                    + App.getContextPath() + "/resources/vendors/acm-3.0/themes/basic/images/a1.png" + "' class='img-circle'>"
                    + "</a>"
                    + "<a href='#' class='clear text-ellipsis'>"
                    + "<strong class='block' id='titleName" + taskId +"'>"
                    + t.name + "</strong>"
                    + "<small>"
                    + "Created Date: " + Acm.getDateFromDatetime(t.create_dt)
                    + "</small></br>" 
                    + "<small>"
                    + "Created By: " + t.owner_s + "</small>"
                    + "</a>"
                    + "<input type='hidden' value='" + taskId + "' /> </li>";
            }

            TaskList.Object.setHtmlUlTasks(html);
        	TaskList.Object.registerClickListItemEvents();
        	TaskList.Event.doClickLnkListItem();
        }
        else {
        	TaskList.Object.showObject(TaskList.Object.$taskDetailView, false);
        	TaskList.Object.showObject(TaskList.Object.$noTaskFoundMeassge, true);
            Acm.Dialog.alert("No task assigned to you was found.");

        }    
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
    
    /**
     * Change the title text of the active task of the tasks list view
     */
    ,updateActiveTaskTitle : function(taskId, title) {
    	var titleSel = $("#titleName" + taskId);
    	titleSel.text(title);
    }
};
