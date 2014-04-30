<%@tag description="ACM No top/side bar Page Layout template" pageEncoding="UTF-8"%>
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
    </div>
    <jsp:invoke fragment="endOfHead"/>
</head>
<body class="">
<section class="vbox">

    <section>
        <section class="hbox stretch">

            <jsp:doBody/>
        </section>
    </section>
</section>

<!--
<script type="text/javascript" src="<c:url value='/resources/js/acm/shared/acm.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/js/acm/shared/acmDialog.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/js/acm/shared/acmAjax.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/js/acm/shared/acmDispatcher.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/js/acm/shared/acmObject.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/js/acm/shared/acmValidation.js'/>"></script>

<script type="text/javascript">
    $(document).ready(function () {
        Acm.initialize();
    });
</script>
-->
<jsp:invoke fragment="endOfBody"/>
</body>
</html>