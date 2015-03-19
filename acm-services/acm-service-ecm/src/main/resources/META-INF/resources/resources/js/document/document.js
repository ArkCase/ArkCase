/**
 * AcmDocument module
 *
 * @author jwu
 */
var AcmDocument = AcmDocument || {
    create: function() {
        if (AcmDocument.Controller.create) {AcmDocument.Controller.create();}
        if (AcmDocument.Model.create)      {AcmDocument.Model.create();}
        if (AcmDocument.View.create)       {AcmDocument.View.create();}
    }
    ,onInitialized: function() {
        if (AcmDocument.Controller.onInitialized) {AcmDocument.Controller.onInitialized();}
        if (AcmDocument.Model.onInitialized)      {AcmDocument.Model.onInitialized();}
        if (AcmDocument.View.onInitialized)       {AcmDocument.View.onInitialized();}
    }
};



