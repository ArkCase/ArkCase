/**
 * Subscription.Controller
 *
 * @author jwu
 */
Subscription.Controller = {
    create : function() {
    }
    ,onInitialized: function() {
    }

    ,MODEL_CHANGED_RESULT                : "subscription-changed-result"                    //param: result
    ,MODEL_CHANGED_FACET                 : "subscription-changed-facet"                     //param: facet

    ,VIEW_SUBMITTED_QUERY                : "subscription-view-submitted-query"              //param: term
    ,VIEW_CHANGED_FACET_SELECTION        : "subscription-view-changed-facet-selection"      //param: selected


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


