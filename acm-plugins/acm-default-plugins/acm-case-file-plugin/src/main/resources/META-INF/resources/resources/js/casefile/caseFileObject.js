/**
 * CaseFile.Object
 *
 * manages screen objects
 *
 * @author jwu
 */
CaseFile.Object = {
    initialize : function() {
        var ti = this.getTreeInfo();
        var tiApp = CaseFile.Object.getCaseFileTreeInfo();
        if (tiApp) {
            ti.initKey = tiApp.initKey;
            ti.start = tiApp.start;
            ti.n = tiApp.n;
            ti.s = tiApp.s;
            ti.q = tiApp.q;
            ti.caseFileId = tiApp.caseFileId;
            CaseFile.Object.setCaseFileTreeInfo(null);
        }
        var items = $(document).items();
        var caseFileId = items.properties("caseFileId").itemValue();
        if (Acm.isNotEmpty(caseFileId)) {
            ti.caseFileId = caseFileId;
        }

        this.$tree = $("#tree");
        this._useFancyTree(this.$tree);


        this.$tabTop = $("#tabTop");
        this.$tabTopBlank = $("#tabTopBlank");

        //this.$lnkCaseType         = $("#caseType");
        //this.$lnkCloseDisposition = $("#disposition");

        this.$lnkIncidentDate        = $("#incidentDate");
        this.$lnkIncidentDate.editable({placement: 'bottom'
            ,emptytext: "Unknown"
            ,format: 'mm/dd/yyyy'
            ,viewformat: 'mm/dd/yyyy'
            ,datepicker: {
                weekStart: 1
            }
            ,success: function(response, newValue) {
                CaseFile.Event.onSaveIncidentDate(newValue);
            }
        });


        this.$lnkCloseDate       = $("#closeDate");
        /*this.$lnkCloseDate.editable({placement: 'bottom'
            ,format: 'mm/dd/yyyy'
           // ,disabled: true
            ,emptytext : "Unknown"
            ,viewformat: 'mm/dd/yyyy'
            ,datepicker: {
                weekStart: 1
            }
            ,success: function(response, newValue) {
                CaseFile.Event.onSaveCloseDate(newValue);
            }
        });*/

        this.$labCaseNumber  = $("#caseNumber"); //this.$lnkCloseDate.parent("div").next("small");
        this.$lnkCaseTitle   = $("#caseTitle");
        this.$lnkCaseTitle.editable({placement: 'bottom'
            ,emptytext: "Unknown"
            ,success: function(response, newValue) {
                CaseFile.Event.onSaveCaseTitle(newValue);
            }
        });


        this.$divRois        = $("#divRois");
        //CaseFile.JTable.createJTableRois(this.$divRois);
        this.$spanAddRoi   = this.$divRois.find(".jtable-toolbar-item-add-record");
        this.$spanAddRoi.unbind("click").on("click", function(e){CaseFile.Event.onClickSpanAddRoi(e);});
        CaseFile.Page.fillReportSelection();

        this.$h4ItemTitleHeader = $("#itemTitle").parent();
        this.$lnkItemTitle  = $("#itemTitle");
        this.$lnkItemTitle.editable({placement: 'bottom'
            ,emptytext: "Unknown"
            ,success: function(response, newValue) {
                CaseFile.Event.onSaveCasePriority(newValue);
            }
        });

        this.$lnkCaseClose = $("#closeCase");
        this.$lnkCaseClose.click(function(e){CaseFile.Event.onCloseCase(e)});

        this.$lnkPriority = $("#priority");
        this.$lnkPriority.editable({placement: 'bottom'
            ,emptytext: "Unknown"
            ,success: function(response, newValue) {
                CaseFile.Event.onSaveCasePriority(newValue);
            }
        });
        this.$lnkAssignee = $("#assignee");
        this.$lnkAssignee.editable({placement: 'bottom'
            ,emptytext: "Unknown"
            ,success: function(response, newValue) {
                CaseFile.Event.onSaveCaseAssignee(newValue);
            }
        });

        this.$lnkSubjectType = $("#subjectType");
        this.$lnkSubjectType.editable({placement: 'bottom'
            ,emptytext: "Unknown"
            ,success: function(response, newValue) {
                CaseFile.Event.onSaveCaseSubjectType(newValue);
            }
        });

        this.$lnkStatus = $("#status");
        this.$lnkStatus.editable({placement: 'bottom'
            ,emptytext: "Unknown"
            ,success: function(response, newValue) {
                CaseFile.Event.onSaveCaseStatus(newValue);
            }
        });




        var formUrls = new Object();
        formUrls["roi"] = $('#roiFormUrl').val();
        this.setFormUrls(formUrls);
    }


	,_formUrls: null
	,getFormUrls: function() {
		return this._formUrls;
	}
	,setFormUrls: function(formUrls) {
		this._formUrls = formUrls;
	}

	,beforeSpanAddRoi: function(html) {
	    this.$spanAddRoi.before(html);
	}
	
	,getSelectReport: function() {
        return Acm.Object.getSelectValue(this.$spanAddRoi.prev().find("select"));
    }
	
    ,showTop: function(show) {
        Acm.Object.show(this.$tabTop, show);
        Acm.Object.show(this.$tabTopBlank, !show);
    }
    ,modal: function($s, onClickBtnPrimary, onClickBtnDefault) {
        if (onClickBtnPrimary) {
            $s.find("button.btn-primary").unbind("click").on("click", function(e){
                onClickBtnPrimary(e, this);
                $s.modal("hide");
            });
        }
        if (onClickBtnDefault) {
            $s.find("button.btn-default").unbind("click").on("click", function(e){onClickBtnDefault(e, this);});
        }

        $s.modal("show");
    }

    ,showDlgAddItem: function(onClickBtnPrimary) {
        this.modal(this.$dlgAddItem, onClickBtnPrimary);
    }


//    ,initCaseType: function(data) {
//        var choices = [];
//        $.each(data, function(idx, val) {
//            var opt = {};
//            opt.value = val;
//            opt.text = val;
//            choices.push(opt);
//        });
//
//        this.$lnkCaseType.editable({placement: 'bottom'
//            ,value: ""
//            ,source: choices
//            ,success: function(response, newValue) {
//                CaseFile.Event.onSaveCaseType(newValue);
//            }
//        });
//    }

//    ,initCloseDisposition: function(data) {
//        var choices = [];
//        $.each(data, function(idx, val) {
//            var opt = {};
//            opt.value = val;
//            opt.text = val;
//            choices.push(opt);
//        });
//
//        this.$lnkCloseDisposition.editable({placement: 'bottom'
//            ,value: ""
//            ,source: choices
//            ,success: function(response, newValue) {
//                CaseFile.Event.onSaveCloseDisposition(newValue);
//            }
//        });
//    }


    //borrow from complaint for now; will fix it later
    ,getCompleteItemModal: function(){
        return this.$lnkCompleteItemModal;
    }

    ,getCaseFileTreeInfo: function() {
        return App.getComplaintTreeInfo();
    }
    ,setCaseFileTreeInfo: function(treeInfo) {
        App.setComplaintTreeInfo(null);
    }

    ,setTextLabCaseNumber: function(txt) {
        Acm.Object.setText(this.$labCaseNumber, txt);
    }
    ,setTextLnkCaseTitle: function(txt) {
        //Acm.Object.setText(this.$lnkCaseTitle, txt);
        this.$lnkCaseTitle.editable("setValue",txt);
    }
    ,setTextLnkIncidentDate: function(txt) {
        if(txt){
            Acm.Object.setText(this.$lnkIncidentDate, txt);
        }
        else{
            txt = "Unknown"
            Acm.Object.setText(this.$lnkIncidentDate, txt);
        }
        //this.$lnkIncidentDate.editable("setValue", txt, true);
    }
    ,setTextLnkCloseDate: function(txt) {
        if(txt){
            Acm.Object.setText(this.$lnkCloseDate, txt);
        }
        else{
            txt = "Unknown"
            Acm.Object.setText(this.$lnkCloseDate, txt);
        }
        //this.$lnkCloseDate.editable("setValue", txt, true);
    }
//    ,setValueLnkCaseType: function(txt) {
//        this.$lnkCaseType.editable("setValue", txt);
//    }
//    ,setValueLnkCloseDisposition: function(txt) {
//        this.$lnkCloseDisposition.editable("setValue", txt);
//    }

    ,populateCaseFile: function(c) {
        this.setTextLabCaseNumber(c.caseNumber);
        this.setTextLnkCaseTitle(c.title);

        //this.setValueLnkCaseType(c.caseType);
        this.setTextLnkIncidentDate(Acm.getDateFromDatetime(c.created));
        this.setTextLnkCloseDate(Acm.getDateFromDatetime(c.closed));
        //this.setValueLnkCloseDisposition(c.disposition);

        /*this.refreshJTablePerson();
        this.refreshJTableRois();*/
//        this.refreshJTableClosingDocs();
    }


//    ,getSelectReport: function() {
//        return Acm.Object.getSelectValue(this.$spanAddDocument.prev().find("select"));
//    }
    ,showTab: function(key) {
        var tabIds = ["tabBlank"
            ,"tabTitle"
            ,"tabPerson"
            ,"tabRois"
            ,"tabRoi"
        ];
        var tabIdsToShow = this._getTabIdsByKey(key);
        for (var i = 0; i < tabIds.length; i++) {
            var show = this._foundItemInArray(tabIds[i], tabIdsToShow);
            Acm.Object.show($("#" + tabIds[i]), show);
        } //for i
    }
    ,_foundItemInArray: function(item, arr) {
        for (var i = 0; i < arr.length; i++) {
            if (item == arr[i]) {
                return true;
            }
        }
        return false;
    }



    //
    //------------------ Tree of CaseFiles ------------------
    //
    ,_treeInfo: {
        start           : 0
        ,n              : 10
        ,total          : -1
        ,s              : null
        ,q              : null
        ,initKey        : null
        ,caseFileId    : 0
    }
    ,getTreeInfo: function() {
        return this._treeInfo;
    }
//    ,setTreeInfo: function(ti) {
//        this._treeInfo = ti;
//    }

    //
    //tabIds               - nodeType - key
    //------------------------------------------
    //tabBlank             - prevPage - prevPage
    //tabBlank             - p        - [pageId]
    //tabTitle,tabEvent    - pc       - [pageId].[caseFileId]
    //tabPerson            - pcp      - [pageId].[caseFileId].p
    //tabRois              - pcr      - [pageId].[caseFileId].r
    //tabRoi               - pcrc     - [pageId].[caseFileId].r.[itemId]
    //tabBlank             - nextPage - nextPage
    //
    ,getNodeTypeByKey: function(key) {
        if (Acm.isEmpty(key)) {
            return null;
        }

        var arr = key.split(".");
        if (1 == arr.length) {
            if ("prevPage" == key) {
                return "prevPage";
            } else if ("nextPage" == key) {
                return "nextPage";
            } else { //if ($.isNumeric(arr[0])) {
                return "p";
            }
        } else if (2 == arr.length) {
            return "pc";
        } else if (3 == arr.length) {
            return "pc" + arr[2];
        } else if (4 == arr.length) {
            return "pc" + arr[2] + "c";
        }
        return null;
    }
    ,_mapNodeTab: {pc: ["tabTitle"]
        ,pcp:  ["tabPerson"]
        ,pcr:  ["tabRois"]
        ,pcrc: ["tabRoi"]
    }
    ,_getTabIdsByKey: function(key) {
        var nodeType = this.getNodeTypeByKey(key);
        var tabIds = ["tabBlank"];
        for (var key in this._mapNodeTab) {
            if (nodeType == key) {
                tabIds = this._mapNodeTab[key];
                break;
            }
        }
        return tabIds;
    }
    ,getCaseFileIdByKey: function(key) {
        return this._parseKey(key).caseFileId;
    }
    ,getPageIdByKey: function(key) {
        return this._parseKey(key).pageId;
    }
    ,getChildIdByKey: function(key) {
        return this._parseKey(key).childId;
    }
    ,_parseKey: function(key) {
        var parts = {pageId: -1, caseFileId: 0, sub: "", childId: 0};
        if (Acm.isEmpty(key)) {
            return parts;
        }

        var arr = key.split(".");
        if (1 <= arr.length) {
            var pageId = parseInt(arr[0]);
            if (! isNaN(pageId)) {
                parts.pageId = pageId;
            }
        }
        if (2 <= arr.length) {
            var caseFileId = parseInt(arr[1]);
            if (! isNaN(caseFileId)) {
                parts.caseFileId = caseFileId;
            }
        }
        if (3 <= arr.length) {
            parts.sub = arr[2];
        }
        if (4 <= arr.length) {
            var childId = parseInt(arr[3]);
            if (! isNaN(caseFileId)) {
                parts.childId = childId;
            }
        }
        return parts;
    }
    ,refreshTree: function(key) {
        this.tree.reload().done(function(){
            if (Acm.isNotEmpty(key)) {
                CaseFile.Object.tree.activateKey(key);
            }
        });
    }
    ,activeTreeNode: function(key) {
        this.tree.activateKey(key);
    }
    ,expandAllTreeNode: function(key) {
        this.tree.activateKey(key);
    }
    ,_useFancyTree: function($s) {
        $s.fancytree({
            activate: function(event, data) {
                var node = data.node;
                var key = node.key;
                var nodeType = CaseFile.Object.getNodeTypeByKey(key);

                CaseFile.Event.onActivateTreeNode(data.node);
            }
            ,dblclick: function(event, data) {
                var node = data.node;
                //alert("dblclick:(" + node.key + "," + node.title + ")");
                //node.setExpanded();
                //toggleExpanded();
            }

            ,focus: function(event, data) {
//                var node = data.node;
//                if ("prevPage" == node.key) {
//                    alert("onFocus:" + node.key);
//                } else if ("nextPage" == node.key) {
//                    alert("onFocus:" + node.key);
//                }
            }
            ,renderNode: function(event, data) {
                // Optionally tweak data.node.span
                var node = data.node;
                var key = node.key;
                var title = node.title;
                var acmIcon = null; //node.data.acmIcon;
                var nodeType = CaseFile.Object.getNodeTypeByKey(key);
                if ("pc" == nodeType) {
                    acmIcon = "<i class='i i-folder'></i>" //"i-notice icon"
                } else if ("prevPage" == nodeType) {
                    acmIcon = "<i class='i i-arrow-up'></i>";
                } else if ("nextPage" == nodeType) {
                    acmIcon = "<i class='i i-arrow-down'></i>";
                }
                if (acmIcon) {
                    var span = node.span;
                    var $spanIcon = $(span.children[1]);
                    $spanIcon.removeClass("fancytree-icon");
                    $spanIcon.html(acmIcon);
                }

            }
//            ,extensions: ["table"]
//
//            ,table: {
//                nodeColumnIdx: 0 // render the node title into the 2nd column
//                //,checkboxColumnIdx: 1 // render the checkboxes into the 1st column
//            }
//
//            ,renderColumns: function(event, data) {
//                var node = data.node,
//                $tdList = $(node.tr).find(">td");
//                // (index #0 is rendered by fancytree by adding the checkbox)
//                $tdList.eq(1).text(node.data.description1);
//                // (index #2 is rendered by fancytree)
//            }

//            ,source: function() {
//                var rc = [
//                    {title: "Node 1", key: "1"}
//                    ,{title: "Folder 2", key: "2", folder: true, children: [
//                        {title: "Node 2.1", key: "3", myOwnAttr: "abc"},
//                        {title: "Node 2.2", key: "4"}
//                    ]}
//                    ,{title: "Folder 30", key: "30", folder: true, lazy: true}
//                ];
//                return rc;
//            }
            ,lazyLoad: function(event, data) {
                CaseFile.Object.lazyLoad(event, data);
            }
            ,loadError: function(event, data) {
                CaseFile.Object.loadError(event, data);
            }
            ,source: function() {
                return CaseFile.Object.treeSource();
            } //end source
        }); //end fancytree

        this.tree = this.$tree.fancytree("getTree");

        $s.contextmenu({
            //delegate: "span.fancytree-title",
            delegate: ".fancytree-title",
            menu: CaseFile.Object.menu_cur,
            beforeOpen: function(event, ui) {
                var node = $.ui.fancytree.getNode(ui.target);
//                node.setFocus();
                node.setActive();
                CaseFile.Object.$tree.contextmenu("replaceMenu", CaseFile.Object._getMenu(node));

            },
            select: function(event, ui) {
                var node = $.ui.fancytree.getNode(ui.target);
                alert("select " + ui.cmd + " on " + node);
            }
        });

    }

    ,deferredGet: function(urlEvaluator, successHandler, postData) {
        var url = urlEvaluator();
        return $.Deferred(function ($dfd) {
            var arg = {
                url: url
                ,type: 'GET'
                ,dataType: 'json'
                ,success: function (data) {
                    var rc = null;
                    if (data) {
                        rc = successHandler(data);
                    }

                    if (rc) {
                        $dfd.resolve(rc);
                    } else {
                        $dfd.reject();
                    }
                }
                ,error: function () {
                    $dfd.reject();
                }
            };
            if (postData) {
                arg.data = postData;
            }
            $.ajax(arg);
        });
    }
    ,treeSource: function() {
        var builder = AcmEx.FancyTreeBuilder.reset();

        var treeInfo = CaseFile.Object.getTreeInfo();
        var start = treeInfo.start;
        var caseFiles = CaseFile.cachePage.get(start);
        if (null == caseFiles || 0 >= caseFiles.length) {
            return builder.getTree();
        }

        if (0 < treeInfo.start) {
            builder.addLeaf({key: "prevPage"
                ,title: treeInfo.start + " records above..."
                ,tooltip: "Review previous records"
                ,expanded: false
                ,folder: false
            });
        }


        var pageId = start.toString();
        for (var i = 0; i < caseFiles.length; i++) {
            var c = caseFiles[i];
            var caseId = parseInt(c.object_id_s);
            builder.addBranch({key: pageId + "." + caseId                       //level 1: /CaseFile
                ,title: c.name + " (" + c.title_t + ")"
                ,tooltip: c.title_t
                ,expanded: false
            })
            .addLeafLast({key: pageId + "." + caseId + ".r"                   //level 2: /CaseFile/ROIs
                ,title: "ROIs"
                ,folder: true
                ,lazy: true
                ,cache: false
            })

        } //end for i

        if ((0 > treeInfo.total)                                    //unknown size
            || (treeInfo.total - treeInfo.n > treeInfo.start)) {   //no more page left
            var title = (0 > treeInfo.total)? "More records..."
                : (treeInfo.total - treeInfo.start - treeInfo.n) + " more records...";
            builder.addLeafLast({key: "nextPage"
                ,title: title
                ,tooltip: "Load more records"
                ,expanded: false
                ,folder: false
            });
        }

        return builder.getTree();
    }
    ,_getCaseFileKey: function(caseFileId) {
        var treeInfo = CaseFile.Object.getTreeInfo();
        var start = treeInfo.start;
        var pageId = start.toString();
        return pageId + "." + caseFileId;
    }
    ,lazyLoad: function(event, data) {
        var key = data.node.key;
        var caseFileId = this.getCaseFileIdByKey(key);
        var c = CaseFile.getCaseFile();
        if (!c) {
            data.result = [];
            return;
        }
        var nodeType = this.getNodeTypeByKey(key);
        switch (nodeType) {

            case "pcr":
                data.result = [{key: key + "." + "1", title: "ROI1" + "[Status]"}
                    ,{key: key + "." + "2", title: "ROI2" + "[Status]"}
                ];
                break;

//            case "ajax":
//                data.result = { url: "ajax-sub2.json" /*, debugDelay: 5000*/ };
//                break;
//            case "custom":
//                data.result = $.Deferred(function (dfd) {
//                    setTimeout(function () {
//                        dfd.resolve([
//                            { title: "Sub item 1" },
//                            { title: "Sub item 2" }
//                        ]);
//                    }, 1000);
//                });
//                break;
//            case "ajax-error":
//                data.result = { url: "not-found.json" };
//                break;
//            case "custom-error":
//                data.result = $.Deferred(function (dfd) {
//                    setTimeout(function () {
//                        dfd.reject(new Error("TEST ERROR"));
//                    }, 1000);
//                });
//                break;
            default:
                data.result = [];
        }
    }

    ,loadError: function(e, data) {
        var error = data.error;
        if (error.status && error.statusText) {
            data.details = "Error status: " + error.statusText + "[" + error.status + "]";
        } else {
            data.details = "Error: " + error;
        }
        //data.message = "Custom error: " + data.message;
    }

    ,_getMenu: function(node) {
        var key = node.key;
        var menu = [
            {title: "Menu:" + key, cmd: "cut", uiIcon: "ui-icon-scissors"},
            {title: "Copy", cmd: "copy", uiIcon: "ui-icon-copy"},
            {title: "Paste", cmd: "paste", uiIcon: "ui-icon-clipboard", disabled: false },
            {title: "----"},
            {title: "Edit", cmd: "edit", uiIcon: "ui-icon-pencil", disabled: true },
            {title: "Delete", cmd: "delete", uiIcon: "ui-icon-trash", disabled: true },
            {title: "More", children: [
                {title: "Sub 1", cmd: "sub1"},
                {title: "Sub 2", cmd: "sub1"}
            ]}
        ];
        return menu;
    }
    //----------------- end of tree -----------------


    ,refreshJTablePerson: function() {
        AcmEx.Object.jTableLoad(this.$divPerson);
    }
    ,refreshJTableRois: function() {
        AcmEx.Object.jTableLoad(this.$divRois);
    }
    ,refreshJTableEvents: function() {
        AcmEx.Object.jTableLoad(this.$divEvents);
    }

};


