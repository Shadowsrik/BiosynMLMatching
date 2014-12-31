/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hzi.helmholtz.Compare;

import de.hzi.helmholtz.Modules.Modules;
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
 * @author skondred Get all pathways from the database. Pair each and use
 * VertexCoverTest to compare. Store each pair comparison.
 */
public class SimpleCompare {

    /*db connection*/
    Connection connection;
    String driverName;
    String serverName;
    String portNumber;
    String dbname;
    String username;
    String password;
    Properties props = null;
    String dbPropertiesFile;
    /**/
    List<DBPathway> allPathways;

    public SimpleCompare() {
        props = new Properties();
        dbPropertiesFile = "database.properties";
        allPathways = new ArrayList<DBPathway>();
    }

    public Properties initializeProperties() {
        //props = new Properties();
        try {
            //Thread currentThread = Thread.currentThread();
            //ClassLoader contextClassLoader = currentThread.getContextClassLoader();
            //InputStream propertiesStream = contextClassLoader.getResourceAsStream(dbPropertiesFile);
            //if (propertiesStream != null) {
            if (dbPropertiesFile != null) {
                props.load(this.getClass().getClassLoader().getResourceAsStream("Main" + System.getProperty("file.separator") + "Resources" + System.getProperty("file.separator") + dbPropertiesFile));
                //props.load(propertiesStream);
                serverName = props.getProperty("server");
                portNumber = props.getProperty("port");
                dbname = props.getProperty("dbname");
                username = props.getProperty("username");
                password = props.getProperty("passwd");
            } else {
                System.out.println("Properties stream not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return props;
    }

    private void setupConnection() {
        try {
            if (props == null || props.isEmpty()) {
                props = initializeProperties();
            }
            // Load the JDBC driver
            driverName = "com.mysql.jdbc.Driver";
            Class.forName(driverName);
            String url = "jdbc:mysql://" + serverName + "/" + dbname + "?";
            connection = DriverManager.getConnection(url, username, password);
            if (connection == null) {
                System.out.println("FATAL: Could not establish database connection!");
                System.exit(1);
            }
            connection.setAutoCommit(false);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (e.getMessage().indexOf("ORA-12519") != -1) {
                    //sleep for a while
                    System.out.println("Sleeping...");
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
        DBPathway dbP;
        Map<Integer, List<String>> pathway = new TreeMap<Integer, List<String>>();
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
        String selectStatement = "select concat(BSYN_ID,'_',B_model) as pathway ,B_gene, gene from (select BSYN_ID,B_model,B_gene,GROUP_CONCAT(concat(IF(B_domian = '','?',B_domian),'_',B_active,'_', IF( ExtractValue(B_buildingblock_xml, '/buildingblock/moiety/parent/name') = '', 'nil', ExtractValue(B_buildingblock_xml, '/buildingblock/moiety/parent/name') ))) as gene from B_BiosyntheticPathways group by BSYN_ID,B_model, B_gene order by B_key) T order by BSYN_ID, B_model, B_gene";
        PreparedStatement preparedStatement = connection.prepareStatement(selectStatement);
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
            System.out.println(allPathways.size());
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
 public void getAllPathwaysWithModules() throws Exception {
        DBPathway dbP;
        String moduleName;
        String geneName = "";  String ModuleID;Modules module;
        Map<Integer, List<String>> pathway = new TreeMap<Integer, List<String>>();
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
        String selectStatement = "select concat(BSYN_ID,'_',B_model) as pathway,B_gene, gene,B_module from (select BSYN_ID,B_model,B_gene,GROUP_CONCAT(concat(IF(B_domian = '','?',B_domian),'_',B_active,'_',IF( ExtractValue(B_buildingblock_xml, '/buildingblock/moiety/name') = '', 'nil',   ExtractValue(B_buildingblock_xml, '/buildingblock/moiety/name') ))) as gene,B_module from B_BiosyntheticPathways group by BSYN_ID,B_model,B_module, B_gene order by B_key) T order by BSYN_ID, B_model, B_module,B_gene";
        PreparedStatement preparedStatement = connection.prepareStatement(selectStatement);
       try {
            if (connection == null || connection.isClosed()) {
                setupConnection();
            }
            ResultSet rSet = preparedStatement.executeQuery();

            List<String> gene = new ArrayList<String>();
            String currPathwayId = "BSYN1_1";
            while (rSet.next()) {
                String pathwayId = rSet.getString(1);
                int geneId = rSet.getInt(4);
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
            System.out.println(allPathways.size());
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
  public void comparePathways() {
        SimpleLevensteinCompare vTest;

        Iterator<DBPathway> firstIter = allPathways.iterator();
        Iterator<DBPathway> secondIter = allPathways.iterator();

        while (firstIter.hasNext()) {
            DBPathway source = firstIter.next();
            //secondIter.next();
            System.out.println("**************************************************");
            secondIter = allPathways.iterator();
            while (secondIter.hasNext()) {
                DBPathway target = secondIter.next();
                System.out.println(source.getPathwayID() + " && " + target.getPathwayID());
                if (!source.getPathwayID().equalsIgnoreCase(target.getPathwayID())) {
                    Map<Integer, Integer> srcGeneIdToPositionMap = new TreeMap<Integer, Integer>();
                    int temp = 0;
                    for (Map.Entry<Integer, List<String>> e : source.getPathway().entrySet()) {
                        srcGeneIdToPositionMap.put(e.getKey(), temp++);
                    }
                    Map<Integer, Integer> tgtGeneIdToPositionMap = new TreeMap<Integer, Integer>();
                    temp = 0;
                    for (Map.Entry<Integer, List<String>> e : target.getPathway().entrySet()) {
                        tgtGeneIdToPositionMap.put(e.getKey(), temp++);
                    }
                    //source.printPathway();
                    //target.printPathway();
                    PathwayComparison pComparison = new PathwayComparison(source.convertToPathwayObj(), target.convertToPathwayObj());
                    //PathwayComparison pComparison = new PathwayComparison(target.convertToPathwayObj(), source.convertToPathwayObj());
                    //pComparison.pathwayComparisonGlobalBestGreedy();
                    pComparison.pathwayComparisonGlobalBestCoalesce();
                    //break;
                }
            }
            System.out.println("**************************************************");
        }
    }

    public void comparePathwaysOld() {
        SimpleLevensteinCompare vTest;

        Iterator<DBPathway> firstIter = allPathways.iterator();
        Iterator<DBPathway> secondIter = allPathways.iterator();

        while (firstIter.hasNext()) {
            DBPathway source = firstIter.next();
            secondIter.next();
            while (secondIter.hasNext()) {
                DBPathway target = firstIter.next();
                Map<Integer, Integer> srcGeneIdToPositionMap = new TreeMap<Integer, Integer>();
                int temp = 0;
                for (Map.Entry<Integer, List<String>> e : source.getPathway().entrySet()) {
                    srcGeneIdToPositionMap.put(e.getKey(), temp++);
                }
                Map<Integer, Integer> tgtGeneIdToPositionMap = new TreeMap<Integer, Integer>();
                temp = 0;
                for (Map.Entry<Integer, List<String>> e : target.getPathway().entrySet()) {
                    tgtGeneIdToPositionMap.put(e.getKey(), temp++);
                }
                source.printPathway();
                target.printPathway();
                vTest = new SimpleLevensteinCompare();
                vTest.compare(source.getPathway(), srcGeneIdToPositionMap, target.getPathway(), tgtGeneIdToPositionMap);
                System.out.println("***************************************************************");
                double score = vTest.getFinalscore();
                String f_one = vTest.getFirstPathwayAlignment();
                String f_two = vTest.getSecondPathwayAlignment();
                break;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        SimpleCompare compare = new SimpleCompare();
        compare.setupConnection();
       // compare.getAllPathways();
        compare.getAllPathwaysWithModules();
        compare.comparePathways();
    }
}
