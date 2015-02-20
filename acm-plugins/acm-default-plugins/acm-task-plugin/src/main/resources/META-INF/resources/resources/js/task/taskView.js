/**
 * Task.View
 *
 * @author jwu
 */
Task.View = Task.View || {
    create : function() {
        if (Task.View.MicroData.create)       {Task.View.MicroData.create();}

    }
    ,onInitialized: function() {
        if (Task.View.MicroData.onInitialized)      {Task.View.MicroData.onInitialized();}
    }

    ,MicroData: {
        create : function() {
        }
        ,onInitialized: function() {
        }

    }


};

