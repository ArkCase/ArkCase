/**
 * SearchBase.View
 *
 * @author jwu
 */
SearchBase.View = {
    create : function($edtSearch, $btnSearch, $divFacet, $divResults, args, jtDataMaker) {
        if (SearchBase.View.Query.create)            {SearchBase.View.Query.create($edtSearch, $btnSearch);}
        if (SearchBase.View.Facet.create)            {SearchBase.View.Facet.create($divFacet);}
        if (SearchBase.View.Results.create)          {SearchBase.View.Results.create($divResults, args, jtDataMaker);}
    }
    ,onInitialized: function() {
        if (SearchBase.View.Query.onInitialized)     {SearchBase.View.Query.onInitialized();}
        if (SearchBase.View.Facet.onInitialized)     {SearchBase.View.Facet.onInitialized();}
        if (SearchBase.View.Results.onInitialized)   {SearchBase.View.Results.onInitialized();}
    }


    ,Query: {
        create: function($edtSearch, $btnSearch) {
            this.$edtSearch = $edtSearch;
            this.$btnSearch = $btnSearch;
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
        create: function($divFacet) {
            this.$divFacet = $divFacet;
            this.setHtmlDivFacet("");

            Acm.Dispatcher.replaceEventListener(SearchBase.Controller.MODEL_CHANGED_FACET  ,this.onModelChangedFacet);
        }
        ,onInitialized: function() {
        }

        ,onClickCheckBox: function(event, ctrl) {
            if(event.ctrlKey) {
                return;
            }

            var selected = [];
            var $checked = SearchBase.View.Facet.$divFacet.find("input:checked");
            $checked.each(function(){
                var s = {};
                s.value = $(this).val();
                s.name = $(this).parent().parent().attr("name");
                s.type = $(this).parent().parent().parent().attr("name");
                selected.push(s);
            });

            SearchBase.Controller.viewChangedFacetSelection(selected);
        }

        ,onModelChangedFacet: function(facet) {
            if (facet.hasError) {
                //alert("View: onModelChangedFacet, hasError, errorMsg:" + facet.errorMsg);
            }
            SearchBase.View.Facet.buildFacetPanel(facet);
        }

        ,_getFacetDisplay: function(label, key) {
            if (Acm.isNotEmpty(label)) {
                return Acm.goodValue(label);
            } else {
                return Acm.goodValue(key);
            }
        }
        ,buildFacetPanel: function(facet) {
            var html = "";
            var si = SearchBase.Model.getSearchInfo();

            if (SearchBase.Model.validateSearchFacet(facet)) {

                if (0 < SearchBase.Model.getCountFacetFields(facet)){
                    html += "<div name='facet_fields'>";
                    for(var i = 0; i < facet.facet_fields.length; i++) {
                        if (0 < Acm.goodValue(facet.facet_fields[i].count, 0)) {
                            if (Acm.isArray(facet.facet_fields[i].values)) {
                                var display = this._getFacetDisplay(facet.facet_fields[i].label, facet.facet_fields[i].key);
                                html += "<h6>" + display + "</h6>";
                                html += "<div class='list-group auto' name='" + display + "'>";
                                for (var j = 0; j < facet.facet_fields[i].values.length; j++) {
                                    if (0 < Acm.goodValue(facet.facet_fields[i].values[j].count, 0)) {
                                        html += "<label class='list-group-item'><input type='checkbox' value='" + Acm.goodValue(facet.facet_fields[i].values[j].name) + "'";
                                        if (SearchBase.Model.findFilter(si, display, Acm.goodValue(facet.facet_fields[i].values[j].name))) {
                                            html += " checked";
                                        }
                                        html += " /><span class='badge bg-info'>" + facet.facet_fields[i].values[j].count
                                            + "</span>" + Acm.goodValue(facet.facet_fields[i].values[j].name)
                                            + "</label>";
                                    }
                                }
                                html += "</div>";
                            }
                        }
                    }
                    html += "</div>";
                }

                if (0 < SearchBase.Model.getCountFacetQueries(facet)){
                    html += "<div name='facet_queries'>";
                    for(var i = 0; i < facet.facet_queries.length; i++) {
                        if (0 < Acm.goodValue(facet.facet_queries[i].count, 0)) {
                            if (Acm.isArray(facet.facet_queries[i].values)) {
                                var display = this._getFacetDisplay(facet.facet_queries[i].label, facet.facet_queries[i].key);
                                html += "<h6>" + display + "</h6>";
                                html += "<div class='list-group auto' name='" + display + "'>";
                                for (var j = 0; j < facet.facet_queries[i].values.length; j++) {
                                    if (0 < Acm.goodValue(facet.facet_queries[i].values[j].count, 0)) {
                                        html += "<label class='list-group-item'><input type='checkbox' value='" + Acm.goodValue(facet.facet_queries[i].values[j].name) + "'";
                                        if (SearchBase.Model.findFilter(si, display, Acm.goodValue(facet.facet_queries[i].values[j].name))) {
                                            html += " checked";
                                        }
                                        html += " /><span class='badge bg-info'>" + facet.facet_queries[i].values[j].count
                                            + "</span>" + Acm.goodValue(facet.facet_queries[i].values[j].name)
                                            + "</label>";
                                    }
                                }
                                html += "</div>";
                            }
                        }
                    }
                    html += "</div>";
                }

                if (0 < SearchBase.Model.getCountFacetDates(facet)){
                    html += "<div name='facet_dates'>";
                    for(var i = 0; i < facet.facet_dates.length; i++) {
                        if (0 < Acm.goodValue(facet.facet_dates[i].count, 0)) {
                            if (Acm.isArray(facet.facet_dates[i].values)) {
                                var display = this._getFacetDisplay(facet.facet_dates[i].label, facet.facet_dates[i].key);
                                html += "<h6>" + display + "</h6>";
                                html += "<div class='list-group auto' name='" + display + "'>";
                                for (var j = 0; j < facet.facet_dates[i].values.length; j++) {
                                    if (0 < Acm.goodValue(facet.facet_dates[i].values[j].count, 0)) {
                                        html += "<label class='list-group-item'><input type='checkbox' value='" + Acm.goodValue(facet.facet_dates[i].values[j].name) + "'";
                                        if (SearchBase.Model.findFilter(si, display, Acm.goodValue(facet.facet_dates[i].values[j].name))) {
                                            html += " checked";
                                        }
                                        html += " /><span class='badge bg-info'>" + facet.facet_dates[i].values[j].count
                                            + "</span>" + Acm.goodValue(facet.facet_dates[i].values[j].name)
                                            + "</label>";
                                    }
                                }
                                html += "</div>";
                            }
                        }
                    }
                    html += "</div>";
                }
            }  //end if validateSearchFacet

            this.setHtmlDivFacet(html);

            this.$divFacet.find("input[type='checkbox']").on("click", function(e) {SearchBase.View.Facet.onClickCheckBox(e, this);});
        }

        ,setHtmlDivFacet: function(val) {
            return Acm.Object.setHtml(this.$divFacet, val);
        }
    }

    ,Results: {
        create: function($divResults, args, jtDataMaker) {
            this.$divResults = $divResults;
            this.jtDataMaker = (jtDataMaker)? jtDataMaker : this._jtDataMakerDefault;
            SearchBase.View.Results._useJTable(args);

            Acm.Dispatcher.replaceEventListener(SearchBase.Controller.VIEW_SUBMITTED_QUERY         ,this.onViewSubmittedQuery        ,Acm.Dispatcher.PRIORITY_LOW);
            Acm.Dispatcher.replaceEventListener(SearchBase.Controller.VIEW_CHANGED_FACET_SELECTION ,this.onViewChangedFacetSelection ,Acm.Dispatcher.PRIORITY_LOW);
        }
        ,onInitialized: function() {
        }
        ,getSelectedRows: function() {
            return this.$divResults.jtable('selectedRows');
        }
        ,onViewSubmittedQuery: function(term) {
            AcmEx.Object.JTable.load(SearchBase.View.Results.$divResults);
        }
        ,onViewChangedFacetSelection: function(selected) {
            //todo: compare selected with si.filter, do nothing if same

            AcmEx.Object.JTable.load(SearchBase.View.Results.$divResults);
        }

        ,_jtDataMakerDefault: function(result) {
            var jtData = AcmEx.Object.JTable.getEmptyRecords();
            if (result) {
                for (var i = 0; i < result.docs.length; i++) {
                    var Record = {};
                    Record.id = result.docs[i].object_id_s;
                    Record.name       = Acm.goodValue(result.docs[i].name);
                    Record.type       = Acm.goodValue(result.docs[i].object_type_s);
                    Record.title      = Acm.goodValue(result.docs[i].title_parseable);
                    Record.parentId   = Acm.goodValue(result.docs[i].parent_id_s);
                    Record.parentName = Acm.goodValue(result.docs[i].parent_number_lcs);
                    Record.parentType = Acm.goodValue(result.docs[i].parent_type_s);
                    Record.owner      = Acm.goodValue(result.docs[i].assignee_full_name_lcs); //owner_s
                    Record.modified   = Acm.getDateTimeFromDatetime(result.docs[i].modified_date_tdt);
                    jtData.Records.push(Record);
                }

                jtData.TotalRecordCount = result.numFound;
            }
            return jtData;
        }

        ,_argsDefault: {
            title: 'Search Results'
            ,multiselect: false
            ,selecting: false
            ,selectingCheckboxes: false
            ,pageSize: 16
            ,paging: true
            ,sorting: true
            ,actions: {
                pagingListAction: function (postData, jtParams, sortMap) {
                    var si = SearchBase.Model.getSearchInfo();
                    if (Acm.isEmpty(si.q)) {
                        return AcmEx.Object.JTable.getEmptyRecords();
                    }
                    si.start = Acm.goodValue(jtParams.jtStartIndex, 0);

                    if (SearchBase.Model.isFacetUpToDate()) {
                        //var page = si.start;
                        //var result = SearchBase.Model.cacheResult.get(page);
                        var result = SearchBase.Model.getCachedResult(si);
                        if (result) {
                            return SearchBase.View.Results.jtDataMaker(result);
                        }
                    }

                    return SearchBase.Service.facetSearchDeferred(si
                        ,postData
                        ,jtParams
                        ,sortMap
                        ,function(data) {
                            var result = data;

                            var title = si.total + ' results of "' + si.q + '"';
                            AcmEx.Object.JTable.setTitle(SearchBase.View.Results.$divResults, title);

                            return SearchBase.View.Results.jtDataMaker(result);
                        }
                        ,function(error) {
                            AcmEx.Object.JTable.setTitle(SearchBase.View.Results.$divResults, "Error occurred");
                        }
                    );

                }
            }  //end actions
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
                        var url = App.buildObjectUrl(Acm.goodValue(data.record.type), Acm.goodValue(data.record.id), "#");
                        var $lnk = $("<a href='" + url + "'>" + Acm.goodValue(data.record.name) + "</a>");


                        //$lnk.click(function(){alert("click" + data.record.id)});

                        //var $lnk = $("<p>line1</p><p>line2</p></br><p>line3</p><a href='" + url + "'>" + data.record.name + "</a><div>hello world1</div><div>hello world2</div>");

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
                ,parentId: {
                    title: 'Parent ID'
                    ,key: false
                    ,list: false
                    ,create: false
                    ,edit: false
                    ,sorting: false
                }
                ,parentName: {
                    title: 'Parent'
                    ,width: '15%'
                    ,sorting: false
                    ,display: function(data) {
                        var url = App.buildObjectUrl(Acm.goodValue(data.record.parentType), Acm.goodValue(data.record.parentId), "#");
                        var $lnk = $("<a href='" + url + "'>" + Acm.goodValue(data.record.parentName, Acm.goodValue(data.record.parentId)) + "</a>");
                        return $lnk;
                    }
                }
                ,parentType: {
                    title: 'Parent Type'
                    ,sorting: false
                    ,list: false
                }
                ,owner: {
                    title: 'Assignee'
                    ,width: '15%'
                    ,sorting: false
                }
                ,modified: {
                    title: 'Modified'
                    ,type: 'textarea'
                    ,width: '20%'
                    ,sorting: false
                }
            } //end field
        } //end argDefault

        ,_useJTable: function(args) {
            var argsToUse = this._argsDefault;
            for (var arg in args) {
                argsToUse[arg] = args[arg];
            }

            var sortMap = {};
            sortMap["title"] = "title_parseable";

            AcmEx.Object.JTable.usePaging(this.$divResults, argsToUse, sortMap);

        }
    }

};

