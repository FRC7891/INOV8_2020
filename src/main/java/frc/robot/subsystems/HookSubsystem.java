/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.HookConstants;

public class HookSubsystem extends SubsystemBase {
  
  public final VictorSPX motor1 = new VictorSPX(HookConstants.Motor1ID);

  public HookSubsystem() {

  }

  public void level(double speed,double speed2) {

      motor1.set(ControlMode.PercentOutput,-speed+speed2);

  }
}
