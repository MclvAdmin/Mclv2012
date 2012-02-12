package org.usfirst.frc;

/*
 * StartApplication.java
 *
 */
import com.sun.squawk.util.MathUtils;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.camera.AxisCameraException;
import edu.wpi.first.wpilibj.image.BinaryImage;
import edu.wpi.first.wpilibj.image.ColorImage;
import edu.wpi.first.wpilibj.image.NIVisionException;
import edu.wpi.first.wpilibj.image.ParticleAnalysisReport;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import edu.wpi.first.wpilibj.image.CriteriaCollection;
import edu.wpi.first.wpilibj.image.NIVision;

/**
 * The startApp method of this class is called by the VM to start the
 * application.
 * 
 * The manifest specifies this class as MIDlet-1, which means it will
 * be selected for execution.
 */
public class FRC_UserProgram extends MIDlet {
    private static final long PERIOD = 10;
    private static final double DISTANCE = 2.1336; //Meters
    private static final double TARGET_WIDTH = 0.6096;
    //private static final double HEIGHT_FUNCTION;
    private static final double TARGET_HEIGHT = 0.4573;
    private static final double AXIS_206_ANGLE = 55*Math.PI/180;
    private static final double AXIS_M1011_ANGLE = 47*Math.PI/180;
    private static final double EXPERIMENTAL_AXIS_M1011_ANGLE = 42.98*Math.PI/180;
    private static final double LENS_ANGLE = EXPERIMENTAL_AXIS_M1011_ANGLE;
    private static final double[] SAT_RANGE = {0,0.5};
    private AxisCamera cam;
    private CriteriaCollection cc = new CriteriaCollection();    
    
    
    private boolean live = true;
    protected void startApp() throws MIDletStateChangeException {
        init();
        while(live){
            runSleep();
        }
    }

    protected void pauseApp() {
    }

    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
    }
    private void runSleep(){
        run();
        try{Thread.sleep(PERIOD);} catch(InterruptedException e){System.out.println(e.getClass());}
    }
    private void run(){
        double[] widths = getTargetWidths(4);
        if(widths != null && widths.length != 0){
            double time = Timer.getFPGATimestamp();
            double[] angles = new double[widths.length];
            for(int i = 0; i<angles.length; i++){
                double w_0 = perpWidth(DISTANCE);
                angles[i] = MathUtils.acos(widths[i]/w_0);
            }
            System.out.println("Angles " + angles.length + " :");
            for(int i = 0; i<angles.length; i++){
                System.out.println(i + ": Width: " + widths[i] + " Angle: "+ angles[i]);
            }
            System.out.println("Time: " + (Timer.getFPGATimestamp() - time));
            System.out.println("----------------");
        }
    }
    private void init(){
        cam = AxisCamera.getInstance();
        cam.writeResolution(AxisCamera.ResolutionT.k320x240);
        cam.writeColorLevel(100);
        cam.writeBrightness(50);
        cc.addCriteria(NIVision.MeasurementType.IMAQ_MT_BOUNDING_RECT_WIDTH,0,300,false);
        cc.addCriteria(NIVision.MeasurementType.IMAQ_MT_BOUNDING_RECT_HEIGHT,100,300,false);
        //cam.writeExposureControl(AxisCamera.ExposureT.hold);
    }

    private double[] getTargetWidths(int num) {
        double[] ret;
        try {
            ColorImage image = cam.getImage();
            int satLow = (int) (255*SAT_RANGE[0]);
            int satHigh = (int) (255*SAT_RANGE[1]);
            BinaryImage binImage = image.thresholdHSL(0,255,satLow, satHigh, 116, 255);
            ParticleAnalysisReport[] particles = binImage.getOrderedParticleAnalysisReports();
            //binImage = binImage.removeSmallObjects(false, binImage.getNumberParticles()-num);
            //binImage = binImage.convexHull(false);
            binImage = binImage.particleFilter(cc);
            ret = new double[Math.min(particles.length,num)];
            for(int i = 0; i<ret.length; i++){
                ret[i] = (double) particles[i].boundingRectWidth;
                System.out.println("Area: " + particles[i].particleArea + " Height: " + particles[i].boundingRectHeight + " Width: "+particles[i].boundingRectWidth+" x: " + particles[i].center_mass_x_normalized + " y: " + particles[i].center_mass_y_normalized);
            }
            image.free();
            binImage.free();
            return ret;
        } catch (AxisCameraException ex) {
            ex.printStackTrace();
            return null;
        } catch (NIVisionException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    private double perpWidth(double distance){
        double ret = 0;
        double stageWidth = 2*distance*Math.tan(LENS_ANGLE/2);
        ret = (TARGET_WIDTH/stageWidth)*((double) cam.getResolution().width);
        //System.out.println("Stage: " + stageWidth + " pWidth: " + ret);
        System.out.println("Perp Width: " + ret);
        return ret;
    }
    
}
