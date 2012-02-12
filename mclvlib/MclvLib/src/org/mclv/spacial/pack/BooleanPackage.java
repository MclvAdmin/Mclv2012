/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial.pack;

/**
 *
 * @author God
 */
public class BooleanPackage extends Package{
    private boolean val;
    public BooleanPackage(boolean val){
        super();
        this.val = val;
    }
    public boolean booleanValue(){ //same as Boolean method
        return val;
    }
}
