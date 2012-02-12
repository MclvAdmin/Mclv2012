/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial.pack;

/**
 *
 * @author romana
 */
public class NavPackage {
    private double[] dest = {0,0};
    private boolean halt = false;
    public NavPackage(boolean halt){
        this.halt = halt;
    }
    public NavPackage(double x, double y){
        dest[0] = x;
        dest[1] = y;
    }
    public double getX(){
        return dest[0];
    }
    public double getY(){
        return dest[1];
    }
    public double[] dest(){
        return dest;
    }
    public boolean halt(){
        return halt;
    }
}
