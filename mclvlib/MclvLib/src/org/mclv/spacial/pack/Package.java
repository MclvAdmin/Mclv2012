/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial.pack;

import org.mclv.spacial.config.Constants;

/**
 *
 * @author God
 */
public abstract class Package {
    private static short globId = 0;
    private Short id;
    public Package(){
        id = getId();
    }
    public int dataSize(){
        return 1;
    }
    public Short id(){
        return id;
    }
    private static Short getId(){
        globId++;
        if(globId > Constants.PACK_MAX_VAL){
            globId = 0;
        }
        return new Short(globId);
    }
}
