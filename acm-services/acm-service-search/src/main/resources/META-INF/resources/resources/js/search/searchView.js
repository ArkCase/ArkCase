/**
 * Search.View
 *
 * @author jwu
 */
Search.View = {
    create : function() {
        if (Search.View.Query.create)            {Search.View.Query.create();}
    }
    ,onInitialized: function() {
        if (Search.View.Query.onInitialized)     {Search.View.Query.onInitialized();}
    }

    ,Query: {
        create: function() {
            if ("undefined" != typeof Topbar) {
                Acm.Dispatcher.addEventListener(Topbar.Controller.QuickSearch.VIEW_CHANGED_QUICK_SEARCH_TERM ,this.onTopbarViewChangedQuickSearchTerm);
            }
        }
        ,onInitialized: function() {
            if ("undefined" != typeof Topbar) {
                var term = Topbar.Model.QuickSearch.getQuickSearchTerm();
                Topbar.Model.QuickSearch.setQuickSearchTerm(null);
                SearchBase.View.Query.submit(term);
            }
        }

        ,onTopbarViewChangedQuickSearchTerm: function(term) {
            SearchBase.View.Query.submit(term);
            return true;
        }
    }


};

