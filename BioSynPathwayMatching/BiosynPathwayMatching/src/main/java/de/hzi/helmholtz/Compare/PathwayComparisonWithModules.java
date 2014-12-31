/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hzi.helmholtz.Compare;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
import de.hzi.helmholtz.Domains.Domain;
import de.hzi.helmholtz.ModuleGenes.ModuleGene;
import de.hzi.helmholtz.Modules.Module;
import de.hzi.helmholtz.Pathways.PathwayWithModules;
import de.hzi.helmholtz.Writers.WriteResultToDatabase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;

/**
 *
 * @author srdu001
 */
public class PathwayComparisonWithModules {

    private static double finalscore = 0;
    static double s_add = 1.0, s_penality = -1.0;
    static double SCORE_MISMATCH_ERROR = 0.2f;
    static double DISTANCE_MISMATCH_ERROR = 1;
    JFrame frame;
    //Map<Integer, List<String>> Qmap = new HashMap<Integer, List<String>>();
    //Map<Integer, List<String>> Tmap = new HashMap<Integer, List<String>>();
    private PathwayWithModules source;
    private PathwayWithModules target;
    private BiMap<Integer, Integer> srcGeneIdToPositionMap;
    private BiMap<Integer, Integer> tgtGeneIdToPositionMap;
    // Range of window sizes to be considered for combining domains into single genes --> [1,maxWindowSize]
    private int maxWindowSize = 0;
    private double functionWeight = 1.9f;
    private double statusWeight = 0.1f;
    private double substrateWeight = 0;
    private int GENE_MAX_DISTANCE = 1000;
    Multimap<String, Multimap<String, Integer>> ScoreFunctionMatchMisMatch = ArrayListMultimap.create();
    Multimap<String, Multimap<String, Integer>> ScoreStatusMatchMisMatch = ArrayListMultimap.create();
    Multimap<String, Multimap<String, Integer>> ScoreSubstrateMatchMisMatch = ArrayListMultimap.create();

    double kscore = 0.747;
    double lambda = 1.39;
    private Map<String, Map<String, Double>> bestResultMapping;

    private String UniqueJobID = "";
    WriteResultToDatabase todatabase = new WriteResultToDatabase();

    public PathwayComparisonWithModules(final PathwayWithModules source, final PathwayWithModules target, int windowsize, String ProcessId) {
        this.source = source;
        this.target = target;

        maxWindowSize = windowsize;
        constructBiMaps();
        //System.out.println(source.toString());
        //System.out.println(target.toString());
        UniqueJobID = ProcessId;
        frame = new JFrame("Matching");
    }

    /* Construct position to geneId bimaps of both source and target pathways */
    private void constructBiMaps() {
        srcGeneIdToPositionMap = HashBiMap.create();
        int temp = 1;
        for (ModuleGene e : source.getModulegenes()) {
            srcGeneIdToPositionMap.put(e.getGeneId(), temp++);
        }
        tgtGeneIdToPositionMap = HashBiMap.create();
        temp = 1;
        for (ModuleGene e : target.getModulegenes()) {
            tgtGeneIdToPositionMap.put(e.getGeneId(), temp++);
        }
    }

    /* Utility function to transform 1+2 to geneid(1)+geneid(2) */
    public String reconstructWithGeneId(String positionIdStr, BiMap<Integer, Integer> newGeneIdToPositionMap) {
        String geneIdStr = "";
        String[] positions = positionIdStr.split("\\+");
        for (String position : positions) {
            int pos = Integer.parseInt(position.trim());
            geneIdStr += newGeneIdToPositionMap.inverse().get(pos) + "+";
        }
        geneIdStr = geneIdStr.substring(0, geneIdStr.length() - 1);
        return geneIdStr;
    }

    public Multimap<Double, String> SubsetsMatching(final PathwayWithModules firstPathway, final PathwayWithModules secondPathway, BiMap<Integer, Integer> newSourceGeneIdToPositionMap, BiMap<Integer, Integer> newTargetGeneIdToPositionMap, int Yes) {
        Multimap<Double, String> resultPerfect = TreeMultimap.create(Ordering.natural().reverse(), Ordering.natural());
        PathwayWithModules firstPathwayCopy = new PathwayWithModules(firstPathway);// Copy of the Query pathway
        PathwayWithModules secondPathwayCopy = new PathwayWithModules(secondPathway);// Copy of the Target pathway'
        // PathwayWithModules secondPathwayCopy1 = new PathwayWithModules(secondPathway);
        int currentQueryGene = 0;
        Iterator<ModuleGene> sourceGeneIt = firstPathway.moduleGeneIterator();
        List<Integer> QueryToRemove = new ArrayList<Integer>();
        List<Integer> TargetToRemove = new ArrayList<Integer>();
        while (sourceGeneIt.hasNext()) {
            currentQueryGene++;
            ModuleGene queryGene = sourceGeneIt.next();

            int currentTargetGene = 0;
            Multiset<String> qfunction = LinkedHashMultiset.create();
            List<String> qfunctionList = new ArrayList<String>();
            List<String> qactivity = new ArrayList<String>();
            List<Set<String>> qsubstrate = new ArrayList<Set<String>>();
            for (Module m : queryGene.getModule()) {
                for (Domain d : m.getDomains()) {
                    qfunction.add(d.getDomainFunctionString());
                    qfunctionList.add(d.getDomainFunctionString());
                    qactivity.add(d.getStatus().toString());
                    qsubstrate.add(d.getSubstrates());
                }
            }
            Iterator<ModuleGene> targetGeneIt = secondPathway.moduleGeneIterator();

            while (targetGeneIt.hasNext()) {
                currentTargetGene++;
                ModuleGene targetGene = targetGeneIt.next();
                Multiset<String> tfunction = LinkedHashMultiset.create();
                List<String> tfunctionList = new ArrayList<String>();
                List<String> tactivity = new ArrayList<String>();
                List<Set<String>> tsubstrate = new ArrayList<Set<String>>();
                for (Module m : targetGene.getModule()) {
                    for (Domain d : m.getDomains()) {
                        tfunctionList.add(d.getDomainFunctionString());
                        tfunction.add(d.getDomainFunctionString());
                        tactivity.add(d.getStatus().toString());
                        tsubstrate.add(d.getSubstrates());
                    }
                }
                Multiset<String> DomainsCovered = Multisets.intersection(qfunction, tfunction);
                if (DomainsCovered.size() == qfunction.size() && DomainsCovered.size() == tfunction.size()) {
                    Multimap<Double, Multimap<String, Integer>> activityscores = myFunction.calculate(qactivity, tactivity);
                    Multimap<String, Integer> Functionscores = ArrayListMultimap.create();

                    int TranspositionDomains = LevenshteinDistance.computeLevenshteinDistance(qfunctionList, tfunctionList);
                    if (TranspositionDomains > 0) {
                        TranspositionDomains = 1;
                    }

                    Functionscores.put(qfunction.size() + "-0", TranspositionDomains);
                    Multimap<Double, Multimap<String, Integer>> substratescore = myFunction.calculate(getSubstrateList(qsubstrate), getSubstrateList(tsubstrate));
                    Object activityScore = activityscores.asMap().keySet().toArray()[0];
                    Object substrateScore = substratescore.asMap().keySet().toArray()[0];
                    double finalScore = Math.round((((2.9 * 1.0) + (0.05 * Double.parseDouble(activityScore.toString().trim())) + (0.05 * Double.parseDouble(substrateScore.toString().trim()))) / 3) * 100.0) / 100.0;
                    String ConvertedGeneIDs = "";
                    if (Yes == 0) {
                        ConvertedGeneIDs = reconstructWithGeneId(Integer.toString(currentQueryGene), newSourceGeneIdToPositionMap) + "->" + reconstructWithGeneId(Integer.toString(currentTargetGene), newTargetGeneIdToPositionMap);
                    } else {
                        ConvertedGeneIDs = reconstructWithGeneId(Integer.toString(currentTargetGene), newTargetGeneIdToPositionMap) + "->" + reconstructWithGeneId(Integer.toString(currentQueryGene), newSourceGeneIdToPositionMap);
                    }
                    resultPerfect.put(finalScore, ConvertedGeneIDs);
                    ScoreFunctionMatchMisMatch.put(ConvertedGeneIDs, Functionscores);
                    ScoreStatusMatchMisMatch.putAll(ConvertedGeneIDs, activityscores.values());
                    ScoreSubstrateMatchMisMatch.putAll(ConvertedGeneIDs, substratescore.values());

                    TargetToRemove.add(currentTargetGene);
                    QueryToRemove.add(currentQueryGene);
                }
            }

        }
        for (int i : TargetToRemove) {
            secondPathwayCopy.removeGene(i);
        }
        for (int i : QueryToRemove) {
            firstPathwayCopy.removeGene(i);
        }
        if (firstPathwayCopy.size() > 0 && secondPathwayCopy.size() > 0) {
            // Re-construct the bimaps
            newSourceGeneIdToPositionMap = HashBiMap.create();
            int temp = 0;
            for (ModuleGene e : firstPathwayCopy.getModulegenes()) {
                temp = temp + 1;
                newSourceGeneIdToPositionMap.put(e.getGeneId(), temp);
            }
            newTargetGeneIdToPositionMap = HashBiMap.create();
            temp = 0;
            for (ModuleGene e : secondPathwayCopy.getModulegenes()) {
                temp = temp + 1;
                newTargetGeneIdToPositionMap.put(e.getGeneId(), temp);
            }
            resultPerfect.putAll(SubsetIdentification(firstPathwayCopy, secondPathwayCopy, newSourceGeneIdToPositionMap, newTargetGeneIdToPositionMap, Yes));
        }
        System.out.println(resultPerfect);
        return resultPerfect;
    }

    public Multimap<Double, String> SubsetIdentification(PathwayWithModules firstPathway, PathwayWithModules secondPathway, BiMap<Integer, Integer> newSourceGeneIdToPositionMap, BiMap<Integer, Integer> newTargetGeneIdToPositionMap, int Yes) {
        Multimap<Double, String> result = TreeMultimap.create(Ordering.natural().reverse(), Ordering.natural());

        Iterator<ModuleGene> sourceGeneIt = firstPathway.moduleGeneIterator();
        int currentQueryGene = 0;
        while (sourceGeneIt.hasNext()) {
            currentQueryGene++;
            ModuleGene queryGene = sourceGeneIt.next();
            Multimap<Integer, String> resultr = TreeMultimap.create(Ordering.natural(), Ordering.natural());
            int currentTargetGene = 0;
            Multiset<String> qfunction = LinkedHashMultiset.create();
            List<String> qfunctionList = new ArrayList<String>();
            List<String> qactivity = new ArrayList<String>();
            List<Set<String>> qsubstrate = new ArrayList<Set<String>>();
            for (Module m : queryGene.getModule()) {
                for (Domain d : m.getDomains()) {
                    qfunction.add(d.getDomainFunctionString());
                    qfunctionList.add(d.getDomainFunctionString());
                    qactivity.add(d.getStatus().toString());
                    qsubstrate.add(d.getSubstrates());
                }
            }
            List<String> TargenesSelected = new ArrayList<String>();
            Iterator<ModuleGene> targetGeneIt = secondPathway.moduleGeneIterator();
            while (targetGeneIt.hasNext()) {
                currentTargetGene++;
                ModuleGene targetGene = targetGeneIt.next();
                Multiset<String> tfunction = LinkedHashMultiset.create();
                List<String> tactivity = new ArrayList<String>();
                List<Set<String>> tsubstrate = new ArrayList<Set<String>>();
                List<String> tfunctionList = new ArrayList<String>();
                Iterator<Module> mIter = targetGene.moduleIterator();
                while (mIter.hasNext()) {
                    Module m = mIter.next();
                    Iterator<Domain> dIter = m.domainIterator();
                    while (dIter.hasNext()) {
                        Domain d = dIter.next();
                        tfunction.add(d.getDomainFunctionString());
                        tfunctionList.add(d.getDomainFunctionString());
                        tactivity.add(d.getStatus().toString());
                        tsubstrate.add(d.getSubstrates());
                    }
                }
                Multiset<String> DomainsCovered = Multisets.intersection(qfunction, tfunction);
                int Differences = Math.max(Math.abs(DomainsCovered.size() - tfunction.size()), Math.abs(DomainsCovered.size() - qfunction.size()));
                if (DomainsCovered.size() == tfunction.size() && tfunction.size() > 4) {
                    TargenesSelected.add(Integer.toString(currentTargetGene));
                } else {
                    resultr.put(Differences, Integer.toString(currentTargetGene));
                }

            }
            int count = 0;
            if (resultr.size() > 0) {
                while (TargenesSelected.size() < 2) {
                    Multiset<String> k = LinkedHashMultiset.create(resultr.values());
                    Multiset<String> t = LinkedHashMultiset.create(TargenesSelected);
                    Multiset<String> Covered = Multisets.intersection(k, t);
                    if (Covered.size() == k.size()) {
                        break;
                    }

                    try {
                        TargenesSelected.addAll(resultr.get(Integer.parseInt(resultr.keySet().toArray()[count].toString())));
                    } catch (Exception ds) {
                    }
                    count = count + 1;
                }
            }
            // //System.out.println(TargenesSelected);
            //  Permutation perm = new Permutation();
            //  List<String> perms = perm.run(TargenesSelected);
            CombinationGenerator c = new CombinationGenerator(10, 10);
            List<String> perms = c.GenerateAllPossibleCombinations(TargenesSelected);
            myFunction sim = new myFunction();
            double score = 0;
            String targetIdentified = "";
            List<ModuleGene> targetGenesList = secondPathway.getModulegenes();
            for (String permu : perms) {
                String[] values = permu.replace("[", "").replace("]", "").split(",");
                List<String> mergedTargetgenes = new ArrayList<String>();
                List<Integer> ToRemove = new ArrayList<Integer>();
                List<String> tactivity = new ArrayList<String>();
                List<Set<String>> tsubstrate = new ArrayList<Set<String>>();
                for (String j : values) {
                    ToRemove.add(Integer.parseInt(j.trim()));
                    for (Module m : targetGenesList.get(Integer.parseInt(j.trim()) - 1).getModule()) {
                        for (Domain i : m.getDomains()) {
                            mergedTargetgenes.add(i.getDomainFunctionString());
                            tactivity.add(i.getStatus().toString());
                            tsubstrate.add(i.getSubstrates());
                        }
                    }
                }
                Multimap<Double, Multimap<String, Integer>> FunctionScores = sim.calculate(qfunctionList, mergedTargetgenes);
                Multimap<Double, Multimap<String, Integer>> activityscores = myFunction.calculate(qactivity, tactivity);
                Multimap<Double, Multimap<String, Integer>> substratescores = myFunction.calculate(getSubstrateList(qsubstrate), getSubstrateList(tsubstrate));
                Object FunctionScore = FunctionScores.asMap().keySet().toArray()[0];
                Object activityScore = activityscores.asMap().keySet().toArray()[0];
                Object substrateScore = substratescores.asMap().keySet().toArray()[0];

                double finalScore = Math.round((((2.9 * Double.parseDouble(FunctionScore.toString().trim())) + (0.05 * Double.parseDouble(activityScore.toString().trim())) + (0.05 * Double.parseDouble(substrateScore.toString().trim()))) / 3) * 100.0) / 100.0;
                targetIdentified = permu.replace(",", "+");
                String ConvertedGeneIDs = "";
                if (Yes == 0) {
                    ConvertedGeneIDs = reconstructWithGeneId(Integer.toString(currentQueryGene), newSourceGeneIdToPositionMap) + "->" + reconstructWithGeneId(targetIdentified.replace("[", "").replace("]", ""), newTargetGeneIdToPositionMap);
                } else {
                    ConvertedGeneIDs = reconstructWithGeneId(targetIdentified.replace("[", "").replace("]", ""), newTargetGeneIdToPositionMap) + "->" + reconstructWithGeneId(Integer.toString(currentQueryGene), newSourceGeneIdToPositionMap);
                }
                // String ConvertedGeneIDs = reconstructWithGeneId(Integer.toString(currentQueryGene), newSourceGeneIdToPositionMap) + "->" + reconstructWithGeneId(targetIdentified.replace("[", "").replace("]", ""), newTargetGeneIdToPositionMap);

                result.put(finalScore, ConvertedGeneIDs);

                ScoreFunctionMatchMisMatch.putAll(ConvertedGeneIDs, FunctionScores.values());
                ScoreStatusMatchMisMatch.putAll(ConvertedGeneIDs, activityscores.values());
                ScoreSubstrateMatchMisMatch.putAll(ConvertedGeneIDs, substratescores.values());

            }

        }
        return result;
    }

    private class executionModule implements Runnable {

        private String permu;
        private List<ModuleGene> targetGenesList;
        private List<String> qfunctionList;
        private List<String> qactivity;
        private List<Set<String>> qsubstrate;
        private BiMap<Integer, Integer> newSourceGeneIdToPositionMap;
        private BiMap<Integer, Integer> newTargetGeneIdToPositionMap;
        private int Yes;
        private int currentQueryGene;
        private Multimap<Double, String> result;
        private String command;

        public executionModule(String s, String permu, List<ModuleGene> targetGenesList, List<String> qfunctionList, List<String> qactivity, List<Set<String>> qsubstrate,
                BiMap<Integer, Integer> newSourceGeneIdToPositionMap, BiMap<Integer, Integer> newTargetGeneIdToPositionMap, int Yes, int currentQueryGene, Multimap<Double, String> result) {
            this.command = s;
            this.permu = permu;
            this.targetGenesList = targetGenesList;
            this.qfunctionList = qfunctionList;
            this.qactivity = qactivity;
            this.qsubstrate = qsubstrate;
            this.newSourceGeneIdToPositionMap = newSourceGeneIdToPositionMap;
            this.newTargetGeneIdToPositionMap = newTargetGeneIdToPositionMap;
            this.Yes = Yes;
            this.currentQueryGene = currentQueryGene;
            this.result = result;
        }

        public void ThreadLoop(String permu, List<ModuleGene> targetGenesList, List<String> qfunctionList, List<String> qactivity, List<Set<String>> qsubstrate,
                BiMap<Integer, Integer> newSourceGeneIdToPositionMap, BiMap<Integer, Integer> newTargetGeneIdToPositionMap, int Yes, int currentQueryGene, Multimap<Double, String> result) {

            myFunction sim = new myFunction();
            String[] values = permu.replace("[", "").replace("]", "").split(",");
            List<String> mergedTargetgenes = new ArrayList<String>();
            List<Integer> ToRemove = new ArrayList<Integer>();
            List<String> tactivity = new ArrayList<String>();
            List<Set<String>> tsubstrate = new ArrayList<Set<String>>();
            for (String j : values) {
                ToRemove.add(Integer.parseInt(j.trim()));
                for (Module m : targetGenesList.get(Integer.parseInt(j.trim()) - 1).getModule()) {
                    for (Domain i : m.getDomains()) {
                        mergedTargetgenes.add(i.getDomainFunctionString());
                        tactivity.add(i.getStatus().toString());
                        tsubstrate.add(i.getSubstrates());
                    }
                }
            }
            Multimap<Double, Multimap<String, Integer>> FunctionScores = sim.calculate(qfunctionList, mergedTargetgenes);
            Multimap<Double, Multimap<String, Integer>> activityscores = myFunction.calculate(qactivity, tactivity);
            Multimap<Double, Multimap<String, Integer>> substratescores = myFunction.calculate(getSubstrateList(qsubstrate), getSubstrateList(tsubstrate));
            Object FunctionScore = FunctionScores.asMap().keySet().toArray()[0];
            Object activityScore = activityscores.asMap().keySet().toArray()[0];
            Object substrateScore = substratescores.asMap().keySet().toArray()[0];

            double finalScore = Math.round((((2.9 * Double.parseDouble(FunctionScore.toString().trim())) + (0.05 * Double.parseDouble(activityScore.toString().trim())) + (0.05 * Double.parseDouble(substrateScore.toString().trim()))) / 3) * 100.0) / 100.0;
            String targetIdentified = permu.replace(",", "+");
            String ConvertedGeneIDs = "";
            // System.out.println(ConvertedGeneIDs+ "         " + ConvertedGeneIDs);
            if (Yes == 0) {
                ConvertedGeneIDs = reconstructWithGeneId(Integer.toString(currentQueryGene), newSourceGeneIdToPositionMap) + "->" + reconstructWithGeneId(targetIdentified.replace("[", "").replace("]", ""), newTargetGeneIdToPositionMap);
            } else {
                ConvertedGeneIDs = reconstructWithGeneId(targetIdentified.replace("[", "").replace("]", ""), newTargetGeneIdToPositionMap) + "->" + reconstructWithGeneId(Integer.toString(currentQueryGene), newSourceGeneIdToPositionMap);
            }
            // String ConvertedGeneIDs = reconstructWithGeneId(Integer.toString(currentQueryGene), newSourceGeneIdToPositionMap) + "->" + reconstructWithGeneId(targetIdentified.replace("[", "").replace("]", ""), newTargetGeneIdToPositionMap);
            //  System.out.println(permu + "          " + finalScore + "         " + ConvertedGeneIDs);

            result.put(finalScore, ConvertedGeneIDs);

            ScoreFunctionMatchMisMatch.putAll(ConvertedGeneIDs, FunctionScores.values());
            ScoreStatusMatchMisMatch.putAll(ConvertedGeneIDs, activityscores.values());
            ScoreSubstrateMatchMisMatch.putAll(ConvertedGeneIDs, substratescores.values());
        }

        @Override
        public void run() {
            ThreadLoop(permu, targetGenesList, qfunctionList, qactivity, qsubstrate,
                    newSourceGeneIdToPositionMap, newTargetGeneIdToPositionMap, Yes, currentQueryGene, result);
        }
    }

    public void update() {

    }

    public void SubsetMatchingOfPathways() {
        PathwayWithModules newSource = new PathwayWithModules(source);
        PathwayWithModules newTarget = new PathwayWithModules(target);
        BiMap<Integer, Integer> newSourceGeneIdToPositionMap = srcGeneIdToPositionMap;
        BiMap<Integer, Integer> newTargetGeneIdToPositionMap = tgtGeneIdToPositionMap;
        int Yes = 0;
        Multimap<Double, String> forward = SubsetsMatching(newSource, newTarget, newSourceGeneIdToPositionMap, newTargetGeneIdToPositionMap, Yes); // key: qgeneId, value: {score=tgenecombination;...}
        newSource = new PathwayWithModules(SimpleCompareWithModules.sourcecopy);
        newTarget = new PathwayWithModules(SimpleCompareWithModules.targetcopy);
        Yes = 1;
        Multimap<Double, String> reverse = SubsetsMatching(newTarget, newSource, newTargetGeneIdToPositionMap, newSourceGeneIdToPositionMap, Yes);

        TreeMultimap<Double, String> globalMap = TreeMultimap.create(Ordering.natural().reverse(), Ordering.natural());
        globalMap.putAll(forward);
        globalMap.putAll(reverse);
        //System.out.println(globalMap);
        bestResultMapping = GetBestFromGlobalMap(globalMap);
        // Calculations4SubsetBitScore
        System.out.println(bestResultMapping);

        Double Bitscore = Calculations4SubsetBitScore(bestResultMapping);
        int m = SimpleCompare.SizeofTargetPathwaysInDatabase;
        int n = SimpleCompare.SizeofQueryPathway;

        double eval = (m * n * kscore * (Math.exp(-(lambda * Bitscore))));
        System.out.println(Bitscore);

        String[] SourcePathwaysID = source.getPathwayId().split("_");
        String[] TargetPathwaysID = target.getPathwayId().split("_");
        System.out.println("*********************");

        // write result ot database for further use
        try {
            //  todatabase.WriteToDatabase(UniqueJobID, SourcePathwaysID[0].trim(), SourcePathwaysID[1].trim(), TargetPathwaysID[0].trim(), TargetPathwaysID[1].trim(), bestResultMapping.toString(), Bitscore, eval);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double Calculations4SubsetBitScore(Map<String, Map<String, Double>> bestResultMappings) {
        SmithWaterman_b sw = new SmithWaterman_b();
        int MatchingSubstrateCount = 0;
        int MismatchSubstrateCount = 0;
        int MatchingFunctionCount = 0;
        int MismatchFunctionCount = 0;

        Multiset<String> qfunction = LinkedHashMultiset.create();
        Multiset<String> tfunction = LinkedHashMultiset.create();
        List<Set<String>> qsubstrate = new ArrayList<Set<String>>();
        List<Set<String>> tsubstrate = new ArrayList<Set<String>>();
        String GeneKey = "";
        int FunctionMatch = 0, StatusMatch = 0, SubstrateMatch = 0;
        int FunctionMisMatch = 0, StatusMisMatch = 0, SubstrateMisMatch = 0;
        int TranspositionDomains = 0;
        for (Map.Entry<String, Map<String, Double>> i : bestResultMappings.entrySet()) {
            for (Map.Entry<String, Double> j : i.getValue().entrySet()) {

                GeneKey = i.getKey() + "->" + j.getKey();
            }
            Collection<Multimap<String, Integer>> Function = ScoreFunctionMatchMisMatch.get(GeneKey);
            for (Multimap<String, Integer> GetFunction : Function) {
                for (Map.Entry<String, Integer> Get : GetFunction.entries()) {
                    String[] points = Get.getKey().split("-");
                    FunctionMatch += Integer.parseInt(points[0].trim());
                    FunctionMisMatch += Integer.parseInt(points[1].trim());
                    TranspositionDomains += Get.getValue();
                    break;
                }
                break;
            }
            Collection<Multimap<String, Integer>> Status = ScoreStatusMatchMisMatch.get(GeneKey);
            for (Multimap<String, Integer> GetFunction : Status) {
                for (Map.Entry<String, Integer> Get : GetFunction.entries()) {
                    String[] points = Get.getKey().split("-");
                    StatusMatch += Integer.parseInt(points[0].trim());
                    StatusMisMatch += Integer.parseInt(points[1].trim());
                    break;
                }
                break;
            }
            Collection<Multimap<String, Integer>> Substrate = ScoreSubstrateMatchMisMatch.get(GeneKey);
            for (Multimap<String, Integer> GetFunction : Substrate) {
                for (Map.Entry<String, Integer> Get : GetFunction.entries()) {
                    String[] points = Get.getKey().split("-");
                    SubstrateMatch += Integer.parseInt(points[0].trim());
                    SubstrateMisMatch += Integer.parseInt(points[1].trim());
                    break;
                }
                break;
            }
        }

        if (FunctionMisMatch == 0) {

        }
        //////System.out.println(MismatchCount + "                 " + MatchingCount);
        int rawFunctionScore = ((FunctionMatch * 3) - (FunctionMisMatch * 2) - TranspositionDomains);
        int rawStatusScore = (StatusMatch * 3) - (StatusMisMatch * 2);
        int rawSubstrateScore = (SubstrateMatch * 3) - (SubstrateMisMatch * 2);

        double SubstrateBitScore = (((lambda * rawSubstrateScore) + Math.log(kscore)) / Math.log(2));
        double StatusBitScore = (((lambda * rawStatusScore) + Math.log(kscore)) / Math.log(2));
        double FunctionBitScore = (((lambda * rawFunctionScore) + Math.log(kscore)) / Math.log(2));
        Double BitScore = SubstrateBitScore + StatusBitScore + FunctionBitScore;
        ////System.out.println(rawFunctionScore + "                " + MatchingSubstrateCount + "              " + BitScore);
        ScoreFunctionMatchMisMatch.clear();
        ScoreStatusMatchMisMatch.clear();
        ScoreSubstrateMatchMisMatch.clear();
        return BitScore;
    }

    private List<String> getSubstrateList(List<Set<String>> SubstrateList) {
        List<String> SubstrateListStrings = new ArrayList<String>();
        // String SubstrateListString = "";
        for (int i = 0; i < SubstrateList.size(); i++) {
            SubstrateListStrings.add(SubstrateList.get(i).toString());
        }
        //     SubstrateListStrings.add(SubstrateListString);

        return SubstrateListStrings;
    }

    public Map<String, Map<String, Double>> GetBestFromGlobalMap(TreeMultimap<Double, String> globalMap) {
        Map<String, Double> matchingInTarget;
        Set<String> queryGenesCovered = new HashSet<String>();
        Set<String> targetGenesCovered = new HashSet<String>();
        Map<String, Map<String, Double>> currentBestResultMapping = new TreeMap<String, Map<String, Double>>();

        for (Map.Entry<Double, String> entry : globalMap.entries()) {
            double score = entry.getKey();
            //score=[alignment1, aligment2, ..]
            String alignment = entry.getValue();
            //	//System.out.println(alignment+" dws");
            String bestScoreAlignment = alignment.split(",")[0];
            // start->end

            String start = bestScoreAlignment.split("->")[0];
            String end = bestScoreAlignment.split("->")[1];

            // start and end can be combination of genes
            Set<String> s = new HashSet<String>();
            Set<String> t = new HashSet<String>(Arrays.asList((end + "+").replace("[", "").replace("]", "").split("\\+")));
            String[] p = (start + "+").replace("[", "").replace("]", "").split("\\+");
            String[] q = (end + "+").replace("[", "").replace("]", "").split("\\+");
            for (String i : p) {
                s.add(i.trim());
            }
            for (String i : q) {
                t.add(i.trim());
            }
            // add to result mapping
            Set<String> sIntersection = new HashSet<String>();
            sIntersection.addAll(queryGenesCovered);
            sIntersection.retainAll(s);

            Set<String> tIntersection = new HashSet<String>();
            tIntersection.addAll(targetGenesCovered);
            tIntersection.retainAll(t);

            if (sIntersection.isEmpty() && tIntersection.isEmpty()) {
                matchingInTarget = new HashMap<String, Double>();
                matchingInTarget.put(end, score);
                currentBestResultMapping.put(start, matchingInTarget);
                for (String j : s) {
                    queryGenesCovered.add(j.trim());
                }
                for (String j : t) {
                    targetGenesCovered.add(j.trim());
                }
            }
        }
        return currentBestResultMapping;
    }

    /**
     * @return the finalscore
     */
    public static double getFinalscore() {
        return finalscore;
    }

    /**
     * @param aFinalscore the finalscore to set
     */
    public static void setFinalscore(double aFinalscore) {
        finalscore = aFinalscore;
    }
}
