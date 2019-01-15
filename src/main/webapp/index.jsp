<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>主页</title>
    <script src="${pageContext.request.contextPath}/js/jquery-1.4.2.js"></script>
    <script src="${pageContext.request.contextPath}/laydate/laydate.js"></script>
    <script>
        lay('#version').html('-v'+ laydate.v);

        //执行一个laydate实例
        laydate.render({
            elem: '#dateInput' //指定元素
        });
    </script>
</head>
<body>
    <h2>Welcome To 12306</h2>
    <form action="${pageContext.request.contextPath}/leftNew/checkLeftNews" method="post">
        日期：<input type="text" id="dateInput" name="start_train_date" value="${leftNew.start_train_date}"/>
        上车站：<input type="text" name="from_station_name" value="${leftNew.from_station_name}"/>
        下车站：<input type="text" name="to_station_name" value="${leftNew.to_station_name}"/>
        <input type="submit" value="查询">
    </form>
</body>
</html>
