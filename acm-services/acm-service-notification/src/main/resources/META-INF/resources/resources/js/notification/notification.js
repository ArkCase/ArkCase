/**
 * AcmNotification is namespace component for AcmNotification plugin
 *
 * ('Notification' is taken, so we settle with 'AcmNotification')
 *
 * @author jwu
 */
var AcmNotification = AcmNotification || {
    create: function() {
        if (AcmNotification.Controller.create) {AcmNotification.Controller.create();}
        if (AcmNotification.Model.create)      {AcmNotification.Model.create();}
        if (AcmNotification.View.create)       {AcmNotification.View.create();}

        SearchBase.create("notification"
            ,AcmNotification.View.$edtSearch
            ,AcmNotification.View.$btnSearch
            ,AcmNotification.View.$divFacet
            ,AcmNotification.View.$divResults
            ,AcmNotification.View.args
            ,AcmNotification.View.jtDataMaker
        );
    }
    ,onInitialized: function() {
        if (AcmNotification.Controller.onInitialized) {AcmNotification.Controller.onInitialized();}
        if (AcmNotification.Model.onInitialized)      {AcmNotification.Model.onInitialized();}
        if (AcmNotification.View.onInitialized)       {AcmNotification.View.onInitialized();}
    }
};
