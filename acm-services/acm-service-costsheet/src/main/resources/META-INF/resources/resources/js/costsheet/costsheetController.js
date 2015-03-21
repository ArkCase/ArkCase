/**
 * Costsheet.Controller
 *
 * @author md
 */
Costsheet.Controller = {
    create : function() {
    }
    ,onInitialized: function() {
    }
    ,VIEW_SAVED_DETAIL                   : "costsheet-view-saved-detail"
    ,viewSavedDetail: function(costsheet, details) {
        Acm.Dispatcher.fireEvent(this.VIEW_SAVED_DETAIL, costsheet, details);
    }
    ,MODEL_SAVED_DETAIL                   : "costsheet-model-saved-detail"
    ,modelSavedDetail: function(costsheet, details) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_DETAIL, costsheet, details);
    }
    ,VIEW_EDITTED_COSTSHEET               : "costsheet-view-editted-costsheet"
    ,viewEdittedCostsheet: function(costsheet) {
        Acm.Dispatcher.fireEvent(this.VIEW_EDITTED_COSTSHEET, costsheet);
    }
    ,VIEW_ADDED_COSTSHEET                 : "costsheet-view-added-costsheet"
    ,viewAddedCostsheet: function(costsheet) {
        Acm.Dispatcher.fireEvent(this.VIEW_ADDED_COSTSHEET, costsheet);
    }

    ,MODEL_RETRIEVED_PARENT_OBJECT        : "costsheet-model-retrieved-parent-object"
    ,modelRetrievedParentObject: function(parentObject) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_PARENT_OBJECT, parentObject);
    }
};


