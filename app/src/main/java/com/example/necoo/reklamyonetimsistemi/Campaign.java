package com.example.necoo.reklamyonetimsistemi;

import java.util.List;

/**
 * Created by NECOO on 16.05.2019.
 */

public class Campaign {
    private int companyID;
    private String companyName;
    private String companyLocation; //TODO: we can use Location type instead string
    private String campaignCategory;
    private String campaignInfo;
    private String campaignDeadLine;

    public Campaign() {

    }

    public int getCompanyID() {
        return companyID;
    }

    public void setCompanyID(int companyID) {
        this.companyID = companyID;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyLocation() {
        return companyLocation;
    }

    public void setCompanyLocation(String companyLocation) {
        this.companyLocation = companyLocation;
    }

    public String getCampaignCategory() {
        return campaignCategory;
    }

    public void setCampaignCategory(String campaignCategory) {
        this.campaignCategory = campaignCategory;
    }

    public String getCampaignInfo() {
        return campaignInfo;
    }

    public void setCampaignInfo(String campaignInfo) {
        this.campaignInfo = campaignInfo;
    }

    public String getCampaignDeadLine() {
        return campaignDeadLine;
    }

    public void setCampaignDeadLine(String campaignDeadLine) {
        this.campaignDeadLine = campaignDeadLine;
    }




}
