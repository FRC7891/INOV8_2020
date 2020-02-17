/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.XboxController.Button;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.ShooterConstants;
import frc.robot.Constants.OIConstants;

import com.ctre.phoenix.motorcontrol.ControlMode;
//import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
//import com.ctre.phoenix.motorcontrol.can.VictorSPX;

public class ShooterSubsystem extends SubsystemBase {
 
  public final TalonSRX topMotor = new TalonSRX(ShooterConstants.TopMotorID);
  public final TalonSRX botMotor = new TalonSRX(ShooterConstants.BotMotorID);
  public final XboxController controller = new XboxController(OIConstants.XboxOperator);
  public double topPercent = 0;
  public double botPercent = 0; 

  public ShooterSubsystem() {

 SmartDashboard.setDefaultNumber("topMotor%", 0);
 SmartDashboard.setDefaultNumber("botMotor%", 0);

  }

public void ballMovingFunction(double speedTop, double speedBot){
  topPercent = speedTop;
  botPercent = speedBot;

  topMotor.set(ControlMode.PercentOutput, topPercent);
  botMotor.set(ControlMode.PercentOutput, botPercent);
};


  @Override
  public void periodic() {

    topPercent = SmartDashboard.getNumber("topMotor%", 0);
    botPercent = SmartDashboard.getNumber("botMotor%", 0);

    
    

    // This method will be called once per scheduler run
  }
}