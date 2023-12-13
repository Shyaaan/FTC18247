package org.firstinspires.ftc.teamcode;

import android.widget.ImageView;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
@TeleOp(name="TeamObjectV1")
public class Vision extends LinearOpMode{
    TeamElementPipeline yourPipeline = new TeamElementPipeline();

    public void initCamera(){
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        WebcamName webcamName = hardwareMap.get(WebcamName.class, "Webcam 1");
        OpenCvCamera camera = OpenCvCameraFactory.getInstance().createWebcam(webcamName, cameraMonitorViewId);
        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                camera.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT);

            }
            @Override
            public void onError(int errorCode)
            {
                System.out.println(errorCode);
                telemetry.addLine("Camera Error: " + errorCode);
            }
        });

        camera.setPipeline(yourPipeline);


    }
    public void runOpMode(){
        initCamera();

        waitForStart();
        if (opModeIsActive()) {
            initCamera();
            while (opModeIsActive()){
                sleep(20);
            }
        }
    }


}