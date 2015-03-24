/**
 * Complaint is namespace component for Complaint
 *
 * @author jwu
 */
var Complaint = Complaint || {
    create: function() {
        if (Complaint.Controller.create) {Complaint.Controller.create();}
        if (Complaint.Model.create)      {Complaint.Model.create();}
        if (Complaint.View.create)       {Complaint.View.create();}

        if (ObjNav.create) {
            ObjNav.create({name: "complaint"
                ,$tree            : Complaint.View.Navigator.$tree
                ,treeArgs         : Complaint.View.Navigator.getTreeArgs()
                ,$ulFilter        : Complaint.View.Navigator.$ulFilter
                ,treeFilter       : Complaint.View.MicroData.treeFilter
                ,$ulSort          : Complaint.View.Navigator.$ulSort
                ,treeSort         : Complaint.View.MicroData.treeSort
                ,modelInterface   : Complaint.Model.interface
            });
        }

        if (DocTree.create) {
            DocTree.create({name: "complaint"
                ,parentType        : Complaint.Model.DOC_TYPE_COMPLAINT
//                ,parentId          : null
//                ,$tree            : Complaint.View.Documents.$tree
//                ,treeArgs         : Complaint.View.Documents.getTreeArgs()
//                ,getActiveObjId     : ObjNav.View.Navigator.getActiveObjId
//                ,getPreviousObjId   : ObjNav.View.Navigator.getPreviousObjId
//                ,getContextMenu     : Complaint.View.Documents.getContextMenu()
            });
        }

        if (SubscriptionOp.create) {
            SubscriptionOp.create({
                getSubscriptionInfo: function() {
                    return {userId: App.getUserName()
                        ,objectType: Complaint.Model.DOC_TYPE_COMPLAINT
                        ,objectId: Complaint.Model.getComplaintId()
                    };
                }
            });
        }

        //this.test();
    }

    ,onInitialized: function() {
        if (Complaint.Controller.onInitialized) {Complaint.Controller.onInitialized();}
        if (Complaint.Model.onInitialized)      {Complaint.Model.onInitialized();}
        if (Complaint.View.onInitialized)       {Complaint.View.onInitialized();}

        if (ObjNav.onInitialized)               {ObjNav.onInitialized();}
        if (DocTree.onInitialized)              {DocTree.onInitialized();}
        if (SubscriptionOp.onInitialized)       {SubscriptionOp.onInitialized();}
    }


    ,test: function() {
        CLIPBOARD = null;

        var $treeDoc = $("#treeDoc");
        $treeDoc.fancytree({

            extensions: ["table", "gridnav", "edit", "dnd"]
            ,checkbox: true
            ,table: {
                indentation: 10,      // indent 20px per node level
                nodeColumnIdx: 2,     // render the node title into the 2nd column
                checkboxColumnIdx: 0  // render the checkboxes into the 1st column
            }
            ,gridnav: {
                autofocusInput: false,
                handleCursorKeys: true
            }
            ,renderColumns: function(event, data) {
                var node = data.node,
                    $tdList = $(node.tr).find(">td");
                // (index #0 is rendered by fancytree by adding the checkbox)
                $tdList.eq(1).text("[ID]");


                $tdList.eq(3).text(node.data.type);
                $tdList.eq(4).text(node.data.created);
                $tdList.eq(5).text(node.data.author);
                $tdList.eq(6).text(node.data.version);
                $tdList.eq(7).text(node.data.status);
                $tdList.eq(8).html(node.data.action);



                // (index #2 is rendered by fancytree)
                //$tdList.eq(3).text(node.key);
                //$tdList.eq(4).html("<input type='checkbox' name='like' value='" + node.key + "'>");
            }

            ,source: [
                {"title": "Folder 1", "expanded": true, "folder": true, "action":"<div class='btn-group'><button type='buton' class='dropdown-toggle' data-toggle='dropdown'> <i class='fa fa-cog'></i> </button><ul class='dropdown-menu'><li><a href='#'>Add Subfolder</a></li><li><a href='#'>Add Document</a></li><li><a href='#'>Delete Subfolder</a></li></ul></div>",  "children": [
                    {"title": "Document 1", "type":"[type]", "created":"[created]", "author":"[author]", "version":"[version]", "due":"[due]", "status":"[status]", "action":"<div class='btn-group'><button type='buton' class='dropdown-toggle' data-toggle='dropdown'> <i class='fa fa-cog'></i> </button><ul class='dropdown-menu'><li><a href='#'>Download</a></li><li><a href='#'>Replace</a></li><li><a href='#'>History</a></li><li><a href='#'>Delete</a></li><li><a href='#'>Copy</a></li><li><a href='#'>Move</a></li><li><a href='#'>Edit</a></li><li><a href='#'>View</a></li></ul></div>"},
                    {"title": "Document 2", "type":"[type]", "created":"[created]", "author":"[author]", "version":"[version]", "due":"[due]", "status":"[status]", "action":"<div class='btn-group'><button type='buton' class='dropdown-toggle' data-toggle='dropdown'> <i class='fa fa-cog'></i> </button><ul class='dropdown-menu'><li><a href='#'>Download</a></li><li><a href='#'>Replace</a></li><li><a href='#'>History</a></li><li><a href='#'>Delete</a></li><li><a href='#'>Copy</a></li><li><a href='#'>Move</a></li><li><a href='#'>Edit</a></li><li><a href='#'>View</a></li></ul></div>"},
                    {"title": "Document 3", "type":"[type]", "created":"[created]", "author":"[author]", "version":"[version]", "due":"[due]", "status":"[status]", "action":"<div class='btn-group'><button type='buton' class='dropdown-toggle' data-toggle='dropdown'> <i class='fa fa-cog'></i> </button><ul class='dropdown-menu'><li><a href='#'>Download</a></li><li><a href='#'>Replace</a></li><li><a href='#'>History</a></li><li><a href='#'>Delete</a></li><li><a href='#'>Copy</a></li><li><a href='#'>Move</a></li><li><a href='#'>Edit</a></li><li><a href='#'>View</a></li></ul></div>"},
                    {"title": "Document 4", "type":"[type]", "created":"[created]", "author":"[author]", "version":"[version]", "due":"[due]", "status":"[status]", "action":"<div class='btn-group'><button type='buton' class='dropdown-toggle' data-toggle='dropdown'> <i class='fa fa-cog'></i> </button><ul class='dropdown-menu'><li><a href='#'>Download</a></li><li><a href='#'>Replace</a></li><li><a href='#'>History</a></li><li><a href='#'>Delete</a></li><li><a href='#'>Copy</a></li><li><a href='#'>Move</a></li><li><a href='#'>Edit</a></li><li><a href='#'>View</a></li></ul></div>"}
                ]}
                ,{"title": "Folder 2", "expanded": true, "folder": true, "action":"<div class='btn-group'><button type='buton' class='dropdown-toggle' data-toggle='dropdown'> <i class='fa fa-cog'></i> </button><ul class='dropdown-menu'><li><a href='#'>Add Subfolder</a></li><li><a href='#'>Add Document</a></li><li><a href='#'>Delete Subfolder</a></li></ul></div>",  "children": [
                    {"title": "Document 2.1", "type":"[type]", "created":"[created]", "author":"[author]", "version":"[version]", "due":"[due]", "status":"[status]", "action":"<div class='btn-group'><button type='buton' class='dropdown-toggle' data-toggle='dropdown'> <i class='fa fa-cog'></i> </button><ul class='dropdown-menu'><li><a href='#'>Download</a></li><li><a href='#'>Replace</a></li><li><a href='#'>History</a></li><li><a href='#'>Delete</a></li><li><a href='#'>Copy</a></li><li><a href='#'>Move</a></li><li><a href='#'>Edit</a></li><li><a href='#'>View</a></li></ul></div>"},
                    {"title": "Folder 2.2", "folder":true, "action":"<div class='btn-group'><button type='buton' class='dropdown-toggle' data-toggle='dropdown'> <i class='fa fa-cog'></i> </button><ul class='dropdown-menu'><li><a href='#'>Download</a></li><li><a href='#'>Replace</a></li><li><a href='#'>History</a></li><li><a href='#'>Delete</a></li><li><a href='#'>Copy</a></li><li><a href='#'>Move</a></li><li><a href='#'>Edit</a></li><li><a href='#'>View</a></li></ul></div>", "children": [
                        {"title": "Document 2.1", "type":"[type]", "created":"[created]", "author":"[author]", "version":"[version]", "due":"[due]", "status":"[status]", "action":"<div class='btn-group'><button type='buton' class='dropdown-toggle' data-toggle='dropdown'> <i class='fa fa-cog'></i> </button><ul class='dropdown-menu'><li><a href='#'>Download</a></li><li><a href='#'>Replace</a></li><li><a href='#'>History</a></li><li><a href='#'>Delete</a></li><li><a href='#'>Copy</a></li><li><a href='#'>Move</a></li><li><a href='#'>Edit</a></li><li><a href='#'>View</a></li></ul></div>"},
                        {"title": "Document 2.2", "type":"[type]", "created":"[created]", "author":"[author]", "version":"[version]", "due":"[due]", "status":"[status]", "action":"<div class='btn-group'><button type='buton' class='dropdown-toggle' data-toggle='dropdown'> <i class='fa fa-cog'></i> </button><ul class='dropdown-menu'><li><a href='#'>Download</a></li><li><a href='#'>Replace</a></li><li><a href='#'>History</a></li><li><a href='#'>Delete</a></li><li><a href='#'>Copy</a></li><li><a href='#'>Move</a></li><li><a href='#'>Edit</a></li><li><a href='#'>View</a></li></ul></div>"},
                        {"title": "Document 2.3", "type":"[type]", "created":"[created]", "author":"[author]", "version":"[version]", "due":"[due]", "status":"[status]", "action":"<div class='btn-group'><button type='buton' class='dropdown-toggle' data-toggle='dropdown'> <i class='fa fa-cog'></i> </button><ul class='dropdown-menu'><li><a href='#'>Download</a></li><li><a href='#'>Replace</a></li><li><a href='#'>History</a></li><li><a href='#'>Delete</a></li><li><a href='#'>Copy</a></li><li><a href='#'>Move</a></li><li><a href='#'>Edit</a></li><li><a href='#'>View</a></li></ul></div>"},
                        {"title": "Document 2.4", "type":"[type]", "created":"[created]", "author":"[author]", "version":"[version]", "due":"[due]", "status":"[status]", "action":"<div class='btn-group'><button type='buton' class='dropdown-toggle' data-toggle='dropdown'> <i class='fa fa-cog'></i> </button><ul class='dropdown-menu'><li><a href='#'>Download</a></li><li><a href='#'>Replace</a></li><li><a href='#'>History</a></li><li><a href='#'>Delete</a></li><li><a href='#'>Copy</a></li><li><a href='#'>Move</a></li><li><a href='#'>Edit</a></li><li><a href='#'>View</a></li></ul></div>"}
                    ]},
                    {"title": "Document 2.3", "type":"[type]", "created":"[created]", "author":"[author]", "version":"[version]", "due":"[due]", "status":"[status]", "action":"<div class='btn-group'><button type='buton' class='dropdown-toggle' data-toggle='dropdown'> <i class='fa fa-cog'></i> </button><ul class='dropdown-menu'><li><a href='#'>Download</a></li><li><a href='#'>Replace</a></li><li><a href='#'>History</a></li><li><a href='#'>Delete</a></li><li><a href='#'>Copy</a></li><li><a href='#'>Move</a></li><li><a href='#'>Edit</a></li><li><a href='#'>View</a></li></ul></div>"},
                    {"title": "Document 2.4", "type":"[type]", "created":"[created]", "author":"[author]", "version":"[version]", "due":"[due]", "status":"[status]", "action":"<div class='btn-group'><button type='buton' class='dropdown-toggle' data-toggle='dropdown'> <i class='fa fa-cog'></i> </button><ul class='dropdown-menu'><li><a href='#'>Download</a></li><li><a href='#'>Replace</a></li><li><a href='#'>History</a></li><li><a href='#'>Delete</a></li><li><a href='#'>Copy</a></li><li><a href='#'>Move</a></li><li><a href='#'>Edit</a></li><li><a href='#'>View</a></li></ul></div>"}
                ]}
            ]
            ,edit: {
                triggerStart: ["f2", "dblclick", "shift+click", "mac+enter"]
                ,beforeEdit: function(event, data){
                    // Return false to prevent edit mode
                }
                ,edit: function(event, data){
                    // Editor was opened (available as data.input)
                }
                ,beforeClose: function(event, data){
                    // Return false to prevent cancel/save (data.input is available)
                }
                ,save: function(event, data){
                    // Save data.input.val() or return false to keep editor open
                    console.log("save...", this, data);
                    // Simulate to start a slow ajax request...
                    setTimeout(function(){
                        $(data.node.span).removeClass("pending");
                        // Let's pretend the server returned a slightly modified
                        // title:
                        data.node.setTitle(data.node.title + "!");
                    }, 2000);
                    // We return true, so ext-edit will set the current user input
                    // as title
                    return true;
                }
                ,close: function(event, data){
                    // Editor was removed
                    if( data.save ) {
                        // Since we started an async request, mark the node as preliminary
                        $(data.node.span).addClass("pending");
                    }
                }
            }
            ,dnd: {
                autoExpandMS: 400,
                focusOnClick: true,
                preventVoidMoves: true, // Prevent dropping nodes 'before self', etc.
                preventRecursiveMoves: true, // Prevent dropping nodes on own descendants
                dragStart: function(node, data) {
//                     This function MUST be defined to enable dragging for the tree.
//                     Return false to cancel dragging of node.

                    return true;
                },
                dragEnter: function(node, data) {
//                     data.otherNode may be null for non-fancytree droppables.
//                     Return false to disallow dropping on node. In this case
//                     dragOver and dragLeave are not called.
//                     Return 'over', 'before, or 'after' to force a hitMode.
//                     Return ['before', 'after'] to restrict available hitModes.
//                     Any other return value will calc the hitMode from the cursor position.

//                    // Prevent dropping a parent below another parent (only sort
//                    // nodes under the same parent)
//                               if(node.parent !== data.otherNode.parent){
//                     return false;
//                     }
//                     // Don't allow dropping *over* a node (would create a child)
//                     return ["before", "after"];

                    return true;
                },
                dragDrop: function(node, data) {
//                     This function MUST be defined to enable dropping of items on the tree.

                    data.otherNode.moveTo(node, data.hitMode);
                }
            }

        }).on("nodeCommand", function(event, data){
            // Custom event handler that is triggered by keydown-handler and
            // context menu:
            var refNode, moveMode,
                tree = $(this).fancytree("getTree"),
                node = tree.getActiveNode();

            switch( data.cmd ) {
                case "moveUp":
                    refNode = node.getPrevSibling();
                    if( refNode ) {
                        node.moveTo(refNode, "before");
                        node.setActive();
                    }
                    break;
                case "moveDown":
                    refNode = node.getNextSibling();
                    if( refNode ) {
                        node.moveTo(refNode, "after");
                        node.setActive();
                    }
                    break;
                case "indent":
                    refNode = node.getPrevSibling();
                    if( refNode ) {
                        node.moveTo(refNode, "child");
                        refNode.setExpanded();
                        node.setActive();
                    }
                    break;
                case "outdent":
                    if( !node.isTopLevel() ) {
                        node.moveTo(node.getParent(), "after");
                        node.setActive();
                    }
                    break;
                case "rename":
                    node.editStart();
                    break;
                case "remove":
                    refNode = node.getNextSibling() || node.getPrevSibling() || node.getParent();
                    node.remove();
                    if( refNode ) {
                        refNode.setActive();
                    }
                    break;
                case "addChild":
                    node.editCreateNode("child", "");
                    break;
                case "addSibling":
                    node.editCreateNode("after", "");
                    break;
                case "cut":
                    CLIPBOARD = {mode: data.cmd, data: node};
                    break;
                case "copy":
                    CLIPBOARD = {
                        mode: data.cmd,
                        data: node.toDict(function(n){
                            delete n.key;
                        })
                    };
                    break;
                case "clear":
                    CLIPBOARD = null;
                    break;
                case "paste":
                    if( CLIPBOARD.mode === "cut" ) {
                        // refNode = node.getPrevSibling();
                        CLIPBOARD.data.moveTo(node, "child");
                        CLIPBOARD.data.setActive();
                    } else if( CLIPBOARD.mode === "copy" ) {
                        node.addChildren(CLIPBOARD.data).setActive();
                    }
                    break;
                default:
                    alert("Unhandled command: " + data.cmd);
                    return;
            }
        }).on("keydown", function(e){
            var cmd = null;

            // console.log(e.type, $.ui.fancytree.eventToString(e));
            switch( $.ui.fancytree.eventToString(e) ) {
                case "ctrl+shift+n":
                case "meta+shift+n": // mac: cmd+shift+n
                    cmd = "addChild";
                    break;
                case "ctrl+c":
                case "meta+c": // mac
                    cmd = "copy";
                    break;
                case "ctrl+v":
                case "meta+v": // mac
                    cmd = "paste";
                    break;
                case "ctrl+x":
                case "meta+x": // mac
                    cmd = "cut";
                    break;
                case "ctrl+n":
                case "meta+n": // mac
                    cmd = "addSibling";
                    break;
                case "del":
                case "meta+backspace": // mac
                    cmd = "remove";
                    break;
                // case "f2":  // already triggered by ext-edit pluging
                //   cmd = "rename";
                //   break;
                case "ctrl+up":
                    cmd = "moveUp";
                    break;
                case "ctrl+down":
                    cmd = "moveDown";
                    break;
                case "ctrl+right":
                case "ctrl+shift+right": // mac
                    cmd = "indent";
                    break;
                case "ctrl+left":
                case "ctrl+shift+left": // mac
                    cmd = "outdent";
            }
            if( cmd ){
                $(this).trigger("nodeCommand", {cmd: cmd});
                // e.preventDefault();
                // e.stopPropagation();
                return false;
            }
        });

        $treeDoc.contextmenu({
            delegate: "span.fancytree-node",
            menu: [
                {title: "Edit <kbd>[F2]</kbd>", cmd: "rename", uiIcon: "ui-icon-pencil" },
                {title: "Delete <kbd>[Del]</kbd>", cmd: "remove", uiIcon: "ui-icon-trash" },
                {title: "----"},
                {title: "New sibling <kbd>[Ctrl+N]</kbd>", cmd: "addSibling", uiIcon: "ui-icon-plus" },
                {title: "New child <kbd>[Ctrl+Shift+N]</kbd>", cmd: "addChild", uiIcon: "ui-icon-arrowreturn-1-e" },
                {title: "----"},
                {title: "Cut <kbd>Ctrl+X</kbd>", cmd: "cut", uiIcon: "ui-icon-scissors"},
                {title: "Copy <kbd>Ctrl-C</kbd>", cmd: "copy", uiIcon: "ui-icon-copy"},
                {title: "Paste as child<kbd>Ctrl+V</kbd>", cmd: "paste", uiIcon: "ui-icon-clipboard", disabled: true }
            ],
            beforeOpen: function(event, ui) {
                var node = $.ui.fancytree.getNode(ui.target);
                $treeDoc.contextmenu("enableEntry", "paste", !!CLIPBOARD);
                node.setActive();
            },
            select: function(event, ui) {
                var that = this;
                // delay the event, so the menu can close and the click event does
                // not interfere with the edit control
                setTimeout(function(){
                    $(that).trigger("nodeCommand", {cmd: ui.cmd});
                }, 100);
            }
        });

    }
};

