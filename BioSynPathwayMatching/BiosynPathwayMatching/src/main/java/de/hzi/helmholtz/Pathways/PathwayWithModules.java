/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hzi.helmholtz.Pathways;

import de.hzi.helmholtz.Domains.Domain;
import de.hzi.helmholtz.ModuleGenes.ModuleGene;
import de.hzi.helmholtz.Modules.Module;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author srdu001
 */
public class PathwayWithModules {
    /*Pathway ID*/

    private String PathwayWithModulesId;
    /*Pathway name*/
    private String PathwayWithModulesName;
    /*Ordered list of Modulegenes*/
    private List<ModuleGene> Modulegenes;
    /*Number of Modulegenes*/
    private int size;

    public PathwayWithModules() {
    }

    public PathwayWithModules(String PathwayWithModulesId, String PathwayWithModulesName, List<ModuleGene> m) {
        this.PathwayWithModulesId = PathwayWithModulesId;
        this.PathwayWithModulesName = PathwayWithModulesName;
        this.Modulegenes = m;
        this.size = m.size();
    }

    public PathwayWithModules(PathwayWithModules p) {
        this.PathwayWithModulesId = p.PathwayWithModulesId;
        this.PathwayWithModulesName = p.PathwayWithModulesName;
        this.Modulegenes = p.getModulegenes();
        this.size = p.getModulegenes().size();
    }

    /**
     * @return the modules
     */
    public List<ModuleGene> getModulegenes() {
        return Modulegenes;
    }

    /**
     * @param Modulegenes the modules to set
     */
    public void setModulegenes(List<ModuleGene> Modulegenes) {
        this.Modulegenes = Modulegenes;
    }

    public Iterator<ModuleGene> moduleGeneIterator() {
        return this.Modulegenes.iterator();
    }

    /**
     * @return the PathwayWithModulesName
     */
    public String getPathwayName() {
        return PathwayWithModulesName;
    }

    /**
     * @param PathwayWithModulesName the PathwayWithModulesName to set
     */
    public void setPathwayName(String PathwayWithModulesName) {
        this.PathwayWithModulesName = PathwayWithModulesName;
    }

    /**
     * @return the PathwayWithModulesId
     */
    public String getPathwayId() {
        return PathwayWithModulesId;
    }

    /**
     * @param PathwayWithModulesId the PathwayWithModulesId to set
     */
    public void setPathwayId(String PathwayWithModulesId) {
        this.PathwayWithModulesId = PathwayWithModulesId;
    }

    public boolean removeGene(int geneId) {
        for (int i = 0; i < this.Modulegenes.size(); i++) {
            ModuleGene g = this.Modulegenes.get(i);
            if (g.getGeneId() == geneId) {
                this.Modulegenes.remove(g);
                return true;
            }
        }
        return false;
    }

    /**
     * @return the size
     */
    public int size() {
        return this.getModulegenes().size();
    }

    @Override
    public String toString() {
        String toReturn = this.PathwayWithModulesId + "\n";
        for (ModuleGene g : this.Modulegenes) {
            int geneId = g.getGeneId();
            toReturn += geneId + ": [";
            List<Module> values = g.getModule();
            for (Module m : values) {
                for (Domain d : m.getDomains()) {
                    toReturn += d.toString() + ",";
                }
            }
            toReturn = toReturn.substring(0, toReturn.length() - 1);
            toReturn += "]\n";
        }
        return toReturn;
    }
}
