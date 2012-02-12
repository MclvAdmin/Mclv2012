/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial.pack;

import org.mclv.spacial.net.PrimitiveStore;

/**
 *
 * @author God
 */
public class OutPackage extends NetPackage{
    public OutPackage(byte type, byte[] address, PrimitiveStore prims){ //PRIMS MUST BE ORDERED!
        super(type, address);
        this.prims = prims;
    }
}
