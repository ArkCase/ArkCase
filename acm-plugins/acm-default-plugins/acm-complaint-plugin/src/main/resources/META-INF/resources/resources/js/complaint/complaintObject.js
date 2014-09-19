/**
 * Complaint.Object
 *
 * manages screen objects
 *
 * @author jwu
 */
Complaint.Object = {
    initialize : function() {
        var ti = this.getTreeInfo();
        var tiApp = App.getComplaintTreeInfo();
        if (tiApp) {
            ti.initKey = tiApp.initKey;
            ti.start = tiApp.start;
            ti.n = tiApp.n;
            ti.s = tiApp.s;
            ti.q = tiApp.q;
            ti.complaintId = tiApp.complaintId;
            App.setComplaintTreeInfo(null);
        }

        var items = $(document).items();
        var complaintId = items.properties("complaintId").itemValue();
        if (Acm.isNotEmpty(complaintId)) {
            ti.complaintId = complaintId;
        }
        var token = items.properties("token").itemValue();
        this.setToken(token);

        this.$lnkTitle          = $("#caseTitle");
        this.$lnkTitle.editable({placement: 'bottom'
            ,emptytext: "Unknown"
            ,success: function(response, newValue) {
                Complaint.Event.onSaveTitle(newValue);
            }
        });
        this.$lnkComplaintNum   = $("#complaintNum");

        this.$lnkIncident       = $("#incident");
        this.$lnkIncident.editable({placement: 'bottom'
            ,emptytext: "Unknown"
            ,format: 'mm/dd/yyyy'
            ,viewformat: 'mm/dd/yyyy'
            ,datepicker: {
                weekStart: 1
            }
            ,success: function(response, newValue) {
                Complaint.Event.onSaveIncidentDate(newValue);
            }
        });
        this.$lnkPriority       = $("#priority");
        this.$lnkAssigned       = $("#assigned");
        this.$lnkComplaintType  = $("#type");
        this.$lnkStatus         = $("#status");

        this.$divDetails        = $(".complaintDetails");
        this.$btnEditDetails    = $("#tabDetail button:eq(0)");
        this.$btnSaveDetails    = $("#tabDetail button:eq(1)");
        this.$btnEditDetails.on("click", function(e) {Complaint.Event.onClickBtnEditDetails(e);});
        this.$btnSaveDetails.on("click", function(e) {Complaint.Event.onClickBtnSaveDetails(e);});

        this.$divInitiator      = $("#divInitiator");
        Complaint.JTable.createJTableInitiator(this.$divInitiator);

        this.$divPeople         = $("#divPeople");
        Complaint.JTable.createJTablePeople(this.$divPeople);

        this.$divDocuments      = $("#divDocuments");
        Complaint.JTable.createJTableDocuments(this.$divDocuments);
//        this.$spanAddDocument   = this.$divDocuments.find(".jtable-toolbar-item-add-record");
//        this.$spanAddDocument.unbind("click").on("click", function(e){Complaint.Event.onClickSpanAddDocument(e);});
//        //Complaint.Page.fillReportSelection();

        this.$divTasks          = $("#divTasks");
        Complaint.JTable.createJTableTasks(this.$divTasks);
        this.$spanAddTask       = this.$divTasks.find(".jtable-toolbar-item-add-record");
        this.$spanAddTask.unbind("click").on("click", function(e){Complaint.Event.onClickSpanAddTask(e);});

        this.$tree = $("#tree");
        this._useFancyTree(this.$tree);
    }

    ,_token: ""
    ,getToken: function() {
        return this._token;
    }
    ,setToken: function(token) {
        this._token = token;
    }

    ,beforeSpanAddDocument: function(html) {
        this.$spanAddDocument.before(html);
    }
    ,getSelectReport: function() {
        return Acm.Object.getSelectValue(this.$spanAddDocument.prev().find("select"));
    }
    ,showTab: function(key) {
        var tabIds = ["tabBlank"
            ,"tabDetail"
            ,"tabInitiator"
            ,"tabPeople"
            ,"tabNotes"
            ,"tabDocuments"
            ,"tabTasks"
            ,"tabRefComplaints"
            ,"tabRefCases"
            ,"tabRefTasks"
            ,"tabRefDocuments"
            ,"tabApprovers"
            ,"tabCollaborators"
            ,"tabWatchers"
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


    ,initAssignee: function(data) {
        var choices = []; //[{value: "", text: "Choose Assignee"}];
        $.each(data, function(idx, val) {
            var opt = {};
            opt.value = val.userId;
            opt.text = val.fullName;
            choices.push(opt);
        });

        this.$lnkAssigned.editable({placement: 'bottom'
            ,emptytext: "Unknown"
            ,value: ""
            ,source: choices
            ,success: function(response, newValue) {
                Complaint.Event.onSaveAssigned(newValue);
            }
        });
    }
    ,initComplaintType: function(data) {
        var choices = []; //[{value: "", text: "Choose Type"}];
        $.each(data, function(idx, val) {
            var opt = {};
            opt.value = val;
            opt.text = val;
            choices.push(opt);
        });

        this.$lnkComplaintType.editable({placement: 'bottom'
            ,emptytext: "Unknown"
            ,value: ""
            ,source: choices
            ,success: function(response, newValue) {
                Complaint.Event.onSaveComplaintType(newValue);
            }
        });
    }
    ,initPriority: function(data) {
        var choices = []; //[{value: "", text: "Choose Priority"}];
        $.each(data, function(idx, val) {
            var opt = {};
            opt.value = val;
            opt.text = val;
            choices.push(opt);
        });

        this.$lnkPriority.editable({placement: 'bottom'
            ,emptytext: "Unknown"
            ,value: ""
            ,source: choices
            ,success: function(response, newValue) {
                Complaint.Event.onSavePriority(newValue);
            }
        });
    }
    ,setValueLnkTitle: function(txt) {
        this.$lnkTitle.editable("setValue", txt);
    }
    ,setValueLnkComplaintNum: function(txt) {
        Acm.Object.setText(this.$lnkComplaintNum, txt);
    }
    ,setValueLnkIncident: function(txt) {
        this.$lnkIncident.editable("setValue", txt, true);   //true - convert txt to internal format (Date Object)
    }
    ,setValueLnkPriority: function(txt) {
        this.$lnkPriority.editable("setValue", txt);
    }
    ,setValueLnkAssigned: function(txt) {
        this.$lnkAssigned.editable("setValue", txt);
    }
    ,setValueLnkComplaintType: function(txt) {
        this.$lnkComplaintType.editable("setValue", txt);
    }
    ,setTextLnkStatus: function(txt) {
        Acm.Object.setText(this.$lnkStatus, txt);
    }
    ,getHtmlDivDetails: function() {
        //return AcmEx.Object.getSummernote(this.$divDetails);
        return Acm.Object.getHtml(this.$divDetails);
    }
    ,setHtmlDivDetails: function(html) {
        //AcmEx.Object.setSummerNote(this.$divDetails, html);
        Acm.Object.setHtml(this.$divDetails, html);
    }
    ,editDivDetails: function() {
        AcmEx.Object.editSummerNote(this.$divDetails);
    }
    ,saveDivDetails: function() {
        return AcmEx.Object.saveSummerNote(this.$divDetails);
    }

    ,populateComplaint: function(c) {
        this.setValueLnkTitle(c.complaintTitle);
        //this.setTextH4TitleHeader(" (" + c.complaintNumber + ")");
        this.setValueLnkComplaintNum(c.complaintNumber);
        this.setValueLnkIncident(Acm.getDateFromDatetime(c.created));
        this.setValueLnkPriority(c.priority);
        this.setValueLnkAssigned(c.assignee);
        this.setValueLnkComplaintType(c.complaintType);
        this.setTextLnkStatus(c.status);

        this.setHtmlDivDetails(c.details);

        this.refreshJTableInitiator();
        this.refreshJTableDocuments();
        this.refreshJTableTasks();
    }



    //
    //------------------ Tree of Complaints ------------------
    //
    ,_treeInfo: {
        start           : 0
        ,n              : 10
        ,total          : -1
        ,s              : null
        ,q              : null
        ,initKey        : null
        ,complaintId    : 0
    }
    ,getTreeInfo: function() {
        return this._treeInfo;
    }
//    ,setTreeInfo: function(ti) {
//        this._treeInfo = ti;
//    }

    //
    //tabIds            - nodeType - key
    //------------------------------------------
    //tabBlank          - prevPage - prevPage
    //tabBlank          - p        - [pageId]
    //all ex tabBlank)  - pc       - [pageId].[complaintId]
    // [d,i,p]          - pci      - [pageId].[complaintId].i
    //tabDetail         - pcid     - [pageId].[complaintId].id
    //tabInitiator      - pcii     - [pageId].[complaintId].ii
    //tabPeople         - pcip     - [pageId].[complaintId].ip
    //tabPeople         - pcipc    - [pageId].[complaintId].ip.[personId]
    //tabNotes          - pcin     - [pageId].[complaintId].in
    //tabDocuments      - pcd      - [pageId].[complaintId].d
    //tabTasks          - pct      - [pageId].[complaintId].t
    // [c,s,t,d]        - pcr      - [pageId].[complaintId].r
    //tabRefComplaints  - pcrc     - [pageId].[complaintId].rc
    //tabRefCases       - pcrs     - [pageId].[complaintId].rs
    //tabRefTasks       - pcrt     - [pageId].[complaintId].rt
    //tabRefDocuments   - pcrd     - [pageId].[complaintId].rd
    // [a,c,w]          - pcp      - [pageId].[complaintId].p
    //tabApprovers      - pcpa     - [pageId].[complaintId].pa
    //tabCollaborators  - pcpc     - [pageId].[complaintId].pc
    //tabWatchers       - pcpw     - [pageId].[complaintId].pw
    //tabBlank          - nextPage - nextPage
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
    ,_mapNodeTab: {
        pc: ["tabDetail"
            ,"tabInitiator"
            ,"tabPeople"
            ,"tabNotes"
            ,"tabDocuments"
            ,"tabTasks"
            ,"tabRefComplaints"
            ,"tabRefCases"
            ,"tabRefTasks"
            ,"tabRefDocuments"
            ,"tabApprovers"
            ,"tabCollaborators"
            ,"tabWatchers"
        ]
        ,pci: ["tabDetail"
            ,"tabInitiator"
            ,"tabPeople"
            ,"tabNotes"
        ]
        ,pcid: ["tabDetail"]
        ,pcii: ["tabInitiator"]
        ,pcip: ["tabPeople"]
        ,pcipc: ["tabPeople"]
        ,pcin: ["tabNotes"]
        ,pcd: ["tabDocuments"]
        ,pct: ["tabTasks"]
        ,pcr: ["tabRefComplaints"
            ,"tabRefCases"
            ,"tabRefTasks"
            ,"tabRefDocuments"
        ]
        ,pcrc: ["tabRefComplaints"]
        ,pcrs: ["tabRefCases"]
        ,pcrt: ["tabRefTasks"]
        ,pcrd: ["tabRefDocuments"]
        ,pcp: ["tabApprovers"
            ,"tabCollaborators"
            ,"tabWatchers"
        ]
        ,pcpa: ["tabApprovers"]
        ,pcpc: ["tabCollaborators"]
        ,pcpw: ["tabWatchers"]
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
    ,getComplaintIdByKey: function(key) {
        return this._parseKey(key).complaintId;
    }
    ,getPageIdByKey: function(key) {
        return this._parseKey(key).pageId;
    }
    ,getChildIdByKey: function(key) {
        return this._parseKey(key).childId;
    }
    ,_parseKey: function(key) {
        var parts = {pageId: -1, complaintId: 0, sub: "", childId: 0};
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
            var complaintId = parseInt(arr[1]);
            if (! isNaN(complaintId)) {
                parts.complaintId = complaintId;
            }
        }
        if (3 <= arr.length) {
            parts.sub = arr[2];
        }
        if (4 <= arr.length) {
            var childId = parseInt(arr[3]);
            if (! isNaN(complaintId)) {
                parts.childId = childId;
            }
        }
        return parts;
    }
    ,refreshTree: function(key) {
        this.tree.reload().done(function(){
            if (Acm.isNotEmpty(key)) {
                Complaint.Object.tree.activateKey(key);
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
                Complaint.Event.onActivateTreeNode(data.node);
            }
            ,dblclick: function(event, data) {
                var node = data.node;
                alert("dblclick:(" + node.key + "," + node.title + ")");
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
                var nodeType = Complaint.Object.getNodeTypeByKey(key);
                if ("pc" == nodeType) {
                    acmIcon = "<i class='i i-notice'></i>"; //"i-notice icon"
                } else if ("pcd" == nodeType) {
                    acmIcon = "<i class='i i-file'></i>";
                } else if ("pct" == nodeType) {
                    acmIcon = "<i class='i i-checkmark'></i>";
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

            ,source: function() {
                var builder = AcmEx.FancyTreeBuilder.reset();

                var treeInfo = Complaint.Object.getTreeInfo();
                var start = treeInfo.start;
                var complaints = Complaint.cachePage.get(start);
                if (null == complaints || 0 >= complaints.length) {
                    return builder.getTree();
                }

                if (0 < treeInfo.start) {
                    builder.addLeaf({key: "prevPage"
                        ,title: treeInfo.start + " records above..."
                        ,tooltip: "Review previous records"
                        ,expanded: false
                        ,folder: false
                        ,acmIcon: "<i class='i i-arrow-up'></i>"
                    });
                }


                // populaet complaint data
                var pageId = start.toString();
                for (var i = 0; i < complaints.length; i++) {
                    var c = complaints[i];
                    var complaintId = parseInt(c.object_id_s);

                    builder.addBranch({key: pageId + "." + complaintId                      //level 1: /Complaint
                        ,title: c.title_t
                        ,tooltip: c.name
                        ,expanded: false
                        ,acmIcon: "<i class='i i-notice'></i>" //"i-notice icon";
                    })

                        .addBranch({key: pageId + "." + complaintId + ".i"                   //level 2: /Complaint/Incident
                            ,title: "Incident"
                            ,folder: true
                        })
                            .addLeaf({key: pageId + "." + complaintId + ".id"                //level 3: /Complaint/Incident/Detail
                                ,title: "Detail"
                            })
                            .addLeaf({key: pageId + "." + complaintId + ".ii"                //level 3: /Complaint/Incident/Initiator
                                ,title: "Initiator"
                            })
                            .addLeaf({key: pageId + "." + complaintId + ".ip"                //level 3: /Complaint/Incident/People
                                ,title: "People"
                            })
                            .addLeafLast({key: pageId + "." + complaintId + ".in"            //level 3: /Complaint/Incident/Notes
                                ,title: "Notes"
                            })


                        .addLeaf({key: pageId + "." + complaintId + ".d"                   //level 2: /Complaint/Documents
                            ,title: "Documents"
                        })

                        .addLeaf({key: pageId + "." + complaintId + ".t"                   //level 2: /Complaint/Tasks
                                ,title: "Tasks"
                        })


                        .addBranch({key: pageId + "." + complaintId + ".r"                   //level 2: /Complaint/References
                            ,title: "References"
                            ,folder: true
                        })
                            .addLeaf({key: pageId + "." + complaintId + ".rc"                //level 3: /Complaint/References/Complaints
                                ,title: "Other Complaints"
                            })
                            .addLeaf({key: pageId + "." + complaintId + ".rs"                //level 3: /Complaint/References/Cases
                                ,title: "Other Cases"
                            })
                            .addLeaf({key: pageId + "." + complaintId + ".rt"                //level 3: /Complaint/References/Tasks
                                ,title: "Other Tasks"
                            })
                            .addLeafLast({key: pageId + "." + complaintId + ".rd"            //level 3: /Complaint/References/Documents
                                ,title: "Other Documents"
                            })


                        .addBranchLast({key: pageId + "." + complaintId + ".p"               //level 2: /Complaint/Participants
                            ,title: "Participants"
                            ,folder: true
                        })
                            .addLeaf({key: pageId + "." + complaintId + ".pa"                //level 3: /Complaint/Participants/Approvers
                                ,title: "Approvers"
                            })
                            .addLeaf({key: pageId + "." + complaintId + ".pc"                //level 3: /Complaint/Participants/Collaborators
                                ,title: "Collaborators"
                            })
                            .addLeafLast({key: pageId + "." + complaintId + ".pw"            //level 3: /Complaint/Participants/Watchers
                                ,title: "Watchers"
                            });
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
                        ,acmIcon: "<i class='i i-arrow-down'></i>"
                    });
                }

                return builder.getTree();

            } //end source
        }); //end fancytree

        this.tree = this.$tree.fancytree("getTree");

        $s.contextmenu({
            //delegate: "span.fancytree-title",
            delegate: ".fancytree-title",
            menu: Complaint.Object.menu_cur,
            beforeOpen: function(event, ui) {
                var node = $.ui.fancytree.getNode(ui.target);
//                node.setFocus();
                node.setActive();
                Complaint.Object.$tree.contextmenu("replaceMenu", Complaint.Object._getMenu(node));

            },
            select: function(event, ui) {
                var node = $.ui.fancytree.getNode(ui.target);
                alert("select " + ui.cmd + " on " + node);
            }
        });

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


    ,refreshJTableInitiator: function() {
        AcmEx.Object.jTableLoad(this.$divInitiator);
    }
    ,refreshJTableDocuments: function() {
        AcmEx.Object.jTableLoad(this.$divDocuments);
    }
    ,refreshJTableTasks: function() {
        AcmEx.Object.jTableLoad(this.$divTasks);
    }

};




