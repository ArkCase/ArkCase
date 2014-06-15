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
        if (Acm.isNotEmpty(arr)) {
            var len = arr.length;
            for (var i = 0; i < len; i++) {
                var t = arr[i];
                if (0 == i) {
                    Task.setTaskId(t.taskId);
                }

                html += "<li class='list-group-item'><a href='#' class='clear text-ellipsis'> <small class='pull-right'>"
                    + Acm.getDateFromDatetime(t.dueDate) + "</small><small>"
                    + t.title + "</small></a><input type='hidden' value='" + t.taskId + "' /> </li>";
            }
        }

        TaskList.Object.setHtmlUlTasks(html);
        TaskList.Object.registerClickListItemEvents();

        TaskList.Event.doClickLnkListItem();
    }
};

