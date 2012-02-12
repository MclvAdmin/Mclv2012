/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mclv.spacial.pack;

import edu.wpi.first.wpilibj.camera.AxisCamera;
import org.mclv.spacial.config.Constants;

/**
 *
 * @author God
 */
public class AxisConfigPackage extends Package{
    private int color = 0;
    private int brightness = 0;
    private AxisCamera.ResolutionT resolution;
    private AxisCamera.ExposureT exposure;
    private AxisCamera.ExposurePriorityT exposurePriority;
    private AxisCamera.RotationT rotation;
    private AxisCamera.WhiteBalanceT whiteBalance;
    private boolean live = true;
    public AxisConfigPackage(boolean live){
        this(live,Constants.CAM_COLOR,Constants.CAM_BRIGHT);
    }
    public AxisConfigPackage(boolean live, int color, int brightness){
        this(live,color,brightness,Constants.CAM_RES,Constants.CAM_EXP,Constants.CAM_EXP_PRIOR,Constants.CAM_ROT,Constants.CAM_BAL);
    }
    public AxisConfigPackage(boolean live, int color, int brightness, AxisCamera.ResolutionT resolution, AxisCamera.ExposureT exposure, AxisCamera.ExposurePriorityT exposurePriority, AxisCamera.RotationT rotation,AxisCamera.WhiteBalanceT whiteBalance){
        this.live = live;
        this.color = color;
        this.brightness = brightness;
        this.exposure = exposure;
        this.exposurePriority = exposurePriority;
        this.rotation = rotation;
        this.resolution = resolution;
        this.whiteBalance = whiteBalance;
    }
    public boolean live(){
        return live;
    }
    public int color(){
        return color;
    }
    public int brightness(){
        return brightness;
    }
    public AxisCamera.ResolutionT resolution(){
        return resolution;
    }
    public AxisCamera.ExposureT exposure(){
        return exposure;
    }
    public AxisCamera.ExposurePriorityT exposurePriority(){
        return exposurePriority;
    }
    public AxisCamera.RotationT rotation(){
        return rotation;
    }
    public AxisCamera.WhiteBalanceT whiteBalance(){
        return whiteBalance;
    }
}
