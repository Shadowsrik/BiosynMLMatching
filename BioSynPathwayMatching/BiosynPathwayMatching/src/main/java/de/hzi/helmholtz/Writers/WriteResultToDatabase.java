/*
 * The results obtained from comapring pathways are added to database (unique id, QBSYN,Qmodel,TBSYN,TModel,Result string)
 */
package de.hzi.helmholtz.Writers;

import de.hzi.helmholtz.Compare.DBPathway;
import de.hzi.helmholtz.Compare.SimpleCompare;
import de.hzi.helmholtz.Resources.properties;
import java.sql.PreparedStatement;
import java.util.UUID;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

/**
 *
 * @author srdu001
 */
public class WriteResultToDatabase {

    SimpleCompare compare = new SimpleCompare();
    public Connection connection;
    String driverName;

    public void WriteToDatabase(String UniqueID, String QBiosyn, String QModel, String TBiosyn, String TModel, String Result, double bitscore, String eval) throws Exception {
        if (connection == null || connection.isClosed()) {
            connection = SimpleCompare.connection;
        }
        java.util.Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());

        String insertStatement = "insert into B_PathwayComparison (process_ID, query_bsynID, query_model, target_bsynID, target_model, resultset,Timestamp,Bitscore,eval) values (?, ?, ?, ?, ?, ?, ?, ?,?)";
        try {
            if (connection == null || connection.isClosed()) {
                //setupConnection();
                connection = SimpleCompare.connection;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertStatement)) {

            preparedStatement.setString(1, UniqueID);
            preparedStatement.setString(2, QBiosyn);
            preparedStatement.setString(3, QModel);
            preparedStatement.setString(4, TBiosyn);
            preparedStatement.setString(5, TModel);
            preparedStatement.setString(6, Result);
            preparedStatement.setTimestamp(7, timestamp);
            preparedStatement.setDouble(8, bitscore);
            preparedStatement.setString(9, eval);

            try {
                if (connection == null || connection.isClosed()) {
                    //  setupConnection();
                    connection = SimpleCompare.connection;
                }
                try {
              //      preparedStatement.executeUpdate();
                    connection.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            }
        }
    }

    public void maxScoreWriting(String UniqueID, String QueryModel, double maxbitscore) throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = SimpleCompare.connection;
        }
        java.util.Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());

        //  String updateStatement = "update B_PathwaysJobID set maxscore = ? where job_ID = ?";
        String insertStatement = "insert into B_JobIDModelMaxscores (job_ID, QueryModel, maxscore) values (?, ?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertStatement);
        preparedStatement.setString(1, UniqueID);
        preparedStatement.setString(2, QueryModel);
        preparedStatement.setDouble(3, maxbitscore);

        try {
            if (connection == null || connection.isClosed()) {
                connection = SimpleCompare.connection;
                // setupConnection();
            }
            try {
              //  preparedStatement.executeUpdate();
                connection.commit();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            }
            // connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
