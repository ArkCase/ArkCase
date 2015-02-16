/**
 * SearchBase is namespace component for common search component shared by search, notification, subscription pages
 *
 * @author jwu
 */
var SearchBase = SearchBase || {
    create: function(args) {
        if (SearchBase.Controller.create) {SearchBase.Controller.create(args);}
        if (SearchBase.Model.create)      {SearchBase.Model.create(args);}
        if (SearchBase.View.create)       {SearchBase.View.create(args);}
    }
    ,onInitialized: function() {
        if (SearchBase.Controller.onInitialized) {SearchBase.Controller.onInitialized();}
        if (SearchBase.Model.onInitialized)      {SearchBase.Model.onInitialized();}
        if (SearchBase.View.onInitialized)       {SearchBase.View.onInitialized();}
    }

    ,showSearchDialog: function(args) {
        if (Acm.isEmpty(args.$dlgObjectPicker)) {
            args.$dlgObjectPicker = $("#dlgObjectPicker");
        }
        if (Acm.isEmpty(args.$edtSearch)) {
            args.$edtSearch = $("#edtPoSearch");
        }
        if (Acm.isEmpty(args.$btnSearch)) {
            args.$btnSearch = args.$edtSearch.next().find("button");
        }
        if (Acm.isEmpty(args.$divFacets)) {
            args.$divFacets = $("#divPoFacets");
        }
        if (Acm.isEmpty(args.$divResults)) {
            args.$divResults = $("#divPoResults");
        }
        if (!args.jtArgs) {
            args.jtArgs = {multiselect:true, selecting:true, selectingCheckboxes:true};
        }
        this.create(args);

        Acm.deferred(SearchBase.onInitialized);

        Acm.Dialog.modal(args.$dlgObjectPicker, args.onClickBtnPrimary, args.onClickBtnDefault);
    }
//    ,validateArgs: function(data) {
//        if (Acm.isEmpty(data)) {
//            return false;
//        }
//        if (Acm.isEmpty(data.$edtSearch)) {
//            return false;
//        }
//        if (Acm.isEmpty(data.$btnSearch)) {
//            return false;
//        }
//        if (Acm.isEmpty(data.$divFacets)) {
//            return false;
//        }
//        if (Acm.isEmpty(data.$divResults)) {
//            return false;
//        }
//        return true;
//    }
};

