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

    ,API_RETRIEVE_AUDIT_LIST         : "/api/latest/plugin/search/CASE_FILE"

    ,retrieveAuditListDeferred : function(postData, jtParams, sortMap, callbackSuccess, callbackError) {
        var pageIndex = jtParams.jtStartIndex;

        return AcmEx.Service.JTable.deferredPagingListAction(postData, jtParams, sortMap
            ,function() {
                var url;
                url =  App.getContextPath() + Audit.Service.API_RETRIEVE_AUDIT_LIST;

                //for test
                url = App.getContextPath() + "/api/latest/plugin/search/CASE_FILE";

                return url;
            }
            ,function(data) {
                var jtData = null
                if (Audit.Model.validateAudit(data)) {
                    var responseHeader = data.responseHeader;
                    if (0 == responseHeader.status) {
                        //response.start should match to jtParams.jtStartIndex
                        //response.docs.length should be <= jtParams.jtPageSize

                        var response = data.response;
                        var auditList = [];
                        for (var i = 0; i < response.docs.length; i++) {
                            var doc = response.docs[i];
                            var audit = {};
                            audit.id       = doc.object_id_s;
                            audit.dateTime = Acm.getDateFromDatetime(doc.create_dt);
                            audit.user     = Acm.goodValue(doc.assignee_s);
                            audit.activity = Acm.goodValue(response.docs[i].name);
                            audit.result   = Acm.goodValue(doc.status_s);
                            audit.ip       = Acm.goodValue(doc.priority_i);
                            audit.objectId = doc.object_id_s;
                            auditList.push(audit);
                        }
                        Audit.Model.cacheAuditList.put(pageIndex, auditList);

                        jtData = callbackSuccess(auditList);

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

