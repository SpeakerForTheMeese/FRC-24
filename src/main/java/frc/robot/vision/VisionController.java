package frc.robot.vision;

import edu.wpi.first.math.geometry.Pose3d;
import frc.robot.vision.AprilTagFieldLayout;
import org.photonvision.PhotonCamera;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import org.photonvision.PhotonUtils;
import org.photonvision.targeting.PhotonTrackedTarget;


public class VisionController {
    private double yaw;

    private NetworkTable table;
    private RobotCamera camera = new RobotCamera("Camera", 0, 0, 0, 0, 0);

    private Pose3d measuredPose;

    public void periodic() {
        // This method will be called once per scheduler run

        var photonVisionLatestResult = camera.getLatestResult();
        SmartDashboard.putBoolean("Camera has Target", photonVisionLatestResult.hasTargets());
    
        if (photonVisionLatestResult.hasTargets()){
            var target = photonVisionLatestResult.getBestTarget();
            updateYaw(target);

            measuredPose = PhotonUtils.estimateFieldToRobotAprilTag(target.getBestCameraToTarget(), AprilTagFieldLayout.getTagPose(target.getFiducialId()), camera.getRelativeTransform());
        }

        updateShuffleboard();
    }

    public void updateShuffleboard() {
        SmartDashboard.putNumber("Yaw", yaw);
    }

    // PhotonVisionResult may need to be changed
    public void updateYaw(PhotonTrackedTarget target) {
        yaw = target.getYaw();
    }

    public VisionController() {
        table = NetworkTableInstance.getDefault().getTable("limelight");
    }


    // Getters and Setters
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getArea() {
        return area;
    }

    public double getTargetRelativeYaw() {
        return yaw;
    }

    public Pose3d getMeasuredPose() {
        return measuredPose;
    }
}
