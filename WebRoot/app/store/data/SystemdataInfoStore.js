/*
 * author:钱邓水
 * 构造一个系统数据字典管理Store层
 */
Ext.define('AP.store.data.SystemdataInfoStore', {
    extend: 'Ext.data.BufferedStore',
    id: "SystemdataInfoStoreId",
    alias: 'widget.systemdataInfoStore',
    model: 'AP.model.data.SystemdataInfoModel',
    autoLoad: true,
    pageSize: defaultPageSize,
    proxy: {
        type: 'ajax',
        url: context + '/systemdataInfoController/findSystemdataInfoByListId',
        actionMethods: {
            read: 'POST'
        },
        start: 0,
        limit: defaultPageSize,
        reader: {
            type: 'json',
            rootProperty: 'totalRoot',
            totalProperty: 'totalCount',
            keepRawData: true
        }
    },
    //分页监听事件
    listeners: {
        load: function (store, options, eOpts) {
            //获得列表数
            var get_rawData = store.proxy.reader.rawData;
            var arrColumns = get_rawData.columns;
            var rw_g = Ext.getCmp("SystemdataInfoGridPanelId");
            if (!isNotVal(rw_g)) {
                var newColumns = Ext.JSON.decode(createDiagStatisticsColumn(arrColumns));
                var SystemdataInfoGridPanel_panel = Ext.create('Ext.grid.Panel', {
                    id: "SystemdataInfoGridPanelId",
                    border: false,
                    columnLines: true,
                    forceFit: true,
                    selType: 'checkboxmodel',
                    multiSelect: true,
                    emptyText: "<div class='con_div_' id='div_dataactiveid'><" + cosog.string.nodata + "></div>",
                    store: store,
                    columns: newColumns,
                    listeners: {
                        itemdblclick: editSystemdataInfo,
                        selectionchange: function (sm, selections) {
//                            var n = selections.length || 0;
//                            var edit = Ext.getCmp("editSDBtnId");
//                            edit.setDisabled(n != 1);
//                            if (n > 0) {
//                                var add = Ext.getCmp("addSystemdataInfoAction_Id")
//                                add.setDisabled(true);
//                                Ext.getCmp("delSDBtnId").setDisabled(false);
//                            } else {
//                                Ext.getCmp("addSystemdataInfoAction_Id").setDisabled(false);
//                                Ext.getCmp("delSDBtnId").setDisabled(true);
//                            }
                        }
                    }
                });
                Ext.getCmp("SystemdataInfoGridPanelViewId").add(SystemdataInfoGridPanel_panel);
            }
        },
        beforeload: function (store, options) {
            var new_params = {
                typeName: Ext.getCmp('sysdatacomboxfield_Id').getValue(),
                sysName: Ext.getCmp('sysname_Id').getValue()

            };
            Ext.apply(store.proxy.extraParams, new_params);
        }
    }
});