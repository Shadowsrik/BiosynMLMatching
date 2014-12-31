/*
 * To change this template, choose Tools | Templates
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
import de.hzi.helmholtz.Genes.Gene;
import de.hzi.helmholtz.Genes.GeneSimilarity;
import de.hzi.helmholtz.Pathways.Pathway;
import de.hzi.helmholtz.Writers.WriteResultToDatabase;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author srdu001
 */
public class PathwayComparison {

    private static double finalscore = 0;
    static double s_add = 1.0, s_penality = -1.0;
    static double SCORE_MISMATCH_ERROR = 0.2f;
    static double DISTANCE_MISMATCH_ERROR = 1;
    JFrame frame;
    //Map<Integer, List<String>> Qmap = new HashMap<Integer, List<String>>();
    //Map<Integer, List<String>> Tmap = new HashMap<Integer, List<String>>();
    private Pathway source;
    private Pathway target;
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
    static double maxbitscore = 0;
    private String UniqueJobID = "";
    WriteResultToDatabase todatabase = new WriteResultToDatabase();
    int self = 0;

    public PathwayComparison(final Pathway source, final Pathway target, int windowsize, String ProcessId, String ALGORITHM, int self) {
        try {
            this.source = source;
            this.target = target;
            maxWindowSize = windowsize;
            constructBiMaps();
            this.self = self;
             System.out.println(source.toString());
             System.out.println(target.toString());
            UniqueJobID = ProcessId;
            String[] SourcePathwaysID = source.getPathwayId().split("_");
            if (ALGORITHM.equals("0")) {
                System.out.println("Started pathwayComparisonGlobalBestGreedy  for " + source.getPathwayId() + " and " + target.getPathwayId() + "...");
                pathwayComparisonGlobalBestGreedy();
                if (self == 1) {
                    todatabase.maxScoreWriting(UniqueJobID,(SourcePathwaysID[0].trim()), maxbitscore);
                }
            } else if (ALGORITHM.equals("1")) {
                System.out.println("Started pathwayComparisonGlobalBestCoalesce for " + source.getPathwayId() + " and " + target.getPathwayId() + "...");
                pathwayComparisonGlobalBestCoalesce();
                if (self == 1) {
                    todatabase.maxScoreWriting(UniqueJobID, (SourcePathwaysID[0].trim()), maxbitscore);
                }
            } else {
                System.out.println("Started SubsetMatchingOfPathways for " + source.getPathwayId() + " and " + target.getPathwayId() + "...");
                try {
                    SubsetMatchingOfPathways();
                    if (self == 1) {
                        todatabase.maxScoreWriting(UniqueJobID,(SourcePathwaysID[0].trim()), maxbitscore);
                    }

                } catch (Exception ds) {
                    System.out.println("Error in SubsetMatchingOfPathways:" + ds);
                }
            }
            // frame = new JFrame("Matching");
        } catch (Exception ds) {
            System.out.println("Error in PathwayComparison:" + ds);
        }
    }

    /* Construct position to geneId bimaps of both source and target pathways */
    private void constructBiMaps() {
        srcGeneIdToPositionMap = HashBiMap.create();
        int temp = 1;
        for (Gene e : source.getGenes()) {
            srcGeneIdToPositionMap.put(e.getGeneId(), temp++);
        }
        tgtGeneIdToPositionMap = HashBiMap.create();
        temp = 1;
        for (Gene e : target.getGenes()) {
            tgtGeneIdToPositionMap.put(e.getGeneId(), temp++);
        }
    }

    /* Iterate over source genes
     * Compare each source gene against every target gene
     * Keep the comparison scores in forwardScores
     * Keep the best forward scores for gene-gene in forwardBestScores
     * Do the same in the reverse direction
     */
    public Multimap<Integer, Multimap<Double, String>> pcompare(Pathway firstPathway, Pathway secondPathway) {

        Multimap<Double, String> forwardScores = ArrayListMultimap.create();
        Multimap<Integer, Multimap<Double, String>> forwardBestScores = ArrayListMultimap.create();

        int currentQueryGene = 0;
        Iterator<Gene> sourceGeneIt = firstPathway.geneIterator(); // for reverse scoring, this becomes targetGeneIt
        while (sourceGeneIt.hasNext()) {
            Gene queryGene = sourceGeneIt.next();
            // clear forward scores after one gene is done
            forwardScores.clear();

            Iterator<Gene> targetGeneIt = secondPathway.geneIterator();
            int currentTargetGene = 0;
            while (targetGeneIt.hasNext()) {
                Gene targetGene = targetGeneIt.next();
                GeneSimilarity geneSimilarity = new GeneSimilarity();
                if ((queryGene.size() <= targetGene.size())) {
                    // Match one source gene against one target gene with the same index
                    List<Gene> targetGenes = new ArrayList<Gene>();
                    targetGenes.add(targetGene);
                    double score = geneSimilarity.levenshteinSimilarity(queryGene, targetGenes, functionWeight, statusWeight, substrateWeight);
                    forwardScores.put(score, currentTargetGene + "");
                } else if (queryGene.size() > targetGene.size()) {
                    // Merge multiple target genes and compare to one source gene
                    // store scores for windows of all sizes upto maxWindowSize
                    for (int currentWindowSize = 0; currentWindowSize < maxWindowSize; currentWindowSize++) {
                        if (currentTargetGene + currentWindowSize < secondPathway.size()) {
                            // construct list of target genes to compare, list size = currentWindowSize
                            List<Gene> mergedGenes = new ArrayList<Gene>();
                            List<Gene> targetGenesList = secondPathway.getGenes();
                            for (int i = currentTargetGene; i <= currentTargetGene + currentWindowSize; i++) {
                                mergedGenes.add(targetGenesList.get(i));
                            }
                            double score = geneSimilarity.levenshteinSimilarity(queryGene, mergedGenes, functionWeight, statusWeight, substrateWeight);
                            if (score > 0) {
                                String combinedGenes = "";
                                for (int i = currentTargetGene; i <= currentTargetGene + currentWindowSize; i++) {
                                    combinedGenes += i + "+";
                                }
                                combinedGenes = combinedGenes.substring(0, combinedGenes.length() - 1);
                                forwardScores.put(Math.abs(score), combinedGenes);
                            } else {
                                String combinedGenes = "";
                                for (int i = currentTargetGene + currentWindowSize; i >= currentTargetGene; i--) {
                                    combinedGenes += i + "+";
                                }
                                combinedGenes = combinedGenes.substring(0, combinedGenes.length() - 1);
                                forwardScores.put(Math.abs(score), combinedGenes);
                            }
                        }
                    }
                }
                currentTargetGene++;
            }
            Multimap<Double, String> forwardscore1 = ArrayListMultimap.create(forwardScores);
            TreeMultimap<Double, String> forwardscore = TreeMultimap.create(Ordering.natural().reverse(), Ordering.natural());
            forwardscore.putAll(forwardscore1);
            forwardBestScores.put(currentQueryGene, forwardscore);
            currentQueryGene++;
        }
        // ////System.out.println("  dsds  "+forwardBestScores);
        return forwardBestScores;
    }

    /*
     * Obtains the higher of the scores of matching genes in forward direction or reverse direction on per gene basis
     */
    public String getmax(Collection<Multimap<Double, String>> collection, Multimap<Integer, Multimap<Double, String>> reverse) {
        String geneToBeCompared = "";
        String maxScoringGene = ""; // can be one gene or combination of many genes
        double max = 0;
        boolean isGenePresent = true;

        if (collection.size() > 0) {
            while (isGenePresent) {
                for (Multimap<Double, String> gene : collection) {
                    try {
                        max = gene.keySet().iterator().next();
                        finalscore = max;
                        for (String geneCombinationWithBestScore : gene.get(max)) {
                            if (geneCombinationWithBestScore.contains("+")) {
                                String[] individualGenes = geneCombinationWithBestScore.split("\\+");
                                for (String individualGene : individualGenes) {
                                    if (reverse.containsKey(Integer.parseInt(individualGene))) {
                                        if (geneToBeCompared.equals("")) {
                                            geneToBeCompared = individualGene;
                                        } else {
                                            geneToBeCompared += "+" + individualGene;
                                        }
                                    }
                                }
                            } else {
                                if (reverse.containsKey(Integer.parseInt(geneCombinationWithBestScore))) {
                                    if (geneToBeCompared.equals("")) {
                                        geneToBeCompared = geneCombinationWithBestScore;
                                    } else {
                                        geneToBeCompared += ";" + geneCombinationWithBestScore;
                                    }
                                }
                            }
                        }
                        if (geneToBeCompared.trim().equals("")) {
                            gene.asMap().remove(max);
                        }
                    } catch (Exception ds) {
                        ds.printStackTrace();
                    }
                }
                if (!geneToBeCompared.trim().equals("")) {
                    isGenePresent = false;
                }
                double roundOff = (double) Math.round(max * 100) / 100; // set score precision to two decimal places
                maxScoringGene = roundOff + "=" + geneToBeCompared;
            }
        }
        return maxScoringGene;
    }

    public static void removemax(Multimap<Integer, Multimap<Double, String>> collections, int key) {
        collections.asMap().remove(key);
    }

    /* Utility function to transform 1+2 to geneid(1)+geneid(2) */
    public String reconstructWithGeneId(String positionIdStr, BiMap<Integer, Integer> newGeneIdToPositionMap) {
        String geneIdStr = "";
        String[] positions = positionIdStr.split("\\+");
        Arrays.sort(positions);
        for (String position : positions) {
            int pos = Integer.parseInt(position.trim());
            geneIdStr += newGeneIdToPositionMap.inverse().get(pos) + "+";
        }
        geneIdStr = geneIdStr.substring(0, geneIdStr.length() - 1);
        return geneIdStr;
    }

    public void pathwayComparisonGlobalBestGreedy() {
        Multimap<Integer, Multimap<Double, String>> forward = pcompare(source, target); // key: qgeneId, value: {score=tgenecombination;...}
        Multimap<Integer, Multimap<Double, String>> reverse = pcompare(target, source);

        /* Create global list of matchings ordered by score by joining forward and reverse lists
         * key: querygene -> targetgenes
         * value: score
         */
        TreeMultimap<Double, String> globalMap = TreeMultimap.create(Ordering.natural().reverse(), Ordering.natural());
        for (Map.Entry<Integer, Multimap<Double, String>> e : forward.entries()) {
            int fgene = e.getKey();
            Multimap<Double, String> geneAndScore = e.getValue();
            for (Map.Entry<Double, String> scoreEntry : geneAndScore.entries()) {
                double score = scoreEntry.getKey();
                String matchingGeneString = scoreEntry.getValue();
                String[] multipleMatchingGenes = matchingGeneString.split(",");
                for (String matchingGene : multipleMatchingGenes) {
                    String newKey = fgene + "->" + matchingGene;
                    globalMap.put(score, newKey);
                }
            }
        }
        for (Map.Entry<Integer, Multimap<Double, String>> e : reverse.entries()) {
            int rgene = e.getKey();
            Multimap<Double, String> geneAndScore = e.getValue();
            for (Map.Entry<Double, String> scoreEntry : geneAndScore.entries()) {
                double score = scoreEntry.getKey();
                String matchingGeneString = scoreEntry.getValue();
                String[] multipleMatchingGenes = matchingGeneString.split(",");
                for (String matchingGene : multipleMatchingGenes) {
                    String newKey = matchingGene + "->" + rgene;
                    globalMap.put(score, newKey);
                }
            }
        }

        // create alignment
        //  //////System.out.println(globalMap);
        bestResultMapping = new TreeMap<String, Map<String, Double>>();
        Map<String, Double> matchingInTarget;
        Set<String> queryGenesCovered = new HashSet<String>();
        Set<String> targetGenesCovered = new HashSet<String>();

        for (Map.Entry<Double, String> entry : globalMap.entries()) {
            double score = entry.getKey();
            //score=[alignment1, aligment2, ..]
            String alignment = entry.getValue();

            String bestScoreAlignment = alignment.split(",")[0];
            // start->end
            String start = bestScoreAlignment.split("->")[0];
            String end = bestScoreAlignment.split("->")[1];

            // start and end can be combination of genes
            Set<String> s = new HashSet<String>(Arrays.asList((start + "+").split("\\+")));
            Set<String> t = new HashSet<String>(Arrays.asList((end + "+").split("\\+")));

            // add to result mapping
            Set<String> sIntersection = new HashSet<String>();
            sIntersection.addAll(queryGenesCovered);
            sIntersection.retainAll(s);

            Set<String> tIntersection = new HashSet<String>();
            tIntersection.addAll(targetGenesCovered);
            tIntersection.retainAll(t);

            if (sIntersection.isEmpty() && tIntersection.isEmpty()) {
                matchingInTarget = new HashMap<String, Double>();
                matchingInTarget.put(reconstructWithGeneId(end, tgtGeneIdToPositionMap), score);
                bestResultMapping.put(reconstructWithGeneId(start, srcGeneIdToPositionMap), matchingInTarget);
                queryGenesCovered.addAll(s);
                targetGenesCovered.addAll(t);
            }
        }
        String[] SourcePathwaysID = source.getPathwayId().split("_");
        String[] TargetPathwaysID = target.getPathwayId().split("_");
        //////System.out.println(bestResultMapping);
        Double Bitscore = Calculations4BitScore(bestResultMapping);
        int m = SimpleCompare.SizeofTargetPathwaysInDatabase;
        int n = SimpleCompare.SizeofQueryPathway;

        Double modifiedBitscore = Bitscore / 5;
        NumberFormat formatter;
        Double value = m * n * kscore * Math.exp((-(lambda * modifiedBitscore)));
        formatter = new DecimalFormat("0.##E0");
        String eval = formatter.format(value);
        System.out.println(eval + "           " + Bitscore);
        // write result ot database for further use
        maxbitscore = Bitscore;
        // write result ot database for further use
        try {
            if (self != 1) {
                todatabase.WriteToDatabase(UniqueJobID, SourcePathwaysID[0].trim(), SourcePathwaysID[1].trim(), TargetPathwaysID[0].trim(), TargetPathwaysID[1].trim(), bestResultMapping.toString(), Bitscore, eval);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //     JOptionPane.showMessageDialog(null, bestResultMapping,new Throwable().getStackTrace()[0].getLineNumber() + " sdsdsd321321", JOptionPane.INFORMATION_MESSAGE);
        // //////System.out.println(bestResultMapping);
    }

    public Multimap<Double, String> SubsetsMatching(final Pathway firstPathway, final Pathway secondPathway, BiMap<Integer, Integer> newSourceGeneIdToPositionMap, BiMap<Integer, Integer> newTargetGeneIdToPositionMap, int Yes) {
        Multimap<Double, String> resultPerfect = TreeMultimap.create(Ordering.natural().reverse(), Ordering.natural());
        Pathway firstPathwayCopy = new Pathway(firstPathway);// Copy of the Query pathway
        Pathway secondPathwayCopy = new Pathway(secondPathway);// Copy of the Target pathway'
        // Pathway secondPathwayCopy1 = new Pathway(secondPathway);
        int currentQueryGene = 0;
        Iterator<Gene> sourceGeneIt = firstPathway.geneIterator();
        List<Integer> QueryToRemove = new ArrayList<Integer>();
        List<Integer> TargetToRemove = new ArrayList<Integer>();
        while (sourceGeneIt.hasNext()) {
            currentQueryGene++;
            Gene queryGene = sourceGeneIt.next();

            int currentTargetGene = 0;
            Multiset<String> qfunction = LinkedHashMultiset.create();
            List<String> qfunctionList = new ArrayList<String>();
            List<String> qactivity = new ArrayList<String>();
            List<Set<String>> qsubstrate = new ArrayList<Set<String>>();
            for (Domain d : queryGene.getDomains()) {
                qfunction.add(d.getDomainFunctionString());
                qfunctionList.add(d.getDomainFunctionString());
                qactivity.add(d.getStatus().toString());
                qsubstrate.add(d.getSubstrates());
            }
            Iterator<Gene> targetGeneIt = secondPathway.geneIterator();

            while (targetGeneIt.hasNext()) {
                currentTargetGene++;
                Gene targetGene = targetGeneIt.next();
                Multiset<String> tfunction = LinkedHashMultiset.create();
                List<String> tfunctionList = new ArrayList<String>();
                List<String> tactivity = new ArrayList<String>();
                List<Set<String>> tsubstrate = new ArrayList<Set<String>>();
                for (Domain d : targetGene.getDomains()) {
                    tfunctionList.add(d.getDomainFunctionString());
                    tfunction.add(d.getDomainFunctionString());
                    tactivity.add(d.getStatus().toString());
                    tsubstrate.add(d.getSubstrates());
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
            secondPathwayCopy.removeGene(Integer.parseInt(reconstructWithGeneId(Integer.toString(i), newTargetGeneIdToPositionMap)));
        }
        for (int i : QueryToRemove) {
            firstPathwayCopy.removeGene(Integer.parseInt(reconstructWithGeneId(Integer.toString(i), newSourceGeneIdToPositionMap)));
        }
        if (firstPathwayCopy.size() > 0 && secondPathwayCopy.size() > 0) {
            // Re-construct the bimaps
            newSourceGeneIdToPositionMap = HashBiMap.create();
            int temp = 0;
            for (Gene e : firstPathwayCopy.getGenes()) {
                temp = temp + 1;
                newSourceGeneIdToPositionMap.put(e.getGeneId(), temp);
            }
            newTargetGeneIdToPositionMap = HashBiMap.create();
            temp = 0;
            for (Gene e : secondPathwayCopy.getGenes()) {
                temp = temp + 1;
                newTargetGeneIdToPositionMap.put(e.getGeneId(), temp);
            }
            resultPerfect.putAll(SubsetIdentification(firstPathwayCopy, secondPathwayCopy, newSourceGeneIdToPositionMap, newTargetGeneIdToPositionMap, Yes));
        }
        ////System.out.println(resultPerfect);
        return resultPerfect;
    }

    public Multimap<Double, String> SubsetIdentification(Pathway firstPathway, Pathway secondPathway, BiMap<Integer, Integer> newSourceGeneIdToPositionMap, BiMap<Integer, Integer> newTargetGeneIdToPositionMap, int Yes) {
        Multimap<Double, String> result = TreeMultimap.create(Ordering.natural().reverse(), Ordering.natural());

        Iterator<Gene> sourceGeneIt = firstPathway.geneIterator();
        int currentQueryGene = 0;
        while (sourceGeneIt.hasNext()) {
            currentQueryGene++;
            Gene queryGene = sourceGeneIt.next();
            Multimap<Integer, String> resultr = TreeMultimap.create(Ordering.natural(), Ordering.natural());
            int currentTargetGene = 0;
            Multiset<String> qfunction = LinkedHashMultiset.create();
            List<String> qfunctionList = new ArrayList<String>();
            List<String> qactivity = new ArrayList<String>();
            List<Set<String>> qsubstrate = new ArrayList<Set<String>>();
            for (Domain d : queryGene.getDomains()) {
                qfunction.add(d.getDomainFunctionString());
                qfunctionList.add(d.getDomainFunctionString());
                qactivity.add(d.getStatus().toString());
                qsubstrate.add(d.getSubstrates());
            }
            List<String> TargenesSelected = new ArrayList<String>();
            Iterator<Gene> targetGeneIt = secondPathway.geneIterator();
            while (targetGeneIt.hasNext()) {
                currentTargetGene++;
                Gene targetGene = targetGeneIt.next();
                Multiset<String> tfunction = LinkedHashMultiset.create();
                List<String> tactivity = new ArrayList<String>();
                List<Set<String>> tsubstrate = new ArrayList<Set<String>>();
                List<String> tfunctionList = new ArrayList<String>();
                Iterator<Domain> dIter = targetGene.domainIterator();
                while (dIter.hasNext()) {
                    Domain d = dIter.next();
                    tfunction.add(d.getDomainFunctionString());
                    tfunctionList.add(d.getDomainFunctionString());
                    tactivity.add(d.getStatus().toString());
                    tsubstrate.add(d.getSubstrates());
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
                int tsize = 0;
                if ((firstPathway.size() >= 8) && (secondPathway.size() >= 8)) {
                    tsize = 5;
                } else if ((firstPathway.size() >= 8) || (secondPathway.size() >= 8)) {
                    tsize = 6;
                } else {
                    tsize = 8;
                }
                while (TargenesSelected.size() < tsize) {
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
            // ////System.out.println(TargenesSelected);
            //  Permutation perm = new Permutation();
            //  List<String> perms = perm.run(TargenesSelected);
            CombinationGenerator c = new CombinationGenerator(10, 10);
            List<String> perms = c.GenerateAllPossibleCombinations(TargenesSelected);
            myFunction sim = new myFunction();
            double score = 0;
            String targetIdentified = "";
            List<Gene> targetGenesList = secondPathway.getGenes();
            for (String permu : perms) {
                String[] values = permu.replace("[", "").replace("]", "").split(",");
                List<String> mergedTargetgenes = new ArrayList<String>();
                List<Integer> ToRemove = new ArrayList<Integer>();
                List<String> tactivity = new ArrayList<String>();
                List<Set<String>> tsubstrate = new ArrayList<Set<String>>();
                for (String j : values) {
                    ToRemove.add(Integer.parseInt(j.trim()));
                    for (Domain i : targetGenesList.get(Integer.parseInt(j.trim()) - 1).getDomains()) {

                        mergedTargetgenes.add(i.getDomainFunctionString());
                        tactivity.add(i.getStatus().toString());
                        tsubstrate.add(i.getSubstrates());
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

    static void findMissingNumbers(int[] a, int first, int last) {
        // before the array: numbers between first and a[0]-1
        for (int i = first; i < a[0]; i++) {
            System.out.println(i);
        }
        // inside the array: at index i, a number is missing if it is between a[i-1]+1 and a[i]-1
        for (int i = 1; i < a.length; i++) {
            for (int j = 1 + a[i - 1]; j < a[i]; j++) {
                System.out.println(j);
            }
        }
        // after the array: numbers between a[a.length-1] and last
        for (int i = 1 + a[a.length - 1]; i <= last; i++) {
            System.out.println(i);
        }
    }

    public void SubsetMatchingOfPathways() {

        Pathway newSource = new Pathway(source);
        Pathway newTarget = new Pathway(target);
        BiMap<Integer, Integer> newSourceGeneIdToPositionMap = srcGeneIdToPositionMap;
        BiMap<Integer, Integer> newTargetGeneIdToPositionMap = tgtGeneIdToPositionMap;
        int Yes = 0;
        Multimap<Double, String> forward = SubsetsMatching(newSource, newTarget, newSourceGeneIdToPositionMap, newTargetGeneIdToPositionMap, Yes); // key: qgeneId, value: {score=tgenecombination;...}
        newSource = new Pathway(SimpleCompare.sourcecopy);
        if (self == 1) {
            newTarget = new Pathway(SimpleCompare.sourcecopy);
        } else {
            newTarget = new Pathway(SimpleCompare.targetcopy);
        }
        Yes = 1;
        Multimap<Double, String> reverse = SubsetsMatching(newTarget, newSource, newTargetGeneIdToPositionMap, newSourceGeneIdToPositionMap, Yes);

        TreeMultimap<Double, String> globalMap = TreeMultimap.create(Ordering.natural().reverse(), Ordering.natural());
        globalMap.putAll(forward);
        globalMap.putAll(reverse);
        System.out.println(globalMap);
        bestResultMapping = GetBestFromGlobalMap(globalMap);
        // Calculations4SubsetBitScore
          System.out.println(bestResultMapping);

        Double Bitscore = Calculations4SubsetBitScore(bestResultMapping);
        int m = SimpleCompare.SizeofTargetPathwaysInDatabase;
        int n = SimpleCompare.SizeofQueryPathway;

        Double modifiedBitscore = Bitscore / 5;
        NumberFormat formatter;
        Double value = m * n * kscore * Math.exp((-(lambda * modifiedBitscore)));
        formatter = new DecimalFormat("0.##E0");
        String eval = formatter.format(value);
        System.out.println(eval + "           " + Bitscore);
        maxbitscore = Bitscore;
        String[] SourcePathwaysID = source.getPathwayId().split("_");
        String[] TargetPathwaysID = target.getPathwayId().split("_");
        // write result ot database for further use
        try {
            if (self != 1) {
                todatabase.WriteToDatabase(UniqueJobID, SourcePathwaysID[0].trim(), SourcePathwaysID[1].trim(), TargetPathwaysID[0].trim(), TargetPathwaysID[1].trim(), bestResultMapping.toString(), Bitscore, eval);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    float exponential(int n, float x) {
        float sum = 1.0f; // initialize sum of series

        for (int i = n - 1; i > 0; --i) {
            sum = 1 + x * sum / i;
        }

        return sum;
    }

    public void pathwayComparisonGlobalBestCoalesce() {
        bestResultMapping = new TreeMap<String, Map<String, Double>>();;
        Map<String, Map<String, Double>> currentBestResultMapping;

        Pathway newSource = new Pathway(source);
        Pathway newTarget;
        if (self == 1) {
            newTarget = new Pathway(SimpleCompare.sourcecopy);
        } else {
            newTarget = new Pathway(SimpleCompare.targetcopy);
        }
        BiMap<Integer, Integer> newSourceGeneIdToPositionMap = srcGeneIdToPositionMap;
        BiMap<Integer, Integer> newTargetGeneIdToPositionMap = tgtGeneIdToPositionMap;

        while (newSource.size() > 0 && newTarget.size() > 0) {

            // find current best mapping
            currentBestResultMapping = pathwayComparisonCoalesce(newSource, newTarget, newSourceGeneIdToPositionMap, newTargetGeneIdToPositionMap);

            // remove already mapped genes in source and target
            for (Map.Entry<String, Map<String, Double>> entry : currentBestResultMapping.entrySet()) {
                String queryGenesCovered = entry.getKey();
                String[] qGenesCovered = queryGenesCovered.split("\\+");
                for (String qGenesCovered1 : qGenesCovered) {
                    int qGeneCovered = Integer.parseInt(qGenesCovered1);
                    newSource.removeGene(qGeneCovered);
                }
                for (Map.Entry<String, Double> tEntry : entry.getValue().entrySet()) {
                    String targetGenesCovered = tEntry.getKey();
                    String[] tGenesCovered = targetGenesCovered.split("\\+");
                    for (String tGenesCovered1 : tGenesCovered) {
                        int tGeneCovered = Integer.parseInt(tGenesCovered1);
                        newTarget.removeGene(tGeneCovered);
                    }
                }
            }

            bestResultMapping.putAll(currentBestResultMapping);
        }
        String[] SourcePathwaysID = source.getPathwayId().split("_");
        String[] TargetPathwaysID = target.getPathwayId().split("_");
        ////System.out.println("*********************");
        System.out.println(bestResultMapping);
        Double Bitscore = Calculations4BitScore(bestResultMapping);
        int m = SimpleCompare.SizeofTargetPathwaysInDatabase;
        int n = SimpleCompare.SizeofQueryPathway;

        Double modifiedBitscore = Bitscore / 5;
        NumberFormat formatter;
        Double value = m * n * kscore * Math.exp((-(lambda * modifiedBitscore)));
        formatter = new DecimalFormat("0.##E0");
        String eval = formatter.format(value);
        System.out.println(eval + "           " + Bitscore);

        maxbitscore = Bitscore;
        // write result ot database for further use
        try {
            if (self != 1) {
                //todatabase.WriteToDatabase(UniqueJobID, SourcePathwaysID[0].trim(), SourcePathwaysID[1].trim(), TargetPathwaysID[0].trim(), TargetPathwaysID[1].trim(), bestResultMapping.toString(), Bitscore, eval);
            }
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
        int unmatchedDomains = 0;
        double mismatchedDomains = 0.0;
        if (target.size() > 0) {
            Iterator<Gene> targetGeneIt = target.geneIterator();
            while (targetGeneIt.hasNext()) {
                Gene targetGene = targetGeneIt.next();
                unmatchedDomains = unmatchedDomains + targetGene.getDomains().size();
            }
        }
        if (source.size() > 0) {
            Iterator<Gene> targetGeneIt = source.geneIterator();
            while (targetGeneIt.hasNext()) {
                Gene targetGene = targetGeneIt.next();
                unmatchedDomains = unmatchedDomains + targetGene.getDomains().size();
            }
        }
        unmatchedDomains = (unmatchedDomains) / 7;
        ////////System.out.println(MismatchCount + "                 " + MatchingCount);
        int rawFunctionScore = ((FunctionMatch * 3) - (FunctionMisMatch * 2)  - unmatchedDomains);
        int rawStatusScore = (StatusMatch * 3) - (StatusMisMatch * 2);
        int rawSubstrateScore = (SubstrateMatch * 3) - (SubstrateMisMatch * 2);

        double SubstrateBitScore = (((lambda * (rawSubstrateScore)) + Math.log(kscore)) / Math.log(2));
        double StatusBitScore = (((lambda * (rawStatusScore)) + Math.log(kscore)) / Math.log(2));
        double FunctionBitScore = (((lambda * rawFunctionScore) + Math.log(kscore)) / Math.log(2));
        Double BitScore = (SubstrateBitScore*0.3) + (StatusBitScore*0.2) + (FunctionBitScore);

       System.out.println(rawFunctionScore + "                " + rawSubstrateScore + "              " +rawStatusScore + "                " + BitScore);
        ScoreFunctionMatchMisMatch.clear();
        ScoreStatusMatchMisMatch.clear();
        ScoreSubstrateMatchMisMatch.clear();
        return BitScore;
    }

    public double Calculations4BitScore(Map<String, Map<String, Double>> bestResultMappings) {
        SmithWaterman_b sw = new SmithWaterman_b();
        int MatchingSubstrateCount = 0;
        int MismatchSubstrateCount = 0;
        int MatchingFunctionCount = 0;
        int MismatchFunctionCount = 0;

        List<String> qfunction = new ArrayList<String>();
        List<String> tfunction = new ArrayList<String>();
        List<Set<String>> qsubstrate = new ArrayList<Set<String>>();
        List<Set<String>> tsubstrate = new ArrayList<Set<String>>();
        for (Map.Entry<String, Map<String, Double>> i : bestResultMappings.entrySet()) {
            qfunction.clear();
            tfunction.clear();
            qsubstrate.clear();
            tsubstrate.clear();
            if (i.getKey().contains("+")) {
                String[] queryGeneIDs = i.getKey().split("\\+");
                for (String geneID : queryGeneIDs) {
                    Gene gene = SimpleCompare.sourcecopy.getGenes().get(srcGeneIdToPositionMap.get(Integer.parseInt(geneID.trim())) - 1);
                    ////////System.out.println(source.getGenes().get(Integer.parseInt(geneID.trim())));
                    for (Domain domain : gene.getDomains()) {
                        qfunction.add(domain.getDomainFunctionString());
                        qsubstrate.add(domain.getSubstrates());
                    }
                }
            } else {
                // //////System.out.println(SimpleCompare.sourcecopy);
                Gene f = SimpleCompare.sourcecopy.getGenes().get(srcGeneIdToPositionMap.get(Integer.parseInt(i.getKey().trim())) - 1);
                for (Domain j : f.getDomains()) {
                    qfunction.add(j.getDomainFunctionString());
                    qsubstrate.add(j.getSubstrates());
                }
            }
            for (Map.Entry<String, Double> j : i.getValue().entrySet()) {
                if (j.getKey().contains("+")) {
                    String[] targetGeneIDs = j.getKey().split("\\+");
                    for (String geneID : targetGeneIDs) {
                        Gene gene = SimpleCompare.targetcopy.getGenes().get(tgtGeneIdToPositionMap.get(Integer.parseInt(geneID.trim())) - 1);
                        //   //////System.out.println(source.getGenes().get(Integer.parseInt(geneID.trim())));
                        for (Domain domain : gene.getDomains()) {
                            tfunction.add(domain.getDomainFunctionString());
                            tsubstrate.add(domain.getSubstrates());
                        }
                    }
                } else {

                    Gene gene = SimpleCompare.targetcopy.getGenes().get(tgtGeneIdToPositionMap.get(Integer.parseInt(j.getKey().trim())) - 1);
                    for (Domain domain : gene.getDomains()) {
                        tfunction.add(domain.getDomainFunctionString());
                        tsubstrate.add(domain.getSubstrates());
                    }
                }
                // String[] targetGeneIDs = i.getValue()

            }

            sw.init(qfunction, tfunction);
            sw.process();
            sw.backtrack();
            MatchingFunctionCount += sw.countlet;
            MismatchFunctionCount += sw.countdash;
            SmithWaterman_b sw1 = new SmithWaterman_b();
            sw1.init(getSubstrateList(qsubstrate), getSubstrateList(tsubstrate));
            sw1.process();
            sw1.backtrack();
            sw1.printScoreAndAlignments();
            MatchingSubstrateCount += sw1.scoring();

        }

        ////////System.out.println(MismatchCount + "                 " + MatchingCount);
        int rawFunctionScore = (MatchingFunctionCount * 3) - (MismatchFunctionCount * 2);
        // int rawSubstrateScore =  (MatchingSubstrateCount * 1) - (MismatchSubstrateCount * 1);
        //int rawScore = rawFunctionScore + rawSubstrateScore;

        double BitScore = (((lambda * rawFunctionScore) + Math.log(kscore)) / Math.log(2)) + MatchingSubstrateCount;

        ////System.out.println(rawFunctionScore + "                " + MatchingSubstrateCount + "              " + BitScore);
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
            //	////System.out.println(alignment+" dws");
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

    public Map<String, Map<String, Double>> pathwayComparisonCoalesce(Pathway newSource, Pathway newTarget, BiMap<Integer, Integer> newSourceGeneIdToPositionMap, BiMap<Integer, Integer> newTargetGeneIdToPositionMap) {

        Multimap<Integer, Multimap<Double, String>> forward = pcompare(newSource, newTarget); // key: qgeneId, value: {score=tgenecombination;...}
        Multimap<Integer, Multimap<Double, String>> reverse = pcompare(newTarget, newSource);

        // Re-construct the bimaps
        newSourceGeneIdToPositionMap = HashBiMap.create();
        int temp = 0;
        for (Gene e : newSource.getGenes()) {
            newSourceGeneIdToPositionMap.put(e.getGeneId(), temp++);
        }
        newTargetGeneIdToPositionMap = HashBiMap.create();
        temp = 0;
        for (Gene e : newTarget.getGenes()) {
            newTargetGeneIdToPositionMap.put(e.getGeneId(), temp++);
        }

        /* Create global list of matchings ordered by score by joining forward and reverse lists
         * key: querygene -> targetgenes
         * value: score
         */
        TreeMultimap<Double, String> globalMap = TreeMultimap.create(Ordering.natural().reverse(), Ordering.natural());
        for (Map.Entry<Integer, Multimap<Double, String>> e : forward.entries()) {
            int fgene = e.getKey();
            Multimap<Double, String> geneAndScore = e.getValue();
            for (Map.Entry<Double, String> scoreEntry : geneAndScore.entries()) {
                double score = scoreEntry.getKey();
                String matchingGeneString = scoreEntry.getValue();
                String[] multipleMatchingGenes = matchingGeneString.split(",");
                for (String matchingGene : multipleMatchingGenes) {
                    String newKey = fgene + "->" + matchingGene;
                    globalMap.put(score, newKey);
                }
            }
        }
        for (Map.Entry<Integer, Multimap<Double, String>> e : reverse.entries()) {
            int rgene = e.getKey();
            Multimap<Double, String> geneAndScore = e.getValue();
            for (Map.Entry<Double, String> scoreEntry : geneAndScore.entries()) {
                double score = scoreEntry.getKey();
                String matchingGeneString = scoreEntry.getValue();
                String[] multipleMatchingGenes = matchingGeneString.split(",");
                for (String matchingGene : multipleMatchingGenes) {
                    String newKey = matchingGene + "->" + rgene;
                    globalMap.put(score, newKey);
                }
            }
        }
        //////System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
        // create alignment
        // ////System.out.println(globalMap);
        Map<String, Double> matchingInTarget;
        Set<String> queryGenesCovered = new HashSet<String>();
        Set<String> targetGenesCovered = new HashSet<String>();
        Map<String, Map<String, Double>> currentBestResultMapping = new TreeMap<String, Map<String, Double>>();
        for (Map.Entry<Double, String> entry : globalMap.entries()) {
            double score = entry.getKey();
            //score=[alignment1, aligment2, ..]
            String alignment = entry.getValue();
            int i = 100;
            for (String collection : globalMap.asMap().get(score)) {
                int count = collection.length() - collection.replace("+", "").length();
                if (i > count) {
                    i = count;
                    alignment = collection;
                }
            }
            String bestScoreAlignment = alignment.split(",")[0];
            // start->end
            String start = bestScoreAlignment.split("->")[0];
            String end = bestScoreAlignment.split("->")[1];

            // start and end can be combination of genes
            Set<String> s = new HashSet<String>(Arrays.asList((start + "+").split("\\+")));
            Set<String> t = new HashSet<String>(Arrays.asList((end + "+").split("\\+")));

            // add to result mapping
            Set<String> sIntersection = new HashSet<String>();
            sIntersection.addAll(queryGenesCovered);
            sIntersection.retainAll(s);

            Set<String> tIntersection = new HashSet<String>();
            tIntersection.addAll(targetGenesCovered);
            tIntersection.retainAll(t);

            if (sIntersection.isEmpty() && tIntersection.isEmpty()) {
                matchingInTarget = new HashMap<String, Double>();
                matchingInTarget.put(reconstructWithGeneId(end, newTargetGeneIdToPositionMap), score);
                currentBestResultMapping.put(reconstructWithGeneId(start, newSourceGeneIdToPositionMap), matchingInTarget);
                queryGenesCovered.addAll(s);
                targetGenesCovered.addAll(t);
                break;
            }
        }
        return currentBestResultMapping;
    }

    public void pathwayComparison() {
        Multimap<Integer, Multimap<Double, String>> forward = pcompare(source, target);
        Multimap<Integer, Multimap<Double, String>> copyOfForward = ArrayListMultimap.create(forward); // make changes to this copy while combining genes
        Multimap<Integer, Multimap<Double, String>> reverse = pcompare(target, source);

        int nearestGene = GENE_MAX_DISTANCE;
        double overallScore = 0;

        String matchingGene;

        for (int i = 0; i < forward.size(); i++) {
            int currentIndex = i + 1; // here i is the id of the current gene
            nearestGene = GENE_MAX_DISTANCE;    // re-assign to max distance

            boolean currentTargetGeneAssigned = false;

            String whichGeneInTarget = "-999";

            while (currentTargetGeneAssigned == false) {
                // collect best scores in the forward direction into a string array (each array element looks like 0.5=1+2)
                String[] target_scores = getmax(copyOfForward.get(currentIndex), reverse).toString().split("=");

                if (copyOfForward.get(currentIndex).size() > 0) {

                    if (target_scores.length >= 1) {

                        double currentTargetGeneScore = Double.parseDouble(target_scores[0].toString().trim());
                        String currentTargetGeneCombination = target_scores[1].trim();
                        currentTargetGeneCombination = currentTargetGeneCombination + ";";  // add a ; symbol in the end to every candidate gene combination in target
                        if (currentTargetGeneScore > 0) {   // has a non-zero score
                            // collect individual gene combinations in a string array
                            String[] candidateTargetGenes = currentTargetGeneCombination.split(";");
                            for (String candidateTargetGene : candidateTargetGenes) {   // are there multiple gene combinations in target having equal scores?
                                candidateTargetGene = candidateTargetGene + "+";    // add a + symbol in the end to every gene and gene combination in the target

                                String[] genesInTargetCombination = candidateTargetGene.split("\\+");

                                String firstGeneInTargetCombination = genesInTargetCombination[0];
                                if (Integer.parseInt(firstGeneInTargetCombination) == currentIndex) {
                                    //  if both are pointing to each other then assign
                                    overallScore = currentTargetGeneScore;
                                    whichGeneInTarget = candidateTargetGene;
                                    currentTargetGeneAssigned = true;
                                }
                            }
                            if (currentTargetGeneAssigned == false) {
                                // no target gene wants to point to the query gene
                            }
                        }
                    }
                } else {
                    break;
                }

                // add results to finalresults
            }
            if (whichGeneInTarget.equalsIgnoreCase("-999")) {
                whichGeneInTarget = "no";
                overallScore = 0;
            }
        }
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
