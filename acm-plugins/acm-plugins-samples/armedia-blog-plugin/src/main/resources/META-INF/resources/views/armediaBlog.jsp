<!DOCTYPE html>
<%@include file="/fragments/header.jspf" %>

<link rel="stylesheet" media="screen" href="<c:url value="/resources/css/feedReader/FeedEk.css"/>" />

<script type="text/javascript" src="<c:url value="/resources/js/feedReader/FeedEk.js"/> "></script>

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

</head>

<body>
<%@include file="/fragments/topbar.jspf" %>

<div id="content">
    <div id="divRss"></div>
</div>

</body>

<%@include file="/fragments/footer.jspf" %>
