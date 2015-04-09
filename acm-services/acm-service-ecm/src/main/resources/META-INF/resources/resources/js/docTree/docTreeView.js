/**
 * DocTree.View
 *
 * @author jwu
 */
DocTree.View = DocTree.View || {
    create : function(args) {
        this.parentType = args.parentType;
        this.parentId = args.parentId;

        this.$tree = (args.$tree)? args.$tree : $("#treeDoc");
        this.createDocTree(args.treeArgs);

        this.doUploadForm = args.uploadForm;
        this.fileTypes  = args.fileTypes;
        //this.docSubMenu = this.Menu.makeDocSubMenu(this.fileTypes);

        this.$formDownloadDoc = (args.$formDownloadDoc)? args.$formDownloadDoc : $("#formDownloadDoc");
        this.$formUploadDoc   = (args.$formUploadDoc)  ? args.$formUploadDoc   : $("#formUploadDoc");
        this.$fileInput    = (args.$fileInput)   ? args.$fileInput    : $("#file");
        this.$fileInput.on("change", function(e) {
            DocTree.View.$formUploadDoc.submit();
        });
        this.$formUploadDoc.submit(function(e) {DocTree.View.onSubmitFormUploadFile(e, this);});

        //this.getActiveObjId = (args.getActiveObjId)?args.getActiveObjId : ObjNav.View.Navigator.getActiveObjId;
        //this.getPreviousObjId = (args.getPreviousObjId)?args.getPreviousObjId : ObjNav.View.Navigator.getPreviousObjId;

        Acm.Dispatcher.addEventListener(DocTree.Controller.VIEW_CHANGED_PARENT           ,this.onViewChangedParent);
        Acm.Dispatcher.addEventListener(DocTree.Controller.VIEW_CHANGED_TREE             ,this.onViewChangedTree);
        Acm.Dispatcher.addEventListener(DocTree.Controller.MODEL_UPLOADED_FILE           ,this.onModelUploadedFile);
        Acm.Dispatcher.addEventListener(DocTree.Controller.MODEL_RETRIEVED_FOLDERLIST    ,this.onModelRetrievedFolderList);


        Acm.Dispatcher.addEventListener(DocTree.Controller.MODEL_ADDED_FOLDER            ,this.onModelAddedFolder);
        Acm.Dispatcher.addEventListener(DocTree.Controller.MODEL_ADDED_DOCUMENT          ,this.onModelAddedDocument);
    }
    ,onInitialized: function() {
    }


    ,uploadFile: function(node, fileType) {
        DocTree.View.uploadToFolderNode = node;
        DocTree.View.uploadFileType = fileType;
        DocTree.View.$fileInput.click();
    }
    ,uploadForm: function(node, formType) {
        DocTree.View.uploadToFolderNode = node;
        DocTree.View.uploadFileType = formType;
        if (DocTree.View.doUploadForm) {
            DocTree.View.doUploadForm(formType, function() {
                DocTree.View.onLoadingFrevvoForm();
            });
        }
    }

    ,_addFileNode: function(folderNode, name, type) {
        var fileNode = folderNode.addChildren({"title": "Uploading " + name + "...", "name": name, "type": type, "loadStatus": "loading", "action": DocTree.View.Source.getHtmlAction()});
        fileNode.setStatus("loading");
        return fileNode;
    }
    ,_addingFileNodes: function(folderNode, names, type) {
        var deferred = $.Deferred();
        if (folderNode.lazy && !folderNode.children) {
            folderNode.setExpanded(true).always(function(){// Wait until expand finished, then do the paste
                var fileNodes = [];
                for (var i = 0; i < names.length; i++) {
                    var fileNode = DocTree.View._addFileNode(folderNode, names[i], type);
                    fileNodes.push(fileNode);
                }
                deferred.resolve(fileNodes);
            });
        } else {
            var fileNodes = [];
            for (var i = 0; i < names.length; i++) {
                var fileNode = DocTree.View._addFileNode(folderNode, names[i], type);
                fileNodes.push(fileNode);
            }
            deferred.resolve(fileNodes);
        }
        return deferred.promise();
    }
    ,onFailedAddingFileNode: function() {
        var z = 1;
    }
    ,onLoadingFrevvoForm: function() {
        var folderNode = DocTree.View.uploadToFolderNode;
        var fileType = DocTree.View.uploadFileType;
        var names = [fileType + " form"];
        var promiseAddNode = DocTree.View._addingFileNodes(folderNode, names, fileType);

        setTimeout(function(){
            var promiseUploadFile = DocTree.Service.checkUploadForm(DocTree.Model.getObjType(), DocTree.Model.getObjId(), folderNode.data.objectId, folderNode.data.startRow, folderNode, fileType);
            $.when(promiseUploadFile, promiseAddNode).done(function(uploadedFiles, fileNodes){
                if (!Acm.isArrayEmpty(uploadedFiles) && DocTree.View.validateNodes(fileNodes)) {
                    for (var i = 0; i < uploadedFiles.length; i++) {
                        var uploadedFile = uploadedFiles[i];
                        var emptyNode = null;
                        if (0 == i) {
                            emptyNode = DocTree.View._findEmptyNode(folderNode, fileType);
                            if (emptyNode) {
                                DocTree.View._fileDataToNodeData(uploadedFile, emptyNode);
                                emptyNode.renderTitle();
                                emptyNode.setStatus("ok");
                            }
                        }


                        if (!emptyNode) {
                            var fileNode = folderNode.addChildren({"title": Acm.goodValue(uploadedFile.name), "action": DocTree.View.Source.getHtmlAction()});
                            DocTree.View._fileDataToNodeData(uploadedFile, fileNode);
                            fileNode.renderTitle();
                        }
                    }
                }
            });
        }, 5000);

    }
    ,_fileDataToNodeData: function(fileData, nodeData) {
        if (fileData && nodeData) {
            if (!nodeData.data) {
                nodeData.data = {};
            }
            nodeData.key             = Acm.goodValue(fileData.objectId, 0);
            nodeData.title           = Acm.goodValue(fileData.name);
            nodeData.tooltip         = Acm.goodValue(fileData.name);
            nodeData.data.name       = Acm.goodValue(fileData.name);
            nodeData.data.type       = Acm.goodValue(fileData.type);
            nodeData.data.objectId   = Acm.goodValue(fileData.objectId, 0);
            nodeData.data.objectType = Acm.goodValue(fileData.objectType);
            nodeData.data.created    = Acm.goodValue(fileData.created);
            nodeData.data.creator    = Acm.goodValue(fileData.creator);
            nodeData.data.status     = Acm.goodValue(fileData.status);
            nodeData.data.version    = Acm.goodValue(fileData.version);
            nodeData.data.category   = Acm.goodValue(fileData.category);
        }
        return nodeData;
    }
    ,_findEmptyNode: function(folderNode, fileType) {
        var node = null;
        for (var i = folderNode.children.length - 1; 0 <= i; i--) {
            if (fileType == folderNode.children[i].data.type) {
                if (Acm.isEmpty(folderNode.children[i].data.objectId)) {
                    node = folderNode.children[i];
                    break;
                }
            }
        }
        return node;
    }
    ,onSubmitFormUploadFile: function(event, ctrl) {
        event.preventDefault();

        var folderNode = DocTree.View.uploadToFolderNode;
        var fileType = DocTree.View.uploadFileType;
        var files = DocTree.View.$fileInput[0].files;
        var names = [];
        for(var i = 0; i < files.length; i++ ){
            names.push(files[i].name);
        }
        var promiseAddNode = DocTree.View._addingFileNodes(folderNode, names, fileType);

        var fd = new FormData();
        fd.append("parentObjectType", DocTree.Model.getObjType());
        fd.append("parentObjectId", DocTree.Model.getObjId());
        if (!DocTree.View.isTopNode(folderNode)) {
            fd.append("parentFolderId", folderNode.data.objectId);
        }
        fd.append("fileType", fileType);
        fd.append("category", "Document");
        for(var i = 0; i < files.length; i++ ){
            fd.append("files[]", files[i]);
        }

        var folderId = (DocTree.View.isTopNode(folderNode))? 0 : folderNode.data.objectId;
        var pageId = folderNode.data.startRow;
        var cacheKey = DocTree.Model.getCacheKey(folderId, pageId);
        var promiseUploadFile = DocTree.Service.uploadFile(fd, cacheKey, folderNode);
        $.when(promiseUploadFile, promiseAddNode).done(function(uploadedFiles, fileNodes){
            if (!Acm.isArrayEmpty(uploadedFiles) && DocTree.View.validateNodes(fileNodes)) {
                for (var i = 0; i < uploadedFiles.length; i++) {
                    var uploadedFile = uploadedFiles[i];
                    var type = Acm.goodValue(uploadedFile.type);
                    var name = Acm.goodValue(uploadedFile.name);
                    var fileNode = DocTree.View._matchFileNode(type, name, fileNodes);
                    if (fileNode) {
                        DocTree.View._fileDataToNodeData(uploadedFile, fileNode);
                        fileNode.renderTitle();
                        fileNode.setStatus("ok");
                    }
                } //end for
            }
        });
    }
    ,_getNameOrig: function(name) {
        if (Acm.isEmpty(name)) {
            return name;
        }

        var nameOrig = "";
        var und_parts = name.split("_");
        if (und_parts && 2 <= und_parts.length) {
            nameOrig += name.substring(0, name.length - und_parts[und_parts.length - 1].length - 1);
        } else {
            return name;
        }
        var dot_parts = und_parts[und_parts.length - 1].split(".");
        if (dot_parts && 2 <= dot_parts.length) {
            nameOrig += "." + dot_parts[dot_parts.length - 1];
        }
        if (Acm.isEmpty(nameOrig)) {
            nameOrig = name;
        }
        return nameOrig;
    }
    ,_matchFileNode: function(type, name, fileNodes) {
        var fileNode = null;
        for (var i = 0; i < fileNodes.length; i++) {
            var nameOrig = this._getNameOrig(name);
            if (fileNodes[i].data.name == nameOrig && fileNodes[i].data.type == type) {
                fileNode = fileNodes[i];
                break;
            }
        }
        return fileNode;
    }

    ,onViewChangedParent: function(objType, objId) {
        DocTree.View.switchObject(objType, objId);
    }

    // The gear button click events toggle the menu popup. For some unknown reason, the events are fired
    // multiple times rapidly, which mess up the menu toggle logic. _contextMenuIsOpening flag is used
    // to ignore click events during a 100ms time window.
    ,_contextMenuIsOpening: false
    ,onViewChangedTree: function() {
        var $btnTreeBody = DocTree.View.$tree.find("tbody");
        var $btnTreeActions = DocTree.View.$tree.find("button");
        $btnTreeActions.on("click", function(e) {
            if (DocTree.View._contextMenuIsOpening) {
                return;
            }

            var $treeBody = DocTree.View.$tree.find("tbody");
            //var isOpen = $btnTreeBody.contextmenu("isOpen");   //This does not work as expected
            var isOpen = Acm.Object.isVisible($(".ui-menu"));
            if (isOpen) {
                $btnTreeBody.contextmenu("close");

            } else {
                DocTree.View._contextMenuIsOpening = true;
                setTimeout(function(){
                    DocTree.View._contextMenuIsOpening = false;
                }, 100);

                $btnTreeBody.contextmenu("open", $(this));
            }
        });

    }
    ,onModelUploadedFile: function(uploadInfo, folderNode) {
        if (uploadInfo.hasError) {
            //node.setStatus("error");
            var z = 1;
        } else {
            if (DocTree.Model.validateUploadInfo(uploadInfo) && DocTree.View.validateNode(folderNode)) {

            }
            //folderNode.data.
        }
    }
    ,onModelRetrievedFolderList: function(folderList, objType, objId, folderId, pageId, folderNode) {
        if (DocTree.Model.validateFolderList(folderList) && DocTree.View.validateNode(folderNode)) {
            folderNode.data.objectId = folderList.folderId;
            folderNode.data.totalChildren = folderList.totalChildren;
            folderNode.renderTitle();
        }
    }
    ,validateNodes: function(data) {
        if (Acm.isNotArray(data)) {
            return false;
        }
        for (var i = 0; i < data.length; i++) {
            if (!this.validateNode(data[i])) {
                return false;
            }
        }
        return true;
    }
    ,validateNode: function(data) {
        if (Acm.isEmpty(data)) {
            return false;
        }
        if (Acm.isEmpty(data.tree)) {
            return false;
        }
        if (Acm.isEmpty(data.data)) {
            return false;
        }
        if (Acm.isEmpty(data.key)) {
            return false;
        }
        return true;
    }
    ,onModelAddedFolder: function(node, parentId, folder) {

//        $(node.span).removeClass("pending");
//        // Let's pretend the server returned a slightly modified
//        // title:
//        node.setTitle(node.title + "!");
//        //var $divError = $("#divError");
//        //$divError.slideUp("slow");
//
//        App.View.ErrorBoard.show("hello folder", "world");
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

//        App.View.ErrorBoard.show("hello doc");
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
                var node = data.node;
                var $tdList = $(node.tr).find(">td");
                // (index #0 is rendered by fancytree by adding the checkbox)
                //$tdList.eq(1).text(node.data.objectId);
                $tdList.eq(1).html(DocTree.View.Source.getHtmlDocLink(node.data.objectId));
                // (index #2 is rendered by fancytree)

                //if (DocTree.View.isTopNode(node)) {
                if (node.folder) {  //DocTree.View.isFolderNode(node)
//                    var html =  '<div class="jtable-main-container">'
//                        +'<div class="jtable-bottom-panel">'
//                        +'    <div class="jtable-left-area">'
//                        +'        <span class="jtable-page-list">'
//                        +'            <span class="jtable-page-number-first jtable-page-number-disabled">&lt;&lt;</span>'
//                        +'            <span class="jtable-page-number-previous jtable-page-number-disabled">&lt;</span>'
//                        +'            <span class="jtable-page-number">1</span>'
//                        +'            <span class="jtable-page-number">2</span>'
//                        +'            <span class="jtable-page-number-space">...</span>'
//                        +'            <span class="jtable-page-number">4</span>'
//                        +'            <span class="jtable-page-number jtable-page-number-active jtable-page-number-disabled">5</span>'
//                        +'            <span class="jtable-page-number">6</span>'
//                        +'            <span class="jtable-page-number-space">...</span>'
//                        +'            <span class="jtable-page-number">152</span>'
//                        +'            <span class="jtable-page-number">153</span>'
//                        +'            <span class="jtable-page-number-next jtable-page-number-disabled">&gt;</span>'
//                        +'            <span class="jtable-page-number-last jtable-page-number-disabled">&gt;&gt;</span>'
//                        +'            </span>'
//                        +'        <span class="jtable-goto-page" style="display: inline;">'
//                        +'            <span>Go to page: </span>'
//                        +'               <select>'
//                        +'                  <option value="1">1</option>'
//                        +'                  <option value="2">2</option>'
//                        +'                  <option value="3">3</option>'
//                        +'                  <option value="4">4</option>'
//                        +'                  <option value="5">5</option>'
//                        +'                  <option value="6">6</option>'
//                        +'                  <option value="7">7</option>'
//                        +'                  <option value="8">8</option>'
//                        +'                  <option value="9">9</option>'
//                        +'                  <option value="10">10</option>'
//                        +'               </select>'
//                        +'            </span>'
//                        +'        <span class="jtable-page-size-change">'
//                        +'            <span>Row count: </span>'
//                        +'            <select>'
//                        +'                <option value="10">10</option>'
//                        +'                <option value="25">25</option>'
//                        +'                <option value="50">50</option>'
//                        +'                <option value="100">100</option>'
//                        +'                <option value="250">250</option>'
//                        +'                <option value="500">500</option>'
//                        +'            </select>'
//                        +'        </span>'
//                        +'    </div>'
//                        +'    <div class="jtable-right-area">'
//                        +'        <span class="jtable-page-info">Showing 1-1 of 1</span>'
//                        +'    </div>'
//                        +'</div></div>';
//                    $tdList.eq(3).html(html);
                } else if (DocTree.View.isFileNode(node)) {
                    $tdList.eq(3).text(node.data.type);
                    $tdList.eq(4).text(Acm.getDateFromDatetime(node.data.created));
                    $tdList.eq(5).text(Acm.__FixMe__getUserFullName(node.data.creator));
                    $tdList.eq(6).text(node.data.version);
                    $tdList.eq(7).text(node.data.status);
                    //$tdList.eq(8).html(node.data.action);
//                $tdList.eq(8).html(DocTree.View.Source.getHtmlAction());
                } else {  //non file, non folder
                    $tdList.eq(0).text("");
                    //$tdList.eq(2).text("Btn" + node.title);

                }


                //$tdList.eq(3).text(node.key);
                //$tdList.eq(4).html("<input type='checkbox' name='like' value='" + node.key + "'>");
            }

            ,createNode: function(event, data) {
                var node = data.node;
//                if (DocTree.View.isFileNode(node) || DocTree.View.isFolderNode(node)) {
//                    $(node.tr).addClass("hasMenu");
//                }
//                if (node.folder) {
//                    var $tdEq3 = $(node.tr).find(">td:eq(3)");
//                    var $tdGt3 = $(node.tr).find(">td:gt(3)");
//                    $tdEq3.attr("colspan", 5);
//                    $tdGt3.remove();
//                }
                var z = 1;
            }
            ,renderNode: function(event, data) {
                var node = data.node;
                var acmIcon = null;
                var nodeType = Acm.goodValue(node.data.objectType);
                if (DocTree.Model.NODE_TYPE_PREV == nodeType) {
                    acmIcon = "<i class='i i-arrow-up'></i>" //"i-notice icon"
                } else if (DocTree.Model.NODE_TYPE_NEXT == nodeType) {
                    acmIcon = "<i class='i i-arrow-down'></i>";
                }
                if (acmIcon) {
                    var span = node.span;
                    var $spanIcon = $(span.children[1]);
                    $spanIcon.removeClass("fancytree-icon");
                    $spanIcon.html(acmIcon);
                }
            }
            ,click    : DocTree.View.onClick
            ,dblclick : DocTree.View.onDblClick
            ,keydown  : DocTree.View.onKeyDown
            ,source   : DocTree.View.Source.source()
            ,lazyLoad : DocTree.View.Source.lazyLoad
            ,edit: {
                triggerStart: ["f2", "shift+click", "mac+enter"]
                ,beforeEdit: function(event, data){
                    if (DocTree.View.isTopNode(data.node) || DocTree.View.isSpecialNode(data.node)) {
                        return false;// Return false to prevent edit mode
                    }
                    if (data.node.isLoading()) {
                        return false;
                    }
                    DocTree.View.setEditing(true);
                    var z = 1;
                }
                ,edit: function(event, data){
                    data.input.select();
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
                        parentId = parent.data.objectId;
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
                        var id = data.node.data.objectId;
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

                    if (DocTree.View.isTopNode(data.node) || DocTree.View.isSpecialNode(data.node)) {
                        return false;
                    }
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
                    } else if (DocTree.Model.NODE_TYPE_PREV == data.node.data.objectType) {
                        return ["after"];
                    } else if (DocTree.Model.NODE_TYPE_NEXT == data.node.data.objectType) {
                        return ["before"];
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
            .on("command"   , DocTree.View.onCommand)
//            .on("keydown"   , DocTree.View.onKeyDown)
//            .on("dblclick"  , DocTree.View.onDblClick)
//            .on("click"     , DocTree.View.onClick)
            //.on("mouseenter, mouseleave", ".fancytree-title", function(event){
//            .on("mouseenter, mouseleave", ".fancytree-node", function(event){
//                // Add a hover handler to all node titles (using event delegation)
//                var node = $.ui.fancytree.getNode(event);
//                node.info(event.type);
//            })
//            .on("mouseenter, mouseleave", ".fancytree-node", function(event){
            .on("mouseenter", ".fancytree-node", function(event){
                var node = $.ui.fancytree.getNode(event);
                if (node) {
                    if (DocTree.View.isSpecialNode(node)) {
                        //node.info(event.type + node.data.objectType);
                        DocTree.View.Paging.pagingAlert(node);
                    }
                }
            })
            .on("mouseleave", ".fancytree-node", function(event){
                var node = $.ui.fancytree.getNode(event);
                if (node) {
                    if (DocTree.View.isSpecialNode(node)) {
                        //node.info(event.type + node.data.objectType);
                        DocTree.View.Paging.pagingRelieve();
                    }
                }
            })
//            .on("hover", ".fancytree-node", function(event){
//                var node = $.ui.fancytree.getNode(event);
//                if (node) {
//                    if (DocTree.View.isSpecialNode(node)) {
//                        node.info(event.type + node.data.objectType);
//
//                    }
//                }
//            })
        ;

        this.tree = $tree.fancytree("getTree");
        var $treeBody = $tree.find("tbody");
        DocTree.View.Menu.useContextMenu($treeBody);


//        var $treeBody = $tree.find("tbody");
//        $treeBody.contextmenu({
//            delegate: "button"
//            ,autoTrigger: false
//            ,menu: []
//            ,beforeOpen: function(event, ui) {
//                var node = $.ui.fancytree.getNode(ui.target);
//                $treeBody.contextmenu("replaceMenu", DocTree.View.getContextMenu(node));
//                $treeBody.contextmenu("enableEntry", "paste", !!DocTree.View.CLIPBOARD);
//                node.setActive();
//            }
//            ,select: function(event, ui) {
//                var that = this;
//                //var node = $.ui.fancytree.getNode(ui.target);
//
//                // delay the event, so the menu can close and the click event does
//                // not interfere with the edit control
//                setTimeout(function(){
//                    $(that).trigger("command", {cmd: ui.cmd});
//                }, 100);
//            }
//        });

    }

    ,Menu: {
        useContextMenu: function($s) {
            if (!this.docSubMenu) {
                this.docSubMenu = this.makeDocSubMenu(DocTree.View.fileTypes);
            }

            $s.contextmenu({
                menu: []
                //,delegate: "span.fancytree-node"
                ,delegate: "tr"
                ,beforeOpen: function(event, ui) {
                    var node = $.ui.fancytree.getNode(ui.target);
                    if (DocTree.View.isSpecialNode(node)) {
                        return false;
                    }
                    $s.contextmenu("replaceMenu", DocTree.View.Menu.getContextMenu(node));
                    $s.contextmenu("enableEntry", "paste", !!DocTree.View.CLIPBOARD);
                    node.setActive();
                }
                ,select: function(event, ui) {
                    // delay the event, so the menu can close and the click event does
                    // not interfere with the edit control
                    var that = this;
                    setTimeout(function(){
                        $(that).trigger("command", {cmd: ui.cmd});
                    }, 100);
                }
            });
        }

        ,getContextMenu: function(node) {
            var menu = [];
            if (node) {
                if (DocTree.View.isTopNode(node)) {
                    menu = [
                        {title: "New Folder <kbd>[Ctrl+N]</kbd>", cmd: "newFolder", uiIcon: "ui-icon-plus" }
                        ,{title: "New Document</kbd>", children: DocTree.View.Menu.docSubMenu}
                        ,{title: "----" }
                        ,{title: "Paste <kbd>Ctrl+V</kbd>", cmd: "paste", uiIcon: "ui-icon-clipboard", disabled: true }
                    ];
                } else if (DocTree.View.isFolderNode(node)) {
                    menu = [
                        //{title: "New sibling <kbd>[Ctrl+N]</kbd>", cmd: "addSibling", uiIcon: "ui-icon-plus" }
                        //,{title: "New child <kbd>[Ctrl+Shift+N]</kbd>", cmd: "addChild", uiIcon: "ui-icon-arrowreturn-1-e" }
                        {title: "New Folder <kbd>[Ctrl+N]</kbd>", cmd: "newFolder", uiIcon: "ui-icon-plus" }
                        //,{title: "New Document <kbd>[Ctrl+Shift+N]</kbd>", cmd: "newDocument", uiIcon: "ui-icon-arrowreturn-1-e" }
                        ,{title: "New Document</kbd>", children: DocTree.View.Menu.docSubMenu}
                        ,{title: "----" }
                        ,{title: "Cut <kbd>Ctrl+X</kbd>", cmd: "cut", uiIcon: "ui-icon-scissors" }
                        ,{title: "Copy <kbd>Ctrl-C</kbd>", cmd: "copy", uiIcon: "ui-icon-copy" }
                        ,{title: "Paste <kbd>Ctrl+V</kbd>", cmd: "paste", uiIcon: "ui-icon-clipboard", disabled: true }
                        ,{title: "----" }
                        ,{title: "Rename <kbd>[F2]</kbd>", cmd: "rename", uiIcon: "ui-icon-pencil" }
                        ,{title: "Delete <kbd>[Del]</kbd>", cmd: "remove", uiIcon: "ui-icon-trash" }
                    ];
                } else if (DocTree.View.isFileNode(node)) {
                    menu = [
                        {title: "Open", cmd: "open", uiIcon: "ui-icon-folder-open" }
                        ,{title: "Edit", cmd: "edit", uiIcon: "ui-icon-pencil" }
                        ,{title: "----" }
                        ,{title: "Cut <kbd>Ctrl+X</kbd>", cmd: "cut", uiIcon: "ui-icon-scissors" }
                        ,{title: "Copy <kbd>Ctrl-C</kbd>", cmd: "copy", uiIcon: "ui-icon-copy" }
                        ,{title: "Paste <kbd>Ctrl+V</kbd>", cmd: "paste", uiIcon: "ui-icon-clipboard", disabled: true }
                        ,{title: "----" }
                        ,{title: "Rename <kbd>[F2]</kbd>", cmd: "rename", uiIcon: "ui-icon-pencil" }
                        ,{title: "Delete <kbd>[Del]</kbd>", cmd: "remove", uiIcon: "ui-icon-trash" }
                        ,{title: "----" }
                        ,{title: "Download", cmd: "download", uiIcon: "ui-icon-arrowthickstop-1-s" }
                        ,{title: "Replace", cmd: "replace", uiIcon: "" }
                        ,{title: "History", cmd: "history", uiIcon: "" }
                    ];
                }
            }
            return menu;
        }


        // To create a menu like this:
        //        var menu = [
        //            {title: "Electronic Communication", cmd: "form/electronicCommunicationFormUrl"}
        //            ,{title: "Report of Investigation", cmd: "form/roiFormUrl"}
        //            ,{title: "Medical Release", cmd: "file/mr"}
        //            ,{title: "General Release", cmd: "file/gr"}
        //            ,{title: "eDelivery", cmd: "file/ev"}
        //            ,{title: "SF86 Signature", cmd: "file/sig"}
        //            ,{title: "Notice of Investigation", cmd: "file/noi"}
        //            ,{title: "Witness Interview Request", cmd: "file/wir"}
        //            ,{title: "Other", cmd: "file/other"}
        //        ];
        ,makeDocSubMenu: function(fileTypes) {
            var menu = [], item;
            if (Acm.isArray(fileTypes)) {
                for (var i = 0; i < fileTypes.length; i++) {
                    item = {};
                    if (Acm.isNotEmpty(fileTypes[i].label) && Acm.isNotEmpty(fileTypes[i].type)) {
                        item.title = fileTypes[i].label;
                        if (Acm.isNotEmpty(fileTypes[i].form)) {
                            item.cmd = "form/" + fileTypes[i].type;
                        } else {
                            item.cmd = "file/" + fileTypes[i].type;
                        }
                    }
                    menu.push(item);
                }
            }
            return menu;
        }
    }

    ,Paging: {
        _triggerNode: null
        ,pagingAlert: function(node) {
            DocTree.View.Paging._triggerNode = node;
            setTimeout(function(){
                var node = DocTree.View.Paging._triggerNode;
                DocTree.View.Paging.doPaging(node);
            }, 2000);
        }
        ,pagingRelieve: function() {
            DocTree.View.Paging._triggerNode = null;
        }
        ,doPaging: function(node) {
            if (!node) {
                return;
            }
            var parent = node.getParent();
            if (!parent) {
                return;
            }

            if (DocTree.Model.NODE_TYPE_PREV == node.data.objectType) {
                var startRow = Acm.goodValue(parent.data.startRow, 0) - Acm.goodValue(parent.data.maxRows, DocTree.Model.Config.getMaxRows());
                if (0 > startRow) {
                    startRow = 0;
                }
                parent.data.startRow = startRow;
                parent.resetLazy();
                parent.setExpanded(true);
            } else if (DocTree.Model.NODE_TYPE_NEXT == node.data.objectType) {
                var startRow = Acm.goodValue(parent.data.startRow, 0) + Acm.goodValue(parent.data.maxRows, DocTree.Model.Config.getMaxRows());
                var totalChildren = Acm.goodValue(parent.data.totalChildren, -1);
                if (0 <= totalChildren) {   // -1 is a special value for unknown totalChildren; keep increasing in this case
                    if (totalChildren <= startRow) {
                        startRow = totalChildren - 1;
                        if (0 > startRow) {
                            startRow = 0;
                        }
                    }
                }
                parent.data.startRow = startRow;
                parent.resetLazy();
                parent.setExpanded(true);
            }
        }
    }

    ,Source: {
        source: function() {
            var src = [];
            var containerObjectType = (this.parentType)? this.parentType : DocTree.Model.getObjType();
            var containerObjectId   = (this.parentId)  ? this.parentId   : DocTree.Model.getObjId();
            if (Acm.isNotEmpty(containerObjectType) && Acm.isNotEmpty(containerObjectId)) {
                src = AcmEx.FancyTreeBuilder
                    .reset()
                    .addBranchLast({key: containerObjectType + "." + containerObjectId
                        //,title          : containerObjectType + " (" + containerObjectId + ") /"
                        ,title          : "/"
                        ,tooltip        : "root"
                        ,expanded: false
                        ,folder: true
                        ,lazy: true
                        ,cache: false
                        ,objectId: 0
                        ,root: true
                        ,startRow: 0
                        //,folderId: 0
                        ,containerObjectType: containerObjectType
                        ,containerObjectId: containerObjectId
                        ,totalChildren: -1
    //            doc.objectId = uploadInfo.fileId;
    //            doc.objectType = DocTree.Model.NODE_TYPE_FILE;
    //            doc.created = uploadInfo.created;
    //            doc.creator = uploadInfo.creator;
    //            doc.modified = uploadInfo.modified;
    //            doc.modifier = uploadInfo.modifier;
    //            doc.name = uploadInfo.fileName;
    //            doc.type = uploadInfo.fileType;
    //            doc.status = uploadInfo.status;
    //            doc.version = "";
                        ,"action": DocTree.View.Source.getHtmlAction()
                    })
                    .getTree();
            }
            return src;
        }
        ,getHtmlDocLink: function(fileId) {
            var html = "<div></div>";
            if (fileId) {
                var url = App.getContextPath() + "/plugin/document/" + fileId;
                html = "<div class='btn-group'><a href='" + url + "'>" + fileId + "</a></div>";
            }
            return html;
        }
        ,getHtmlAction: function() {
            return "<div class='btn-group'><button type='button'> <i class='fa fa-cog'></i> </button></div>";
        }

        ,_makeChildNodes: function(folderList) {
            var builder = AcmEx.FancyTreeBuilder.reset();
            if (DocTree.Model.validateFolderList(folderList)) {
                var  startRow = Acm.goodValue(folderList.startRow, 0);
                var  maxRows = Acm.goodValue(folderList.maxRows, 0);
                var  totalChildren = Acm.goodValue(folderList.totalChildren, -1);
                var  folderId = Acm.goodValue(folderList.folderId, 0);

                if (0 < startRow) {
                    builder.addLeaf({key: folderId + ".prev"
                        ,title: startRow + " items above..."
                        ,tooltip: "Review previous items"
                        ,expanded: false
                        ,folder: false
                        ,objectType: DocTree.Model.NODE_TYPE_PREV
                    });
                }

                for (var i = 0; i < folderList.children.length; i++) {
                    var child = folderList.children[i];
                    if ("folder" == Acm.goodValue(child.objectType)) {
                        builder.addLeaf({"key":"f1"
                            , "title": "Folder 1"
                            , toolTip:"tip1"
                            , "expanded": false
                            , "folder": true
                            , lazy: true
                            , cache: false
                            , "id":"f1"
                            ,totalChildren: -1
                            , "action": DocTree.View.Source.getHtmlAction()
                        });

                    } if (DocTree.Model.NODE_TYPE_FILE == Acm.goodValue(child.objectType)) {
                        var nodeData = {};
                        DocTree.View._fileDataToNodeData(child, nodeData);
                        nodeData.folder = false;
                        nodeData.action = DocTree.View.Source.getHtmlAction();
                        builder.addLeaf(nodeData);
                    }
                }
                //builder.makeLast();

                if ((0 > totalChildren) || (totalChildren - maxRows > startRow)) {//unknown size or more page
                    var title = (0 > totalChildren)? "More items..." : (totalChildren - startRow - maxRows) + " more items...";
                    builder.addLeafLast({key: Acm.goodValue(folderId, 0) + ".next"
                        ,title: title
                        ,tooltip: "Load more items"
                        ,expanded: false
                        ,folder: false
                        ,objectType: DocTree.Model.NODE_TYPE_NEXT
                    });
                }
            }

    //        builder.addLeaf({"key":"f1"
    //            , "title": "Folder 1", toolTip:"tip1"
    //            , "expanded": false, "folder": true, lazy: true, cache: false
    //            , "id":"f1", "action": DocTree.View.Source.getHtmlAction()
    //        });
    //        builder.addLeaf({"key":"f2"
    //            , "title": "Folder 2", toolTip:"tip2"
    //            , "expanded": false
    //            , "folder": true
    //            , lazy: true, cache: false
    //            , "id":"f2", "action": DocTree.View.Source.getHtmlAction()
    //        });
    //        builder.addLeaf({"key":"d3"
    //            , "title": "doc 3", toolTip:"tip3"
    //            , "folder": false
    //            , "id":"d3", "type":"[type]", "created":"[created]", "author":"[author]", "version":"[version]", "due":"[due]", "status":"[status]"
    //            , "action": DocTree.View.Source.getHtmlAction()
    //        });
    //        builder.addLeaf({"key":"d4"
    //            , "title": "doc 4", toolTip:"tip4"
    //            , "folder": false
    //            , "id":"d4", "type":"[type]", "created":"[created]", "author":"[author]", "version":"[version]", "due":"[due]", "status":"[status]"
    //            , "action": DocTree.View.Source.getHtmlAction()
    //        });
            return builder.getTree();
        }
        ,lazyLoad: function(event, data) {
            var objType = DocTree.Model.getObjType();
            var objId   = DocTree.Model.getObjId();
            var node = data.node;
            var folderId = Acm.goodValue(node.data.objectId, 0);
            if (DocTree.View.isTopNode(node)) {
                folderId = 0;
            }
            var pageId = Acm.goodValue(node.data.startRow);
            var cacheKey = DocTree.Model.getCacheKey(folderId, pageId);
            var folderList = DocTree.Model.cacheFolderList.get(cacheKey);
            if (DocTree.Model.validateFolderList(folderList)) {
                data.result = DocTree.View.Source._makeChildNodes(folderList);
                setTimeout(function() {
                    DocTree.Controller.viewChangedTree();
                }, 500);

            } else {
                data.result = DocTree.Service.retrieveFolderListDeferred(DocTree.Model.getObjType(), DocTree.Model.getObjId(), folderId, pageId, data.node
                    ,function(folderList) {
                        data.node.data.totalChildren = folderList.totalChildren;
                        var rc = DocTree.View.Source._makeChildNodes(folderList);
                        setTimeout(function() {
                            DocTree.Controller.viewChangedTree();
                        }, 500);
                        return rc;
                    }
                );
            }
        }
    }

    ,Command: {

    }

    ,Dnd: {

    }

    ,onCommand: function(event, data){
        // Custom event handler that is triggered by keydown-handler and
        // context menu:
        var refNode, moveMode,
            tree = $(this).fancytree("getTree"),
            node = tree.getActiveNode();

        if (0 == data.cmd.indexOf("form/")) {
            var fileType =  data.cmd.substring(5);
            DocTree.View.uploadForm(node, fileType);
            return;
        }
        if (0 == data.cmd.indexOf("file/")) {
            var fileType =  data.cmd.substring(5);
            DocTree.View.uploadFile(node, fileType);
            return;
        }
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
                    node.editCreateNode("child", {"title": "New Folder", "folder": true, "action": DocTree.View.Source.getHtmlAction()});
                }
                break;
            case "newDocument":
                if (!DocTree.View.isEditing()) {
//                    if (!DocTree.View.uploadFolderNode) {
////                        node.editCreateNode("child", {"title": "New Document", "action": DocTree.View.Source.getHtmlAction()});
//////                        DocTree.View.uploadFolderNode = node;
//                        setTimeout(function() {
//                            DocTree.View.uploadToFolder(node);
//                            //DocTree.View.$fileInput.click();
//                        }, 200);
//                    }

                    //node.editCreateNode("child", "New Document");
                    //node.editCreateNode("child", {"title": "New Document", "action": DocTree.View.Source.getHtmlAction()});
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
                DocTree.View._doDownload(node);
                break;
            case "replace":
                node.setStatus("error");
                break;
            case "history":
                node.setStatus("ok");
                break;
            case "open":
                var url = App.getContextPath() + "/plugin/document/" + node.data.objectId;
                window.open(url);
                break;
            case "edit":
                break;
            default:
                Acm.log("Unhandled command: " + data.cmd);
                return;
        }
    }
    ,_doDownload: function(node) {
        DocTree.View.$formDownloadDoc.attr("action", App.getContextPath() + DocTree.Service.API_DOWNLOAD_DOCUMENT_ + node.data.objectId);
        DocTree.View.$formDownloadDoc.submit();
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
    ,onKeyDown: function(event, data){
        var cmd = null;

        // console.log(event.type, $.ui.fancytree.eventToString(event));
        switch( $.ui.fancytree.eventToString(event) ) {
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
            // event.preventDefault();
            // event.stopPropagation();
            return false;
        }
    }
    ,onDblClick: function(event, data) {
        var tree = $(this).fancytree("getTree"),
            node = tree.getActiveNode();
        if (!DocTree.View.isEditing()) {
            if (DocTree.View.isFileNode(node)) {
                $(this).trigger("command", {cmd: "open"});
            }
        }
        //return false;
    }
    ,onClick: function(event, data) {
        if (DocTree.View.isSpecialNode(data.node)) {
            DocTree.View.Paging.doPaging(data.node);
        }
        return true;
    }

    ,isTopNode: function(node) {
        if (node) {
            if (node.data.root) { //not fancy tree root node, which is the parent and is invisible
                return true;
            }
        }
        return false;
    }
    ,isFolderNode: function(node) {
        if (node) {
            if (node.folder) {
                return true;
            }
        }
        return false;
    }
    ,isFileNode: function(node) {
        if (node) {
            if (node.data) {
                if (DocTree.Model.NODE_TYPE_FILE == Acm.goodValue(node.data.objectType)) {   //if (!node.isFolder()) {
                    return true;
                }
            }
        }
        return false;
    }
    ,isSpecialNode: function(node) {
        if (node) {
            if (node.data) {
                if (DocTree.Model.NODE_TYPE_FILE != Acm.goodValue(node.data.objectType) && !node.folder) {
                    return true;
                }
            }
        }
        return false;
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
                    if (dict && dict.data && dict.data.containerObjectType == previousObjType && dict.data.containerObjectId == previousObjId) {
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
            DocTree.View.tree.reload(DocTree.View.Source.source());
        }
        Acm.deferred(DocTree.Controller.viewChangedTree);
    }

    ,getSelectedNodes: function() {
        var a1 = this.tree.getSelectedNodes();
        var z = 1;
        return this.tree.getSelectedNodes();
    }


};

