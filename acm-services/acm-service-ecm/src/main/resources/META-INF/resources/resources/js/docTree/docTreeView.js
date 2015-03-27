/**
 * DocTree.View
 *
 * @author jwu
 */
DocTree.View = DocTree.View || {
    create : function(args) {
        this.$imgFileLoading   = $("#imgFileLoading");
        this.$formDoc          = $("#formDoc");
        this.$fileInput        = $("#file");

        this.$lnkChangePicture = $("#lnkChangePicture");
        this.$lnkChangePicture.on("click", function(e) {DocTree.View.onClickLnkChangePicture(e, this);});

        this.$fileInput.on("change", function(e) {DocTree.View.onChangeFileInput(e, this);});
        this.$formDoc.submit(function(e) {DocTree.View.onSubmitFormDoc(e, this);});
        this.uploadToFolderNode = null;


        this.$tree = (args.$tree)? args.$tree : $("#treeDoc");
        this.createDocTree(args.treeArgs);
        this.parentType = args.parentType;
        this.parentId = args.parentId;
        //this.getActiveObjId = (args.getActiveObjId)?args.getActiveObjId : ObjNav.View.Navigator.getActiveObjId;
        //this.getPreviousObjId = (args.getPreviousObjId)?args.getPreviousObjId : ObjNav.View.Navigator.getPreviousObjId;

        Acm.Dispatcher.addEventListener(DocTree.Controller.VIEW_CHANGED_PARENT           ,this.onViewChangedParent);
        Acm.Dispatcher.addEventListener(DocTree.Controller.VIEW_CHANGED_TREE             ,this.onViewChangedTree);
        Acm.Dispatcher.addEventListener(DocTree.Controller.MODEL_UPLOADED_FILE           ,this.onModelUploadedFile);


        Acm.Dispatcher.addEventListener(DocTree.Controller.MODEL_ADDED_FOLDER            ,this.onModelAddedFolder);
        Acm.Dispatcher.addEventListener(DocTree.Controller.MODEL_ADDED_DOCUMENT          ,this.onModelAddedDocument);
    }
    ,onInitialized: function() {
    }


    ,onClickLnkChangePicture: function(event, ctrl) {
        DocTree.View.$fileInput.click();
    }

    ,uploadFile: function(node) {
        DocTree.View.uploadToFolderNode = node;
        DocTree.View.$fileInput.click();
    }
    ,onChangeFileInput: function(event, ctrl) {
        DocTree.View.$formDoc.submit();
    }

    ,_doAddFileNode: function(node, name) {
        return node.addChildren({"title": name, "loadStatus": "loading", "action": DocTree.View.getHtmlActionDocument()});
    }
    ,onSubmitFormDoc: function(event, ctrl) {
        event.preventDefault();

        var _this = DocTree.View;
        var fd = new FormData();
        fd.append("parentObjectType", DocTree.Model.getObjType());
        fd.append("parentObjectId", DocTree.Model.getObjId());
        fd.append("fileType", "other");
        fd.append("category", "Document");
        fd.append("file", _this.$fileInput[0].files[0]);
        DocTree.Service.uploadFile(fd, 0);
        return;


        var _this = DocTree.View;
        var name = _this.$fileInput[0].files[0].name;
        var node = DocTree.View.uploadToFolderNode;
        var child;
        if (node.lazy && !node.children) {
            node.setExpanded(true).always(function(){// Wait until expand finished, then do the paste
                child = DocTree.View._doAddFileNode(node, name);
                child.setStatus("loading");
            });
        } else {
            child = DocTree.View._doAddFileNode(node, name);
            child.setStatus("loading");
        }

        //var cacheKey = DocTree.Model.getCacheKey(node.data.folderId, node.data.startRow);
        //DocTree.Model.cacheFolder.remove(cacheKey);

        var fd = new FormData();
        fd.append("parentObjectType", DocTree.Model.getObjType());
        fd.append("parentObjectId", DocTree.Model.getObjId());
        if (0 < node.data.folderId) {
            fd.append("parentFolderId", node.data.folderId);
        }
        fd.append("fileType", "roi");
        fd.append("category", "Document");
        fd.append("file", _this.$fileInput[0].files[0]);
        DocTree.Service.uploadFile(fd, node.key);
    }

    ,showImgFileLoading: function(show) {
        Acm.Object.show(this.$imgFileLoading, show);
    }


    ,onViewChangedParent: function(objType, objId) {
        DocTree.View.switchObject(objType, objId);
    }
    ,onViewChangedTree: function() {
        $("a[cmd]").on("click", function(e) {
            e.preventDefault();
            var cmd = $(this).attr("cmd");
            if (Acm.isNotEmpty(cmd)) {
                // delay the event, so the menu can close and the click event does
                // not interfere with the edit control
                setTimeout(function(){
                    DocTree.View.$tree.trigger("command", {cmd: cmd});
                }, 100);
            }
        });
    }
    ,onModelUploadedFile: function(uploadInfo, key) {
        var node = DocTree.View.tree.getNodeByKey(key);
        if (node) {
            if (uploadInfo.hasError) {
                //node.setStatus("error");
                var z = 1;
            } else {
                //node.setStatus("ok");
                var z = 2;
            }

        }
        var z = 3;

    }
    ,onModelAddedFolder: function(node, parentId, folder) {

        $(node.span).removeClass("pending");
        // Let's pretend the server returned a slightly modified
        // title:
        node.setTitle(node.title + "!");
        //var $divError = $("#divError");
        //$divError.slideUp("slow");

        App.View.ErrorBoard.show("hello folder", "world");
        var z = 1;
    }
    ,onModelAddedDocument: function(node, parentId, folder) {
        //var $divError = $("#divError");
        //$divError.slideDown("slow");

//        App.View.ErrorBoard.showBtnDetail(true);
//        App.View.ErrorBoard.showDivBoard(false);
//        App.View.ErrorBoard.showBtnDetail(false);
//        App.View.ErrorBoard.showDivBoard(true);
//        App.View.ErrorBoard.showBtnDetail(true);
//        App.View.ErrorBoard.showBtnDetail(false);

        App.View.ErrorBoard.show("hello doc");
        var z = 1;
    }

    ,showDialog: function(args) {
        if (Acm.isEmpty(args.$dlgDocumentPicker)) {
            args.$dlgDocumentPicker = $("#dlgDocumentPicker");
        }
        if (Acm.isNotEmpty(args.title)) {
            args.$dlgDocumentPicker.find('.modal-title').text(args.title);
        }
        if (Acm.isNotEmpty(args.btnOkText)) {
            args.$dlgDocumentPicker.find('button.btn-primary').text(args.btnOkText);
        }
        if (Acm.isNotEmpty(args.btnCancelText)) {
            args.$dlgDocumentPicker.find('button.btn-default').text(args.btnCancelText);
        }
        Acm.Dialog.modal(args.$dlgDocumentPicker, args.onClickBtnPrimary, args.onClickBtnDefault);
    }

    ,_isEditing: false
    ,isEditing: function() {
        return this._isEditing;
    }
    ,setEditing: function(isEditing) {
        this._isEditing = isEditing;
    }
    ,CLIPBOARD : null
    ,_getDefaultTreeArgs: function() {
        return {
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
                $tdList.eq(1).text(node.data.id);


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
            ,beforeexpand: function(event, data) {
                return true;
            }
            ,expand: function(event, data) {
                var z=1;
            }
            ,source: DocTree.View.source()
            ,lazyLoad: DocTree.View.lazyLoad
            ,edit: {
                triggerStart: ["f2", "dblclick", "shift+click", "mac+enter"]
                ,beforeEdit: function(event, data){
                    if (data.node.data.root) {
                        return false;// Return false to prevent edit mode
                    }
                    if (data.node.isLoading()) {
                        return false;
                    }
                    DocTree.View.setEditing(true);
                    var z = 1;
                }
                ,edit: function(event, data){
                    // Editor was opened (available as data.input)
                    var z = 1;
                }
                ,beforeClose: function(event, data){
                    // Return false to prevent cancel/save (data.input is available)
                    var z = 1;
                }
                ,save: function(event, data){
                    var parentId = null;
                    var parent = data.node.getParent();
                    if (parent) {
                        parentId = parent.data.id;
                    }
                    var name = data.input.val();
                    var key = data.node.key;
                    if (data.isNew) {
                        if (data.node.folder) {
                            DocTree.Controller.viewAddedFolder(data.node, parentId, name);
                        } else {
                            DocTree.Controller.viewAddedDocument(data.node, parentId, name);
                        }
                    } else {
                        var id = data.node.data.id;
                        if (data.node.folder) {
                            DocTree.Controller.viewRenamedFolder(data.node, id, parentId, name);
                        } else {
                            DocTree.Controller.viewRenamedDocument(data.node, id, parentId, name);
                        }
                    }

//                    console.log("save...", this, data);
//                    // Simulate to start a slow ajax request...
//                    setTimeout(function(){
//                        $(data.node.span).removeClass("pending");
//                        // Let's pretend the server returned a slightly modified
//                        // title:
//                        data.node.setTitle(data.node.title + "!");
//                    }, 2000);
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
                    DocTree.View.setEditing(false);
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
                    if (DocTree.View.isEditing()) {
                        return false;
                    }

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
//                        if(node.parent !== data.otherNode.parent){
//                            return false;
//                        }
//
                    if (data.node.folder) {
                        return true;
                    } else {
                        return ["before", "after"];  // Don't allow dropping *over* a document node (would create a child)
                    }
                },
                dragDrop: function(node, data) {
                    if (node.lazy && !node.children) {
                        node.setExpanded(true).always(function(){
                            // Wait until expand finished, then add the additional child
                            data.otherNode.moveTo(node, data.hitMode);
                        });
                    } else {
                        data.otherNode.moveTo(node, data.hitMode);
                    }
                }
            }

        };
    }

    ,createDocTree: function(treeArgs) {
        var treeArgsToUse = this._getDefaultTreeArgs();
        for (var arg in treeArgs) {
            treeArgsToUse[arg] = treeArgs[arg];
        }

        var $tree = this.$tree;
        $tree.fancytree(treeArgsToUse)
            .on("command", DocTree.View.onCommand)
            .on("keydown", DocTree.View.onKeyDown);

        this.tree = $tree.fancytree("getTree");

        $tree.contextmenu({
            delegate: "span.fancytree-node"
            ,menu: []
            ,beforeOpen: function(event, ui) {
                var node = $.ui.fancytree.getNode(ui.target);
                $tree.contextmenu("replaceMenu", DocTree.View.getContextMenu(node));
                $tree.contextmenu("enableEntry", "paste", !!DocTree.View.CLIPBOARD);
                node.setActive();
            }
            ,select: function(event, ui) {
                var that = this;
                //var node = $.ui.fancytree.getNode(ui.target);

                // delay the event, so the menu can close and the click event does
                // not interfere with the edit control
                setTimeout(function(){
                    $(that).trigger("command", {cmd: ui.cmd});
                }, 100);
            }
        });

    }
    ,source0: function() {
        var src = [{"id":"root", "title": "/", "expanded": true, "folder": true, "action":"",  "children": [
            {"id":"f1", "title": "Folder 1", "expanded": true, "folder": true, "action":"<div class='btn-group'><button type='buton' class='dropdown-toggle' data-toggle='dropdown'> <i class='fa fa-cog'></i> </button><ul class='dropdown-menu'><li><a href='#'>Add Subfolder</a></li><li><a href='#'>Add Document</a></li><li><a href='#'>Delete Subfolder</a></li></ul></div>",  "children": [
                {"id":"d1", "title": "Document 1", "type":"[type]", "created":"[created]", "author":"[author]", "version":"[version]", "due":"[due]", "status":"[status]", "action":"<div class='btn-group'><button type='buton' class='dropdown-toggle' data-toggle='dropdown'> <i class='fa fa-cog'></i> </button><ul class='dropdown-menu'><li><a href='#'>Download</a></li><li><a href='#'>Replace</a></li><li><a href='#'>History</a></li><li><a href='#'>Delete</a></li><li><a href='#'>Copy</a></li><li><a href='#'>Move</a></li><li><a href='#'>Edit</a></li><li><a href='#'>View</a></li></ul></div>"},
                {"id":"d2", "title": "Document 2", "type":"[type]", "created":"[created]", "author":"[author]", "version":"[version]", "due":"[due]", "status":"[status]", "action":"<div class='btn-group'><button type='buton' class='dropdown-toggle' data-toggle='dropdown'> <i class='fa fa-cog'></i> </button><ul class='dropdown-menu'><li><a href='#'>Download</a></li><li><a href='#'>Replace</a></li><li><a href='#'>History</a></li><li><a href='#'>Delete</a></li><li><a href='#'>Copy</a></li><li><a href='#'>Move</a></li><li><a href='#'>Edit</a></li><li><a href='#'>View</a></li></ul></div>"},
                {"id":"d3", "title": "Document 3", "type":"[type]", "created":"[created]", "author":"[author]", "version":"[version]", "due":"[due]", "status":"[status]", "action":"<div class='btn-group'><button type='buton' class='dropdown-toggle' data-toggle='dropdown'> <i class='fa fa-cog'></i> </button><ul class='dropdown-menu'><li><a href='#'>Download</a></li><li><a href='#'>Replace</a></li><li><a href='#'>History</a></li><li><a href='#'>Delete</a></li><li><a href='#'>Copy</a></li><li><a href='#'>Move</a></li><li><a href='#'>Edit</a></li><li><a href='#'>View</a></li></ul></div>"},
                {"id":"d4", "title": "Document 4", "type":"[type]", "created":"[created]", "author":"[author]", "version":"[version]", "due":"[due]", "status":"[status]", "action":"<div class='btn-group'><button type='buton' class='dropdown-toggle' data-toggle='dropdown'> <i class='fa fa-cog'></i> </button><ul class='dropdown-menu'><li><a href='#'>Download</a></li><li><a href='#'>Replace</a></li><li><a href='#'>History</a></li><li><a href='#'>Delete</a></li><li><a href='#'>Copy</a></li><li><a href='#'>Move</a></li><li><a href='#'>Edit</a></li><li><a href='#'>View</a></li></ul></div>"}
            ]}
            ,{"id":"f2", "title": "Folder 2", "expanded": true, "folder": true, "action":"<div class='btn-group'><button type='buton' class='dropdown-toggle' data-toggle='dropdown'> <i class='fa fa-cog'></i> </button><ul class='dropdown-menu'><li><a href='#'>Add Subfolder</a></li><li><a href='#'>Add Document</a></li><li><a href='#'>Delete Subfolder</a></li></ul></div>",  "children": [
                {"id":"d2.1", "title": "Document 2.1", "type":"[type]", "created":"[created]", "author":"[author]", "version":"[version]", "due":"[due]", "status":"[status]", "action":"<div class='btn-group'><button type='buton' class='dropdown-toggle' data-toggle='dropdown'> <i class='fa fa-cog'></i> </button><ul class='dropdown-menu'><li><a href='#'>Download</a></li><li><a href='#'>Replace</a></li><li><a href='#'>History</a></li><li><a href='#'>Delete</a></li><li><a href='#'>Copy</a></li><li><a href='#'>Move</a></li><li><a href='#'>Edit</a></li><li><a href='#'>View</a></li></ul></div>"},
                {"id":"f2.2", "title": "Folder 2.2", "folder":true, "action":"<div class='btn-group'><button type='buton' class='dropdown-toggle' data-toggle='dropdown'> <i class='fa fa-cog'></i> </button><ul class='dropdown-menu'><li><a href='#'>Download</a></li><li><a href='#'>Replace</a></li><li><a href='#'>History</a></li><li><a href='#'>Delete</a></li><li><a href='#'>Copy</a></li><li><a href='#'>Move</a></li><li><a href='#'>Edit</a></li><li><a href='#'>View</a></li></ul></div>", "children": [
                    {"id":"d2.2.1", "title": "Document 2.2.1", "type":"[type]", "created":"[created]", "author":"[author]", "version":"[version]", "due":"[due]", "status":"[status]", "action":"<div class='btn-group'><button type='buton' class='dropdown-toggle' data-toggle='dropdown'> <i class='fa fa-cog'></i> </button><ul class='dropdown-menu'><li><a href='#'>Download</a></li><li><a href='#'>Replace</a></li><li><a href='#'>History</a></li><li><a href='#'>Delete</a></li><li><a href='#'>Copy</a></li><li><a href='#'>Move</a></li><li><a href='#'>Edit</a></li><li><a href='#'>View</a></li></ul></div>"},
                    {"id":"d2.2.2", "title": "Document 2.2.2", "type":"[type]", "created":"[created]", "author":"[author]", "version":"[version]", "due":"[due]", "status":"[status]", "action":"<div class='btn-group'><button type='buton' class='dropdown-toggle' data-toggle='dropdown'> <i class='fa fa-cog'></i> </button><ul class='dropdown-menu'><li><a href='#'>Download</a></li><li><a href='#'>Replace</a></li><li><a href='#'>History</a></li><li><a href='#'>Delete</a></li><li><a href='#'>Copy</a></li><li><a href='#'>Move</a></li><li><a href='#'>Edit</a></li><li><a href='#'>View</a></li></ul></div>"},
                    {"id":"d2.2.3", "title": "Document 2.2.3", "type":"[type]", "created":"[created]", "author":"[author]", "version":"[version]", "due":"[due]", "status":"[status]", "action":"<div class='btn-group'><button type='buton' class='dropdown-toggle' data-toggle='dropdown'> <i class='fa fa-cog'></i> </button><ul class='dropdown-menu'><li><a href='#'>Download</a></li><li><a href='#'>Replace</a></li><li><a href='#'>History</a></li><li><a href='#'>Delete</a></li><li><a href='#'>Copy</a></li><li><a href='#'>Move</a></li><li><a href='#'>Edit</a></li><li><a href='#'>View</a></li></ul></div>"},
                    {"id":"d2.2.4", "title": "Document 2.2.4", "type":"[type]", "created":"[created]", "author":"[author]", "version":"[version]", "due":"[due]", "status":"[status]", "action":"<div class='btn-group'><button type='buton' class='dropdown-toggle' data-toggle='dropdown'> <i class='fa fa-cog'></i> </button><ul class='dropdown-menu'><li><a href='#'>Download</a></li><li><a href='#'>Replace</a></li><li><a href='#'>History</a></li><li><a href='#'>Delete</a></li><li><a href='#'>Copy</a></li><li><a href='#'>Move</a></li><li><a href='#'>Edit</a></li><li><a href='#'>View</a></li></ul></div>"}
                ]},
                {"id":"d2.3", "title": "Document 2.3", "type":"[type]", "created":"[created]", "author":"[author]", "version":"[version]", "due":"[due]", "status":"[status]", "action":"<div class='btn-group'><button type='buton' class='dropdown-toggle' data-toggle='dropdown'> <i class='fa fa-cog'></i> </button><ul class='dropdown-menu'><li><a href='#'>Download</a></li><li><a href='#'>Replace</a></li><li><a href='#'>History</a></li><li><a href='#'>Delete</a></li><li><a href='#'>Copy</a></li><li><a href='#'>Move</a></li><li><a href='#'>Edit</a></li><li><a href='#'>View</a></li></ul></div>"},
                {"id":"d2.4", "title": "Document 2.4", "type":"[type]", "created":"[created]", "author":"[author]", "version":"[version]", "due":"[due]", "status":"[status]", "action":"<div class='btn-group'><button type='buton' class='dropdown-toggle' data-toggle='dropdown'> <i class='fa fa-cog'></i> </button><ul class='dropdown-menu'><li><a href='#'>Download</a></li><li><a href='#'>Replace</a></li><li><a href='#'>History</a></li><li><a href='#'>Delete</a></li><li><a href='#'>Copy</a></li><li><a href='#'>Move</a></li><li><a href='#'>Edit</a></li><li><a href='#'>View</a></li></ul></div>"}
            ]}
        ]}];
        return src;
    }
    ,source: function() {
        var src = [];
        var objType = (this.parentType)? this.parentType : ObjNav.View.Navigator.getActiveObjType();
        var objId   = (this.parentId)  ? this.parentId   : ObjNav.View.Navigator.getActiveObjId();
        if (Acm.isNotEmpty(objType) && Acm.isNotEmpty(objId)) {
            src = AcmEx.FancyTreeBuilder
                .reset()
                .addBranchLast({key: objType + "." + objId
                    ,title          : objType + " (" + objId + ") /"
                    ,tooltip        : "root"
                    ,expanded: false
                    ,folder: true
                    ,lazy: true
                    ,cache: false
                    ,id: 0
                    ,root: true
                    ,startRow: 0
                    ,folderId: 0
                    ,containerObjectType: objType
                    ,containerObjectId: objId
                    ,"action": DocTree.View.getHtmlActionRoot()
                })
                .getTree();
        }
        return src;
    }
    ,getHtmlActionRoot: function() {
        return "<div class='btn-group'><button type='buton' class='dropdown-toggle' data-toggle='dropdown'> <i class='fa fa-cog'></i> </button><ul class='dropdown-menu'>"
            + "<li><a href='#' cmd='newFolder'>New Folder</a></li>"
            + "<li><a href='#' cmd='newDocument'>New Document</a></li>"
            + "</ul></div>";
    }
    ,getHtmlActionFolder: function() {
        return "<div class='btn-group'><button type='buton' class='dropdown-toggle' data-toggle='dropdown'> <i class='fa fa-cog'></i> </button><ul class='dropdown-menu'>"
            + "<li><a href='#' cmd='newFolder'>New Folder</a></li>"
            + "<li><a href='#' cmd='newDocument'>New Document</a></li>"
            + "<li><a href='#' cmd='rename'>Rename</a></li>"
            + "<li><a href='#' cmd='delete'>Delete</a></li>"
            + "<li><a href='#' cmd='cut'>Cut</a></li>"
            + "<li><a href='#' cmd='copy'>Copy</a></li>"
            + "<li><a href='#' cmd='paste'>Paste</a></li>"
            + "</ul></div>";
    }
    ,getHtmlActionDocument: function() {
        return "<div class='btn-group'><button type='buton' class='dropdown-toggle' data-toggle='dropdown'> <i class='fa fa-cog'></i> </button><ul class='dropdown-menu'>"
            + "<li><a href='#' cmd='rename'>Rename</a></li>"
            + "<li><a href='#' cmd='delete'>Delete</a></li>"
            + "<li><a href='#' cmd='cut'>Cut</a></li>"
            + "<li><a href='#' cmd='copy'>Copy</a></li>"
            + "<li><a href='#' cmd='paste'>Paste</a></li>"
            + "<li><a href='#' cmd='download'>Download</a></li>"
            + "<li><a href='#' cmd='replace'>Replace</a></li>"
            + "<li><a href='#' cmd='history'>History</a></li>"
            + "<li><a href='#' cmd='edit'>Edit</a></li>"
            + "<li><a href='#' cmd='view'>View</a></li>"
            + "</ul></div>";
    }
    ,_makeChildNodes: function(fd) {
        var builder = AcmEx.FancyTreeBuilder.reset();
        builder.addLeaf({"key":"f1"
            , "title": "Folder 1", toolTip:"tip1"
            , "expanded": false, "folder": true, lazy: true, cache: false
            , "id":"f1", "action": DocTree.View.getHtmlActionFolder()
        });
        builder.addLeaf({"key":"f2"
            , "title": "Folder 2", toolTip:"tip2"
            , "expanded": false
            , "folder": true
            , lazy: true, cache: false
            , "id":"f2", "action": DocTree.View.getHtmlActionFolder()
        });
        builder.addLeaf({"key":"d3"
            , "title": "doc 3", toolTip:"tip3"
            , "folder": false
            , "id":"d3", "type":"[type]", "created":"[created]", "author":"[author]", "version":"[version]", "due":"[due]", "status":"[status]"
            , "action": DocTree.View.getHtmlActionDocument()
        });
        builder.addLeaf({"key":"d4"
            , "title": "doc 4", toolTip:"tip4"
            , "folder": false
            , "id":"d4", "type":"[type]", "created":"[created]", "author":"[author]", "version":"[version]", "due":"[due]", "status":"[status]"
            , "action": DocTree.View.getHtmlActionDocument()
        });
        return builder.getTree();
    }
    ,lazyLoad: function(event, data) {
        var objType = DocTree.Model.getObjType();
        var objId   = DocTree.Model.getObjId();
        var nodeData = data.node.data;
        var folderId = nodeData.folderId;
        if (nodeData.root) {
            folderId = 0;
        }
        var pageId = nodeData.startRow;
        var cacheKey = DocTree.Model.getCacheKey(folderId, pageId);
        var fd = DocTree.Model.cacheFolder.get(cacheKey);
        if (fd) {
            data.result = DocTree.View._makeChildNodes(fd);
            setTimeout(function() {
                DocTree.Controller.viewChangedTree();
            }, 500);

        } else {
            data.result = DocTree.Service.retrieveFolderListDeferred(DocTree.Model.getObjType(), DocTree.Model.getObjId(), folderId, pageId
                ,function(fd) {
                    if (DocTree.Model.validateFolderList(fd)) {
                        nodeData.folderId = fd.folderId;
                        nodeData.totalChildren = fd.totalChildren;
                    }
                    var rc = DocTree.View._makeChildNodes(fd);
                    setTimeout(function() {
                        DocTree.Controller.viewChangedTree();
                    }, 500);
                    return rc;
                }
            );
        }

        return;


        var src = [];
        if (data.node.folder) {

            var builder = AcmEx.FancyTreeBuilder.reset();
            var key = data.node.key;
            if ("root" == key) {
                builder.addLeaf({"key":"f1"
                    , "title": "Folder 1", toolTip:"tip1"
                    , "expanded": false, "folder": true, lazy: true, cache: false
                    , "id":"f1", "action": DocTree.View.getHtmlActionFolder()
                });
                builder.addLeaf({"key":"f2"
                    , "title": "Folder 2", toolTip:"tip2"
                    , "expanded": false
                    , "folder": true
                    , lazy: true, cache: false
                    , "id":"f2", "action": DocTree.View.getHtmlActionFolder()
                });
                builder.addLeaf({"key":"d3"
                    , "title": "doc 3", toolTip:"tip3"
                    , "folder": false
                    , "id":"d3", "type":"[type]", "created":"[created]", "author":"[author]", "version":"[version]", "due":"[due]", "status":"[status]"
                    , "action": DocTree.View.getHtmlActionDocument()
                });
                builder.addLeaf({"key":"d4"
                    , "title": "doc 4", toolTip:"tip4"
                    , "folder": false
                    , "id":"d4", "type":"[type]", "created":"[created]", "author":"[author]", "version":"[version]", "due":"[due]", "status":"[status]"
                    , "action": DocTree.View.getHtmlActionDocument()
                });


            } else {
                builder.addLeaf({"key":"sf1"
                    , "title": "sFolder 1", toolTip:"tip1"
                    , "expanded": false, "folder": true, lazy: true, cache: false
                    , "id":"sf1", "action": DocTree.View.getHtmlActionFolder()
                });
                builder.addLeaf({"key":"sf2"
                    , "title": "sFolder 2", toolTip:"tip2"
                    , "expanded": false
                    , "folder": true
                    , lazy: true, cache: false
                    , "id":"sf2", "action": DocTree.View.getHtmlActionFolder()
                });
                builder.addLeaf({"key":"sd3"
                    , "title": "sdoc 3", toolTip:"tip3"
                    , "folder": false
                    , "id":"sd3", "type":"[type]", "created":"[created]", "author":"[author]", "version":"[version]", "due":"[due]", "status":"[status]"
                    , "action": DocTree.View.getHtmlActionDocument()
                });
                builder.addLeaf({"key":"sd4"
                    , "title": "sdoc 4", toolTip:"tip4"
                    , "folder": false
                    , "id":"sd4", "type":"[type]", "created":"[created]", "author":"[author]", "version":"[version]", "due":"[due]", "status":"[status]"
                    , "action": DocTree.View.getHtmlActionDocument()
                });
            }

            src = builder.getTree();
        }

        data.result = src;
        Acm.deferred(function() {
            DocTree.Controller.viewChangedTree();
        });
    }
    ,onCommand: function(event, data){
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
            case "newFolder":
                if (!DocTree.View.isEditing()) {
                    //node.editCreateNode("child", "New Folder");
                    node.editCreateNode("child", {"title": "New Folder", "folder": true, "action": DocTree.View.getHtmlActionFolder()});
                }
                break;
            case "newDocument":
                if (!DocTree.View.isEditing()) {
//                    if (!DocTree.View.uploadFolderNode) {
////                        node.editCreateNode("child", {"title": "New Document", "action": DocTree.View.getHtmlActionDocument()});
//////                        DocTree.View.uploadFolderNode = node;
//                        setTimeout(function() {
//                            DocTree.View.uploadToFolder(node);
//                            //DocTree.View.$fileInput.click();
//                        }, 200);
//                    }

                    //node.editCreateNode("child", "New Document");
                    //node.editCreateNode("child", {"title": "New Document", "action": DocTree.View.getHtmlActionDocument()});
                    DocTree.View.uploadFile(node);
//
//                    DocTree.View.$fileInput.click();
//                    //DocTree.View.$fileInput.click();
//
//                    if (node.lazy && !node.children) {
//                        node.setExpanded(true).always(function(){// Wait until expand finished, then do the paste
//                            DocTree.View._doNewFile(node);
//                        });
//                    } else {
//                        DocTree.View._doNewFile(node);
//                    }
                }
                break;

            case "cut":
                DocTree.View.CLIPBOARD = {mode: data.cmd, data: node};
                break;
            case "copy":
                DocTree.View.CLIPBOARD = {
                    mode: data.cmd,
                    data: node.toDict(function(n){
                        delete n.key;
                    })
                };
                break;
            case "clear":
                DocTree.View.CLIPBOARD = null;
                break;
            case "paste":
                if (node.lazy && !node.children) {
                    node.setExpanded(true).always(function(){// Wait until expand finished, then do the paste
                        DocTree.View._doPaste(node);
                    });
                } else {
                    DocTree.View._doPaste(node);
                }
                break;
            case "download":
                node.setStatus("loading");
                break;
            case "replace":
                node.setStatus("error");
                break;
            case "history":
                node.setStatus("ok");
                break;
            default:
                Acm.log("Unhandled command: " + data.cmd);
                return;
        }
    }
    ,_doPaste: function(node) {
        if( DocTree.View.CLIPBOARD.mode === "cut" ) {
            // refNode = node.getPrevSibling();
            if (node.folder) {
                DocTree.View.CLIPBOARD.data.moveTo(node, "child");
            } else {
                DocTree.View.CLIPBOARD.data.moveTo(node, "after");
            }
            DocTree.View.CLIPBOARD.data.setActive();
        } else if( DocTree.View.CLIPBOARD.mode === "copy" ) {
            if (node.folder) {
                node.addChildren(DocTree.View.CLIPBOARD.data).setActive();
            } else {
                node.addNode(DocTree.View.CLIPBOARD.data, "after").setActive();
            }
        }
    }
    ,onKeyDown: function(e){
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
            $(this).trigger("command", {cmd: cmd});
            // e.preventDefault();
            // e.stopPropagation();
            return false;
        }
    }
    ,getContextMenu: function(node) {
        var menu = [];
        if (node) {
            if (node.data.root) {
                menu = [
                    {title: "New Folder <kbd>[Ctrl+N]</kbd>", cmd: "newFolder", uiIcon: "ui-icon-plus" }
                    ,{title: "New Document <kbd>[Ctrl+Shift+N]</kbd>", cmd: "newDocument", uiIcon: "ui-icon-arrowreturn-1-e" }
                ];
            } else if (node.folder) {
                menu = [
                    //{title: "New sibling <kbd>[Ctrl+N]</kbd>", cmd: "addSibling", uiIcon: "ui-icon-plus" }
                    //,{title: "New child <kbd>[Ctrl+Shift+N]</kbd>", cmd: "addChild", uiIcon: "ui-icon-arrowreturn-1-e" }
                    {title: "New Folder <kbd>[Ctrl+N]</kbd>", cmd: "newFolder", uiIcon: "ui-icon-plus" }
                    ,{title: "New Document <kbd>[Ctrl+Shift+N]</kbd>", cmd: "newDocument", uiIcon: "ui-icon-arrowreturn-1-e" }
                    ,{title: "----" }
                    ,{title: "Rename <kbd>[F2]</kbd>", cmd: "rename", uiIcon: "ui-icon-pencil" }
                    ,{title: "Delete <kbd>[Del]</kbd>", cmd: "remove", uiIcon: "ui-icon-trash" }
                    ,{title: "----" }
                    ,{title: "Cut <kbd>Ctrl+X</kbd>", cmd: "cut", uiIcon: "ui-icon-scissors" }
                    ,{title: "Copy <kbd>Ctrl-C</kbd>", cmd: "copy", uiIcon: "ui-icon-copy" }
                    ,{title: "Paste <kbd>Ctrl+V</kbd>", cmd: "paste", uiIcon: "ui-icon-clipboard", disabled: true }
                ];
            } else {
                menu = [
                    {title: "Rename <kbd>[F2]</kbd>", cmd: "rename", uiIcon: "ui-icon-pencil" }
                    ,{title: "Delete <kbd>[Del]</kbd>", cmd: "remove", uiIcon: "ui-icon-trash" }
                    ,{title: "----" }
                    ,{title: "Cut <kbd>Ctrl+X</kbd>", cmd: "cut", uiIcon: "ui-icon-scissors" }
                    ,{title: "Copy <kbd>Ctrl-C</kbd>", cmd: "copy", uiIcon: "ui-icon-copy" }
                    ,{title: "Paste <kbd>Ctrl+V</kbd>", cmd: "paste", uiIcon: "ui-icon-clipboard", disabled: true }
                    ,{title: "----" }
                    ,{title: "Download <kbd>[Del]</kbd>", cmd: "download", uiIcon: "ui-icon-trash" }
                    ,{title: "Replace <kbd>[Del]</kbd>", cmd: "replace", uiIcon: "ui-icon-trash" }
                    ,{title: "History <kbd>[Del]</kbd>", cmd: "history", uiIcon: "ui-icon-trash" }
                ];
            }
        }
        return menu;
    }
    ,getTopNode: function() {
        var topNode = null;
        if (DocTree.View.tree) {
            var rootNode = DocTree.View.tree.getRootNode();
            if (rootNode) {
                topNode = rootNode.children[0];
            }
        }
        return topNode;
    }
    ,expandTopNode: function() {
        var node = DocTree.View.$tree.fancytree("getRootNode");
        if (node) {
            var topNode = node.children[0];
            if (!topNode.children) {
                topNode.setExpanded(true);
            }
        }
    }
    ,switchObject: function(activeObjType, activeObjId) {
        if (!DocTree.View.tree) {
            return;
        }

        var dict = null;
        var topNode = DocTree.View.getTopNode();
        if (topNode) {
            var previousObjType = DocTree.Model.getObjType();
            var previousObjId = DocTree.Model.getObjId();
            if (previousObjType != activeObjType || previousObjId != activeObjId) {
                var dictTree = DocTree.View.tree.toDict();
                if (!Acm.isArrayEmpty(dictTree)) {
                    dict = dictTree[0];
                    if (dict && dict.data && dict.data.id == previousObjId) {
                        DocTree.Model.cacheTree.put(previousObjType + "." + previousObjId, dict);
                    }
                }
            }
        }

        DocTree.Model.setObjType(activeObjType);
        DocTree.Model.setObjId(activeObjId);
        dict = DocTree.Model.cacheTree.get(activeObjType + "." + activeObjId);
        if (dict && topNode) {
            topNode.removeChildren();
            topNode.resetLazy();
            topNode.fromDict(dict);
        } else {
            DocTree.View.tree.reload(DocTree.View.source());
        }
        Acm.deferred(DocTree.Controller.viewChangedTree);
    }

    ,getSelectedNodes: function() {
        var a1 = this.tree.getSelectedNodes();
        var z = 1;
        return this.tree.getSelectedNodes();
    }


};

