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
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.TalonFXInvertType;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.ctre.phoenix.sensors.PigeonIMU_StatusFrame;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.DriveTrainConstants;

public class DriveTrainSubsystem extends SubsystemBase {
  private final TalonFX MotorL1 = new TalonFX(DriveTrainConstants.MotorL1ID);
  private final TalonFX MotorR1 = new TalonFX(DriveTrainConstants.MotorR1ID);
  private final TalonFX MotorL2 = new TalonFX(DriveTrainConstants.MotorL2ID);
  private final TalonFX MotorR2 = new TalonFX(DriveTrainConstants.MotorR2ID);
  
  //for pidgeon
  private final PigeonIMU _pidgey = new PigeonIMU(3);
  double SensorTurnValue;
  public DriveTrainSubsystem() {
/** Invert Directions for Left and Right */
TalonFXInvertType _leftInvert = TalonFXInvertType.CounterClockwise; //Same as invert = "false"
TalonFXInvertType _rightInvert = TalonFXInvertType.Clockwise; //Same as invert = "true"

/** Config Objects for motor controllers */
TalonFXConfiguration _leftConfig = new TalonFXConfiguration();
TalonFXConfiguration _rightConfig = new TalonFXConfiguration();

/* Set Neutral Mode */
MotorL1.setNeutralMode(NeutralMode.Brake);
MotorR1.setNeutralMode(NeutralMode.Brake);

/* Configure output and sensor direction */
MotorL1.setInverted(_leftInvert);
MotorR1.setInverted(_rightInvert);


/* Reset Pigeon Configs */
_pidgey.configFactoryDefault();


/** Feedback Sensor Configuration */

/** Distance Configs */

/* Configure the left Talon's selected sensor as integrated sensor */
_leftConfig.primaryPID.selectedFeedbackSensor = FeedbackDevice.IntegratedSensor; //Local Feedback Source

/* Configure the Remote (Left) Talon's selected sensor as a remote sensor for the right Talon */
_rightConfig.remoteFilter0.remoteSensorDeviceID = MotorL1.getDeviceID(); //Device ID of Remote Source
_rightConfig.remoteFilter0.remoteSensorSource = RemoteSensorSource.TalonFX_SelectedSensor; //Remote Source Type

/* Now that the Left sensor can be used by the master Talon,
 * set up the Left (Aux) and Right (Master) distance into a single
 * Robot distance as the Master's Selected Sensor 0. */
setRobotDistanceConfigs(_rightInvert, _rightConfig);

/* FPID for Distance */
_rightConfig.slot0.kF = DriveTrainConstants.kGains_Distanc.kF;
_rightConfig.slot0.kP = DriveTrainConstants.kGains_Distanc.kP;
_rightConfig.slot0.kI = DriveTrainConstants.kGains_Distanc.kI;
_rightConfig.slot0.kD = DriveTrainConstants.kGains_Distanc.kD;
_rightConfig.slot0.integralZone = DriveTrainConstants.kGains_Distanc.kIzone;
_rightConfig.slot0.closedLoopPeakOutput = DriveTrainConstants.kGains_Distanc.kPeakOutput;



/** Heading Configs */
_rightConfig.remoteFilter1.remoteSensorDeviceID = _pidgey.getDeviceID();    //Pigeon Device ID
_rightConfig.remoteFilter1.remoteSensorSource = RemoteSensorSource.Pigeon_Yaw; //This is for a Pigeon over CAN
_rightConfig.auxiliaryPID.selectedFeedbackSensor = FeedbackDevice.RemoteSensor1; //Set as the Aux Sensor
_rightConfig.auxiliaryPID.selectedFeedbackCoefficient = 3600.0 / DriveTrainConstants.kPigeonUnitsPerRotation; //Convert Yaw to tenths of a degree

/* false means talon's local output is PID0 + PID1, and other side Talon is PID0 - PID1
 *   This is typical when the master is the right Talon FX and using Pigeon
 * 
 * true means talon's local output is PID0 - PID1, and other side Talon is PID0 + PID1
 *   This is typical when the master is the left Talon FX and using Pigeon
 */
_rightConfig.auxPIDPolarity = false;

/* FPID for Heading */
_rightConfig.slot1.kF = DriveTrainConstants.kGains_Turning.kF;
_rightConfig.slot1.kP = DriveTrainConstants.kGains_Turning.kP;
_rightConfig.slot1.kI = DriveTrainConstants.kGains_Turning.kI;
_rightConfig.slot1.kD = DriveTrainConstants.kGains_Turning.kD;
_rightConfig.slot1.integralZone =DriveTrainConstants.kGains_Turning.kIzone;
_rightConfig.slot1.closedLoopPeakOutput =DriveTrainConstants.kGains_Turning.kPeakOutput;


/* Config the neutral deadband. */
_leftConfig.neutralDeadband =DriveTrainConstants.kNeutralDeadband;
_rightConfig.neutralDeadband =DriveTrainConstants.kNeutralDeadband;


/**
 * 1ms per loop.  PID loop can be slowed down if need be.
 * For example,
 * - if sensor updates are too slow
 * - sensor deltas are very small per update, so derivative error never gets large enough to be useful.
 * - sensor movement is very slow causing the derivative error to be near zero.
 */
int closedLoopTimeMs = 1;
MotorR1.configClosedLoopPeriod(0, closedLoopTimeMs,DriveTrainConstants.kTimeoutMs);
MotorR1.configClosedLoopPeriod(1, closedLoopTimeMs,DriveTrainConstants.kTimeoutMs);

/* Motion Magic Configs */
_rightConfig.motionAcceleration = 2000; //(distance units per 100 ms) per second
_rightConfig.motionCruiseVelocity = 2000; //distance units per 100 ms



/* APPLY the config settings */
MotorL1.configAllSettings(_leftConfig);
MotorR1.configAllSettings(_rightConfig);


/* Set status frame periods to ensure we don't have stale data */
/* These aren't configs (they're not persistant) so we can set these after the configs.  */
MotorR1.setStatusFramePeriod(StatusFrame.Status_12_Feedback1, 20,DriveTrainConstants.kTimeoutMs);
MotorR1.setStatusFramePeriod(StatusFrame.Status_13_Base_PIDF0, 20,DriveTrainConstants.kTimeoutMs);
MotorR1.setStatusFramePeriod(StatusFrame.Status_14_Turn_PIDF1, 20,DriveTrainConstants.kTimeoutMs);
MotorR1.setStatusFramePeriod(StatusFrame.Status_10_Targets, 10,DriveTrainConstants.kTimeoutMs);
MotorL1.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, 5,DriveTrainConstants.kTimeoutMs);
_pidgey.setStatusFramePeriod(PigeonIMU_StatusFrame.CondStatus_9_SixDeg_YPR , 5,DriveTrainConstants.kTimeoutMs);
}


  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
  public void setLeftMotors(double speed) {
    MotorL1.set(ControlMode.PercentOutput, speed * DriveTrainConstants.SpeedL);
    MotorL1.follow(MotorL2);
    }
    public void setRightMotors(double speed) {
      MotorR1.set(ControlMode.PercentOutput, speed * DriveTrainConstants.SpeedR);
      MotorR1.follow(MotorR2);
      }

	 void setRobotDistanceConfigs(TalonFXInvertType masterInvertType, TalonFXConfiguration masterConfig){
		/**
		 * Determine if we need a Sum or Difference.
		 * 
		 * The auxiliary Talon FX will always be positive
		 * in the forward direction because it's a selected sensor
		 * over the CAN bus.
		 * 
		 * The master's native integrated sensor may not always be positive when forward because
		 * sensor phase is only applied to *Selected Sensors*, not native
		 * sensor sources.  And we need the native to be combined with the 
		 * aux (other side's) distance into a single robot distance.
		 */

		/* THIS FUNCTION should not need to be modified. 
		   This setup will work regardless of whether the master
		   is on the Right or Left side since it only deals with
		   distance magnitude.  */

		/* Check if we're inverted */
		if (masterInvertType == TalonFXInvertType.Clockwise){
			/* 
				If master is inverted, that means the integrated sensor
				will be negative in the forward direction.
				If master is inverted, the final sum/diff result will also be inverted.
				This is how Talon FX corrects the sensor phase when inverting 
				the motor direction.  This inversion applies to the *Selected Sensor*,
				not the native value.
				Will a sensor sum or difference give us a positive total magnitude?
				Remember the Master is one side of your drivetrain distance and 
				Auxiliary is the other side's distance.
					Phase | Term 0   |   Term 1  | Result
				Sum:  -1 *((-)Master + (+)Aux   )| NOT OK, will cancel each other out
				Diff: -1 *((-)Master - (+)Aux   )| OK - This is what we want, magnitude will be correct and positive.
				Diff: -1 *((+)Aux    - (-)Master)| NOT OK, magnitude will be correct but negative
			*/

			masterConfig.diff0Term = FeedbackDevice.IntegratedSensor; //Local Integrated Sensor
			masterConfig.diff1Term = FeedbackDevice.RemoteSensor0;   //Aux Selected Sensor
			masterConfig.primaryPID.selectedFeedbackSensor = FeedbackDevice.SensorDifference; //Diff0 - Diff1
		} else {
			/* Master is not inverted, both sides are positive so we can sum them. */
			masterConfig.sum0Term = FeedbackDevice.RemoteSensor0;    //Aux Selected Sensor
			masterConfig.sum1Term = FeedbackDevice.IntegratedSensor; //Local IntegratedSensor
			masterConfig.primaryPID.selectedFeedbackSensor = FeedbackDevice.SensorSum; //Sum0 + Sum1
		}

		/* Since the Distance is the sum of the two sides, divide by 2 so the total isn't double
		   the real-world value */
		masterConfig.primaryPID.selectedFeedbackCoefficient = 0.5;
   }
    public void PIDArcade(double speed, double turn) {
      			/* Calculate targets from gamepad inputs */
			double target_sensorUnits = speed * DriveTrainConstants.kSensorUnitsPerRotation * DriveTrainConstants.kRotationsToTravel;
			double SensorTurnValue =+ turn * DriveTrainConstants.turn_rate;
			
		/*
		 * Configured for MotionMagic on Quad Encoders' Sum and Auxiliary PID on Pigeon
		 */
			MotorR1.set(ControlMode.MotionMagic, target_sensorUnits, DemandType.AuxPID, SensorTurnValue);
			MotorL1.follow(MotorR1, FollowerType.AuxOutput1);
			MotorL2.follow(MotorR1, FollowerType.AuxOutput1);
			MotorR2.follow(MotorR1);
	}
		/** Zero all sensors, both Talons and Pigeon */
		void zeroSensors() {
			MotorL1.getSensorCollection().setIntegratedSensorPosition(0,DriveTrainConstants.kTimeoutMs);
			MotorR1.getSensorCollection().setIntegratedSensorPosition(0,DriveTrainConstants.kTimeoutMs);
			_pidgey.setYaw(0,DriveTrainConstants.kTimeoutMs);
			_pidgey.setAccumZAngle(0,DriveTrainConstants.kTimeoutMs);
			System.out.println("[Quadrature Encoders + Pigeon] All sensors are zeroed.\n");
		}
		
		/** Zero QuadEncoders, used to reset position when initializing Motion Magic */
		void zeroDistance(){
			MotorL1.getSensorCollection().setIntegratedSensorPosition(0,DriveTrainConstants.kTimeoutMs);
			MotorR1.getSensorCollection().setIntegratedSensorPosition(0,DriveTrainConstants.kTimeoutMs);
			System.out.println("[Quadrature Encoders] All encoders are zeroed.\n");
		}
	 }