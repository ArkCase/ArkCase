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

    }

    ,onInitialized: function() {
        if (Complaint.Controller.onInitialized) {Complaint.Controller.onInitialized();}
        if (Complaint.Model.onInitialized)      {Complaint.Model.onInitialized();}
        if (Complaint.View.onInitialized)       {Complaint.View.onInitialized();}

        if (ObjNav.onInitialized)               {ObjNav.onInitialized();}
        if (SubscriptionOp.onInitialized)       {SubscriptionOp.onInitialized();}
    }

    ,test: function() {
        var $t = $("#treetable2");
        $("#treetable").fancytree({
//            source: [{
//                title: "2014-03-12321 - Sample Complaint Title</em>",
//                href: "complaints.html",
//                tooltip: "2014-03-12321",
//                expanded: "fancytree-expanded",
//
//                children: [{
//                    title: "Initiator",
//                    href: "complaintIntitiator.html",
//                    description1: "John Doe",
//                    description2: "Victim"
//                },{
//                    title: "Details",
//                    href: "complaintDetails.html"
//                }, {
//                    title: "People",
//                    href: "complaintPeople.html"
//                }, {
//                    title: "Documents",
//                    expanded: "fancytree-expanded",
//                    href: "complaintDocuments.html",
//                    expanded: "fancytree-expanded"
//                }, {
//                    title: "Participants",
//                    folder: false,
//                    href: "complaintParticipants.html"
//                }, {
//                    title: "Notes",
//                    href: "complaintNotes.html"
//                }, {
//                    title: "Tasks",
//                    href: "complaintTasks.html",
//                    nodeType: "task"
//                }, {
//                    title: "References",
//                    href: "complaintOther.html"
//                }, {
//                    title: "History",
//                    folder: false,
//                    href: "complaintHistory.html"
//                }]
//            }]
//            ,

            extensions: ["table", "edit", "dnd"]
            ,checkbox: true
            ,table: {
                indentation: 10,      // indent 20px per node level
                nodeColumnIdx: 2,     // render the node title into the 2nd column
                checkboxColumnIdx: 0  // render the checkboxes into the 1st column
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

        });
    }
};

