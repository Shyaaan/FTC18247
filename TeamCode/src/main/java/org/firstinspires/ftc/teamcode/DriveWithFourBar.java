package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import java.lang.reflect.Array;

@TeleOp(name = "Drive_With_FourBarV2")
public class DriveWithFourBar extends LinearOpMode {

    private DcMotorEx front_left_drive;
    private DcMotorEx rear_left_drive;
    private DcMotorEx front_right_drive;
    private DcMotorEx rear_right_drive;
    private DcMotorEx arm_motor_a;
    private DcMotorEx arm_motor_b;

    private int[] motor_positions = {0,0,0,0,0};

    /**
     * This function is executed when this Op Mode is selected from the Driver Station.
     */
    @Override
    public void runOpMode() {
        front_left_drive = (DcMotorEx) hardwareMap.get(DcMotor.class, "front_left_drive");
        rear_left_drive = (DcMotorEx) hardwareMap.get(DcMotor.class, "rear_left_drive");
        front_right_drive = (DcMotorEx) hardwareMap.get(DcMotor.class, "front_right_drive");
        rear_right_drive = (DcMotorEx) hardwareMap.get(DcMotor.class, "rear_right_drive");

        arm_motor_a = (DcMotorEx) hardwareMap.get(DcMotor.class, "FourBarLiftKitA");
        arm_motor_b = (DcMotorEx) hardwareMap.get(DcMotor.class, "FourBarLiftKitB");
        ResetMotor(arm_motor_a);
        ResetMotor(arm_motor_b);
        // Put initialization blocks here.
        front_left_drive.setDirection(DcMotorSimple.Direction.REVERSE);
        rear_left_drive.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

        if (opModeIsActive()) {
            // Put run blocks here.
            while (opModeIsActive()) {
                // Put loop blocks here.
                int position = arm_motor_a.getCurrentPosition();
                drive();
                telemetry.addLine("Motor A Position: " + position);
                telemetry.update();
                sleep(20);
            }
            arm_motor_a.setVelocity(0);
            arm_motor_b.setVelocity(0);
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

        float LeftArmY = -gamepad2.left_stick_y;
        float RightArmY = gamepad2.right_stick_y;

        ControlArm(arm_motor_a,RightArmY);
        ControlArm(arm_motor_b,LeftArmY);

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
    private void ControlArm(DcMotorEx motor, float controller) {
        double speed_coefficient = 450;
        double speed = speed_coefficient * controller;
        
        int position =  motor.getCurrentPosition();
        int limit = -2220;

        if (position <= .95 * limit){
            speed = 1.0/2.0*speed;
        }
        if (position <= .995 * limit) {
            speed = speed_coefficient;
        }

        if (Math.abs(speed) > 5) {
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor.setVelocity(speed);
            motor_positions[motor.getPortNumber()] = position;
        }
        else {
            motor.setTargetPosition(motor_positions[motor.getPortNumber()]);
            motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            motor.setPower(.3);
        }
        telemetry.addLine("Motor: " + motor.getPortNumber() + " Position: " + motor.getCurrentPosition());
        telemetry.addLine("Controller: " + controller + " Speed: " + speed);
        telemetry.addLine("Run Mode: " + motor.getMode().toString() + " Target Position: " + motor.getTargetPosition());
    }
    private void ResetMotor(DcMotorEx motor) {
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor.setVelocity(-100);
        sleep(100);
        motor.setVelocity(0);
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
}