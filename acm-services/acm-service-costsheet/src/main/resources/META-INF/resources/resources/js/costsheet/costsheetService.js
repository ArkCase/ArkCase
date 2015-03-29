/**
 *Costsheet.Service
 *
 * manages all service call to application server
 *
 * @author md
 */
Costsheet.Service = {
    create : function() {
    }
    ,onInitialized: function() {
    }
    ,Detail:{
        saveCostsheet: function(costsheetId, costsheet, handler) {
            ObjNav.Service.Detail.saveObject(Costsheet.Model.DOC_TYPE_COSTSHEET, costsheetId, costsheet, handler);
        }
        ,saveDetail: function(costsheet,details) {
            if (Costsheet.Model.Detail.validateCostsheet(costsheet)) {
                //have to remove group property from the JSON
                //to prevent from error
                delete costsheet.user["group"];
                costsheet.details = details;
                Costsheet.Service.Detail.saveCostsheet(costsheet.id, costsheet
                    ,function(data) {
                        Costsheet.Controller.modelSavedDetail(costsheet, Acm.Service.responseWrapper(data, data.details));
                    }
                );
            }
        }
    }

    ,ParentDetail: {
        create: function () {
        }
        , onInitialized: function () {
        }

        , API_RETRIEVE_COMPLAINT_: "/api/latest/plugin/complaint/byId/"
        , API_RETRIEVE_CASE_FILE_: "/api/latest/plugin/casefile/byId/"

        , retrieveComplaint: function (objId,objType) {
            var url = App.getContextPath() + this.API_RETRIEVE_COMPLAINT_ + objId;
            Acm.Service.asyncGet(
                function (response) {
                    if (response.hasError) {
                        Costsheet.Controller.modelRetrievedParentObject(response);

                    } else {
                        if (Costsheet.Model.ParentDetail.validateComplaint(response)) {
                            var complaint = response;
                            var unifiedData = Costsheet.Model.ParentDetail.makeUnifiedData(complaint, Costsheet.Model.DOC_TYPE_COMPLAINT);
                            if (Costsheet.Model.ParentDetail.validateUnifiedData(unifiedData)) {
                                Costsheet.Model.ParentDetail.cacheParentObject.put(objId + "." + objType, unifiedData);
                                Costsheet.Controller.modelRetrievedParentObject(unifiedData);
                            }
                        }
                    }
                }
                , url
            )
        }

        , retrieveCaseFile: function (objId,objType) {
            var url = App.getContextPath() + this.API_RETRIEVE_CASE_FILE_ + objId;
            Acm.Service.asyncGet(
                function (response) {
                    if (response.hasError) {
                        Costsheet.Controller.modelRetrievedParentObject(response);

                    } else {
                        if (Costsheet.Model.ParentDetail.validateCaseFile(response)) {
                            var caseFile = response;
                            var unifiedData = Costsheet.Model.ParentDetail.makeUnifiedData(caseFile, Costsheet.Model.DOC_TYPE_CASE_FILE);
                            if (Costsheet.Model.ParentDetail.validateUnifiedData(unifiedData)) {
                                Costsheet.Model.ParentDetail.cacheParentObject.put(objId + "." + objType, unifiedData);
                                Costsheet.Controller.modelRetrievedParentObject(unifiedData);
                            }
                        }
                    }
                }
                , url
            )
        }
    }
};
