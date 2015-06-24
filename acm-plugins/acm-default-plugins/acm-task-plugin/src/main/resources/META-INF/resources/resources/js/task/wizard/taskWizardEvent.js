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
        else if(!(data.percentComplete >= 0 && data.percentComplete <= 100))
        {
            Acm.Dialog.info($.t("task:wizard.msg.please-check-percent-complete"));
        }
        else{
            TaskWizard.Service.createAdhocTask(data);
            e.preventDefault();
        }
    }


    ,onPostInit: function() {
    }

    ,onClickBtnChooseAssignee: function() {
        SearchBase.Dialog.create({name: "New Assignee"
            ,title: $.t("search:dialog.taskwizard.title")
            ,prompt: $.t("search:dialog.taskwizard.prompt")
            ,btnGoText: $.t("search:dialog.taskwizard.btnGoText")
            ,btnOkText: $.t("search:dialog.taskwizard.btnOkText")
            ,btnCancelText: $.t("search:dialog.taskwizard.btnCancelText")
            ,filters: [{key: "Object Type", values: ["USER"]}]
            ,onClickBtnPrimary : function(event, ctrl) {
                SearchBase.Dialog.getSelectedRows().each(function () {
                    var record = $(this).data('record');
                    TaskWizard.Object.setValueEdtAssignee(Acm.goodValue(record.name) + " (" + Acm.goodValue(record.id) + ")");
                });
            }
        }).show();
    }
};
