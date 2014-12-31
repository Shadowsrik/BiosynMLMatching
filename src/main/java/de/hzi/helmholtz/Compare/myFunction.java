package de.hzi.helmholtz.Compare;

import com.google.common.collect.ArrayListMultimap;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

public class myFunction {

    public static Multimap<Double, Multimap<String, Integer>> calculate(List<String> stringOne, List<String> stringTwo) {
        Multiset<String> qfunction = LinkedHashMultiset.create(stringOne);
        Multiset<String> tfunction = LinkedHashMultiset.create(stringTwo);
        Multimap<Double, Multimap<String, Integer>> ScoreMatchMisMatch = ArrayListMultimap.create();
        Multiset<String> DomainsCovered = Multisets.intersection(qfunction, tfunction);

        double scoreForstringOne = (double) DomainsCovered.size() / qfunction.size();
        double scoreForstringTwo = (double) DomainsCovered.size() / tfunction.size();
        //System.out.println( scoreForstringOne+"     lev1:         "+scoreForstringTwo);

        int CountMismatchQuery = Math.abs(DomainsCovered.size() - qfunction.size());
        int CountMismatcTarget = Math.abs(DomainsCovered.size() - tfunction.size());
        //sw.init(qfunction, tfunction);
        //  sw.process();
        //  sw.backtrack();
        int Match = DomainsCovered.size();
        int MisMatch = CountMismatchQuery + CountMismatcTarget;
        Multimap<String, Integer> MatchMisMatch = ArrayListMultimap.create();
        int TranspositionDomains = 0;
        if(MisMatch==0)
        {
            TranspositionDomains = LevenshteinDistance.computeLevenshteinDistance(stringTwo, stringTwo);
            if(TranspositionDomains>0)
            {
                TranspositionDomains = 1;
            }
        }
        MatchMisMatch.put(Match+"-"+ MisMatch,TranspositionDomains);
        double score = 0.000;
        score = (scoreForstringOne + scoreForstringTwo) / 2;
        ScoreMatchMisMatch.put(score, MatchMisMatch);
        return ScoreMatchMisMatch;

    }

    public static void main(String[] args) {
        List<String> Qg5 = Arrays.asList("C", "A", "PCP", "C", "A", "PCP", "C", "A", "PCP", "C", "A", "PCP", "TE");
        List<String> Qg6 = Arrays.asList("C", "A", "PCP", "TE", "PCP", "PK", "nil", "nil", "nil", "nil");
        //System.out.println( "lev1:         "+myFunction.calculate(Qg5, Qg6));

    }
}
