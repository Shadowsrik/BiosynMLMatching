/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hzi.helmholtz.Compare;

import com.google.common.collect.BiMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
import de.hzi.helmholtz.Pathways.PathwayWithModules;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author srdu001
 */
public class ModuleClassThread implements Runnable {

    private Thread t;
    private String threadName;
    PathwayWithModules source;
    PathwayWithModules target;
    BiMap<Integer, Integer> SourceGeneIdToPositionMap;
    BiMap<Integer, Integer> TargetGeneIdToPositionMap;
    static PathwayComparisonWithModules comparison;
    int yes = -1;
    Multimap<Double, String> forward;

    public ModuleClassThread(String name, PathwayWithModules newSource, PathwayWithModules newTarget, BiMap<Integer, Integer> newSourceGeneIdToPositionMap, BiMap<Integer, Integer> newTargetGeneIdToPositionMap, int Yes) {
        threadName = name;
        source = new PathwayWithModules(newSource);
        target = new PathwayWithModules(newTarget);
        SourceGeneIdToPositionMap = newSourceGeneIdToPositionMap;
        TargetGeneIdToPositionMap = newTargetGeneIdToPositionMap;
        System.out.println("Creating " + threadName);
        yes = Yes;
    }

    public boolean isAlive() {
        boolean retturn;
        if(t.isAlive())
        {
            retturn = true;
        }
        else
        {
            retturn = false;
        }
        return retturn;
    }

    @Override
    public void run() {
        forward = TreeMultimap.create(Ordering.natural().reverse(), Ordering.natural());
        PathwayComparisonWithModules comparison = new PathwayComparisonWithModules(source, target, yes, threadName);
        forward = comparison.SubsetsMatching(source, target, SourceGeneIdToPositionMap, TargetGeneIdToPositionMap, yes);

//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Multimap<Double, String> GetResult() {
        return forward;
    }
  public void join() {
        try {
            t.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(ModuleClassThread.class.getName()).log(Level.SEVERE, null, ex);
        }
  }
    public void start() {
        System.out.println("Starting " + threadName);
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }

}
