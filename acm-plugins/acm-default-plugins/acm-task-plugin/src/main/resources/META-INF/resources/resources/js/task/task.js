/**
 * Task is namespace component for Task plugin
 *
 * @author jwu
 */
var Task = Task || {
    create: function() {
        if (Task.Model.create)      {Task.Model.create();}
        if (Task.View.create)       {Task.View.create();}
        if (Task.Controller.create) {Task.Controller.create();}

        if (ObjNav.create) {
            ObjNav.create({name: "task"
                ,$tree             : Task.View.Navigator.$tree
                ,treeArgs          : Task.View.Navigator.getTreeArgs()
                ,$ulFilter         : Task.View.Navigator.$ulFilter
                ,treeFilter        : Task.View.MicroData.treeFilter
                ,$ulSort           : Task.View.Navigator.$ulSort
                ,treeSort          : Task.View.MicroData.treeSort
                ,modelInterface    : Task.Model.interface
            });
        }

        if (SubscriptionOp.create) {
            SubscriptionOp.create({
                getSubscriptionInfo: function() {
                    return {userId: App.getUserName()
                        , objectType: Task.Model.DOC_TYPE_TASK
                        , objectId: ObjNav.Model.getObjectId()
                    };
                }
            });
        }
    }
    ,onInitialized: function() {
        if (Task.Model.onInitialized)      {Task.Model.onInitialized();}
        if (Task.View.onInitialized)       {Task.View.onInitialized();}
        if (Task.Controller.onInitialized) {Task.Controller.onInitialized();}
        if (ObjNav.onInitialized)          {ObjNav.onInitialized();}
        if (SubscriptionOp.onInitialized)  {SubscriptionOp.onInitialized();}
    }


    , DLG_REJECT_TASK_START: 0
    , DLG_REJECT_TASK_N: 10
    , DLG_REJECT_TASK_SORT_DIRECTION: 'ASC'
    , REJECT_COMMENT: 'REJECT_COMMENT'
};

