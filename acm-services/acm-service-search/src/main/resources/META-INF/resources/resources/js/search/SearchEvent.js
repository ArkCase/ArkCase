/**
 * Search.Event
 *
 * event handlers for objects
 *
 * @author jwu
 */
Search.Event = {
    initialize : function() {
    }

    ,onClickBtnSearch : function(e) {
alert("go");
        e.preventDefault();
    }

    ,onClickBtnToggleSubNav : function(e) {
        //alert("toggle");
        //Search.Object.showSubNav(true);
        //e.preventDefault();
    }


    ,onPostInit: function() {
        //var term = Acm.getUrlParameter("term");
        var term = Topbar.Object.getValueEdtSearch();
        if (Acm.isNotEmpty(term)) {
            //Search.Object.showSubNav(false);
            Search.setQuickSearchTerm(term);
            Search.Object.reloadJTableResults();
        }
    }
};
