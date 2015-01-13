/**
 * Search.View
 *
 * @author jwu
 */
Search.View = {
    create : function() {
        if (Search.View.Query.create)            {Search.View.Query.create();}
        if (Search.View.Facet.create)            {Search.View.Facet.create();}
        if (Search.View.Results.create)          {Search.View.Results.create();}
    }
    ,onInitialized: function() {
        if (Search.View.Query.onInitialized)     {Search.View.Query.onInitialized();}
        if (Search.View.Facet.onInitialized)     {Search.View.Facet.onInitialized();}
        if (Search.View.Results.onInitialized)   {Search.View.Results.onInitialized();}
    }

    ,Query: {
        create: function() {
            this.$edtSearch = $("#searchQuery");
            this.$btnSearch = this.$edtSearch.next().find("button");
            this.$btnSearch.on("click", function(e) {Search.View.Query.onClickBtnSearch(e, this);});
        }
        ,onInitialized: function() {
        }
//        ,onPostInit: function() {
//            var term = Topbar.Model.QuickSearch.getQuickSearchTerm();
//            if (Acm.isNotEmpty(term)) {
//                Search.Object.reloadJTableResults();
//            }
//        }
        ,onClickBtnSearch : function(event, ctrl) {
            event.preventDefault();

            var term = Search.View.Query.getValueEdtSearch();
            Search.Controller.viewSubmittedQuery(term);
        }

        ,getValueEdtSearch: function() {
            return Acm.Object.getPlaceHolderInput(this.$edtSearch);
        }
    }

    ,Facet: {
        create: function() {
            this.$divSearchQuery = $("#divFacet");

//            this.$imgPicLoading    = $("#picLoading");
//            this.$lnkChangePicture = $("#lnkChangePicture");
//            this.$formPicture      = $("#formPicture");
//            this.$fileInput        = $("#file");
//
//            this.$lnkChangePicture.on("click", function(e) {Search.View.Picture.onClickLnkChangePicture(e, this);});
//            this.$fileInput.on("change", function(e) {Search.View.Picture.onChangeFileInput(e, this);});
//            this.$formPicture.submit(function(e) {Search.View.Picture.onSubmitFormPicture(e, this);});
//
//
            Acm.Dispatcher.addEventListener(Search.Controller.MODEL_CHANGED_FACET  ,this.onModelChangedFacet);
        }
        ,onInitialized: function() {
        }

        ,onClickLnkChangePicture: function(event, ctrl) {

        }

        ,onModelChangedFacet: function(facet) {
            if (facet.hasError) {
                //alert("View: onModelChangedFacet, hasError, errorMsg:" + facet.errorMsg);
            }
            //to build facet UI component
        }
    }

    ,Results: {
        create: function() {
            this.$divResults = $("#divResults");
            Search.View.Results.useJTable(this.$divResults);

            Acm.Dispatcher.addEventListener(Search.Controller.VIEW_SUBMITTED_QUERY, this.onViewSubmittedQuery, Acm.Dispatcher.PRIORITY_LOW);
        }
        ,onInitialized: function() {
        }

        ,onViewSubmittedQuery: function(term) {
            AcmEx.Object.JTable.load(Search.View.Results.$divResults);
        }

        ,_makeJtData: function(result) {
            var jtData = AcmEx.Object.JTable.getEmptyRecords();
            if (result) {
                for (var i = 0; i < result.docs.length; i++) {
                    var Record = {};
                    Record.id = result.docs[i].object_id_s;
                    Record.name    = Acm.goodValue(result.docs[i].name);
                    Record.type    = Acm.goodValue(result.docs[i].object_type_s);
                    Record.title   = Acm.goodValue(result.docs[i].title_t);
                    Record.owner   = Acm.goodValue(result.docs[i].owner_s);
                    Record.created = Acm.goodValue(result.docs[i].create_dt);
                    jtData.Records.push(Record);
                }

                jtData.TotalRecordCount = result.numFound;
            }
            return jtData;
        }
        ,useJTable: function($jt) {
            var sortMap = {};
            sortMap["title"] = "title_t";

            AcmEx.Object.JTable.usePaging($jt
                ,{
                    title: 'Search Results'
                    ,multiselect: false
                    ,selecting: false
                    ,selectingCheckboxes: false
                    ,paging: true
                    ,sorting: true
                    ,actions: {
                        pagingListAction: function (postData, jtParams, sortMap) {
                            var term = Search.Model.getTerm();
                            if (Acm.isEmpty(term)) {
                                return AcmEx.Object.JTable.getEmptyRecords();
                            }

                            var page = Acm.goodValue(jtParams.jtStartIndex, 0);
                            var result = Search.Model.cacheResult.get(page);
                            if (result) {
                                return Search.View.Results._makeJtData(result);

                            } else {
                                return Search.Service.facetSearchDeferred(term
                                    ,postData
                                    ,jtParams
                                    ,sortMap
                                    ,function(data) {
                                        var result = data;
                                        return Search.View.Results._makeJtData(result);
                                    }
                                    ,function(error) {
                                    }
                                );
                            }  //end else
                        }

                    }

                    ,fields: {
                        id: {
                            title: 'ID'
                            ,key: true
                            ,list: false
                            ,create: false
                            ,edit: false
                            ,sorting: false
                        }
                        ,name: {
                            title: 'Name'
                            ,width: '15%'
                            ,sorting: false
                            ,display: function(data) {
                                var url = App.getContextPath();
                                if (App.OBJTYPE_CASE == data.record.type) {
                                    url += "/plugin/casefile/" + data.record.id;
                                } else if (App.OBJTYPE_COMPLAINT == data.record.type) {
                                    url += "/plugin/complaint/" + data.record.id;
                                } else if (App.OBJTYPE_TASK == data.record.type) {
                                    url += "/plugin/task/" + data.record.id;
                                } else if (App.OBJTYPE_DOCUMENT == data.record.type) {
                                    url += "/plugin/document/" + data.record.id;
                                } else if (App.OBJTYPE_PEOPLE == data.record.type) {
                                    url += "/plugin/people/" + data.record.id;
                                }else if (App.OBJTYPE_PERSON == data.record.type) {
                                    url += "/plugin/person/" + data.record.id;
                                }
                                var $lnk = $("<a href='" + url + "'>" + data.record.name + "</a>");
                                //$lnk.click(function(){alert("click" + data.record.id)});
                                return $lnk;
                            }
                        }
                        ,type: {
                            title: 'Type'
                            //,options: [App.OBJTYPE_CASE, App.OBJTYPE_COMPLAINT, App.OBJTYPE_TASK, App.OBJTYPE_DOCUMENT]
                            ,sorting: false
                        }
                        ,title: {
                            title: 'Title'
                            ,width: '30%'
                        }
                        ,owner: {
                            title: 'Owner'
                            ,width: '15%'
                            ,sorting: false
                        }
                        ,created: {
                            title: 'Created'
                            ,type: 'textarea'
                            ,width: '20%'
                            ,sorting: false
                        }
                    } //end field
                } //end arg
                ,sortMap
            );
        }
    }

};

