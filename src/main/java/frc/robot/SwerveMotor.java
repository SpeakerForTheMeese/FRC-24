package frc.robot;

import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.SparkMaxAbsoluteEncoder.Type;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;

import static frc.robot.Constants.*;

public final class SwerveMotor {

    // Instance Variables
    private final PIDController pidController = new PIDController(STEER_KP, STEER_KI, STEER_KD);
    private final double offset;
    private double dynamicOffset = 0;
    private final CANSparkMax steerMotor;
    private final CANSparkMax driveMotor;
    // private final RelativeEncoder driveEncoder;
    // private final RelativeEncoder steerEncoder;
    private final AbsoluteEncoder steerAbsoluteEncoder;

    private double prevAngle = 0;
    private double directionFactor = 1;


    public SwerveMotor(int steerPort, int drivePort, double offset) {
        this.offset = offset;
        this.steerMotor = new CANSparkMax(steerPort,MotorType.kBrushless);
        this.driveMotor = new CANSparkMax(drivePort,MotorType.kBrushless);
        // this.steerEncoder = this.steerMotor.getEncoder();
        // this.driveEncoder = this.driveMotor.getEncoder();
        this.steerAbsoluteEncoder = this.steerMotor.getAbsoluteEncoder(Type.kDutyCycle);
    }

    public void calibrate() {
        this.steerMotor.getEncoder().setPosition(0);
        this.dynamicOffset = getAbsoluteSteeringPosition() * FULL_ROTATION;
    }

    public void zeroPosition() {
        steerMotor.set(pidController.calculate(getSteeringPosition(), this.getOffset()));
    }

    public void stopSteering() {
        steerMotor.set(0);
    }

    public void steer(double goalRotation){
        double goalAngle = prevAngle + closestAngle(prevAngle, goalRotation + this.getOffset());
        
        steerMotor.set(pidController.calculate(prevAngle, goalAngle));
        prevAngle = getSteeringPosition();
    }

    public void drive(double speed) {
        // driveMotor.setInverted(inverted);
        driveMotor.set(speed);
    }

    
    // Helper functions

    // This function is used to calculate the angle the wheel should be set to
    // based on the previous angle to determine which direction to turn

    // If the wheel is turning more than 90 degrees, then the wheel should spin in the opposite direction
    // and the drive wheel should spin in the opposite direction

    // https://compendium.readthedocs.io/en/latest/tasks/drivetrains/swerve.html
    private double closestAngle(double previous, double goal)
    {
        // get direction
        double dir = modulo(goal, FULL_ROTATION) - modulo(previous, FULL_ROTATION);
        
        // If rotation is greater than 180 degrees, then rotate swerve in the other way
        if (Math.abs(dir) > FULL_ROTATION/2)
        {
            dir = -(Math.signum(dir) * FULL_ROTATION) + dir;
        }

        // If rotation is greater than 90 degrees, then spin drive wheel in opposite direction
        if (Math.abs(dir) > FULL_ROTATION/4)
        {
            dir = Math.signum(dir) * (FULL_ROTATION/2 - Math.abs(dir));
            directionFactor *= -1;
        }

        return dir;
    }

    // For some reason the built-in modulo function didn't work...
    private static double modulo(double a, double b)
    {
        return a - b * Math.floor(a / b);
    }


    // Getters and Setters
    public double getOffset() {
        return (offset + dynamicOffset) % FULL_ROTATION;
    }
    public double getSteeringPosition() {
        return steerMotor.getEncoder().getPosition() / RELATIVE_ENCODER_RATIO * FULL_ROTATION;
    }
    public double getAbsoluteSteeringPosition() {
        return steerAbsoluteEncoder.getPosition();
    }

    public SwerveModulePosition getSwervePosition(){
        return new SwerveModulePosition(
                driveMotor.getEncoder().getPosition(), new Rotation2d(getSteeringPosition()));
    }
}