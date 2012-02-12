/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial.pack;

/**
 *
 * @author God
 */
public class DoublePackage extends Package{
    private double val;
    public DoublePackage(double val){
        super();
        this.val = val;
    }
    public double doubleValue(){
        return val;
    }
    public int dataSize(){
        return 1;
    }
}
