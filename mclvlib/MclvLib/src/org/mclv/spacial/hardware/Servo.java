/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mclv.spacial.hardware;

/**
 *
 * @author God
 */
public class Servo implements Runnable,Syncable{
    private int syncGroup = 0;
    public Servo(){
        
    }
    public void run(){
        
    }
    public void setSyncGroup(int group){
        syncGroup = group;
    }
    public int syncGroup(){
        return syncGroup;
    }
}
