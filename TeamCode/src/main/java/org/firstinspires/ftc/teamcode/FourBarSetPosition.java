package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.HashMap;
import java.util.Map;

@TeleOp(name = "DriveFourBarV2.1.0")
public class FourBarSetPosition extends LinearOpMode {

    private DcMotorEx front_left_drive;
    private DcMotorEx rear_left_drive;
    private DcMotorEx front_right_drive;
    private DcMotorEx rear_right_drive;
    private DcMotorEx arm_motor_a;
    private DcMotorEx arm_motor_b;
    private DcMotorEx conveyor_motor;
    private Servo conveyor_gate;
    private final double ConveyorSpeed = -.6;
    private boolean ConveyorEnabled = false;
    private boolean SlowMode = true;
    private boolean Updated = false;
    private boolean GateOpened = false;

    private int[] motor_positions = {0,0,0,0,0};
    private static final Map<String,Integer> ARM_THREE = new HashMap<String,Integer>(){{
        put("A",0);
        put("B",0);
    }};
    private static final Map<String,Integer> ARM_TWO = new HashMap<String,Integer>(){{
        put("A",0);
        put("B",0);
    }};
    private static final Map<String,Integer> ARM_ONE = new HashMap<String,Integer>(){{
        put("A",0);
        put("B",0);
    }};
    private static final Map<String,Integer> ARM_ZERO = new HashMap<String,Integer>(){{
        put("A",0);
        put("B",0);
    }};
    private Map<String, Integer> CurrentArmPosition = ARM_ZERO;

    /**
     * This function is executed when this Op Mode is selected from the Driver Station.
     */
    @Override
    public void runOpMode() {
        front_left_drive = (DcMotorEx) hardwareMap.get(DcMotor.class, "front_left_drive");
        rear_left_drive = (DcMotorEx) hardwareMap.get(DcMotor.class, "rear_left_drive");
        front_right_drive = (DcMotorEx) hardwareMap.get(DcMotor.class, "front_right_drive");
        rear_right_drive = (DcMotorEx) hardwareMap.get(DcMotor.class, "rear_right_drive");

        conveyor_motor = (DcMotorEx) hardwareMap.get(DcMotor.class,"ConveyorMotor");
        conveyor_gate = hardwareMap.get(Servo.class,"PixelsShallNotPass");
        conveyor_gate.setPosition(.5);

        arm_motor_a = (DcMotorEx) hardwareMap.get(DcMotor.class, "FourBarLiftKitA");
        arm_motor_b = (DcMotorEx) hardwareMap.get(DcMotor.class, "FourBarLiftKitB");

        ResetMotor(arm_motor_a);
        ResetMotor(arm_motor_b);

        // Put initialization blocks here.
        front_left_drive.setDirection(DcMotorSimple.Direction.REVERSE);
        rear_left_drive.setDirection(DcMotorSimple.Direction.REVERSE);
        telemetry.addLine("Limits and controls will not operate correctly if the robot's arms do not start as low as they can go.");
        telemetry.update();
        waitForStart();

        if (opModeIsActive()) {
            // Put run blocks here.
            while (opModeIsActive()) {
                // Put loop blocks here.
                int position = arm_motor_a.getCurrentPosition();
                drive();
                telemetry.addLine("Motor A ID: " + arm_motor_a.getPortNumber());
                telemetry.addLine("Motor B ID: " + arm_motor_b.getPortNumber());

                telemetry.update();
                sleep(20);
            }
            arm_motor_a.setVelocity(0);
            arm_motor_b.setVelocity(0);
            conveyor_motor.setPower(0);
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
        boolean PadDown = gamepad2.dpad_down;
        boolean PadUp = gamepad2.dpad_up;
        boolean PadLeft = gamepad2.dpad_left;
        boolean PadRight = gamepad2.dpad_right;

        //Conveyor Controls
        boolean RightBumper_2 = gamepad2.right_bumper;
        boolean LeftBumper_2 = gamepad2.left_bumper;

        //Speed Controls
        boolean LeftBumper_1 = gamepad1.left_bumper;

        if (RightBumper_2){
            ConveyorEnabled = !ConveyorEnabled;
        }
        if (LeftBumper_2){
            GateOpened = !GateOpened;
        }
        if (!Updated && LeftBumper_1){
            SlowMode = !SlowMode;
            Updated = true;
        }
        else {
            Updated = false;
        }

        if (PadRight){
            CurrentArmPosition = ARM_ZERO;
        }
        if (PadUp){
            CurrentArmPosition = ARM_THREE;
        }
        if (PadLeft){
            CurrentArmPosition = ARM_TWO;
        }
        if (PadDown){
            CurrentArmPosition = ARM_ONE;
        }

        //ControlArm(arm_motor_a,RightArmY);
        //ControlArm(arm_motor_b,LeftArmY);

        vertical = SpeedFormula(LeftStickY) * 2700;
        horizontal = SpeedFormula(LeftStickX) * 2700;
        pivot = SpeedFormula(-RightStickX) * 2700;

        conveyor_motor.setPower(ConveyorSpeed * (ConveyorEnabled ? 1 : 0));
        conveyor_gate.setPosition(GateOpened ? .75 : .25);

        setArmPosition(CurrentArmPosition);

        ((DcMotorEx) front_left_drive).setVelocity((vertical - horizontal) + pivot);
        ((DcMotorEx) front_right_drive).setVelocity((vertical + horizontal) - pivot);
        ((DcMotorEx) rear_left_drive).setVelocity(vertical + horizontal + pivot);
        ((DcMotorEx) rear_right_drive).setVelocity((vertical - horizontal) - pivot);
    }
    private double SpeedFormula(float x){
        return (SlowMode ? 1.0 : 1.0/4.0) * (x/3.0 + 2.0/3.0*Math.pow(x,5));
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
        motor.setVelocity(-300);
        sleep(100);
        motor.setVelocity(0);
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
    private void setArmPosition(Map<String,Integer> Position){
        arm_motor_a.setTargetPosition(Position.get("A"));
        arm_motor_b.setTargetPosition(Position.get("B"));
        arm_motor_a.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        arm_motor_b.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        arm_motor_a.setPower(.5);
        arm_motor_b.setPower(.5);
        telemetry.addLine("Motor: " + arm_motor_b.getPortNumber() + " Position: " + arm_motor_b.getCurrentPosition());
        telemetry.addLine("Run Mode: " + arm_motor_b.getMode().toString() + " Target Position: " + arm_motor_b.getTargetPosition());
        telemetry.addLine("Motor: " + arm_motor_a.getPortNumber() + " Position: " + arm_motor_a.getCurrentPosition());
        telemetry.addLine("Run Mode: " + arm_motor_a.getMode().toString() + " Target Position: " + arm_motor_a.getTargetPosition());
    }
}