/**
 * Dashboard.Object
 *
 * manages screen objects
 *
 * @author jwu
 */
Dashboard.Object = {
    initialize : function() {
        $tabMyTasks = $("#tabMyTasks");
        $tabMyTasksAllRows = $("#tabMyTasks tr");
        $tabMyTasksLastRow = $("#tabMyTasks tr");
    }

    ,resetTableMyTasks: function() {
        $tabMyTasks.find("tbody > tr").remove();
    }
    ,addRowTableMyTasks: function(row) {
        $tabMyTasks.find("tbody:last").append(row);
    }
};




