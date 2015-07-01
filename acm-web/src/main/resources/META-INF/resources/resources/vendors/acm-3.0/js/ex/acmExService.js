/**
 * AcmEx.Service
 *
 * @author jwu
 */
AcmEx.Service = {
    create: function() {
    }

    ,JTable: {
        deferredPagingListAction: function(url, postData, jtParams, sortMap, responseHandler) {
            if (Acm.isEmpty(url)) {
                return AcmEx.Object.JTable.getEmptyRecords();
            }
            if (Acm.isNotEmpty(jtParams.jtStartIndex)) {
                url += this._catNextParam(url) + "start=" + jtParams.jtStartIndex;
            }
            if (Acm.isNotEmpty(jtParams.jtPageSize)) {
                url += this._catNextParam(url) + "n=" + jtParams.jtPageSize;
            }
            if (Acm.isNotEmpty(jtParams.jtSorting)) {
                var arr = jtParams.jtSorting.split(" ");
                if (2 == arr.length) {
                    for (var key in sortMap) {
                        if (key == arr[0]) {
                            url += this._catNextParam(url) + "s=" + sortMap[key] + "%20" + arr[1];
                        }
                    }
                }
            }
            //return Acm.Service.deferredGet(responseHandler, url, postData);
            return Acm.Service.call({type: "GET"
                ,url: url
                ,data: postData
                ,callback: function(response) {
                    if (!data.hasError) {
                        return responseHandler(response);
                    }
                }
            });
        }
    }


};