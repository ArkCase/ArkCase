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
        var a0 = $tabMyTasks.find("tr");
        var a1 = $tabMyTasks.find("thead > tr");
        var a2 = $tabMyTasks.find("thead tr");
        var a3 = $tabMyTasks.find("tbody > tr");
        var a4 = $tabMyTasks.find("tbody tr");
        var z = 1;

        $tabMyTasks.find("tbody > tr").remove();
    }
    ,addRowTableMyTasks: function(row) {
        $tabMyTasks.find("tbody:last").append(row);
    }
};




