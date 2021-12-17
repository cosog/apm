package com.cosog.model.gridmodel;

import java.util.List;

public class WellHandsontableChangedData {

	private List<String> delidslist;

    private List<Updatelist> updatelist;

    private List<Updatelist> insertlist;

    public void setDelidslist(List<String> delidslist){
        this.delidslist = delidslist;
    }
    public List<String> getDelidslist(){
        return this.delidslist;
    }
    public void setUpdatelist(List<Updatelist> updatelist){
        this.updatelist = updatelist;
    }
    public List<Updatelist> getUpdatelist(){
        return this.updatelist;
    }
    public void setInsertlist(List<Updatelist> insertlist){
        this.insertlist = insertlist;
    }
    public List<Updatelist> getInsertlist(){
        return this.insertlist;
    }
	
	public static class Updatelist
	{
	    private String id;

	    private String orgName="";

	    private String wellName="";
	    
	    private String applicationScenariosName="";

	    private String instanceName="";
	    
	    private String alarmInstanceName="";

	    private String signInId="";

	    private String slave="";
	    
	    private String videoUrl="";
	    
	    private String sortNum="";

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getOrgName() {
			return orgName;
		}

		public void setOrgName(String orgName) {
			this.orgName = orgName;
		}

		public String getWellName() {
			return wellName;
		}

		public void setWellName(String wellName) {
			this.wellName = wellName;
		}

		public String getVideoUrl() {
			return videoUrl;
		}

		public void setVideoUrl(String videoUrl) {
			this.videoUrl = videoUrl;
		}

		public String getSortNum() {
			return sortNum;
		}

		public void setSortNum(String sortNum) {
			this.sortNum = sortNum;
		}

		public String getSignInId() {
			return signInId;
		}

		public void setSignInId(String signInId) {
			this.signInId = signInId;
		}

		public String getSlave() {
			return slave;
		}

		public void setSlave(String slave) {
			this.slave = slave;
		}

		public String getInstanceName() {
			return instanceName;
		}

		public void setInstanceName(String instanceName) {
			this.instanceName = instanceName;
		}

		public String getAlarmInstanceName() {
			return alarmInstanceName;
		}

		public void setAlarmInstanceName(String alarmInstanceName) {
			this.alarmInstanceName = alarmInstanceName;
		}

		public String getApplicationScenariosName() {
			return applicationScenariosName;
		}

		public void setApplicationScenariosName(String applicationScenariosName) {
			this.applicationScenariosName = applicationScenariosName;
		}
	    
	}
}
