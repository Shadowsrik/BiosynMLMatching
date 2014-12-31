/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thrift.functions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import thrift.properties.Properties;

/**
 *
 * @author zhos001
 */
public class ParseExperimentParametersAndQueryResults {

    Connection connection = null;
    String querytype = "";
    String queryID = "";
    String queryIDString = "";

    public void parseExperimentParameters(String qryId, String qryIdStr, String parameters, Connection conn) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = saxParserFactory.newSAXParser();
        connection = conn;
        InputStream stream = new ByteArrayInputStream(parameters.getBytes("UTF-8"));
        queryID = qryId;
        queryIDString = qryIdStr;

        DefaultHandler defaultHandler = new DefaultHandler() {
            boolean inputValue = false;
            String inputVal = "";
            String inputType = "";

            public void startElement(String uri, String localName, String qName,
                    Attributes attributes) throws SAXException {
                if (qName.equalsIgnoreCase("query")) {

                    parseQueryAtt(attributes);
                }
                if (qName.equalsIgnoreCase("input")) {
                    inputType = attributes.getValue("type");
                    inputValue = true;
                }
                if (qName.equalsIgnoreCase("parameters")) {
                    parseParametersAtt(attributes);
                }
            }

            public void endElement(String uri, String localName, String qName)
                    throws SAXException {
                if (qName.equalsIgnoreCase("input")) {
                    parseInput(inputType, inputVal);
                }
            }

            public void characters(char ch[], int start, int length)
                    throws SAXException {
                if (inputValue) {
                    inputVal = new String(ch, start, length);
                    inputValue = false;
                }
            }

        };
        saxParser.parse(stream, defaultHandler);
    }

    private void parseQueryAtt(Attributes attributes) {
        querytype = attributes.getValue("type");
    }

    private void parseInput(String inputType, String inputValue) {
        if (inputType.equals("compound")) {

        }
    }

    private void parseParametersAtt(Attributes attributes) {
        String query = "";
        if (querytype.equals("META")) {
            query = "SELECT R_Key,mzlist, Rel_Intensitylist,Retentiontime,m_area,Analyticalsystem,META_Id,peaks,FeatureNr,M1 FROM S_Metabolite_mining ";

            String retTime1 = attributes.getValue("retTime1");
            String retTime2 = attributes.getValue("retTime2");
            String M1_1 = attributes.getValue("M1_1");
            String M1_2 = attributes.getValue("M1_2");
            String analSystem = attributes.getValue("analSystem");

            if (!M1_1.equals(Properties.CHECK_DISABLED) && !M1_2.equals(Properties.CHECK_DISABLED)) {
                query += " where (M1 between '" + M1_1 + "' and '" + M1_2 + "') ";
            }

            if (!retTime1.equals(Properties.CHECK_DISABLED) && !retTime2.equals(Properties.CHECK_DISABLED)) {
                query += " and (Retentiontime between '" + retTime1 + "' and '" + retTime2 + "') ";
            }

            if (!analSystem.equals(Properties.CHECK_DISABLED)) {
                query += " and analyticalsystem = " + analSystem + " ";
            }
        }
        queryResults(query);
    }

    private void queryResults(String query) {
        if (querytype.equals("META")) {
            try {
                Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = stmt.executeQuery(query);
                Map<String, String> metaList = new HashMap<String, String>();
                while (rs.next()) {
                    String resultXml = buildXML(rs.getInt("R_Key"), rs.getString("mzlist"), rs.getString("Rel_Intensitylist"), rs.getDouble("Retentiontime"),
                            rs.getDouble("m_area"), rs.getInt("Analyticalsystem"), rs.getString("META_Id"), rs.getInt("peaks"), rs.getInt("FeatureNr"), rs.getDouble("M1"));
                    metaList.put(rs.getString("META_Id"), resultXml);
                }
                insertIntoResultTable(metaList);
            } catch (SQLException e) {
            } catch (Exception e) {
            }
        }
    }

    private String buildXML(int rKey, String mzlist, String Rel_Intensitylist, double retTime, double mArea,
            int analSystem, String metaID, int peaks, int FeatureNr, double M1) throws ParserConfigurationException, TransformerConfigurationException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        StringWriter sw = new StringWriter();
        // root elements
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("result");
        Attr attrType = doc.createAttribute("type");
        attrType.setValue("META");
        rootElement.setAttributeNode(attrType);
        Attr attrVal = doc.createAttribute("value");
        attrVal.setValue(metaID);
        rootElement.setAttributeNode(attrVal);
        doc.appendChild(rootElement);

        Element parameters = doc.createElement("parameters");
        Attr attrRKey = doc.createAttribute("R_Key");
        attrRKey.setValue(String.valueOf(rKey));
        parameters.setAttributeNode(attrRKey);
        Attr attrMzList = doc.createAttribute("mzlist");
        attrMzList.setValue(mzlist);
        parameters.setAttributeNode(attrMzList);
        Attr attrRelIntenList = doc.createAttribute("relIntensitylist");
        attrRelIntenList.setValue(Rel_Intensitylist);
        parameters.setAttributeNode(attrRelIntenList);
        Attr attrRetTime = doc.createAttribute("retTime");
        attrRetTime.setValue(String.valueOf(retTime));
        parameters.setAttributeNode(attrRetTime);
        Attr attrMArea = doc.createAttribute("mArea");
        attrMArea.setValue(String.valueOf(mArea));
        parameters.setAttributeNode(attrMArea);
        Attr attrAnalSystem = doc.createAttribute("analSystem");
        attrAnalSystem.setValue(String.valueOf(analSystem));
        parameters.setAttributeNode(attrAnalSystem);
        Attr attrPeaks = doc.createAttribute("peaks");
        attrPeaks.setValue(String.valueOf(peaks));
        parameters.setAttributeNode(attrPeaks);
        Attr attrFeatureNr = doc.createAttribute("FeatureNr");
        attrFeatureNr.setValue(String.valueOf(FeatureNr));
        parameters.setAttributeNode(attrFeatureNr);
        Attr attrM1 = doc.createAttribute("M1");
        attrM1.setValue(String.valueOf(M1));
        parameters.setAttributeNode(attrM1);
        rootElement.appendChild(parameters);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(sw);
        transformer.transform(source, result);

        return sw.toString();
    }

    private void insertIntoResultTable(Map<String, String> metaList) {

        for (String metaId : metaList.keySet()) {
            String strainKeyQuery = "select s_MXID from S_Metabolome_data where META_ID = '" + metaId + "'";
            String strainKey = "";
            try {
                Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = stmt.executeQuery(strainKeyQuery);
                while (rs.next()) {
                    strainKey = String.valueOf(rs.getInt("s_MXID"));
                }
            } catch (SQLException e) {
            }
            String insertQuery = "insert into QRY_Results VALUES(Null, '" + queryIDString + "','META','" + metaId + "','" + metaList.get(metaId) + "'," + strainKey + ")";
            try {
                Statement st = (Statement) connection.createStatement();
                st.executeUpdate(insertQuery);
            } catch (SQLException ex) {
            }
        }
    }

}
