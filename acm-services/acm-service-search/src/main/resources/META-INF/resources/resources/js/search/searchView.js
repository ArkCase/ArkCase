/**
 * Search.View
 *
 * @author jwu
 */
Search.View = {
    create : function() {
        if (Search.View.MicroData.create)        {Search.View.MicroData.create();}
        if (Search.View.Query.create)            {Search.View.Query.create();}
        if (Search.View.Facet.create)            {Search.View.Facet.create();}
        if (Search.View.Results.create)          {Search.View.Results.create();}
    }
    ,onInitialized: function() {
        if (Search.View.MicroData.onInitialized) {Search.View.MicroData.onInitialized();}
        if (Search.View.Query.onInitialized)     {Search.View.Query.onInitialized();}
        if (Search.View.Facet.onInitialized)     {Search.View.Facet.onInitialized();}
        if (Search.View.Results.onInitialized)   {Search.View.Results.onInitialized();}
    }

    ,MicroData: {
        create : function() {
            this.objectTypes = Acm.Object.MicroData.getJson("objectTypes");
        }
        ,onInitialized: function() {
        }

        ,findObjectType: function(type) {
            var found = null;
            if (Acm.isArray(this.objectTypes)) {
                for (var i = 0; i < this.objectTypes.length; i++) {
                    var objectType = this.objectTypes[i];
                    if (Acm.compare(type, objectType.type)) {
                        found = objectType;
                        break;
                    }
                }
            }
            return found;
        }
    }

    ,Query: {
        create: function() {
            this.$edtSearch = $("#searchQuery");
            this.$btnSearch = this.$edtSearch.next().find("button");
            this.$btnSearch.on("click", function(e) {Search.View.Query.onClickBtnSearch(e, this);});
            this.$edtSearch.keyup(function(event){
                if(13 == event.keyCode){
                    Search.View.Query.$btnSearch.click();
                }
            });

            if ("undefined" != typeof Topbar) {
                Acm.Dispatcher.addEventListener(Topbar.Controller.QuickSearch.VIEW_CHANGED_QUICK_SEARCH_TERM ,this.onTopbarViewChangedQuickSearchTerm);
            }
        }
        ,onInitialized: function() {
            if ("undefined" != typeof Topbar) {
                var term = Topbar.Model.QuickSearch.getQuickSearchTerm();
                Topbar.Model.QuickSearch.setQuickSearchTerm(null);
                Search.View.Query._submit(term);
            }
        }

        ,onClickBtnSearch : function(event, ctrl) {
            event.preventDefault();

            var term = Search.View.Query.getValueEdtSearch();
            Search.View.Query._submit(term);
        }

        ,onTopbarViewChangedQuickSearchTerm: function(term) {
            Search.View.Query._submit(term);
            return true;
        }
        ,_submit: function(term) {
            if (Acm.isNotEmpty(term)) {
                Search.Controller.viewSubmittedQuery(term);
            }
        }
        ,getValueEdtSearch: function() {
            return Acm.Object.getPlaceHolderInput(this.$edtSearch);
        }
    }

    ,Facet: {
        create: function() {
            this.$divFacet = $(".facets");

            Acm.Dispatcher.addEventListener(Search.Controller.MODEL_CHANGED_FACET  ,this.onModelChangedFacet);
        }
        ,onInitialized: function() {
        }

        ,onClickCheckBox: function(event, ctrl) {
            if(event.ctrlKey) {
                return;
            }

            var selected = [];
            var $checked = Search.View.Facet.$divFacet.find("input:checked");
            $checked.each(function(){
                var s = {};
                s.value = $(this).val();
                s.name = $(this).parent().parent().attr("name");
                s.type = $(this).parent().parent().parent().attr("name");
                selected.push(s);
            });

            Search.Controller.viewChangedFacetSelection(selected);
        }

        ,onModelChangedFacet: function(facet) {
            if (facet.hasError) {
                //alert("View: onModelChangedFacet, hasError, errorMsg:" + facet.errorMsg);
            }
            Search.View.Facet.buildFacetPanel(facet);
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
            var si = Search.Model.getSearchInfo();

            if (Search.Model.validateSearchFacet(facet)) {

                if (0 < Search.Model.getCountFacetFields(facet)){
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
                                        if (Search.Model.findFilter(si, display, Acm.goodValue(facet.facet_fields[i].values[j].name))) {
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

                if (0 < Search.Model.getCountFacetQueries(facet)){
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
                                        if (Search.Model.findFilter(si, display, Acm.goodValue(facet.facet_queries[i].values[j].name))) {
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

                if (0 < Search.Model.getCountFacetDates(facet)){
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
                                        if (Search.Model.findFilter(si, display, Acm.goodValue(facet.facet_dates[i].values[j].name))) {
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

            this.$divFacet.find("input[type='checkbox']").on("click", function(e) {Search.View.Facet.onClickCheckBox(e, this);});
        }

        ,setHtmlDivFacet: function(val) {
            return Acm.Object.setHtml(this.$divFacet, val);
        }
    }

    ,Results: {
        create: function() {
            this.$divResults = $("#divResults");
            Search.View.Results.useJTable(this.$divResults);

            Acm.Dispatcher.addEventListener(Search.Controller.VIEW_SUBMITTED_QUERY         ,this.onViewSubmittedQuery        ,Acm.Dispatcher.PRIORITY_LOW);
            Acm.Dispatcher.addEventListener(Search.Controller.VIEW_CHANGED_FACET_SELECTION ,this.onViewChangedFacetSelection ,Acm.Dispatcher.PRIORITY_LOW);
        }
        ,onInitialized: function() {
        }

        ,onViewSubmittedQuery: function(term) {
            AcmEx.Object.JTable.load(Search.View.Results.$divResults);
        }
        ,onViewChangedFacetSelection: function(selected) {
            //todo: compare selected with si.filter, do nothing if same

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
                    Record.title   = Acm.goodValue(result.docs[i].title_parseable);
                    Record.parent  = Acm.goodValue(result.docs[i].name);
                    Record.parentType = Acm.goodValue(result.docs[i].object_type_s);
                    Record.owner   = Acm.goodValue(result.docs[i].assignee_full_name_lcs); //owner_s
                    Record.created = Acm.goodValue(result.docs[i].modified_date_tdt); //create_tdt
                    jtData.Records.push(Record);
                }

                jtData.TotalRecordCount = result.numFound;
            }
            return jtData;
        }
        ,useJTable: function($jt) {
            var sortMap = {};
            sortMap["title"] = "title_parseable";

            AcmEx.Object.JTable.usePaging($jt
                ,{
                    title: 'Search Results'
                    ,multiselect: false
                    ,selecting: false
                    ,selectingCheckboxes: false
                    ,pageSize: 16
                    ,paging: true
                    ,sorting: true
                    ,actions: {
                        pagingListAction: function (postData, jtParams, sortMap) {
                            var si = Search.Model.getSearchInfo();
                            if (Acm.isEmpty(si.q)) {
                                return AcmEx.Object.JTable.getEmptyRecords();
                            }
                            si.start = Acm.goodValue(jtParams.jtStartIndex, 0);

                            if (Search.Model.isFacetUpToDate()) {
                                //var page = si.start;
                                //var result = Search.Model.cacheResult.get(page);
                                var result = Search.Model.getCachedResult(si);
                                if (result) {
                                    return Search.View.Results._makeJtData(result);
                                }
                            }

                            return Search.Service.facetSearchDeferred(si
                                ,postData
                                ,jtParams
                                ,sortMap
                                ,function(data) {
                                    var result = data;

                                    var title = si.total + ' results of "' + si.q + '"';
                                    AcmEx.Object.JTable.setTitle($jt, title);

                                    return Search.View.Results._makeJtData(result);
                                }
                                ,function(error) {
                                    AcmEx.Object.JTable.setTitle($jt, "Error occurred");
                                }
                            );

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
                                var url = "#";
                                var objectType = Search.View.MicroData.findObjectType(Acm.goodValue(data.record.type));
                                if (objectType) {
                                    url = App.getContextPath() + Acm.goodValue(objectType.url) + Acm.goodValue(data.record.id) + Acm.goodValue(objectType.urlEnd);
                                }

//                                var url = App.getContextPath();
//                                if (App.OBJTYPE_CASE == data.record.type) {
//                                    url += "/plugin/casefile/" + data.record.id;
//                                } else if (App.OBJTYPE_COMPLAINT == data.record.type) {
//                                    url += "/plugin/complaint/" + data.record.id;
//                                } else if (App.OBJTYPE_TASK == data.record.type) {
//                                    url += "/plugin/task/" + data.record.id;
//                                } else if (App.OBJTYPE_DOCUMENT == data.record.type) {
//                                    url += "/plugin/document/" + data.record.id;
//                                } else if (App.OBJTYPE_PEOPLE == data.record.type) {
//                                    url += "/plugin/people/" + data.record.id;
//                                }else if (App.OBJTYPE_PERSON == data.record.type) {
//                                    url += "/plugin/person/" + data.record.id;
//                                }


                                var $lnk = $("<a href='" + url + "'>" + data.record.name + "</a>");
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
                        ,parent: {
                            title: 'Parent'
                            ,width: '15%'
                            ,sorting: false
                            ,display: function(data) {
                                var url = "#";
                                var objectType = Search.View.MicroData.findObjectType(Acm.goodValue(data.record.parentType));
                                if (objectType) {
                                    url = App.getContextPath() + Acm.goodValue(objectType.url) + Acm.goodValue(data.record.id) + Acm.goodValue(objectType.urlEnd);
                                }
                                var $lnk = $("<a href='" + url + "'>" + data.record.name + "</a>");
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
                        ,created: {
                            title: 'Modified'
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

