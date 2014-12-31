/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hzi.helmholtz.Compare;

import de.hzi.helmholtz.Domains.Domain;
import de.hzi.helmholtz.Domains.Status;
import de.hzi.helmholtz.ModuleGenes.ModuleGene;
import de.hzi.helmholtz.Modules.Module;
import de.hzi.helmholtz.Pathways.Pathway;
import de.hzi.helmholtz.Pathways.PathwayWithModules;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author srdu001
 */
public class DBPathwayWithModules {

    private String pathwayID;
    private Map<Integer, Map<String, List<String>>> pathway;

    public DBPathwayWithModules() {
    }

    public DBPathwayWithModules(String pid, Map<Integer, Map<String, List<String>>> p) {
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
    public Map<Integer, Map<String, List<String>>> getPathway() {
        return pathway;
    }

    /**
     * @param pathway the pathway to set
     */
    public void setPathway(Map<Integer, Map<String, List<String>>> pathway) {
        this.pathway = pathway;
    }

    public PathwayWithModules convertToPathwayObj() {
        PathwayWithModules p;
        //   List<Gene> listOfGenes = new ArrayList<Gene>();
        List<ModuleGene> listOfModuleGenes = new ArrayList<ModuleGene>();
        int domainIdIndex = 1;
        List<Module> listmodules = new ArrayList<Module>();
        List<Domain> listOfDomains;
        for (Map.Entry<Integer, Map<String, List<String>>> entry : this.pathway.entrySet()) {
            int geneId = entry.getKey();
            for (Map.Entry<String, List<String>> entryValue : entry.getValue().entrySet()) {
                String moduleId = entryValue.getKey();
                listOfDomains = new ArrayList<Domain>();
                List<String> values = entryValue.getValue();
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
                Module m = new Module(moduleId, moduleId + "", listOfDomains);
                listmodules.add(m);
            }
            ModuleGene g = new ModuleGene(geneId, geneId + "", listmodules);
            listOfModuleGenes.add(g);
        }

        p = new PathwayWithModules(pathwayID, pathwayID, listOfModuleGenes);
        return p;
    }

    public void printPathway() {
        System.out.println(this.pathwayID);
        for (Map.Entry<Integer, Map<String, List<String>>> entry : this.pathway.entrySet()) {
            for (Map.Entry<String, List<String>> entryValue : entry.getValue().entrySet()) {
                String key = entryValue.getKey();
                List<String> values = entryValue.getValue();
                System.out.print("\t" + key + ": ");
                System.out.println(values);
            }
        }
    }
}
