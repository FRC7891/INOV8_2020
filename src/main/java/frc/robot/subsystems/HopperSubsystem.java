/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.HopperConstants;

public class HopperSubsystem extends SubsystemBase {

  public final TalonSRX motor2 = new TalonSRX(HopperConstants.Motor2ID);

  public HopperSubsystem() {

  }

  public void transportForward() {

    motor2.set(ControlMode.PercentOutput, HopperConstants.forwardspeed);

  }

  public void transportBackward() {

    motor2.set(ControlMode.PercentOutput, HopperConstants.backwardspeed);

  }

  public void motorStop() {
    motor2.set(ControlMode.PercentOutput, 0);

  }

  public Object TransportForward(double rawAxis) {
    return null;

  }

  public void spin(double speed) {
    motor2.set(ControlMode.PercentOutput, speed * 0.4);
  }
}
