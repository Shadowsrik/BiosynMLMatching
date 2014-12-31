/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thrift.functions;

/**
 *
 * @author zhos001
 */
public class Compound {

    public  int count = 0;

    public  void setCount(int count) {
        this.count = count;
    }

    public  int getCount() {
        return count;
    }

    public  boolean insert = true;
    public  boolean isTAscore = false;
    public  boolean isArea = false;
    public  boolean isIntensity = false;
    public  boolean isMassDev = false;
    public  boolean isMSigma = false;
    public  String Flag = "";
    public  String SCRN_id = "";
    public  String SCRN_obs = "";
    public  String s_MxID = "";
    public  String c_id = "";
    public  String TAcmpd_nr = "";
    public  String c_formula = "";
    public  String c_name = "";
    public  String c_iontype = "";
    public  String charge = "";
    public  String ionmass_meas = "";
    public  String ionmass_calc = "";
    public  String c_ionformula = "";
    public  String c_iontype_explain = "";
    public  String ret_meas = "";
    public  String ret_exp = "";
    public  String err_ppm = "";
    public  String err_mda = "";
    public  String Sigmafit = "";
    public  String area = "";
    public  String intensity = "";
    public  String algorithm = "";
    public  String TAscore = "";
    public  String ret_diff = "";
    public  String LCMSKey = "";
    public  String c_AnalyticalSystemID = "";
    public  String s_key = "";
    public  String f_id = "";
    public  String M1 = "";
    public  String M2 = "";
    public  String M3 = "";
    public  String M4 = "";
    public  String M5 = "";
    public  String I1 = "";
    public  String I2 = "";
    public  String I3 = "";
    public  String I4 = "";
    public  String I5 = "";
    public  String Masses = "";
    public  String Intensities = "";
    public  String checkTAScore = "";
    public  String checkArea = "";
    public  String checkIntensity = "";
    public  String checkMassDev = "";
    public  String checkmSigma = "";
    
    public  String exceptionMessageLogs = "";

    public  String getExceptionMessageLogs() {
        return exceptionMessageLogs;
    }

    public  void setExceptionMessageLogs(String exceptionMessageLogs) {
        this.exceptionMessageLogs = exceptionMessageLogs;
    }

    /*public  void init() {
        insert = true;
        isTAscore = false;
        isArea = false;
        isIntensity = false;
        isMassDev = false;
        isMSigma = false;
        exceptionMessageLogs = "";
        Flag = "";
        SCRN_id = "";
        SCRN_obs = "";
        s_MxID = "";
        c_id = "";
        TAcmpd_nr = "";
        c_formula = "";
        c_name = "";
        c_iontype = "";
        charge = "";
        ionmass_meas = "";
        ionmass_calc = "";
        c_ionformula = "";
        c_iontype_explain = "";
        ret_meas = "";
        ret_exp = "";
        err_ppm = "";
        err_mda = "";
        Sigmafit = "";
        area = "";
        intensity = "";
        algorithm = "";
        TAscore = "";
        ret_diff = "";
        LCMSKey = "";
        c_AnalyticalSystemID = "";
        s_key = "";
        f_id = "";
        M1 = "";
        M2 = "";
        M3 = "";
        M4 = "";
        M5 = "";
        I1 = "";
        I2 = "";
        I3 = "";
        I4 = "";
        I5 = "";
        Masses = "";
        Intensities = "";
        checkTAScore = "";
        checkArea = "";
        checkIntensity = "";
        checkMassDev = "";
        checkmSigma = "";
    }*/

    public  boolean isInsert() {
        return insert;
    }

    public  void setInsert(boolean insert) {
        this.insert = insert;
    }
    
    public  void setIsTAscore(boolean isTAscore) {
        this.isTAscore = isTAscore;
    }

    public  void setIsArea(boolean isArea) {
        this.isArea = isArea;
    }

    public  void setIsIntensity(boolean isIntensity) {
        this.isIntensity = isIntensity;
    }

    public  void setIsMassDev(boolean isMassDev) {
        this.isMassDev = isMassDev;
    }

    public  void setIsMSigma(boolean isMSigma) {
        this.isMSigma = isMSigma;
    }

    public  boolean isIsTAscore() {
        return isTAscore;
    }

    public  boolean isIsArea() {
        return isArea;
    }

    public  boolean isIsIntensity() {
        return isIntensity;
    }

    public  boolean isIsMassDev() {
        return isMassDev;
    }

    public  boolean isIsMSigma() {
        return isMSigma;
    }

    public  void setCheckTAScore(String checkTAScore) {
        this.checkTAScore = checkTAScore;
    }

    public  void setCheckArea(String checkArea) {
        this.checkArea = checkArea;
    }

    public  void setCheckIntensity(String checkIntensity) {
        this.checkIntensity = checkIntensity;
    }

    public  void setCheckMassDev(String checkMassDev) {
        this.checkMassDev = checkMassDev;
    }

    public  void setCheckmSigma(String checkmSigma) {
        this.checkmSigma = checkmSigma;
    }

    public  String getCheckTAScore() {
        return checkTAScore;
    }

    public  String getCheckArea() {
        return checkArea;
    }

    public  String getCheckIntensity() {
        return checkIntensity;
    }

    public  String getCheckMassDev() {
        return checkMassDev;
    }

    public  String getCheckmSigma() {
        return checkmSigma;
    }

    public  String getFlag() {
        return Flag;
    }

    public  String getSCRN_id() {
        return SCRN_id;
    }

    public  String getSCRN_obs() {
        return SCRN_obs;
    }

    public  String getS_MxID() {
        return s_MxID;
    }

    public  String getC_id() {
        return c_id;
    }

    public  String getTAcmpd_nr() {
        return TAcmpd_nr;
    }

    public  String getC_formula() {
        return c_formula;
    }

    public  String getC_name() {
        return c_name;
    }

    public  String getC_iontype() {
        return c_iontype;
    }

    public  String getCharge() {
        return charge;
    }

    public  String getIonmass_meas() {
        return ionmass_meas;
    }

    public  String getIonmass_calc() {
        return ionmass_calc;
    }

    public  String getC_ionformula() {
        return c_ionformula;
    }

    public  String getC_iontype_explain() {
        return c_iontype_explain;
    }

    public  String getRet_meas() {
        return ret_meas;
    }

    public  String getRet_exp() {
        return ret_exp;
    }

    public  String getErr_ppm() {
        return err_ppm;
    }

    public  String getErr_mda() {
        return err_mda;
    }

    public  String getSigmafit() {
        return Sigmafit;
    }

    public  String getArea() {
        return area;
    }

    public  String getIntensity() {
        return intensity;
    }

    public  String getAlgorithm() {
        return algorithm;
    }

    public  String getTAscore() {
        return TAscore;
    }

    public  String getRet_diff() {
        return ret_diff;
    }

    public  String getLCMSKey() {
        return LCMSKey;
    }

    public  String getC_AnalyticalSystemID() {
        return c_AnalyticalSystemID;
    }

    public  String getS_key() {
        return s_key;
    }

    public  String getF_id() {
        return f_id;
    }

    public  String getM1() {
        return M1;
    }

    public  String getM2() {
        return M2;
    }

    public  String getM3() {
        return M3;
    }

    public  String getM4() {
        return M4;
    }

    public  String getM5() {
        return M5;
    }

    public  String getI1() {
        return I1;
    }

    public  String getI2() {
        return I2;
    }

    public  String getI3() {
        return I3;
    }

    public  String getI4() {
        return I4;
    }

    public  String getI5() {
        return I5;
    }

    public  String getMasses() {
        return Masses;
    }

    public  String getIntensities() {
        return Intensities;
    }

    public  void setFlag(String Flag) {
        this.Flag = Flag;
    }

    public  void setSCRN_id(String SCRN_id) {
        this.SCRN_id = SCRN_id;
    }

    public  void setSCRN_obs(String SCRN_obs) {
        this.SCRN_obs = SCRN_obs;
    }

    public  void setS_MxID(String s_MxID) {
        this.s_MxID = s_MxID;
    }

    public  void setC_id(String c_id) {
        this.c_id = c_id;
    }

    public  void setTAcmpd_nr(String TAcmpd_nr) {
        this.TAcmpd_nr = TAcmpd_nr;
    }

    public  void setC_formula(String c_formula) {
        this.c_formula = c_formula;
    }

    public  void setC_name(String c_name) {
        this.c_name = c_name;
    }

    public  void setC_iontype(String c_iontype) {
        this.c_iontype = c_iontype;
    }

    public  void setCharge(String charge) {
        this.charge = charge;
    }

    public  void setIonmass_meas(String ionmass_meas) {
        this.ionmass_meas = ionmass_meas;
    }

    public  void setIonmass_calc(String ionmass_calc) {
        this.ionmass_calc = ionmass_calc;
    }

    public  void setC_ionformula(String c_ionformula) {
        this.c_ionformula = c_ionformula;
    }

    public  void setC_iontype_explain(String c_iontype_explain) {
        this.c_iontype_explain = c_iontype_explain;
    }

    public  void setRet_meas(String ret_meas) {
        this.ret_meas = ret_meas;
    }

    public  void setRet_exp(String ret_exp) {
        this.ret_exp = ret_exp;
    }

    public  void setErr_ppm(String err_ppm) {
        this.err_ppm = err_ppm;
    }

    public  void setErr_mda(String err_mda) {
        this.err_mda = err_mda;
    }

    public  void setSigmafit(String Sigmafit) {
        this.Sigmafit = Sigmafit;
    }

    public  void setArea(String area) {
        this.area = area;
    }

    public  void setIntensity(String intensity) {
        this.intensity = intensity;
    }

    public  void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public  void setTAscore(String TAscore) {
        this.TAscore = TAscore;
    }

    public  void setRet_diff(String ret_diff) {
        this.ret_diff = ret_diff;
    }

    public  void setLCMSKey(String LCMSKey) {
        this.LCMSKey = LCMSKey;
    }

    public  void setC_AnalyticalSystemID(String c_AnalyticalSystemID) {
        this.c_AnalyticalSystemID = c_AnalyticalSystemID;
    }

    public  void setS_key(String s_key) {
        this.s_key = s_key;
    }

    public  void setF_id(String f_id) {
        this.f_id = f_id;
    }

    public  void setM1(String M1) {
        this.M1 = M1;
    }

    public  void setM2(String M2) {
        this.M2 = M2;
    }

    public  void setM3(String M3) {
        this.M3 = M3;
    }

    public  void setM4(String M4) {
        this.M4 = M4;
    }

    public  void setM5(String M5) {
        this.M5 = M5;
    }

    public  void setI1(String I1) {
        this.I1 = I1;
    }

    public  void setI2(String I2) {
        this.I2 = I2;
    }

    public  void setI3(String I3) {
        this.I3 = I3;
    }

    public  void setI4(String I4) {
        this.I4 = I4;
    }

    public  void setI5(String I5) {
        this.I5 = I5;
    }

    public  void setMasses(String Masses) {
        this.Masses = Masses;
    }

    public  void setIntensities(String Intensities) {
        this.Intensities = Intensities;
    }

}
