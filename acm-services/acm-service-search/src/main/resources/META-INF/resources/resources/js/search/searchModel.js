/**
 * Search.Model
 *
 * @author jwu
 */
Search.Model = {
    create : function() {
        this.cacheResult = new Acm.Model.CacheFifo(8);

        Acm.Dispatcher.addEventListener(Search.Controller.VIEW_SUBMITTED_QUERY  ,this.onViewSubmittedQuery);
    }
    ,onInitialized: function() {
    }

    ,onViewSubmittedQuery: function(term) {
        Search.Model.setTerm(term);
    }

    ,_term: null
    ,getTerm: function() {
        return this._term;
    }
    ,setTerm: function(term) {
        this._term = term;
    }

    ,_termPrev: null
    ,getTermPrev: function() {
        return this._termPrev;
    }
    ,setTermPrev: function(termPrev) {
        this._termPrev = termPrev;
    }

    ,_header: {}
    ,getHeader: function() {
        return this._header();
    }
    ,setHeader: function(header) {
        this.header = header;
    }

    ,_facet: {
        facet_queries: {}
        ,facet_fields: {}
        ,facet_dates: {}
        ,facet_ranges: {}
    }
    ,getFacet: function() {
        return this._facet;
    }
    ,setFacet: function(facet) {
        this._facet = facet;
    }


    ,validateSearchData: function(data) {
        if (!this.validateSearchResult(data)) {
            return false;
        }
//        if (!this.validateSearchFacet(data)) {
//            return false;
//        }
        return true;
    }
    ,validateSearchResult: function(data) {
        return Acm.Validator.validateSolrData(data);
    }
    ,validateSearchFacet: function(data) {
        if (!data) {
            return false;
        }

        if (Acm.isEmpty(data.facet_queries)) {
            return false;
        }
        if (Acm.isEmpty(data.facet_dates)) {
            return false;
        }
        if (Acm.isEmpty(data.facet_ranges)) {
            return false;
        }

        return true;
    }


};

