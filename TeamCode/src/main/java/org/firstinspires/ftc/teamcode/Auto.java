package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@Autonomous(name="ParkingV1.0.0")
public class Auto extends LinearOpMode{
    private DcMotorEx front_left_drive;
    private DcMotorEx rear_left_drive;
    private DcMotorEx front_right_drive;
    private DcMotorEx rear_right_drive;

    private AprilTagProcessor aprilTag;
    private AprilTagDetection TargetTag;
    private VisionPortal visionPortal;
    private boolean hasPixel = false;
    private String TeamColor = "Not Found";

    private static final boolean USE_WEBCAM = true;  // true for webcam, false for phone camera
    public void runOpMode(){
        front_left_drive = (DcMotorEx) hardwareMap.get(DcMotor.class, "front_left_drive");
        rear_left_drive = (DcMotorEx) hardwareMap.get(DcMotor.class, "rear_left_drive");
        front_right_drive = (DcMotorEx) hardwareMap.get(DcMotor.class, "front_right_drive");
        rear_right_drive = (DcMotorEx) hardwareMap.get(DcMotor.class, "rear_right_drive");

        // Put initialization blocks here.
        front_left_drive.setDirection(DcMotorSimple.Direction.REVERSE);
        rear_left_drive.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

        if (opModeIsActive()) {
            driveTarget(300,600,0,0);
            driveTarget(-1000,0,1000,0);
        }
        visionPortal.close();
    }
    public void drive(double vertical, double horizontal, double pivot){
        front_left_drive.setVelocity((vertical - horizontal) + pivot);
        front_right_drive.setVelocity((vertical + horizontal) - pivot);
        rear_left_drive.setVelocity(vertical + horizontal + pivot);
        rear_right_drive.setVelocity((vertical - horizontal) - pivot);
    }
    public void driveTarget(int Target, int v, int h, int p){
        int direction = 1;

        int RL = rear_left_drive.getCurrentPosition();
        if (Target < 0){
            direction = -1;
        }
        Target += RL;
        telemetry.addLine("Target: " + Target);
        telemetry.addLine("Position: " + RL);
        telemetry.update();
        while ((direction*RL < direction*Target) && opModeIsActive()){
            drive(v * direction,h * direction,p * direction);
            sleep(20);
            RL = rear_left_drive.getCurrentPosition();
        }
        drive(0,0,0);
    }

    public void driveTilTag(int targetId) {
        TargetTag = null;
        int loops = 0;
        while (loops <= 360) {
            driveTarget(100,0,0,100);
            List<AprilTagDetection> CurrentDetections = aprilTag.getDetections();
            for (AprilTagDetection detection : CurrentDetections){
                if (detection.id == targetId){
                    TargetTag = detection;
                    return;
                }
            }
            loops += 1;
        }
    }

    /**
     * @param firstId
     * @param secondId
     * @return Integer of april tag found or -1 timed out.
     */
    public int driveTilEitherTag(int firstId, int secondId) {
        TargetTag = null;
        int loops = 0;
        while (loops <= 360) {
            driveTarget(100,0,0,100);
            List<AprilTagDetection> CurrentDetections = aprilTag.getDetections();
            for (AprilTagDetection detection : CurrentDetections){
                if (detection.id == firstId || detection.id == secondId){
                    //TargetTag = detection;
                    return detection.id;
                }

            }
            loops += 1;
        }
        return -1;
    }
    private void initAprilTag() {

        // Create the AprilTag processor.
        aprilTag = new AprilTagProcessor.Builder()
                //.setDrawAxes(false)
                //.setDrawCubeProjection(false)
                //.setDrawTagOutline(true)
                //.setTagFamily(AprilTagProcessor.TagFamily.TAG_36h11)
                //.setTagLibrary(AprilTagGameDatabase.getCenterStageTagLibrary())
                //.setOutputUnits(DistanceUnit.INCH, AngleUnit.DEGREES)

                // == CAMERA CALIBRATION ==
                // If you do not manually specify calibration parameters, the SDK will attempt
                // to load a predefined calibration for your camera.
                //.setLensIntrinsics(578.272, 578.272, 402.145, 221.506)

                // ... these parameters are fx, fy, cx, cy.

                .build();

        // Create the vision portal by using a builder.
        VisionPortal.Builder builder = new VisionPortal.Builder();

        // Set the camera (webcam vs. built-in RC phone camera).
        if (USE_WEBCAM) {
            builder.setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"));
        } else {
            builder.setCamera(BuiltinCameraDirection.BACK);
        }

        // Choose a camera resolution. Not all cameras support all resolutions.
        //builder.setCameraResolution(new Size(640, 480));

        // Enable the RC preview (LiveView).  Set "false" to omit camera monitoring.
        //builder.enableCameraMonitoring(true);

        // Set the stream format; MJPEG uses less bandwidth than default YUY2.
        //builder.setStreamFormat(VisionPortal.StreamFormat.YUY2);

        // Choose whether or not LiveView stops if no processors are enabled.
        // If set "true", monitor shows solid orange screen if no processors enabled.
        // If set "false", monitor shows camera view without annotations.
        //builder.setAutoStopLiveView(false);

        // Set and enable the processor.
        builder.addProcessor(aprilTag);
        builder.enableLiveView(false);
        // Build the Vision Portal, using the above settings.
        visionPortal = builder.build();

        // Disable or re-enable the aprilTag processor at any time.
        //visionPortal.setProcessorEnabled(aprilTag, true);

    }   // end method initAprilTag()
}