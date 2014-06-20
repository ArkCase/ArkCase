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


    ,onSubmitFormSearch : function(e) {
        var term = Topbar.Object.getValueEdtSearch();
        var term1 = $(e).find("#acmQuickSearch").val();
        Topbar.setQuickSearchTerm(term);
        return false;
    }

    ,onPostInit: function() {
        //Topbar.Service.getTypeAheadTerms();
    }
};
