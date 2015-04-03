/**
 * AcmEx.Model
 *
 * @author jwu
 */
AcmEx.Model = {
    create : function() {
        if (AcmEx.Service.create) {AcmEx.Service.create();}
    }
    ,onInitialize : function() {
        if (AcmEx.Service.onInitialize) {AcmEx.Service.onInitialize();}
    }

}