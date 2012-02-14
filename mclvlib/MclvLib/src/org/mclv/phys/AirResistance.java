/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.phys;

import org.mclv.math.RungeKutta4_O2;
import org.mclv.spacial.config.Constants;

/**
 *
 * @author God
 */
public class AirResistance extends RungeKutta4_O2{
    private static final double G = 9.80665;
    private static final double D_BALL = 0.47;
    private static final double R_BALL = 0.101063389;
    private static final double P_AIR = 1.225;
    private static final double CROSS_AREA = Math.PI*(R_BALL*R_BALL);
    private static final double M_BALL = 0.317514659;
    private static final double K = (D_BALL*P_AIR*CROSS_AREA)/M_BALL;
    public AirResistance(double[] vxyz, double dt){
        super(zero(vxyz.length),vxyz,0,dt);
    }
    public void fAt(double[] velI){
        initialY1 = velI;
    }
    public double[] vxyz(double[] position, double[] target){ //Simple method to return the proper firing velocity given
        double[][] val = inverse(position,target,zero(3),Constants.DEVIATION,Constants.VEL_STEP,Constants.MAX_TRAJ_TIME); //@TODO add v expression without air resistance
        return val[1];
    }
    public double F(int i,double x, double x1,double t){ //i is geometric index variable: 0 = x, 1 = y, 2 = z
        switch(i){
            case 0: return -0.5*K*(x1*x1)*sign(x1);
            case 1: return -0.5*K*(x1*x1)*sign(x1);
            case 2: return -0.5*K*(x1*x1)*sign(x1) -G;
        }
        return 0;
    }
    private static double sign(double val){
        if(val <0){
            return -1;
        }
        return 1;
    }
    public static double[] zero(int i){
        double[] ret = new double[i];
        for(int j = 0; j<ret.length; j++){
            ret[j] = 0;
        }
        return ret;
    }
}
