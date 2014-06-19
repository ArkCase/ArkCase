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
        var term2 = Topbar.Object.getValueEdtSearch();
        var term = $(e).find("#acmQuickSearch").val();
        localStorage.setItem("AcmSearchTerm", term);
        return false;
    }

    ,onPostInit: function() {
        //Topbar.Service.getTypeAheadTerms();
    }
};
