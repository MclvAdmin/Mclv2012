/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial.hardware;

import org.mclv.spacial.util.Sampleable;
import org.mclv.spacial.config.Constants;
import edu.wpi.first.wpilibj.Joystick;
import java.util.Vector;
/**
 *
 * @author God
 */
public class JoySamp implements Sampleable{
    private Joystick joy;
    public JoySamp(Joystick joy){
        this.joy = joy;
    }
    public Object getObj(){
        Vector returnVector = new Vector(0);
        Vector buttonVector = new Vector(0);
        Vector axisVector = new Vector(0);
        for(int i = 0; i<Constants.JOY_BUTTONS; i++){
            buttonVector.addElement((joy.getRawButton(i) ? Boolean.TRUE : Boolean.FALSE));
            if(i<Constants.JOY_AXES){
                axisVector.addElement(new Double(joy.getRawAxis(i)));
            }
        }
        returnVector.addElement(axisVector);
        returnVector.addElement(buttonVector);
        return returnVector;
    }
}
