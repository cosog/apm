package com.cosog.service.back;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cosog.model.AcquisitionUnitGroup;
import com.cosog.model.MasterAndAuxiliaryDevice;
import com.cosog.model.PipelineDeviceAddInfo;
import com.cosog.model.PumpDeviceAddInfo;
import com.cosog.model.User;
import com.cosog.model.data.DataDictionary;
import com.cosog.model.drive.KafkaConfig;
import com.cosog.model.drive.ModbusProtocolConfig;
import com.cosog.model.gridmodel.AuxiliaryDeviceHandsontableChangedData;
import com.cosog.model.gridmodel.WellGridPanelData;
import com.cosog.model.gridmodel.WellHandsontableChangedData;
import com.cosog.service.base.BaseService;
import com.cosog.service.base.CommonDataService;
import com.cosog.service.data.DataitemsInfoService;
import com.cosog.task.EquipmentDriverServerTask;
import com.cosog.utils.EquipmentDriveMap;
import com.cosog.utils.Page;
import com.cosog.utils.StringManagerUtils;

@Service("wellInformationManagerService")
public class WellInformationManagerService<T> extends BaseService<T> {
	@Autowired
	private CommonDataService service;
	@Autowired
	private DataitemsInfoService dataitemsInfoService;
	
	public String loadWellComboxList(Page pager,String orgId,String wellName,String deviceTypeStr) throws Exception {
		//String orgIds = this.getUserOrgIds(orgId);
		StringBuffer result_json = new StringBuffer();
		StringBuffer sqlCuswhere = new StringBuffer();
		int deviceType=StringManagerUtils.stringToInteger(deviceTypeStr);
		String tableName="tbl_pumpdevice";
		if(deviceType>=200&&deviceType<300){
			tableName="tbl_pipelinedevice";
		}else if(deviceType>=300){
			tableName="tbl_smsdevice";
		}
		
		if(deviceType==1){
			tableName="tbl_pipelinedevice";
		}else if(deviceType==2){
			tableName="tbl_smsdevice";
		}
		
		
		String sql = " select  t.wellName as wellName,t.wellName as dm from  "+tableName+" t  ,tbl_org  g where 1=1 and  t.orgId=g.org_id  and g.org_id in ("
				+ orgId + ")";
		if(StringManagerUtils.isNotNull(deviceTypeStr) && deviceType>=100 && StringManagerUtils.isNotNull(deviceTypeStr) && deviceType<300){
			sql += " and t.deviceType ="+deviceType;
		}
		if (StringManagerUtils.isNotNull(wellName)) {
			sql += " and t.wellName like '%" + wellName + "%'";
		}
		sql += " order by t.sortNum, t.wellName";
		sqlCuswhere.append("select * from   ( select a.*,rownum as rn from (");
		sqlCuswhere.append(""+sql);
		int maxvalue=pager.getLimit()+pager.getStart();
		sqlCuswhere.append(" ) a where  rownum <="+maxvalue+") b");
		sqlCuswhere.append(" where rn >"+pager.getStart());
		String finalsql=sqlCuswhere.toString();
		try {
			int totals=this.getTotalCountRows(sql);
			List<?> list = this.findCallSql(finalsql);
			result_json.append("{\"totals\":"+totals+",\"list\":[{boxkey:\"\",boxval:\"选择全部\"},");
			String get_key = "";
			String get_val = "";
			if (null != list && list.size() > 0) {
				for (Object o : list) {
					Object[] obj = (Object[]) o;
					get_key = obj[0] + "";
					get_val = (String) obj[1];
					result_json.append("{boxkey:\"" + get_key + "\",");
					result_json.append("boxval:\"" + get_val + "\"},");
				}
				if (result_json.toString().endsWith(",")) {
					result_json.deleteCharAt(result_json.length() - 1);
				}
			}
			result_json.append("]}");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result_json.toString();
	}
	
	public String loadDeviceTypeComboxList() throws Exception {
		//String orgIds = this.getUserOrgIds(orgId);
		StringBuffer result_json = new StringBuffer();
		StringBuffer sqlCuswhere = new StringBuffer();
		String sql = "select t.itemvalue,t.itemname from TBL_CODE t where upper(t.itemcode)=upper('deviceType') order by t.itemvalue ";
		
		try {
			int totals=this.getTotalCountRows(sql);
			List<?> list = this.findCallSql(sql);
			result_json.append("{\"totals\":"+totals+",\"list\":[{boxkey:\"\",boxval:\"选择全部\"},");
			String get_key = "";
			String get_val = "";
			if (null != list && list.size() > 0) {
				for (Object o : list) {
					Object[] obj = (Object[]) o;
					get_key = obj[0] + "";
					get_val = (String) obj[1];
					result_json.append("{boxkey:\"" + get_key + "\",");
					result_json.append("boxval:\"" + get_val + "\"},");
				}
				if (result_json.toString().endsWith(",")) {
					result_json.deleteCharAt(result_json.length() - 1);
				}
			}
			result_json.append("]}");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result_json.toString();
	}
	
	public String loadDataDictionaryComboxList(String itemCode) throws Exception {
		//String orgIds = this.getUserOrgIds(orgId);
		StringBuffer result_json = new StringBuffer();
		StringBuffer sqlCuswhere = new StringBuffer();
		String sql = "select t.itemvalue,t.itemname from TBL_CODE t where upper(t.itemcode)=upper('"+itemCode+"') order by t.itemvalue ";
		
		try {
			int totals=this.getTotalCountRows(sql);
			List<?> list = this.findCallSql(sql);
			result_json.append("{\"totals\":"+totals+",\"list\":[{boxkey:\"\",boxval:\"选择全部\"},");
			String get_key = "";
			String get_val = "";
			if (null != list && list.size() > 0) {
				for (Object o : list) {
					Object[] obj = (Object[]) o;
					get_key = obj[0] + "";
					get_val = (String) obj[1];
					result_json.append("{boxkey:\"" + get_key + "\",");
					result_json.append("boxval:\"" + get_val + "\"},");
				}
				if (result_json.toString().endsWith(",")) {
					result_json.deleteCharAt(result_json.length() - 1);
				}
			}
			result_json.append("]}");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result_json.toString();
	}
	
//	public void saveWellEditerGridData(WellHandsontableChangedData wellHandsontableChangedData,String orgId,int deviceType,User user) throws Exception {
//		getBaseDao().saveWellEditerGridData(wellHandsontableChangedData,orgId,deviceType,user);
//	}
	
	public void savePumpDeviceData(WellHandsontableChangedData wellHandsontableChangedData,String orgId,int deviceType,User user) throws Exception {
		getBaseDao().savePumpDeviceData(wellHandsontableChangedData,orgId,deviceType,user);
	}
	
	public void savePipelineDeviceData(WellHandsontableChangedData wellHandsontableChangedData,String orgId,int deviceType,User user) throws Exception {
		getBaseDao().savePipelineDeviceData(wellHandsontableChangedData,orgId,deviceType,user);
	}
	
	public void saveSMSDeviceData(WellHandsontableChangedData wellHandsontableChangedData,String orgId,int deviceType,User user) throws Exception {
		getBaseDao().saveSMSDeviceData(wellHandsontableChangedData,orgId,deviceType,user);
	}
	
	public void deleteMasterAndAuxiliary(final int masterid) throws Exception {
		final String hql = "DELETE MasterAndAuxiliaryDevice u where u.masterid ="+masterid+"";
		getBaseDao().bulkObjectDelete(hql);
	}
	
	public void deleteDeviceAdditionalInfo(final int deviceId,int deviceType) throws Exception {
		String model="PumpDeviceAddInfo";
		if(deviceType>=200&&deviceType<300){
			model="PipelineDeviceAddInfo";
		}
		final String hql = "DELETE "+model+" u where u.wellId ="+deviceId+"";
		getBaseDao().bulkObjectDelete(hql);
	}
	
	public void grantMasterAuxiliaryDevice(MasterAndAuxiliaryDevice r) throws Exception {
		getBaseDao().saveOrUpdateObject(r);
	}
	
	public void saveDeviceAdditionalInfo(PumpDeviceAddInfo r) throws Exception {
		getBaseDao().saveOrUpdateObject(r);
	}
	
	public void saveDeviceAdditionalInfo(PipelineDeviceAddInfo r) throws Exception {
		getBaseDao().saveOrUpdateObject(r);
	}
	
	public void saveAuxiliaryDeviceHandsontableData(AuxiliaryDeviceHandsontableChangedData auxiliaryDeviceHandsontableChangedData) throws Exception {
		getBaseDao().saveAuxiliaryDeviceHandsontableData(auxiliaryDeviceHandsontableChangedData);
	}
	
	public void editWellName(String oldWellName,String newWellName,String orgid) throws Exception {
		getBaseDao().editWellName(oldWellName,newWellName,orgid);
	}
	
	public void editPumpDeviceName(String oldWellName,String newWellName,String orgid) throws Exception {
		getBaseDao().editPumpDeviceName(oldWellName,newWellName,orgid);
	}
	
	public void editPipelineDeviceName(String oldWellName,String newWellName,String orgid) throws Exception {
		getBaseDao().editPipelineDeviceName(oldWellName,newWellName,orgid);
	}
	
	public void editSMSDeviceName(String oldWellName,String newWellName,String orgid) throws Exception {
		getBaseDao().editSMSDeviceName(oldWellName,newWellName,orgid);
	}
	
	public void editAuxiliaryDeviceName(String oldName,String newName) throws Exception {
		getBaseDao().editAuxiliaryDeviceName(oldName,newName);
	}

	public List<T> loadWellInformationID(Class<T> clazz) {
		String queryString = "SELECT u.jlbh,u.jh FROM WellInformation u order by u.jlbh ";
		return getBaseDao().find(queryString);
	}

	public List<T> loadWellOrgInfo() {
		String queryString = "SELECT distinct(o.orgName) as orgName ,o.orgCode FROM WellInformation u ,Org o where u.dwbh=o.org_code  order by o.orgCode";
		return getBaseDao().find(queryString);
	}

	public String showWellTypeTree() throws Exception {
		String sql = "select t.dm as id,t.itemname as text from tbl_code t where t.itemcode='JLX'";
		List<?> list = this.findCallSql(sql);
		StringBuffer result_json = new StringBuffer();
		String get_key = "";
		String get_val = "";
		result_json.append("[");
		if (null != list && list.size() > 0) {
			for (Object o : list) {
				Object[] obj = (Object[]) o;
				get_key = obj[0] + "";
				get_val = obj[1] + "";
				if (get_key.equalsIgnoreCase("100") || get_key.equalsIgnoreCase("200")) {
					 if(get_key.equalsIgnoreCase( "200")){
						result_json.deleteCharAt(result_json.length() - 1);
						result_json.append("]}]} ,");
					}
					result_json.append("{");
					result_json.append("id:'" + get_key + "',");
					result_json.append("text:'" + get_val + "',");
					result_json.append("expanded:true,");
					result_json.append("children:[");
				} else if (get_key.equalsIgnoreCase( "101") || get_key .equalsIgnoreCase("111")) {
					if(get_key .equalsIgnoreCase( "111")){
						result_json.deleteCharAt(result_json.length() - 1);
						result_json.append("]},");
					}
					result_json.append("{");
					if(get_key.equalsIgnoreCase( "101")||get_key.equalsIgnoreCase( "111")){
						result_json.append("id:'" + get_key + "_p',");
					}else{
					    result_json.append("id:'" + get_key + "',");
					}
					result_json.append("text:'" + get_val + "',");
					result_json.append("expanded:true,");
					result_json.append("children:[");
					if(get_key .equalsIgnoreCase( "101")){
						result_json.append("{id:'" + get_key + "',");
						result_json.append("text:'" + get_val + "',");
						result_json.append("leaf:true },");
					}else  if(get_key .equalsIgnoreCase( "111")){
						result_json.append("{id:'" + get_key + "',");
						result_json.append("text:'" + get_val + "',");
						result_json.append("leaf:true },");
					}
				} else if (get_key.startsWith("10") || get_key.startsWith("11")
						|| get_key.startsWith("20")) {
					result_json.append("{id:'" + get_key + "',");
					result_json.append("text:'" + get_val + "',");
					result_json.append("leaf:true },");
				}
			}
			if (result_json.toString().endsWith(",")) {
				result_json.deleteCharAt(result_json.length() - 1);
			}
		}
		result_json.append("]}]");
		String da="100_p";
		da.substring(0, 3);
			
		return result_json.toString();

	}

	/**
	 * <p>
	 * 描述：加载组织类型的下拉菜单数据信息
	 * </p>
	 * 
	 * @return
	 * @throws Exception
	 */
	public String loadSszcdyType(String type) throws Exception {
		StringBuffer result_json = new StringBuffer();
		String sql = "";
		sql = " select t.itemvalue,t.itemname from tbl_code t where  itemcode='SSZCDY'";
		try {
			List<?> list = this.find(sql);
			result_json.append("[");
			String get_key = "";
			String get_val = "";
			if (null != list && list.size() > 0) {
				for (Object o : list) {
					Object[] obj = (Object[]) o;
					get_key = obj[0] + "";
					get_val = (String) obj[1];
					result_json.append("{boxkey:\"" + get_key + "\",");
					result_json.append("boxval:\"" + get_val + "\"},");
				}
				if (result_json.toString().endsWith(",")) {
					result_json.deleteCharAt(result_json.length() - 1);
				}
			}
			result_json.append("]");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result_json.toString();
	}

//	public List<T> fingWellByJhList() throws Exception {
//		String sql = " select  distinct (wellName) from tbl_wellinformation w  order by sortNum ";
//		return this.getBaseDao().find(sql);
//	}

	@SuppressWarnings("rawtypes")
	public String getWellInformationProList(Map map,Page pager,int recordCount) {
		StringBuffer result_json = new StringBuffer();
		StringBuffer instanceDropdownData = new StringBuffer();
		StringBuffer alarmInstanceDropdownData = new StringBuffer();
		StringBuffer SMSInstanceDropdownData = new StringBuffer();
		StringBuffer applicationScenariosDropdownData = new StringBuffer();
		String ddicName="pumpDeviceManager";
		String tableName="viw_pumpdevice";
		int protocolType=0;
		Map<String, Object> equipmentDriveMap = EquipmentDriveMap.getMapObject();
		if(equipmentDriveMap.size()==0){
			EquipmentDriverServerTask.loadProtocolConfig();
			equipmentDriveMap = EquipmentDriveMap.getMapObject();
		}
		String wellInformationName = (String) map.get("wellInformationName");
		int deviceType=StringManagerUtils.stringToInteger((String) map.get("deviceType"));
		String orgId = (String) map.get("orgId");
		String WellInformation_Str = "";
		if (StringManagerUtils.isNotNull(wellInformationName)) {
			WellInformation_Str = " and t.wellname like '%" + wellInformationName+ "%'";
		}
		if(deviceType>=100&&deviceType<200){
			ddicName="pumpDeviceManager";
			tableName="viw_pumpdevice";
			protocolType=0;
		}else if(deviceType>=200&&deviceType<300){
			ddicName="pipelineDeviceManager";
			tableName="viw_pipelinedevice";
			protocolType=1;
		}else if(deviceType>=300){
			ddicName="SMSDeviceManager";
			tableName="viw_smsdevice";
		}
		
		String columns=service.showTableHeadersColumns(ddicName);
		String sql = "select id,orgName,wellName,applicationScenariosName,instanceName,alarmInstanceName,signInId,slave,"
				+ " factorynumber,model,productiondate,deliverydate,commissioningdate,controlcabinetmodel,t.pipelinelength,"
				+ " videoUrl,sortNum"
				+ " from "+tableName+" t where 1=1"
				+ WellInformation_Str;
//		if(deviceType!=2){
//			sql+= " and t.orgid in ("+orgId+" )  ";
//		}
		sql+= " and t.orgid in ("+orgId+" )  ";		
		
		sql+= " and t.devicetype="+deviceType;
		sql+= " order by t.sortnum,t.wellname ";
		String instanceSql="select t.name from tbl_protocolinstance t where t.devicetype="+protocolType+" order by t.sort";
		String alarmInstanceSql="select t.name from tbl_protocolalarminstance t where t.devicetype="+protocolType+" order by t.sort";
		String SMSInstanceSql="select t.name from tbl_protocolsmsinstance t order by t.sort";
		String applicationScenariosSql="select c.itemname from tbl_code c where c.itemcode='APPLICATIONSCENARIOS' order by c.itemvalue";
		
		instanceDropdownData.append("[");
		SMSInstanceDropdownData.append("[");
		alarmInstanceDropdownData.append("[");
		applicationScenariosDropdownData.append("[");
		if(deviceType>=300){
			List<?> SMSInstanceList = this.findCallSql(SMSInstanceSql);
			for(int i=0;i<SMSInstanceList.size();i++){
				SMSInstanceDropdownData.append("'"+SMSInstanceList.get(i)+"',");
			}
			if(SMSInstanceDropdownData.toString().endsWith(",")){
				SMSInstanceDropdownData.deleteCharAt(SMSInstanceDropdownData.length() - 1);
			}
		}else{
			List<?> instanceList = this.findCallSql(instanceSql);
			List<?> alarmInstanceList = this.findCallSql(alarmInstanceSql);
			List<?> applicationScenariosList = this.findCallSql(applicationScenariosSql);
			
			for(int i=0;i<instanceList.size();i++){
				instanceDropdownData.append("'"+instanceList.get(i)+"',");
			}
			if(instanceDropdownData.toString().endsWith(",")){
				instanceDropdownData.deleteCharAt(instanceDropdownData.length() - 1);
			}
			for(int i=0;i<alarmInstanceList.size();i++){
				alarmInstanceDropdownData.append("'"+alarmInstanceList.get(i)+"',");
			}
			if(alarmInstanceDropdownData.toString().endsWith(",")){
				alarmInstanceDropdownData.deleteCharAt(alarmInstanceDropdownData.length() - 1);
			}
			
			for(int i=0;i<applicationScenariosList.size();i++){
				applicationScenariosDropdownData.append("'"+applicationScenariosList.get(i)+"',");
			}
			if(applicationScenariosDropdownData.toString().endsWith(",")){
				applicationScenariosDropdownData.deleteCharAt(applicationScenariosDropdownData.length() - 1);
			}
		}
		instanceDropdownData.append("]");
		alarmInstanceDropdownData.append("]");
		SMSInstanceDropdownData.append("]");
		applicationScenariosDropdownData.append("]");
		
		String json = "";
		
		List<?> list = this.findCallSql(sql);
		
		result_json.append("{\"success\":true,\"totalCount\":"+list.size()+","
				+ "\"instanceDropdownData\":"+instanceDropdownData.toString()+","
				+ "\"alarmInstanceDropdownData\":"+alarmInstanceDropdownData.toString()+","
				+ "\"SMSInstanceDropdownData\":"+SMSInstanceDropdownData.toString()+","
				+ "\"applicationScenariosDropdownData\":"+applicationScenariosDropdownData.toString()+","
				+ "\"columns\":"+columns+",\"totalRoot\":[");
		for(int i=0;i<list.size();i++){
			Object[] obj = (Object[]) list.get(i);
			
			result_json.append("{\"id\":\""+obj[0]+"\",");
			result_json.append("\"orgName\":\""+obj[1]+"\",");
			result_json.append("\"wellName\":\""+obj[2]+"\",");
			result_json.append("\"applicationScenariosName\":\""+obj[3]+"\",");
			result_json.append("\"instanceName\":\""+obj[4]+"\",");
			result_json.append("\"alarmInstanceName\":\""+obj[5]+"\",");
			result_json.append("\"signInId\":\""+obj[6]+"\",");
			result_json.append("\"slave\":\""+obj[7]+"\",");
			
			result_json.append("\"factoryNumber\":\""+obj[8]+"\",");
			result_json.append("\"model\":\""+obj[9]+"\",");
			result_json.append("\"productionDate\":\""+obj[10]+"\",");
			result_json.append("\"deliveryDate\":\""+obj[11]+"\",");
			result_json.append("\"commissioningDate\":\""+obj[12]+"\",");
			result_json.append("\"controlcabinetDodel\":\""+obj[13]+"\",");
			
			result_json.append("\"pipelineLength\":\""+obj[14]+"\",");
			
			result_json.append("\"videoUrl\":\""+obj[15]+"\",");
			result_json.append("\"sortNum\":\""+obj[16]+"\"},");
		}
		for(int i=1;i<=recordCount-list.size();i++){
			result_json.append("{\"jlbh\":\"-99999\",\"id\":\"-99999\"},");
		}
		if(result_json.toString().endsWith(",")){
			result_json.deleteCharAt(result_json.length() - 1);
		}
		result_json.append("]}");
		json=result_json.toString().replaceAll("null", "");
		return json;
	}
	
	@SuppressWarnings("rawtypes")
	public String getPumpDeviceInfoList(Map map,Page pager,int recordCount) {
		StringBuffer result_json = new StringBuffer();
		StringBuffer instanceDropdownData = new StringBuffer();
		StringBuffer alarmInstanceDropdownData = new StringBuffer();
		StringBuffer applicationScenariosDropdownData = new StringBuffer();
		String ddicName="pumpDeviceManager";
		String tableName="viw_pumpdevice";
		int protocolType=0;
		Map<String, Object> equipmentDriveMap = EquipmentDriveMap.getMapObject();
		if(equipmentDriveMap.size()==0){
			EquipmentDriverServerTask.loadProtocolConfig();
			equipmentDriveMap = EquipmentDriveMap.getMapObject();
		}
		String wellInformationName = (String) map.get("wellInformationName");
		int deviceType=StringManagerUtils.stringToInteger((String) map.get("deviceType"));
		String orgId = (String) map.get("orgId");
		String WellInformation_Str = "";
		if (StringManagerUtils.isNotNull(wellInformationName)) {
			WellInformation_Str = " and t.wellname like '%" + wellInformationName+ "%'";
		}
		
		String columns=service.showTableHeadersColumns(ddicName);
		String sql = "select id,orgName,wellName,applicationScenariosName,instanceName,alarmInstanceName,signInId,slave,"
				+ " videoUrl,sortNum"
				+ " from "+tableName+" t where 1=1"
				+ WellInformation_Str;
		sql+= " and t.orgid in ("+orgId+" )";
		sql+= " and t.devicetype="+deviceType;
		sql+= " order by t.sortnum,t.wellname ";
		String instanceSql="select t.name from tbl_protocolinstance t where t.devicetype="+protocolType+" order by t.sort";
		String alarmInstanceSql="select t.name from tbl_protocolalarminstance t where t.devicetype="+protocolType+" order by t.sort";
		String applicationScenariosSql="select c.itemname from tbl_code c where c.itemcode='APPLICATIONSCENARIOS' order by c.itemvalue";
		
		instanceDropdownData.append("[");
		alarmInstanceDropdownData.append("[");
		applicationScenariosDropdownData.append("[");

		List<?> instanceList = this.findCallSql(instanceSql);
		List<?> alarmInstanceList = this.findCallSql(alarmInstanceSql);
		List<?> applicationScenariosList = this.findCallSql(applicationScenariosSql);
		
		for(int i=0;i<instanceList.size();i++){
			instanceDropdownData.append("'"+instanceList.get(i)+"',");
		}
		if(instanceDropdownData.toString().endsWith(",")){
			instanceDropdownData.deleteCharAt(instanceDropdownData.length() - 1);
		}
		for(int i=0;i<alarmInstanceList.size();i++){
			alarmInstanceDropdownData.append("'"+alarmInstanceList.get(i)+"',");
		}
		if(alarmInstanceDropdownData.toString().endsWith(",")){
			alarmInstanceDropdownData.deleteCharAt(alarmInstanceDropdownData.length() - 1);
		}
		
		for(int i=0;i<applicationScenariosList.size();i++){
			applicationScenariosDropdownData.append("'"+applicationScenariosList.get(i)+"',");
		}
		if(applicationScenariosDropdownData.toString().endsWith(",")){
			applicationScenariosDropdownData.deleteCharAt(applicationScenariosDropdownData.length() - 1);
		}
		instanceDropdownData.append("]");
		alarmInstanceDropdownData.append("]");
		applicationScenariosDropdownData.append("]");
		
		String json = "";
		List<?> list = this.findCallSql(sql);
		result_json.append("{\"success\":true,\"totalCount\":"+list.size()+","
				+ "\"instanceDropdownData\":"+instanceDropdownData.toString()+","
				+ "\"alarmInstanceDropdownData\":"+alarmInstanceDropdownData.toString()+","
				+ "\"applicationScenariosDropdownData\":"+applicationScenariosDropdownData.toString()+","
				+ "\"columns\":"+columns+",\"totalRoot\":[");
		for(int i=0;i<list.size();i++){
			Object[] obj = (Object[]) list.get(i);
			
			result_json.append("{\"id\":\""+obj[0]+"\",");
			result_json.append("\"orgName\":\""+obj[1]+"\",");
			result_json.append("\"wellName\":\""+obj[2]+"\",");
			result_json.append("\"applicationScenariosName\":\""+obj[3]+"\",");
			result_json.append("\"instanceName\":\""+obj[4]+"\",");
			result_json.append("\"alarmInstanceName\":\""+obj[5]+"\",");
			result_json.append("\"signInId\":\""+obj[6]+"\",");
			result_json.append("\"slave\":\""+obj[7]+"\",");
			result_json.append("\"videoUrl\":\""+obj[8]+"\",");
			result_json.append("\"sortNum\":\""+obj[9]+"\"},");
		}
		for(int i=1;i<=recordCount-list.size();i++){
			result_json.append("{\"jlbh\":\"-99999\",\"id\":\"-99999\"},");
		}
		if(result_json.toString().endsWith(",")){
			result_json.deleteCharAt(result_json.length() - 1);
		}
		result_json.append("]}");
		json=result_json.toString().replaceAll("null", "");
		return json;
	}
	
	@SuppressWarnings("rawtypes")
	public String getPumpDeviceInfoExportData(Map map,Page pager,int recordCount) {
		StringBuffer result_json = new StringBuffer();
		String tableName="viw_pumpdevice";
		Map<String, Object> equipmentDriveMap = EquipmentDriveMap.getMapObject();
		if(equipmentDriveMap.size()==0){
			EquipmentDriverServerTask.loadProtocolConfig();
			equipmentDriveMap = EquipmentDriveMap.getMapObject();
		}
		String wellInformationName = (String) map.get("wellInformationName");
		int deviceType=StringManagerUtils.stringToInteger((String) map.get("deviceType"));
		String orgId = (String) map.get("orgId");
		String WellInformation_Str = "";
		if (StringManagerUtils.isNotNull(wellInformationName)) {
			WellInformation_Str = " and t.wellname like '%" + wellInformationName+ "%'";
		}
		String sql = "select id,orgName,wellName,applicationScenariosName,instanceName,alarmInstanceName,signInId,slave,"
				+ " videoUrl,sortNum"
				+ " from "+tableName+" t where 1=1"
				+ WellInformation_Str;
		sql+= " and t.orgid in ("+orgId+" )";
		sql+= " and t.devicetype="+deviceType;
		sql+= " order by t.sortnum,t.wellname ";
		
		
		String json = "";
		List<?> list = this.findCallSql(sql);
		result_json.append("[");
		for(int i=0;i<list.size();i++){
			Object[] obj = (Object[]) list.get(i);
			
			result_json.append("{\"id\":\""+obj[0]+"\",");
			result_json.append("\"orgName\":\""+obj[1]+"\",");
			result_json.append("\"wellName\":\""+obj[2]+"\",");
			result_json.append("\"applicationScenariosName\":\""+obj[3]+"\",");
			result_json.append("\"instanceName\":\""+obj[4]+"\",");
			result_json.append("\"alarmInstanceName\":\""+obj[5]+"\",");
			result_json.append("\"signInId\":\""+obj[6]+"\",");
			result_json.append("\"slave\":\""+obj[7]+"\",");
			result_json.append("\"videoUrl\":\""+obj[8]+"\",");
			result_json.append("\"sortNum\":\""+obj[9]+"\"},");
		}
		for(int i=1;i<=recordCount-list.size();i++){
			result_json.append("{\"jlbh\":\"-99999\",\"id\":\"-99999\"},");
		}
		if(result_json.toString().endsWith(",")){
			result_json.deleteCharAt(result_json.length() - 1);
		}
		result_json.append("]");
		json=result_json.toString().replaceAll("null", "");
		return json;
	}
	
	@SuppressWarnings("rawtypes")
	public String getPipeDeviceInfoList(Map map,Page pager,int recordCount) {
		StringBuffer result_json = new StringBuffer();
		StringBuffer instanceDropdownData = new StringBuffer();
		StringBuffer alarmInstanceDropdownData = new StringBuffer();
		StringBuffer applicationScenariosDropdownData = new StringBuffer();
		String ddicName="pipelineDeviceManager";
		String tableName="viw_pipelinedevice";
		int protocolType=1;
		Map<String, Object> equipmentDriveMap = EquipmentDriveMap.getMapObject();
		if(equipmentDriveMap.size()==0){
			EquipmentDriverServerTask.loadProtocolConfig();
			equipmentDriveMap = EquipmentDriveMap.getMapObject();
		}
		String wellInformationName = (String) map.get("wellInformationName");
		int deviceType=StringManagerUtils.stringToInteger((String) map.get("deviceType"));
		String orgId = (String) map.get("orgId");
		String WellInformation_Str = "";
		if (StringManagerUtils.isNotNull(wellInformationName)) {
			WellInformation_Str = " and t.wellname like '%" + wellInformationName+ "%'";
		}
		
		String columns=service.showTableHeadersColumns(ddicName);
		String sql = "select id,orgName,wellName,applicationScenariosName,instanceName,alarmInstanceName,signInId,slave,"
				+ " videoUrl,sortNum"
				+ " from "+tableName+" t where 1=1"
				+ WellInformation_Str;
		sql+= " and t.orgid in ("+orgId+" )  ";		
		
		sql+= " and t.devicetype="+deviceType;
		sql+= " order by t.sortnum,t.wellname ";
		String instanceSql="select t.name from tbl_protocolinstance t where t.devicetype="+protocolType+" order by t.sort";
		String alarmInstanceSql="select t.name from tbl_protocolalarminstance t where t.devicetype="+protocolType+" order by t.sort";
		String applicationScenariosSql="select c.itemname from tbl_code c where c.itemcode='APPLICATIONSCENARIOS' order by c.itemvalue";
		
		instanceDropdownData.append("[");
		alarmInstanceDropdownData.append("[");
		applicationScenariosDropdownData.append("[");

		List<?> instanceList = this.findCallSql(instanceSql);
		List<?> alarmInstanceList = this.findCallSql(alarmInstanceSql);
		List<?> applicationScenariosList = this.findCallSql(applicationScenariosSql);
		
		for(int i=0;i<instanceList.size();i++){
			instanceDropdownData.append("'"+instanceList.get(i)+"',");
		}
		if(instanceDropdownData.toString().endsWith(",")){
			instanceDropdownData.deleteCharAt(instanceDropdownData.length() - 1);
		}
		for(int i=0;i<alarmInstanceList.size();i++){
			alarmInstanceDropdownData.append("'"+alarmInstanceList.get(i)+"',");
		}
		if(alarmInstanceDropdownData.toString().endsWith(",")){
			alarmInstanceDropdownData.deleteCharAt(alarmInstanceDropdownData.length() - 1);
		}
		
		for(int i=0;i<applicationScenariosList.size();i++){
			applicationScenariosDropdownData.append("'"+applicationScenariosList.get(i)+"',");
		}
		if(applicationScenariosDropdownData.toString().endsWith(",")){
			applicationScenariosDropdownData.deleteCharAt(applicationScenariosDropdownData.length() - 1);
		}
	
		instanceDropdownData.append("]");
		alarmInstanceDropdownData.append("]");
		applicationScenariosDropdownData.append("]");
		
		String json = "";
		
		List<?> list = this.findCallSql(sql);
		
		result_json.append("{\"success\":true,\"totalCount\":"+list.size()+","
				+ "\"instanceDropdownData\":"+instanceDropdownData.toString()+","
				+ "\"alarmInstanceDropdownData\":"+alarmInstanceDropdownData.toString()+","
				+ "\"applicationScenariosDropdownData\":"+applicationScenariosDropdownData.toString()+","
				+ "\"columns\":"+columns+",\"totalRoot\":[");
		for(int i=0;i<list.size();i++){
			Object[] obj = (Object[]) list.get(i);
			
			result_json.append("{\"id\":\""+obj[0]+"\",");
			result_json.append("\"orgName\":\""+obj[1]+"\",");
			result_json.append("\"wellName\":\""+obj[2]+"\",");
			result_json.append("\"applicationScenariosName\":\""+obj[3]+"\",");
			result_json.append("\"instanceName\":\""+obj[4]+"\",");
			result_json.append("\"alarmInstanceName\":\""+obj[5]+"\",");
			result_json.append("\"signInId\":\""+obj[6]+"\",");
			result_json.append("\"slave\":\""+obj[7]+"\",");
			result_json.append("\"videoUrl\":\""+obj[8]+"\",");
			result_json.append("\"sortNum\":\""+obj[9]+"\"},");
		}
		for(int i=1;i<=recordCount-list.size();i++){
			result_json.append("{\"jlbh\":\"-99999\",\"id\":\"-99999\"},");
		}
		if(result_json.toString().endsWith(",")){
			result_json.deleteCharAt(result_json.length() - 1);
		}
		result_json.append("]}");
		json=result_json.toString().replaceAll("null", "");
		return json;
	}
	
	@SuppressWarnings("rawtypes")
	public String getPipeDeviceInfoExportData(Map map,Page pager,int recordCount) {
		StringBuffer result_json = new StringBuffer();
		String tableName="viw_pipelinedevice";
		String wellInformationName = (String) map.get("wellInformationName");
		int deviceType=StringManagerUtils.stringToInteger((String) map.get("deviceType"));
		String orgId = (String) map.get("orgId");
		String WellInformation_Str = "";
		if (StringManagerUtils.isNotNull(wellInformationName)) {
			WellInformation_Str = " and t.wellname like '%" + wellInformationName+ "%'";
		}
		String sql = "select id,orgName,wellName,applicationScenariosName,instanceName,alarmInstanceName,signInId,slave,"
				+ " videoUrl,sortNum"
				+ " from "+tableName+" t where 1=1"
				+ WellInformation_Str;
		sql+= " and t.orgid in ("+orgId+" )  ";		
		
		sql+= " and t.devicetype="+deviceType;
		sql+= " order by t.sortnum,t.wellname ";
		
		String json = "";
		
		List<?> list = this.findCallSql(sql);
		
		result_json.append("[");
		for(int i=0;i<list.size();i++){
			Object[] obj = (Object[]) list.get(i);
			
			result_json.append("{\"id\":\""+obj[0]+"\",");
			result_json.append("\"orgName\":\""+obj[1]+"\",");
			result_json.append("\"wellName\":\""+obj[2]+"\",");
			result_json.append("\"applicationScenariosName\":\""+obj[3]+"\",");
			result_json.append("\"instanceName\":\""+obj[4]+"\",");
			result_json.append("\"alarmInstanceName\":\""+obj[5]+"\",");
			result_json.append("\"signInId\":\""+obj[6]+"\",");
			result_json.append("\"slave\":\""+obj[7]+"\",");
			result_json.append("\"videoUrl\":\""+obj[8]+"\",");
			result_json.append("\"sortNum\":\""+obj[9]+"\"},");
		}
		for(int i=1;i<=recordCount-list.size();i++){
			result_json.append("{\"jlbh\":\"-99999\",\"id\":\"-99999\"},");
		}
		if(result_json.toString().endsWith(",")){
			result_json.deleteCharAt(result_json.length() - 1);
		}
		result_json.append("]");
		json=result_json.toString().replaceAll("null", "");
		return json;
	}
	
	@SuppressWarnings("rawtypes")
	public String getSMSDeviceInfoList(Map map,Page pager,int recordCount) {
		StringBuffer result_json = new StringBuffer();
		StringBuffer SMSInstanceDropdownData = new StringBuffer();
		String ddicName="SMSDeviceManager";
		String tableName="viw_smsdevice";
		Map<String, Object> equipmentDriveMap = EquipmentDriveMap.getMapObject();
		if(equipmentDriveMap.size()==0){
			EquipmentDriverServerTask.loadProtocolConfig();
			equipmentDriveMap = EquipmentDriveMap.getMapObject();
		}
		String wellInformationName = (String) map.get("wellInformationName");
		String orgId = (String) map.get("orgId");
		String WellInformation_Str = "";
		if (StringManagerUtils.isNotNull(wellInformationName)) {
			WellInformation_Str = " and t.wellname like '%" + wellInformationName+ "%'";
		}
		
		String columns=service.showTableHeadersColumns(ddicName);
		String sql = "select id,orgName,wellName,instanceName,signInId,sortNum"
				+ " from "+tableName+" t where 1=1"
				+ WellInformation_Str;
		sql+= " and t.orgid in ("+orgId+" )  ";		
		
		sql+= " order by t.sortnum,t.wellname ";
		String SMSInstanceSql="select t.name from tbl_protocolsmsinstance t order by t.sort";

		SMSInstanceDropdownData.append("[");
		List<?> SMSInstanceList = this.findCallSql(SMSInstanceSql);
		for(int i=0;i<SMSInstanceList.size();i++){
			SMSInstanceDropdownData.append("'"+SMSInstanceList.get(i)+"',");
		}
		if(SMSInstanceDropdownData.toString().endsWith(",")){
			SMSInstanceDropdownData.deleteCharAt(SMSInstanceDropdownData.length() - 1);
		}
		SMSInstanceDropdownData.append("]");
		
		String json = "";
		
		List<?> list = this.findCallSql(sql);
		
		result_json.append("{\"success\":true,\"totalCount\":"+list.size()+","
				+ "\"SMSInstanceDropdownData\":"+SMSInstanceDropdownData.toString()+","
				+ "\"columns\":"+columns+",\"totalRoot\":[");
		for(int i=0;i<list.size();i++){
			Object[] obj = (Object[]) list.get(i);
			
			result_json.append("{\"id\":\""+obj[0]+"\",");
			result_json.append("\"orgName\":\""+obj[1]+"\",");
			result_json.append("\"wellName\":\""+obj[2]+"\",");
			result_json.append("\"instanceName\":\""+obj[3]+"\",");
			result_json.append("\"signInId\":\""+obj[4]+"\",");
			result_json.append("\"sortNum\":\""+obj[5]+"\"},");
		}
		for(int i=1;i<=recordCount-list.size();i++){
			result_json.append("{\"jlbh\":\"-99999\",\"id\":\"-99999\"},");
		}
		if(result_json.toString().endsWith(",")){
			result_json.deleteCharAt(result_json.length() - 1);
		}
		result_json.append("]}");
		json=result_json.toString().replaceAll("null", "");
		return json;
	}
	
	@SuppressWarnings("rawtypes")
	public String getSMSDeviceInfoExportData(Map map,Page pager,int recordCount) {
		StringBuffer result_json = new StringBuffer();
		String tableName="viw_smsdevice";
		String wellInformationName = (String) map.get("wellInformationName");
		String orgId = (String) map.get("orgId");
		String WellInformation_Str = "";
		if (StringManagerUtils.isNotNull(wellInformationName)) {
			WellInformation_Str = " and t.wellname like '%" + wellInformationName+ "%'";
		}
		String sql = "select id,orgName,wellName,instanceName,signInId,sortNum"
				+ " from "+tableName+" t where 1=1"
				+ WellInformation_Str;
		sql+= " and t.orgid in ("+orgId+" )  ";		
		
		sql+= " order by t.sortnum,t.wellname ";
		
		String json = "";
		
		List<?> list = this.findCallSql(sql);
		
		result_json.append("[");
		for(int i=0;i<list.size();i++){
			Object[] obj = (Object[]) list.get(i);
			
			result_json.append("{\"id\":\""+obj[0]+"\",");
			result_json.append("\"orgName\":\""+obj[1]+"\",");
			result_json.append("\"wellName\":\""+obj[2]+"\",");
			result_json.append("\"instanceName\":\""+obj[3]+"\",");
			result_json.append("\"signInId\":\""+obj[4]+"\",");
			result_json.append("\"sortNum\":\""+obj[5]+"\"},");
		}
		for(int i=1;i<=recordCount-list.size();i++){
			result_json.append("{\"jlbh\":\"-99999\",\"id\":\"-99999\"},");
		}
		if(result_json.toString().endsWith(",")){
			result_json.deleteCharAt(result_json.length() - 1);
		}
		result_json.append("]");
		json=result_json.toString().replaceAll("null", "");
		return json;
	}
	
	@SuppressWarnings("rawtypes")
	public String doAuxiliaryDeviceShow(Map map,Page pager,String deviceType,int recordCount) {
		StringBuffer result_json = new StringBuffer();
		String ddicName="auxiliaryDeviceManager";
		
		String columns=service.showTableHeadersColumns(ddicName);
		String sql = "select t.id,t.name,decode(t.type,1,'管辅件','泵辅件') as type,t.model,t.remark,t.sort from tbl_auxiliarydevice t where 1=1";
		if(StringManagerUtils.isNotNull(deviceType)){
			sql+= " and t.type="+deviceType;
		}
		sql+= " order by t.sort,t.name";
		
		String json = "";
		
		List<?> list = this.findCallSql(sql);
		
		result_json.append("{\"success\":true,\"totalCount\":"+list.size()+",\"columns\":"+columns+",\"totalRoot\":[");
		for(int i=0;i<list.size();i++){
			Object[] obj = (Object[]) list.get(i);
			
			result_json.append("{\"id\":\""+obj[0]+"\",");
			result_json.append("\"name\":\""+obj[1]+"\",");
			result_json.append("\"type\":\""+obj[2]+"\",");
			result_json.append("\"model\":\""+obj[3]+"\",");
			result_json.append("\"remark\":\""+obj[4]+"\",");
			result_json.append("\"sort\":\""+obj[5]+"\"},");
		}
		for(int i=1;i<=recordCount-list.size();i++){
			result_json.append("{\"jlbh\":\"-99999\",\"id\":\"-99999\"},");
		}
		if(result_json.toString().endsWith(",")){
			result_json.deleteCharAt(result_json.length() - 1);
		}
		result_json.append("]}");
		json=result_json.toString().replaceAll("null", "");
		return json;
	}
	
	@SuppressWarnings("rawtypes")
	public String getAuxiliaryDeviceExportData(Map map,Page pager,String deviceType,int recordCount) {
		StringBuffer result_json = new StringBuffer();
		String sql = "select t.id,t.name,decode(t.type,1,'管辅件','泵辅件') as type,t.model,t.remark,t.sort from tbl_auxiliarydevice t where 1=1";
		if(StringManagerUtils.isNotNull(deviceType)){
			sql+= " and t.type="+deviceType;
		}
		sql+= " order by t.sort,t.name";
		
		String json = "";
		
		List<?> list = this.findCallSql(sql);
		
		result_json.append("[");
		for(int i=0;i<list.size();i++){
			Object[] obj = (Object[]) list.get(i);
			
			result_json.append("{\"id\":\""+obj[0]+"\",");
			result_json.append("\"name\":\""+obj[1]+"\",");
			result_json.append("\"type\":\""+obj[2]+"\",");
			result_json.append("\"model\":\""+obj[3]+"\",");
			result_json.append("\"remark\":\""+obj[4]+"\",");
			result_json.append("\"sort\":\""+obj[5]+"\"},");
		}
		if(result_json.toString().endsWith(",")){
			result_json.deleteCharAt(result_json.length() - 1);
		}
		result_json.append("]");
		json=result_json.toString().replaceAll("null", "");
		return json;
	}
	
	public String getAuxiliaryDevice(String deviceName,String deviceType) {
		StringBuffer result_json = new StringBuffer();
		List<Integer> auxiliaryIdList=new ArrayList<Integer>();
		String columns = "["
				+ "{ \"header\":\"序号\",\"dataIndex\":\"id\",width:50 ,children:[] },"
				+ "{ \"header\":\"名称\",\"dataIndex\":\"name\",width:120 ,children:[] },"
				+ "{ \"header\":\"规格型号\",\"dataIndex\":\"model\",width:80 ,children:[] }"
				+ "]";
		String deviceTableName="tbl_pumpdevice";
		if(StringManagerUtils.stringToInteger(deviceType)>=200 && StringManagerUtils.stringToInteger(deviceType)<300){
			deviceTableName="tbl_pipelinedevice";
		}
		
		
		
		String sql = "select t.id,t.name,decode(t.type,1,'管辅件','泵辅件') as type,t.model,t.remark,t.sort from tbl_auxiliarydevice t where 1=1";
		String auxiliarySql="select t2.auxiliaryid from "+deviceTableName+" t,tbl_auxiliary2master t2 "
				+ " where t.id=t2.masterid and t.devicetype="+deviceType+" and t.wellname='"+deviceName+"'";
		if(StringManagerUtils.stringToInteger(deviceType)>=200 && StringManagerUtils.stringToInteger(deviceType)<300){
			sql+= " and t.type=1";
		}else{
			sql+= " and t.type=0";
		}
		sql+= " order by t.sort,t.name";
		
		String json = "";
		
		List<?> list = this.findCallSql(sql);
		List<?> auxiliaryList = this.findCallSql(auxiliarySql);
		for(int i=0;i<auxiliaryList.size();i++){
			auxiliaryIdList.add(StringManagerUtils.stringToInteger(auxiliaryList.get(i)+""));
		}
		
		result_json.append("{\"success\":true,\"totalCount\":"+list.size()+",\"columns\":"+columns+",\"totalRoot\":[");
		for(int i=0;i<list.size();i++){
			Object[] obj = (Object[]) list.get(i);
			boolean checked=false;
			if(StringManagerUtils.existOrNot(auxiliaryIdList, StringManagerUtils.stringToInteger(obj[0]+""))){
				checked=true;
			}
			result_json.append("{\"checked\":"+checked+",");
			result_json.append("\"id\":\""+(i+1)+"\",");
			result_json.append("\"realId\":\""+obj[0]+"\",");
			result_json.append("\"name\":\""+obj[1]+"\",");
			result_json.append("\"model\":\""+obj[3]+"\"},");
		}
		if(result_json.toString().endsWith(",")){
			result_json.deleteCharAt(result_json.length() - 1);
		}
		result_json.append("]}");
		json=result_json.toString().replaceAll("null", "");
		return json;
	}
	
	public String getDeviceAdditionalInfo(String deviceName,String deviceType) {
		StringBuffer result_json = new StringBuffer();
		List<Integer> auxiliaryIdList=new ArrayList<Integer>();
		String columns = "["
				+ "{ \"header\":\"序号\",\"dataIndex\":\"id\",width:50 ,children:[] },"
				+ "{ \"header\":\"名称\",\"dataIndex\":\"itemName\",width:120 ,children:[] },"
				+ "{ \"header\":\"值\",\"dataIndex\":\"itemValue\",width:120 ,children:[] },"
				+ "{ \"header\":\"单位\",\"dataIndex\":\"itemUnit\",width:80 ,children:[] }"
				+ "]";
		String deviceTableName="tbl_pumpdevice";
		String infoTableName="tbl_pumpdeviceaddinfo";
		if(StringManagerUtils.stringToInteger(deviceType)>=200 && StringManagerUtils.stringToInteger(deviceType)<300){
			deviceTableName="tbl_pipelinedevice";
			infoTableName="tbl_pipelinedeviceaddinfo";
		}
		String sql = "select t2.id,t2.itemname,t2.itemvalue,t2.itemunit "
				+ " from "+deviceTableName+" t,"+infoTableName+" t2 "
				+ " where t.id=t2.wellid and t.wellname='"+deviceName+"' and t.devicetype= "+StringManagerUtils.stringToInteger(deviceType)
				+ " order by t2.id";
		
		List<?> list = this.findCallSql(sql);
		result_json.append("{\"success\":true,\"totalCount\":"+list.size()+",\"columns\":"+columns+",\"totalRoot\":[");
		for(int i=0;i<list.size();i++){
			Object[] obj = (Object[]) list.get(i);
			result_json.append("{\"id\":"+obj[0]+",");
			result_json.append("\"itemName\":\""+obj[1]+"\",");
			result_json.append("\"itemValue\":\""+obj[2]+"\",");
			result_json.append("\"itemUnit\":\""+obj[3]+"\"},");
		}
		for(int i=list.size();i<20;i++){
			result_json.append("{},");
		}
		if(result_json.toString().endsWith(",")){
			result_json.deleteCharAt(result_json.length() - 1);
		}
		result_json.append("]}");
		return result_json.toString().replaceAll("null", "");
	}
	
	public String getAcquisitionUnitList(String protocol){
		StringBuffer result_json = new StringBuffer();
		String unitSql="select t.unit_name from tbl_acq_unit_conf t where 1=1";
		if(StringManagerUtils.isNotNull(protocol)){
			unitSql+=" and t.protocol='"+protocol+"'";
		}
		unitSql+= " order by t.id";
		List<?> unitList = this.findCallSql(unitSql);
		result_json.append("{\"data\":[");
		for(int i=0;i<unitList.size();i++){
			result_json.append("\""+unitList.get(i)+"\",");
		}
		if(result_json.toString().endsWith(",")){
			result_json.deleteCharAt(result_json.length() - 1);
		}
		result_json.append("]}");
		return result_json.toString();
	}
	
	public String exportWellInformationData(Map map,Page pager,int recordCount) {
		StringBuffer result_json = new StringBuffer();
		String ddicName="pumpDeviceManager";
		String wellInformationName = (String) map.get("wellInformationName");
		int deviceType=StringManagerUtils.stringToInteger((String) map.get("deviceType"));
		String orgId = (String) map.get("orgId");
		String WellInformation_Str = "";
		if (StringManagerUtils.isNotNull(wellInformationName)) {
			WellInformation_Str = " and t.wellname like '%" + wellInformationName+ "%'";
		}
		if(deviceType==0){
			ddicName="pumpDeviceManager";
		}else if(deviceType==1){
			ddicName="pipelineDeviceManager";
		}else if(deviceType==2){
			ddicName="SMSDeviceManager";
		}
		String sql = "select id,orgName,wellName,applicationScenariosName,instanceName,alarmInstanceName,signInId,slave,"
				+ " factorynumber,model,productiondate,deliverydate,commissioningdate,controlcabinetmodel,t.pipelinelength,"
				+ " videoUrl,sortNum"
				+ " from viw_wellinformation t where 1=1"
				+ WellInformation_Str;
//		if(deviceType!=2){
//			sql+= " and t.orgid in ("+orgId+" )  ";
//		}
		sql+= " and t.orgid in ("+orgId+" )  ";
				
		sql+= " and t.devicetype="+deviceType;
		sql+= " order by t.sortnum,t.wellname ";
		
		String json = "";
		List<?> list = this.findCallSql(sql);
		
		result_json.append("[");
		for(int i=0;i<list.size();i++){
			Object[] obj = (Object[]) list.get(i);
			
			result_json.append("{\"id\":\""+obj[0]+"\",");
			result_json.append("\"orgName\":\""+obj[1]+"\",");
			result_json.append("\"wellName\":\""+obj[2]+"\",");
			result_json.append("\"applicationScenariosName\":\""+obj[3]+"\",");
			result_json.append("\"instanceName\":\""+obj[4]+"\",");
			result_json.append("\"alarmInstanceName\":\""+obj[5]+"\",");
			result_json.append("\"signInId\":\""+obj[6]+"\",");
			result_json.append("\"slave\":\""+obj[7]+"\",");
			
			result_json.append("\"factoryNumber\":\""+obj[8]+"\",");
			result_json.append("\"model\":\""+obj[9]+"\",");
			result_json.append("\"productionDate\":\""+obj[10]+"\",");
			result_json.append("\"deliveryDate\":\""+obj[11]+"\",");
			result_json.append("\"commissioningDate\":\""+obj[12]+"\",");
			result_json.append("\"controlcabinetDodel\":\""+obj[13]+"\",");
			
			result_json.append("\"pipelineLength\":\""+obj[14]+"\",");
			
			result_json.append("\"videoUrl\":\""+obj[15]+"\",");
			result_json.append("\"sortNum\":\""+obj[16]+"\"},");
		}
		if(result_json.toString().endsWith(",")){
			result_json.deleteCharAt(result_json.length() - 1);
		}
		result_json.append("]");
		json=result_json.toString().replaceAll("null", "");
		return json;
	}

}
