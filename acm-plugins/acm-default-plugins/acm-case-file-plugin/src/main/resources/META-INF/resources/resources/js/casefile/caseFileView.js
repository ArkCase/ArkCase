/**
 * CaseFile.View
 *
 * @author jwu
 */
CaseFile.View = {
    create : function() {
        if (CaseFile.View.MicroData.create)       {CaseFile.View.MicroData.create();}
        if (CaseFile.View.Tree.create)            {CaseFile.View.Tree.create();}
        if (CaseFile.View.Action.create)          {CaseFile.View.Action.create();}
        if (CaseFile.View.Detail.create)          {CaseFile.View.Detail.create();}
        if (CaseFile.View.People.create)    	  {CaseFile.View.People.create();}
        if (CaseFile.View.Documents.create)       {CaseFile.View.Documents.create();}
        if (CaseFile.View.Participants.create)    {CaseFile.View.Participants.create();}
        if (CaseFile.View.Tasks.create)           {CaseFile.View.Tasks.create();}
        if (CaseFile.View.Notes.create)           {CaseFile.View.Notes.create();}
    }
    ,initialize: function() {
        if (CaseFile.View.MicroData.initialize)   {CaseFile.View.MicroData.initialize();}
        if (CaseFile.View.Tree.initialize)        {CaseFile.View.Tree.initialize();}
        if (CaseFile.View.Action.initialize)      {CaseFile.View.Action.initialize();}
        if (CaseFile.View.Detail.initialize)      {CaseFile.View.Detail.initialize();}
        if (CaseFile.View.People.initialize)	  {CaseFile.View.People.initialize();}
        if (CaseFile.View.Documents.initialize)   {CaseFile.View.Documents.initialize();}
        if (CaseFile.View.Participants.initialize){CaseFile.View.Participants.initialize();}
        if (CaseFile.View.Tasks.initialize)       {CaseFile.View.Tasks.initialize();}
        if (CaseFile.View.Notes.initialize)       {CaseFile.View.Notes.initialize();}
    }

    ,MicroData: {
        create : function() {
            var items = $(document).items();
            this.caseFileId = items.properties("caseFileId").itemValue();
            this.token = items.properties("token").itemValue();
            
            
            this.formUrls = new Object();
            
            this.formUrls["roi"] = items.properties("urlRoiForm").itemValue();
            this.formUrls["close_case"] = items.properties("urlCloseCaseForm").itemValue();
            this.formUrls["edit_close_case"] = items.properties("urlEditCloseCaseForm").itemValue();
        }
        ,initialize: function() {
        }

        ,getCaseFileId: function() {
            return this.caseFileId;
        }
        ,getToken: function() {
            return this.token;
        }
        ,getFormUrls: function(){
        	return this.formUrls;
        }
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
                var urlCloseCaseForm = CaseFile.View.MicroData.getFormUrls()['close_case'];
                var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                var c = CaseFile.Model.getCaseFile(caseFileId);
                if (Acm.isNotEmpty(urlCloseCaseForm) && Acm.isNotEmpty(c)) {
                    if (Acm.isNotEmpty(c.caseNumber)) {
                        urlCloseCaseForm = urlCloseCaseForm.replace("_data=(", "_data=(caseId:'" + caseFileId + "',caseNumber:'" + c.caseNumber + "',");

                        //CaseFile.View.Action._showPopup(urlCloseCaseForm, "", 860, 700);
                        Acm.Dialog.openWindow(urlCloseCaseForm, "", 860, 700
                            ,function() {
                                CaseFile.Controller.viewClosedCaseFile(caseFileId);
                            }
                        );
                    }
                }
            });
        }


        ,onClickBtnConsolidateCase: function() {
            CaseFile.View.Action.setValueEdtConsolidateCase("");
            CaseFile.View.Action.showDlgConsolidateCase(function(event, ctrl) {
                var caseNumber = CaseFile.View.Action.getValueEdtConsolidateCase();
                alert("Consolidate case:" + caseNumber);
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

//        ,_showPopup: function(url, title, w, h) {
//
//            var dualScreenLeft = window.screenLeft != undefined ? window.screenLeft : screen.left;
//            var dualScreenTop = window.screenTop != undefined ? window.screenTop : screen.top;
//
//            width = window.innerWidth ? window.innerWidth : document.documentElement.clientWidth ? document.documentElement.clientWidth : screen.width;
//            height = window.innerHeight ? window.innerHeight : document.documentElement.clientHeight ? document.documentElement.clientHeight : screen.height;
//
//            var left = ((width / 2) - (w / 2)) + dualScreenLeft;
//            var top = ((height / 2) - (h / 2)) + dualScreenTop;
//            var newWindow = window.open(url, title, 'scrollbars=yes, resizable=1, width=' + w + ', height=' + h + ', top=' + top + ', left=' + left);
//
//
//            if (window.focus) {
//                newWindow.focus();
//            }
//
//            this._checkClosePopup(newWindow);
//        }
//
//        ,_checkClosePopup: function(newWindow){
//            var timer = setInterval(function() {
//                if(newWindow.closed) {
//                    clearInterval(timer);
//                    CaseFile.Controller.viewClosedCaseFile();
//                }
//            }, 1000);
//        }
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
            return AcmEx.Object.SummerNote.get(this.$divDetail);
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
    
    ,People: {
        create: function() {
            this.$divPeople    = $("#divPeople");
            this.createJTablePeople(this.$divPeople);
            
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VE_CASE_FILE_SELECTED     ,this.onCaseFileSelected);
        }
        ,initialize: function() {
        }

        ,onCaseFileRetrieved: function(caseFile) {
            if (caseFile.hasError) {
                //empty table?
            } else {
                AcmEx.Object.JTable.load(CaseFile.View.People.$divPeople);
            }
        }
        ,onCaseFileSelected: function(caseFileId) {
            AcmEx.Object.JTable.load(CaseFile.View.People.$divPeople);
        }

        ,createJTablePeople: function($s) {
            this._createJTable4SubTablePeople($s, {
            	title: 'People'
                ,paging: false
                ,messages: {
                    addNewRecord: 'Add Person'
                }
                ,actions: {
                    listAction: function(postData, jtParams) {
                    	var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                        if (0 >= caseFileId) {
                            return AcmEx.Object.JTable.getEmptyRecords();
                        }

                        var rc = AcmEx.Object.JTable.getEmptyRecords();
                        var c = CaseFile.Model.getCaseFile(caseFileId);
                        /*if(c && c.originator){
                            if(c.personAssociations){
                                var personAssociations = c.personAssociations;
                                var cnt = personAssociations.length;
                                for (var i = 0; i < cnt; i++) {
                                    if(personAssociations[i].id != c.originator.id){
                                        var person = personAssociations[i].person;
                                        rc.Records.push({
                                            personId: person.id
                                            ,title: person.title
                                            ,givenName: person.givenName
                                            ,familyName: person.familyName
                                            ,personType: personAssociations[i].personType
                                        });
                                    }
                                }
                            }
                        }*/
                        //return rc;
                        return {
	                          "Result": "OK"
	                          ,"Records": [
	                              {"id": 11, "title": "Mr", "givenName": "Some Name 1", "familyName": "Some Second Name 1", "personType": "Initiator"}
	                              ,{"id": 12, "title": "Mrs", "givenName": "Some Name 2", "familyName": "Some Second Name 2", "personType": "Complaintant"}
	                          ]
	                          ,"TotalRecordCount": 2
	                      };
                    }
                    ,createAction: function(postData, jtParams) {
                        var record = Acm.urlToJson(postData);
                        var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                        if (0 >= caseFileId) {
                            return AcmEx.Object.JTable.getEmptyRecords();
                        }

                        var rc = AcmEx.Object.JTable.getEmptyRecords();
                        var c = CaseFile.Model.getCaseFile(caseFileId);
                        if (c) {
                            if (c.personAssociations){
                                rc.Record.title = record.title;
                                rc.Record.givenName = record.givenName;
                                rc.Record.familyName = record.familyName;
                                rc.Record.personType = record.personType;
                            }
                        }
                        return rc;
                    }
                    ,updateAction: function(postData, jtParams) {
                        var record = Acm.urlToJson(postData);
                        var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                        if (0 >= caseFileId) {
                            return AcmEx.Object.JTable.getEmptyRecords();
                        }

                        var rc = AcmEx.Object.JTable.getEmptyRecords();
                        var c = CaseFile.Model.getCaseFile(caseFileId);
                        if (c) {
                            if (c.personAssociations) {
                                rc.Record.title = record.title;
                                rc.Record.givenName = record.givenName;
                                rc.Record.familyName = record.familyName;
                                rc.Record.personType = record.personType;
                            }
                        }
                        return rc;
                    }
                    ,deleteAction: function(postData, jtParams) {
                        return {
                           "Result": "OK"
                        };
                    }
                }
                ,fields: {
                    personId: {
                        title: 'ID'
                        ,key: true
                        ,list: false
                        ,create: false
                        ,edit: false
                    }
                    ,title: {
                        title: 'Title'
                        ,width: '10%'
                        ,options: CaseFile.Model.Lookup.getPersonTitles()
                    }
                    ,givenName: {
                        title: 'First Name'
                        ,width: '15%'
                    }
                    ,familyName: {
                        title: 'Last Name'
                        ,width: '15%'
                    }
                    ,personType: {
                        title: 'Type'
                        //,options: App.getContextPath() + '/api/latest/plugin/complaint/types'
                        ,options: CaseFile.Model.Lookup.getPersonTypes()
                    }
                }
                ,recordAdded: function(event, data){
                    var record = data.record;
                    var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                    if (0 >= caseFileId) {
                        return AcmEx.Object.JTable.getEmptyRecords();
                    }
                    var c = CaseFile.Model.getCaseFile(caseFileId);
                    if (c) {
                        if (c.personAssociations) {
                        	// TODO: Perform add
                            /*var newPersonAssociationRecord = Complaint.JTable._getNewPersonAssociationRecord();
                            newPersonAssociationRecord.parentType = App.OBJTYPE_COMPLAINT;
                            newPersonAssociationRecord.parentId = complaint.complaintId;
                            newPersonAssociationRecord.personType = record.personType;
                            newPersonAssociationRecord.personDescription = record.personDescription;
                            newPersonAssociationRecord.person.title = record.title;
                            newPersonAssociationRecord.person.givenName = record.givenName;
                            newPersonAssociationRecord.person.familyName = record.familyName;
                            Complaint.Service.savePersonAssociation(newPersonAssociationRecord);*/
                        }
                    }
                 }

                ,recordUpdated: function(event, data){
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var record = data.record;
                    var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                    if (0 >= caseFileId) {
                        return AcmEx.Object.JTable.getEmptyRecords();
                    }
                    var c = CaseFile.Model.getCaseFile(caseFileId);
                    if (c) {
                        if (c.personAssociations) {
                            var personAssociations = c.personAssociations;
                            if(personAssociations[whichRow].person){
                                var person = personAssociations[whichRow].person;
                                personAssociations[whichRow].personType = record.personType;
                                person.title = record.title;
                                person.givenName = record.givenName;
                                person.familyName = record.familyName;
                                CaseFile.Service.Detail.saveComplaint(c);
                            }
                        }
                    }
                }
                ,recordDeleted: function(event,data) {
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var record = data.record;
                    var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                    if (0 >= caseFileId) {
                        return AcmEx.Object.JTable.getEmptyRecords();
                    }
                    var c = CaseFile.Model.getCaseFile(caseFileId);
                    if (c) {
                        if (c.personAssociations) {
                            var personAssociations = c.personAssociations;
                            if (personAssociations[whichRow]) {
                                var personAssocId = personAssociations[whichRow].id;
                                // TODO: Perform delete
                                //CaseFile.Service.deletePersonAssociationById(personAssocId);
                            }
                        }
                    }
                }   
            });
        }
        
        ,_findPersonAssoc: function(personId,personAssociations) {
            var personAssoc;
            for (var i = 0; i < personAssociations.length; i++) {
                if (personId == personAssociations[i].person.id) {
                    personAssoc = personAssociations[i];
                    break;
                }
            }
            return personAssoc;
        }
        
        ,_createJTable4SubTablePeople: function($s, arg) {
            //return;

            var argNew = {fields:{}};
            argNew.fields.subTables = {
                title: 'Entities'
                ,width: '10%'
                ,sorting: false
                ,edit: false
                ,create: false
                ,openChildAsAccordion: true
                ,display: function (commData) {
                    var $a = $("<a href='#' class='inline animated btn btn-default btn-xs' data-toggle='class:show'><i class='fa fa-phone'></i></a>");
                    var $b = $("<a href='#' class='inline animated btn btn-default btn-xs' data-toggle='class:show'><i class='fa fa-book'></i></a>");
                    var $c = $("<a href='#' class='inline animated btn btn-default btn-xs' data-toggle='class:show'><i class='fa fa-map-marker'></i></a>");
                    var $d = $("<a href='#' class='inline animated btn btn-default btn-xs' data-toggle='class:show'><i class='fa fa-users'></i></a>");

                    $a.click(function (e) {
                    	CaseFile.View.People._togglePeopleDevices($s, $a);
                        e.preventDefault();
                    });
                    $b.click(function (e) {
                        CaseFile.View.People._togglePeopleOrganizations($s, $b);
                        e.preventDefault();
                    });
                    $c.click(function (e) {
                    	CaseFile.View.People._togglePeopleLocations($s, $c);
                        e.preventDefault();
                    });
                    $d.click(function (e) {
                    	CaseFile.View.People._togglePeopleAliases($s, $d);
                        e.preventDefault();
                    });
                    return $a.add($b).add($c).add($d);
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
            $s.jtable(argNew);
            $s.jtable('load');
        }
        
        
        
        
        // ============================= Toggle Sub-Table =====================================
        ,_togglePeopleDevices: function($t, $row) {
        	AcmEx.Object.JTable.toggleSubJTable($t, $row, this._openPeopleDevices, this._closePeopleDevices, CaseFile.Model.Lookup.PERSON_SUBTABLE_TITLE_DEVICES);
        }
        ,_togglePeopleOrganizations: function($t, $row) {
        	AcmEx.Object.JTable.toggleSubJTable($t, $row, this._openPeopleOrganizations, this._closePeopleOrganizations, CaseFile.Model.Lookup.PERSON_SUBTABLE_TITLE_ORGANIZATIONS);
        }
        ,_togglePeopleLocations: function($t, $row) {
        	AcmEx.Object.JTable.toggleSubJTable($t, $row, this._openPeopleLocations, this._closePeopleLocations, CaseFile.Model.Lookup.PERSON_SUBTABLE_TITLE_LOCATIONS);
        }
        ,_togglePeopleAliases: function($t, $row) {
        	AcmEx.Object.JTable.toggleSubJTable($t, $row, this._openPeopleAliases, this._closePeopleAliases, CaseFile.Model.Lookup.PERSON_SUBTABLE_TITLE_ALIASES);
        }   
        // ============================= Toggle Sub-Table =====================================
        
        
        
        // ============================= Open/Close People Devices Sub-Table ===================================
        ,_closePeopleDevices: function($t, $row) {
            $t.jtable('closeChildTable', $row.closest('tr'));
        }
        ,_openPeopleDevices: function($t, $row) {
            $t.jtable('openChildTable'
                ,$row.closest('tr')
                , {
                    title: CaseFile.Model.Lookup.PERSON_SUBTABLE_TITLE_DEVICES, sorting: true, messages: {
                        addNewRecord: 'Add Device'
                    }, actions: {
                        listAction: function (postData, jtParams) {
                            var rc = AcmEx.Object.jTableGetEmptyRecords();
                            var recordParent = $row.closest('tr').data('record');
                            if (recordParent && recordParent.personId) {
                                var personId = recordParent.personId;

                                //var rowId = $row.closest('tr')[0].rowIndex;
                                //var rowId = $row.closest('tr').index();
                                //var rowIndex = rowId - 1;
                                //var rowIndex = 0;
                                //var rowIndex = $row
//                                .closest('tr') // closest tr parent element
//                                .prevAll() // all sibling elements in front of it
//                                .length; // find their count
                                var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                                if (0 >= caseFileId) {
                                    return AcmEx.Object.JTable.getEmptyRecords();
                                }
                                var c = CaseFile.Model.getCaseFile(caseFileId);
                                if (c) {
                                    if (c.personAssociations) {
                                        var personAssociations = c.personAssociations;
                                        var currentPersonAssoc = this._findPersonAssoc(personId, personAssociations);
                                        if (currentPersonAssoc.person) {
                                            var person = currentPersonAssoc.person;
                                            if (person.contactMethods) {
                                                var contactMethods = person.contactMethods;
                                                var cnt = contactMethods.length;
                                                for (var i = 0; i < cnt; i++) {
                                                    rc.Records.push({
                                                        personId: person.id,
                                                        type: contactMethods[i].type,
                                                        value: Acm.goodValue(contactMethods[i].value),
                                                        created: Acm.getDateFromDatetime(contactMethods[i].created),
                                                        creator: contactMethods[i].creator
                                                    });
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            return rc;
                        }, createAction: function (postData, jtParams) {
                            var rc = AcmEx.Object.jTableGetEmptyRecord();
                            var recordParent = $row.closest('tr').data('record');
                            if (recordParent && recordParent.personId) {
                                var personId = recordParent.personId;
                                var record = Acm.urlToJson(postData);
                                var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                                if (0 >= caseFileId) {
                                    return AcmEx.Object.JTable.getEmptyRecords();
                                }
                                var c = CaseFile.Model.getCaseFile(caseFileId);
                                if (c) {
                                    if (c.personAssociations) {
                                        var personAssociations = c.personAssociations;
                                        var currentPersonAssoc = this._findPersonAssoc(personId, personAssociations);
                                        if (currentPersonAssoc.person) {
                                            var person = currentPersonAssoc.person;
                                            rc.Record.personId = person.id;
                                            rc.Record.type = record.type;
                                            rc.Record.value = Acm.goodValue(record.value);
                                            rc.Record.created = Acm.getCurrentDay(); //record.created;
                                            rc.Record.creator = App.getUserName();   //record.creator;
                                        }
                                    }
                                }
                            }
                            return rc;
                        }, updateAction: function (postData, jtParams) {
                            var rc = AcmEx.Object.jTableGetEmptyRecord();
                            var recordParent = $row.closest('tr').data('record');
                            if (recordParent && recordParent.personId) {
                                var personId = recordParent.personId;
                                var record = Acm.urlToJson(postData);
                                var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                                if (0 >= caseFileId) {
                                    return AcmEx.Object.JTable.getEmptyRecords();
                                }
                                var c = CaseFile.Model.getCaseFile(caseFileId);
                                if (c) {
                                    if (c.personAssociations) {
                                        var personAssociations = c.personAssociations;
                                        var currentPersonAssoc = this._findPersonAssoc(personId, personAssociations);
                                        if (currentPersonAssoc.person) {
                                            var person = currentPersonAssoc.person;
                                            rc.Record.personId = person.id;
                                            rc.Record.type = record.type;
                                            rc.Record.value = Acm.goodValue(record.value);
                                            rc.Record.created = Acm.getCurrentDay(); //record.created;
                                            rc.Record.creator = App.getUserName();   //record.creator;
                                        }
                                    }
                                }
                            }
                            return rc;
                        }, deleteAction: function (postData, jtParams) {
                            return {
                                "Result": "OK"
                            };
                        }
                    }, fields: {
                        personId: {
                            key: false, create: false, edit: false, list: false
                        }, id: {
                            key: false, type: 'hidden', edit: false, defaultValue: 0
                        }, type: {
                            title: 'Type', width: '15%', options: CaseFile.Model.Lookup.getDeviceTypes()
                        }, value: {
                            title: 'Value', width: '30%'
                        }, created: {
                            title: 'Date Added', width: '20%', create: false, edit: false
                            //,type: 'date'
                            //,displayFormat: 'yy-mm-dd'
                        }, creator: {
                            title: 'Added By', width: '30%', create: false, edit: false
                        }
                    }, recordAdded: function (event, data) {
                        var recordParent = $row.closest('tr').data('record');
                        if (recordParent && recordParent.personId) {
                            var personId = recordParent.personId;
                            var record = data.record;
                            var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                            if (0 >= caseFileId) {
                                return AcmEx.Object.JTable.getEmptyRecords();
                            }
                            var c = CaseFile.Model.getCaseFile(caseFileId);
                            if (c) {
                                if (c.personAssociations) {
                                    var personAssociations = c.personAssociations;
                                    var currentPersonAssoc = this._findPersonAssoc(personId, personAssociations);
                                    if (currentPersonAssoc.person) {
                                        var person = currentPersonAssoc.person;
                                        if (person.contactMethods) {
                                            var contactMethods = person.contactMethods;
                                            var contactMethod = {};
                                            contactMethod.type = record.type;
                                            contactMethod.value = Acm.goodValue(record.value);
                                            contactMethods.push(contactMethod);
                                            
                                            // TODO: Perform Save Case
                                            //Complaint.Service.saveComplaint(complaint);
                                        }
                                    }
                                }
                            }
                        }
                    }, recordUpdated: function (event, data) {
                        var recordParent = $row.closest('tr').data('record');
                        if (recordParent && recordParent.personId) {
                            var personId = recordParent.personId;
                            var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                            var record = data.record;
                            var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                            if (0 >= caseFileId) {
                                return AcmEx.Object.JTable.getEmptyRecords();
                            }
                            var c = CaseFile.Model.getCaseFile(caseFileId);
                            if (c) {
                                if (c.personAssociations) {
                                    var personAssociations = c.personAssociations;
                                    var currentPersonAssoc = this._findPersonAssoc(personId, personAssociations);
                                    if (currentPersonAssoc.person) {
                                        var person = currentPersonAssoc.person;
                                        if (person.contactMethods) {
                                            var contactMethods = person.contactMethods;
                                            var contactMethod = contactMethods[whichRow];
                                            contactMethod.type = record.type;
                                            contactMethod.value = Acm.goodValue(record.value);
                                            
                                            // TODO: Perform Save Case
                                            //Complaint.Service.saveComplaint(complaint);
                                        }
                                    }
                                }
                            }
                        }
                    }, recordDeleted: function (event, data) {
                        var recordParent = $row.closest('tr').data('record');
                        if (recordParent && recordParent.personId) {
                            var personId = recordParent.personId;
                            var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                            var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                            if (0 >= caseFileId) {
                                return AcmEx.Object.JTable.getEmptyRecords();
                            }
                            var c = CaseFile.Model.getCaseFile(caseFileId);
                            if (c) {
                                if (c.personAssociations) {
                                    var personAssociations = c.personAssociations;
                                    var currentPersonAssoc = this._findPersonAssoc(personId, personAssociations);
                                    if (currentPersonAssoc.person) {
                                        var person = currentPersonAssoc.person;
                                        if (person.contactMethods) {
                                            var contactMethods = person.contactMethods;
                                            contactMethods.splice(whichRow, 1);
                                            
                                            // TODO: Perform Save Case
                                            //Complaint.Service.saveComplaint(complaint);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                ,function (data) { //opened handler
                    data.childTable.jtable('load');
                });
        }
        // ============================= Open/Close People Devices Sub-Table ===================================
        
        
        // ============================= Open/Close People Organizations Sub-Table ===================================
        ,_closePeopleOrganizations: function($t, $row) {
            $t.jtable('closeChildTable', $row.closest('tr'));
        }
        ,_openPeopleOrganizations: function($t, $row) {
            $t.jtable('openChildTable',
                $row.closest('tr'),
                {
                    title: CaseFile.Model.Lookup.PERSON_SUBTABLE_TITLE_ORGANIZATIONS
                    ,sorting: true
                    ,messages: {
                        addNewRecord: 'Add Organization'
                    }
                    ,actions: {
                    listAction: function (postData, jtParams) {
                        var rc = AcmEx.Object.jTableGetEmptyRecords();
                        var recordParent = $row.closest('tr').data('record');
                        if (recordParent && recordParent.personId) {
                            var personId = recordParent.personId;
                            var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                            if (0 >= caseFileId) {
                                return AcmEx.Object.JTable.getEmptyRecords();
                            }
                            var c = CaseFile.Model.getCaseFile(caseFileId);
                            if (c) {
                                if (c.personAssociations) {
                                    var personAssociations = c.personAssociations;
                                    var currentPersonAssoc = this._findPersonAssoc(personId, personAssociations);
                                    if (currentPersonAssoc.person) {
                                        var person = currentPersonAssoc.person;
                                        if (person.organizations) {
                                            var organizations = person.organizations;
                                            var cnt = organizations.length;
                                            for (var i = 0; i < cnt; i++) {
                                                rc.Records.push({
                                                    personId: person.id,
                                                    type: organizations[i].organizationType,
                                                    value: Acm.goodValue(organizations[i].organizationValue),
                                                    created: Acm.getDateFromDatetime(organizations[i].created),
                                                    creator: organizations[i].creator
                                                });
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        return rc;
                    }
                    , createAction: function (postData, jtParams) {
                        var rc = AcmEx.Object.jTableGetEmptyRecord();
                        var recordParent = $row.closest('tr').data('record');
                        if (recordParent && recordParent.personId) {
                            var personId = recordParent.personId;
                            var record = Acm.urlToJson(postData);
                            var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                            if (0 >= caseFileId) {
                                return AcmEx.Object.JTable.getEmptyRecords();
                            }
                            var c = CaseFile.Model.getCaseFile(caseFileId);
                            if (c) {
                                if (c.personAssociations) {
                                    var personAssociations = complaint.personAssociations;
                                    var currentPersonAssoc = this._findPersonAssoc(personId, personAssociations);
                                    if (currentPersonAssoc.person) {
                                        var person = currentPersonAssoc.person;
                                        rc.Record.personId = person.id;
                                        rc.Record.type = record.type;
                                        rc.Record.value = Acm.goodValue(record.value);
                                        rc.Record.created = Acm.getCurrentDay(); //record.created;
                                        rc.Record.creator = App.getUserName();   //record.creator;
                                    }
                                }
                            }
                        }
                        return rc;
                    }
                    , updateAction: function (postData, jtParams) {
                        var rc = AcmEx.Object.jTableGetEmptyRecord();
                        var recordParent = $row.closest('tr').data('record');
                        if (recordParent && recordParent.personId) {
                            var personId = recordParent.personId;
                            var record = Acm.urlToJson(postData);
                            var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                            if (0 >= caseFileId) {
                                return AcmEx.Object.JTable.getEmptyRecords();
                            }
                            var c = CaseFile.Model.getCaseFile(caseFileId);
                            if (c) {
                                if (c.personAssociations) {
                                    var personAssociations = c.personAssociations;
                                    var currentPersonAssoc = this._findPersonAssoc(personId, personAssociations);
                                    if (currentPersonAssoc.person) {
                                        var person = currentPersonAssoc.person;
                                        rc.Record.personId = person.id;
                                        rc.Record.type = record.type;
                                        rc.Record.value = Acm.goodValue(record.value);
                                        rc.Record.created = Acm.getCurrentDay(); //record.created;
                                        rc.Record.creator = App.getUserName();   //record.creator;
                                    }
                                }
                            }
                        }
                        return rc;
                    }
                    , deleteAction: function (postData, jtParams) {
                        return {
                            "Result": "OK"
                        };
                    }
                }
                , fields: {
                    personId: {
                        type: 'hidden',
                        defaultValue: 1 //commData.record.StudentId
                    }
                    , id: {
                        key: true,
                        create: false,
                        edit: false,
                        list: false
                    }
                    , type: {
                        title: 'Type',
                        width: '15%',
                        options: CaseFile.Model.Lookup.getOrganizationTypes()
                    }
                    , value: {
                        title: 'Value',
                        width: '30%'
                    }
                    , created: {
                        title: 'Date Added',
                        width: '20%',
                        create: false,
                        edit: false
                    }
                    , creator: {
                        title: 'Added By',
                        width: '30%',
                        create: false,
                        edit: false
                    }
                }
                , recordAdded: function (event, data) {
                    var recordParent = $row.closest('tr').data('record');
                    if (recordParent && recordParent.personId) {
                        var personId = recordParent.personId;
                        var record = data.record;
                        var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                        if (0 >= caseFileId) {
                            return AcmEx.Object.JTable.getEmptyRecords();
                        }
                        var c = CaseFile.Model.getCaseFile(caseFileId);
                        if (c) {
                            if (c.personAssociations) {
                                var personAssociations = this._findPersonAssoc(personId, personAssociations);
                                if (currentPersonAssoc.person) {
                                    var person = currentPersonAssoc.person;
                                    if (person.organizations) {
                                        var organizations = person.organizations;
                                        var organization = {};
                                        organization.organizationType = record.type;
                                        organization.organizationValue = Acm.goodValue(record.value);
                                        organizations.push(organization);
                                        
                                        // TODO: Perform Save Case
                                        //Complaint.Service.saveComplaint(complaint);
                                    }
                                }
                            }
                        }
                    }
                }
                , recordUpdated: function (event, data) {
                    var recordParent = $row.closest('tr').data('record');
                    if (recordParent && recordParent.personId) {
                        var personId = recordParent.personId;
                        var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                        var record = data.record;
                        var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                        if (0 >= caseFileId) {
                            return AcmEx.Object.JTable.getEmptyRecords();
                        }
                        var c = CaseFile.Model.getCaseFile(caseFileId);
                        if (c) {
                            if (c.personAssociations) {
                                var personAssociations = c.personAssociations;
                                var currentPersonAssoc = this._findPersonAssoc(personId, personAssociations);
                                if (currentPersonAssoc.person) {
                                    var person = currentPersonAssoc.person;
                                    if (person.organizations) {
                                        var organizations = person.organizations;
                                        var organization = organizations[whichRow];
                                        organization.organizationType = record.type;
                                        organization.organizationValue = Acm.goodValue(record.value);
                                        
                                        // TODO: Perform Save Case
                                        //Complaint.Service.saveComplaint(complaint);
                                    }
                                }
                            }
                        }
                    }
                }
                , recordDeleted: function (event, data) {
                    var recordParent = $row.closest('tr').data('record');
                    if (recordParent && recordParent.personId) {
                        var personId = recordParent.personId;
                        var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                        var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                        if (0 >= caseFileId) {
                            return AcmEx.Object.JTable.getEmptyRecords();
                        }
                        var c = CaseFile.Model.getCaseFile(caseFileId);
                        if (c) {
                            if (c.personAssociations) {
                                var personAssociations = c.personAssociations;
                                var currentPersonAssoc = this._findPersonAssoc(personId, personAssociations);
                                if (currentPersonAssoc.person) {
                                    var person = currentPersonAssoc.person;
                                    if (person.organizations) {
                                        var organizations = person.organizations;
                                        organizations.splice(whichRow, 1);
                                        
                                        // TODO: Perform Save Complaint
                                        //Complaint.Service.saveComplaint(complaint);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            ,function (data) { //opened handler
                data.childTable.jtable('load');
            });
        }
        // ============================= Open/Close People Organizations Sub-Table ===================================
        
        
        
        // ============================= Open/Close People Locations Sub-Table ===================================
        ,_closePeopleLocations: function($t, $row) {
            $t.jtable('closeChildTable', $row.closest('tr'));
        }
        ,_openPeopleLocations: function($t, $row) {
            $t.jtable('openChildTable',
                $row.closest('tr'),
                {
                    title: CaseFile.Model.Lookup.PERSON_SUBTABLE_TITLE_LOCATIONS
                    ,sorting: true
                    ,messages: {
                        addNewRecord: 'Add Location'
                    }
                    ,actions: {
                    listAction: function(postData, jtParams) {
                        var rc = AcmEx.Object.jTableGetEmptyRecords();
                        var recordParent = $row.closest('tr').data('record');
                        if (recordParent && recordParent.personId) {
                            var personId = recordParent.personId;
                            var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                            if (0 >= caseFileId) {
                                return AcmEx.Object.JTable.getEmptyRecords();
                            }
                            var c = CaseFile.Model.getCaseFile(caseFileId);
                            if (c) {
                                if (c.personAssociations) {
                                    var personAssociations = c.personAssociations;
                                    var currentPersonAssoc = this._findPersonAssoc(personId, personAssociations);
                                    if (currentPersonAssoc.person) {
                                        var person = currentPersonAssoc.person;
                                        if (person.addresses) {
                                            var addresses = person.addresses;
                                            var cnt = addresses.length;
                                            if(cnt > 0) {
                                                for (var i = 0; i < cnt; i++) {
                                                    rc.Records.push({
                                                        personId: person.id,
                                                        type: addresses[i].type,
                                                        streetAddress: addresses[i].streetAddress,
                                                        city: addresses[i].city,
                                                        state: addresses[i].state,
                                                        zip: Acm.goodValue(addresses[i].zip),
                                                        created: Acm.getDateFromDatetime(addresses[i].created),
                                                        creator: addresses[i].creator
                                                    });
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        return rc;
                    }
                    ,createAction: function(postData, jtParams) {
                        var rc = AcmEx.Object.jTableGetEmptyRecord();
                        var recordParent = $row.closest('tr').data('record');
                        if (recordParent && recordParent.personId) {
                            var personId = recordParent.personId;
                            var record = Acm.urlToJson(postData);
                            var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                            if (0 >= caseFileId) {
                                return AcmEx.Object.JTable.getEmptyRecords();
                            }
                            var c = CaseFile.Model.getCaseFile(caseFileId);
                            if (c) {
                                if (c.personAssociations) {
                                    var personAssociations = c.personAssociations;
                                    var currentPersonAssoc = this._findPersonAssoc(personId, personAssociations);
                                    if (currentPersonAssoc.person) {
                                        var person = currentPersonAssoc.person;
                                        rc.Record.personId = person.id;
                                        rc.Record.type = record.type;
                                        rc.Record.streetAddress = record.streetAddress;
                                        rc.Record.city = record.city;
                                        rc.Record.state = record.state;
                                        rc.Record.zip = record.zip;
                                        rc.Record.created = Acm.getCurrentDay(); //record.created;
                                        rc.Record.creator = App.getUserName();   //record.creator;
                                    }
                                }
                            }
                        }
                        return rc;
                    }
                    ,updateAction: function(postData, jtParams) {
                        var rc = AcmEx.Object.jTableGetEmptyRecord();
                        var recordParent = $row.closest('tr').data('record');
                        if (recordParent && recordParent.personId) {
                            var personId = recordParent.personId;
                            var record = Acm.urlToJson(postData);
                            var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                            if (0 >= caseFileId) {
                                return AcmEx.Object.JTable.getEmptyRecords();
                            }
                            var c = CaseFile.Model.getCaseFile(caseFileId);
                            if (c) {
                                if (c.personAssociations) {
                                    var personAssociations = c.personAssociations;
                                    var currentPersonAssoc = this._findPersonAssoc(personId, personAssociations);
                                    if (currentPersonAssoc.person) {
                                        var person = currentPersonAssoc.person;
                                        rc.Record.personId = person.id;
                                        rc.Record.type = record.type;
                                        rc.Record.streetAddress = record.streetAddress;
                                        rc.Record.city = record.city;
                                        rc.Record.state = record.state;
                                        rc.Record.zip = record.zip;
                                        rc.Record.created = record.created;
                                        rc.Record.creator = record.creator;
                                    }
                                }
                            }
                        }
                        return rc;
                    }
                    ,deleteAction: function(postData, jtParams) {
                        return {
                            "Result": "OK"
                        };
                    }
                }

                ,fields: {
                    personId: {
                        type: 'hidden'
                        ,defaultValue: 1 //commData.record.StudentId
                    }
                    ,id: {
                        key: true
                        ,create: false
                        ,edit: false
                        ,list: false
                    }
                    ,type: {
                        title: 'Type'
                        ,width: '8%'
                        ,options: CaseFile.Model.Lookup.getLocationTypes()
                    }
                    ,streetAddress: {
                        title: 'Address'
                        ,width: '20%'
                    }
                    ,city: {
                        title: 'City'
                        ,width: '10%'
                    }
                    ,state: {
                        title: 'State'
                        ,width: '8%'
                    }
                    ,zip: {
                        title: 'Zip'
                        ,width: '8%'
                    }
                    ,country: {
                        title: 'Country'
                        ,width: '8%'
                    }
                    ,created: {
                        title: 'Date Added'
                        ,width: '15%'
                        ,create: false
                        ,edit: false
                    }
                    ,creator: {
                        title: 'Added By'
                        ,width: '15%'
                        ,create: false
                        ,edit: false
                    }
                }
                ,recordAdded : function (event, data) {
                    var recordParent = $row.closest('tr').data('record');
                    if (recordParent && recordParent.personId) {
                        var personId = recordParent.personId;
                        var record = data.record;
                        var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                        if (0 >= caseFileId) {
                            return AcmEx.Object.JTable.getEmptyRecords();
                        }
                        var c = CaseFile.Model.getCaseFile(caseFileId);
                        if (c) {
                            if (c.personAssociations) {
                                var personAssociations = c.personAssociations;
                                var currentPersonAssoc = this._findPersonAssoc(personId, personAssociations);
                                if (currentPersonAssoc.person) {
                                    var person = currentPersonAssoc.person;
                                    if (person.addresses) {
                                        var addresses = person.addresses;
                                        var address = {};
                                        address.type = record.type;
                                        address.streetAddress = record.streetAddress;
                                        address.city = record.city;
                                        address.state = record.state;
                                        address.zip = record.zip;
                                        addresses.push(address);
                                        
                                        // TODO: Perform Save Case
                                        //Complaint.Service.saveComplaint(complaint);
                                    }
                                }
                            }
                        }
                    }
                }
                ,recordUpdated : function (event, data) {
                    var recordParent = $row.closest('tr').data('record');
                    if (recordParent && recordParent.personId) {
                        var personId = recordParent.personId;
                        var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                        var record = data.record;
                        var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                        if (0 >= caseFileId) {
                            return AcmEx.Object.JTable.getEmptyRecords();
                        }
                        var c = CaseFile.Model.getCaseFile(caseFileId);
                        if (c) {
                            if (c.personAssociations) {
                                var personAssociations = c.personAssociations;
                                var currentPersonAssoc = this._findPersonAssoc(personId, personAssociations);
                                if (currentPersonAssoc.person) {
                                    var person = currentPersonAssoc.person;
                                    if (person.addresses) {
                                        var addresses = person.addresses;
                                        var address = addresses[whichRow];
                                        address.type = record.type;
                                        address.streetAddress = record.streetAddress;
                                        address.city = record.city;
                                        address.state = record.state;
                                        address.zip = record.zip;
                                        
                                        // TODO: Perform Save Case
                                        //Complaint.Service.saveComplaint(complaint);
                                    }
                                }
                            }
                        }
                    }
                }
                ,recordDeleted : function (event, data) {
                    var recordParent = $row.closest('tr').data('record');
                    if (recordParent && recordParent.personId) {
                        var personId = recordParent.personId;
                        var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                        var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                        if (0 >= caseFileId) {
                            return AcmEx.Object.JTable.getEmptyRecords();
                        }
                        var c = CaseFile.Model.getCaseFile(caseFileId);
                        if (c) {
                            if (c.personAssociations) {
                                var personAssociations = c.personAssociations;
                                var currentPersonAssoc = this._findPersonAssoc(personId, personAssociations);
                                if (currentPersonAssoc.person) {
                                    var person = currentPersonAssoc.person;
                                    if (person.addresses) {
                                        var addresses = person.addresses;
                                        addresses.splice(whichRow, 1);
                                        
                                        // TODO: Perform Save Case
                                        //Complaint.Service.saveComplaint(complaint);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            ,function (data) { //opened handler
                data.childTable.jtable('load');
            });
        }
        // ============================= Open/Close People Locations Sub-Table ===================================
        
        
        
        // ============================= Open/Close People Alias Sub-Table ===================================
        ,_closePeopleAliases: function($jt, $row) {
            $jt.jtable('closeChildTable', $row.closest('tr'));
        }
        ,_openPeopleAliases: function($jt, $row) {
            $jt.jtable('openChildTable',
                $row.closest('tr'),
                {
                    title: CaseFile.Model.Lookup.PERSON_SUBTABLE_TITLE_ALIASES
                    ,sorting: true
                    ,messages: {
                        addNewRecord: 'Add Alias'
                    }
                    ,actions: {
                    listAction: function(postData, jtParams) {
                        var rc = AcmEx.Object.jTableGetEmptyRecords();
                        var recordParent = $row.closest('tr').data('record');
                        if (recordParent && recordParent.personId) {
                            var personId = recordParent.personId;
                            var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                            if (0 >= caseFileId) {
                                return AcmEx.Object.JTable.getEmptyRecords();
                            }
                            var c = CaseFile.Model.getCaseFile(caseFileId);
                            if (c) {
                                if (c.personAssociations) {
                                    var personAssociations = c.personAssociations;
                                    var currentPersonAssoc = this._findPersonAssoc(personId, personAssociations);
                                    if (currentPersonAssoc.person) {
                                        var person = currentPersonAssoc.person;
                                        if (person.personAliases) {
                                            var personAliases = person.personAliases;
                                            var cnt = personAliases.length;
                                            if(cnt > 0)
                                            {
                                                for (var i = 0; i < cnt; i++) {
                                                    rc.Records.push({
                                                        personId: person.id,
                                                        type: personAliases[i].aliasType,
                                                        value: Acm.goodValue(personAliases[i].aliasValue),
                                                        created: Acm.getDateFromDatetime(personAliases[i].created),
                                                        creator: personAliases[i].creator
                                                    });
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        return rc;
                    }
                    ,createAction: function(postData, jtParams) {
                        var rc = AcmEx.Object.jTableGetEmptyRecord();
                        var recordParent = $row.closest('tr').data('record');
                        if (recordParent && recordParent.personId) {
                            var personId = recordParent.personId;
                            var record = Acm.urlToJson(postData);
                            var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                            if (0 >= caseFileId) {
                                return AcmEx.Object.JTable.getEmptyRecords();
                            }
                            var c = CaseFile.Model.getCaseFile(caseFileId);
                            if (c) {
                                //var assocId = complaint.originator.id;
                                if (c.personAssociations) {
                                    var personAssociations = c.personAssociations;
                                    var currentPersonAssoc = this._findPersonAssoc(personId, personAssociations);
                                    if (currentPersonAssoc.person) {
                                        var person = currentPersonAssoc.person;
                                        rc.Record.personId = person.id;
                                        rc.Record.type = record.type;
                                        rc.Record.value = Acm.goodValue(record.value);
                                        rc.Record.created = Acm.getCurrentDay(); //record.created;
                                        rc.Record.creator = App.getUserName();   //record.creator;
                                    }
                                }
                            }
                        }
                        return rc;
                    }
                    ,updateAction: function(postData, jtParams) {
                        var rc = AcmEx.Object.jTableGetEmptyRecord();
                        var recordParent = $row.closest('tr').data('record');
                        if (recordParent && recordParent.personId) {
                            var personId = recordParent.personId;
                            var record = Acm.urlToJson(postData);
                            var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                            if (0 >= caseFileId) {
                                return AcmEx.Object.JTable.getEmptyRecords();
                            }
                            var c = CaseFile.Model.getCaseFile(caseFileId);
                            if (c) {
                                if (c.personAssociations) {
                                    var personAssociations = c.personAssociations;
                                    var currentPersonAssoc = this._findPersonAssoc(personId, personAssociations);
                                    if (currentPersonAssoc.person) {
                                        var person = currentPersonAssoc.person;
                                        rc.Record.personId = person.id;
                                        rc.Record.type = record.type;
                                        rc.Record.value = Acm.goodValue(record.value);
                                        rc.Record.created = Acm.getCurrentDay(); //record.created;
                                        rc.Record.creator = App.getUserName();   //record.creator;
                                    }
                                }
                            }
                        }
                        return rc;
                    }
                    ,deleteAction: function(postData, jtParams) {
                        return {
                            "Result": "OK"
                        };
                    }
                }
                ,fields: {
                    personId: {
                        type: 'hidden'
                        ,defaultValue: 1 //commData.record.StudentId
                    }
                    ,id: {
                        key: true
                        ,create: false
                        ,edit: false
                        ,list: false
                    }
                    ,type: {
                        title: 'Type'
                        ,width: '15%'
                        ,options: CaseFile.Model.Lookup.getAliasTypes()
                    }
                    ,value: {
                        title: 'Value'
                        ,width: '30%'
                    }
                    ,created: {
                        title: 'Date Added'
                        ,width: '20%'
                        ,create: false
                        ,edit: false
                    }
                    ,creator: {
                        title: 'Added By'
                        ,width: '30%'
                        ,create: false
                        ,edit: false
                    }
                }
                ,recordAdded : function (event, data) {
                    var recordParent = $row.closest('tr').data('record');
                    if (recordParent && recordParent.personId) {
                        var personId = recordParent.personId;
                        var record = data.record;
                        var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                        if (0 >= caseFileId) {
                            return AcmEx.Object.JTable.getEmptyRecords();
                        }
                        var c = CaseFile.Model.getCaseFile(caseFileId);
                        if (c) {
                            if (c.personAssociations) {
                                var personAssociations = c.personAssociations;
                                var currentPersonAssoc = this._findPersonAssoc(personId, personAssociations);
                                if (currentPersonAssoc.person) {
                                    var person = currentPersonAssoc.person;
                                    if (person.personAliases) {
                                        var personAliases = person.personAliases;
                                        var personAlias = {};
                                        personAlias.aliasType = record.type;
                                        personAlias.aliasValue = Acm.goodValue(record.value);
                                        personAliases.push(personAlias);
                                        
                                        // TODO: Perform Save Case
                                        //Complaint.Service.saveComplaint(complaint);
                                    }
                                }
                            }
                        }
                    }
                }
                ,recordUpdated : function (event, data) {
                    var recordParent = $row.closest('tr').data('record');
                    if (recordParent && recordParent.personId) {
                        var personId = recordParent.personId;
                        var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                        var record = data.record;
                        var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                        if (0 >= caseFileId) {
                            return AcmEx.Object.JTable.getEmptyRecords();
                        }
                        var c = CaseFile.Model.getCaseFile(caseFileId);
                        if (c) {
                            if (c.personAssociations) {
                                var personAssociations = c.personAssociations;
                                var currentPersonAssoc = this._findPersonAssoc(personId, personAssociations);
                                if (currentPersonAssoc.person) {
                                    var person = currentPersonAssoc.person;
                                    if (person.personAliases) {
                                        var personAliases = person.personAliases;
                                        var personAlias = personAliases[whichRow];
                                        personAlias.aliasType = record.type;
                                        personAlias.aliasValue = Acm.goodValue(record.value);
                                        
                                        // TODO: Perform Save Case
                                        //Complaint.Service.saveComplaint(complaint);
                                    }
                                }
                            }
                        }
                    }
                }
                ,recordDeleted : function (event, data) {
                    var recordParent = $row.closest('tr').data('record');
                    if (recordParent && recordParent.personId) {
                        var personId = recordParent.personId;
                        var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                        var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                        if (0 >= caseFileId) {
                            return AcmEx.Object.JTable.getEmptyRecords();
                        }
                        var c = CaseFile.Model.getCaseFile(caseFileId);
                        if (c) {
                            if (c.personAssociations) {
                                var personAssociations = c.personAssociations;
                                var currentPersonAssoc = this._findPersonAssoc(personId, personAssociations);
                                if (currentPersonAssoc.person) {
                                    var person = currentPersonAssoc.person;
                                    if (person.personAliases) {
                                        var personAliases = person.personAliases;
                                        personAliases.splice(whichRow, 1);
                                        
                                        // TODO: Perform Save Case
                                        //Complaint.Service.saveComplaint(complaint);

                                    }
                                }
                            }
                        }
                    }
                }
            }
            ,function (data) { //opened handler
                data.childTable.jtable('load');
            });
        }
        // ============================= Open/Close People Alias Sub-Table ===================================
    }

    ,Participants: {
        create: function() {
            this.$divParticipants    = $("#divParticipants");
            this.createJTableParticipants(this.$divParticipants);

            Acm.Dispatcher.addEventListener(CaseFile.Controller.ME_CASE_FILE_RETRIEVED    ,this.onCaseFileRetrieved);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VE_CASE_FILE_SELECTED     ,this.onCaseFileSelected);
        }
        ,initialize: function() {
        }

        ,onCaseFileRetrieved: function(caseFile) {
            if (caseFile.hasError) {
                //empty table?
            } else {
                AcmEx.Object.JTable.load(CaseFile.View.Participants.$divParticipants);
            }
        }
        ,onCaseFileSelected: function(caseFileId) {
            AcmEx.Object.JTable.load(CaseFile.View.Participants.$divParticipants);
        }

        ,createJTableParticipants: function($s) {
            AcmEx.Object.JTable.useBasic($s, {
                title: 'Participants'
                ,paging: false
                ,messages: {
                    addNewRecord: 'Add Participants'
                }
                ,actions: {
                    listAction: function(postData, jtParams) {
                        var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                        if (0 >= caseFileId) {
                            return AcmEx.Object.JTable.getEmptyRecords();
                        }

                        var rc = AcmEx.Object.JTable.getEmptyRecords();
                        var c = CaseFile.Model.getCaseFile(caseFileId);
//                        if (c && Acm.isArray(c.participants)) {
//                            for (var i = 0; i < c.participants.length; i++) {
//                                var participant = c.participants[i];
//                                var record = {};
//                                record.id = Acm.goodValue(childObject.targetId, 0);
//                                record.title = Acm.goodValue(childObject.targetName);
//                                record.created = Acm.getDateFromDatetime(childObject.created);
//                                record.creator = Acm.goodValue(childObject.creator);
//                                record.status = Acm.goodValue(childObject.status);
//                                rc.Records.push(record);
//                            }
//                        }
                        return rc;

//for test
//                        return {
//                            "Result": "OK"
//                            ,"Records": [
//                                {"id": 11, "title": "Nick Name", "created": "01-02-03", "creator": "123 do re mi", "status": "JJ"}
//                                ,{"id": 12, "title": "Some Name", "created": "14-05-15", "creator": "xyz abc", "status": "Ice Man"}
//                            ]
//                            ,"TotalRecordCount": 2
//                        };
                    }
//                    ,createAction: function(postData, jtParams) {
//                        //custom web form creation takes over; this action should never be called
//                        var rc = {"Result": "OK", "Record": {id:0, title:"", created:"", creator:"", status:""}};
//                        return rc;
//                    }
                    ,updateAction: function(postData, jtParams) {
                        var record = Acm.urlToJson(postData);
                        var rc = AcmEx.Object.JTable.getEmptyRecord();
                        //id,created,creator is readonly
                        //rc.Record.id = record.id;
                        //rc.Record.created = record.created;
                        //rc.Record.creator = record.creator;
                        rc.Record.title = record.title;
                        rc.Record.status = record.status;
                        return rc;
                    }
                }
                ,fields: {
                    id: {
                        title: 'ID'
                        ,key: true
                        ,list: false
                        ,create: false
                        ,edit: false
                    }
                    ,xxxxxxxx: {
                        title: 'Name'
                        ,width: '10%'
//                        ,display: function (commData) {
//                            var a = "<a href='" + App.getContextPath() + CaseFile.Service.Documents.API_DOWNLOAD_DOCUMENT_
//                                + ((0 >= commData.record.id)? "#" : commData.record.id)
//                                + "'>" + commData.record.title + "</a>";
//                            return $(a);
//                        }
                    }
//                    ,created: {
//                        title: 'Created'
//                        ,width: '15%'
//                        ,edit: false
//                    }
//                    ,creator: {
//                        title: 'Creator'
//                        ,width: '15%'
//                        ,edit: false
//                    }
//                    ,status: {
//                        title: 'Status'
//                        ,width: '30%'
//                    }
                }
                ,recordUpdated : function (event, data) {
//                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
//                    var record = data.record;
//                    var caseFileId = CaseFile.View.Tree.getActiveCaseId();
//                    var c = CaseFile.Model.getCaseFile(caseFileId);
//                    if (c && Acm.isArray(c.participants)) {
//                        if (0 < c.participants.length && whichRow < c.participants.length) {
////                                var participant = c.participants[whichRow];
////                                //id,created,creator is readonly
////                                //childObject.Record.id = record.id;
////                                //childObject.Record.created = record.created;
////                                //childObject.Record.creator = record.creator;
//
//                            var participants = {Record:{}};
//                            participants.Record.title = record.title;
//                            participants.Record.status = record.status;
//
//                            CaseFile.Controller.viewChangedParticipant(caseFileId, whichRow, participants);
//                        }
//                    }
                }
            });
        }
    }

    ,Documents: {
        create: function() {
            this.$divDocuments    = $("#divDocs");
            this.createJTableDocuments(this.$divDocuments);
            AcmEx.Object.JTable.clickAddRecordHandler(this.$divDocuments, CaseFile.View.Documents.onClickSpanAddDocument);
            this.$spanAddDocument = this.$divDocuments.find(".jtable-toolbar-item-add-record");
            CaseFile.View.Documents.fillReportSelection();

            Acm.Dispatcher.addEventListener(CaseFile.Controller.ME_CASE_FILE_RETRIEVED    ,this.onCaseFileRetrieved);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VE_CASE_FILE_SELECTED     ,this.onCaseFileSelected);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VE_CASE_FILE_CLOSED       ,this.onCaseFileClosed);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VE_DOCUMENT_ADDED         ,this.onDocumentAdded);


        }
        ,initialize: function() {
        }

        ,onCaseFileRetrieved: function(caseFile) {
            if (caseFile.hasError) {
                //empty table?
            } else {
                AcmEx.Object.JTable.load(CaseFile.View.Documents.$divDocuments);
            }
        }
        ,onCaseFileSelected: function(caseFileId) {
            AcmEx.Object.JTable.load(CaseFile.View.Documents.$divDocuments);
        }
        ,onCaseFileClosed: function(caseFileId) {
            AcmEx.Object.JTable.load(CaseFile.View.Documents.$divDocuments);
        }
        ,onDocumentAdded: function(caseFileId) {
            AcmEx.Object.JTable.load(CaseFile.View.Documents.$divDocuments);
        }

        ,onClickSpanAddDocument: function(event, ctrl) {
            var report = CaseFile.View.Documents.getSelectReport();
            var token = CaseFile.View.MicroData.getToken();

            var caseFileId = CaseFile.Model.getCaseFileId();
            var caseFile = CaseFile.Model.getCaseFile(caseFileId);
            if (caseFile) {
                var url = CaseFile.View.MicroData.getFormUrls()[report];
                if (Acm.isNotEmpty(url)) {
                    url = url.replace("_data=(", "_data=(type:'case', caseId:'" + caseFileId
                        + "',caseNumber:'" + Acm.goodValue(caseFile.caseNumber)
                        + "',caseTitle:'" + Acm.goodValue(caseFile.title)
                        + "',");

                    Acm.Dialog.openWindow(url, "", 810, $(window).height() - 30
                        ,function() {
                            CaseFile.Controller.viewAddedDocument(caseFileId);
                        }
                    );
                }
            }

        }

        ,fillReportSelection: function() {
            var html = "<span>"
                + "<select class='input-sm form-control input-s-sm inline v-middle'>"
                + "<option value='roi'>Report of Investigation</option>"
                + "</select>"
                + "</span>";

            this.$spanAddDocument.before(html);
        }
        ,getSelectReport: function() {
            return Acm.Object.getSelectValue(this.$spanAddDocument.prev().find("select"));
        }

        ,createJTableDocuments: function($s) {
            AcmEx.Object.JTable.useBasic($s, {
                title: 'Documents'
                ,paging: false
                ,messages: {
                    addNewRecord: 'Add Document'
                }
                ,actions: {
                    listAction: function(postData, jtParams) {
                        var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                        if (0 >= caseFileId) {
                            return AcmEx.Object.JTable.getEmptyRecords();
                        }

                        var rc = AcmEx.Object.JTable.getEmptyRecords();
                        var c = CaseFile.Model.getCaseFile(caseFileId);
                        if (c && Acm.isArray(c.childObjects)) {
                            for (var i = 0; i < c.childObjects.length; i++) {
                                var childObject = c.childObjects[i];
                                var record = {};
                                record.id = Acm.goodValue(childObject.targetId, 0);
                                record.title = Acm.goodValue(childObject.targetName);
                                record.created = Acm.getDateFromDatetime(childObject.created);
                                record.creator = Acm.goodValue(childObject.creator);
                                record.status = Acm.goodValue(childObject.status);
                                rc.Records.push(record);
                            }
                        }
                        return rc;

//for test
//                        return {
//                            "Result": "OK"
//                            ,"Records": [
//                                {"id": 11, "title": "Nick Name", "created": "01-02-03", "creator": "123 do re mi", "status": "JJ"}
//                                ,{"id": 12, "title": "Some Name", "created": "14-05-15", "creator": "xyz abc", "status": "Ice Man"}
//                            ]
//                            ,"TotalRecordCount": 2
//                        };
                    }
                    ,createAction: function(postData, jtParams) {
                        //custom web form creation takes over; this action should never be called
                        var rc = {"Result": "OK", "Record": {id:0, title:"", created:"", creator:"", status:""}};
                        return rc;
                    }
                    ,updateAction: function(postData, jtParams) {
                        var record = Acm.urlToJson(postData);
                        var rc = AcmEx.Object.JTable.getEmptyRecord();
                        //id,created,creator is readonly
                        //rc.Record.id = record.id;
                        //rc.Record.created = record.created;
                        //rc.Record.creator = record.creator;
                        rc.Record.title = record.title;
                        rc.Record.status = record.status;
                        return rc;
                    }
                }
                ,fields: {
                    id: {
                        title: 'ID'
                        ,key: true
                        ,list: false
                        ,create: false
                        ,edit: false
                    }
                    ,title: {
                        title: 'Title'
                        ,width: '10%'
                        ,display: function (commData) {
                            var a = "<a href='" + App.getContextPath() + CaseFile.Service.Documents.API_DOWNLOAD_DOCUMENT_
                                + ((0 >= commData.record.id)? "#" : commData.record.id)
                                + "'>" + commData.record.title + "</a>";
                            return $(a);
                        }
                    }
                    ,created: {
                        title: 'Created'
                        ,width: '15%'
                        ,edit: false
                    }
                    ,creator: {
                        title: 'Creator'
                        ,width: '15%'
                        ,edit: false
                    }
                    ,status: {
                        title: 'Status'
                        ,width: '30%'
                    }
                }
                ,recordUpdated : function (event, data) {
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var record = data.record;
                    var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                    var c = CaseFile.Model.getCaseFile(caseFileId);
                    if (c && Acm.isArray(c.childObjects)) {
                        if (0 < c.childObjects.length && whichRow < c.childObjects.length) {
//                                var childObject = c.childObjects[whichRow];
//                                //id,created,creator is readonly
//                                //childObject.Record.id = record.id;
//                                //childObject.Record.created = record.created;
//                                //childObject.Record.creator = record.creator;

                            var childObject = {Record:{}};
                            childObject.Record.title = record.title;
                            childObject.Record.status = record.status;

                            CaseFile.Controller.viewChangedChildObject(caseFileId, whichRow, childObject);
                        }
                    }
                }
            });
        }
    }

    ,Notes: {
        create: function() {
            this.$divNotes          = $("#divNotes");
            this.createJTableNotes(this.$divNotes);

            Acm.Dispatcher.addEventListener(CaseFile.Controller.ME_CASE_FILE_RETRIEVED    ,this.onCaseFileRetrieved);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VE_CASE_FILE_SELECTED     ,this.onCaseFileSelected);
        }
        ,initialize: function() {
        }

        ,onCaseFileRetrieved: function(caseFile) {
            if (caseFile.hasError) {
                //empty table?
            } else {
                AcmEx.Object.JTable.load(CaseFile.View.Notes.$divNotes);
            }
        }
        ,onCaseFileSelected: function(caseFileId) {
            AcmEx.Object.JTable.load(CaseFile.View.Notes.$divNotes);
        }

        ,_makeJtData: function(noteList) {
            var jtData = AcmEx.Object.JTable.getEmptyRecords();
            if (noteList) {
                for (var i = 0; i < noteList.length; i++) {
                    var Record = {};
                    Record.id         = Acm.goodValue(noteList[i].id, 0);
                    Record.note       = Acm.goodValue(noteList[i].note);
                    Record.created    = Acm.getDateFromDatetime(noteList[i].created);
                    Record.creator    = Acm.goodValue(noteList[i].creator);
                    //Record.parentId   = Acm.goodValue(noteList[i].parentId);
                    //Record.parentType = Acm.goodValue(noteList[i].parentType);
                    jtData.Records.push(Record);
                }
                jtData.TotalRecordCount = noteList.length;
            }
            return jtData;
        }
        ,createJTableNotes: function($jt) {
            var sortMap = {};
            sortMap["created"] = "created";

            AcmEx.Object.JTable.usePaging($jt
                ,{
                    title: 'Notes'
                    ,selecting: true
                    ,multiselect: false
                    ,selectingCheckboxes: false
                    ,messages: {
                        addNewRecord: 'Add Note'
                    }
                    ,actions: {
                        pagingListAction: function (postData, jtParams, sortMap) {
                            var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                            if (0 >= caseFileId) {
                                return AcmEx.Object.JTable.getEmptyRecords();
                            }

                            var noteList = CaseFile.Model.Notes.cacheNoteList.get(caseFileId);
                            if (noteList) {
                                return CaseFile.View.Notes._makeJtData(noteList);

                            } else {
                                return CaseFile.Service.Notes.retrieveNoteListDeferred(caseFileId
                                    ,postData
                                    ,jtParams
                                    ,sortMap
                                    ,function(data) {
                                        var noteList = data;
                                        return CaseFile.View.Notes._makeJtData(noteList);
                                    }
                                    ,function(error) {
                                    }
                                );
                            }  //end else
                        }
                        ,createAction: function(postData, jtParams) {
                            var record = Acm.urlToJson(postData);
                            var rc = AcmEx.Object.JTable.getEmptyRecord();
                            var caseFileId = CaseFile.Model.getCaseFileId();
                            var caseFile = CaseFile.Model.getCaseFile(caseFileId);
                            if (caseFile) {
                                rc.Record.parentId = Acm.goodValue(caseFileId, 0);
                                rc.Record.parentType = CaseFile.Model.getObjectType();
                                rc.Record.note = record.note;
                                rc.Record.created = Acm.getCurrentDay(); //record.created;
                                rc.Record.creator = App.getUserName();   //record.creator;
                            }
                            return rc;
                        }
                        ,updateAction: function(postData, jtParams) {
                            var record = Acm.urlToJson(postData);
                            var rc = AcmEx.Object.jTableGetEmptyRecord();
                            var caseFileId = CaseFile.Model.getCaseFileId();
                            var caseFile = CaseFile.Model.getCaseFile(caseFileId);
                            if (caseFile) {
                                rc.Record.parentId = Acm.goodValue(caseFileId, 0);
                                rc.Record.parentType = CaseFile.Model.getObjectType();
                                rc.Record.note = record.note;
                                rc.Record.created = Acm.getCurrentDay(); //record.created;
                                rc.Record.creator = App.getUserName();   //record.creator;
                            }
                            return rc;
                        }
                        ,deleteAction: function(postData, jtParams) {
                            return {
                                "Result": "OK"
                            };
                        }
                    }

                    ,fields: {
                        id: {
                            title: 'ID'
                            ,key: true
                            ,list: false
                            ,create: false
                            ,edit: false
                            ,defaultvalue : 0
                        }
                        ,note: {
                            title: 'Note'
                            ,type: 'textarea'
                            ,width: '50%'
                            ,edit: true
                        }
                        ,created: {
                            title: 'Created'
                            ,width: '15%'
                            ,edit: false
                            ,create: false
                        }
                        ,creator: {
                            title: 'Author'
                            ,width: '15%'
                            ,edit: false
                            ,create: false
                        }
                    } //end field
                    ,recordAdded : function (event, data) {
                        var record = data.record;
                        alert("add");
//                        var complaint = Complaint.getComplaint();
//                        if (complaint) {
//                            var noteToSave = {};
//                            //noteToSave.id = record.id;
//                            noteToSave.note = record.note;
//                            noteToSave.created = Acm.getCurrentDayInternal(); //record.created;
//                            noteToSave.creator = record.creator;   //record.creator;
//                            noteToSave.parentId = complaint.complaintId;
//                            noteToSave.parentType = App.OBJTYPE_COMPLAINT;
//                            Complaint.Service.saveNote(noteToSave);
//                        }
                    }
                    ,recordUpdated: function(event,data){
                        var whichRow = data.row.prevAll("tr").length;
                        var record = data.record;
                        alert("update");
//                        var complaint = Complaint.getComplaint();
//                        if(complaint){
//                            var notes = Complaint.cacheNoteList.get(Complaint.getComplaintId());
//                            if (notes) {
//                                if(notes[whichRow]){
//                                    var noteToSave;
//                                    noteToSave = notes[whichRow];
//                                    noteToSave.note = record.note;
//                                    Complaint.Service.saveNote(noteToSave);
//                                }
//                            }
//                        }
                    }
                    ,recordDeleted : function (event, data) {
                        var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                        alert("delete");
//                        var complaint = Complaint.getComplaint();
//                        if (complaint) {
//                            var notes = Complaint.cacheNoteList.get(Complaint.getComplaintId());
//                            if (notes) {
//                                var noteToDelete = notes[whichRow];
//                                var noteId = noteToDelete.id;
//                                Complaint.Service.deleteNoteById(noteId);
//                            }
//                        }
                    }
                } //end arg
                ,sortMap
            );
        }
    }

    ,Tasks: {
        create: function() {
            this.$divTasks          = $("#divTasks");
            this.createJTableTasks(this.$divTasks);
            AcmEx.Object.JTable.clickAddRecordHandler(this.$divTasks, CaseFile.View.Tasks.onClickSpanAddTask);

            Acm.Dispatcher.addEventListener(CaseFile.Controller.ME_CASE_FILE_RETRIEVED    ,this.onCaseFileRetrieved);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VE_CASE_FILE_SELECTED     ,this.onCaseFileSelected);
        }
        ,initialize: function() {
        }

        ,URL_TASK_DETAIL:  "/plugin/task/"
        ,URL_NEW_TASK_:    "/plugin/task/wizard?parentType=CASE_FILE&reference="

        ,onCaseFileRetrieved: function(caseFile) {
            if (caseFile.hasError) {
                //empty table?
            } else {
                AcmEx.Object.JTable.load(CaseFile.View.Tasks.$divTasks);
            }
        }
        ,onCaseFileSelected: function(caseFileId) {
            AcmEx.Object.JTable.load(CaseFile.View.Tasks.$divTasks);
        }
        ,onClickSpanAddTask: function(event, ctrl) {
            var caseFileId = CaseFile.Model.getCaseFileId();
            var caseFile = CaseFile.Model.getCaseFile(caseFileId);
            if (caseFile) {
                var caseNumber = Acm.goodValue(caseFile.caseNumber);
                var url = CaseFile.View.Tasks.URL_NEW_TASK_  + caseNumber;
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

