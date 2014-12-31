/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hzi.helmholtz.Compare;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import de.hzi.helmholtz.Pathways.Pathway;
import de.hzi.helmholtz.Resources.properties;
import de.hzi.helmholtz.ThriftService.ThriftServer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.thrift.transport.TTransportException;

/**
 *
 * @author srdu001 Get all pathways from the database. Store each pair
 * comparison.
 */
public class SimpleCompare {

    /*db connection*/
    public static Connection connection;

    public static int SizeofQueryPathway = 0;
    public static int SizeofTargetPathwaysInDatabase = 0;
    String driverName;
    String serverName;
    String portNumber;
    String dbname;
    static Logger logger;
    String username;
    String password;

    static FileHandler fh;

    public static Pathway sourcecopy;
    public static Pathway targetcopy;
    private int maxWindowSize = 1;
    public String UniqueJobID;
    /**/
    List<DBPathway> allPathways;
    List<DBPathway> QueryPathway;
    int self = 1;

    public SimpleCompare() {
        System.out.println("Starting SimpleCompare");

        allPathways = new ArrayList<DBPathway>();
        QueryPathway = new ArrayList<DBPathway>();

        // //System.out.println(s);
    }

    public String StartComparison(String jobID, String windowSize, String algorithm, String BSYNID, String modelID, String modulesfromEditor, String comparisonOption, String PathwayOptionForComparison) {
        //Comparison OPtions:
        //0 - with all pathways
        //1 - compare with selected pathway
        //2 - compare with pathways from selected strain
        //
        String returnvalue = "";
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            System.out.println("Thrift started StartComparison ..." + dateFormat.format(cal.getTime()));
            returnvalue = setupConnection();
            if (returnvalue.equalsIgnoreCase("Success")) {
                System.out.println("Connection established ...");
                if (jobID.trim().length() > 1) {
                    if (Integer.parseInt(comparisonOption.trim()) == 0) {
                        getAllPathways();
                    } else if (Integer.parseInt(comparisonOption.trim()) == 1) {
                        getPathwayForComparison(PathwayOptionForComparison);
                    } else if (Integer.parseInt(comparisonOption.trim()) == 2) {
                        getAllPathwaysFromStrain(PathwayOptionForComparison);
                    }
                    System.out.println("Got all Pathways ...");

                    if (BSYNID.trim().length() <= 1 && modulesfromEditor.trim().length() > 1) {
                        // if job is made from editor and submitted to matching function
                        makeQuerypathwayUserInput(modulesfromEditor);
                        // System.out.println(modulesfromEditor);
                    } else {
                        getQueryPathways(BSYNID, modelID);
                    }
                    System.out.println("QueryPathways obtained ...");
                    System.out.println("Started comparing pathways ...");
                    if (QueryPathway.size() > 0) {
                        comparePathways(algorithm, windowSize, jobID);
                    } else {
                        System.out.println("No query Pathways found...");
                    }
                } else {
                    connection.close();
                }
            } else {
                connection.close();
            }
        } catch (Exception ex) {
            System.out.println(ex);
            Logger.getLogger(ThriftServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return returnvalue;
    }

    public void initializeProperties() {
        try {
            serverName = properties.server;
            portNumber = properties.port;
            dbname = properties.dbname;
            username = properties.username;
            password = properties.passwd;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String setupConnection() {
        String returnValue = "";
        try {
            initializeProperties();
            // Load the JDBC driver
            // System.out.println(serverName + "       " + portNumber + "          " + dbname + "         " + username + "          " + password);
            driverName = "com.mysql.jdbc.Driver";
            Class.forName(driverName);
            //  connection = DriverManager.getConnection("jdbc:mysql://" + properties.MXBASE_SERVER + ":" + properties.port + "/" + properties.dbname + "?" + "user=" + properties.username + "&password=" + properties.passwd + "");
            connection = DriverManager.getConnection("jdbc:mysql://" + properties.server + ":" + properties.port + "/" + properties.dbname + "?" + "user=" + properties.testusername + "&password=" + properties.testpasswd + "");

            // connection = DriverManager.getConnection("jdbc:mysql://" + properties.MXBASE_SERVER + ":" + properties.port + "/" + properties.dbname + "?" + "user=" + properties.username + "&password=" + properties.passwd + "");
            // String url = "jdbc:mysql://" + serverName + "/" + dbname + "?";
            // connection = DriverManager.getConnection(url, username, password);
            if (connection == null) {
                System.out.println("FATAL: Could not establish database connection!");
                System.exit(1);
            }
            System.out.println("connected ..........");
            connection.setAutoCommit(false);
            returnValue = "Success";
        } catch (Exception e) {
            e.printStackTrace();
            returnValue = e.toString();
        } finally {

        }
        return returnValue;
    }

    public void getAllPathwaysFromStrain(String strainid) throws Exception {
        DBPathway dbP;
        Map<Integer, List<String>> pathway = new TreeMap<Integer, List<String>>();
        if (connection == null || connection.isClosed()) {
            setupConnection();
        }
        String groupconcatStatement = "SET @@group_concat_max_len=9182";
        try (PreparedStatement groupconcatPreparedStatement = connection.prepareStatement(groupconcatStatement)) {
            try {
                if (connection == null || connection.isClosed()) {
                    setupConnection();
                }
                groupconcatPreparedStatement.execute();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (groupconcatPreparedStatement != null) {
                    groupconcatPreparedStatement.close();
                }
            }
        }

        String selectStatement = "select concat(a.BSYN_ID,'_',a.B_model) as pathway ,T.B_gene, T.gene from (select BSYN_ID,B_model,B_gene,GROUP_CONCAT(concat(IF(B_domian = '','?',B_domian),'_',B_active,'_',\n"
                + "IF( ExtractValue(B_buildingblock_xml, '/buildingblock/moiety/name') = '', 'nil', ExtractValue(B_buildingblock_xml, '/buildingblock/moiety/name') ))) as gene from B_BiosyntheticPathways\n"
                + "group by BSYN_ID,B_model, B_gene order by B_key) T join B_BiosynthesisModels a on a.BSYN_ID = T.BSYN_ID where  a.s_MxID = " + strainid.trim() + " order by T.BSYN_ID, T.B_model, T.B_gene ";
        // String selectStatement = "select concat(a.BSYN_ID,'_',a.B_model) as pathway ,T.B_gene, T.gene from (select BSYN_ID,B_model,B_gene,GROUP_CONCAT(concat(IF(B_domian = '','?',B_domian),'_',B_active,'_',IF( ExtractValue(B_buildingblock_xml, '/buildingblock/moiety/name') = '', 'nil', ExtractValue(B_buildingblock_xml, '/buildingblock/moiety/name') ))) as gene from B_BiosyntheticPathways group by BSYN_ID,B_model, B_gene order by B_key) T join B_BiosynthesisModels a on a.BSYN_ID = T.BSYN_ID where  a.BSRC_ID = '" + QueryID.trim() + "' order by T.BSYN_ID, T.B_model, T.B_gene";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectStatement)) {
            try {
                if (connection == null || connection.isClosed()) {
                    setupConnection();
                }
                ResultSet rSet = preparedStatement.executeQuery();

                List<String> gene = new ArrayList<String>();
                String currPathwayId = "BSYN1_1";
                while (rSet.next()) {
                    String pathwayId = rSet.getString(1);
                    int geneId = rSet.getInt(2);
                    String geneContent = rSet.getString(3);
                    if (pathwayId.equalsIgnoreCase(currPathwayId)) {
                        // we are in current pathway
                        gene = Arrays.asList(geneContent.split(","));
                        pathway.put(geneId, gene);
                    } else {
                        dbP = new DBPathway(currPathwayId, pathway);
                        allPathways.add(dbP);
                        currPathwayId = pathwayId;
                        pathway = new TreeMap<Integer, List<String>>();
                        gene = Arrays.asList(geneContent.split(","));

                        pathway.put(geneId, gene);
                    }
                }
                dbP = new DBPathway(currPathwayId, pathway);
                allPathways.add(dbP);
                ////System.out.println(allPathways.size());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            }
        }
    }

    public void getAllPathways() throws Exception {
        DBPathway dbP;
        Map<Integer, List<String>> pathway = new TreeMap<Integer, List<String>>();
        if (connection == null || connection.isClosed()) {
            setupConnection();
        }
        String groupconcatStatement = "SET @@group_concat_max_len=9182";
        try (PreparedStatement groupconcatPreparedStatement = connection.prepareStatement(groupconcatStatement)) {
            try {
                if (connection == null || connection.isClosed()) {
                    setupConnection();
                }
                groupconcatPreparedStatement.execute();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (groupconcatPreparedStatement != null) {
                    groupconcatPreparedStatement.close();
                }
            }
        }

        String selectStatement = "select concat(BSYN_ID,'_',B_model) as pathway ,B_gene, gene from (select BSYN_ID,B_model,B_gene,GROUP_CONCAT(concat(IF(B_domian = '','?',B_domian),'_',B_active,'_', IF( ExtractValue(B_buildingblock_xml, '/buildingblock/moiety/name') = '', 'nil', ExtractValue(B_buildingblock_xml, '/buildingblock/moiety/name') ))) as gene from B_BiosyntheticPathways group by BSYN_ID,B_model, B_gene order by B_key) T order by BSYN_ID, B_model, B_gene";
        // String selectStatement = "select concat(a.BSYN_ID,'_',a.B_model) as pathway ,T.B_gene, T.gene from (select BSYN_ID,B_model,B_gene,GROUP_CONCAT(concat(IF(B_domian = '','?',B_domian),'_',B_active,'_',IF( ExtractValue(B_buildingblock_xml, '/buildingblock/moiety/name') = '', 'nil', ExtractValue(B_buildingblock_xml, '/buildingblock/moiety/name') ))) as gene from B_BiosyntheticPathways group by BSYN_ID,B_model, B_gene order by B_key) T join B_BiosynthesisModels a on a.BSYN_ID = T.BSYN_ID where  a.BSRC_ID = '" + QueryID.trim() + "' order by T.BSYN_ID, T.B_model, T.B_gene";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectStatement)) {
            try {
                if (connection == null || connection.isClosed()) {
                    setupConnection();
                }
                ResultSet rSet = preparedStatement.executeQuery();

                List<String> gene = new ArrayList<String>();
                String currPathwayId = "BSYN1_1";
                while (rSet.next()) {
                    String pathwayId = rSet.getString(1);
                    int geneId = rSet.getInt(2);
                    String geneContent = rSet.getString(3);
                    if (pathwayId.equalsIgnoreCase(currPathwayId)) {
                        // we are in current pathway
                        gene = Arrays.asList(geneContent.split(","));
                        pathway.put(geneId, gene);
                    } else {
                        dbP = new DBPathway(currPathwayId, pathway);
                        allPathways.add(dbP);
                        currPathwayId = pathwayId;
                        pathway = new TreeMap<Integer, List<String>>();
                        gene = Arrays.asList(geneContent.split(","));

                        pathway.put(geneId, gene);
                    }
                }
                dbP = new DBPathway(currPathwayId, pathway);
                allPathways.add(dbP);
                ////System.out.println(allPathways.size());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            }
        }
    }

    public List<DBPathway> getQueryPathways(String QueryID, String ModelID) throws Exception {
        DBPathway dbP;
        String selectStatement = "";
        try {
            if (connection == null || connection.isClosed()) {
                setupConnection();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String groupconcatStatement = "SET @@group_concat_max_len=9182";
        try (PreparedStatement groupconcatPreparedStatement = connection.prepareStatement(groupconcatStatement)) {
            try {
                if (connection == null || connection.isClosed()) {
                    setupConnection();
                }
                groupconcatPreparedStatement.execute();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (groupconcatPreparedStatement != null) {
                    groupconcatPreparedStatement.close();
                }
            }
        }
        Map<Integer, List<String>> QueryPathways = new TreeMap<Integer, List<String>>();

        ////System.out.println(ModelID);
        if (ModelID.equalsIgnoreCase("All")) {
            selectStatement = "select concat(a.BSYN_ID,'_',a.B_model) as pathway ,T.B_gene, T.gene from (select BSYN_ID,B_model,B_gene,GROUP_CONCAT(concat(IF(B_domian = '','?',B_domian),'_',B_active,'_',\n"
                    + "IF( ExtractValue(B_buildingblock_xml, '/buildingblock/moiety/name') = '', 'nil', ExtractValue(B_buildingblock_xml, '/buildingblock/moiety/name') ))) as gene from B_BiosyntheticPathways\n"
                    + "group by BSYN_ID,B_model, B_gene order by B_key) T join B_BiosynthesisModels a on a.BSYN_ID = T.BSYN_ID where  a.BSRC_ID = '" + QueryID.trim() + "' order by T.BSYN_ID, T.B_model, T.B_gene";

        } else {
            selectStatement = "select concat(a.BSYN_ID,'_',a.B_model) as pathway ,T.B_gene, T.gene from\n"
                    + "(select BSYN_ID,B_model,B_gene,GROUP_CONCAT(concat(IF(B_domian = '','?',B_domian),'_',B_active,'_',\n"
                    + "IF( ExtractValue(B_buildingblock_xml, '/buildingblock/moiety/name') = '', 'nil', ExtractValue(B_buildingblock_xml, '/buildingblock/moiety/name') ))) as gene from B_BiosyntheticPathways\n"
                    + "group by BSYN_ID,B_model, B_gene order by B_key) T join B_BiosynthesisModels a\n"
                    + "on a.BSYN_ID = T.BSYN_ID where  a.BSRC_ID  = '" + QueryID.trim() + "' and a.B_model = '" + ModelID.trim() + "' order by T.BSYN_ID, T.B_model, T.B_gene";
        }
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectStatement)) {
            try {
                if (connection == null || connection.isClosed()) {
                    setupConnection();
                }
                ResultSet rSet = preparedStatement.executeQuery();

                List<String> gene = new ArrayList<String>();
                int p = 0;
                String currPathwayId = "";
                while (rSet.next()) {
                    if (p == 0) {
                        currPathwayId = rSet.getString(1);
                        p = 1;
                    }
                    String pathwayId = rSet.getString(1);

                    int geneId = rSet.getInt(2);
                    String geneContent = rSet.getString(3);
                    ////System.out.println(geneContent);
                    if (pathwayId.equalsIgnoreCase(currPathwayId)) {
                        // we are in current pathway
                        gene = Arrays.asList(geneContent.split(","));
                        QueryPathways.put(geneId, gene);
                    } else {
                        dbP = new DBPathway(currPathwayId, QueryPathways);
                        QueryPathway.add(dbP);
                        currPathwayId = pathwayId;
                        QueryPathways = new TreeMap<Integer, List<String>>();
                        gene = Arrays.asList(geneContent.split(","));

                        QueryPathways.put(geneId, gene);
                        //QueryPathway.add(QueryPathways);
                    }
                }
                dbP = new DBPathway(currPathwayId, QueryPathways);
                QueryPathway.add(dbP);
                ////System.out.println(QueryPathway.size());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            }

        }
        return QueryPathway;
    }

    public void comparePathwaystoEverything(String ALGORITHM, String windowsize, String ProcessID) {
        try {
            if (connection == null || connection.isClosed()) {
                setupConnection();
            }
            Iterator<DBPathway> firstIter = allPathways.iterator();
            Iterator<DBPathway> secondIter = allPathways.iterator();
            SizeofTargetPathwaysInDatabase = allPathways.size();
            maxWindowSize = Integer.parseInt(windowsize.trim());
            UniqueJobID = ProcessID;

            while (firstIter.hasNext()) {
                DBPathway source = firstIter.next();

                //secondIter.next();
                ////System.out.println("**************************************************");
                secondIter = allPathways.iterator();
                while (secondIter.hasNext()) {

                    SizeofQueryPathway = 0;
                    DBPathway target = secondIter.next();
                    ////System.out.println(source.getPathwayID() + " && " + target.getPathwayID());

                    Map<Integer, Integer> srcGeneIdToPositionMap = new TreeMap<Integer, Integer>();
                    int temp = 0;

                    sourcecopy = new Pathway(source.convertToPathwayObj());
                    targetcopy = new Pathway(target.convertToPathwayObj());
                    for (Map.Entry<Integer, List<String>> e : source.getPathway().entrySet()) {

                        SizeofQueryPathway += e.getValue().size();
                        srcGeneIdToPositionMap.put(e.getKey(), temp++);
                    }
                    Map<Integer, Integer> tgtGeneIdToPositionMap = new TreeMap<Integer, Integer>();
                    temp = 0;
                    for (Map.Entry<Integer, List<String>> e : target.getPathway().entrySet()) {
                        tgtGeneIdToPositionMap.put(e.getKey(), temp++);
                    }
                    // source.printPathway();
                    //    target.printPathway();
                    
                    if (self == 1) {
                        PathwayComparison pComparison = new PathwayComparison(source.convertToPathwayObj(), source.convertToPathwayObj(), maxWindowSize, UniqueJobID, ALGORITHM, self);
                        self = 0;
                    } else {
                        PathwayComparison pComparison = new PathwayComparison(source.convertToPathwayObj(), target.convertToPathwayObj(), maxWindowSize, UniqueJobID, ALGORITHM, self);
                    }

                    //PathwayComparison pComparison = new PathwayComparison(target.convertToPathwayObj(), source.convertToPathwayObj());
                    //
                    //  ////System.out.println(SizeofQueryPathway + "                 " + SizeofTargetPathwaysInDatabase);
                    //break;
                }

                System.out.println("**************************************************");
            }

            System.out.println("Done ... Enjoy with your results");
        } catch (Exception ex) {
            System.out.println("Error in SimpleCompare:" + ex);
            Logger
                    .getLogger(ThriftServer.class
                            .getName()).log(Level.SEVERE, null, ex);

        } finally {
            try {
                connection.close();

            } catch (SQLException ex) {
                Logger.getLogger(SimpleCompare.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void comparePathways(String ALGORITHM, String windowsize, String ProcessID) {
        try {

            Iterator<DBPathway> firstIter = QueryPathway.iterator();
            Iterator<DBPathway> secondIter = allPathways.iterator();
            SizeofTargetPathwaysInDatabase = allPathways.size();
            maxWindowSize = Integer.parseInt(windowsize.trim());
            UniqueJobID = ProcessID;

            while (firstIter.hasNext()) {
                DBPathway source = firstIter.next();
                self = 1;
                //secondIter.next();
                ////System.out.println("**************************************************");
                secondIter = allPathways.iterator();
                while (secondIter.hasNext()) {

                    SizeofQueryPathway = 0;
                    DBPathway target = secondIter.next();
                    ////System.out.println(source.getPathwayID() + " && " + target.getPathwayID());

                    Map<Integer, Integer> srcGeneIdToPositionMap = new TreeMap<Integer, Integer>();
                    int temp = 0;

                    sourcecopy = new Pathway(source.convertToPathwayObj());
                    targetcopy = new Pathway(target.convertToPathwayObj());
                    for (Map.Entry<Integer, List<String>> e : source.getPathway().entrySet()) {

                        SizeofQueryPathway += e.getValue().size();
                        srcGeneIdToPositionMap.put(e.getKey(), temp++);
                    }
                    Map<Integer, Integer> tgtGeneIdToPositionMap = new TreeMap<Integer, Integer>();
                    temp = 0;
                    for (Map.Entry<Integer, List<String>> e : target.getPathway().entrySet()) {
                        tgtGeneIdToPositionMap.put(e.getKey(), temp++);
                    }
                    // source.printPathway();
                    //    target.printPathway();
                    if (self == 1) {
                        PathwayComparison pComparison = new PathwayComparison(source.convertToPathwayObj(), source.convertToPathwayObj(), maxWindowSize, UniqueJobID, ALGORITHM, self);
                        self = 0;
                    } else {
                        PathwayComparison pComparison = new PathwayComparison(source.convertToPathwayObj(), target.convertToPathwayObj(), maxWindowSize, UniqueJobID, ALGORITHM, self);
                    }

                    //PathwayComparison pComparison = new PathwayComparison(target.convertToPathwayObj(), source.convertToPathwayObj());
                    //
                    //  ////System.out.println(SizeofQueryPathway + "                 " + SizeofTargetPathwaysInDatabase);
                    //break;
                }

                System.out.println("**************************************************");
            }

            System.out.println("Done ... Enjoy with your results");
        } catch (Exception ex) {
            System.out.println("Error in SimpleCompare:" + ex);
            Logger
                    .getLogger(ThriftServer.class
                            .getName()).log(Level.SEVERE, null, ex);

        } finally {
            try {
                connection.close();

            } catch (SQLException ex) {
                Logger.getLogger(SimpleCompare.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private Pathway makeQuerypathwayUserInput(String pathway) {
        Multimap<Integer, String> map = ArrayListMultimap.create();
        Map<Integer, List<String>> data_pathway = new HashMap<Integer, List<String>>();
        String same = "";
        String[] domains = pathway.split(",");
        int id = 0, count = 0;
        List<String> domains_dat = new ArrayList<String>();
        List<Map.Entry<Integer, String>> keypair = new ArrayList<Map.Entry<Integer, String>>();
        HashMap<Integer, List<String>> maping1 = new HashMap<Integer, List<String>>();
        for (String i : domains) {
            String[] data = i.split(":");
            map.put(Integer.parseInt(data[0].trim()), data[1].trim());
        }
        int c = 0;
        for (Map.Entry<Integer, String> i1 : map.entries()) {
            c = c + 1;
            if (map.containsKey(c)) {
                Collection<String> myValues = map.get(c);
                domains_dat = new ArrayList<String>();
                domains_dat.addAll(myValues);
                data_pathway.put(c, domains_dat);
            } else {
                break;
            }
        }
        DBPathway source = new DBPathway("1_1", data_pathway);
        Pathway s = new Pathway();
        QueryPathway.add(source);
        //JOptionPane.showMessageDialog(null, QueryPathway, new Throwable().getStackTrace()[0].getLineNumber() + " sdsdsd321321", JOptionPane.INFORMATION_MESSAGE);
        return s;
    }

    private List<DBPathway> getPathwayForComparison(String PathwayIdForComparison) throws SQLException {
        DBPathway dbP;
        String selectStatement = "";
        try {
            if (connection == null || connection.isClosed()) {
                setupConnection();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String groupconcatStatement = "SET @@group_concat_max_len=9182";
        try (PreparedStatement groupconcatPreparedStatement = connection.prepareStatement(groupconcatStatement)) {
            try {
                if (connection == null || connection.isClosed()) {
                    setupConnection();
                }
                groupconcatPreparedStatement.execute();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (groupconcatPreparedStatement != null) {
                    groupconcatPreparedStatement.close();
                }
            }
        }
        Map<Integer, List<String>> QueryPathways = new TreeMap<Integer, List<String>>();

        ////System.out.println(ModelID);
        selectStatement = "select concat(a.BSYN_ID,'_',a.B_model) as pathway ,T.B_gene, T.gene from (select BSYN_ID,B_model,B_gene,GROUP_CONCAT(concat(IF(B_domian = '','?',B_domian),'_',B_active,'_',\n"
                + "IF( ExtractValue(B_buildingblock_xml, '/buildingblock/moiety/name') = '', 'nil', ExtractValue(B_buildingblock_xml, '/buildingblock/moiety/name') ))) as gene from B_BiosyntheticPathways\n"
                + "group by BSYN_ID,B_model, B_gene order by B_key) T join B_BiosynthesisModels a on a.BSYN_ID = T.BSYN_ID where  a.BSRC_ID = '" + PathwayIdForComparison.trim() + "' order by T.BSYN_ID, T.B_model, T.B_gene";

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectStatement)) {
            try {
                if (connection == null || connection.isClosed()) {
                    setupConnection();
                }
                ResultSet rSet = preparedStatement.executeQuery();

                List<String> gene = new ArrayList<String>();
                int p = 0;
                String currPathwayId = "";
                while (rSet.next()) {
                    if (p == 0) {
                        currPathwayId = rSet.getString(1);
                        p = 1;
                    }
                    String pathwayId = rSet.getString(1);

                    int geneId = rSet.getInt(2);
                    String geneContent = rSet.getString(3);
                    ////System.out.println(geneContent);
                    if (pathwayId.equalsIgnoreCase(currPathwayId)) {
                        // we are in current pathway
                        gene = Arrays.asList(geneContent.split(","));
                        QueryPathways.put(geneId, gene);
                    } else {
                        dbP = new DBPathway(currPathwayId, QueryPathways);
                        allPathways.add(dbP);
                        currPathwayId = pathwayId;
                        QueryPathways = new TreeMap<Integer, List<String>>();
                        gene = Arrays.asList(geneContent.split(","));

                        QueryPathways.put(geneId, gene);
                        //QueryPathway.add(QueryPathways);
                    }
                }
                dbP = new DBPathway(currPathwayId, QueryPathways);
                allPathways.add(dbP);
                ////System.out.println(QueryPathway.size());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            }

        }
        return allPathways;
    }

    public void addToMap(HashMap<Integer, List<String>> map, Integer key, String value) {
        if (!map.containsKey(key)) {
            map.put(key, new ArrayList<String>());
        }
        map.get(key).add(value);
    }

    public static void main(String[] args) throws Exception {

        /*   ThriftServer server = new ThriftServer();
         try {
         DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
         Calendar cal = Calendar.getInstance();
         System.out.println("BiosynThrift started:  " + dateFormat.format(cal.getTime())); //2014/08/06 16:00:22
         server.init("8282");
         } catch (InterruptedException ex) {
         Logger.getLogger(ThriftServer.class.getName()).log(Level.SEVERE, null, ex);
         } catch (TTransportException ex) {
         Logger.getLogger(ThriftServer.class.getName()).log(Level.SEVERE, null, ex);
         }
         if (args.length < 4) {
         //  JOptionPane.showMessageDialog(null, "noarfs", new Throwable().getStackTrace()[0].getLineNumber() + " sdsdsd321321", JOptionPane.INFORMATION_MESSAGE);
         } else {
         try {
         //String jobID, String windowSize, String algorithm, String BSYNID, String modelID)

         // JOptionPane.showMessageDialog(null, args, new Throwable().getStackTrace()[0].getLineNumber() + " sdsdsd321321", JOptionPane.INFORMATION_MESSAGE);
         //
         SimpleCompare compare = new SimpleCompare();
         compare.StartComparison(args[0], args[1], args[2], args[3], args[4], args[5]);

         //System.out.println(s);
         } catch (Exception ds) {
         //  JOptionPane.showMessageDialog(null, ds.toString(), new Throwable().getStackTrace()[0].getLineNumber() + " sdsdsd321321", JOptionPane.INFORMATION_MESSAGE);
         }
         }*/
//"1:KS_active_nil,1:AT_active_nil,1:DH_active_nil,1:ER_active_nil,1:KR_active_nil,1:ACP_active_nil"
         SimpleCompareWithModules compare = new SimpleCompareWithModules();
     //   SimpleCompare compare;
       // compare = new SimpleCompare();
        // compare.getAllPathways();
        // compare.comparePathwaystoEverything("3", "3","EverythingToEverything");
     //   compare.StartComparison("1112", "3", "3", "BSRC32", "7", "1", "0", "0");
          compare.setupConnection();
     compare.getAllPathways();
           compare.getQueryPathways("BSYN1", "1");
     compare.comparePathways("2", "3", "4");
    }
}
