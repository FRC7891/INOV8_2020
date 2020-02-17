/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.XboxController.Button;
//import frc.robot.subsystems.ExampleSubsystem;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.Constants.OIConstants;
import frc.robot.commands.Drivetrain;
import frc.robot.commands.PIDDrivetrain;
import frc.robot.subsystems.DriveTrainSubsystem;
import frc.robot.subsystems.ElevatorSubsystem;
import frc.robot.subsystems.HookSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.PIDPracticeSubsystem;
import frc.robot.subsystems.ShooterSubsystem;


/**
 * This class is where the bulk of the robot should be declared.  Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls).  Instead, the structure of the robot
 * (including subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  private final HookSubsystem m_hooksubsystem = new HookSubsystem();
  private final DriveTrainSubsystem m_drivetrainsubsystem = new DriveTrainSubsystem();
  private final PIDPracticeSubsystem m_pidpracticesubsystem = new PIDPracticeSubsystem(); 
  private final ElevatorSubsystem m_elevatorsubsystem = new ElevatorSubsystem();
  private final IntakeSubsystem m_IntakeSubsystem = new IntakeSubsystem();
  private final ShooterSubsystem m_ShooterSubsytem = new ShooterSubsystem();
  XboxController m_controller = new XboxController(0);
  XboxController m_opperator = new XboxController(1);
  // The robot's subsystems and commands are defined here...
  //private final ExampleSubsystem m_exampleSubsystem = new ExampleSubsystem();

  //private final ExampleCommand m_autoCommand = new ExampleCommand(m_exampleSubsystem);



  /**
   * The container for the robot.  Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    // Configure the button bindings
    configureButtonBindings();

    m_hooksubsystem.setDefaultCommand(
      new RunCommand(() -> m_hooksubsystem.level(m_opperator.getRawAxis(OIConstants.RightStickX)), m_hooksubsystem)
      );

    m_drivetrainsubsystem.setDefaultCommand(
      new PIDDrivetrain(m_drivetrainsubsystem, m_controller)
    );

    m_pidpracticesubsystem.setDefaultCommand(
      new RunCommand(() -> m_pidpracticesubsystem.rpsspeed(0), m_pidpracticesubsystem)
    );

    m_elevatorsubsystem.setDefaultCommand(
      new RunCommand(() -> m_elevatorsubsystem.raise(m_opperator.getRawAxis(OIConstants.RightStickY)), m_elevatorsubsystem)
    );
    m_IntakeSubsystem.setDefaultCommand(
      new RunCommand(() -> m_IntakeSubsystem.suck(m_opperator.getRawAxis(OIConstants.RightStickX)), m_IntakeSubsystem)
      );

  }

  /**
   * Use this method to define your button->command mappings.  Buttons can be created by
   * instantiating a {@link GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a
   * {@link edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {
//This code is for trickling ballls into low goals while aligned to the goal(need testing for values)
    new JoystickButton(m_opperator, Button.kA.value)
        .whenPressed(() -> m_ShooterSubsytem.ballMovingFunction(0.1,.1))
        .whenReleased(() -> m_ShooterSubsytem.ballMovingFunction(.1,.1));
//This code if for shooting balls for high goal from fixed posiition(need testing for values)
    new JoystickButton(m_opperator, Button.kY.value)
        .whenPressed(() -> m_ShooterSubsytem.ballMovingFunction(0.1,.1))
        .whenReleased(() -> m_ShooterSubsytem.ballMovingFunction(.1,.1));

//This code is for passing balls from loading zone to trench(need testing for values)
    new JoystickButton(m_opperator, Button.kY.value)
         .whenPressed(() -> m_ShooterSubsytem.ballMovingFunction(0.1,.1))
         .whenReleased(() -> m_ShooterSubsytem.ballMovingFunction(.1,.1));
}


  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An ExampleCommand will run in autonomous
    return null;
  }
}
