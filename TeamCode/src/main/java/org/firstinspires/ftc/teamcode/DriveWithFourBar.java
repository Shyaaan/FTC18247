package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "Drive_With_FourBarV1")
public class DriveWithFourBar extends LinearOpMode {

    private DcMotor front_left_drive;
    private DcMotor rear_left_drive;
    private DcMotor front_right_drive;
    private DcMotor rear_right_drive;
    private DcMotor arm_motor_a;
    private DcMotor arm_motor_b;

    /**
     * This function is executed when this Op Mode is selected from the Driver Station.
     */
    @Override
    public void runOpMode() {
        front_left_drive = hardwareMap.get(DcMotor.class, "front_left_drive");
        rear_left_drive = hardwareMap.get(DcMotor.class, "rear_left_drive");
        front_right_drive = hardwareMap.get(DcMotor.class, "front_right_drive");
        rear_right_drive = hardwareMap.get(DcMotor.class, "rear_right_drive");

        arm_motor_a = hardwareMap.get(DcMotor.class, "FourBarLiftKitA");
        arm_motor_a.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        arm_motor_a.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        // Put initialization blocks here.
        front_left_drive.setDirection(DcMotorSimple.Direction.REVERSE);
        rear_left_drive.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

        if (opModeIsActive()) {
            // Put run blocks here.
            while (opModeIsActive()) {
                // Put loop blocks here.
                drive();
                int position = arm_motor_a.getCurrentPosition();
                telemetry.addLine("Motor A Position: " + String.valueOf(position));
                telemetry.update();
                sleep(20);
            }
            ((DcMotorEx) arm_motor_a).setVelocity(0);
            arm_motor_a.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        }
    }

    /**
     * Describe this function...
     */
    private void drive() {
        //Drive variables
        double vertical;
        double horizontal;
        double pivot;

        //Arm variables
        double Collective;
        double Individual;

        //Drive controls

        float LeftStickY = -gamepad1.left_stick_y;
        LeftStickY = Math.round(LeftStickY);
        float LeftStickX = -gamepad1.left_stick_x;
        LeftStickX = Math.round(LeftStickX);
        float RightStickX = -gamepad1.right_stick_x;
        RightStickX = Math.round(RightStickX);
        float RightStickY = -gamepad1.right_stick_y;
        RightStickY = Math.round(RightStickY);

        //Arm controls

        float LeftArmY = gamepad2.left_stick_y;
        float RightArmY = gamepad2.right_stick_y;

        double SpeedCoefficient = 100;
        double Speed = SpeedCoefficient * RightArmY;

        int Position =  arm_motor_a.getCurrentPosition();
        int Limit = -2220;

        if (Position <= .95 * Limit){
            Speed = 1.0/2.0*Speed;
        }
        if (Position < Limit) {
            Speed = SpeedCoefficient;
        }

        ((DcMotorEx) arm_motor_a).setVelocity(Speed);

        telemetry.addLine("Right Stick Y: " + RightArmY + " Speed: " + Speed);

        vertical = SpeedFormula(LeftStickY) * 2700;
        horizontal = SpeedFormula(LeftStickX) * 2700;
        pivot = SpeedFormula(-RightStickX) * 2700;
        //((DcMotorEx) front_left_drive).setVelocity((vertical - horizontal) + pivot);
        //((DcMotorEx) front_right_drive).setVelocity((vertical + horizontal) - pivot);
        //((DcMotorEx) rear_left_drive).setVelocity(vertical + horizontal + pivot);
        //((DcMotorEx) rear_right_drive).setVelocity((vertical - horizontal) - pivot);
    }
    private double SpeedFormula(float x){
        return x/3 + 2.0/3.0*Math.pow(x,5);
    }

}