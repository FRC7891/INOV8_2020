/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.DriveTrainConstants;

public class DriveTrainSubsystem extends SubsystemBase {
  private TalonFX MotorL1 = new TalonFX(DriveTrainConstants.MotorL1ID);
  private TalonFX MotorR1 = new TalonFX(DriveTrainConstants.MotorR1ID);
  private TalonFX MotorL2 = new TalonFX(DriveTrainConstants.MotorL2ID);
  private TalonFX MotorR2 = new TalonFX(DriveTrainConstants.MotorR2ID);
  public DriveTrainSubsystem() {

  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
  public void setLeftMotors(double speed) {
    MotorL1.set(ControlMode.PercentOutput, -speed * DriveTrainConstants.SpeedL);
    MotorL2.set(ControlMode.PercentOutput, -speed * DriveTrainConstants.SpeedL);
    }
    public void setRightMotors(double speed) {
      MotorR1.set(ControlMode.PercentOutput, speed * DriveTrainConstants.SpeedR);
      MotorR2.set(ControlMode.PercentOutput, speed * DriveTrainConstants.SpeedR);
      }
}
