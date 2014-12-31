/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hzi.helmholtz.Modules;

import de.hzi.helmholtz.Domains.Domain;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author srdu001
 */
public class Module {
     /*Id of the Module*/
    private String moduleId;
    /*Name of the Module*/
    private String moduleName;
    /*Ordered list of domains*/
    private List<Domain> domains;
    /*Number of domains in this Module*/
    private int size;

    public Module(String id, String name, List<Domain> domains) {
        this.moduleId = id;
        this.moduleName = name;
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
    public String getModuleName() {
        return moduleName;
    }

    /**
     * @param geneName the moduleName to set
     */
    public void setModuleName(String ModuleName) {
        this.moduleName = ModuleName;
    }

    /**
     * @return the geneId
     */
    public String getModuleId() {
        return moduleId;
    }

    /**
     * @param geneId the geneId to set
     */
    public void setModuleId(String ModuleId) {
        this.moduleId = ModuleId;
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
        toReturn += domains.toString();

        return toReturn;
    }
}
