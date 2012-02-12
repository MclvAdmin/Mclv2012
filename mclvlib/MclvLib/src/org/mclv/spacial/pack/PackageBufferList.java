/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial.pack;

import java.util.Hashtable;
import org.mclv.spacial.util.Array;

/**
 *
 * @author God
 */
public class PackageBufferList { //@TODO complete
    private PackageBuffer[] buffers;
    public PackageBufferList(PackageBuffer[] buffers){
        this.buffers = buffers;
    }
    public void addBuffer(PackageBuffer buffer){
        PackageBuffer[] buff = {buffer};
        buffers = (PackageBuffer[]) Array.merge(buffers,buff);
    }
    public Hashtable newDataByClass(){
        return null;
    }
}
