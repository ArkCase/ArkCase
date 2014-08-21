<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout>
<jsp:attribute name="endOfHead">
    <title>${pageDescriptor.title}</title>

    <%--<link rel="stylesheet" media="screen" href="<c:url value='/'/>resources/vendors/${vd_angular}/css/sample.min.0a2f966c.css" />--%>
    <link rel="stylesheet" media="screen" href="<c:url value='/'/>resources/vendors/${vd_angular}/css/ng-table.css"/>
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

    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_slimscroll}/jquery.slimscroll.min.js"></script>

    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_angular}/js/angular.min.7931140c.js"></script>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_angular}/js/angular-route.min.7d4fa7e4.js"></script>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_angular}/js/ng-table.js"></script>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_angular}/js/sample.min.js"></script>
</jsp:attribute>

<jsp:body>
<section id="content">
<section class="vbox">
<%--<section class="scrollable padder">--%>

    <div class="container" ng-app="sample" style="width:100%">
        <%--<div class="navbar navbar-inverse navbar-fixed-top" role="navigation">--%>
        <%--<div class="navbar navbar-inverse " role="navigation">--%>
            <%--<div class="container" ng-controller="navigationCtrl">--%>
                <%--<div class="navbar-header">--%>
                    <%--<button type="button" class="navbar-toggle" ng-click="toggleNav()">--%>
                        <%--<span class="sr-only">Toggle navigation</span>--%>
                        <%--<span class="icon-bar"></span>--%>
                        <%--<span class="icon-bar"></span>--%>
                        <%--<span class="icon-bar"></span>--%>
                    <%--</button>--%>
                    <%--<a class="navbar-brand" href="#">ADF</a>--%>
                <%--</div>--%>
                <%--<div collapse="navCollapsed" class="collapse navbar-collapse">--%>
                    <%--<ul class="nav navbar-nav">--%>
                        <%--<li ng-class="navClass('sample/01')">--%>
                            <%--<a href="#/sample/01">Sample 01</a>--%>
                        <%--</li>--%>
                        <%--<li ng-class="navClass('sample/02')">--%>
                            <%--<a href="#/sample/02">Sample 02</a>--%>
                        <%--</li>--%>
                    <%--</ul>--%>
                <%--</div><!--/.nav-collapse -->--%>
            <%--</div>--%>
        <%--</div>--%>

        <div ng-view />

    </div>

<%--</section>--%>
<footer class="footer bg-white b-t b-light">
    <p>Powered by Armedia Case Management 3.0.</p>
</footer>
</section>
</section>
</jsp:body>
</t:layout>


