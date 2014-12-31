/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hzi.helmholtz.Pathways;

import de.hzi.helmholtz.Domains.Domain;
import de.hzi.helmholtz.Genes.Gene;
import de.hzi.helmholtz.Modules.Module;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author srdu001
 */
public class PathwayUsingModules {
     /*Pathway ID*/
    private String pathwayId;
    /*Pathway name*/
    private String pathwayName;
    /*Ordered list of modules*/
    private List<Module> module;
    /*Number of modules*/
    private int size;

    

    public PathwayUsingModules(String pathwayId, String pathwayName, List<Module> m) {
        this.pathwayId = pathwayId;
        this.pathwayName = pathwayName;
        this.module = m;
        this.size = m.size();
    }
    
    public PathwayUsingModules(PathwayUsingModules p) {
        this.pathwayId = p.pathwayId;
        this.pathwayName = p.pathwayName;
        this.module = p.getModules();
        this.size = p.getModules().size();
    }

    /**
     * @return the modules
     */
    public List<Module> getModules() {
        return module;
    }

    /**
     * @param modules the modules to set
     */
    public void setModules(List<Module> modules) {
        this.module = modules;
    }

    public Iterator<Module> geneIterator() {
        return this.module.iterator();
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

    public boolean removeModule(String moduleId) {
        for (int i = 0; i < this.module.size(); i++) {
            Module g = this.module.get(i);
            if (g.getModuleId().trim().equals(moduleId.trim())) {
                this.module.remove(g);
                return true;
            }
        }
        return false;
    }

    /**
     * @return the size
     */
    public int size() {
        return this.getModules().size();
    }

    @Override
    public String toString() {
        String toReturn = this.pathwayId + "\n";
        for (Module g : this.module) {
            String moduleId = g.getModuleId();
            toReturn += moduleId + ": [";
            List<Domain> values = g.getDomains();
            for (Domain d : values) {
                toReturn += d.toString() + ",";
            }
            toReturn = toReturn.substring(0, toReturn.length() - 1);
            toReturn += "]\n";
        }
        return toReturn;
    }
}
