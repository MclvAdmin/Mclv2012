/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial.pack;

/**
 *
 * @author God
 */
public class SyncPackage extends Package{
    private Package pack;
    private int group;
    public SyncPackage(Package pack, int group){
        this.pack = pack;
        this.group = group;
    }
    public Package pack(){
        return pack;
    }
    public boolean match(Class packClass, int group){
        if(packClass != pack.getClass()){
            return false;
        }
        if(this.group != group){
            return false;
        }
        return true;
    }
}
