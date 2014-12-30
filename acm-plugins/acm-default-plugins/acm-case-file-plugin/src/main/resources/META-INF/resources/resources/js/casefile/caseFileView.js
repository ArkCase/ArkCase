/**
 * CaseFile.View
 *
 * @author jwu
 */
CaseFile.View = CaseFile.View || {
    create : function() {
        if (CaseFile.View.MicroData.create)       {CaseFile.View.MicroData.create();}
        if (CaseFile.View.Tree.create)            {CaseFile.View.Tree.create();}
        if (CaseFile.View.Action.create)          {CaseFile.View.Action.create();}
        if (CaseFile.View.Detail.create)          {CaseFile.View.Detail.create();}
        if (CaseFile.View.People.create)    	  {CaseFile.View.People.create();}
        if (CaseFile.View.Documents.create)       {CaseFile.View.Documents.create();}
        if (CaseFile.View.Participants.create)    {CaseFile.View.Participants.create();}
        if (CaseFile.View.Notes.create)           {CaseFile.View.Notes.create();}
        if (CaseFile.View.Tasks.create)           {CaseFile.View.Tasks.create();}
        if (CaseFile.View.References.create)      {CaseFile.View.References.create();}
        if (CaseFile.View.Events.create)          {CaseFile.View.Events.create();}
        if (CaseFile.View.Correspondence.create)        {CaseFile.View.Correspondence.create();}

    }
    ,onInitialized: function() {
        if (CaseFile.View.MicroData.onInitialized)   {CaseFile.View.MicroData.onInitialized();}
        if (CaseFile.View.Tree.onInitialized)        {CaseFile.View.Tree.onInitialized();}
        if (CaseFile.View.Action.onInitialized)      {CaseFile.View.Action.onInitialized();}
        if (CaseFile.View.Detail.onInitialized)      {CaseFile.View.Detail.onInitialized();}
        if (CaseFile.View.People.onInitialized)	  {CaseFile.View.People.onInitialized();}
        if (CaseFile.View.Documents.onInitialized)   {CaseFile.View.Documents.onInitialized();}
        if (CaseFile.View.Participants.onInitialized){CaseFile.View.Participants.onInitialized();}
        if (CaseFile.View.Notes.onInitialized)       {CaseFile.View.Notes.onInitialized();}
        if (CaseFile.View.Tasks.onInitialized)       {CaseFile.View.Tasks.onInitialized();}
        if (CaseFile.View.References.onInitialized)  {CaseFile.View.References.onInitialized();}
        if (CaseFile.View.Events.onInitialized)      {CaseFile.View.Events.onInitialized();}
        if (CaseFile.View.Correspondence.onInitialized)        {CaseFile.View.Correspondence.onInitialized();}

    }

    ,MicroData: {
        create : function() {
            var items = $(document).items();
            //this.caseFileId = items.properties("caseFileId").itemValue();
            //this.token = items.properties("token").itemValue();
            this.caseFileId = Acm.Object.MicroData.get("caseFileId");
            this.token      = Acm.Object.MicroData.get("token");
            
            
            this.formUrls = new Object();
            
            this.formUrls["edit_case_file"] = items.properties("urlEditCaseFileForm").itemValue();
            this.formUrls["reinvestigate_case_file"] = items.properties("urlReinvestigateCaseFileForm").itemValue();
            this.formUrls["roi"] = items.properties("urlRoiForm").itemValue();
            this.formUrls["enable_frevvo_form_engine"] = items.properties("enableFrevvoFormEngine").itemValue();
            this.formUrls["change_case_status"] = items.properties("urlChangeCaseStatusForm").itemValue();
            this.formUrls["edit_change_case_status"] = items.properties("urlEditChangeCaseStatusForm").itemValue();
        }
        ,onInitialized: function() {
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

            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_RETRIEVED_CASE_FILE_LIST, this.onModelRetrievedCaseFileList);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CHANGED_CASE_TITLE       , this.onViewChangedCaseTitle);
            if ("undefined" != typeof Topbar) {
                Acm.Dispatcher.addEventListener(Topbar.Controller.Asn.VIEW_SET_ASN_DATA       , this.onTopbarViewSetAsnData, Acm.Dispatcher.PRIORITY_HIGH);
            }
        }
        ,onInitialized: function() {
        }

        ,onModelRetrievedCaseFileList: function(key) {
            if (key.hasError) {
                alert(key.errorMsg);
            } else {
                CaseFile.View.Tree.refreshTree(key);
            }
        }
        ,onViewChangedCaseTitle: function(caseFileId, title) {
            CaseFile.View.Tree.updateTitle(caseFileId, title);
        }
        ,onTopbarViewSetAsnData: function(asnData) {
            if (CaseFile.Model.Tree.Config.validateTreeInfo(asnData)) {
                if (0 == asnData.name.indexOf("/plugin/casefile")) {
                    var treeInfo = CaseFile.Model.Tree.Config.getTreeInfo();
                    if (CaseFile.Model.Tree.Config.sameResultSet(asnData)) {
                        if (asnData.key) {
                            var key = CaseFile.Model.Tree.Key.combinePageIdSubKey(asnData.start, asnData.key);
                            CaseFile.View.Tree.refreshTree(key);
                        }
                        return true;
                    }
                }
            }
            return false;
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
        ,testTree: function() {
            var key = "0.441.p";
            var node;
            var a;
            //this.refreshTree(key);
            //return;
            this.toggleAllTreeNode("0.441");
            //this.expandAllTreeNode("0.441");
            //this.collapseAllTreeNode("0.441");

            return;

            node = $("#tree").fancytree("getActiveNode");
            node = this.tree.getActiveNode();
            node = $("#tree").fancytree("getRootNode");
            node = this.tree.getRootNode();
            node = this.tree.getNodeByKey("0.441");
            node = this.tree.getNodeByKey("0.441.p");
            if (node) {
                a = node.isExpanded();
                if (!a) {
                    node.setExpanded(true);
                }
                a = node.isExpanded();
            }
            var z = 1;


        }
        ,refreshTree: function(key) {
            this.tree.reload().done(function(){
                if (Acm.isNotEmpty(key)) {
                    var parts = key.split(CaseFile.Model.Tree.Key.KEY_SEPARATOR);
                    if (parts && 1 < parts.length) {
                        var parentKey = parts[0];
                        //exclue page ID, so start from 1; expand parents only, not include self, so length-1
                        for (var i = 1; i < parts.length-1; i++) {
                            parentKey += CaseFile.Model.Tree.Key.KEY_SEPARATOR + parts[i];
                            var node = CaseFile.View.Tree.tree.getNodeByKey(parentKey);
                            if (node) {
                                if (!node.isExpanded()) {
                                    node.setExpanded(true);
                                }
                            }
                        }
                    }

                    CaseFile.View.Tree.tree.activateKey(key);
                }
            });
        }
        ,activeTreeNode: function(key) {
            this.tree.activateKey(key);
        }
        ,expandAllTreeNode: function(key) {
            var thisNode = this.tree.getNodeByKey(key);
            if (thisNode) {
                thisNode.setExpanded(true);
                thisNode.visit(function(node) {
                    node.setExpanded(true);
                });
            }
        }
        ,collapseAllTreeNode: function(key) {
            var thisNode = this.tree.getNodeByKey(key);
            if (thisNode) {
                thisNode.setExpanded(false);
                thisNode.visit(function(node) {
                    node.setExpanded(false);
                });
            }
        }
        ,toggleAllTreeNode: function(key) {
            var thisNode = this.tree.getNodeByKey(key);
            if (thisNode) {
                thisNode.toggleExpanded();
                thisNode.visit(function(node) {
                    node.toggleExpanded();
                });
            }
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
            var caseFile = CaseFile.Model.Detail.getCaseFile(caseFileId);
            if (node && caseFile) {
                var nodeDisplay = this._getCaseNodeDisplay(caseTitle, Acm.goodValue(caseFile.caseNumber));
                node.setTitle(nodeDisplay);
                this._fixNodeIcon(node);
            }
        }
        ,treeSource: function() {
            var builder = AcmEx.FancyTreeBuilder.reset();

            var treeInfo = CaseFile.Model.Tree.Config.getTreeInfo();
            var caseFiles = CaseFile.Model.List.cachePage.get(treeInfo.start);
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
                builder.addLeaf({key: treeInfo.start + CaseFile.Model.Tree.Key.KEY_SEPARATOR + caseId                       //level 1: /CaseFile
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
                        .addLeaf({key: key + CaseFile.Model.Tree.Key.KEY_SEPARATOR + CaseFile.Model.Tree.Key.NODE_TYPE_PART_DETAILS         //level 2: /CaseFile/Details
                            ,title: "Details"
                        })
                        .addLeaf({key: key + CaseFile.Model.Tree.Key.KEY_SEPARATOR + CaseFile.Model.Tree.Key.NODE_TYPE_PART_PEOPLE          //level 2: /CaseFile/People
                            ,title: "People"
                        })
                        .addLeaf({key: key + CaseFile.Model.Tree.Key.KEY_SEPARATOR + CaseFile.Model.Tree.Key.NODE_TYPE_PART_DOCUMENTS       //level 2: /CaseFile/Documents
                            ,title: "Documents"
//                            ,folder: true
//                            ,lazy: true
//                            ,cache: false
                        })
                        .addLeaf({key: key + CaseFile.Model.Tree.Key.KEY_SEPARATOR + CaseFile.Model.Tree.Key.NODE_TYPE_PART_PARTICIPANTS    //level 2: /CaseFile/Participants
                            ,title: "Participants"
                        })
                        .addLeaf({key: key + CaseFile.Model.Tree.Key.KEY_SEPARATOR + CaseFile.Model.Tree.Key.NODE_TYPE_PART_NOTES           //level 2: /CaseFile/Notes
                            ,title: "Notes"
                        })
                        .addLeaf({key: key + CaseFile.Model.Tree.Key.KEY_SEPARATOR + CaseFile.Model.Tree.Key.NODE_TYPE_PART_TASKS           //level 2: /CaseFile/Tasks
                            ,title: "Tasks"
                        })
                        .addLeaf({key: key + CaseFile.Model.Tree.Key.KEY_SEPARATOR + CaseFile.Model.Tree.Key.NODE_TYPE_PART_REFERENCES      //level 2: /CaseFile/References
                            ,title: "References"
                        })
                        .addLeaf({key: key + CaseFile.Model.Tree.Key.KEY_SEPARATOR + CaseFile.Model.Tree.Key.NODE_TYPE_PART_HISTORY         //level 2: /CaseFile/History
                            ,title: "History"
                        })
                        .addLeaf({key: key + CaseFile.Model.Tree.Key.KEY_SEPARATOR + CaseFile.Model.Tree.Key.NODE_TYPE_PART_TEMPLATES         //level 2: /CaseFile/Correspondence
                            ,title: "Correspondence"
                        })
                        .getTree();

                    break;

                case CaseFile.Model.Tree.Key.NODE_TYPE_PART_PAGE + CaseFile.Model.Tree.Key.NODE_TYPE_PART_OBJECT + CaseFile.Model.Tree.Key.NODE_TYPE_PART_DOCUMENTS: //"pco":
                    var caseFileId = CaseFile.Model.Tree.Key.getCaseFileIdByKey(key);
                    var c = CaseFile.Model.Detail.getCaseFile(caseFileId);
                    if (c) {
                        data.result = [
                             {key: key + CaseFile.Model.Tree.Key.KEY_SEPARATOR + "1", title: "Document1" + "[Status]"}
                            ,{key: key + CaseFile.Model.Tree.Key.KEY_SEPARATOR + "2", title: "Doc2" + "[Status]"}
                        ];
                    } else {
                        data.result = CaseFile.Service.Detail.retrieveCaseFileDeferred(caseFileId
                            ,function(response) {
                                var z = 1;

                                var resultFake = [
                                     {key: key + CaseFile.Model.Tree.Key.KEY_SEPARATOR + "3", title: "Document3" + "[Status]"}
                                    ,{key: key + CaseFile.Model.Tree.Key.KEY_SEPARATOR + "4", title: "Doc4" + "[Status]"}
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
            this.$olMilestoneTrack          = $(".track-progress");
            this.$dlgChangeCaseStatus      = $("#changeCaseStatus");
            this.$dlgConsolidateCase       = $("#consolidateCase");
            this.$edtConsolidateCase       = $("#edtConsolidateCase");
            this.$btnEditCaseFile    	   = $("#tabTitle button[data-title='Edit Case File']");
            this.$btnChangeCaseStatus      = $("#tabTitle button[data-title='Change Case Status']");
            this.$btnConsolidateCase       = $("#tabTitle button[data-title='Consolidate Case']");
            this.$btnReinvestigateCaseFile = $("#tabTitle button[data-title='Reinvestigate Case File']");
            this.$btnEditCaseFile   	  .on("click", function(e) {CaseFile.View.Action.onClickBtnEditCaseFile(e, this);});
            this.$btnChangeCaseStatus     .on("click", function(e) {CaseFile.View.Action.onClickBtnChangeCaseStatus(e, this);});
            this.$btnConsolidateCase      .on("click", function(e) {CaseFile.View.Action.onClickBtnConsolidateCase(e, this);});
            this.$btnReinvestigateCaseFile.on("click", function(e) {CaseFile.View.Action.onClickBtnReinvestigateCaseFile(e, this);});

            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_RETRIEVED_CASE_FILE   ,this.onModelRetrievedCaseFile);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_SELECTED_CASE_FILE     ,this.onViewSelectedCaseFile);
        }
        ,onInitialized: function() {
        }

        ,onClickBtnEditCaseFile: function() {
        	var urlEditCaseFileForm = CaseFile.View.MicroData.getFormUrls()['edit_case_file'];
        	var caseFileId = CaseFile.View.Tree.getActiveCaseId();
            var c = CaseFile.Model.Detail.getCaseFile(caseFileId);
            if (Acm.isNotEmpty(urlEditCaseFileForm) && Acm.isNotEmpty(c)) {
            	var xmlId = '';
            	var pdfId = '';
            	if (Acm.isNotEmpty(c.childObjects) && c.childObjects.length > 0) {
            		for (var i = 0; i < c.childObjects.length; i++) {
            			var child = c.childObjects[i];
            			
            			if (child.targetType != null && child.targetType == 'FILE' && 
            			    child.targetName != null && child.targetName.indexOf('form_case_file_') == 0 &&
            			    child.targetName.substr(-4) == '.xml') 
            			{
            				xmlId = child.targetId;
            			}
            			
            			if (child.targetType != null && child.targetType == 'FILE' && 
            				child.targetName != null && child.targetName.indexOf('Case_File_') == 0&&
            			    child.targetName.substr(-4) == '.pdf') 
            			{
            				pdfId = child.targetId;
            			}
            		}
            	}
            	
            	urlEditCaseFileForm = urlEditCaseFileForm.replace("/embed?", "/popupform?");
            	urlEditCaseFileForm = urlEditCaseFileForm.replace("_data=(", "_data=(caseId:'" + caseFileId + "',caseNumber:'" + c.caseNumber + "',mode:'edit',xmlId:'" + xmlId + "',pdfId:'" + pdfId + "',");
            	Acm.Dialog.openWindow(urlEditCaseFileForm, "", 860, 700
                    ,function() {
                        CaseFile.Controller.viewChangedCaseFile(caseFileId);
                    }
                );
            }
        }
        
        ,onClickBtnChangeCaseStatus: function() {
            CaseFile.View.Action.showDlgChangeCaseStatus(function(event, ctrl){
                var urlChangeCaseStatusForm = CaseFile.View.MicroData.getFormUrls()['change_case_status'];
                var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                var c = CaseFile.Model.Detail.getCaseFile(caseFileId);
                if (Acm.isNotEmpty(urlChangeCaseStatusForm) && Acm.isNotEmpty(c)) {
                    if (Acm.isNotEmpty(c.caseNumber)) {
                        urlChangeCaseStatusForm = urlChangeCaseStatusForm.replace("_data=(", "_data=(caseId:'" + caseFileId + "',caseNumber:'" + c.caseNumber + "',");
                        Acm.Dialog.openWindow(urlChangeCaseStatusForm, "", 860, 700
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
        ,onClickBtnReinvestigateCaseFile: function() {
        	var urlReinvestigateCaseFileForm = CaseFile.View.MicroData.getFormUrls()['reinvestigate_case_file'];
        	var caseFileId = CaseFile.View.Tree.getActiveCaseId();
            var c = CaseFile.Model.Detail.getCaseFile(caseFileId);
            if (Acm.isNotEmpty(urlReinvestigateCaseFileForm) && Acm.isNotEmpty(c)) {
            	var xmlId = '';
            	if (Acm.isNotEmpty(c.childObjects) && c.childObjects.length > 0) {
            		for (var i = 0; i < c.childObjects.length; i++) {
            			var child = c.childObjects[i];
            			
            			if (child.targetType != null && child.targetType == 'FILE' && 
            			    child.targetName != null && child.targetName.indexOf('form_case_file_') == 0 &&
            			    child.targetName.substr(-4) == '.xml') 
            			{
            				xmlId = child.targetId;
            			}
            		}
            	}
            	urlReinvestigateCaseFileForm = urlReinvestigateCaseFileForm.replace("/embed?", "/popupform?");
            	urlReinvestigateCaseFileForm = urlReinvestigateCaseFileForm.replace("_data=(", "_data=(caseId:'" + caseFileId + "',caseNumber:'" + c.caseNumber + "',mode:'reinvestigate',xmlId:'" + xmlId + "',");
            	Acm.Dialog.openWindow(urlReinvestigateCaseFileForm, "", 860, 700
                    ,function() {
            			// TODO: When James will find solution, we should change this
            			window.location.href = App.getContextPath() + '/plugin/casefile';
                    }
                );
            }
        }

        ,onModelRetrievedCaseFile: function(caseFile) {
            if (!caseFile.hasError) {
                CaseFile.View.Action.populate(caseFile);
            }
        }
        ,onViewSelectedCaseFile: function(caseFileId) {
            var caseFile = CaseFile.Model.Detail.cacheCaseFile.get(caseFileId);
            CaseFile.View.Action.populate(caseFile);
        }

        ,populate: function(caseFile) {
            if (CaseFile.Model.Detail.validateData(caseFile)) {
                CaseFile.View.Action.showBtnChangeCaseStatus(Acm.goodValue(caseFile.changeCaseStatus, true));
                CaseFile.View.Action.showMilestone(Acm.goodValue(caseFile.milestones));
            }
        }
        ,showMilestone: function(milestones) {
            var achievedMilestones = [];
            for ( var m = 0; m < milestones.length; m++ )
            {
                achievedMilestones.push(milestones[m].milestoneName);
            }

            var allMilestones = ["Initiated", "Waiver", "Adjudication", "Issued", "Closed"];

            var html = "";
            for ( var i = 0; i < allMilestones.length; i++ )
            {
                html += "<li";
                for ( var j = 0; j < achievedMilestones.length; j++ )
                {
                    if ( achievedMilestones[j] === allMilestones[i] )
                    {
                        html += " class='done'";
                        break;
                    }
                }
                html += "><span>" + allMilestones[i] + "</span><i></i></li>\r";
            }
            this.setHtmlOlMilestoneTracker(html);
            this.setAttrOlMilestoneTracker("data-steps", allMilestones.length);
        }
        ,showDlgChangeCaseStatus: function(onClickBtnPrimary) {
            Acm.Dialog.bootstrapModal(this.$dlgChangeCaseStatus, onClickBtnPrimary);
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
        ,showBtnChangeCaseStatus: function(show) {
            Acm.Object.show(this.$btnChangeCaseStatus, show);
        }
        ,setHtmlOlMilestoneTracker: function(html) {
            Acm.Object.setHtml(this.$olMilestoneTrack, html);
        }
        ,setAttrOlMilestoneTracker: function(name, value) {
            this.$olMilestoneTrack.attr(name, value);
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
                    CaseFile.Controller.viewChangedCaseTitle(CaseFile.View.Tree.getActiveCaseId(), newValue);
                }
            });
//            AcmEx.Object.XEditable.useEditableDate(this.$lnkIncidentDate, {
//                success: function(response, newValue) {
//                    CaseFile.Controller.viewChangedIncidentDate(CaseFile.View.Tree.getActiveCaseId(), newValue);
//                }
//            });
            AcmEx.Object.XEditable.useEditableDate(this.$lnkDueDate, {
                success: function(response, newValue) {
                    CaseFile.Controller.viewChangedDueDate(CaseFile.View.Tree.getActiveCaseId(), newValue);
                }
            });


            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_FOUND_ASSIGNEES        ,this.onModelFoundAssignees);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_FOUND_SUBJECT_TYPES    ,this.onModelFoundSubjectTypes);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_FOUND_PRIORITIES       ,this.onModelFoundPriorities);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_RETRIEVED_CASE_FILE    ,this.onModelRetrievedCaseFile);
            //Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_SAVED_CASE_FILE        ,this.onModelSavedCaseFile);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_SAVED_CASE_TITLE       ,this.onModelSavedCaseTitle);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_SAVED_INCIDENT_DATE    ,this.onModelSavedIncidentDate);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_SAVED_ASSIGNEE         ,this.onModelSavedAssignee);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_SAVED_SUBJECT_TYPE     ,this.onModelSavedSubjectType);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_SAVED_PRIORITY         ,this.onModelSavedPriority);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_SAVED_DUE_DATE         ,this.onModelSavedDueDate);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_SAVED_DETAIL           ,this.onModelSavedDetail);
            //MODEL_ADDED_PARTICIPANT
            //MODEL_UPDATED_PARTICIPANT
            //MODEL_DELETED_PARTICIPANT
            //MODEL_SAVED_CHILD_OBJECT
            //MODEL_ADDED_PERSON_ASSOCIATION
            //MODEL_UPDATED_PERSON_ASSOCIATION
            //MODEL_DELETED_PERSON_ASSOCIATION
            //MODEL_ADDED_ADDRESS
            //MODEL_UPDATED_ADDRESS
            //MODEL_DELETED_ADDRESS
            //MODEL_ADDED_CONTACT_METHOD
            //MODEL_UPDATED_CONTACT_METHOD
            //MODEL_DELETED_CONTACT_METHOD
            //MODEL_ADDED_CONTACT_METHOD
            //MODEL_UPDATED_CONTACT_METHOD
            //MODEL_DELETED_CONTACT_METHOD
            //MODEL_ADDED_CONTACT_METHOD
            //MODEL_ADDED_CONTACT_METHOD
            //MODEL_ADDED_CONTACT_METHOD
            //MODEL_ADDED_CONTACT_METHOD
            //MODEL_UPDATED_CONTACT_METHOD
            //MODEL_DELETED_CONTACT_METHOD

            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_SELECTED_TREE_NODE     ,this.onViewSelectedTreeNode);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_SELECTED_CASE_FILE     ,this.onViewSelectedCaseFile);
        }
        ,onInitialized: function() {
        }


        ,onModelFoundAssignees: function(assignees) {
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
                    CaseFile.Controller.viewChangedAssignee(CaseFile.View.Tree.getActiveCaseId(), newValue);
                }
            });
        }
        ,onModelFoundSubjectTypes: function(subjectTypes) {
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
                    CaseFile.Controller.viewChangedSubjectType(CaseFile.View.Tree.getActiveCaseId(), newValue);
                }
            });
        }
        ,onModelFoundPriorities: function(priorities) {
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
                    CaseFile.Controller.viewChangedPriority(CaseFile.View.Tree.getActiveCaseId(), newValue);
                }
            });
        }
        ,onModelRetrievedCaseFile: function(caseFile) {
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
        ,onModelSavedCaseTitle: function(caseFileId, title) {
            if (title.hasError) {
                //alert("View: onCaseTitleSaved, hasError, errorMsg:" + title.errorMsg);
                CaseFile.View.Detail.setTextLnkCaseTitle("(Error)");
            }
        }
        ,onModelSavedIncidentDate: function(caseFileId, incidentDate) {
            if (incidentDate.hasError) {
                CaseFile.View.Detail.setTextLnkIncidentDate("(Error)");
            }
        }
        ,onModelSavedAssignee: function(caseFileId, assginee) {
            if (assginee.hasError) {
                CaseFile.View.Detail.setTextLnkAssignee("(Error)");
            }
        }
        ,onModelSavedSubjectType: function(caseFileId, subjectType) {
            if (subjectType.hasError) {
                CaseFile.View.Detail.setTextLnkSubjectType("(Error)");
            }
        }
        ,onModelSavedPriority: function(caseFileId, priority) {
            if (priority.hasError) {
                CaseFile.View.Detail.setTextLnkPriority("(Error)");
            }
        }
        ,onModelSavedDueDate: function(caseFileId, created) {
            if (created.hasError) {
                CaseFile.View.Detail.setTextLnkDueDate("(Error)");
            }
        }
        ,onModelSavedDetail: function(caseFileId, details) {
            if (details.hasError) {
                CaseFile.View.Detail.setHtmlDivDetail("(Error)");
            }
        }


        ,onViewSelectedTreeNode: function(key) {
            CaseFile.View.Detail.showPanel(key);
        }
        ,onViewSelectedCaseFile: function(caseFileId) {
            CaseFile.View.Detail.showTopPanel(0 < caseFileId);

            var caseFile = CaseFile.Model.Detail.cacheCaseFile.get(caseFileId);
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
            CaseFile.Controller.viewChangedDetail(CaseFile.View.Tree.getActiveCaseId(), htmlDetail);
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
                this.setTextLnkIncidentDate(Acm.getDateFromDatetime(c.created));//c.incidentDate
                this.setTextLnkSubjectType(Acm.goodValue(c.caseType));
                this.setTextLnkPriority(Acm.goodValue(c.priority));
                this.setTextLnkDueDate(Acm.getDateFromDatetime(c.dueDate));
                this.setTextLnkStatus(Acm.goodValue(c.status));
                this.setHtmlDivDetail(Acm.goodValue(c.details));

                var assignee = CaseFile.Model.Detail.getAssignee(c);
                this.setTextLnkAssignee(Acm.goodValue(assignee));
            }
        }

        ,setTextLabCaseNumber: function(txt) {
            Acm.Object.setText(this.$labCaseNumber, txt);
        }
        ,setTextLnkCaseTitle: function(txt) {
            AcmEx.Object.XEditable.setValue(this.$lnkCaseTitle, txt);
        }
        ,setTextLnkIncidentDate: function(txt) {
            //AcmEx.Object.XEditable.setDate(this.$lnkIncidentDate, txt);
            Acm.Object.setText(this.$lnkIncidentDate, txt);
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
//        ,showChangeCaseStatusButton: function() {
//        	if (CaseFile.View.Action.$btnChangeCaseStatus) {
//        		CaseFile.View.Action.$btnChangeCaseStatus.show();
//        	}
//        }
//        ,hideChangeCaseStatusButton: function() {
//        	if (CaseFile.View.Action.$btnChangeCaseStatus) {
//        		CaseFile.View.Action.$btnChangeCaseStatus.hide();
//        	}
//        }

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
            this.$divPeople = $("#divPeople");
            this.createJTable(this.$divPeople);

            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_RETRIEVED_CASE_FILE         ,this.onModelRetrievedCaseFile);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_SELECTED_CASE_FILE           ,this.onViewSelectedCaseFile);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_ADDED_PERSON_ASSOCIATION    ,this.onModelAddedPersonAssociation);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_UPDATED_PERSON_ASSOCIATION  ,this.onModelUpdatedPersonAssociation);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_DELETED_PERSON_ASSOCIATION  ,this.onModelDeletedPersonAssociation);

            if (this.ContactMethods.create)     {this.ContactMethods.create();}
            if (this.SecurityTags.create)       {this.SecurityTags.create();}
            if (this.Organizations.create)      {this.Organizations.create();}
            if (this.Addresses.create)          {this.Addresses.create();}
            if (this.Aliases.create)            {this.Aliases.create();}
        }
        ,onInitialized: function() {
            if (CaseFile.View.People.ContactMethods.onInitialized)     {CaseFile.View.People.ContactMethods.onInitialized();}
            if (CaseFile.View.People.SecurityTags.onInitialized)       {CaseFile.View.People.SecurityTags.onInitialized();}
            if (CaseFile.View.People.Organizations.onInitialized)      {CaseFile.View.People.Organizations.onInitialized();}
            if (CaseFile.View.People.Addresses.onInitialized)          {CaseFile.View.People.Addresses.onInitialized();}
            if (CaseFile.View.People.Aliases.onInitialized)            {CaseFile.View.People.Aliases.onInitialized();}
        }

        ,onModelRetrievedCaseFile: function(caseFile) {
            if (caseFile.hasError) {
                //empty table?
            } else {
                AcmEx.Object.JTable.load(CaseFile.View.People.$divPeople);
            }
        }
        ,onViewSelectedCaseFile: function(caseFileId) {
            AcmEx.Object.JTable.load(CaseFile.View.People.$divPeople);
        }
        ,onModelAddedPersonAssociation: function(personAssociation) {
            if (personAssociation.hasError) {
                AcmEx.Object.JTable.load(CaseFile.View.People.$divPeople);
            }
        }
        ,onModelUpdatedPersonAssociation: function(personAssociation) {
            if (personAssociation.hasError) {
                AcmEx.Object.JTable.load(CaseFile.View.People.$divPeople);
            }
        }
        ,onModelDeletedPersonAssociation: function(personAssociationId) {
            if (personAssociationId.hasError) {
                AcmEx.Object.JTable.load(CaseFile.View.People.$divPeople);
            }
        }

        ,createJTable: function($s) {
            AcmEx.Object.JTable.useChildTable($s
                ,[
                    CaseFile.View.People.ContactMethods.createLink
                    ,CaseFile.View.People.Organizations.createLink
                    ,CaseFile.View.People.Addresses.createLink
                    ,CaseFile.View.People.Aliases.createLink
                ]
                ,{
                    title: 'People'
                    ,paging: true   //fix me
                    ,sorting: true  //fix me
                    ,messages: {
                        addNewRecord: 'Add Person'
                    }
                    ,actions: {
                        listAction: function(postData, jtParams) {
                            var rc = AcmEx.Object.JTable.getEmptyRecords();
                            var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                            var c = CaseFile.Model.Detail.getCaseFile(caseFileId);
                            if (CaseFile.Model.Detail.validateData(c)) {
                                var personAssociations = c.personAssociations;
                                for (var i = 0; i < personAssociations.length; i++) {
                                    if (CaseFile.Model.Detail.validatePersonAssociation(personAssociations[i])) {
                                        rc.Records.push({
                                            assocId:     personAssociations[i].id
                                            ,title:      personAssociations[i].person.title
                                            ,givenName:  personAssociations[i].person.givenName
                                            ,familyName: personAssociations[i].person.familyName
                                            ,personType: personAssociations[i].personType
                                        });
                                    }
                                }
                                rc.TotalRecordCount = rc.Records.length;
                            }
                            return rc;
    //                        return {
    //	                          "Result": "OK"&& c.originator
    //	                          ,"Records": [
    //	                              {"id": 11, "title": "Mr", "givenName": "Some Name 1", "familyName": "Some Second Name 1", "personType": "Initiator"}
    //	                              ,{"id": 12, "title": "Mrs", "givenName": "Some Name 2", "familyName": "Some Second Name 2", "personType": "Complaintant"}
    //	                          ]
    //	                          ,"TotalRecordCount": 2
    //	                      };
                        }
                        ,createAction: function(postData, jtParams) {
                            var record = Acm.urlToJson(postData);
                            var rc = AcmEx.Object.JTable.getEmptyRecord();
                            rc.Record.title = record.title;
                            rc.Record.givenName = record.givenName;
                            rc.Record.familyName = record.familyName;
                            rc.Record.personType = record.personType;
                            return rc;
                        }
                        ,updateAction: function(postData, jtParams) {
                            var record = Acm.urlToJson(postData);
                            var rc = AcmEx.Object.JTable.getEmptyRecord();
                            rc.Record.title = record.title;
                            rc.Record.givenName = record.givenName;
                            rc.Record.familyName = record.familyName;
                            rc.Record.personType = record.personType;
                            return rc;
                        }
                        ,deleteAction: function(postData, jtParams) {
                            return {
                               "Result": "OK"
                            };
                        }
                    }
                    ,fields: {
                        assocId: {
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
                            ,options: CaseFile.Model.Lookup.getPersonTypes()
                        }
                    }
                    ,recordAdded: function(event, data){
                        var record = data.record;
                        var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                        if (0 < caseFileId) {
                            var pa = {};
                            pa.personType = record.personType;
                            //pa.personDescription = record.personDescription;
                            pa.person = {};
                            pa.person.title = record.title;
                            pa.person.givenName = record.givenName;
                            pa.person.familyName = record.familyName;
                            CaseFile.Controller.viewAddedPersonAssociation(caseFileId, pa);
                        }
                     }

                    ,recordUpdated: function(event, data){
                        var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                        var record = data.record;
                        var assocId = record.assocId;
                        var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                        var c = CaseFile.Model.Detail.getCaseFile(caseFileId);
                        if (CaseFile.Model.Detail.validateData(c)) {
                            if (c.personAssociations.length > whichRow) {
                                var pa = c.personAssociations[whichRow];
                                if (CaseFile.Model.Detail.validatePersonAssociation(pa)) {
                                    pa.person.title = record.title;
                                    pa.person.givenName = record.givenName;
                                    pa.person.familyName = record.familyName;
                                    pa.personType = record.personType;
                                    CaseFile.Controller.viewUpdatedPersonAssociation(caseFileId, pa);
                                }
                            }
                        }
                    }
                    ,recordDeleted: function(event,data) {
                        var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                        var record = data.record;
                        var personAssociationId = record.assocId;
                        var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                        if (0 < caseFileId && 0 < personAssociationId) {
                            CaseFile.Controller.viewDeletedPersonAssociation(caseFileId, personAssociationId);
                        }
                    }
                }
            );
        }


        ,_commonTypeValueRecord: function ($row, postData) {
            var rc = AcmEx.Object.jTableGetEmptyRecord();
            var recordParent = $row.closest('tr').data('record');
            if (recordParent && recordParent.assocId) {
                var assocId = recordParent.assocId;
                var record = Acm.urlToJson(postData);
                rc.Record.assocId = assocId;
                rc.Record.type = Acm.goodValue(record.type);
                rc.Record.value = Acm.goodValue(record.value);
                rc.Record.created = Acm.getCurrentDay(); //record.created;
                rc.Record.creator = App.getUserName();   //record.creator;
            }
            return rc;
        }

        ,ContactMethods: {
            create: function() {
                Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_ADDED_CONTACT_METHOD        ,this.onModelAddedContactMethod);
                Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_UPDATED_CONTACT_METHOD      ,this.onModelUpdatedContactMethod);
                Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_DELETED_CONTACT_METHOD      ,this.onModelDeletedContactMethod);
            }
            ,onInitialized: function() {
            }

            ,onModelAddedContactMethod: function(contactMethod) {
                if (contactMethod.hasError) {
                    //refresh child table??;
                }
            }
            ,onModelUpdatedContactMethod: function(contactMethod) {
                if (contactMethod.hasError) {
                    //refresh child table??;
                }
            }
            ,onModelDeletedContactMethod: function(contactMethodId) {
                if (contactMethodId.hasError) {
                    //refresh child table??;
                }
            }

            ,createLink: function($jt) {
                var $link = $("<a href='#' class='inline animated btn btn-default btn-xs' data-toggle='class:show' title='Communication Devices'><i class='fa fa-phone'></i></a>");
                $link.click(function (e) {
                    AcmEx.Object.JTable.toggleChildTable($jt, $link, CaseFile.View.People.ContactMethods.onOpen, CaseFile.Model.Lookup.PERSON_SUBTABLE_TITLE_CONTACT_METHODS);
                    e.preventDefault();
                });
                return $link;
            }
            ,onOpen: function($jt, $row) {
                AcmEx.Object.JTable.useAsChild($jt, $row, {
                    title: CaseFile.Model.Lookup.PERSON_SUBTABLE_TITLE_CONTACT_METHODS
                    ,sorting: true
                    ,messages: {
                        addNewRecord: 'Add Device'
                    }
                    ,actions: {
                        listAction: function (postData, jtParams) {
                            var rc = AcmEx.Object.jTableGetEmptyRecords();
                            var recordParent = $row.closest('tr').data('record');
                            if (recordParent && recordParent.assocId) {
                                var assocId = recordParent.assocId;

                                var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                                var c = CaseFile.Model.Detail.getCaseFile(caseFileId);
                                if (CaseFile.Model.Detail.validateData(c)) {
                                    var personAssociations = c.personAssociations;
                                    var personAssociation = CaseFile.Model.Detail.findPersonAssociation(assocId, personAssociations);
                                    if (CaseFile.Model.Detail.validatePersonAssociation(personAssociation)) {
                                        var contactMethods = personAssociation.person.contactMethods;
                                        for (var i = 0; i < contactMethods.length; i++) {
                                            rc.Records.push({
                                                assocId  : assocId
                                                ,id      : Acm.goodValue(contactMethods[i].id, 0)
                                                ,type    : Acm.goodValue(contactMethods[i].type)
                                                ,value   : Acm.goodValue(contactMethods[i].value)
                                                ,created : Acm.getDateFromDatetime(contactMethods[i].created)
                                                ,creator : Acm.goodValue(contactMethods[i].creator)
                                            });
                                        }
                                    }
                                }
                            }
                            return rc;
                        }
                        ,createAction: function (postData, jtParams) {
                            return CaseFile.View.People._commonTypeValueRecord($row, postData);
                        }
                        ,updateAction: function (postData, jtParams) {
                            return CaseFile.View.People._commonTypeValueRecord($row, postData);
                        }
                        ,deleteAction: function (postData, jtParams) {
                            return {"Result": "OK"};
                        }
                    }
                    ,fields: {
                        assocId: {
                            type: 'hidden'
                            ,defaultValue: 0
                        }
                        , id: {
                            key: true
                            ,create: false
                            ,edit: false
                            ,list: false
                        }
                        ,type: {
                            title: 'Type'
                            ,width: '15%'
                            ,options: CaseFile.Model.Lookup.getContactMethodTypes()
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
                            //,type: 'date'
                            //,displayFormat: 'yy-mm-dd'
                        }
                        ,creator: {
                            title: 'Added By'
                            ,width: '30%'
                            ,create: false
                            ,edit: false
                        }
                    }
                    ,recordAdded: function (event, data) {
                        //var recordParent = $row.closest('tr').data('record');
                        //if (recordParent && recordParent.assocId && 0 < caseFileId) {
                        //    var assocId = recordParent.assocId;
                        var record = data.record;
                        var contactMethod = {};
                        var assocId = record.assocId;
                        contactMethod.type  = Acm.goodValue(record.type);
                        contactMethod.value = Acm.goodValue(record.value);
                        var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                        if (0 < caseFileId && 0 < assocId) {
                            CaseFile.Controller.viewAddedContactMethod(caseFileId, assocId, contactMethod);
                        }
                    }
                    ,recordUpdated: function (event, data) {
                        //var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                        //var recordParent = $row.closest('tr').data('record');
                        //if (recordParent && recordParent.assocId && 0 < caseFileId) {
                        //    var assocId = recordParent.assocId;
                        var record = data.record;
                        var contactMethod = {};
                        var assocId = record.assocId;
                        contactMethod.id    = Acm.goodValue(record.id, 0);
                        contactMethod.type  = Acm.goodValue(record.type);
                        contactMethod.value = Acm.goodValue(record.value);
                        var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                        if (0 < caseFileId && 0 < assocId && 0 < contactMethod.id) {
                            CaseFile.Controller.viewUpdatedContactMethod(caseFileId, assocId, contactMethod);
                        }
                    }
                    ,recordDeleted: function (event, data) {
                        //var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                        var record = data.record;
                        var assocId = record.assocId;
                        var contactMethodId = Acm.goodValue(record.id, 0);
                        var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                        if (0 < caseFileId && 0 < assocId && 0 < contactMethodId) {
                            CaseFile.Controller.viewDeletedContactMethod(caseFileId, assocId, contactMethodId);
                        }
                    }
                });
            }
        }

        ,SecurityTags: {
            create: function() {
                Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_ADDED_SECURITY_TAG        ,this.onModelAddedSecurityTag);
                Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_UPDATED_SECURITY_TAG      ,this.onModelUpdatedSecurityTag);
                Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_DELETED_SECURITY_TAG      ,this.onModelDeletedSecurityTag);
            }
            ,onInitialized: function() {
            }

            ,onModelAddedSecurityTag: function(securityTag) {
                if (securityTag.hasError) {
                    //refresh child table??;
                }
            }
            ,onModelUpdatedSecurityTag: function(securityTag) {
                if (securityTag.hasError) {
                    //refresh child table??;
                }
            }
            ,onModelDeletedSecurityTag: function(securityTagId) {
                if (securityTagId.hasError) {
                    //refresh child table??;
                }
            }

            ,createLink: function($jt) {
                var $link = $("<a href='#' class='inline animated btn btn-default btn-xs' data-toggle='class:show'><i class='fa fa-phone'></i></a>");
                $link.click(function (e) {
                    AcmEx.Object.JTable.toggleChildTable($jt, $link, CaseFile.View.People.ContactMethods.onOpen, CaseFile.Model.Lookup.PERSON_SUBTABLE_TITLE_SECURITY_TAGS);
                    e.preventDefault();
                });
                return $link;
            }
            ,onOpen: function($jt, $row) {
                AcmEx.Object.JTable.useAsChild($jt, $row, {
                    title: CaseFile.Model.Lookup.PERSON_SUBTABLE_TITLE_SECURITY_TAGS
                    ,sorting: true
                    ,messages: {
                        addNewRecord: 'Add Device'
                    }
                    ,actions: {
                        listAction: function (postData, jtParams) {
                            var rc = AcmEx.Object.jTableGetEmptyRecords();
                            var recordParent = $row.closest('tr').data('record');
                            if (recordParent && recordParent.assocId) {
                                var assocId = recordParent.assocId;

                                var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                                var c = CaseFile.Model.Detail.getCaseFile(caseFileId);
                                if (CaseFile.Model.Detail.validateData(c)) {
                                    var personAssociations = c.personAssociations;
                                    var personAssociation = CaseFile.Model.Detail.findPersonAssociation(assocId, personAssociations);
                                    if (CaseFile.Model.Detail.validatePersonAssociation(personAssociation)) {
                                        var securityTags = personAssociation.person.securityTags;
                                        for (var i = 0; i < securityTags.length; i++) {
                                            rc.Records.push({
                                                assocId  : assocId
                                                ,id      : Acm.goodValue(securityTags[i].id, 0)
                                                ,type    : Acm.goodValue(securityTags[i].type)
                                                ,value   : Acm.goodValue(securityTags[i].value)
                                                ,created : Acm.getDateFromDatetime(securityTags[i].created)
                                                ,creator : Acm.goodValue(securityTags[i].creator)
                                            });
                                        }
                                    }
                                }
                            }
                            return rc;
                        }
                        ,createAction: function (postData, jtParams) {
                            return CaseFile.View.People._commonTypeValueRecord($row, postData);
                        }
                        ,updateAction: function (postData, jtParams) {
                            return CaseFile.View.People._commonTypeValueRecord($row, postData);
                        }
                        ,deleteAction: function (postData, jtParams) {
                            return {"Result": "OK"};
                        }
                    }
                    ,fields: {
                        assocId: {
                            type: 'hidden'
                            ,defaultValue: 0
                        }
                        , id: {
                            key: true
                            ,create: false
                            ,edit: false
                            ,list: false
                        }
                        ,type: {
                            title: 'Type'
                            ,width: '15%'
                            ,options: CaseFile.Model.Lookup.getSecurityTagTypes()
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
                            //,type: 'date'
                            //,displayFormat: 'yy-mm-dd'
                        }
                        ,creator: {
                            title: 'Added By'
                            ,width: '30%'
                            ,create: false
                            ,edit: false
                        }
                    }
                    ,recordAdded: function (event, data) {
                        var record = data.record;
                        var securityTag = {};
                        var assocId = record.assocId;
                        securityTag.type  = Acm.goodValue(record.type);
                        securityTag.value = Acm.goodValue(record.value);
                        var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                        if (0 < caseFileId && 0 < assocId) {
                            CaseFile.Controller.viewAddedSecurityTag(caseFileId, assocId, securityTag);
                        }
                    }
                    ,recordUpdated: function (event, data) {
                        var record = data.record;
                        var securityTag = {};
                        var assocId = record.assocId;
                        securityTag.id    = Acm.goodValue(record.id, 0);
                        securityTag.type  = Acm.goodValue(record.type);
                        securityTag.value = Acm.goodValue(record.value);
                        var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                        if (0 < caseFileId && 0 < assocId && 0 < securityTag.id) {
                            CaseFile.Controller.viewUpdatedSecurityTag(caseFileId, assocId, securityTag);
                        }
                    }
                    ,recordDeleted: function (event, data) {
                        var record = data.record;
                        var assocId = record.assocId;
                        var securityTagId = Acm.goodValue(record.id, 0);
                        var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                        if (0 < caseFileId && 0 < assocId && 0 < securityTagId) {
                            CaseFile.Controller.viewDeletedSecurityTag(caseFileId, assocId, securityTagId);
                        }
                    }
                });
            }
        }

        ,Organizations: {
            create: function() {
                Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_ADDED_ORGANIZATION        ,this.onModelAddedOrganization);
                Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_UPDATED_ORGANIZATION      ,this.onModelUpdatedOrganization);
                Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_DELETED_ORGANIZATION      ,this.onModelDeletedOrganization);
            }
            ,onInitialized: function() {
            }

            ,onModelAddedOrganization: function(organization) {
                if (organization.hasError) {
                    //refresh child table??;
                }
            }
            ,onModelUpdatedOrganization: function(organization) {
                if (organization.hasError) {
                    //refresh child table??;
                }
            }
            ,onModelDeletedOrganization: function(organizationId) {
                if (organizationId.hasError) {
                    //refresh child table??;
                }
            }

            ,createLink: function($jt) {
                var $link = $("<a href='#' class='inline animated btn btn-default btn-xs' data-toggle='class:show' title='Organizations'><i class='fa fa-book'></i></a>");
                $link.click(function (e) {
                    AcmEx.Object.JTable.toggleChildTable($jt, $link, CaseFile.View.People.Organizations.onOpen, CaseFile.Model.Lookup.PERSON_SUBTABLE_TITLE_ORGANIZATIONS);
                    e.preventDefault();
                });
                return $link;
            }
            ,onOpen: function($jt, $row) {
                AcmEx.Object.JTable.useAsChild($jt, $row, {
                    title: CaseFile.Model.Lookup.PERSON_SUBTABLE_TITLE_ORGANIZATIONS
                    ,sorting: true
                    ,messages: {
                        addNewRecord: 'Add Organization'
                    }
                    ,actions: {
                        listAction: function (postData, jtParams) {
                            var rc = AcmEx.Object.jTableGetEmptyRecords();
                            var recordParent = $row.closest('tr').data('record');
                            if (recordParent && recordParent.assocId) {
                                var assocId = recordParent.assocId;

                                var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                                var c = CaseFile.Model.Detail.getCaseFile(caseFileId);
                                if (CaseFile.Model.Detail.validateData(c)) {
                                    var personAssociations = c.personAssociations;
                                    var personAssociation = CaseFile.Model.Detail.findPersonAssociation(assocId, personAssociations);
                                    if (CaseFile.Model.Detail.validatePersonAssociation(personAssociation)) {
                                        var organizations = personAssociation.person.organizations;
                                        for (var i = 0; i < organizations.length; i++) {
                                            rc.Records.push({
                                                assocId  : assocId
                                                ,id      : Acm.goodValue(organizations[i].organizationId, 0)
                                                ,type    : Acm.goodValue(organizations[i].organizationType)
                                                ,value   : Acm.goodValue(organizations[i].organizationValue)
                                                ,created : Acm.getDateFromDatetime(organizations[i].created)
                                                ,creator : Acm.goodValue(organizations[i].creator)
                                            });
                                        }
                                    }
                                }
                            }
                            return rc;
                        }
                        ,createAction: function (postData, jtParams) {
                            return CaseFile.View.People._commonTypeValueRecord($row, postData);
                        }
                        ,updateAction: function (postData, jtParams) {
                            return CaseFile.View.People._commonTypeValueRecord($row, postData);
                        }
                        ,deleteAction: function (postData, jtParams) {
                            return {"Result": "OK"};
                        }
                    }
                    , fields: {
                        assocId: {
                            type: 'hidden'
                            ,defaultValue: 0
                        }
                        , id: {
                            key: true
                            ,create: false
                            ,edit: false
                            ,list: false
                        }
                        , type: {
                            title: 'Type'
                            ,width: '15%'
                            ,options: CaseFile.Model.Lookup.getOrganizationTypes()
                        }
                        , value: {
                            title: 'Value'
                            ,width: '30%'
                        }
                        , created: {
                            title: 'Date Added'
                            ,width: '20%'
                            ,create: false
                            ,edit: false
                        }
                        , creator: {
                            title: 'Added By'
                            ,width: '30%'
                            ,create: false
                            ,edit: false
                        }
                    }
                    , recordAdded: function (event, data) {
                        var record = data.record;
                        var organization = {};
                        var assocId = record.assocId;
                        organization.organizationType  = Acm.goodValue(record.type);
                        organization.organizationValue = Acm.goodValue(record.value);
                        var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                        if (0 < caseFileId && 0 < assocId) {
                            CaseFile.Controller.viewAddedOrganization(caseFileId, assocId, organization);
                        }
                    }
                    , recordUpdated: function (event, data) {
                        var record = data.record;
                        var organization = {};
                        var assocId = record.assocId;
                        organization.organizationId    = Acm.goodValue(record.id, 0);
                        organization.organizationType  = Acm.goodValue(record.type);
                        organization.organizationValue = Acm.goodValue(record.value);
                        var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                        if (0 < caseFileId && 0 < assocId && 0 < organization.organizationId) {
                            CaseFile.Controller.viewUpdatedOrganization(caseFileId, assocId, organization);
                        }
                    }
                    , recordDeleted: function (event, data) {
                        var record = data.record;
                        var assocId = record.assocId;
                        var organizationId = Acm.goodValue(record.id, 0);
                        var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                        if (0 < caseFileId && 0 < assocId && 0 < organizationId) {
                            CaseFile.Controller.viewDeletedOrganization(caseFileId, assocId, organizationId);
                        }
                    }
                });
            }
        }

        ,Addresses: {
            create: function() {
                Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_ADDED_ADDRESS        ,this.onModelAddedAddress);
                Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_UPDATED_ADDRESS      ,this.onModelUpdatedAddress);
                Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_DELETED_ADDRESS      ,this.onModelDeletedAddress);
            }
            ,onInitialized: function() {
            }

            ,onModelAddedAddress: function(address) {
                if (address.hasError) {
                    //refresh child table??;
                }
            }
            ,onModelUpdatedAddress: function(address) {
                if (address.hasError) {
                    //refresh child table??;
                }
            }
            ,onModelDeletedAddress: function(addressId) {
                if (addressId.hasError) {
                    //refresh child table??;
                }
            }

            ,createLink: function($jt) {
                var $link = $("<a href='#' class='inline animated btn btn-default btn-xs' data-toggle='class:show' title='Locations'><i class='fa fa-map-marker'></i></a>");
                $link.click(function (e) {
                    AcmEx.Object.JTable.toggleChildTable($jt, $link, CaseFile.View.People.Addresses.onOpen, CaseFile.Model.Lookup.PERSON_SUBTABLE_TITLE_ADDRESSES);
                    e.preventDefault();
                });
                return $link;
            }
            ,onOpen: function($jt, $row) {
                AcmEx.Object.JTable.useAsChild($jt, $row, {
                    title: CaseFile.Model.Lookup.PERSON_SUBTABLE_TITLE_ADDRESSES
                    ,sorting: true
                    ,messages: {
                        addNewRecord: 'Add Location'
                    }
                    ,actions: {
                        listAction: function (postData, jtParams) {
                            var rc = AcmEx.Object.jTableGetEmptyRecords();
                            var recordParent = $row.closest('tr').data('record');
                            if (recordParent && recordParent.assocId) {
                                var assocId = recordParent.assocId;

                                var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                                var c = CaseFile.Model.Detail.getCaseFile(caseFileId);
                                if (CaseFile.Model.Detail.validateData(c)) {
                                    var personAssociations = c.personAssociations;
                                    var personAssociation = CaseFile.Model.Detail.findPersonAssociation(assocId, personAssociations);
                                    if (CaseFile.Model.Detail.validatePersonAssociation(personAssociation)) {
                                        var addresses = personAssociation.person.addresses;
                                        for (var i = 0; i < addresses.length; i++) {
                                            rc.Records.push({
                                                assocId        : assocId
                                                ,id            : Acm.goodValue(addresses[i].id, 0)
                                                ,type          : Acm.goodValue(addresses[i].type)
                                                ,streetAddress : Acm.goodValue(addresses[i].streetAddress)
                                                ,city          : Acm.goodValue(addresses[i].city)
                                                ,state         : Acm.goodValue(addresses[i].state)
                                                ,zip           : Acm.goodValue(addresses[i].zip)
                                                ,country       : Acm.goodValue(addresses[i].country)
                                                ,created       : Acm.getDateFromDatetime(addresses[i].created)
                                                ,creator       : Acm.goodValue(addresses[i].creator)
                                            });
                                        }
                                    }
                                }
                            }
                            return rc;
                        }
                        ,createAction: function(postData, jtParams) {
                            var rc = AcmEx.Object.jTableGetEmptyRecord();
                            var recordParent = $row.closest('tr').data('record');
                            if (recordParent && recordParent.assocId) {
                                var assocId = recordParent.assocId;
                                var record = Acm.urlToJson(postData);
                                rc.Record.assocId       = assocId;
                                rc.Record.type          = Acm.goodValue(record.type);
                                rc.Record.streetAddress = Acm.goodValue(record.streetAddress);
                                rc.Record.city          = Acm.goodValue(record.city);
                                rc.Record.state         = Acm.goodValue(record.state);
                                rc.Record.zip           = Acm.goodValue(record.zip);
                                rc.Record.country       = Acm.goodValue(record.country);
                                rc.Record.created       = Acm.getCurrentDay(); //record.created;
                                rc.Record.creator       = App.getUserName();   //record.creator;
                            }
                            return rc;
                        }
                        ,updateAction: function(postData, jtParams) {
                            var rc = AcmEx.Object.jTableGetEmptyRecord();
                            var recordParent = $row.closest('tr').data('record');
                            if (recordParent && recordParent.assocId) {
                                var assocId = recordParent.assocId;
                                var record = Acm.urlToJson(postData);
                                rc.Record.assocId       = assocId;
                                rc.Record.type          = Acm.goodValue(record.type);
                                rc.Record.streetAddress = Acm.goodValue(record.streetAddress);
                                rc.Record.city          = Acm.goodValue(record.city);
                                rc.Record.state         = Acm.goodValue(record.state);
                                rc.Record.zip           = Acm.goodValue(record.zip);
                                rc.Record.country       = Acm.goodValue(record.country);
                                rc.Record.created       = Acm.getCurrentDay(); //record.created;
                                rc.Record.creator       = App.getUserName();   //record.creator;
                            }
                            return rc;
                        }
                        ,deleteAction: function(postData, jtParams) {
                            return {"Result": "OK"};
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
                            ,options: CaseFile.Model.Lookup.getAddressTypes()
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
                        var record = data.record;
                        var address = {};
                        var assocId = record.assocId;
                        address.type          = Acm.goodValue(record.type);
                        address.streetAddress = Acm.goodValue(record.streetAddress);
                        address.city          = Acm.goodValue(record.city);
                        address.state         = Acm.goodValue(record.state);
                        address.zip           = Acm.goodValue(record.zip);
                        address.country       = Acm.goodValue(record.country);
                        var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                        if (0 < caseFileId && 0 < assocId) {
                            CaseFile.Controller.viewAddedAddress(caseFileId, assocId, address);
                        }
                    }
                    ,recordUpdated : function (event, data) {
                        var record = data.record;
                        var address = {};
                        var assocId = record.assocId;
                        address.id            = Acm.goodValue(record.id, 0);
                        address.type          = Acm.goodValue(record.type);
                        address.streetAddress = Acm.goodValue(record.streetAddress);
                        address.city          = Acm.goodValue(record.city);
                        address.state         = Acm.goodValue(record.state);
                        address.zip           = Acm.goodValue(record.zip);
                        address.country       = Acm.goodValue(record.country);
                        var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                        if (0 < caseFileId && 0 < assocId && 0 < address.id) {
                            CaseFile.Controller.viewUpdatedAddress(caseFileId, assocId, address);
                        }
                    }
                    ,recordDeleted : function (event, data) {
                        var record = data.record;
                        var assocId = record.assocId;
                        var addressId  = Acm.goodValue(record.id, 0);
                        var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                        if (0 < caseFileId && 0 < assocId && 0 < addressId) {
                            CaseFile.Controller.viewDeletedAddress(caseFileId, assocId, addressId);
                        }
                    }
                });
            }
        }

        ,Aliases: {
            create: function() {
                Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_ADDED_PERSON_ALIAS        ,this.onModelAddedPersonAlias);
                Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_UPDATED_PERSON_ALIAS      ,this.onModelUpdatedPersonAlias);
                Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_DELETED_PERSON_ALIAS      ,this.onModelDeletedPersonAlias);
            }
            ,onInitialized: function() {
            }

            ,onModelAddedPersonAlias: function(personAlias) {
                if (personAlias.hasError) {
                    //refresh child table??;
                }
            }
            ,onModelUpdatedPersonAlias: function(personAlias) {
                if (personAlias.hasError) {
                    //refresh child table??;
                }
            }
            ,onModelDeletedPersonAlias: function(personAliasId) {
                if (personAliasId.hasError) {
                    //refresh child table??;
                }
            }

            ,createLink: function($jt) {
                var $link = $("<a href='#' class='inline animated btn btn-default btn-xs' data-toggle='class:show' title='Aliases'><i class='fa fa-users'></i></a>");
                $link.click(function (e) {
                    AcmEx.Object.JTable.toggleChildTable($jt, $link, CaseFile.View.People.Aliases.onOpen, CaseFile.Model.Lookup.PERSON_SUBTABLE_TITLE_ALIASES);
                    e.preventDefault();
                });
                return $link;
            }
            ,onOpen: function($jt, $row) {
                AcmEx.Object.JTable.useAsChild($jt, $row, {
                    title: CaseFile.Model.Lookup.PERSON_SUBTABLE_TITLE_ALIASES
                    ,sorting: true
                    ,messages: {
                        addNewRecord: 'Add Alias'
                    }
                    ,actions: {
                        listAction: function (postData, jtParams) {
                            var rc = AcmEx.Object.jTableGetEmptyRecords();
                            var recordParent = $row.closest('tr').data('record');
                            if (recordParent && recordParent.assocId) {
                                var assocId = recordParent.assocId;

                                var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                                var c = CaseFile.Model.Detail.getCaseFile(caseFileId);
                                if (CaseFile.Model.Detail.validateData(c)) {
                                    var personAssociations = c.personAssociations;
                                    var personAssociation = CaseFile.Model.Detail.findPersonAssociation(assocId, personAssociations);
                                    if (CaseFile.Model.Detail.validatePersonAssociation(personAssociation)) {
                                        var personAliases = personAssociation.person.personAliases;
                                        for (var i = 0; i < personAliases.length; i++) {
                                            rc.Records.push({
                                                assocId  : assocId
                                                ,id      : Acm.goodValue(personAliases[i].id, 0)
                                                ,type    : Acm.goodValue(personAliases[i].aliasType)
                                                ,value   : Acm.goodValue(personAliases[i].aliasValue)
                                                ,created : Acm.getDateFromDatetime(personAliases[i].created)
                                                ,creator : Acm.goodValue(personAliases[i].creator)
                                            });
                                        }
                                    }
                                }
                            }
                            return rc;
                        }
                        ,createAction: function (postData, jtParams) {
                            return CaseFile.View.People._commonTypeValueRecord($row, postData);
                        }
                        ,updateAction: function (postData, jtParams) {
                            return CaseFile.View.People._commonTypeValueRecord($row, postData);
                        }
                        ,deleteAction: function (postData, jtParams) {
                            return {"Result": "OK"};
                        }
                    }
                    ,fields: {
                        assocId: {
                            type: 'hidden'
                            ,defaultValue: 0
                        }
                        , id: {
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
                            //,type: 'date'
                            //,displayFormat: 'yy-mm-dd'
                        }
                        ,creator: {
                            title: 'Added By'
                            ,width: '30%'
                            ,create: false
                            ,edit: false
                        }
                    }
                    ,recordAdded : function (event, data) {
                        var record = data.record;
                        var personAlias = {};
                        var assocId = record.assocId;
                        personAlias.aliasType  = Acm.goodValue(record.type);
                        personAlias.aliasValue = Acm.goodValue(record.value);
                        var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                        if (0 < caseFileId && 0 < assocId) {
                            CaseFile.Controller.viewAddedPersonAlias(caseFileId, assocId, personAlias);
                        }
                    }
                    ,recordUpdated : function (event, data) {
                        var record = data.record;
                        var personAlias = {};
                        var assocId = record.assocId;
                        personAlias.id         = Acm.goodValue(record.id, 0);
                        personAlias.aliasType  = Acm.goodValue(record.type);
                        personAlias.aliasValue = Acm.goodValue(record.value);
                        var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                        if (0 < caseFileId && 0 < assocId && 0 < personAlias.id) {
                            CaseFile.Controller.viewUpdatedPersonAlias(caseFileId, assocId, personAlias);
                        }
                    }
                    ,recordDeleted : function (event, data) {
                        var record = data.record;
                        var assocId = record.assocId;
                        var personAliasId = Acm.goodValue(record.id, 0);
                        var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                        if (0 < caseFileId && 0 < assocId && 0 < personAliasId) {
                            CaseFile.Controller.viewDeletedPersonAlias(caseFileId, assocId, personAliasId);
                        }
                    }
                });
            } //onOpen
        }
    }

    ,Documents: {
        create: function() {
            //for cases frevvo form is disabled in the properties file
            this.$formAddDocument = $("#formAddDocument");
            this.$btnAddDocument = $("#addDocument")
            this.$btnAddDocument.on("change", function(e) {CaseFile.View.Documents.onChangeFileInput(e, this);});
            this.$formAddDocument.submit(function(e) {CaseFile.View.Documents.onSubmitAddDocument(e, this);});

            this.$divDocuments    = $("#divDocs");
            this.createJTableDocuments(this.$divDocuments);
            AcmEx.Object.JTable.clickAddRecordHandler(this.$divDocuments, CaseFile.View.Documents.onClickSpanAddDocument);
            this.$spanAddDocument = this.$divDocuments.find(".jtable-toolbar-item-add-record");
            CaseFile.View.Documents.fillReportSelection();



            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_RETRIEVED_CASE_FILE     ,this.onModelRetrievedCaseFile);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_ADDED_DOCUMENT          ,this.onModelAddedDocument);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_CREATED_CORRESPONDENCE  ,this.onModelCreatedCorrespondence);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_SELECTED_CASE_FILE       ,this.onViewSelectedCaseFile);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CLOSED_CASE_FILE         ,this.onViewClosedCaseFile);


        }
        ,onInitialized: function() {
        }

        ,onChangeFileInput: function(event, ctrl) {
            CaseFile.View.Documents.$formAddDocument.submit();
        }
        ,onSubmitAddDocument: function(event, ctrl) {
            event.preventDefault();
            var count = CaseFile.View.Documents.$btnAddDocument[0].files.length;
            var report = CaseFile.View.Documents.getSelectReportText();

            var fd = new FormData();
            fd.append("uploadFileType", report);
            fd.append("caseFileId", CaseFile.Model.getCaseFileId());
            for(var i = 0; i < count; i++ ){
                fd.append("files[]", CaseFile.View.Documents.$btnAddDocument[0].files[i]);
            }
            CaseFile.Service.Documents.uploadDocument(fd);
            this.$formAddDocument[0].reset();
        }
        ,onModelAddedDocument: function(caseFileId) {
            if (caseFileId.hasError) {
                ;
            } else {
                AcmEx.Object.JTable.load(CaseFile.View.Documents.$divDocuments);
            }
        }
        ,onModelCreatedCorrespondence: function(caseFileId) {
            if (caseFileId.hasError) {
                ;
            } else {
                AcmEx.Object.JTable.load(CaseFile.View.Documents.$divDocuments);
            }
        }
        ,onModelRetrievedCaseFile: function(caseFile) {
            if (caseFile.hasError) {
                //empty table?
            } else {
                AcmEx.Object.JTable.load(CaseFile.View.Documents.$divDocuments);
            }
        }
        ,onViewSelectedCaseFile: function(caseFileId) {
            AcmEx.Object.JTable.load(CaseFile.View.Documents.$divDocuments);
        }
        ,onViewClosedCaseFile: function(caseFileId) {
            AcmEx.Object.JTable.load(CaseFile.View.Documents.$divDocuments);
        }
        ,onViewAddedDocument: function(caseFileId) {
            AcmEx.Object.JTable.load(CaseFile.View.Documents.$divDocuments);
        }


        ,onClickSpanAddDocument: function(event, ctrl) {
            var enableFrevvoFormEngine = CaseFile.View.MicroData.getFormUrls()['enable_frevvo_form_engine'];
            var report = CaseFile.View.Documents.getSelectReport();
            var reportext = CaseFile.View.Documents.getSelectReportText();

            if(report == "roi"){
                var token = CaseFile.View.MicroData.getToken();

                var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                var caseFile = CaseFile.Model.Detail.getCaseFile(caseFileId);
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
            else{
                CaseFile.View.Documents.$btnAddDocument.click();
            }
        }
/*html+= "<form id='formFiles' style='display:none;'>"
    + "<input id='newAttachment' type='file' name='files[]' multiple/>"
    + "</form>"*/

        ,fillReportSelection: function() {
            var html = "<span>"
                + "<select class='input-sm form-control input-s-sm inline v-middle' id='docDropDownValue'>"
                + "<option value='roi'>Report of Investigation</option>"
                + "<option value='mr'>Medical Release</option>"
                + "<option value='gr'>General Release</option>"
                + "<option value='ev'>eDelivery</option>"
                + "<option value='sig'>SF86 Signature</option>"
                + "<option value='ot'>Other</option>"
                + "</select>"
                + "</span>";


            this.$spanAddDocument.before(html);
        }
        ,getSelectReport: function() {
            return Acm.Object.getSelectValue(this.$spanAddDocument.prev().find("select"));
        }
        ,getSelectReportText: function() {
            return Acm.Object.getSelectedText(this.$spanAddDocument.prev().find("select"));
        }

        ,createJTableDocuments: function($s) {
            AcmEx.Object.JTable.useBasic($s, {
                title: 'Documents'
                ,paging: true   //fix me
                ,sorting: true  //fix me
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
                        var documents = CaseFile.Model.Documents.cacheDocuments.get(caseFileId);

                        if(Acm.isArray(documents)){
                            for (var i = 0; i < documents.length; i++) {
                                var childObject = documents[i];
                                if (Acm.compare(CaseFile.Model.DOCUMENT_TARGET_TYPE_FILE, childObject.targetType)) {
                                    var record = {};
                                    record.id = Acm.goodValue(childObject.id, 0);
                                    record.title = Acm.goodValue(childObject.name);
                                    record.created = Acm.getDateFromDatetime(childObject.created);
                                    record.creator = Acm.__FixMe__getUserFullName(Acm.goodValue(childObject.creator));
                                    record.status = Acm.goodValue(childObject.status);
                                    record.docType = Acm.goodValue(childObject.targetSubtype);
                                    rc.Records.push(record);
                                }
                            }
                            rc.TotalRecordCount = rc.Records.length;
                        }
                        /*var c = CaseFile.Model.Detail.getCaseFile(caseFileId);
                        if (c && Acm.isArray(c.childObjects)) {
                            for (var i = 0; i < c.childObjects.length; i++) {
                                var childObject = c.childObjects[i];
                                if (Acm.compare("FILE", childObject.targetType)) {
                                    var record = {};
                                    record.id = Acm.goodValue(childObject.targetId, 0);
                                    record.title = Acm.goodValue(childObject.targetName);
                                    record.created = Acm.getDateFromDatetime(childObject.created);
                                    record.creator = Acm.goodValue(childObject.creator);
                                    record.status = Acm.goodValue(childObject.status);
                                    rc.Records.push(record);
                                }
                            }
                            rc.TotalRecordCount = rc.Records.length;
                        }*/
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
//not tested, disabled
//                    ,updateAction: function(postData, jtParams) {
//                        var record = Acm.urlToJson(postData);
//                        var rc = AcmEx.Object.JTable.getEmptyRecord();
//                        //id,created,creator is readonly
//                        //rc.Record.id = record.id;
//                        //rc.Record.created = record.created;
//                        //rc.Record.creator = record.creator;
//                        rc.Record.title = record.title;
//                        rc.Record.status = record.status;
//                        return rc;
//                    }
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
                        ,width: '30%'
                        ,display: function (commData) {
                            var a = "<a href='" + App.getContextPath() + CaseFile.Service.Documents.API_DOWNLOAD_DOCUMENT_
                                + ((0 >= commData.record.id)? "#" : commData.record.id)
                                + "'>" + commData.record.title + "</a>";
                            return $(a);
                        }
                    }
                    ,docType: {
                        title: 'Document Type'
                        ,width: '15%'
                        ,edit: false
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
                        ,width: '10%'
                    }
                }
                ,recordUpdated : function (event, data) {
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var record = data.record;
                    var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                    var c = CaseFile.Model.Detail.getCaseFile(caseFileId);
                    if (c && Acm.isArray(c.childObjects)) {
                        var childObject = {};
                        childObject.targetId = record.id;
                        childObject.title = record.title;
                        childObject.status = record.status;
                        //childObject.parentId = record.parentId;
                        CaseFile.Controller.viewChangedChildObject(caseFileId, childObject);
                    }
                }
            });
        }
    }

    ,Participants: {
        create: function() {
            this.$divParticipants    = $("#divParticipants");
            this.createJTableParticipants(this.$divParticipants);

            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_RETRIEVED_CASE_FILE    ,this.onModelRetrievedCaseFile);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_SAVED_ASSIGNEE         ,this.onModelSavedAssignee);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_SELECTED_CASE_FILE      ,this.onViewSelectedCaseFile);
        }
        ,onInitialized: function() {
        }

        ,onModelRetrievedCaseFile: function(caseFile) {
            if (caseFile.hasError) {
                //empty table?
            } else {
                AcmEx.Object.JTable.load(CaseFile.View.Participants.$divParticipants);
            }
        }
        ,onModelSavedAssignee: function(caseFileId, assginee) {
            if (!assginee.hasError) {
                AcmEx.Object.JTable.load(CaseFile.View.Participants.$divParticipants);
            }
        }
        ,onViewSelectedCaseFile: function(caseFileId) {
            AcmEx.Object.JTable.load(CaseFile.View.Participants.$divParticipants);
        }

        ,createJTableParticipants: function($s) {
            AcmEx.Object.JTable.useBasic($s, {
                title: 'Participants'
                ,paging: true   //fix me
                ,sorting: true  //fix me
                ,messages: {
                    addNewRecord: 'Add Participant'
                }
                ,actions: {
                    listAction: function(postData, jtParams) {
                        var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                        if (0 >= caseFileId) {
                            return AcmEx.Object.JTable.getEmptyRecords();
                        }

                        var rc = AcmEx.Object.JTable.getEmptyRecords();
                        var c = CaseFile.Model.Detail.getCaseFile(caseFileId);
                        if (c && Acm.isArray(c.participants)) {
                            for (var i = 0; i < c.participants.length; i++) {
                                var participant = c.participants[i];
                                var record = {};
                                record.id = Acm.goodValue(participant.id, 0);
                                record.title = Acm.__FixMe__getUserFullName(Acm.goodValue(participant.participantLdapId));
                                record.type = Acm.goodValue(participant.participantType);
                                rc.Records.push(record);
                            }
                            rc.TotalRecordCount = rc.Records.length;
                        }
                        return rc;
                    }
                    ,createAction: function(postData, jtParams) {
                        var record = Acm.urlToJson(postData);
                        var rc = AcmEx.Object.JTable.getEmptyRecord();
                        var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                        var caseFile = CaseFile.Model.Detail.getCaseFile(caseFileId);
                        if (caseFile) {
                            rc.Record.title = record.title;
                            rc.Record.type = record.type;
                        }
                        return rc;
                    }
                    ,updateAction: function(postData, jtParams) {
                        var record = Acm.urlToJson(postData);
                        var rc = AcmEx.Object.JTable.getEmptyRecord();
                        var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                        var caseFile = CaseFile.Model.Detail.getCaseFile(caseFileId);
                        if (caseFile) {
                            rc.Record.title = record.title;
                            rc.Record.type = record.type;
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
                    }
                    ,title: {
                        title: 'Name'
                        ,width: '70%'
                    }
                    ,type: {
                        title: 'Type'
                        ,width: '30%'
                    }
                }
                ,recordAdded : function (event, data) {
                    var record = data.record;
                    var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                    if (0 < caseFileId) {
                        var participant = {};
                        participant.participantLdapId = record.title;
                        participant.participantType = record.type;
                        CaseFile.Controller.viewAddedParticipant(caseFileId, participant);
                    }
                }
                ,recordUpdated : function (event, data) {
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var record = data.record;
                    var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                    var c = CaseFile.Model.Detail.getCaseFile(caseFileId);
                    if (c && Acm.isArray(c.participants)) {
                        if (0 < c.participants.length && whichRow < c.participants.length) {
                            var participant = c.participants[whichRow];
                            participant.participantLdapId = record.title;
                            participant.participantType = record.type;
                            CaseFile.Controller.viewUpdatedParticipant(caseFileId, participant);
                        }
                    }
                }
                ,recordDeleted : function (event, data) {
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var record = data.record;
                    var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                    var c = CaseFile.Model.Detail.getCaseFile(caseFileId);
                    if (c && Acm.isArray(c.participants)) {
                        if (0 < c.participants.length && whichRow < c.participants.length) {
                            var participant = c.participants[whichRow];
                            CaseFile.Controller.viewDeletedParticipant(caseFileId, participant.id);
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

            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_RETRIEVED_CASE_FILE    ,this.onModelRetrievedCaseFile);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_ADDED_NOTE             ,this.onModelAdddNote);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_UPDATED_NOTE           ,this.onModelUpdatedNote);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_DELETED_NOTE           ,this.onModelDeletedNote);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_SELECTED_CASE_FILE      ,this.onViewSelectedCaseFile);
        }
        ,onInitialized: function() {
        }

//
        ,onModelRetrievedCaseFile: function(caseFile) {
            if (caseFile.hasError) {
                //empty table?
            } else {
                AcmEx.Object.JTable.load(CaseFile.View.Notes.$divNotes);
            }
        }
        ,onModelAdddNote: function(caseFile) {
            if (caseFile.hasError) {
                //show error
            } else {
                AcmEx.Object.JTable.load(CaseFile.View.Notes.$divNotes);
            }
        }
        ,onModelUpdatedNote: function(caseFile) {
            if (caseFile.hasError) {
                //show error
            } else {
                AcmEx.Object.JTable.load(CaseFile.View.Notes.$divNotes);
            }
        }
        ,onModelDeletedNote: function(noteId) {
            if (noteId.hasError) {
                //show error
            } else {
                AcmEx.Object.JTable.load(CaseFile.View.Notes.$divNotes);
            }
        }
        ,onViewSelectedCaseFile: function(caseFileId) {
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
                    Record.creator    = Acm.__FixMe__getUserFullName(Acm.goodValue(noteList[i].creator));
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
                            var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                            var caseFile = CaseFile.Model.Detail.getCaseFile(caseFileId);
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
                            var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                            var caseFile = CaseFile.Model.Detail.getCaseFile(caseFileId);
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
                        var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                        if (0 < caseFileId) {
                            var noteToSave = {};
                            //noteToSave.id = record.id;
                            noteToSave.id = 0;
                            noteToSave.note = record.note;
                            noteToSave.created = Acm.getCurrentDayInternal(); //record.created;
                            noteToSave.creator = record.creator;   //record.creator;
                            noteToSave.parentId = caseFileId;
                            noteToSave.parentType = CaseFile.Model.getObjectType();
                            //CaseFile.Service.Notes.saveNote(noteToSave);
                            CaseFile.Controller.viewAddedNote(noteToSave);
                        }
                    }
                    ,recordUpdated: function(event,data){
                        var whichRow = data.row.prevAll("tr").length;
                        var record = data.record;
                        var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                        if (0 < caseFileId) {
                            var notes = CaseFile.Model.Notes.cacheNoteList.get(caseFileId);
                            if (notes) {
                                if(notes[whichRow]){
                                    notes[whichRow].note = record.note;
                                    //CaseFile.Service.Notes.saveNote(notes[whichRow]);
                                    CaseFile.Controller.viewUpdatedNote(notes[whichRow]);
                                }
                            }
                        }
                    }
                    ,recordDeleted : function (event, data) {
                        var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                        var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                        if (0 < caseFileId) {
                            var notes = CaseFile.Model.Notes.cacheNoteList.get(caseFileId);
                            if (notes) {
                                if(notes[whichRow]){
                                    //CaseFile.Service.Notes.deleteNote(notes[whichRow].id);
                                    CaseFile.Controller.viewDeletedNote(notes[whichRow].id);
                                }
                            }
                        }
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

            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_RETRIEVED_TASKS    ,this.onModelRetrievedTasks);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_RETRIEVED_CASE_FILE    ,this.onModelRetrievedCaseFile);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_SELECTED_CASE_FILE      ,this.onViewSelectedCaseFile);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_COMPLETED_TASK      ,this.onModelCompletedTask);

        }
        ,onInitialized: function() {
        }

        ,URL_TASK_DETAIL:  "/plugin/task/"
        ,URL_NEW_TASK_:    "/plugin/task/wizard?parentType=CASE_FILE&reference="


        ,onModelRetrievedCaseFile: function(caseFile) {
            if (caseFile.hasError) {
                //empty table?
            } else {
                AcmEx.Object.JTable.load(CaseFile.View.Tasks.$divTasks);
            }
        }
        ,onViewSelectedCaseFile: function(caseFileId) {
            AcmEx.Object.JTable.load(CaseFile.View.Tasks.$divTasks);
        }
        ,onModelRetrievedTasks: function(tasks) {
            if (tasks.hasError) {
                //empty table?
            } else {
                AcmEx.Object.JTable.load(CaseFile.View.Tasks.$divTasks);
            }
        }
        ,onModelCompletedTask: function(task) {
            if (task.hasError) {
                //empty table?
            } else {
                AcmEx.Object.JTable.load(CaseFile.View.Tasks.$divTasks);
            }
        }
        ,onClickSpanAddTask: function(event, ctrl) {
            var caseFileId = CaseFile.View.Tree.getActiveCaseId();
            var caseFile = CaseFile.Model.Detail.getCaseFile(caseFileId);
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
        ,onClickBtnCompleteTask : function(taskId) {
           // alert("adhoc task");

            CaseFile.Service.Tasks.completeTask(taskId);
        }
        ,onClickBtnTaskWithOutcome : function(outcome,taskId) {
            var tasks = CaseFile.Model.Tasks.cacheTasks.get(0);
            var task = {};
            for(var i = 0; i < tasks.length; i++) {
                if (tasks[i].taskId == taskId) {
                    task = tasks[i];
                }
            }
            for(var i = 0; i < task.availableOutcomes.length; i++){
                var availableOutcome = task.availableOutcomes[i];
                if(availableOutcome.name == outcome) {
                    task.taskOutcome = availableOutcome;
                }
            }
            if(task.taskOutcome){
                CaseFile.Service.Tasks.completeTaskWithOutcome(task);
            }

            //alert("task with outcome");

        }
        ,retrieveTaskOutcome : function(taskId){
            var tasks = CaseFile.Model.Tasks.cacheTasks.get(0);
            var $a = $("");
            if(tasks){
                for(var i = 0; i < tasks.length; i++){
                    if(tasks[i].taskId == taskId){
                        var task = tasks[i];
                        if(task.adhocTask == true && task.completed == false){
                            $a = $("<div class='btn-group-task'><button class='btn btn-default btn-sm adhoc' title='Complete Task'>Complete</button></div>");
                        }
                        else if(task.adhocTask == false && task.completed == false && task.availableOutcomes != null){
                             var availableOutcomes = task.availableOutcomes;
                             for(var j = 0; j < availableOutcomes.length; j++ ){
                                 if(availableOutcomes[j].name == 'COMPLETE'){
                                 $a = $("<div class='btn-group-task'><button class='btn btn-default btn-sm businessProcess' id='COMPLETE' title='Complete Task Outcome'>Complete</button></div>");
                                 }
                             }
                        }
                    }
                }
            }
            return $a;
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
                    Record.assignee = Acm.__FixMe__getUserFullName(taskList[i].assignee);
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
                    ,multiselect: false
                    ,selecting: false
                    ,selectingCheckboxes: false
                    ,paging: true   //fix me
                    ,sorting: true  //fix me
                    ,messages: {
                        addNewRecord: 'Add Task'
                    }
                    ,actions: {
                        pagingListAction: function (postData, jtParams, sortMap) {
                            var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                            if (0 >= caseFileId) {
                                return AcmEx.Object.JTable.getEmptyRecords();
                            }

                            var taskList = CaseFile.Model.Tasks.cacheTaskSolr.get(caseFileId);
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
                            ,display: function (commData) {
                                var a = "<a href='" + App.getContextPath() + '/plugin/task/' +
                                    + ((0 >= commData.record.id)? "#" : commData.record.id)
                                    + "'>" + commData.record.id + "</a>";
                                return $(a);
                            }
                        }
                        ,title: {
                            title: 'Title'
                            ,width: '30%'
                            //,sorting: false
                            ,display: function (commData) {
                                var a = "<a href='" + App.getContextPath() + '/plugin/task/' +
                                    + ((0 >= commData.record.id)? "#" : commData.record.id)
                                    + "'>" + commData.record.title + "</a>";
                                return $(a);
                            }
                        }
                        ,assignee: {
                            title: 'Assignee'
                            ,sorting: false
                            ,width: '25'
                        }
                        ,created: {
                            title: 'Created'
                            ,width: '15%'
                            //,sorting: false
                        }
                        ,priority: {
                            title: 'Priority'
                            ,width: '10%'
                            //,sorting: false
                        }
                        ,dueDate: {
                            title: 'Due'
                            ,width: '15%'
                            //,sorting: true
                        }
                        ,status: {
                            title: 'Status'
                            ,width: '10%'
                            //,sorting: false
                        }
                        ,description: {
                            title: 'Action'
                            ,width: '10%'
                            //,sorting: false
                            ,edit: false
                            ,create: false
                            ,display: function (commData) {
                                var $a = CaseFile.View.Tasks.retrieveTaskOutcome(commData.record.id);
                                $a.on("click", ".businessProcess", function(e) {CaseFile.View.Tasks.onClickBtnTaskWithOutcome(e.target.id,commData.record.id);$a.hide();});
                                $a.on("click", ".adhoc", function(e) {CaseFile.View.Tasks.onClickBtnCompleteTask(commData.record.id);$a.hide();});
                                return $a;
                            }
                        }
                    } //end field
                } //end arg
                ,sortMap
            );
        }
    }

    ,References: {
        create: function() {
            this.$divReferences          = $("#divRefs");
            this.createJTableReferences(this.$divReferences);

            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_RETRIEVED_CASE_FILE    ,this.onModelRetrievedCaseFile);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_ADDED_DOCUMENT         ,this.onModelAddedDocument);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_CREATED_CORRESPONDENCE ,this.onModelCreatedCorrespondence);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_SELECTED_CASE_FILE      ,this.onViewCaseFileSelected);
        }
        ,onInitialized: function() {
        }

        ,onModelRetrievedCaseFile: function(caseFile) {
            if (caseFile.hasError) {
                ;
            } else {
                AcmEx.Object.JTable.load(CaseFile.View.References.$divReferences);
            }
        }
        ,onModelAddedDocument: function(caseFileId) {
            if (caseFileId.hasError) {
                ;
            } else {
                AcmEx.Object.JTable.load(CaseFile.View.References.$divReferences);
            }
        }
        ,onModelCreatedCorrespondence: function(caseFileId) {
            if (caseFileId.hasError) {
                ;
            } else {
                AcmEx.Object.JTable.load(CaseFile.View.References.$divReferences);
            }
        }
        ,onViewCaseFileSelected: function(caseFileId) {
            AcmEx.Object.JTable.load(CaseFile.View.References.$divReferences);
        }

        ,createJTableReferences: function($jt) {
            //var sortMap = {};
            //sortMap["modified"] = "modified";

            AcmEx.Object.JTable.useBasic($jt, {
                    title: 'References'
                    ,paging: true   //fix me
                    ,sorting: true  //fix me
                    ,messages: {
                        addNewRecord: 'Add Reference'
                    }
                    ,actions: {
                        listAction: function(postData, jtParams) {
                            var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                            if (0 >= caseFileId) {
                                return AcmEx.Object.JTable.getEmptyRecords();
                            }

                            var rc = AcmEx.Object.JTable.getEmptyRecords();
                            var c = CaseFile.Model.Detail.getCaseFile(caseFileId);
                            if (c && Acm.isArray(c.references)) {
                                for (var i = 0; i < c.references.length; i++) {
                                    var reference = c.references[i];
                                    var record = {};
                                    record.id = Acm.goodValue(reference.targetId, 0);
                                    record.title = Acm.goodValue(reference.targetName);
                                    record.modified = Acm.getDateFromDatetime(reference.modified);
                                    record.type = Acm.goodValue(reference.targetType);
                                    record.status = Acm.goodValue(reference.status);
                                    rc.Records.push(record);
                                }
                                rc.TotalRecordCount = rc.Records.length;
                            }
                            return rc;
                        }
//unsure the requirement, add/update/delete not implemented
//                        ,createAction: function(postData, jtParams) {
//                            var record = Acm.urlToJson(postData);
//                            var rc = AcmEx.Object.JTable.getEmptyRecord();
//
//                            return rc;
//                        }
//                        ,deleteAction: function(postData, jtParams) {
//                            return {
//                                "Result": "OK"
//                            };
//                        }
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
                        ,title: {
                            title: 'Title'
                            ,width: '30%'
                            ,edit: true
                            ,create: false
                            ,display: function(data) {
                                var url = App.getContextPath() + '/plugin/casefile/' + data.record.id;
                                var $lnk = $("<a href='" + url + "'>" + data.record.title + "</a>");
                                return $lnk;
                            }
                        }
                        ,modified: {
                            title: 'Modified'
                            ,width: '14%'
                            ,edit: false
                            ,create: false
                        }
                        ,type: {
                            title: 'Reference Type'
                            ,width: '14%'
                            ,edit: false
                            ,create: false
                        }
                        ,status: {
                            title: 'Status'
                            ,width: '14%'
                            ,edit: false
                            ,create: false
                        }
                    } //end field
                    ,recordAdded : function (event, data) {
                        var record = data.record;
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
//                ,sortMap
            );
        }
    }

    ,Events: {
        create: function() {
            this.$divEvents          = $("#divEvents");
            this.createJTableEvents(this.$divEvents);

            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_RETRIEVED_CASE_FILE    ,this.onModelRetrievedCaseFile);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_SELECTED_CASE_FILE      ,this.onViewSelectedCaseFile);
        }
        ,onInitialized: function() {
        }

        ,onModelRetrievedCaseFile: function(caseFile) {
            if (caseFile.hasError) {
                //empty table?
            } else {
                AcmEx.Object.JTable.load(CaseFile.View.Events.$divEvents);
            }
        }
        ,onViewSelectedCaseFile: function(caseFileId) {
            AcmEx.Object.JTable.load(CaseFile.View.Events.$divEvents);
        }

        ,_makeJtData: function(eventList) {
            var jtData = AcmEx.Object.JTable.getEmptyRecords();
//            if (eventList) {
//                for (var i = 0; i < eventList.length; i++) {
//                    var Record = {};
//                    Record.id         = Acm.goodValue(eventList[i].id, 0);
//                    Record.note       = Acm.goodValue(eventList[i].note);
//                    Record.created    = Acm.getDateFromDatetime(eventList[i].created);
//                    Record.creator    = Acm.goodValue(eventList[i].creator);
//                    //Record.parentId   = Acm.goodValue(eventList[i].parentId);
//                    //Record.parentType = Acm.goodValue(eventList[i].parentType);
//                    jtData.Records.push(Record);
//                }
//                jtData.TotalRecordCount = eventList.length;
//            }
            return jtData;
        }
        ,createJTableEvents: function($jt) {
            var sortMap = {};
            sortMap["created"] = "created";

            AcmEx.Object.JTable.usePaging($jt
                ,{
                    title: 'Events'
                    ,selecting: true
                    ,multiselect: false
                    ,selectingCheckboxes: false
                    ,messages: {
                        addNewRecord: 'Add Event'
                    }
                    ,actions: {
                        pagingListAction: function (postData, jtParams, sortMap) {
                            return AcmEx.Object.JTable.getEmptyRecords();

//                            var caseFileId = CaseFile.View.Tree.getActiveCaseId();
//                            if (0 >= caseFileId) {
//                                return AcmEx.Object.JTable.getEmptyRecords();
//                            }
//
//                            var eventList = CaseFile.Model.Events.cacheEventList.get(caseFileId);
//                            if (eventList) {
//                                return CaseFile.View.Events._makeJtData(eventList);
//
//                            } else {
//                                return CaseFile.Service.Events.retrieveEventListDeferred(caseFileId
//                                    ,postData
//                                    ,jtParams
//                                    ,sortMap
//                                    ,function(data) {
//                                        var eventList = data;
//                                        return CaseFile.View.Events._makeJtData(eventList);
//                                    }
//                                    ,function(error) {
//                                    }
//                                );
//                            }  //end else
                        }
                    }
                    , fields: {
                        id: {
                            title: 'ID'
                            ,key: true
                            ,list: false
                            ,create: false
                            ,edit: false
                        }, eventType: {
                            title: 'Event Name'
                            ,width: '50%'
                        }, eventDate: {
                            title: 'Date'
                            ,width: '25%'
                        }, userId: {
                            title: 'User'
                            ,width: '25%'
                        }
                    } //end field
                } //end arg
                ,sortMap
            );
        }
    }



    ,Correspondence: {
        create: function () {
            this.$divTemplates = $("#divTemplates");
            this.createJTableCorrespondence(this.$divTemplates);

            AcmEx.Object.JTable.clickAddRecordHandler(this.$divTemplates, CaseFile.View.Correspondence.onClickSpanAddDocument);
            this.$spanAddTemplate = this.$divTemplates.find(".jtable-toolbar-item-add-record");
            CaseFile.View.Correspondence.fillReportSelection();

            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_RETRIEVED_CASE_FILE     ,this.onModelRetrievedCaseFile);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_CREATED_CORRESPONDENCE  ,this.onModelCreatedCorrespondence);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_SELECTED_CASE_FILE       ,this.onViewSelectedCaseFile);
        }
        , onInitialized: function () {
        }
        , onModelRetrievedCaseFile: function (caseFile) {
            if (caseFile.hasError) {
                ;
            } else {
                AcmEx.Object.JTable.load(CaseFile.View.Correspondence.$divTemplates);
            }
        }
        , onViewSelectedCaseFile: function (caseFileId) {
            AcmEx.Object.JTable.load(CaseFile.View.Correspondence.$divTemplates);
        }
        ,onModelCreatedCorrespondence: function(caseFileId) {
            if (caseFileId.hasError) {
                ;
            } else {
                AcmEx.Object.JTable.load(CaseFile.View.Correspondence.$divTemplates);
            }
        }

        ,getSelectTemplate: function() {
            return Acm.Object.getSelectValue(this.$spanAddTemplate.prev().find("select"));
        }
        ,onClickSpanAddDocument: function(event, ctrl) {
            var caseFileId = CaseFile.View.Tree.getActiveCaseId();
            var templateName = CaseFile.View.Correspondence.getSelectTemplate();
            CaseFile.Controller.viewClickedAddCorrespondence(caseFileId, templateName);
        }

        ,fillReportSelection: function() {
            var html = "<span>"
                + "<select class='input-sm form-control input-s-sm inline v-middle' id='docDropDownValue'>"
//                + "<option value='GR'>General Release</option>"
//                + "<option value='MR'>Medical Release</option>"
//                + "<option value='CG'>Clearance Granted</option>"
//                + "<option value='CD'>Clearance Denied</option>"
                + "<option value='GeneralRelease.docx'>General Release</option>"
                + "<option value='MedicalRelease.docx'>Medical Release</option>"
                + "<option value='ClearanceGranted.docx'>Clearance Granted</option>"
                + "<option value='ClearanceDenied.docx'>Clearance Denied</option>"
                + "</select>"
                + "</span>"
                ;


            this.$spanAddTemplate.before(html);
        }

        , createJTableCorrespondence: function ($s) {
            AcmEx.Object.JTable.useBasic($s, {
                title: 'Correspondence'
                ,paging: true   //fix me
                ,sorting: true  //fix me
                , messages: {
                    addNewRecord: 'Add Correspondence'
                }
                , actions: {
                    listAction: function (postData, jtParams) {
                        var caseFileId = CaseFile.View.Tree.getActiveCaseId();
                        if (0 >= caseFileId) {
                            return AcmEx.Object.JTable.getEmptyRecords();
                        }

                        var rc = AcmEx.Object.JTable.getEmptyRecords();
                        var documents = CaseFile.Model.Documents.cacheDocuments.get(caseFileId);
                        if(Acm.isArray(documents)){
                            for (var i = 0; i < documents.length; i++) {
                                var childObject = documents[i];
                                if (Acm.compare(CaseFile.Model.DOCUMENT_TARGET_TYPE_FILE, childObject.targetType)) {
                                    var record = {};
                                    if (Acm.compare(CaseFile.Model.DOCUMENT_CATEGORY_CORRESPONDENCE, childObject.category)) {
                                        record.id = Acm.goodValue(childObject.id, 0);
                                        record.title = Acm.goodValue(childObject.name);
                                        record.created = Acm.getDateFromDatetime(childObject.created);
                                        record.creator = Acm.goodValue(childObject.creator);
                                        //record.status = Acm.goodValue(childObject.status);
                                        rc.Records.push(record);
                                    }
                                }
                            }
                            rc.TotalRecordCount = rc.Records.length;
                        }
                        return rc;

                    }
                    ,createAction: function(postData, jtParams) {
                        //placeholder. this action should never be called
                        var rc = {"Result": "OK", "Record": {id:0, title:"", created:"", creator:""}};
                        return rc;
                    }
                }
                , fields: {
                    id: {
                        title: 'ID'
                        , key: true
                        , list: false
                        , create: false
                        , edit: false
                        , defaultvalue: 0
                    }
                    , title: {
                        title: 'Title'
                        , width: '50%'
                        , edit: false
                        , create: false
                        ,display: function (commData) {
                            var a = "<a href='" + App.getContextPath() + CaseFile.Service.Documents.API_DOWNLOAD_DOCUMENT_
                                + ((0 >= commData.record.id)? "#" : commData.record.id)
                                + "'>" + commData.record.title + "</a>";
                            return $(a);
                        }
                    }
                    , created: {
                        title: 'Created'
                        , width: '15%'
                        , edit: false
                        , create: false
                    }
                    , creator: {
                        title: 'Creator'
                        , width: '15%'
                        , edit: false
                        , create: false
                    }
                }
            });
        }
    }
};

