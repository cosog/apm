var auxiliaryDeviceInfoHandsontableHelper = null;
Ext.define('AP.view.well.AuxiliaryDeviceInfoPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.auxiliaryDeviceInfoPanel',
    id: 'AuxiliaryDeviceInfoPanel_Id',
    layout: 'fit',
    border: false,
    initComponent: function () {
        Ext.apply(this, {
            tbar: [{
                xtype: "combobox",
                fieldLabel: '辅件类型',
                id: 'AuxiliaryDeviceTypeComb_Id',
                labelWidth: 60,
                width: 170,
                labelAlign: 'left',
                triggerAction: 'all',
                displayField: "boxval",
                valueField: "boxkey",
                selectOnFocus: true,
                forceSelection: true,
                value: '',
                allowBlank: false,
                editable: false,
                emptyText: cosog.string.all,
                blankText: cosog.string.all,
                store: new Ext.data.SimpleStore({
                    fields: ['boxkey', 'boxval'],
                    data: [['', '选择全部'], [0, '泵辅件'], [1, '管辅件']]
                }),
                queryMode: 'local',
                listeners: {
                    select: function (v, o) {
                        CreateAndLoadAuxiliaryDeviceInfoTable();
                    }
                }
            }, '-', {
                id: 'AuxiliaryDeviceTotalCount_Id',
                xtype: 'component',
                hidden: false,
                tpl: cosog.string.totalCount + ': {count}',
                style: 'margin-right:15px'
            }, '->', {
                xtype: 'button',
                text: cosog.string.exportExcel,
                pressed: true,
                hidden: false,
                handler: function (v, o) {
                    var fields = "";
                    var heads = "";
                    var deviceType = Ext.getCmp('AuxiliaryDeviceTypeComb_Id').getValue();
                    var url = context + '/wellInformationManagerController/exportAuxiliaryDeviceData';
                    for (var i = 0; i < auxiliaryDeviceInfoHandsontableHelper.colHeaders.length; i++) {
                        fields += auxiliaryDeviceInfoHandsontableHelper.columns[i].data + ",";
                        heads += auxiliaryDeviceInfoHandsontableHelper.colHeaders[i] + ","
                    }
                    if (isNotVal(fields)) {
                        fields = fields.substring(0, fields.length - 1);
                        heads = heads.substring(0, heads.length - 1);
                    }
                    
                    var fileName='辅件设备';
                    var title='辅件设备';
                    if(deviceType===0){
                    	fileName='泵辅件设备';
                    	title='泵辅件设备';
                    }else if(deviceType===1){
                    	fileName='管辅件设备';
                    	title='泵辅件设备';
                    }

                    var param = "&fields=" + fields + "&heads=" + URLencode(URLencode(heads)) 
                    + "&orgId=" + leftOrg_Id + "&deviceType=" + deviceType + "&recordCount=10000" 
                    + "&fileName=" + URLencode(URLencode(fileName)) 
                    + "&title=" + URLencode(URLencode(title));
                    openExcelWindow(url + '?flag=true' + param);
                }
            }, '-', {
                xtype: 'button',
                iconCls: 'note-refresh',
                text: cosog.string.refresh,
                pressed: true,
                hidden: false,
                handler: function (v, o) {
                    CreateAndLoadAuxiliaryDeviceInfoTable();
                }

            }, '-', {
                xtype: 'button',
                itemId: 'saveAuxiliaryDeviceDataBtnId',
                id: 'saveAuxiliaryDeviceDataBtn_Id',
                disabled: false,
                hidden: false,
                pressed: true,
                text: cosog.string.save,
                iconCls: 'save',
                handler: function (v, o) {
                    auxiliaryDeviceInfoHandsontableHelper.saveData();
                }
            }, '-', {
                xtype: 'button',
                itemId: 'editAuxiliaryDeviceNameBtnId',
                id: 'editAuxiliaryDeviceNameBtn_Id',
                disabled: false,
                hidden: false,
                pressed: true,
                text: '修改设备名称',
                iconCls: 'edit',
                handler: function (v, o) {
                    auxiliaryDeviceInfoHandsontableHelper.editWellName();
                }
            }],
            html: '<div class="AuxiliaryDeviceContainer" style="width:100%;height:100%;"><div class="con" id="AuxiliaryDeviceTableDiv_id"></div></div>',
            listeners: {
                resize: function (abstractcomponent, adjWidth, adjHeight, options) {
                    if (auxiliaryDeviceInfoHandsontableHelper != null && auxiliaryDeviceInfoHandsontableHelper.hot != null && auxiliaryDeviceInfoHandsontableHelper.hot != undefined) {
                        CreateAndLoadAuxiliaryDeviceInfoTable();
                    }
                },
                beforeclose: function (panel, eOpts) {
                    if (auxiliaryDeviceInfoHandsontableHelper != null) {
                        if (auxiliaryDeviceInfoHandsontableHelper.hot != undefined) {
                            auxiliaryDeviceInfoHandsontableHelper.hot.destroy();
                        }
                        auxiliaryDeviceInfoHandsontableHelper = null;
                    }
                }
            }
        })
        this.callParent(arguments);
    }
});

function CreateAndLoadAuxiliaryDeviceInfoTable(isNew) {
    if(isNew&&auxiliaryDeviceInfoHandsontableHelper!=null){
    	if(auxiliaryDeviceInfoHandsontableHelper.hot!=undefined){
    		auxiliaryDeviceInfoHandsontableHelper.hot.destroy();
    	}
    	auxiliaryDeviceInfoHandsontableHelper=null;
    }
    var deviceType = Ext.getCmp('AuxiliaryDeviceTypeComb_Id').getValue();
    Ext.Ajax.request({
        method: 'POST',
        url: context + '/wellInformationManagerController/doAuxiliaryDeviceShow',
        success: function (response) {
            var result = Ext.JSON.decode(response.responseText);
            if (auxiliaryDeviceInfoHandsontableHelper == null || auxiliaryDeviceInfoHandsontableHelper.hot == null || auxiliaryDeviceInfoHandsontableHelper.hot == undefined) {
                auxiliaryDeviceInfoHandsontableHelper = AuxiliaryDeviceInfoHandsontableHelper.createNew("AuxiliaryDeviceTableDiv_id");
                var colHeaders = "[";
                var columns = "[";

                for (var i = 0; i < result.columns.length; i++) {
                    colHeaders += "'" + result.columns[i].header + "'";
                    if (result.columns[i].dataIndex.toUpperCase() === "type".toUpperCase()) {
                    	columns += "{data:'" + result.columns[i].dataIndex + "',type:'dropdown',strict:true,allowInvalid:false,source:['泵辅件', '管辅件']}";
                    } else if (result.columns[i].dataIndex.toUpperCase() === "sort".toUpperCase()) {
                        columns += "{data:'" + result.columns[i].dataIndex + "',type:'text',allowInvalid: true, validator: function(val, callback){return handsontableDataCheck_Num_Nullable(val, callback,this.row, this.col,auxiliaryDeviceInfoHandsontableHelper);}}";
                    } else {
                        columns += "{data:'" + result.columns[i].dataIndex + "'}";
                    }
                    if (i < result.columns.length - 1) {
                        colHeaders += ",";
                        columns += ",";
                    }
                }
                colHeaders += "]";
                columns += "]";
                auxiliaryDeviceInfoHandsontableHelper.colHeaders = Ext.JSON.decode(colHeaders);
                auxiliaryDeviceInfoHandsontableHelper.columns = Ext.JSON.decode(columns);
                auxiliaryDeviceInfoHandsontableHelper.createTable(result.totalRoot);
            } else {
                auxiliaryDeviceInfoHandsontableHelper.hot.loadData(result.totalRoot);
            }
            Ext.getCmp("AuxiliaryDeviceTotalCount_Id").update({
                count: result.totalCount
            });
        },
        failure: function () {
            Ext.MessageBox.alert("错误", "与后台联系的时候出了问题");
        },
        params: {
            deviceType: deviceType,
            recordCount: 50,
            page: 1,
            limit: 10000
        }
    });
};

var AuxiliaryDeviceInfoHandsontableHelper = {
    createNew: function (divid) {
        var auxiliaryDeviceInfoHandsontableHelper = {};
        auxiliaryDeviceInfoHandsontableHelper.hot = '';
        auxiliaryDeviceInfoHandsontableHelper.divid = divid;
        auxiliaryDeviceInfoHandsontableHelper.validresult = true; //数据校验
        auxiliaryDeviceInfoHandsontableHelper.colHeaders = [];
        auxiliaryDeviceInfoHandsontableHelper.columns = [];

        auxiliaryDeviceInfoHandsontableHelper.AllData = {};
        auxiliaryDeviceInfoHandsontableHelper.updatelist = [];
        auxiliaryDeviceInfoHandsontableHelper.delidslist = [];
        auxiliaryDeviceInfoHandsontableHelper.insertlist = [];
        auxiliaryDeviceInfoHandsontableHelper.editNameList = [];

        auxiliaryDeviceInfoHandsontableHelper.addColBg = function (instance, td, row, col, prop, value, cellProperties) {
            Handsontable.renderers.TextRenderer.apply(this, arguments);
            td.style.backgroundColor = 'rgb(242, 242, 242)';
        }

        auxiliaryDeviceInfoHandsontableHelper.createTable = function (data) {
            $('#' + auxiliaryDeviceInfoHandsontableHelper.divid).empty();
            var hotElement = document.querySelector('#' + auxiliaryDeviceInfoHandsontableHelper.divid);
            auxiliaryDeviceInfoHandsontableHelper.hot = new Handsontable(hotElement, {
                data: data,
                hiddenColumns: {
                    columns: [0],
                    indicators: true
                },
                columns: auxiliaryDeviceInfoHandsontableHelper.columns,
                stretchH: 'all', //延伸列的宽度, last:延伸最后一列,all:延伸所有列,none默认不延伸
                autoWrapRow: true,
                rowHeaders: true, //显示行头
                colHeaders: auxiliaryDeviceInfoHandsontableHelper.colHeaders, //显示列头
                columnSorting: true, //允许排序
                contextMenu: {
                    items: {
                        "row_above": {
                            name: '向上插入一行',
                        },
                        "row_below": {
                            name: '向下插入一行',
                        },
                        "col_left": {
                            name: '向左插入一列',
                        },
                        "col_right": {
                            name: '向右插入一列',
                        },
                        "remove_row": {
                            name: '删除行',
                        },
                        "remove_col": {
                            name: '删除列',
                        },
                        "merge_cell": {
                            name: '合并单元格',
                        },
                        "copy": {
                            name: '复制',
                        },
                        "cut": {
                            name: '剪切',
                        },
                        "paste": {
                            name: '粘贴',
                            disabled: function () {
                            },
                            callback: function () {
                            }
                        }
                    }
                }, //右键菜单展示
                sortIndicator: true,
                manualColumnResize: true, //当值为true时，允许拖动，当为false时禁止拖动
                manualRowResize: true, //当值为true时，允许拖动，当为false时禁止拖动
                //	                dropdownMenu: ['filter_by_condition', 'filter_by_value', 'filter_action_bar'],
                filters: true,
                renderAllRows: true,
                search: true,
                cells: function (row, col, prop) {
                    var cellProperties = {};
                    var visualRowIndex = this.instance.toVisualRow(row);
                    var visualColIndex = this.instance.toVisualColumn(col);
                },
                afterDestroy: function () {
                },
                beforeRemoveRow: function (index, amount) {
                    var ids = [];
                    //封装id成array传入后台
                    if (amount != 0) {
                        for (var i = index; i < amount + index; i++) {
                            var rowdata = auxiliaryDeviceInfoHandsontableHelper.hot.getDataAtRow(i);
                            ids.push(rowdata[0]);
                        }
                        auxiliaryDeviceInfoHandsontableHelper.delExpressCount(ids);
                        auxiliaryDeviceInfoHandsontableHelper.screening();
                    }
                },
                afterChange: function (changes, source) {
                    //params 参数 1.column num , 2,id, 3,oldvalue , 4.newvalue
                    if (changes != null) {
                        for (var i = 0; i < changes.length; i++) {
                            var params = [];
                            var index = changes[i][0]; //行号码
                            var rowdata = auxiliaryDeviceInfoHandsontableHelper.hot.getDataAtRow(index);
                            params.push(rowdata[0]);
                            params.push(changes[i][1]);
                            params.push(changes[i][2]);
                            params.push(changes[i][3]);
                            if ("edit" == source && params[1] == "name") { //编辑井名单元格
                                var data = "{\"oldName\":\"" + params[2] + "\",\"newName\":\"" + params[3] + "\"}";
                                auxiliaryDeviceInfoHandsontableHelper.editNameList.push(Ext.JSON.decode(data));
                            }

                            //仅当单元格发生改变的时候,id!=null,说明是更新
                            if (params[2] != params[3] && params[0] != null && params[0] > 0) {
                                var data = "{";
                                for (var j = 0; j < auxiliaryDeviceInfoHandsontableHelper.columns.length; j++) {
                                    data += auxiliaryDeviceInfoHandsontableHelper.columns[j].data + ":'" + rowdata[j] + "'";
                                    if (j < auxiliaryDeviceInfoHandsontableHelper.columns.length - 1) {
                                        data += ","
                                    }
                                }
                                data += "}"
                                auxiliaryDeviceInfoHandsontableHelper.updateExpressCount(Ext.JSON.decode(data));
                            }
                        }
                    }
                }
            });
        }
        //插入的数据的获取
        auxiliaryDeviceInfoHandsontableHelper.insertExpressCount = function () {
            var idsdata = auxiliaryDeviceInfoHandsontableHelper.hot.getDataAtCol(0); //所有的id
            for (var i = 0; i < idsdata.length; i++) {
                //id=null时,是插入数据,此时的i正好是行号
                if (idsdata[i] == null || idsdata[i] < 0) {
                    //获得id=null时的所有数据封装进data
                    var rowdata = auxiliaryDeviceInfoHandsontableHelper.hot.getDataAtRow(i);
                    //var collength = hot.countCols();
                    if (rowdata != null) {
                        var data = "{";
                        for (var j = 0; j < auxiliaryDeviceInfoHandsontableHelper.columns.length; j++) {
                            data += auxiliaryDeviceInfoHandsontableHelper.columns[j].data + ":'" + rowdata[j] + "'";
                            if (j < auxiliaryDeviceInfoHandsontableHelper.columns.length - 1) {
                                data += ","
                            }
                        }
                        data += "}"
                        auxiliaryDeviceInfoHandsontableHelper.insertlist.push(Ext.JSON.decode(data));
                    }
                }
            }
            if (auxiliaryDeviceInfoHandsontableHelper.insertlist.length != 0) {
                auxiliaryDeviceInfoHandsontableHelper.AllData.insertlist = auxiliaryDeviceInfoHandsontableHelper.insertlist;
            }
        }
        //保存数据
        auxiliaryDeviceInfoHandsontableHelper.saveData = function () {
            var IframeViewSelection = Ext.getCmp("IframeView_Id").getSelectionModel().getSelection();
            //插入的数据的获取
            auxiliaryDeviceInfoHandsontableHelper.insertExpressCount();
            if (JSON.stringify(auxiliaryDeviceInfoHandsontableHelper.AllData) != "{}" && auxiliaryDeviceInfoHandsontableHelper.validresult) {
                Ext.Ajax.request({
                    method: 'POST',
                    url: context + '/wellInformationManagerController/saveAuxiliaryDeviceHandsontableData',
                    success: function (response) {
                        rdata = Ext.JSON.decode(response.responseText);
                        if (rdata.success) {
                            Ext.MessageBox.alert("信息", "保存成功");
                            //保存以后重置全局容器
                            auxiliaryDeviceInfoHandsontableHelper.clearContainer();
                            CreateAndLoadAuxiliaryDeviceInfoTable();
                        } else {
                            Ext.MessageBox.alert("信息", "数据保存失败");

                        }
                    },
                    failure: function () {
                        Ext.MessageBox.alert("信息", "请求失败");
                        auxiliaryDeviceInfoHandsontableHelper.clearContainer();
                    },
                    params: {
                        data: JSON.stringify(auxiliaryDeviceInfoHandsontableHelper.AllData)
                    }
                });
            } else {
                if (!auxiliaryDeviceInfoHandsontableHelper.validresult) {
                    Ext.MessageBox.alert("信息", "数据类型错误");
                } else {
                    Ext.MessageBox.alert("信息", "无数据变化");
                }
            }

        }

        //修改设备名称
        auxiliaryDeviceInfoHandsontableHelper.editWellName = function () {
            if (auxiliaryDeviceInfoHandsontableHelper.editNameList.length > 0 && auxiliaryDeviceInfoHandsontableHelper.validresult) {
                Ext.Ajax.request({
                    method: 'POST',
                    url: context + '/wellInformationManagerController/editAuxiliaryDeviceName',
                    success: function (response) {
                        rdata = Ext.JSON.decode(response.responseText);
                        if (rdata.success) {
                            Ext.MessageBox.alert("信息", "保存成功");
                            auxiliaryDeviceInfoHandsontableHelper.clearContainer();
                            CreateAndLoadAuxiliaryDeviceInfoTable();
                        } else {
                            Ext.MessageBox.alert("信息", "数据保存失败");
                        }
                    },
                    failure: function () {
                        Ext.MessageBox.alert("信息", "请求失败");
                        auxiliaryDeviceInfoHandsontableHelper.clearContainer();
                    },
                    params: {
                        data: JSON.stringify(auxiliaryDeviceInfoHandsontableHelper.editNameList)
                    }
                });
            } else {
                if (!auxiliaryDeviceInfoHandsontableHelper.validresult) {
                    Ext.MessageBox.alert("信息", "数据类型错误");
                } else {
                    Ext.MessageBox.alert("信息", "无数据变化");
                }
            }
        }


        //删除的优先级最高
        auxiliaryDeviceInfoHandsontableHelper.delExpressCount = function (ids) {
            //传入的ids.length不可能为0
            $.each(ids, function (index, id) {
                if (id != null) {
                    auxiliaryDeviceInfoHandsontableHelper.delidslist.push(id);
                }
            });
            auxiliaryDeviceInfoHandsontableHelper.AllData.delidslist = auxiliaryDeviceInfoHandsontableHelper.delidslist;
        }

        //updatelist数据更新
        auxiliaryDeviceInfoHandsontableHelper.screening = function () {
            if (auxiliaryDeviceInfoHandsontableHelper.updatelist.length != 0 && auxiliaryDeviceInfoHandsontableHelper.delidslist.lentgh != 0) {
                for (var i = 0; i < auxiliaryDeviceInfoHandsontableHelper.delidslist.length; i++) {
                    for (var j = 0; j < auxiliaryDeviceInfoHandsontableHelper.updatelist.length; j++) {
                        if (auxiliaryDeviceInfoHandsontableHelper.updatelist[j].id == auxiliaryDeviceInfoHandsontableHelper.delidslist[i]) {
                            //更新updatelist
                            auxiliaryDeviceInfoHandsontableHelper.updatelist.splice(j, 1);
                        }
                    }
                }
                //把updatelist封装进AllData
                auxiliaryDeviceInfoHandsontableHelper.AllData.updatelist = auxiliaryDeviceInfoHandsontableHelper.updatelist;
            }
        }

        //更新数据
        auxiliaryDeviceInfoHandsontableHelper.updateExpressCount = function (data) {
            if (JSON.stringify(data) != "{}") {
                var flag = true;
                //判断记录是否存在,更新数据     
                $.each(auxiliaryDeviceInfoHandsontableHelper.updatelist, function (index, node) {
                    if (node.id == data.id) {
                        //此记录已经有了
                        flag = false;
                        //用新得到的记录替换原来的,不用新增
                        auxiliaryDeviceInfoHandsontableHelper.updatelist[index] = data;
                    }
                });
                flag && auxiliaryDeviceInfoHandsontableHelper.updatelist.push(data);
                //封装
                auxiliaryDeviceInfoHandsontableHelper.AllData.updatelist = auxiliaryDeviceInfoHandsontableHelper.updatelist;
            }
        }

        auxiliaryDeviceInfoHandsontableHelper.clearContainer = function () {
            auxiliaryDeviceInfoHandsontableHelper.AllData = {};
            auxiliaryDeviceInfoHandsontableHelper.updatelist = [];
            auxiliaryDeviceInfoHandsontableHelper.delidslist = [];
            auxiliaryDeviceInfoHandsontableHelper.insertlist = [];
            auxiliaryDeviceInfoHandsontableHelper.editNameList = [];
        }

        return auxiliaryDeviceInfoHandsontableHelper;
    }
};