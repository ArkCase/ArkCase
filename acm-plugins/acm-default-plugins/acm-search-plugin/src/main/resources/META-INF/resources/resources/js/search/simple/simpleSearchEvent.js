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
        var term = localStorage.getItem("AcmSearchTerm");
        SimpleSearch.Object.setValueEdtSearch(term);
        SimpleSearch.Service.search(term);
        localStorage.setItem("AcmSearchTerm", null);
    }
};
