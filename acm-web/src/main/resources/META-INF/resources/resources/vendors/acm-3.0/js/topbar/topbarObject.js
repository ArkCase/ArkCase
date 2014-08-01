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
        this.$edtSearch = this.$formSearch.find("input.typeahead");
        this.$btnSearch = this.$formSearch.find("button[type='submit']");

        this.$formSearch.submit(function() {Topbar.Event.onSubmitFormSearch(this);});
        this.$btnSearch.click(function(e) {Topbar.Event.onClickBtnSearch(e);});

        this.$formSearch.attr("method", "get");
        var term = this.getQuickSearchTerm();
        this.setActionFormSearch(term);

        this.useTypeAhead(this.$edtSearch);
    }


    ,setActionFormSearch: function(term) {
        var url = App.getContextPath() + "/plugin/search"
        if (Acm.isNotEmpty(term)) {
            url += "?q=" + term;
        }
        this.$formSearch.attr("action", url);
    }
    ,getQuickSearchTerm: function() {
        var term = sessionStorage.getItem("AcmQuickSearchTerm");
        if (term === "null") {
            return null;
        }
        return term;
    }
    ,setQuickSearchTerm: function(term) {
        if (term === "null") {
            sessionStorage.removeItem("AcmQuickSearchTerm");
        }
        sessionStorage.setItem("AcmQuickSearchTerm", term);
    }
    ,getValueEdtSearch: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtSearch);
    }
    ,setValueEdtSearch: function(val) {
        return Acm.Object.setPlaceHolderInput(this.$edtSearch, val);
    }
    ,getValueHidSearch: function() {
        return Acm.Object.getPlaceHolderInput(this.$hidSearch);
    }
    ,setValueHidSearch: function(val) {
        return Acm.Object.setPlaceHolderInput(this.$hidSearch, val);
    }

///////////////////////////////////////
    ,_ctrObjs: {}
    ,_ctrTitles: []

    ,_onSuccessSuggestion: function(query, process, data) {
        //reset these ctrs every time the user searches
        //because we're potentially getting entirely different results from the api
        Topbar.Object._ctrObjs = {};
        Topbar.Object._ctrTitles = [];

        //Using underscore.js for a functional approach at looping over the returned data.
        _.each( data, function(item, ix, list){

            //for each iteration of this loop the "item" argument contains
            //1 ctr object from the array in our json, such as:
            // { "id":7, "title":"Pierce Brosnan" }

            //add the label to the display array
            Topbar.Object._ctrTitles.push( item.title );

            //also store a hashmap so that when bootstrap gives us the selected
            //title we can map that back to an id value
            Topbar.Object._ctrObjs[ item.title ] = item;
        });

        //send the array of results to bootstrap for display
        process( Topbar.Object._ctrTitles );
    }

    //get the data to populate the typeahead (plus an id value)
    ,_throttledRequest: function(query, process){
        //get the data to populate the typeahead (plus an id value)
        $.ajax({
            url: App.getContextPath() + Topbar.Service.API_TYPEAHEAD_SUGGESTION + "?q=" + query
            ,cache: false
            ,success: function(data){
                Topbar.Object._onSuccessSuggestion(query, process, data);
            }
        });
        var z = 1;
    }

    ,useTypeAhead: function($s) {
        //$(".typeahead").typeahead({
        $s.typeahead({
            source: function ( query, process ) {

                //here we pass the query (search) and process callback arguments to the throttled function
                _.debounce(Topbar.Object._throttledRequest( query, process ), 300);

            }
            ,highlighter: function( item ){
                var ctr = Topbar.Object._ctrObjs[ item ];
                var icon = "";

                if (ctr.type == "Complaint") {
                    icon = '<i class="i i-notice i-2x"></i>';
                } else if (ctr.type == "Case") {
                    icon = '<i class="i i-folder i-2x"></i>';
                } else if (ctr.type == "Task") {
                    icon = '<i class="i i-checkmark i-2x"></i>';
                } else if (ctr.type == "Document") {
                    icon = '<i class="i i-file i-2x"></i>';
                } else {
                    icon = '<i class="i i-circle i-2x"></i>';
                }

                return '<div class="ctr">'
                    +'<div class="icontype">' + icon + '</div>'
                    +'<div class="title">' + ctr.title + '</div>'
                    +'<div class="identifier">' + ctr.identifier + ' ('+ ctr.type + ')' + '</div>'
                    +'<div class="author">By ' + ctr.author  + '  on '+ ctr.date + '</div>'
                    +'</div>';
            }
            , updater: function ( selectedtitle ) {

                //note that the "selectedtitle" has nothing to do with the markup provided
                //by the highlighter function. It corresponds to the array of titles
                //that we sent from the source function.

                //save the id value into the hidden field
                $( "#ctrId" ).val( Topbar.Object._ctrObjs[ selectedtitle ].id );

                //return the string you want to go into the textbox (the title)
                return selectedtitle;
            }
            ,hint: true
            ,highlight: true
            ,minLength: 1

        });

    }
///////////// from static lookup pool //////////////////////////
//    ,getTypeAheadTerms: function() {
//        sessionStorage.getItem("AcmTypeAheadTerms");
//    }
//    ,setTypeAheadTerms: function(typeAheadTerms) {
//        sessionStorage.setItem("AcmTypeAheadTerms", typeAheadTerms);
//    }
//    ,useTypeAheadSearch: function(typeAheadTerms) {
//        this._useTypeAhead(this.$edtSearch, typeAheadTerms);
//    }
//    ,_useTypeAhead0: function ($s, typeAheadTerms){
//        $s.typeahead({
//                hint: true
//                ,highlight: true
//                ,minLength: 1
//            }
//            ,{
//                name: 'TypeAheadTerms'
//                ,displayKey: 'value'
//                ,source: this._substringMatcher(typeAheadTerms)
//            });
//    }
//    ,_substringMatcher : function(strs) {
//        return function findMatches(q, cb) {
//            var matches = [];
//            var substrRegex = new RegExp(q, 'i');
//            $.each(strs, function(i, str) {
//                if (substrRegex.test(str)) {
//                    matches.push({ value: str });
//                }
//            });
//
//            cb(matches);
//        };
//    }
/////////////////////////////////////////
};



