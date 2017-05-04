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
<!-- <title>性能监控</title> -->
<!-- echart start-->
<%-- <script src="${ctx}/static/js/echarts/echarts-all-3.js"></script> --%>
<%-- <script src="${ctx}/static/js/echarts/echarts.min.js"></script> --%>
<%-- <script src="${ctx}/static/js/echarts/src/esl.js"></script> --%>
<!-- <script src="http://echarts.baidu.com/echarts2/doc/example/www/js/echarts.js"></script> -->
<script src="${ctx}/static/js/newEcharts/dist/echarts.js"></script>
<!-- echart end-->
</head>
<body>
	<!-- Main content starts -->
	<div class="content">
		<!-- Main bar -->
		<div class="mainbar">
			<!-- Page heading -->
			<div class="page-head">
				<h2 class="pull-left">
					<i class="icon-table"></i> 性能监控
				</h2>

				<!-- Breadcrumb -->
				<div class="bread-crumb pull-right">
					<a href="${ctx}"><i class="icon-home"></i> Home</a> <span
						class="divider">/</span> <a href="${ctx}" class="bread-current">Dashboard</a>
				</div>

				<div class="clearfix"></div>

			</div>
			<!-- Page heading ends -->
			<div id="addItem"></div>
			<!-- Matter -->
			<div class="matter">
				<div class="container">
					<div class="row">
						<div class="col-md-12">
							<div class="widget wgreen">
								<div class="widget-head">
									<div class="widget-icons pull-left">
										<i class="icon-briefcase"></i>警告设置
									</div>
									<div class="clearfix"></div>
								</div>
								<div class="widget-content">
									<div class="padd">
										<table class="table table-bordered">
											<thead>
												<tr>
													<th>名称</th>
													<th>参数</th>
													<th>预警设置</th>
													<th>邮箱设置</th>
												</tr>
											</thead>
											<tbody>
												<tr>
													<td>CPU</td>
													<td>当前使用率：<span id="td_cpuUsage" style="color: red;">50</span>
														%
													</td>
													<td>
														<table>
															<tr>
																<td>使用率超出</td>
																<td><input class='inputclass' name='cpu' id='cpu'
																	type='text' value='${cpu}' /> %,</td>
																<td>发送邮箱提示 <a class='btn btn-info'
																	href='javascript:void(0)' onclick='modifySer("cpu");'>
																		修改 </a></td>
															</tr>
														</table>
													</td>
													<td rowspan='3' align="center"
														style="vertical-align: middle;"><input
														class='inputclass' style='width: 250px; height: 32px;'
														name='toEmail' id='toEmail' type='text' value='${toEmail}' />
														<a class='btn btn-info' href='javascript:void(0)'
														onclick='modifySer("toEmail");'> 修改 </a></td>
												</tr>
												<tr>
													<td>服务器内存</td>
													<td>当前使用率：<span id="td_serverUsage"
														style="color: blue;">50</span> %
													</td>
													<td>
														<table>
															<tr>
																<td>使用率超出</td>
																<td><input class='inputclass' name='ram' id='ram'
																	type='text' value='${ram}' /> %,</td>
																<td>发送邮箱提示 <a class='btn btn-info'
																	href='javascript:void(0)' onclick='modifySer("ram");'>
																		修改 </a></td>
															</tr>
														</table>
													</td>
												</tr>
												<tr>
													<td>JVM内存</td>
													<td>当前使用率：<span id="td_jvmUsage" style="color: green;">50</span>
														%
													</td>
													<td>
														<table>
															<tr>
																<td>使用率超出</td>
																<td><input class='inputclass' name='jvm' id='jvm'
																	type='text' value='${jvm}' /> %,</td>
																<td>发送邮箱提示 <a class='btn btn-info'
																	href='javascript:void(0)' onclick='modifySer("jvm");'>
																		修改 </a></td>
															</tr>
														</table>
													</td>
												</tr>
											</tbody>
										</table>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-md-3">
							<div class="widget">
								<div class="widget-head">
									<div class="widget-icons pull-left">
										<i class="icon-list"></i>服务器信息
									</div>
									<div class="clearfix"></div>
								</div>
								<div class="widget-content" style="overflow: scroll;">
									<div class="padd">
										<table class="table  table-striped table-bordered table-hover">
											<tbody>
												<tr>
													<td>ip地址</td>
													<td id="hostIp">${systemInfo.hostIp}</td>
												</tr>
												<tr>
													<td>主机名</td>
													<td id="hostName">${systemInfo.hostName}</td>
												</tr>
												<tr>
													<td>操作系统</td>
													<td id="osName">${systemInfo.osName}</td>
												</tr>
												<tr>
													<td>操作系统架构</td>
													<td id="arch">${systemInfo.arch}</td>
												</tr>
												<tr>
													<td>操作系统版本</td>
													<td id="osVersion">${systemInfo.osVersion}</td>
												</tr>
												<tr>
													<td>内核个数</td>
													<td id="processors">${systemInfo.processors}</td>
												</tr>
												<tr>
													<td>java运行版本</td>
													<td id="javaVersion">${systemInfo.javaVersion}</td>
												</tr>
												<tr>
													<td>java供应商url</td>
													<td class="left" id="javaUrl">${systemInfo.javaUrl}</td>
												</tr>
												</tr>
												<tr>
													<td>java安装路径</td>
													<td class="left" id="javaHome">${systemInfo.javaHome}</td>
												</tr>
												</tr>
												<tr>
													<td>临时文件路径</td>
													<td class="left" id="tmpdir">${systemInfo.tmpdir}</td>
												</tr>
											</tbody>
										</table>
									</div>
								</div>
							</div>
						</div>
						<div class="col-md-9">
							<div class="widget">
								<div class="widget-head">
									<div class="widget-icons pull-left">
										<i class="icon-leaf"></i>实时监控
									</div>
									<div class="clearfix"></div>
								</div>
								<div class="widget-content">
									<div id="lineChart" style="height: 370px;"></div>
								</div>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-md-12">
							<div class="widget">
								<div class="widget-head">
									<div class="widget-icons pull-left">
										<i class="icon-rss"></i>实时监控
									</div>
									<div class="clearfix"></div>
								</div>
								<div class="widget-content">
									<div class="form-group">
										<div class="col-md-4">
											<div id="gaugeChart1" style="height: 240px;"></div>
										</div>
										<div class="col-md-4">
											<div id="gaugeChart2" style="height: 240px;"></div>
										</div>
										<div class="col-md-4">
											<div id="gaugeChart3" style="height: 240px;"></div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	</div>

	<script type="text/javascript">
		function modifySer(key) {
			$.ajax({
				async : false,
				url : "${ctx}/monitor/modifySer",
				data : {
					"key" : key,
					"value" : $("#" + key).val()
				},
				dataType : "json",
				success : function(data) {
					if (data.flag) {
						alert("更新成功！");
					} else {
						alert("更新失败！");
					}
				}
			});
		}

		// Step:3 conifg ECharts's path, link to echarts.js from current page.
		// Step:3 为模块加载器配置echarts的路径，从当前页面链接到echarts.js，定义所需图表路径
		require.config({
			paths : {
				echarts : '../static/js/newEcharts/dist'
			}
		});
		// Step:4 require echarts and use it in the callback.
		// Step:4 动态加载echarts然后在回调函数中开始使用，注意保持按需加载结构定义图表路径
		require(
				[ 'echarts', 'echarts/chart/line', 'echarts/chart/gauge' ],
				function(ec) {
					//--- 折柱 ---
					var myChart = ec.init(document.getElementById('lineChart'));
					var now = new Date();
					var res = [];
					var len = 20;
					while (len--) {
						var time = now.toLocaleTimeString().replace(/^\D*/, '');
						time = time.substr(time.indexOf(":") + 1);
						res.unshift(time);
						now = new Date(now - 1000);
					}
					var option = {
						title : {
							text : 'cpu和内存使用率变化',
						},
						tooltip : {
							trigger : 'axis'
						},
						legend : {
							data : [ 'jvm内存使用率', '物理内存使用率', 'cpu使用率' ]
						},
						//右上角的工具栏配置
						toolbox : {
							show : true,
							feature : {
								mark : {
									show : true
								},//辅助线
								dataView : {
									show : true,
									readOnly : false
								},//数据视图
								magicType : {
									show : true,
									type : [ 'line', 'bar' ]
								},//视图切换
								restore : {
									show : true
								},//还原
								saveAsImage : {
									show : true
								}
							//保存
							}
						},
						//拖拽重计算
						calculable : true,
						xAxis : [ {
							type : 'category',// 坐标轴类型，横轴默认为类目型'category'，纵轴默认为数值型'value'
							name : '时间',
							data : res,
							axisLabel : {
								rotate : 40,
							}
						} ],
						yAxis : [ {
							type : 'value',
							name : '使用率',
							min : 0,
							max : 100,
							splitNumber : 5,//分割段数
							axisLabel : {
								formatter : '{value} %'
							},
						} ],
						series : [
								{
									name : 'jvm内存使用率',
									type : 'line',
									data : [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
											0, 0, 0, 0, 0, 0, 0, 0, 0 ]
								},
								{
									name : '物理内存使用率',
									type : 'line',
									data : [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
											0, 0, 0, 0, 0, 0, 0, 0, 0 ]
								},
								{
									name : 'cpu使用率',
									type : 'line',
									data : [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
											0, 0, 0, 0, 0, 0, 0, 0, 0 ]
								} ]
					};
					myChart.setOption(option);
					var gaugeChart1 = ec.init(document
							.getElementById('gaugeChart1'));
					var gaugeChart2 = ec.init(document
							.getElementById('gaugeChart2'));
					var gaugeChart3 = ec.init(document
							.getElementById('gaugeChart3'));
					one_option = {
						tooltip : {
							formatter : "{a} <br/>{b} : {c}%"
						},
						series : [ {
							name : '监控指标',
							type : 'gauge',
							radius : [ 0, '95%' ],
							axisLine : { // 坐标轴线
								lineStyle : { // 属性lineStyle控制线条样式
									width : 20
								}
							},
							pointer : {
								color : '#FF0000'
							},
							title : {
								show : true,
								offsetCenter : [ 0, "95%" ],
							},
							detail : {
								formatter : '{value}%'
							},
							data : [ {
								value : 50,
								name : 'JVM使用率'
							} ]
						} ]
					};
					two_option = {
						tooltip : {
							formatter : "{a} <br/>{b} : {c}%"
						},
						series : [ {
							name : '监控指标',
							type : 'gauge',
							startAngle : 180,
							endAngle : 0,
							center : [ '50%', '90%' ], // 默认全局居中
							radius : 180,
							axisLine : { // 坐标轴线
								lineStyle : { // 属性lineStyle控制线条样式
									width : 140
								}
							},
							axisTick : { // 坐标轴小标记
								splitNumber : 10, // 每份split细分多少段
								length : 12, // 属性length控制线长
							},
							axisLabel : { // 坐标轴文本标签，详见axis.axisLabel

								textStyle : { // 其余属性默认使用全局文本样式，详见TEXTSTYLE
									color : '#fff',
									fontSize : 15,
									fontWeight : 'bolder'
								}
							},

							pointer : {
								width : 10,
								length : '80%',
								color : 'rgba(255, 255, 255, 0.8)'
							},
							title : {
								show : true,
								offsetCenter : [ 0, 15 ], // x, y，单位px
							/* textStyle: {       // 其余属性默认使用全局文本样式，详见TEXTSTYLE
							     color: '#fff',
							     fontSize: 25
							 }*/
							},
							detail : {
								show : true,
								backgroundColor : 'rgba(0,0,0,0)',
								borderWidth : 0,
								borderColor : '#ccc',
								offsetCenter : [ 0, -40 ], // x, y，单位px
								formatter : '{value}%',
								textStyle : { // 其余属性默认使用全局文本样式，详见TEXTSTYLE
									fontSize : 20
								}
							},
							data : [ {
								value : 50,
								name : 'cpu使用率'
							} ]
						} ]
					};

					gaugeChart1.setOption(one_option);
					gaugeChart2.setOption(two_option);
					one_option.series[0].data[0].name = '内存使用率';
					one_option.series[0].pointer.color = '#428bca'
					gaugeChart3.setOption(one_option);
					var ws = null;
					function connect(wsurl) {
						if ('WebSocket' in window) {
							ws = new WebSocket(wsurl);
						} else if ('MozWebSocket' in window) {
							ws = new MozWebSocket(wsurl);
						} else {
							alert('WebSocket is not supported by this browser.');
							return;
						}
						ws.onopen = function(event) {
						};
						ws.onmessage = function(event) {
							var axisData;
							axisData = (new Date()).toLocaleTimeString()
									.replace(/^\D*/, '');
							axisData = axisData
									.substr(axisData.indexOf(":") + 1);
							var jvm = [];
							var ram = [];
							var cpu = [];
							var jvmUsage = $.parseJSON(event.data).jvmUsage;
							var ramUsage = $.parseJSON(event.data).ramUsage;
							var cpuUsage = $.parseJSON(event.data).cpuUsage;
							$("#td_jvmUsage").html(jvmUsage);
							$("#td_serverUsage").html(ramUsage);
							$("#td_cpuUsage").html(cpuUsage);

							jvm.push(jvmUsage);
							ram.push(ramUsage);
							cpu.push(cpuUsage);
							// 动态数据接口 addData
							myChart.addData([ [ 0, // 系列索引
							jvm, // 新增数据
							false, // 新增数据是否从队列头部插入
							false, // 是否增加队列长度，false则自定删除原有数据，队头插入删队尾，队尾插入删队头
							], [ 1, // 系列索引
							ram, // 新增数据
							false, // 新增数据是否从队列头部插入
							false, // 是否增加队列长度，false则自定删除原有数据，队头插入删队尾，队尾插入删队头
							], [ 2, // 系列索引
							cpu, // 新增数据
							false, // 新增数据是否从队列头部插入
							false, // 是否增加队列长度，false则自定删除原有数据，队头插入删队尾，队尾插入删队头
							axisData // 坐标轴标签
							] ]);

							one_option.series[0].data[0].value = jvmUsage;
							one_option.series[0].data[0].name = 'JVM使用率';
							one_option.series[0].pointer.color = '#FF0000'
							gaugeChart1.setOption(one_option, true);

							two_option.series[0].data[0].value = cpuUsage;
							gaugeChart2.setOption(two_option, true);

							one_option.series[0].data[0].value = ramUsage;
							one_option.series[0].data[0].name = '内存使用率';
							one_option.series[0].pointer.color = '#428bca'
							gaugeChart3.setOption(one_option, true);
						};
						ws.onclose = function(event) {
						};
					}
					function initialize() {
						if (window.location.protocol == 'http:') {
							var wsuri = "${socPath}monitor";
							connect(wsuri);
						} else {
							connect("ws://" + request.getServerName() + ":"
									+ request.getServerPort() + path
									+ "/monitor");
						}
					}
					;
					initialize();
				});
	</script>
</body>