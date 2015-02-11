/**
 * AcmNotification.Model
 *
 * @author jwu
 */
AcmNotification.Model = {
    create : function() {
        if (AcmNotification.Service.create){AcmNotification.Service.create();}
    }
    ,onInitialized: function() {
        if (AcmNotification.Service.onInitialized){AcmNotification.Service.onInitialized();}
    }

};

