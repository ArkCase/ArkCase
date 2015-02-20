/**
 * SearchBase.Controller
 *
 * @author jwu
 */
SearchBase.Controller = {
    create : function(args) {
        var name = Acm.goodValue(args.name, "search");
        this.MODEL_CHANGED_RESULT          = name + "-search-changed-result";                   //param: result
        this.MODEL_CHANGED_FACET           = name + "-search-changed-facet";                    //param: facet
        this.VIEW_SUBMITTED_QUERY          = name + "-search-view-submitted-query";             //param: term
        this.VIEW_CHANGED_FACET_SELECTION  = name + "-search-view-changed-facet-selection";     //param: selected
    }
    ,onInitialized: function() {
    }

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


