package de.hzi.helmholtz.Compare;

import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

class VertexCoverTest {

    static double finalscore = 0;
    static double s_add = 1.0, s_penality = -1.0;
    static Multimap<Double, String> finalresult = ArrayListMultimap.create();
    static Multimap<Double, String> finalresultf = ArrayListMultimap.create();
    static Multimap<Double, String> finalresultr = ArrayListMultimap.create();
    static double maximumscore = 0;
    static Map<Integer, List<String>> Qmap = new HashMap<Integer, List<String>>();
    static Map<Integer, List<String>> Tmap = new HashMap<Integer, List<String>>();
    static boolean yes = true;
    static int qmapsize = 0;

    static Multimap<String, Multimap<Double, String>> finalresults = ArrayListMultimap.create();

    static Multimap<String, String> Best_final = ArrayListMultimap.create();

    static int tmapsize = 0;

    public static void makeup(Multimap<Integer, Multimap<Double, String>> forward, Multimap<Integer, Multimap<Double, String>> reverse) {
		//	Map<Integer,List<String>> Qmap = new HashMap<Integer, List<String>>();
        //	Map<Integer,List<String>> Tmap = new HashMap<Integer, List<String>>();
        //	//System.out.println(Qmap);
        ////System.out.println(Tmap);
        Multimap<Integer, Multimap<Double, String>> forward1 = ArrayListMultimap.create(forward);
        int diff = 100;
        String tgene = "";
        Multimap<Integer, Multimap<Double, String>> reverse1 = ArrayListMultimap.create(reverse);
        for (Entry<Integer, Multimap<Double, String>> i : forward1.entries()) {

            diff = 100;
            int count = i.getKey();

            String[] Target_genescores = (getmax1(forward.get(count), reverse)).toString().split("=");

            //	//System.out.println(" dsa                                  "+ (getmax1(forward.get(count),reverse)).toString());
            if (Target_genescores.length > 1) {

                if (Double.parseDouble(Target_genescores[0].trim()) == maximumscore) {

                    if (Target_genescores[1].contains(";")) {

                        String[] genes = Target_genescores[1].toString().split(";");
                        for (String Tgene : genes) {
                            if (genes[0].contains("+")) {
                                String[] p1 = genes[0].split("\\+");

                                if ((Tmap.containsKey(Integer.parseInt(p1[0].trim()))) && (Tmap.containsKey(Integer.parseInt(p1[1].trim())))) {
                                    for (String o1 : p1) {
                                        Tmap.remove(Integer.parseInt(o1.trim()));
                                    }
                                    tgene = genes[0];
                                }
                                ////System.out.println(count+"            "+tgene);
                            } else {

                                if (Tmap.containsKey(Integer.parseInt(Tgene))) {

                                    int dif = Math.abs(Integer.parseInt(Tgene.trim()) - count);
                                    //	//System.out.println(dif+"    dfs         "+Tgene+"                    "+count);
                                    if (diff > dif) {
                                        diff = dif;
                                        tgene = Tgene;
                                        Tmap.remove(Integer.parseInt(tgene));

                                    }

                                }

                            }

                        }

                        if (Best_final.containsKey(Integer.toString(count))) {
                            String[] scoreGene = Best_final.get(Integer.toString(count)).toString().split("=");
                            ////System.out.println(" ds       "+Best_final.get(Integer.toString(count)).toString());
                            if (Double.parseDouble(Target_genescores[0].trim()) > Double.parseDouble(scoreGene[0].trim())) {
                                Best_final.asMap().remove(Integer.toString(count));
                                Best_final.put(Integer.toString(count), (Target_genescores[0].trim() + "=" + tgene));
                            }

                        } else {

                            Best_final.put(Integer.toString(count), Target_genescores[0].trim() + "=" + tgene);
                            //	//System.out.println(count+"   ds1       "+Best_final);
                        }
                        //	Best_final.put(count, Target_genescores[0].trim()+"="+tgene);
                        finalresultf.put(Double.parseDouble(Target_genescores[0].trim()), count + "=" + tgene);

                    } else {
                        if (Target_genescores[1].trim().contains("+")) {
                            String[] p1 = Target_genescores[1].trim().split("\\+");
                            for (String o1 : p1) {
                                Tmap.remove(Integer.parseInt(o1.trim()));
                            }
                        } else {
                            Tmap.remove(Integer.parseInt(Target_genescores[1].trim()));
                        }

                        if (Best_final.containsKey(Integer.toString(count))) {
                            String[] scoreGene = Best_final.get(Integer.toString(count)).toString().split("=");

                            if (Double.parseDouble(Target_genescores[0].trim()) > Double.parseDouble(scoreGene[0].trim())) {
                                Best_final.asMap().remove(Integer.toString(count));
                                Best_final.put(Integer.toString(count), (Target_genescores[0].trim() + "=" + Target_genescores[1].trim()));
                            }

                        } else {
                            Best_final.put(Integer.toString(count), Target_genescores[0].trim() + "=" + Target_genescores[1].trim());

                        }
                        finalresultf.put(Double.parseDouble(Target_genescores[0].trim()), count + "=" + Target_genescores[1].trim());

                    }
                    Qmap.remove((count));
                }

            }
        }
		////System.out.println(" ds       "+Tmap);
        ////System.out.println( (getmax1(reverse.get(8),forward)).toString());

        try {
            for (int i = 0; i < reverse.size(); i++) {
                ////System.out.println(i+"                      "+reverse1.size());
                diff = 100;
                int count = i + 1;
				//	forward.asMap().remove(Integer.parseInt(Query_genescores[1].trim()));
                //finalresult.put(scoring, Query_genescores[1].trim()+"="+tgene);

                String[] Target_genescores = (getmax1(reverse.get(count), forward)).toString().split("=");
                if (count == 7) {
                    ////System.out.println("dsaas                              "+Target_genescores.length);
                }
                if (Target_genescores.length > 1) {
                    if ((getmax1(reverse.get(count), forward)).toString().trim().length() > 1) {
                        if (Double.parseDouble(Target_genescores[0].trim()) == maximumscore) {
                            if (Target_genescores[1].contains(";")) {
                                String[] genes = Target_genescores[1].toString().split(";");

                                if (genes[0].contains("+")) {
                                    String[] p1 = genes[0].split("\\+");

                                    if ((Qmap.containsKey(Integer.parseInt(p1[0].trim()))) && (Qmap.containsKey(Integer.parseInt(p1[1].trim())))) {
                                        for (String o1 : p1) {
                                            Qmap.remove(Integer.parseInt(o1.trim()));
                                        }
                                        tgene = genes[0];
                                    }
                                    //System.out.println(count+"            "+tgene);
                                } else {
                                    tgene = genes[0];
                                    Qmap.remove(Integer.parseInt(tgene));
                                }
                                //System.out.println(count+"            "+tgene);
                                if (Best_final.containsKey(tgene)) {
                                    String[] scoreGene = Best_final.get((tgene)).toString().split("=");

                                    if (Double.parseDouble(Target_genescores[0].trim()) > Double.parseDouble(scoreGene[0].trim())) {
                                        //System.out.println(Target_genescores[1].trim()+" ds2       "+Best_final.get(Integer.toString(count)).toString());
                                        Best_final.asMap().remove(Target_genescores[1].trim());
                                        Best_final.put((tgene), (Target_genescores[0].trim() + "=" + count));
                                    }

                                } else {
                                    //System.out.println(Target_genescores[1].trim()+" ds3       "+Best_final.get(Integer.toString(count)).toString());
                                    Best_final.put(tgene, Target_genescores[0].trim() + "=" + count);

                                }

                                finalresultr.put(Double.parseDouble(Target_genescores[0].trim()), tgene + "=" + count);

                                ////System.out.println("finalr                               "+finalresultr);
                            } else {

                                if (Target_genescores[1].trim().contains("+")) {
                                    String[] p1 = Target_genescores[1].trim().split("\\+");
                                    for (String o1 : p1) {
                                        Qmap.remove(Integer.parseInt(o1.trim()));
                                    }
                                } else {
                                    Qmap.remove(Integer.parseInt(Target_genescores[1].trim()));
                                }

                                if (Best_final.containsKey(Target_genescores[1].trim())) {
                                    String[] scoreGene = Best_final.get((Target_genescores[1].trim())).toString().split("=");

                                    if (Double.parseDouble(Target_genescores[0].trim()) > Double.parseDouble(scoreGene[0].trim())) {
                                        Best_final.asMap().remove(Target_genescores[1].trim());
                                        Best_final.put((Target_genescores[1].trim()), (Target_genescores[0].trim() + "=" + count));
                                    }

                                } else {
                                    Best_final.put(Target_genescores[1].trim(), Target_genescores[0].trim() + "=" + count);

                                }
                                finalresultr.put(Double.parseDouble(Target_genescores[0].trim()), Target_genescores[1].trim() + "=" + count);
                            }

                            Tmap.remove((count));

                            //finalresult.put(Double.parseDouble(Target_genescores[0].trim()), Target_genescores[1].trim()+"="+ count);
                            Tmap.remove((count));
							//	//System.out.println("finalr                               "+finalresultr);

                        }
                    }
                }
            }
        } catch (Exception ds) {
        }
		//	//System.out.println(maximumscore+"               finalr                               "+finalresultr);	
        //	//System.out.println("Best_final                               "+Best_final);
        //	//System.out.println("finalr                               "+finalresultf);
        //
    }

    public static void main(String[] args) {

        double value = 0.0000000000000000000000001;
        ////System.out.println(Math.abs(Math.log(0.99)));

        String[] a = {"KS", "AT", "DH", "KR", "ACP"};
        String[] b = {"KS", "AT", "AT", "DH", "KR", "ACP"};

        String a1 = "abcd";
        String b1 = "a3242342";

        final JFrame frame = new JFrame("Matching");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(1000, 200);

        //frame.setVisible(true);
        frame.setLocationRelativeTo(null);

        JLabel j2 = new JLabel();

        List<String> Qg1 = Arrays.asList("ACP");
        List<String> Qg2 = Arrays.asList("KS", "AT", "KC", "ACP");

		////////System.out.println(LevenshteinDistance.computeLevenshteinDistance(Qg1,Qg2));
        //////////System.out.println(n.execute(a1, b1));

        /*	List<Map<Integer,List<String>>> Qdata = new ArrayList<Map<Integer,List<String>>>();//QUERY
         List<Map<Integer,List<String>>> Tdata = new ArrayList<Map<Integer,List<String>>>();//TARGET





         List<String> Qg1 = Arrays.asList("C", "A", "PC", "R");
         List<String> Qg2 = Arrays.asList("DQ", "EQ", "KC", "ACP");
         List<String> Qg3 = Arrays.asList("KQ", "AT");
         List<String> Qg4 = Arrays.asList("KQ", "AT", "DQ", "KC","ACP","KQ", "AT", "KC","AC","KQ", "AT", "DQ", "KC","AC");
         List<String> Qg5 = Arrays.asList("KQ", "AT", "DQ", "KC","ACP");
         List<String> Qg6 = Arrays.asList("KQ", "AT", "DQ", "KC","ACP");
         List<String> Qg7 = Arrays.asList("ACP","KQ", "AT","AT", "DQ", "KC","ACP");

         Map<Integer,List<String>> Qmap = new HashMap<Integer, List<String>>();
         Qmap.put(1, Qg1);
         Qmap.put(2, Qg2);
         Qmap.put(3, Qg3);
         Qmap.put(4, Qg4);
         Qmap.put(5, Qg5);
         Qmap.put(6, Qg6);
         Qmap.put(7, Qg7);


         List<String> Tg1 = Arrays.asList("C", "A", "PC", "?");
         List<String> Tg2 = Arrays.asList("KQ", "AT","DQ", "EQ", "KC", "AC");
         List<String> Tg3 = Arrays.asList("AT", "DQ", "KC","AC");
         List<String> Tg4 = Arrays.asList("KQ", "AT", "DQ", "KC","AC","KQ", "AT", "KC","AC");
         List<String> Tg5 = Arrays.asList("KQ", "AT", "DQ", "KC","AC");
         List<String> Tg6 = Arrays.asList("KQ", "AT", "DQ", "KC","AC");
         List<String> Tg7 = Arrays.asList("AT","AT", "DQ", "KC","AC");
         List<String> Tg8 = Arrays.asList("AC");
         List<String> Tg9 = Arrays.asList("ACL","AC","C","A","PC");

         Map<Integer,List<String>> Tmap = new HashMap<Integer, List<String>>();
         Tmap.put(1, Tg1);
         Tmap.put(2, Tg2);
         Tmap.put(3, Tg3);
         Tmap.put(4, Tg4);
         Tmap.put(5, Tg5);
         Tmap.put(6, Tg6);
         Tmap.put(7, Tg7);
         Tmap.put(8, Tg8);
         Tmap.put(9, Tg9);*/
        List<Map<Integer, List<String>>> Tdata = new ArrayList<Map<Integer, List<String>>>();
        List<Map<Integer, List<String>>> Qdata = new ArrayList<Map<Integer, List<String>>>();

        /*		List<String> Tgene6 = Arrays.asList("ACP","KS", "AT","AT", "DH", "KR","ACP");
         List<String> Tgene5 = Arrays.asList("KS", "AT", "DH", "KR","ACP");
         List<String> Tgene4 = Arrays.asList("KS", "AT", "DH", "KR","ACP");
         List<String> Tgene3 = Arrays.asList("KS", "AT", "DH", "KR","ACP","KS", "AT", "KR","ACP","KS", "AT", "DH", "KR","ACP");
         List<String> Tgene2 = Arrays.asList("KS", "AT");
         List<String> Tgene1 = Arrays.asList("C", "A", "PCP", "?");

         Tmap.put(1, Tgene6);
         Tmap.put(2, Tgene5);
         Tmap.put(3, Tgene4);
         Tmap.put(4, Tgene3);
         Tmap.put(5, Tgene2);
         Tmap.put(6, Tgene1);*/
        /*		List<String> Tgene9 = Arrays.asList("ACL","ACP","C","A","PCP");
         List<String> Tgene8 = Arrays.asList("ACP");
         List<String> Tgene7 = Arrays.asList("AT","AT", "DH", "KR","ACP");
         List<String> Tgene6 = Arrays.asList("KS", "AT", "DH", "KR","ACP");
         List<String> Tgene5 = Arrays.asList("KS", "AT", "DH", "KR","ACP");
         List<String> Tgene4 = Arrays.asList("KS", "AT", "DH", "KR","ACP","KS", "AT", "KR","ACP");
         List<String> Tgene3 = Arrays.asList("AT", "DH", "KR","ACP");
         List<String> Tgene2 = Arrays.asList("KS", "AT","DH", "ER", "KR", "ACP");
         List<String> Tgene1 = Arrays.asList("C", "A", "PCP", "?");


         Tmap.put(1, Tgene9);
         Tmap.put(2, Tgene8);
         Tmap.put(3, Tgene7);
         Tmap.put(4, Tgene6);
         Tmap.put(5, Tgene5);
         Tmap.put(6, Tgene4);
         Tmap.put(7, Tgene3);
         Tmap.put(8, Tgene2);
         Tmap.put(9, Tgene1);*/
        /*List<String> Qgene1 = Arrays.asList("C", "A", "PCP", "Red");
         List<String> Qgene2 = Arrays.asList("DH", "ER", "KR", "ACP");
         List<String> Qgene3 = Arrays.asList("KS", "AT");
         List<String> Qgene4 = Arrays.asList("KS", "AT", "DH", "KR","ACP","KS", "AT", "KR","ACP","KS", "AT", "DH", "KR","ACP");
         List<String> Qgene5 = Arrays.asList("KS", "AT", "DH", "KR","ACP");
         List<String> Qgene6 = Arrays.asList("KS", "AT", "DH", "KR","ACP");
         List<String> Qgene7 = Arrays.asList("ACP","KS", "AT","AT", "DH", "KR","ACP");
         Qmap.put(1, Qgene7);
         Qmap.put(2, Qgene6);
         Qmap.put(3, Qgene5);
         Qmap.put(4, Qgene4);
         Qmap.put(5, Qgene3);
         Qmap.put(6, Qgene2);
         Qmap.put(7, Qgene1);*/

        /*
         List<String> Tgene6 = Arrays.asList("ACP-active-nil","KS-active-nil","AT-active-iso","AT-active-mmal", "DH-active-nil", "KR-active-nil","ACP-active-nil");
         List<String> Tgene5 = Arrays.asList("KS-active-nil", "AT-active-mmal", "DH-active-nil", "KR-active-nil","ACP-active-nil");
         List<String> Tgene4 = Arrays.asList("KS-active-nil", "AT-active-mmal", "DH-active-nil", "KR-active-nil","ACP-active-nil");

         List<String> Tgene3 = Arrays.asList("KS-active-nil", "AT-active-mal", "DH-active-nil", "KR-active-nil","ACP-active-nil","KS-active-nil", "AT-active-mal", "KR-active-nil","ACP-active-nil","KS-active-nil", "AT-active-mal", "DH-active-nil", "KR-active-nil","ACP-active-nil");
         List<String> Tgene2 = Arrays.asList("KS-active-nil", "AT-active-mmal");
         List<String> Tgene1 = Arrays.asList("C-active-nil", "A-active-ala", "PCP-active-nil", "Red-active-nil");



         Tmap.put(1, Tgene6);
         Tmap.put(2, Tgene5);
         Tmap.put(3, Tgene4);
         Tmap.put(4, Tgene3);
         Tmap.put(5, Tgene2);
         Tmap.put(6, Tgene1);
         */
        List<String> Tgene9 = Arrays.asList("ACL-active-nil", "ACP-active-nil", "C-active-nil", "A-active-gly", "PCP-active-nil");
        List<String> Tgene1 = Arrays.asList("ACP-active-nil");
        List<String> Tgene7 = Arrays.asList("AT-active-iso", "AT-active-mmal", "DH-active-nil", "KR-active-nil", "ACP-active-nil");
        List<String> Tgene6 = Arrays.asList("KS-active-nil", "AT-active-mmal", "DH-active-nil", "KR-active-nil", "ACP-active-nil");
        List<String> Tgene5 = Arrays.asList("KS-active-nil", "AT-active-mmal", "DH-active-nil", "KR-active-nil", "ACP-active-nil");
        List<String> Tgene4 = Arrays.asList("KS-active-nil", "AT-active-mmal/mal", "DH-active-nil", "KR-active-nil", "ACP-active-nil", "KS-active-nil", "AT-active-mmal/mal", "KR-active-nil", "ACP-active-nil");
        List<String> Tgene3 = Arrays.asList("AT-active-mal", "DH-active-nil", "KR-active-nil", "ACP-active-nil");
        List<String> Tgene2 = Arrays.asList("KS-active-nil", "AT-active-mmal", "DH-active-nil", "ER-active-nil", "KR-active-nil", "ACP-active-nil");
        List<String> Tgene8 = Arrays.asList("C-active-nil", "A-active-ala", "PCP-active-nil", "?-active-nil");

        Tmap.put(7, Tgene7);
        Tmap.put(6, Tgene6);
        Tmap.put(5, Tgene5);
        Tmap.put(4, Tgene4);
        Tmap.put(3, Tgene3);
        Tmap.put(2, Tgene2);
        Tmap.put(1, Tgene1);
        Tmap.put(8, Tgene8);
        Tmap.put(9, Tgene9);

        List<String> Qgene1 = Arrays.asList("C-active-nil", "A-active-ala", "PCP-active-nil", "Red-active-nil");
        List<String> Qgene4 = Arrays.asList("DH-active-nil", "ER-active-nil", "KR-active-nil", "ACP-active-nil");
        List<String> Qgene3 = Arrays.asList("KS-active-nil", "AT-active-mmal");
        List<String> Qgene2 = Arrays.asList("KS-active-nil", "AT-active-mal", "DH-active-nil", "KR-active-nil", "ACP-active-nil", "KS-active-nil", "AT-active-mal", "KR-active-nil", "ACP-active-nil", "KS-active-nil", "AT-active-mal", "DH-active-nil", "KR-active-nil", "ACP-active-nil");
        List<String> Qgene5 = Arrays.asList("KS-active-nil", "AT-active-mmal", "DH-active-nil", "KR-active-nil", "ACP-active-nil");
        List<String> Qgene6 = Arrays.asList("KS-active-nil", "AT-active-mmal", "DH-active-nil", "KR-active-nil", "ACP-active-nil");
        List<String> Qgene7 = Arrays.asList("ACP-active-nil", "KS-active-nil", "AT-active-iso", "AT-active-mmal", "DH-active-nil", "KR-active-nil", "ACP-active-nil");
        Qmap.put(7, Qgene7);
        Qmap.put(6, Qgene6);
        Qmap.put(5, Qgene5);
        Qmap.put(4, Qgene4);
        Qmap.put(3, Qgene3);
        Qmap.put(2, Qgene2);
        Qmap.put(1, Qgene1);

        int window = 2;
		//forward search

        /*Qmap.clear();
         Tmap.clear();*/
        xml2text xml2 = new xml2text();

        xml2text.main();

        /*Qmap = xml2text.Qmap;
         Tmap = xml2text.Tmap;*/
        Map<Integer, List<String>> Qmap1 = new HashMap<Integer, List<String>>(Qmap);
        Map<Integer, List<String>> Tmap1 = new HashMap<Integer, List<String>>(Tmap);

        qmapsize = Qmap.size();
        tmapsize = Tmap.size();
		////////System.out.println(Qmap);
        ////////System.out.println(Tmap);
		/*Multimap<Integer, Multimap<Double, String>> forward = comparing(Qmap,Tmap);
         Multimap<Integer, Multimap<Double, String>> forward1 = ArrayListMultimap.create(forward);
         Multimap<Integer, Multimap<Double,  String>> reverse = comparing(Tmap,Qmap);
         Multimap<Integer, Multimap<Double,  String>> reverse1 = ArrayListMultimap.create(reverse);*/
        //Multimap<Double,String> finalresult = ArrayListMultimap.create();
        List<Integer> query = new ArrayList<Integer>();
        List<Integer> target = new ArrayList<Integer>();
        List<String> values = new ArrayList<String>();
        double scoring = 0;
        int diff = 100;
        String tgene = "";

        Best_final.clear();
        finalresultf.clear();
        finalresultr.clear();

        while (yes) {

			////System.out.println(Qmap);
            ////System.out.println(Tmap);
            //	//System.out.println(yes);
            Multimap<Integer, Multimap<Double, String>> forward = comparing(Qmap, Tmap, Qmap1, Tmap1, tmapsize);

            Multimap<Integer, Multimap<Double, String>> reverse = comparing(Tmap, Qmap, Tmap1, Qmap1, qmapsize);

			////System.out.println("******************************************************************************************************************************************************************************************************************");
            ////System.out.println(forward+"   dsadas");
            if (forward.size() <= 0 || reverse.size() <= 0) {
                yes = false;
            } else {
                makeup(forward, reverse);
                maximumscore = 0;
                //	//System.out.println(maximumscore+"                 finalr                               "+finalresultf);
            }
        }

        if (Qmap.size() == 0 && Tmap.size() > 0) {

            for (int j : Tmap.keySet()) {
                Best_final.put("no", "0.0=" + j);
            }
        } else if (Qmap.size() > 0 && Tmap.size() == 0) {

            for (int j : Qmap.keySet()) {
                Best_final.put(j + "", "0.0=no");
                //finalresult.put(0.0, j+"=no");
            }
        }

        System.out.println(Best_final);
        JPanel p = new JPanel();
        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.insets = new Insets(2, 1, 2, 1);

        gbc1.weightx = 1.0;
        gbc1.weighty = 1.0;
        gbc1.gridy = 1;
        double scoring1 = 0;
        long startTime = System.currentTimeMillis();
        int countdash = 0, countalph = 0;
        int count = 0;
        JLabel io = new JLabel();

        io.setText("<html><font color=\"brown\">Query pathway: <br>Target pathway: </font></html>");
        JLabel io1 = new JLabel("      ");
        p.add(io, gbc1);
        p.add(io1, gbc1);
        for (String i : Best_final.keySet()) {
            count++;
            List<String> list = new ArrayList<String>();
            for (String j1 : Best_final.get(i)) {
                //System.out.println(j1);
                String[] j = j1.toString().split("=");
                if (i.contains("+")) {
                    List<String> tfunction = new ArrayList<String>();

                    List<String> qfunction = new ArrayList<String>();

                    for (String i1 : Tmap1.get(Integer.parseInt(j[1].trim().trim()))) {

                        String[] get = i1.split("-");
                        tfunction.add(get[0].trim());

                    }
                    //	////////System.out.println(i.getValue());
                    String[] k = i.split("\\+");
                    for (String h : k) {
                        for (String i1 : Qmap1.get(Integer.parseInt(h.trim()))) {

                            String[] get = i1.split("-");
                            qfunction.add(get[0].trim());

                        }

                    }
                    SmithWaterman_b sw = new SmithWaterman_b();
                    sw.init(qfunction, tfunction);
                    sw.process();
                    sw.backtrack();
                    countdash += sw.countdash;
                    countalph += sw.countlet;

                    sw.printScoreAndAlignments();

					//	score =  (double)Math.round(((score1+score2+score3)/3) * 100.0) / 100.0;
                    //SmithWaterman sw = new SmithWaterman(qfunction,tfunction);
                    //	////////System.out.println("Alignment Score: " + sw.computeSmithWaterman());
                    ////////System.out.println("");
                    io = new JLabel();
                    io.setText("<html><font color=\"brown\">" + sw.printScoreAndAlignments1() + "<br>" + sw.printScoreAndAlignments2() + "</font></html>");
                    io1 = new JLabel("      ");
                    p.add(io, gbc1);
                    p.add(io1, gbc1);
                    gbc1.gridy++;
                    scoring1 += sw.scoring();

                } else if (j[1].contains("+")) {
                    List<String> tfunction = new ArrayList<String>();

                    List<String> tfunction1 = new ArrayList<String>();
                    List<String> qfunction = new ArrayList<String>();
                    System.out.print(i.trim());
                    for (String i1 : Qmap1.get(Integer.parseInt(i.trim()))) {

                        String[] get = i1.split("-");
                        qfunction.add(get[0].trim());

                    }

                    //	////////System.out.println(i.getValue());
                    String[] k = j[1].split("\\+");
                    for (String h : k) {

                        for (String i1 : Tmap1.get(Integer.parseInt(h.trim()))) {

                            String[] get = i1.split("-");
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

                    io = new JLabel();
                    io.setText("<html><font color=\"green\">" + sw.printScoreAndAlignments1() + "<br>" + sw.printScoreAndAlignments2() + "</font></html>");
                    io1 = new JLabel("      ");
                    p.add(io, gbc1);
                    p.add(io1, gbc1);
                    gbc1.gridy++;

                    scoring1 += sw.scoring();
					//SmithWaterman sw = new SmithWaterman(qfunction,tfunction);
                    //////////System.out.println("Alignment Score: " + sw.computeSmithWaterman());
                    ////////System.out.println("");
                } else if (j[1].contains("no")) {
                    List<String> qfunction = new ArrayList<String>();
                    for (String i1 : Qmap.get(Integer.parseInt(i.trim()))) {

                        String[] get = i1.split("-");
                        qfunction.add(get[0].trim());

                    }
                    countdash += qfunction.size();

					////////System.out.println(qfunction.toString().replace("[", "").replace("]", "").replace(",", ""));
                    ////////System.out.println("--not found--");
                    ////////System.out.println("");
                    io = new JLabel();
                    io.setText("<html><font color=\"red\">" + qfunction.toString().replace("[", "").replace("]", "").replace(",", "") + "<br>--not found--</font></html>");
                    io1 = new JLabel("      ");
                    p.add(io, gbc1);
                    p.add(io1, gbc1);
                    gbc1.gridy++;
                    scoring1 += 0;

                } else if (i.contains("no")) {
                    List<String> qfunction = new ArrayList<String>();
                    for (String i1 : Tmap.get(Integer.parseInt(j[1].trim()))) {

                        String[] get = i1.split("-");
                        qfunction.add(get[0].trim());

                    }
                    countdash += qfunction.size();

					////////System.out.println(qfunction.toString().replace("[", "").replace("]", "").replace(",", ""));
                    ////////System.out.println("--not found--");
                    ////////System.out.println("");
                    io = new JLabel();
                    io.setText("<html><font color=\"red\">--not found--<br>" + qfunction.toString().replace("[", "").replace("]", "").replace(",", "") + "</font></html>");
                    io1 = new JLabel("      ");
                    p.add(io, gbc1);
                    p.add(io1, gbc1);
                    gbc1.gridy++;
                    scoring1 += 0;
                } else {
                    //System.out.println(j[1].trim());
                    List<String> tfunction = new ArrayList<String>();

                    for (String i1 : Tmap1.get(Integer.parseInt(j[1].trim()))) {

                        String[] get = i1.split("-");
                        tfunction.add(get[0].trim());

                    }

                    List<String> qfunction = new ArrayList<String>();

                    for (String i1 : Qmap1.get(Integer.parseInt(i.trim()))) {

                        String[] get = i1.split("-");
                        qfunction.add(get[0].trim());

                    }
                    SmithWaterman_b sw = new SmithWaterman_b();
                    sw.init(qfunction, tfunction);
                    sw.process();
                    sw.backtrack();
                    countdash += sw.countdash;
                    countalph += sw.countlet;
					////////System.out.println(sw.printScoreAndAlignments1());
                    ////////System.out.println("");
                    io = new JLabel();
                    io.setText("<html><font color=\"blue\">" + sw.printScoreAndAlignments1() + "<br>" + sw.printScoreAndAlignments2() + "</font></html>");
                    io1 = new JLabel("      ");
                    p.add(io, gbc1);
                    p.add(io1, gbc1);
                    gbc1.gridy++;
                    scoring1 += sw.scoring();
					//String[] k = sw.printScoreAndAlignments();
                    //SmithWaterman sw = new SmithWaterman(qfunction,tfunction);
                    //////////System.out.println("Alignment Score: " + sw.computeSmithWaterman());
                    ////////System.out.println("");

                }
            }
            //////////System.out.println(i.getKey()+"       "+i.getValue());
        }//
        List<String> qfunction = new ArrayList<String>();
        List<String> tfunction = new ArrayList<String>();

        for (List<String> i1 : Tmap.values()) {
            for (String i2 : i1) {
                String[] get = i2.split("-");
                tfunction.add(get[0].trim());
            }
            tfunction.add("                 ");
        }
        for (List<String> i1 : Qmap.values()) {
            for (String i2 : i1) {
                String[] get = i2.split("-");
                qfunction.add(get[0].trim());
            }

            qfunction.add("                 ");
        }
        JPanel kk = new JPanel();
        io1 = new JLabel();
        io = new JLabel();
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
		////////System.out.println(countalph+"       elapsedTime:       " +  countdash);
        ////////System.out.println(Math.log(2)+"    fafafad         " +  sco);
        double eval = (m * n * k * (Math.exp(-(lambda * sc))));

		//eval = (double)Math.round(eval) ;
        ////////System.out.println(n+"   "+m+"    eval         "+eval);
        DecimalFormat df = new DecimalFormat("#.##");
        //////System.out.print(df.format(eval));
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
		//System.out.println("elapsedTime: " +  elapsedTime);
		/*
         ////System.out.println(finalresult);
         JPanel p = new JPanel();
         GridBagConstraints gbc1 = new GridBagConstraints ();
         gbc1.insets = new Insets(2, 1, 2, 1);

         gbc1.weightx = 1.0;
         gbc1.weighty = 1.0;
         gbc1.gridy = 1;
         double scoring1 =0;
         long startTime = System.currentTimeMillis();
         int countdash =0,countalph=0;
         for(Entry<Double, String> i:finalresult.entries())
         {
         List<String> list = new ArrayList<String>();
         String[] j = i.getValue().split("=");
         if(j[0].contains("+"))
         {
         List<String> tfunction = new ArrayList<String>();


         List<String> qfunction = new ArrayList<String>();

         for(String i1 : Tmap.get(Integer.parseInt(j[1].trim().trim())))
         {

         String[] get = i1.split("-");
         tfunction.add(get[0].trim());

         }
         //	////////System.out.println(i.getValue());
         String[] k = j[0].split("\\+");
         for(String h:k)
         {
         for(String i1 : Qmap.get(Integer.parseInt(h.trim())))
         {

         String[] get = i1.split("-");
         qfunction.add(get[0].trim());

         }



         }
         SmithWaterman_b sw = new SmithWaterman_b();
         sw.init(qfunction, tfunction);            
         sw.process();
         sw.backtrack();
         countdash += sw.countdash;
         countalph += sw.countlet;

         sw.printScoreAndAlignments();

         //	score =  (double)Math.round(((score1+score2+score3)/3) * 100.0) / 100.0;
         //SmithWaterman sw = new SmithWaterman(qfunction,tfunction);
         //	////////System.out.println("Alignment Score: " + sw.computeSmithWaterman());
         ////////System.out.println("");
         JLabel io = new JLabel();
         io.setText("<html><font color=\"brown\">"+sw.printScoreAndAlignments1()+"<br>"+sw.printScoreAndAlignments2()+"</font></html>");
         JLabel io1 = new JLabel("      ");
         p.add(io,gbc1);
         p.add(io1,gbc1);
         gbc1.gridy++;
         scoring1+=sw.scoring();

         }
         else if(j[1].contains("+"))
         {
         List<String> tfunction = new ArrayList<String>();

         List<String> tfunction1 = new ArrayList<String>();
         List<String> qfunction = new ArrayList<String>();

         for(String i1 : Qmap.get(Integer.parseInt(j[0].trim())))
         {

         String[] get = i1.split("-");
         qfunction.add(get[0].trim());

         }

         //	////////System.out.println(i.getValue());
         String[] k = j[1].split("\\+");
         for(String h:k)
         {

         for(String i1 : Tmap.get(Integer.parseInt(h.trim())))
         {

         String[] get = i1.split("-");
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
         io.setText("<html><font color=\"green\">"+sw.printScoreAndAlignments1()+"<br>"+sw.printScoreAndAlignments2()+"</font></html>");
         JLabel io1 = new JLabel("      ");
         p.add(io,gbc1);
         p.add(io1,gbc1);
         gbc1.gridy++;



         scoring1+=sw.scoring();
         //SmithWaterman sw = new SmithWaterman(qfunction,tfunction);
         //////////System.out.println("Alignment Score: " + sw.computeSmithWaterman());
         ////////System.out.println("");
         }
         else if(j[1].contains("no"))
         {
         List<String> qfunction = new ArrayList<String>();
         for(String i1 : Qmap.get(Integer.parseInt(j[0].trim())))
         {

         String[] get = i1.split("-");
         qfunction.add(get[0].trim());

         }
         countdash += qfunction.size();

         ////////System.out.println(qfunction.toString().replace("[", "").replace("]", "").replace(",", ""));
         ////////System.out.println("--not found--");
         ////////System.out.println("");

         JLabel io = new JLabel();
         io.setText("<html><font color=\"red\">"+qfunction.toString().replace("[", "").replace("]", "").replace(",", "")+"<br>--not found--</font></html>");
         JLabel io1 = new JLabel("      ");
         p.add(io,gbc1);
         p.add(io1,gbc1);
         gbc1.gridy++;
         scoring1+=0;

         }
         else if(j[0].contains("no"))
         {
         List<String> qfunction = new ArrayList<String>();
         for(String i1 : Tmap.get(Integer.parseInt(j[1].trim())))
         {

         String[] get = i1.split("-");
         qfunction.add(get[0].trim());

         }
         countdash += qfunction.size();

         ////////System.out.println(qfunction.toString().replace("[", "").replace("]", "").replace(",", ""));
         ////////System.out.println("--not found--");
         ////////System.out.println("");

         JLabel io = new JLabel();
         io.setText("<html><font color=\"red\">--not found--<br>"+qfunction.toString().replace("[", "").replace("]", "").replace(",", "")+"</font></html>");
         JLabel io1 = new JLabel("      ");
         p.add(io,gbc1);
         p.add(io1,gbc1);
         gbc1.gridy++;
         scoring1+=0;
         }
         else
         {
         //////////System.out.println(i.getValue());
         List<String> tfunction = new ArrayList<String>();

         for(String i1 : Tmap.get(Integer.parseInt(j[1].trim())))
         {

         String[] get = i1.split("-");
         tfunction.add(get[0].trim());

         }

         List<String> qfunction = new ArrayList<String>();

         for(String i1 : Qmap.get(Integer.parseInt(j[0].trim())))
         {

         String[] get = i1.split("-");
         qfunction.add(get[0].trim());

         }
         SmithWaterman_b sw = new SmithWaterman_b();
         sw.init(qfunction, tfunction);            
         sw.process();
         sw.backtrack();
         countdash += sw.countdash;
         countalph += sw.countlet;
         ////////System.out.println(sw.printScoreAndAlignments1());
         ////////System.out.println("");
         JLabel io = new JLabel();
         io.setText("<html><font color=\"blue\">"+sw.printScoreAndAlignments1()+"<br>"+sw.printScoreAndAlignments2()+"</font></html>");
         JLabel io1 = new JLabel("      ");
         p.add(io,gbc1);
         p.add(io1,gbc1);
         gbc1.gridy++;
         scoring1+=sw.scoring();
         //String[] k = sw.printScoreAndAlignments();
         //SmithWaterman sw = new SmithWaterman(qfunction,tfunction);
         //////////System.out.println("Alignment Score: " + sw.computeSmithWaterman());
         ////////System.out.println("");

         }
         //////////System.out.println(i.getKey()+"       "+i.getValue());
         }//
         List<String> qfunction = new ArrayList<String>();
         List<String> tfunction = new ArrayList<String>();

         for(List<String> i1 : Tmap.values())
         {
         for(String i2 : i1)
         {
         String[] get = i2.split("-");
         tfunction.add(get[0].trim());
         }
         tfunction.add("                 ");
         }
         for(List<String> i1 : Qmap.values())
         {
         for(String i2 : i1)
         {
         String[] get = i2.split("-");
         qfunction.add(get[0].trim());
         }

         qfunction.add("                 ");
         }
         JPanel kk = new JPanel();
         JLabel io1 = new JLabel();
         JLabel io = new JLabel();
         Collections.reverse(qfunction);
         Collections.reverse(tfunction);
         JLabel io2 = new JLabel("<html><font color=\"Purple\">"+qfunction+"</font><br><font color=\"Olive\">"+tfunction+" </font></html>");


         //kk.add(io2,gbc1);
         int sc= (countalph*1)-(countdash*3);
         double k = 0.747;
         double lambda = 1.39;
         double sco = ((lambda*sc)+Math.log(k))/Math.log(2);


         // (double)Math.round(((score1+score2+score3)/3) * 100.0) / 100.0
         sco = Math.round(sco* 100.0) / 100.0;
         int m =500,n=0;
         for(Entry<Integer, List<String>> i :Qmap.entrySet())
         {

         n+=i.getValue().size();
         }
         for(Entry<Integer, List<String>> i :Tmap.entrySet())
         {

         m+=i.getValue().size();
         }
         ////////System.out.println(countalph+"       elapsedTime:       " +  countdash);
         ////////System.out.println(Math.log(2)+"    fafafad         " +  sco);
         double eval = (m*n*k*(Math.exp(-(lambda*sc))));

         //eval = (double)Math.round(eval) ;



         ////////System.out.println(n+"   "+m+"    eval         "+eval);
         DecimalFormat df = new DecimalFormat("#.##");
         //////System.out.print(df.format(eval));
         io.setText("<html><font color=\"Purple\">Bitscore:  </font><font color=\"Olive\">"+sco+" </font></html>");
         io1.setText("<html><font color=\"Maroon\">e-val:  </font><font color=\"Olive\">"+eval+" </font></html>");
         kk.add(io,gbc1);
         gbc1.gridy++;
         kk.add(io1,gbc1);
         frame.add(p, BorderLayout.NORTH);
         frame.add(kk, BorderLayout.SOUTH);
         frame.setVisible(true);
         j2.setText("<html><font color=\"red\">hello world!</font></html>");
         frame.add(j2, BorderLayout.NORTH);
         long stopTime = System.currentTimeMillis();
         double elapsedTime = ((stopTime - startTime)) ;
         */

		// expected value: kmne^-(lamda*s)
        //////////System.out.println("elapsedTime: " +  elapsedTime);
    }

    public static String getmax1(Collection<Multimap<Double, String>> collection, Multimap<Integer, Multimap<Double, String>> reverse) {
        String j = "", geneIDs = "";
        String score = "";
        double max = 0;
        boolean sa = true;
        /*reverse.asMap().remove(4);
         reverse.asMap().remove(5);*/
        ////System.out.println(collection+"     dsa");
        if (collection.size() > 0) {
            while (sa) {
                for (Multimap<Double, String> i : collection) {
                    try {
                        max = i.keySet().iterator().next();
                        //////////System.out.println(max);
                        finalscore = max;
                        for (String s : i.get(max)) {

                            //	////////System.out.println(s);
                            if (s.contains("+")) {
                                if (j.equals("")) {

                                } else {
                                    j += ";";
                                }
                                String[] k = s.split("\\+");
                                for (String ju : k) {

                                    if (reverse.containsKey(Integer.parseInt(ju))) {

                                        if (j.equals("")) {
                                            j = ju;
                                        } else {
                                            j += "+" + ju;
                                        }
                                    } else {
                                        break;
                                    }

                                }

                            } else {
                                //////////System.out.println(s+"     dsad         "+reverse);
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
                //////////System.out.println(j+"          dsa");
                if (!j.trim().equals("")) {
                    sa = false;

                }

                double roundOff = (double) Math.round(max * 100) / 100;
                score = roundOff + "=" + j;
                //////////System.out.println(score);

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
		//	////////System.out.println(Qgene1+" " + Tgene1);

        //////////System.out.println(LevenshteinDistance.computeLevenshteinDistance(Qgene1,Tgene1));
    }

    public static Multimap<Integer, Multimap<Double, String>> comparing(Map<Integer, List<String>> Qmap, Map<Integer, List<String>> Tmap, Map<Integer, List<String>> Qmap1, Map<Integer, List<String>> Tmap1, int tmapsize2) {
		////System.out.println(Qmap);

        int next_itr_q = 0;
        double sccc = 0;

        Multimap<Double, String> forwardscores = ArrayListMultimap.create();
        Multimap<Integer, Multimap<Double, String>> Forward_bestscores = ArrayListMultimap.create();
        /*for(Entry<Integer, List<String>> query :Qmap.entrySet())
         {*/
        for (int query : Qmap.keySet()) {
            double bestval = 0;
            next_itr_q = query;
            int next_itr_t = 0;

            forwardscores.clear();
            for (int target : Tmap.keySet()) {
                //	//System.out.println(" fdsad    "+target);
                boolean gotit = true;

                next_itr_t = target;
                if ((Qmap.get(query).size() <= Tmap.get(target).size())) {

                    List<String> qfunction = new ArrayList<String>();
                    List<String> qactivity = new ArrayList<String>();
                    List<String> qsubstrate = new ArrayList<String>();
                    for (String i : Qmap.get(query)) {

                        String[] get = i.split("-");
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
                    for (String i : Tmap.get(target)) {

                        String[] get = i.split("-");
                        tfunction.add(get[0].trim());
                        tactivity.add(get[1].trim());
                        tsubstrate.add(get[2].trim());
                    }

                    score1 = 1 - ((double) LevenshteinDistance.computeLevenshteinDistance(qfunction, tfunction) / (Math.max(qfunction.size(), tfunction.size())));

                    score2 = 1 - ((double) LevenshteinDistance.computeLevenshteinDistance(qactivity, tactivity) / (Math.max(qactivity.size(), tactivity.size())));
                    score3 = 1 - ((double) LevenshteinDistance.computeLevenshteinDistance(qsubstrate, tsubstrate) / (Math.max(qsubstrate.size(), tsubstrate.size())));
                    score = Math.round(((score1 + score2 + score3) / 3) * 100.0) / 100.0;
                    if (score > maximumscore) {
                        maximumscore = score;
                    }
					//////////System.out.println(query.getValue()+"    "+target.getValue());
                    //////////System.out.println(LevenshteinDistance.computeLevenshteinDistance(qsubstrate,tsubstrate));
                    //////////System.out.println(score1+"        "+score2+"        "+score3+"        "+score);

                    forwardscores.put(score, Integer.toString(next_itr_t));

                } else if (Qmap.get(query).size() > Tmap.get(target).size()) {

                    List<String> qfunction = new ArrayList<String>();
                    List<String> qactivity = new ArrayList<String>();
                    List<String> qsubstrate = new ArrayList<String>();
                    for (String i : Qmap.get(query)) {

                        String[] get = i.split("-");
                        qfunction.add(get[0].trim());
                        qactivity.add(get[1].trim());
                        qsubstrate.add(get[2].trim());
                    }
                    ////System.out.println(query+"    "+target+"    "+Tmap.get(next_itr_t+1)+""+tmapsize);
                    if (next_itr_t + 1 < tmapsize2) {

                        //	//System.out.println("fd      "+target+"      "+next_itr_t);
                        List<String> t1 = new ArrayList<String>();
                        List<String> t2 = new ArrayList<String>();;
                        while (true) {
                            t1 = Tmap.get(target);
                            t2 = Tmap.get(next_itr_t + 1);

                            if ((t1 == null) || (t2 == null)) {
                                if (next_itr_t < tmapsize2) {
                                    next_itr_t = next_itr_t + 1;
                                } else {

                                    break;
                                }

                            } else {
                                break;
                            }
                        }

                        int count = t1.size() + t2.size();

                        String itr = next_itr_t + "+" + (next_itr_t + 1);

                        List<String> newList = new ArrayList<String>();
                        List<String> rnewList = new ArrayList<String>();
                        newList.addAll(t1);
                        newList.addAll(t2);

                        rnewList.addAll(t2);
                        rnewList.addAll(t1);
                        ////System.out.println("  dfs     "+target+"         " + newList+"                "+itr);
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

                            String[] get = i.split("-");
                            tfunction.add(get[0].trim());
                            tactivity.add(get[1].trim());
                            tsubstrate.add(get[2].trim());
                        }
                        for (String i : rnewList) {

                            String[] get = i.split("-");
                            rtfunction.add(get[0].trim());
                            rtactivity.add(get[1].trim());
                            rtsubstrate.add(get[2].trim());
                        }

						//	//System.out.println(query+"    "+rnewList);
						//SmithWaterman sw = new SmithWaterman(query.getValue(),newList);
                        ////////////System.out.println("Alignment Score: " + sw.computeSmithWaterman());
                        rscore = 1 - ((double) LevenshteinDistance.computeLevenshteinDistance(qfunction, rtfunction) / (Math.max(qfunction.size(), rtfunction.size())));

                        score1 = 1 - ((double) LevenshteinDistance.computeLevenshteinDistance(qfunction, tfunction) / (Math.max(qfunction.size(), tfunction.size())));
                        if (rscore < score1) {
                            itr = target + "+" + (next_itr_t + 1);

                            score2 = 1 - ((double) LevenshteinDistance.computeLevenshteinDistance(qactivity, tactivity) / (Math.max(qactivity.size(), tactivity.size())));
                            score3 = 1 - ((double) LevenshteinDistance.computeLevenshteinDistance(qsubstrate, tsubstrate) / (Math.max(qsubstrate.size(), tsubstrate.size())));
                        } else {
                            score1 = rscore;
                            itr = (next_itr_t + 1) + "+" + target;
                            score2 = 1 - ((double) LevenshteinDistance.computeLevenshteinDistance(qactivity, rtactivity) / (Math.max(qactivity.size(), rtactivity.size())));
                            score3 = 1 - ((double) LevenshteinDistance.computeLevenshteinDistance(qsubstrate, rtsubstrate) / (Math.max(qsubstrate.size(), rtsubstrate.size())));

                        }
                        score = Math.round((((2 * score1) + (0.5 * score2) + (0.5 * score3)) / 3) * 100.0) / 100.0;
                        if (score > maximumscore) {
                            maximumscore = score;
                        }
						//////////System.out.println(query.getValue()+"    "+newList);
                        //////////System.out.println(LevenshteinDistance.computeLevenshteinDistance(qsubstrate,tsubstrate));
                        //////////System.out.println(score1+"        "+score2+"        "+score3+"        "+score);

                        forwardscores.put(score, itr);

                    }

                }
            }
            Multimap<Double, String> forwardscore1 = ArrayListMultimap.create(forwardscores);
            TreeMultimap<Double, String> forwardscore = TreeMultimap.create(Ordering.natural().reverse(), Ordering.natural());

            forwardscore.putAll(forwardscore1);

            Forward_bestscores.put(next_itr_q, forwardscore);

        }
        //	//System.out.println(maximumscore+"                "+Forward_bestscores+"      score");
        return Forward_bestscores;

    }

    public static void calculations1(Multimap<Integer, Multimap<Double, String>> forward, Multimap<Integer, Multimap<Double, String>> forward1, Multimap<Integer, Multimap<Double, String>> reverse) {
        double scoring = 0;
        int diff = 100;
        String tgene = "";

        List<Integer> query = new ArrayList<Integer>();
        List<Integer> target = new ArrayList<Integer>();
        List<String> values = new ArrayList<String>();
        Multimap<Integer, Multimap<Double, String>> forward2 = ArrayListMultimap.create(forward);
        for (int i : forward2.keys()) {
            int count = i;
            //////System.out.println("Count:  "+forward.get(count).size());
            if (forward.get(count).size() > 0) {
                double sce = 0;
                //////System.out.println(count);
                diff = 100;
                String[] Target_genescores = (getmax1(forward.get(count), reverse)).toString().split("=");
                //////System.out.println(getmax1(forward.get(count),reverse)+"    "+Target_genescores.length);
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
                                        //////////System.out.println("----sdfds    "+Query_genescores.length);
                                        if (Query_genescores.length > 1) {
                                            //	////////System.out.println(getmax(reverse.get(Integer.parseInt(Tgene))));
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
                                            tgene = "no";
                                            scoring = 0;
                                        }
                                    }
                                } else {
                                    //////////System.out.println(Tgene);
                                    String[] Query_genescores = getmax1(reverse.get(Integer.parseInt(Tgene)), forward).split("=");
                                    ////////System.out.println("----sdfds    "+Query_genescores.length);
                                    if (Query_genescores.length > 1) {
                                        //////System.out.println(getmax1(reverse.get(Integer.parseInt(Tgene)),forward)+"           "+ Tgene);
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
                                                    tgene = "no";
                                                    scoring = 0;
                                                }
                                            }
                                        }
                                    } else {
                                        tgene = "no";
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
                                //////////System.out.println(sce+"   dfs               ");

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
                                            //	////////System.out.println(getmax(reverse.get(Integer.parseInt(Tgene))));
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
                                                        tgene = "no";
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
                                                            tgene = "no";
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
                                                        tgene = "no";
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                //////////System.out.println(Tgene);
                                String[] Query_genescores = getmax1(reverse.get(Integer.parseInt(Tgene)), forward).split("=");
                                //////System.out.println(getmax1(reverse.get(Integer.parseInt(Tgene)),forward)+"            "+Tgene);
                                if (Query_genescores.length > 1) {
                                    ////System.out.println(getmax1(reverse.get(Integer.parseInt(Tgene)),forward)+"     "+count);
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
                                                        tgene = "no";
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
                                                    tgene = "no";
                                                }
                                            }
                                        }

                                    } else {
                                        //String u = Query_genescores[1].trim();
                                        String[] Tgenes = Tgene.split("\\+");

                                        for (String gene : Tgenes) {
                                            String[] Query_genescores1 = getmax1(reverse.get(Integer.parseInt(gene)), forward).split("=");
                                            ////////System.out.println(count+"          "+Tgene+"           "+getmax1(reverse.get(Integer.parseInt(gene)),forward));
                                            sce += Double.parseDouble(Query_genescores1[0].trim());
                                            //////////System.out.println(sce+"   dfs               "+Query_genescores[0].trim());
                                        }

                                        sce = (sce / 2);
                                        //	////////System.out.println(sce+"         "+Target_genescores[0].trim());
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
                                                    tgene = "no";
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
                                                tgene = "no";
                                            }

                                        } else {
                                            String u = Query_genescores[1].trim();
											////System.out.println(Query_genescores[0].trim()+"   dsf   "+count+"     "+Double.parseDouble(Target_genescores[0].trim()));

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
                                                tgene = "no";
                                                scoring = 0;
                                            }
                                        }
                                    }

                                }
                            }
                        }

                        //	////////System.out.println(scoring+"     ;           "+ tgene);
                        if (!tgene.trim().equals("")) {
                            finalresult.put(scoring, count + "=" + tgene);
                            forward.asMap().remove(count);
                            try {
                                reverse.asMap().remove(Integer.parseInt(tgene.trim()));
                            } catch (Exception dsc) {
                            }
                        }
                    } else {

                        tgene = "no";
                        scoring = 0;
                        finalresult.put(0.0, count + "=" + tgene);
                    }
                }
            }
        }

    }

    Predicate<Integer> evenFilter = new Predicate<Integer>() {
        public boolean apply(Integer i) {
            return (i % 2 == 0);
        }
    };

    public static void calculations(Multimap<Integer, Multimap<Double, String>> forward, Multimap<Integer, Multimap<Double, String>> forward1, Multimap<Integer, Multimap<Double, String>> reverse) {
        double scoring = 0;
        int diff = 100;
        String tgene = "";

        List<Integer> query = new ArrayList<Integer>();
        List<Integer> target = new ArrayList<Integer>();
        List<String> values = new ArrayList<String>();
        for (int i = 0; i < forward1.size(); i++) {
            int count = i + 1;
            double sce = 0;
            //	//////System.out.println(count);
            diff = 100;
            String[] Target_genescores = (getmax1(forward.get(count), reverse)).toString().split("=");
            ////////System.out.println(getmax1(forward.get(count),reverse)+"    "+Target_genescores.length);
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
                                    //////////System.out.println("----sdfds    "+Query_genescores.length);
                                    if (Query_genescores.length > 1) {
                                        //	////////System.out.println(getmax(reverse.get(Integer.parseInt(Tgene))));
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
                                        tgene = "no";
                                        scoring = 0;
                                    }
                                }
                            } else {
                                //////////System.out.println(Tgene);
                                String[] Query_genescores = getmax1(reverse.get(Integer.parseInt(Tgene)), forward).split("=");
                                ////////System.out.println("----sdfds    "+Query_genescores.length);
                                if (Query_genescores.length > 1) {
                                    ////////System.out.println(getmax1(reverse.get(Integer.parseInt(Tgene)),forward)+"           "+ Tgene);
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
                                                tgene = "no";
                                                scoring = 0;
                                            }
                                        }
                                    }
                                } else {
                                    tgene = "no";
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
                            //////////System.out.println(sce+"   dfs               ");

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
                                        //	////////System.out.println(getmax(reverse.get(Integer.parseInt(Tgene))));
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
                                                    tgene = "no";
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
                                                        tgene = "no";
                                                    }
                                                }
                                            } else {
                                                String u = Query_genescores[1].trim();
                                                if (Double.parseDouble(Query_genescores[0].trim()) <= Double.parseDouble(Target_genescores[0].trim())) {
                                                    scoring = Double.parseDouble(Target_genescores[0].trim());
                                                    int dif = Math.abs(Integer.parseInt(u) - count);

                                                    //////System.out.println(u+"          dsad");
                                                    if (diff >= dif) {

                                                        if (diff == dif) {

                                                            tgene = u;
                                                        } else {
                                                            diff = dif;
                                                            tgene = Tgene;
                                                        }

                                                    }
                                                } else if (Double.parseDouble(Query_genescores[0].trim()) >= Double.parseDouble(Target_genescores[0].trim())) {
                                                    //////System.out.println(u+"          dsad");
                                                    scoring = Double.parseDouble(Query_genescores[0].trim());
                                                    int dif = Math.abs(Integer.parseInt(u) - count);
                                                    if (diff > dif) {
                                                        diff = dif;
                                                        tgene = Tgene;
                                                    }
                                                } else {
                                                    tgene = "no";
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            //////////System.out.println(Tgene);
                            String[] Query_genescores = getmax1(reverse.get(Integer.parseInt(Tgene)), forward).split("=");
                            //	////////System.out.println(getmax(reverse.get(Integer.parseInt(Tgene))));
                            if (Query_genescores.length > 1) {
                                //////System.out.println(getmax1(reverse.get(Integer.parseInt(Tgene)),forward)+"   dsadasdasd");
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
                                                    tgene = "no";
                                                }
                                            }
                                        } else {

                                            if (Double.parseDouble(Query_genescores[0].trim()) <= Double.parseDouble(Target_genescores[0].trim())) {
                                                scoring = Double.parseDouble(Target_genescores[0].trim());
                                                int dif = Math.abs(Integer.parseInt(u) - Integer.parseInt(Tgene));
                                                //////System.out.println(u);
                                                if (diff >= dif) {

                                                    //////System.out.println(diff+"               "+dif+ "               "+u+"           "+Tgene);
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
                                        ////////System.out.println(count+"          "+Tgene+"           "+getmax1(reverse.get(Integer.parseInt(gene)),forward));
                                        sce += Double.parseDouble(Query_genescores1[0].trim());
                                        //////////System.out.println(sce+"   dfs               "+Query_genescores[0].trim());
                                    }

                                    sce = (sce / 2);
                                    //	////////System.out.println(sce+"         "+Target_genescores[0].trim());
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
                                                tgene = "no";
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
                                            tgene = "no";
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
                                            tgene = "no";
                                            scoring = 0;
                                        }
                                    }
                                }

                            }
                        }
                    }

                    //	////////System.out.println(scoring+"     ;           "+ tgene);
                    if (!tgene.trim().equals("")) {
                        finalresult.put(scoring, count + "=" + tgene);
                        forward.asMap().remove(count);
                        try {
                            reverse.asMap().remove(Integer.parseInt(tgene.trim()));
                        } catch (Exception dsc) {
                        }
                    }
                } else {

                    tgene = "no";
                    scoring = 0;
                    finalresult.put(0.0, count + "=" + tgene);
                }
            }

        }
    }

}
