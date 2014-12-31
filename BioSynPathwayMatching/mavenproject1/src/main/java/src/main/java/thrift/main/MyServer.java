/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thrift.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import thriftserver.ThriftServer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.thrift.transport.TTransportException;
import thrift.properties.Properties;

/**
 *
 * @author Zhazira-pc
 */
public class MyServer {
    /*public static void StartsimpleServer(AdditionService.Processor<AdditionServiceHandler> processor) {
     try {
     TServerTransport serverTransport = new TServerSocket(8181);
     TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));
     // Use this for a multithreaded server
     // TServer server = new TThreadPoolServer(new
     // TThreadPoolServer.Args(serverTransport).processor(processor));
     System.out.println("Starting the simple server...");
     server.serve();
     } catch (Exception e) {
     e.printStackTrace();
     }
     }*/
    /*public static void StartsimpleServerwithUserList(UserListService.Processor<UserListServiceHandler> processor) {
     try {
     TServerTransport serverTransport = new TServerSocket(8181);
     TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));
     // Use this for a multithreaded server
     // TServer server = new TThreadPoolServer(new
     // TThreadPoolServer.Args(serverTransport).processor(processor));
     System.out.println("Starting the simple server...");
     server.serve();
     } catch (Exception e) {
     e.printStackTrace();
     }
     }*/

    /*public static void StartThriftServer(ThriftServerice.Processor<ThriftServiceHandler> processor) {
     try {
     TServerTransport serverTransport = new TServerSocket(8181);
     //TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));
     // Use this for a multithreaded server
     TServer server = new TThreadPoolServer(new
     TThreadPoolServer.Args(serverTransport).processor(processor));
     System.out.println("Starting the simple server...");
     server.serve();
     } catch (Exception e) {
     e.printStackTrace();
     }
     }*/
    public static String connect() {
        String portNo = "";
        System.out.
                println("-------- MySQL JDBC Connection Testing ------------");
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your MySQL JDBC Driver?");
            e.printStackTrace();
            return portNo;
        }

        Connection connection = null;
        try {
            //connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/myxobase",Properties.ZHAZ_USER,Properties.ZHAZ_USER_PASSWORD);
            connection = DriverManager
                    .getConnection(
                            "jdbc:mysql://" + Properties.MXBASE_SERVER + ":"+Properties.MYSQL_PORT_NO+"/" + Properties.MXBASE_DATABASE + "?"
                            + "user=" + Properties.DEFAULT_USER + "&password=" + Properties.DEFAULT_USER_PASSWORD + ""
                    );
            Statement stmt = connection.createStatement(
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery(
                    "select Value from Mxbase_Settings where Setting = 'Thriftservice';");
            boolean available = true;//false;
            while (rs.next()) {
                if(rs.getString("Value").equals("1"))
                    available = true;
            }
            if(!available)
                return portNo;
            rs = stmt.executeQuery(
                    "select Value from Mxbase_Settings where Setting = 'Thriftserviceport';");
            while (rs.next()) {
                portNo = rs.getString("Value");
            }
            connection.close();
            return portNo;
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return portNo;
        }
    }

    public static void main(String[] args) {
        //OdbcConnection.connect();
        //StartsimpleServer(new AdditionService.Processor<AdditionServiceHandler>(new AdditionServiceHandler()));
        //StartsimpleServerwithUserList(new UserListService.Processor<UserListServiceHandler>(new UserListServiceHandler()));
        //StartThriftServer(new ThriftServerice.Processor<ThriftServiceHandler>(new ThriftServiceHandler()));
        String portNo = connect();
        ThriftServer server = new ThriftServer();
        try {
            server.init(portNo);
        } catch (InterruptedException ex) {
            Logger.getLogger(MyServer.class.getName()).
                    log(Level.SEVERE,
                            null,
                            ex);
        } catch (TTransportException ex) {
            Logger.getLogger(MyServer.class.getName()).
                    log(Level.SEVERE,
                            null,
                            ex);
        }
    }
}
