/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.XboxController.Button;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.POVButton;
import frc.robot.Constants.OIConstants;
import frc.robot.commands.Drivetrain;
import frc.robot.commands.PIDDrivetrain;
import frc.robot.commands.ShooterSpeedReached;
import frc.robot.commands.TurnyBoiTheSequal;
import frc.robot.subsystems.DriveTrainSubsystem;
import frc.robot.subsystems.ElevatorSubsystem;
import frc.robot.subsystems.HookSubsystem;
import frc.robot.subsystems.HopperSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.ShooterSubsystem;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a "declarative" paradigm, very little robot logic should
 * actually be handled in the {@link Robot} periodic methods (other than the
 * scheduler calls). Instead, the structure of the robot (including subsystems,
 * commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  private final HookSubsystem m_hooksubsystem = new HookSubsystem();
  private final DriveTrainSubsystem m_drivetrainsubsystem = new DriveTrainSubsystem();
  private final ElevatorSubsystem m_elevatorsubsystem = new ElevatorSubsystem();
  private final IntakeSubsystem m_IntakeSubsystem = new IntakeSubsystem();
  private final HopperSubsystem m_hoppersubsystem = new HopperSubsystem();
  private final ShooterSubsystem m_ShooterSubsystem = new ShooterSubsystem();
  XboxController m_controller = new XboxController(0);
  XboxController m_operator = new XboxController(1);

  private final NetworkTableInstance nt = NetworkTableInstance.getDefault();
  private final SendableChooser<Command> autonomousModeChooser = new SendableChooser<Command>();

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    // Configure the button bindings
    configureButtonBindings();

    m_drivetrainsubsystem.setDefaultCommand(
        // new RunCommand(() ->
        // m_drivetrainsubsystem.DirectionDeg(m_controller.getRawAxis(OIConstants.RightStickX),
        //   m_controller.getRawAxis(OIConstants.RightStickY)), m_drivetrainsubsystem));
        // new Drivetrain(m_drivetrainsubsystem, m_controller));
        new PIDDrivetrain(m_drivetrainsubsystem, m_controller));

    // m_drivetrainsubsystem.setDefaultCommand(
    // new RunCommand(() ->
    // m_drivetrainsubsystem.DirectionDeg(m_controller.getRawAxis(OIConstants.RightStickX),
    // m_controller.getRawAxis(OIConstants.RightStickY)), m_drivetrainsubsystem));

    m_hooksubsystem
        .setDefaultCommand(new RunCommand(() -> m_hooksubsystem.level(m_operator.getRawAxis(OIConstants.RightTrigger),
            m_operator.getRawAxis(OIConstants.LeftTrigger)), m_hooksubsystem));

    m_elevatorsubsystem.setDefaultCommand(new RunCommand(
        () -> m_elevatorsubsystem.raise(m_operator.getRawAxis(OIConstants.RightStickY)), m_elevatorsubsystem));
    m_IntakeSubsystem.setDefaultCommand(new RunCommand(
        () -> m_IntakeSubsystem.suck(m_operator.getRawAxis(OIConstants.LeftStickY) * -0.70), m_IntakeSubsystem));
    m_hoppersubsystem.setDefaultCommand(new RunCommand(
        () -> m_hoppersubsystem.spin(m_operator.getRawAxis(OIConstants.LeftStickY) * 0.90), m_hoppersubsystem));

  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    return (Command) autonomousModeChooser.getSelected();
  }

  /**
   * Use this method to define your button->command mappings. Buttons can be
   * created by instantiating a {@link GenericHID} or one of its subclasses
   * ({@link edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then
   * passing it to a {@link edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {

    new JoystickButton(m_operator, Button.kStickLeft.value).whenPressed(
        new RunCommand(() -> m_hoppersubsystem.transportForward(), m_hoppersubsystem).alongWith(new RunCommand(
            () -> m_IntakeSubsystem.suck(m_operator.getRawAxis(OIConstants.LeftStickY)), m_IntakeSubsystem)));

    // When the B button on the Xbox controller is pressed, a new command will be run which moves the hopper
    // backwards for (Insert time) in order to free up space for spinning the motor which happens after
    // because of the .andThen command. After it is shown that the shooter speed is true, it will move the
    // hopper forward and into the shooter, firing the balls.
    new JoystickButton(m_controller, Button.kB.value)
        .whenPressed(new RunCommand(() -> m_hoppersubsystem.transportBackward(), m_hoppersubsystem).withTimeout(0.3)
            .andThen(new ShooterSpeedReached(),(new RunCommand(() -> m_hoppersubsystem.transportForward(), m_hoppersubsystem))));

    // This code is for bring balls back a slight bit. Needs what ever value up is
    // on the d-pad.
    new POVButton(m_operator, 0 /* DButton_UpDownIdk?? */ ).whenPressed(() -> m_hoppersubsystem.ballJerkBackward());

    // This code is for trickling balls into low goals while aligned to the
    // goal(need testing for values)
    new JoystickButton(m_operator, Button.kA.value).whenPressed(() -> m_ShooterSubsystem.ballMovingPID(0, 0))
        .whenReleased(() -> m_ShooterSubsystem.ballMovingPID(0, 0));
    // This code if for shooting balls for high goal from fixed position(need
    // testing for values)
    new JoystickButton(m_operator, Button.kY.value).whenPressed(() -> m_ShooterSubsystem.ballMovingPID(0, 0))
        .whenReleased(() -> m_ShooterSubsystem.ballMovingPID(0, 0));
    // This code is for un-jamming the hopper
    new JoystickButton(m_operator, Button.kB.value).whenPressed(() -> m_ShooterSubsystem.ballMovingPID(0, 0))
        .whenReleased(() -> m_ShooterSubsystem.ballMovingPID(0, 0));

    // this is for the quick turn function
    new JoystickButton(m_operator, Button.kStickLeft.value)
        .whileHeld(() -> new TurnyBoiTheSequal(m_drivetrainsubsystem, m_controller));


    // This code if for shooting balls for high goal from fixed position(need
    // testing for values)
    new JoystickButton(m_operator, Button.kY.value)
        .whenPressed(new RunCommand(() -> m_hoppersubsystem.transportBackward(), m_hoppersubsystem).withTimeout(0.3)
            .andThen(new RunCommand(() -> m_ShooterSubsystem.ballMovingPID(0, 0))))
        .whenReleased(() -> m_ShooterSubsystem.ballMovingPID(0, 0));

    // This code is for passing balls from loading zone to trench(need testing for
    // values)
    new JoystickButton(m_operator, Button.kX.value)
        .whenPressed(new RunCommand(() -> m_hoppersubsystem.transportBackward(), m_hoppersubsystem).withTimeout(0.3)
            .andThen(new RunCommand(() -> m_ShooterSubsystem.ballMovingPID(0, 0))))
        .whenReleased(() -> m_ShooterSubsystem.ballMovingPID(0, 0));

    // This code is for trickling ballls into low goals while aligned to the
    // goal(need testing for values)
    new JoystickButton(m_operator, Button.kA.value)
        .whenPressed(new RunCommand(() -> m_hoppersubsystem.transportBackward(), m_hoppersubsystem).withTimeout(0.3)
            .andThen(new RunCommand(() -> m_ShooterSubsystem.ballMovingPID(0, 0))))
        .whenReleased(() -> m_ShooterSubsystem.ballMovingPID(0, 0));

    // Basically this gives the driver the ability to shoot.
    // I may make it so that it can only do it after the shooter speed is reached,
    // along with an overrride
    // mode just in case
    // I'll just put the override mode on the interface
    // I'll need to define shooterPID. Right now it just returns true
    // Please remove the print text. Im really tired right now, and I dont want to
    /*
     * new JoystickButton(m_controller, Button.kBumperRight.value) .whenPressed(new
     * ConditionalCommand(m_hoppersubsystem.transportForward(),
     * print("I can't do that yet"), ShooterSubsystem.botShooterPID::get));
     * 
     * /* new JoystickButton(m_operator, Button.kStickLeft.value) .whenPressed(new
     * RunCommand(() -> m_hoppersubsystem.transportForward(), m_hoppersubsystem)
     * .alongWith (new RunCommand(() ->
     * m_IntakeSubsystem.suck(m_operator.getRawAxis(OIConstants.LeftStickY)),
     * m_IntakeSubsystem)));
     */

    // When the B button on the Xbox controller is pressed, a new command will be
    // run which moves the hopper
    // backwards for (Insert time) in order to free up space for spinning the motor
    // which happens after
    // because of the .andThen command. After it is shown that the shooter speed is
    // true, it will move the
    // hopper forward and into the shooter, firing the balls.
    // new JoystickButton(m_controller, Button.kB.value)
    // .whenPressed(new RunCommand(() -> m_hoppersubsystem.transportBackward(),
    // m_hoppersubsystem).withTimeout(0.3)
    // .andThen(new ShooterSpeedReached(),
    // (new RunCommand(() -> m_hoppersubsystem.transportForward(),
    // m_hoppersubsystem))));

    // new JoystickButton(m_operator, Button.kStickLeft.value).whenPressed(
    // new RunCommand(() -> m_hoppersubsystem.transportForward(),
    // m_hoppersubsystem).alongWith(new RunCommand(
    // () -> m_IntakeSubsystem.suck(m_operator.getRawAxis(OIConstants.LeftStickY)),
    // m_IntakeSubsystem)));

    /*
     * //This code is for trickling ballls into low goals while aligned to the
     * goal(need testing for values) new JoystickButton(m_operator, Button.kA.value)
     * .whenPressed(() -> m_ShooterSubsystem.ballMovingFunction(0.3,0.3))
     * .whenReleased(() -> m_ShooterSubsystem.ballMovingFunction(0,0)); //This code
     * if for shooting balls for high goal from fixed position(need testing for
     * values) new JoystickButton(m_operator, Button.kY.value) .whenPressed(() ->
     * m_ShooterSubsystem.ballMovingFunction(0.65,.75)) .whenReleased(() ->
     * m_ShooterSubsystem.ballMovingFunction(0, 0)); //This code is for toggling
     * slider shooter new JoystickButton(m_operator,Button.kX.value) .whenPressed(()
     * -> m_ShooterSubsystem.sliderValueFunction()) .whenReleased(() ->
     * m_ShooterSubsystem.ballMovingFunction(0, 0));
     *
     * //This code is for passing balls from loading zone to trench(need testing for
     * values) new JoystickButton(m_operator, Button.kY.value) .whenPressed(() ->
     * m_ShooterSubsystem.ballMovingFunction(.4,.5)) .whenReleased(() ->
     * m_ShooterSubsystem.ballMovingFunction(0, 0));
     *
     * //This code is for un-jamming the hopper new JoystickButton(m_operator,
     * Button.kB.value) .whenPressed(() ->
     * m_ShooterSubsystem.ballMovingFunction(-.3,-.3)) .whenReleased(() ->
     * m_ShooterSubsystem.ballMovingFunction(0, 0));
     *
     *
     * //When the B button on the Xbox controller is pressed, a new command will be
     * run which moves the hopper //backwards for (Insert time) in order to free up
     * space for spinning the motor which happens after //because of the .andThen
     * command. After it is shown that the shooter speed is true, it will move the
     * //hopper forward and into the shooter, firing the balls. new
     * JoystickButton(m_controller, Button.kB.value) .whenPressed(new RunCommand(()
     * -> m_hoppersubsystem.transportBackward(), m_hoppersubsystem).withTimeout(0.3)
     * .andThen(new ShooterSpeedReached(),(new RunCommand(() ->
     * m_hoppersubsystem.transportForward(), m_hoppersubsystem))));
     *
     *
     * new JoystickButton(m_operator, Button.kStickLeft.value) .whenPressed(new
     * RunCommand(() -> m_hoppersubsystem.transportForward(), m_hoppersubsystem)
     * .alongWith (new RunCommand(() ->
     * m_IntakeSubsystem.suck(m_operator.getRawAxis(OIConstants.LeftStickY)),
     * m_IntakeSubsystem)));
     *
     */
    // When the B button on the Xbox controller is pressed, a new command will be
    // run which moves the hopper
    // backwards for (Insert time) in order to free up space for spinning the motor
    // which happens after
    // because of the .andThen command. After it is shown that the shooter speed is
    // true, it will move the
    // hopper forward and into the shooter, firing the balls.
  }
}
