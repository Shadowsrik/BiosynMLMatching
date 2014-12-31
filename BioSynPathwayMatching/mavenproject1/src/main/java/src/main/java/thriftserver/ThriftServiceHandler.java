/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thriftserver;

import java.io.UnsupportedEncodingException;
import thrift.functions.ServerFunctionImplementations;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.thrift.TException;

/**
 * This class has all Thrift Server-side functions implementation which can be called from Client-side. 
 * @author Zhazira-pc
 */
public class ThriftServiceHandler
        implements ThriftService.Iface {

    /**
     * Initially has null value, gets value after mySQLConnect method is called.
     */
    public Connection connection = null;

    /**
     * Test call to check whether there is connection to the thrift.
     * @param test
     * @return
     */
    public String testConnection(boolean test) {
        if (test) {
            return "Success";
        }
        return "Problem";
    }

    /**
     * Connects to the MySQL database server with the default Thrift_User.
     * @param server server name
     * @param database database name
     * @param userName omit
     * @param userpw omit
     * @throws org.apache.thrift.TException
     */
    public void mySQLConnect(String server,
            String database,
            String userName,
            String userpw)
            throws org.apache.thrift.TException {
        System.out.println("ThriftServiceHandler.mySQLConnect(" + server + "," + database + ")");
        ServerFunctionImplementations sfi = new ServerFunctionImplementations();
        connection = sfi.getMySQLConnection(server,
                database,
                userName,
                userpw);
    }

    /**
     * Returns obtained TA Screening export results for each compound key in a given c_ids list.
     * @param headerLine include header line.
     * @param withoutAnalData include compounds without Analytical data.
     * @param retentionTime include compounds without Retention time.
     * @param pseuFormula generate neutral pseudo-formulae.
     * @param analDetailsToName append analytical details to names.
     * @param polarity the polarity value.
     * @param analSystem the Analytical System value.
     * @param c_ids the list of selected compound keys.
     * @return list of all export result lines.
     * @throws org.apache.thrift.TException
     */
    public List<String> bruckerTAScreening_Export(boolean headerLine,
            boolean withoutAnalData,
            boolean retentionTime,
            boolean pseuFormula,
            boolean analDetailsToName,
            String polarity,
            String analSystem,
            List<Integer> c_ids)
            throws org.apache.thrift.TException {
        System.out.println("ThriftServiceHandler.bruckerTAScreening_Export(" + polarity + "," + analSystem + ")");
        List<String> resultStr = new ArrayList<String>();

        if (connection != null) {

            ServerFunctionImplementations sfi = new ServerFunctionImplementations();
            List<String> newString = sfi.BruckerTAScrrening(headerLine,
                    withoutAnalData,
                    retentionTime,
                    pseuFormula,
                    analDetailsToName,
                    polarity,
                    analSystem,
                    c_ids,
                    connection);
            try {
                for (String str : newString) {
                    System.out.println();
                    String result = new String(str.getBytes("UTF-8"), "UTF-8");
                    resultStr.add(result);
                }
                //return newString;
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(ThriftServiceHandler.class.getName()).
                        log(Level.SEVERE,
                                null,
                                ex);
            }
        }
        return resultStr;
    }

    /**
     * Reads XML files from the S_Metabolites_TAFiles table for each SCRN_Key in a given SRCNList, parses each XML file and writes obtained TS_Screen results into the S_Metabolites_TAresults table with its corresponding SCRN_Key.
     * @param SCRNList the list of SCRN_Keys.
     * @param TAScore  the minimum TA Score value (has value "Disabled" if this filter option is ignored).
     * @param area  the minimum area value (has value "Disabled" if this filter option is ignored).
     * @param intensity the minimum intensity value (has value "Disabled" if this filter option is ignored)
     * @param massDev the mass deviation threshold (has value "Disabled" if this filter option is ignored).
     * @param mSigma the mSigma threshold (has value "Disabled" if this filter option is ignored).
     * @return
     */
    public String taSreeningXMLParser(List<String> SCRNList, String TAScore, String area, String intensity, String massDev, String mSigma) {
        System.out.println("ThriftServiceHandler.taSreeningXMLParser(" + SCRNList.toString() + "," + TAScore + "," + area + "," + intensity + "," + massDev + "," + mSigma + ")");
        String result = "";
        if (connection != null) {
            ServerFunctionImplementations sfi = new ServerFunctionImplementations();
            result = sfi.taSreeningXMLParser(SCRNList, TAScore, area, intensity, massDev, mSigma, connection);
        }
        return result;
    }

    /**
     * Calculates intensities and masses for a given ion Formula with ion charge using IPC libraries.
     * @param ionFormula the ion formula.
     * @param ionCharge the ion charge (has value "empty" if ion charge is ignored).
     * @return the list of intensities and masses.
     * @throws TException
     */
    public List<String> ipc(String ionFormula, String ionCharge) throws TException {
        System.out.println("ThriftServiceHandler.ipc(" + ionFormula + "," + ionCharge + ")");
        ServerFunctionImplementations sfi = new ServerFunctionImplementations();
        List<String> result = sfi.ipc(ionFormula, ionCharge);
        return result;
    }

    /**
     * Reads XML files from the S_Metabolome_blobs table for each META_Key in a given METAList, parses each XML file and writes obtained meta results into the S_Metabolite_mining table with its corresponding META_Key.
     * @param METAList the list of META_Keys.
     * @param intensity the minimum intensity value (has value "Disabled" if this filter option is ignored).
     * @param area the minimum area value (has value "Disabled" if this filter option is ignored).
     * @param peaksNo the no of min.peaks in spectrum (has value "Disabled" if this filter option is ignored).
     * @param retTime the retention time threshold (has value "Disabled" if this filter option is ignored).
     * @param recalculateCharge recalculate charge (false if this filter options is ignored)
     * @return
     */
    public String metabolomeMiningXMLParser(List<String> METAList, String intensity, String area, String peaksNo, String retTime, boolean recalculateCharge) {
        String result = "";
        if (connection != null) {
            ServerFunctionImplementations sfi = new ServerFunctionImplementations();
            result = sfi.metabolomeMiningXMLParser(METAList, intensity, area, peaksNo, retTime, recalculateCharge, connection);
        }
        return result;
    }
    
    /**
     * Reads query parameters, query ID, query type from the QRY_Experiments table for each QRY_Key in a given QRY_IDList, make MySQL queries, and write results into the QRY_Results table with its corresponding query ID and query type.
     * @param QRY_IDList list of QRY_Keys.
     */
    public void metabolomeMiningQuery(List<String> QRY_IDList){
        if (connection != null) {
            ServerFunctionImplementations sfi = new ServerFunctionImplementations();
            sfi.metabolomeMiningQuery(QRY_IDList, connection);
        }
    }
    
}
