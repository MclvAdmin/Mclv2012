/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial.system;

import org.mclv.spacial.pack.PriorityPackage;
import org.mclv.spacial.StreamTask;
import org.mclv.spacial.FSpace;

/**
 *
 * @author romana
 */
public class PriorityManager implements Runnable{
    private FSpace space;
    public PriorityManager(PriorityPackage priority){
        space = new FSpace(this);
    }
    public void run(){
        StreamTask[] currentTasks;
    }
}
