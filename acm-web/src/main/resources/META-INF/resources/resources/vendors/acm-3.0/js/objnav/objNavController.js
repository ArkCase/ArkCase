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
    ,modelRetrievedObjectList: function(key) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_OBJECT_LIST, key);
    }
    ,MODEL_RETRIEVED_OBJECT_LIST_ERROR     : "objnav-model-retrieved-object_list-error"
    ,modelRetrievedObjectListError: function(error) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_OBJECT_LIST_ERROR, error);
    }
    ,MODEL_RETRIEVED_OBJECT                : "objnav-model-retrieved-detail"
    ,modelRetrievedObject: function(objData) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_OBJECT, objData);
    }
    ,MODEL_RETRIEVED_OBJECT_ERROR          : "objnav-model-retrieved-detail-error"
    ,modelRetrievedObjectError: function(error) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_OBJECT_ERROR, error);
    }
    ,MODEL_SAVED_OBJECT                    : "objnav-model-saved-detail"
    ,modelSavedObject : function(objData) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_OBJECT, objData);
    }
    ,MODEL_SAVED_OBJECT_ERROR              : "objnav-model-saved-detail-error"
    ,modelSavedObjectError : function(error) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_OBJECT_ERROR, error);
    }

    ,VIEW_CLICKED_PREV_PAGE                : "objnav-view-clicked-prev-page"
    ,viewClickedPrevPage: function() {
        Acm.Dispatcher.fireEvent(this.VIEW_CLICKED_PREV_PAGE);
    }
    ,VIEW_CLICKED_NEXT_PAGE 	           : "objnav-view-clicked-next-page"
    ,viewClickedNextPage: function() {
        Acm.Dispatcher.fireEvent(this.VIEW_CLICKED_NEXT_PAGE);
    }
    ,VIEW_SELECTED_OBJECT 		           : "objnav-view-selected-object"
    ,viewSelectedObject: function(nodeType, objId) {
        Acm.Dispatcher.fireEvent(this.VIEW_SELECTED_OBJECT, nodeType, objId);
    }
    ,VIEW_SELECTED_TREE_NODE 		       : "objnav-view-selected-tree-node"
    ,viewSelectedTreeNode: function(nodeKey) {
        Acm.Dispatcher.fireEvent(this.VIEW_SELECTED_TREE_NODE, nodeKey);
    }
    ,VIEW_CHANGED_TREE_FILTER              : "objnav-view-changed-tree-filter"
    ,viewChangedTreeFilter: function(filter) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_TREE_FILTER, filter);
    }
    ,VIEW_CHANGED_TREE_SORT                : "objnav-view-changed-tree-sort"
    ,viewChangedTreeSort: function(sort) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_TREE_SORT, sort);
    }

};


