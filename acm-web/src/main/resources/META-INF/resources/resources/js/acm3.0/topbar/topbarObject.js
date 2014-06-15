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
        this.$formSearch.submit(function() {Topbar.Event.onSubmitFormSearch(this);});

        this.$formSearch.attr("method", "get");
        var url = Acm.getContextPath() + "/plugin/search"
        var term = localStorage.getItem("AcmSearchTerm");
        if (Acm.isNotEmpty(term)) {
            url += "?term=" + term;
        }
        this.$formSearch.attr("action", url);

        this.$edtSearch = this.$formSearch.find("input.typeahead");
    }


    ,getSearchTerms: function() {
        localStorage.getItem("AcmSearchTerms");
    }
    ,setSearchTerms: function(searchTerms) {
        localStorage.setItem("AcmSearchTerms", searchTerms);
    }
    ,useTypeAheadSearch: function(searchTerms) {
        this._useTypeAhead(this.$edtSearch, searchTerms);
    }
    ,_useTypeAhead: function ($s, searchTerms){
        $s.typeahead({
                hint: true
                ,highlight: true
                ,minLength: 1
            }
            ,{
                name: 'searchTerms'
                ,displayKey: 'value'
                ,source: this._substringMatcher(searchTerms)
            });
    }
    ,_substringMatcher : function(strs) {
        return function findMatches(q, cb) {
            var matches = [];
            var substrRegex = new RegExp(q, 'i');
            $.each(strs, function(i, str) {
                if (substrRegex.test(str)) {
                    matches.push({ value: str });
                }
            });

            cb(matches);
        };
    }

};



