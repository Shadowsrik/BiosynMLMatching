/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hzi.helmholtz.Compare;

import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import de.hzi.helmholtz.Compare.DBPathway;
import de.hzi.helmholtz.Compare.PathwayComparison;
import de.hzi.helmholtz.Domains.Domain;
import de.hzi.helmholtz.Modules.Module;
import de.hzi.helmholtz.Pathways.Pathway;
import de.hzi.helmholtz.Pathways.PathwayUsingModules;
import de.hzi.helmholtz.Resources.properties;
import de.hzi.helmholtz.ThriftService.ThriftServer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author srdu001
 */
public class SimpleCompareUsingModules {
    /*db connection*/

    public Connection connection;
    public String UniqueJobID;
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

    public static PathwayUsingModules sourcecopy;
    public static PathwayUsingModules targetcopy;
    private int maxWindowSize = 1;
    /**/
    List<DBPathwayUsingModules> allPathways;
    List<DBPathwayUsingModules> QueryPathway;
    int self = 1;

    public SimpleCompareUsingModules() {
        //System.out.println("Starting SimpleCompare");

        allPathways = new ArrayList<DBPathwayUsingModules>();
        QueryPathway = new ArrayList<DBPathwayUsingModules>();

        // ////System.out.println(s);
    }

    public void StartComparison(String jobID, String windowSize, String algorithm, String BSYNID, String modelID, String modulesfromEditor) {
        try {
            //System.out.println("Thrift started StartComparison ...");
            setupConnection();
            //System.out.println("Connection established ...");
            getAllPathways();
            //System.out.println("Got all Pathways ...");

            if (BSYNID.trim().length() <= 2 && modulesfromEditor.trim().length() > 0) {
                // if job is made from editor and submitted to matching function
                makeQuerypathwayUserInput(modulesfromEditor);

            } else {
                getQueryPathways(BSYNID, modelID);
            }
            //System.out.println("QueryPathways obtained ...");
            //System.out.println("Started comparing pathways ...");
            if (QueryPathway.size() > 0) {
                comparePathways(algorithm, windowSize, jobID);
            } else {
                //System.out.println("No query Pathways found...");
            }
        } catch (Exception ex) {
            Logger.getLogger(ThriftServer.class.getName()).log(Level.SEVERE, null, ex);
        }

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

    public void setupConnection() {
        try {
            initializeProperties();
            // Load the JDBC driver
            //System.out.println(serverName + "       " + portNumber + "          " + dbname + "         " + username + "          " + password);
            driverName = "com.mysql.jdbc.Driver";
            Class.forName(driverName);
            // connection = DriverManager.getConnection("jdbc:mysql://" + properties.MXBASE_SERVER + ":" + properties.port + "/" + properties.dbname + "?"    + "user=" + properties.username + "&password=" + properties.passwd + ""            );

            connection = DriverManager.getConnection("jdbc:mysql://" + properties.testserver + ":" + properties.testport + "/" + properties.testdbname + "?" + "user=" + properties.testusername + "&password=" + properties.testpasswd + "");

            // String url = "jdbc:mysql://" + serverName + "/" + dbname + "?";
            // connection = DriverManager.getConnection(url, username, password);
            if (connection == null) {
                //System.out.println("FATAL: Could not establish database connection!");
                System.exit(1);
            }
            //System.out.println("connected ..........");
            connection.setAutoCommit(false);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (e.getMessage().indexOf("ORA-12519") != -1) {
                    //sleep for a while
                    //////System.out.println("Sleeping...");
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
        DBPathwayUsingModules dbP;
        Map<String, List<String>> moduleslist = new TreeMap<String, List<String>>();
        Map<String, List<String>> pathway = new TreeMap<String, List<String>>();
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

        String selectStatement = "select concat(BSYN_ID,'_',B_model),B_gene, gene,B_module from (select BSYN_ID,B_model,B_gene,GROUP_CONCAT(concat(IF(B_domian = '','?',B_domian),'_',B_active,'_',IF( ExtractValue(B_buildingblock_xml, '/buildingblock/moiety/name') = '', 'nil',   ExtractValue(B_buildingblock_xml, '/buildingblock/moiety/name') ))) as gene,B_module from B_BiosyntheticPathways group by BSYN_ID,B_model, B_gene,B_module order by B_key) T order by T.BSYN_ID, T.B_model, T.B_gene,T.B_module";
        PreparedStatement preparedStatement = connection.prepareStatement(selectStatement);
        try {
            if (connection == null || connection.isClosed()) {
                setupConnection();
            }
            ResultSet rSet = preparedStatement.executeQuery();
            int modulecount = 0;
            List<String> gene = new ArrayList<String>();
            List<String> module = new ArrayList<String>();
            String currPathwayId = "BSYN1_1";
            int currGeneId = -1;
            while (rSet.next()) {
                String pathwayId = rSet.getString(1);
                int geneId = rSet.getInt(2);
                String geneContent = rSet.getString(3);
                String moduleID = rSet.getString(4);
                if (pathwayId.equalsIgnoreCase(currPathwayId)) {
                    // we are in current pathway
                    if (moduleID.trim().equals("")) {
                        modulecount++;
                        moduleID = geneId + "_cre" + modulecount;
                    } else {
                        moduleID = geneId + "_" + moduleID;
                    }
                    module = Arrays.asList(geneContent.split(","));
                    pathway.put(moduleID, module);
                } else {

                    dbP = new DBPathwayUsingModules(currPathwayId, pathway);
                    allPathways.add(dbP);
                    currPathwayId = pathwayId;
                    modulecount = 0;
                    pathway = new TreeMap<String, List<String>>();
                    module = Arrays.asList(geneContent.split(","));
                    if (moduleID.trim().equals("")) {
                        modulecount++;
                        moduleID = geneId + "_cre" + modulecount;
                    } else {
                        moduleID = geneId + "_" + moduleID;
                    }
                    pathway.put(moduleID, module);
                }
            }
            dbP = new DBPathwayUsingModules(currPathwayId, pathway);
            allPathways.add(dbP);
            //////System.out.println(allPathways.size());
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

    public List<DBPathwayUsingModules> getQueryPathways(String QueryID, String ModelID) throws Exception {
        DBPathwayUsingModules dbP;
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
        Map<String, List<String>> QueryPathways = new TreeMap<String, List<String>>();

        //////System.out.println(ModelID);
        if (ModelID.equalsIgnoreCase("All")) {
            selectStatement = "select concat(a.BSYN_ID,'_',a.B_model) as pathway ,T.B_gene, T.gene,T.B_module from (select BSYN_ID,B_model,B_module,B_gene,GROUP_CONCAT(concat(IF(B_domian = '','?',B_domian),'_',B_active,'_',\n"
                    + "IF( ExtractValue(B_buildingblock_xml, '/buildingblock/moiety/name') = '', 'nil', ExtractValue(B_buildingblock_xml, '/buildingblock/moiety/name') ))) as gene from B_BiosyntheticPathways\n"
                    + " group by BSYN_ID,B_model, B_gene,B_module order by B_key) T join B_BiosynthesisModels a on a.BSYN_ID = T.BSYN_ID where  a.BSRC_ID = '" + QueryID.trim() + " order by T.BSYN_ID, T.B_model, T.B_gene,T.B_module";

        } else {
            selectStatement = "select concat(a.BSYN_ID,'_',a.B_model) as pathway ,T.B_gene, T.gene,T.B_module from (select BSYN_ID,B_model,B_module,B_gene,GROUP_CONCAT(concat(IF(B_domian = '','?',B_domian),'_',B_active,'_',\n"
                    + "IF( ExtractValue(B_buildingblock_xml, '/buildingblock/moiety/name') = '', 'nil', ExtractValue(B_buildingblock_xml, '/buildingblock/moiety/name') ))) as gene from B_BiosyntheticPathways\n"
                    + " group by BSYN_ID,B_model, B_gene,B_module order by B_key) T join B_BiosynthesisModels a on a.BSYN_ID = T.BSYN_ID where  a.BSRC_ID = '" + QueryID.trim() + "'  and a.B_model = " + ModelID.trim() + " order by T.BSYN_ID, T.B_model, T.B_gene,T.B_module";
        }
        PreparedStatement preparedStatement = connection.prepareStatement(selectStatement);
        try {
            if (connection == null || connection.isClosed()) {
                setupConnection();
            }
            ResultSet rSet = preparedStatement.executeQuery();

            List<String> gene = new ArrayList<String>();
            int p = 0;
            int modulecount = 0;
            String currPathwayId = "";
            List<String> module = new ArrayList<String>();
            while (rSet.next()) {
                if (p == 0) {
                    currPathwayId = rSet.getString(1);
                    p = 1;
                }
                String pathwayId = rSet.getString(1);

                int geneId = rSet.getInt(2);
                String geneContent = rSet.getString(3);
                String moduleID = rSet.getString(4);
                //////System.out.println(geneContent);
                if (pathwayId.equalsIgnoreCase(currPathwayId)) {
                    // we are in current pathway
                    if (moduleID.trim().equals("")) {
                        modulecount++;
                        moduleID = geneId + "_cre" + modulecount;
                    } else {
                        moduleID = geneId + "_" + moduleID;
                    }
                    module = Arrays.asList(geneContent.split(","));
                    QueryPathways.put(moduleID, module);
                } else {

                    dbP = new DBPathwayUsingModules(currPathwayId, QueryPathways);
                    QueryPathway.add(dbP);
                    currPathwayId = pathwayId;
                    QueryPathways = new TreeMap<String, List<String>>();
                    if (moduleID.trim().equals("")) {
                        modulecount++;
                        moduleID = geneId + "_cre" + modulecount;
                    } else {
                        moduleID = geneId + "_" + moduleID;
                    }
                    module = Arrays.asList(geneContent.split(","));

                    QueryPathways.put(moduleID, module);
                    //QueryPathway.add(QueryPathways);
                }
            }
            dbP = new DBPathwayUsingModules(currPathwayId, QueryPathways);
            QueryPathway.add(dbP);
            //////System.out.println(QueryPathway.size());
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

    public void comparePathways(String ALGORITHM, String windowsize, String ProcessID) {
        try {

            Iterator<DBPathwayUsingModules> firstIter = QueryPathway.iterator();
            Iterator<DBPathwayUsingModules> secondIter = allPathways.iterator();
            SizeofTargetPathwaysInDatabase = allPathways.size();
            maxWindowSize = Integer.parseInt(windowsize.trim());
            UniqueJobID = ProcessID;

            while (firstIter.hasNext()) {
                DBPathwayUsingModules source = firstIter.next();

                //secondIter.next();
                //////System.out.println("**************************************************");
                secondIter = allPathways.iterator();
                while (secondIter.hasNext()) {

                    SizeofQueryPathway = 0;
                    DBPathwayUsingModules target = secondIter.next();
                    //////System.out.println(source.getPathwayID() + " && " + target.getPathwayID());

                    Map<String, Integer> srcGeneIdToPositionMap = new TreeMap<String, Integer>();
                    int temp = 0;

                    sourcecopy = new PathwayUsingModules(source.convertToPathwayObj());
                    targetcopy = new PathwayUsingModules(target.convertToPathwayObj());
                    for (Map.Entry<String, List<String>> e : source.getPathway().entrySet()) {

                        SizeofQueryPathway += e.getValue().size();
                        srcGeneIdToPositionMap.put(e.getKey(), temp++);
                    }
                    Map<String, Integer> tgtGeneIdToPositionMap = new TreeMap<String, Integer>();
                    temp = 0;
                    for (Map.Entry<String, List<String>> e : target.getPathway().entrySet()) {
                        tgtGeneIdToPositionMap.put(e.getKey(), temp++);
                    }
                    source.printPathway();
                    target.printPathway();
                    //source.convertToPathwayObj().getModules()
                    Iterator<Module> sourceGeneIt = source.convertToPathwayObj().geneIterator();
                    Multiset<String> qfunction = LinkedHashMultiset.create();
                    while (sourceGeneIt.hasNext()) {
                        Module queryGene = sourceGeneIt.next();
                        for (Domain d : queryGene.getDomains()) {
                            qfunction.add(d.getDomainFunctionString());
                        }
                    }
                    Iterator<Module> targetGeneIt = target.convertToPathwayObj().geneIterator();
                    Multiset<String> tfunction = LinkedHashMultiset.create();
                    while (targetGeneIt.hasNext()) {
                        Module targetGene = targetGeneIt.next();
                        for (Domain d : targetGene.getDomains()) {
                            tfunction.add(d.getDomainFunctionString());
                        }
                    }
                    Multiset<String> DomainsCommon = Multisets.intersection(qfunction, tfunction);
                    if (DomainsCommon.size() > 5) {
                        PathwayComparisonUsingModules pComparison = new PathwayComparisonUsingModules(source.convertToPathwayObj(), target.convertToPathwayObj(), maxWindowSize, UniqueJobID, ALGORITHM, self);
                    }

                    //PathwayComparison pComparison = new PathwayComparison(target.convertToPathwayObj(), source.convertToPathwayObj());
                    //
                    //  //////System.out.println(SizeofQueryPathway + "                 " + SizeofTargetPathwaysInDatabase);
                    //break;
                }

                //System.out.println("**************************************************");
            }
            //System.out.println("Done ... Enjoy with your results");
        } catch (Exception ex) {
            //System.out.println("Error in SimpleCompare:" + ex);
            Logger.getLogger(ThriftServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        ////System.out.println("done");
        //  JOptionPane.showMessageDialog(null, "Done", "less arguments" + " sdsdsd321321", JOptionPane.INFORMATION_MESSAGE);

    }

    private PathwayUsingModules makeQuerypathwayUserInput(String pathway) {
        Map<String, List<String>> map = new TreeMap<String, List<String>>();
        String same = "";
        String[] domains = pathway.split(",");
        String id = "";
        int count = 0;
        List<String> domains_dat = new ArrayList<String>();
        for (String i : domains) {

            String[] data = i.split(":");
            if (count == 0) {
                same = data[0].trim();
                count = 1;
            }
            if (data[0].trim().equalsIgnoreCase(same.trim())) {
                domains_dat.add(data[1].trim());
                id = (data[0].trim());
            } else {
                same = data[0].trim();
                map.put(id, domains_dat);
                domains_dat.clear();
            }
        }
        map.put(id, domains_dat);
        DBPathwayUsingModules source = new DBPathwayUsingModules("1_1", map);
        PathwayUsingModules s = new PathwayUsingModules(source.convertToPathwayObj());
        QueryPathway.add(source);
        return s;
    }
}
