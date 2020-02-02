/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.DriveTrainConstants;
import frc.robot.subsystems.DriveTrainSubsystem;

public class Drivetrain extends CommandBase {
  /**
   * Creates a new Drivetrain.
   */
  DriveTrainSubsystem m_drivetrainsubsystem;
  XboxController m_driverController;
  public Drivetrain(DriveTrainSubsystem subsystem, XboxController driverController) {
    m_drivetrainsubsystem = subsystem;
    m_driverController = driverController;
    // Use addRequirements() here to declare subsystem dependencies.
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    double leftstickY = m_driverController.getRawAxis(DriveTrainConstants.LeftStickY);
    double rightstickX = m_driverController.getRawAxis(DriveTrainConstants.RightStickX);
 
    m_drivetrainsubsystem.setLeftMotors(leftstickY - rightstickX);
    m_drivetrainsubsystem.setRightMotors(leftstickY + rightstickX);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
