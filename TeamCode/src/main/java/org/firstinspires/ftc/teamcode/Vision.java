package org.firstinspires.ftc.teamcode;


//import com.google.android.material.snackbar.Snackbar;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.firstinspires.ftc.robotcore.internal.ui.UILocation;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
@Disabled
@TeleOp(name="VisionV1.0.0")
public class Vision extends LinearOpMode{
    TeamElementPipeline yourPipeline = new TeamElementPipeline();
    OpenCvCamera camera;
    private DcMotorEx front_left_drive;
    private DcMotorEx rear_left_drive;
    private DcMotorEx front_right_drive;
    private DcMotorEx rear_right_drive;
    private String ObjectPosition;
    private static Vision Instance;
    private Map<String, Integer> tag_map = new HashMap<String, Integer>() {{
        put("LeftBlue", 1);
        put("CenterBlue", 2);
        put("RightBlue",3);
        put("LeftRed",4);
        put("CenterRed",5);
        put("RightRed",6);
    }};

    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;

    private static final boolean USE_WEBCAM = true;  // true for webcam, false for phone camera
    private AprilTagDetection TargetTag = null;
    public void initCamera(){
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        WebcamName webcamName = hardwareMap.get(WebcamName.class, "Webcam 1");
        camera = OpenCvCameraFactory.getInstance().createWebcam(webcamName, cameraMonitorViewId);
        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                AppUtil.getInstance().showToast(UILocation.ONLY_LOCAL, "Camera opened, starting stream.");
                camera.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT);

            }
            @Override
            public void onError(int errorCode)
            {
                System.out.println(errorCode);
                AppUtil.getInstance().showToast(UILocation.ONLY_LOCAL, "Error Starting Camera! Error #" + errorCode);
                telemetry.addLine("Camera Error: " + errorCode);
            }
        });

        yourPipeline.setCamera(camera);
        camera.setPipeline(yourPipeline);


    }
    public void runOpMode(){
        Instance = this;
        front_left_drive = (DcMotorEx) hardwareMap.get(DcMotor.class, "front_left_drive");
        rear_left_drive = (DcMotorEx) hardwareMap.get(DcMotor.class, "rear_left_drive");
        front_right_drive = (DcMotorEx) hardwareMap.get(DcMotor.class, "front_right_drive");
        rear_right_drive = (DcMotorEx) hardwareMap.get(DcMotor.class, "rear_right_drive");

        // Put initialization blocks here.
        front_left_drive.setDirection(DcMotorSimple.Direction.REVERSE);
        rear_left_drive.setDirection(DcMotorSimple.Direction.REVERSE);

        initCamera();
        initAprilTag();
        waitForStart();

        if (opModeIsActive()) {
            if (yourPipeline.getPosition() == yourPipeline.CENTER){
                ObjectPosition = yourPipeline.CENTER;
            }
            else {
                driveTarget(-200,0,200,0);
            }
            if (yourPipeline.getPosition() == yourPipeline.CENTER){
                ObjectPosition = yourPipeline.LEFT;
            }
            else {
                ObjectPosition = yourPipeline.RIGHT;
            }


            driveTarget(-100,0,0,100);
            if (!opModeIsActive()){
                visionPortal.close();
                return;
            }
            List<AprilTagDetection> currentDetections = aprilTag.getDetections();

            if (currentDetections.size() > 0){
                yourPipeline.setColor(getColorFromTag(currentDetections.get(0).id));
            }

            while (opModeIsActive()){
                telemetry.addLine(String.valueOf(opModeIsActive()));
                telemetry.addLine(yourPipeline.getPosition());
                telemetry.addLine(yourPipeline.getColor());
                telemetry.update();
                sleep(20);
            }
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
        telemetry.addLine("Target: " + String.valueOf(Target));
        telemetry.addLine("Position: " + String.valueOf(RL));
        telemetry.update();
        while ((direction*RL < direction*Target) && opModeIsActive()){
            drive(v * direction,h * direction,p * direction);
            sleep(20);
            RL = rear_left_drive.getCurrentPosition();
        }
        drive(0,0,0);
    }
    private int getTagId(String Color, String Position){
        return tag_map.get(Position+Color);
    }
    private String getColorFromTag(int id){
        String key = getKeyByValue(tag_map,id);
        if (key.contains("Blue")){
            return yourPipeline.BLUE;
        }
        else {
            return yourPipeline.RED;
        }
    }
    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet())
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        return null;
    }
    public void sendTelemetry(String String){
        telemetry.addLine(String);
    }
    public static Vision getInstance(){
        return Instance;
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
    public boolean OpmodeStatus(){
        return opModeIsActive();
    }
}