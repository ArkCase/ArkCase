/**
 * SimpleSearch is namespace component for SimpleSearch plugin
 *
 * @author jwu
 */
var SimpleSearch = SimpleSearch || {
    initialize: function() {
        SimpleSearch.Object.initialize();
        SimpleSearch.Event.initialize();
        SimpleSearch.Page.initialize();
        SimpleSearch.Rule.initialize();
        SimpleSearch.Service.initialize();
        SimpleSearch.Callback.initialize();

        Acm.deferred(SimpleSearch.Event.onPostInit);
    }

    ,Object: {}
    ,Event:{}
    ,Page: {}
    ,Rule: {}
    ,Service: {}
    ,Callback: {}
};

