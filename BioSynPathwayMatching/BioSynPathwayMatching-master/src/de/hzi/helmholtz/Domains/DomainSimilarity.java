/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hzi.helmholtz.Domains;

import java.util.Properties;

/**
 *
 * @author skondred
 */
public class DomainSimilarity {

    private double subdomainPenalty;
    private double substrateMismatchPenalty;
    private Properties penaltyProps;
    
    public void readPenalties(){
        try {
            penaltyProps.load(this.getClass().getClassLoader().getResourceAsStream("de" + System.getProperty("file.separator") + "hzi"
                                                                                    + "helmhotlz" + System.getProperty("file.separator") + "Domains"
                                                                                    + System.getProperty("file.separator") + "domainpenalties.properties"));
            subdomainPenalty = Double.parseDouble(penaltyProps.getProperty("SUBDOMAINPENALTY"));
            substrateMismatchPenalty = Double.parseDouble(penaltyProps.getProperty("SUBSTRATEMISMATCHPENALTY"));
        } catch (Exception e) {
        }
    }
    
    /* Return 0 for equivalence 
     * -SUBDOMAINPENALTY for sub-domain
     * -SUBSTRATEMISMATCHPENALTY for substrate mismatch
     */
    public static double getDomainSimilarity(Domain d1, Domain d2) {
        return getSubDomainSimilarity(d1, d2) + getSubstrateSimilarity(d1, d2);
    }

    public static double getSubDomainSimilarity(Domain d1, Domain d2) {
        if (d1.getDomainFunctionString().equalsIgnoreCase(d2.getDomainFunctionString())) {
            return 0.0;
        }else{
            //if(d1.getDomainFunctionSubtype().equalsIgnoreCase(d2.getDomainFunctionSubtype()))
            return 1;
        }
    }

    /*
     * If set of substrates are equivalent return 0
     * If set of substrates are disjoint return high
     * If set of substrates have intersection return medium
     */
    public static double getSubstrateSimilarity(Domain d1, Domain d2) {
        return 0.0;
    }
}
