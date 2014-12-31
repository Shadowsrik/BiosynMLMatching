/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hzi.helmholtz.Compare;

import de.hzi.helmholtz.Domains.Domain;
import de.hzi.helmholtz.Domains.Status;
import de.hzi.helmholtz.Genes.Gene;
import de.hzi.helmholtz.Pathways.Pathway;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author skondred
 */
public class DBPathway {

    private String pathwayID;
    private Map<Integer, List<String>> pathway;

    public DBPathway() {
    }

    public DBPathway(String pid, Map<Integer, List<String>> p) {
        this.pathwayID = pid;
        this.pathway = p;
    }

    /**
     * @return the pathwayID
     */
    public String getPathwayID() {
        return pathwayID;
    }

    /**
     * @param pathwayID the pathwayID to set
     */
    public void setPathwayID(String pathwayID) {
        this.pathwayID = pathwayID;
    }

    /**
     * @return the pathway
     */
    public Map<Integer, List<String>> getPathway() {
        return pathway;
    }

    /**
     * @param pathway the pathway to set
     */
    public void setPathway(Map<Integer, List<String>> pathway) {
        this.pathway = pathway;
    }

    public Pathway convertToPathwayObj() {
        Pathway p;
        List<Gene> listOfGenes = new ArrayList<Gene>();

        int domainIdIndex = 1;
        List<Domain> listOfDomains;
        for (Map.Entry<Integer, List<String>> entry : this.pathway.entrySet()) {
            int geneId = entry.getKey();
            listOfDomains = new ArrayList<Domain>();
            List<String> values = entry.getValue();
            for (String value : values) {
                String[] parts = value.split("_");
                String geneFunction = parts[0];

                String geneStatus = parts[1];
                Status status;
                if (geneStatus.equalsIgnoreCase("active")) {
                    status = Status.ACTIVE;
                } else {
                    status = Status.INACTIVE;
                }

                String geneSubstrate = parts[2];
                Set<String> substrates = new TreeSet<String>();
                substrates.add(geneSubstrate);

                Domain d = new Domain(domainIdIndex++, domainIdIndex, geneFunction, status, substrates);
                listOfDomains.add(d);
            }
            Gene g = new Gene(geneId, geneId + "", listOfDomains);
            listOfGenes.add(g);
        }

        p = new Pathway(pathwayID, pathwayID, listOfGenes);
        return p;
    }

    public void printPathway() {
        System.out.println(this.pathwayID);
        for (Map.Entry<Integer, List<String>> entry : this.pathway.entrySet()) {
            int key = entry.getKey();
            List<String> values = entry.getValue();
            System.out.print("\t" + key + ": ");
            System.out.println(values);
        }
    }
}
