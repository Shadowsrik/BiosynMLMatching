/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hzi.helmholtz.Compare;

import de.hzi.helmholtz.Domains.Domain;
import de.hzi.helmholtz.Domains.Status;
import de.hzi.helmholtz.Modules.Module;
import de.hzi.helmholtz.Pathways.Pathway;
import de.hzi.helmholtz.Pathways.PathwayUsingModules;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author srdu001
 */
public class DBPathwayUsingModules {
    private String pathwayID;
    private Map<String, List<String>> pathway;

    public DBPathwayUsingModules() {
    }

    public DBPathwayUsingModules(String pid, Map<String, List<String>> p) {
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
    public Map<String, List<String>> getPathway() {
        return pathway;
    }

    /**
     * @param pathway the pathway to set
     */
    public void setPathway(Map<String, List<String>> pathway) {
        this.pathway = pathway;
    }

    public PathwayUsingModules convertToPathwayObj() {
        PathwayUsingModules p;
        List<Module> listOfModule = new ArrayList<Module>();

        int domainIdIndex = 1;
        List<Domain> listOfDomains;
        for (Map.Entry<String, List<String>> entry : this.pathway.entrySet()) {
            String geneId = entry.getKey();
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
            Module g = new Module(geneId, geneId + "", listOfDomains);
            listOfModule.add(g);
        }

        p = new PathwayUsingModules(pathwayID, pathwayID, listOfModule);
        return p;
    }

    public void printPathway() {
        System.out.println(this.pathwayID);
        for (Map.Entry<String, List<String>> entry : this.pathway.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            System.out.print("\t" + key + ": ");
            System.out.println(values);
        }
    }
}
