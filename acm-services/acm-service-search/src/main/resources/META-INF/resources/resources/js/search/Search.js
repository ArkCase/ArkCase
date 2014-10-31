/**
 * Search is namespace component for Search plugin
 *
 * @author jwu
 */
var Search = Search || {
    create: function() {
        Search.Object.create();
        Search.Event.create();
        Search.Page.create();
        Search.Rule.create();
        Search.Service.create();
        Search.Callback.create();

        Acm.deferred(Search.Event.onPostInit);
    }

};

