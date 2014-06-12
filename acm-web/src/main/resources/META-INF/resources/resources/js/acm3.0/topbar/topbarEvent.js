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
        var term = $(e).find("input").val();
        localStorage.setItem("AcmSearchTerm", term);
        return false;
    }

    ,onPostInit: function() {
        Topbar.Service.getSearchTerms();
    }
};
