/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ThriftService;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.thrift.TException;

/**
 * This class has all Thrift Server-side functions implementation which can be
 * called from Client-side.
 *
 * @author Zhazira-pc
 */
public class ThriftServiceHandler
        implements BiosynThriftService.Iface {

    /**
     * Initially has null value, gets value after mySQLConnect method is called.
     */
    public Connection connection = null;



    public String SimpleCompare(String jobID, String windowSize, String algorithm, String mxbaseID, String modelID) throws org.apache.thrift.TException {
        return "returning";
    }
}
