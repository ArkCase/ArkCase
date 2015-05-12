<%@tag description="ACM Page Layout template" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@attribute name="endOfHead" fragment="true" %>
<%@attribute name="endOfBody" fragment="true" %>

<!DOCTYPE html>
<html lang="en" class="app">
<head>
    <meta charset="utf-8" />
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <%--<%@include file="/WEB-INF/tagf/global.tagf" %>--%>
    <%@include file="/resources/include/global.jspf" %>

    <div id="acmData" itemscope="true" style="display: none">
        <span itemprop="contextPath"><%=request.getContextPath()%></span>
        <span itemprop="userName">${sessionScope.acm_username}</span>
        <span itemprop="application">${sessionScope.acm_application.toJson()}</span>
        <span itemprop="objectTypes">${acm_application.getObjectTypesAsJson()}</span>
        <span itemprop="issueCollectorFlag">${acm_application.getIssueCollectorFlag()}</span>
    </div>
    <jsp:invoke fragment="endOfHead"/>
</head>
<body class="">
<section class="vbox">
    <%--<%@include file="/WEB-INF/tagf/topbar.tagf"%>--%>
    <%@include file="/resources/include/topbar.jspf" %>

    <section>
        <section class="hbox stretch">
            <%--<%@include file="/WEB-INF/tagf/sidebar.tagf"%>--%>
            <%@include file="/resources/include/sidebar.jspf" %>
            <section id="content">
                <%--<%@include file="/WEB-INF/tagf/msgBoard.tagf"%>--%>
                <%@include file="/resources/include/msgBoard.jspf" %>
                <jsp:doBody/>
            </section>
        </section>
    </section>
    <%--<%@include file="/WEB-INF/tagf/footer.tagf"%>--%>
    <%@include file="/resources/include/footer.jspf" %>
</section>

<script type="text/javascript" src="<c:url value='/resources/vendors/${vd_underscore}/${js_underscore}'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/vendors/${vd_typeahead}/${js_typeahead}'/>"></script>
<%--<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_typeahead}/typeahead.jquery.js"></script>--%>
<%--<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_typeahead}/bloodhound.js"></script>--%>

<script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/app/app.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/app/appModel.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/app/appView.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/app/appController.js'/>"></script>

<script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/app/appObject.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/app/appEvent.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/app/appService.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/app/appCallback.js'/>"></script>

<script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/topbar/topbar.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/topbar/topbarService.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/topbar/topbarModel.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/topbar/topbarView.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/topbar/topbarController.js'/>"></script>

<script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/sidebar/sidebar.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/sidebar/sidebarService.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/sidebar/sidebarModel.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/sidebar/sidebarView.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/sidebar/sidebarController.js'/>"></script>


<script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/issueCollector/issueCollector.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/issueCollector/issueCollectorService.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/issueCollector/issueCollectorModel.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/issueCollector/issueCollectorView.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/issueCollector/issueCollectorController.js'/>"></script>


<script type="text/javascript" src="<c:url value='/resources/js/search/searchBase.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/js/search/searchBaseModel.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/js/search/searchBaseService.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/js/search/searchBaseView.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/js/search/searchBaseController.js'/>"></script>

<script type="text/javascript">
    if ("undefined" != typeof App) {
        App.create();
    }
</script>

<jsp:invoke fragment="endOfBody"/>

<script type="text/javascript">
    jQuery(document).ready(function() {
        var context = {};
        context.path = Acm.Object.MicroData.get("contextPath");
        Application.run(context);
    });
</script>

<%--<%@include file="/WEB-INF/tagf/dialog.tagf" %>--%>
<%--<%@include file="/resources/include/dialog.jspf" %>--%>

</body>
</html>