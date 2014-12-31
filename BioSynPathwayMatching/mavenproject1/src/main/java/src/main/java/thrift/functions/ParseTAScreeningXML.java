/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thrift.functions;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import thrift.properties.Properties;

/**
 *
 * @author zhos001
 */
public class ParseTAScreeningXML {

    Compound cmp = null;
    Connection connection = null;
    int count = 0;

    public void TAScreeningXMLParser(InputStream xmlBinaryStream, Compound compound, Connection conn) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = saxParserFactory.newSAXParser();
        cmp = compound;
        connection = conn;
        cmp.setInsert(true);
        DefaultHandler defaultHandler = new DefaultHandler() {
            public void startElement(String uri, String localName, String qName,
                    Attributes attributes) throws SAXException {
                if (qName.equalsIgnoreCase("cmpd")) {
                    count++;
                    try {
                        if (!parse_Compound(attributes)) {
                            cmp.setInsert(false);
                        }
                    } catch (IOException ex) {
                        cmp.exceptionMessageLogs += "ParseTAScreeningXML.startElement(IOException): {" + ex.getMessage() + "};";
                    }
                }
                if (qName.equalsIgnoreCase("TargetedMSPeak")) {
                    parse_TargetedMSPeak(attributes);
                }
                if (qName.equalsIgnoreCase("pk")) {
                    parse_pk(attributes);
                }
                if (qName.equalsIgnoreCase("GMF_result")) {
                    parseGMF_result(attributes);
                    if (checkCompound() ) {//&& cmp.isInsert()
                        insertIntoTAResultsTable();
                    }
                }
            }

            public void endElement(String uri, String localName, String qName)
                    throws SAXException {
            }

            public void characters(char ch[], int start, int length)
                    throws SAXException {
            }

        };
        saxParser.parse(xmlBinaryStream, defaultHandler);
    }

    private boolean checkCompound() {
        
        boolean check = true;
        try {
            if (cmp.isIsTAscore() && !cmp.getCheckTAScore().equals(Properties.CHECK_DISABLED)) {
                //System.out.println(Compound.getTAscore()+"::tascore"+Compound.getCheckTAScore());
                if (Integer.parseInt(cmp.getTAscore()) < Integer.valueOf(cmp.getCheckTAScore())) {
                    check = false;
                }
            }
            if (cmp.isIsArea() && !cmp.getCheckArea().equals(Properties.CHECK_DISABLED)) {
                //System.out.println(Compound.getArea()+":area:"+Compound.getCheckArea());
                int area = (int) Math.round(Double.parseDouble(cmp.getArea()));
                int checkArea = (int) Math.round(Double.parseDouble(cmp.getCheckArea()));
                //System.out.println(Compound.getArea()+":area:"+Compound.getCheckArea());
                if (area < checkArea) {
                    check = false;
                }
            }
            if (cmp.isIsIntensity() && !cmp.getCheckIntensity().equals(Properties.CHECK_DISABLED)) {
                //System.out.println(Compound.getIntensity()+":inten:"+Compound.getCheckIntensity());
                int intensity = (int) Math.round(Double.parseDouble(cmp.getIntensity()));
                int checkInten = (int) Math.round(Double.parseDouble(cmp.getCheckIntensity()));
                //System.out.println(Compound.getIntensity()+":inten:"+Compound.getCheckIntensity());
                if (intensity < checkInten) {
                    check = false;
                }
            }
            if (cmp.isIsMassDev() && !cmp.getCheckMassDev().equals(Properties.CHECK_DISABLED)) {
                //System.out.println(Compound.getErr_ppm()+":mz:"+Compound.getCheckMassDev());
                double mz = Math.abs(Double.parseDouble(cmp.getErr_ppm().replace(",", ".")));
                double checkMz = Math.abs(Double.parseDouble(cmp.getCheckMassDev().replace(",", ".")));
                //System.out.println(Compound.getErr_ppm()+":mz:"+Compound.getCheckMassDev());
                if (mz > checkMz) {
                    check = false;
                }
            }
            if (cmp.isIsMSigma() && !cmp.getCheckmSigma().equals(Properties.CHECK_DISABLED)) {
                //System.out.println(Compound.getSigmafit()+":msigma:"+Compound.getCheckmSigma());
                double mSigma = Math.abs(Double.parseDouble(cmp.getSigmafit().replace(",", ".")));
                double checkMSigma = Math.abs(Double.parseDouble(cmp.getCheckmSigma().replace(",", ".")));
                //System.out.println(Compound.getSigmafit()+":msigma:"+Compound.getCheckmSigma());
                if (mSigma < checkMSigma) {
                    check = false;
                }
            }
        } catch (Exception ex) {
            cmp.exceptionMessageLogs += "ParseTAScreeningXML.checkCompound(Exception): {" + ex.getMessage() + "};";
            check = false;
        }
        return check;
    }

    private void parse_TargetedMSPeak(Attributes attributes) {
        String theoretical_mz = attributes.getValue("theoretical_mz");
        cmp.setIonmass_calc(theoretical_mz);
        
    }

    private void parse_pk(Attributes attributes) {
        String mz = attributes.getValue("mz");
        cmp.setIonmass_meas(mz);
    }

    private void parseGMF_result(Attributes attributes) {
        String sigmaFit = attributes.getValue("sigmaFit");
        String error_mDa = attributes.getValue("error_mDa");
        String error_ppm = attributes.getValue("error_ppm");
        cmp.setErr_ppm(error_ppm);
        cmp.setErr_mda(error_mDa);
        cmp.setSigmafit(sigmaFit);
    }

    private boolean parse_Compound(Attributes attributes) throws IOException {
        //String cmpd_name = attributes.getValue("cmpd_name");
        //String aux1 = attributes.getValue("aux1");
        //String aux0 = attributes.getValue("aux0");
        //String concentration = attributes.getValue("concentration");
        //String unit_concentration = attributes.getValue("unit_concentration");
        //String target_formula = attributes.getValue("target_formula");
        //String rt_unit = attributes.getValue("rt_unit");
        //String target_analyte_id = attributes.getValue("target_analyte_id");
        //String target_compound_moz = attributes.getValue("target_compound_moz");
        //String target_identified = attributes.getValue("target_identified");
        //String target_compound_type = attributes.getValue("target_compound_type");
     
        String cmpdnr = attributes.getValue("cmpdnr");
        String rt = attributes.getValue("rt");
        String intensity = attributes.getValue("i");
        String area = attributes.getValue("a");
        String target_compound = attributes.getValue("target_compound");
        String target_compound_regnr = attributes.getValue("target_compound_regnr");
        String target_expected_retention_time = attributes.getValue("target_expected_retention_time");
        String target_score = attributes.getValue("target_score");
        String algorithm = attributes.getValue("algorithm");

                    
        cmp.setTAscore(target_score);
        cmp.setRet_exp(target_expected_retention_time);
        cmp.setC_id(target_compound_regnr);
        cmp.setArea(area);
        cmp.setIntensity(intensity);
        cmp.setRet_meas(rt);
        cmp.setSCRN_obs(cmpdnr);
        cmp.setTAcmpd_nr(cmpdnr);
        cmp.setAlgorithm(algorithm);

        String[] parts1 = target_compound.split("#");
        String[] parts2 = parts1[1].trim().split(":");
        String cName = parts1[0].trim();
        String analSys = parts2[0].trim();
        String LCMS = parts2[1].trim();
        String ionType = parts2[2].trim();
        String ionTypeExpl = parts2[3].trim();

        cmp.setC_name(cName);
        cmp.setC_iontype(ionType);
        cmp.setC_iontype_explain(ionTypeExpl);
        cmp.setLCMSKey(LCMS);
        cmp.setC_AnalyticalSystemID(analSys);

        String[] parts = getFromCompoundTable(target_compound_regnr).split(";");

        cmp.setC_formula(parts[0]);
        cmp.setF_id(parts[1].trim());

        String parsedFormula = "";
        Pattern pattern = Pattern.compile("([A-Z][a-z]*)([0-9]*)");
        Matcher matcher = pattern.matcher(parts[0]);
        while (matcher.find()) {
            parsedFormula += matcher.group();
            if (!Character.isDigit(matcher.group().charAt(matcher.group().length() - 1))) {
                parsedFormula += "1";
            }
        }

        String[] values = getFromCAnalyticalType1IonTypesTable(ionTypeExpl);
        int ionCharge = Integer.valueOf(values[0]);
        cmp.setCharge(values[0].trim());

        int ionMultimeric = Integer.valueOf(values[2]);
        String ionTypeRule = values[1];

        String ionFormula = "";
        pattern = Pattern.compile("(([a-zA-Z]{1,2})(\\d+))");
        matcher = pattern.matcher(parsedFormula);
        while (matcher.find()) {
            int mod = Integer.valueOf(matcher.group(3)) * ionMultimeric;
            ionFormula += matcher.group(2) + mod;
        }

        ionFormula = modifyFormula(ionFormula, ionTypeRule);
        cmp.setC_ionformula(ionFormula);

        if (!(String.valueOf(ionCharge).equals("") && ionFormula.equals(""))) {
            List<String> massIntenList = getMassesAndIntensities(ionFormula, String.valueOf(ionCharge));
            if (massIntenList.size() == 0) {
                return false;
            }
            List<Double> mzList = new ArrayList<Double>();
            List<Double> intensityList = new ArrayList<Double>();
            String mzs = "";
            String intensities = "";
            for (String item : massIntenList) {
                String[] listParts = item.split(";");
                mzList.add(Double.parseDouble(listParts[0]));
                intensityList.add(Double.parseDouble(listParts[1]));
                mzs += listParts[0] + ";";
                intensities += listParts[1] + ";";
            }
            cmp.setMasses(mzs);
            cmp.setIntensities(intensities);

            if (mzList.size() == intensityList.size()) {
                try {
                    double rt_double = (double) Math.round(Double.parseDouble(rt.replace(",", ".")) * ((double) 100)) / ((double) 100);
                    double target_expected_retention_time_double = (double) Math.round(Double.parseDouble(target_expected_retention_time.replace(",", ".")) * ((double) 100)) / ((double) 100);;
                    double charge = (double) Math.round((target_expected_retention_time_double - rt_double) * ((double) 100)) / (double) 100;
                    cmp.setRet_diff(String.valueOf(Math.abs(charge)));
                } catch (Exception ex) {
                    cmp.exceptionMessageLogs += "ParseTAScreeningXML.parse_Compound(Exception): {" + ex.getMessage() + "};";
                }
                for (int i = 1; i <= 5; i++) {
                    String defaultVal = "0.0";
                    if (mzList.size() >= i) {
                        if (i == 1) {
                            cmp.setM1(String.valueOf(mzList.get(0)));
                            cmp.setI1(String.valueOf(intensityList.get(0)));
                        } else if (i == 2) {
                            cmp.setM2(String.valueOf(mzList.get(1)));
                            cmp.setI2(String.valueOf(intensityList.get(1)));
                        } else if (i == 3) {
                            cmp.setM3(String.valueOf(mzList.get(2)));
                            cmp.setI3(String.valueOf(intensityList.get(2)));
                        } else if (i == 4) {
                            cmp.setM4(String.valueOf(mzList.get(3)));
                            cmp.setI4(String.valueOf(intensityList.get(3)));
                        } else if (i == 5) {
                            cmp.setM5(String.valueOf(mzList.get(4)));
                            cmp.setI5(String.valueOf(intensityList.get(4)));
                        }
                    } else {
                        if (i == 1) {
                            cmp.setM1(defaultVal);
                            cmp.setI1(defaultVal);
                        } else if (i == 2) {
                            cmp.setM2(defaultVal);
                            cmp.setI2(defaultVal);
                        } else if (i == 3) {
                            cmp.setM3(defaultVal);
                            cmp.setI3(defaultVal);
                        } else if (i == 4) {
                            cmp.setM4(defaultVal);
                            cmp.setI4(defaultVal);
                        } else if (i == 5) {
                            cmp.setM5(defaultVal);
                            cmp.setI5(defaultVal);
                        }
                    }
                }
            }
        }
        return true;
    }

    public List<String> getMassesAndIntensities(String ionFormula, String ionCharge) throws IOException {

        List<String> massIntenList = new ArrayList<String>();
        String procQuery = "";
        if (ionCharge.equals("empty")) {
            procQuery = "java -cp " + Properties.PROJECT_PATH + Properties.PROJECT_NAME + " ipc.IPC -f 100 -c " + ionFormula + " -ei -r 10000 -t -b";
        } else {
            procQuery = "java -cp " + Properties.PROJECT_PATH + Properties.PROJECT_NAME + " ipc.IPC -f 100 -c " + ionFormula + " -z " + ionCharge + " -ei -r 10000 -t -b";
        }

        Process p = Runtime.getRuntime().exec(procQuery);
        InputStream in = p.getInputStream();
        StringBuilder sb = new StringBuilder();
        for (int c = 0; (c = in.read()) > -1;) {
            sb.append((char) c);
        }
        in.close();
        if (sb.toString().trim().equals("")) {
            return massIntenList;
        }
        String[] lines = sb.toString().split("\\r?\\n");
        //System.out.println(sb.toString());
        for (String line : lines) {
            String[] list = line.split("\\t?\\s+");
            if (list.length == 3) {
                try {
                    Double.parseDouble(list[0].replace(",", "."));
                    Double.parseDouble(list[1].replace(",", "."));
                    Double.parseDouble(list[2].replace(",", "."));
                    massIntenList.add(list[0].replace(",", ".") + ";" + list[2].replace(",", "."));
                } catch (NumberFormatException ex) {
                    //if(!list[0].equals("Covered"))
                    //cmp.exceptionMessageLogs += "ParseTAScreeningXML.getMassesAndIntensities(NumberFormatException): {" + ex.getMessage() + "};";
                }
            }
        }

        return massIntenList;
    }

    private String getFromCompoundTable(String c_id) {
        String out = "";
        try {
            Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            ResultSet rs = stmt.executeQuery(
                    "select c_formula,f_id from C_Compound where c_id = ('" + c_id + "')");

            while (rs.next()) {
                out = rs.getString("c_formula") + ";" + rs.getInt("f_id");
            }
        } catch (SQLException ex) {
            cmp.exceptionMessageLogs += "ParseTAScreeningXML.getFromCompoundTable(SQLException): {" + ex.getMessage() + "};";
        }
        return out;
    }

    private String[] getFromCAnalyticalType1IonTypesTable(String ionType) {
        String[] val = new String[3];
        try {
            Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            ResultSet rs = stmt.executeQuery(
                    "SELECT c_IonCharge,c_IonTypeRule,c_IonMultimeric from C_Analytical_Type1_IonTypes where c_IonTypeExplained = ('" + ionType + "')");

            while (rs.next()) {
                val[0] = String.valueOf(rs.getInt("c_IonCharge"));
                val[1] = rs.getString("c_IonTypeRule");
                val[2] = String.valueOf(rs.getInt("c_IonMultimeric"));
            }
        } catch (SQLException ex) {
            cmp.exceptionMessageLogs += "ParseTAScreeningXML.getFromCAnalyticalType1IonTypesTable(SQLException): {" + ex.getMessage() + "};";
        } catch (Exception ex) {
            cmp.exceptionMessageLogs += "ParseTAScreeningXML.getFromCAnalyticalType1IonTypesTable(Exception): {" + ex.getMessage() + "};";
        }
        return val;
    }

    private String modifyFormula(String ionFormula, String ionTypeRule) {
        String modFormula = "";
        Pattern pattern = Pattern.compile("([A-Za-z]+[0-9]+)");
        Matcher matcher = pattern.matcher(ionFormula);
        Map<String, Integer> breakFormula = new LinkedHashMap<String, Integer>();
        while (matcher.find()) {
            String[] parts = matcher.group(1).split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
            breakFormula.put(parts[0], Integer.valueOf(parts[1]));
        }

        pattern = Pattern.compile("([\\+\\-][A-Za-z]+[0-9]+)");
        matcher = pattern.matcher(ionTypeRule);
        while (matcher.find()) {
            String[] parts = matcher.group(1).split("(?<=\\-)|(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)|(?<=\\+)");
            int amount = Integer.valueOf(parts[2]);
            if (parts[0].equals("-")) {
                amount = Integer.valueOf(parts[2]) * (-1);
            }

            if (breakFormula.containsKey(parts[1])) {
                breakFormula.put(parts[1], (breakFormula.get(parts[1]) + amount));
            } else {
                breakFormula.put(parts[1], amount);
            }
        }

        for (String key : breakFormula.keySet()) {
            modFormula += key + breakFormula.get(key).toString();
        }

        return modFormula;
    }

    private void insertIntoTAResultsTable() {

        String insertQuery = "insert INTO S_Metabolites_TAresults (Flag,SCRN_id, SCRN_obs, s_MxID,c_id, c_formula, c_name, c_iontype, charge, ionmass_meas, ionmass_calc, c_ionformula, c_iontype_explain, ret_meas, ret_exp,  err_ppm, err_mda, Sigmafit, area, intensity, algorithm, TAscore, ret_diff, LCMSKey, c_AnalyticalSystemID,SCRN_key,f_id,M1, M2, M3, M4, M5, I1, I2, I3, I4, I5, Masses, Intensities) VALUES (" + cmp.getFlag() + ",'" + cmp.getSCRN_id() + "','" + cmp.getSCRN_obs() + "','" + cmp.getS_MxID() + "','" + cmp.getC_id() + "','" + cmp.getC_formula() + "','" + cmp.getC_name() + "','" + cmp.getC_iontype() + "','" + cmp.getCharge() + "','" + cmp.getIonmass_meas() + "','" + cmp.getIonmass_calc() + "','" + cmp.getC_ionformula() + "','" + cmp.getC_iontype_explain() + "','" + cmp.getRet_meas() + "','" + cmp.getRet_exp() + "','" + cmp.getErr_ppm() + "','" + cmp.getErr_mda() + "','" + cmp.getSigmafit() + "','" + cmp.getArea() + "','" + cmp.getIntensity() + "','" + cmp.getAlgorithm() + "','" + cmp.getTAscore() + "','" + cmp.getRet_diff() + "','" + cmp.getLCMSKey() + "','" + cmp.getC_AnalyticalSystemID() + "','" + cmp.getS_key() + "','" + cmp.getF_id() + "','" + cmp.getM1() + "','" + cmp.getM2() + "','" + cmp.getM3() + "','" + cmp.getM4() + "','" + cmp.getM5() + "','" + cmp.getI1() + "','" + cmp.getI2() + "','" + cmp.getI3() + "','" + cmp.getI4() + "','" + cmp.getI5() + "','" + cmp.getMasses() + "','" + cmp.getIntensities() + "')";
        try {
            Statement st = (Statement) connection.createStatement();
            st.executeUpdate(insertQuery);
            cmp.setCount(cmp.getCount() + 1);
        } catch (SQLException ex) {
            cmp.exceptionMessageLogs += "ParseTAScreeningXML.insertIntoTAResultsTable(SQLException): {" + ex.getMessage() + "};";
        }
    }
}
