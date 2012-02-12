/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial.pack;

/**
 *
 * @author God
 */
public class PositionPackage extends Package{
    double angle;
    double[] xy;
    double[] vxy;
    public PositionPackage(double[] xy, double angle){
        super();
        this.xy = xy;
        this.angle = angle;
    }
    public PositionPackage(double[] xy, double angle, double[] vxy){
        this(xy,angle);
        this.vxy = vxy;
    }
    public boolean hasVelocity(){
        if(vxy != null){
            return true;
        }
        return false;
    }
    public double[] s(){
        return xy;
    }
    public double[] velocity(){
        return vxy;
    }
    public double angle(){
        return angle;
    }
}
