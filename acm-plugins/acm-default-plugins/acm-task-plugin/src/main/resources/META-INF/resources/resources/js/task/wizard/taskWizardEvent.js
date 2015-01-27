/**
 * TaskWizard.Event
 *
 * event handlers for objects
 *
 * @author jwu
 */
TaskWizard.Event = {
    create : function() {
    }

    ,onClickBtnSave : function(e) {
        var data = TaskWizard.Object.getTaskData();
        TaskWizard.Service.createAdhocTask(data);
        e.preventDefault();
    }


    ,onPostInit: function() {
        var data = TaskWizard.getAssignees();
        if (Acm.isEmpty(data)) {
            TaskWizard.Service.getAssignees();
        }
        Acm.keepTrying(TaskWizard.Event._tryInitOwners, 8, 200);
    }

    ,_tryInitOwners: function() {
        var data = TaskWizard.getAssignees();
        if (Acm.isNotEmpty(data)) {
            TaskWizard.Object.initOwners(data);
            return true;
        } else {
            return false;
        }
    }
};
