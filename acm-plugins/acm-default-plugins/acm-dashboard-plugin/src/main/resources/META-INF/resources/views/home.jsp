<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<t:layout>
<jsp:attribute name="endOfHead">
    <title><spring:message code="dashboard.page.title" text="Dashboard | ACM | Armedia Case Management" /></title>

    <link rel="stylesheet" media="screen" href="<c:url value='/resources/vendors/${vd_angular}/css/sample.test.css'/>" />

    <!--[if lte IE 8]>
      <script>
        document.createElement('adf-dashboard');
        document.createElement('adf-widget');
        document.createElement('adf-widget-content');

        document.createElement('highchart');
      </script>
    <![endif]-->
</jsp:attribute>


<jsp:attribute name="endOfBody">
    <script type="text/javascript" src="<c:url value='/resources/js/dashboard/dashboard.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/dashboard/dashboardObject.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/dashboard/dashboardEvent.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/dashboard/dashboardPage.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/dashboard/dashboardRule.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/dashboard/dashboardService.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/dashboard/dashboardCallback.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_slimscroll}/${js_slimscroll}'/>"></script>

    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_angular}/js/${js_angular_min}'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_angular}/js/${js_angular_route_min}'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_angular}/js/${js_angular_moment}'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_angular}/js/${js_angular_table}'/>"></script>

    <script type="text/javascript" src="<c:url value='/resources/js/dashboard/dashboardAngular/${dashboardFileName}'/>"></script>
</jsp:attribute>

<jsp:body>
    <section class="vbox">
        <section class="scrollable">

            <div class="container" ng-app="sample" style="width:100%">
                <div ng-view />
            </div>

            <%--</section>--%>
            <footer class="footer bg-white b-t b-light">
                <p data-i18n="dashboard:label.powered-by-arkcase">Powered by ArkCase</p>
            </footer>
        </section>
    </section>

</jsp:body>
</t:layout>


