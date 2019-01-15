<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Title</title>
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

    <center>

        <form action="${pageContext.request.contextPath}/leftNew/checkLeftNews" method="post">
            日期：<input type="text" id="dateInput" name="start_train_date" value="${leftNew.start_train_date}"/>
            上车站：<input type="text" name="from_station_name" value="${leftNew.from_station_name}"/>
            下车站：<input type="text" name="to_station_name" value="${leftNew.to_station_name}"/>
            <input type="submit" value="查询">
        </form>

        <table border="1" style="text-align: center">
            <tr>
                <td>列车编号</td>
                <td>起始站</td>
                <td>终点站</td>
                <td>上车站</td>
                <td>下车站</td>
                <td>发车日期</td>
                <td>上车时间</td>
                <td>到达时间</td>
                <td>历时</td>
                <td>订票状态</td>
                <td>商务座</td>
                <td>一等座</td>
                <td>二等座</td>
                <td>无座</td>
            </tr>

            <c:forEach items="${leftNews}" var="leftNew">
                <tr>
                    <td>${leftNew.station_train_code}</td>
                    <td>${leftNew.start_station_name}</td>
                    <td>${leftNew.end_station_name}</td>
                    <td>${leftNew.from_station_name}</td>
                    <td>${leftNew.to_station_name}</td>
                    <td>${leftNew.start_train_date}</td>
                    <td>${leftNew.start_time}</td>
                    <td>${leftNew.arrive_time}</td>
                    <td>${leftNew.lishi}</td>
                    <td>${leftNew.canWebBuy}</td>
                    <td>${leftNew.swz_num}</td>
                    <td>${leftNew.zy_num}</td>
                    <td>${leftNew.ze_num}</td>
                    <td>${leftNew.wz_num}</td>
                </tr>
            </c:forEach>

        </table>

    </center>
</body>
</html>
