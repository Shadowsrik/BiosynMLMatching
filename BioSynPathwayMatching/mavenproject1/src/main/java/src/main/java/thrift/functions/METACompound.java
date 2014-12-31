/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package thrift.functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author zhos001
 */
public class METACompound {

    public String cmpdKey = "";
    public String metaKey = "";
    public String analSystem = "";
    public String m_area = "";
    public int peakNo = 0;
    public String polarity = "";
    public String RetTime = "";
    
    List<String> chargeList = new ArrayList<String>();
    Map<String,List<String>> pkIntensityList = new HashMap<String, List<String>>();
    Map<String,List<String>> pkMassList = new HashMap<String, List<String>>();
    Map<String,Integer> pkPeakNoList = new HashMap<String, Integer>();
    Map<String,String> pkIntensitiesList = new HashMap<String, String>();
    Map<String,String> pkMzsList = new HashMap<String, String>();
    Map<String,String> pkDeconvoluted_molecular_massList = new HashMap<String, String>();
    Map<String,String> pkResList = new HashMap<String, String>();
    Map<String,String> pkAreaList = new HashMap<String, String>();
    Map<String,String> pkSnList = new HashMap<String, String>();
    
    public Map<String, Integer> getPkPeakNoList() {
        return pkPeakNoList;
    }

    public void setPkPeakNoList(Map<String, Integer> pkPeakNoList) {
        this.pkPeakNoList = pkPeakNoList;
    }

    public Map<String, String> getPkIntensitiesList() {
        return pkIntensitiesList;
    }

    public void setPkIntensitiesList(Map<String, String> pkIntensitiesList) {
        this.pkIntensitiesList = pkIntensitiesList;
    }

    public Map<String, String> getPkMzsList() {
        return pkMzsList;
    }

    public void setPkMzsList(Map<String, String> pkMzsList) {
        this.pkMzsList = pkMzsList;
    }

    public Map<String, String> getPkDeconvoluted_molecular_massList() {
        return pkDeconvoluted_molecular_massList;
    }

    public void setPkDeconvoluted_molecular_massList(Map<String, String> pkDeconvoluted_molecular_massList) {
        this.pkDeconvoluted_molecular_massList = pkDeconvoluted_molecular_massList;
    }

    public Map<String, String> getPkResList() {
        return pkResList;
    }

    public void setPkResList(Map<String, String> pkResList) {
        this.pkResList = pkResList;
    }

    public Map<String, String> getPkAreaList() {
        return pkAreaList;
    }

    public void setPkAreaList(Map<String, String> pkAreaList) {
        this.pkAreaList = pkAreaList;
    }

    public Map<String, String> getPkSnList() {
        return pkSnList;
    }

    public void setPkSnList(Map<String, String> pkSnList) {
        this.pkSnList = pkSnList;
    }

    
    public List<String> getChargeList() {
        return chargeList;
    }

    public void setChargeList(List<String> chargeList) {
        this.chargeList = chargeList;
    }

    public Map<String, List<String>> getPkIntensityList() {
        return pkIntensityList;
    }

    public void setPkIntensityList(Map<String, List<String>> pkIntensityList) {
        this.pkIntensityList = pkIntensityList;
    }

    public Map<String, List<String>> getPkMassList() {
        return pkMassList;
    }

    public void setPkMassList(Map<String, List<String>> pkMassList) {
        this.pkMassList = pkMassList;
    }

    public String getRetTime() {
        return RetTime;
    }

    public void setRetTime(String RetTime) {
        this.RetTime = RetTime;
    }

    public String getPolarity() {
        return polarity;
    }

    public void setPolarity(String polarity) {
        this.polarity = polarity;
    }

    public int getPeakNo() {
        return peakNo;
    }

    public void setPeakNo(int peakNo) {
        this.peakNo = peakNo;
    }

    public String getM_area() {
        return m_area;
    }

    public void setM_area(String m_area) {
        this.m_area = m_area;
    }

    public String getAnalSystem() {
        return analSystem;
    }

    public void setAnalSystem(String analSystem) {
        this.analSystem = analSystem;
    }

    public String getMetaKey() {
        return metaKey;
    }

    public void setMetaKey(String metaKey) {
        this.metaKey = metaKey;
    }

    public String getCmpdKey() {
        return cmpdKey;
    }

    public void setCmpdKey(String cmpdKey) {
        this.cmpdKey = cmpdKey;
    }
}
