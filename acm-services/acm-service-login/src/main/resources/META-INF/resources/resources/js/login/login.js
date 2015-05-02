/**
 * Login is namespace component for Login plugin
 *
 * @author jwu
 */
var Login = Login || {
    create: function() {
        if (Login.View.create) {Login.View.create();}

        Application.initSessionData();
    }
    ,onInitialize: function() {
        if (Login.View.onInitialize) {Login.View.onInitialize();}
    }



};

