/**
 * Topbar.Event
 *
 * event handlers for objects
 *
 * @author jwu
 */
Topbar.Event = {
    initialize : function() {
    }


    ,onClickBtnSearch : function(e) {
        var term = Topbar.Object.getValueEdtSearch();
        Topbar.Object.setActionFormSearch(term);
        console.log("Topbar.Event.onClickBtnSearch, term=" + term);
        //e.preventDefault();
    }

    ,onSubmitFormSearch : function(e) {
        var term = Topbar.Object.getValueEdtSearch();
        Topbar.Object.setQuickSearchTerm(term);
        return false;
    }

    ,onPostInit: function() {
        //Topbar.Service.getTypeAheadTerms();
    }
};
