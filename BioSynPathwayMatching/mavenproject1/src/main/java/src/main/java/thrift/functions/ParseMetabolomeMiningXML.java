/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thrift.functions;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author zhos001
 */
public class ParseMetabolomeMiningXML {

    Connection connection = null;
    METACompound currentCmpd = new METACompound();
    boolean isRecalculateCharge = false;
    String METAKey = "";
    String analSystem = "";
    public double checkArea = 0;
    public double checkIntensity = 0;
    public int checkPeakNo = 0;
    public double checkRetTimeFrom = 0;
    public double checkRetTimeTo = 0;
    public int cmpdCounter = 0;

    void METAXMLParser(InputStream binaryStream, String mKey, String anSystem, boolean recalculateCharge, Connection conn)
            throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = saxParserFactory.newSAXParser();
        connection = conn;
        isRecalculateCharge = recalculateCharge;
        METAKey = mKey;
        analSystem = anSystem;

        DefaultHandler defaultHandler = new DefaultHandler() {
            public void startElement(String uri, String localName, String qName,
                    Attributes attributes) throws SAXException {
                if (qName.equalsIgnoreCase("cmpd")) {

                    currentCmpd = new METACompound();
                    currentCmpd.setMetaKey(METAKey);
                    currentCmpd.setAnalSystem(analSystem);
                    parseCmpd(attributes);
                }
                if (qName.equalsIgnoreCase("ms_spectrum")) {
                    parseMsSpectrum(attributes);
                }
                if (qName.equalsIgnoreCase("pk")) {
                    parsePk(attributes);
                }
            }

            public void endElement(String uri, String localName, String qName)
                    throws SAXException {
                if (qName.equalsIgnoreCase("cmpd")) {
                    insertIntoMetabolomeMiningTable();
                }
            }

            public void characters(char ch[], int start, int length)
                    throws SAXException {
            }

        };
        saxParser.parse(binaryStream, defaultHandler);
    }

    private void parseCmpd(Attributes attributes) {
        String cmpdnr = attributes.getValue("cmpdnr");
        String rt = attributes.getValue("rt");
        String a = attributes.getValue("a");

        currentCmpd.setCmpdKey(cmpdnr);
        currentCmpd.setM_area(a);
        currentCmpd.setRetTime(rt);
    }

    private void parseMsSpectrum(Attributes attributes) {
        String polarity = attributes.getValue("polarity");
        currentCmpd.setPolarity(polarity);
    }

    private void parsePk(Attributes attributes) {
        String a = attributes.getValue("a");
        String i = attributes.getValue("i");
        String sn = attributes.getValue("sn");
        String res = attributes.getValue("res");
        String z = attributes.getValue("z");
        String deconvoluted_molecular_mass = attributes.getValue("deconvoluted_molecular_mass");
        String mz = attributes.getValue("mz");

        //set chargelist
        List<String> chargeList = currentCmpd.getChargeList();
        if (!chargeList.contains(z)) {
            chargeList.add(z);
            currentCmpd.setChargeList(chargeList);
        }
        //set peakNo
        Integer peakNo;
        Map<String, Integer> pkPeakNoList = currentCmpd.getPkPeakNoList();
        if (pkPeakNoList.containsKey(z)) {
            peakNo = pkPeakNoList.get(z) + 1;
            pkPeakNoList.remove(z);
        } else {
            peakNo = 1;
        }
        pkPeakNoList.put(z, peakNo);
        currentCmpd.setPkPeakNoList(pkPeakNoList);

        //set Intensities
        Map<String, String> pkIntensitiesList = currentCmpd.getPkIntensitiesList();
        String intensities = String.valueOf(Double.parseDouble(i.replace(",", ".")));
        if (pkIntensitiesList.containsKey(z)) {
            intensities = pkIntensitiesList.get(z) + ";" + String.valueOf(Double.parseDouble(i.replace(",", ".")));
            pkIntensitiesList.remove(z);
        }
        pkIntensitiesList.put(z, intensities);
        currentCmpd.setPkIntensitiesList(pkIntensitiesList);

        //set Mzs
        Map<String, String> pkMzsList = currentCmpd.getPkMzsList();
        String mzs = String.valueOf(Double.parseDouble(mz.replace(",", ".")));
        if (pkMzsList.containsKey(z)) {
            mzs = pkMzsList.get(z) + ";" + String.valueOf(Double.parseDouble(mz.replace(",", ".")));
            pkMzsList.remove(z);
        }
        pkMzsList.put(z, mzs);
        currentCmpd.setPkMzsList(pkMzsList);

        //set I1,I2,I3,I4,I5
        List<String> intensityList;
        Map<String, List<String>> pkIntensityList = currentCmpd.getPkIntensityList();
        if (pkIntensityList.containsKey(z)) {
            intensityList = pkIntensityList.get(z);
            pkIntensityList.remove(z);
        } else {
            intensityList = new ArrayList<String>();
        }
        intensityList.add(String.valueOf(Double.parseDouble(i.replace(",", "."))));
        pkIntensityList.put(z, intensityList);
        currentCmpd.setPkIntensityList(pkIntensityList);

        //set M1,M2,M3,M4,M5
        List<String> pkMassList;
        Map<String, List<String>> pkMassesList = currentCmpd.getPkMassList();
        if (pkMassesList.containsKey(z)) {
            pkMassList = pkMassesList.get(z);
            pkMassesList.remove(z);
        } else {
            pkMassList = new ArrayList<String>();
        }
        pkMassList.add(String.valueOf(Double.parseDouble(mz.replace(",", "."))));
        pkMassesList.put(z, pkMassList);
        currentCmpd.setPkMassList(pkMassesList);

        //set Decon.Mol.Masses
        Map<String, String> pkDeconvoluted_molecular_massList = currentCmpd.getPkDeconvoluted_molecular_massList();
        String deconMolMasses = String.valueOf(Double.parseDouble(deconvoluted_molecular_mass.replace(",", "."))) ;
        if (pkDeconvoluted_molecular_massList.containsKey(z)) {
            deconMolMasses = pkDeconvoluted_molecular_massList.get(z) + ";" + String.valueOf(Double.parseDouble(deconvoluted_molecular_mass.replace(",", ".")));
            pkDeconvoluted_molecular_massList.remove(z);
        }
        pkDeconvoluted_molecular_massList.put(z, deconMolMasses);
        currentCmpd.setPkDeconvoluted_molecular_massList(pkDeconvoluted_molecular_massList);

        //set Res
        Map<String, String> pkResList = currentCmpd.getPkResList();
        String pkres = String.valueOf(Double.parseDouble(res.replace(",", ".")));
        if (pkResList.containsKey(z)) {
            pkres = pkResList.get(z) + ";" + String.valueOf(Double.parseDouble(res.replace(",", ".")));
            pkResList.remove(z);
        }
        pkResList.put(z, pkres);
        currentCmpd.setPkResList(pkResList);

        //set Area
        Map<String, String> pkAreaList = currentCmpd.getPkAreaList();
        String pkareas = String.valueOf(Double.parseDouble(a.replace(",", ".")));
        if (pkAreaList.containsKey(z)) {
            pkareas = pkAreaList.get(z) + ";" + String.valueOf(Double.parseDouble(a.replace(",", ".")));
            pkAreaList.remove(z);
        }
        pkAreaList.put(z, pkareas);
        currentCmpd.setPkAreaList(pkAreaList);

        //set Area
        Map<String, String> pkSnList = currentCmpd.getPkSnList();
        String pksn = String.valueOf(Double.parseDouble(sn.replace(",", ".")));
        if (pkSnList.containsKey(z)) {
            pksn = pkSnList.get(z) + ";" + String.valueOf(Double.parseDouble(sn.replace(",", ".")));
            pkSnList.remove(z);
        }
        pkSnList.put(z, pksn);
        currentCmpd.setPkSnList(pkSnList);

    }

    private boolean checkCompound(String intensityStr, int peaksNo) {
        if (checkArea != (double) 0) {
            double area = Double.parseDouble(currentCmpd.getM_area().replace(",", "."));
            if (area < checkArea) {
                return false;
            }
        }

        if (checkIntensity != (double) 0) {
            double intensity = Double.parseDouble(intensityStr.replace(",", "."));
            if (intensity < checkIntensity) {
                return false;
            }
        }
        if (checkPeakNo != (double) 0) {
            if (peaksNo < checkPeakNo) {
                return false;
            }
        }
        if (checkRetTimeFrom != (double) 0 || checkRetTimeTo != (double) 0) {
            double retTime = Double.parseDouble(currentCmpd.getRetTime().replace(",", "."));
            if (retTime < checkRetTimeFrom || retTime > checkRetTimeTo) {
                return false;
            }
        }
        return true;
    }

    private void insertIntoMetabolomeMiningTable() {
        List<String> chargeList = currentCmpd.getChargeList();
        for (String charge : chargeList) {

            String[] intensities = currentCmpd.getPkIntensitiesList().get(charge).split(";");
            double maxIntensity = 0;
            for (String intensity : intensities) {
                if (maxIntensity < Double.parseDouble(intensity.replace(",", "."))) {
                    maxIntensity = Double.parseDouble(intensity.replace(",", "."));
                }
            }
            String relIntensityList = "";
            for (String intensity : intensities) {
                double relIntensity = (Double.parseDouble(intensity.replace(",", ".")) * 100) / maxIntensity;
                relIntensityList += String.valueOf(relIntensity) + ";";
            }
            if (relIntensityList.length() > 0) {
                relIntensityList = relIntensityList.substring(0, relIntensityList.length() - 1);
            }

            String inChagre = "";
            if (isRecalculateCharge) {
                String[] mzList = currentCmpd.getPkMzsList().get(charge).split(";");
                List<Double> devMZs = new ArrayList<Double>();
                for (int i = 0; i < mzList.length - 1; i++) {
                    double j = Math.abs(Double.parseDouble(mzList[i].replace(",", ".")) - Double.parseDouble(mzList[i + 1].replace(",", ".")));
                    devMZs.add(j);
                }
                double p = 0;
                int counter = 0;
                for (double d : devMZs) {
                    p += 1 / d;
                    counter++;
                }
                double result = p / (double) counter;
                int res = (int) Math.round(result);
                inChagre = String.valueOf(res);
            } else {
                inChagre = charge;
            }

            List<String> massList = currentCmpd.getPkMassList().get(charge);
            List<String> intensityList = currentCmpd.getPkIntensityList().get(charge);

            if (massList.size() < 5) {
                while (massList.size() != 5) {
                    massList.add("0");
                }
            }
            if (intensityList.size() < 5) {
                while (intensityList.size() != 5) {
                    intensityList.add("0");
                }
            }

            String retTime = currentCmpd.getRetTime();
            if (retTime.length() == 0) {
                retTime = "0";
            } else {
                double ret = Math.round(Double.parseDouble(retTime.replace(",", ".")) * 100) /(double) 100;
                retTime = String.valueOf(ret).replaceAll(",", ".");
            }

            String insertQuery = "insert into S_Metabolite_mining (META_Id,FeatureNr, Retentiontime, Charge, mzlist, Intensitylist, Arealist, SNlist, Reslist, Deconvolutedlist,M1, M2, M3, M4, M5, I1, I2, I3, I4, I5,Rel_Intensitylist,META_key,peaks,m_area,Analyticalsystem)  values ('META"
                    + currentCmpd.getMetaKey() + "','" + currentCmpd.getCmpdKey() + "','" + retTime + "', " + currentCmpd.getPolarity() + inChagre + ",'"
                    + currentCmpd.getPkMzsList().get(charge) + "','" + currentCmpd.getPkIntensitiesList().get(charge) + "','" + currentCmpd.getPkAreaList().get(charge) + "','"
                    + currentCmpd.getPkSnList().get(charge) + "','" + currentCmpd.getPkResList().get(charge) + "','"
                    + currentCmpd.getPkDeconvoluted_molecular_massList().get(charge) + "','" + massList.get(0) + "','" + massList.get(1) + "','" + massList.get(2) + "','"
                    + massList.get(3) + "','" + massList.get(4) + "','" + intensityList.get(0) + "','"
                    + intensityList.get(1) + "','" + intensityList.get(2) + "','" + intensityList.get(3) + "','" + intensityList.get(4) + "','"
                    + relIntensityList + "','" + currentCmpd.getMetaKey() + "','" + currentCmpd.getPkPeakNoList().get(charge) + "','" + currentCmpd.getM_area() + "','"
                    + currentCmpd.getAnalSystem() + "')";
            if (checkCompound(intensityList.get(0), currentCmpd.getPkPeakNoList().get(charge))) {
                try {
                    Statement st = (Statement) connection.createStatement();
                    st.executeUpdate(insertQuery);
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
    }

}
