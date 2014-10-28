<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<t:layout>
<jsp:attribute name="endOfHead">
    <title><spring:message code="admin.page.title" text="Admin | ACM | Armedia Case Management" /></title>
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
                                <header class="header bg-white b-b clearfix">
                                    <div class="row m-t-sm"></div>
                                </header>
                                <section class="scrollable wrapper w-f">
                                    <section class=" panel-default">
                                        <h3><i class="fa fa-long-arrow-left"></i> Configure settings in the application.</h3>
                                    </section>
                                </section>
                            </section>
                        </aside>

                    </section>
                </section>
            </section>
        </section>
    </jsp:body>
</t:layout>


