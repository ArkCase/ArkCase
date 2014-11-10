/**
 * AcmEx.Service
 *
 * @author jwu
 */
AcmEx.Service = {
    create: function() {
    }

    ,JTable: {
        _catNextParam: function(url) {
            return (0 < url.indexOf('?'))? "&" : "?";
        }
        ,deferredPagingListAction: function(postData, jtParams, sortMap, urlEvealuator, responseHandler) {
            if (Acm.isEmpty(App.getContextPath())) {
                return AcmEx.Object.JTable.getEmptyRecords();
            }

            var url = urlEvealuator();
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
            return Acm.Service.deferredGet(responseHandler, url, postData);
        }
    }


};