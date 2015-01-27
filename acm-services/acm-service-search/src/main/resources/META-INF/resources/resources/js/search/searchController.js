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

    ,MODEL_CHANGED_RESULT                : "search-changed-result"                    //param: result
    ,MODEL_CHANGED_FACET                 : "search-changed-facet"                     //param: facet

    ,VIEW_SUBMITTED_QUERY                : "search-view-submitted-query"              //param: term
    ,VIEW_CHANGED_FACET_SELECTION        : "search-view-changed-facet-selection"      //param: selected


    ,modelChangedResult: function(result) {
        Acm.Dispatcher.fireEvent(this.MODEL_CHANGED_RESULT, result);
    }
    ,modeChangedFacet: function(facet) {
        Acm.Dispatcher.fireEvent(this.MODEL_CHANGED_FACET, facet);
    }

    ,viewSubmittedQuery: function(term) {
        Acm.Dispatcher.fireEvent(this.VIEW_SUBMITTED_QUERY, term);
    }
    ,viewChangedFacetSelection: function(selected) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_FACET_SELECTION, selected);
    }

};


