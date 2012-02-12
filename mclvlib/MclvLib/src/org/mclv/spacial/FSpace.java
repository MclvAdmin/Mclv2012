/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mclv.spacial;
import org.mclv.spacial.pack.Package;
import java.util.Hashtable;
import java.util.Vector;
import org.mclv.spacial.pack.PackLog;
import org.mclv.spacial.pack.PackageList;
/**
 *
 * @author romana
 */
public class FSpace implements Runnable,Killable,StreamTask{
    private static final Runnable NULL_ACTION = new NullAction();
    private static Hashtable spaceBySuper = new Hashtable();
    private static Hashtable spaceByAction = new Hashtable();
    private static FList spaces = new FList();
    private static FSpace metaSpace;
    private Runnable action;
    private int degree;
    private FList superSpaces;
    private FList adjacentSpaces; //@TODO evaluate performance of dedicated adjacent list versus computed by superspace subspace lists
    private FList subSpaces;
    public FSpace(Runnable action, FSpace[] superSpaces, Object nullObj){//Added nullObj to avoid ambiguous reference in metaSpace()
        subSpaces = new FList();
        adjacentSpaces = new FList();
        if(action == null){
            action = NULL_ACTION;
        }
        this.action = action;
        if(action instanceof TaskObscure){
            this.action = ((TaskObscure) action).action();
        }
        
        if(superSpaces == null || superSpaces[0] == null){
            superSpaces = new FSpace[0];
            if(metaSpace == null){
                metaSpace = this;
                Vector entry = new Vector(0);
                entry.addElement(metaSpace);
                spaceBySuper.put(action, entry);
            }
            else{
                throw new NullPointerException(); 
            }
        }
        else{
            archive(this, superSpaces);
        }
        this.superSpaces = new FList(superSpaces);
        adjacentSpaces = adjacentSpaces();
    }
    public FSpace(Runnable action, FSpace superSpace){ 
        this(action,toArray(superSpace),null);
    }
    public FSpace(Runnable action){
        this(action,metaSpace());
    }
    public void addSuperSpace(FSpace superSpace){
        superSpace.addSub(this); //@TODO determine which order is better, if any preference
        superSpaces.addSpace(superSpace);
    }
    public FList subSpaces(){
        return subSpaces;
    }
    public FList superSpaces(){
        return superSpaces;
    }
    private static void archive(FSpace space, FSpace[] superSpaces){
        spaces.addSpace(space);
        for(int i = 0; i<superSpaces.length; i++){
            superSpaces[i].addSub(space);
            if(spaceBySuper.containsKey(superSpaces[i])){
                Vector entry = (Vector) spaceBySuper.get(superSpaces[i]);
                /*for(int j = 0; j<entry.size(); j++){
                    ((FSpace) entry.elementAt(j)).addAdjacent(space);
                }*/
                entry.addElement(space);
            }
            else{
                Vector entry = new Vector(0);
                entry.addElement(space);
                spaceBySuper.put(superSpaces[i], entry);
            }
        }
        //Add space-action correlation
        spaceByAction.put(space.action(),space);
    }
    private void addAdjacent(FSpace adjSpace){
        adjacentSpaces.addSpace(adjSpace);
    }
    private void addSub(FSpace subSpace){
        FSpace[] list = subSpaces.list();
        for(int i = 0; i<list.length; i++){
            list[i].addAdjacent(subSpace);
        }
        subSpaces.addSpace(subSpace);
    }
    public static FSpace spaceByAction(Runnable action){
        if(spaceByAction.containsKey(action)){
            return (FSpace) spaceByAction.get(action);
        }
        return null;
    }
    public static FSpace metaSpace(){
        if(metaSpace == null){
            FSpace nullSpace = new FSpace(null,null);
        }
        return metaSpace;
    }
    public void sendAdjacentPack(Package pack){
        adjacentSpaces.sendPack(pack);
    }
    public static FList allSpaces(){
        return spaces;
    }
    public FList spaceByInstance(Class testClass){
        Vector matching = new Vector(0);
        FSpace[] superSpace = superSpaces.list();
        if(!spaceBySuper.containsKey(superSpace)){
            return null;
        }
        FSpace[] entry = (FSpace[]) spaceBySuper.get(superSpace);
        for(int i = 0; i<entry.length; i++){
            if(entry[i].action().getClass() == testClass){
                matching.addElement(entry[i]);
            }
        }
        if(matching.isEmpty()){
            return null;
        }
        else{
            FSpace[] returnArray = new FSpace[matching.size()];
            for(int i = 0; i<matching.size(); i++){
                returnArray[i] = (FSpace) matching.elementAt(i);
            }
            return new FList(returnArray);
        }
    }
    /*public FList adjacentSpaces(){
        return adjacentSpaces;
    }*/
    public FList adjacentSpaces(){
        FSpace[] list = superSpaces.list();
        for(int i = 0; i<list.length; i++){
            adjacentSpaces.merge(((FSpace) list[i]).subSpaces);
        }
        return adjacentSpaces;
    }
    /*public FList adjacentSpaces(){
        FSpace[] spaces = new FSpace[0];
        FSpace[] supers = superSpaces.list();
        for(int i = 0; i<supers.length; i++){
            spaces = merge(spaces, (FSpace[]) spaceBySuper.get(supers[i]));
        }
        return new FList(spaces);
    }
    /*public static FList adjacentByAction(Runnable action){
        return spacesBySuperSpace(spaceByAction(action).superSpace);
    }*/
    public FList adjacentByAction(Runnable action){
        return adjacentSpaces.spacesWithAction(action);
    }
    public static FList spacesBySuperSpace(FSpace superSpace){
        return new FList((FSpace[]) spaceBySuper.get(superSpace));
    }
    public FSpace nextSpace(FSpace superSpace){
        if(!spaceBySuper.containsKey(superSpace)){
            return null;
        }
        FSpace[] entry = (FSpace[]) spaceBySuper.get(superSpace);
        for(int i = 0; i<entry.length; i++){
            if(entry[i] == this && i != entry.length - 1){
                return entry[i + 1];
            }
        }
        return null;
    }
    public void run(){
        try{action.run();} //@TODO Test and revise exception handling
        catch(Exception e){
            this.set(false);
        }
    }
    public void set(boolean live){
        if(action instanceof Killable){
            ((Killable) action).set(live);
        }
        if(action() instanceof Killable){
            ((Killable) action()).set(live);
        }
    }
    public void feedData(Object data){
        if(action() instanceof StreamTask){
            ((StreamTask) action()).feedData(data);
        }
    }
    public PackLog packLog(){
        if(action() instanceof StreamTask){
            return ((StreamTask) action()).packLog();
        }
        return null;
    }
    public PackageList acceptedPacks(){
        Class[] list = new Class[0];
        if(action() instanceof StreamTask){
            return ((StreamTask) action()).acceptedPacks();
        }
        return new PackageList(list);
    }
    public Runnable action(){
        if(!(action instanceof TaskObscure)){
            return action;
        }
        else{
            return ((TaskObscure) action).action();
        }
    }
    private FSpace[] merge(FSpace[] a, FSpace[] b){
        FSpace[] returnArray = new FSpace[a.length + b.length];
        System.arraycopy(a, 0, returnArray, 0, a.length);
        System.arraycopy(b, a.length, returnArray, 0, b.length);
        return returnArray;
    }
    private static FSpace[] toArray(FSpace space){
        FSpace[] a = {space};
        return a;
    }
}
