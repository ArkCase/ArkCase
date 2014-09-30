/**
 * Search is namespace component for Search plugin
 *
 * @author jwu
 */
var Search = Search || {
    initialize: function() {
        Search.Object.initialize();
        Search.Event.initialize();
        Search.Page.initialize();
        Search.Rule.initialize();
        Search.Service.initialize();
        Search.Callback.initialize();

        Acm.deferred(Search.Event.onPostInit);
    }

};

