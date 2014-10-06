/**
 * CaseFile.Callback
 *
 * Callback handlers for server responses
 *
 * @author jwu
 */
CaseFile.Callback = {
    initialize : function() {
        Acm.Dispatcher.addEventListener(this.EVENT_LIST_RETRIEVED, this.onListRetrieved);
        Acm.Dispatcher.addEventListener(this.EVENT_DETAIL_RETRIEVED, this.onDetailRetrieved);
        Acm.Dispatcher.addEventListener(this.EVENT_CASEFILE_SAVED, this.onCaseFileSaved);
        Acm.Dispatcher.addEventListener(this.EVENT_CASEFILE_CLOSED, this.onCaseFileClosed);

    }

    ,EVENT_LIST_RETRIEVED		: "caseFile-list-retrieved"
    ,EVENT_DETAIL_RETRIEVED		: "caseFile-detail-retrieved"
    ,EVENT_CASEFILE_SAVED		: "caseFile-caseFile-saved"
    ,EVENT_CASEFILE_CLOSED      : "caseFile-caseFile-closed"


    ,onListRetrieved : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to retrieve caseFile list:" + response.errorMsg);
        } else {
            var treeInfo = CaseFile.Object.getTreeInfo();
            //todo: compare treeInfo with response, if not match do nothing (user click something else before result)
            //if (treeInfo.start != response start) {
            //  return;
            //}
            treeInfo.total = 32;  //= response total

            var caseFiles = response;
            var start = treeInfo.start;
            CaseFile.cachePage.put(start, caseFiles);

            var key = treeInfo.initKey;
            if (null == key) {
                if (0 < caseFiles.length) {
                    var caseFileId = caseFiles[0].caseFileId;
                    if (0 < caseFileId) {
                        key = start + "." + caseFileId;
                    }
                }
            } else {
                treeInfo.initKey = null;
            }
            CaseFile.Object.refreshTree(key);
        }
    }
    ,onDetailRetrieved : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to retrieve caseFile detail:" + response.errorMsg);
        } else {
            if (Acm.isNotEmpty(response.id)) {
                var caseFile = response;
                var caseFileId = CaseFile.getCaseFileId();
                if (caseFileId != caseFile.id) {
                    return;         //user clicks another caseFile before callback, do nothing
                }

                //handle single caseFile situation
                var treeInfo = CaseFile.Object.getTreeInfo();
                if (0 < treeInfo.caseFileId) {
                    treeInfo.total = 1;

                    var pageId = treeInfo.start;
                    var caseFiles = [caseFile];
                    CaseFile.cachePage.put(pageId, caseFiles);

                    var key = pageId + "." + treeInfo.caseFileId.toString();
                    CaseFile.Object.refreshTree(key);
                }

                CaseFile.cacheCaseFile.put(caseFileId, caseFile);
                CaseFile.Object.populateCaseFile(caseFile);
            }
        }
    }
    ,onCaseFileSaved : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to create or save caseFile:" + response.errorMsg);
        }
        else {
            CaseFile.cacheCaseFile.put(response.id, response);
        }
    }
    ,onCaseFileClosed : function(Callback, response) {

        if (response.hasError) {
            Acm.Dialog.error("Failed to close caseFile:" + response.errorMsg);
        }
        else {
            CaseFile.Object.setTextLnkCloseDate(Acm.getDateFromDatetime(response.closed));
            CaseFile.cacheCaseFile.put(response.id, response);
            CaseFile.Object.refreshJTableEvents();
        }
    }
};
