/**
 * Search.Controller
 *
 * @author jwu
 */
Search.Controller = {
    create : function() {
    }
    ,onInitialized: function() {
    }

    ,MODEL_CHANGED_RESULT                : "search-changed-result"            //param: result
    ,MODEL_CHANGED_FACET                 : "search-changed-facet"             //param: facet

    ,VIEW_SUBMITTED_QUERY                : "search-view-submitted-query"      //param: term

    ,modelChangedResult: function(result) {
        Acm.Dispatcher.fireEvent(this.MODEL_CHANGED_RESULT, result);
    }
    ,modeChangedFacet: function(facet) {
        Acm.Dispatcher.fireEvent(this.MODEL_CHANGED_FACET, facet);
    }

    ,viewSubmittedQuery: function(term) {
        Acm.Dispatcher.fireEvent(this.VIEW_SUBMITTED_QUERY, term);
    }

};


