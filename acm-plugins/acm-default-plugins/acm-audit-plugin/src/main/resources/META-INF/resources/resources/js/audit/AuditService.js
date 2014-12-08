/**
 * Audit.Service
 *
 * manages all service call to application server
 *
 * @author jwu
 */
Audit.Service = {
    create: function() {
    }
    ,onInitialized: function() {
    }

    ,API_RETRIEVE_AUDIT_LIST         : "/api/latest/plugin/audit/page"

    ,retrieveAuditListDeferred : function(postData, jtParams, sortMap, callbackSuccess, callbackError) {
        var pageIndex = jtParams.jtStartIndex;

        return AcmEx.Service.JTable.deferredPagingListAction(postData, jtParams, sortMap
            ,function() {
                return  App.getContextPath() + Audit.Service.API_RETRIEVE_AUDIT_LIST;
            }
            ,function(data) {
                var jtData = null
                if (Audit.Model.validateAudit(data)) {
                    var results = data.resultPage;
                    //response.start should match to jtParams.jtStartIndex
                    //response.resultPage.length should be <= jtParams.jtPageSize

                    var response = data.response;
                    var auditList = [];
                    for (var i = 0; i < results.length; i++) {
                        var audit = {};
                        audit.id       = i;
                        audit.dateTime = Acm.getDateFromDatetime(results[i].eventDate);
                        audit.user     = Acm.goodValue(results[i].userId);
                        audit.activity = Acm.goodValue(results[i].fullEventType);
                        audit.result   = Acm.goodValue(results[i].eventResult);
                        audit.ip       = Acm.goodValue(results[i].ipAddress);
                        audit.objectId = Acm.goodValue(results[i].objectId, 0);
                        auditList.push(audit);
                    }
                    Audit.Model.cacheAuditList.put(pageIndex, auditList);
                    Audit.Model.setTotalCount(data.totalCount);

                    jtData = callbackSuccess(auditList);
                }

                return jtData;
            }
        );
    }




};

