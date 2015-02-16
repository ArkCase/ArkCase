/**
 * Subscribe is namespace component for Subscribe action
 *
 * @author jwu
 */
var Subscribe = Subscribe || {
    create: function() {
        if (Subscribe.Controller.create) {Subscribe.Controller.create();}
        if (Subscribe.Model.create)      {Subscribe.Model.create();}
        if (Subscribe.View.create)       {Subscribe.View.create();}
    }
    ,onInitialized: function() {
        if (Subscribe.Controller.onInitialized) {Subscribe.Controller.onInitialized();}
        if (Subscribe.Model.onInitialized)      {Subscribe.Model.onInitialized();}
        if (Subscribe.View.onInitialized)       {Subscribe.View.onInitialized();}
    }

    ,Controller: {
        create : function() {}
        ,onInitialized: function() {}

        ,MODEL_FOUND_ASSIGNEES                 : "subscribe-model-found-assignees"              //param: assignees
    }

    ,Model: {
        create : function() {
            if (Subscribe.Model.create) {Subscribe.Model.create();}
        }
        ,onInitialized: function() {
            if (Subscribe.Model.onInitialized) {Subscribe.Model.onInitialized();}
        }


    }

    ,Service: {
        create : function() {}
        ,onInitialized: function() {}


    }

    ,View: {
        create : function() {}
        ,onInitialized: function() {}


    }
};
