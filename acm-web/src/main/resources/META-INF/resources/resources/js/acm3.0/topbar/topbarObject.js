/**
 * Topbar.Object
 *
 * manages screen objects
 *
 * @author jwu
 */
Topbar.Object = {
    initialize : function() {
        this.$formSearch = $("form[role='search']");
        this.$formSearch.attr("method", "get");
        var url = Acm.getContextPath() + "/plugin/search"
        var term = localStorage.getItem("AcmSearchTerm");
        if (Acm.isNotEmpty(term)) {
            url += "?term=" + term;
        }
        this.$formSearch.attr("action", url);

        this.$formSearch.submit(function() {Topbar.Event.onSubmitFormSearch(this);});
    }

};




