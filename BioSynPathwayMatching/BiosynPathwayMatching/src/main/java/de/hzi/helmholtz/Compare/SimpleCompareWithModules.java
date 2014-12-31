/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hzi.helmholtz.Compare;

import de.hzi.helmholtz.Pathways.Pathway;
import de.hzi.helmholtz.Pathways.PathwayWithModules;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

/**
 *
 * @author srdu001
 */
public class SimpleCompareWithModules {
    /*db connection*/

    public Connection connection;
    public String UniqueJobID;
    public static int SizeofQueryPathway = 0;
    public static int SizeofTargetPathwaysInDatabase = 0;
    String driverName;
    String serverName;
    String portNumber;
    String dbname;
    String username;
    String password;
    Properties props = null;
    String dbPropertiesFile;
    public static PathwayWithModules sourcecopy;
    public static PathwayWithModules targetcopy;
    private int maxWindowSize = 1;
    /**/
    List<DBPathwayWithModules> allPathways;
    List<DBPathwayWithModules> QueryPathway;

    public SimpleCompareWithModules() {
        props = new Properties();
        dbPropertiesFile = "database.properties";
        allPathways = new ArrayList<DBPathwayWithModules>();
        QueryPathway = new ArrayList<DBPathwayWithModules>();
    }

    public Properties initializeProperties() {
        //props = new Properties();
        try {

            if (dbPropertiesFile != null) {

              
            } else {
                //System.out.println("Properties stream not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return props;
    }

    public void setupConnection() {
        try {
            
            // Load the JDBC driver
            driverName = "com.mysql.jdbc.Driver";
            Class.forName(driverName);
              serverName = "mxbase.pharma.uni-sb.de";
                portNumber = "3306";
                dbname = "Myxobase_Test";
                username = "srdu01_UT_z34GHq";
                password = "srdumx";
            String url = "jdbc:mysql://" + serverName + "/" + dbname + "?";
            connection = DriverManager.getConnection(url, username, password);
            if (connection == null) {
                //System.out.println("FATAL: Could not establish database connection!");
                System.exit(1);
            }
            connection.setAutoCommit(false);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (e.getMessage().indexOf("ORA-12519") != -1) {
                    //sleep for a while
                    //System.out.println("Sleeping...");
                    Thread.sleep(500);
                } else {
                    Thread.sleep(1000);
                }
                //setupConnection();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public void getAllPathways() throws Exception {
        DBPathwayWithModules dbP;

        Map<Integer, Map<String, List<String>>> pathway = new TreeMap<Integer, Map<String, List<String>>>();
        Map<String, List<String>> moduleslist = new TreeMap<String, List<String>>();
        String groupconcatStatement = "SET @@group_concat_max_len=9182";
        PreparedStatement groupconcatPreparedStatement = connection.prepareStatement(groupconcatStatement);
        try {
            if (connection == null || connection.isClosed()) {
                setupConnection();
            }
            groupconcatPreparedStatement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String selectStatement = "select concat(BSYN_ID,'_',B_model),B_gene, gene,B_module from (select BSYN_ID,B_model,B_gene,GROUP_CONCAT(concat(IF(B_domian = '','?',B_domian),'_',B_active,'_',IF( ExtractValue(B_buildingblock_xml, '/buildingblock/moiety/name') = '', 'nil',   ExtractValue(B_buildingblock_xml, '/buildingblock/moiety/name') ))) as gene,B_module from B_BiosyntheticPathways group by BSYN_ID,B_model, B_gene,B_module order by B_key) T ";
        PreparedStatement preparedStatement = connection.prepareStatement(selectStatement);
        try {
            if (connection == null || connection.isClosed()) {
                setupConnection();
            }
            ResultSet rSet = preparedStatement.executeQuery();

            List<String> gene = new ArrayList<String>();
            String currPathwayId = "BSYN1_1";
            List<String> module = new ArrayList<String>();
            int modulecount = 0;
            int currGeneId = -1;
            while (rSet.next()) {
                String pathwayId = rSet.getString(1);
                int geneId = rSet.getInt(2);
                String geneContent = rSet.getString(3);
                String moduleID = rSet.getString(4);
                if (pathwayId.equalsIgnoreCase(currPathwayId)) {
                    if (moduleID.trim().equals("")) {
                        modulecount++;
                        moduleID = "created" + modulecount;
                    }
                    if (currGeneId == (geneId)) {
                        module = Arrays.asList(geneContent.split(","));
                        moduleslist.put(moduleID, module);
                    } else {
                        moduleslist = new TreeMap<String, List<String>>();
                        currGeneId = (geneId);
                        module = Arrays.asList(geneContent.split(","));
                        moduleslist.put(moduleID, module);
                        pathway.put(geneId, moduleslist);
                    }
                } else {
                    dbP = new DBPathwayWithModules(currPathwayId, pathway);
                    allPathways.add(dbP);
                    if (moduleID.trim().equals("")) {
                        modulecount++;
                        moduleID = "created" + modulecount;
                    }
                    currPathwayId = pathwayId;
                    pathway = new TreeMap<Integer, Map<String, List<String>>>();
                    if (currGeneId == (geneId)) {
                        module = Arrays.asList(geneContent.split(","));
                        moduleslist.put(moduleID, module);
                    } else {
                        moduleslist = new TreeMap<String, List<String>>();
                        currGeneId = (geneId);
                        module = Arrays.asList(geneContent.split(","));
                        moduleslist.put(moduleID, module);
                        pathway.put(geneId, moduleslist);
                    }
                }
            }
            dbP = new DBPathwayWithModules(currPathwayId, pathway);
            allPathways.add(dbP);
            //System.out.println(allPathways.size());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    public List<DBPathwayWithModules> getQueryPathways(String QueryID, String ModelID) throws Exception {
        DBPathwayWithModules dbP;
        String selectStatement = "";
        try {
            if (connection == null || connection.isClosed()) {
                setupConnection();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String groupconcatStatement = "SET @@group_concat_max_len=9182";
        PreparedStatement groupconcatPreparedStatement = connection.prepareStatement(groupconcatStatement);
        try {
            if (connection == null || connection.isClosed()) {
                setupConnection();
            }
            groupconcatPreparedStatement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //  Map<Integer, List<String>> QueryPathways = new TreeMap<Integer, List<String>>();
        Map<Integer, Map<String, List<String>>> QueryPathways = new TreeMap<Integer, Map<String, List<String>>>();
        Map<String, List<String>> moduleslist = new TreeMap<String, List<String>>();

        //System.out.println(ModelID);
        if (ModelID.equalsIgnoreCase("All")) {
            selectStatement = "select concat(BSYN_ID,'_',B_model),B_gene, gene,B_module from (select BSYN_ID,B_model,B_gene,GROUP_CONCAT(concat(IF(B_domian = '','?',B_domian),'_',B_active,'_',IF( ExtractValue(B_buildingblock_xml, '/buildingblock/moiety/name') = '', 'nil',   ExtractValue(B_buildingblock_xml, '/buildingblock/moiety/name') ))) as gene,B_module from B_BiosyntheticPathways group by BSYN_ID,B_model, B_gene,B_module order by B_key) T where BSYN_ID = '" + QueryID.trim() + "'";

        } else {
            selectStatement = "select concat(BSYN_ID,'_',B_model),B_gene, gene,B_module from (select BSYN_ID,B_model,B_gene,GROUP_CONCAT(concat(IF(B_domian = '','?',B_domian),'_',B_active,'_',IF( ExtractValue(B_buildingblock_xml, '/buildingblock/moiety/name') = '', 'nil',   ExtractValue(B_buildingblock_xml, '/buildingblock/moiety/name') ))) as gene,B_module from B_BiosyntheticPathways group by BSYN_ID,B_model, B_gene,B_module order by B_key) T where BSYN_ID = '" + QueryID.trim() + "' and B_model = '" + ModelID.trim() + "'";
        }
        PreparedStatement preparedStatement = connection.prepareStatement(selectStatement);
        try {
            if (connection == null || connection.isClosed()) {
                setupConnection();
            }
            ResultSet rSet = preparedStatement.executeQuery();
            int currGeneId = -1;
            int modulecount = 0;
            List<String> gene = new ArrayList<String>();
            List<String> module = new ArrayList<String>();
            int p = 0;
            String currPathwayId = "";

            while (rSet.next()) {
                String pathwayId = rSet.getString(1);
                int geneId = rSet.getInt(2);
                String geneContent = rSet.getString(3);
                String moduleID = rSet.getString(4);
                if (pathwayId.equalsIgnoreCase(currPathwayId)) {
                    if (moduleID.trim().equals("")) {
                        modulecount++;
                        moduleID = "created" + modulecount;

                    }
                    if (currGeneId == (geneId)) {
                        module = Arrays.asList(geneContent.split(","));
                        moduleslist.put(moduleID, module);
                    } else {
                        moduleslist = new TreeMap<String, List<String>>();
                        currGeneId = (geneId);
                        module = Arrays.asList(geneContent.split(","));
                        moduleslist.put(moduleID, module);
                        QueryPathways.put(geneId, moduleslist);
                    }
                } else {
                    if(QueryPathways.size()>0)
                    {
                    dbP = new DBPathwayWithModules(currPathwayId, QueryPathways);
                    QueryPathway.add(dbP);
                    }
                    if (moduleID.trim().equals("")) {
                        modulecount++;
                        moduleID = "created" + modulecount;

                    }
                    System.out.println(QueryPathways);
                    currPathwayId = pathwayId;
                    QueryPathways = new TreeMap<Integer, Map<String, List<String>>>();
                    if (currGeneId == (geneId)) {
                        module = Arrays.asList(geneContent.split(","));
                        moduleslist.put(moduleID, module);
                    } else {
                        moduleslist = new TreeMap<String, List<String>>();
                        currGeneId = (geneId);
                        module = Arrays.asList(geneContent.split(","));
                        moduleslist.put(moduleID, module);

                        QueryPathways.put(geneId, moduleslist);

                    }
                }
            }
            dbP = new DBPathwayWithModules(currPathwayId, QueryPathways);
            QueryPathway.add(dbP);
            //System.out.println(QueryPathway.size());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        return QueryPathway;
    }

    public String comparePathways(String ALGORITHM, String windowsize, String ProcessID) {
        Iterator<DBPathwayWithModules> firstIter = QueryPathway.iterator();
        Iterator<DBPathwayWithModules> secondIter = allPathways.iterator();
        SizeofTargetPathwaysInDatabase = allPathways.size();
        maxWindowSize = Integer.parseInt(windowsize.trim());
        UniqueJobID = ProcessID;
        while (firstIter.hasNext()) {
            DBPathwayWithModules source = firstIter.next();
            //secondIter.next();
            //System.out.println("**************************************************");
            secondIter = allPathways.iterator();
            while (secondIter.hasNext()) {

                SizeofQueryPathway = 0;
                DBPathwayWithModules target = secondIter.next();
                //System.out.println(source.getPathwayID() + " && " + target.getPathwayID());

                Map<Integer, Integer> srcGeneIdToPositionMap = new TreeMap<Integer, Integer>();
                int temp = 0;

                sourcecopy = new PathwayWithModules(source.convertToPathwayObj());
                targetcopy = new PathwayWithModules(target.convertToPathwayObj());
                for (Map.Entry<Integer, Map<String, List<String>>> entry : source.getPathway().entrySet()) {
                    int geneId = entry.getKey();
                    for (Map.Entry<String, List<String>> e : entry.getValue().entrySet()) {
                        SizeofQueryPathway += e.getValue().size();
                    }
                    srcGeneIdToPositionMap.put(geneId, temp++);
                }
                Map<Integer, Integer> tgtGeneIdToPositionMap = new TreeMap<Integer, Integer>();
                temp = 0;
                for (Map.Entry<Integer, Map<String, List<String>>> entry : target.getPathway().entrySet()) {
                    int geneId = entry.getKey();
                    tgtGeneIdToPositionMap.put(geneId, temp++);
                }
                source.printPathway();
                target.printPathway();

                PathwayComparisonWithModules pComparison = new PathwayComparisonWithModules(source.convertToPathwayObj(), target.convertToPathwayObj(), maxWindowSize, UniqueJobID);
                pComparison.SubsetMatchingOfPathways();
             //   PathwayComparisonWithModules pComparison = new PathwayComparisonWithModules(target.convertToPathwayObj(), source.convertToPathwayObj());
                //
                //  //System.out.println(SizeofQueryPathway + "                 " + SizeofTargetPathwaysInDatabase);
              /*  if (ALGORITHM.equals("0")) {
                 pComparison.pathwayComparisonGlobalBestGreedy();
                 } else if (ALGORITHM.equals("1")) {
                 pComparison.pathwayComparisonGlobalBestCoalesce();
                 } else {
                 pComparison.SubsetMatchingOfPathways();
                 }*/
                //break;

            }

            System.out.println("**************************************************");
        }
        System.out.println("done");
        //  JOptionPane.showMessageDialog(null, "Done", "less arguments" + " sdsdsd321321", JOptionPane.INFORMATION_MESSAGE);
        return "done";
    }

    private static Pathway makeQuerypathwayUserInput(String pathway) {
        Map<Integer, List<String>> map = new TreeMap<Integer, List<String>>();
        String same = "";
        String[] domains = pathway.split(",");
        int id = 0, count = 0;
        List<String> domains_dat = new ArrayList<String>();
        for (String i : domains) {
            String[] data = i.split(":");
            if (count == 0) {
                same = data[0].trim();
                count = 1;
            }
            if (data[0].trim().equalsIgnoreCase(same.trim())) {
                domains_dat.add(data[1].trim());
                id = Integer.parseInt(data[0].trim());
            } else {
                same = data[0].trim();
                map.put(id, domains_dat);
                domains_dat.clear();
            }
        }
        map.put(id, domains_dat);
        DBPathway source = new DBPathway("1", map);
        Pathway s = new Pathway(source.convertToPathwayObj());
        return s;
    }

    /*   public static void main(String[] args) throws Exception {
     if (args.length < 4) {
     JOptionPane.showMessageDialog(null, args, "Warning:less arguments", JOptionPane.INFORMATION_MESSAGE);
     } else {
     try {
     //  JOptionPane.showMessageDialog(null, args, "Right arguments" + " sdsdsd321321", JOptionPane.INFORMATION_MESSAGE);
     SimpleCompare compare = new SimpleCompare();
     compare.setupConnection();
     compare.getAllPathways();
     compare.getQueryPathways(args[0], args[1]);
     String s = compare.comparePathways(args[2], args[3], args[4]);
     System.out.println(s);

     } catch (Exception ds) {

     }
     }
     if(args[5].length()>1)
     {
     makeQuerypathwayUserInput(args[5]);
     }*/
    /*   SimpleCompare compare = new SimpleCompare();
     compare.setupConnection();
     compare.getAllPathways();
     compare.getQueryPathways("BSYN18", "1");
     compare.comparePathways("2", "3", "4");
     }*/

    void StartComparison(String dsa, String string, String string0, String bsrC1, String string1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
