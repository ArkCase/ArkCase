/**
 * AcmEx serves as namespace for support for extended functions, third party support, etc.
 *
 * @author jwu
 */
var AcmEx = AcmEx || {
    create : function() {
        if (AcmEx.Object.create)  {AcmEx.Object.create()};
        //if (AcmEx.Model.create)   {AcmEx.Model.create();}
        if (AcmEx.Service.create) {AcmEx.Service.create();}
    }
    ,onInitialize : function() {
        if (AcmEx.Object.onInitialize)  {AcmEx.Object.onInitialize()};
        //if (AcmEx.Model.onInitialize)   {AcmEx.Model.onInitialize();}
        if (AcmEx.Service.onInitialize) {AcmEx.Service.onInitialize();}
    }




    //
    // Depth first tree builder for fancytree (more accurately a forest builder)
    //
    ,FancyTreeBuilder: {
        _path: []
        ,_depth: 0
        ,_pushDepth: function(node) {
            if (this._path.length > this._depth) {
                this._path[this._depth] = node;
            } else {
                this._path.push(node);
            }
            this._depth++;
        }
        ,_popDepth: function() {
            if (0 >= this._depth) {
                return null;
            }
            this._depth--;
            return this._path[this._depth];
        }
        ,_peekDepth: function() {
            if (0 >= this._depth) {
                return null;
            }
            return this._path[this._depth-1];
        }

        ,_nodes: []
        ,reset: function() {
            this._path = [];
            this._depth = 0;
            this._nodes = [];
            return this;
        }
        ,addBranch: function(node) {
            return this._addNode(node, false);
        }
        ,addBranchLast: function(node) {
            return this._addNode(node, true);
        }
        ,makeLast: function() {
            //keep popping stack until a node that is not the last child is found
            var nonLastChildFound = false;
            do {
                var item = this._peekDepth();
                nonLastChildFound = false;
                if (item) {
                    if (item.isLast) {
                        this._popDepth();
                        nonLastChildFound = true;
                    }
                }
            } while (nonLastChildFound);

            this._popDepth();   //the node found is not last child, so next node to insert should be its sibling. Pop to parent to prepare for inserting its sibling

            return this;
        }
        ,addLeaf: function(node) {
            this._addNode(node, false);
            this._popDepth();
            return this;
        }
        ,addLeafLast: function(node) {
            this._addNode(node, true);
            this._popDepth();
            this.makeLast();
            return this;
        }
        ,_addNode: function(node, isLast) {
            if (0 == this._depth) {
                this._nodes.push(node);
            } else {
                var parent = this._peekDepth();
                if (!parent.node.children) {
                    parent.node.children = [node];
                } else {
                    parent.node.children.push(node);
                }
            }
            this._pushDepth({node:node, isLast:isLast});
            return this;
        }
        ,getTree: function() {
            return this._nodes;
        }
    }

};



