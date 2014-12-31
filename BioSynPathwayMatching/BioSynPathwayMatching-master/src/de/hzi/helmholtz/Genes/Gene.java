/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hzi.helmholtz.Genes;

import de.hzi.helmholtz.Domains.Domain;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author skondred
 */
public class Gene {

    /*Id of the gene*/
    private int geneId;
    /*Name of the gene*/
    private String geneName;
    /*Ordered list of domains*/
    private List<Domain> domains;
    /*Number of domains in this gene*/
    private int size;

    public Gene(int id, String name, List<Domain> domains) {
        this.geneId = id;
        this.geneName = name;
        this.domains = domains;
        this.size = domains.size();
    }

    /**
     * @param domains the domains to set
     */
    public void setDomains(List<Domain> domains) {
        this.domains = domains;
        this.size = domains.size();
    }

    public Iterator<Domain> domainIterator() {
        return this.getDomains().iterator();
    }

    /**
     * @return the domains
     */
    public List<Domain> getDomains() {
        return domains;
    }

    /**
     * @return the moduleName
     */
    public String getGeneName() {
        return geneName;
    }

    /**
     * @param geneName the moduleName to set
     */
    public void setGeneName(String geneName) {
        this.geneName = geneName;
    }

    /**
     * @return the geneId
     */
    public int getGeneId() {
        return geneId;
    }

    /**
     * @param geneId the geneId to set
     */
    public void setGeneId(int geneId) {
        this.geneId = geneId;
    }

    /**
     * @return the size
     */
    public int size() {
        return size;
    }

    @Override
    public String toString() {
        String toReturn = "";

        toReturn += geneId;
        toReturn += ":";
        toReturn += domains.toString();

        return toReturn;
    }
}
