/**
 * SearchBase is namespace component for common search component shared by search, notification, subscription pages
 *
 * @author jwu
 */
var SearchBase = SearchBase || {
    create: function(name, $edtSearch, $btnSearch, $divFacet, $divResults, args, jtDataMaker) {
        if (SearchBase.Controller.create) {SearchBase.Controller.create(name);}
        if (SearchBase.Model.create)      {SearchBase.Model.create();}
        if (SearchBase.View.create)       {SearchBase.View.create($edtSearch, $btnSearch, $divFacet, $divResults, args, jtDataMaker);}
    }
    ,onInitialized: function() {
        if (SearchBase.Controller.onInitialized) {SearchBase.Controller.onInitialized();}
        if (SearchBase.Model.onInitialized)      {SearchBase.Model.onInitialized();}
        if (SearchBase.View.onInitialized)       {SearchBase.View.onInitialized();}
    }

    ,createDialog: function(name, $edtSearch, $btnSearch, $divFacet, $divResults, args, jtDataMaker) {
        if (!args) {
            args = {multiselect:true, selecting:true, selectingCheckboxes:true};
        }
        this.create(name, $edtSearch, $btnSearch, $divFacet, $divResults, args, jtDataMaker);
        this.onInitialized();
    }
    ,setApi: function(url) {
        SearchBase.Model.setApi(url);
    }
    ,fixFilters: function(filters) {
        SearchBase.Model.fixFilters(filters);
    }
};

