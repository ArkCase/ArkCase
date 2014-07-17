<%@tag description="ACM Page Layout template" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@attribute name="endOfHead" fragment="true" %>
<%@attribute name="endOfBody" fragment="true" %>

<!DOCTYPE html>
<html lang="en" class="app">
<head>
    <meta charset="utf-8" />
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <%@include file="/WEB-INF/tagf/global.tagf" %>

    <div id="acmData" itemscope="true" style="display: none">
        <span itemprop="contextPath"><%=request.getContextPath()%></span>
        <span itemprop="userName">${sessionScope.acm_username}</span>
    </div>
    <jsp:invoke fragment="endOfHead"/>
</head>
<body class="">
<section class="vbox">
    <%@include file="/WEB-INF/tagf/topbar.tagf"%>

    <section>
        <section class="hbox stretch">
            <%@include file="/WEB-INF/tagf/sidebar.tagf"%>

            <jsp:doBody/>
        </section>
    </section>
    <%@include file="/WEB-INF/tagf/footer.tagf"%>
</section>

<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_underscore}/underscore-min.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_typeahead}/typeahead.js"></script>
<%--<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_typeahead}/typeahead.jquery.js"></script>--%>
<%--<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_typeahead}/bloodhound.js"></script>--%>

<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/js/core/acm.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/js/core/acmDialog.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/js/core/acmAjax.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/js/core/acmDispatcher.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/js/core/acmObject.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/js/core/acmEvent.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/js/core/acmService.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/js/core/acmCallback.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/js/core/acmRule.js"></script>

<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/js/topbar/topbar.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/js/topbar/topbarObject.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/js/topbar/topbarEvent.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/js/topbar/topbarPage.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/js/topbar/topbarRule.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/js/topbar/topbarService.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/js/topbar/topbarCallback.js"></script>

<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/js/sidebar/sidebar.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/js/sidebar/sidebarObject.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/js/sidebar/sidebarEvent.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/js/sidebar/sidebarPage.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/js/sidebar/sidebarRule.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/js/sidebar/sidebarService.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/js/sidebar/sidebarCallback.js"></script>

<jsp:invoke fragment="endOfBody"/>
<%@include file="/WEB-INF/tagf/ready.tagf"%>
</body>
</html>