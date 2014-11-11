/**
 * CaseFile.View
 *
 * @author jwu
 */
CaseFile.View = {
    create : function() {
        if (CaseFile.View.Tree.create)    {CaseFile.View.Tree.create();}
        if (CaseFile.View.Action.create)  {CaseFile.View.Action.create();}
        if (CaseFile.View.Detail.create)  {CaseFile.View.Detail.create();}
        if (CaseFile.View.Tasks.create)   {CaseFile.View.Tasks.create();}
    }
    ,initialize: function() {
        if (CaseFile.View.Tree.initialize)    {CaseFile.View.Tree.initialize();}
        if (CaseFile.View.Action.initialize)  {CaseFile.View.Action.initialize();}
        if (CaseFile.View.Detail.initialize)  {CaseFile.View.Detail.initialize();}
        if (CaseFile.View.Tasks.initialize)   {CaseFile.View.Tasks.initialize();}
    }

    ,Tree: {
        create: function() {
            this.$tree = $("#tree");
            this._useFancyTree(this.$tree);

            Acm.Dispatcher.addEventListener(CaseFile.Controller.ME_CASE_FILE_LIST_RETRIEVED, this.onCaseFileListRetrieved);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VE_CASE_TITLE_CHANGED,       this.onCaseTitleChanged);
        }
        ,initialize: function() {
        }

        ,onCaseFileListRetrieved: function(key) {
            if (key.hasError) {
                alert(key.errorMsg);
            } else {
                CaseFile.View.Tree.refreshTree(key);
            }
        }
        ,onCaseTitleChanged: function(caseFileId, title) {
            CaseFile.View.Tree.updateTitle(caseFileId, title);
        }

        ,onTreeNodeActivated: function(node) {
            if ("prevPage" == node.key) {
                CaseFile.Controller.viewClickedPrevPage();
            } else if ("nextPage" == node.key) {
                CaseFile.Controller.viewClickedNextPage();
            } else {
                var caseFileId = CaseFile.Model.Tree.Key.getCaseFileIdByKey(node.key);
                CaseFile.Controller.viewSelectedCaseFile(caseFileId);
            }

            CaseFile.Controller.viewSelectedTreeNode(node.key);
        }

        ,refreshTree: function(key) {
            this.tree.reload().done(function(){
                if (Acm.isNotEmpty(key)) {
                    CaseFile.View.Tree.tree.activateKey(key);
                }
            });
        }
        ,activeTreeNode: function(key) {
            this.tree.activateKey(key);
        }
        ,expandAllTreeNode: function(key) {
            this.tree.activateKey(key);
        }

        ,_activeKey: null
        ,getActiveKey: function() {
            return this._activeKey;
        }
        ,getActiveCaseId: function() {
            var caseFileId = CaseFile.Model.Tree.Key.getCaseFileIdByKey(this._activeKey);
            return caseFileId;
        }

        ,_useFancyTree: function($s) {
            $s.fancytree({
                activate: function(event, data) {
                    var node = data.node;
                    var key = node.key;
                    var nodeType = CaseFile.Model.Tree.Key.getNodeTypeByKey(key);

                    CaseFile.View.Tree._activeKey = key;
                    CaseFile.View.Tree.onTreeNodeActivated(data.node);
                }
                ,beforeActivate: function(event, data) {
                    if (App.Object.Dirty.isDirty()) {
                        var node = data.node;
                        var key = node.key;
                        if (key == CaseFile.View.Tree._activeKey) {
                            return true;
                        } else {
                            var reason = App.Object.Dirty.getFirst();
                            Acm.Dialog.alert("Need to save data first: " + reason);
                            return false;
                        }
                    }
                    return true;
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
                    var node = data.node;
                    CaseFile.View.Tree._fixNodeIcon(node);
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

                ,lazyLoad: function(event, data) {
                    CaseFile.View.Tree.lazyLoad(event, data);
                }
                ,loadError: function(event, data) {
                    CaseFile.View.Tree.loadError(event, data);
                }
                ,source: function() {
                    return CaseFile.View.Tree.treeSource();
                } //end source
            }); //end fancytree

            this.tree = this.$tree.fancytree("getTree");

            $s.contextmenu({
                //delegate: "span.fancytree-title",
                delegate: ".fancytree-title",
                menu: CaseFile.View.Tree.menu_cur,
                beforeOpen: function(event, ui) {
                    var node = $.ui.fancytree.getNode(ui.target);
//                node.setFocus();
                    node.setActive();
                    CaseFile.View.Tree.$tree.contextmenu("replaceMenu", CaseFile.View.Tree._getMenu(node));

                },
                select: function(event, ui) {
                    var node = $.ui.fancytree.getNode(ui.target);
                    alert("select " + ui.cmd + " on " + node);
                }
            });

        }
        ,_getCaseNodeDisplay: function(caseTitle, caseName) {
            return  caseTitle + " (" + caseName + ")";
        }
        ,_fixNodeIcon: function(node) {
            var key = node.key;
            var nodeType = CaseFile.Model.Tree.Key.getNodeTypeByKey(key);
            var acmIcon = CaseFile.Model.Tree.Key.getIconByKey(key);
            if (acmIcon) {
                var span = node.span;
                var $spanIcon = $(span.children[1]);
                $spanIcon.removeClass("fancytree-icon");
                $spanIcon.html("<i class='i " + acmIcon + "'></i>");
            }
        }
        ,updateTitle: function(caseFileId, caseTitle) {
            //var node = this.$tree.fancytree("getActiveNode");
            var key = CaseFile.Model.Tree.Key.getCaseFileKey(caseFileId);
            var node = this.tree.getNodeByKey(key);
            var caseFile = CaseFile.Model.getCaseFile(caseFileId);
            if (node && caseFile) {
                var nodeDisplay = this._getCaseNodeDisplay(caseTitle, Acm.goodValue(caseFile.caseNumber));
                node.setTitle(nodeDisplay);
                this._fixNodeIcon(node);
            }
        }
        ,treeSource: function() {
            var builder = AcmEx.FancyTreeBuilder.reset();

            var treeInfo = CaseFile.Model.Tree.Config.getTreeInfo();
            var caseFiles = CaseFile.Model.cachePage.get(treeInfo.start);
            if (null == caseFiles || 0 >= caseFiles.length) {
                return builder.getTree();
            }

            if (0 < treeInfo.start) {
                builder.addLeaf({key: CaseFile.Model.Tree.Key.NODE_TYPE_PART_PREV_PAGE
                    ,title: treeInfo.start + " records above..."
                    ,tooltip: "Review previous records"
                    ,expanded: false
                    ,folder: false
                });
            }

            for (var i = 0; i < caseFiles.length; i++) {
                var c = caseFiles[i];
                var caseId = parseInt(c.object_id_s);
                builder.addLeaf({key: treeInfo.start + "." + caseId                       //level 1: /CaseFile
                    ,title: this._getCaseNodeDisplay(c.title_t, c.name)
                    ,tooltip: c.title_t
                    ,expanded: false
                    ,folder: true
                    ,lazy: true
                    ,cache: false
                });
            } //end for i
            builder.makeLast();

            if ((0 > treeInfo.total)                                    //unknown size
                || (treeInfo.total - treeInfo.n > treeInfo.start)) {   //no more page left
                var title = (0 > treeInfo.total)? "More records..."
                    : (treeInfo.total - treeInfo.start - treeInfo.n) + " more records...";
                builder.addLeafLast({key: CaseFile.Model.Tree.Key.NODE_TYPE_PART_PREV_PAGE
                    ,title: title
                    ,tooltip: "Load more records"
                    ,expanded: false
                    ,folder: false
                });
            }

            return builder.getTree();
        }
        ,lazyLoad: function(event, data) {
            var treeInfo = CaseFile.Model.Tree.Config.getTreeInfo();
            var pageId = treeInfo.start;

            var key = data.node.key;
            var nodeType = CaseFile.Model.Tree.Key.getNodeTypeByKey(key);
            switch (nodeType) {
                case CaseFile.Model.Tree.Key.NODE_TYPE_PART_PAGE + CaseFile.Model.Tree.Key.NODE_TYPE_PART_OBJECT: //"pc":
                    data.result = AcmEx.FancyTreeBuilder
                        .reset()
                        .addLeaf({key: key + "." + CaseFile.Model.Tree.Key.NODE_TYPE_PART_DETAILS         //level 2: /CaseFile/Details
                            ,title: "Details"
                        })
                        .addLeaf({key: key + "." + CaseFile.Model.Tree.Key.NODE_TYPE_PART_PEOPLE          //level 2: /CaseFile/People
                            ,title: "People"
                        })
                        .addLeaf({key: key + "." + CaseFile.Model.Tree.Key.NODE_TYPE_PART_DOCUMENTS       //level 2: /CaseFile/Documents
                            ,title: "Documents"
//                            ,folder: true
//                            ,lazy: true
//                            ,cache: false
                        })
                        .addLeaf({key: key + "." + CaseFile.Model.Tree.Key.NODE_TYPE_PART_PARTICIPANTS    //level 2: /CaseFile/Participants
                            ,title: "Participants"
                        })
                        .addLeaf({key: key + "." + CaseFile.Model.Tree.Key.NODE_TYPE_PART_NOTES           //level 2: /CaseFile/Notes
                            ,title: "Notes"
                        })
                        .addLeaf({key: key + "." + CaseFile.Model.Tree.Key.NODE_TYPE_PART_TASKS           //level 2: /CaseFile/Tasks
                            ,title: "Tasks"
                        })
                        .addLeaf({key: key + "." + CaseFile.Model.Tree.Key.NODE_TYPE_PART_TASKS           //level 2: /CaseFile/References
                            ,title: "References"
                        })
                        .addLeaf({key: key + "." + CaseFile.Model.Tree.Key.NODE_TYPE_PART_HISTORY         //level 2: /CaseFile/History
                            ,title: "History"
                        })
                        .getTree();

                    break;

                case CaseFile.Model.Tree.Key.NODE_TYPE_PART_PAGE + CaseFile.Model.Tree.Key.NODE_TYPE_PART_OBJECT + CaseFile.Model.Tree.Key.NODE_TYPE_PART_DOCUMENTS: //"pco":
                    var caseFileId = CaseFile.Model.Tree.Key.getCaseFileIdByKey(key);
                    var c = CaseFile.Model.getCaseFile(caseFileId);
                    if (c) {
                        data.result = [{key: key + "." + "1", title: "Document1" + "[Status]"}
                            ,{key: key + "." + "2", title: "Doc2" + "[Status]"}
                        ];
                    } else {
                        data.result = CaseFile.Service.Detail.retrieveCaseFileDeferred(caseFileId
                            ,function(response) {
                                var z = 1;

                                var resultFake = [{key: key + "." + "3", title: "Document3" + "[Status]"}
                                    ,{key: key + "." + "4", title: "Doc4" + "[Status]"}
                                ];
                                return resultFake;
                            }
                        );

                    }

                    break;

                default:
                    data.result = [];
                    break;
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

        ,menu_cur: []  //initial default menu; todo: combine with _getMenu(null)
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
    }


    ,Action: {
        create: function() {
            this.$dlgCloseCase          = $("#closeCase");
            this.$dlgConsolidateCase    = $("#consolidateCase");
            this.$edtConsolidateCase    = $("#edtConsolidateCase");
            this.$btnCloseCase          = $("#tabTitle button[data-title='Close Case']");
            this.$btnConsolidateCase    = $("#tabTitle button[data-title='Consolidate Case']");
            this.$btnCloseCase          .on("click", function(e) {CaseFile.View.Action.onClickBtnCloseCase      (e, this);});
            this.$btnConsolidateCase    .on("click", function(e) {CaseFile.View.Action.onClickBtnConsolidateCase(e, this);});
        }
        ,initialize: function() {
        }

        ,onClickBtnCloseCase: function() {
            CaseFile.View.Action.showDlgCloseCase(function(event, ctrl){
                alert("close case");
            });
        }
        ,onClickBtnConsolidateCase: function() {
            CaseFile.View.Action.setValueEdtConsolidateCase("");
            CaseFile.View.Action.showDlgConsolidateCase(function(event, ctrl) {
                var caseNumber = CaseFile.View.Action.getValueEdtConsolidateCase();
                alert("Consolidate case:" + caseNumber);
                var z = 1;
            });
        }
        ,showDlgCloseCase: function(onClickBtnPrimary) {
            Acm.Dialog.bootstrapModal(this.$dlgCloseCase, onClickBtnPrimary);
        }
        ,showDlgConsolidateCase: function(onClickBtnPrimary) {
            Acm.Dialog.bootstrapModal(this.$dlgConsolidateCase, onClickBtnPrimary);
        }
        ,getValueEdtConsolidateCase: function() {
            return Acm.Object.getValue(this.$edtConsolidateCase);
        }
        ,setValueEdtConsolidateCase: function(val) {
            Acm.Object.setValue(this.$edtConsolidateCase, val);
        }
    }


    ,Detail: {
        create: function() {
            this.$tabTop          = $("#tabTop");
            this.$tabTopBlank     = $("#tabTopBlank");

            this.$divDetail       = $(".divDetail");
            this.$btnEditDetail   = $("#tabDetail button:eq(0)");
            this.$btnSaveDetail   = $("#tabDetail button:eq(1)");
            this.$btnEditDetail.on("click", function(e) {CaseFile.View.Detail.onClickBtnEditDetail(e, this);});
            this.$btnSaveDetail.on("click", function(e) {CaseFile.View.Detail.onClickBtnSaveDetail(e, this);});

            this.$labCaseNumber   = $("#caseNumber");
            this.$lnkCaseTitle    = $("#caseTitle");
            this.$lnkIncidentDate = $("#incident");
            this.$lnkPriority     = $("#priority");
            this.$lnkAssignee     = $("#assigned");
            this.$lnkSubjectType  = $("#type");
            this.$lnkDueDate      = $("#dueDate");
            this.$lnkStatus       = $("#status");

            AcmEx.Object.XEditable.useEditable(this.$lnkCaseTitle, {
                success: function(response, newValue) {
                    CaseFile.Controller.viewChangedCaseTitle(CaseFile.Model.getCaseFileId(), newValue);
                }
            });
            AcmEx.Object.XEditable.useEditableDate(this.$lnkIncidentDate, {
                success: function(response, newValue) {
                    CaseFile.Controller.viewChangedIncidentDate(CaseFile.Model.getCaseFileId(), newValue);
                }
            });
            AcmEx.Object.XEditable.useEditableDate(this.$lnkDueDate, {
                success: function(response, newValue) {
                    CaseFile.Controller.viewChangedDueDate(CaseFile.Model.getCaseFileId(), newValue);
                }
            });


            Acm.Dispatcher.addEventListener(CaseFile.Controller.ME_ASSIGNEES_FOUND        ,this.onAssigneesFound);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.ME_SUBJECT_TYPES_FOUND    ,this.onSubjectTypesFound);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.ME_PRIORITIES_FOUND       ,this.onPrioritiesFound);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.ME_CASE_FILE_RETRIEVED    ,this.onCaseFileRetrieved);
            //Acm.Dispatcher.addEventListener(CaseFile.Controller.ME_CASE_FILE_SAVED        ,this.onCaseFileSaved);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.ME_CASE_TITLE_SAVED       ,this.onCaseTitleSaved);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.ME_INCIDENT_DATE_SAVED    ,this.onIncidentDateSaved);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.ME_ASSIGNEE_SAVED         ,this.onAssigneeSaved);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.ME_SUBJECT_TYPE_SAVED     ,this.onSubjectTypeSaved);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.ME_PRIORITY_SAVED         ,this.onPrioritySaved);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.ME_DUE_DATE_SAVED         ,this.onDueDateSaved);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.ME_DETAIL_SAVED           ,this.onDetailSaved);

            Acm.Dispatcher.addEventListener(CaseFile.Controller.VE_TREE_NODE_SELECTED     ,this.onTreeNodeSelected);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VE_CASE_FILE_SELECTED     ,this.onCaseFileSelected);
        }
        ,initialize: function() {
        }


        ,onAssigneesFound: function(assignees) {
            var choices = [];
            $.each(assignees, function(idx, val) {
                var opt = {};
                opt.value = val.userId;
                opt.text = val.fullName;
                choices.push(opt);
            });

            AcmEx.Object.XEditable.useEditable(CaseFile.View.Detail.$lnkAssignee, {
                source: choices
                ,success: function(response, newValue) {
                    CaseFile.Controller.viewChangedAssignee(CaseFile.Model.getCaseFileId(), newValue);
                }
            });
        }
        ,onSubjectTypesFound: function(subjectTypes) {
            var choices = [];
            $.each(subjectTypes, function(idx, val) {
                var opt = {};
                opt.value = val;
                opt.text = val;
                choices.push(opt);
            });

            AcmEx.Object.XEditable.useEditable(CaseFile.View.Detail.$lnkSubjectType, {
                source: choices
                ,success: function(response, newValue) {
                    CaseFile.Controller.viewChangedSubjectType(CaseFile.Model.getCaseFileId(), newValue);
                }
            });
        }
        ,onPrioritiesFound: function(priorities) {
            var choices = []; //[{value: "", text: "Choose Priority"}];
            $.each(priorities, function(idx, val) {
                var opt = {};
                opt.value = val;
                opt.text = val;
                choices.push(opt);
            });

            AcmEx.Object.XEditable.useEditable(CaseFile.View.Detail.$lnkPriority, {
                source: choices
                ,success: function(response, newValue) {
                    CaseFile.Controller.viewChangedPriority(CaseFile.Model.getCaseFileId(), newValue);
                }
            });
        }
        ,onCaseFileRetrieved: function(caseFile) {
            if (caseFile.hasError) {
                alert("View: onCaseFileRetrieved, hasError");
            } else {
                CaseFile.View.Detail.populateCaseFile(caseFile);
            }
        }
//        ,onCaseFileSaved: function(caseFile) {
//            //todo: pop ASN message
//            if (caseFile.hasError) {
//                alert("View: onCaseFileSaved, hasError");
//            } else {
//                alert("View: onCaseFileSaved");
//            }
//
//        }
        ,onCaseTitleSaved: function(caseFileId, title) {
            if (title.hasError) {
                //alert("View: onCaseTitleSaved, hasError, errorMsg:" + title.errorMsg);
                CaseFile.View.Detail.setTextLnkCaseTitle("(Error)");
            }
        }
        ,onIncidentDateSaved: function(caseFileId, incidentDate) {
            if (incidentDate.hasError) {
                CaseFile.View.Detail.setTextLnkIncidentDate("(Error)");
            }
        }
        ,onAssigneeSaved: function(caseFileId, assginee) {
            if (assginee.hasError) {
                CaseFile.View.Detail.setTextLnkAssignee("(Error)");
            }
        }
        ,onSubjectTypeSaved: function(caseFileId, subjectType) {
            if (subjectType.hasError) {
                CaseFile.View.Detail.setTextLnkSubjectType("(Error)");
            }
        }
        ,onPrioritySaved: function(caseFileId, priority) {
            if (priority.hasError) {
                CaseFile.View.Detail.setTextLnkPriority("(Error)");
            }
        }
        ,onDueDateSaved: function(caseFileId, created) {
            if (created.hasError) {
                CaseFile.View.Detail.setTextLnkDueDate("(Error)");
            }
        }
        ,onDetailSaved: function(caseFileId, details) {
            if (details.hasError) {
                CaseFile.View.Detail.setHtmlDivDetail("(Error)");
            }
        }



        ,onTreeNodeSelected: function(key) {
            CaseFile.View.Detail.showPanel(key);
        }
        ,onCaseFileSelected: function(caseFileId) {
            CaseFile.View.Detail.showTopPanel(0 < caseFileId);

            var caseFile = CaseFile.Model.cacheCaseFile.get(caseFileId);
            if (caseFile) {
                CaseFile.View.Detail.populateCaseFile(caseFile);
            }
        }

        ,onClickBtnEditDetail: function(event, ctrl) {
            App.Object.Dirty.declare("Editing case detail");
            CaseFile.View.Detail.editDivDetail();
        }
        ,onClickBtnSaveDetail: function(event, ctrl) {
            var htmlDetail = CaseFile.View.Detail.saveDivDetail();
            CaseFile.Controller.viewChangedDetail(CaseFile.Model.getCaseFileId(), htmlDetail);
            App.Object.Dirty.clear("Editing case detail");
        }


        ,showTopPanel: function(show) {
            Acm.Object.show(this.$tabTop, show);
            Acm.Object.show(this.$tabTopBlank, !show);
        }
        ,showPanel: function(key) {
            var tabIds = CaseFile.Model.Tree.Key.getTabIds();
            var tabIdsToShow = CaseFile.Model.Tree.Key.getTabIdsByKey(key);
            for (var i = 0; i < tabIds.length; i++) {
                var show = Acm.isItemInArray(tabIds[i], tabIdsToShow);
                Acm.Object.show($("#" + tabIds[i]), show);
            }
        }
        ,populateCaseFile: function(c) {
            if (c) {
                this.setTextLabCaseNumber(Acm.goodValue(c.caseNumber));
                this.setTextLnkCaseTitle(Acm.goodValue(c.title));
                this.setTextLnkIncidentDate(Acm.getDateFromDatetime(c.incidentDate));
                this.setTextLnkSubjectType(Acm.goodValue(c.caseType));
                this.setTextLnkPriority(Acm.goodValue(c.priority));
                this.setTextLnkDueDate(Acm.getDateFromDatetime(c.dueDate));
                this.setTextLnkStatus(Acm.goodValue(c.status));
                this.setHtmlDivDetail(Acm.goodValue(c.details));

                var assignee = CaseFile.Model.getAssignee(c);
                this.setTextLnkAssignee(Acm.goodValue(assignee));
            }
        }

        ,setTextLabCaseNumber: function(txt) {
            Acm.Object.setText(this.$labCaseNumber, txt);
        }
        ,setTextLnkCaseTitle: function(txt) {
            AcmEx.Object.XEditable.setValue(this.$lnkCaseTitle, txt);
            //this.$lnkCaseTitle.editable("setValue",txt);
        }
        ,setTextLnkIncidentDate: function(txt) {
            AcmEx.Object.XEditable.setDate(this.$lnkIncidentDate, txt);
        }
        ,setTextLnkAssignee: function(txt) {
            AcmEx.Object.XEditable.setValue(this.$lnkAssignee, txt);
        }
        ,setTextLnkSubjectType: function(txt) {
            AcmEx.Object.XEditable.setValue(this.$lnkSubjectType, txt);
        }
        ,setTextLnkPriority: function(txt) {
            AcmEx.Object.XEditable.setValue(this.$lnkPriority, txt);
        }
        ,setTextLnkDueDate: function(txt) {
            AcmEx.Object.XEditable.setDate(this.$lnkDueDate, txt);
        }
        ,setTextLnkStatus: function(txt) {
            Acm.Object.setText(this.$lnkStatus, txt);
        }
        ,getHtmlDivDetail: function() {
            return AcmEx.Object.getSummernote(this.$divDetail);
        }
        ,setHtmlDivDetail: function(html) {
            AcmEx.Object.SummerNote.set(this.$divDetail, html);
        }
        ,editDivDetail: function() {
            AcmEx.Object.SummerNote.edit(this.$divDetail);
        }
        ,saveDivDetail: function() {
            return AcmEx.Object.SummerNote.save(this.$divDetail);
        }

        ,populateCaseFile_old: function(c) {
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
    }

    ,Tasks: {
        create: function() {
            this.$divTasks          = $("#divTasks");
            this.createJTableTasks(this.$divTasks);
            this.$spanAddTask       = this.$divTasks.find(".jtable-toolbar-item-add-record");
            this.$spanAddTask.unbind("click").on("click", function(e){CaseFile.View.Tasks.onClickSpanAddTask(e, this);});

            Acm.Dispatcher.addEventListener(CaseFile.Controller.VE_CASE_FILE_SELECTED     ,this.onCaseFileSelected);
        }
        ,initialize: function() {
        }

        ,URL_TASK_DETAIL:  "/plugin/task/"
        ,URL_NEW_TASK_:    "/plugin/task/wizard?parentType=CASE_FILE&reference="

        ,onCaseFileSelected: function(caseFileId) {
            AcmEx.Object.jTableLoad(CaseFile.View.Tasks.$divTasks);
        }
        ,onClickSpanAddTask: function(event, ctrl) {
            alert("onClickSpanAddTask");
            return;
            var caseFileId = CaseFile.Model.getCaseFileId();
            var caseFile = CaseFile.Model.getCaseFile(caseFileId);
            if (caseFile) {
                var caseNumber = Acm.goodValue(caseFile.caseNumber);
                var url = CaseFile.View.Tasks.URL_NEW_TASK  + caseNumber;
                App.gotoPage(url);
            }
        }
        ,onClickBtnTaskAssign: function(event, ctrl) {
            alert("onClickBtnTaskAssign");
        }
        ,onClickBtnTaskUnassign: function(event, ctrl) {
            alert("onClickBtnTaskUnassign");
        }

        ,_makeJtData: function(taskList) {
            var jtData = AcmEx.Object.JTable.getEmptyRecords();
            if (taskList) {
                for (var i = 0; i < taskList.length; i++) {
                    var Record = {};
                    Record.id       = taskList[i].id;
                    Record.title    = taskList[i].title;
                    Record.created  = taskList[i].created;
                    Record.priority = taskList[i].priority;
                    Record.dueDate  = taskList[i].dueDate;
                    Record.status   = taskList[i].status;
                    Record.assignee = taskList[i].assignee;
                    jtData.Records.push(Record);
                }
                jtData.TotalRecordCount = taskList.length;
            }
            return jtData;
        }
        ,createJTableTasks: function($jt) {
            var sortMap = {};
            sortMap["title"] = "title_t";

            AcmEx.Object.JTable.usePaging($jt
                ,{
                    title: 'Tasks'
                    ,selecting: true
                    ,multiselect: false
                    ,selectingCheckboxes: false
                    ,messages: {
                        addNewRecord: 'Add Task'
                    }
                    ,actions: {
                        pagingListAction: function (postData, jtParams, sortMap) {
                            var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                            if (0 >= caseFileId) {
                                return AcmEx.Object.JTable.getEmptyRecords();
                            }

                            var taskList = CaseFile.Model.Tasks.cacheTaskList.get(caseFileId);
                            if (taskList) {
                                return CaseFile.View.Tasks._makeJtData(taskList);

                            } else {
                                return CaseFile.Service.Tasks.retrieveTaskListDeferred(caseFileId
                                    ,postData
                                    ,jtParams
                                    ,sortMap
                                    ,function(data) {
                                        var taskList = data;
                                        return CaseFile.View.Tasks._makeJtData(taskList);
                                    }
                                    ,function(error) {
                                    }
                                );
                            }  //end else
                        }

                        ,createAction: function(postData, jtParams) {
                            return AcmEx.Object.JTable.getEmptyRecord();
                        }
                    }

                    ,fields: {
                        id: {
                            title: 'ID'
                            ,key: true
                            ,list: true
                            ,create: false
                            ,edit: false
                            ,sorting: false
                        }
                        ,title: {
                            title: 'Title'
                            ,width: '30%'
                            ,sorting: false
                        }
                        ,created: {
                            title: 'Created'
                            ,width: '15%'
                            ,sorting: false
                        }
                        ,priority: {
                            title: 'Priority'
                            ,width: '10%'
                            ,sorting: false
                        }
                        ,dueDate: {
                            title: 'Due'
                            ,width: '15%'
                            ,sorting: true
                        }
                        ,status: {
                            title: 'Status'
                            ,width: '10%'
                            ,sorting: false
                        }
                        ,description: {
                            title: 'Action'
                            ,width: '10%'
                            ,sorting: false
                            ,edit: false
                            ,create: false
                            ,display: function (commData) {
                                var $a = $("<a href='#' class='inline animated btn btn-default btn-xs' data-toggle='class:show'><i class='fa fa-phone'></i></a>");
                                var $b = $("<a href='#' class='inline animated btn btn-default btn-xs' data-toggle='class:show'><i class='fa fa-book'></i></a>");

                                $a.click(function (e) {
                                    CaseFile.View.Tasks.onClickBtnTaskAssign(e, this);
                                    e.preventDefault();
                                });
                                $b.click(function (e) {
                                    CaseFile.View.Tasks.onClickBtnTaskUnassign(e, this);
                                    e.preventDefault();
                                });
                                return $a.add($b);
                            }
                        }
                    } //end field
                } //end arg
                ,sortMap
            );
        }
    }
};

