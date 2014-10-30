<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<t:layout>
    <jsp:attribute name="endOfHead">
        <title><spring:message code="adminAccess.page.title" text="Admin | ACM | Armedia Case Management" /></title>
    </jsp:attribute>


    <jsp:attribute name="endOfBody">
        <script type="text/javascript" src="<c:url value='/resources/js/admin/access/adminAccess.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/resources/js/admin/access/adminAccessObject.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/resources/js/admin/access/adminAccessEvent.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/resources/js/admin/access/adminAccessPage.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/resources/js/admin/access/adminAccessRule.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/resources/js/admin/access/adminAccessService.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/resources/js/admin/access/adminAccessCallback.js'/>"></script>

        <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_slimscroll}/${js_slimscroll}"></script>

        <!-- JTable -->
        <link rel="stylesheet" href="<c:url value='/'/>resources/vendors/${vd_acm}/themes/basic/${vd_jtable}/blue/jtable.css" type="text/css"/>
        <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_jtable}/${js_jtable}"></script>

        <%--Fancy Tree--%>
        <link href="<c:url value='/'/>resources/vendors/${vd_fancytree}/skin-win8/ui.fancytree.css" rel="stylesheet">
        <script src="<c:url value='/'/>resources/vendors/${vd_fancytree}/${js_fancytree}"></script>
        <script src="<c:url value='/'/>resources/vendors/${vd_contextmenu}/${js_contextmenu}"></script>

    </jsp:attribute>

    <jsp:body>

        <section id="content">
            <section class="vbox">
                <section class="scrollable">
                    <section class="hbox stretch"><!-- /.aside -->

                        <aside class="aside-lg bg-light lt">
                            <section class="vbox animated fadeInLeft">
                                <section class="scrollable">
                                    <header class="dk header">
                                        <h3 class="m-b-xs text-black pull-left"><spring:message code="admin.page.descLong" text="Administration" /></h3>
                                    </header>
                                    <div class="row m-b">
                                        <div class="col-sm-12">
                                            <div id="tree"></div>
                                        </div>
                                    </div>
                                </section>
                            </section>
                        </aside>
                        <aside id="email-content" class="bg-light lter">
                                <section class="vbox">
                                    <section class="scrollable padder">
                                        <section class="row m-b-md">
                                            <div class="col-sm-12">
                                                <h3 class="m-b-xs text-black"><spring:message code="adminAccess.page.descShort" text="Access Control Policy" /></h3>
                                            </div>
                                        </section>
                                        <section>
                                            <div class="row">
                                                <div class="col-md-12">
                                                    <section class="panel panel-default" id="secIncident">
                                                        <div id="divAdminAccessControlPolicy" style="width:100%"></div>
                                                    </section>
                                                </div>
                                            </div>
                                        </section>
                                    </section>
                            </section>
                        </aside>

                    </section>
                </section>
            </section>
        </section>


  <%--      <script type='text/javascript'>//<![CDATA[

        $(function() {
            $("#tree").fancytree({

                activate: function(event, data) {
                    var node = data.node;
                    // Use <a> href and target attributes to load the content:
                    if( node.data.href ){
                        // Open target
                        window.open(node.data.href, "_self");
                        // or open target in iframe
    //                $("[name=contentFrame]").attr("src", node.data.href);
                    }
                },

                source: [{
                    title: "Access Controls",
                    folder: true,
                    expanded: "fancytree-expanded",
                    children: [{
                        title: "Access Control Policy",
                        href: "access"
                    }, {
                        title: "Object locks",
                        href: "locks",
                        description1: "John Doe",
                        description2: "Victim"
                    }]
                }, {
                    title: "Dashboard",
                    folder: true,
                    expanded: "fancytree-expanded",
                    children: [{
                        title: "Dashboard Configuration",
                        href: "dashboardconfig"
                    }]
                }, {
                    title: "Reports",
                    folder: true,
                    expanded: "fancytree-expanded",
                    children: [{
                        title: "Reports Configuration",
                        href: "reportsconfig"
                    }]
                }]
            });

        });
        </script>--%>

    </jsp:body>
</t:layout>


