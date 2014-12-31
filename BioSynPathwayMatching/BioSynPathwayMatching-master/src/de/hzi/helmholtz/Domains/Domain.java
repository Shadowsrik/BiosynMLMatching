/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hzi.helmholtz.Domains;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author skondred
 */
public class Domain implements DomainInterface {

    private int ID; // ID assigned by us
    private int IDInFile; // ID from the xml file
    private String domainString; // Full function name of domain (e.g. KSa) i.e. strconcat(function,subtype)
    private String domainFunctionString;    // domain function
    private String domainFunctionSubtype;   // domain subtype
    private Status domainStatus;    // domain status
    private Set<String> domainSubstrates;   // set of substrated for the domain

    public Domain(int id, int IDInFile, String function, Status status) {
        this.ID = id;
        this.IDInFile = IDInFile;
        this.domainFunctionString = function;
        this.domainString = function;
        this.domainStatus = status;
        this.domainSubstrates = new TreeSet<String>();
    }

    public Domain(int id, int IDInFile, String function, String subtype, Status status) {
        this.ID = id;
        this.IDInFile = IDInFile;
        this.IDInFile = IDInFile;
        this.domainFunctionString = function;
        this.domainFunctionSubtype = subtype;
        this.domainString = subtype;
        this.domainStatus = status;
        this.domainSubstrates = new TreeSet<String>();
    }

    public Domain(int id, int IDInFile, String function, Status status, Set<String> substrates) {
        this.ID = id;
        this.IDInFile = IDInFile;
        this.domainFunctionString = function;
        this.domainString = function;
        this.domainStatus = status;
        this.domainSubstrates = new TreeSet<String>();
        if (!substrates.isEmpty()) {
            Iterator<String> it = substrates.iterator();
            while (it.hasNext()) {
                this.domainSubstrates.add(it.next());
            }
        }
    }

    public Domain(int id, int IDInFile, String function, String subtype, Status status, Set<String> substrates) {
        this.ID = id;
        this.IDInFile = IDInFile;
        this.domainFunctionString = function;
        this.domainFunctionSubtype = subtype;
        this.domainString = subtype;
        this.domainStatus = status;
        this.domainSubstrates = new TreeSet<String>();
        if (!substrates.isEmpty()) {
            Iterator<String> it = substrates.iterator();
            while (it.hasNext()) {
                this.domainSubstrates.add(it.next());
            }
        }
    }

    @Override
    public int getID() {
        //throw new UnsupportedOperationException("Not supported yet.");
        return this.ID;
    }

    @Override
    public String getDomainString() {
        //throw new UnsupportedOperationException("Not supported yet.");
        return this.domainString;
    }

    /**
     * @param ID the ID to set
     */
    public void setID(int ID) {
        this.ID = ID;
    }

    /**
     * @return the IDInFile
     */
    public int getIDInFile() {
        return IDInFile;
    }

    /**
     * @param IDInFile the IDInFile to set
     */
    public void setIDInFile(int IDInFile) {
        this.IDInFile = IDInFile;
    }

    /**
     * @param domainString the domainString to set
     */
    public void setDomainString(String domainString) {
        this.domainString = domainString;
    }

    /**
     * @return the domainFunctionString
     */
    public String getDomainFunctionString() {
        return domainFunctionString;
    }

    /**
     * @param domainFunctionString the domainFunctionString to set
     */
    public void setDomainFunctionString(String domainFunctionString) {
        this.domainFunctionString = domainFunctionString;
    }

    /**
     * @return the domainFunctionSubtype
     */
    public String getDomainFunctionSubtype() {
        return domainFunctionSubtype;
    }

    /**
     * @param domainFunctionSubtype the domainFunctionSubtype to set
     */
    public void setDomainFunctionSubtype(String domainFunctionSubtype) {
        this.domainFunctionSubtype = domainFunctionSubtype;
    }

    /**
     * @return the status
     */
    public Status getStatus() {
        return domainStatus;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(Status status) {
        this.domainStatus = status;
    }

    /**
     * @return the substrates
     */
    public Set<String> getSubstrates() {
        return domainSubstrates;
    }

    /**
     * @param substrates the substrates to set
     */
    public void setSubstrates(Set<String> substrates) {
        this.domainSubstrates = substrates;
    }
    
    @Override
    public String toString(){
        return this.domainFunctionString+"_"+this.domainStatus+"_"+this.domainSubstrates.toString();
    }
}
