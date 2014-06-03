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
        //window.location.href = Acm.getContextPath() + "/plugin/search?term=" + term;

        //jwu: window.location with url param does not work; use localStorage to cheat for now
        localStorage.setItem("AcmSearchTerm", term);

        //window.open(Acm.getContextPath() + "/plugin/search?term=" + term);
        //window.open(Acm.getContextPath() + "/plugin/search?term=" + term, "_self");
        //window.location = Acm.getContextPath() + "/plugin/search?term=" + term;
        //window.location.assign(Acm.getContextPath() + "/plugin/search?term=" + term);

        //Acm.deferred(Topbar.Event.f);

        //e.preventDefault();
        //Acm.sleep(5000);

        return false;
    }

//    ,f: function() {
//        var term = localStorage.getItem("AcmSearchTerm");
//        window.location.href = Acm.getContextPath() + "/plugin/search?term=" + term;
//    }

    ,onPostInit: function() {
    }
};
