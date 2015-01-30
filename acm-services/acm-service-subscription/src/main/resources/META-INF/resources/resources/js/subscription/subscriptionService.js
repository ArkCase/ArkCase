/**
 * Subscription.Service
 *
 * manages all service call to application server
 *
 * @author jwu
 */
Subscription.Service = {
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
                url =  App.getContextPath() + Subscription.Service.API_FACET_SEARCH_;
                url += searchInfo.q;

                //for test
                //url = App.getContextPath() + "/resources/facetSearch.json?q=xyz";

                var filter = Subscription.Model.makeFilterParam(searchInfo);
                url += filter;

                return url;
            }
            ,function(data) {
                var jtData = null
                if (Subscription.Model.validateFacetSearchData(data)) {
                    if (0 == data.responseHeader.status) {
                        //response.start should match to jtParams.jtStartIndex
                        //response.docs.length should be <= jtParams.jtPageSize

                        searchInfo.total = data.response.numFound;

                        var result = data.response;
                        //var page = Acm.goodValue(jtParams.jtStartIndex, 0);
                        //Subscription.Model.cacheResult.put(page, result);
                        Subscription.Model.putCachedResult(searchInfo, result);
                        jtData = callbackSuccess(result);
                        Subscription.Controller.modelChangedResult(Acm.Service.responseWrapper(data, result));

                        if (!Subscription.Model.isFacetUpToDate()) {
                            var facet = Subscription.Model.makeFacet(data);
                            Subscription.Controller.modeChangedFacet(Acm.Service.responseWrapper(data, facet));
                            Subscription.Model.setFacetUpToDate(true);
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

};
