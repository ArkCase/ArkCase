/**
 * TaskWizard.Event
 *
 * event handlers for objects
 *
 * @author jwu
 */
TaskWizard.Event = {
    initialize : function() {
    }

    ,onClickBtnSave : function(e) {
        var data = TaskWizard.Object.getTaskData();
        TaskWizard.Service.createAdhocTask(data);
        e.preventDefault();
    }


    ,onPostInit: function() {
        TaskWizard.Service.getAssignees();

        TaskWizard.Object.setValueEdtDueDate(Acm.getCurrentDay());
        TaskWizard.Object.showEdtDueDate(true);     //temp fix: show a non-display due date to get rid of initial popup

        TaskWizard.Object.setValueEdtPriority(50);
    }
};
