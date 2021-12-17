Ext.define("AP.view.realTimeMonitoring.RealTimeMonitoringInfoView", {
    extend: 'Ext.panel.Panel',
    alias: 'widget.realTimeMonitoringInfoView', // 定义别名
    layout: 'fit',
    border: false,
    initComponent: function () {
        var me = this;
        var PumpRealTimeMonitoringInfoView = Ext.create('AP.view.realTimeMonitoring.PumpRealTimeMonitoringInfoView');
        var PipelineRealTimeMonitoringInfoView = Ext.create('AP.view.realTimeMonitoring.PipelineRealTimeMonitoringInfoView');
        Ext.apply(me, {
        	items: [{
        		xtype: 'tabpanel',
        		id:"RealTimeMonitoringTabPanel",
        		activeTab: 0,
        		border: false,
        		tabPosition: 'bottom',
        		tabBar:{
                	items: [{
                        xtype: 'tbfill'
                    },{
                		xtype: 'button',
                        id:"CPUUsedPercentLabel_id",
//                        width: 180,
                        text: 'CPU:',
                        handler: function (v, o) {
                        	Ext.getCmp('ResourceMonitoringCurveItem_Id').setValue("CPU使用率(%)");
                            Ext.getCmp('ResourceMonitoringCurveItemCode_Id').setValue("cpuUsedPercent");
                            var itemCode= Ext.getCmp('ResourceMonitoringCurveItemCode_Id').getValue();
                        	var ResourceProbeHistoryCurveWindow=Ext.create("AP.view.realTimeMonitoring.ResourceProbeHistoryCurveWindow", {
            				    html:'<div id="ResourceProbeHistoryCurve_'+itemCode+'_DivId" style="width:100%;height:100%;"></div>'
                        	});
                        	ResourceProbeHistoryCurveWindow.show();
                        }
                	},{
                		xtype: 'button',
                        id:"memUsedPercentLabel_id",
                        text: '内存:',
//                        width: 130,
                        handler: function (v, o) {
                        	Ext.getCmp('ResourceMonitoringCurveItem_Id').setValue("内存使用率(%)");
                            Ext.getCmp('ResourceMonitoringCurveItemCode_Id').setValue("memUsedPercent");
                            var itemCode= Ext.getCmp('ResourceMonitoringCurveItemCode_Id').getValue();
                        	var ResourceProbeHistoryCurveWindow=Ext.create("AP.view.realTimeMonitoring.ResourceProbeHistoryCurveWindow", {
            				    html:'<div id="ResourceProbeHistoryCurve_'+itemCode+'_DivId" style="width:100%;height:100%;"></div>'
                        	});
                        	ResourceProbeHistoryCurveWindow.show();
                        }
                	},{
                		xtype: 'button',
                        id:"tableSpaceSizeProbeLabel_id",
                        text: '表空间:',
//                        width: 130,
                        handler: function (v, o) {
                        	Ext.getCmp('ResourceMonitoringCurveItem_Id').setValue("表空间使用率(%)");
                            Ext.getCmp('ResourceMonitoringCurveItemCode_Id').setValue("tableSpaceSize");
                            var itemCode= Ext.getCmp('ResourceMonitoringCurveItemCode_Id').getValue();
                        	var ResourceProbeHistoryCurveWindow=Ext.create("AP.view.realTimeMonitoring.ResourceProbeHistoryCurveWindow", {
            				    html:'<div id="ResourceProbeHistoryCurve_'+itemCode+'_DivId" style="width:100%;height:100%;"></div>'
                        	});
                        	ResourceProbeHistoryCurveWindow.show();
                        }
                	},{
                		xtype: 'button',
                        id:"adRunStatusProbeLabel_id",
                        text: '驱动:',
//                        width: 100,
                        handler: function (v, o) {
                        	Ext.getCmp('ResourceMonitoringCurveItem_Id').setValue("驱动运行状态");
                            Ext.getCmp('ResourceMonitoringCurveItemCode_Id').setValue("adRunStatus");
                            var itemCode= Ext.getCmp('ResourceMonitoringCurveItemCode_Id').getValue();
                        	var ResourceProbeHistoryCurveWindow=Ext.create("AP.view.realTimeMonitoring.ResourceProbeHistoryCurveWindow", {
            				    html:'<div id="ResourceProbeHistoryCurve_'+itemCode+'_DivId" style="width:100%;height:100%;"></div>'
                        	});
                        	ResourceProbeHistoryCurveWindow.show();
                        }
                	},{
                        id: 'ResourceMonitoringCurveItem_Id',
                        xtype: 'textfield',
                        value: '',
                        hidden: true
                    },{
                        id: 'ResourceMonitoringCurveItemCode_Id',
                        xtype: 'textfield',
                        value: '',
                        hidden: true
                    }]
                },
        		items: [{
        				title: '桥梁',
        				id:'PumpRealTimeMonitoringInfoPanel_Id',
        				items: [PumpRealTimeMonitoringInfoView],
        				layout: "fit",
        				border: false
        			},{
        				title: '管设备',
        				id:'PipelineRealTimeMonitoringInfoPanel_Id',
        				items: [PipelineRealTimeMonitoringInfoView],
        				layout: "fit",
        				hidden: true,
        				border: false
        			}],
        			listeners: {
        				tabchange: function (tabPanel, newCard,oldCard, obj) {
        					Ext.getCmp("bottomTab_Id").setValue(newCard.id); 
        					if(newCard.id=="PumpRealTimeMonitoringInfoPanel_Id"){
        						var statTabActiveId = Ext.getCmp("PumpRealTimeMonitoringStatTabPanel").getActiveTab().id;
        						if(statTabActiveId=="PumpRealTimeMonitoringStatGraphPanel_Id"){
        							loadAndInitCommStatusStat(true);
        						}else if(newCard.id=="PumpRealTimeMonitoringDeviceTypeStatGraphPanel_Id"){
        							loadAndInitDeviceTypeStat(true);
        						}
        						Ext.getCmp('RealTimeMonitoringPumpDeviceListComb_Id').setValue('');
        						Ext.getCmp('RealTimeMonitoringPumpDeviceListComb_Id').setRawValue('');
        						var gridPanel = Ext.getCmp("PumpRealTimeMonitoringListGridPanel_Id");
        						if (isNotVal(gridPanel)) {
        							gridPanel.getSelectionModel().deselectAll(true);
        							gridPanel.getStore().load();
        						}else{
        							Ext.create('AP.store.realTimeMonitoring.PumpRealTimeMonitoringWellListStore');
        						}
        					}else if(newCard.id=="PipelineRealTimeMonitoringInfoPanel_Id"){
        						var statTabActiveId = Ext.getCmp("PipelineRealTimeMonitoringStatTabPanel").getActiveTab().id;
        						if(statTabActiveId=="PipelineRealTimeMonitoringStatGraphPanel_Id"){
        							loadAndInitCommStatusStat(true);
        						}else if(newCard.id=="PipelineRealTimeMonitoringDeviceTypeStatGraphPanel_Id"){
        							loadAndInitDeviceTypeStat(true);
        						}
        						Ext.getCmp('RealTimeMonitoringPipelineDeviceListComb_Id').setValue('');
        						Ext.getCmp('RealTimeMonitoringPipelineDeviceListComb_Id').setRawValue('');
        						var gridPanel = Ext.getCmp("PipelineRealTimeMonitoringListGridPanel_Id");
        						if (isNotVal(gridPanel)) {
        							gridPanel.getSelectionModel().deselectAll(true);
        							gridPanel.getStore().load();
        						}else{
        							Ext.create('AP.store.realTimeMonitoring.PipelineRealTimeMonitoringWellListStore');
        						}
        					}
        				}
        			}
            	}]
        });
        me.callParent(arguments);
    }

});

function createRealTimeMonitoringStatColumn(columnInfo) {
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
            myColumns += ",xtype: 'rownumberer',sortable : false,locked:false";
        }
        else if (attr.dataIndex.toUpperCase()=='count'.toUpperCase()) {
            myColumns += ",sortable : false,dataIndex:'" + attr.dataIndex + "',renderer:function(value,o,p,e){return adviceStatTableCommStatusColor(value,o,p,e);}";
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

function createRealTimeMonitoringColumn(columnInfo) {
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

ResourceProbeHistoryCurveChartFn = function (get_rawData, itemName, itemCode, divId) {
    var tickInterval = 1;
    var data = get_rawData.totalRoot;
    tickInterval = Math.floor(data.length / 10) + 1;
    var title = itemName.split("(")[0] + "曲线";
    var legend=false;
    var legendName = [itemName];
    if(itemCode.toUpperCase()=='cpuUsedPercent'.toUpperCase()){
    	legendName = [];
    	for (var i = 0; i < data.length; i++) {
    		if(isNotVal(data[i].value)){
    			var cpus=data[i].value.split(";");
        		if(cpus.length>legendName.length){
        			legendName = [];
        			for(var j = 0; j < cpus.length; j++){
        				legendName.push("CPU"+(j+1));
        			}
        		}
    		}
    		
    	}
    	legend=true;
    	ytitle='CPU使用率(%)';
    }
    var series = "[";
    for (var i = 0; i < legendName.length; i++) {
        series += "{\"name\":\"" + legendName[i] + "\",";
        series += "\"data\":[";
        for (var j = 0; j < data.length; j++) {
        	
    		var year = parseInt(data[j].acqTime.split(" ")[0].split("-")[0]);
            var month = parseInt(data[j].acqTime.split(" ")[0].split("-")[1]);
            var day = parseInt(data[j].acqTime.split(" ")[0].split("-")[2]);
            var hour = parseInt(data[j].acqTime.split(" ")[1].split(":")[0]);
            var minute = parseInt(data[j].acqTime.split(" ")[1].split(":")[1]);
            var second = parseInt(data[j].acqTime.split(" ")[1].split(":")[2]);
            if(itemCode.toUpperCase()=='cpuUsedPercent'.toUpperCase()){
            	if(isNotVal(data[j].value)){
            		var values=data[j].value.split(";");
            		if(values.length>i){
            			series += "[" + Date.parse(data[j].acqTime.replace(/-/g, '/')) + "," + values[i] + "]";
            		}else{
            			series += "[" + Date.parse(data[j].acqTime.replace(/-/g, '/')) + "," + null + "]";
            		}
            	}else{
            		series += "[" + Date.parse(data[j].acqTime.replace(/-/g, '/')) + "," + null + "]";
            	}
            }else{
            	if(isNotVal(data[j].value)){
            		series += "[" + Date.parse(data[j].acqTime.replace(/-/g, '/')) + "," + data[j].value + "]";
        		}else{
        			series += "[" + Date.parse(data[j].acqTime.replace(/-/g, '/')) + "," + null + "]";
        		}
            }
            if (j != data.length - 1) {
                series += ",";
            }
        }
        series += "]}";
        if (i != legendName.length - 1) {
            series += ",";
        }
    }
    series += "]";
    var ser = Ext.JSON.decode(series);
    var color = ['#800000', // 红
       '#008C00', // 绿
       '#000000', // 黑
       '#0000FF', // 蓝
       '#F4BD82', // 黄
       '#FF00FF' // 紫
     ];

    initResourceProbeHistoryCurveChartFn(ser, tickInterval, divId, title, "[" + get_rawData.startDate + "~" + get_rawData.endDate + "]", "时间", itemName, color,legend);

    return false;
};

function initResourceProbeHistoryCurveChartFn(series, tickInterval, divId, title, subtitle, xtitle, ytitle, color,legend) {
    
    Highcharts.setOptions({
        global: {
            useUTC: false
        }
    });

    mychart = new Highcharts.Chart({
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
            labels: {
                formatter: function () {
                    return Highcharts.dateFormat("%Y-%m-%d", this.value);
                },
                rotation: 0, //倾斜度，防止数量过多显示不全  
                step: 2
            }
        },
        yAxis: [{
            lineWidth: 1,
            title: {
                text: ytitle,
                style: {
                    color: '#000000',
                    fontWeight: 'bold'
                }
            },
            labels: {
                formatter: function () {
                    return Highcharts.numberFormat(this.value, 2);
                }
            }
      }],
        tooltip: {
            crosshairs: true, //十字准线
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
            layout: 'vertical',
            align: 'right',
            verticalAlign: 'middle',
            enabled: legend,
            borderWidth: 0
        },
        series: series
    });
};


function exportRealTimeMonitoringDataExcel(orgId,deviceType,deviceName,commStatusStatValue,deviceTypeStatValue,fileName,title,columnStr) {
    var url = context + '/realTimeMonitoringController/exportDeviceRealTimeOverviewDataExcel';
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
    fields = "id," + lockedfields+","+unlockedfields;
    heads = "序号," + lockedheads+","+unlockedheads;
    fields="";
    heads="";
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



function gotoDeviceHistory(deviceName,deviceType){
	Ext.getCmp("realtimeTurnToHisyorySign_Id").setValue('true');//跳转标志
	var tabPanel = Ext.getCmp("frame_center_ids");
	var moduleId="DeviceHistoryQuery";
	var getTabId = tabPanel.getComponent(moduleId);
	var haveModule=false;
	if(!getTabId){
		var moduleStore=Ext.getCmp("MainIframeView_Id").getStore();
		var moduleCount=moduleStore.getCount();
		for(var i=0;i<moduleCount;i++){
			var rec=moduleStore.getAt(i);
			if(rec.isLeaf()&&rec.id=="DeviceHistoryQuery"){
				tabPanel.add(Ext.create(rec.data.viewsrc, {
                    id: rec.data.id,
                    closable: true,
                    iconCls: rec.data.md_icon,
                    closeAction: 'destroy',
                    title: rec.data.text,
                    listeners: {
                        afterrender: function () {
                        },
                        delay: 150
                    }
                })).show();
                Ext.getCmp("topModule_Id").setValue(rec.data.mdCode);
				haveModule=true;
				break;
			}
		}
	}else{
		haveModule=true;
	}
	if(haveModule){
		if(deviceType===0){
			Ext.getCmp('HistoryQueryPumpDeviceListComb_Id').setValue(deviceName);
			var historyGridPanel=Ext.getCmp("PumpHistoryQueryListGridPanel_Id");
			tabPanel.setActiveTab(moduleId);
			var historyQueryTabPanel = Ext.getCmp("HistoryQueryTabPanel");
			if(historyQueryTabPanel.getActiveTab().id=="PipelineHistoryQueryInfoPanel_Id"){
				historyQueryTabPanel.setActiveTab("PumpHistoryQueryInfoPanel_Id");
			}
		}if(deviceType===1){
			Ext.getCmp('HistoryQueryPipelineDeviceListComb_Id').setValue(deviceName);
			var historyGridPanel=Ext.getCmp("PipelineHistoryQueryListGridPanel_Id");
			tabPanel.setActiveTab(moduleId);
			var historyQueryTabPanel = Ext.getCmp("HistoryQueryTabPanel");
			if(historyQueryTabPanel.getActiveTab().id=="PumpHistoryQueryInfoPanel_Id"){
				historyQueryTabPanel.setActiveTab("PipelineHistoryQueryInfoPanel_Id");
			}
		}
	}
}


function loadAndInitCommStatusStat(all){
	var orgId = Ext.getCmp('leftOrg_Id').getValue();
	var deviceType=0;
	var deviceTypeStatValue='';
	var activeId = Ext.getCmp("RealTimeMonitoringTabPanel").getActiveTab().id;
	if(activeId=="PumpRealTimeMonitoringInfoPanel_Id"){
		deviceType=0;
		if(all){
			Ext.getCmp("PumpRealTimeMonitoringStatSelectCommStatus_Id").setValue('');
			Ext.getCmp("PumpRealTimeMonitoringStatSelectDeviceType_Id").setValue('');
			deviceTypeStatValue='';
		}else{
			deviceTypeStatValue=Ext.getCmp("PumpRealTimeMonitoringStatSelectDeviceType_Id").getValue();
		}
	}else if(activeId=="PipelineRealTimeMonitoringInfoPanel_Id"){
		deviceType=1;
		if(all){
			Ext.getCmp("PipelineRealTimeMonitoringStatSelectCommStatus_Id").setValue('');
			Ext.getCmp("PipelineRealTimeMonitoringStatSelectDeviceType_Id").setValue('');
			deviceTypeStatValue='';
		}else{
			deviceTypeStatValue=Ext.getCmp("PipelineRealTimeMonitoringStatSelectDeviceType_Id").getValue();
		}
	}
	
	Ext.Ajax.request({
		method:'POST',
		url:context + '/realTimeMonitoringController/getRealTimeMonitoringCommStatusStatData',
		success:function(response) {
			var result =  Ext.JSON.decode(response.responseText);
			Ext.getCmp("AlarmShowStyle_Id").setValue(JSON.stringify(result.AlarmShowStyle));
			initRealTimeMonitoringStatPieOrColChat(result);
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


function initRealTimeMonitoringStatPieOrColChat(get_rawData) {
	var divid="PumpRealTimeMonitoringStatGraphPanelPieDiv_Id";
	var tabPanel = Ext.getCmp("RealTimeMonitoringTabPanel");
	var activeId = tabPanel.getActiveTab().id;
	if(activeId=="PumpRealTimeMonitoringInfoPanel_Id"){
		divid="PumpRealTimeMonitoringStatGraphPanelPieDiv_Id";
	}else if(activeId=="PipelineRealTimeMonitoringInfoPanel_Id"){
		divid="PipelineRealTimeMonitoringStatGraphPanelPieDiv_Id";
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
	
	ShowRealTimeMonitoringStatPieOrColChat(title,divid, "设备数占", pieData,colors);
	
};

function ShowRealTimeMonitoringStatPieOrColChat(title,divid, name, data,colors) {
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
						var statSelectCommStatusId="PumpRealTimeMonitoringStatSelectCommStatus_Id";
						var deviceListComb_Id="RealTimeMonitoringPumpDeviceListComb_Id";
						var gridPanel_Id="PumpRealTimeMonitoringListGridPanel_Id";
						var store="AP.store.realTimeMonitoring.PumpRealTimeMonitoringWellListStore";
						var activeId = Ext.getCmp("RealTimeMonitoringTabPanel").getActiveTab().id;
						if(activeId=="PumpRealTimeMonitoringInfoPanel_Id"){
							statSelectCommStatusId="PumpRealTimeMonitoringStatSelectCommStatus_Id";
							deviceListComb_Id="RealTimeMonitoringPumpDeviceListComb_Id";
							gridPanel_Id="PumpRealTimeMonitoringListGridPanel_Id";
							store="AP.store.realTimeMonitoring.PumpRealTimeMonitoringWellListStore";
						}else if(activeId=="PipelineRealTimeMonitoringInfoPanel_Id"){
							statSelectCommStatusId="PipelineRealTimeMonitoringStatSelectCommStatus_Id";
							deviceListComb_Id="RealTimeMonitoringPipelineDeviceListComb_Id";
							gridPanel_Id="PipelineRealTimeMonitoringListGridPanel_Id";
							store="AP.store.realTimeMonitoring.PipelineRealTimeMonitoringWellListStore";
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

function loadAndInitDeviceTypeStat(all){
	var orgId = Ext.getCmp('leftOrg_Id').getValue();
	var deviceType=0;
	var commStatusStatValue='';
	
	var activeId = Ext.getCmp("RealTimeMonitoringTabPanel").getActiveTab().id;
	if(activeId=="PumpRealTimeMonitoringInfoPanel_Id"){
		deviceType=0;
		if(all){
			Ext.getCmp("PumpRealTimeMonitoringStatSelectCommStatus_Id").setValue('');
			Ext.getCmp("PumpRealTimeMonitoringStatSelectDeviceType_Id").setValue('');
			commStatusStatValue='';
		}else{
			commStatusStatValue=Ext.getCmp("PumpRealTimeMonitoringStatSelectCommStatus_Id").getValue();
		}
		
	}else if(activeId=="PipelineRealTimeMonitoringInfoPanel_Id"){
		deviceType=1;
		if(all){
			Ext.getCmp("PipelineRealTimeMonitoringStatSelectCommStatus_Id").setValue('');
			Ext.getCmp("PipelineRealTimeMonitoringStatSelectCommStatus_Id").setValue('');
			commStatusStatValue='';
		}else{
			commStatusStatValue=Ext.getCmp("PipelineRealTimeMonitoringStatSelectCommStatus_Id").getValue();
		}
	}
	Ext.Ajax.request({
		method:'POST',
		url:context + '/realTimeMonitoringController/getRealTimeMonitoringDeviceTypeStatData',
		success:function(response) {
			var result =  Ext.JSON.decode(response.responseText);
			Ext.getCmp("AlarmShowStyle_Id").setValue(JSON.stringify(result.AlarmShowStyle));
			initRealTimeMonitoringDeviceTypeStatPieOrColChat(result);
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

function initRealTimeMonitoringDeviceTypeStatPieOrColChat(get_rawData) {
	var divid="PumpRealTimeMonitoringDeviceTypeStatPieDiv_Id";
	var activeId = Ext.getCmp("RealTimeMonitoringTabPanel").getActiveTab().id;
	if(activeId=="PumpRealTimeMonitoringInfoPanel_Id"){
		divid="PumpRealTimeMonitoringDeviceTypeStatPieDiv_Id";
	}else if(activeId=="PipelineRealTimeMonitoringInfoPanel_Id"){
		divid="PipelineRealTimeMonitoringDeviceTypeStatPieDiv_Id";
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
	ShowRealTimeMonitoringDeviceTypeStatPieChat(title,divid, "设备数占", pieData,colors);
};

function ShowRealTimeMonitoringDeviceTypeStatPieChat(title,divid, name, data,colors) {
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
						var statSelectDeviceType_Id="PumpRealTimeMonitoringStatSelectDeviceType_Id";
						var deviceListComb_Id="RealTimeMonitoringPumpDeviceListComb_Id";
						var gridPanel_Id="PumpRealTimeMonitoringListGridPanel_Id";
						var store="AP.store.realTimeMonitoring.PumpRealTimeMonitoringWellListStore";
						var activeId = Ext.getCmp("RealTimeMonitoringTabPanel").getActiveTab().id;
						if(activeId=="PumpRealTimeMonitoringInfoPanel_Id"){
							statSelectDeviceType_Id="PumpRealTimeMonitoringStatSelectDeviceType_Id";
							deviceListComb_Id="RealTimeMonitoringPumpDeviceListComb_Id";
							gridPanel_Id="PumpRealTimeMonitoringListGridPanel_Id";
							store="AP.store.realTimeMonitoring.PumpRealTimeMonitoringWellListStore";
						}else if(activeId=="PipelineRealTimeMonitoringInfoPanel_Id"){
							statSelectDeviceType_Id="PipelineRealTimeMonitoringStatSelectDeviceType_Id";
							deviceListComb_Id="RealTimeMonitoringPipelineDeviceListComb_Id";
							gridPanel_Id="PipelineRealTimeMonitoringListGridPanel_Id";
							store="AP.store.realTimeMonitoring.PipelineRealTimeMonitoringWellListStore";
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

function deviceRealtimeMonitoringCurve(deviceType){
	var selectRowId="PumpRealTimeMonitoringInfoDeviceListSelectRow_Id";
	var gridPanelId="PumpRealTimeMonitoringListGridPanel_Id";
	var contentId="pumpRealTimeMonitoringCurveContent";
	var containerId="pumpRealTimeMonitoringCurveContainer";
	var divPrefix="pumpRealTimeMonitoringCurveDiv";
	if(deviceType==1){
		selectRowId="PipelineRealTimeMonitoringInfoDeviceListSelectRow_Id";
		gridPanelId="PipelineRealTimeMonitoringListGridPanel_Id";
		contentId="pipelineRealTimeMonitoringCurveContent";
		containerId="pipelineRealTimeMonitoringCurveContainer";
		divPrefix="pipelineRealTimeMonitoringCurveDiv";
	}
	
	
	var orgId = Ext.getCmp('leftOrg_Id').getValue();
	var deviceName='';
	var selectRow= Ext.getCmp(selectRowId).getValue();
	if(selectRow>=0){
		deviceName = Ext.getCmp(gridPanelId).getSelectionModel().getSelection()[0].data.wellName;
	}
	Ext.Ajax.request({
		method:'POST',
		url:context + '/realTimeMonitoringController/getRealTimeMonitoringCurveData',
		success:function(response) {
			var result =  Ext.JSON.decode(response.responseText);
		    var data = result.list;
		    var totals=result.curveCount;
		    var legendName =result.curveItems;
        	var colors=result.curveColors;
		    
		    var defaultColors=["#7cb5ec", "#434348", "#90ed7d", "#f7a35c", "#8085e9", "#f15c80", "#e4d354", "#2b908f", "#f45b5b", "#91e8e1"];
		   
		    var tickInterval = 1;
		    tickInterval = Math.floor(data.length / 2) + 1;
		    if(tickInterval<100){
		    	tickInterval=100;
		    }
		    
//		    var content = Ext.getCmp(contentId); // 获取功图列表panel信息
//            var panelHeight = content.getHeight(); // panel的高度
//            var panelWidth = content.getWidth(); // panel的宽度
//            var scrollWidth = 0; // 默认无滚动条
//            var columnCount = 2;
//            var rowCount = (totals%columnCount==0)?(totals/columnCount):(parseInt(totals/columnCount)+1); // 无滚动时总行数
//            var chartWidth = parseInt(panelWidth / columnCount)-2; // 无滚动条时图形宽度
//            var chartHeight = parseInt(chartWidth * 0.75); // 无滚动条时图形高度
//            if(totals==1){
//            	rowCount=1;
//            	chartWidth=panelWidth-1;
//            	chartHeight=panelHeight-1;
//            }else if(totals>1&&rowCount<=2){
//            	chartHeight=parseInt(panelHeight / rowCount)-2
//            }
//            var tatolHeight = chartHeight * rowCount; // 无滚动条时的图形总高度
//            if( tatolHeight > panelHeight ){ // 判断有无滚动条
//                scrollWidth = getScrollWidth(); // 滚动条的宽度
//                columnCount = 2; // 有滚动条时一行显示的图形个数
//                chartWidth = parseInt((panelWidth - scrollWidth) / columnCount); // 有滚动条时图形宽度
//                chartHeight = parseInt(chartWidth * 0.75); // 有滚动条时图形高度
//            }
//            var chartWidth2 = chartWidth + 'px';
//            var chartHeight2 = chartHeight + 'px';
//            var chartWidth2 = 50 + '%';
//            var chartHeight2 = chartHeight + 'px';
            
		    var columnCount = 2;
		    var rowCount = (totals%columnCount==0)?(totals/columnCount):(parseInt(totals/columnCount)+1);
		    
		    var chartWidth2 = 50 + '%';
            var chartHeight2 = 50 + '%';
            
            if(totals==1){
            	chartWidth2 = 100 + '%';
                chartHeight2 = 100 + '%';
            }else if(totals==2){
            	chartWidth2 = 100 + '%';
                chartHeight2 = 50 + '%';
            }else{
            	chartWidth2 = 50 + '%';
                chartHeight2 = 50 + '%';
            }
            
            $('#'+containerId).html(''); // 将html内容清空
            var htmlResult = '';
            var divId = '';
		    
            if (totals > 0) {
            	//添加div
            	for(var i=0;i<totals;i++){
            		divId = divPrefix + i+"_Id";
                    htmlResult += '<div id=\"' + divId + '\"';
                    htmlResult += ' style="height:'+ chartHeight2 +';width:'+ chartWidth2 +';"';
                    htmlResult += '></div>';
            	}
                $('#'+containerId).append(htmlResult);
                var aa=($('#'+containerId)[0]);
                var bb=aa.childElementCount;
                var cc=aa.children;
                var dd=cc[0].id;
                //数据处理
                for(var i=0;i<totals;i++){
                	divId = divPrefix + i+"_Id";
                	var xTitle='';
                	var yTitle=legendName[i];
                	var title = result.deviceName+":"+legendName[i].split("(")[0] + "实时曲线";
                	var subtitle='';
        		    var color=[];
        		    color.push(colors[i]);
        		    if(color[0]==''){
        		    	color[0]=defaultColors[i%10];
        		    }else{
        		    	color[0]='#'+colors[i];
        		    }
        		    
        		    var series = "[";
    		        series += "{\"name\":\"" + legendName[i] + "\"," 
    		        series += "\"data\":[";
    		        for (var j = 0; j < data.length; j++) {
    		        	series += "[" + Date.parse(data[j].acqTime.replace(/-/g, '/')) + "," + data[j].data[i] + "]";
    		            if (j != data.length - 1) {
    		                series += ",";
    		            }
    		        }
    		        series += "]}";
        		    series += "]";
        		    
        		    var ser = Ext.JSON.decode(series);
        		    
        		    var timeFormat='%H:%M';
        		    initDeviceRealtimeMonitoringStockChartFn(ser, tickInterval, divId, title, subtitle, xTitle, yTitle,color,false,true,false,timeFormat);
//        		    initDeviceRealtimeMonitoringChartFn(ser, tickInterval, divId, title, subtitle, xTitle, yTitle,color,false,timeFormat);
                }
            }
		},
		failure:function(){
			Ext.MessageBox.alert("错误","与后台联系的时候出了问题");
		},
		params: {
			deviceName:deviceName,
			deviceType:deviceType
        }
	});
};

function deviceRealtimeMonitoringCurve2(deviceType){
	var selectRowId="PumpRealTimeMonitoringInfoDeviceListSelectRow_Id";
	var gridPanelId="PumpRealTimeMonitoringListGridPanel_Id";
	
	var divId="pumpRealTimeMonitoringCurveDiv_Id";
	if(deviceType==1){
		selectRowId="PipelineRealTimeMonitoringInfoDeviceListSelectRow_Id";
		gridPanelId="PipelineRealTimeMonitoringListGridPanel_Id";
		divId="pipelineRealTimeMonitoringCurveDiv_Id";
	}
	
	
	var orgId = Ext.getCmp('leftOrg_Id').getValue();
	var deviceName='';
	var selectRow= Ext.getCmp(selectRowId).getValue();
	if(selectRow>=0){
		deviceName = Ext.getCmp(gridPanelId).getSelectionModel().getSelection()[0].data.wellName;
	}
	Ext.Ajax.request({
		method:'POST',
		url:context + '/realTimeMonitoringController/getRealTimeMonitoringCurveData',
		success:function(response) {
			var result =  Ext.JSON.decode(response.responseText);
		    var data = result.list;
//		    var color = ['#800000', // 红
//			       '#008C00', // 绿
//			       '#000000', // 黑
//			       '#0000FF', // 蓝
//			       '#F09614', // 黄
//			       '#FF00FF' // 紫
//			     ];
		    
		    var defaultColors=["#7cb5ec", "#434348", "#90ed7d", "#f7a35c", "#8085e9", "#f15c80", "#e4d354", "#2b908f", "#f45b5b", "#91e8e1"];
		   
		    var tickInterval = 1;
		    tickInterval = Math.floor(data.length / 2) + 1;
		    if(tickInterval<100){
		    	tickInterval=100;
		    }
		    var title = result.deviceName + "实时曲线";
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
		    
		    var timeFormat='%H:%M';
		    initDeviceRealtimeMonitoringStockChartFn(ser, tickInterval, divId, title, '', '', yAxis, color,true,timeFormat);
		},
		failure:function(){
			Ext.MessageBox.alert("错误","与后台联系的时候出了问题");
		},
		params: {
			deviceName:deviceName,
			deviceType:deviceType
        }
	});
};

function initDeviceRealtimeMonitoringChartFn(series, tickInterval, divId, title, subtitle, xtitle,yTitle, color,legend,timeFormat) {
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
        yAxis: {
            lineWidth: 1,
            title: {
                text: yTitle,
                style: {
//                    color: '#000000',
//                    fontWeight: 'bold'
                }
            },
            labels: {
                formatter: function () {
                    return Highcharts.numberFormat(this.value, 0);
                }
            },
            allowDecimals: false,    // 刻度值是否为小数
//            minorTickInterval: '',   // 不显示次刻度线
            opposite:false
      },
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

function initDeviceRealtimeMonitoringStockChartFn(series, tickInterval, divId, title, subtitle, xtitle,yTitle, color,legend,navigator,scrollbar,timeFormat) {
    Highcharts.setOptions({
        global: {
            useUTC: false
        }
    });
//    var mychart = new Highcharts.Chart({
    var mychart = new Highcharts.stockChart({
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
        navigator: {
    		enabled: navigator
    	},
    	scrollbar: {
    		enabled: scrollbar
    	},
        rangeSelector: {
    		buttons: [{
    			count: 1,
    			type: 'hour',//minute hour week month all
    			text: '1小时'
    		}, {
    			count: 6,
    			type: 'hour',
    			text: '6小时'
    		}, {
    			count: 12,
    			type: 'hour',
    			text: '12小时'
    		}, {
    			type: 'all',
    			text: '全部'
    		}],
    		inputEnabled: false,
    		selected: 0
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
        yAxis: {
            lineWidth: 1,
            title: {
                text: yTitle,
                style: {
//                    color: '#000000',
//                    fontWeight: 'bold'
                }
            },
            labels: {
                formatter: function () {
                    return Highcharts.numberFormat(this.value, 0);
                }
            },
            allowDecimals: false,    // 刻度值是否为小数
//            minorTickInterval: '',   // 不显示次刻度线
            opposite:false
      },
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

function initDeviceRealtimeMonitoringStockChartFn2(series, tickInterval, divId, title, subtitle, xtitle, yAxis, color,legend,timeFormat) {
    Highcharts.setOptions({
        global: {
            useUTC: false
        }
    });

    var mychart = new Highcharts.stockChart({
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
        rangeSelector: {
    		buttons: [{
    			count: 1,
    			type: 'hour',//minute hour week month all
    			text: '1小时'
    		}, {
    			count: 6,
    			type: 'hour',
    			text: '6小时'
    		}, {
    			count: 12,
    			type: 'hour',
    			text: '12小时'
    		}, {
    			type: 'all',
    			text: '全部'
    		}],
    		inputEnabled: false,
    		selected: 0
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