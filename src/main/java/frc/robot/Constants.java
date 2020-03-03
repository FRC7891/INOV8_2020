/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide
 * numerical or boolean constants. This class should not be used for any other
 * purpose. All constants should be declared globally (i.e. public static). Do
 * not put anything functional in this class.
 *
 * <p>
 * It is advised to statically import this class (or one of its inner classes)
 * wherever the constants are needed, to reduce verbosity.
 */
public final class Constants {

	public static final int kTimeoutMs = 30;

	public static final class OIConstants {
		public static final int RightStickX = 4;
		public static final int RightStickY = 5;
		public static final int RightTrigger = 3;
		public static final int LeftTrigger = 2;
		public static final int LeftStickY = 1;
		public static final int LeftStickX = 0;
	}

	public static class HookConstants {
		public static final int Motor1ID = 9;
	}

	public static class HopperConstants {
		public static final int Motor2ID = 8;
		public static double forwardspeed = 0.1;
		public static double backwardspeed = -0.1;
	}

	public static final class ShooterConstants {

		public static final int TopMotorID = 5;
		public static final int BotMotorID = 6;

		public static final double HighShot = 0;
		public static final double Pass = 0;
		public static final double LowShot = 0;
		// --------------------------------------------------------

		/**
		 * How many sensor units per rotation. Using CTRE Magnetic Encoder.
		 * 
		 * @link https://github.com/CrossTheRoadElec/Phoenix-Documentation#what-are-the-units-of-my-sensor
		 */
		public final static int kSensorUnitsPerRotation = 4096;

		/**
		 * Set to zero to skip waiting for confirmation. Set to nonzero to wait and
		 * report to DS if action fails.
		 */
		public final static int kTimeoutMs = 30;

		/**
		 * Motor neutral dead-band, set to the minimum 0.1%.
		 */
		public final static double kNeutralDeadband = 0.001;

		/**
		 * PID Gains may have to be adjusted based on the responsiveness of control
		 * loop. kF: 1023 represents output value to Talon at 100%, 6800 represents
		 * Velocity units at 100% output Not all set of Gains are used in this project
		 * and may be removed as desired.
		 * 
		 * kP kI kD kF Iz PeakOut
		 */
		public final static Gains kGains_Distanc = new Gains(0.1, 0.0, 0.0, 0.0, 100, 0.50);
		public final static Gains kGains_Turning = new Gains(2.0, 0.0, 4.0, 0.0, 200, 1.00);

		// The velocity PID for shooter
		public final static Gains kGains_Velocit = new Gains(0.1, 0.0, 20.0, 1023.0 / 6800.0, 300, 0.50);

		public final static Gains kGains_MotProf = new Gains(1.0, 0.0, 0.0, 1023.0 / 6800.0, 400, 1.00);

		/** ---- Flat constants, you should not need to change these ---- */
		/*
		 * We allow either a 0 or 1 when selecting an ordinal for remote devices [You
		 * can have up to 2 devices assigned remotely to a talon/victor]
		 */
		public final static int REMOTE_0 = 0;
		public final static int REMOTE_1 = 1;
		/*
		 * We allow either a 0 or 1 when selecting a PID Index, where 0 is primary and 1
		 * is auxiliary
		 */
		public final static int PID_PRIMARY = 0;
		public final static int PID_TURN = 1;
		/*
		 * Firmware currently supports slots [0, 3] and can be used for either PID Set
		 */
		public final static int SLOT_0 = 0;
		public final static int SLOT_1 = 1;
		public final static int SLOT_2 = 2;
		public final static int SLOT_3 = 3;
		/* ---- Named slots, used to clarify code ---- */
		public final static int kSlot_Distanc = SLOT_0;
		public final static int kSlot_Turning = SLOT_1;
		public final static int kSlot_Velocit = SLOT_2;
		public final static int kSlot_MotProf = SLOT_3;
	}

	public static class ElevatorConstants {
		public static final int Motor1ID = 10;
		public static final int limitSwitchID = 8;
		public static final int limitSwitchID2 = 9;
	}

	public static class DriveTrainConstants {

		public static final int MotorL1ID = 1;
		public static final int MotorR1ID = 2;
		public static final int MotorL2ID = 3;
		public static final int MotorR2ID = 4;

		public static final double SpeedL = .4;
		public static final double SpeedR = .4;

		// PID Gains kP kI kD kF kI kZ
		public final static Gains kGains_Distanc = new Gains(0.1, 0.0, 0.0, 0.0, 100, 0.50);
		public final static Gains kGains_Turning = new Gains(2.0, 0.0, 4.0, 0.0, 200, 1.00);
		public final static Gains kGains_Velocit = new Gains(0.1, 0.0, 20.0, 1023.0 / 6800.0, 300, 0.50);
		public final static Gains kGains_MotProf = new Gains(1.0, 0.0, 0.0, 1023.0 / 6800.0, 400, 1.00);

		/*
		 * We allow either a 0 or 1 when selecting an ordinal for remote devices [You
		 * can have up to 2 devices assigned remotely to a talon/victor]
		 */
		public final static int REMOTE_0 = 0;
		public final static int REMOTE_1 = 1;
		/*
		 * We allow either a 0 or 1 when selecting a PID Index, where 0 is primary and 1
		 * is auxiliary
		 */
		public final static int PID_PRIMARY = 0;
		public final static int PID_TURN = 1;
		/*
		 * Firmware currently supports slots [0, 3] and can be used for either PID Set
		 */
		public final static int SLOT_0 = 0;
		public final static int SLOT_1 = 1;
		public final static int SLOT_2 = 2;
		public final static int SLOT_3 = 3;
		/* ---- Named slots, used to clarify code ---- */
		public final static int kSlot_Distanc = SLOT_0;
		public final static int kSlot_Turning = SLOT_1;
		public final static int kSlot_Velocit = SLOT_2;
		public final static int kSlot_MotProf = SLOT_3;

		//
		public final static int kSensorUnitsPerRotation = 4096;

		/**
		 * Number of rotations to drive when performing Distance Closed Loop
		 */
		public final static double kRotationsToTravel = 6;

		/**
		 * This is a property of the Pigeon IMU, and should not be changed.
		 */
		public final static int kPigeonUnitsPerRotation = 8192;

		/**
		 * Set to zero to skip waiting for confirmation. Set to nonzero to wait and
		 * report to DS if action fails.
		 */
		public final static int kTimeoutMs = 30;

		/**
		 * Motor neutral dead-band, set to the minimum 0.1%.
		 */
		public final static double kNeutralDeadband = 0.001;

		// pidgeon ID
		public final static int PidgeonID = 1;
		public static double turn_rate = 1 / 50;
	}

	public static class PIDConstants {
		/**
		 * Which PID slot to pull gains from. Starting 2018, you can choose from 0,1,2
		 * or 3. Only the first two (0,1) are visible in web-based configuration.
		 */
		public static final int kSlotIdx = 0;

		/**
		 * Talon SRX/ Victor SPX will supported multiple (cascaded) PID loops. For now
		 * we just want the primary one.
		 */
		public static final int kPIDLoopIdx = 0;

		/**
		 * Set to zero to skip waiting for confirmation, set to nonzero to wait and
		 * report to DS if action fails.
		 */
		public static final int kTimeoutMs = 30;

		public static final int kSensorUnitsPerRotation = 4096;
		/**
		 * PID Gains may have to be adjusted based on the responsiveness of control
		 * loop. kF: 1023 represents output value to Talon at 100%, 7200 represents
		 * Velocity units at 100% output
		 *
		 * kP kI kD kF Iz PeakOut
		 */
		public final static Gains kGains_Velocit = new Gains(0.25, 0.001, 20, 0 / 7200.0, 300, 1.00);

		public static final int Motor1ID = 6;
	}

	public static class IntakeConstants {

		public static final int Motor1ID = 7;

	}

}
