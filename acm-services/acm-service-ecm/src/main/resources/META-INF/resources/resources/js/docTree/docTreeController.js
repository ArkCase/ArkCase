/**
 * DocTree.Controller
 *
 * @author jwu
 */
DocTree.Controller = DocTree.Controller || {
    create : function(args) {
        var name = Acm.goodValue(args.name, "doctree");
        this.VIEW_CHANGED_TREE = name + "-view-changed-tree";
        this.VIEW_ADDED_FOLDER = name + "-view-added-folder";
        this.VIEW_ADDED_DOCUMENT = name + "-view-added-document";
        this.VIEW_RENAMED_FOLDRE = name + "-view-renamed-folder";
        this.VIEW_RENAMED_DOCUMENT = name + "-view-renamed-document";

        this.MODEL_ADDED_FOLDER = name + "-model-added-folder";
        this.MODEL_ADDED_DOCUMENT = name + "-model-added-document";
    }
    ,onInitialized: function() {
    }

    ,viewChangedTree: function() {
        Acm.Dispatcher.fireEvent(DocTree.Controller.VIEW_CHANGED_TREE);
    }
    ,viewAddedFolder: function(node, parentId, name) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.VIEW_ADDED_FOLDER, node, parentId, name);
    }
    ,modelAddedFolder: function(node, parentId, folder) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.MODEL_ADDED_FOLDER, node, parentId, folder);
    }
    ,viewAddedDocument: function(node, parentId, name) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.VIEW_ADDED_DOCUMENT, node, parentId, name);
    }
    ,modelAddedDocument: function(node, parentId, document) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.MODEL_ADDED_DOCUMENT, node, parentId, document);
    }
    ,viewRenamedFolder: function(node, id, parentId, name) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.VIEW_RENAMED_FOLDRE, node, id, parentId, name);
    }
    ,viewRenamedDocument: function(node, id, parentId, name) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.VIEW_RENAMED_DOCUMENT, node, id, parentId, name);
    }
};

