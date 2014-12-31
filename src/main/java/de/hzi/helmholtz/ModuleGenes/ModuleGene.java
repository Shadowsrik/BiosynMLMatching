/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hzi.helmholtz.ModuleGenes;

import de.hzi.helmholtz.Domains.Domain;
import de.hzi.helmholtz.Modules.Module;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author srdu001
 */
public class ModuleGene {
     /*Id of the gene*/
    private int geneId;
    /*Name of the gene*/
    private String geneName;
    /*Ordered list of Modules*/
    private List<Module> Modules;
    /*Number of Modules in this gene*/
    private int size;

    public ModuleGene(int id, String name, List<Module> Modules) {
        this.geneId = id;
        this.geneName = name;
        this.Modules = Modules;
        this.size = Modules.size();
    }

    /**
     * @param Modules the Modules to set
     */
    public void setModules(List<Module> Modules) {
        this.Modules = Modules;
        this.size = Modules.size();
    }

    public Iterator<Module> moduleIterator() {
        return this.getModule().iterator();
    }

    /**
     * @return the Modules
     */
    public List<Module> getModule() {
        return Modules;
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
        toReturn += Modules.toString();

        return toReturn;
    }
}
