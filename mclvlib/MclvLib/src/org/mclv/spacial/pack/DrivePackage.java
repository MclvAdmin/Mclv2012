/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial.pack;

/**
 *
 * @author God
 */
public class DrivePackage extends Package{
    public static final DrivePackage NULL_PACKAGE = new DrivePackage();
    private double left = 0;
    private double right = 0;
    private DrivePackage(){}
    public DrivePackage(double left, double right){
        super();
        this.left = left;
        this.right = right;
    }
    public DrivePackage(double[] out){
        super();
        this.left = out[0];
        this.right = out[1];
    }
    public double left(){
        return left;
    }
    public double right(){
        return right;
    }
    public int dataSize(){
        return 1;
    }
}
