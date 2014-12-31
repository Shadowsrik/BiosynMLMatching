/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hzi.helmholtz.Readers;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.Files;
import de.hzi.helmholtz.Domains.Domain;
import de.hzi.helmholtz.Domains.Status;
import de.hzi.helmholtz.Genes.Gene;
import de.hzi.helmholtz.Pathways.Pathway;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author skondred
 */
public class TSVDataReader {

    String genesFile;
    String geneDomainsFile;
    // Delimiters in the gene domains file
    private static String DOMAIN_DELIMITER = "-";
    private static String DOMAINSTATUS_DELIMITER = ";";
    private static String MODULENAME_DELIMITER = ":";
    // Delimiters in the pathway file
    private static String PATHWAYNAME_DELIMITER = ":";
    private static String MODULE_DELIMITER = "-";
    private List<Pathway> pathways;

    public TSVDataReader() {
    }

    public TSVDataReader(String genesFile, String domainsFile) {
        this.genesFile = genesFile;
        this.geneDomainsFile = domainsFile;
        this.pathways = new ArrayList<Pathway>();
    }

    public String getPathwayNameFromFile(String pathwayline) {
        if (pathwayline.contains(PATHWAYNAME_DELIMITER)) {
            return (pathwayline.split(PATHWAYNAME_DELIMITER)[0]).trim();
        } else {
            return "";
        }
    }

    public String getGeneNameFromLine(String domainline) {
        if (domainline.contains(MODULENAME_DELIMITER)) {
            return (domainline.split(MODULENAME_DELIMITER)[0]).trim();
        } else {
            return "";
        }
    }

    public List<Gene> getGenesFromLine(String domainline) {
        List<Gene> allGenes = new ArrayList<Gene>();
        //String geneName = (domainline.split(MODULENAME_DELIMITER)[0]).trim();
        String genesStr = (domainline.split(PATHWAYNAME_DELIMITER)[1]).trim();
        String[] genes = genesStr.split(MODULE_DELIMITER);
        if (genes.length >= 1) {
            for (int i = 0; i < genes.length; i++) {
                String geneName = genes[i].trim().split(MODULE_DELIMITER)[0];
                Gene m = new Gene(i,geneName, new ArrayList<Domain>());
                allGenes.add(m);
            }
            return allGenes;
        } else {
            return allGenes; //empty list
        }
    }

    public List<Domain> getDomainsFromLine(String domainline) {
        List<Domain> allDomains = new ArrayList<Domain>();
        //String geneName = (domainline.split(MODULENAME_DELIMITER)[0]).trim();
        String domainsStr = (domainline.split(MODULENAME_DELIMITER)[1]).trim();
        String[] domains = domainsStr.split(DOMAIN_DELIMITER);
        if (domains.length >= 1) {
            int domainIdInFile = 0; //dummy
            for (int i = 0; i < domains.length; i++) {
                String domainName = domains[i].trim().split(DOMAINSTATUS_DELIMITER)[0];
                String domainStatus = domains[i].trim().split(DOMAINSTATUS_DELIMITER)[1];
                Status status;
                if (domainStatus.toLowerCase().equalsIgnoreCase("active")) {
                    status = Status.ACTIVE;
                } else {
                    status = Status.INACTIVE;
                }
                Domain d = new Domain(domainName.hashCode(), domainIdInFile, domainName, status);
                allDomains.add(d);
                domainIdInFile++;
            }
            return allDomains;
        } else {
            return allDomains; //empty list
        }
    }

    public void read() {
        try {

            /*Construct genes*/
            Multimap<String, Gene> moduleMap = LinkedListMultimap.create();
            List<Gene> genesInPathway = new ArrayList<Gene>();
            File genedomainsf = new File(geneDomainsFile);
            ImmutableList<String> domainlines = Files.asCharSource(genedomainsf, Charsets.UTF_8).readLines();

            String pathwayName = "";
            String geneName = "";
            List<Domain> geneDomains = new ArrayList<Domain>();
            for (int i = 0; i < domainlines.size(); i++) {
                String nextDomainLine = domainlines.get(i);
                System.out.println(nextDomainLine);
                if (!nextDomainLine.trim().equals("")) {
                    if (nextDomainLine.contains("#")) {
                        pathwayName = (nextDomainLine.split("#")[0]).trim();
                    } else if (nextDomainLine.contains(":")) {
                        geneName = getGeneNameFromLine(nextDomainLine);
                        geneDomains = getDomainsFromLine(nextDomainLine);
                        Gene m = new Gene(i, geneName, geneDomains);
                        genesInPathway.add(m);
                        moduleMap.put(geneName, m);
                    }
                } else {
                    pathwayName = "";
                }
            }

            /*Construct gene pathways*/
            Multimap<String, Gene> pathwayMap = LinkedListMultimap.create();
            File genesf = new File(genesFile);
            ImmutableList<String> lines = Files.asCharSource(genesf, Charsets.UTF_8).readLines();
            pathwayName = "";
            List<Gene> pathwayModules = new ArrayList<Gene>();
            for (int i = 0; i < lines.size(); i++) {
                String nextGeneLine = lines.get(i);
                if (!nextGeneLine.trim().equals("")) {
                    if (nextGeneLine.contains(":")) {
                        pathwayName = getPathwayNameFromFile(nextGeneLine);
                        pathwayModules = getGenesFromLine(nextGeneLine); // these have only gene names with empty list of domains
                    }
                } else {    // finished reading a pathway
                    Iterator<Gene> it = pathwayModules.iterator();
                    while (it.hasNext()) {
                        Gene m = it.next();
                        Collection<Gene> modulesInMap = moduleMap.get(m.getGeneName());
                        if (modulesInMap.size() > 0) {
                            List<Domain> domainsofCurrModule = modulesInMap.iterator().next().getDomains();
                            if (domainsofCurrModule.size() > 0) {
                                m.setDomains(domainsofCurrModule);
                            }
                        }
                    }
                    Pathway p = new Pathway(i+"", pathwayName, pathwayModules);
                    this.pathways.add(p);
                    pathwayName = "";
                    pathwayModules = new ArrayList<Gene>();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TSVDataReader reader = new TSVDataReader("./data/small/genes.txt", "./data/small/genes_domains.txt");
        reader.read();
    }
}
