/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hzi.helmholtz.Pathways;

import de.hzi.helmholtz.Domains.Domain;
import de.hzi.helmholtz.Genes.Gene;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author skondred
 */
public class Pathway {

    /*Pathway ID*/
    private String pathwayId;
    /*Pathway name*/
    private String pathwayName;
    /*Ordered list of genes*/
    private List<Gene> genes;
    /*Number of genes*/
    private int size;

    public Pathway() {
    }

    public Pathway(String pathwayId, String pathwayName, List<Gene> m) {
        this.pathwayId = pathwayId;
        this.pathwayName = pathwayName;
        this.genes = m;
        this.size = m.size();
    }
    
     public Pathway(Pathway p) {
        this.pathwayId = p.pathwayId;
        this.pathwayName = p.pathwayName;
        this.genes = p.getGenes();
        this.size = p.getGenes().size();
    }

    /**
     * @return the modules
     */
    public List<Gene> getGenes() {
        return genes;
    }

    /**
     * @param genes the modules to set
     */
    public void setGenes(List<Gene> genes) {
        this.genes = genes;
    }

    public Iterator<Gene> geneIterator() {
        return this.genes.iterator();
    }

    /**
     * @return the pathwayName
     */
    public String getPathwayName() {
        return pathwayName;
    }

    /**
     * @param pathwayName the pathwayName to set
     */
    public void setPathwayName(String pathwayName) {
        this.pathwayName = pathwayName;
    }

    /**
     * @return the pathwayId
     */
    public String getPathwayId() {
        return pathwayId;
    }

    /**
     * @param pathwayId the pathwayId to set
     */
    public void setPathwayId(String pathwayId) {
        this.pathwayId = pathwayId;
    }

    public boolean removeGene(int geneId){
        for (int i = 0; i < this.genes.size(); i++) {
            Gene g = this.genes.get(i);
            if(g.getGeneId() == geneId){
                this.genes.remove(g);
                return true;
            }
        }
        return false;
    }
    
    /**
     * @return the size
     */
    public int size() {
        return this.getGenes().size();
    }

    @Override
    public String toString() {
        String toReturn = this.pathwayId+"\n";
        for (Gene g : this.genes) {
            int geneId = g.getGeneId();
            toReturn += geneId + ": [";
            List<Domain> values = g.getDomains();
            for (Domain d : values) {
                toReturn += d.toString() + ",";
            }
            toReturn = toReturn.substring(0,toReturn.length()-1);
            toReturn += "]\n";
        }
        return toReturn;
    }
}