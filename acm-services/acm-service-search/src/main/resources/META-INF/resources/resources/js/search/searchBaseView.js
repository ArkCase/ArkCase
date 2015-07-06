/**
 * SearchBase.View
 *
 * @author jwu
 */
SearchBase.View = {
    create : function(args) {
        if (SearchBase.View.Query.create)            {SearchBase.View.Query.create(args);}
        if (SearchBase.View.Facet.create)            {SearchBase.View.Facet.create(args);}
        if (SearchBase.View.Results.create)          {SearchBase.View.Results.create(args);}
    }
    ,onInitialized: function() {
        if (SearchBase.View.Query.onInitialized)     {SearchBase.View.Query.onInitialized();}
        if (SearchBase.View.Facet.onInitialized)     {SearchBase.View.Facet.onInitialized();}
        if (SearchBase.View.Results.onInitialized)   {SearchBase.View.Results.onInitialized();}
    }

    ,Query: {
        create: function(args) {
            var $edtSearchDefault  = $("#searchQuery");
            var $btnSearchDefault  = $edtSearchDefault.next().find("button");
            this.$edtSearch = (args.$edtSearch)? args.$edtSearch : $edtSearchDefault;
            this.$btnSearch = (args.$btnSearch)? args.$btnSearch : $btnSearchDefault;
            this.$btnSearch.unbind("click").on("click", function(e) {SearchBase.View.Query.onClickBtnSearch(e, this);});
            this.$edtSearch.keyup(function(event){
                if(13 == event.keyCode){
                    SearchBase.View.Query.$btnSearch.click();
                }
            });
            this.setValueEdtSearch("");
        }
        ,onInitialized: function() {
        }

        ,onClickBtnSearch : function(event, ctrl) {
            event.preventDefault();

            var term = SearchBase.View.Query.getValueEdtSearch();
            SearchBase.View.Query.submit(term);
        }

        ,submit: function(term) {
            if (Acm.isNotEmpty(term)) {
                SearchBase.Controller.viewSubmittedQuery(term);
            }
        }
        ,getValueEdtSearch: function() {
            return Acm.Object.getPlaceHolderInput(this.$edtSearch);
        }
        ,setValueEdtSearch: function(value) {
            Acm.Object.setPlaceHolderInput(this.$edtSearch, value);
        }
    }

    ,Facet: {
        create: function(args) {
            this.$divFacets = (args.$divFacets)? args.$divFacets : $("#divFacets");
            this.topFacets = args.topFacets;

            //Acm.Dispatcher.replaceEventListener(SearchBase.Controller.MODEL_CHANGED_FACET  ,this.onModelChangedFacet);
            Acm.Dispatcher.replaceEventListener(SearchBase.Controller.MODEL_CHANGED_RESULT  ,this.onModelChangedResult);
        }
        ,onInitialized: function() {
            SearchBase.View.Facet.initFacetPanel();
        }

        ,onClickCheckBox: function(event, ctrl) {
            if(event.ctrlKey) {
                return;
            }

            var selected = [];
            var $checked = SearchBase.View.Facet.$divFacets.find("input:checked");
            $checked.each(function(){
                var s = {};
                s.value = $(this).val();
                s.name = $(this).parent().parent().attr("name");
                s.type = $(this).parent().parent().parent().attr("name");
                selected.push(s);
            });

            SearchBase.Controller.viewChangedFacetSelection(selected);
        }

//        ,onModelChangedFacet: function(facet) {
//            if (facet.hasError) {
//                //alert("View: onModelChangedFacet, hasError, errorMsg:" + facet.errorMsg);
//            }
//            SearchBase.View.Facet.buildFacetPanel(facet);
//        }
        ,onModelChangedResult: function(data) {
            if (data) {
                if (!SearchBase.Model.isFacetUpToDate()) {
                    SearchBase.Model.setFacetUpToDate(true);
                    var facet = SearchBase.Model.makeFacet(data);
                    SearchBase.View.Facet.buildFacetPanel(facet);
                }
            }
        }

        ,_getFacetDisplay: function(label, key) {
            if (Acm.isNotEmpty(label)) {
                return Acm.goodValue(label);
            } else {
                return Acm.goodValue(key);
            }
        }
        ,initFacetPanel: function() {
            var si = SearchBase.Model.getSearchInfo();
            var html = this._buildFilterSection(si.filters);
            this.setHtmlDivFacet(html);
        }
        //mergeFilters
        ,_buildFacetSection: function(sectionName, sectionFields, si, fixedFilters, copiedFilters, buildTopFacets) {
            if (!buildTopFacets) {
                buildTopFacets = false;
            }

            var html = "";
            if (Acm.isArray(sectionFields)) {
                html += "<div name='" + sectionName + "'>";
                for(var i = 0; i < sectionFields.length; i++) {
                    if (0 < Acm.goodValue(sectionFields[i].count, 0)) {
                        var fieldKey = this._getFacetDisplay(sectionFields[i].label, sectionFields[i].key);
                        if (Acm.isNotEmpty(fieldKey)) {
                            if (Acm.isArray(sectionFields[i].values)) {
                                var isTopFacet = Acm.isItemInArray(fieldKey, this.topFacets);
                                if (isTopFacet == buildTopFacets) {
                                    html += "<h6>" + fieldKey + "</h6>";
                                    html += "<div class='list-group auto' name='" + fieldKey + "'>";
                                    for (var j = 0; j < sectionFields[i].values.length; j++) {
                                        var fieldValue = sectionFields[i].values[j].name;
                                        if (Acm.isNotEmpty(fieldValue)) {
                                            var fieldCount = Acm.goodValue(sectionFields[i].values[j].count, 0);
                                            var isFixedFilter = SearchBase.Model.findFilter(fixedFilters, fieldKey, fieldValue);
                                            if (0 < fieldCount || isFixedFilter) {
                                                html += "<label class='list-group-item'><input type='checkbox' value='" + fieldValue + "'";
                                                if (SearchBase.Model.findFilter(si.filters, fieldKey, fieldValue)) {
                                                    html += " checked";
                                                }
                                                if (isFixedFilter) {
                                                    html += " disabled";
                                                    SearchBase.Model.removeFilter(copiedFilters, fieldKey, fieldValue);
                                                }
                                                html += " /><span class='badge bg-info'>" + fieldCount
                                                    + "</span>" + fieldValue
                                                    + "</label>";
                                            }
                                        }
                                    }
                                    html += "</div>";
                                }
                            }
                        }
                    }
                }
                html += "</div>";
            }
            return html;
        }
        ,_buildFilterSection: function(filters) {
            var html = "";
            if (SearchBase.Model.validateFilters(filters)) {
                html += "<div name='filter_fields'>";
                for (var i = 0; i < filters.length; i++) {
                    var fieldKey = filters[i].key;
                    if (Acm.isNotEmpty(fieldKey)) {
                        html += "<h6>" + fieldKey + "</h6>";
                        html += "<div class='list-group auto' name='" + fieldKey + "'>";
                        for (var j = 0; j < filters[i].values.length; j++) {
                            var fieldValue = Acm.goodValue(filters[i].values[j]);
                            if (Acm.isNotEmpty(fieldValue)) {
                                html += "<label class='list-group-item'><input type='checkbox' value='" + fieldValue + "'";
                                if (SearchBase.Model.findFilter(filters, fieldKey, fieldValue)) {
                                    html += " checked disabled";
                                }
                                html += " /><span class='badge bg-info'>" + "0"
                                    + "</span>" + fieldValue
                                    + "</label>";
                            }
                        }
                        html += "</div>";
                    }
                }
                html += "</div>";
            }
            return html;
        }
        ,buildFacetPanel: function(facet) {
            var htmlFixed = "";
            var htmlTopFacets = "";
            var html = "";
            var si = SearchBase.Model.getSearchInfo();
            var fixedFilters = SearchBase.Model.getFixedFilters();
            var copiedFilters = [];
            SearchBase.Model.addFixedFilters(copiedFilters);

            if (SearchBase.Model.validateSearchFacet(facet)) {
                if (0 < SearchBase.Model.getCountFacetFields(facet)){
                    html += this._buildFacetSection("facet_fields", facet.facet_fields, si, fixedFilters, copiedFilters);
                }
                if (0 < SearchBase.Model.getCountFacetQueries(facet)){
                    html += this._buildFacetSection("facet_queries", facet.facet_queries, si, fixedFilters, copiedFilters);
                }
                if (0 < SearchBase.Model.getCountFacetDates(facet)){
                    html += this._buildFacetSection("facet_dates", facet.facet_dates, si, fixedFilters, copiedFilters);
                }

                if (0 < SearchBase.Model.getCountFacetFields(facet)){
                    htmlTopFacets += this._buildFacetSection("facet_fields", facet.facet_fields, si, fixedFilters, copiedFilters, true);
                }
                if (0 < SearchBase.Model.getCountFacetQueries(facet)){
                    htmlTopFacets += this._buildFacetSection("facet_queries", facet.facet_queries, si, fixedFilters, copiedFilters, true);
                }
                if (0 < SearchBase.Model.getCountFacetDates(facet)){
                    htmlTopFacets += this._buildFacetSection("facet_dates", facet.facet_dates, si, fixedFilters, copiedFilters, true);
                }
            }  //end if validateSearchFacet

            htmlFixed = this._buildFilterSection(copiedFilters);

            this.setHtmlDivFacet(htmlTopFacets + htmlFixed + html);

            this.$divFacets.find("input[type='checkbox']").on("click", function(e) {SearchBase.View.Facet.onClickCheckBox(e, this);});
        }

        ,setHtmlDivFacet: function(val) {
            return Acm.Object.setHtml(this.$divFacets, val);
        }
    }

    ,Results: {
        create: function(args) {
            this.$divResults = (args.$divResults)? args.$divResults : $("#divResults");
            AcmEx.Object.JTable.setTitle(this.$divResults, "Search Results");

            this.jtDataMaker = (args.jtDataMaker)? args.jtDataMaker : this._jtDataMakerDefault;
            SearchBase.View.Results._useJTable(args.jtArgs);

            //Acm.Dispatcher.replaceEventListener(SearchBase.Controller.VIEW_SUBMITTED_QUERY         ,this.onViewSubmittedQuery        ,Acm.Dispatcher.PRIORITY_LOW);
            Acm.Dispatcher.replaceEventListener(SearchBase.Controller.VIEW_SUBMITTED_QUERY         ,this.onViewSubmittedQuery);
            //Acm.Dispatcher.replaceEventListener(SearchBase.Controller.VIEW_CHANGED_FACET_SELECTION ,this.onViewChangedFacetSelection ,Acm.Dispatcher.PRIORITY_LOW);
            Acm.Dispatcher.replaceEventListener(SearchBase.Controller.VIEW_CHANGED_FACET_SELECTION ,this.onViewChangedFacetSelection);
        }
        ,onInitialized: function() {
        }
        ,getSelectedRows: function() {
            return this.$divResults.jtable('selectedRows');
        }
        ,onViewSubmittedQuery: function(term) {
            if (SearchBase.Model.submittedQuery(term)) {
                AcmEx.Object.JTable.load(SearchBase.View.Results.$divResults);
            }
        }
        ,onViewChangedFacetSelection: function(selected) {
            if (SearchBase.Model.changedFacetSelection(selected)) {
                AcmEx.Object.JTable.load(SearchBase.View.Results.$divResults);
            }

        }

        ,displayName: function(data) {
            var url = App.buildObjectUrl(Acm.goodValue(data.record.type), Acm.goodValue(data.record.id), "#");
            var $lnk = $("<a href='" + url + "'>" + Acm.goodValue(data.record.name) + "</a>");
            //var $lnk = $("<p>line1</p><p>line2</p></br><p>line3</p><a href='" + url + "'>" + data.record.name + "</a><div>hello world1</div><div>hello world2</div>");
            return $lnk;
        }
        ,displayParent: function(data) {
            var url = App.buildObjectUrl(Acm.goodValue(data.record.parentType), Acm.goodValue(data.record.parentId), "#");
            var $lnk = $("<a href='" + url + "'>" + Acm.goodValue(data.record.parentName, Acm.goodValue(data.record.parentId)) + "</a>");
            return $lnk;
        }
        ,_getDefaultJtArgs: function() {
            return {
                $jt: this.$divResults
                ,sortMap: {name  : "name"
                    ,title       : "title_parseable"
                    ,modified    : "modified_date_tdt"
                }
                ,dataMaker: function(result) {
                    var jtData = AcmEx.Object.JTable.getEmptyRecords();
                    if (result && Acm.isArray(result.docs)) {
                        for (var i = 0; i < result.docs.length; i++) {
                            var Record = {};
                            Record.id         = Acm.goodValue(result.docs[i].object_id_s);
                            Record.name       = Acm.goodValue(result.docs[i].name);
                            Record.type       = Acm.goodValue(result.docs[i].object_type_s);
                            Record.title      = Acm.goodValue(result.docs[i].title_parseable);
                            Record.parentId   = Acm.goodValue(result.docs[i].parent_id_s);
                            Record.parentName = Acm.goodValue(result.docs[i].parent_number_lcs);
                            Record.parentType = Acm.goodValue(result.docs[i].parent_type_s);
                            Record.owner      = Acm.goodValue(result.docs[i].assignee_full_name_lcs); //owner_s
                            Record.modified   = Acm.getDateTimeFromDatetime(result.docs[i].modified_date_tdt,$.t("common:date.full"));
                            Record.email      = Acm.goodValue(result.docs[i].email_lcs);
                            jtData.Records.push(Record);
                        }

                        jtData.TotalRecordCount = Acm.goodValue(result.numFound, 0);
                    }
                    return jtData;
                }
                ,keyGetter: function(si, jtParams) {
                    var comboKey = Acm.goodValue(si.q) + "." + JSON.stringify(Acm.goodValue(si.filters, []));
                    return AcmEx.Model.JTable.defaultIdCacheKey(comboKey, jtParams);
                }

                ,title: $.t("search:table.title")
                ,multiselect: false
                ,selecting: false
                ,selectingCheckboxes: false
                ,pageSize: 20
                ,actions: {
                    serviceListAction: function (postData, jtParams, sortMap, dataMaker, keyGetter) {
                        var si = SearchBase.Model.getSearchInfo();
                        if (Acm.isEmpty(si.q)) {
                            return AcmEx.Object.JTable.getEmptyRecords();
                        }

                        si.start = Acm.goodValue(jtParams.jtStartIndex, 0);
                        si.n = Acm.goodValue(jtParams.jtPageSize, 0);
                        si.s = Acm.goodValue(jtParams.jtSorting);

                        var cacheKey = keyGetter(si, jtParams);
                        return SearchBase.Model.facetSearchListAction(si
                                ,postData
                                ,jtParams
                                ,sortMap
                                ,dataMaker
                                ,cacheKey
                            ).done(function(result) {
                                var title;
                                if(si.q == "*"){
                                    title = si.total + ' results';
                                } else{
                                    title = si.total + ' results of "' + si.q + '"';
                                }
                                AcmEx.Object.JTable.setTitle(SearchBase.View.Results.$divResults, title);

                            }).fail(function(response) {
                                AcmEx.Object.JTable.setTitle(SearchBase.View.Results.$divResults, "Error occurred");
                            })
                        ;
                    }
                }  //end actions
                ,fields: {
                    id: {
                        title: $.t("search:table.field.id")
                        ,key: true
                        ,list: false
                        ,create: false
                        ,edit: false
                        ,sorting: false
                    }
                    ,name: {
                        title: $.t("search:table.field.name")
                        ,width: '15%'
                        ,sorting: false
                        ,display: function(data) {
                            return SearchBase.View.Results.displayName(data);
                        }
                    }
                    ,type: {
                        title: $.t("search:table.field.type")
                        //,options: [App.OBJTYPE_CASE, App.OBJTYPE_COMPLAINT, App.OBJTYPE_TASK, App.OBJTYPE_DOCUMENT]
                        ,sorting: false
                    }
                    ,title: {
                        title: $.t("search:table.field.title")
                        ,width: '30%'
                    }
                    ,parentId: {
                        title: $.t("search:table.field.parent-id")
                        ,key: false
                        ,list: false
                        ,create: false
                        ,edit: false
                        ,sorting: false
                    }
                    ,parentName: {
                        title: $.t("search:table.field.parent-name")
                        ,width: '15%'
                        ,sorting: false
                        ,display: function(data) {
                            return SearchBase.View.Results.displayParent(data);
                        }
                    }
                    ,parentType: {
                        title: $.t("search:table.field.parent-type")
                        ,sorting: false
                        ,list: false
                    }
                    ,owner: {
                        title: $.t("search:table.field.owner")
                        ,width: '15%'
                        ,sorting: false
                    }
                    ,modified: {
                        title: $.t("search:table.field.modified")
                        ,type: 'textarea'
                        ,width: '20%'
                    }
                } //end field
            };
        }

        ,_useJTable: function(jtArgs) {
                var jtArgsToUse = this._getDefaultJtArgs();
                for (var arg in jtArgs) {
                    jtArgsToUse[arg] = jtArgs[arg];
                }

            AcmEx.Object.JTable.usePaging_new(jtArgsToUse);
        }
    }

};

