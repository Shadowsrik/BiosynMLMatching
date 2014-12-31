/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thrift.functions;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import thrift.properties.Properties;

/**
 * 
 * @author Zhazira-pc
 */
public class ServerFunctionImplementations {

    /**
     * Connects to the MySQL database server with the default Thrift_User.
     * @param server server name
     * @param database database name
     * @param userName omit
     * @param userpw omit
     * @return
     */
    public Connection getMySQLConnection(String server,
            String database,
            String userName,
            String userpw) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            return null;
        }

        Connection connection = null;
        try {
            connection = DriverManager
                    .getConnection(
                            "jdbc:mysql://" + server + "/" + database + "?useUnicode=true&characterEncoding=UTF-8",
                            Properties.THRIFT_USER, //.ZHAZ_USER,
                            Properties.THRIFT_USER_PASSWORD //.ZHAZ_USER_PASSWORD
                    );

        } catch (SQLException e) {
            return connection;
        }
        if (connection != null) {
            System.out.println("You made it, take control your database now!");
        } else {
            System.out.println("Failed to make connection!");
        }
        return connection;
    }

    /**
     * Returns obtained TA Screening export results for each compound key in a given c_ids list.
     * @param headerLine include header line
     * @param withoutAnalData include compounds without Analytical data
     * @param retentionTime include compounds without Retention time
     * @param pseuFormulae generate neutral pseudo-formulae
     * @param analDetailsToNames append analytical details to names
     * @param polarity the polarity value
     * @param analSystem the Analytical System value
     * @param cIDList the list of selected compound keys
     * @param connection omit
     * @return list of all export result lines
     */
    public List<String> BruckerTAScrrening(boolean headerLine,
            boolean withoutAnalData,
            boolean retentionTime,
            boolean pseuFormulae,
            boolean analDetailsToNames,
            String polarity,
            String analSystem,
            List<Integer> cIDList,
            Connection connection) {
        List<String> resultList = new ArrayList<String>();
        String str = "";

        if (headerLine) {
            str = "m/z,rt,formula,name,rn,id2,id3,relativeRetentiontime,area,indivSigma,indivMassError,Qual1,Qual2,Qual3";
            //str += System.getProperty("line.separator");
            resultList.add(str);
        }

        try {
            for (int c_id : cIDList) {
                Statement stmt = connection.createStatement(
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);
                ResultSet rs1 = stmt.executeQuery(
                        "select  t.c_IonMass as 'Ion m/z',t.c_RetentionTime as 'Ret.', "
                        + " k.c_IonCharge as 'Charge',t.c_IonFormula as 'Formula',t.c_ID,t.c_AnalyticalSystemID,"
                        + " t.c_type1key as 'LCMS key',t.c_IonType ,k.c_IonTypeExplained as 'Iontype',"
                        + " t.c_MinArea as 'Min.area', k.c_IonTypeRule,t.c_IndivError as 'err', t.c_IndivSigma as 'sig',"
                        + " t.c_QualIon1 as 'qi1', t.c_QualIon2 as 'qi2', t.c_QualIon3 as 'qi3',"
                        + " t.c_IonCharge as 'ionCharge', t.c_IonFormula as 'IonFormula', c.c_name as 'c_Name', "
                        + " c.c_formula from C_Analytical_Type1 t "
                        + " left join C_Analytical_Type1_IonTypes k on k.c_iontype = t.c_iontype"
                        + " left join C_Compound c on c.c_id = t.c_ID "
                        + " where t.c_id= '" + c_id + "' and "
                        + " t.c_analyticalsystemid = '" + analSystem + "';");
                int count = 0;
                while (rs1.next()) {
                    count++;
                    str = "";
                    if (rs1.getString("c_Name") == null) {
                        continue;
                    }
                    int ionCharge = rs1.getInt("ionCharge");
                    String ionChargewithSign = "";

                    if (ionCharge >= 0) {
                        if (polarity.equals("Negative")) {
                            continue;
                        }
                        ionChargewithSign = String.valueOf(rs1.getInt(
                                "Charge")) + "+";
                    } else {
                        if (polarity.equals("Positive")) {
                            continue;
                        }
                        ionChargewithSign = String.valueOf(Math.abs(rs1.getInt(
                                "Charge"))) + "-";
                    }

                    double ion = rs1.getDouble("Ion m/z");
                    String ionstr = "";
                    double retTime = rs1.getDouble("Ret.");
                    if (rs1.wasNull()) {
                        if (!retentionTime) {
                            continue;
                        }
                        str += ionstr + ",,";
                    }
                    String retTimestr = "";
                    if (ion == (int) ion) {
                        ionstr = String.format(Locale.US,
                                "%d",
                                (int) ion);
                    } else {
                        ionstr = String.format(Locale.US,
                                "%s",
                                ion);
                    }

                    if (retTime == (int) retTime) {
                        if ((retTime == (double) 0) && !retentionTime && !withoutAnalData) {
                            continue;
                        }
                        retTimestr = String.format(Locale.US,
                                "%d",
                                (int) retTime);
                    } else {
                        retTimestr = String.format(Locale.US,
                                "%s",
                                retTime);
                    }
                    str
                            += ionstr + ","
                            + retTimestr + ",";

                    if (pseuFormulae) {
                        String str137 = parseIonFormula(rs1.getString("Formula"),
                                polarity);
                        str += String.format(Locale.US,
                                "%s",
                                str137) + ",";
                    } else {
                        str += String.format(Locale.US,
                                "%s",
                                rs1.getString("IonFormula"))
                                + "^" + ionChargewithSign + ",";
                    }
                    if (analDetailsToNames) {
                        str += String.format(Locale.US,
                                "%s",
                                rs1.getString("c_Name")) + "  #  ";
                    } else {
                        str += String.format(Locale.US,
                                "%s",
                                rs1.getString("c_Name")) + ",";
                    }
                    String errStr = "";
                    double err = rs1.getDouble("err");
                    if (!(err == (double) 0) && !rs1.wasNull()) {
                        if (err == (int) err) {
                            errStr = String.format(Locale.US,
                                    "%d",
                                    (int) err);
                        } else {
                            errStr = String.format(Locale.US,
                                    "%s",
                                    err);
                        }
                    }

                    String sigStr = "";
                    double sig = rs1.getDouble("sig");
                    if (!(sig == (double) 0) && !rs1.wasNull()) {
                        if (sig == (int) sig) {
                            sigStr = String.format(Locale.US,
                                    "%d",
                                    (int) sig);
                        } else {
                            sigStr = String.format(Locale.US,
                                    "%s",
                                    sig);
                        }
                    }

                    String qi1Str = "";
                    double qi1 = rs1.getDouble("qi1");
                    if (!(qi1 == (double) 0) && !rs1.wasNull()) {
                        if (qi1 == (int) qi1) {
                            qi1Str = String.format(Locale.US,
                                    "%d",
                                    (int) qi1);
                        } else {
                            qi1Str = String.format(Locale.US,
                                    "%s",
                                    qi1);
                        }
                    }

                    String qi2Str = "";
                    double qi2 = rs1.getDouble("qi2");
                    if (!(qi2 == (double) 0) && !rs1.wasNull()) {
                        if (qi2 == (int) qi2) {
                            qi2Str = String.format(Locale.US,
                                    "%d",
                                    (int) qi2);
                        } else {
                            qi2Str = String.format(Locale.US,
                                    "%s",
                                    qi2);
                        }
                    }

                    String qi3Str = "";
                    double qi3 = rs1.getDouble("qi3");
                    if (!(qi3 == (double) 0) && !rs1.wasNull()) {
                        if (qi3 == (int) qi3) {
                            qi3Str = String.format(Locale.US,
                                    "%d",
                                    (int) qi3);
                        } else {
                            qi3Str = String.format(Locale.US,
                                    "%s",
                                    qi3);
                        }
                    }
                    if (analDetailsToNames) {
                        str += String.valueOf(rs1.getInt(
                                "c_AnalyticalSystemID")) + ":" + rs1.
                                getString(
                                        "LCMS key") + ":" + String.valueOf(
                                        rs1.
                                        getInt("c_IonType")) + ":" + rs1.
                                getString("Iontype") + "," + String.valueOf(
                                        rs1.
                                        getInt("c_ID")) + "," + String.
                                valueOf(
                                        rs1.getInt("c_AnalyticalSystemID")) + ":" + rs1.
                                getString("LCMS key") + "," + String.
                                valueOf(
                                        rs1.getInt("c_IonType")) + ":" + rs1.
                                getString("Iontype") + ",," + String.
                                valueOf(
                                        rs1.getString("Min.area")) + "," + errStr + "," + sigStr + "," + qi1Str + "," + qi2Str + "," + qi3Str;
                    } else {
                        str
                                += String.valueOf(rs1.getInt("c_ID")) + "," + String.
                                valueOf(rs1.getInt("c_AnalyticalSystemID")) + ":" + rs1.
                                getString("LCMS key") + "," + String.
                                valueOf(rs1.getInt(
                                                "c_IonType")) + ":" + rs1.
                                getString("Iontype") + ",," + String.
                                valueOf(rs1.getInt("Min.area")) + "," + errStr + "," + sigStr + "," + qi1Str + "," + qi2Str + "," + qi3Str;
                    }
                    //str += System.getProperty("line.separator");
                    resultList.add(str);
                }

                if (withoutAnalData && count == 0) {
                    str = "";
                    String dataStr = "";
                    Pattern p = Pattern.compile("(([a-zA-Z]{1,2})(\\d+))");
                    rs1 = stmt.executeQuery(
                            "select c_formula,c_name from C_Compound where c_id = " + c_id + "");
                    String formula = "";
                    while (rs1.next()) {
                        formula = rs1.getString("c_formula");
                        Matcher m = p.matcher(formula);
                        while (m.find()) {
                            String mod = m.group(3).
                                    toString();
                            int num = Integer.valueOf(mod);
                            if (m.group(2).
                                    toString().
                                    equals("H") || m.group(2).
                                    toString().
                                    equals("h")) {
                                if (polarity.equals("Negative")) {
                                    num = num - 1;
                                } else {
                                    num = num + 1;
                                }
                                dataStr += m.group(2).
                                        toString() + num;
                            } else {
                                dataStr += m.group(2).
                                        toString() + num;
                            }
                        }
                        if (pseuFormulae) {
                            str += ",,"
                                    + String.format(Locale.US,
                                            "%s",
                                            formula) + ",";
                        } else {
                            if (polarity.equals("Negative")) {
                                str += ",," + dataStr + "^-1,";
                            } else {
                                str += ",," + dataStr + "^1+,";
                            }

                        }
                        String analDetails = "";
                        String polarityDetails1 = "";
                        String polarityDetails2 = "";
                        if (polarity.equals("Negative")) {
                            polarityDetails1 = ":LCMS0:1:[M-H]-";
                            polarityDetails2 = ":LCMS0,1:[M-H]-";
                        } else {
                            polarityDetails1 = ":LCMS0:1:[M+H]+";
                            polarityDetails2 = ":LCMS0,1:[M+H]+";
                        }
                        if (analDetailsToNames) {
                            analDetails = " # " + analSystem + polarityDetails1;
                        }
                        str += rs1.getString(
                                "c_name") + analDetails + ","
                                + String.valueOf(c_id) + "," + analSystem + polarityDetails2 + "," + "," + ",,,,,";

                        //str += System.getProperty("line.separator");
                        resultList.add(str);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ServerFunctionImplementations.class.getName()).
                    log(Level.SEVERE,
                            null,
                            ex);
        }

        return resultList;
    }

    private String parseIonFormula(String IonFormula,
            String polarity) {
        char[] charArray = IonFormula.toCharArray();
        String str15 = "";
        String str136 = "";
        String str137 = "";
        for (char c : charArray) {
            if (Character.isLetter(c) && Character.
                    isUpperCase(c)) {
                if (str15.equals("H")) {
                    int w = 0;
                    if (polarity.equals("Positive") || polarity.
                            equals("Both")) {
                        w = Integer.valueOf(str136) - 1;
                    } else if (polarity.equals("Negative")) {
                        w = Integer.valueOf(str136) + 1;
                    }
                    str137 += String.valueOf(w);
                }
                str15 = "";
                str15 += c;
                str137 += c;
            } else if (Character.isLetter(c) && Character.
                    isLowerCase(c)) {
                str137 += c;
            } else if (Character.isDigit(c)) {
                if (str15.equals("H")) {
                    str136 += c;
                } else {
                    str137 += c;
                }
            }
        }
        return str137;
    }

    /**
     *
     * @param SCRNList
     * @param TAScore
     * @param area
     * @param intensity
     * @param massDev
     * @param mSigma
     * @param connection
     * @return
     */
    public String taSreeningXMLParser(List<String> SCRNList, String TAScore, String area, String intensity, String massDev, String mSigma, Connection connection) {
        //cmp.init();
        Compound cmp = new Compound();
        ParseTAScreeningXML xmlparser = new ParseTAScreeningXML();
        cmp.setCheckTAScore(TAScore);
        cmp.setCheckArea(area);
        cmp.setCheckIntensity(intensity);
        cmp.setCheckMassDev(massDev);
        cmp.setCheckmSigma(mSigma);
        cmp.setFlag("0");

        if (!TAScore.equals(Properties.CHECK_DISABLED)) {
            cmp.setIsTAscore(true);
        }
        if (!area.equals(Properties.CHECK_DISABLED)) {
            cmp.setIsArea(true);
        }
        if (!intensity.equals(Properties.CHECK_DISABLED)) {
            cmp.setIsIntensity(true);
        }
        if (!massDev.equals(Properties.CHECK_DISABLED)) {
            cmp.setIsMassDev(true);
        }
        if (!mSigma.equals(Properties.CHECK_DISABLED)) {
            cmp.setIsMSigma(true);
        }
        try {
            cmp.setCount(0);

            for (String SCRNKey : SCRNList) {

                String[] parts = SCRNKey.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
                cmp.setSCRN_id(SCRNKey);
                cmp.setS_key(parts[1]);

                Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);

                ResultSet rs = stmt.executeQuery(
                        "select t.s_MXID, f.s_file from S_Metabolites_TAFiles f, S_Metabolites_TAScreening t where t.SCRN_ID = '" + SCRNKey + "' and t.SCRN_ID = f.SCRN_ID");

                while (rs.next()) {
                    cmp.setS_MxID(String.valueOf(rs.getInt("s_MXID")));

                    Blob File = (Blob) rs.getBlob("s_file");
                    InputStream binaryStream = File.getBinaryStream(1, File.length());
                    try {
                        xmlparser.TAScreeningXMLParser(binaryStream, cmp, connection);
                    } catch (ParserConfigurationException ex) {
                        cmp.exceptionMessageLogs += "ServerFunctionImplementations.taSreeningXMLParser(ParserConfigurationException): {" + ex.getMessage() + "};";
                    } catch (SAXException ex) {
                        cmp.exceptionMessageLogs += "ServerFunctionImplementations.taSreeningXMLParser(SAXException): {" + ex.getMessage() + "};";
                    } catch (IOException ex) {
                        cmp.exceptionMessageLogs += "ServerFunctionImplementations.taSreeningXMLParser(IOException): {" + ex.getMessage() + "};";
                    }
                }
            }
        } catch (SQLException ex) {
            cmp.exceptionMessageLogs += "ServerFunctionImplementations.taSreeningXMLParser(SQLException): {" + ex.getMessage() + "};";
        } catch (Exception ex) {
            cmp.exceptionMessageLogs += "ServerFunctionImplementations.taSreeningXMLParser(Exception): {" + ex.getMessage() + "};";
        }
        if (cmp.getExceptionMessageLogs().trim().equals("")) {
            return "Done";
        } else {
            return cmp.getExceptionMessageLogs().trim();
        }

    }

    /**
     *
     * @param ionFomrula
     * @param ionCharge
     * @return
     */
    public List<String> ipc(String ionFomrula, String ionCharge) {
        List<String> massAndIntenList = new ArrayList<String>();
        ParseTAScreeningXML xmlparser = new ParseTAScreeningXML();
        try {
            massAndIntenList = xmlparser.getMassesAndIntensities(ionFomrula, ionCharge);
        } catch (Exception ex) {
            Logger.getLogger(ServerFunctionImplementations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return massAndIntenList;
    }

    /**
     *
     * @param METAList
     * @param intensity
     * @param area
     * @param peaksNo
     * @param retTime
     * @param recalculateCharge
     * @param connection
     * @return
     */
    public String metabolomeMiningXMLParser(List<String> METAList, String intensity, String area, String peaksNo, String retTime, boolean recalculateCharge, Connection connection) {

        //System.out.println(METAList.toString()+intensity+area+peaksNo+retTime+recalculateCharge);
        for (String METAKey : METAList) {
            try {
                ParseMetabolomeMiningXML metMiningXmlParser = new ParseMetabolomeMiningXML();
                if (!intensity.equals(Properties.CHECK_DISABLED)) {
                    metMiningXmlParser.checkIntensity = Double.parseDouble(intensity.replace(",", "."));
                }
                if (!area.equals(Properties.CHECK_DISABLED)) {
                    metMiningXmlParser.checkArea = Double.parseDouble(area.replace(",", "."));
                }
                if (!peaksNo.equals(Properties.CHECK_DISABLED)) {
                    metMiningXmlParser.checkPeakNo = Integer.valueOf(peaksNo);
                }
                if (!retTime.equals(Properties.CHECK_DISABLED)) {
                    String[] parts = retTime.split(";");
                    metMiningXmlParser.checkRetTimeFrom = Double.parseDouble(parts[0].replace(",", "."));
                    metMiningXmlParser.checkRetTimeTo = Double.parseDouble(parts[1].replace(",", "."));
                }

                Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);

                ResultSet rs = stmt.executeQuery(
                        "select b.META_blob,d.Analyticalsystem from S_Metabolome_blobs b,S_Metabolome_data d where b.META_ID = '" + METAKey + "' and b.META_ID = d.META_key");
                while (rs.next()) {
                    Blob File = (Blob) rs.getBlob("META_blob");
                    InputStream binaryStream = File.getBinaryStream(1, File.length());
                    metMiningXmlParser.METAXMLParser(binaryStream, METAKey, String.valueOf(rs.getInt("Analyticalsystem")), recalculateCharge, connection);
                }
            } catch (SQLException ex) {
            } catch (Exception ex) {
            }
        }
        return "";
    }

    /**
     *
     * @param QRY_IDList
     * @param connection
     */
    public void metabolomeMiningQuery(List<String> QRY_IDList, Connection connection) {
        System.out.println(QRY_IDList.toString());
        for (String qryId : QRY_IDList) {
            try {
                ParseExperimentParametersAndQueryResults parser = new ParseExperimentParametersAndQueryResults();
                Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = stmt.executeQuery("select QRY_parameters,QRY_ID from QRY_Experiments where QRY_Key = " + qryId + "");
                String parameters = "";
                String QRY_ID = "";
                while (rs.next()) {
                    parameters = rs.getString("QRY_parameters");
                    QRY_ID = rs.getString("QRY_ID");
                }
                if (!parameters.equals("")) {
                    parser.parseExperimentParameters(qryId,QRY_ID, parameters, connection);
                    Statement st = (Statement) connection.createStatement();
                    st.executeUpdate("update QRY_Experiments set QRY_status = '1' where QRY_Key = " + qryId + "");
                }
            } catch (SQLException e) {
            } catch (Exception e) {
            }
        }
    }
}
