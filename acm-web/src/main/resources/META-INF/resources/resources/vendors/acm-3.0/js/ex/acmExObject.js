/**
 * AcmEx.Object
 *
 * @author jwu
 */
AcmEx.Object = {
    create : function() {
    }

    //This set of functions are to be retired in future.
    //Please use AcmEx.Object.SummerNote.xxxx()
    ,getSummerNote : function($s) {
        return $s.code();
    }
    ,setSummerNote : function($s, value) {
        $s.code(value);
    }
    ,editSummerNote: function($s) {
        $s.summernote({focus: true});
    }
    ,saveSummerNote: function($s) {
        var aHtml = $s.code(); //save HTML If you need(aHTML: array).
        $s.destroy();
        return aHtml;
    }
    ,cancelSummerNote: function($s) {
        $s.summernote({focus: false});
        $s.destroy();
    }
    ,SummerNote: {
        use: function($s) {
            $s.summernote();
        }
        ,get: function($s) {
            return $s.code();
        }
        ,set: function($s, value) {
            $s.summernote();
            $s.code(value);
            $s.destroy();
        }
        ,edit: function($s) {
            $s.summernote({focus: true});
        }
        ,save: function($s) {
            var aHtml = $s.code(); //save HTML If you need(aHTML: array).
            $s.destroy();
            return aHtml;
        }
        ,cancel: function($s) {
            $s.summernote({focus: false});
            $s.destroy();
        }
    }

    //
    //JTable functions are to be retired in future.
    //Please use AcmEx.Object.JTable.xxxx()
    //
    ,JTABLE_DEFAULT_PAGE_SIZE: 8
    ,jTableGetEmptyRecords: function() { return {"Result": "OK","Records": [],"TotalRecordCount": 0};}
    ,jTableGetEmptyRecord: function() { return {"Result": "OK","Record": {}};}
    ,jTableSetTitle: function(title) {
        //todo: passing $jt
        Acm.Object.setText($(".jtable-title-text"), title);
    }
    ,jTableLoad: function($jt) {
        $jt.jtable('load');
    }
    ,_catNextParam: function(url) {
        return (0 < url.indexOf('?'))? "&" : "?";
    }
    ,jTableDefaultPagingListAction: function(postData, jtParams, sortMap, urlEvealuator, responseHandler) {
        if (Acm.isEmpty(App.getContextPath())) {
            return AcmEx.Object.jTableGetEmptyRecords();
        }

        var url = urlEvealuator();
        if (Acm.isNotEmpty(jtParams.jtStartIndex)) {
            url += this._catNextParam(url) + "start=" + jtParams.jtStartIndex;
        }
        if (Acm.isNotEmpty(jtParams.jtPageSize)) {
            url += this._catNextParam(url) + "n=" + jtParams.jtPageSize;
        }
        if (Acm.isNotEmpty(jtParams.jtSorting)) {
            var arr = jtParams.jtSorting.split(" ");
            if (2 == arr.length) {
                for (var key in sortMap) {
                    if (key == arr[0]) {
                        url += this._catNextParam(url) + "s=" + sortMap[key] + "%20" + arr[1];
                    }
                }
            }
        }
        return $.Deferred(function ($dfd) {
            $.ajax({
                url: url,
                type: 'GET',
                dataType: 'json',
                data: postData,
                success: function (data) {
                    if (data) {
                        var jtResponse = responseHandler(data);
                    }

                    if (jtResponse.jtData) {
                        $dfd.resolve(jtResponse.jtData);
                    } else {
                        $dfd.reject();
                        Acm.Dialog.error(jtResponse.jtError);
                    }
                },
                error: function () {
                    $dfd.reject();
                }
            });
        });
    }
    ,jTableCreatePaging: function($jt, jtArg, sortMap) {
        jtArg.paging = true;
        if (!jtArg.pageSize) {
            jtArg.pageSize = AcmEx.Object.JTABLE_DEFAULT_PAGE_SIZE;
        }
        if (!jtArg.recordAdded) {
            jtArg.recordAdded = function(event, data){
                $jt.jtable('load');
            }
        }
        if (!jtArg.recordUpdated) {
            jtArg.recordUpdated = function(event, data){
                $jt.jtable('load');
            }
        }

        if (sortMap) {
            jtArg.sorting = true;
        } else if (!jtArg.sorting) {
            jtArg.sorting = false;
        }

        if (jtArg.actions.pagingListAction){
            jtArg.actions.listAction = function(postData, jtParams) {
                return jtArg.actions.pagingListAction(postData, jtParams, sortMap);
            }
        }

        $jt.jtable(jtArg);

        $jt.jtable('load');
    }

    ,JTable: {
        JTABLE_DEFAULT_PAGE_SIZE: 8
        ,getEmptyRecords: function() { return {"Result": "OK","Records": [],"TotalRecordCount": 0};}
        ,getEmptyRecord: function() { return {"Result": "OK","Record": {}};}
        ,setTitle: function($jt, title) {
            Acm.Object.setText($jt.find(".jtable-title-text"), title);
        }
        ,load: function($jt) {
            $jt.jtable('load');
        }
        ,useBasic: function($jt, jtArg) {
            $jt.jtable(jtArg);
            $jt.jtable('load');
        }
        //
        // sortMap can be overloaded as hash map, comparator, or paging url builder
        //
        ,usePaging: function($jt, jtArg, sortMap) {
            jtArg.paging = true;
            if (!jtArg.pageSize) {
                jtArg.pageSize = AcmEx.Object.JTable.JTABLE_DEFAULT_PAGE_SIZE;
            }
            if (!jtArg.recordAdded) {
                jtArg.recordAdded = function(event, data){
                    $jt.jtable('load');
                }
            }
            if (!jtArg.recordUpdated) {
                jtArg.recordUpdated = function(event, data){
                    $jt.jtable('load');
                }
            }

            if (sortMap) {
                jtArg.sorting = true;
            } else if (!jtArg.sorting) {
                jtArg.sorting = false;
            }

            if (jtArg.actions.pagingListAction){
                jtArg.actions.listAction = function(postData, jtParams) {
                    return jtArg.actions.pagingListAction(postData, jtParams, sortMap);
                }
            }

            jtArg.fields._idx = {
                type: "hidden"
                ,list: true
                ,create: false
                ,edit: false
            }

            $jt.jtable(jtArg);
            $jt.jtable('load');
        }
        ,useChildTable: function($jt, childLinks, arg, sortMap) {
            var argNew = {fields:{}};
            argNew.fields.subTables = {
                title: 'Entities'
                ,width: '10%'
                ,sorting: false
                ,edit: false
                ,create: false
                ,openChildAsAccordion: true
                ,display: function (commData) {
                    var $links = null;
                    for (var i = 0; i < childLinks.length; i++) {
                        if (0 == i) {
                            $links = childLinks[i]($jt);
                        } else {
                            $links = $links.add(childLinks[i]($jt));
                        }
                    }
                    return $links;
                }
            }
            for (var key in arg) {
                if ("fields" == key) {
                    for (var FieldKey in arg.fields) {
                        argNew.fields[FieldKey] = arg.fields[FieldKey];
                    }
                } else {
                    argNew[key] = arg[key];
                }
            }

//            $jt.jtable(argNew);
//            $jt.jtable('load');
            this.usePaging($jt, argNew, sortMap);
        }
        ,useAsChild: function($jt, $row, arg) {
            $jt.jtable('openChildTable'
                ,$row.closest('tr')
                ,arg
                ,function (data) { //opened handler
                    data.childTable.jtable('load');
                }
            );
        }
        ,toggleChildTable: function($t, $row, fnOpen, title) {
            var $childRow = $t.jtable('getChildRow', $row.closest('tr'));
            var curTitle = $childRow.find("div.jtable-title-text").text();

            var toClose;
            if ($t.jtable('isChildRowOpen', $row.closest('tr'))) {
                toClose = (curTitle === title);
            } else {
                toClose = false;
            }

            if (toClose) {
                //fnClose($t, $row);
                $t.jtable('closeChildTable', $row.closest('tr'));
            } else {
                fnOpen($t, $row);
            }
        }
        ,clickAddRecordHandler: function($jt, handler) {
            var $spanAddRecord = $jt.find(".jtable-toolbar-item-add-record");
            $spanAddRecord.unbind("click").on("click", function(e){handler(e, this);});
        }
        ,_hashMapComparator: function(item1, item2, sortBy, sortDir) {
            var value1 = item1[sortBy];
            var value2 = item2[sortBy];
            var rc = ((value1 < value2) ? -1 : ((value1 > value2) ? 1 : 0));
            return ("DESC" == sortDir)? -rc : rc;
//            if ("DESC" == sortDir) {
//                rc = -rc;
//            }
//            return rc;
        }
        //
        // comparator can be overloaded with a sortMap, in that case, a default comparator is used
        //
        ,getPagingItems: function(jtParams, arr, comparator) {
            var pagingItems = [];
            if (!Acm.isArrayEmpty(arr)) {
                var sortItems = [];
                for (var i = 0; i < arr.length; i++) {
                    var sortItem = {idx: i, item: arr[i]};
                    sortItems.push(sortItem);
                }

                if (Acm.isNotEmpty(jtParams.jtSorting)) {
                    sortItems.sort(function(a, b){
                        var jtSorting = jtParams.jtSorting;
                        var sortArr = jtSorting.split(" ");
                        var sortBy = sortArr[0];
                        var sortDir = sortArr[1];

                        if (!comparator) {
                            return 0;
                        } else if (typeof comparator === "function") {
                            return comparator(a.item, b.item, sortBy, sortDir);
                        } else if (typeof comparator === "object") {
                            var sortMap = comparator;
                            var itemSortBy = sortMap[sortBy];
                            return AcmEx.Object.JTable._hashMapComparator(a.item, b.item, itemSortBy, sortDir);
                        } else {
                            return 0;
                        }

                    });
                }

                var jtPageSize = jtParams.jtPageSize;
                var jtStartIndex = jtParams.jtStartIndex;
                for (var i = jtStartIndex; i < jtStartIndex + jtPageSize && i < sortItems.length; i++) {
                    pagingItems.push(sortItems[i]);
                }
            }
            return pagingItems;
        }
        ,getPagingRecord: function(pagingItem) {
            var record = {};
            record._idx = pagingItem.idx;
            return record;
        }
        ,getPagingItemData: function(pagingItem) {
            return pagingItem.item;
        }
        ,getPagingRow: function(data) {
            var record = data.record;
            return record._idx;
        }
        ,getTableRow: function(data) {
            var whichRow = data.row.prevAll("tr").length;  //count prev siblings
            return whichRow;
        }

//        //toggleSubJTable is to be retired; use toggleChildTable
//        ,toggleSubJTable: function($t, $row, fnOpen, fnClose, title) {
//            var $childRow = $t.jtable('getChildRow', $row.closest('tr'));
//            var curTitle = $childRow.find("div.jtable-title-text").text();
//
//            var toClose;
//            if ($t.jtable('isChildRowOpen', $row.closest('tr'))) {
//                toClose = (curTitle === title);
//            } else {
//                toClose = false;
//            }
//
//            if (toClose) {
//                fnClose($t, $row);
//            } else {
//                fnOpen($t, $row);
//            }
//        }
    }


    //
    // x-editable
    //
    ,XEditable: {
        useEditable: function($s, arg) {
            var hasSource = Acm.isNotEmpty(arg.source);
            var wasCreated = $s.hasClass("editable");
            if (hasSource && wasCreated) {
            	var currentValue = null;
            	if (Acm.isNotEmpty(arg.currentValue)) {
            		currentValue = arg.currentValue;
            	}
                $s.editable("option", "source", arg.source);
                $s.editable("option", "success", arg.success);
                $s.editable("setValue", currentValue);
            } else {
                arg.placement = Acm.goodValue(arg.placement, "bottom");
                arg.emptytext = Acm.goodValue(arg.emptytext, "Unknown");
                $s.editable(arg);
            }
        }

        ,useEditableDate: function($s, arg) {
            arg.placement = Acm.goodValue(arg.placement, "bottom");
            arg.emptytext = Acm.goodValue(arg.emptytext, "Unknown");
            arg.format = Acm.goodValue(arg.format, "mm/dd/yyyy");
            arg.viewformat = Acm.goodValue(arg.viewformat, $.t("common:date.short").toLowerCase());
            arg.datepicker = Acm.goodValue(arg.datepicker, {
                weekStart: 1
            });
            $s.editable(arg);
        }

        ,getValue: function($s) {
            return $s.editable("getValue");
        }
        ,setValue: function($s, txt) {
            $s.editable("setValue", txt);
        }
        ,setDate: function($s, txt) {
            if (txt) {
                // Apply internal format  'MM/DD/YYYY' to date
                txt = moment(txt, $.t("common:date.short")).format('MM/DD/YYYY');
                //jwu:
                //     no need to i18n "short", it is an internal string
                //     need to read format from config, but prefer to pass the format as argument, not to mingle with $.t or config functions here
                $s.editable("setValue", txt, true);  //true = use internal format
            } else {
                Acm.Object.setText($s, "Unknown");
            }
        }

        ,xDateToDatetime: function(d) {
            if (null == d) {
                return "";
            }
            var month = d.getMonth()+1;
            var day = d.getDate()+1;
            var year = d.getFullYear();
            var hour = d.getHours();
            var minute = d.getMinutes();
            var second = d.getSeconds();

            return year
                + "-" + this._padZero(month)
                + "-" + this._padZero(day)
                + "T" + this._padZero(hour)
                + ":" + this._padZero(minute)
                + ":" + this._padZero(second)
                + ".000+0000"
                ;
        }
        ,_padZero: function(i) {
            return (10 > i) ? "0" + i : "" + i;
        }
    }


    ////////////////// some function ideas for future use
    ,useDobInput: function($s) {
        $s.datepicker({
            changeMonth: true, changeYear: true,
//            yearRange: "-100:+1", minDate: '-100y', maxDate: '0',
            yearRange: "-100:+1", minDate: '-100y', maxDate: '+1y',
            onSelect: function(dateText){
                jQuery(this).css({'color' : '#000'});
            }
        });
    }

    ,useSsnInput: function($s) {
//        $s.keypress(function (keypressed) {
//            return AcmEx.Callbacks.onSsnKeypress(this, keypressed);
//        });

        $s.keyup(function() {
            // remove any characters that are not numbers
            var socnum = this.value.replace(/\D/g, '');
            var sslen  = socnum.length;

            if(sslen>3&&sslen<6) {
                var ssa=socnum.slice(0,3);
                var ssb=socnum.slice(3,5);
                this.value=ssa+"-"+ssb;
            } else {
                if(sslen>5) {
                    var ssa=socnum.slice(0,3);
                    var ssb=socnum.slice(3,5);
                    var ssc=socnum.slice(5,9);
                    this.value=ssa+"-"+ssb+"-"+ssc;
                } else {
                    this.value=socnum;
                }
            }
        });
    }
    ,getSsnValue: function (ssnDisplay) {
        var ssn = ssnDisplay.replace(/\D/g, '');
        return ssn;
    }
    ,getSsnDisplay: function (ssn) {
        if (Acm.isEmpty(ssn))
            return "";

        var ssnDisplay = ssn.substring(0, 3) + '-' + ssn.substring(3, 5) + '-' + ssn.substring(5, 9);
        return ssnDisplay;
    }

    ,usePagination: function ($s, totalItems, itemsPerPage, callback) {
        if (totalItems > 0) {
            $s.paginationskipinit(totalItems,
                {
                    items_per_page: itemsPerPage,
                    pagerLocation: "both",
                    prev_text: "Previous",
                    next_text: "Next",
                    prev_show_always: false,
                    next_show_always: false,
                    pages_show_always: true,
                    callback: callback
                }
            );
        } else {
            $s.html("");
        }
    }

};




