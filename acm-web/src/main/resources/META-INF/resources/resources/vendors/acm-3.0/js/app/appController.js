/**
 * App.Controller
 *
 * @author jwu
 */
App.Controller = {
    create : function() {
        if (App.Controller.Login.create)            {App.Controller.Login.create();}
    }
    ,onInitialized: function() {
        if (App.Controller.Login.onInitialized)     {App.Controller.Login.onInitialized();}
    }

    ,Login: {
        create : function() {
        }
        ,onInitialized: function() {
        }

        ,MODEL_DETECTED_IDLE                 : "app-login-model-detected-idle"
        ,modelDetectedIdle: function(assignees) {
            Acm.Dispatcher.fireEvent(this.MODEL_DETECTED_IDLE);
        }
    }
};




