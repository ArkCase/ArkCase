<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<!doctype html>
<head>
    <meta charset="utf-8">

    <!-- Enable IE Standards mode -->
    <meta http-equiv="x-ua-compatible" content="ie=edge">
    <title>Simultaneous editing</title>
    <style type="text/css">
        body {
            margin: 0;
            padding: 0;
            overflow:hidden;
            -ms-content-zooming: none;
        }

        #office_frame {
            width: 100%;
            height: 100%;
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            margin: 0;
            border: none;
            display: block;
        }
    </style>
</head>
<body>
    <iframe id="office_frame" src="${url}"></iframe>
</body>
</html>
