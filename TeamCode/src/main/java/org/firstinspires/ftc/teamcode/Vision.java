package org.firstinspires.ftc.teamcode;


//import com.google.android.material.snackbar.Snackbar;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.firstinspires.ftc.robotcore.internal.ui.UILocation;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
@TeleOp(name="TeamObjectV1")
public class Vision extends LinearOpMode{
    TeamElementPipeline yourPipeline = new TeamElementPipeline();
    OpenCvCamera camera;
    private DcMotorEx front_left_drive;
    private DcMotorEx rear_left_drive;
    private DcMotorEx front_right_drive;
    private DcMotorEx rear_right_drive;

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

        camera.setPipeline(yourPipeline);


    }
    public void runOpMode(){
        front_left_drive = (DcMotorEx) hardwareMap.get(DcMotor.class, "front_left_drive");
        rear_left_drive = (DcMotorEx) hardwareMap.get(DcMotor.class, "rear_left_drive");
        front_right_drive = (DcMotorEx) hardwareMap.get(DcMotor.class, "front_right_drive");
        rear_right_drive = (DcMotorEx) hardwareMap.get(DcMotor.class, "rear_right_drive");

        // Put initialization blocks here.
        front_left_drive.setDirection(DcMotorSimple.Direction.REVERSE);
        rear_left_drive.setDirection(DcMotorSimple.Direction.REVERSE);

        initCamera();
        waitForStart();
        if (opModeIsActive()) {
            while (opModeIsActive()){
                sleep(20);
                //AppUtil.getInstance().showToast(UILocation.ONLY_LOCAL, yourPipeline.getPosition());
                telemetry.addLine(yourPipeline.getPosition());
                telemetry.update();
                String Position = yourPipeline.getPosition();
                if (Position == yourPipeline.LEFT){
                    drive(-100,-100,0);
                }
                if (Position == yourPipeline.RIGHT){
                    drive(-100,100,0);
                }
            }
            drive(0,0,0);
        }
    }
    public void drive(double vertical, double horizontal, double pivot){
        front_left_drive.setVelocity((vertical - horizontal) + pivot);
        front_right_drive.setVelocity((vertical + horizontal) - pivot);
        rear_left_drive.setVelocity(vertical + horizontal + pivot);
        rear_right_drive.setVelocity((vertical - horizontal) - pivot);
    }


}