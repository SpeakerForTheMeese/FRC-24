package frc.robot;

import edu.wpi.first.wpilibj.XboxController;

import static frc.robot.Constants.*;

public class TeleopController {
    // Instance Variables
    boolean joystickController = false;

    public TeleopController(boolean jsController) {
        joystickController = jsController;
    }

    public void teleopInit(Drivetrain driveTrain) {
        // Calibrate relative encoders to match absolute encoders
        driveTrain.calibrateSteering();
    }

    public void teleopPeriodic(XboxController m_stick, Drivetrain drivetrain, VisionController visionController) {
        double leftX = -m_stick.getLeftX();
        double leftY = m_stick.getLeftY();
        double leftAngle = getDriveAngle(leftX, leftY);

        double leftR = Math.sqrt(Math.pow(leftX, 2) + Math.pow(leftY, 2));
        double driveSpeed = (leftR < JOYSTICK_DEAD_ZONE) ? 0 : leftR * DRIVE_SPEED;

        double rightX = m_stick.getRightX();
        double rightY = m_stick.getRightY();
        double rightR = Math.sqrt(Math.pow(rightX, 2) + Math.pow(rightY, 2));
        double rightAngle = getHeadingAngle(rightX, rightY);

        // Check if either joystick is beyond the dead zone
        if (driveSpeed > 0 || rightR > JOYSTICK_DEAD_ZONE) {
            drivetrain.move(leftX, leftY, rightAngle); // Using Odometry
        } else
        if (m_stick.getAButton()) {
            drivetrain.steer(0);
        } else
        if (m_stick.getXButton()) {
            drivetrain.pointStraight();
        } else
        if (m_stick.getPOV() != -1) {
            double angle = ((double) m_stick.getPOV())/360.0*FULL_ROTATION;
            drivetrain.point(angle);
        }else
        if (m_stick.getRightBumperReleased()) {
            drivetrain.calibrateSteering();
        } else
        if(m_stick.getAButtonReleased() || m_stick.getBButtonReleased()){
            drivetrain.stopSteering();
        } else {
            drivetrain.stopSteering();
            drivetrain.drive(0);
        }

        drivetrain.periodic();

        drivetrain.updateShuffleboard();
    }

    // Previous (working)
//    public void teleopPeriodic(XboxController m_stick, Drivetrain drivetrain) {
//        double leftX = -m_stick.getLeftX();
//        double leftY = m_stick.getLeftY();
//        double theta = getDriveAngle(leftX, leftY);
//
//        double r = Math.sqrt(Math.pow(leftX, 2) + Math.pow(leftY, 2));
//        double driveSpeed = (r < JOYSTICK_DEAD_ZONE) ? 0 : r * DRIVE_SPEED * (joystickController ? 1 : (m_stick.getRawAxis(3)*0.25 + 0.4));
//
//        double rightX = joystickController ?
//                Math.abs(m_stick.getRightX()) < JOYSTICK_DEAD_ZONE ? 0 : -m_stick.getRightX() :
//                Math.abs(m_stick.getRawAxis(2)) < JOYSTICK_DEAD_ZONE ? 0 : -m_stick.getRawAxis(2) * (m_stick.getRawAxis(3)*0.25 + 0.4);
//
//        double turnSpeed = TURN_SPEED * rightX;
//
//        boolean doFieldOrientedDriving = !(m_stick.getLeftTriggerAxis() > TRIGGER_DEAD_ZONE);
//
//        // Check if either joystick is beyond the dead zone
//        if (driveSpeed > 0 || Math.abs(turnSpeed) > 0) {
//            drivetrain.move(theta, driveSpeed, turnSpeed, doFieldOrientedDriving);
//        } else if (m_stick.getPOV() != -1) {
//            drivetrain.pointStraight(((double) m_stick.getPOV())/360.0*FULL_ROTATION);
//        }
//        else
//        if (m_stick.getAButton()) {
//            drivetrain.zeroSteering();
//        } else
//        if (m_stick.getYButton()) {
//            drivetrain.steer(0);
//        } else
//        if (m_stick.getBButton()) {
//            drivetrain.steer(1);
//        } else
//        if (m_stick.getXButton()) {
//            drivetrain.pointStraight();
//        } else
//        if (m_stick.getRightBumperReleased()) {
//            drivetrain.calibrateSteering();
//        } else
//        if(m_stick.getAButtonReleased() || m_stick.getBButtonReleased()){
//            drivetrain.stopSteering();
//        } else {
//            drivetrain.stopSteering();
//            drivetrain.drive(0);
//        }
//
//        drivetrain.periodic();
//
//        drivetrain.updateShuffleboard();
//    }

    // Helper functions
    public double getDriveAngle(double x, double y) {
        return (((Math.atan2(y, -x))/Math.PI + 0.25*FULL_ROTATION) % FULL_ROTATION);
    }
    public double getHeadingAngle(double x, double y) {
        return (((Math.atan2(y, -x))/Math.PI + 0.0*FULL_ROTATION) % FULL_ROTATION);
    }
}
