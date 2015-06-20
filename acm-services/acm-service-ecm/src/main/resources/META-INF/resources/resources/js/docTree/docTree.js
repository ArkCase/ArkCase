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

    ,Dialog: {
        create: function(args) {
            if (Acm.isEmpty(args.$divResults)) {
                args.$tree = $("#treeDocumentPicker");
            }
//        if (!args.jtArgs) {
//            args.jtArgs = {multiselect:true, selecting:true, selectingCheckboxes:true};
//        }
            DocTree.create(args);

            if (Acm.isEmpty(args.$dlgDocumentPicker)) {
                args.$dlgDocumentPicker = $("#dlgDocumentPicker");
            }
            this.$dlgDocumentPicker = args.$dlgDocumentPicker;

            if (Acm.isNotEmpty(args.title)) {
                args.$dlgDocumentPicker.find('.modal-title').text(args.title);
            }
            if (Acm.isNotEmpty(args.btnOkText)) {
                args.$dlgDocumentPicker.find('button.btn-primary').text(args.btnOkText);
            }
            if (Acm.isNotEmpty(args.btnCancelText)) {
                args.$dlgDocumentPicker.find('button.btn-default').text(args.btnCancelText);
            }
            this.onClickBtnPrimary = args.onClickBtnPrimary;
            this.onClickBtnDefault = args.onClickBtnDefault;

            Acm.deferred(DocTree.onInitialized);

            return this;
        }
        ,show: function() {
            Acm.Dialog.modal(this.$dlgDocumentPicker, this.onClickBtnPrimary, this.onClickBtnDefault);
        }
        ,getSelector: function() {
            return this.$dlgDocumentPicker;
        }
        ,getSelectedNodes: function() {
            return DocTree.View.tree.getSelectedNodes();
        }

    }


};


