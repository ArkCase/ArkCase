/**
 * DocTree module
 *
 * @author jwu
 */
var DocTree = DocTree || {
    create: function(args) {
        if (Acm.isEmpty(args)) {
            args = {};
        }
        if (DocTree.Controller.create) {DocTree.Controller.create(args);}
        if (DocTree.Model.create)      {DocTree.Model.create(args);}
        if (DocTree.View.create)       {DocTree.View.create(args);}
    }
    ,onInitialized: function() {
        if (DocTree.Controller.onInitialized) {DocTree.Controller.onInitialized();}
        if (DocTree.Model.onInitialized)      {DocTree.Model.onInitialized();}
        if (DocTree.View.onInitialized)       {DocTree.View.onInitialized();}
    }

    ,showDocumentDialog: function(args) {
        if (Acm.isEmpty(args.$divResults)) {
            args.$tree = $("#treeDocumentPicker");
        }
//        if (!args.jtArgs) {
//            args.jtArgs = {multiselect:true, selecting:true, selectingCheckboxes:true};
//        }
        this.create(args);

        Acm.deferred(DocTree.onInitialized);

        DocTree.View.showDialog(args);
    }
};


