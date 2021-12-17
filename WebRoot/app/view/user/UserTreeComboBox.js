Ext.Loader.setConfig({
    enabled: true
});
Ext.define('AP.view.user.UserTreeComboBox', {
    extend: 'Ext.form.field.ComboBox',
    alias: 'widget.userTreeComboBox',
    store: new Ext.data.ArrayStore({
        fields: [],
        data: [[]]
    }),
    editable: false,
    //resizable : true,
    labelAlign: 'left',
    readOnly: false,
    editable: false,
    _idValue: null,
    _txtValue: null,
    callback: Ext.emptyFn,
    initComponent: function () {
        var combo = this;
        this.callParent(arguments);
        this.treeRenderId = Ext.id();
        // 必须要用这个定义tpl
        this.tpl = new Ext.XTemplate('<tpl for="."><div style="height:200' + 'px;"><div id="' + this.treeRenderId + '"></div></div></tpl>');

        var treeObj = new Ext.tree.Panel({
            border: false,
            width: 160,
            autoScroll: true,
            rootVisible: false,
            store: Ext.create('Ext.data.TreeStore', {
                fields: ['orgId', 'text', 'leaf'],
                autoLoad: false,
                proxy: {
                    type: 'ajax',
                    url: context + '/orgManagerController/constructOrgRightTree.action',
                    reader: 'json'
                },
                root: {
                    expanded: true,
                    text: 'orgName'
                }
            })
        });
        treeObj.on('itemclick', function (view, rec) {
            if (rec) {
                combo.setValue(this._txtValue = rec.get('text'));
                combo._idValue = rec.data.orgId;
                //设置回调  
                combo.callback.call(this, rec.data.orgId, rec.get('text'));
                combo.collapse();
            }
        });

        this.on({
            'expand': function () {
                this.value = "";
                if (!treeObj.rendered && treeObj && !this.readOnly) {
                    Ext.defer(function () {
                        treeObj.render(this.treeRenderId);
                    }, 160, this);
                }
            }
        });

    },
    getValue: function () { // 获取id值
        return this._idValue;
    },
    getTextValue: function () { // 获取text值
        return this._txtValue;
    },
    setLocalValue: function (txt, id) { // 设值
        this._idValue = id;
        this.setValue(this._txtValue = txt);
    }
});