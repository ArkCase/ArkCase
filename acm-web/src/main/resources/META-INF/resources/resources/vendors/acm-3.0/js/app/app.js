/**
 * App serves as namespace for Application
 *
 * @author jwu
 */
var App = App || {
    initialize : function() {
        App.Object.initialize();
        App.Event.initialize();
        App.Service.initialize();
        App.Callback.initialize();

        Acm.deferred(App.Event.onPostInit);
    }

    ,Object : {}
    ,Event : {}
    ,Service : {}
    ,Callback : {}


    ,getContextPath: function() {
        return App.Object.getContextPath();
    }
    ,getUserName: function() {
        return App.Object.getUserName();
    }

    ,gotoPage: function(url) {
        window.location.href = App.getContextPath() + url;
    }

};


