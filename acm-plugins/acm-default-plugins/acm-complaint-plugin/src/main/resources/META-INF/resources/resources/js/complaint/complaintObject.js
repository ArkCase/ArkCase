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
        this.$lnkTitle.editable({placement: 'right'});
        this.$h4TitleHeader     = $("#caseTitle").parent();

        this.$lnkIncident       = $("#incident");
        this.$lnkIncident.editable({placement: 'bottom'
            ,format: 'yyyy-mm-dd'
            ,viewformat: 'yyyy/mm/dd'
            ,datepicker: {
                weekStart: 1
            }
        });
        this.$lnkPriority       = $("#priority");
        this.$lnkAssigned       = $("#assigned");
        this.$lnkComplaintType  = $("#type");
        this.$lnkStatus         = $("#status");

        this.$divDetails        = $(".complaintDetails");

        this.$divInitiator      = $("#divInitiator");
        this._createJTableInitiator(this.$divInitiator);

        this.$divDocuments      = $("#divDocuments");
        this._createJTableDocuments(this.$divDocuments);
        this.$spanAddDocument   = this.$divDocuments.find(".jtable-toolbar-item-add-record");
        this.$spanAddDocument.unbind("click").on("click", function(e){Complaint.Event.onClickSpanAddDocument(e);});
        Complaint.Page.fillReportSelection();

        this.$divTasks          = $("#divTasks");
        this._createJTableTasks(this.$divTasks);
        this.$spanAddTask       = this.$divTasks.find(".jtable-toolbar-item-add-record");
        this.$spanAddTask.unbind("click").on("click", function(e){ComplaintList.Event.onClickSpanAddTask(e);});

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
            ,"tabMain"
            ,"tabDetail"
            ,"tabInitiator"
            ,"tabPeople"
            ,"tabNotes"
            ,"tabPending"
            ,"tabApproved"
            ,"tabRejected"
            ,"tabUnassigned"
            ,"tabAssigned"
            ,"tabCompleted"
            ,"tabRefComplaints"
            ,"tabRefCases"
            ,"tabRefTasks"
            ,"tabRefDocuments"
            ,"tabApprovers"
            ,"tabCollaborators"
            ,"tabWatchers"
        ];
        var tabId = this._getTabIdByKey(key);
        for (var i = 0; i < tabIds.length; i++) {
            var show = (tabId == tabIds[i]);

            var nodeType = this.getNodeTypeByKey(key);
            if ("pc" == nodeType) {
                show = 0 < i;
            }

            Acm.Object.show($("#" + tabIds[i]), show);
        }
    }


    ,initAssignee: function(data) {
        var choices = []; //[{value: "", text: "Choose Assignee"}];
        $.each(data, function(idx, val) {
            var opt = {};
            opt.value = val.userId;
            opt.text = val.fullName;
            choices.push(opt);
        });

        this.$lnkAssigned.editable({placement: 'bottom', value: "",
            source: choices
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

        this.$lnkComplaintType.editable({placement: 'bottom', value: "",
            source: choices
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

        this.$lnkPriority.editable({placement: 'bottom', value: "",
            source: choices
        });
    }
    ,setValueLnkTitle: function(txt) {
        this.$lnkTitle.editable("setValue", txt);
    }
    ,setTextH4TitleHeader: function(txt) {
        Acm.Object.setTextNodeText(this.$h4TitleHeader, txt, 1);
    }
    ,setValueLnkIncident: function(txt) {
        Acm.Object.setText(this.$lnkIncident, txt);
        //this.$lnkIncident.editable("setValue", txt);
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
    ,setHtmlDivDetails: function(html) {
        Acm.Object.setHtml(this.$divDetails, html);
    }

    ,updateDetail: function(c) {
        this.setValueLnkTitle(c.complaintTitle);
        this.setTextH4TitleHeader(" (" + c.complaintNumber + ")");
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
    //tabId             - nodeType - key
    //------------------------------------------
    //tabBlank          - prevPage - prevPage
    //tabBlank          - p        - [pageId]
    //tabMain           - pc       - [pageId].[complaintId]
    //tabDetail         - pci      - [pageId].[complaintId].i
    //tabDetail         - pcid     - [pageId].[complaintId].id
    //tabInitiator      - pcii     - [pageId].[complaintId].ii
    //tabPeople         - pcip     - [pageId].[complaintId].ip
    //tabPeople         - pcipc    - [pageId].[complaintId].ip.[personId]
    //tabNotes          - pcin     - [pageId].[complaintId].in
    //tabPending        - pca      - [pageId].[complaintId].a
    //tabPending        - pcap     - [pageId].[complaintId].ap
    //tabPending        - pcapc    - [pageId].[complaintId].ap.[docId]
    //tabApproved       - pcaa     - [pageId].[complaintId].aa
    //tabApproved       - pcaac    - [pageId].[complaintId].aa.[docId]
    //tabRejected       - pcar     - [pageId].[complaintId].ar
    //tabRejected       - pcarc    - [pageId].[complaintId].ar.[docId]
    //tabUnassigned     - pct      - [pageId].[complaintId].t
    //tabUnassigned     - pctu     - [pageId].[complaintId].tu
    //tabUnassigned     - pctuc    - [pageId].[complaintId].tu.[taskId]
    //tabAssigned       - pcta     - [pageId].[complaintId].ta
    //tabAssigned       - pctac    - [pageId].[complaintId].ta.[taskId]
    //tabCompleted      - pctc     - [pageId].[complaintId].tc
    //tabCompleted      - pctcc    - [pageId].[complaintId].tc.[taskId]
    //tabRefComplaints  - pcr      - [pageId].[complaintId].r
    //tabRefComplaints  - pcrc     - [pageId].[complaintId].rc
    //tabRefCases       - pcrs     - [pageId].[complaintId].rs
    //tabRefTasks       - pcrt     - [pageId].[complaintId].rt
    //tabRefDocuments   - pcrd     - [pageId].[complaintId].rd
    //tabApprovers      - pcp      - [pageId].[complaintId].p
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
    ,_getTabIdByKey: function(key) {
        if (Acm.isEmpty(key)) {
            return "tabBlank";
        }

        var arr = key.split(".");
        if (1 == arr.length) {
            return "tabBlank";      //"p", "prevPage", "nextPage"
        } else if (2 == arr.length) {
            return "tabMain";       //"pc"
        } else if (3 <= arr.length) {
            if ("i" == arr[2]) {
                return "tabDetail";
            } else if ("id" == arr[2]) {
                return "tabDetail";
            } else if ("ii" == arr[2]) {
                return "tabInitiator";
            } else if ("ip" == arr[2]) {
                return "tabPeople";
            } else if ("in" == arr[2]) {
                return "tabNotes";

            } else if ("a" == arr[2]) {
                return "tabPending";
            } else if ("ap" == arr[2]) {
                return "tabPending";
            } else if ("aa" == arr[2]) {
                return "tabApproved";
            } else if ("ar" == arr[2]) {
                return "tabRejected";

            } else if ("t" == arr[2]) {
                return "tabUnassigned";
            } else if ("tu" == arr[2]) {
                return "tabUnassigned";
            } else if ("ta" == arr[2]) {
                return "tabAssigned";
            } else if ("tc" == arr[2]) {
                return "tabCompleted";

            } else if ("r" == arr[2]) {
                return "tabRefComplaints";
            } else if ("rc" == arr[2]) {
                return "tabRefComplaints";
            } else if ("rs" == arr[2]) {
                return "tabRefCases";
            } else if ("rt" == arr[2]) {
                return "tabRefTasks";
            } else if ("rd" == arr[2]) {
                return "tabRefDocuments";

            } else if ("p" == arr[2]) {
                return "tabApprovers";
            } else if ("pa" == arr[2]) {
                return "tabApprovers";
            } else if ("pc" == arr[2]) {
                return "tabCollaborators";
            } else if ("pw" == arr[2]) {
                return "tabWatchers";
            } else {
                return "tabBlank";
            }
        }
        return null;
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
                    acmIcon = "<i class='i i-notice'></i>" //"i-notice icon"
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


                var pageId = start.toString();
                for (var i = 0; i < complaints.length; i++) {
                    var c = complaints[i];

                    builder.addBranch({key: pageId + "." + c.complaintId                       //level 1: /Complaint
                        ,title: c.complaintTitle
                        ,tooltip: c.complaintNumber
                        ,expanded: false
                        ,acmIcon: "<i class='i i-notice'></i>" //"i-notice icon";
                    })

                        .addBranch({key: pageId + "." + c.complaintId + ".i"                   //level 2: /Complaint/Incident
                            ,title: "Incident"
                            ,folder: true
                        })
                            .addLeaf({key: pageId + "." + c.complaintId + ".id"                //level 3: /Complaint/Incident/Detail
                                ,title: "Detail"
                            })
                            .addLeaf({key: pageId + "." + c.complaintId + ".ii"                //level 3: /Complaint/Incident/Initiator
                                ,title: "Initiator"
                            })
                            .addLeaf({key: pageId + "." + c.complaintId + ".ip"                //level 3: /Complaint/Incident/People
                                ,title: "People"
                            })
                            .addLeafLast({key: pageId + "." + c.complaintId + ".in"            //level 3: /Complaint/Incident/Notes
                                ,title: "Notes"
                            })


                        .addBranch({key: pageId + "." + c.complaintId + ".a"                   //level 2: /Complaint/Attachments
                            ,title: "Attachments"
                            ,folder: true
                        })
                            .addLeaf({key: pageId + "." + c.complaintId + ".ap"                //level 3: /Complaint/Incident/Initiator
                                ,title: "Pending"
                            })
                            .addLeaf({key: pageId + "." + c.complaintId + ".aa"                //level 3: /Complaint/Incident/Approved
                                ,title: "Approved"
                            })
                            .addLeafLast({key: pageId + "." + c.complaintId + ".ar"            //level 3: /Complaint/Incident/Rejected
                                ,title: "Rejected"
                            })


                        .addBranch({key: pageId + "." + c.complaintId + ".t"                   //level 2: /Complaint/Tasks
                                ,title: "Tasks"
                                ,folder: true
                        })
                            .addLeaf({key: pageId + "." + c.complaintId + ".tu"                //level 3: /Complaint/Tasks/Unassigned
                                ,title: "Unassigned"
                            })
                            .addLeaf({key: pageId + "." + c.complaintId + ".ta"                //level 3: /Complaint/Tasks/Assigned
                                ,title: "Assigned"
                            })
                            .addLeafLast({key: pageId + "." + c.complaintId + ".tu"            //level 3: /Complaint/Tasks/Completed
                                ,title: "Completed"
                            })


                        .addBranch({key: pageId + "." + c.complaintId + ".r"                   //level 2: /Complaint/References
                            ,title: "References"
                            ,folder: true
                        })
                            .addLeaf({key: pageId + "." + c.complaintId + ".rc"                //level 3: /Complaint/References/Complaints
                                ,title: "Other Complaints"
                            })
                            .addLeaf({key: pageId + "." + c.complaintId + ".rs"                //level 3: /Complaint/References/Cases
                                ,title: "Other Cases"
                            })
                            .addLeaf({key: pageId + "." + c.complaintId + ".rt"                //level 3: /Complaint/References/Tasks
                                ,title: "Other Tasks"
                            })
                            .addLeafLast({key: pageId + "." + c.complaintId + ".rd"            //level 3: /Complaint/References/Documents
                                ,title: "Other Documents"
                            })


                        .addBranchLast({key: pageId + "." + c.complaintId + ".p"               //level 2: /Complaint/Participants
                            ,title: "Participants"
                            ,folder: true
                        })
                            .addLeaf({key: pageId + "." + c.complaintId + ".pa"                //level 3: /Complaint/Participants/Approvers
                                ,title: "Approvers"
                            })
                            .addLeaf({key: pageId + "." + c.complaintId + ".pc"                //level 3: /Complaint/Participants/Collaborators
                                ,title: "Collaborators"
                            })
                            .addLeafLast({key: pageId + "." + c.complaintId + ".pw"            //level 3: /Complaint/Participants/Watchers
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


    //
    //------------------ Initiator ------------------
    //
    ,_toggleSubJTable: function($t, $row, fnOpen, fnClose, title) {
        var $childRow = $t.jtable('getChildRow', $row.closest('tr'));
        var curTitle = $childRow.find("div.jtable-title-text").text();

        var toClose;
        if ($t.jtable('isChildRowOpen', $row.closest('tr'))) {
            toClose = (curTitle === title);
        } else {
            toClose = false;
        }

        if (toClose) {
            fnClose($t, $row);
        } else {
            fnOpen($t, $row);
        }
    }

    ,refreshJTableInitiator: function() {
        AcmEx.Object.jTableLoad(this.$divInitiator);
    }
    ,_createJTableInitiator: function($s) {
        $s.jtable({
            title: 'Initiator'
            ,paging: false
            ,actions: {
                listAction: function(postData, jtParams) {
                    var rc = {"Result": "OK", "Records": [{id:0, title:"", giventName:"", familyName:"", type:"", description:""}]};
                    var c = Complaint.getComplaint();
                    if (c) {
                        if (Acm.isEmpty(c.originator)) {
                            rc.Records[0].id = c.originator.id;
                            rc.Records[0].title = c.originator.title;
                            rc.Records[0].givenName = c.originator.givenName;
                            rc.Records[0].familyName = c.originator.familyName;
                            rc.Records[0].type = "";
                            rc.Records[0].description = "";
                        }
                    }
                    return rc;
                }
                ,updateAction: function(postData, jtParams) {
                    var record = Acm.urlToJson(postData);
                    var c = Complaint.getComplaint();
                    var rc = {"Result": "OK", "Record": {}};
                    rc.Record.id = c.originator.id;    // (record.id) is empty, do not assign;
                    rc.Record.title = c.originator.title = record.title;
                    rc.Record.givenName = c.originator.givenName = record.givenName;
                    rc.Record.familyName = c.originator.familyName = record.familyName;
                    rc.Record.type = record.type;
                    rc.Record.description = record.description;
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
                ,subTables: {
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
                            Complaint.Object._toggleInitiatorDevices($s, $a);
                            e.preventDefault();
                        });
                        $b.click(function (e) {
                            Complaint.Object._toggleInitiatorOrganizations($s, $b);
                            e.preventDefault();
                        });
                        $c.click(function (e) {
                            Complaint.Object._toggleInitiatorLocations($s, $c);
                            e.preventDefault();
                        });
                        $d.click(function (e) {
                            Complaint.Object._toggleInitiatorAliases($s, $d);
                            e.preventDefault();
                        });
                        return $a.add($b).add($c).add($d);
                    }
                }


                ,title: {
                    title: 'Title'
                    ,width: '10%'
                    ,options: Complaint.getPersonTitles()
                }
                ,givenName: {
                    title: 'First Name'
                    ,width: '15%'
                }
                ,familyName: {
                    title: 'Last Name'
                    ,width: '15%'
                }
                ,type: {
                    title: 'Type'
                    //,options: App.getContextPath() + '/api/latest/plugin/complaint/types'
                    ,options: Complaint.getPersonTypes()
                }
                ,description: {
                    title: 'Description'
                    ,type: 'textarea'
                    ,width: '30%'
                }
            }
            ,recordAdded: function(event, data){
                $s.jtable('load');
            }
            ,recordUpdated: function(event, data){
                $s.jtable('load');
            }
        });

        $s.jtable('load');
    }
    ,_toggleInitiatorDevices: function($t, $row) {
        this._toggleSubJTable($t, $row, this._openInitiatorDevices, this._closeInitiatorDevices, Complaint.PERSON_SUBTABLE_TITLE_DEVICES);
    }
    ,_toggleInitiatorOrganizations: function($t, $row) {
        this._toggleSubJTable($t, $row, this._openInitiatorOrganizations, this._closeInitiatorOrganizations, Complaint.PERSON_SUBTABLE_TITLE_ORGANIZATIONS);
    }
    ,_toggleInitiatorLocations: function($t, $row) {
        this._toggleSubJTable($t, $row, this._openInitiatorLocations, this._closeInitiatorLocations, Complaint.PERSON_SUBTABLE_TITLE_LOCATIONS);
    }
    ,_toggleInitiatorAliases: function($t, $row) {
        this._toggleSubJTable($t, $row, this._openInitiatorAliases, this._closeInitiatorAliases, Complaint.PERSON_SUBTABLE_TITLE_ALIASES);
    }
    ,_closeInitiatorDevices: function($t, $row) {
        $t.jtable('closeChildTable', $row.closest('tr'));
    }
    ,_openInitiatorDevices: function($t, $row) {
        $t.jtable('openChildTable'
            ,$row.closest('tr')
            ,{
                title: Complaint.PERSON_SUBTABLE_TITLE_DEVICES
                ,sorting: true
                ,actions: {
                    listAction: function(postData, jtParams) {
                        var c = Complaint.getComplaint();
                        var contactMethods = c.originator.contactMethods;
                        var cnt = contactMethods.length;;

                        var rc = {"Result": "OK", "Records": []};
                        for (i = 0; i < cnt; i++) {
                            rc.Records.push({personId: c.originator.id
                                ,id: contactMethods[i].id
                                ,type: contactMethods[i].type
                                ,value: contactMethods[i].value
                                ,created: contactMethods[i].created
                                ,creator: contactMethods[i].creator
                            });
                        }
                        return rc;
//                        return {
//                            "Result": "OK"
//                            ,"Records": [
//                                { "personId":  1, "id": "a", "type": "Phone", "value": "703-123-5678", "created": "01-02-03", "creator": "123 do re mi" }
//                                ,{ "personId": 2, "id": "b", "type": "Email", "value": "doe@gmail.com", "created": "14-05-15", "creator": "xyz abc" }
//                            ]
//                            //,"TotalRecordCount": 2
//                        };

                    }
                    ,createAction: function(postData, jtParams) {
                        var record = Acm.urlToJson(postData);
                        var c = Complaint.getComplaint();
                        var rc = {"Result": "OK", "Record": {}};
                        rc.Record.personId = c.originator.id;
                        rc.Record.id = parseInt(record.id);
                        rc.Record.type = record.type;
                        rc.Record.value = record.value;
                        rc.Record.created = Acm.getCurrentDay(); //record.created;
                        rc.Record.creator = App.getUserName();   //record.creator;
                        return rc;
//                        return {
//                            "Result": "OK"
//                            ,"Record":
//                            { "personId": 3, "id": "c", "type": "Phone", "value": "703-123-9999", "created": "01-02-03", "creator": "test" }
//                        };
                    }
                    ,updateAction: function(postData, jtParams) {
                        var record = Acm.urlToJson(postData);
                        var c = Complaint.getComplaint();
                        var rc = {"Result": "OK", "Record": {}};
                        rc.Record.personId = c.originator.id;
                        //rc.Record.id = parseInt(record.id);           //no such field in postData, ignored
                        rc.Record.type = record.type;
                        rc.Record.value = record.value;
                        rc.Record.created = record.created;
                        rc.Record.creator = record.creator;
                        return rc;
//                        return {
//                            "Result": "OK"
//                            ,"Record":
//                            { "personId": 3, "id": "c", "type": "Phone", "value": "703-123-9999", "created": "01-02-03", "creator": "test" }
//                        };

                    }
                    ,deleteAction: function(postData, jtParams) {
                        return {
                            "Result": "OK"
                        };
                    }
                }
                ,fields: {
                    personId: {
                        key: false
                        ,create: false
                        ,edit: false
                        ,list: false
                    }
                    ,id: {
                        key: false
                        ,type: 'hidden'
                        ,edit: false
                        ,defaultValue: 0
                    }
                    ,type: {
                        title: 'Type'
                        ,width: '15%'
                        ,options: Complaint.getDeviceTypes()
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
                    var c = Complaint.getComplaint();
                    var contactMethods = c.originator.contactMethods;
                    var contactMethod = {};
                    contactMethod.id = parseInt(record.id);
                    contactMethod.type = record.type;
                    contactMethod.value = record.value;
                    //contactMethod.created = record.created;   //created,creator is readonly
                    //contactMethod.creator = record.creator;
                    contactMethods.push(contactMethod);
                }
                ,recordUpdated : function (event, data) {
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var record = data.record;
                    var c = Complaint.getComplaint();
                    var contactMethods = c.originator.contactMethods;
                    var contactMethod = contactMethods[whichRow];
                    contactMethod.type = record.type;
                    contactMethod.value = record.value;
                    //contactMethod.created = record.created;   //created,creator is readonly
                    //contactMethod.creator = record.creator;
                }
                ,recordDeleted : function (event, data) {
                    var r = data.row;
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var c = Complaint.getComplaint();
                    var contactMethods = c.originator.contactMethods;
                    contactMethods.splice(whichRow, 1);
                }
            }
            ,function (data) { //opened handler
                data.childTable.jtable('load');
            });
    }
    ,_closeInitiatorOrganizations: function($t, $row) {
        $t.jtable('closeChildTable', $row.closest('tr'));
    }
    ,_openInitiatorOrganizations: function($t, $row) {
        $t.jtable('openChildTable',
            $row.closest('tr'),
            {
                title: Complaint.PERSON_SUBTABLE_TITLE_ORGANIZATIONS
                //,paging: true
                //,pageSize: 10
                ,sorting: true
                ,actions: {
                listAction: function(postData, jtParams) {
                    return {
                        "Result": "OK"
                        ,"Records": [
                            { "personId":  1, "id": "a", "type": "com", "value": "ABC, Inc.", "createDate": "01-02-03", "createBy": "123 do re mi" }
                            ,{ "personId": 2, "id": "b", "type": "gov", "value": "IRS", "createDate": "14-05-15", "createBy": "xyz abc" }
                        ]
                        //,"TotalRecordCount": 2
                    };
                }
                ,createAction: function(postData, jtParams) {
                    return {
                        "Result": "OK"
                        ,"Record":
                        { "personId": 3, "id": "c", "type": "com", "value": "ABC, Inc.", "createDate": "01-02-03", "createBy": "test" }
                    };
                }
                ,updateAction: function(postData, jtParams) {
                    return {
                        "Result": "OK"
                        ,"Record":
                        { "personId": 3, "id": "c", "type": "gov", "value": "IRS", "createDate": "01-02-03", "createBy": "test" }
                    };
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
                    ,options: Complaint.getOrganizationTypes()
                }
                ,value: {
                    title: 'Value'
                    ,width: '30%'
                }
                ,createDate: {
                    title: 'Date Added'
                    ,width: '20%'
                    //,type: 'date'
                    //,displayFormat: 'yy-mm-dd'
                    ,create: false
                    ,edit: false
                }
                ,createBy: {
                    title: 'Added By'
                    ,width: '30%'
                }
            }
            }
            ,function (data) { //opened handler
                data.childTable.jtable('load');
            });
    }
    ,_closeInitiatorLocations: function($t, $row) {
        $t.jtable('closeChildTable', $row.closest('tr'));
    }
    ,_openInitiatorLocations: function($t, $row) {
        $t.jtable('openChildTable',
            $row.closest('tr'),
            {
                title: Complaint.PERSON_SUBTABLE_TITLE_LOCATIONS
                //,paging: true
                //,pageSize: 10
                ,sorting: true
                ,actions: {
                listAction: function(postData, jtParams) {
                    return {
                        "Result": "OK"
                        ,"Records": [
                            { "personId":  1, "id": "a", "type": "Home", "address": "123 Main St", "city": "Vienna", "state": "VA", "zip": "22000", "country": "US", "createDate": "01-02-03", "createBy": "123 do re mi" }
                            ,{ "personId": 2, "id": "b", "type": "Office", "address": "999 Fairfax Blvd #201, Fairfax, VA 22030", "city": "Vienna", "state": "VA", "zip": "22000", "country": "US", "createDate": "14-05-15", "createBy": "xyz abc" }
                        ]
                        //,"TotalRecordCount": 2
                    };
                }
                ,createAction: function(postData, jtParams) {
                    return {
                        "Result": "OK"
                        ,"Record":
                        { "personId": 3, "id": "c", "type": "Home", "address": "123 Main St", "city": "Vienna", "state": "VA", "zip": "22000", "country": "US", "createDate": "01-02-03", "createBy": "test" }
                    };
                }
                ,updateAction: function(postData, jtParams) {
                    return {
                        "Result": "OK"
                        ,"Record":
                        { "personId": 3, "id": "c", "type": "Hotel", "address": "123 Main St", "city": "Vienna", "state": "VA", "zip": "22000", "country": "US", "createDate": "01-02-03", "createBy": "test" }
                    };
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
                    ,options: Complaint.getLocationTypes()
                }
                ,address: {
                    title: 'Address'
                    ,width: '30%'
                }
                ,city: {
                    title: 'City'
                    ,width: '12%'
                }
                ,state: {
                    title: 'State'
                    ,width: '5%'
                }
                ,zip: {
                    title: 'Zip'
                    ,width: '8%'
                }
                ,country: {
                    title: 'Country'
                    ,width: '8%'
                }
                ,createDate: {
                    title: 'Date Added'
                    ,width: '15%'
                    //,type: 'date'
                    //,displayFormat: 'yy-mm-dd'
                    ,create: false
                    ,edit: false
                }
                ,createBy: {
                    title: 'Added By'
                    ,width: '30%'
                }
            }
            }
            ,function (data) { //opened handler
                data.childTable.jtable('load');
            });
    }
    ,_closeInitiatorAliases: function($jt, $row) {
        $jt.jtable('closeChildTable', $row.closest('tr'));
    }
    ,_openInitiatorAliases: function($jt, $row) {
        $jt.jtable('openChildTable',
            $row.closest('tr'),
            {
                title: Complaint.PERSON_SUBTABLE_TITLE_ALIASES
                //,paging: true
                //,pageSize: 10
                ,sorting: true
                ,actions: {
                listAction: function(postData, jtParams) {
                    return {
                        "Result": "OK"
                        ,"Records": [
                            { "personId":  1, "id": "a", "type": "Nick Name", "value": "JJ", "createDate": "01-02-03", "createBy": "123 do re mi" }
                            ,{ "personId": 2, "id": "b", "type": "Some Name", "value": "Ice Man", "createDate": "14-05-15", "createBy": "xyz abc" }
                        ]
                        //,"TotalRecordCount": 2
                    };
                }
                ,createAction: function(postData, jtParams) {
                    return {
                        "Result": "OK"
                        ,"Record":
                        { "personId": 3, "id": "c", "type": "Nick Name", "value": "Ice Man", "createDate": "01-02-03", "createBy": "test" }
                    };
                }
                ,updateAction: function(postData, jtParams) {
                    return {
                        "Result": "OK"
                        ,"Record":
                        { "personId": 3, "id": "c", "type": "Nick Name", "value": "Big Man", "createDate": "01-02-03", "createBy": "test" }
                    };
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
                    ,options: Complaint.getAliasTypes()
                }
                ,value: {
                    title: 'Value'
                    ,width: '30%'
                }
                ,createDate: {
                    title: 'Date Added'
                    ,width: '20%'
                    //,type: 'date'
                    //,displayFormat: 'yy-mm-dd'
                    ,create: false
                    ,edit: false
                }
                ,createBy: {
                    title: 'Added By'
                    ,width: '30%'
                }
            }
            }
            ,function (data) { //opened handler
                data.childTable.jtable('load');
            });
    }

    //----------------- end of Initiator -----------------------


    //
    //----------------- Documents ------------------------------
    //
    ,refreshJTableDocuments: function() {
        AcmEx.Object.jTableLoad(this.$divDocuments);
    }
    ,_createJTableDocuments: function($s) {
        $s.jtable({
            title: 'Documents'
            ,paging: false
            ,actions: {
                listAction: function(postData, jtParams) {
                    var rc = {"Result": "OK", "Records": []};
                    var c = Complaint.getComplaint();
                    if (c) {
                        if (Acm.isEmpty(c.childObjects)) {
                            for (var i = 0; i < c.childObjects; i++) {
                                var childObject = c.childObjects[i];
                                var record = {};
                                record.id = childObject.targetId;
                                record.title = childObject.targetName;
                                record.created = Acm.getDateFromDatetime(childObject.created);
                                record.creator = childObject.creator;
                                record.status = childObject.status;
                                rc.Records.push(record);
                            }
                        }
                    }
                    return rc;
//for test
//                    return {
//                        "Result": "OK"
//                        ,"Records": [
//                            {"id": 11, "title": "Nick Name", "created": "01-02-03", "creator": "123 do re mi", "status": "JJ"}
//                            ,{"id": 12, "title": "Some Name", "created": "14-05-15", "creator": "xyz abc", "status": "Ice Man"}
//                        ]
//                        ,"TotalRecordCount": 2
//                    };
                }
                ,createAction: function(postData, jtParams) {
                    //custom web form creation takes over; this action should never be called
                    var rc = {"Result": "OK", "Record": {id:0, title:"", created:"", creator:"", status:""}};
                    return rc;
                }
                ,updateAction: function(postData, jtParams) {
                    var record = Acm.urlToJson(postData);
                    var rc = {"Result": "OK", "Record": {}};
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
                        var a = "<a href='" + App.getContextPath() + Complaint.Service.API_DOWNLOAD_DOCUMENT
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
                var c = Complaint.getComplaint();
                if (c) {
                    if (c.childObjeccts) {
                        if (0 < c.childObjects.length) {
                            var childObject = c.childObjects[whichRow];
                            //id,created,creator is readonly
                            //childObject.Record.id = record.id;
                            //childObject.Record.created = record.created;
                            //childObject.Record.creator = record.creator;
                            childObject.Record.title = record.title;
                            childObject.Record.status = record.status;
                        }
                    }
                }
            }
        });

        $s.jtable('load');
    }
    //----------------- end of Documents ----------------------


    //
    //------------------ Tasks ------------------
    //
    ,refreshJTableTasks: function() {
        AcmEx.Object.jTableLoad(this.$divTasks);
    }

    ,_createJTableTasks: function($jt) {
        var sortMap = {};
        sortMap["title"] = "title_t";


        AcmEx.Object.jTableCreatePaging($jt
            ,{
                title: 'Tasks'
                ,selecting: true
                ,multiselect: false
                ,selectingCheckboxes: false

                ,actions: {
                    pagingListAction: function (postData, jtParams, sortMap) {
                        return AcmEx.Object.jTableDefaultPagingListAction(postData, jtParams, sortMap
                            ,function() {
                                var url;
                                url =  App.getContextPath() + Complaint.Service.API_RETRIEVE_TASKS;
                                url += Complaint.getComplaintId();
                                return url;
                            }
                            ,function(data) {
                                var jtData = null;
                                var err = "Invalid search data";
                                if (data) {
                                    if (Acm.isNotEmpty(data.responseHeader)) {
                                        var responseHeader = data.responseHeader;
                                        if (Acm.isNotEmpty(responseHeader.status)) {
                                            if (0 == responseHeader.status) {
                                                var response = data.response;
                                                //response.start should match to jtParams.jtStartIndex
                                                //response.docs.length should be <= jtParams.jtPageSize

                                                jtData = AcmEx.Object.jTableGetEmptyResult();
                                                for (var i = 0; i < response.docs.length; i++) {
                                                    var Record = {};
                                                    Record.id = response.docs[i].object_id_s;
                                                    Record.title = Acm.goodValue(response.docs[i].name); //title_t ?
                                                    Record.created = Acm.goodValue(response.docs[i].create_dt);
                                                    Record.priority = "[priority]";
                                                    Record.dueDate = "[due]";
                                                    Record.status = Acm.goodValue(response.docs[i].status_s);
                                                    Record.assignee = "[assignee]";
                                                    jtData.Records.push(Record);

                                                }
                                                jtData.TotalRecordCount = response.numFound;


                                            } else {
                                                if (Acm.isNotEmpty(data.error)) {
                                                    err = data.error.msg + "(" + data.error.code + ")";
                                                }
                                            }
                                        }
                                    }
                                }

                                return {jtData: jtData, jtError: err};
                            }
                        );
                    }

                    ,createAction: function(postData, jtParams) {
                        return AcmEx.Object.jTableGetEmptyResult();
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
                        ,sorting: false
                    }
                    ,status: {
                        title: 'status'
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
                                Complaint.Event.onClickBtnTaskAssign(e);
                                e.preventDefault();
                            });
                            $b.click(function (e) {
                                Complaint.Event.onClickBtnTaskUnassign(e);
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
    //---------------- end of Tasks --------------------------



    ,initialize0 : function() {
        this.$ulComplaints      = $("#ulComplaints");
        this.$asideComplaints   = this.$ulComplaints.closest("aside");
        this.$ulTabs            = $("#ulTabs");

        var items = $(document).items();
        var complaintId = items.properties("complaintId").itemValue();
        if (Acm.isNotEmpty(complaintId)) {
            Complaint.setComplaintId(complaintId);
            this.showAsideComplaints(false);
            Complaint.setSingleObject(true);
        } else {
            Complaint.setSingleObject(false);
        }
        this.setInitId(items.properties("initId").itemValue());
        this.setInitTab(items.properties("initTab").itemValue());


        this.$lnkTitle          = $("#caseTitle");
        this.$h4TitleHeader     = $("#caseTitle").parent();

        this.$lnkIncident       = $("#incident");
        this.$lnkPriority       = $("#priority");
        this.$lnkAssigned       = $("#assigned");
        this.$lnkComplaintType  = $("#type");
        this.$lnkStatus         = $("#status");

        this.$divDetails        = $(".complaintDetails");
        this.$secIncident       = $("#secIncident");
        this.$tableIncident     = $("#secIncident>div>table");

        this.$divInitiator      = $("#divInitiator");
        this._createJTableInitiator(this.$divInitiator);

        this.$secDocDocuments   = $("#secDocDocuments");
        this.$tableDocDocuments = $("#secDocDocuments>div>table");
        this.$lnkNewDoc         = $("#secDocDocuments>div>span");
        //this.$upploadList       = $('#secDocDocuments ul');
        this.$upploadList       = $('#upload ul');
        this._useFileUpload(this.$secDocDocuments, this.$tableDocDocuments, this.$upploadList, this.$lnkNewDoc);

        this.$tableTasks        = $("div#tasks>div>div>section>div>table");
        this.$lnkNewTasks       = $("div#tasks>div>div>section>div>span");
        this.$lnkNewTasks.click(function(e){Complaint.Event.onClickLnkNewTasks(e);});

        this.$divTasks          = $("#divTasks");
        this._createJTableTasks(this.$divTasks);
        this.$spanAddTask       = this.$divTasks.find(".jtable-toolbar-item-add-record");
        this.$spanAddTask.unbind("click").on("click", function(e){Complaint.Event.onClickSpanAddTask(e);});

        this.$tableRefDocuments = $("#secRefDocuments>div>table");


        //$.fn.editable.defaults.url = '/post';
        this.$lnkTitle.editable({placement: 'right'});
        this.$lnkIncident.editable({placement: 'bottom'
            ,format: 'yyyy-mm-dd'
            ,viewformat: 'yyyy/mm/dd'
            ,datepicker: {
                weekStart: 1
            }
        });

//test area
//        $("#sex").editable({placement: 'right'
//            , value: "F"
//            ,source: [
//                //{value: "", text: 'no select'},
//                {value: "M", text: 'Male'}
//                ,{value: "F", text: 'Female'}
//            ]
//            ,url: ""
//            ,success: function(response, newValue) {
//                console.log("editable, sex=" + newValue); //update backbone model
//            }
//
//        });
//        $("#sex").editable("setValue", "");


        this.$tree = $("#tree");
        this._useFancyTree(this.$tree);
    }




//    ,updateTasks: function(response) {
//        var tasks = [];
//        for (var i = 0; i < response.docs.length; i++) {
//            var obj = response.docs[i];
//            var task = {};
//            task.taskId = obj.object_id_s;
//            task.title = obj.name; //?or obj.title_t
//            task.created = obj.create_dt;
//            task.priority = "[priority]";
//            task.dueDate ="[due]";
//            task.status = obj.status_s;
//            task.assignee = "[assignee]";
//
//            tasks.push(task);
//        }
//        Complaint.setTasks(tasks);
//
//        this.$divTasks.jtable('load');
//    }



    ,resetTableDocDocuments: function() {
        this.$tableDocDocuments.find("tbody > tr").remove();
    }
    ,addRowTableDocDocuments: function(row) {
        this.$tableDocDocuments.find("tbody:last").append(row);
    }
    ,resetTableTasks: function() {
        this.$tableTasks.find("tbody > tr").remove();
    }
    ,addRowTableTasks: function(row) {
        this.$tableTasks.find("tbody:last").append(row);
    }
    ,registerChangeSelTasksEvents: function() {
        this.$tableTasks.find("select").change(function(e) {Complaint.Event.onChangeSelTasks(this);});
    }
    ,resetTableRefDocuments: function() {
        this.$tableRefDocuments.find("tbody > tr").remove();
    }
    ,addRowTableRefDocuments: function(row) {
        this.$tableRefDocuments.find("tbody:last").append(row);
    }


    //
    // Documents
    //
    ,removeUploadFileArea: function() {
        this.$upploadList.find("li").remove();
    }
    ,_jqXHR : undefined
    ,_useFileUpload: function($upload, $drop, $ul, $click) {
        $(function(){
            $click.click(function(){
                // Simulate a click on the file input button
                // to show the file browser dialog
                $(this).parent().find('input').click();
            });

            // Initialize the jQuery File Upload plugin
            _jqXHR = $upload.fileupload({
                url: App.getContextPath() + Complaint.Service.API_UPLOAD_COMPLAINT_FILE
                ,dropZone: $drop

                ,done: function (e, data) {
//                    var a1 = data.result
//                    var a2 = data.textStatus;
//                    var a3 = data.jqXHR;

                    if ("success" == data.textStatus) {
                        Complaint.Object.removeUploadFileArea();
                        Complaint.Event.doClickLnkListItem();
                    }
                }

                ,formData: function(form) {
                    var fd = [{}];
                    fd[0].name = "complaintId";
                    fd[0].value = Complaint.getComplaintId();
                    return fd;
                }

                // This function is called when a file is added to the queue;
                // either via the browse button, or via drag/drop:
                ,add: function (e, data) {

                    var tpl = $('<li class="working"><input type="text" value="0" data-width="48" data-height="48"'+
                        ' data-fgColor="#0788a5" data-readOnly="1" data-bgColor="#3e4043" /><p></p><span></span></li>');

                    // Append the file name and file size
                    tpl.find('p').text(data.files[0].name)
                        .append('<i>' + formatFileSize(data.files[0].size) + '</i>');

                    // Add the HTML to the UL element
                    data.context = tpl.appendTo($ul);

                    // Initialize the knob plugin
                    tpl.find('input').knob();

                    // Listen for clicks on the cancel icon
                    tpl.find('span').click(function(){

                        if(tpl.hasClass('working')){
                            _jqXHR.abort();
                        }

                        tpl.fadeOut(function(){
                            tpl.remove();
                        });

                    });

                    // Automatically upload the file once it is added to the queue
                    _jqXHR = data.submit();
                }

                ,progress: function(e, data){
                    // Calculate the completion percentage of the upload
                    var progress = parseInt(data.loaded / data.total * 100, 10);

                    // Update the hidden input field and trigger a change
                    // so that the jQuery knob plugin knows to update the dial
                    data.context.find('input').val(progress).change();

                    if(progress == 100){
                        data.context.removeClass('working');
                    }
                }

                ,fail:function(e, data){
                    // Something has gone wrong!
                    data.context.addClass('error');
                }


//To Explore:
                //redirect : to complaintList
                //redirectParamName:
                //autoUpload: false
                //sequentialUploads: true
//
//check if complaintId not created, create it first
//                ,submit: function (e, data) {
//                    var input = $('#input');
//                    data.formData = {example: input.val()};
//                    if (!data.formData.example) {
//                        data.context.find('button').prop('disabled', false);
//                        input.focus();
//                        return false;
//                    }
//                }
//                ,always: function (e, data) {
//                    // data.result
//                    // data.textStatus;
//                    // data.jqXHR;
//                }

            });


            // Prevent the default action when a file is dropped on the window
            $(document).on('drop dragover', function (e) {
                e.preventDefault();
            });

            // Helper function that formats the file sizes
            function formatFileSize(bytes) {
                if (typeof bytes !== 'number') {
                    return '';
                }

                if (bytes >= 1000000000) {
                    return (bytes / 1000000000).toFixed(2) + ' GB';
                }

                if (bytes >= 1000000) {
                    return (bytes / 1000000).toFixed(2) + ' MB';
                }

                return (bytes / 1000).toFixed(2) + ' KB';
            }

        });
    }



};




