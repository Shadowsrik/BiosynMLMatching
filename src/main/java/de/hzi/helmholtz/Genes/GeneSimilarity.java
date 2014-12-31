/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hzi.helmholtz.Genes;

import com.google.common.collect.Lists;
import de.hzi.helmholtz.Compare.LevenshteinDistance;
import de.hzi.helmholtz.Domains.Domain;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import de.hzi.helmholtz.Compare.CosineSimilarity;

/**
 *
 * @author srdu001
 */
public class GeneSimilarity {

    /* Compare two genes domain-by-domain using domain penalties for levenshtein distance implemented in LevenshteinDistance.java */
    public double levenshteinSimilarity(Gene query, List<Gene> toCompare, double functionMatchWeight, double statusMatchWeight, double substrateMatchWeight) {
        int direction = 0; // tells if forward match was better or reverse
        List<String> qfunction = new ArrayList<String>();
        List<String> qactivity = new ArrayList<String>();
        List<Set<String>> qsubstrate = new ArrayList<Set<String>>();
        for (Domain d : query.getDomains()) {
            qfunction.add(d.getDomainFunctionString());
            qactivity.add(d.getStatus().toString());
            qsubstrate.add(d.getSubstrates());
        }
        CosineSimilarity sim = new CosineSimilarity();
        double finalscore = 0.0f;
        double functionscore = 0.0f;
        double statusScore = 0.0f;
        double substrateScore = 0.0f;

        // combine the list  of genes into one in the forward direction .. and get levenshtein score
        double straightFunctionScore = 0.0f;
        List<String> tfunction = new ArrayList<String>();
        List<String> tactivity = new ArrayList<String>();
        List<Set<String>> tsubstrate = new ArrayList<Set<String>>();
        for (Gene g : toCompare) {
            Iterator<Domain> dIter = g.domainIterator();
            while (dIter.hasNext()) {
                Domain d = dIter.next();
                tfunction.add(d.getDomainFunctionString());
                tactivity.add(d.getStatus().toString());
                tsubstrate.add(d.getSubstrates());
            }
        }

        straightFunctionScore = (double) sim.calculate(qfunction, tfunction);
       // straightFunctionScore = 1 - ((double) LevenshteinDistance.computeLevenshteinDistance(qfunction, tfunction) / (Math.max(qfunction.size(), tfunction.size())));

        // combine the list  of genes into one in the reverse direction .. and get levenshtein score
        Lists.reverse(toCompare);
        double reverseFunctionScore = 0.0f;
        List<String> rtfunction = new ArrayList<String>();
        List<String> rtactivity = new ArrayList<String>();
        List<Set<String>> rtsubstrate = new ArrayList<Set<String>>();
        for (Gene g : Lists.reverse(toCompare)) {
            Iterator<Domain> dIter = g.domainIterator();
            while (dIter.hasNext()) {
                Domain d = dIter.next();
                rtfunction.add(d.getDomainFunctionString());
                rtactivity.add(d.getStatus().toString());
                rtsubstrate.add(d.getSubstrates());
            }
        }
        //reverseFunctionScore = 1 - ((double) LevenshteinDistance.computeLevenshteinDistance(qfunction, rtfunction) / (Math.max(qfunction.size(), rtfunction.size())));
        reverseFunctionScore = (double) sim.calculate(qfunction, tfunction);
        if (straightFunctionScore >= reverseFunctionScore) {
            direction = 1;
            functionscore = straightFunctionScore;
            statusScore = getStatusComparisonScore(qactivity, tactivity);
            substrateScore = getSubstrateComparisonScore(qsubstrate, tsubstrate);
        } else {
            direction = -1;
            functionscore = reverseFunctionScore;
            statusScore = getStatusComparisonScore(qactivity, rtactivity);
            substrateScore = getSubstrateComparisonScore(qsubstrate, rtsubstrate);
        }
        finalscore = direction * Math.round((((2.9 * functionscore) + (0.05 * statusScore) + (0.05 * substrateScore)) / 3) * 100.0) / 100.0;
        /*   if (functionMatchWeight == 0 || statusMatchWeight == 0 || substrateMatchWeight == 0) {
         finalscore = direction * Math.round((((2 * functionscore) + (0.5 * statusScore) + (0 * substrateScore)) / 2) * 100.0) / 100.0;
       
         } else {
         finalscore = direction * Math.round((((functionMatchWeight * functionscore) + (statusMatchWeight * statusScore) + (substrateMatchWeight * substrateScore)) / 2) * 100.0) / 100.0;
         }*/
        return finalscore;
    }
    
    /* Compare statuses of query gene with list of target genes .. currently positive only .. doesnt penalize mismatches*/
    private double getStatusComparisonScore(List<String> queryStatusesList, List<String> targetStatusesList) {
        double score = 0.0f;
        if (queryStatusesList.size() == targetStatusesList.size()) {
            /*for (int i = 0; i < queryStatusesList.size(); i++) {
             String q = queryStatusesList.get(i);
             String t = targetStatusesList.get(i);
             if (q.equalsIgnoreCase(t)) {
             score++;
             }
             }*/
            CosineSimilarity sim = new CosineSimilarity();
            //  score = 1 - ((double) LevenshteinDistance.computeLevenshteinDistance(queryStatusesList, targetStatusesList) / (Math.max(queryStatusesList.size(), targetStatusesList.size())));
            score = sim.calculate(queryStatusesList, targetStatusesList);
        }
        return score;
    }

    /* Compare substrates of query gene with list of target genes*/
    private double getSubstrateComparisonScore(List<Set<String>> querySubstrateList, List<Set<String>> targetSubstrateList) {
        double score = 0.0f;

        /*for (int i = 0; i < querySubstrateList.size(); i++) {
         Set<String> q = querySubstrateList.get(i);
         Set<String> t = targetSubstrateList.get(i);
         q.retainAll(t);
         score += q.size();
         }*/
        List<String> querySubstratesStrings = new ArrayList<String>();
        String querySubstratesString = "";
        for (int i = 0; i < querySubstrateList.size(); i++) {
            //   querySubstratesString += querySubstrateList.get(i).toString();
            querySubstratesStrings.add(querySubstrateList.get(i).toString());
        }
        querySubstratesStrings.add(querySubstratesString);

        List<String> targetSubstratesStrings = new ArrayList<String>();
        String targetSubstratesString = "";
        for (int i = 0; i < targetSubstrateList.size(); i++) {
            //  targetSubstratesString += targetSubstrateList.get(i).toString();
            targetSubstratesStrings.add(targetSubstrateList.get(i).toString());
        }
        targetSubstratesStrings.add(targetSubstratesString);
        CosineSimilarity sim = new CosineSimilarity();
        score = sim.calculate(querySubstratesStrings, targetSubstratesStrings);
        //    score = 1 - ((double) LevenshteinDistance.computeLevenshteinDistance(querySubstratesStrings, targetSubstratesStrings) / (Math.max(querySubstratesStrings.size(), targetSubstratesStrings.size())));

        return score;
    }
}
