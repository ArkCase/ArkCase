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
    ,API_FACET_SEARCH_       : "/api/v1/plugin/search/quickSearch?q="
    ,facetSearchDeferred : function(term, postData, jtParams, sortMap, callbackSuccess, callbackError) {
        return AcmEx.Service.JTable.deferredPagingListAction(postData, jtParams, sortMap
            ,function() {
                var url;
                url =  App.getContextPath() + Search.Service.API_FACET_SEARCH_;
                url += term;

                //for test
                //url = App.getContextPath() + "/api/latest/plugin/search/CASE_FILE";

                return url;
            }
            ,function(data) {
                var jtData = null
                if (Search.Model.validateSearchData(data)) {
                    var header = data.responseHeader;
                    var facet = data.facet_counts;
                    if (0 == header.status) {
                        //response.start should match to jtParams.jtStartIndex
                        //response.docs.length should be <= jtParams.jtPageSize

                        var result = data.response;
                        var page = Acm.goodValue(jtParams.jtStartIndex, 0);
                        Search.Model.cacheResult.put(page, result);
                        jtData = callbackSuccess(result);
                        Search.Controller.modelChangedResult(Acm.Service.responseWrapper(data, result));

                        var prev = Search.Model.getTermPrev();
                        if (term != prev) {
                            Search.Model.setHeader(header);
                            Search.Model.setFacet(facet);
                            Search.Controller.modeChangedFacet(Acm.Service.responseWrapper(data, facet));
                            Search.Model.setTermPrev(prev);
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

