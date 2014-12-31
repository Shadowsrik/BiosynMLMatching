/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hzi.helmholtz.Compare;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 *
 * @author skondred
 */
public class SimpleLevensteinCompare {

    private double finalscore = 0;
    static double s_add = 1.0, s_penality = -1.0;
    private Multimap<Double, String> finalresult;
    private String firstPathwayAlignment;
    private String secondPathwayAlignment;

    public SimpleLevensteinCompare() {
        finalresult = ArrayListMultimap.create();
    }

    public void compare(Map<Integer, List<String>> Qmap, Map<Integer, Integer> QgeneIdPosMap, Map<Integer, List<String>> Tmap, Map<Integer, Integer> TgeneIdPosMap) {

        BiMap<Integer, Integer> qbiMap0 = HashBiMap.create();
        qbiMap0.putAll(QgeneIdPosMap);
        BiMap<Integer, Integer> qbiMap = qbiMap0.inverse();
        BiMap<Integer, Integer> tbiMap0 = HashBiMap.create();
        tbiMap0.putAll(TgeneIdPosMap);
        BiMap<Integer, Integer> tbiMap = tbiMap0.inverse();

        final JFrame frame = new JFrame("Matching");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(1400, 200);

        //frame.setVisible(true);
        frame.setLocationRelativeTo(null);


        JLabel j2 = new JLabel();


        ////System.out.println(LevenshteinDistance.computeLevenshteinDistance(Qg1,Qg2));
        //////System.out.println(n.execute(a1, b1));

        //        List<Map<Integer, List<String>>> Tdata = new ArrayList<Map<Integer, List<String>>>();
        //        List<Map<Integer, List<String>>> Qdata = new ArrayList<Map<Integer, List<String>>>();
        //        Map<Integer, List<String>> Qmap = new HashMap<Integer, List<String>>();
        //        Map<Integer, List<String>> Tmap = new HashMap<Integer, List<String>>();
        //
        //
        //        List<String> Tgene9 = Arrays.asList("ACL-active-nil", "ACP-active-nil", "C-active-nil", "A-active-gly", "PCP-active-nil");
        //        List<String> Tgene8 = Arrays.asList("ACP-active-nil");
        //        List<String> Tgene7 = Arrays.asList("AT-active-iso", "AT-active-mmal", "DH-active-nil", "KR-active-nil", "ACP-active-nil");
        //        List<String> Tgene6 = Arrays.asList("KS-active-nil", "AT-active-mmal", "DH-active-nil", "KR-active-nil", "ACP-active-nil");
        //        List<String> Tgene5 = Arrays.asList("KS-active-nil", "AT-active-mmal", "DH-active-nil", "KR-active-nil", "ACP-active-nil");
        //        List<String> Tgene4 = Arrays.asList("KS-active-nil", "AT-active-mmal/mal", "DH-active-nil", "KR-active-nil", "ACP-active-nil", "KS-active-nil", "AT-active-mmal/mal", "KR-active-nil", "ACP-active-nil");
        //        List<String> Tgene3 = Arrays.asList("AT-active-mal", "DH-active-nil", "KR-active-nil", "ACP-active-nil");
        //        List<String> Tgene2 = Arrays.asList("KS-active-nil", "AT-active-mmal", "DH-active-nil", "ER-active-nil", "KR-active-nil", "ACP-active-nil");
        //        List<String> Tgene1 = Arrays.asList("C-active-nil", "A-active-ala", "PCP-active-nil", "?-active-nil");
        //
        //
        //        Tmap.put(1, Tgene9);
        //        Tmap.put(2, Tgene8);
        //        Tmap.put(3, Tgene7);
        //        Tmap.put(4, Tgene6);
        //        Tmap.put(5, Tgene5);
        //        Tmap.put(6, Tgene4);
        //        Tmap.put(7, Tgene3);
        //        Tmap.put(8, Tgene2);
        //        Tmap.put(9, Tgene1);
        //
        //
        //        List<String> Qgene1 = Arrays.asList("C-active-nil", "A-active-ala", "PCP-active-nil", "Red-active-nil");
        //        List<String> Qgene2 = Arrays.asList("DH-active-nil", "ER-active-nil", "KR-active-nil", "ACP-active-nil");
        //        List<String> Qgene3 = Arrays.asList("KS-active-nil", "AT-active-mmal");
        //        List<String> Qgene4 = Arrays.asList("KS-active-nil", "AT-active-mal", "DH-active-nil", "KR-active-nil", "ACP-active-nil", "KS-active-nil", "AT-active-mal", "KR-active-nil", "ACP-active-nil", "KS-active-nil", "AT-active-mal", "DH-active-nil", "KR-active-nil", "ACP-active-nil");
        //        List<String> Qgene5 = Arrays.asList("KS-active-nil", "AT-active-mmal", "DH-active-nil", "KR-active-nil", "ACP-active-nil");
        //        List<String> Qgene6 = Arrays.asList("KS-active-nil", "AT-active-mmal", "DH-active-nil", "KR-active-nil", "ACP-active-nil");
        //        List<String> Qgene7 = Arrays.asList("ACP-active-nil", "KS-active-nil", "AT-active-iso", "AT-active-mmal", "DH-active-nil", "KR-active-nil", "ACP-active-nil");
        //        Qmap.put(1, Qgene7);
        //        Qmap.put(2, Qgene6);
        //        Qmap.put(3, Qgene5);
        //        Qmap.put(4, Qgene4);
        //        Qmap.put(5, Qgene3);
        //        Qmap.put(6, Qgene2);
        //        Qmap.put(7, Qgene1);

        int window = 2;
        //forward search



        //        Qmap.clear();
        //        Tmap.clear();
        //
        //
        //        xml2text xml2 = new xml2text();
        //
        //        xml2text.main();
        //
        //        Qmap = xml2text.Qmap;
        //        Tmap = xml2text.Tmap;
        ////System.out.println(Qmap);
        ////System.out.println(Tmap);
        Multimap<Integer, Multimap<Double, String>> forward = comparing(Qmap, QgeneIdPosMap, Tmap, TgeneIdPosMap);
        Multimap<Integer, Multimap<Double, String>> forward1 = ArrayListMultimap.create(forward);
        Multimap<Integer, Multimap<Double, String>> reverse = comparing(Tmap, TgeneIdPosMap, Qmap, QgeneIdPosMap);
        //Multimap<Double,String> finalresult = ArrayListMultimap.create();
        List<Integer> query = new ArrayList<Integer>();
        List<Integer> target = new ArrayList<Integer>();
        List<String> values = new ArrayList<String>();
        double scoring = 0;
        int diff = 100;
        String tgene = "";


        for (int i = 0; i < forward1.size(); i++) {
            int count = i + 1;
            double sce = 0;
            //System.out.println(count);
            diff = 100;
            String[] Target_genescores = (getmax1(forward.get(count), reverse)).toString().split("=");
            //System.out.println(getmax1(forward.get(count),reverse));
            tgene = "";

            if (Target_genescores.length > 1) {
                if (Double.parseDouble(Target_genescores[0].toString().trim()) > 0) {
                    if (Target_genescores[1].contains(";")) {
                        String[] genes = Target_genescores[1].toString().split(";");
                        for (String Tgene : genes) {
                            if (Tgene.contains("+")) {
                                String[] p = Tgene.split("\\+");
                                for (String o : p) {
                                    String[] Query_genescores = getmax1(reverse.get(Integer.parseInt(o.trim())), forward).split("=");
                                    ////System.out.println("----sdfds    "+Query_genescores.length);
                                    if (Query_genescores.length > 1) {
                                        //	//System.out.println(getmax(reverse.get(Integer.parseInt(Tgene))));
                                        if (Query_genescores[1].contains(";")) {
                                            String[] Query_values = Query_genescores[1].split(";");
                                            values = Arrays.asList(Query_values);
                                        }
                                        for (String u : values) {
                                            if (u.contains("+")) {
                                                String[] p1 = u.split("\\+");
                                                for (String o1 : p1) {
                                                    if (Integer.parseInt(o1.trim()) == count) {
                                                        if (Double.parseDouble(Query_genescores[0].trim()) <= Double.parseDouble(Target_genescores[0].trim())) {
                                                            scoring = Double.parseDouble(Target_genescores[0].trim());
                                                            int dif = Math.abs(Integer.parseInt(o1.trim()) - count);
                                                            if (diff > dif) {
                                                                diff = dif;
                                                                tgene = Tgene;

                                                            }
                                                        }

                                                    }
                                                }
                                            } else {
                                                if (Integer.parseInt(u) == count) {
                                                    if (Double.parseDouble(Query_genescores[0].trim()) <= Double.parseDouble(Target_genescores[0].trim())) {
                                                        scoring = Double.parseDouble(Target_genescores[0].trim());
                                                        int dif = Math.abs(Integer.parseInt(u) - count);
                                                        if (diff > dif) {
                                                            diff = dif;
                                                            tgene = Tgene;

                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        // tgene = "-1";
                                        scoring = 0;
                                    }
                                }
                            } else {
                                ////System.out.println(Tgene);
                                String[] Query_genescores = getmax1(reverse.get(Integer.parseInt(Tgene)), forward).split("=");
                                //	//System.out.println(Tgene+"                 ----sdfds    "+getmax1(reverse.get(Integer.parseInt(Tgene)),forward));
                                if (Query_genescores.length > 1) {

                                    if (Query_genescores[1].contains(";")) {
                                        String[] Query_values = Query_genescores[1].split(";");
                                        values = Arrays.asList(Query_values);


                                        for (String u : values) {
                                            //	//System.out.println(Query_genescores[0].trim()+"    ff111     "+Target_genescores[0].trim() );
                                            if (Double.parseDouble(Query_genescores[0].trim()) <= Double.parseDouble(Target_genescores[0].trim())) {
                                                scoring = Double.parseDouble(Target_genescores[0].trim());
                                                int dif = Math.abs(Integer.parseInt(u) - count);
                                                if (diff > dif) {
                                                    diff = dif;
                                                    tgene = Tgene;
                                                }
                                            } else {
                                                tgene = "-1";
                                                scoring = 0;
                                            }

                                        }
                                    } else {
                                        ////System.out.println(Query_genescores[0].trim()+"    ff     "+Target_genescores[0].trim()+"   "+Query_genescores[1].trim()+"   "+Target_genescores[1].trim()+"     "+count  );
                                        if (Double.parseDouble(Query_genescores[0].trim()) <= Double.parseDouble(Target_genescores[0].trim())) {
                                            scoring = Double.parseDouble(Target_genescores[0].trim());
                                            int dif = Math.abs(Integer.parseInt(Query_genescores[1].trim()) - count);
                                            if (diff > dif) {
                                                diff = dif;
                                                tgene = Tgene;
                                            }
                                            ////System.out.println(tgene.trim()+"    ff65     "+Query_genescores[1].trim() );
                                        } else if (Double.parseDouble(Query_genescores[0].trim()) > Double.parseDouble(Target_genescores[0].trim())) {
                                            if (Target_genescores[1].trim().contains(";")) {
                                                String[] p = Target_genescores[1].trim().split(";");
                                                for (String p1 : p) {
                                                    scoring = Double.parseDouble(Query_genescores[0].trim());
                                                    int dif = Math.abs(Integer.parseInt(Query_genescores[1].trim()) - Integer.parseInt(p1));
                                                    if (diff > dif) {
                                                        diff = dif;
                                                        tgene = p1;
                                                    }
                                                }
                                                ////System.out.println(tgene.trim()+"    ff65     "+Query_genescores[1].trim() );
                                                try {
                                                    reverse.asMap().remove(Integer.parseInt(tgene.trim()));
                                                } catch (Exception dsc) {
                                                    System.out.println(dsc + " ok7");
                                                }

                                                forward.asMap().remove(Integer.parseInt(Query_genescores[1].trim()));
                                                finalresult.put(scoring, Query_genescores[1].trim() + "=" + tgene);
                                                tgene = "";




                                            }
                                        } else {
                                            tgene = "-1";
                                            scoring = 0;
                                        }

                                    }
                                } else {
                                    tgene = "-1";
                                    scoring = 0;
                                }
                            }
                        }
                    } else {

                        String Tgene = Target_genescores[1].toString().trim();
                        if (Tgene.contains("+")) {
                            String[] Tgenes = Tgene.split("\\+");
                            for (String gene : Tgenes) {
                                String[] Query_genescores = getmax1(reverse.get(Integer.parseInt(gene)), forward).split("=");
                                sce += Double.parseDouble(Query_genescores[0].trim());
                            }

                            sce = (sce / 2);
                            ////System.out.println(sce+"   dfs               ");

                            if (sce <= Double.parseDouble(Target_genescores[0].trim())) {
                                scoring = Double.parseDouble(Target_genescores[0].trim());
                                tgene = Tgene;
                                for (String gene : Tgenes) {
                                    try {
                                        reverse.asMap().remove(Integer.parseInt(gene.trim()));
                                    } catch (Exception dsc) {
                                        System.out.println(dsc + " ok6");
                                    }
                                }
                                forward.asMap().remove(count);
                            } else {
                                for (String gene : Tgenes) {
                                    String[] Query_genescores = getmax1(reverse.get(Integer.parseInt(gene)), forward).split("=");
                                    if (Query_genescores.length > 1) {
                                        //	//System.out.println(getmax(reverse.get(Integer.parseInt(Tgene))));
                                        if (Query_genescores[1].contains(";")) {
                                            String[] Query_values = Query_genescores[1].split(";");
                                            for (String u : Query_values) {

                                                if (Double.parseDouble(Query_genescores[0].trim()) <= Double.parseDouble(Target_genescores[0].trim())) {
                                                    scoring = Double.parseDouble(Target_genescores[0].trim());
                                                    int dif = Math.abs(Integer.parseInt(u) - count);
                                                    if (diff > dif) {
                                                        diff = dif;
                                                        tgene = gene;
                                                    }
                                                } else {
                                                    //tgene = "-1";
                                                }
                                            }
                                        } else {
                                            if (Query_genescores[1].trim().contains("+")) {
                                                String[] gens = Query_genescores[1].trim().split("\\+");
                                                for (String gen : gens) {
                                                    if (Double.parseDouble(Query_genescores[0].trim()) <= Double.parseDouble(Target_genescores[0].trim())) {
                                                        scoring = Double.parseDouble(Target_genescores[0].trim());
                                                        int dif = Math.abs(Integer.parseInt(gen.trim()) - count);
                                                        if (diff > dif) {
                                                            diff = dif;
                                                            tgene = Tgene;
                                                        }
                                                    } else {
                                                        tgene = "-1";
                                                    }
                                                }
                                            } else {
                                                String u = Query_genescores[1].trim();
                                                if (Double.parseDouble(Query_genescores[0].trim()) <= Double.parseDouble(Target_genescores[0].trim())) {
                                                    scoring = Double.parseDouble(Target_genescores[0].trim());
                                                    int dif = Math.abs(Integer.parseInt(u) - count);
                                                    if (diff > dif) {
                                                        diff = dif;
                                                        tgene = Tgene;
                                                    }
                                                } else if (Double.parseDouble(Query_genescores[0].trim()) >= Double.parseDouble(Target_genescores[0].trim())) {
                                                    scoring = Double.parseDouble(Query_genescores[0].trim());
                                                    int dif = Math.abs(Integer.parseInt(u) - count);
                                                    if (diff > dif) {
                                                        diff = dif;
                                                        tgene = Tgene;
                                                    }
                                                } else {
                                                    tgene = "-1";
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            ////System.out.println(Tgene);
                            String[] Query_genescores = getmax1(reverse.get(Integer.parseInt(Tgene)), forward).split("=");
                            System.out.println(getmax1(reverse.get(Integer.parseInt(Tgene)), forward));


                            //System.out.println(Tgene+"             "+getmax1(reverse.get(Integer.parseInt(Tgene)),forward));
                            if (Query_genescores.length > 1) {
                                ////System.out.println(getmax1(reverse.get(Integer.parseInt(Tgene)),forward));
                                if (Query_genescores[1].contains(";")) {
                                    String[] Query_values = Query_genescores[1].split(";");
                                    for (String u : Query_values) {
                                        if (u.contains("+")) {
                                            String[] p1 = u.split("\\+");
                                            for (String o1 : p1) {

                                                if (Double.parseDouble(Query_genescores[0].trim()) <= Double.parseDouble(Target_genescores[0].trim())) {
                                                    scoring = Double.parseDouble(Target_genescores[0].trim());
                                                    int dif = Math.abs(Integer.parseInt(o1.trim()) - count);
                                                    if (diff > dif) {
                                                        diff = dif;
                                                        tgene = Tgene;
                                                    }
                                                } else {
                                                    tgene = "-1";
                                                }
                                            }
                                        } else {

                                            if (Double.parseDouble(Query_genescores[0].trim()) <= Double.parseDouble(Target_genescores[0].trim())) {
                                                scoring = Double.parseDouble(Target_genescores[0].trim());
                                                int dif = Math.abs(Integer.parseInt(u) - count);
                                                if (diff > dif) {
                                                    diff = dif;
                                                    tgene = Tgene;
                                                }
                                            } else {
                                                tgene = "-1";
                                            }
                                        }
                                    }

                                } else if (Query_genescores[1].contains("+")) {

                                    String[] Tgenes = Query_genescores[1].split("\\+");
                                    int sd = 0;
                                    for (String gene : Tgenes) {

                                        String[] Query_genescores1 = getmax1(forward.get(Integer.parseInt(gene)), reverse).split("=");
                                        //	System.out.println(gene+"        "+Arrays.asList(Query_genescores1)+"            "+forward);
                                        //	System.out.println(getmax1(forward.get(Integer.parseInt(gene)), reverse) + "  kmn");
                                        //System.out.println(Query_genescores[0] + "   dfs               " + Query_genescores1[0].trim());
                                        if (Query_genescores1[0].trim().length() > 1) {
                                            if (Double.parseDouble(Query_genescores1[0].trim()) >= Double.parseDouble(Query_genescores[0].trim())) {
                                                scoring = Double.parseDouble(Query_genescores1[0].trim());
                                                sd = sd + 1;
                                                tgene = "";
                                                try {
                                                    reverse.asMap().remove(Integer.parseInt(Query_genescores1[1].trim().trim()));
                                                } catch (Exception dsc) {
                                                    System.out.println(dsc + " ok5");
                                                }
                                                //System.out.println(Query_genescores1[0]+"    dsadsadsa1     "+Query_genescores[0]);
                                                forward.asMap().remove(Integer.parseInt(gene.trim()));

                                                finalresult.put(scoring, gene.trim() + "=" + Query_genescores1[1].trim());
                                                if (sd == Tgenes.length) {
                                                    try {
                                                        reverse.asMap().remove(Integer.parseInt(Target_genescores[1].trim().trim()));
                                                    } catch (Exception dsc) {
                                                        System.out.println(dsc + " ok4");
                                                    }
                                                }
                                                if (forward.keys().contains(count)) {
                                                    finalresult.put(Double.parseDouble(Target_genescores[0].trim()), count + "=" + Target_genescores[1].trim());
                                                    forward.asMap().remove((count));
                                                }

                                            } else if (Double.parseDouble(Query_genescores1[0].trim()) < Double.parseDouble(Query_genescores[0].trim())) {
                                                scoring = Double.parseDouble(Query_genescores1[0].trim());
                                                sd = sd + 1;
                                                tgene = "";
                                                try {
                                                    reverse.asMap().remove(Integer.parseInt(Query_genescores1[1].trim().trim()));
                                                } catch (Exception dsc) {
                                                    System.out.println(dsc + " ok3");
                                                }
                                                //System.out.println(Query_genescores[0]+"    dsadsadsa     "+Target_genescores[1]);
                                                forward.asMap().remove(Integer.parseInt(gene.trim()));

                                                finalresult.put(scoring, gene.trim() + "=" + Query_genescores1[1].trim());
                                                if (sd == Tgenes.length) {
                                                    try {
                                                        reverse.asMap().remove(Integer.parseInt(Target_genescores[1].trim().trim()));
                                                    } catch (Exception dsc) {
                                                        System.out.println(dsc + " ok2");
                                                    }
                                                }
                                                if (Query_genescores[1].contains("+")) {

                                                    String[] gens = Query_genescores[1].trim().split("\\+");
                                                    for (String gen : gens) {
                                                        forward.asMap().remove(Integer.parseInt(gen));
                                                    }
                                                    finalresult.put(Double.parseDouble(Target_genescores[0].trim()), Query_genescores[1] + "=" + Target_genescores[1].trim());
                                                    break;
                                                }

                                                if (forward.keys().contains(count)) {
                                                    finalresult.put(Double.parseDouble(Target_genescores[0].trim()), count + "=" + Target_genescores[1].trim());
                                                    forward.asMap().remove((count));
                                                }

                                            } else {
                                                scoring = 0;
                                                tgene = "-1";
                                            }
                                        } else {
                                            System.out.println(gene + "   dfs               " + Query_genescores[0].trim());
                                            //finalresult.put(0.0, gene.trim() + "=" + "-1");
                                        }

                                    }
                                } else {
                                    //String u = Query_genescores[1].trim();
                                    String[] Tgenes = Tgene.split("\\+");


                                    for (String gene : Tgenes) {
                                        String[] Query_genescores1 = getmax1(reverse.get(Integer.parseInt(gene)), forward).split("=");
                                        //System.out.println(count+"          "+Tgene+"           "+getmax1(reverse.get(Integer.parseInt(gene)),forward));
                                        sce += Double.parseDouble(Query_genescores1[0].trim());
                                        ////System.out.println(sce+"   dfs               "+Query_genescores[0].trim());
                                    }

                                    sce = (sce / 2);
                                    //	//System.out.println(sce+"         "+Target_genescores[0].trim());
                                    if (Query_genescores[1].trim().contains("+")) {
                                        String[] gens = Query_genescores[1].trim().split("\\+");
                                        for (String gen : gens) {


                                            if (sce <= Double.parseDouble(Target_genescores[0].trim())) {
                                                scoring = Double.parseDouble(Target_genescores[0].trim());
                                                int dif = Math.abs(Integer.parseInt(gen.trim()) - count);
                                                if (diff > dif) {
                                                    diff = dif;
                                                    tgene = Tgene;

                                                }

                                            } else {
                                                tgene = "-1";
                                            }
                                        }
                                        if (Double.parseDouble(Query_genescores[0].trim()) >= Double.parseDouble(Target_genescores[0].trim())) {
                                            scoring = Double.parseDouble(Query_genescores[0].trim());

                                            tgene = "";
                                            try {
                                                reverse.asMap().remove(Integer.parseInt(Tgene.trim()));
                                            } catch (Exception dsc) {
                                                System.out.println(dsc + " ok1");
                                            }
                                            for (String gen : gens) {
                                                forward.asMap().remove(Integer.parseInt(gen));
                                            }
                                            finalresult.put(scoring, Query_genescores[1] + "=" + Tgene);

                                        } else {
                                            tgene = "-1";
                                        }

                                    } else {
                                        String u = Query_genescores[1].trim();
                                        ////System.out.println(Query_genescores[0].trim()+"       "+Double.parseDouble(Target_genescores[0].trim()));


                                        if (Double.parseDouble(Query_genescores[0].trim()) <= Double.parseDouble(Target_genescores[0].trim())) {
                                            scoring = Double.parseDouble(Target_genescores[0].trim());
                                            int dif = Math.abs(Integer.parseInt(u) - count);
                                            if (diff > dif) {
                                                diff = dif;
                                                tgene = Tgene;

                                            }

                                        } else {
                                            tgene = "-1";
                                            scoring = 0;
                                        }
                                    }
                                }

                            }
                        }
                    }



                    //	//System.out.println(scoring+"     ;           "+ tgene);
                    if (!tgene.trim().equals("")) {
                        finalresult.put(scoring, count + "=" + tgene);
                        System.out.println("finalresult                    " + finalresult);
                        forward.asMap().remove(count);
                        try {
                            reverse.asMap().remove(Integer.parseInt(tgene.trim()));
                        } catch (Exception dsc) {

                            System.out.println(dsc);
                        }
                    }
                } else {

                    tgene = "-1";
                    scoring = 0;
                    finalresult.put(0.0, count + "=" + tgene);
                }
            }

        }
        System.out.println(finalresult);
        JPanel p = new JPanel();
        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.insets = new Insets(2, 1, 2, 1);

        gbc1.weightx = 1.0;
        gbc1.weighty = 1.0;
        gbc1.gridy = 1;
        double scoring1 = 0;
        long startTime = System.currentTimeMillis();
        int countdash = 0, countalph = 0;
        for (Entry<Double, String> i : finalresult.entries()) {
            List<String> list = new ArrayList<String>();
            String[] j = i.getValue().split("=");
            if (j[0].contains("+")) {
                List<String> tfunction = new ArrayList<String>();


                List<String> qfunction = new ArrayList<String>();

                for (String i1 : Tmap.get(tbiMap.get(Integer.parseInt(j[1].trim().trim())))) {

                    String[] get = i1.split("_");
                    tfunction.add(get[0].trim());

                }
                //	////System.out.println(i.getValue());
                String[] k = j[0].split("\\+");
                for (String h : k) {
                    int key = qbiMap.get(Integer.parseInt(h.trim()));
                    if (Qmap.containsKey(key)) {
                        for (String i1 : (String[]) Qmap.get(key).toArray()) {

                            String[] get = i1.split("_");
                            qfunction.add(get[0].trim());

                        }
                    }
                }
                SmithWaterman_b sw = new SmithWaterman_b();
                sw.init(qfunction, tfunction);
                sw.process();
                sw.backtrack();
                countdash += sw.countdash;
                countalph += sw.countlet;

                //sw.printScoreAndAlignments();

                //	score =  (double)Math.round(((score1+score2+score3)/3) * 100.0) / 100.0;
                //SmithWaterman sw = new SmithWaterman(qfunction,tfunction);
                //	////System.out.println("Alignment Score: " + sw.computeSmithWaterman());
                ////System.out.println("");
                JLabel io = new JLabel();
                io.setText("<html><font color=\"brown\">" + sw.printScoreAndAlignments1() + "<br>" + sw.printScoreAndAlignments2() + "</font></html>");
                this.setFirstPathwayAlignment(sw.printScoreAndAlignments1());
                this.setSecondPathwayAlignment(sw.printScoreAndAlignments2());
                JLabel io1 = new JLabel("      ");
                p.add(io, gbc1);
                p.add(io1, gbc1);
                gbc1.gridy++;
                scoring1 += sw.scoring();

            } else if (j[1].contains("+")) {
                List<String> tfunction = new ArrayList<String>();

                List<String> tfunction1 = new ArrayList<String>();
                List<String> qfunction = new ArrayList<String>();

                for (String i1 : Qmap.get(qbiMap.get(Integer.parseInt(j[0].trim())))) {

                    String[] get = i1.split("_");
                    qfunction.add(get[0].trim());

                }

                //	////System.out.println(i.getValue());
                String[] k = j[1].split("\\+");
                for (String h : k) {

                    for (String i1 : Tmap.get(tbiMap.get(Integer.parseInt(h.trim())))) {

                        String[] get = i1.split("_");
                        tfunction.add(get[0].trim());
                        tfunction1.add(get[0].trim());
                    }

                }
                SmithWaterman_b sw = new SmithWaterman_b();
                sw.init(qfunction, tfunction);
                sw.process();
                sw.backtrack();

                countdash += sw.countdash;
                countalph += sw.countlet;
                sw.printScoreAndAlignments();



                JLabel io = new JLabel();
                io.setText("<html><font color=\"green\">" + sw.printScoreAndAlignments1() + "<br>" + sw.printScoreAndAlignments2() + "</font></html>");
                this.setFirstPathwayAlignment(sw.printScoreAndAlignments1());
                this.setSecondPathwayAlignment(sw.printScoreAndAlignments2());
                JLabel io1 = new JLabel("      ");
                p.add(io, gbc1);
                p.add(io1, gbc1);
                gbc1.gridy++;



                scoring1 += sw.scoring();
                //SmithWaterman sw = new SmithWaterman(qfunction,tfunction);
                //////System.out.println("Alignment Score: " + sw.computeSmithWaterman());
                ////System.out.println("");
            } else if (j[1].contains("-1")) {
                List<String> qfunction = new ArrayList<String>();
                for (String i1 : Qmap.get(qbiMap.get(Integer.parseInt(j[0].trim())))) {

                    String[] get = i1.split("_");
                    qfunction.add(get[0].trim());

                }
                countdash += qfunction.size();

                ////System.out.println(qfunction.toString().replace("[", "").replace("]", "").replace(",", ""));
                ////System.out.println("--not found--");
                ////System.out.println("");

                JLabel io = new JLabel();
                io.setText("<html><font color=\"red\">" + qfunction.toString().replace("[", "").replace("]", "").replace(",", "") + "<br>--not found--</font></html>");
                JLabel io1 = new JLabel("      ");
                p.add(io, gbc1);
                p.add(io1, gbc1);
                gbc1.gridy++;
                scoring1 += 0;

            } else if (j[0].contains("-1")) {
                List<String> qfunction = new ArrayList<String>();
                for (String i1 : Tmap.get(tbiMap.get(Integer.parseInt(j[1].trim())))) {

                    String[] get = i1.split("_");
                    qfunction.add(get[0].trim());

                }
                countdash += qfunction.size();

                ////System.out.println(qfunction.toString().replace("[", "").replace("]", "").replace(",", ""));
                ////System.out.println("--not found--");
                ////System.out.println("");

                JLabel io = new JLabel();
                io.setText("<html><font color=\"red\">--not found--<br>" + qfunction.toString().replace("[", "").replace("]", "").replace(",", "") + "</font></html>");
                JLabel io1 = new JLabel("      ");
                p.add(io, gbc1);
                p.add(io1, gbc1);
                gbc1.gridy++;
                scoring1 += 0;
            } else {
                //////System.out.println(i.getValue());
                List<String> tfunction = new ArrayList<String>();

                for (String i1 : Tmap.get(tbiMap.get(Integer.parseInt(j[1].trim())))) {

                    String[] get = i1.split("_");
                    tfunction.add(get[0].trim());

                }

                List<String> qfunction = new ArrayList<String>();

                for (String i1 : Qmap.get(qbiMap.get(Integer.parseInt(j[0].trim())))) {

                    String[] get = i1.split("_");
                    qfunction.add(get[0].trim());

                }
                SmithWaterman_b sw = new SmithWaterman_b();
                sw.init(qfunction, tfunction);
                sw.process();
                sw.backtrack();
                countdash += sw.countdash;
                countalph += sw.countlet;
                ////System.out.println(sw.printScoreAndAlignments1());
                ////System.out.println("");
                JLabel io = new JLabel();
                io.setText("<html><font color=\"blue\">" + sw.printScoreAndAlignments1() + "<br>" + sw.printScoreAndAlignments2() + "</font></html>");
                this.setFirstPathwayAlignment(sw.printScoreAndAlignments1());
                this.setSecondPathwayAlignment(sw.printScoreAndAlignments2());
                JLabel io1 = new JLabel("      ");
                p.add(io, gbc1);
                p.add(io1, gbc1);
                gbc1.gridy++;
                scoring1 += sw.scoring();
                //String[] k = sw.printScoreAndAlignments();
                //SmithWaterman sw = new SmithWaterman(qfunction,tfunction);
                //////System.out.println("Alignment Score: " + sw.computeSmithWaterman());
                ////System.out.println("");

            }
            //////System.out.println(i.getKey()+"       "+i.getValue());
        }//
        List<String> qfunction = new ArrayList<String>();
        List<String> tfunction = new ArrayList<String>();

        for (List<String> i1 : Tmap.values()) {
            for (String i2 : i1) {
                String[] get = i2.split("_");
                tfunction.add(get[0].trim());
            }
            tfunction.add("                 ");
        }
        for (List<String> i1 : Qmap.values()) {
            for (String i2 : i1) {
                String[] get = i2.split("_");
                qfunction.add(get[0].trim());
            }

            qfunction.add("                 ");
        }
        JPanel kk = new JPanel();
        JLabel io1 = new JLabel();
        JLabel io = new JLabel();
        Collections.reverse(qfunction);
        Collections.reverse(tfunction);
        JLabel io2 = new JLabel("<html><font color=\"Purple\">" + qfunction + "</font><br><font color=\"Olive\">" + tfunction + " </font></html>");


        //kk.add(io2,gbc1);
        int sc = (countalph * 1) - (countdash * 3);
        double k = 0.747;
        double lambda = 1.39;
        double sco = ((lambda * sc) + Math.log(k)) / Math.log(2);


        // (double)Math.round(((score1+score2+score3)/3) * 100.0) / 100.0
        sco = Math.round(sco * 100.0) / 100.0;
        int m = 500, n = 0;
        for (Entry<Integer, List<String>> i : Qmap.entrySet()) {

            n += i.getValue().size();
        }
        for (Entry<Integer, List<String>> i : Tmap.entrySet()) {

            m += i.getValue().size();
        }
        ////System.out.println(countalph+"       elapsedTime:       " +  countdash);
        ////System.out.println(Math.log(2)+"    fafafad         " +  sco);
        double eval = (m * n * k * (Math.exp(-(lambda * sc))));

        //eval = (double)Math.round(eval) ;



        ////System.out.println(n+"   "+m+"    eval         "+eval);
        DecimalFormat df = new DecimalFormat("#.##");
        ////System.out.print(df.format(eval));
        io.setText("<html><font color=\"Purple\">Bitscore:  </font><font color=\"Olive\">" + sco + " </font></html>");
        io1.setText("<html><font color=\"Maroon\">e-val:  </font><font color=\"Olive\">" + eval + " </font></html>");
        kk.add(io, gbc1);
        gbc1.gridy++;
        kk.add(io1, gbc1);
        frame.add(p, BorderLayout.NORTH);
        frame.add(kk, BorderLayout.SOUTH);
        frame.setVisible(true);
        j2.setText("<html><font color=\"red\">hello world!</font></html>");
        frame.add(j2, BorderLayout.NORTH);
        long stopTime = System.currentTimeMillis();
        double elapsedTime = ((stopTime - startTime));


        // expected value: kmne^-(lamda*s)


        //////System.out.println("elapsedTime: " +  elapsedTime);
    }

    public String getmax1(Collection<Multimap<Double, String>> collection, Multimap<Integer, Multimap<Double, String>> reverse) {
        String j = "";
        String score = "";
        double max = 0;
        boolean sa = true;
        /*reverse.asMap().remove(4);
         reverse.asMap().remove(5);*/
        //////System.out.println(collection);
        if (collection.size() > 0) {
            while (sa) {
                for (Multimap<Double, String> i : collection) {
                    try {
                        max = i.keySet().iterator().next();
                        //////System.out.println(max);
                        setFinalscore(max);
                        for (String s : i.get(max)) {
                            //	////System.out.println(s);
                            if (s.contains("+")) {
                                String[] k = s.split("\\+");
                                for (String ju : k) {
                                    int p = 0;
                                    if (reverse.containsKey(Integer.parseInt(ju))) {

                                        if (j.equals("")) {
                                            j = ju;
                                        } else {
                                            j += "+" + ju;
                                        }
                                    }


                                }
                            } else {
                                //////System.out.println(s+"     dsad         "+reverse);
                                if (reverse.containsKey(Integer.parseInt(s))) {

                                    if (j.equals("")) {
                                        j = s;
                                    } else {
                                        j += ";" + s;
                                    }
                                }

                            }

                        }

                        if (j.trim().equals("")) {
                            i.asMap().remove(max);

                        }
                    } catch (Exception ds) {
                    }
                }
                //////System.out.println(j+"          dsa");
                if (!j.trim().equals("")) {
                    sa = false;

                }

                double roundOff = (double) Math.round(max * 100) / 100;
                score = roundOff + "=" + j;
                //////System.out.println(score);

            }
        }
        return score;
    }

    public static void removemax(Multimap<Integer, Multimap<Double, String>> collections, int key) {
        collections.asMap().remove(key);
    }

    public static void seperate() {
        List<String> Qgene1 = Arrays.asList("C", "A", "PCP", "Red");
        List<String> Tgene1 = Arrays.asList("C", "A", "PCP", "?");
        //	////System.out.println(Qgene1+" " + Tgene1);

        //////System.out.println(LevenshteinDistance.computeLevenshteinDistance(Qgene1,Tgene1));
    }

    public static Multimap<Integer, Multimap<Double, String>> comparing(Map<Integer, List<String>> Qmap, Map<Integer, Integer> QgeneIdPosMap, Map<Integer, List<String>> Tmap, Map<Integer, Integer> TgeneIdPosMap) {
        int next_itr_q = 0;
        Multimap<Double, String> forwardscores = ArrayListMultimap.create();
        Multimap<Integer, Multimap<Double, String>> Forward_bestscores = ArrayListMultimap.create();
        BiMap<Integer, Integer> qbiMap0 = HashBiMap.create();
        qbiMap0.putAll(QgeneIdPosMap);
        BiMap<Integer, Integer> qbiMap = qbiMap0.inverse();
        BiMap<Integer, Integer> tbiMap0 = HashBiMap.create();
        tbiMap0.putAll(TgeneIdPosMap);
        BiMap<Integer, Integer> tbiMap = tbiMap0.inverse();

        for (Map.Entry<Integer, List<String>> query : Qmap.entrySet()) {
            double bestval = 0;
            next_itr_q = QgeneIdPosMap.get(query.getKey());
            int next_itr_t = 0;


            forwardscores.clear();
            for (Entry<Integer, List<String>> target : Tmap.entrySet()) {


                //next_itr_t = next_itr_t + 1;
                next_itr_t = TgeneIdPosMap.get(target.getKey());
                if ((query.getValue().size() == target.getValue().size()) || (query.getValue().size() < target.getValue().size())) {
                    List<String> qfunction = new ArrayList<String>();
                    List<String> qactivity = new ArrayList<String>();
                    List<String> qsubstrate = new ArrayList<String>();
                    for (String i : query.getValue()) {

                        String[] get = i.split("_");
                        qfunction.add(get[0].trim());
                        qactivity.add(get[1].trim());
                        qsubstrate.add(get[2].trim());
                    }
                    double score = 0.0;
                    double score1 = 0.0;
                    double score2 = 0.0;
                    double score3 = 0.0;
                    //SmithWaterman sw = new SmithWaterman(query.getValue(),target.getValue());

                    List<String> tfunction = new ArrayList<String>();
                    List<String> tactivity = new ArrayList<String>();
                    List<String> tsubstrate = new ArrayList<String>();
                    for (String i : target.getValue()) {

                        String[] get = i.split("_");
                        tfunction.add(get[0].trim());
                        tactivity.add(get[1].trim());
                        tsubstrate.add(get[2].trim());
                    }


                    score1 = 1 - ((double) LevenshteinDistance.computeLevenshteinDistance(qfunction, tfunction) / (Math.max(qfunction.size(), tfunction.size())));

                    score2 = 1 - ((double) LevenshteinDistance.computeLevenshteinDistance(qactivity, tactivity) / (Math.max(qactivity.size(), tactivity.size())));
                    score3 = 1 - ((double) LevenshteinDistance.computeLevenshteinDistance(qsubstrate, tsubstrate) / (Math.max(qsubstrate.size(), tsubstrate.size())));
                    score = Math.round(((score1 + score2 + score3) / 3) * 100.0) / 100.0;

                    //////System.out.println(query.getValue()+"    "+target.getValue());
                    //////System.out.println(LevenshteinDistance.computeLevenshteinDistance(qsubstrate,tsubstrate));
                    //////System.out.println(score1+"        "+score2+"        "+score3+"        "+score);

                    forwardscores.put(score, Integer.toString(next_itr_t));

                } else if (query.getValue().size() < target.getValue().size()) {

                    if (next_itr_q + 1 < Qmap.size()) {


                        List<String> qfunction = new ArrayList<String>();
                        List<String> qactivity = new ArrayList<String>();
                        List<String> qsubstrate = new ArrayList<String>();
                        for (String i : target.getValue()) {

                            String[] get = i.split("_");
                            qfunction.add(get[0].trim());
                            qactivity.add(get[1].trim());
                            qsubstrate.add(get[2].trim());
                        }


                        List<String> t1 = Qmap.get(qbiMap.get(next_itr_q));
                        List<String> t2 = Qmap.get(qbiMap.get(next_itr_q + 1));
                        int count = t1.size() + t2.size();
                        String itr = next_itr_q + "+" + (next_itr_q + 1);
                        List<String> newList = new ArrayList<String>();
                        List<String> rnewList = new ArrayList<String>();
                        newList.addAll(t1);
                        newList.addAll(t2);

                        rnewList.addAll(t2);
                        rnewList.addAll(t1);
                        ////////System.out.println(query.getValue()+"         " + newList+"                "+itr);
                        double rscore = 0.0;
                        double score = 0.0;
                        double score1 = 0.0;
                        double score2 = 0.0;
                        double score3 = 0.0;
                        //SmithWaterman sw = new SmithWaterman(query.getValue(),target.getValue());
                        List<String> tfunction = new ArrayList<String>();
                        List<String> tactivity = new ArrayList<String>();
                        List<String> tsubstrate = new ArrayList<String>();
                        List<String> rtfunction = new ArrayList<String>();
                        List<String> rtactivity = new ArrayList<String>();
                        List<String> rtsubstrate = new ArrayList<String>();

                        for (String i : newList) {

                            String[] get = i.split("_");
                            tfunction.add(get[0].trim());
                            tactivity.add(get[1].trim());
                            tsubstrate.add(get[2].trim());
                        }
                        for (String i : rnewList) {

                            String[] get = i.split("_");
                            rtfunction.add(get[0].trim());
                            rtactivity.add(get[1].trim());
                            rtsubstrate.add(get[2].trim());
                        }





                        //SmithWaterman sw = new SmithWaterman(query.getValue(),newList);
                        ////////System.out.println("Alignment Score: " + sw.computeSmithWaterman());
                        rscore = 1 - ((double) LevenshteinDistance.computeLevenshteinDistance(qfunction, rtfunction) / (Math.max(qfunction.size(), rtfunction.size())));


                        score1 = 1 - ((double) LevenshteinDistance.computeLevenshteinDistance(qfunction, tfunction) / (Math.max(qfunction.size(), tfunction.size())));
                        if (rscore < score1) {

                            score2 = 1 - ((double) LevenshteinDistance.computeLevenshteinDistance(qactivity, tactivity) / (Math.max(qactivity.size(), tactivity.size())));
                            score3 = 1 - ((double) LevenshteinDistance.computeLevenshteinDistance(qsubstrate, tsubstrate) / (Math.max(qsubstrate.size(), tsubstrate.size())));
                        } else {
                            score1 = rscore;
                            itr = (next_itr_q + 1) + "+" + (next_itr_q);
                            score2 = 1 - ((double) LevenshteinDistance.computeLevenshteinDistance(qactivity, rtactivity) / (Math.max(qactivity.size(), rtactivity.size())));
                            score3 = 1 - ((double) LevenshteinDistance.computeLevenshteinDistance(qsubstrate, rtsubstrate) / (Math.max(qsubstrate.size(), rtsubstrate.size())));

                        }
                        score = Math.round((((2 * score1) + (0.5 * score2) + (0.5 * score3)) / 3) * 100.0) / 100.0;

                        //////System.out.println(query.getValue()+"    "+newList);
                        //////System.out.println(LevenshteinDistance.computeLevenshteinDistance(qsubstrate,tsubstrate));
                        //////System.out.println(score1+"        "+score2+"        "+score3+"        "+score);

                        forwardscores.put(score, itr);
                    }
                } else if (query.getValue().size() > target.getValue().size()) {
                    List<String> qfunction = new ArrayList<String>();
                    List<String> qactivity = new ArrayList<String>();
                    List<String> qsubstrate = new ArrayList<String>();
                    for (String i : query.getValue()) {

                        String[] get = i.split("_");
                        qfunction.add(get[0].trim());
                        qactivity.add(get[1].trim());
                        qsubstrate.add(get[2].trim());
                    }
                    if (next_itr_t + 1 < Tmap.size()) {
                        List<String> t1 = Tmap.get(tbiMap.get(next_itr_t));
                        List<String> t2 = Tmap.get(tbiMap.get(next_itr_t + 1));
                        int count = t1.size() + t2.size();
                        String itr = next_itr_t + "+" + (next_itr_t + 1);
                        List<String> newList = new ArrayList<String>();
                        List<String> rnewList = new ArrayList<String>();
                        newList.addAll(t1);
                        newList.addAll(t2);

                        rnewList.addAll(t2);
                        rnewList.addAll(t1);
                        ////////System.out.println(query.getValue()+"         " + newList+"                "+itr);
                        double rscore = 0.0;
                        double score = 0.0;
                        double score1 = 0.0;
                        double score2 = 0.0;
                        double score3 = 0.0;
                        //SmithWaterman sw = new SmithWaterman(query.getValue(),target.getValue());
                        List<String> tfunction = new ArrayList<String>();
                        List<String> tactivity = new ArrayList<String>();
                        List<String> tsubstrate = new ArrayList<String>();
                        List<String> rtfunction = new ArrayList<String>();
                        List<String> rtactivity = new ArrayList<String>();
                        List<String> rtsubstrate = new ArrayList<String>();

                        for (String i : newList) {

                            String[] get = i.split("_");
                            tfunction.add(get[0].trim());
                            tactivity.add(get[1].trim());
                            tsubstrate.add(get[2].trim());
                        }
                        for (String i : rnewList) {

                            String[] get = i.split("_");
                            rtfunction.add(get[0].trim());
                            rtactivity.add(get[1].trim());
                            rtsubstrate.add(get[2].trim());
                        }





                        //SmithWaterman sw = new SmithWaterman(query.getValue(),newList);
                        ////////System.out.println("Alignment Score: " + sw.computeSmithWaterman());
                        rscore = 1 - ((double) LevenshteinDistance.computeLevenshteinDistance(qfunction, rtfunction) / (Math.max(qfunction.size(), rtfunction.size())));


                        score1 = 1 - ((double) LevenshteinDistance.computeLevenshteinDistance(qfunction, tfunction) / (Math.max(qfunction.size(), tfunction.size())));
                        if (rscore < score1) {

                            score2 = 1 - ((double) LevenshteinDistance.computeLevenshteinDistance(qactivity, tactivity) / (Math.max(qactivity.size(), tactivity.size())));
                            score3 = 1 - ((double) LevenshteinDistance.computeLevenshteinDistance(qsubstrate, tsubstrate) / (Math.max(qsubstrate.size(), tsubstrate.size())));
                        } else {
                            score1 = rscore;
                            itr = (next_itr_t + 1) + "+" + (next_itr_t);
                            score2 = 1 - ((double) LevenshteinDistance.computeLevenshteinDistance(qactivity, rtactivity) / (Math.max(qactivity.size(), rtactivity.size())));
                            score3 = 1 - ((double) LevenshteinDistance.computeLevenshteinDistance(qsubstrate, rtsubstrate) / (Math.max(qsubstrate.size(), rtsubstrate.size())));

                        }
                        score = Math.round((((2 * score1) + (0.5 * score2) + (0.5 * score3)) / 3) * 100.0) / 100.0;

                        //////System.out.println(query.getValue()+"    "+newList);
                        //////System.out.println(LevenshteinDistance.computeLevenshteinDistance(qsubstrate,tsubstrate));
                        //////System.out.println(score1+"        "+score2+"        "+score3+"        "+score);

                        forwardscores.put(score, itr);
                    }
                }
            }
            Multimap<Double, String> forwardscore1 = ArrayListMultimap.create(forwardscores);
            TreeMultimap<Double, String> forwardscore = TreeMultimap.create(Ordering.natural().reverse(), Ordering.natural());

            forwardscore.putAll(forwardscore1);

            Forward_bestscores.put(next_itr_q, forwardscore);

        }
        System.out.println(Forward_bestscores);
        return Forward_bestscores;

    }

    public void calculations1(Multimap<Integer, Multimap<Double, String>> forward, Multimap<Integer, Multimap<Double, String>> forward1, Multimap<Integer, Multimap<Double, String>> reverse) {
        double scoring = 0;
        int diff = 100;
        String tgene = "";

        List<Integer> query = new ArrayList<Integer>();
        List<Integer> target = new ArrayList<Integer>();
        List<String> values = new ArrayList<String>();
        Multimap<Integer, Multimap<Double, String>> forward2 = ArrayListMultimap.create(forward);
        for (int i : forward2.keys()) {
            int count = i;
            //System.out.println("Count:  "+forward.get(count).size());
            if (forward.get(count).size() > 0) {
                double sce = 0;
                //System.out.println(count);
                diff = 100;
                String[] Target_genescores = (getmax1(forward.get(count), reverse)).toString().split("=");
                //System.out.println(getmax1(forward.get(count),reverse)+"    "+Target_genescores.length);
                tgene = "";

                if (Target_genescores.length > 1) {
                    if (Double.parseDouble(Target_genescores[0].toString().trim()) > 0) {
                        if (Target_genescores[1].contains(";")) {
                            String[] genes = Target_genescores[1].toString().split(";");
                            for (String Tgene : genes) {
                                if (Tgene.contains("+")) {
                                    String[] p = Tgene.split("\\+");
                                    for (String o : p) {
                                        String[] Query_genescores = getmax1(reverse.get(Integer.parseInt(o.trim())), forward).split("=");
                                        //////System.out.println("----sdfds    "+Query_genescores.length);
                                        if (Query_genescores.length > 1) {
                                            //	////System.out.println(getmax(reverse.get(Integer.parseInt(Tgene))));
                                            if (Query_genescores[1].contains(";")) {
                                                String[] Query_values = Query_genescores[1].split(";");
                                                values = Arrays.asList(Query_values);
                                            }
                                            for (String u : values) {
                                                if (u.contains("+")) {
                                                    String[] p1 = u.split("\\+");
                                                    for (String o1 : p1) {
                                                        if (Integer.parseInt(o1.trim()) == count) {
                                                            if (Double.parseDouble(Query_genescores[0].trim()) <= Double.parseDouble(Target_genescores[0].trim())) {
                                                                scoring = Double.parseDouble(Target_genescores[0].trim());
                                                                int dif = Math.abs(Integer.parseInt(o1.trim()) - count);
                                                                if (diff > dif) {
                                                                    diff = dif;
                                                                    tgene = Tgene;

                                                                }
                                                            }

                                                        }
                                                    }
                                                } else {
                                                    if (Integer.parseInt(u) == count) {
                                                        if (Double.parseDouble(Query_genescores[0].trim()) <= Double.parseDouble(Target_genescores[0].trim())) {
                                                            scoring = Double.parseDouble(Target_genescores[0].trim());
                                                            int dif = Math.abs(Integer.parseInt(u) - count);
                                                            if (diff > dif) {
                                                                diff = dif;
                                                                tgene = Tgene;

                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            tgene = "-1";
                                            scoring = 0;
                                        }
                                    }
                                } else {
                                    //////System.out.println(Tgene);
                                    String[] Query_genescores = getmax1(reverse.get(Integer.parseInt(Tgene)), forward).split("=");
                                    ////System.out.println("----sdfds    "+Query_genescores.length);
                                    if (Query_genescores.length > 1) {
                                        //System.out.println(getmax1(reverse.get(Integer.parseInt(Tgene)),forward)+"           "+ Tgene);
                                        if (Query_genescores[1].contains(";")) {
                                            String[] Query_values = Query_genescores[1].split(";");
                                            values = Arrays.asList(Query_values);
                                        }
                                        for (String u : values) {
                                            if (Integer.parseInt(u) == count) {
                                                if (Double.parseDouble(Query_genescores[0].trim()) <= Double.parseDouble(Target_genescores[0].trim())) {
                                                    scoring = Double.parseDouble(Target_genescores[0].trim());
                                                    int dif = Math.abs(Integer.parseInt(u) - count);
                                                    if (diff > dif) {
                                                        diff = dif;
                                                        tgene = Tgene;
                                                    }
                                                } else {
                                                    tgene = "-1";
                                                    scoring = 0;
                                                }
                                            }
                                        }
                                    } else {
                                        tgene = "-1";
                                        scoring = 0;
                                    }
                                }
                            }
                        } else {

                            String Tgene = Target_genescores[1].toString().trim();
                            if (Tgene.contains("+")) {
                                String[] Tgenes = Tgene.split("\\+");
                                for (String gene : Tgenes) {
                                    String[] Query_genescores = getmax1(reverse.get(Integer.parseInt(gene)), forward).split("=");
                                    sce += Double.parseDouble(Query_genescores[0].trim());
                                }

                                sce = (sce / 2);
                                //////System.out.println(sce+"   dfs               ");

                                if (sce <= Double.parseDouble(Target_genescores[0].trim())) {
                                    scoring = Double.parseDouble(Target_genescores[0].trim());
                                    tgene = Tgene;
                                    for (String gene : Tgenes) {
                                        try {
                                            reverse.asMap().remove(Integer.parseInt(gene.trim()));
                                        } catch (Exception dsc) {
                                        }
                                    }
                                    forward.asMap().remove(count);
                                } else {
                                    for (String gene : Tgenes) {
                                        String[] Query_genescores = getmax1(reverse.get(Integer.parseInt(gene)), forward).split("=");
                                        if (Query_genescores.length > 1) {
                                            //	////System.out.println(getmax(reverse.get(Integer.parseInt(Tgene))));
                                            if (Query_genescores[1].contains(";")) {
                                                String[] Query_values = Query_genescores[1].split(";");
                                                for (String u : Query_values) {

                                                    if (Double.parseDouble(Query_genescores[0].trim()) <= Double.parseDouble(Target_genescores[0].trim())) {
                                                        scoring = Double.parseDouble(Target_genescores[0].trim());
                                                        int dif = Math.abs(Integer.parseInt(u) - count);
                                                        if (diff > dif) {
                                                            diff = dif;
                                                            tgene = gene;
                                                        }
                                                    } else {
                                                        tgene = "-1";
                                                    }
                                                }
                                            } else {
                                                if (Query_genescores[1].trim().contains("+")) {
                                                    String[] gens = Query_genescores[1].trim().split("\\+");
                                                    for (String gen : gens) {
                                                        if (Double.parseDouble(Query_genescores[0].trim()) <= Double.parseDouble(Target_genescores[0].trim())) {
                                                            scoring = Double.parseDouble(Target_genescores[0].trim());
                                                            int dif = Math.abs(Integer.parseInt(gen.trim()) - count);
                                                            if (diff > dif) {
                                                                diff = dif;
                                                                tgene = Tgene;
                                                            }
                                                        } else {
                                                            tgene = "-1";
                                                        }
                                                    }
                                                } else {
                                                    String u = Query_genescores[1].trim();
                                                    if (Double.parseDouble(Query_genescores[0].trim()) <= Double.parseDouble(Target_genescores[0].trim())) {
                                                        scoring = Double.parseDouble(Target_genescores[0].trim());
                                                        int dif = Math.abs(Integer.parseInt(u) - count);
                                                        if (diff > dif) {
                                                            diff = dif;
                                                            tgene = Tgene;
                                                        }
                                                    } else if (Double.parseDouble(Query_genescores[0].trim()) >= Double.parseDouble(Target_genescores[0].trim())) {
                                                        scoring = Double.parseDouble(Query_genescores[0].trim());
                                                        int dif = Math.abs(Integer.parseInt(u) - count);
                                                        if (diff > dif) {
                                                            diff = dif;
                                                            tgene = Tgene;
                                                        }
                                                    } else {
                                                        tgene = "-1";
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                //////System.out.println(Tgene);
                                String[] Query_genescores = getmax1(reverse.get(Integer.parseInt(Tgene)), forward).split("=");
                                //System.out.println(getmax1(reverse.get(Integer.parseInt(Tgene)),forward)+"            "+Tgene);
                                if (Query_genescores.length > 1) {
                                    System.out.println(getmax1(reverse.get(Integer.parseInt(Tgene)), forward) + "     " + count);
                                    if (Query_genescores[1].contains(";")) {
                                        String[] Query_values = Query_genescores[1].split(";");
                                        for (String u : Query_values) {
                                            if (u.contains("+")) {
                                                String[] p1 = u.split("\\+");
                                                for (String o1 : p1) {

                                                    if (Double.parseDouble(Query_genescores[0].trim()) <= Double.parseDouble(Target_genescores[0].trim())) {
                                                        scoring = Double.parseDouble(Target_genescores[0].trim());
                                                        int dif = Math.abs(Integer.parseInt(o1.trim()) - count);
                                                        if (diff > dif) {
                                                            diff = dif;
                                                            tgene = Tgene;
                                                        }
                                                    } else {
                                                        tgene = "-1";
                                                    }
                                                }
                                            } else {

                                                if (Double.parseDouble(Query_genescores[0].trim()) <= Double.parseDouble(Target_genescores[0].trim())) {
                                                    scoring = Double.parseDouble(Target_genescores[0].trim());
                                                    int dif = Math.abs(Integer.parseInt(u) - count);
                                                    if (diff > dif) {
                                                        diff = dif;
                                                        tgene = Tgene;
                                                    }
                                                } else {
                                                    tgene = "-1";
                                                }
                                            }
                                        }

                                    } else {
                                        //String u = Query_genescores[1].trim();
                                        String[] Tgenes = Tgene.split("\\+");


                                        for (String gene : Tgenes) {
                                            String[] Query_genescores1 = getmax1(reverse.get(Integer.parseInt(gene)), forward).split("=");
                                            ////System.out.println(count+"          "+Tgene+"           "+getmax1(reverse.get(Integer.parseInt(gene)),forward));
                                            sce += Double.parseDouble(Query_genescores1[0].trim());
                                            //////System.out.println(sce+"   dfs               "+Query_genescores[0].trim());
                                        }

                                        sce = (sce / 2);
                                        //	////System.out.println(sce+"         "+Target_genescores[0].trim());
                                        if (Query_genescores[1].trim().contains("+")) {
                                            String[] gens = Query_genescores[1].trim().split("\\+");
                                            for (String gen : gens) {


                                                if (sce <= Double.parseDouble(Target_genescores[0].trim())) {
                                                    scoring = Double.parseDouble(Target_genescores[0].trim());
                                                    int dif = Math.abs(Integer.parseInt(gen.trim()) - count);
                                                    if (diff > dif) {
                                                        diff = dif;
                                                        tgene = Tgene;

                                                    }

                                                } else {
                                                    tgene = "-1";
                                                }
                                            }
                                            if (Double.parseDouble(Query_genescores[0].trim()) >= Double.parseDouble(Target_genescores[0].trim())) {
                                                scoring = Double.parseDouble(Query_genescores[0].trim());

                                                tgene = "";
                                                try {
                                                    reverse.asMap().remove(Integer.parseInt(Tgene.trim()));
                                                } catch (Exception dsc) {
                                                }
                                                for (String gen : gens) {
                                                    forward.asMap().remove(Integer.parseInt(gen));
                                                }
                                                finalresult.put(scoring, Query_genescores[1] + "=" + Tgene);

                                            } else {
                                                tgene = "-1";
                                            }

                                        } else {
                                            String u = Query_genescores[1].trim();
                                            System.out.println(Query_genescores[0].trim() + "   dsf   " + count + "     " + Double.parseDouble(Target_genescores[0].trim()));


                                            if (Double.parseDouble(Query_genescores[0].trim()) <= Double.parseDouble(Target_genescores[0].trim())) {
                                                scoring = Double.parseDouble(Target_genescores[0].trim());
                                                int dif = Math.abs(Integer.parseInt(u) - count);
                                                if (diff > dif) {
                                                    diff = dif;
                                                    tgene = Tgene;

                                                }

                                            }
                                            if (Double.parseDouble(Query_genescores[0].trim()) > Double.parseDouble(Target_genescores[0].trim())) {
                                                scoring = Double.parseDouble(Query_genescores[0].trim());
                                                //int dif = Math.abs(Integer.parseInt(u)-count);

                                                tgene = Integer.toString(count);



                                            } else {
                                                tgene = "-1";
                                                scoring = 0;
                                            }
                                        }
                                    }

                                }
                            }
                        }



                        //	////System.out.println(scoring+"     ;           "+ tgene);
                        if (!tgene.trim().equals("")) {
                            finalresult.put(scoring, count + "=" + tgene);
                            forward.asMap().remove(count);
                            try {
                                reverse.asMap().remove(Integer.parseInt(tgene.trim()));
                            } catch (Exception dsc) {
                            }
                        }
                    } else {

                        tgene = "-1";
                        scoring = 0;
                        finalresult.put(0.0, count + "=" + tgene);
                    }
                }
            }
        }

    }

    public void calculations(Multimap<Integer, Multimap<Double, String>> forward, Multimap<Integer, Multimap<Double, String>> forward1, Multimap<Integer, Multimap<Double, String>> reverse) {
        double scoring = 0;
        int diff = 100;
        String tgene = "";

        List<Integer> query = new ArrayList<Integer>();
        List<Integer> target = new ArrayList<Integer>();
        List<String> values = new ArrayList<String>();
        for (int i = 0; i < forward1.size(); i++) {
            int count = i + 1;
            double sce = 0;
            //	//System.out.println(count);
            diff = 100;
            String[] Target_genescores = (getmax1(forward.get(count), reverse)).toString().split("=");
            ////System.out.println(getmax1(forward.get(count),reverse)+"    "+Target_genescores.length);
            tgene = "";

            if (Target_genescores.length > 1) {
                if (Double.parseDouble(Target_genescores[0].toString().trim()) > 0) {
                    if (Target_genescores[1].contains(";")) {
                        String[] genes = Target_genescores[1].toString().split(";");
                        for (String Tgene : genes) {
                            if (Tgene.contains("+")) {
                                String[] p = Tgene.split("\\+");
                                for (String o : p) {
                                    String[] Query_genescores = getmax1(reverse.get(Integer.parseInt(o.trim())), forward).split("=");
                                    //////System.out.println("----sdfds    "+Query_genescores.length);
                                    if (Query_genescores.length > 1) {
                                        //	////System.out.println(getmax(reverse.get(Integer.parseInt(Tgene))));
                                        if (Query_genescores[1].contains(";")) {
                                            String[] Query_values = Query_genescores[1].split(";");
                                            values = Arrays.asList(Query_values);
                                        }
                                        for (String u : values) {
                                            if (u.contains("+")) {
                                                String[] p1 = u.split("\\+");
                                                for (String o1 : p1) {
                                                    if (Integer.parseInt(o1.trim()) == count) {
                                                        if (Double.parseDouble(Query_genescores[0].trim()) <= Double.parseDouble(Target_genescores[0].trim())) {
                                                            scoring = Double.parseDouble(Target_genescores[0].trim());
                                                            int dif = Math.abs(Integer.parseInt(o1.trim()) - count);
                                                            if (diff > dif) {
                                                                diff = dif;
                                                                tgene = Tgene;

                                                            }
                                                        }

                                                    }
                                                }
                                            } else {
                                                if (Integer.parseInt(u) == count) {
                                                    if (Double.parseDouble(Query_genescores[0].trim()) <= Double.parseDouble(Target_genescores[0].trim())) {
                                                        scoring = Double.parseDouble(Target_genescores[0].trim());
                                                        int dif = Math.abs(Integer.parseInt(u) - count);
                                                        if (diff > dif) {
                                                            diff = dif;
                                                            tgene = Tgene;

                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        tgene = "-1";
                                        scoring = 0;
                                    }
                                }
                            } else {
                                //////System.out.println(Tgene);
                                String[] Query_genescores = getmax1(reverse.get(Integer.parseInt(Tgene)), forward).split("=");
                                ////System.out.println("----sdfds    "+Query_genescores.length);
                                if (Query_genescores.length > 1) {
                                    ////System.out.println(getmax1(reverse.get(Integer.parseInt(Tgene)),forward)+"           "+ Tgene);
                                    if (Query_genescores[1].contains(";")) {
                                        String[] Query_values = Query_genescores[1].split(";");
                                        values = Arrays.asList(Query_values);
                                    }
                                    for (String u : values) {
                                        if (Integer.parseInt(u) == count) {
                                            if (Double.parseDouble(Query_genescores[0].trim()) <= Double.parseDouble(Target_genescores[0].trim())) {
                                                scoring = Double.parseDouble(Target_genescores[0].trim());
                                                int dif = Math.abs(Integer.parseInt(u) - count);
                                                if (diff > dif) {
                                                    diff = dif;
                                                    tgene = Tgene;
                                                }
                                            } else {
                                                tgene = "-1";
                                                scoring = 0;
                                            }
                                        }
                                    }
                                } else {
                                    tgene = "-1";
                                    scoring = 0;
                                }
                            }
                        }
                    } else {

                        String Tgene = Target_genescores[1].toString().trim();
                        if (Tgene.contains("+")) {
                            String[] Tgenes = Tgene.split("\\+");
                            for (String gene : Tgenes) {
                                String[] Query_genescores = getmax1(reverse.get(Integer.parseInt(gene)), forward).split("=");
                                sce += Double.parseDouble(Query_genescores[0].trim());
                            }

                            sce = (sce / 2);
                            //////System.out.println(sce+"   dfs               ");

                            if (sce <= Double.parseDouble(Target_genescores[0].trim())) {
                                scoring = Double.parseDouble(Target_genescores[0].trim());
                                tgene = Tgene;
                                for (String gene : Tgenes) {
                                    try {
                                        reverse.asMap().remove(Integer.parseInt(gene.trim()));
                                    } catch (Exception dsc) {
                                    }
                                }
                                forward.asMap().remove(count);
                            } else {
                                for (String gene : Tgenes) {
                                    String[] Query_genescores = getmax1(reverse.get(Integer.parseInt(gene)), forward).split("=");
                                    if (Query_genescores.length > 1) {
                                        //	////System.out.println(getmax(reverse.get(Integer.parseInt(Tgene))));
                                        if (Query_genescores[1].contains(";")) {
                                            String[] Query_values = Query_genescores[1].split(";");
                                            for (String u : Query_values) {

                                                if (Double.parseDouble(Query_genescores[0].trim()) <= Double.parseDouble(Target_genescores[0].trim())) {
                                                    scoring = Double.parseDouble(Target_genescores[0].trim());
                                                    int dif = Math.abs(Integer.parseInt(u) - count);
                                                    if (diff > dif) {
                                                        diff = dif;
                                                        tgene = gene;
                                                    }
                                                } else {
                                                    tgene = "-1";
                                                }
                                            }
                                        } else {
                                            if (Query_genescores[1].trim().contains("+")) {
                                                String[] gens = Query_genescores[1].trim().split("\\+");
                                                for (String gen : gens) {
                                                    if (Double.parseDouble(Query_genescores[0].trim()) <= Double.parseDouble(Target_genescores[0].trim())) {
                                                        scoring = Double.parseDouble(Target_genescores[0].trim());
                                                        int dif = Math.abs(Integer.parseInt(gen.trim()) - count);
                                                        if (diff > dif) {
                                                            diff = dif;
                                                            tgene = Tgene;
                                                        }
                                                    } else {
                                                        tgene = "-1";
                                                    }
                                                }
                                            } else {
                                                String u = Query_genescores[1].trim();
                                                if (Double.parseDouble(Query_genescores[0].trim()) <= Double.parseDouble(Target_genescores[0].trim())) {
                                                    scoring = Double.parseDouble(Target_genescores[0].trim());
                                                    int dif = Math.abs(Integer.parseInt(u) - count);

                                                    //System.out.println(u+"          dsad");
                                                    if (diff >= dif) {

                                                        if (diff == dif) {

                                                            tgene = u;
                                                        } else {
                                                            diff = dif;
                                                            tgene = Tgene;
                                                        }

                                                    }
                                                } else if (Double.parseDouble(Query_genescores[0].trim()) >= Double.parseDouble(Target_genescores[0].trim())) {
                                                    //System.out.println(u+"          dsad");
                                                    scoring = Double.parseDouble(Query_genescores[0].trim());
                                                    int dif = Math.abs(Integer.parseInt(u) - count);
                                                    if (diff > dif) {
                                                        diff = dif;
                                                        tgene = Tgene;
                                                    }
                                                } else {
                                                    tgene = "-1";
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            //////System.out.println(Tgene);
                            String[] Query_genescores = getmax1(reverse.get(Integer.parseInt(Tgene)), forward).split("=");
                            //	////System.out.println(getmax(reverse.get(Integer.parseInt(Tgene))));
                            if (Query_genescores.length > 1) {
                                //System.out.println(getmax1(reverse.get(Integer.parseInt(Tgene)),forward)+"   dsadasdasd");
                                if (Query_genescores[1].contains(";")) {
                                    String[] Query_values = Query_genescores[1].split(";");
                                    for (String u : Query_values) {
                                        if (u.contains("+")) {
                                            String[] p1 = u.split("\\+");
                                            for (String o1 : p1) {

                                                if (Double.parseDouble(Query_genescores[0].trim()) <= Double.parseDouble(Target_genescores[0].trim())) {
                                                    scoring = Double.parseDouble(Target_genescores[0].trim());
                                                    int dif = Math.abs(Integer.parseInt(o1.trim()) - count);
                                                    if (diff > dif) {
                                                        diff = dif;
                                                        tgene = Tgene;
                                                    }
                                                } else {
                                                    tgene = "-1";
                                                }
                                            }
                                        } else {

                                            if (Double.parseDouble(Query_genescores[0].trim()) <= Double.parseDouble(Target_genescores[0].trim())) {
                                                scoring = Double.parseDouble(Target_genescores[0].trim());
                                                int dif = Math.abs(Integer.parseInt(u) - Integer.parseInt(Tgene));
                                                //System.out.println(u);
                                                if (diff >= dif) {


                                                    //System.out.println(diff+"               "+dif+ "               "+u+"           "+Tgene);
                                                    if (diff == dif) {
                                                        tgene = "";

                                                        reverse.asMap().remove(Integer.parseInt(Tgene.trim()));


                                                        forward.asMap().remove(Integer.parseInt(u.trim()));

                                                        finalresult.put(scoring, u.trim() + "=" + Tgene);

                                                    } else {
                                                        diff = dif;
                                                        tgene = Tgene;
                                                    }
                                                }
                                            } else {
                                                tgene = "";
                                            }
                                        }
                                    }

                                } else {
                                    //String u = Query_genescores[1].trim();
                                    String[] Tgenes = Tgene.split("\\+");


                                    for (String gene : Tgenes) {
                                        String[] Query_genescores1 = getmax1(reverse.get(Integer.parseInt(gene)), forward).split("=");
                                        ////System.out.println(count+"          "+Tgene+"           "+getmax1(reverse.get(Integer.parseInt(gene)),forward));
                                        sce += Double.parseDouble(Query_genescores1[0].trim());
                                        //////System.out.println(sce+"   dfs               "+Query_genescores[0].trim());
                                    }

                                    sce = (sce / 2);
                                    //	////System.out.println(sce+"         "+Target_genescores[0].trim());
                                    if (Query_genescores[1].trim().contains("+")) {
                                        String[] gens = Query_genescores[1].trim().split("\\+");
                                        for (String gen : gens) {


                                            if (sce <= Double.parseDouble(Target_genescores[0].trim())) {
                                                scoring = Double.parseDouble(Target_genescores[0].trim());
                                                int dif = Math.abs(Integer.parseInt(gen.trim()) - count);
                                                if (diff > dif) {
                                                    diff = dif;
                                                    tgene = Tgene;

                                                }

                                            } else {
                                                tgene = "-1";
                                            }
                                        }
                                        if (Double.parseDouble(Query_genescores[0].trim()) >= Double.parseDouble(Target_genescores[0].trim())) {
                                            scoring = Double.parseDouble(Query_genescores[0].trim());

                                            tgene = "";
                                            try {
                                                reverse.asMap().remove(Integer.parseInt(Tgene.trim()));
                                            } catch (Exception dsc) {
                                            }
                                            for (String gen : gens) {
                                                forward.asMap().remove(Integer.parseInt(gen));
                                            }
                                            finalresult.put(scoring, Query_genescores[1] + "=" + Tgene);

                                        } else {
                                            tgene = "-1";
                                        }

                                    } else {
                                        String u = Query_genescores[1].trim();


                                        if (Double.parseDouble(Query_genescores[0].trim()) <= Double.parseDouble(Target_genescores[0].trim())) {
                                            scoring = Double.parseDouble(Target_genescores[0].trim());
                                            int dif = Math.abs(Integer.parseInt(u) - count);
                                            if (diff > dif) {
                                                diff = dif;
                                                tgene = Tgene;

                                            }

                                        } else {
                                            tgene = "-1";
                                            scoring = 0;
                                        }
                                    }
                                }

                            }
                        }
                    }



                    //	////System.out.println(scoring+"     ;           "+ tgene);
                    if (!tgene.trim().equals("")) {
                        finalresult.put(scoring, count + "=" + tgene);
                        forward.asMap().remove(count);
                        try {
                            reverse.asMap().remove(Integer.parseInt(tgene.trim()));
                        } catch (Exception dsc) {
                        }
                    }
                } else {

                    tgene = "-1";
                    scoring = 0;
                    finalresult.put(0.0, count + "=" + tgene);
                }
            }

        }
    }

    /**
     * @return the finalscore
     */
    public double getFinalscore() {
        return finalscore;
    }

    /**
     * @param finalscore the finalscore to set
     */
    public void setFinalscore(double finalscore) {
        this.finalscore = finalscore;
    }

    /**
     * @return the finalresult
     */
    public Multimap<Double, String> getFinalResult() {
        return finalresult;
    }

    /**
     * @param finalresult the finalresult to set
     */
    public void setFinalresult(Multimap<Double, String> finalresult) {
        this.finalresult = finalresult;
    }

    /**
     * @return the firstPathwayAlignment
     */
    public String getFirstPathwayAlignment() {
        return firstPathwayAlignment;
    }

    /**
     * @param firstPathwayAlignment the firstPathwayAlignment to set
     */
    public void setFirstPathwayAlignment(String firstPathwayAlignment) {
        this.firstPathwayAlignment = firstPathwayAlignment;
    }

    /**
     * @return the secondPathwayAlignment
     */
    public String getSecondPathwayAlignment() {
        return secondPathwayAlignment;
    }

    /**
     * @param secondPathwayAlignment the secondPathwayAlignment to set
     */
    public void setSecondPathwayAlignment(String secondPathwayAlignment) {
        this.secondPathwayAlignment = secondPathwayAlignment;
    }
}
