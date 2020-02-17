/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.ElevatorConstants;

public class ElevatorSubsystem extends SubsystemBase {
  private final DigitalInput elevLimitSwitch = new DigitalInput(ElevatorConstants.limitSwitchID);

  public final TalonSRX motor1 = new TalonSRX(ElevatorConstants.Motor1ID);

  public ElevatorSubsystem() {
    motor1.configFactoryDefault();
        
		/* Config the sensor used for Primary PID and sensor direction */
        motor1.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 
                                            0,
				                            0);

		/* Ensure sensor is positive when output is positive */
		motor1.setSensorPhase(true);
    motor1.setSelectedSensorPosition(0, 0, Constants.kTimeoutMs);
  }

  public void raise(double speed) {
    if(elevLimitSwitch.get() == false && speed < 0) {
      speed = 0;
      motor1.setSelectedSensorPosition(0, 0, Constants.kTimeoutMs);
    }
    else if(motor1.getSelectedSensorPosition() >= 40000 && speed < 0)
    {
      speed = 0;
    }
 
    motor1.set(ControlMode.PercentOutput, speed);
    System.out.println(motor1.getSelectedSensorPosition());
  }
  
  
  @Override
  public void periodic() {
        
  }
}
