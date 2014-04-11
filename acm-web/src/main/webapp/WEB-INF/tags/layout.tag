<%@tag description="ACM Page Layout template" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@attribute name="head" fragment="true" %>
<%@attribute name="endOfBody" fragment="true" %>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <%--<title><spring:message code="webapp.header.title"/></title>--%>
    <%@include file="/WEB-INF/tagf/global.tagf" %>
    <jsp:invoke fragment="head"/>
</head>
<body>
<%@include file="/WEB-INF/tagf/topbar.tagf"%>
<%@include file="/WEB-INF/tagf/sidebar.tagf"%>
<div id="divContent">
    <jsp:doBody/>
</div>
<%@include file="/WEB-INF/tagf/footer.tagf"%>
<jsp:invoke fragment="endOfBody"/>
</body>
</html>

