/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mclv.spacial.config;

import edu.wpi.first.wpilibj.camera.AxisCamera;
/**
 *
 * @author God
 */
public class Constants {
    //SHOOTER CALC
    public static final double MAX_TRAJ_TIME = 4;
    public static final double VEL_STEP = 0.05;
    public static final double[] DEVIATION = {0.02,0.02,0.02};
    
    //NET
    public static final int[] PORTS = {1140}; //the port(s) on which to connect to the mclv network server
    public static final String SERVER_IP = "10.11.55.6"; //The ip of the computer running mclv network server software
    public static final double STAT_TIMEOUT = 2; //The general network timeout value for a variety of actions including connection time and hearbeat interval
    
    //CAMERA
    public static final int CAM_COLOR = 100; //Default camera color setting
    public static final int CAM_BRIGHT = 10; //Default camera brightness setting
    public static final AxisCamera.ResolutionT CAM_RES = AxisCamera.ResolutionT.k640x480;
    public static final AxisCamera.ExposureT CAM_EXP = AxisCamera.ExposureT.automatic;
    public static final AxisCamera.ExposurePriorityT CAM_EXP_PRIOR = AxisCamera.ExposurePriorityT.imageQuality;
    public static final AxisCamera.RotationT CAM_ROT = AxisCamera.RotationT.k0;
    public static final AxisCamera.WhiteBalanceT CAM_BAL = AxisCamera.WhiteBalanceT.automatic;
    //@TODO make field of view regressions (functions of distance) for axis 206 and m1011
    
    //JAGUAR
    public static final boolean CAN_DRIVE = false;
    public static final int SAMPLE_SIZE = 15;
    public static final boolean RANDOM_SAMPLE = false; 
    public static final boolean AVERAGE = false;
    public static final double JAG_RATE = 7;
    public static final double JAG_MAX_STEP = 0.4;
    public static final boolean[] JAG_INVERSION = {true,false};
    public static final boolean JAG_SAFE = false;
    
    //INTERTIAL NAVIGATION
    public static final int INERTIAL_BUFFER = 1000;
    public static final double ANGLE_DEV = 2*Math.PI/180; //2 degree heading tolerance for navigation
    public static final double DISTANCE_DEV = 0.075; //7.5 cm accuracy tolerance for navigation
    public static final double TURN_RATE = 0.5;
    public static final double STRAIGHT_RATE = 0.75;
    
    
    //HUMAN INPUT
    public static final int DRIVE_STICKS = 2;
    public static final int AUX_STICKS = 0;
    
    public static final int JOY_BUTTONS = 10;
    public static final int JOY_AXES = 3;
    public static final int DRIVE_AXIS = 2;
    
    //DEFAULT BUFFER SIZE
    public static final int COMPUTATION_BUFFER_LENGTH = 1000;
    
    //GAME PHASES
    public static final int NULL_GPHASE = 0;
    public static final int AUTO_GPHASE = 1;
    public static final int TELEOP_GPHASE = 2;
    public static final int ENDGAME_GPHASE = 3;
    
    public static final double MIX_THRESHOLD = 0.02;
    
    public static final short PACK_MAX_VAL = 9999;
    
    //DEFAULT SAMPLE RATES
    public static final int DEFAULT_RATE = 100;
    public static final int INERTIAL_RATE = 150;
    
    //OPTIMIZATION (LOOP TASK)
    public static final long MIN_PERIOD = 1;
    public static final int OPT_INTERVAL = 5; //@TODO Make in terms of time
    
    public static final int TIME_INTERVAL = 20;
    
    //CRIO SLOTS
    public static final int ANALOG_SLOT = 1;
    public static final int DIGITAL_SLOT = 2;
    public static final int[] SLOTS_USED = {0,ANALOG_SLOT,DIGITAL_SLOT}; //MUST have zero slot
    
    public static final boolean debug = true;
}
