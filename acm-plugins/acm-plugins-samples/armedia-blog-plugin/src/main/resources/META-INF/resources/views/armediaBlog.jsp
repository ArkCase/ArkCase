<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout>
    <jsp:attribute name="endOfHead">
        <title>Armedia Blog | ACM | Armedia Case Management</title>
        <script type="text/javascript">
            $(document).ready(function ()
            {
                $('#divRss').FeedEk({
                    FeedUrl : '${feedUrl}',
                    MaxCount : 5,
                    ShowDesc : true,
                    ShowPubDate:true,
                    DescCharacterLimit:500,
                    TitleLinkTarget:'_blank'
                });
            });

        </script>
    </jsp:attribute>

    <jsp:attribute name="endOfBody">
        <link rel="stylesheet" media="screen" href="<c:url value="/resources/css/feedReader/FeedEk.css"/>" />
        <script type="text/javascript" src="<c:url value="/resources/js/feedReader/FeedEk.js"/> "></script>
    </jsp:attribute>

    <jsp:body>
        <div id="divRss"></div>
    </jsp:body>
</t:layout>
