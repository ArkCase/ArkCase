/**
 * SearchBase.Model
 *
 * @author jwu
 */
SearchBase.Model = {
    create : function(args) {
        this.cacheResult = new Acm.Model.CacheFifo(8);

        var si = this.getDefaultSearchInfo();
        this.setSearchInfo(si);
        if (this.validateFilters(args.filters)) {
            this.setFixedFilters(args.filters);
            this.addFixedFilters(si.filters);
        }

        Acm.Dispatcher.replaceEventListener(SearchBase.Controller.VIEW_SUBMITTED_QUERY          ,this.onViewSubmittedQuery);
        Acm.Dispatcher.replaceEventListener(SearchBase.Controller.VIEW_CHANGED_FACET_SELECTION  ,this.onViewChangedFacetSelection);

        if (SearchBase.Service.create) {SearchBase.Service.create(args);}
    }
    ,onInitialized: function() {
        if (SearchBase.Service.onInitialized) {SearchBase.Service.onInitialized();}
    }
    ,onViewSubmittedQuery: function(term) {
        var si = SearchBase.Model.getSearchInfo();
        if (!Acm.compare(term, si.q)) {
            si.q = term;
            si.filters = [];
            SearchBase.Model.addFixedFilters(si.filters);
            SearchBase.Model.setFacetUpToDate(false);
        }
    }
    ,onViewChangedFacetSelection: function(selected) {
        //todo: ??? compare selected with si.filters; if same, do nothing and return

        SearchBase.Model.setFacetUpToDate(false);

        var si = SearchBase.Model.getSearchInfo();
        si.filters = [];
        if (Acm.isArray(selected)) {
            var cur = {key: null, values: []};

            for (var i = 0; i < selected.length; i++) {
                var s = selected[i];
                if (Acm.isNotEmpty(s.name) && Acm.isNotEmpty(s.value)) {
                    if (s.name == cur.key) {
                        cur.values.push(s.value);
                    } else {
                        if (Acm.isNotEmpty(cur.key)) {
                            si.filters.push(cur);
                        }
                        cur = {};
                        cur.key = s.name;
                        cur.values = [s.value];
                    }
                }
            }
            if (0 < i) {
                si.filters.push(cur);
            }
        }

        SearchBase.Model.addFixedFilters(si.filters);
    }


    ,getCachedResult: function(si) {
        var page = si.start;
        return SearchBase.Model.cacheResult.get(page);
    }
    ,putCachedResult: function(si, result) {
        var page = si.start;
        //SearchBase.Model.cacheResult.put(page, result);
    }

    //
    // filter array json format: [{key, values:['v1', 'v2', ...]}, ...]
    //
    ,getDefaultSearchInfo: function() {
        return {
            q: null
            ,start: 0
            ,n: 16
            ,total: 0
            ,filters: []
            ,sorters: []
        };
    }

    ,_searchInfo: null
    ,getSearchInfo: function() {
        return this._searchInfo;
    }
    ,setSearchInfo: function(searchInfo) {
        this._searchInfo = searchInfo;
    }

    ,_fixedFilters: []
    ,getFixedFilters: function() {
        return this._fixedFilters;
    }
    ,setFixedFilters: function(filters) {
        this._fixedFilters = filters;
    }
    ,addFixedFilters: function(toFilters) {
        if (!this.validateFilters(toFilters)) {
            return;
        }

        var fixedFilters = this.getFixedFilters();
        if (!this.validateFilters(fixedFilters)) {
            return;
        }

        for (var i = 0; i < fixedFilters.length; i++) {
            for (var j = 0; j < fixedFilters[i].values.length; j++) {
                this.addFilter(toFilters, fixedFilters[i].key, fixedFilters[i].values[j]);
            }
        }
    }
    ,validateFilters: function(data) {
        if (!Acm.isArray(data)) {
            return false;
        }
        for (var i = 0; i < data.length; i++) {
            if (Acm.isEmpty(data[i].key)) {
                return false;
            }
            if (!Acm.isArray(data[i].values)) {
                return false;
            }
        }
        return true;
    }

    ,addFilter: function(filters, key, value) {
        var filter = null;
        if (Acm.isArray(filters)) {
            for (var i = 0; i < filters.length; i++) {
                var filter = filters[i];
                if (filter.key == key) {
                    //find value first to avoid adding duplicate
                    for (var j = 0; j < filter.values.length; j++) {
                        if (filter.values[j] == value) {
                            return;
                        }
                    }
                    filter.values.push(value);
                    return;
                }
            }
        } else {
            filters = [];
        }

        //key entry not found, create one
        filter = {};
        filter.key = key;
        filter.values = [];
        filter.values.push(value);
        filters.push(filter);
    }
    ,removeFilter: function(filters, key, value) {
        if (Acm.isArray(filters)) {
            for (var i = 0; i < filters.length; i++) {
                var filter = filters[i];
                if (filter.key == key) {
                    for (var j = 0; j < filter.values.length; j++) {
                        if (filter.values[j] == value) {
                            filters[i].values.splice(j, 1);
                            break;
                        }
                    }
                    if (0 >= filter.values.length) {
                        filters.splice(i, 1);
                    }
                }
            }
        }
    }
    ,findFilter: function(filters, key, value) {
        if (!Acm.isArrayEmpty(filters)) {
            for (var i = 0; i < filters.length; i++) {
                var filter = filters[i];
                if (filter.key == key) {
                    if (!Acm.isArrayEmpty(filter.values)) {
                        for (var j = 0; j < filter.values.length; j++) {
                            if (filter.values[j] == value) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    ,makeFilterParam: function(filters) {
        var param = "";
        if (!Acm.isArrayEmpty(filters)) {
            for (var i = 0; i < filters.length; i++) {
                if (0 == i) {
                    param= '&filters=';
                } else {
                    param += '%26';
                }

                if (!Acm.isArrayEmpty(filters[i].values)) {
                    for (var j = 0; j < filters[i].values.length; j++) {
                        if (0 == j) {
                            param += 'fq="' + Acm.goodValue(filters[i].key) + '":';
                        } else {
                            param += '|';
                        }
                        param += Acm.goodValue(filters[i].values[j]);
                    }
                }
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
    // Facet field array JSON format: [{label, key, count, values:[{name, count, def}, ...]}, ...]
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

//    ,validateFacetFields: function(data) {
//        if (Acm.isNotArray(data)) {
//            return false;
//        }
//        for (var i = 0; i < data.length; i++) {
//            if (Acm.isEmpty(data[i].label) && Acm.isEmpty(data[i].key)) {
//                return false;
//            }
//            if (Acm.isNotArray(data[i].values)) {
//                return false;
//            }
//            for (var j = 0; j < data[i].values.length; j++) {
//                if (Acm.isEmpty(data[i].values[j].name)) {
//                    return false;
//                }
//                if (Acm.isEmpty(data[i].values[j].count)) {
//                    return false;
//                }
//            }
//        }
//        return true;
//    }

    ,makeFacet: function(facetSearch) {
        var facet = {
            facet_queries  : []
            ,facet_fields  : []
            ,facet_dates   : []
            ,facet_ranges  : []
        };

        if (SearchBase.Model.validateFacetSearchData(facetSearch)) {
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

