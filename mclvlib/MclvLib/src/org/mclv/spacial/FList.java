/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;

import org.mclv.spacial.pack.Package;
import org.mclv.spacial.pack.PackLog;
import org.mclv.spacial.pack.PackageList;
/**
 *
 * @author God
 */
public class FList {
    private FSpace[] list;
    private Hashtable spacesByAction;
    private Hashtable spacesByAccepted;
    //private PackLog outLog = new PackLog();
    public FList(FSpace[] list){
        if(list == null){
            list = new FSpace[0];
        }
        this.list = list;
        spacesByAction = new Hashtable();
        spacesByAccepted = new Hashtable();
        for(int i = 0; i<list.length; i++){
            addByCriteria(list[i]);
        }
    }
    public FList(){
        this(null);
    }
    public FSpace matchAction(Runnable action){
        for(int i = 0; i<list.length; i++){
            Runnable entry = list[i].action();
            if(entry == action){
                return list[i];
            }
        }
        return null;
    }
    public FSpace matchPack(Class pack){
        for(int i = 0; i<list.length; i++){
            PackageList entry = list[i].acceptedPacks();
            if(entry.hasPackageType(pack)){
                return list[i];
            }
        }
        return null;
    }
    public FSpace[] matchPackAll(Class pack){
        FSpace[] spaces = new FSpace[0];
        /*for(int i = 0; i<list.length; i++){
            PackageList entry = list[i].acceptedPacks();
            if(entry.hasPackageType(pack)){
                FSpace[] thisElement = {list[i]};
                spaces = spaceMerge(spaces,thisElement);
            }
        }*/
        if(spacesByAccepted.containsKey(pack)){
            spaces = (FSpace[]) spacesByAccepted.get(pack);
        }
        return spaces;
    }
    public void addSpace(FSpace newSpace){
        //FSpace[] oldList = (FSpace[]) list.clone(); CLONE DOES NOT WORK
        FSpace[] oldList = new FSpace[list.length];
        System.arraycopy(list, 0, oldList, 0, oldList.length);
        list = new FSpace[list.length + 1];
        System.arraycopy(oldList, 0, list, 1, oldList.length);
        list[0] = newSpace;
        addByCriteria(newSpace);
    }
    public FSpace[] list(){
        return list;
    }
    public FList spacesWithAction(Runnable action){
        FList returnList = new FList();
        if(spacesByAction.containsKey(action)){
            Vector entry = (Vector) spacesByAction.get(action);
            for(int i = 0; i<entry.size(); i++){
                returnList.addSpace((FSpace) entry.elementAt(i));
            }
        }
        return returnList;
    }
    public void merge(FList list){
        this.list = spaceMerge(this.list(), list.list());
        Enumeration e = list.spacesByAction.elements();
        while(e.hasMoreElements()){
            Vector thisEntry = (Vector) e.nextElement();
            for(int i = 0; i<thisEntry.size(); i++){
                addByCriteria((FSpace) thisEntry.elementAt(i));
            }
        }
    }
    public void sendPack(Package pack){ //@TODO implement in streamtasks
        //outLog.newPack(pack); //@TODO analyze need for out log when not debugging
        FSpace[] destinations = matchPackAll(pack.getClass());
        for(int i = 0; i<destinations.length; i++){
            destinations[i].feedData(pack);
        }
    }
    private void addByCriteria(FSpace space){
        addByAction(space);
        addByAccepted(space);
    }
    private void addByAction(FSpace space){
        Runnable action = space.action();
        if(spacesByAction.containsKey(action)){
            Vector entry = (Vector) spacesByAction.get(action);
            entry.addElement(space);
        }
        else{
            Vector entry = new Vector(0);
            entry.addElement(space);
            spacesByAction.put(action, entry);
        }
    }
    private void addByAccepted(FSpace space){
        Class[] classList = space.acceptedPacks().list();
        for(int i = 0; i<classList.length; i++){
            FSpace[] thisSpace = {space};
            if(spacesByAccepted.containsKey(classList[i])){
                FSpace[] entry = (FSpace[]) spacesByAccepted.get(classList[i]);
                entry = spaceMerge(entry,thisSpace);
                spacesByAccepted.put(classList[i], entry); //@TODO Redundant?
            }
            else{
                spacesByAccepted.put(classList[i], thisSpace);
            }
        }
    }
    private FSpace[] spaceMerge(FSpace[] a, FSpace[] b){
        FSpace[] returnArray = new FSpace[a.length + b.length];
        System.arraycopy(a, 0, returnArray, 0, a.length);
        System.arraycopy(b, 0, returnArray, a.length, b.length);
        return returnArray;
    }
}
