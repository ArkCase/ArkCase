/**
 * SimpleSearch.Event
 *
 * event handlers for objects
 *
 * @author jwu
 */
SimpleSearch.Event = {
    initialize : function() {
    }


    ,onPostInit: function() {
        //var term = Acm.getUrlParameter("term");
        var term = Topbar.getQuickSearchTerm();
        if (Acm.isNotEmpty(term)) {
            SimpleSearch.Object.setValueEdtQuickSearch(term);
            SimpleSearch.Object.showSubNav(false);

            //SimpleSearch.Service.search(term);
            Topbar.setQuickSearchTerm(null);

            //SimpleSearch.Object.createJTableResults();
        }
    }
};
