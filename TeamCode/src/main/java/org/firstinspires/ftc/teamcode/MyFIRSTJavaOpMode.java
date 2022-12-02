package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.List;

@TeleOp

public class MyFIRSTJavaOpMode extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftFrontDrive = null;
    private DcMotor leftBackDrive = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor rightBackDrive = null;

    private DcMotor servoSpool = null;
    private DcMotor servoArm = null;

    @Override
    public void runOpMode() {
        // Initialize the hardware variables. Note that the strings used here must correspond
        // to the names assigned during the robot configuration step on the DS or RC devices.

        List<DcMotor> devices = hardwareMap.getAll(DcMotor.class);

        leftFrontDrive = hardwareMap.get(DcMotor.class, "left_front_drive");
        leftBackDrive = hardwareMap.get(DcMotor.class, "left_back_drive");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "right_front_drive");
        rightBackDrive = hardwareMap.get(DcMotor.class, "right_back_drive");

        servoSpool = hardwareMap.get(DcMotor.class, "spool");
        servoArm = hardwareMap.get(DcMotor.class, "arm");

        // I think this removes the need for an encoder cable

        servoArm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        // servoSpool.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // By setting these values to new Gamepad(), they will default to all
        // boolean values as false and all float values as 0
        Gamepad currentGamepad1 = new Gamepad();

        Gamepad previousGamepad1 = new Gamepad();

        Gamepad currentGamepad2 = new Gamepad();

        Gamepad previousGamepad2 = new Gamepad();


        // ########################################################################################
        // !!!            IMPORTANT Drive Information. Test your motor directions.            !!!!!
        // ########################################################################################
        // Most robots need the motors on one side to be reversed to drive forward.
        // The motor reversals shown here are for a "direct drive" robot (the wheels turn the same direction as the motor shaft)
        // If your robot has additional gear reductions or uses a right-angled drive, it's important to ensure
        // that your motors are turning in the correct direction.  So, start out with the reversals here, BUT
        // when you first test your robot, push the left joystick forward and observe the direction the wheels turn.
        // Reverse the direction (flip FORWARD <-> REVERSE ) of any wheel that runs backward
        // Keep testing until ALL the wheels move the robot forward when you push the left joystick forward.
        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);


        // Wait for the game to start (driver presses PLAY)
        telemetry.addData("Status", "Initialized");
        telemetry.addData("Devices", devices);
        telemetry.update();

        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            // Store the gamepad values from the previous loop iteration in
            // previousGamepad1/2 to be used in this loop iteration.
            // This is equivalent to doing this at the end of the previous
            // loop iteration, as it will run in the same order except for
            // the first/last iteration of the loop.
            previousGamepad1.copy(currentGamepad1);

            // Store the gamepad values from this loop iteration in
            // currentGamepad1/2 to be used for the entirety of this loop iteration.
            // This prevents the gamepad values from changing between being
            // used and stored in previousGamepad1/2.
            currentGamepad1.copy(gamepad1);

            previousGamepad2.copy(currentGamepad2);

            currentGamepad2.copy(gamepad2);


            double max;

            // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
            double axial = -gamepad1.left_stick_y;  // Note: pushing stick forward gives negative value
            double lateral = gamepad1.left_stick_x;
            double yaw = gamepad1.right_stick_x;

            // Combine the joystick requests for each axis-motion to determine each wheel's power.
            // Set up a variable for each drive wheel to save the power level for telemetry.
            double leftFrontPower = axial + lateral + yaw;
            double rightFrontPower = axial - lateral - yaw;
            double leftBackPower = axial - lateral + yaw;
            double rightBackPower = axial + lateral - yaw;

            // Normalize the values so no wheel power exceeds 100%
            // This ensures that the robot maintains the desired motion.
            max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
            max = Math.max(max, Math.abs(leftBackPower));
            max = Math.max(max, Math.abs(rightBackPower));

            if (max > 1.0) {
                leftFrontPower /= max;
                rightFrontPower /= max;
                leftBackPower /= max;
                rightBackPower /= max;
            }


            /*
            // This is test code:
            //
            // Uncomment the following code to test your motor directions.
            // Each button should make the corresponding motor run FORWARD.
            //   1) First get all the motors to take to correct positions on the robot
            //      by adjusting your Robot Configuration if necessary.
            //   2) Then make sure they run in the correct direction by modifying the
            //      the setDirection() calls above.
            // Once the correct motors move in the correct direction re-comment this code.

            // Test code for DCMotor set to 1.0
            // double leftFrontPower = 1.0

            double leftFrontPower = gamepad1.x ? 1.0 : 0.0;  // X gamepad
            double leftBackPower = gamepad1.a ? 1.0 : 0.0;  // A gamepad
            double rightFrontPower = gamepad1.y ? 1.0 : 0.0;  // Y gamepad
            double rightBackPower = gamepad1.b ? 1.0 : 0.0;  // B gamepad*/


            /*
            FOLLOW THESE STEPS FOR SERVO CONTROL
            https://gm0.org/en/latest/docs/software/tutorials/gamepad.html?highlight=gamepad#storing-gamepad-state

            Storing the previous state of the GamePad assures that their is a controlled state
            and prevents from race conditions and unpredictable motor behavior
            from interfering because of latency from actions performed by the driver.

             */

            // https://gm0.org/en/latest/docs/software/tutorials/gamepad.html?highlight=gamepad#rising-edge-detector

            /*
            This is the Servo Spool at the front of the robot which lifts the telescoping pulley
             */

            if (currentGamepad2.y && !previousGamepad2.y) {
                servoSpool.setPower(1.0);
            }

            if (currentGamepad2.a && !previousGamepad2.a) {
                servoSpool.setPower(-1.0);
            }

            boolean eitherWerePreviouslyPressed = previousGamepad2.a || previousGamepad2.y;
            // If true set the motor speed to 0

            boolean bothAreReleased = !currentGamepad2.a && !currentGamepad2.y;

            if (bothAreReleased == true && eitherWerePreviouslyPressed == true) {
                servoSpool.setPower(0);
            }

            if (currentGamepad2.x && !previousGamepad2.x) {
                servoArm.setPower(0.25);
            }

            if (currentGamepad2.b && !previousGamepad2.b) {
                servoArm.setPower(-0.5);
            }

            boolean eitherWerePreviouslyPressedHand = previousGamepad2.x || previousGamepad2.b;
            // If true set the motor speed to 0

            boolean bothAreReleasedHand = !currentGamepad2.x && !currentGamepad2.b;

            if (bothAreReleasedHand == true && eitherWerePreviouslyPressedHand == true) {
                servoArm.setPower(0);
            }


            // Send calculated power to wheels
            leftFrontDrive.setPower(leftFrontPower);
            rightFrontDrive.setPower(rightFrontPower);
            leftBackDrive.setPower(leftBackPower);
            rightBackDrive.setPower(rightBackPower);
            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Front left/Right", "%4.2f, %4.2f", leftFrontPower, rightFrontPower);
            telemetry.addData("Back  left/Right", "%4.2f, %4.2f", leftBackPower, rightBackPower);
//            telemetry.addData("Servo Spool", "%4.2f", servoSpool);
            telemetry.update();
        }
    }
}


