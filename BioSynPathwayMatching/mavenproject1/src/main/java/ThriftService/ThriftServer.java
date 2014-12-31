/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ThriftService;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;

/**
 *
 * @author Zhazira-pc
 */
public class ThriftServer
        implements Runnable {

    TServer server;

    public void init(String portNo)
            throws InterruptedException, TTransportException {
        try {
            if(portNo.equals("") ){
                System.out.println("Port number is empty");
                return;
            }
            System.out.println("Starting server on port "+portNo+" ...");
            
            //TSSLTransportFactory.TSSLTransportParameters params = new TSSLTransportFactory.TSSLTransportParameters();
            //params.setKeyStore(Properties.THIFT_SSC_PATH, Properties.THRIFT_SSC_PW);
            
            ThriftServiceHandler handler = new ThriftServiceHandler();
            BiosynThriftService.Processor<ThriftServiceHandler> processor = new BiosynThriftService.Processor<ThriftServiceHandler>(
                    handler);
            //TServerSocket serverTransport = TSSLTransportFactory.getServerSocket( Integer.valueOf(portNo),1000000000, InetAddress.getByName(Properties.MXBASE_SERVER), params);
            //TServerTransport serverTransport = new TServerSocket(Integer.valueOf(portNo));
            TNonblockingServerTransport trans = new TNonblockingServerSocket(Integer.valueOf(portNo));
            //TNonblockingServerTransport serverTransport = new TNonblockingServerSocket(Integer.valueOf(portNo));
            TThreadedSelectorServer.Args args = new TThreadedSelectorServer.Args(trans);
            args.transportFactory(new TFramedTransport.Factory());
            args.protocolFactory(new TCompactProtocol.Factory());// TBinaryProtocol.Factory());
            args.processor(processor);
            args.selectorThreads(4);
            args.workerThreads(32);
            server = new TThreadedSelectorServer(args);
            //server = new TNonblockingServer(new TNonblockingServer.Args(serverTransport).processor(processor));
            //server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));
            server.serve();
            //new Thread(this).start();
            
            //while (!server.isServing()) {
            //    Thread.sleep(1);
            //};
        } catch (Exception ex) {
            Logger.getLogger(ThriftServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() {
        server.serve();
    }
}
