package com.cosog.service.historyQuery;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cosog.model.AlarmShowStyle;
import com.cosog.model.data.DataDictionary;
import com.cosog.model.drive.ModbusProtocolConfig;
import com.cosog.service.base.BaseService;
import com.cosog.service.base.CommonDataService;
import com.cosog.service.data.DataitemsInfoService;
import com.cosog.task.EquipmentDriverServerTask;
import com.cosog.utils.DataModelMap;
import com.cosog.utils.EquipmentDriveMap;
import com.cosog.utils.Page;
import com.cosog.utils.ProtocolItemResolutionData;
import com.cosog.utils.StringManagerUtils;
import com.google.gson.Gson;

@Service("historyQueryService")
public class HistoryQueryService<T> extends BaseService<T>  {
	@Autowired
	private CommonDataService service;
	@Autowired
	private DataitemsInfoService dataitemsInfoService;
	
	public String getHistoryQueryCommStatusStatData(String orgId,String deviceType,String deviceTypeStatValue) throws IOException, SQLException{
		StringBuffer result_json = new StringBuffer();
		Map<String, Object> dataModelMap = DataModelMap.getMapObject();
		AlarmShowStyle alarmShowStyle=(AlarmShowStyle) dataModelMap.get("AlarmShowStyle");
		if(alarmShowStyle==null){
			EquipmentDriverServerTask.initAlarmStyle();
			alarmShowStyle=(AlarmShowStyle) dataModelMap.get("AlarmShowStyle");
		}
		String tableName="tbl_pumpacqdata_latest";
		String deviceTableName="viw_pumpdevice";
		if(StringManagerUtils.stringToInteger(deviceType)!=0){
			tableName="tbl_pipelineacqdata_latest";
			deviceTableName="viw_pipelinedevice";
		}
		
		String sql="select t2.commstatus,count(1) from "+deviceTableName+" t "
				+ " left outer join "+tableName+" t2 on  t2.wellid=t.id "
				+ " where t.orgid in("+orgId+") ";
		if(StringManagerUtils.isNotNull(deviceTypeStatValue)){
			sql+=" and t.devicetypename='"+deviceTypeStatValue+"'";
		}
		sql+=" group by t2.commstatus";
		
		List<?> list = this.findCallSql(sql);
		String columns = "["
				+ "{ \"header\":\"序号\",\"dataIndex\":\"id\",width:50,children:[] },"
				+ "{ \"header\":\"名称\",\"dataIndex\":\"item\",children:[] },"
				+ "{ \"header\":\"变量\",\"dataIndex\":\"count\",children:[] }"
				+ "]";
		result_json.append("{ \"success\":true,\"columns\":"+columns+",");
		result_json.append("\"totalCount\":3,");
		
		int total=0,online=0,offline=0;
		result_json.append("\"totalRoot\":[");
		for(int i=0;i<list.size();i++){
			Object[] obj=(Object[]) list.get(i);
			if(StringManagerUtils.stringToInteger(obj[0]+"")==1){
				online=StringManagerUtils.stringToInteger(obj[1]+"");
			}else{
				offline=StringManagerUtils.stringToInteger(obj[1]+"");
			}
		}
		total=online+offline;
		result_json.append("{\"id\":1,");
		result_json.append("\"item\":\"全部\",");
		result_json.append("\"itemCode\":\"all\",");
		result_json.append("\"count\":"+total+"},");
		
		result_json.append("{\"id\":2,");
		result_json.append("\"item\":\"在线\",");
		result_json.append("\"itemCode\":\"online\",");
		result_json.append("\"count\":"+online+"},");
		
		result_json.append("{\"id\":3,");
		result_json.append("\"item\":\"离线\",");
		result_json.append("\"itemCode\":\"offline\",");
		result_json.append("\"count\":"+offline+"}");
		result_json.append("]");
		result_json.append(",\"AlarmShowStyle\":"+new Gson().toJson(alarmShowStyle));
		result_json.append("}");
		return result_json.toString().replaceAll("\"null\"", "\"\"");
	}
	
	public String getHistoryQueryDeviceTypeStatData(String orgId,String deviceType,String commStatusStatValue) throws IOException, SQLException{
		StringBuffer result_json = new StringBuffer();
		Map<String, Object> dataModelMap = DataModelMap.getMapObject();
		AlarmShowStyle alarmShowStyle=(AlarmShowStyle) dataModelMap.get("AlarmShowStyle");
		if(alarmShowStyle==null){
			EquipmentDriverServerTask.initAlarmStyle();
			alarmShowStyle=(AlarmShowStyle) dataModelMap.get("AlarmShowStyle");
		}
		String tableName="tbl_pumpacqdata_latest";
		String deviceTableName="viw_pumpdevice";
		if(StringManagerUtils.stringToInteger(deviceType)!=0){
			tableName="tbl_pipelineacqdata_latest";
			deviceTableName="viw_pipelinedevice";
		}
		
		String sql="select t.devicetypename,t.devicetype,count(1) from "+deviceTableName+" t "
				+ " left outer join "+tableName+" t2 on t.id=t2.wellid "
				+ " where t.orgid in("+orgId+") ";
		
		
		
		if(StringManagerUtils.isNotNull(commStatusStatValue)){
			sql+=" and decode(t2.commstatus,1,'在线','离线')='"+commStatusStatValue+"'";
		}
		sql+=" group by t.devicetypename,t.devicetype";
		sql+=" order by t.devicetype";
		
		List<?> list = this.findCallSql(sql);
		String columns = "["
				+ "{ \"header\":\"序号\",\"dataIndex\":\"id\",width:50,children:[] },"
				+ "{ \"header\":\"名称\",\"dataIndex\":\"item\",children:[] },"
				+ "{ \"header\":\"变量\",\"dataIndex\":\"count\",children:[] }"
				+ "]";
		result_json.append("{ \"success\":true,\"columns\":"+columns+",");
		result_json.append("\"totalCount\":"+list.size()+",");
		
		result_json.append("\"totalRoot\":[");
		for(int i=0;i<list.size();i++){
			Object[] obj=(Object[]) list.get(i);
			result_json.append("{\"id\":"+(i+1)+",");
			result_json.append("\"item\":\""+obj[0]+"\",");
			result_json.append("\"itemCode\":\""+obj[1]+"\",");
			result_json.append("\"count\":"+obj[2]+"},");
		}
		if(result_json.toString().endsWith(",")){
			result_json.deleteCharAt(result_json.length() - 1);
		}
		result_json.append("]");
		result_json.append(",\"AlarmShowStyle\":"+new Gson().toJson(alarmShowStyle));
		result_json.append("}");
		return result_json.toString().replaceAll("\"null\"", "\"\"");
	}
	
	public String getHistoryQueryDeviceList(String orgId,String deviceName,String deviceType,String commStatusStatValue,String deviceTypeStatValue,Page pager) throws IOException, SQLException{
		StringBuffer result_json = new StringBuffer();
		Map<String, Object> dataModelMap = DataModelMap.getMapObject();
		AlarmShowStyle alarmShowStyle=(AlarmShowStyle) dataModelMap.get("AlarmShowStyle");
		if(alarmShowStyle==null){
			EquipmentDriverServerTask.initAlarmStyle();
			alarmShowStyle=(AlarmShowStyle) dataModelMap.get("AlarmShowStyle");
		}
		String deviceTableName="tbl_pumpdevice";
		String tableName="tbl_pumpacqdata_latest";
		if(StringManagerUtils.stringToInteger(deviceType)==1){
			tableName="tbl_pipelineacqdata_latest";
			deviceTableName="tbl_pipelinedevice";
		}
		String columns = "["
				+ "{ \"header\":\"序号\",\"dataIndex\":\"id\",width:50,children:[] },"
				+ "{ \"header\":\"设备名称\",\"dataIndex\":\"wellName\",flex:9,children:[] },"
				+ "{ \"header\":\"通信状态\",\"dataIndex\":\"commStatusName\",flex:5,children:[] },"
				+ "{ \"header\":\"采集时间\",\"dataIndex\":\"acqTime\",flex:10,children:[] },"
				+ "{ \"header\":\"设备类型\",\"dataIndex\":\"deviceTypeName\",flex:6,children:[] }"
				+ "]";
		
		String sql="select t2.id,t.wellname,t2.commstatus,"
				+ "decode(t2.commstatus,1,'在线','离线') as commStatusName,"
				+ "decode(t5.alarmsign,0,0,null,0,t5.alarmlevel) as commAlarmLevel,"
				+ "to_char(t2.acqtime,'yyyy-mm-dd hh24:mi:ss'),c1.itemname as devicetypename ";
		
		
		sql+= " from "+deviceTableName+" t "
				+ " left outer join "+tableName+" t2 on t2.wellid=t.id"
				+ " left outer join tbl_protocolalarminstance t3 on t.alarminstancecode=t3.code"
				+ " left outer join tbl_alarm_unit_conf t4 on t3.alarmunitid=t4.id"
				+ " left outer join tbl_alarm_item2unit_conf t5 on t4.id=t5.unitid and t5.type=3  and  decode(t2.commstatus,1,'在线','离线')=t5.itemname"
				+ " left outer join tbl_code c1 on c1.itemcode='DEVICETYPE' and t.devicetype=c1.itemvalue "
				+ " where  t.orgid in ("+orgId+") ";
		if(StringManagerUtils.isNotNull(deviceName)){
			sql+=" and t.wellName='"+deviceName+"'";
		}
		if(StringManagerUtils.isNotNull(commStatusStatValue)){
			sql+=" and decode(t2.commstatus,1,'在线','离线')='"+commStatusStatValue+"'";
		}
		if(StringManagerUtils.isNotNull(deviceTypeStatValue)){
			sql+=" and c1.itemname='"+deviceTypeStatValue+"'";
		}
		sql+=" order by t.sortnum,t.wellname";
		
		int maxvalue=pager.getLimit()+pager.getStart();
		String finalSql="select * from   ( select a.*,rownum as rn from ("+sql+" ) a where  rownum <="+maxvalue+") b where rn >"+pager.getStart();
		
		int totals=this.getTotalCountRows(sql);
		List<?> list = this.findCallSql(finalSql);
		result_json.append("{ \"success\":true,\"columns\":"+columns+",");
		result_json.append("\"start_date\":\""+pager.getStart_date()+"\",");
		result_json.append("\"end_date\":\""+pager.getEnd_date()+"\",");
		result_json.append("\"totalCount\":"+totals+",");
		result_json.append("\"totalRoot\":[");
		for(int i=0;i<list.size();i++){
			Object[] obj=(Object[]) list.get(i);
			result_json.append("{\"id\":"+obj[0]+",");
			result_json.append("\"wellName\":\""+obj[1]+"\",");
			result_json.append("\"commStatus\":"+obj[2]+",");
			result_json.append("\"commStatusName\":\""+obj[3]+"\",");
			result_json.append("\"commAlarmLevel\":"+obj[4]+",");
			result_json.append("\"acqTime\":\""+obj[5]+"\",");
			result_json.append("\"deviceTypeName\":\""+obj[6]+"\"},");
		}
		if(result_json.toString().endsWith(",")){
			result_json.deleteCharAt(result_json.length() - 1);
		}
		result_json.append("]");
		result_json.append(",\"AlarmShowStyle\":"+new Gson().toJson(alarmShowStyle)+"}");
		return result_json.toString().replaceAll("\"null\"", "\"\"");
	}
	
	public String getHistoryQueryDeviceListExportData(String orgId,String deviceName,String deviceType,String commStatusStatValue,String deviceTypeStatValue,Page pager) throws IOException, SQLException{
		StringBuffer result_json = new StringBuffer();
		Map<String, Object> dataModelMap = DataModelMap.getMapObject();
		AlarmShowStyle alarmShowStyle=(AlarmShowStyle) dataModelMap.get("AlarmShowStyle");
		if(alarmShowStyle==null){
			EquipmentDriverServerTask.initAlarmStyle();
			alarmShowStyle=(AlarmShowStyle) dataModelMap.get("AlarmShowStyle");
		}
		String deviceTableName="tbl_pumpdevice";
		String tableName="tbl_pumpacqdata_latest";
		if(StringManagerUtils.stringToInteger(deviceType)==1){
			tableName="tbl_pipelineacqdata_latest";
			deviceTableName="tbl_pipelinedevice";
		}
		
		String sql="select t2.id,t.wellname,t2.commstatus,"
				+ "decode(t2.commstatus,1,'在线','离线') as commStatusName,"
				+ "decode(t5.alarmsign,0,0,null,0,t5.alarmlevel) as commAlarmLevel,"
				+ "to_char(t2.acqtime,'yyyy-mm-dd hh24:mi:ss'),c1.itemname as devicetypename ";
		
		
		sql+= " from "+deviceTableName+" t "
				+ " left outer join "+tableName+" t2 on t2.wellid=t.id"
				+ " left outer join tbl_protocolalarminstance t3 on t.alarminstancecode=t3.code"
				+ " left outer join tbl_alarm_unit_conf t4 on t3.alarmunitid=t4.id"
				+ " left outer join tbl_alarm_item2unit_conf t5 on t4.id=t5.unitid and t5.type=3  and  decode(t2.commstatus,1,'在线','离线')=t5.itemname"
				+ " left outer join tbl_code c1 on c1.itemcode='DEVICETYPE' and t.devicetype=c1.itemvalue "
				+ " where  t.orgid in ("+orgId+") ";
		if(StringManagerUtils.isNotNull(deviceName)){
			sql+=" and t.wellName='"+deviceName+"'";
		}
		if(StringManagerUtils.isNotNull(commStatusStatValue)){
			sql+=" and decode(t2.commstatus,1,'在线','离线')='"+commStatusStatValue+"'";
		}
		if(StringManagerUtils.isNotNull(deviceTypeStatValue)){
			sql+=" and c1.itemname='"+deviceTypeStatValue+"'";
		}
		sql+=" order by t.sortnum,t.wellname";
		
		int maxvalue=pager.getLimit()+pager.getStart();
		String finalSql="select * from   ( select a.*,rownum as rn from ("+sql+" ) a where  rownum <="+maxvalue+") b where rn >"+pager.getStart();
		
		int totals=this.getTotalCountRows(sql);
		List<?> list = this.findCallSql(finalSql);
		result_json.append("[");
		for(int i=0;i<list.size();i++){
			Object[] obj=(Object[]) list.get(i);
			result_json.append("{\"id\":"+obj[0]+",");
			result_json.append("\"wellName\":\""+obj[1]+"\",");
			result_json.append("\"commStatus\":"+obj[2]+",");
			result_json.append("\"commStatusName\":\""+obj[3]+"\",");
			result_json.append("\"commAlarmLevel\":"+obj[4]+",");
			result_json.append("\"acqTime\":\""+obj[5]+"\",");
			result_json.append("\"deviceTypeName\":\""+obj[6]+"\"},");
		}
		if(result_json.toString().endsWith(",")){
			result_json.deleteCharAt(result_json.length() - 1);
		}
		result_json.append("]");
		return result_json.toString().replaceAll("\"null\"", "\"\"");
	}
	
	public String getDeviceHistoryData(String orgId,String deviceName,String deviceType,Page pager) throws IOException, SQLException{
		StringBuffer result_json = new StringBuffer();
		Map<String, Object> dataModelMap = DataModelMap.getMapObject();
		AlarmShowStyle alarmShowStyle=(AlarmShowStyle) dataModelMap.get("AlarmShowStyle");
		if(alarmShowStyle==null){
			EquipmentDriverServerTask.initAlarmStyle();
			alarmShowStyle=(AlarmShowStyle) dataModelMap.get("AlarmShowStyle");
		}
		ModbusProtocolConfig.Protocol protocol=null;
		Map<String, Object> equipmentDriveMap = EquipmentDriveMap.getMapObject();
		if(equipmentDriveMap.size()==0){
			EquipmentDriverServerTask.loadProtocolConfig();
			equipmentDriveMap = EquipmentDriveMap.getMapObject();
		}
		ModbusProtocolConfig modbusProtocolConfig=(ModbusProtocolConfig) equipmentDriveMap.get("modbusProtocolConfig");
		for(int i=0;i<modbusProtocolConfig.getProtocol().size();i++){
			
			if(modbusProtocolConfig.getProtocol().get(i).getDeviceType()==StringManagerUtils.stringToInteger(deviceType)){
				protocol=modbusProtocolConfig.getProtocol().get(i);
				break;
			}
			
		}
		String hisTableName="tbl_pumpacqdata_hist";
		String deviceTableName="tbl_pumpdevice";
		String ddicName="pumpHistoryQuery";
		DataDictionary ddic = null;
		List<String> ddicColumnsList=new ArrayList<String>();
		if(StringManagerUtils.stringToInteger(deviceType)==1){
			hisTableName="tbl_pipelineacqdata_hist";
			deviceTableName="tbl_pipelinedevice";
			ddicName="pipelineHistoryQuery";
		}
		
		
		ddic  = dataitemsInfoService.findTableSqlWhereByListFaceId(ddicName);
		String columns = ddic.getTableHeader();
		
		String sql="select t2.id,t.wellname,t2.commstatus,"
				+ "decode(t2.commstatus,1,'在线','离线') as commStatusName,"
				+ "decode(t5.alarmsign,0,0,null,0,t5.alarmlevel) as commAlarmLevel,"
				+ "to_char(t2.acqtime,'yyyy-mm-dd hh24:mi:ss') ";
		
		String[] ddicColumns=ddic.getSql().split(",");
		for(int i=0;i<ddicColumns.length;i++){
			if(ddicColumns[i].toUpperCase().contains("ADDR")){
				ddicColumnsList.add(ddicColumns[i]);
			}
		}
		for(int i=0;i<ddicColumnsList.size();i++){
			boolean isMatch=false;
			String itemStr="";
			if(protocol!=null){
				for(int j=0;j<protocol.getItems().size();j++){
					if(ddicColumnsList.get(i).equalsIgnoreCase("addr"+protocol.getItems().get(j).getAddr())){
						if(protocol.getItems().get(j).getMeaning()!=null && protocol.getItems().get(j).getMeaning().size()>0){
							isMatch=true;
							itemStr=",decode(t2."+ddicColumnsList.get(i);
							for(int k=0;k<protocol.getItems().get(j).getMeaning().size();k++){
								itemStr+=","+protocol.getItems().get(j).getMeaning().get(k).getValue()+",'"+protocol.getItems().get(j).getMeaning().get(k).getMeaning()+"'";
							}
							
							itemStr+=",t2."+ddicColumnsList.get(i)+") as "+ddicColumnsList.get(i);
						}
						break;
					}
				}
			}
			if(isMatch){
				sql+=itemStr;
			}else{
				sql+=",t2."+ddicColumnsList.get(i);
			}
		}
		
		
		sql+= " from "+deviceTableName+" t "
				+ " left outer join "+hisTableName+" t2 on t2.wellid=t.id"
				+ " left outer join tbl_protocolalarminstance t3 on t.alarminstancecode=t3.code"
				+ " left outer join tbl_alarm_unit_conf t4 on t3.alarmunitid=t4.id"
				+ " left outer join tbl_alarm_item2unit_conf t5 on t4.id=t5.unitid and t5.type=3  and  decode(t2.commstatus,1,'在线','离线')=t5.itemname"
				+ " where  t.orgid in ("+orgId+") "
				+ " and t2.acqTime between to_date('"+pager.getStart_date()+"','yyyy-mm-dd') and to_date('"+pager.getEnd_date()+"','yyyy-mm-dd')+1 and t.wellName='"+deviceName+"'"
				+ "  order by t2.acqtime desc";
		
		int maxvalue=pager.getLimit()+pager.getStart();
		String finalSql="select * from   ( select a.*,rownum as rn from ("+sql+" ) a where  rownum <="+maxvalue+") b where rn >"+pager.getStart();
		
		int totals=this.getTotalCountRows(sql);
		List<?> list = this.findCallSql(finalSql);
		result_json.append("{ \"success\":true,\"columns\":"+columns+",");
		result_json.append("\"start_date\":\""+pager.getStart_date()+"\",");
		result_json.append("\"end_date\":\""+pager.getEnd_date()+"\",");
		result_json.append("\"totalCount\":"+totals+",");
		result_json.append("\"totalRoot\":[");
		for(int i=0;i<list.size();i++){
			Object[] obj=(Object[]) list.get(i);
			result_json.append("{\"id\":"+obj[0]+",");
			result_json.append("\"wellName\":\""+obj[1]+"\",");
			result_json.append("\"commStatus\":"+obj[2]+",");
			result_json.append("\"commStatusName\":\""+obj[3]+"\",");
			result_json.append("\"commAlarmLevel\":"+obj[4]+",");
			result_json.append("\"acqTime\":\""+obj[5]+"\",");
			result_json.append("\"details\":\"\",");
			for(int j=0;j<ddicColumnsList.size();j++){
				result_json.append("\""+ddicColumnsList.get(j).replaceAll(" ", "")+"\":\""+obj[6+j]+"\",");
			}
			if(result_json.toString().endsWith(",")){
				result_json.deleteCharAt(result_json.length() - 1);
			}
			result_json.append("},");
		}
		if(result_json.toString().endsWith(",")){
			result_json.deleteCharAt(result_json.length() - 1);
		}
		result_json.append("]");
		result_json.append(",\"AlarmShowStyle\":"+new Gson().toJson(alarmShowStyle)+"}");
		return result_json.toString().replaceAll("\"null\"", "\"\"");
	}
	
	public String getDeviceHistoryExportData(String orgId,String deviceName,String deviceType,Page pager) throws IOException, SQLException{
		StringBuffer result_json = new StringBuffer();
		ModbusProtocolConfig.Protocol protocol=null;
		Map<String, Object> equipmentDriveMap = EquipmentDriveMap.getMapObject();
		if(equipmentDriveMap.size()==0){
			EquipmentDriverServerTask.loadProtocolConfig();
			equipmentDriveMap = EquipmentDriveMap.getMapObject();
		}
		ModbusProtocolConfig modbusProtocolConfig=(ModbusProtocolConfig) equipmentDriveMap.get("modbusProtocolConfig");
		for(int i=0;i<modbusProtocolConfig.getProtocol().size();i++){
			
			if(modbusProtocolConfig.getProtocol().get(i).getDeviceType()==StringManagerUtils.stringToInteger(deviceType)){
				protocol=modbusProtocolConfig.getProtocol().get(i);
				break;
			}
			
		}
		String hisTableName="tbl_pumpacqdata_hist";
		String deviceTableName="tbl_pumpdevice";
		String ddicName="pumpHistoryQuery";
		DataDictionary ddic = null;
		List<String> ddicColumnsList=new ArrayList<String>();
		if(StringManagerUtils.stringToInteger(deviceType)==1){
			hisTableName="tbl_pipelineacqdata_hist";
			deviceTableName="tbl_pipelinedevice";
			ddicName="pipelineHistoryQuery";
		}
		ddic  = dataitemsInfoService.findTableSqlWhereByListFaceId(ddicName);
		
		String sql="select t2.id,t.wellname,t2.commstatus,"
				+ "decode(t2.commstatus,1,'在线','离线') as commStatusName,"
				+ "decode(t5.alarmsign,0,0,null,0,t5.alarmlevel) as commAlarmLevel,"
				+ "to_char(t2.acqtime,'yyyy-mm-dd hh24:mi:ss') ";
		String[] ddicColumns=ddic.getSql().split(",");
		for(int i=0;i<ddicColumns.length;i++){
			if(ddicColumns[i].toUpperCase().contains("ADDR")){
				ddicColumnsList.add(ddicColumns[i]);
			}
		}
		for(int i=0;i<ddicColumnsList.size();i++){
			boolean isMatch=false;
			String itemStr="";
			if(protocol!=null){
				for(int j=0;j<protocol.getItems().size();j++){
					if(ddicColumnsList.get(i).equalsIgnoreCase("addr"+protocol.getItems().get(j).getAddr())){
						if(protocol.getItems().get(j).getMeaning()!=null && protocol.getItems().get(j).getMeaning().size()>0){
							isMatch=true;
							itemStr=",decode(t2."+ddicColumnsList.get(i);
							for(int k=0;k<protocol.getItems().get(j).getMeaning().size();k++){
								itemStr+=","+protocol.getItems().get(j).getMeaning().get(k).getValue()+",'"+protocol.getItems().get(j).getMeaning().get(k).getMeaning()+"'";
							}
							
							itemStr+=",t2."+ddicColumnsList.get(i)+") as "+ddicColumnsList.get(i);
						}
						break;
					}
				}
			}
			if(isMatch){
				sql+=itemStr;
			}else{
				sql+=",t2."+ddicColumnsList.get(i);
			}
		}
		sql+= " from "+deviceTableName+" t "
				+ " left outer join "+hisTableName+" t2 on t2.wellid=t.id"
				+ " left outer join tbl_protocolalarminstance t3 on t.alarminstancecode=t3.code"
				+ " left outer join tbl_alarm_unit_conf t4 on t3.alarmunitid=t4.id"
				+ " left outer join tbl_alarm_item2unit_conf t5 on t4.id=t5.unitid and t5.type=3  and  decode(t2.commstatus,1,'在线','离线')=t5.itemname"
				+ " where  t.orgid in ("+orgId+") "
				+ " and t2.acqTime between to_date('"+pager.getStart_date()+"','yyyy-mm-dd') and to_date('"+pager.getEnd_date()+"','yyyy-mm-dd')+1 and t.wellName='"+deviceName+"'"
				+ "  order by t2.acqtime desc";
		String finalSql=sql;
		List<?> list = this.findCallSql(finalSql);
		result_json.append("[");
		for(int i=0;i<list.size();i++){
			Object[] obj=(Object[]) list.get(i);
			result_json.append("{\"id\":"+obj[0]+",");
			result_json.append("\"wellName\":\""+obj[1]+"\",");
			result_json.append("\"commStatus\":"+obj[2]+",");
			result_json.append("\"commStatusName\":\""+obj[3]+"\",");
			result_json.append("\"commAlarmLevel\":"+obj[4]+",");
			result_json.append("\"acqTime\":\""+obj[5]+"\",");
			result_json.append("\"details\":\"\",");
			for(int j=0;j<ddicColumnsList.size();j++){
				result_json.append("\""+ddicColumnsList.get(j).replaceAll(" ", "")+"\":\""+obj[6+j]+"\",");
			}
			if(result_json.toString().endsWith(",")){
				result_json.deleteCharAt(result_json.length() - 1);
			}
			result_json.append("},");
		}
		if(result_json.toString().endsWith(",")){
			result_json.deleteCharAt(result_json.length() - 1);
		}
		result_json.append("]");
		return result_json.toString().replaceAll("\"null\"", "\"\"");
	}
	
	public String getDeviceHistoryDetailsData(String deviceName,String deviceType,String recordId,String userAccount) throws IOException, SQLException{
		int items=3;
		StringBuffer result_json = new StringBuffer();
		StringBuffer info_json = new StringBuffer();
		Map<String, Object> dataModelMap = DataModelMap.getMapObject();
		AlarmShowStyle alarmShowStyle=(AlarmShowStyle) dataModelMap.get("AlarmShowStyle");
		if(alarmShowStyle==null){
			EquipmentDriverServerTask.initAlarmStyle();
			alarmShowStyle=(AlarmShowStyle) dataModelMap.get("AlarmShowStyle");
		}
		String hisTableName="tbl_pumpacqdata_hist";
		String deviceTableName="tbl_pumpdevice";
		if(StringManagerUtils.stringToInteger(deviceType)==1){
			hisTableName="tbl_pipelineacqdata_hist";
			deviceTableName="tbl_pipelinedevice";
		}
		String itemsSql="select t.wellname,t3.protocol, "
				+ " listagg(t6.itemname, ',') within group(order by t6.groupid,t6.id ) key,"
				+ " listagg(decode(t6.sort,null,9999,t6.sort), ',') within group(order by t6.groupid,t6.id ) sort, "
				+ " listagg(decode(t6.bitindex,null,9999,t6.bitindex), ',') within group(order by t6.groupid,t6.id ) bitindex  "
				+ " from "+deviceTableName+" t,tbl_protocolinstance t2,tbl_acq_unit_conf t3,tbl_acq_group2unit_conf t4,tbl_acq_group_conf t5,tbl_acq_item2group_conf t6 "
				+ " where t.instancecode=t2.code and t2.unitid=t3.id and t3.id=t4.unitid and t4.groupid=t5.id and t5.id=t6.groupid "
				+ " and t.wellname='"+deviceName+"' "
				+ " and decode(t6.showlevel,null,9999,t6.showlevel)>=( select r.showlevel from tbl_role r,tbl_user u where u.user_type=r.role_id and u.user_id='"+userAccount+"' )"
				+ " group by t.wellname,t3.protocol";
		String alarmItemsSql="select t2.itemname,t2.itemcode,t2.itemaddr,t2.type,t2.bitindex,t2.value, "
				+ " t2.upperlimit,t2.lowerlimit,t2.hystersis,t2.delay,decode(t2.alarmsign,0,0,t2.alarmlevel) as alarmlevel "
				+ " from "+deviceTableName+" t, tbl_alarm_item2unit_conf t2,tbl_alarm_unit_conf t3,tbl_protocolalarminstance t4 "
				+ " where t.alarminstancecode=t4.code and t4.alarmunitid=t3.id and t3.id=t2.unitid "
				+ " and t.wellname='"+deviceName+"' "
				+ " order by t2.id";
		
		List<?> itemsList = this.findCallSql(itemsSql);
		List<?> alarmItemsList = this.findCallSql(alarmItemsSql);
		String columns = "[";
		for(int i=1;i<=items;i++){
			columns+= "{ \"header\":\"名称\",\"dataIndex\":\"name"+i+"\",children:[] },"
					+ "{ \"header\":\"变量\",\"dataIndex\":\"value"+i+"\",children:[] }";
			if(i<items){
				columns+=",";
			}
		}
		columns+= "]";
		result_json.append("{ \"success\":true,\"columns\":"+columns+",");
		result_json.append("\"totalRoot\":[");
		info_json.append("[");
		for(int i=0;i<itemsList.size();i++){
			Object[] itemsObj=(Object[]) itemsList.get(i);
			String protocolName=itemsObj[1]+"";
			Map<String, Object> equipmentDriveMap = EquipmentDriveMap.getMapObject();
			if(equipmentDriveMap.size()==0){
				EquipmentDriverServerTask.loadProtocolConfig();
				equipmentDriveMap = EquipmentDriveMap.getMapObject();
			}
			ModbusProtocolConfig modbusProtocolConfig=(ModbusProtocolConfig) equipmentDriveMap.get("modbusProtocolConfig");
			
			ModbusProtocolConfig.Protocol protocol=null;
			for(int j=0;j<modbusProtocolConfig.getProtocol().size();j++){
				if(protocolName.equalsIgnoreCase(modbusProtocolConfig.getProtocol().get(j).getName())){
					protocol=modbusProtocolConfig.getProtocol().get(j);
					break;
				}
			}
			
			if(protocol!=null && StringManagerUtils.isNotNull(itemsObj[2]+"")){
				String acqColumns="";
				String[] itemsArr=(itemsObj[2]+"").split(",");
				String[] itemsSortArr=(itemsObj[3]+"").split(",");
				String[] itemsBitIndexArr=(itemsObj[4]+"").split(",");
				List<ModbusProtocolConfig.Items> protocolItems=new ArrayList<ModbusProtocolConfig.Items>();
				List<ProtocolItemResolutionData> protocolItemResolutionDataList=new ArrayList<ProtocolItemResolutionData>();
				for(int j=0;j<protocol.getItems().size();j++){
					if((!"w".equalsIgnoreCase(protocol.getItems().get(j).getRWType())) 
							&& (StringManagerUtils.existOrNot(itemsArr, protocol.getItems().get(j).getTitle(), false))){
						for(int k=0;k<itemsArr.length;k++){
							if(protocol.getItems().get(j).getTitle().equalsIgnoreCase(itemsArr[k])){
								String column="ADDR"+protocol.getItems().get(j).getAddr();
								String columnName=protocol.getItems().get(j).getTitle();
								protocolItems.add(protocol.getItems().get(j));
								break;
							}
						}
					}
				}
				
				String sql="select t.id,t.wellname,to_char(t2.acqtime,'yyyy-mm-dd hh24:mi:ss'), t2.commstatus,decode(t2.commstatus,1,'在线','离线') as commStatusName,decode(t2.commstatus,1,0,100) as commAlarmLevel ";
				for(int j=0;j<protocolItems.size();j++){
					sql+=",t2.ADDR"+protocolItems.get(j).getAddr();
				}
				sql+= " from "+deviceTableName+" t "
						+ " left outer join "+hisTableName+" t2 on t2.wellid=t.id"
						+ " where 1=1 and t2.id="+recordId;
				List<?> list = this.findCallSql(sql);
				if(list.size()>0){
					int row=1;
					Object[] obj=(Object[]) list.get(0);
					for(int j=0;j<protocolItems.size();j++){
						String columnName=protocolItems.get(j).getTitle();
						String rawColumnName=columnName;
						String value=obj[j+6]+"";
						String rawValue=obj[j+6]+"";
						String addr=protocolItems.get(j).getAddr()+"";
						String column="ADDR"+addr;
						String columnDataType=protocolItems.get(j).getIFDataType();
						String resolutionMode=protocolItems.get(j).getResolutionMode()+"";
						String bitIndex="";
						String unit=protocolItems.get(j).getUnit();
						int sort=9999;
						if(protocolItems.get(j).getResolutionMode()==1||protocolItems.get(j).getResolutionMode()==2){//如果是枚举量
							for(int l=0;l<itemsArr.length;l++){
								if(itemsArr[l].equalsIgnoreCase(protocolItems.get(j).getTitle())){
									sort=StringManagerUtils.stringToInteger(itemsSortArr[l]);
									break;
								}
							}
							if(StringManagerUtils.isNotNull(value)&&protocolItems.get(j).getMeaning()!=null&&protocolItems.get(j).getMeaning().size()>0){
								for(int l=0;l<protocolItems.get(j).getMeaning().size();l++){
									if(StringManagerUtils.stringToFloat(value)==(protocolItems.get(j).getMeaning().get(l).getValue())){
										value=protocolItems.get(j).getMeaning().get(l).getMeaning();
										break;
									}
								}
							}
							ProtocolItemResolutionData protocolItemResolutionData =new ProtocolItemResolutionData(rawColumnName,columnName,value,rawValue,addr,column,columnDataType,resolutionMode,bitIndex,unit,sort);
							protocolItemResolutionDataList.add(protocolItemResolutionData);
						}else if(protocolItems.get(j).getResolutionMode()==0){//如果是开关量
							boolean isMatch=false;
							if(protocolItems.get(j).getMeaning()!=null&&protocolItems.get(j).getMeaning().size()>0){
								String[] valueArr=value.split(",");
								for(int l=0;l<protocolItems.get(j).getMeaning().size();l++){
									isMatch=false;
									columnName=protocolItems.get(j).getMeaning().get(l).getMeaning();
									sort=9999;
									
									for(int n=0;n<itemsArr.length;n++){
										if(itemsArr[n].equalsIgnoreCase(protocolItems.get(j).getTitle()) 
												&&StringManagerUtils.stringToInteger(itemsBitIndexArr[n])==protocolItems.get(j).getMeaning().get(l).getValue()
												){
											sort=StringManagerUtils.stringToInteger(itemsSortArr[n]);
											isMatch=true;
											break;
										}
									}
									if(!isMatch){
										continue;
									}
									if(StringManagerUtils.isNotNull(value)){
										for(int m=0;valueArr!=null&&m<valueArr.length;m++){
											if(m==protocolItems.get(j).getMeaning().get(l).getValue()  ){
												isMatch=true;
												bitIndex=m+"";
												if("bool".equalsIgnoreCase(columnDataType) || "boolean".equalsIgnoreCase(columnDataType)){
													value=("true".equalsIgnoreCase(valueArr[m]) || "1".equalsIgnoreCase(valueArr[m]))?"开":"关";
													rawValue=("true".equalsIgnoreCase(valueArr[m]) || "1".equalsIgnoreCase(valueArr[m]))?"1":"0";
												}else{
													value=valueArr[m];
												}
												
												ProtocolItemResolutionData protocolItemResolutionData =new ProtocolItemResolutionData(rawColumnName,columnName,value,rawValue,addr,column,columnDataType,resolutionMode,bitIndex,unit,sort);
												protocolItemResolutionDataList.add(protocolItemResolutionData);
												break;
											}
										}
									}else{
										for(int m=0;m<itemsArr.length;m++){
											if(itemsArr[m].equalsIgnoreCase(protocolItems.get(j).getTitle()) && itemsBitIndexArr[m].equalsIgnoreCase(protocolItems.get(j).getMeaning().get(l).getValue()+"") ){
												sort=StringManagerUtils.stringToInteger(itemsSortArr[m]);
												break;
											}
										}
										ProtocolItemResolutionData protocolItemResolutionData =new ProtocolItemResolutionData(rawColumnName,columnName,value,rawValue,addr,column,columnDataType,resolutionMode,protocolItems.get(j).getMeaning().get(l).getValue()+"",unit,sort);
										protocolItemResolutionDataList.add(protocolItemResolutionData);
									}
								}
							}else{
								for(int l=0;l<itemsArr.length;l++){
									if(itemsArr[l].equalsIgnoreCase(protocolItems.get(j).getTitle())){
										sort=StringManagerUtils.stringToInteger(itemsSortArr[l]);
										break;
									}
								}
								ProtocolItemResolutionData protocolItemResolutionData =new ProtocolItemResolutionData(rawColumnName,columnName,value,rawValue,addr,column,columnDataType,resolutionMode,bitIndex,unit,sort);
								protocolItemResolutionDataList.add(protocolItemResolutionData);
							}
						}else{//如果是数据量
							for(int l=0;l<itemsArr.length;l++){
								if(itemsArr[l].equalsIgnoreCase(protocolItems.get(j).getTitle())){
									sort=StringManagerUtils.stringToInteger(itemsSortArr[l]);
									break;
								}
							}
							columnName=protocolItems.get(j).getTitle();
							addr=protocolItems.get(j).getAddr()+"";
							column="ADDR"+addr;
							columnDataType=protocolItems.get(j).getIFDataType();
							resolutionMode=protocolItems.get(j).getResolutionMode()+"";
							ProtocolItemResolutionData protocolItemResolutionData =new ProtocolItemResolutionData(rawColumnName,columnName,value,rawValue,addr,column,columnDataType,resolutionMode,bitIndex,unit,sort);
							protocolItemResolutionDataList.add(protocolItemResolutionData);
						} 
					}
					//排序
					Collections.sort(protocolItemResolutionDataList);
					//插入间隔的空项
					List<ProtocolItemResolutionData> finalProtocolItemResolutionDataList=new ArrayList<ProtocolItemResolutionData>();
					for(int j=0;j<protocolItemResolutionDataList.size();j++){
						if(j>0&&protocolItemResolutionDataList.get(j).getSort()<9999
							&&protocolItemResolutionDataList.get(j).getSort()-protocolItemResolutionDataList.get(j-1).getSort()>1
						){
							int def=protocolItemResolutionDataList.get(j).getSort()-protocolItemResolutionDataList.get(j-1).getSort();
							for(int k=1;k<def;k++){
								ProtocolItemResolutionData protocolItemResolutionData =new ProtocolItemResolutionData();
								finalProtocolItemResolutionDataList.add(protocolItemResolutionData);
							}
						}
						finalProtocolItemResolutionDataList.add(protocolItemResolutionDataList.get(j));
					}
					
					
					if(finalProtocolItemResolutionDataList.size()%items==0){
						row=finalProtocolItemResolutionDataList.size()/items+1;
					}else{
						row=finalProtocolItemResolutionDataList.size()/items+2;
					}
					result_json.append("{\"name1\":\""+(obj[1]+":"+obj[2]+" "+obj[4])+"\"},");
					
					for(int j=1;j<row;j++){
						//记录每一行的详细信息
						result_json.append("{");
						for(int k=0;k<items;k++){
							int index=items*(j-1)+k;
							String columnName="";
							String value="";
							String rawValue="";
							String addr="";
							String column="";
							String columnDataType="";
							String resolutionMode="";
							String bitIndex="";
							String unit="";
							int alarmLevel=0;
							if(index<finalProtocolItemResolutionDataList.size()){
								columnName=finalProtocolItemResolutionDataList.get(index).getColumnName();
								value=finalProtocolItemResolutionDataList.get(index).getValue();
								rawValue=finalProtocolItemResolutionDataList.get(index).getRawValue();
								addr=finalProtocolItemResolutionDataList.get(index).getAddr();
								column=finalProtocolItemResolutionDataList.get(index).getColumn();
								columnDataType=finalProtocolItemResolutionDataList.get(index).getColumnDataType();
								resolutionMode=finalProtocolItemResolutionDataList.get(index).getResolutionMode();
								bitIndex=finalProtocolItemResolutionDataList.get(index).getBitIndex();
								unit=finalProtocolItemResolutionDataList.get(index).getUnit();
								for(int l=0;l<alarmItemsList.size();l++){
									Object[] alarmItemObj=(Object[]) alarmItemsList.get(l);
									if(finalProtocolItemResolutionDataList.get(index).getAddr().equals(alarmItemObj[2]+"")){
										int alarmType=StringManagerUtils.stringToInteger(alarmItemObj[3]+"");
										if(alarmType==2 && StringManagerUtils.isNotNull(rawValue)){//数据量报警
											float hystersis=StringManagerUtils.stringToFloat(alarmItemObj[8]+"");
											if((StringManagerUtils.isNotNull(alarmItemObj[6]+"") && StringManagerUtils.stringToFloat(rawValue)>StringManagerUtils.stringToFloat(alarmItemObj[6]+"")+hystersis)
													||(StringManagerUtils.isNotNull(alarmItemObj[7]+"") && StringManagerUtils.stringToFloat(rawValue)<StringManagerUtils.stringToFloat(alarmItemObj[7]+"")-hystersis)
													){
												alarmLevel=StringManagerUtils.stringToInteger(alarmItemObj[10]+"");
												
											}
											break;
										}else if(alarmType==0){//开关量报警
											if(StringManagerUtils.isNotNull(bitIndex)){
												if(bitIndex.equals(alarmItemObj[4]+"") && StringManagerUtils.stringToInteger(rawValue)==StringManagerUtils.stringToInteger(alarmItemObj[5]+"")){
													alarmLevel=StringManagerUtils.stringToInteger(alarmItemObj[10]+"");
												}
											}
										}else if(alarmType==1){//枚举量报警
											if(StringManagerUtils.stringToInteger(rawValue)==StringManagerUtils.stringToInteger(alarmItemObj[5]+"")){
												alarmLevel=StringManagerUtils.stringToInteger(alarmItemObj[10]+"");
											}
										}
									}
								}
							}
//							result_json.append("\"name"+(k+1)+"\":\""+columnName+"\",");
							if(StringManagerUtils.isNotNull(columnName)&&StringManagerUtils.isNotNull(unit)){
								result_json.append("\"name"+(k+1)+"\":\""+(columnName+"("+unit+")")+"\",");
							}else{
								result_json.append("\"name"+(k+1)+"\":\""+columnName+"\",");
							}
							result_json.append("\"value"+(k+1)+"\":\""+value+"\",");
							info_json.append("{\"row\":"+j+",\"col\":"+k+",\"addr\":\""+addr+"\","
									+ "\"columnName\":\""+columnName+"\","
									+ "\"column\":\""+column+"\","
									+ "\"value\":\""+value+"\","
									+ "\"columnDataType\":\""+columnDataType+"\","
									+ "\"resolutionMode\":\""+resolutionMode+"\","
									+ "\"alarmLevel\":"+alarmLevel+"},");
						}
						if(result_json.toString().endsWith(",")){
							result_json.deleteCharAt(result_json.length() - 1);
						}
						result_json.append("},");
					}
					if(result_json.toString().endsWith(",")){
						result_json.deleteCharAt(result_json.length() - 1);
					}
				}
			}
		}
		if(info_json.toString().endsWith(",")){
			info_json.deleteCharAt(info_json.length() - 1);
		}
		info_json.append("]");
		result_json.append("]");
		result_json.append(",\"CellInfo\":"+info_json);
		result_json.append(",\"AlarmShowStyle\":"+new Gson().toJson(alarmShowStyle));
		result_json.append("}");
//		System.out.println(result_json.toString());
		return result_json.toString().replaceAll("null", "");
	}
	
	public String getHistoryQueryCurveData(String deviceName,String deviceType,String startDate,String endDate)throws Exception {
		StringBuffer result_json = new StringBuffer();
		StringBuffer itemsBuff = new StringBuffer();
		StringBuffer curveColorBuff = new StringBuffer();
		String tableName="tbl_pumpacqdata_hist";
		String deviceTableName="tbl_pumpdevice";
		if(StringManagerUtils.stringToInteger(deviceType)==1){
			tableName="tbl_pipelineacqdata_hist";
			deviceTableName="tbl_pipelinedevice";
		}
		String protocolSql="select upper(t3.protocol) from "+deviceTableName+" t,tbl_protocolinstance t2,tbl_acq_unit_conf t3 where t.instancecode=t2.code and t2.unitid=t3.id"
				+ " and  t.wellname='"+deviceName+"' ";
		
		String curveItemsSql="select t6.itemname,t6.bitindex,t6.historycurvecolor "
				+ " from "+deviceTableName+" t,tbl_protocolinstance t2,tbl_acq_unit_conf t3,tbl_acq_group2unit_conf t4,tbl_acq_group_conf t5,tbl_acq_item2group_conf t6 "
				+ " where t.instancecode=t2.code and t2.unitid=t3.id and t3.id=t4.unitid and t4.groupid=t5.id and t5.id=t6.groupid "
				+ " and t.wellname='"+deviceName+"' and t6.historycurve>=0 "
				+ " order by t6.historycurve,t6.sort,t6.id";
		List<?> protocolList = this.findCallSql(protocolSql);
		List<?> curveItemList = this.findCallSql(curveItemsSql);
		String protocolName="";
		String unit="";
		String dataType="";
		int resolutionMode=0;
		List<String> itemNameList=new ArrayList<String>();
		List<String> itemColumnList=new ArrayList<String>();
		List<String> curveColorList=new ArrayList<String>();
		if(protocolList.size()>0){
			protocolName=protocolList.get(0)+"";
			Map<String, Object> equipmentDriveMap = EquipmentDriveMap.getMapObject();
			if(equipmentDriveMap.size()==0){
				EquipmentDriverServerTask.loadProtocolConfig();
				equipmentDriveMap = EquipmentDriveMap.getMapObject();
			}
			ModbusProtocolConfig modbusProtocolConfig=(ModbusProtocolConfig) equipmentDriveMap.get("modbusProtocolConfig");
			if(modbusProtocolConfig!=null&&modbusProtocolConfig.getProtocol()!=null){
				for(int i=0;i<modbusProtocolConfig.getProtocol().size();i++){
					if(protocolName.equalsIgnoreCase(modbusProtocolConfig.getProtocol().get(i).getName())){
						for(int j=0;j<curveItemList.size();j++){
							Object[] itemObj=(Object[]) curveItemList.get(j);
							for(int k=0;k<modbusProtocolConfig.getProtocol().get(i).getItems().size();k++){
								if(modbusProtocolConfig.getProtocol().get(i).getItems().get(k).getTitle().equalsIgnoreCase(itemObj[0]+"")){
									itemColumnList.add("addr"+modbusProtocolConfig.getProtocol().get(i).getItems().get(k).getAddr());
									if(StringManagerUtils.isNotNull(modbusProtocolConfig.getProtocol().get(i).getItems().get(k).getUnit())){
										itemNameList.add(modbusProtocolConfig.getProtocol().get(i).getItems().get(k).getTitle()+"("+modbusProtocolConfig.getProtocol().get(i).getItems().get(k).getUnit()+")");
									}else{
										itemNameList.add(modbusProtocolConfig.getProtocol().get(i).getItems().get(k).getTitle());
									}
									curveColorList.add((itemObj[2]+"").replaceAll("null", ""));
									break;
								}
							}
						}
						break;
					}
				}
			}
		}
		
		itemsBuff.append("[");
		for(int i=0;i<itemNameList.size();i++){
			itemsBuff.append("\""+itemNameList.get(i)+"\",");
		}
		if (itemsBuff.toString().endsWith(",")) {
			itemsBuff.deleteCharAt(itemsBuff.length() - 1);
		}
		itemsBuff.append("]");
		
		curveColorBuff.append("[");
		for(int i=0;i<curveColorList.size();i++){
			curveColorBuff.append("\""+curveColorList.get(i)+"\",");
		}
		if (curveColorBuff.toString().endsWith(",")) {
			curveColorBuff.deleteCharAt(curveColorBuff.length() - 1);
		}
		curveColorBuff.append("]");
		
		result_json.append("{\"deviceName\":\""+deviceName+"\",\"startDate\":\""+startDate+"\",\"endDate\":\""+endDate+"\",\"curveItems\":"+itemsBuff+",\"curveColors\":"+curveColorBuff+",\"list\":[");
		if(itemColumnList.size()>0){
			String columns="";
			for(int i=0;i<itemColumnList.size();i++){
				columns+=","+itemColumnList.get(i);
			}
			String sql="select to_char(t.acqtime,'yyyy-mm-dd hh24:mi:ss') as acqtime"+columns
					+ " from "+tableName +" t,"+deviceTableName+" t2 "
					+ " where t.wellid=t2.id "
					+ " and t.acqtime between to_date('"+startDate+"','yyyy-mm-dd')  and to_date('"+endDate+"','yyyy-mm-dd')+1"
					+ " and t2.wellname='"+deviceName+"' ";
			int total=this.getTotalCountRows(sql);
			int rarefy=total/500+1;
			sql+= " order by t.acqtime";
			
			String finalSql=sql;
			if(rarefy>1){
				finalSql="select acqtime"+columns+" from  (select v.*, rownum as rn from ("+sql+") v ) v2 where mod(rn-1,"+rarefy+")=0";
			}
			List<?> list = this.findCallSql(finalSql);
			for(int i=0;i<list.size();i++){
				Object[] obj=(Object[]) list.get(i);
				result_json.append("{\"acqTime\":\"" + obj[0] + "\",\"data\":[");
				for(int j=1;j<obj.length;j++){
					result_json.append(obj[j]+",");
				}
				if (result_json.toString().endsWith(",")) {
					result_json.deleteCharAt(result_json.length() - 1);
				}
				result_json.append("]},");
			}
			if (result_json.toString().endsWith(",")) {
				result_json.deleteCharAt(result_json.length() - 1);
			}
		}
		result_json.append("]}");
//		System.out.println(result_json.toString());
		return result_json.toString();
	}
}
