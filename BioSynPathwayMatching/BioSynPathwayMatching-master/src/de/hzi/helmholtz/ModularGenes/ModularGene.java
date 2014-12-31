/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hzi.helmholtz.ModularGenes;

import de.hzi.helmholtz.Domains.Domain;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author skondred
 */
public class ModularGene {

    /*Id of the gene*/
    private int moduleId;
    /*Name of the gene*/
    private String moduleName;
    /*Ordered list of domains*/
    private List<Domain> modules;
    /*Number of domains in this gene*/
    private int size;

    public ModularGene(int id, String name, List<Domain> modules) {
        this.moduleId = id;
        this.moduleName = name;
        this.modules = modules;
        this.size = modules.size();
    }

    /**
     * @param domains the domains to set
     */
    public void setDomains(List<Domain> modules) {
        this.modules = modules;
        this.size = modules.size();
    }

    public Iterator<Domain> domainIterator() {
        return this.getDomains().iterator();
    }

    /**
     * @return the domains
     */
    public List<Domain> getDomains() {
        return modules;
    }

    /**
     * @return the moduleName
     */
    public String getmoduleName() {
        return moduleName;
    }

    /**
     * @param moduleName the moduleName to set
     */
    public void setmoduleName(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * @return the geneId
     */
    public int getGeneId() {
        return moduleId;
    }

    /**
     * @param geneId the geneId to set
     */
    public void setmoduleId(int moduleId) {
        this.moduleId = moduleId;
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

        toReturn += moduleId;
        toReturn += ":";
        toReturn += modules.toString();

        return toReturn;
    }
}
