/**
 * ObjNav.Controller
 *
 * @author jwu
 */
ObjNav.Controller = {
    create : function() {
    }
    ,onInitialized: function() {
    }

    ,MODEL_RETRIEVED_OBJECT_LIST           : "objnav-model-retrieved-object_list"
    ,MODEL_RETRIEVED_OBJECT_LIST_ERROR     : "objnav-model-retrieved-object_list-error"
    ,MODEL_RETRIEVED_OBJECT                : "objnav-model-retrieved-detail"
    ,MODEL_RETRIEVED_OBJECT_ERROR          : "objnav-model-retrieved-detail-error"
    ,MODEL_SAVED_OBJECT                    : "objnav-model-saved-detail"
    ,MODEL_SAVED_OBJECT_ERROR              : "objnav-model-saved-detail-error"

    ,VIEW_CLICKED_PREV_PAGE                : "objnav-view-clicked-prev-page"
    ,VIEW_CLICKED_NEXT_PAGE 	           : "objnav-view-clicked-next-page"
    ,VIEW_SELECTED_OBJECT 		           : "objnav-view-selected-object"
    ,VIEW_SELECTED_TREE_NODE 		       : "objnav-view-selected-tree-node"

    ,VIEW_CHANGED_TREE_FILTER              : "case-view-changed-tree-filter"
    ,VIEW_CHANGED_TREE_SORT                : "case-view-changed-tree-sort"

    ,modelRetrievedObjectList: function(key) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_OBJECT_LIST, key);
    }
    ,modelRetrievedObjectListError: function(error) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_OBJECT_LIST_ERROR, error);
    }
    ,modelRetrievedObject: function(objData) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_OBJECT, objData);
    }
    ,modelRetrievedObjectError: function(error) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_OBJECT_ERROR, error);
    }
    ,modelSavedObject : function(objData) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_OBJECT, objData);
    }
    ,modelSavedObjectError : function(error) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_OBJECT_ERROR, error);
    }

    ,viewClickedPrevPage: function() {
        Acm.Dispatcher.fireEvent(this.VIEW_CLICKED_PREV_PAGE);
    }
    ,viewClickedNextPage: function() {
        Acm.Dispatcher.fireEvent(this.VIEW_CLICKED_NEXT_PAGE);
    }
    ,viewSelectedObject: function(nodeType, objId) {
        Acm.Dispatcher.fireEvent(this.VIEW_SELECTED_OBJECT, nodeType, objId);
    }
    ,viewSelectedTreeNode: function(nodeKey) {
        Acm.Dispatcher.fireEvent(this.VIEW_SELECTED_TREE_NODE, nodeKey);
    }

    ,viewChangedTreeFilter: function(filter) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_TREE_FILTER, filter);
    }
    ,viewChangedTreeSort: function(sort) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_TREE_SORT, sort);
    }
};


