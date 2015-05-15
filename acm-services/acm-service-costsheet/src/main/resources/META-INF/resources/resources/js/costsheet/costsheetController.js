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
    ,VIEW_CLOSED_EDIT_COSTSHEET_WINDOW               : "costsheet-view-closed-edit-costsheet-window"
    ,viewClosedEditCostsheetWindow: function(costsheet) {
        Acm.Dispatcher.fireEvent(this.VIEW_CLOSED_EDIT_COSTSHEET_WINDOW, costsheet);
    }
    ,VIEW_CLOSED_ADD_COSTSHEET_WINDOW                 : "costsheet-view-closed-add-costsheet-window"
    ,viewClosedAddCostsheetWindow: function(costsheet) {
        Acm.Dispatcher.fireEvent(this.VIEW_CLOSED_ADD_COSTSHEET_WINDOW, costsheet);
    }

    ,MODEL_RETRIEVED_PARENT_OBJECT        : "costsheet-model-retrieved-parent-object"
    ,modelRetrievedParentObject: function(parentObject) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_PARENT_OBJECT, parentObject);
    }
};


