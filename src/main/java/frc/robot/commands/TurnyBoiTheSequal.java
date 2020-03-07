/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.Constants.DriveTrainConstants;
import frc.robot.Constants.OIConstants;
import frc.robot.subsystems.DriveTrainSubsystem;

public class TurnyBoiTheSequal extends CommandBase {

  DriveTrainSubsystem m_drivetrainsubsystem;
  XboxController m_driverController;
  public TurnyBoiTheSequal(DriveTrainSubsystem subsystem, XboxController driverController) {
    m_drivetrainsubsystem = subsystem;
    m_driverController = driverController;
    addRequirements(subsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    double DirectionBoi = m_drivetrainsubsystem.DirectionDeg(m_driverController.getRawAxis(OIConstants.RightStickX),m_driverController.getRawAxis(OIConstants.LeftStickY));
    double TurnyBoiTheCompleteTrilogy = m_drivetrainsubsystem.TurnyBoi(DirectionBoi);
    m_drivetrainsubsystem.Quickturn(TurnyBoiTheCompleteTrilogy);
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
