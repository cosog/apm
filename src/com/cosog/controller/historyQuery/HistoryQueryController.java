package com.cosog.controller.historyQuery;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cosog.controller.base.BaseController;
import com.cosog.controller.realTimeMonitoring.RealTimeMonitoringController;
import com.cosog.model.User;
import com.cosog.model.data.DataDictionary;
import com.cosog.service.base.CommonDataService;
import com.cosog.service.data.DataitemsInfoService;
import com.cosog.service.historyQuery.HistoryQueryService;
import com.cosog.utils.Constants;
import com.cosog.utils.Page;
import com.cosog.utils.ParamUtils;
import com.cosog.utils.StringManagerUtils;

@Controller
@RequestMapping("/historyQueryController")
@Scope("prototype")
public class HistoryQueryController extends BaseController  {

	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(RealTimeMonitoringController.class);
	@Autowired
	private HistoryQueryService<?> historyQueryService;
	@Autowired
	private CommonDataService service;
	@Autowired
	private DataitemsInfoService dataitemsInfoService;
	private String limit;
	private String msg = "";
	private String wellName;
	private String deviceName;
	private String deviceType;
	private String deviceTypeStatValue;
	private String commStatusStatValue;
	private String page;
	private String orgId;
	private String startDate;
	private String endDate;
	private int totals;
	
	@RequestMapping("/getHistoryQueryCommStatusStatData")
	public String getHistoryQueryCommStatusStatData() throws Exception {
		String json = "";
		orgId = ParamUtils.getParameter(request, "orgId");
		deviceType = ParamUtils.getParameter(request, "deviceType");
		deviceTypeStatValue = ParamUtils.getParameter(request, "deviceTypeStatValue");
		this.pager = new Page("pagerForm", request);
		User user=null;
		if (!StringManagerUtils.isNotNull(orgId)) {
			HttpSession session=request.getSession();
			user = (User) session.getAttribute("userLogin");
			if (user != null) {
				orgId = "" + user.getUserorgids();
			}
		}
		json = historyQueryService.getHistoryQueryCommStatusStatData(orgId,deviceType,deviceTypeStatValue);
		response.setContentType("application/json;charset="+ Constants.ENCODING_UTF8);
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter pw = response.getWriter();
		pw.print(json);
		pw.flush();
		pw.close();
		return null;
	}
	
	@RequestMapping("/getHistoryQueryDeviceTypeStatData")
	public String getHistoryQueryDeviceTypeStatData() throws Exception {
		String json = "";
		orgId = ParamUtils.getParameter(request, "orgId");
		deviceType = ParamUtils.getParameter(request, "deviceType");
		commStatusStatValue = ParamUtils.getParameter(request, "commStatusStatValue");
		this.pager = new Page("pagerForm", request);
		User user=null;
		if (!StringManagerUtils.isNotNull(orgId)) {
			HttpSession session=request.getSession();
			user = (User) session.getAttribute("userLogin");
			if (user != null) {
				orgId = "" + user.getUserorgids();
			}
		}
		json = historyQueryService.getHistoryQueryDeviceTypeStatData(orgId,deviceType,commStatusStatValue);
		response.setContentType("application/json;charset="+ Constants.ENCODING_UTF8);
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter pw = response.getWriter();
		pw.print(json);
		pw.flush();
		pw.close();
		return null;
	}
	
	@RequestMapping("/getHistoryQueryDeviceList")
	public String getHistoryQueryDeviceList() throws Exception {
		String json = "";
		orgId = ParamUtils.getParameter(request, "orgId");
		deviceName = ParamUtils.getParameter(request, "deviceName");
		deviceType = ParamUtils.getParameter(request, "deviceType");
		commStatusStatValue = ParamUtils.getParameter(request, "commStatusStatValue");
		deviceTypeStatValue = ParamUtils.getParameter(request, "deviceTypeStatValue");
		this.pager = new Page("pagerForm", request);
		User user=null;
		if (!StringManagerUtils.isNotNull(orgId)) {
			HttpSession session=request.getSession();
			user = (User) session.getAttribute("userLogin");
			if (user != null) {
				orgId = "" + user.getUserorgids();
			}
		}
		json = historyQueryService.getHistoryQueryDeviceList(orgId,deviceName,deviceType,commStatusStatValue,deviceTypeStatValue,pager);
		//HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("application/json;charset="
				+ Constants.ENCODING_UTF8);
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter pw = response.getWriter();
		pw.print(json);
		pw.flush();
		pw.close();
		return null;
	}
	
	@RequestMapping("/exportHistoryQueryDeviceListExcel")
	public String exportHistoryQueryDeviceListExcel() throws Exception {
		String json = "";
		orgId = ParamUtils.getParameter(request, "orgId");
		deviceName = java.net.URLDecoder.decode(ParamUtils.getParameter(request, "deviceName"),"utf-8");
		deviceType = ParamUtils.getParameter(request, "deviceType");
		commStatusStatValue = java.net.URLDecoder.decode(ParamUtils.getParameter(request, "commStatusStatValue"),"utf-8");
		deviceTypeStatValue = java.net.URLDecoder.decode(ParamUtils.getParameter(request, "deviceTypeStatValue"),"utf-8");
		
		String heads = java.net.URLDecoder.decode(ParamUtils.getParameter(request, "heads"),"utf-8");
		String fields = ParamUtils.getParameter(request, "fields");
		String fileName = java.net.URLDecoder.decode(ParamUtils.getParameter(request, "fileName"),"utf-8");
		String title = java.net.URLDecoder.decode(ParamUtils.getParameter(request, "title"),"utf-8");
		
		this.pager = new Page("pagerForm", request);
		User user=null;
		if (!StringManagerUtils.isNotNull(orgId)) {
			HttpSession session=request.getSession();
			user = (User) session.getAttribute("userLogin");
			if (user != null) {
				orgId = "" + user.getUserorgids();
			}
		}
		
		json = historyQueryService.getHistoryQueryDeviceListExportData(orgId,deviceName,deviceType,commStatusStatValue,deviceTypeStatValue,pager);
		this.service.exportGridPanelData(response,fileName,title, heads, fields,json);
		response.setContentType("application/json;charset="+ Constants.ENCODING_UTF8);
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter pw = response.getWriter();
		pw.print(json);
		pw.flush();
		pw.close();
		return null;
	}
	
	@RequestMapping("/getDeviceHistoryData")
	public String getDeviceHistoryData() throws Exception {
		String json = "";
		orgId = ParamUtils.getParameter(request, "orgId");
		deviceName = ParamUtils.getParameter(request, "deviceName");
		deviceType = ParamUtils.getParameter(request, "deviceType");
		startDate = ParamUtils.getParameter(request, "startDate");
		endDate = ParamUtils.getParameter(request, "endDate");
		this.pager = new Page("pagerForm", request);
		User user=null;
		if (!StringManagerUtils.isNotNull(orgId)) {
			HttpSession session=request.getSession();
			user = (User) session.getAttribute("userLogin");
			if (user != null) {
				orgId = "" + user.getUserorgids();
			}
		}
		
		String tableName="tbl_pumpacqdata_hist";
		String deviceTableName="tbl_pumpdevice";
		if(StringManagerUtils.stringToInteger(deviceType)==1){
			tableName="tbl_pipelineacqdata_hist";
			deviceTableName="tbl_pipelinedevice";
		}
		if(StringManagerUtils.isNotNull(deviceName)&&!StringManagerUtils.isNotNull(endDate)){
			String sql = " select to_char(max(t.acqTime),'yyyy-mm-dd') from "+tableName+" t where t.wellId=( select t2.id from "+deviceTableName+" t2 where t2.wellName='"+deviceName+"' "+" ) ";
			List list = this.service.reportDateJssj(sql);
			if (list.size() > 0 &&list.get(0)!=null&&!list.get(0).toString().equals("null")) {
				endDate = list.get(0).toString();
			} else {
				endDate = StringManagerUtils.getCurrentTime();
			}
			if(!StringManagerUtils.isNotNull(startDate)){
//				startDate=StringManagerUtils.addDay(StringManagerUtils.stringToDate(endDate),-10);
				startDate=endDate;
			}
		}
		pager.setStart_date(startDate);
		pager.setEnd_date(endDate);
		json = historyQueryService.getDeviceHistoryData(orgId,deviceName,deviceType,pager);
		//HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("application/json;charset="
				+ Constants.ENCODING_UTF8);
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter pw = response.getWriter();
		pw.print(json);
		pw.flush();
		pw.close();
		return null;
	}
	
	@RequestMapping("/exportHistoryQueryDataExcel")
	public String exportHistoryQueryDataExcel() throws Exception {
		String json = "";
		orgId = ParamUtils.getParameter(request, "orgId");
		deviceName = java.net.URLDecoder.decode(ParamUtils.getParameter(request, "deviceName"),"utf-8");
		deviceType = ParamUtils.getParameter(request, "deviceType");
		startDate = ParamUtils.getParameter(request, "startDate");
		endDate = ParamUtils.getParameter(request, "endDate");
		
		String heads = java.net.URLDecoder.decode(ParamUtils.getParameter(request, "heads"),"utf-8");
		String fields = ParamUtils.getParameter(request, "fields");
		String fileName = java.net.URLDecoder.decode(ParamUtils.getParameter(request, "fileName"),"utf-8");
		String title = java.net.URLDecoder.decode(ParamUtils.getParameter(request, "title"),"utf-8");
		
		DataDictionary ddic = null;
		String ddicName="pumpHistoryQuery";
		if(StringManagerUtils.stringToInteger(deviceType)!=0){
			ddicName="pipelineHistoryQuery";
		}
		ddic  = dataitemsInfoService.findTableSqlWhereByListFaceId(ddicName);
		heads=StringUtils.join(ddic.getHeaders(), ",");
		fields=StringUtils.join(ddic.getFields(), ",");
		
		this.pager = new Page("pagerForm", request);
		User user=null;
		if (!StringManagerUtils.isNotNull(orgId)) {
			HttpSession session=request.getSession();
			user = (User) session.getAttribute("userLogin");
			if (user != null) {
				orgId = "" + user.getUserorgids();
			}
		}
		
		String tableName="tbl_pumpacqdata_hist";
		String deviceTableName="tbl_pumpdevice";
		if(StringManagerUtils.stringToInteger(deviceType)==1){
			tableName="tbl_pipelineacqdata_hist";
			deviceTableName="tbl_pipelinedevice";
		}
		if(StringManagerUtils.isNotNull(deviceName)&&!StringManagerUtils.isNotNull(endDate)){
			String sql = " select to_char(max(t.acqTime),'yyyy-mm-dd') from "+tableName+" t where t.wellId=( select t2.id from "+deviceTableName+" t2 where t2.wellName='"+deviceName+"' "+" ) ";
			List list = this.service.reportDateJssj(sql);
			if (list.size() > 0 &&list.get(0)!=null&&!list.get(0).toString().equals("null")) {
				endDate = list.get(0).toString();
			} else {
				endDate = StringManagerUtils.getCurrentTime();
			}
			if(!StringManagerUtils.isNotNull(startDate)){
//				startDate=StringManagerUtils.addDay(StringManagerUtils.stringToDate(endDate),-10);
				startDate=endDate;
			}
		}
		pager.setStart_date(startDate);
		pager.setEnd_date(endDate);
		
		json = historyQueryService.getDeviceHistoryExportData(orgId,deviceName,deviceType,pager);
		this.service.exportGridPanelData(response,fileName,title, heads, fields,json);
		response.setContentType("application/json;charset="+ Constants.ENCODING_UTF8);
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter pw = response.getWriter();
		pw.print(json);
		pw.flush();
		pw.close();
		return null;
	}
	
	@RequestMapping("/getDeviceHistoryDetailsData")
	public String getDeviceHistoryDetailsData() throws Exception {
		String json = "";
		HttpSession session=request.getSession();
		orgId = ParamUtils.getParameter(request, "orgId");
		String recordId = ParamUtils.getParameter(request, "recordId");
		deviceType = ParamUtils.getParameter(request, "deviceType");
		deviceName = ParamUtils.getParameter(request, "deviceName");
		this.pager = new Page("pagerForm", request);
		User user = (User) session.getAttribute("userLogin");
		if(user!=null){
			json = historyQueryService.getDeviceHistoryDetailsData(deviceName,deviceType,recordId,user.getUserId());
		}
		
		//HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("application/json;charset="
				+ Constants.ENCODING_UTF8);
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter pw = response.getWriter();
		pw.print(json);
		pw.flush();
		pw.close();
		return null;
	}
	
	@RequestMapping("/getHistoryQueryCurveData")
	public String getHistoryQueryCurveData() throws Exception {
		String json = "";
		HttpSession session=request.getSession();
		User user = (User) session.getAttribute("userLogin");
		String deviceName = ParamUtils.getParameter(request, "deviceName");
		deviceType = ParamUtils.getParameter(request, "deviceType");
		startDate = ParamUtils.getParameter(request, "startDate");
		endDate = ParamUtils.getParameter(request, "endDate");
		String deviceTableName="tbl_pumpdevice";
		String tableName="tbl_pumpacqdata_hist";
		if(StringManagerUtils.stringToInteger(deviceType)==1){
			deviceTableName="tbl_pipelinedevice";
			tableName="tbl_pipelineacqdata_hist";
		}
		if(!StringManagerUtils.isNotNull(endDate)){
			String sql = " select to_char(max(t.acqTime),'yyyy-mm-dd') from "+tableName+" t where t.wellId=( select t2.id from "+deviceTableName+" t2 where t2.wellName='"+deviceName+"' ) ";
			List list = this.service.reportDateJssj(sql);
			if (list.size() > 0 &&list.get(0)!=null&&!list.get(0).toString().equals("null")) {
				endDate = list.get(0).toString();
			} else {
				endDate = StringManagerUtils.getCurrentTime();
			}
			if(!StringManagerUtils.isNotNull(startDate)){
//				startDate=StringManagerUtils.addDay(StringManagerUtils.stringToDate(endDate),-10);
				startDate=endDate;
			}
		}
		
		
		this.pager = new Page("pagerForm", request);
		json = historyQueryService.getHistoryQueryCurveData(deviceName,deviceType,startDate,endDate);
		//HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("application/json;charset="
				+ Constants.ENCODING_UTF8);
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter pw = response.getWriter();
		pw.print(json);
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
	public String getWellName() {
		return wellName;
	}
	public void setWellName(String wellName) {
		this.wellName = wellName;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public int getTotals() {
		return totals;
	}
	public void setTotals(int totals) {
		this.totals = totals;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getDeviceTypeStatValue() {
		return deviceTypeStatValue;
	}

	public void setDeviceTypeStatValue(String deviceTypeStatValue) {
		this.deviceTypeStatValue = deviceTypeStatValue;
	}

	public String getCommStatusStatValue() {
		return commStatusStatValue;
	}

	public void setCommStatusStatValue(String commStatusStatValue) {
		this.commStatusStatValue = commStatusStatValue;
	}
}
