/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hzi.helmholtz.Compare;

import de.hzi.helmholtz.Domains.Domain;
import de.hzi.helmholtz.Genes.Gene;
import de.hzi.helmholtz.Pathways.Pathway;
import de.hzi.helmholtz.Readers.TSVDataReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author skondred
 */
public class LongestCommonSubsequence {
    
    public LongestCommonSubsequence() {
    }
    
    public List<Domain> LCS(Pathway p1, Pathway p2) {
        List<Domain> lcs = new ArrayList<Domain>();
        
        
        List<Domain> p1Domains = new ArrayList<Domain>();
        Iterator<Gene> p1ModuleIter = p1.geneIterator();
        while (p1ModuleIter.hasNext()) {
            p1Domains.addAll(p1ModuleIter.next().getDomains());
        }
        
        List<Domain> p2Domains = new ArrayList<Domain>();
        Iterator<Gene> p2ModuleIter = p2.geneIterator();
        while (p2ModuleIter.hasNext()) {
            p2Domains.addAll(p2ModuleIter.next().getDomains());
        }
        
        int[][] num = new int[p1Domains.size() + 1][p2Domains.size() + 1];  //2D array, initialized to 0

        //Actual algorithm
        for (int i = 1; i <= p1Domains.size(); i++) {
            for (int j = 1; j <= p2Domains.size(); j++) {
                if (p1Domains.get(i - 1).equals(p2Domains.get(j - 1))) {
                    num[i][j] = 1 + num[i - 1][j - 1];
                } else {
                    num[i][j] = Math.max(num[i - 1][j], num[i][j - 1]);
                }
            }
        }
        
        System.out.println("length of LCS = " + num[p1Domains.size()][p2Domains.size()]);
        
        return lcs;
    }
    
    public static void main(String[] args) {
        try {
            TSVDataReader testReader = new TSVDataReader("./data/small/genes.txt", "./data/small/genes_domains.txt");
            testReader.read();
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
