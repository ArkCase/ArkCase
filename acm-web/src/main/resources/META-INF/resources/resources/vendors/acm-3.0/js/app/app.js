/**
 * App serves as namespace for Application
 *
 * @author jwu
 */
var App = App || {
    prepare : function() {
        if (App.Model.prepare)             App.Model.prepare();
    }
    ,create : function() {
        if (App.Controller.create)         App.Controller.create();
        if (App.Model.create)              App.Model.create();
        if (App.View.create)               App.View.create();

        //this.create_old();
    }
    ,onInitialized: function() {
        if (App.Controller.onInitialized)  App.Controller.onInitialized();
        if (App.Model.onInitialized)       App.Model.onInitialized();
        if (App.View.onInitialized)        App.View.onInitialized();
    }
    ,config: function() {
        App.Model.Config.request();
    }

    ,_context: null
    ,getPageContext: function() {
        if (Acm.isNotEmpty(this._context)) {
            return this._context;
        }

        var context = {};
        context.path = App.getContextPath();
        context.resourceNamespace = Acm.Object.MicroData.get("resourceNamespace");

        this._context = context;
        return context;
    }

    ,getContextPath: function() {
        return Acm.Object.MicroData.get("contextPath");
    }
    ,getUserName: function() {
        return Acm.Object.MicroData.get("userName");
    }

    ,buildObjectUrl : function(objectType, objectId, defaultUrl) {
        var url = null;
        var ot = App.View.MicroData.findObjectType(objectType);
        if (ot && Acm.isNotEmpty(ot.url)) {
            url = App.getContextPath() + ot.url + objectId + Acm.goodValue(ot.urlEnd);
        }
        if (null == url && undefined != defaultUrl) {
            url = defaultUrl;
        }
        return url;
    }

    ,gotoPage: function(url) {
        window.location.href = App.getContextPath() + url;
    }

    ,checkConfig: function(context) {
        var promise = $.when();
        if (Acm.isEmpty(context.loginPage)) {
            promise = App.Model.Config.resolveConfig();
        }
        return promise;
    }
    ,initI18n: function(context) {
        return App.Model.I18n.init(context);
    }

};


