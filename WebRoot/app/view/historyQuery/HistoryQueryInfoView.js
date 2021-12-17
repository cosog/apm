Ext.define("AP.view.historyQuery.HistoryQueryInfoView", {
    extend: 'Ext.panel.Panel',
    alias: 'widget.historyMonitoringInfoView', // 定义别名
    layout: 'fit',
    border: false,
    initComponent: function () {
        var me = this;
        var PumpHistoryQueryInfoView = Ext.create('AP.view.historyQuery.PumpHistoryQueryInfoView');
        var PipelineHistoryQueryInfoView = Ext.create('AP.view.historyQuery.PipelineHistoryQueryInfoView');
        Ext.apply(me, {
        	items: [{
        		xtype: 'tabpanel',
        		id:"HistoryQueryTabPanel",
        		activeTab: 0,
        		border: false,
        		tabPosition: 'bottom',
        		items: [{
        				title: '桥梁',
        				id:'PumpHistoryQueryInfoPanel_Id',
        				items: [PumpHistoryQueryInfoView],
        				layout: "fit",
        				border: false
        			},{
        				title: '管设备',
        				id:'PipelineHistoryQueryInfoPanel_Id',
        				items: [PipelineHistoryQueryInfoView],
        				layout: "fit",
        				hidden: true,
        				border: false
        			}],
        			listeners: {
        				tabchange: function (tabPanel, newCard,oldCard, obj) {
        					Ext.getCmp("bottomTab_Id").setValue(newCard.id); 
        					if(newCard.id=="PumpHistoryQueryInfoPanel_Id"){
        						var statTabActiveId = Ext.getCmp("PumpHistoryQueryStatTabPanel").getActiveTab().id;
        						if(statTabActiveId=="PumpHistoryQueryStatGraphPanel_Id"){
        							loadAndInitHistoryQueryCommStatusStat(true);
        						}else if(newCard.id=="PumpHistoryQueryDeviceTypeStatGraphPanel_Id"){
        							loadAndInitHistoryQueryDeviceTypeStat(true);
        						}
//        						Ext.getCmp('HistoryQueryPumpDeviceListComb_Id').setValue('');
//        						Ext.getCmp('HistoryQueryPumpDeviceListComb_Id').setRawValue('');
        						var gridPanel = Ext.getCmp("PumpHistoryQueryDeviceListGridPanel_Id");
        						if (isNotVal(gridPanel)) {
        							gridPanel.getStore().load();
        						}else{
        							Ext.create('AP.store.historyQuery.PumpHistoryQueryWellListStore');
        						}
        					}else if(newCard.id=="PipelineHistoryQueryInfoPanel_Id"){
        						var statTabActiveId = Ext.getCmp("PipelineHistoryQueryStatTabPanel").getActiveTab().id;
        						if(statTabActiveId=="PipelineHistoryQueryStatGraphPanel_Id"){
        							loadAndInitHistoryQueryCommStatusStat(true);
        						}else if(newCard.id=="PipelineHistoryQueryDeviceTypeStatGraphPanel_Id"){
        							loadAndInitHistoryQueryDeviceTypeStat(true);
        						}
//        						Ext.getCmp('HistoryQueryPipelineDeviceListComb_Id').setValue('');
//        						Ext.getCmp('HistoryQueryPipelineDeviceListComb_Id').setRawValue('');
        						var gridPanel = Ext.getCmp("PipelineHistoryQueryDeviceListGridPanel_Id");
        						if (isNotVal(gridPanel)) {
        							gridPanel.getStore().load();
        						}else{
        							Ext.create('AP.store.historyQuery.PipelineHistoryQueryWellListStore');
        						}
        					}
        				}
        			}
            	}],
        		listeners: {
        			beforeclose: function ( panel, eOpts) {},
        			afterrender: function ( panel, eOpts) {}
        		}
        });
        me.callParent(arguments);
    }

});

function createHistoryQueryDeviceListColumn(columnInfo) {
    var myArr = columnInfo;

    var myColumns = "[";
    for (var i = 0; i < myArr.length; i++) {
        var attr = myArr[i];
        var width_ = "";
        var lock_ = "";
        var hidden_ = "";
        var flex_ = "";
        if (attr.hidden == true) {
            hidden_ = ",hidden:true";
        }
        if (isNotVal(attr.lock)) {
            //lock_ = ",locked:" + attr.lock;
        }
        if (isNotVal(attr.width)) {
            width_ = ",width:" + attr.width;
        }
        if (isNotVal(attr.flex)) {
        	flex_ = ",flex:" + attr.flex;
        }
        myColumns += "{text:'" + attr.header + "',lockable:true,align:'center' "+width_+flex_;
        if (attr.dataIndex.toUpperCase() == 'id'.toUpperCase()) {
            myColumns += ",xtype: 'rownumberer',sortable : false,locked:false";
        }
        else if (attr.dataIndex.toUpperCase()=='wellName'.toUpperCase()) {
            myColumns += ",sortable : false,locked:false,dataIndex:'" + attr.dataIndex + "',renderer:function(value){return \"<span data-qtip=\"+(value==undefined?\"\":value)+\">\"+(value==undefined?\"\":value)+\"</span>\";}";
        }
        else if (attr.dataIndex.toUpperCase()=='commStatusName'.toUpperCase()) {
            myColumns += ",sortable : false,dataIndex:'" + attr.dataIndex + "',renderer:function(value,o,p,e){return adviceCommStatusColor(value,o,p,e);}";
        }
        else if (attr.dataIndex.toUpperCase() == 'acqTime'.toUpperCase()) {
            myColumns += ",sortable : false,locked:false,dataIndex:'" + attr.dataIndex + "',renderer:function(value,o,p,e){return adviceTimeFormat(value,o,p,e);}";
        } 
        else {
            myColumns += hidden_ + lock_ + ",sortable : false,dataIndex:'" + attr.dataIndex + "',renderer:function(value){return \"<span data-qtip=\"+(value==undefined?\"\":value)+\">\"+(value==undefined?\"\":value)+\"</span>\";}";
            //        	myColumns += hidden_ + lock_ + width_ + ",sortable : false,dataIndex:'" + attr.dataIndex + "'";
        }
        myColumns += "}";
        if (i < myArr.length - 1) {
            myColumns += ",";
        }
    }
    myColumns += "]";
    return myColumns;
};

function createHistoryQueryColumn(columnInfo) {
    var myArr = columnInfo;

    var myColumns = "[";
    for (var i = 0; i < myArr.length; i++) {
        var attr = myArr[i];
        var width_ = "";
        var lock_ = "";
        var hidden_ = "";
        if (attr.hidden == true) {
            hidden_ = ",hidden:true";
        }
        if (isNotVal(attr.lock)) {
            //lock_ = ",locked:" + attr.lock;
        }
        if (isNotVal(attr.width)) {
            width_ = ",width:" + attr.width;
        }
        myColumns += "{text:'" + attr.header + "',lockable:true,align:'center' "+width_;
        if (attr.dataIndex.toUpperCase() == 'id'.toUpperCase()) {
            myColumns += ",xtype: 'rownumberer',sortable : false,locked:true";
        }
        else if (attr.dataIndex.toUpperCase()=='wellName'.toUpperCase()) {
            myColumns += ",sortable : false,locked:true,dataIndex:'" + attr.dataIndex + "',renderer:function(value){return \"<span data-qtip=\"+(value==undefined?\"\":value)+\">\"+(value==undefined?\"\":value)+\"</span>\";}";
        }
        else if (attr.dataIndex.toUpperCase()=='commStatusName'.toUpperCase()) {
            myColumns += ",sortable : false,dataIndex:'" + attr.dataIndex + "',renderer:function(value,o,p,e){return adviceCommStatusColor(value,o,p,e);}";
        }
        else if (attr.dataIndex.toUpperCase()=='runStatusName'.toUpperCase()) {
            myColumns += ",sortable : false,dataIndex:'" + attr.dataIndex + "',renderer:function(value,o,p,e){return adviceRunStatusColor(value,o,p,e);}";
        }
        else if (attr.dataIndex.toUpperCase() == 'acqTime'.toUpperCase()) {
            myColumns += ",sortable : false,locked:false,dataIndex:'" + attr.dataIndex + "',renderer:function(value,o,p,e){return adviceTimeFormat(value,o,p,e);}";
        } 
        else {
            myColumns += hidden_ + lock_ + ",sortable : false,dataIndex:'" + attr.dataIndex + "',renderer:function(value){return \"<span data-qtip=\"+(value==undefined?\"\":value)+\">\"+(value==undefined?\"\":value)+\"</span>\";}";
            //        	myColumns += hidden_ + lock_ + width_ + ",sortable : false,dataIndex:'" + attr.dataIndex + "'";
        }
        myColumns += "}";
        if (i < myArr.length - 1) {
            myColumns += ",";
        }
    }
    myColumns += "]";
    return myColumns;
};

function exportHistoryQueryDeviceListExcel(orgId,deviceType,deviceName,commStatusStatValue,deviceTypeStatValue,fileName,title,columnStr) {
    var url = context + '/historyQueryController/exportHistoryQueryDeviceListExcel';
    var fields = "";
    var heads = "";
    var lockedheads = "";
    var unlockedheads = "";
    var lockedfields = "";
    var unlockedfields = "";
    var columns_ = Ext.JSON.decode(columnStr);
    
    Ext.Array.each(columns_, function (name, index, countriesItSelf) {
        var column = columns_[index];
        if (index > 0 && !column.hidden) {
        	if(column.locked){
        		lockedfields += column.dataIndex + ",";
        		lockedheads += column.text + ",";
        	}else{
        		unlockedfields += column.dataIndex + ",";
        		unlockedheads += column.text + ",";
        	}
            
        }
    });
    if (isNotVal(lockedfields)) {
    	lockedfields = lockedfields.substring(0, lockedfields.length - 1);
    	lockedheads = lockedheads.substring(0, lockedheads.length - 1);
    }
    if (isNotVal(unlockedfields)) {
    	unlockedfields = unlockedfields.substring(0, unlockedfields.length - 1);
    	unlockedheads = unlockedheads.substring(0, unlockedheads.length - 1);
    }
    fields="id"+(isNotVal(lockedfields)?(","+lockedfields):"")+(isNotVal(unlockedfields)?(","+unlockedfields):"");
    heads="序号"+(isNotVal(lockedheads)?(","+lockedheads):"")+(isNotVal(unlockedheads)?(","+unlockedheads):"");
    
    var param = "&fields=" + fields + "&heads=" + URLencode(URLencode(heads)) 
    + "&orgId=" + orgId 
    + "&deviceType=" + deviceType 
    + "&deviceName=" + URLencode(URLencode(deviceName))
    + "&commStatusStatValue=" + URLencode(URLencode(commStatusStatValue))
    + "&deviceTypeStatValue=" + URLencode(URLencode(deviceTypeStatValue))
    + "&fileName=" + URLencode(URLencode(fileName)) 
    + "&title=" + URLencode(URLencode(title));
    openExcelWindow(url + '?flag=true' + param);
};

function exportHistoryQueryDataExcel(orgId,deviceType,deviceName,startDate,endDate,fileName,title,columnStr) {
    var url = context + '/historyQueryController/exportHistoryQueryDataExcel';
    var fields = "";
    var heads = "";
    var lockedheads = "";
    var unlockedheads = "";
    var lockedfields = "";
    var unlockedfields = "";
    var columns_ = Ext.JSON.decode(columnStr);
    
    Ext.Array.each(columns_, function (name, index, countriesItSelf) {
        var column = columns_[index];
        if (index > 0 && !column.hidden) {
        	if(column.locked){
        		lockedfields += column.dataIndex + ",";
        		lockedheads += column.text + ",";
        	}else{
        		unlockedfields += column.dataIndex + ",";
        		unlockedheads += column.text + ",";
        	}
            
        }
    });
    if (isNotVal(lockedfields)) {
    	lockedfields = lockedfields.substring(0, lockedfields.length - 1);
    	lockedheads = lockedheads.substring(0, lockedheads.length - 1);
    }
    if (isNotVal(unlockedfields)) {
    	unlockedfields = unlockedfields.substring(0, unlockedfields.length - 1);
    	unlockedheads = unlockedheads.substring(0, unlockedheads.length - 1);
    }
    fields="id"+(isNotVal(lockedfields)?(","+lockedfields):"")+(isNotVal(unlockedfields)?(","+unlockedfields):"");
    heads="序号"+(isNotVal(lockedheads)?(","+lockedheads):"")+(isNotVal(unlockedheads)?(","+unlockedheads):"");
    fields="";
    heads="";
    var param = "&fields=" + fields + "&heads=" + URLencode(URLencode(heads)) 
    + "&orgId=" + orgId 
    + "&deviceType=" + deviceType 
    + "&deviceName=" + URLencode(URLencode(deviceName))
    + "&startDate=" + startDate
    + "&endDate=" + endDate
    + "&fileName=" + URLencode(URLencode(fileName)) 
    + "&title=" + URLencode(URLencode(title));
    openExcelWindow(url + '?flag=true' + param);
};


function deviceHistoryQueryCurve(deviceType){
	var selectRowId="PumpHistoryQueryInfoDeviceListSelectRow_Id";
	var gridPanelId="PumpHistoryQueryDeviceListGridPanel_Id";
	var startDateId="PumpHistoryQueryStartDate_Id";
	var endDateId="PumpHistoryQueryEndDate_Id";
	var divId="pumpHistoryQueryCurveDiv_Id";
	
	if(deviceType==1){
		selectRowId="PipelineHistoryQueryInfoDeviceListSelectRow_Id";
		gridPanelId="PipelineHistoryQueryDeviceListGridPanel_Id";
		startDateId="PipelineHistoryQueryStartDate_Id";
		endDateId="PipelineHistoryQueryEndDate_Id";
		divId="pipelineHistoryQueryCurveDiv_Id";
	}
	
	
	var orgId = Ext.getCmp('leftOrg_Id').getValue();
	var deviceName='';
	var selectRow= Ext.getCmp(selectRowId).getValue();
	if(selectRow>=0){
		deviceName = Ext.getCmp(gridPanelId).getSelectionModel().getSelection()[0].data.wellName;
	}
	var startDate=Ext.getCmp(startDateId).rawValue;
    var endDate=Ext.getCmp(endDateId).rawValue;
	Ext.Ajax.request({
		method:'POST',
		url:context + '/historyQueryController/getHistoryQueryCurveData',
		success:function(response) {
			var result =  Ext.JSON.decode(response.responseText);
		    var data = result.list;
		    var defaultColors=["#7cb5ec", "#434348", "#90ed7d", "#f7a35c", "#8085e9", "#f15c80", "#e4d354", "#2b908f", "#f45b5b", "#91e8e1"];
		    var tickInterval = 1;
		    tickInterval = Math.floor(data.length / 10) + 1;
		    if(tickInterval<100){
		    	tickInterval=100;
		    }
		    var title = result.deviceName + "历史曲线";
		    var xTitle='采集时间';
		    var legendName =result.curveItems;
		    
		    var color=result.curveColors;
		    for(var i=0;i<color.length;i++){
		    	if(color[i]==''){
		    		color[i]=defaultColors[i%10];
		    	}else{
		    		color[i]='#'+color[i];
		    	}
		    }
		    
		    var yTitle=legendName[0];
		    
		    var series = "[";
		    var yAxis= [];
		    for (var i = 0; i < legendName.length; i++) {
		        series += "{\"name\":\"" + legendName[i] + "\"," 
		        +"\"yAxis\":"+i+",";
		        series += "\"data\":[";
		        for (var j = 0; j < data.length; j++) {
		        	series += "[" + Date.parse(data[j].acqTime.replace(/-/g, '/')) + "," + data[j].data[i] + "]";
		            if (j != data.length - 1) {
		                series += ",";
		            }
		        }
		        series += "]}";
		        if (i != legendName.length - 1) {
		            series += ",";
		        }
		        var opposite=false;
		        if(i>0){
		        	opposite=true;
		        }
		        var singleAxis={
		                lineWidth: 1,
		                title: {
		                    text: legendName[i],
		                    style: {
//		                        color: '#000000',
//		                        fontWeight: 'bold'
		                    }
		                },
		                labels: {
		                    formatter: function () {
		                        return Highcharts.numberFormat(this.value, 0);
		                    }
		                },
			            allowDecimals: false,    // 刻度值是否为小数
//			            minorTickInterval: '',   // 不显示次刻度线
		                opposite:opposite
		          };
		        yAxis.push(singleAxis);
		        
		    }
		    series += "]";
		    
		    var ser = Ext.JSON.decode(series);
		    var timeFormat='%m-%d';
//		    timeFormat='%H:%M';
		    initDeviceHistoryCurveChartFn(ser, tickInterval, divId, title, '', '', yAxis, color,true,timeFormat);
		},
		failure:function(){
			Ext.MessageBox.alert("错误","与后台联系的时候出了问题");
		},
		params: {
			deviceName:deviceName,
			startDate:startDate,
            endDate:endDate,
			deviceType:deviceType
        }
	});
};

function initDeviceHistoryCurveChartFn(series, tickInterval, divId, title, subtitle, xtitle, yAxis, color,legend,timeFormat) {
    Highcharts.setOptions({
        global: {
            useUTC: false
        }
    });

    var mychart = new Highcharts.Chart({
        chart: {
            renderTo: divId,
            type: 'spline',
            shadow: true,
            borderWidth: 0,
            zoomType: 'xy'
        },
        credits: {
            enabled: false
        },
        title: {
            text: title
        },
        subtitle: {
            text: subtitle
        },
        colors: color,
        xAxis: {
            type: 'datetime',
            title: {
                text: xtitle
            },
//            tickInterval: tickInterval,
            tickPixelInterval:tickInterval,
            labels: {
                formatter: function () {
                    return Highcharts.dateFormat(timeFormat, this.value);
                },
                autoRotation:true,//自动旋转
                rotation: -45 //倾斜度，防止数量过多显示不全  
//                step: 2
            }
        },
        yAxis: yAxis,
        tooltip: {
            crosshairs: true, //十字准线
            shared: true,
            style: {
                color: '#333333',
                fontSize: '12px',
                padding: '8px'
            },
            dateTimeLabelFormats: {
                millisecond: '%Y-%m-%d %H:%M:%S.%L',
                second: '%Y-%m-%d %H:%M:%S',
                minute: '%Y-%m-%d %H:%M',
                hour: '%Y-%m-%d %H',
                day: '%Y-%m-%d',
                week: '%m-%d',
                month: '%Y-%m',
                year: '%Y'
            }
        },
        exporting: {
            enabled: true,
            filename: 'class-booking-chart',
            url: context + '/exportHighcharsPicController/export'
        },
        plotOptions: {
            spline: {
                lineWidth: 1,
                fillOpacity: 0.3,
                marker: {
                    enabled: true,
                    radius: 3, //曲线点半径，默认是4
                    //                            symbol: 'triangle' ,//曲线点类型："circle", "square", "diamond", "triangle","triangle-down"，默认是"circle"
                    states: {
                        hover: {
                            enabled: true,
                            radius: 6
                        }
                    }
                },
                shadow: true
            }
        },
        legend: {
            layout: 'horizontal',//horizontal水平 vertical 垂直
            align: 'center',  //left，center 和 right
            verticalAlign: 'bottom',//top，middle 和 bottom
            enabled: legend,
            borderWidth: 0
        },
        series: series
    });
};

function loadAndInitHistoryQueryCommStatusStat(all){
	var orgId = Ext.getCmp('leftOrg_Id').getValue();
	var deviceType=0;
	var deviceTypeStatValue='';
	var activeId = Ext.getCmp("HistoryQueryTabPanel").getActiveTab().id;
	if(activeId=="PumpHistoryQueryInfoPanel_Id"){
		deviceType=0;
		if(all){
			Ext.getCmp("PumpHistoryQueryStatSelectCommStatus_Id").setValue('');
			Ext.getCmp("PumpHistoryQueryStatSelectDeviceType_Id").setValue('');
			deviceTypeStatValue='';
		}else{
			deviceTypeStatValue=Ext.getCmp("PumpHistoryQueryStatSelectDeviceType_Id").getValue();
		}
	}else if(activeId=="PipelineHistoryQueryInfoPanel_Id"){
		deviceType=1;
		if(all){
			Ext.getCmp("PipelineHistoryQueryStatSelectCommStatus_Id").setValue('');
			Ext.getCmp("PipelineHistoryQueryStatSelectDeviceType_Id").setValue('');
			deviceTypeStatValue='';
		}else{
			deviceTypeStatValue=Ext.getCmp("PipelineHistoryQueryStatSelectDeviceType_Id").getValue();
		}
	}
	
	Ext.Ajax.request({
		method:'POST',
		url:context + '/historyQueryController/getHistoryQueryCommStatusStatData',
		success:function(response) {
			var result =  Ext.JSON.decode(response.responseText);
			Ext.getCmp("AlarmShowStyle_Id").setValue(JSON.stringify(result.AlarmShowStyle));
			initHistoryQueryStatPieOrColChat(result);
		},
		failure:function(){
			Ext.MessageBox.alert("错误","与后台联系的时候出了问题");
		},
		params: {
			orgId:orgId,
			deviceType:deviceType,
			deviceTypeStatValue:deviceTypeStatValue
        }
	});
}

function initHistoryQueryStatPieOrColChat(get_rawData) {
	var divid="PumpHistoryQueryStatGraphPanelPieDiv_Id";
	var activeId = Ext.getCmp("HistoryQueryTabPanel").getActiveTab().id;
	if(activeId=="PumpHistoryQueryInfoPanel_Id"){
		divid="PumpHistoryQueryStatGraphPanelPieDiv_Id";
	}else if(activeId=="PipelineHistoryQueryInfoPanel_Id"){
		divid="PipelineHistoryQueryStatGraphPanelPieDiv_Id";
	}
	var title="通信状态";
	var datalist=get_rawData.totalRoot;
	
	var pieDataStr="[";
	for(var i=0;i<datalist.length;i++){
		if(datalist[i].itemCode!='all'){
			pieDataStr+="['"+datalist[i].item+"',"+datalist[i].count+"],";
		}
	}
	
	if(stringEndWith(pieDataStr,",")){
		pieDataStr = pieDataStr.substring(0, pieDataStr.length - 1);
	}
	pieDataStr+="]";
	var pieData = Ext.JSON.decode(pieDataStr);
	
	var alarmShowStyle=Ext.JSON.decode(Ext.getCmp("AlarmShowStyle_Id").getValue());
	var colors=[];
	colors.push('#'+alarmShowStyle.Statistics.Normal.BackgroundColor);
	colors.push('#'+alarmShowStyle.Statistics.FirstLevel.BackgroundColor);
	
	ShowHistoryQueryStatPieOrColChat(title,divid, "设备数占", pieData,colors);
};

function ShowHistoryQueryStatPieOrColChat(title,divid, name, data,colors) {
	Highcharts.chart(divid, {
		chart : {
			plotBackgroundColor : null,
			plotBorderWidth : null,
			plotShadow : false
		},
		credits : {
			enabled : false
		},
		title : {
			text : title
		},
		colors : colors,
		tooltip : {
			pointFormat : '设备数: <b>{point.y}</b> 占: <b>{point.percentage:.1f}%</b>'
		},
		legend : {
			align : 'center',
			verticalAlign : 'bottom',
			layout : 'horizontal' //vertical 竖直 horizontal-水平
		},
		plotOptions : {
			pie : {
				allowPointSelect : true,
				cursor : 'pointer',
				dataLabels : {
					enabled : true,
					color : '#000000',
					connectorColor : '#000000',
					format : '<b>{point.name}</b>: {point.y}台'
				},
				events: {
					click: function(e) {
						var statSelectCommStatusId="PumpHistoryQueryStatSelectCommStatus_Id";
						var deviceListComb_Id="HistoryQueryPumpDeviceListComb_Id";
						var gridPanel_Id="PumpHistoryQueryDeviceListGridPanel_Id";
						var store="AP.store.realTimeMonitoring.PumpHistoryQueryWellListStore";
						var activeId = Ext.getCmp("HistoryQueryTabPanel").getActiveTab().id;
						if(activeId=="PumpHistoryQueryInfoPanel_Id"){
							statSelectCommStatusId="PumpHistoryQueryStatSelectCommStatus_Id";
							deviceListComb_Id="HistoryQueryPumpDeviceListComb_Id";
							gridPanel_Id="PumpHistoryQueryDeviceListGridPanel_Id";
							store="AP.store.historyQuery.PumpHistoryQueryWellListStore";
						}else if(activeId=="PipelineHistoryQueryInfoPanel_Id"){
							statSelectCommStatusId="PipelineHistoryQueryStatSelectCommStatus_Id";
							deviceListComb_Id="HistoryQueryPipelineDeviceListComb_Id";
							gridPanel_Id="PipelineHistoryQueryDeviceListGridPanel_Id";
							store="AP.store.historyQuery.PipelineHistoryQueryWellListStore";
						}
						
						if(!e.point.selected){//如果没被选中,则本次是选中
							Ext.getCmp(statSelectCommStatusId).setValue(e.point.name);
						}else{//取消选中
							Ext.getCmp(statSelectCommStatusId).setValue('');
						}
						
						Ext.getCmp(deviceListComb_Id).setValue('');
						Ext.getCmp(deviceListComb_Id).setRawValue('');
						var gridPanel = Ext.getCmp(gridPanel_Id);
						if (isNotVal(gridPanel)) {
							gridPanel.getSelectionModel().deselectAll(true);
							gridPanel.getStore().load();
						}else{
							Ext.create(store);
						}
					}
				},
				showInLegend : true
			}
		},
		exporting:{ 
            enabled:true,    
            filename:'class-booking-chart',    
            url:context + '/exportHighcharsPicController/export'
		},
		series : [{
					type : 'pie',
					name : name,
					data : data
				}]
		});
};

function loadAndInitHistoryQueryDeviceTypeStat(all){
	var orgId = Ext.getCmp('leftOrg_Id').getValue();
	var deviceType=0;
	var commStatusStatValue='';
	var activeId = Ext.getCmp("HistoryQueryTabPanel").getActiveTab().id;
	if(activeId=="PumpHistoryQueryInfoPanel_Id"){
		deviceType=0;
		if(all){
			Ext.getCmp("PumpHistoryQueryStatSelectCommStatus_Id").setValue('');
			Ext.getCmp("PumpHistoryQueryStatSelectDeviceType_Id").setValue('');
			commStatusStatValue='';
		}else{
			commStatusStatValue=Ext.getCmp("PumpHistoryQueryStatSelectCommStatus_Id").getValue();
		}
		
	}else if(activeId=="PipelineHistoryQueryInfoPanel_Id"){
		deviceType=1;
		if(all){
			Ext.getCmp("PipelineHistoryQueryStatSelectCommStatus_Id").setValue('');
			Ext.getCmp("PipelineHistoryQueryStatSelectDeviceType_Id").setValue('');
			commStatusStatValue='';
		}else{
			commStatusStatValue=Ext.getCmp("PipelineHistoryQueryStatSelectCommStatus_Id").getValue();
		}
	}
	Ext.Ajax.request({
		method:'POST',
		url:context + '/historyQueryController/getHistoryQueryDeviceTypeStatData',
		success:function(response) {
			var result =  Ext.JSON.decode(response.responseText);
			Ext.getCmp("AlarmShowStyle_Id").setValue(JSON.stringify(result.AlarmShowStyle));
			initHistoryQueryDeviceTypeStatPieOrColChat(result);
		},
		failure:function(){
			Ext.MessageBox.alert("错误","与后台联系的时候出了问题");
		},
		params: {
			orgId:orgId,
			deviceType:deviceType,
			commStatusStatValue:commStatusStatValue
        }
	});
};

function initHistoryQueryDeviceTypeStatPieOrColChat(get_rawData) {
	var divid="PumpHistoryQueryDeviceTypeStatPieDiv_Id";
	var activeId = Ext.getCmp("HistoryQueryTabPanel").getActiveTab().id;
	if(activeId=="PumpHistoryQueryInfoPanel_Id"){
		divid="PumpHistoryQueryDeviceTypeStatPieDiv_Id";
	}else if(activeId=="PipelineHistoryQueryInfoPanel_Id"){
		divid="PipelineHistoryQueryDeviceTypeStatPieDiv_Id";
	}
	var title="设备类型";
	var datalist=get_rawData.totalRoot;
	
	var pieDataStr="[";
	for(var i=0;i<datalist.length;i++){
		pieDataStr+="['"+datalist[i].item+"',"+datalist[i].count+"],";
	}
	
	if(stringEndWith(pieDataStr,",")){
		pieDataStr = pieDataStr.substring(0, pieDataStr.length - 1);
	}
	pieDataStr+="]";
	var pieData = Ext.JSON.decode(pieDataStr);
	var colors=["#7cb5ec", "#434348", "#90ed7d", "#f7a35c", "#8085e9", "#f15c80", "#e4d354", "#2b908f", "#f45b5b", "#91e8e1"];
	ShowHistoryQueryDeviceTypeStatPieChat(title,divid, "设备数占", pieData,colors);
};

function ShowHistoryQueryDeviceTypeStatPieChat(title,divid, name, data,colors) {
	Highcharts.chart(divid, {
		chart : {
			plotBackgroundColor : null,
			plotBorderWidth : null,
			plotShadow : false
		},
		credits : {
			enabled : false
		},
		title : {
			text : title
		},
		colors : colors,
		tooltip : {
			pointFormat : '设备数: <b>{point.y}</b> 占: <b>{point.percentage:.1f}%</b>'
		},
		legend : {
			align : 'center',
			verticalAlign : 'bottom',
			layout : 'horizontal' //vertical 竖直 horizontal-水平
		},
		plotOptions : {
			pie : {
				allowPointSelect : true,
				cursor : 'pointer',
				dataLabels : {
					enabled : true,
					color : '#000000',
					connectorColor : '#000000',
					format : '<b>{point.name}</b>: {point.y}台'
				},
				events: {
					click: function(e) {
						var statSelectDeviceType_Id="PumpHistoryQueryStatSelectDeviceType_Id";
						var deviceListComb_Id="HistoryQueryPumpDeviceListComb_Id";
						var gridPanel_Id="PumpHistoryQueryDeviceListGridPanel_Id";
						var store="AP.store.historyQuery.PumpHistoryQueryWellListStore";
						var activeId = Ext.getCmp("HistoryQueryTabPanel").getActiveTab().id;
						if(activeId=="PumpHistoryQueryInfoPanel_Id"){
							statSelectDeviceType_Id="PumpHistoryQueryStatSelectDeviceType_Id";
							deviceListComb_Id="HistoryQueryPumpDeviceListComb_Id";
							gridPanel_Id="PumpHistoryQueryDeviceListGridPanel_Id";
							store="AP.store.historyQuery.PumpHistoryQueryWellListStore";
						}else if(activeId=="PipelineHistoryQueryInfoPanel_Id"){
							statSelectDeviceType_Id="PipelineHistoryQueryStatSelectDeviceType_Id";
							deviceListComb_Id="HistoryQueryPipelineDeviceListComb_Id";
							gridPanel_Id="PipelineHistoryQueryDeviceListGridPanel_Id";
							store="AP.store.historyQuery.PipelineHistoryQueryWellListStore";
						}
						
						if(!e.point.selected){//如果没被选中,则本次是选中
							Ext.getCmp(statSelectDeviceType_Id).setValue(e.point.name);
						}else{//取消选中
							Ext.getCmp(statSelectDeviceType_Id).setValue('');
						}
						
						Ext.getCmp(deviceListComb_Id).setValue('');
						Ext.getCmp(deviceListComb_Id).setRawValue('');
						var gridPanel = Ext.getCmp(gridPanel_Id);
						if (isNotVal(gridPanel)) {
							gridPanel.getSelectionModel().deselectAll(true);
							gridPanel.getStore().load();
						}else{
							Ext.create(store);
						}
					}
				},
				showInLegend : true
			}
		},
		exporting:{ 
            enabled:true,    
            filename:'class-booking-chart',    
            url:context + '/exportHighcharsPicController/export'
		},
		series : [{
					type : 'pie',
					name : name,
					data : data
				}]
		});
};