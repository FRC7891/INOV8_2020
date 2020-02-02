/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants.  This class should not be used for any other purpose.  All constants should be
 * declared globally (i.e. public static).  Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
    
    public static final class OIConstants {
        public static final int RightStickX = 4;
        public static final int RightStickY = 5;
    }

    public static class HookConstants {
        public static final int Motor1ID = 0;
    }
    
    public static class ElevatorConstants {
        public static final int Motor1ID = 0;
    }

    public static class DriveTrainConstants {

		public static final int MotorL1ID = 1;
		public static final int MotorR1ID = 2;
		public static final int MotorL2ID = 3;
		public static final int MotorR2ID = 4;
        
        public static final double SpeedL = .6;
        public static final double SpeedR = .6;
        
		public static final int LeftStickY = 1;
		public static final int RightStickX = 4;

    }

    public static class PIDConstants {
        /**
	 * Which PID slot to pull gains from. Starting 2018, you can choose from
	 * 0,1,2 or 3. Only the first two (0,1) are visible in web-based
	 * configuration.
	 */
	public static final int kSlotIdx = 0;

	/**
	 * Talon SRX/ Victor SPX will supported multiple (cascaded) PID loops. For
	 * now we just want the primary one.
	 */
	public static final int kPIDLoopIdx = 0;

	/**
	 * Set to zero to skip waiting for confirmation, set to nonzero to wait and
	 * report to DS if action fails.
	 */
    public static final int kTimeoutMs = 30;


    public static final int kSensorUnitsPerRotation = 4096;
	/**
	 * PID Gains may have to be adjusted based on the responsiveness of control loop.
     * kF: 1023 represents output value to Talon at 100%, 7200 represents Velocity units at 100% output
     * 
	 * 	                                    			  kP   kI   kD   kF          Iz    PeakOut */
    public final static Gains kGains_Velocit = new Gains( 0.25, 0.001, 20, 0/7200.0,  300,  1.00);

	public static final int Motor1ID = 5;
    }
}
