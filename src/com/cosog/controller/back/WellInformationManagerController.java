package com.cosog.controller.back;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cosog.controller.base.BaseController;
import com.cosog.model.MasterAndAuxiliaryDevice;
import com.cosog.model.Org;
import com.cosog.model.PipelineDeviceAddInfo;
import com.cosog.model.PumpDeviceAddInfo;
import com.cosog.model.User;
import com.cosog.model.gridmodel.AuxiliaryDeviceConfig;
import com.cosog.model.gridmodel.AuxiliaryDeviceHandsontableChangedData;
import com.cosog.model.gridmodel.WellGridPanelData;
import com.cosog.model.gridmodel.WellHandsontableChangedData;
import com.cosog.service.back.WellInformationManagerService;
import com.cosog.service.base.CommonDataService;
import com.cosog.task.EquipmentDriverServerTask;
import com.cosog.utils.Constants;
import com.cosog.utils.Page;
import com.cosog.utils.PagingConstants;
import com.cosog.utils.ParamUtils;
import com.cosog.utils.StringManagerUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@RequestMapping("/wellInformationManagerController")
@Scope("prototype")
public class WellInformationManagerController extends BaseController {
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(WellInformationManagerController.class);
	@Autowired
	private WellInformationManagerService<?> wellInformationManagerService;
	@Autowired
	private CommonDataService service;
	private String limit;
	private String msg = "";
	private String wellInformationName;
	private String liftingType;
	private String deviceType;
	private String orgCode;
	private String resCode;
	private String page;
	private String orgId;
	private int totals;
	
	/**
	 * <p>
	 * 描述：实现采出井下拉菜单多级联动方法
	 * </p>
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/loadWellComboxList")
	public String loadWellComboxList() throws Exception {
		this.pager=new Page("pageForm",request);
		String wellName = ParamUtils.getParameter(request, "wellName");
		deviceType= ParamUtils.getParameter(request, "deviceType");
		orgId=ParamUtils.getParameter(request, "orgId");
		User user = null;
		HttpSession session=request.getSession();
		user = (User) session.getAttribute("userLogin");
		if (!StringManagerUtils.isNotNull(orgId)) {
			if (user != null) {
				orgId = "" + user.getUserorgids();
			}
		}
		String json = this.wellInformationManagerService.loadWellComboxList(pager,orgId, wellName,deviceType);
		response.setContentType("application/json;charset=utf-8");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter pw = response.getWriter();
		pw.print(json);
		pw.flush();
		pw.close();
		return null;
	}
	
	@RequestMapping("/loadDeviceTypeComboxList")
	public String loadDeviceTypeComboxList() throws Exception {
		this.pager=new Page("pageForm",request);
		String json = this.wellInformationManagerService.loadDeviceTypeComboxList();
		response.setContentType("application/json;charset=utf-8");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter pw = response.getWriter();
		pw.print(json);
		pw.flush();
		pw.close();
		return null;
	}
	
	@RequestMapping("/loadDataDictionaryComboxList")
	public String loadDataDictionaryComboxList() throws Exception {
		this.pager=new Page("pageForm",request);
		String itemCode = ParamUtils.getParameter(request, "itemCode");
		String json = this.wellInformationManagerService.loadDataDictionaryComboxList(itemCode);
		response.setContentType("application/json;charset=utf-8");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter pw = response.getWriter();
		pw.print(json);
		pw.flush();
		pw.close();
		return null;
	}

	@RequestMapping("/doWellInformationShow")
	public String doWellInformationShow() throws IOException {
		Map<String, Object> map = new HashMap<String, Object>();
		int recordCount =StringManagerUtils.stringToInteger(ParamUtils.getParameter(request, "recordCount"));
		int intPage = Integer.parseInt((page == null || page == "0") ? "1" : page);
		int pageSize = Integer.parseInt((limit == null || limit == "0") ? "20" : limit);
		int offset = (intPage - 1) * pageSize + 1;
		wellInformationName = ParamUtils.getParameter(request, "wellInformationName");
		deviceType= ParamUtils.getParameter(request, "deviceType");
		orgId=ParamUtils.getParameter(request, "orgId");
		User user=null;
		if (!StringManagerUtils.isNotNull(orgId)) {
			HttpSession session=request.getSession();
			user = (User) session.getAttribute("userLogin");
			if (user != null) {
				orgId = "" + user.getUserorgids();
			}
		}
		
		orgCode = ParamUtils.getParameter(request, "orgCode");
		resCode = ParamUtils.getParameter(request, "resCode");
		map.put(PagingConstants.PAGE_NO, intPage);
		map.put(PagingConstants.PAGE_SIZE, pageSize);
		map.put(PagingConstants.OFFSET, offset);
		map.put("wellInformationName", wellInformationName);
		map.put("deviceType", deviceType);
		map.put("orgCode", orgCode);
		map.put("resCode", resCode);
		map.put("orgId", orgId);
		log.debug("intPage==" + intPage + " pageSize===" + pageSize);
		this.pager = new Page("pagerForm", request);
		String json="";
		if(StringManagerUtils.stringToInteger(deviceType)>=100&&StringManagerUtils.stringToInteger(deviceType)<200){
			json = this.wellInformationManagerService.getPumpDeviceInfoList(map, pager,recordCount);
		}else if(StringManagerUtils.stringToInteger(deviceType)>=200&&StringManagerUtils.stringToInteger(deviceType)<300){
			json = this.wellInformationManagerService.getPipeDeviceInfoList(map, pager,recordCount);
		}else if(StringManagerUtils.stringToInteger(deviceType)>=300){
			json = this.wellInformationManagerService.getSMSDeviceInfoList(map, pager,recordCount);
		}
		response.setContentType("application/json;charset=" + Constants.ENCODING_UTF8);
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter pw = response.getWriter();
		pw.print(json);
		pw.flush();
		pw.close();
		return null;
	}
	
	@RequestMapping("/doAuxiliaryDeviceShow")
	public String doAuxiliaryDeviceShow() throws IOException {
		Map<String, Object> map = new HashMap<String, Object>();
		int recordCount =StringManagerUtils.stringToInteger(ParamUtils.getParameter(request, "recordCount"));
		int intPage = Integer.parseInt((page == null || page == "0") ? "1" : page);
		int pageSize = Integer.parseInt((limit == null || limit == "0") ? "20" : limit);
		int offset = (intPage - 1) * pageSize + 1;
		deviceType= ParamUtils.getParameter(request, "deviceType");
		map.put(PagingConstants.PAGE_NO, intPage);
		map.put(PagingConstants.PAGE_SIZE, pageSize);
		map.put(PagingConstants.OFFSET, offset);
		map.put("deviceType", deviceType);
		log.debug("intPage==" + intPage + " pageSize===" + pageSize);
		this.pager = new Page("pagerForm", request);
		String json = this.wellInformationManagerService.doAuxiliaryDeviceShow(map, pager,deviceType,recordCount);
		response.setContentType("application/json;charset=" + Constants.ENCODING_UTF8);
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter pw = response.getWriter();
		pw.print(json);
		pw.flush();
		pw.close();
		return null;
	}
	
	@RequestMapping("/exportAuxiliaryDeviceData")
	public String exportAuxiliaryDeviceData() throws IOException {
		Map<String, Object> map = new HashMap<String, Object>();
		int recordCount =StringManagerUtils.stringToInteger(ParamUtils.getParameter(request, "recordCount"));
		int intPage = Integer.parseInt((page == null || page == "0") ? "1" : page);
		int pageSize = Integer.parseInt((limit == null || limit == "0") ? "20" : limit);
		int offset = (intPage - 1) * pageSize + 1;
		deviceType= ParamUtils.getParameter(request, "deviceType");
		String heads = java.net.URLDecoder.decode(ParamUtils.getParameter(request, "heads"),"utf-8");
		String fields = ParamUtils.getParameter(request, "fields");
		String fileName = java.net.URLDecoder.decode(ParamUtils.getParameter(request, "fileName"),"utf-8");
		String title = java.net.URLDecoder.decode(ParamUtils.getParameter(request, "title"),"utf-8");
		map.put(PagingConstants.PAGE_NO, intPage);
		map.put(PagingConstants.PAGE_SIZE, pageSize);
		map.put(PagingConstants.OFFSET, offset);
		map.put("deviceType", deviceType);
		log.debug("intPage==" + intPage + " pageSize===" + pageSize);
		this.pager = new Page("pagerForm", request);
		String json = this.wellInformationManagerService.getAuxiliaryDeviceExportData(map, pager,deviceType,recordCount);
		this.service.exportGridPanelData(response,fileName,title, heads, fields,json);
		response.setContentType("application/json;charset=" + Constants.ENCODING_UTF8);
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter pw = response.getWriter();
		pw.print(json);
		pw.flush();
		pw.close();
		return null;
	}
	
	@RequestMapping("/getAuxiliaryDevice")
	public String getAuxiliaryDevice() throws IOException {
		Map<String, Object> map = new HashMap<String, Object>();
		String deviceName= ParamUtils.getParameter(request, "deviceName");
		deviceType= ParamUtils.getParameter(request, "deviceType");
		this.pager = new Page("pagerForm", request);
		String json = this.wellInformationManagerService.getAuxiliaryDevice(deviceName,deviceType);
		response.setContentType("application/json;charset=" + Constants.ENCODING_UTF8);
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter pw = response.getWriter();
		pw.print(json);
		pw.flush();
		pw.close();
		return null;
	}
	
	@RequestMapping("/getDeviceAdditionalInfo")
	public String getDeviceAdditionalInfo() throws IOException {
		Map<String, Object> map = new HashMap<String, Object>();
		String deviceName= ParamUtils.getParameter(request, "deviceName");
		deviceType= ParamUtils.getParameter(request, "deviceType");
		this.pager = new Page("pagerForm", request);
		String json = this.wellInformationManagerService.getDeviceAdditionalInfo(deviceName,deviceType);
		response.setContentType("application/json;charset=" + Constants.ENCODING_UTF8);
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter pw = response.getWriter();
		pw.print(json);
		pw.flush();
		pw.close();
		return null;
	}
	
	@RequestMapping("/getAcquisitionUnitList")
	public String getAcquisitionUnitList() throws Exception{
		String protocol = ParamUtils.getParameter(request, "protocol");
		String json = this.wellInformationManagerService.getAcquisitionUnitList(protocol);
		response.setContentType("application/json;charset=" + Constants.ENCODING_UTF8);
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter pw = response.getWriter();
		pw.print(json);
		pw.flush();
		pw.close();
		return null;
	}
	
	@RequestMapping("/exportWellInformationData")
	public String exportWellInformationData() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		int recordCount =StringManagerUtils.stringToInteger(ParamUtils.getParameter(request, "recordCount"));
		int intPage = Integer.parseInt((page == null || page == "0") ? "1" : page);
		int pageSize = Integer.parseInt((limit == null || limit == "0") ? "20" : limit);
		int offset = (intPage - 1) * pageSize + 1;
		//wellInformationName = new String(wellInformationName.getBytes("iso-8859-1"), "utf-8");
//		String orgId=this.findCurrentUserOrgIdInfo("");
		wellInformationName = java.net.URLDecoder.decode(ParamUtils.getParameter(request, "wellInformationName"),"utf-8");
		deviceType= ParamUtils.getParameter(request, "deviceType");
		String heads = java.net.URLDecoder.decode(ParamUtils.getParameter(request, "heads"),"utf-8");
		String fields = ParamUtils.getParameter(request, "fields");
		String fileName = java.net.URLDecoder.decode(ParamUtils.getParameter(request, "fileName"),"utf-8");
		String title = java.net.URLDecoder.decode(ParamUtils.getParameter(request, "title"),"utf-8");
		orgId=ParamUtils.getParameter(request, "orgId");
		User user=null;
		if (!StringManagerUtils.isNotNull(orgId)) {
			HttpSession session=request.getSession();
			user = (User) session.getAttribute("userLogin");
			if (user != null) {
				orgId = "" + user.getUserorgids();
			}
		}
		
		orgCode = ParamUtils.getParameter(request, "orgCode");
		resCode = ParamUtils.getParameter(request, "resCode");
		map.put(PagingConstants.PAGE_NO, intPage);
		map.put(PagingConstants.PAGE_SIZE, pageSize);
		map.put(PagingConstants.OFFSET, offset);
		map.put("wellInformationName", wellInformationName);
		map.put("deviceType", deviceType);
		map.put("orgCode", orgCode);
		map.put("resCode", resCode);
		map.put("orgId", orgId);
		log.debug("intPage==" + intPage + " pageSize===" + pageSize);
		this.pager = new Page("pagerForm", request);// 新疆分页Page 工具类
		String json="[]";
		if(StringManagerUtils.stringToInteger(deviceType)>=100&&StringManagerUtils.stringToInteger(deviceType)<200){
			json = this.wellInformationManagerService.getPumpDeviceInfoExportData(map, pager,recordCount);
		}else if(StringManagerUtils.stringToInteger(deviceType)>=200&&StringManagerUtils.stringToInteger(deviceType)<300){
			json = this.wellInformationManagerService.getPipeDeviceInfoExportData(map, pager,recordCount);
		}else if(StringManagerUtils.stringToInteger(deviceType)>=300){
			json = this.wellInformationManagerService.getSMSDeviceInfoExportData(map, pager,recordCount);
		}
		
		
		this.service.exportGridPanelData(response,fileName,title, heads, fields,json);
		return null;
	}

	@RequestMapping("/loadWellOrgInfo")
	public String loadWellOrgInfo() throws Exception {
		List<?> list = this.wellInformationManagerService.loadWellOrgInfo();
		log.debug("loadWellOrgInfo list==" + list.size());
		Org op = null;
		List<Org> olist = new ArrayList<Org>();
		for (int i = 0; i < list.size(); i++) {
			// 使用对象数组
			Object[] objArray = (Object[]) list.get(i);
			// 最后使用forEach迭代obj对象
			op = new Org();
			op.setOrgCode(objArray[0].toString());
			op.setOrgName(objArray[1].toString());
			olist.add(op);
		}
		Gson g = new Gson();
		String json = g.toJson(olist);
		//HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("application/json;charset=utf-8");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter pw = response.getWriter();
		pw.print(json);
		pw.flush();
		pw.close();
		return null;
	}

	@RequestMapping("/showWellTypeTree")
	public String showWellTypeTree() throws Exception {
		String json = this.wellInformationManagerService.showWellTypeTree();
		//HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("application/json;charset=utf-8");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter pw = response.getWriter();
		pw.print(json);
		log.warn("jh json is ==" + json);
		pw.flush();
		pw.close();
		return null;
	}
	
	
	/**
	 * <p>
	 * 描述：设备基本信息Handsontable表格编辑数据保存
	 * </p>
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("static-access")
	@RequestMapping("/saveWellHandsontableData")
	public String saveWellHandsontableData() throws Exception {
		HttpSession session=request.getSession();
		User user = (User) session.getAttribute("userLogin");
		String data = ParamUtils.getParameter(request, "data").replaceAll("&nbsp;", "").replaceAll(" ", "").replaceAll("null", "");
		String deviceAuxiliaryData = ParamUtils.getParameter(request, "deviceAuxiliaryData").replaceAll("&nbsp;", "").replaceAll(" ", "").replaceAll("null", "");
		String orgId = ParamUtils.getParameter(request, "orgId");
		deviceType = ParamUtils.getParameter(request, "deviceType");
		Gson gson = new Gson();
		String deviceTableName="tbl_pumpdevice";
		java.lang.reflect.Type type = new TypeToken<WellHandsontableChangedData>() {}.getType();
		WellHandsontableChangedData wellHandsontableChangedData=gson.fromJson(data, type);
		if(StringManagerUtils.stringToInteger(deviceType)>=100&&StringManagerUtils.stringToInteger(deviceType)<200){
			deviceTableName="tbl_pumpdevice";
			this.wellInformationManagerService.savePumpDeviceData(wellHandsontableChangedData,orgId,StringManagerUtils.stringToInteger(deviceType),user);
		}else if(StringManagerUtils.stringToInteger(deviceType)>=200&&StringManagerUtils.stringToInteger(deviceType)<300){
			deviceTableName="tbl_pipelinedevice";
			this.wellInformationManagerService.savePipelineDeviceData(wellHandsontableChangedData,orgId,StringManagerUtils.stringToInteger(deviceType),user);
		}else if(StringManagerUtils.stringToInteger(deviceType)>=300){
			this.wellInformationManagerService.saveSMSDeviceData(wellHandsontableChangedData,orgId,StringManagerUtils.stringToInteger(deviceType),user);
		}
		
		//处理辅助设备
		if(StringManagerUtils.stringToInteger(deviceType)<300){
			type = new TypeToken<AuxiliaryDeviceConfig>() {}.getType();
			AuxiliaryDeviceConfig auxiliaryDeviceConfig=gson.fromJson(deviceAuxiliaryData, type);
			if(auxiliaryDeviceConfig!=null){
				String sql="select t.id from "+deviceTableName+" t "
						+ " where t.orgid in("+orgId+") and t.devicetype="+auxiliaryDeviceConfig.getDeviceType()+" and t.wellname='"+auxiliaryDeviceConfig.getDeviceName()+"'";
				List list = this.service.findCallSql(sql);
				if(list.size()>0&&StringManagerUtils.isInteger(list.get(0)+"")){
					int deviceId=StringManagerUtils.stringToInteger(list.get(0)+"");
					this.wellInformationManagerService.deleteMasterAndAuxiliary(deviceId);
					if(auxiliaryDeviceConfig.getAuxiliaryDevice()!=null&&auxiliaryDeviceConfig.getAuxiliaryDevice().size()>0){
						for(int i=0;i<auxiliaryDeviceConfig.getAuxiliaryDevice().size();i++){
							MasterAndAuxiliaryDevice masterAndAuxiliaryDevice=new MasterAndAuxiliaryDevice();
							masterAndAuxiliaryDevice.setMasterid(deviceId);
							masterAndAuxiliaryDevice.setAuxiliaryid(auxiliaryDeviceConfig.getAuxiliaryDevice().get(i));
							masterAndAuxiliaryDevice.setMatrix("0,0,0");
							this.wellInformationManagerService.grantMasterAuxiliaryDevice(masterAndAuxiliaryDevice);
						}
					}
					
					this.wellInformationManagerService.deleteDeviceAdditionalInfo(deviceId,StringManagerUtils.stringToInteger(deviceType));
					if(auxiliaryDeviceConfig.getAdditionalInfoList()!=null&&auxiliaryDeviceConfig.getAdditionalInfoList().size()>0){
						for(int i=0;i<auxiliaryDeviceConfig.getAdditionalInfoList().size();i++){
							if(StringManagerUtils.stringToInteger(deviceType)>=100&&StringManagerUtils.stringToInteger(deviceType)<200){
								PumpDeviceAddInfo pumpDeviceAddInfo=new PumpDeviceAddInfo();
								pumpDeviceAddInfo.setWellId(deviceId);
								pumpDeviceAddInfo.setItemName(auxiliaryDeviceConfig.getAdditionalInfoList().get(i).getItemName());
								pumpDeviceAddInfo.setItemValue(auxiliaryDeviceConfig.getAdditionalInfoList().get(i).getItemValue());
								pumpDeviceAddInfo.setItemUnit(auxiliaryDeviceConfig.getAdditionalInfoList().get(i).getItemUnit());
								this.wellInformationManagerService.saveDeviceAdditionalInfo(pumpDeviceAddInfo);
							}else if(StringManagerUtils.stringToInteger(deviceType)>=200&&StringManagerUtils.stringToInteger(deviceType)<300){
								PipelineDeviceAddInfo pipelineDeviceAddInfo=new PipelineDeviceAddInfo();
								pipelineDeviceAddInfo.setWellId(deviceId);
								pipelineDeviceAddInfo.setItemName(auxiliaryDeviceConfig.getAdditionalInfoList().get(i).getItemName());
								pipelineDeviceAddInfo.setItemValue(auxiliaryDeviceConfig.getAdditionalInfoList().get(i).getItemValue());
								pipelineDeviceAddInfo.setItemUnit(auxiliaryDeviceConfig.getAdditionalInfoList().get(i).getItemUnit());
								this.wellInformationManagerService.saveDeviceAdditionalInfo(pipelineDeviceAddInfo);
							}
						}
					}
				}
			}
		}
		
		EquipmentDriverServerTask.LoadDeviceCommStatus();
		String json ="{success:true}";
		response.setContentType("application/json;charset=utf-8");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter pw = response.getWriter();
		pw.print(json);
		log.warn("jh json is ==" + json);
		pw.flush();
		pw.close();
		return null;
	}
	
	/**
	 * <p>
	 * 描述：辅件设备基本信息Handsontable表格编辑数据保存
	 * </p>
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("static-access")
	@RequestMapping("/saveAuxiliaryDeviceHandsontableData")
	public String saveAuxiliaryDeviceHandsontableData() throws Exception {
		HttpSession session=request.getSession();
		String data = ParamUtils.getParameter(request, "data").replaceAll("&nbsp;", "").replaceAll(" ", "").replaceAll("null", "");
		Gson gson = new Gson();
		java.lang.reflect.Type type = new TypeToken<AuxiliaryDeviceHandsontableChangedData>() {}.getType();
		AuxiliaryDeviceHandsontableChangedData auxiliaryDeviceHandsontableChangedData=gson.fromJson(data, type);
		this.wellInformationManagerService.saveAuxiliaryDeviceHandsontableData(auxiliaryDeviceHandsontableChangedData);
		String json ="{success:true}";
		response.setContentType("application/json;charset=utf-8");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter pw = response.getWriter();
		pw.print(json);
		log.warn("jh json is ==" + json);
		pw.flush();
		pw.close();
		return null;
	}
	
	@SuppressWarnings("static-access")
	@RequestMapping("/editWellName")
	public String editWellName() throws Exception {
		HttpSession session=request.getSession();
		User user = (User) session.getAttribute("userLogin");
		String orgid=user.getUserorgids();
		String data = ParamUtils.getParameter(request, "data").replaceAll("&nbsp;", "");
		deviceType = ParamUtils.getParameter(request, "deviceType");
		JSONObject jsonObject = JSONObject.fromObject("{\"data\":"+data+"}");//解析数据
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		for(int i=0;i<jsonArray.size();i++){
			JSONObject everydata = JSONObject.fromObject(jsonArray.getString(i));
			String oldWellName=everydata.getString("oldWellName");
			String newWellName=everydata.getString("newWellName");
			
			if(StringManagerUtils.stringToInteger(deviceType)>=100&&StringManagerUtils.stringToInteger(deviceType)<200){
				this.wellInformationManagerService.editPumpDeviceName(oldWellName,newWellName,orgid);
			}else if(StringManagerUtils.stringToInteger(deviceType)>=200&&StringManagerUtils.stringToInteger(deviceType)<300){
				this.wellInformationManagerService.editPipelineDeviceName(oldWellName,newWellName,orgid);
			}else if(StringManagerUtils.stringToInteger(deviceType)>=300){
				this.wellInformationManagerService.editSMSDeviceName(oldWellName,newWellName,orgid);
			}
			
			
		}
		EquipmentDriverServerTask.LoadDeviceCommStatus();
		String json ="{success:true}";
		response.setContentType("application/json;charset=utf-8");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter pw = response.getWriter();
		pw.print(json);
		log.warn("jh json is ==" + json);
		pw.flush();
		pw.close();
		return null;
	}
	
	@SuppressWarnings("static-access")
	@RequestMapping("/editAuxiliaryDeviceName")
	public String editAuxiliaryDeviceName() throws Exception {
		HttpSession session=request.getSession();
		User user = (User) session.getAttribute("userLogin");
		String data = ParamUtils.getParameter(request, "data").replaceAll("&nbsp;", "");
		JSONObject jsonObject = JSONObject.fromObject("{\"data\":"+data+"}");//解析数据
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		for(int i=0;i<jsonArray.size();i++){
			JSONObject everydata = JSONObject.fromObject(jsonArray.getString(i));
			String oldName=everydata.getString("oldName");
			String newName=everydata.getString("newName");
			this.wellInformationManagerService.editAuxiliaryDeviceName(oldName,newName);
		}
		String json ="{success:true}";
		response.setContentType("application/json;charset=utf-8");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter pw = response.getWriter();
		pw.print(json);
		log.warn("jh json is ==" + json);
		pw.flush();
		pw.close();
		return null;
	}
	
	
	/**
	 * <p>
	 * 描述：获取角色类型的下拉菜单数据信息
	 * </p>
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/loadSszcdyType")
	public String loadSszcdyType() throws Exception {

		String type = ParamUtils.getParameter(request, "type");
		String json = this.wellInformationManagerService.loadSszcdyType(type);
		//HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("application/json;charset=utf-8");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter pw = response.getWriter();
		pw.print(json);
		log.warn("jh json is ==" + json);
		pw.flush();
		pw.close();
		return null;
	}

	

	public String getLimit() {
		return limit;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getWellInformationName() {
		return wellInformationName;
	}

	public void setWellInformationName(String wellInformationName) {
		this.wellInformationName = wellInformationName;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public int getTotals() {
		return totals;
	}

	public void setTotals(int totals) {
		this.totals = totals;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getResCode() {
		return resCode;
	}

	public void setResCode(String resCode) {
		this.resCode = resCode;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getLiftingType() {
		return liftingType;
	}

	public void setLiftingType(String liftingType) {
		this.liftingType = liftingType;
	}

	public String getDevicetype() {
		return deviceType;
	}

	public void setDevicetype(String deviceType) {
		this.deviceType = deviceType;
	}

}
