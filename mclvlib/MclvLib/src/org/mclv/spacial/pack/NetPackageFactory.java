/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial.pack;

import org.mclv.spacial.FList;
import org.mclv.spacial.system.LoopTask;

/**
 *
 * @author Developer
 */
public class NetPackageFactory implements Runnable{
    private PackageBuffer buff;
    private FList dest;
    private LoopTask task;
    public NetPackageFactory(FList dest){
        buff = new PackageBuffer(NetPackage.class,1000);
        this.dest = dest;
        task = new LoopTask(this,10,0);
    }
    public void run(){
        primeAndSend();
    }
    private void primeAndSend(){
        if(buff.hasData()){
            Package[] packs = buff.newBuffer();
            for(int i = 0; i<packs.length; i++){
                NetPackage thisPack = (NetPackage) packs[i];
                if(!thisPack.primed()){
                    thisPack.compute();
                }
                dest.sendPack((OutPackage) thisPack);
            }
        }
    }
    public void addPack(NetPackage pack){
        buff.addPack(pack);
    }
}
