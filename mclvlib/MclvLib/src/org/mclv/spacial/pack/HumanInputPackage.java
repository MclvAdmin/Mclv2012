/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial.pack;

import org.mclv.spacial.config.Constants;
import java.util.Vector;
import edu.wpi.first.wpilibj.Timer;
/**
 *
 * @author God
 */
public class HumanInputPackage extends Package{
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int DRIVE = 2;
    public static final int AUX = 3;
    private Vector drive;
    private Vector aux;
    private double time;
    public HumanInputPackage(Vector drive, Vector aux){
        super();
        time = Timer.getFPGATimestamp();
        this.drive = drive;
        this.aux = aux;
    }
    public boolean button(int type, int side, int button){
        if(button >= Constants.JOY_BUTTONS){
            return false;
        }
        if(type == AUX){
            Vector buttonV = (Vector) ((Vector) drive.elementAt(side)).elementAt(1);
            return ((Boolean) buttonV.elementAt(button)).booleanValue();
        }
        else if(type == DRIVE && (side == LEFT || side == RIGHT)){
            Vector buttonV = (Vector) ((Vector) drive.elementAt(side)).elementAt(1);
            return ((Boolean) buttonV.elementAt(button)).booleanValue();
        }
        return false;
    }
    public boolean[] buttons(int type, int side){
        int length = Constants.JOY_BUTTONS;
        boolean[] returnArray = new boolean[length];
        Vector buttonV;
        if(type == DRIVE){
            buttonV = (Vector) ((Vector) drive.elementAt(side)).elementAt(1);
        }
        else{
            buttonV = (Vector) ((Vector) aux.elementAt(side)).elementAt(1);
        }
        for(int i = 0; i<length; i++){
            returnArray[i] = ((Boolean) buttonV.elementAt(i)).booleanValue();
        }
        return returnArray;
    }
    public double[] axes(int type, int side){
        int length = Constants.JOY_AXES;
        double[] returnArray = new double[length];
        Vector axisV;
        if(type == DRIVE){
            axisV = (Vector) ((Vector) drive.elementAt(side)).elementAt(0);
        }
        else{
            axisV = (Vector) ((Vector) aux.elementAt(side)).elementAt(0);
        }
        for(int i = 0; i<length; i++){
            returnArray[i] = ((Double) axisV.elementAt(i)).doubleValue();
        }
        return returnArray;
    }
    public double axis(int type, int side, int axis){
        if(axis >= Constants.JOY_AXES){
            return 0;
        }
        if(type == AUX){
            Vector axisV = (Vector) ((Vector) aux.elementAt(side)).elementAt(0);
            return ((Double) axisV.elementAt(axis)).doubleValue();
        }
        else if(type == DRIVE && (side == LEFT || side == RIGHT)){
            Vector axisV = (Vector) ((Vector) drive.elementAt(side)).elementAt(0);
            return ((Double) axisV.elementAt(axis)).doubleValue();
        }
        return 0;
    }
    public double time(){
        return time;
    }
    public double age(){
        return Timer.getFPGATimestamp() - time;
    }
    public int dataSize(){
        return 1;
    }
}
