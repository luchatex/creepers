<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="socPath"
	value="ws://${pageContext.request.serverName}:${pageContext.request.serverPort}${ctx}/" />
<!DOCTYPE html>
<html lang="en">
<head>
<script src="${ctx}/static/js/newEcharts/dist/echarts.js"></script>
</head>
<body>
<!-- Main content starts -->
	<div class="content">
		<!-- Main bar -->
		<div class="mainbar">
			<!-- Page heading -->
			<div class="page-head">
				<h2 class="pull-left">
					<i class="icon-table"></i> 全量任务断点续爬
				</h2>

				<!-- Breadcrumb -->
				<div class="bread-crumb pull-right">
					<a href="${ctx}"><i class="icon-home"></i> Home</a> <span
						class="divider">/</span> <a href="${ctx}" class="bread-current">Dashboard</a>
				</div>
				<div class="clearfix"></div>
			</div>
			<!-- Page heading ends -->

			<!-- Matter -->
			<div class="matter">
				<div class="container">
					<div class="row">
						<div class="col-md-12">
							<div class="widget wgreen">
								<div class="widget-head">
									<div class="pull-left">全量任务断点续爬</div>
									<div class="widget-icons pull-right">
										<a href="#" class="wminimize"><i class="icon-chevron-up"></i></a>
										<a href="#" class="wclose"><i class="icon-remove"></i></a>
									</div>
									<div class="clearfix"></div>
								</div>

								<div class="widget-content">
									<div class="padd">
										<!-- Form starts.  -->
										<form id="queryForm" class="form-horizontal"
											action="${ctx}/taskList/initTaskList" method="get">
											<div class="form-group">
												<div class="col-md-12"></div>
											</div>
											<div class="form-group">
												<label class="col-md-2 control-label">任务类型</label>
												<div class="col-md-3">
													<select id="taskType" name="taskType" class="form-control"
														onchange="countTaskByFlag()">
														<c:forEach items="${taskTypeList }" var="item">
															<option value="${item.value }">${item.name }</option>
														</c:forEach>
													</select>
												</div>

												<div class="col-md-3">
													<a href="#" class="btn btn-primary" title="刷新"
														onclick="countTaskByFlag()"> 刷新 </a> <a href="#"
														class="btn btn-primary" title="清除消息队列"
														onclick="cleanMsgCache()">清除消息队列 </a> <a href="#"
														class="btn btn-primary" title="断点续爬"
														onclick="resumeTask()">断点续传 </a>  <input type="hidden"
														id="wsId" value="">
												</div>
											</div>
											<div id="myEcharts" style="width: 700px; height: 400px;"></div>
										</form>
									</div>
								</div>
								<div class="widget-foot"></div>
							</div>
						</div>
					</div>
				</div>
			<!-- Matter ends -->
		</div>
	</div>

	<script type="text/javascript">
		function countTaskByFlag() {
			var taskType = $("#taskType").val();
			$.ajax({
				type : "get",
				url : '${ctx}/taskList/countTaskByFlag',
				data : {
					'taskType' : taskType,
				},
				success : function(dataJson) {
					// Step:3 conifg ECharts's path, link to echarts.js from current page.
					// Step:3 为模块加载器配置echarts的路径，从当前页面链接到echarts.js，定义所需图表路径
					require.config({
						paths : {
							echarts : '../static/js/newEcharts/dist'
						}
					});
					// Step:4 require echarts and use it in the callback.
					// Step:4 动态加载echarts然后在回调函数中开始使用，注意保持按需加载结构定义图表路径
					require([ 'echarts', 'echarts/chart/pie' ], function(ec) {
						var myChart = ec.init(document
								.getElementById('myEcharts'));
						myChart.setOption({
							series : [ {
								name : '访问来源',
								type : 'pie',
								radius : '70%',
								data : dataJson,
								itemStyle : {
									normal : {
										label : {
											show : true,
											formatter : '{b} : {c} ({d}%)'
										},
										labelLine : {
											show : true
										}
									}
								}
							} ]
						});
					});
				},
				error : function() {
					alert("获取数据失败!");
				},
			})
			// 基于准备好的dom，初始化echarts实例
		};
		countTaskByFlag();
		function cleanMsgCache() {
			var taskType = $("#taskType").val();
			$.ajax({
				type : "get",
				url : '${ctx}/taskList/clearRedisCache',
				data : {
					'taskType' : taskType,
				},
				success : function(dataJson) {
					countTaskByFlag();
				},
				error : function() {
					alert("清除消费队列失败!");
				},
			})
			// 基于准备好的dom，初始化echarts实例
		}
		function resumeTask() {
			var taskType = $("#taskType").val();
			var wsId = $("#wsId").val();
			$.ajax({
				type : "get",
				url : '${ctx}/taskList/resumeTask',
				data : {
					'taskType' : taskType,
					'wsId' : wsId
				},
				success : function(data) {
					countTaskByFlag();
				},
				error : function() {
					alert("爬虫任务断点续爬失败!");
				},
			});
			$("#submit").attr("disabled", "disabled");
		}
	</script>
</body>