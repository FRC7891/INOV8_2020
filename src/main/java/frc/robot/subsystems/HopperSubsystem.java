/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Add your docs here.
 */
public class HopperSubsystem extends Subsystem {
  // Put methods for controlling this subsystem
  // here. Call these from Commands.
  pulic final TalonSRX = new TalongSRX(HopperConstants.Motor2ID)


  public void HopperSubsystem ()


  @Override
  public void initDefaultCommand() {

    }
  
  public void level(double speed) {
   motor2.set(ControlMode.PercentOutput, speed);
  
    }


    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }
}
