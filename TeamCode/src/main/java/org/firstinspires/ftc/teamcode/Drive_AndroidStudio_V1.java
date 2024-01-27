package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "PlainDrive")
public class Drive_AndroidStudio_V1 extends LinearOpMode {

    private DcMotor front_left_drive;
    private DcMotor rear_left_drive;
    private DcMotor front_right_drive;
    private DcMotor rear_right_drive;

    /**
     * This function is executed when this Op Mode is selected from the Driver Station.
     */
    @Override
    public void runOpMode() {
        front_left_drive = hardwareMap.get(DcMotor.class, "front_left_drive");
        rear_left_drive = hardwareMap.get(DcMotor.class, "rear_left_drive");
        front_right_drive = hardwareMap.get(DcMotor.class, "front_right_drive");
        rear_right_drive = hardwareMap.get(DcMotor.class, "rear_right_drive");

        // Put initialization blocks here.
        front_left_drive.setDirection(DcMotorSimple.Direction.REVERSE);
        rear_left_drive.setDirection(DcMotorSimple.Direction.REVERSE);
        waitForStart();
        if (opModeIsActive()) {
            // Put run blocks here.
            while (opModeIsActive()) {
                // Put loop blocks here.
                drive();
                telemetry.update();
            }
        }
    }

    /**
     * Describe this function...
     */
    private void drive() {
        double vertical;
        double horizontal;
        double pivot;
        int speed;

        float LeftStickY = -gamepad1.left_stick_y;
        LeftStickY = Math.round(LeftStickY);
        float LeftStickX = -gamepad1.left_stick_x;
        LeftStickX = Math.round(LeftStickX);
        float RightStickX = -gamepad1.right_stick_x;
        RightStickX = Math.round(RightStickX);
        float RightStickY = -gamepad1.right_stick_y;
        RightStickY = Math.round(RightStickY);

        vertical = SpeedFormula(LeftStickY) * 2700;
        horizontal = SpeedFormula(LeftStickX) * 2700;
        pivot = SpeedFormula(-RightStickX) * 2700;
        ((DcMotorEx) front_left_drive).setVelocity((vertical - horizontal) + pivot);
        ((DcMotorEx) front_right_drive).setVelocity((vertical + horizontal) - pivot);
        ((DcMotorEx) rear_left_drive).setVelocity(vertical + horizontal + pivot);
        ((DcMotorEx) rear_right_drive).setVelocity((vertical - horizontal) - pivot);
    }
    private double SpeedFormula(float x){
        return x/3 + 2.0/3.0*Math.pow(x,5);
    }

}