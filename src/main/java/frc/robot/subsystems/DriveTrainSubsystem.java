/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.FollowerType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.RemoteSensorSource;
import com.ctre.phoenix.motorcontrol.StatorCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;
import com.ctre.phoenix.motorcontrol.TalonFXInvertType;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.ctre.phoenix.sensors.PigeonIMU_StatusFrame;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.DriveTrainConstants;

public class DriveTrainSubsystem extends SubsystemBase {
  private final TalonFX MotorL1 = new TalonFX(DriveTrainConstants.MotorL1ID);
  private final TalonFX MotorR1 = new TalonFX(DriveTrainConstants.MotorR1ID);
  private final TalonFX MotorL2 = new TalonFX(DriveTrainConstants.MotorL2ID);
  private final TalonFX MotorR2 = new TalonFX(DriveTrainConstants.MotorR2ID);

  // for pidgeon
  private final PigeonIMU _pidgey = new PigeonIMU(15);

  double TurnSensorUnits = 0;
  int AtTurnCnt = 0;

  double TargetSensorUnits = 0;
  int AtTargetCnt = 0;

  public DriveTrainSubsystem() {

    // MotorR1.configOpenloopRamp(0.9, 0); //For thing just ask Braden
    // MotorL1.configOpenloopRamp(0.9, 0);
    // Invert Directions for Left and Right
    TalonFXInvertType _leftInvert = TalonFXInvertType.CounterClockwise; // Same as invert = "false"
    TalonFXInvertType _rightInvert = TalonFXInvertType.Clockwise; // Same as invert = "true"

    // ** Config Objects for motor controllers */
    TalonFXConfiguration _leftConfig = new TalonFXConfiguration();
    TalonFXConfiguration _rightConfig = new TalonFXConfiguration();

    // * Set Neutral Mode */
    MotorL1.setNeutralMode(NeutralMode.Brake);
    MotorR1.setNeutralMode(NeutralMode.Brake);
    MotorL2.setNeutralMode(NeutralMode.Brake);
    MotorR1.setNeutralMode(NeutralMode.Brake);

    // * Configure output and sensor direction */
    MotorL1.setInverted(_leftInvert);
    MotorR1.setInverted(_rightInvert);
    MotorL2.setInverted(_leftInvert);
    MotorR2.setInverted(_rightInvert);

    StatorCurrentLimitConfiguration currentConfig = new StatorCurrentLimitConfiguration(true, 35, 40, 100);
    MotorL1.configStatorCurrentLimit(currentConfig);
    MotorR1.configStatorCurrentLimit(currentConfig);
    MotorL2.configStatorCurrentLimit(currentConfig);
    MotorR2.configStatorCurrentLimit(currentConfig);

    // * Reset Pigeon Configs */
    _pidgey.configFactoryDefault();

    // ** Feedback Sensor Configuration */

    // ** Distance Configs */

    // * Configure the left Talon's selected sensor as integrated sensor */
    _leftConfig.primaryPID.selectedFeedbackSensor = FeedbackDevice.IntegratedSensor; // Local Feedback Source

    // * Configure the Remote (Left) Talon's selected sensor as a remote sensor for
    // the right Talon */
    _rightConfig.remoteFilter0.remoteSensorDeviceID = MotorL1.getDeviceID(); // Device ID of Remote Source
    _rightConfig.remoteFilter0.remoteSensorSource = RemoteSensorSource.TalonFX_SelectedSensor; // Remote Source Type

    // * Now that the Left sensor can be used by the master Talon,
    // * set up the Left (Aux) and Right (Master) distance into a single
    // * Robot distance as the Master's Selected Sensor 0. */
    setRobotDistanceConfigs(_rightInvert, _rightConfig);

    // * FPID for Distance */
    _rightConfig.slot0.kF = DriveTrainConstants.kGains_Distanc.kF;
    _rightConfig.slot0.kP = DriveTrainConstants.kGains_Distanc.kP;
    _rightConfig.slot0.kI = DriveTrainConstants.kGains_Distanc.kI;
    _rightConfig.slot0.kD = DriveTrainConstants.kGains_Distanc.kD;
    _rightConfig.slot0.integralZone = DriveTrainConstants.kGains_Distanc.kIzone;
    _rightConfig.slot0.closedLoopPeakOutput = DriveTrainConstants.kGains_Distanc.kPeakOutput;

    // ** Heading Configs */
    _rightConfig.remoteFilter1.remoteSensorDeviceID = _pidgey.getDeviceID(); // Pigeon Device ID
    _rightConfig.remoteFilter1.remoteSensorSource = RemoteSensorSource.Pigeon_Yaw; // This is for a Pigeon over CAN
    _rightConfig.auxiliaryPID.selectedFeedbackSensor = TalonFXFeedbackDevice.RemoteSensor1.toFeedbackDevice();
    _rightConfig.auxiliaryPID.selectedFeedbackCoefficient = 3600.0 / DriveTrainConstants.kPigeonUnitsPerRotation;

    // * false means talon's local output is PID0 + PID1, and other side Talon is
    // PID0 - PID1
    // * This is typical when the master is the right Talon FX and using Pigeon */
    // * true means talon's local output is PID0 - PID1, and other side Talon is
    // PID0 + PID1
    // * This is typical when the master is the left Talon FX and using Pigeon

    _rightConfig.auxPIDPolarity = false;
    // * FPID for Heading */
    _rightConfig.slot1.kF = DriveTrainConstants.kGains_Turning.kF;
    _rightConfig.slot1.kP = DriveTrainConstants.kGains_Turning.kP;
    _rightConfig.slot1.kI = DriveTrainConstants.kGains_Turning.kI;
    _rightConfig.slot1.kD = DriveTrainConstants.kGains_Turning.kD;
    _rightConfig.slot1.integralZone = DriveTrainConstants.kGains_Turning.kIzone;
    _rightConfig.slot1.closedLoopPeakOutput = DriveTrainConstants.kGains_Turning.kPeakOutput;

    // * Config the neutral deadband. */
    // _leftConfig.neutralDeadband = DriveTrainConstants.kNeutralDeadband; //another
    // thing to just ask Braden
    // _rightConfig.neutralDeadband = DriveTrainConstants.kNeutralDeadband;

    // **
    // * 1ms per loop. PID loop can be slowed down if need be.
    // * For example,
    // * - if sensor updates are too slow
    // * - sensor deltas are very small per update, so derivative error never gets
    // large enough to be useful.
    // * - sensor movement is very slow causing the derivative error to be near
    // zero.
    // */
    int closedLoopTimeMs = 1;
    MotorR1.configClosedLoopPeriod(0, closedLoopTimeMs, DriveTrainConstants.kTimeoutMs);
    MotorR1.configClosedLoopPeriod(1, closedLoopTimeMs, DriveTrainConstants.kTimeoutMs);

    // * Motion Magic Configs */
    _rightConfig.motionAcceleration =   (int) (10  * (6400 / (60 * 10))); // (distance units per 100 ms) per second -- Max speed in 1/10s
    _rightConfig.motionCruiseVelocity = (int) (0.8 * (6400 / (60 * 10))); // distance units per 100 ms // MAX 21700 -- 80% of max speed

    // * APPLY the config settings */
    MotorL1.configAllSettings(_leftConfig);
    MotorR1.configAllSettings(_rightConfig);

    MotorR1.configMotionSCurveStrength(2);

    // MotorR1.selectProfileSlot(DriveTrainConstants.kSlot_Distanc,DriveTrainConstants.PID_PRIMARY);
    // //PSPS Need to re-select he slots when switching modes.
    // MotorR1.selectProfileSlot(DriveTrainConstants.kSlot_Turning,DriveTrainConstants.PID_TURN);
    // //PSPS Need to re-select he slots when
    // switching modes.

    // * Set status frame periods to ensure we don't have stale data */
    // * These aren't configs (they're not persistent) so we can set these after the
    // configs. */
    MotorR1.setStatusFramePeriod(StatusFrame.Status_12_Feedback1, 20, DriveTrainConstants.kTimeoutMs);
    MotorR1.setStatusFramePeriod(StatusFrame.Status_13_Base_PIDF0, 20, DriveTrainConstants.kTimeoutMs);
    MotorR1.setStatusFramePeriod(StatusFrame.Status_14_Turn_PIDF1, 20, DriveTrainConstants.kTimeoutMs);
    MotorR1.setStatusFramePeriod(StatusFrame.Status_10_Targets, 10, DriveTrainConstants.kTimeoutMs);
    MotorL1.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, 5, DriveTrainConstants.kTimeoutMs);
    _pidgey.setStatusFramePeriod(PigeonIMU_StatusFrame.CondStatus_9_SixDeg_YPR, 5, DriveTrainConstants.kTimeoutMs);

    zeroSensors();
  }


  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    SmartDashboard.putNumber("turn", TurnSensorUnits);
    SmartDashboard.putNumber("distR", MotorR1.getSensorCollection().getIntegratedSensorPosition());
    SmartDashboard.putNumber("distL", MotorL1.getSensorCollection().getIntegratedSensorPosition());
    SmartDashboard.putNumber("setdist", TargetSensorUnits);
    SmartDashboard.putNumber("gyro", MotorR1.getSelectedSensorPosition(1));
  }


  void setRobotDistanceConfigs(TalonFXInvertType masterInvertType, TalonFXConfiguration masterConfig) {
    /**
     * Determine if we need a Sum or Difference.
     *
     * The auxiliary Talon FX will always be positive in the forward direction
     * because it's a selected sensor over the CAN bus.
     *
     * The master's native integrated sensor may not always be positive when forward
     * because sensor phase is only applied to *Selected Sensors*, not native sensor
     * sources. And we need the native to be combined with the aux (other side's)
     * distance into a single robot distance.
     */

    /*
     * THIS FUNCTION should not need to be modified. This setup will work regardless
     * of whether the master is on the Right or Left side since it only deals with
     * distance magnitude.
     */

    /* Check if we're inverted */
    if (masterInvertType == TalonFXInvertType.Clockwise) {
      /*
       * If master is inverted, that means the integrated sensor will be negative in
       * the forward direction. If master is inverted, the final sum/diff result will
       * also be inverted. This is how Talon FX corrects the sensor phase when
       * inverting the motor direction. This inversion applies to the *Selected
       * Sensor*, not the native value. Will a sensor sum or difference give us a
       * positive total magnitude? Remember the Master is one side of your drivetrain
       * distance and Auxiliary is the other side's distance. Phase | Term 0 | Term 1
       * | Result Sum: -1 *((-)Master + (+)Aux )| NOT OK, will cancel each other out
       * Diff: -1 *((-)Master - (+)Aux )| OK - This is what we want, magnitude will be
       * correct and positive. Diff: -1 *((+)Aux - (-)Master)| NOT OK, magnitude will
       * be correct but negative
       */

      masterConfig.diff0Term = FeedbackDevice.IntegratedSensor; // Local Integrated Sensor
      masterConfig.diff1Term = FeedbackDevice.RemoteSensor0; // Aux Selected Sensor
      masterConfig.primaryPID.selectedFeedbackSensor = FeedbackDevice.SensorDifference; // Diff0 - Diff1
    } else {
      /* Master is not inverted, both sides are positive so we can sum them. */
      masterConfig.sum0Term = FeedbackDevice.RemoteSensor0; // Aux Selected Sensor
      masterConfig.sum1Term = FeedbackDevice.IntegratedSensor; // Local IntegratedSensor
      masterConfig.primaryPID.selectedFeedbackSensor = FeedbackDevice.SensorSum; // Sum0 + Sum1
    }

    /*
     * Since the Distance is the sum of the two sides, divide by 2 so the total
     * isn't double the real-world value
     */
    masterConfig.primaryPID.selectedFeedbackCoefficient = 0.5;
  }


  public void setLeftMotors(double speed) {
    MotorL1.set(ControlMode.PercentOutput, speed * DriveTrainConstants.SpeedL);
    MotorL2.follow(MotorL1);
  }


  public void setRightMotors(double speed) {
    MotorR1.set(ControlMode.PercentOutput, speed * DriveTrainConstants.SpeedR);
    MotorR2.follow(MotorR1);
    TurnSensorUnits = MotorR1.getSelectedSensorPosition(1);
  }


  public void PIDArcade(double speed, double turn) {
    /* Calculate targets from gamepad inputs */
    if (Math.abs(speed) > 0.05) {
      TargetSensorUnits = (speed * DriveTrainConstants.kSensorUnitsPerRotation
          * DriveTrainConstants.kRotationsToTravel)
          + ((MotorL1.getSensorCollection().getIntegratedSensorPosition()
              - MotorR1.getSensorCollection().getIntegratedSensorPosition()) / 2);
    } else {
      TargetSensorUnits = ((MotorL1.getSensorCollection().getIntegratedSensorPosition()
          - MotorR1.getSensorCollection().getIntegratedSensorPosition()) / 2);
    }

    if (Math.abs(turn) > .05) {
      //TurnSensorUnits += -turn * DriveTrainConstants.turn_rate;
      TurnSensorUnits = (-turn * DriveTrainConstants.turn_rate) + MotorR1.getSelectedSensorPosition(1);
    } else {
      TurnSensorUnits = MotorR1.getSelectedSensorPosition(1);
    }

    /*
     * Configured for MotionMagic on Quad Encoders' Sum and Auxiliary PID on Pigeon
     */
    MotorR1.set(ControlMode.MotionMagic, TargetSensorUnits, DemandType.AuxPID, TurnSensorUnits);
    MotorL1.follow(MotorR1, FollowerType.AuxOutput1);
    MotorL2.follow(MotorR1, FollowerType.AuxOutput1);
    MotorR2.follow(MotorR1);
  }


  /** Zero all sensors, both Talons and Pigeon */
  public void zeroSensors() {
    MotorR1.set(ControlMode.PercentOutput, 0);
    MotorR2.follow(MotorR1);
    MotorL1.set(ControlMode.PercentOutput, 0);
    MotorL2.follow(MotorL1);
    TargetSensorUnits = 0;
    TurnSensorUnits = 0;
    MotorL1.getSensorCollection().setIntegratedSensorPosition(0, DriveTrainConstants.kTimeoutMs);
    MotorR1.getSensorCollection().setIntegratedSensorPosition(0, DriveTrainConstants.kTimeoutMs);
    _pidgey.setYaw(0, DriveTrainConstants.kTimeoutMs);
    _pidgey.setAccumZAngle(0, DriveTrainConstants.kTimeoutMs);
    System.out.println("[Quadrature Encoders + Pigeon] All sensors are zeroed.\n");
  }


  /** Zero QuadEncoders, used to reset position when initializing Motion Magic */
  void zeroDistance() {
    MotorR1.set(ControlMode.PercentOutput, 0);
    MotorR2.follow(MotorR1);
    MotorL1.set(ControlMode.PercentOutput, 0);
    MotorL2.follow(MotorL1);
    TargetSensorUnits = 0;
    MotorL1.getSensorCollection().setIntegratedSensorPosition(0, DriveTrainConstants.kTimeoutMs);
    MotorR1.getSensorCollection().setIntegratedSensorPosition(0, DriveTrainConstants.kTimeoutMs);
    System.out.println("[Quadrature Encoders] All encoders are zeroed.\n");
  }


  // in inches
  public void ToTarget(double dist) {

    AtTargetCnt = 0;
    TargetSensorUnits += (dist * (DriveTrainConstants.kSensorUnitsPerRotation * 10.75) / 6 * Math.PI);

    /*
     * Configured for MotionMagic on Quad Encoders' Sum and Auxiliary PID on Pigeon
     */
    MotorR1.set(ControlMode.MotionMagic, TargetSensorUnits, DemandType.AuxPID, TurnSensorUnits);
    MotorL1.follow(MotorR1, FollowerType.AuxOutput1);
    MotorL2.follow(MotorR1, FollowerType.AuxOutput1);
    MotorR2.follow(MotorR1);
  }


  public boolean AtTarget() {
    // 2048 == 1 rotation so 205 = 1/10 rotation
    if (Math.abs(TargetSensorUnits - ((MotorL1.getSensorCollection().getIntegratedSensorPosition()
        - MotorR1.getSensorCollection().getIntegratedSensorPosition()) / 2)) <= 205)
      AtTargetCnt++;
    else
      AtTargetCnt = 0;

    // 50 counts a sec
    if (AtTargetCnt > 10)
      return true;
    return false;
  }


  // Angle is 3600 per rotation
  public void Quickturn(double angle) {
    TurnSensorUnits = (TurnSensorUnits + angle);

    /*
     * Configured for MotionMagic on Quad Encoders' Sum and Auxiliary PID on Pigeon
     */
    MotorR1.set(ControlMode.MotionMagic, 0, DemandType.AuxPID, TurnSensorUnits);
    MotorL1.follow(MotorR1, FollowerType.AuxOutput1);
    MotorL2.follow(MotorR1, FollowerType.AuxOutput1);
    MotorR2.follow(MotorR1);
  }


  public boolean AtTurn() {
     // 10 == 1 degree so 10 = 1 degree
     if (Math.abs(TurnSensorUnits - MotorR1.getSelectedSensorPosition(1)) <= 10)
      AtTurnCnt++;
    else
      AtTurnCnt = 0;

    // 50 counts a sec
    if (AtTurnCnt > 10)
      return true;
    return false;
  }


  public double DirectionDeg(double RightstickX, double RightstickY) {
    if (Math.abs(RightstickX) < .5 && Math.abs(RightstickY) < .5)
      return -1;
    else if (RightstickY < 0 && RightstickX < 0)
      return ((Math.toDegrees(Math.atan2(RightstickY, RightstickX)) + 450) * 10);
    else
      return ((Math.toDegrees(Math.atan2(RightstickY, RightstickX)) + 90) * 10);
  }


  public double TurnyBoi(double Final) {
    if (Final == -1)
      return 0;
    double Remainder = (3600 - (TurnSensorUnits % 3600));
    double Turn = (Final + Remainder);
    double TurnValue = ((Turn) % 3600);
    if (TurnValue > 1800)
      return ((3600 - TurnValue) * -1);
    else
      return (TurnValue);
  }
}