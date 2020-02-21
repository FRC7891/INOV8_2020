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
import frc.robot.Constants.IntakeConstants;


public class IntakeSubsystem extends SubsystemBase {
  
  public final VictorSPX motorsuck = new VictorSPX(IntakeConstants.Motor1ID);
 
   public IntakeSubsystem() {

   }


  

  public void suck (double rotations){

    motorsuck.set(ControlMode.PercentOutput, rotations);
  }

  

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    
  }
}
