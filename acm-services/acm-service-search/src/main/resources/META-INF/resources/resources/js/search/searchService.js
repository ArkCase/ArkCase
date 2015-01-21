/**
 * Search.Service
 *
 * manages all service call to application server
 *
 * @author jwu
 */
Search.Service = {
    create : function() {
    }
    ,onInitialized: function() {
    }

    ,API_QUICK_SEARCH_       : "/api/v1/plugin/search/quickSearch?q="
    ,API_FACET_SEARCH_       : "/api/v1/plugin/search/facetedSearch?q="

    ,facetSearchDeferred : function(searchInfo, postData, jtParams, sortMap, callbackSuccess, callbackError) {
        return AcmEx.Service.JTable.deferredPagingListAction(postData, jtParams, sortMap
            ,function() {
                var url;
                url =  App.getContextPath() + Search.Service.API_FACET_SEARCH_;
                url += searchInfo.q;

                //for test
                //url = App.getContextPath() + "/resources/facetSearch.json?q=xyz";

                if (Acm.isArray(searchInfo.filter)) {
                    if (0 < searchInfo.filter.length) {
                        for (var i = 0; i < searchInfo.filter.length; i++) {
                            if (0 == i) {
                                url += '&filters="';
                            } else {
                                url += '&';
                            }

                            url += 'fq="' + Acm.goodValue(searchInfo.filter[i].name) + '":' + Acm.goodValue(searchInfo.filter[i].value);

                            if (searchInfo.filter.length - 1 == i) {
                                url += '"';
                            }
                        }
                    }
                }

                return url;
            }
            ,function(data) {
                var jtData = null
                if (Search.Model.validateFacetSearchData(data)) {
                    if (0 == data.responseHeader.status) {
                        //response.start should match to jtParams.jtStartIndex
                        //response.docs.length should be <= jtParams.jtPageSize

                        searchInfo.total = data.response.numFound;

                        var result = data.response;
                        //var page = Acm.goodValue(jtParams.jtStartIndex, 0);
                        //Search.Model.cacheResult.put(page, result);
                        Search.Model.putCachedResult(searchInfo, result);
                        jtData = callbackSuccess(result);
                        Search.Controller.modelChangedResult(Acm.Service.responseWrapper(data, result));

                        if (!Search.Model.isFacetUpToDate()) {
                            var facet = Search.Model.makeFacet(data);
                            Search.Controller.modeChangedFacet(Acm.Service.responseWrapper(data, facet));
                            Search.Model.setFacetUpToDate(true);
                        }
                    } else {
                        if (Acm.isNotEmpty(data.error)) {
                            //todo: report error to controller. data.error.msg + "(" + data.error.code + ")";
                        }
                    }
                }
                return jtData;
            }
        );
    }





    ,API_SEARCH       : "/api/latest/plugin/search"
    ,API_SEARCH_DEMO       : "/api/latest/plugin/complaint/byId/"

    //for demo
    ,search : function(term) {
        if (Acm.isEmpty(term)) {
            Search.Page.fillResults([]);
            return;
        }
        Acm.Ajax.asyncGet(App.getContextPath() + this.API_SEARCH_DEMO + term
            ,Search.Callback.EVENT_RESULT_RETRIEVED
        );
    }

    ,search_save : function(term) {
        if (Acm.isEmpty(term)) {
            Search.Page.fillResults([]);
            return;
        }
        Acm.Ajax.asyncGet(App.getContextPath() + this.API_SEARCH
            ,Search.Callback.EVENT_RESULT_RETRIEVED
        );
    }
};
