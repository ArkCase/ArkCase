/**
 * Dashboard.Page
 *
 * manages all dynamic created page element
 *
 * @author jwu
 */
Dashboard.Page = {
    initialize : function() {
    }

    ,fillMyTasks: function(data) {
        Dashboard.Object.resetTableMyTasks();
        var urlBase = App.getContextPath() + "/plugin/task/";
        $.each(data, function(idx, val) {
            var dueDate =  Acm.getDateFromDatetime(val.dueDate);
            var status = (val.completed == true)? "Completed": "Active";
            var row = "<tr>"
                + "<td><a href='" + urlBase + val.taskId + "'>" + val.taskId  + "</a></td>"
                + "<td>" + val.title + "</td>"
                + "<td>" + val.priority + "</td>"
                + "<td>" + dueDate + "</td>"
                + "<td>" + status + "</td>"
                + "</tr>";
            Dashboard.Object.addRowTableMyTasks(row);
        })
    }

};

