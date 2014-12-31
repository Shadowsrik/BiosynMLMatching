package de.hzi.helmholtz.Compare;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.ximpleware.*;

public class xml2text {

    static String Qf = "C:\\Users\\srdu001\\Desktop\\Ccc1071_scaf.xml";
    static String Tf = "C:\\Users\\srdu001\\Desktop\\DK1622_Mxa.xml";
    static String gene_content = "";
    static Map<Integer, List<String>> Qmap;
    static Map<Integer, List<String>> Tmap;
    static Map<Integer, List<String>> Treturn = new HashMap<Integer, List<String>>();

    public static void main() {

        Qmap = new HashMap<Integer, List<String>>(domainsimport(Qf));
        Tmap = new HashMap<Integer, List<String>>();

        int cc = 0;
        for (int i = domainsimport(Tf).size(); i > 0; i--) {
            cc = cc + 1;
            Tmap.put(cc, domainsimport(Tf).get(i));

            //System.out.println(cc);
        }

    }

    public static Map<Integer, List<String>> domainsimport(String f) {
        Treturn.clear();
        String nodels = "", substr = "";;
        String begin = "", end = "", domainid = "", nodeid = "", h = "", function = "";
        String gen = "";
        String geneid = "", gh = "", genename = "";
        try {

            // counting child elements of parlist
            int count = 0, pcount = 0, cc = 0;
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
                                    int genid = 0;
                                    try {
                                        ap1.selectXPath("//domainlist/domain[@id='" + domainid.trim() + "']/location/gene/geneid/text()");
                                    } catch (XPathParseException e) {
                                    }
                                    genid = Integer.parseInt(ap1.evalXPathToString().trim());


                                    if (vn.toElement(VTDNav.FC)) {
                                        do {
                                            if (vn.matchElement("function")) {
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


                                                geneid = "";
                                                gh = "";
                                                genename = "";
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
                                                nodeid = "";
                                                try {
                                                    ap1.selectXPath("/root/domainlist/domain[@id='" + domainid.trim() + "']/nodeid/text()");
                                                } catch (XPathParseException e) {
                                                    // TODO Auto-generated catch block
                                                    e.printStackTrace();
                                                }
                                                nodeid = ap1.evalXPathToString().trim();
//substr



                                                try {
                                                    ap1.selectXPath("/root/model[@id = 1]/nodelist/node[@id='" + nodeid.trim() + "']/buildingblock/moiety[@ratio= 1]/name/text()");
                                                } catch (XPathParseException e) {
                                                    // TODO Auto-generated catch block
                                                    e.printStackTrace();
                                                }
                                                substr = ap1.evalXPathToString().trim();
                                                //	System.out.println(substr+"        ds");

                                                /*//////////System.out.println(domainid+"  "+geneid+" - "+gh);
                                                 //////////System.out.println(startPos1+" - "+stopPos1+"    "+begin+" - "+end);*/


                                            } else if (vn.matchElement("dstatus")) {
                                                h = " ";
                                                if (substr.trim().length() < 1) {
                                                    substr = "nil";
                                                }

                                                try {
                                                    h = vn.toString(vn.getText()).trim();
                                                } catch (Exception ex) {
                                                }


                                                if (!genename.equals(gen)) {

                                                    //System.out.println(nodels+"             "+cc);
                                                    if (nodels.trim().length() > 2) {
                                                        String[] p = nodels.trim().split(",");
                                                        Treturn.put(cc, Arrays.asList(p));
                                                    }
                                                    if (gene_content.equals("")) {
                                                        cc += 1;
                                                        gene_content = System.getProperty("line.separator");
                                                        gene_content = genename + ":         " + function + "-" + h;
                                                        nodels = function.trim() + "-" + h.trim() + "-" + substr.trim();
                                                    } else {
                                                        cc += 1;
                                                        nodels = function.trim() + "-" + h.trim() + "-" + substr.trim();
                                                        gene_content += System.getProperty("line.separator") + genename + ":         " + function + ";" + h;
                                                    }
                                                } else {

                                                    if (gene_content.equals("")) {
                                                        //gene_content = genename+":         "+function+";"+h;
                                                    } else {
                                                        nodels += "," + function.trim() + "-" + h.trim() + "-" + substr.trim();
                                                        gene_content += " - " + function + ";" + h;
                                                    }
                                                }
                                                gen = genename;

                                            }
                                            //	Treturn.put(cc, value);


                                        } while (vn.toElement(VTDNav.NS));

                                        vn.toElement(VTDNav.P);
                                    }




                                } while (vn.toElement(VTDNav.NS, "domain"));
                                if (nodels.trim().length() > 2) {
                                    String[] p = nodels.trim().split(",");
                                    Treturn.put(cc, Arrays.asList(p));
                                }
                                //	System.out.println(nodels+"             "+cc);
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

        } catch (Exception e) {
            ////System.out.println(" Exception during domain:          "+e);
        }
        return Treturn;
    }

    public static void geneimp1(String f) {

        String begin = "", end = "";
        try {

            // counting child elements of parlist
            int count = 0;
            // counting child elements of parlist named "par"
            int par_count = 0;

            VTDGen vg = new VTDGen();
            if (vg.parseFile(f, true)) {

                VTDNav vn = vg.getNav();
                AutoPilot ap1 = new AutoPilot();
                ap1.bind(vn);
                if (vn.matchElement("root")) { // match blix

                    if (vn.toElement(VTDNav.FC, "genelist")) {
                        do {

                            if (vn.toElement(VTDNav.FC, "gene")) {
                                do {
                                    int cc = 0;


                                    if (vn.toElement(VTDNav.FC)) {

                                        do {

                                            if (vn.matchElement("gene_name")) {
                                                if (gene_content.equals("")) {
                                                    gene_content = vn.toString(vn.getText());
                                                } else {
                                                    gene_content += "-" + vn.toString(vn.getText());
                                                }


                                            }


                                        } while (vn.toElement(VTDNav.NS));
                                        vn.toElement(VTDNav.P);
                                    }




                                } while (vn.toElement(VTDNav.NS, "gene"));
                                vn.toElement(VTDNav.P);
                            }
                        } // to next silbing named "command"
                        while (vn.toElement(VTDNav.NS, "genelist"));
                        vn.toElement(VTDNav.P); // go up one level
                    } else {
                        vn.toElement(VTDNav.P); // go up one level	
                    }
                }
            }
        } catch (Exception e) {
            ////System.out.println(" Exception during navigation:          "+e);
        }
    }
}
