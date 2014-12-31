package de.hzi.helmholtz.Readers;

import com.ximpleware.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.xpath.*;
import org.w3c.dom.NodeList;
import java.util.*;

/**
 * A simple example of a fasta importer that always imports its sequences as
 * unaligned nucleotide sequences and doesn't elegantly handle files containing
 * invalid data. The actual fasta importer used by Geneious is more robust and
 * also guesses the sequence type and whether the data is an alignment or
 * stand-alone sequences.
 */
public class BiosynXMLReader {

    public String[] getPermissibleExtensions() {
        return new String[]{".xml"};
    }
    public String f = "", description = "", modellistid = "";
    public String h = "", line = "", context = "", label = "", substrate = "", p_position = "", function = "", xmlclass = "", model = "", pposition = "", gposition = "", node = "", active = "", chemistry = "", genename = "", translation = "", p_name = "", proteinseq = "", moiety = "", transform = "", parentname = "", geneposition = "", g_name = "";
    public String system = "", version = "", created = "", modified = "", author = "", description1 = "", title = "", confidence = "", generator = "", label1 = "", name = "", strain = "", identifier = "", status = "", name1 = "", identifier1 = "", status1 = "", citation = "", name2 = "", status2 = "", citation2 = "", identifier2 = "", sequence2 = "", region2 = "", reg_end = "", red_start = "", sname = "", genetype = "";
    public int count = 0, countnodes = 1;
    public File selFile;
    static int s = 0;
    static XPath xpath = XPathFactory.newInstance().newXPath();
    //public List<SequenceAnnotation> results;
    List<Integer> start = new ArrayList<Integer>();
    List<Integer> end = new ArrayList<Integer>();
    org.w3c.dom.Element elementary = null;
    static int jamuna = 0, gene_p = 0, protein_p = 0, q_p = 0;
    static String id_p = "", pid_p = "", label_anno = "";
    static org.w3c.dom.Document document = null;
    int domains_count = 0;
    java.util.concurrent.locks.Lock lock = new java.util.concurrent.locks.ReentrantLock();
    Map<Integer, String> dictionary = new HashMap<Integer, String>();
    static NodeList models4model;
    static NodeList nl;
    static NodeList nl1;
    static NodeList nodeList;
    static NodeList domainList;
    static NodeList domaingetcount;
    static NodeList motiflist;
    static NodeList motifs;
    static NodeList genelist;

    public String getFileTypeDescription() {
        return "BiosynML Importer";
    }

    public void importDocuments(File file) {
        int doo = 0;
        long startTime = System.currentTimeMillis();

        f = file.getAbsolutePath();

        String[] docnote = new String[2];

        String fileName = new File(f).getName();
        fileName = fileName.substring(0, fileName.lastIndexOf('.'));
        // Our selection signature guarantees we have just one AminoAcidSequenceDocument here.
        String seq = "";

        Thread t = new Thread() {
            @Override
            public void run() {
                nodeimport();
            }
        };

        Thread t1 = new Thread() {
            @Override
            public void run() {
                moduleimport();
            }
        };


        Thread t2 = new Thread() {
            @Override
            public void run() {
                domainsimport();
            }
        };

        t2.start();
        t.start();
        t1.start();

        try {
            t.join();
            t1.join();
            t2.join();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            //////System.out.println(e);
        }
    }

    private void moduleimport() {
        String moduleid = "", modlabel = "", modelid = "";
        try {

            // counting child elements of parlist
            int count = 0, pcount = 0;
            // counting child elements of parlist named "par"
            int par_count = 0;
            int startPos = 0;
            int stopPos = 0;
            int startPos1 = 0;
            int stopPos1 = 0;
            List<Integer> start = new ArrayList<Integer>();
            List<Integer> end = new ArrayList<Integer>();
            VTDGen vg = new VTDGen();
            if (vg.parseFile(f, true)) {

                VTDNav vn = vg.getNav();
                AutoPilot ap1 = new AutoPilot();
                ap1.bind(vn);
                if (vn.matchElement("root")) { // match blix

                    if (vn.toElement(VTDNav.FC, "model")) {
                        do {
                            modelid = vn.toString(vn.getAttrVal("id"));
                            if (vn.toElement(VTDNav.FC, "modulelist")) {
                                do {
                                    if (vn.toElement(VTDNav.FC, "module")) {
                                        do {
                                            moduleid = vn.toString(vn.getAttrVal("id"));
                                            count++; //increment count


                                            if (vn.toElement(VTDNav.FC)) {
                                                do {
                                                    if (vn.matchElement("nodes")) {

                                                        String[] numberStrs = vn.toString(vn.getText()).trim().split("-");
                                                        int[] numbers = new int[numberStrs.length];
                                                        for (int i = 0; i < numberStrs.length; i++) {
                                                            //System.out.println(numberStrs[i].trim());
                                                            try {
                                                                ap1.selectXPath("//domainlist/domain[@id = '" + numberStrs[i].trim() + "']/location/gene/position/begin/text()");
                                                            } catch (XPathParseException e) {
                                                            }
                                                            //System.out.println(ap1.evalXPathToString().trim());
                                                            start.add(Integer.parseInt(ap1.evalXPathToString().trim()));
                                                            try {
                                                                ap1.selectXPath("//domainlist/domain[@id= '" + numberStrs[i].trim() + "']/location/gene/position/end/text()");
                                                            } catch (XPathParseException e) {
                                                            }
                                                            //System.out.println(ap1.evalXPathToString().trim());
                                                            end.add(Integer.parseInt(ap1.evalXPathToString().trim()));

                                                        }
                                                        startPos = Collections.max(start);
                                                        stopPos = Collections.max(end);

                                                        String geneid = "", gh = "";
                                                        try {
                                                            ap1.selectXPath("//domainlist/domain[@id= '" + numberStrs[0].trim() + "']/location/gene/geneid/text()");
                                                        } catch (XPathParseException e) {
                                                        }

                                                        geneid = ap1.evalXPathToString().trim();
                                                        try {
                                                            ap1.selectXPath("/root/genelist/gene[@id= '" + geneid.trim() + "']/gene_location/begin/text()");
                                                        } catch (XPathParseException e) {
                                                        }
                                                        gh = ap1.evalXPathToString().trim();


                                                        if (startPos > stopPos) {
                                                            stopPos = Collections.min(end);
                                                            startPos = Collections.max(start);
                                                            stopPos1 = Math.abs(startPos - Integer.parseInt(gh));
                                                            startPos1 = Math.abs(stopPos - Integer.parseInt(gh));
                                                        } else {
                                                            startPos = Collections.min(start);
                                                            stopPos = Collections.max(end);
                                                            startPos1 = startPos + Integer.parseInt(gh);
                                                            stopPos1 = stopPos + Integer.parseInt(gh);
                                                        }
                                                        par_count++;
                                                    } else if (vn.matchElement("label")) {
                                                        modlabel = vn.toString(vn.getText());
                                                    } else if (vn.matchElement("flag")) {
                                                    }

                                                } while (vn.toElement(VTDNav.NS));
                                                vn.toElement(VTDNav.P);
                                            }

                                        } // to next silbing named "command"
                                        while (vn.toElement(VTDNav.NS, "module"));
                                        vn.toElement(VTDNav.P); // go up one level
                                        ////System.out.println(startPos1+"       "+stopPos1);

                                    }

                                } while (vn.toElement(VTDNav.NS, "modulelist"));
                                vn.toElement(VTDNav.P);
                            }

                        } // to next silbing named "command"
                        while (vn.toElement(VTDNav.NS, "model"));
                        vn.toElement(VTDNav.P); // go up one level
                    } else {
                        vn.toElement(VTDNav.P); // go up one level	
                    }
                }
            }
            //results1.add(cluster_annotation);
        } catch (NavException e) {
            //////System.out.println(" Exception during navigation "+e);
        }
    }

    public void domainsimport() {
        String begin = "", end = "", domainid = "", nodeid = "", h = "", function = "";
        try {

            // counting child elements of parlist
            int count = 0, pcount = 0;
            // counting child elements of parlist named "par"
            int par_count = 0;

            VTDGen vg = new VTDGen();
            if (vg.parseFile(f, true)) {

                VTDNav vn = vg.getNav();
                AutoPilot ap1 = new AutoPilot();
                ap1.bind(vn);
                if (vn.matchElement("root")) { // match blix

                    if (vn.toElement(VTDNav.FC, "domainlist")) {
                        do {

                            if (vn.toElement(VTDNav.FC, "domain")) {

                                do {
                                    pcount = 0;
                                    domainid = vn.toString(vn.getAttrVal("id"));

                                    count++; //increment count
                                    if (vn.toElement(VTDNav.FC)) {
                                        do {
                                            if (vn.matchElement("nodeid")) {
                                                //	//////System.out.println(vn.toString(vn.getText()));


                                                nodeid = vn.toString(vn.getText());
                                                par_count++;
                                            } else if (vn.matchElement("function")) {
                                                function = vn.toString(vn.getText()).trim();
                                                try {
                                                    ap1.selectXPath("/root/domainlist/domain[@id='" + domainid.trim() + "']/location/gene/position/begin/text()");
                                                } catch (XPathParseException e) {
                                                    // TODO Auto-generated catch block
                                                    e.printStackTrace();
                                                }
                                                begin = ap1.evalXPathToString().trim();
                                                try {
                                                    ap1.selectXPath("/root/domainlist/domain[@id='" + domainid.trim() + "']/location/gene/position/end/text()");
                                                } catch (XPathParseException e) {
                                                    // TODO Auto-generated catch block
                                                    e.printStackTrace();
                                                }
                                                end = ap1.evalXPathToString().trim();


                                                String geneid = "", gh = "", genename = "";
                                                try {
                                                    ap1.selectXPath("//domainlist/domain[@id='" + domainid.trim() + "']/location/gene/geneid/text()");
                                                } catch (XPathParseException e) {
                                                }
                                                geneid = ap1.evalXPathToString().trim();
                                                try {
                                                    ap1.selectXPath("/root/genelist/gene[@id= '" + geneid.trim() + "']/gene_location/begin/text()");
                                                } catch (XPathParseException e) {
                                                }
                                                gh = ap1.evalXPathToString().trim();

                                                try {
                                                    ap1.selectXPath("/root/genelist/gene[@id= '" + geneid.trim() + "']/gene_name/text()");
                                                } catch (XPathParseException e) {
                                                }
                                                genename = ap1.evalXPathToString().trim();


                                                int startPos1 = 0, stopPos1 = 0;
                                                if (Integer.parseInt(begin) > Integer.parseInt(end)) {
                                                    stopPos1 = Math.abs(Integer.parseInt(begin) - Integer.parseInt(gh));
                                                    startPos1 = Math.abs(Integer.parseInt(end) - Integer.parseInt(gh));

                                                    ////////////////////System.out.println(label_anno+";"+startPos1+"-"+stopPos1);
                                                } else {

                                                    startPos1 = Math.abs(Integer.parseInt(begin)) + Integer.parseInt(gh);
                                                    stopPos1 = Math.abs(Integer.parseInt(end)) + Integer.parseInt(gh);
                                                }
                                                /*//////System.out.println(domainid+"  "+geneid+" - "+gh);
                                                 //////System.out.println(startPos1+" - "+stopPos1+"    "+begin+" - "+end);*/


                                            } else if (vn.matchElement("subtype")) {
                                                h = " ";
                                                try {
                                                    h = vn.toString(vn.getText()).trim();
                                                } catch (Exception ex) {
                                                }

                                            } else if (vn.matchElement("dstatus")) {
                                                h = " ";
                                                try {
                                                    h = vn.toString(vn.getText()).trim();
                                                } catch (Exception ex) {
                                                }

                                            } else if (vn.matchElement("substrate")) {
                                                h = " ";
                                                try {
                                                    h = vn.toString(vn.getText()).trim();
                                                } catch (Exception ex) {
                                                }

                                            }

                                        } while (vn.toElement(VTDNav.NS));
                                        vn.toElement(VTDNav.P);
                                    }
                                } while (vn.toElement(VTDNav.NS, "domain"));
                                vn.toElement(VTDNav.P);
                            }
                        } // to next silbing named "command"
                        while (vn.toElement(VTDNav.NS, "domainlist"));
                        vn.toElement(VTDNav.P); // go up one level
                    } else {
                        vn.toElement(VTDNav.P); // go up one level	
                    }
                }
            }
        } catch (NavException e) {
            //////System.out.println(" Exception during navigation "+e);
        }

    }

    public void nodeimport() {
        String begin = "", end = "", nodeid = "", modelid = "";
        try {

            // counting child elements of parlist
            int count = 0;
            // counting child elements of parlist named "par"
            int par_count = 0;

            VTDGen vg = new VTDGen();
            AutoPilot ap = new AutoPilot();

            VTDGen vg1 = new VTDGen();
            vg1.parseFile(f, true);

            VTDNav vn1 = vg1.getNav();

            ap.bind(vn1);
            if (vg.parseFile(f, true)) {
                VTDNav vn = vg.getNav();
                AutoPilot ap1 = new AutoPilot();
                ap1.bind(vn);

                if (vn.matchElement("root")) { // match blix

                    if (vn.toElement(VTDNav.FC, "model")) {
                        do {
                            modelid = vn.toString(vn.getAttrVal("id"));
                            if (vn.toElement(VTDNav.FC, "nodelist")) {
                                do {
                                    if (vn.toElement(VTDNav.FC, "node")) {
                                        do {
                                            nodeid = vn.toString(vn.getAttrVal("id"));
                                            count++; //increment count
                                            if (vn.toElement(VTDNav.FC)) {
                                                do {
                                                    if (vn.matchElement("buildingblock")) {
                                                        if (vn.toElement(VTDNav.FC, "moiety")) {
                                                            do {

                                                                if (vn.toElement(VTDNav.FC)) {
                                                                    do {
                                                                        if (vn.matchElement("name")) {
                                                                            //  //////System.out.println(" orderDate==> "               + vn.toString(vn.getAttrVal("name")));
                                                                            h = " ";
                                                                            try {
                                                                                h = vn.toString(vn.getText());
                                                                            } catch (Exception ex) {
                                                                            }
                                                                        } else if (vn.matchElement("category")) {
                                                                            h = " ";
                                                                            try {
                                                                                h = vn.toString(vn.getText());
                                                                            } catch (Exception ex) {
                                                                            }
                                                                        } else if (vn.matchElement("evidence")) {
                                                                            h = " ";
                                                                            try {
                                                                                h = vn.toString(vn.getText());
                                                                            } catch (Exception ex) {
                                                                            }

                                                                        } else if (vn.matchElement("confidence")) {
                                                                            h = " ";
                                                                            try {
                                                                                h = vn.toString(vn.getText());
                                                                            } catch (Exception ex) {
                                                                            }
                                                                        } else if (vn.matchElement("parent")) {

                                                                            if (vn.toElement(VTDNav.FC)) {
                                                                                do {
                                                                                    if (vn.matchElement("name")) {
                                                                                        h = " ";
                                                                                        try {
                                                                                            h = vn.toString(vn.getText());
                                                                                        } catch (Exception ex) {
                                                                                        }

                                                                                    } else if (vn.matchElement("transform")) {
                                                                                        h = " ";
                                                                                        try {
                                                                                            h = vn.toString(vn.getText());
                                                                                        } catch (Exception ex) {
                                                                                        }
                                                                                    }
                                                                                } while (vn.toElement(VTDNav.NS));
                                                                                vn.toElement(VTDNav.P);
                                                                            }
                                                                        }
                                                                    } while (vn.toElement(VTDNav.NS));
                                                                    vn.toElement(VTDNav.P);
                                                                }

                                                            } while (vn.toElement(VTDNav.NS, "motif"));
                                                            vn.toElement(VTDNav.P);
                                                        }
                                                    }
                                                } while (vn.toElement(VTDNav.NS));
                                                vn.toElement(VTDNav.P);
                                            }
                                        } while (vn.toElement(VTDNav.NS, "node"));
                                        vn.toElement(VTDNav.P);
                                    }
                                } // to next silbing named "command"
                                while (vn.toElement(VTDNav.NS, "nodelist"));
                                vn.toElement(VTDNav.P); // go up one level
                            }
                        } // to next silbing named "command"
                        while (vn.toElement(VTDNav.NS, "model"));
                        vn.toElement(VTDNav.P); // go up one level
                    } else {
                        vn.toElement(VTDNav.P); // go up one level	
                    }
                }
            }
        } catch (NavException e) {
            //////System.out.println(" Exception during navigation "+e);
        }
    }

}
