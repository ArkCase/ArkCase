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

    ,API_RETRIEVE_AUDIT_LIST         : "/api/v1/plugin/audit/page"

    ,retrieveAuditListDeferred : function(postData, jtParams, sortMap, callbackSuccess, callbackError) {
        var pageIndex = jtParams.jtStartIndex;

        return AcmEx.Service.JTable.deferredPagingListAction(postData, jtParams, sortMap
            ,function() {
                return  App.getContextPath() + Audit.Service.API_RETRIEVE_AUDIT_LIST;

                //for test
                //return App.getContextPath() + "/api/latest/plugin/search/CASE_FILE";
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

                    jtData = callbackSuccess(auditList);
                }

                return jtData;
            }
//            ,function(data) {
//                var jtData = null
//                if (Audit.Model.validateAudit(data)) {
//                    var responseHeader = data.responseHeader;
//                    if (0 == responseHeader.status) {
//                        //response.start should match to jtParams.jtStartIndex
//                        //response.docs.length should be <= jtParams.jtPageSize
//
//                        var response = data.response;
//                        var auditList = [];
//                        for (var i = 0; i < response.docs.length; i++) {
//                            var doc = response.docs[i];
//                            var audit = {};
//                            audit.id       = doc.object_id_s;
//                            audit.dateTime = Acm.getDateFromDatetime(doc.create_dt);
//                            audit.user     = Acm.goodValue(doc.assignee_s);
//                            audit.activity = Acm.goodValue(response.docs[i].name);
//                            audit.result   = Acm.goodValue(doc.status_s);
//                            audit.ip       = Acm.goodValue(doc.priority_i);
//                            audit.objectId = doc.object_id_s;
//                            auditList.push(audit);
//                        }
//                        Audit.Model.cacheAuditList.put(pageIndex, auditList);
//
//                        jtData = callbackSuccess(auditList);
//
//                    } else {
//                        if (Acm.isNotEmpty(data.error)) {
//                            //todo: report error to controller. data.error.msg + "(" + data.error.code + ")";
//                        }
//                    }
//                }
//
//                return jtData;
//            }
        );
    }




};

