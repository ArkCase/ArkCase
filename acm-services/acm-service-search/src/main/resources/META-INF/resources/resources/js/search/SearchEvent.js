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

    ,onClickBtnSearch : function(event, control) {
        event.preventDefault();

        var data = Search.Object.getData();
        //call search service
    }

    ,onClickToggle: function(e) {
        var id = $(e).attr("id");
        Search.Object.slideToggle(id);

    }


    ,onClickBtnToggleSubNav : function(e) {
        //alert("toggle");
        //Search.Object.showSubNav(true);
        //e.preventDefault();
    }


    ,onPostInit: function() {
        var term = Topbar.Model.QuickSearch.getQuickSearchTerm();
        if (Acm.isNotEmpty(term)) {
            Search.Object.reloadJTableResults();
        }
    }
};
