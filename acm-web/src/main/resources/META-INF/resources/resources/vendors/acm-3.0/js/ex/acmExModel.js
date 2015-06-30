/**
 * AcmEx.Model
 *
 * @author jwu
 */
AcmEx.Model = {
    create : function() {
        //if (AcmEx.Service.create) {AcmEx.Service.create();}
    }
    ,onInitialize : function() {
        //if (AcmEx.Service.onInitialize) {AcmEx.Service.onInitialize();}
    }

    ,JTable: {
        //pagingListAction: function(url, postData, jtParams, sortMap, dataMaker, keyGetter) {
        serviceListAction: function(url, postData, jtParams, sortMap, handler) {
            var pagingUrl = AcmEx.Model.JTable.decorateUrl(url, jtParams, sortMap);
            if (Acm.isEmpty(pagingUrl)) {
                return AcmEx.Object.JTable.getEmptyRecords();
            }

            return Acm.Service.call({type: "GET"
                ,url: pagingUrl
                ,data: postData
                ,callback: function(response) {
                    if (!response.hasError) {
                        return handler(response);
                    }
                }
            });
        }

        ,_addNextParam: function(url) {
            return (0 < url.indexOf('?'))? "&" : "?";
        }

        ,hashMapUrlDecorator: function(baseUrl, pageStart, pageSize, sortBy, sortDir, sortMap) {
            var url = baseUrl;
            if (Acm.isNotEmpty(baseUrl)) {
                if (Acm.isNotEmpty(pageStart)) {
                    url += this._addNextParam(url) + "start=" + pageStart;
                }
                if (Acm.isNotEmpty(pageSize)) {
                    url += this._addNextParam(url) + "n=" + pageSize;
                }
                var itemSortBy = Acm.goodValue(sortMap[sortBy]);
                if (Acm.isNotEmpty(itemSortBy)) {
                    if ("DESC" == Acm.goodValue(sortDir)) {
                        url += this._addNextParam(url) + "s=" + itemSortBy  + "%20DESC";
                    } else {
                        url += this._addNextParam(url) + "s=" + itemSortBy  + "%20ASC";
                    }
                }
            }
            return url;
        }
        ,hashMapUrlDecoratorDir: function(baseUrl, pageStart, pageSize, sortBy, sortDir, sortMap) {
            var url = baseUrl;
            if (Acm.isNotEmpty(baseUrl)) {
                if (Acm.isNotEmpty(pageStart)) {
                    url += this._addNextParam(url) + "start=" + pageStart;
                }
                if (Acm.isNotEmpty(pageSize)) {
                    url += this._addNextParam(url) + "n=" + pageSize;
                }
                var itemSortBy = Acm.goodValue(sortMap[sortBy]);
                if (Acm.isNotEmpty(itemSortBy)) {
                    if ("DESC" == Acm.goodValue(sortDir)) {
                        url += this._addNextParam(url) + "s=" + itemSortBy  + "%20DESC";
                    } else {
                        url += this._addNextParam(url) + "s=" + itemSortBy  + "%20ASC";
                    }
                }
                if ("DESC" == Acm.goodValue(sortDir)) {
                    url += this._addNextParam(url) + "dir=DESC";
                } else if ("ASC" == Acm.goodValue(sortDir)) {
                    url += this._addNextParam(url) + "dir=ASC";
                }
            }
            return url;
        }

        ,_getPagingParam: function(jtParams) {
            var pagingParam = {};
            pagingParam.pageStart = Acm.goodValue(jtParams.jtStartIndex, 0);
            pagingParam.pageSize = Acm.goodValue(jtParams.jtPageSize, 0);
            pagingParam.sortBy = "";
            pagingParam.sortDir = "";
            var jtSorting = Acm.goodValue(jtParams.jtSorting);
            var sortArr = jtSorting.split(" ");
            if (!Acm.isArrayEmpty(sortArr) && 2 == sortArr.length) {
                pagingParam.sortBy = sortArr[0];
                pagingParam.sortDir = sortArr[1];
            }
            return pagingParam;
        }

        //
        // urlDecorator can be overloaded with a sortMap, in that case, a default url decorator is used
        //
        ,decorateUrl: function(baseUrl, jtParams, urlDecorator) {
            var url = baseUrl;
            if (Acm.isNotEmpty(jtParams) && Acm.isNotEmpty(urlDecorator)) {
                var pagingParam = AcmEx.Model.JTable._getPagingParam(jtParams);

                if ("function" === typeof urlDecorator) {
                    return urlDecorator(baseUrl, pagingParam.pageStart, pagingParam.pageSize, pagingParam.sortBy, pagingParam.sortDir);
                } else if ("object" === typeof urlDecorator) {
                    var sortMap = urlDecorator;
                    return AcmEx.Model.JTable.hashMapUrlDecorator(baseUrl, pagingParam.pageStart, pagingParam.pageSize, pagingParam.sortBy, pagingParam.sortDir, sortMap);
                }
            }
            return url;
        }

//        ,getIdCacheKey: function(id, jtParams) {
//            var key = id;
//            return key;
//        }

        ,defaultIdCacheKey: function(id, jtParams) {
            var pagingParam = AcmEx.Model.JTable._getPagingParam(jtParams);
            return id + "." + pagingParam.pageStart + "." + pagingParam.pageSize + "." + pagingParam.sortBy + "." + pagingParam.sortDir;
        }
    }

}