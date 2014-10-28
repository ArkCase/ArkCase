/**
 * Admin.Object
 *
 * manages screen objects
 *
 * @author jwu
 */
Admin.Object = {

    create : function() {
        this.$btnTest = $("#test");
        this.$btnTest.click(function(e) {Admin.Event.onClickBtnTest(e);});


        this.$tree = $("#tree");
        this._useFancyTree(this.$tree);
    }

    //  Use this to build the Admin tree structure
    //------------------ Tree  ------------------
    //

    ,_useFancyTree: function($s) {

        $s.fancytree({
            activate: function(event, data){
                var node = data.node
                if(node.data.href){
                    var url = App.getContextPath() + node.data.href;
                    window.location.href=url;
                }
            },
            source: function() {
                return Admin.Object.treeSource();
            } //end source

        }); //end fancytree

        $s.contextmenu({
            //delegate: "span.fancytree-title",
            delegate: ".fancytree-title",
            beforeOpen: function(event, ui) {
                var node = $.ui.fancytree.getNode(ui.target);
                node.setActive();
            },
            select: function(event, ui) {
                var node = $.ui.fancytree.getNode(ui.target);
                alert("select " + ui.cmd + " on " + node);
            }
        });

    }

    ,treeSource: function() {
        var builder = AcmEx.FancyTreeBuilder.reset();

        builder.addBranch({key: "acc"                                               //level 1: /Access Control
            ,title: "Access Controls"
            ,tooltip: "Access Controls"
            ,folder : true
            ,expanded: true
        })
            .addLeafLast({key: "acp"                                                //level 2: /Access Control/Access Control Policy
                ,title: "Access Control Policy"
                ,href: "/plugin/admin/access"
            })

        builder.addBranch({key: "dsh"                                               //level 1: /Dashboard
            ,title: "Dashboard"
            ,tooltip: "Dashboard"
            ,folder : true
            ,expanded: true
        })
            .addLeafLast({key: "dc"                                                 //level 2: /Dashboard/Dashboard Configuration
                ,title: "Dashboard Configuration"
                ,href: "/plugin/admin/dashboard"
            })

        builder.addBranch({key: "rpt"                                               //level 1: /Reports
            ,title: "Reports"
            ,tooltip: "Reports"
            ,folder : true
            ,expanded: true
        })
            .addLeafLast({key: "rc"                                                 //level 2: /Reports/Reports Configuration
                ,title: "Reports Configuration"
            })
        return builder.getTree();
    }

    //----------------- end of tree -----------------


};




