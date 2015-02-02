/**
 * Subscription.Model
 *
 * @author jwu
 */
Subscription.Model = {
    create : function() {
        this.cacheResult = new Acm.Model.CacheFifo(8);
        this.setSearchInfo(AcmEx.Model.Subscription.getDefaultSearchInfo());

        Acm.Dispatcher.addEventListener(Subscription.Controller.VIEW_SUBMITTED_QUERY          ,this.onViewSubmittedQuery);
        Acm.Dispatcher.addEventListener(Subscription.Controller.VIEW_CHANGED_FACET_SELECTION  ,this.onViewChangedFacetSelection);

    }
    ,onInitialized: function() {
    }

    ,onViewSubmittedQuery: function(term) {
        var si = Subscription.Model.getSearchInfo();
        if (!Acm.compare(term, si.q)) {
            si.q = term;
            si.filter = [];
            Subscription.Model.setFacetUpToDate(false);
        }
    }
    ,onViewChangedFacetSelection: function(selected) {
        //todo: ??? compare selected with si.filter; if same, do nothing and return

        Subscription.Model.setFacetUpToDate(false);

        var si = Subscription.Model.getSearchInfo();
        si.filter = [];
        if (Acm.isArray(selected)) {
            var cur = {key: null, values: []};

            for (var i = 0; i < selected.length; i++) {
                var s = selected[i];
                if (Acm.isNotEmpty(s.name) && Acm.isNotEmpty(s.value)) {
                    if (s.name == cur.key) {
                        cur.values.push(s.value);
                    } else {
                        if (Acm.isNotEmpty(cur.key)) {
                            si.filter.push(cur);
                        }
                        cur = {};
                        cur.key = s.name;
                        cur.values = [s.value];
                    }
                }
            }
            if (0 < i) {
                si.filter.push(cur);
            }
        }
    }

    //
    // filter array json format: [{key, values:['v1', 'v2', ...]}, ...]
    //
    ,_searchInfo: null
    ,getSearchInfo: function() {
        return this._searchInfo;
    }
    ,setSearchInfo: function(searchInfo) {
        this._searchInfo = searchInfo;
    }

    ,getCachedResult: function(si) {
        var page = si.start;
        return Subscription.Model.cacheResult.get(page);
    }
    ,putCachedResult: function(si, result) {
        var page = si.start;
        //Subscription.Model.cacheResult.put(page, result);
    }

    ,addFilter: function(si, key, value) {
        for (var i = 0; i < si.filter.length; i++) {
            var f = si.filter[i];
            if (f.key == key) {
                //find value first to avoid adding duplicate
                for (var j = 0; j < f.values.length; j++) {
                    if (f.values[j] == value) {
                        return;
                    }
                }
                f.values.push(value);
                return;
            }
        }

        //key entry not found, create one
        var newFilter = {};
        newFilter.key = key;
        newFilter.values = [];
        newFilter.values.push(value);
        si.filter.push(newFilter);
    }
    ,removeFilter: function(si, key, value) {
        for (var i = 0; i < si.filter.length; i++) {
            var f = si.filter[i];
            if (f.key == key) {
                for (var j = 0; j < f.values.length; j++) {
                    if (f.values[j] == value) {
                        si.filter[i].values.splice(j, 1);
                        break;
                    }
                }
                if (0 >= f.values.length) {
                    si.filter.splice(i, 1);
                }
            }
        }
    }
    ,findFilter: function(si, key, value) {
        if (!Acm.isArrayEmpty(si.filter)) {
            for (var i = 0; i < si.filter.length; i++) {
                var f = si.filter[i];
                if (f.key == key) {
                    if (!Acm.isArrayEmpty(f.values)) {
                        for (var j = 0; j < f.values.length; j++) {
                            if (f.values[j] == value) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    ,makeFilterParam: function(si) {
        var param = "";
        if (!Acm.isArrayEmpty(si.filter)) {
            for (var i = 0; i < si.filter.length; i++) {
                if (0 == i) {
                    param= '&filters=';
                } else {
                    param += '%26';
                }

                if (!Acm.isArrayEmpty(si.filter[i].values)) {
                    for (var j = 0; j < si.filter[i].values.length; j++) {
                        if (0 == j) {
                            param += 'fq="' + Acm.goodValue(si.filter[i].key) + '":';
                        } else {
                            param += '|';
                        }
                        param += Acm.goodValue(si.filter[i].values[j]);

                    }
                }

//                if (si.filter.length - 1 == i) {
//                    param += '"';
//                }
            }
        }
        return param;
    }

    ,_facetUpToDate: true
    ,isFacetUpToDate: function() {
        return this._facetUpToDate;
    }
    ,setFacetUpToDate: function(facetUpToDate) {
        this._facetUpToDate = facetUpToDate;
    }

    //
    // Facet array JSON format: [{label, key, count, values:[{name, count, def}, ...]}, ...]
    //
    ,_facet: {
        facet_queries: []
        ,facet_fields: []
        ,facet_dates : []
        ,facet_ranges: []
    }
    ,getFacet: function() {
        return this._facet;
    }
    ,setFacet: function(facet) {
        this._facet = facet;
    }
    ,makeFacet: function(facetSearch) {
        var facet = {
            facet_queries  : []
            ,facet_fields  : []
            ,facet_dates   : []
            ,facet_ranges  : []
        };

        if (Search.Model.validateFacetSearchData(facetSearch)) {
            var raw_facet_queries = this._parseFacetEntries(facetSearch.responseHeader.params["facet.query"]);
            facet.facet_fields    = this._parseFacetEntries(facetSearch.responseHeader.params["facet.field"]);
            facet.facet_dates     = this._parseFacetEntries(facetSearch.responseHeader.params["facet.date"]);
            facet.facet_ranges    = this._parseFacetEntries(facetSearch.responseHeader.params["facet.range"]);

            var label = null;
            var labelLast = null;
            facet.facet_queries = [];
            var n = -1;
            for (var i = 0; i < raw_facet_queries.length; i++) {
                var key = raw_facet_queries[i].label;
                if (key) {
                    var ar = key.split(", ");
                    if (2 == ar.length) {
                        label = ar[0];
                        if (label != labelLast) {
                            n++;
                            facet.facet_queries[n] = {};
                            facet.facet_queries[n].label  = ar[0];
                            facet.facet_queries[n].key    = raw_facet_queries[i].key;
                            facet.facet_queries[n].count  = 0;
                            facet.facet_queries[n].values = [];
                            labelLast = label;
                        }
                        var value = {};
                        value.name = ar[1];
                        value.count = facetSearch.facet_counts.facet_queries[key];
                        value.def = raw_facet_queries[i].def;
                        facet.facet_queries[n].values.push(value);
                        facet.facet_queries[n].count += value.count;
                    }
                }
            }


            for (var i = 0; i < facet.facet_fields.length; i++) {
                facet.facet_fields[i].values = [];
                facet.facet_fields[i].count = 0;
                var key = (facet.facet_fields[i].label)? facet.facet_fields[i].label : facet.facet_fields[i].key;
                if (key) {
                    var ar = facetSearch.facet_counts.facet_fields[key];
                    if (Acm.isArray(ar)) {
                        for (var j = 0; j < ar.length; j+=2) {
                            var value = {};
                            value.name  = ar[j];
                            value.count = ar[j+1];
                            facet.facet_fields[i].values.push(value);
                            facet.facet_fields[i].count += value.count;
                        }
                    }
                }
            }

            for (var i = 0; i < facet.facet_dates.length; i++) {
                facet.facet_dates[i].values = [];
                facet.facet_dates[i].count = 0;
                var key = (facet.facet_dates[i].label)? facet.facet_dates[i].label : facet.facet_dates[i].key;
                if (key) {
                    var map = facetSearch.facet_counts.facet_dates[key];
                    if (Acm.isNotEmpty(map)) {
                        for (var name in map) {
                            if ("start" == name) {
                                facet.facet_dates[i].start = map[name];
                            } else if ("end" == name) {
                                facet.facet_dates[i].end = map[name];
                            } else if ("gap" == name) {
                                facet.facet_dates[i].gap = map[name];
                            } else {
                                var value = {};
                                value.name  = name;
                                value.count = map[name];
                                facet.facet_dates[i].values.push(value);
                                facet.facet_dates[i].count += value.count;
                            }
                        }
                    }
                }
            }

        }

        this.setFacet(facet);
        return facet;
    }
    ,_parseFacetEntries: function(facetParam) {
        var facetEntries = [];
        if (Acm.isNotEmpty(facetParam)) {
            var stringEntries = [];
            if (Acm.isArray(facetParam)) {
                stringEntries = facetParam;
            } else {
                stringEntries.push(facetParam);
            }
            for (var i = 0; i < stringEntries.length; i++) {
                var facetEntry = this._parseFacetLabelKeyDef(stringEntries[i]);
                if (facetEntry) {
                    facetEntries.push(facetEntry);
                }
            }
        }
        return facetEntries;
    }
    ,_parseFacetLabelKeyDef: function(s) {
        var facetEntry = null;
        if (Acm.isNotEmpty(s)) {
            facetEntry = {};
            var keyDef = s.split(":");
            if (1 <= keyDef.length) {
                var tmp = keyDef[0];
                var idxLabel = tmp.indexOf("{!key='");
                if (0 != idxLabel) {
                    facetEntry.key = keyDef[0];
                } else {
                    tmp = tmp.substring(7);      //7 == length of "{!key='"
                    var labelKey = tmp.split("'}");
                    if (Acm.isArray(labelKey)) {
                        if (2 == labelKey.length) {
                            facetEntry.label = labelKey[0];
                            facetEntry.key   = labelKey[1];
                        }
                    }
                }
            }
            if (2 == keyDef.length) {
                facetEntry.def = keyDef[1];
            }

        }
        return facetEntry;
    }

    ,getCountFacetQueries: function(facet) {
        var count = 0;
        if (facet) {
            count = this._getCountFacetArray(facet.facet_queries);
        }
        return count;
    }
    ,getCountFacetFields: function(facet) {
        var count = 0;
        if (facet) {
            count = this._getCountFacetArray(facet.facet_fields);
        }
        return count;
    }
    ,getCountFacetDates: function(facet) {
        var count = 0;
        if (facet) {
            count = this._getCountFacetArray(facet.facet_dates);
        }
        return count;
    }
    ,getCountFacetRanges: function(facet) {
        var count = 0;
        if (facet) {
            count = this._getCountFacetArray(facet.facet_ranges);
        }
        return count;
    }
    ,_getCountFacetArray: function(ar) {
        var count = 0;
        if (Acm.isArray(ar)) {
            for (var i = 0; i < ar.length; i++) {
                if (ar[i].count) {
                    count += ar[i].count;
                }
            }
        }
        return count;

    }


//    ,_term: null
//    ,getTerm: function() {
//        return this._term;
//    }
//    ,setTerm: function(term) {
//        this._term = term;
//    }
//
//    ,_termPrev: null
//    ,getTermPrev: function() {
//        return this._termPrev;
//    }
//    ,setTermPrev: function(termPrev) {
//        this._termPrev = termPrev;
//    }
//
//    ,_header: {}
//    ,getHeader: function() {
//        return this._header();
//    }
//    ,setHeader: function(header) {
//        this.header = header;
//    }



    ,validateFacetSearchData: function(data) {
        if (!Acm.Validator.validateSolrData(data)) {
            return false;
        }

        if ("true" != Acm.goodValue(data.responseHeader.params.facet)) {
            return false;
        }


        if (Acm.isEmpty(data.facet_counts)) {
            return false;
        }
        if (Acm.isEmpty(data.facet_counts.facet_queries)) {
            return false;
        }
        if (Acm.isEmpty(data.facet_counts.facet_fields)) {
            return false;
        }
        if (Acm.isEmpty(data.facet_counts.facet_dates)) {
            return false;
        }
        if (Acm.isEmpty(data.facet_counts.facet_ranges)) {
            return false;
        }
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
        if (Acm.isEmpty(data.facet_fields)) {
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

