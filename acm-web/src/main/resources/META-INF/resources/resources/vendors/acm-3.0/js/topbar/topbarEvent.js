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
        Topbar.Object.setQuickSearchTerm(term);
        return false;
    }

    ,onPostInit: function() {
        //Topbar.Service.getTypeAheadTerms();
    }
};
