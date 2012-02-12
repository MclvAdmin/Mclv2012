/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mclv.spacial.hardware;

import edu.wpi.first.wpilibj.image.ColorImage;
import org.mclv.spacial.StreamTask;
import org.mclv.spacial.config.Constants;
import org.mclv.spacial.pack.AxisConfigPackage;
import org.mclv.spacial.pack.PackLog;
import org.mclv.spacial.pack.Package;
import org.mclv.spacial.pack.PackageList;
/**
 *
 * @author God
 */
public class AxisCamera implements Runnable,StreamTask{
    private edu.wpi.first.wpilibj.camera.AxisCamera cam;
    private int color;
    private int brightness;
    private edu.wpi.first.wpilibj.camera.AxisCamera.ResolutionT resolution;
    private PackLog packLog = new PackLog();
    public AxisCamera(){
        color = Constants.CAM_COLOR;
        brightness = Constants.CAM_BRIGHT;
        resolution = Constants.CAM_RES;
        cam = edu.wpi.first.wpilibj.camera.AxisCamera.getInstance();
        cam.writeResolution(resolution);
        cam.writeColorLevel(color);
        cam.writeBrightness(brightness);
    }
    public void run(){
        try{
            cam.getBrightness();
            ColorImage image = cam.getImage();
        }
        catch(Exception e){
            System.out.println(e.getClass());
        }
        
    }
    public void feedData(Object data){
        if(data instanceof Package){
            Package pack = (Package) data;
            packLog.newPack(pack);
            if(pack instanceof AxisConfigPackage){
                config((AxisConfigPackage) pack);
            }
        }
    }
    public PackageList acceptedPacks(){
        Class[] list = {AxisConfigPackage.class};
        return new PackageList(list);
    }
    public PackLog packLog(){
        return packLog;
    }
    private void config(AxisConfigPackage pack){
        cam.writeColorLevel(pack.color());
        cam.writeBrightness(pack.brightness());
        cam.writeExposureControl(pack.exposure());
        cam.writeResolution(pack.resolution());
        cam.writeExposurePriority(pack.exposurePriority());
        cam.writeRotation(pack.rotation());
    }
}
