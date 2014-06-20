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

<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/core/acm.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/core/acmDialog.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/core/acmAjax.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/core/acmDispatcher.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/core/acmObject.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/core/acmValidation.js"></script>

<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/topbar/topbar.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/topbar/topbarObject.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/topbar/topbarEvent.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/topbar/topbarPage.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/topbar/topbarRule.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/topbar/topbarService.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/topbar/topbarCallback.js"></script>

<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/sidebar/sidebar.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/sidebar/sidebarObject.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/sidebar/sidebarEvent.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/sidebar/sidebarPage.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/sidebar/sidebarRule.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/sidebar/sidebarService.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/sidebar/sidebarCallback.js"></script>


<%--<script type="text/javascript" src="//underscorejs.org/underscore-min.js"></script>--%>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_underscore}/underscore-min.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_typeahead}/typeahead.js"></script>
<%--<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_typeahead}/typeahead.jquery.js"></script>--%>
<%--<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_typeahead}/bloodhound.js"></script>--%>

<%--<script type="text/javascript">--%>
    <%--$(document).ready(function () {--%>
        <%--Acm.initialize();--%>
        <%--Topbar.initialize();--%>
        <%--Sidebar.initialize();--%>
    <%--});--%>
<%--</script>--%>
<jsp:invoke fragment="endOfBody"/>
<%@include file="/WEB-INF/tagf/ready.tagf"%>
</body>
</html>