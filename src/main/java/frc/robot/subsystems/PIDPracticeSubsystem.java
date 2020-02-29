/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.PIDConstants;

public class PIDPracticeSubsystem extends SubsystemBase {
  /**
   * Creates a new PIDPracticeSubsystem.
   */

   private final TalonSRX _talon = new TalonSRX(PIDConstants.Motor1ID);
  public PIDPracticeSubsystem() {
    _talon.set(ControlMode.PercentOutput, 0);

    _talon.configFactoryDefault();

		/* Config sensor used for Primary PID [Velocity] */
        _talon.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder,
                                            PIDConstants.kPIDLoopIdx, 
                                            PIDConstants.kTimeoutMs);

        /**
		 * Phase sensor accordingly. 
         * Positive Sensor Reading should match Green (blinking) Leds on Talon
         */
		_talon.setSensorPhase(false);

		/* Config the peak and nominal outputs */
		_talon.configNominalOutputForward(0, PIDConstants.kTimeoutMs);
		_talon.configNominalOutputReverse(0, PIDConstants.kTimeoutMs);
		_talon.configPeakOutputForward(1, PIDConstants.kTimeoutMs);
		_talon.configPeakOutputReverse(-1, PIDConstants.kTimeoutMs);

		/* Config the Velocity closed loop gains in slot0 */
		_talon.config_kF(PIDConstants.kPIDLoopIdx, PIDConstants.kGains_Velocit.kF, PIDConstants.kTimeoutMs);
		_talon.config_kP(PIDConstants.kPIDLoopIdx, PIDConstants.kGains_Velocit.kP, PIDConstants.kTimeoutMs);
		_talon.config_kI(PIDConstants.kPIDLoopIdx, PIDConstants.kGains_Velocit.kI, PIDConstants.kTimeoutMs);
    _talon.config_kD(PIDConstants.kPIDLoopIdx, PIDConstants.kGains_Velocit.kD, PIDConstants.kTimeoutMs);
    
    SmartDashboard.setDefaultNumber("RPS", 0);
  }

public void rpsspeed(double rps) {

  SmartDashboard.putNumber("RPS_OUT", 10 * _talon.getSelectedSensorVelocity(PIDConstants.kPIDLoopIdx) / 4096);
  SmartDashboard.putNumber("ERROR", _talon.getClosedLoopError(PIDConstants.kPIDLoopIdx));

  double targetVelocity_UnitsPer100ms = SmartDashboard.getNumber("RPM", 0) * PIDConstants.kSensorUnitsPerRotation / 10;
			/* 500 RPM in either direction */
			_talon.set(ControlMode.Velocity, targetVelocity_UnitsPer100ms);

}

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
