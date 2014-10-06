/**
 * CaseFile.Service
 *
 * manages all service call to application server
 *
 * @author jwu
 */
CaseFile.Service = {
    initialize : function() {
    }

    ,API_LIST_CASE_FILE         : "/api/latest/plugin/casefile"
    ,API_RETRIEVE_PERSON_       : "/api/latest/plugin/person/find?assocId="
    ,API_RETRIEVE_DETAIL        : "/api/latest/plugin/caseFile/byId/"
    ,API_SAVE_CASE_FILE         : "/api/latest/plugin/casefile/"
    ,API_DOWNLOAD_DOCUMENT      : "/api/v1/plugin/ecm/download/byId/"
    ,API_UPLOAD_CASE_FILE_FILE  : "/api/latest/plugin/caseFile/file"
    ,API_RETRIEVE_TASKS         : "/api/latest/plugin/search/children?parentType=CASE_FILE&childType=TASK&parentId="
    ,API_CLOSE_CASE_FILE_       : "/api/latest/plugin/casefile/closeCase/"


    ,listCaseFile : function(treeInfo) {
        var caseFileId = treeInfo.caseFileId;
        var initKey = treeInfo.initKey;
        var start = treeInfo.start;
        var n = treeInfo.n;
        var s = treeInfo.s;
        var q = treeInfo.q;

        Acm.Ajax.asyncGet(App.getContextPath() + this.API_LIST_CASE_FILE
            ,CaseFile.Callback.EVENT_LIST_RETRIEVED
        );
    }
    ,retrieveDetail : function(caseFileId) {
        //not retrieving detail at this time, let's fake it
        var treeInfo = CaseFile.Object.getTreeInfo();
        var start = treeInfo.start;
        var caseFiles = CaseFile.cachePage.get(start);
        if (null == caseFiles || 0 >= caseFiles.length) {
            return;
        }
        for (var i = 0; i < caseFiles.length; i++) {
            var c = caseFiles[i];
            if (c.id == caseFileId) {
                Acm.Dispatcher.triggerEvent(CaseFile.Callback.EVENT_DETAIL_RETRIEVED, c);
            }
        }
        return;

        Acm.Ajax.asyncGet(App.getContextPath() + this.API_RETRIEVE_DETAIL + caseFileId
            ,CaseFile.Callback.EVENT_DETAIL_RETRIEVED
        );
    }
    ,saveCaseFile : function(data) {
        var updatedCaseFile = data;
        var key = "objectType";

        Acm.Ajax.asyncPost(App.getContextPath() + this.API_SAVE_CASE_FILE
            ,JSON.stringify(updatedCaseFile)
            ,CaseFile.Callback.EVENT_CASEFILE_SAVED
        );
    }

    ,closeCaseFile : function(data) {
        var caseFileId = CaseFile.getCaseFileId();
        Acm.Ajax.asyncPost(App.getContextPath() + this.API_CLOSE_CASE_FILE_ + caseFileId
            ,JSON.stringify(data)
            ,CaseFile.Callback.EVENT_CASEFILE_CLOSED
        );
    }
    ,getCaseTypes: function() {
        return ["SSBI", "Type1", "Type2", "Type3", "Type4"];
        //return ["Type1", "Type2", "Type3", "Type4"];
    }
//    ,getCaseFileTypes: function() {
//        var data = sessionStorage.getItem("AcmCaseFileTypes");
//        var item = ("null" === data)? null : JSON.parse(data);
//        return item;
//    }
//    ,setCaseFileTypes: function(data) {
//        var item = (Acm.isEmpty(data))? null : JSON.stringify(data);
//        sessionStorage.setItem("AcmCaseFileTypes", item);
//    }

    ,getCloseDispositions: function() {
        return ["Close Deposition1", "Close Deposition2", "Close Deposition3", "Close Deposition4"];
        //return ["Close Deposition1", "Close Deposition2", "Close Deposition3", "Close Deposition4"];
    }
    ,getStates: function() {
        return [
            "AK"
            ,"AL"
            ,"AR"
            ,"AZ"
            ,"CA"
            ,"CO"
            ,"CT"
            ,"DE"
            ,"FL"
            ,"GA"
            ,"HI"
            ,"IA"
            ,"ID"
            ,"IL"
            ,"IN"
            ,"KS"
            ,"KY"
            ,"LA"
            ,"MA"
            ,"MD"
            ,"ME"
            ,"MI"
            ,"MN"
            ,"MO"
            ,"MS"
            ,"MT"
            ,"NC"
            ,"ND"
            ,"NE"
            ,"NH"
            ,"NJ"
            ,"NM"
            ,"NV"
            ,"NY"
            ,"OH"
            ,"OK"
            ,"OR"
            ,"PA"
            ,"RI"
            ,"SC"
            ,"SD"
            ,"TN"
            ,"TX"
            ,"UT"
            ,"VA"
            ,"VT"
            ,"WA"
            ,"WI"
            ,"WV"
            ,"WY"
        ];
    }


};

