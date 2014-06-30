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
        var term = Topbar.Object.getQuickSearchTerm();
        if (Acm.isNotEmpty(term)) {
            Search.Object.reloadJTableResults();
        }
    }
};
