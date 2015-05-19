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
        if(Acm.isEmpty(data.title)){
            Acm.Dialog.info($.t("task:wizard.msg.please-enter-subject"));
        }
        else{
            TaskWizard.Service.createAdhocTask(data);
            e.preventDefault();
        }
    }


    ,onPostInit: function() {
    }

    ,onClickBtnChooseAssignee: function() {
        SearchBase.showSearchDialog({name: "New Assignee"
            ,title: "Add New Assignee"
            ,prompt: "Enter to search for user.."
            ,btnGoText: "Search Now!"
            ,btnOkText: "Select"
            ,btnCancelText: "Cancel"
            ,filters: [{key: "Object Type", values: ["USER"]}]
            ,onClickBtnPrimary : function(event, ctrl) {
                SearchBase.Dialog.getSelectedRows().each(function () {
                    var record = $(this).data('record');
                    TaskWizard.Object.setValueEdtAssignee(Acm.goodValue(record.name) + " (" + Acm.goodValue(record.id) + ")");
                });
            }
        });
    }
};
