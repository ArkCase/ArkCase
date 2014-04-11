<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout>
    <jsp:attribute name="head">
        <title>Hello World | ACM | Armedia Case Management</title>
        <script type="text/javascript">
            $(document).ready(function () {
                HelloWorld.initialize();
            });
        </script>
    </jsp:attribute>

    <jsp:attribute name="endOfBody">
        <script type="text/javascript" src="<c:url value='/resources/js/helloWorld.js'/>"></script>
    </jsp:attribute>

    <jsp:body>
        <p>Hello, ${sessionScope.acm_username}!</p>
    </jsp:body>
</t:layout>

