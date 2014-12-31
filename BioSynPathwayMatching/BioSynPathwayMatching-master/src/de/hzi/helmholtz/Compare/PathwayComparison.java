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
import de.hzi.helmholtz.Genes.Gene;
import de.hzi.helmholtz.Genes.GeneSimilarity;
import de.hzi.helmholtz.Pathways.Pathway;
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
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 *
 * @author skondred
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
    private int maxWindowSize = 3;
    private double functionWeight = 2f;
    private double statusWeight = 0.5f;
    private double substrateWeight = 0.5f;
    private int GENE_MAX_DISTANCE = 1000;
    private Map<String, Map<String, Double>> bestResultMapping;

    public PathwayComparison(Pathway source, Pathway target) {
        this.source = source;
        this.target = target;
        constructBiMaps();
        System.out.println(source.toString());
        System.out.println(target.toString());
        frame = new JFrame("Matching");
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

    public void display() {
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(1400, 200);
        //frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    /* Iterate over source genes
     * Compare each source gene against every target gene
     * Keep the comparison scores in forwardScores
     * Keep the best forward scores for gene-gene in forwardBestScores
     * Do the same in the reverse direction
     */
    /*public Multimap<Integer, Multimap<Double, String>> pcompare(Pathway firstPathway, Pathway secondPathway) {

     Multimap<Double, String> forwardScores = ArrayListMultimap.create();
     Multimap<Integer, Multimap<Double, String>> forwardBestScores = ArrayListMultimap.create();

     int currentQueryGene = 0;
     Iterator<Gene> sourceGeneIt = firstPathway.geneIterator(); // for reverse scoring, this becomes targetGeneIt
     while (sourceGeneIt.hasNext()) {
     Gene queryGene = sourceGeneIt.next();
     currentQueryGene++;

     // clear forward scores after one gene is done
     forwardScores.clear();

     Iterator<Gene> targetGeneIt = secondPathway.geneIterator();
     int currentTargetGene = 0;
     while (targetGeneIt.hasNext()) {
     Gene targetGene = targetGeneIt.next();
     currentTargetGene++;

     GeneSimilarity geneSimilarity = new GeneSimilarity();
     if (queryGene.size() == targetGene.size()) {
     // Match one source gene against one target gene with the same index
     List<Gene> targetGenes = new ArrayList<Gene>();
     targetGenes.add(targetGene);
     double score = geneSimilarity.levenshteinSimilarity(queryGene, targetGenes, functionWeight, statusWeight, substrateWeight);
     forwardScores.put(score, currentTargetGene + "");
     } else if (queryGene.size() < targetGene.size()) {
     // Merge multiple source genes and compare to one target gene
     double highestScoreInAllWindows = 0.0f;
     int highestScoringWindow = 0;
     for (int currentWindowSize = 1; currentWindowSize < maxWindowSize; currentWindowSize++) {
     if (currentQueryGene + 1 < firstPathway.size()) {
     // construct list of source genes to compare, list size = currentWindowSize
     List<Gene> mergedGenes = new ArrayList<Gene>();
     List<Gene> sourceGenesList = firstPathway.getGenes();
     for (int i = currentQueryGene; i <= currentWindowSize; i++) {
     mergedGenes.add(sourceGenesList.get(i));
     }
     double score = geneSimilarity.levenshteinSimilarity(targetGene, mergedGenes, functionWeight, statusWeight, substrateWeight);
     if (Math.abs(score) > Math.abs(highestScoreInAllWindows)) {
     highestScoreInAllWindows = score;
     highestScoringWindow = currentWindowSize;
     }
     }
     }
     if (highestScoreInAllWindows < 0) {
     String combinedGenes = "";
     for (int i = currentQueryGene; i < currentQueryGene + highestScoringWindow; i++) {
     combinedGenes += i + "+";
     }
     combinedGenes = combinedGenes.substring(0, combinedGenes.length() - 1);
     forwardScores.put(highestScoreInAllWindows, combinedGenes);
     } else {
     String combinedGenes = "";
     for (int i = currentQueryGene + highestScoringWindow - 1; i > currentQueryGene; i--) {
     combinedGenes += i + "+";
     }
     combinedGenes = combinedGenes.substring(0, combinedGenes.length() - 1);
     forwardScores.put(highestScoreInAllWindows, combinedGenes);
     }
     } else {
     // Merge multiple target genes and compare to one source gene
     double highestScoreInAllWindows = 0.0f;
     int highestScoringWindow = 0;
     for (int currentWindowSize = 1; currentWindowSize < maxWindowSize; currentWindowSize++) {
     if (currentTargetGene + 1 < secondPathway.size()) {
     // construct list of target genes to compare, list size = currentWindowSize
     List<Gene> mergedGenes = new ArrayList<Gene>();
     List<Gene> targetGenesList = secondPathway.getGenes();
     for (int i = currentTargetGene; i <= currentWindowSize; i++) {
     mergedGenes.add(targetGenesList.get(i));
     }
     double score = geneSimilarity.levenshteinSimilarity(queryGene, mergedGenes, functionWeight, statusWeight, substrateWeight);
     if (Math.abs(score) > Math.abs(highestScoreInAllWindows)) {
     highestScoreInAllWindows = score;
     highestScoringWindow = currentWindowSize;
     }
     }
     }
     if (highestScoreInAllWindows < 0) {
     String combinedGenes = "";
     for (int i = currentTargetGene; i < currentTargetGene + highestScoringWindow; i++) {
     combinedGenes += i + "+";
     }
     combinedGenes = combinedGenes.substring(0, combinedGenes.length() - 1);
     forwardScores.put(highestScoreInAllWindows, combinedGenes);
     } else {
     String combinedGenes = "";
     for (int i = currentTargetGene + highestScoringWindow - 1; i > currentTargetGene; i--) {
     combinedGenes += i + "+";
     }
     combinedGenes = combinedGenes.substring(0, combinedGenes.length() - 1);
     forwardScores.put(highestScoreInAllWindows, combinedGenes);
     }
     }
     }
     Multimap<Double, String> forwardscore1 = ArrayListMultimap.create(forwardScores);
     TreeMultimap<Double, String> forwardscore = TreeMultimap.create(Ordering.natural().reverse(), Ordering.natural());
     forwardscore.putAll(forwardscore1);
     forwardBestScores.put(currentQueryGene, forwardscore);
     }
     System.out.println(forwardBestScores);
     return forwardBestScores;
     }*/
    public Multimap<Integer, Multimap<Double, String>> pcompare(Pathway firstPathway, Pathway secondPathway) {

        Multimap<Double, String> forwardScores = ArrayListMultimap.create();
        Multimap<Integer, Multimap<Double, String>> forwardBestScores = ArrayListMultimap.create();

        int currentQueryGene = 0;
        Iterator<Gene> sourceGeneIt = firstPathway.geneIterator(); // for reverse scoring, this becomes targetGeneIt
        while (sourceGeneIt.hasNext()) {
            Gene queryGene = sourceGeneIt.next();
            currentQueryGene++;

            // clear forward scores after one gene is done
            forwardScores.clear();

            Iterator<Gene> targetGeneIt = secondPathway.geneIterator();
            int currentTargetGene = 0;
            while (targetGeneIt.hasNext()) {
                Gene targetGene = targetGeneIt.next();
                currentTargetGene++;

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
                    int maxGap = 1;

                    for (int currentWindowSize = 0; currentWindowSize < maxWindowSize; currentWindowSize++) {
                        if (currentTargetGene + currentWindowSize <= secondPathway.size()) {
                            // construct list of target genes to compare, list size = currentWindowSize
                            List<Gene> mergedGenes = new ArrayList<Gene>();
                            List<Gene> targetGenesList = secondPathway.getGenes();

                            if (currentWindowSize < 2) {
                                for (int i = currentTargetGene - 1; i <= currentWindowSize; i++) {
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
                            } else {
                                // current window size is more than 2, so create gaps
                                for (int g = 1; g <= maxGap; g++) {
                                    for (int i = currentTargetGene - 1; mergedGenes.size() <= currentWindowSize && i + g - 1 < secondPathway.size(); i += g + 1) {
                                        mergedGenes.add(targetGenesList.get(i));
                                    }
                                    double score = geneSimilarity.levenshteinSimilarity(queryGene, mergedGenes, functionWeight, statusWeight, substrateWeight);
                                    List<Integer> combinedGenes = new ArrayList<Integer>();
                                    for (int i = currentTargetGene; combinedGenes.size() <= currentWindowSize && i + g - 1 <= secondPathway.size(); i += g + 1) {
                                        combinedGenes.add(i);   // + "+";
                                    }
                                    if (score > 0) {
                                        String cString = combinedGenes.toString().replaceAll(" ", "").replaceAll(",", "+");
                                        cString = cString.substring(1, cString.length() - 1);
                                        forwardScores.put(Math.abs(score), cString);
                                    } else {
                                        Collections.reverse(combinedGenes);
                                        String cString = combinedGenes.toString().replaceAll(" ", "").replaceAll(",", "+");
                                        cString = cString.substring(1, cString.length() - 1);
                                        forwardScores.put(Math.abs(score), cString);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Multimap<Double, String> forwardscore1 = ArrayListMultimap.create(forwardScores);
            TreeMultimap<Double, String> forwardscore = TreeMultimap.create(Ordering.natural().reverse(), Ordering.natural());
            forwardscore.putAll(forwardscore1);
            forwardBestScores.put(currentQueryGene, forwardscore);
        }
        System.out.println(forwardBestScores);
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
        for (String position : positions) {
            int pos = Integer.parseInt(position);
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
        System.out.println(globalMap);

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

        System.out.println(bestResultMapping);
    }

    public void pathwayComparisonGlobalBestCoalesce() {
        bestResultMapping = new TreeMap<String, Map<String, Double>>();;
        Map<String, Map<String, Double>> currentBestResultMapping;

        Pathway newSource = new Pathway(source);
        Pathway newTarget = new Pathway(target);
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
        System.out.println(bestResultMapping);
    }

    public Map<String, Map<String, Double>> pathwayComparisonCoalesce(Pathway newSource, Pathway newTarget, BiMap<Integer, Integer> newSourceGeneIdToPositionMap, BiMap<Integer, Integer> newTargetGeneIdToPositionMap) {

        Multimap<Integer, Multimap<Double, String>> forward = pcompare(newSource, newTarget); // key: qgeneId, value: {score=tgenecombination;...}
        Multimap<Integer, Multimap<Double, String>> reverse = pcompare(newTarget, newSource);

        // Re-construct the bimaps
        newSourceGeneIdToPositionMap = HashBiMap.create();
        int temp = 1;
        for (Gene e : newSource.getGenes()) {
            newSourceGeneIdToPositionMap.put(e.getGeneId(), temp++);
        }
        newTargetGeneIdToPositionMap = HashBiMap.create();
        temp = 1;
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

        // create alignment
        //System.out.println(globalMap);
        Map<String, Double> matchingInTarget;
        Set<String> queryGenesCovered = new HashSet<String>();
        Set<String> targetGenesCovered = new HashSet<String>();
        Map<String, Map<String, Double>> currentBestResultMapping = new TreeMap<String, Map<String, Double>>();
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
                                } /*else {
                                 double bestInTheTargetCombination = Double.MIN_VALUE;
                                 for (String geneInTargetCombination : genesInTargetCombination) {
                                 String[] query_scores = getmax(reverse.get(Integer.parseInt(geneInTargetCombination.trim())), forward).split("=");
                                 if (Double.parseDouble(query_scores[0]) > bestInTheTargetCombination) {
                                 bestInTheTargetCombination = Double.parseDouble(query_scores[0]);
                                 }
                                 }
                                 if (bestInTheTargetCombination - currentTargetGeneScore <= SCORE_MISMATCH_ERROR) {
                                 // assign
                                 overallScore = currentTargetGeneScore;
                                 whichGeneInTarget = candidateTargetGene;
                                 currentTargetGeneAssigned = true;
                                 }
                                 }*/

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

/*
 currentQueryGeneCombination = currentQueryGeneCombination + ";";  // add a ; symbol in the end to every candidate gene combination in query
 String[] currentQueryGenes = currentQueryGeneCombination.split(";");
 for (String currentQueryGene : currentQueryGenes) {
 currentQueryGene = currentQueryGene + "+";

 String[] genesInQueryCombination = currentQueryGene.split("\\+");
 String bestQueryGene = genesInQueryCombination[0];
 double scoreOfBestQueryGene = 0.0f;
 for (String queryGene : genesInQueryCombination) {
 String[] new_target_scores = getmax(copyOfForward.get(Integer.parseInt(queryGene.trim())), reverse).split("=");

 if (new_target_scores.length >= 1) {
 double newTargetGeneScore = Double.parseDouble(new_target_scores[0].toString().trim());
 String newTargetGeneCombination = new_target_scores[1].trim();
 newTargetGeneCombination = newTargetGeneCombination + ";";
 String[] newTargetGenes = newTargetGeneCombination.split(";");

 for (String newTargetGene : newTargetGenes) {
 newTargetGene = newTargetGene + "+";
 String[] genesInNewTargetCombination = newTargetGene.split("\\+");
 for (String geneInNewTargetCombination : genesInNewTargetCombination) {

 if (currentQueryGeneScore >= newTargetGeneScore) {
 overallScore = currentQueryGeneScore;
 whichGeneInTarget = currentQueryGene;
 } else {
 overallScore = newTargetGeneScore;
 whichGeneInTarget = newTargetGene;
 }
 }
 }
 }
 }
 if (Integer.parseInt(bestQueryGene.trim()) == currentIndex) {

 // collect scores of query to target and retrieve which target combination has max value (e.g., 1=2,3+4)


 // Assign to nearest gene if they have same scores (for e.g., if 1=2;3+4, assign 1=2)
 if (Double.parseDouble(query_scores[0].trim()) <= Double.parseDouble(target_scores[0].trim())) {
 overallScore = Double.parseDouble(target_scores[0].trim());
 int distance = Math.abs(Integer.parseInt(bestQueryGene.trim()) - currentIndex);
 if (distance < nearestGene) {
 nearestGene = distance;
 whichGeneInTarget = candidateTargetGene;
 }
 }
 }
 }
 * 
 * 
 * 
 if (query_scores.length >= 1) {
 double currentQueryGeneScore = Double.parseDouble(query_scores[0].toString().trim());
 String currentQueryGeneCombination = query_scores[1].trim();

 if (currentTargetGeneScore - currentQueryGeneScore <= SCORE_MISMATCH_ERROR) {   // query-to-target edge has higher score than target-to-query edge (e.g., q(1)->t(2+3) > t(2)->q(4))
 //  assign
 overallScore = currentTargetGeneScore;
 whichGeneInTarget = candidateTargetGene;
 currentTargetGeneAssigned = true;
 } else {    // currentQueryGeneScore <= currentTargetGeneScore
 // if both are pointing to each other with different scores, then assign
 // else do position(tgene)-position(qgene)
 //      if +ve then assign
 //      if -ve then do not assign
 currentQueryGeneCombination = currentQueryGeneCombination + ";";  // add a ; symbol in the end to every candidate gene combination in query
 String[] currentQueryGenes = currentQueryGeneCombination.split(";");
 for (String currentQueryGene : currentQueryGenes) {
 currentQueryGene = currentQueryGene + "+";
 String[] genesInQueryCombination = currentQueryGene.split("\\+");
 String bestQueryGene = genesInQueryCombination[0];
 if (Integer.parseInt(bestQueryGene) == currentIndex) {
 // both are pointing to each other, so assign
 overallScore = currentTargetGeneScore;
 whichGeneInTarget = candidateTargetGene;
 currentTargetGeneAssigned = true;
 } else {
 // not pointing to each other, so check distance                                                    
 }
 }
 }
 }
 */
