/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ShooterConstants;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
//import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
//import com.ctre.phoenix.motorcontrol.can.VictorSPX;

public class ShooterSubsystem extends SubsystemBase {
 
  public final TalonSRX topMotor = new TalonSRX(ShooterConstants.TopMotorID);
  public final TalonSRX botMotor = new TalonSRX(ShooterConstants.BotMotorID);
  public double topPercent = 0;
  public double botPercent = 0; 
  public double topPID = 0;
  public double botPID = 0;

  public ShooterSubsystem() {

 SmartDashboard.setDefaultNumber("topMotor%", 0);
 SmartDashboard.setDefaultNumber("botMotor%", 0);
 SmartDashboard.setDefaultNumber("topPID", 0);
 SmartDashboard.setDefaultNumber("botPID", 0);
topShooterPID();
botShooterPID();

  }

  public void botShooterPID() {

    /* Disable all motor controllers */
    botMotor.set(ControlMode.PercentOutput, 0);
    
    /* Factory Default all hardware to prevent unexpected behaviour */
    botMotor.configFactoryDefault();
    
    /* Set Neutral Mode */
    botMotor.setNeutralMode(NeutralMode.Brake);
    
    
    /* Configure output and sensor direction */
    botMotor.setInverted(true);
    botMotor.setSensorPhase(true);
    
    /**
     * Max out the peak output (for all modes).  
     * However you can limit the output of a given PID object with configClosedLoopPeakOutput().
     */
    botMotor.configPeakOutputForward(+1.0, ShooterConstants.kTimeoutMs);
    botMotor.configPeakOutputReverse(-1.0, ShooterConstants.kTimeoutMs);

    /* FPID Gains for velocity servo */
    botMotor.config_kP(ShooterConstants.kSlot_Velocit, ShooterConstants.kGains_Velocit.kP, ShooterConstants.kTimeoutMs);
    botMotor.config_kI(ShooterConstants.kSlot_Velocit, ShooterConstants.kGains_Velocit.kI, ShooterConstants.kTimeoutMs);
    botMotor.config_kD(ShooterConstants.kSlot_Velocit, ShooterConstants.kGains_Velocit.kD, ShooterConstants.kTimeoutMs);
    botMotor.config_kF(ShooterConstants.kSlot_Velocit, ShooterConstants.kGains_Velocit.kF, ShooterConstants.kTimeoutMs);
    botMotor.config_IntegralZone(ShooterConstants.kSlot_Velocit, ShooterConstants.kGains_Velocit.kIzone, ShooterConstants.kTimeoutMs);
    botMotor.configClosedLoopPeakOutput(ShooterConstants.kSlot_Velocit, ShooterConstants.kGains_Velocit.kPeakOutput, ShooterConstants.kTimeoutMs);
    botMotor.configAllowableClosedloopError(ShooterConstants.kSlot_Velocit, 0, ShooterConstants.kTimeoutMs);

    /**
     * 1ms per loop.  PID loop can be slowed down if need be.
     * For example,
     * - if sensor updates are too slow
     * - sensor deltas are very small per update, so derivative error never gets large enough to be useful.
     * - sensor movement is very slow causing the derivative error to be near zero.
     */
    int closedLoopTimeMs = 1;
    botMotor.configClosedLoopPeriod(0, closedLoopTimeMs, ShooterConstants.kTimeoutMs);
    botMotor.configClosedLoopPeriod(1, closedLoopTimeMs, ShooterConstants.kTimeoutMs);

    /**
     * configAuxPIDPolarity(boolean invert, int timeoutMs)
     * false means talon's local output is PID0 + PID1, and other side Talon is PID0 - PID1
     * true means talon's local output is PID0 - PID1, and other side Talon is PID0 + PID1
     */
    botMotor.configAuxPIDPolarity(false, ShooterConstants.kTimeoutMs);
}

public void topShooterPID() {

      /* Disable all motor controllers */
      topMotor.set(ControlMode.PercentOutput, 0);
      
      /* Factory Default all hardware to prevent unexpected behaviour */
      topMotor.configFactoryDefault();
      
      /* Set Neutral Mode */
      topMotor.setNeutralMode(NeutralMode.Brake);
      
      
      /* Configure output and sensor direction */
      topMotor.setInverted(true);
      topMotor.setSensorPhase(true);
      
      /**
       * Max out the peak output (for all modes).  
       * However you can limit the output of a given PID object with configClosedLoopPeakOutput().
       */
      topMotor.configPeakOutputForward(+1.0, ShooterConstants.kTimeoutMs);
      topMotor.configPeakOutputReverse(-1.0, ShooterConstants.kTimeoutMs);
  
      /* FPID Gains for velocity servo */
      topMotor.config_kP(ShooterConstants.kSlot_Velocit, ShooterConstants.kGains_Velocit.kP, ShooterConstants.kTimeoutMs);
      topMotor.config_kI(ShooterConstants.kSlot_Velocit, ShooterConstants.kGains_Velocit.kI, ShooterConstants.kTimeoutMs);
      topMotor.config_kD(ShooterConstants.kSlot_Velocit, ShooterConstants.kGains_Velocit.kD, ShooterConstants.kTimeoutMs);
      topMotor.config_kF(ShooterConstants.kSlot_Velocit, ShooterConstants.kGains_Velocit.kF, ShooterConstants.kTimeoutMs);
      topMotor.config_IntegralZone(ShooterConstants.kSlot_Velocit, ShooterConstants.kGains_Velocit.kIzone, ShooterConstants.kTimeoutMs);
      topMotor.configClosedLoopPeakOutput(ShooterConstants.kSlot_Velocit, ShooterConstants.kGains_Velocit.kPeakOutput, ShooterConstants.kTimeoutMs);
      topMotor.configAllowableClosedloopError(ShooterConstants.kSlot_Velocit, 0, ShooterConstants.kTimeoutMs);

      /**
       * 1ms per loop.  PID loop can be slowed down if need be.
       * For example,
       * - if sensor updates are too slow
       * - sensor deltas are very small per update, so derivative error never gets large enough to be useful.
       * - sensor movement is very slow causing the derivative error to be near zero.
       */
      int closedLoopTimeMs = 1;
      topMotor.configClosedLoopPeriod(0, closedLoopTimeMs, ShooterConstants.kTimeoutMs);
      topMotor.configClosedLoopPeriod(1, closedLoopTimeMs, ShooterConstants.kTimeoutMs);
  
      /**
       * configAuxPIDPolarity(boolean invert, int timeoutMs)
       * false means talon's local output is PID0 + PID1, and other side Talon is PID0 - PID1
       * true means talon's local output is PID0 - PID1, and other side Talon is PID0 + PID1
       */
      topMotor.configAuxPIDPolarity(false, ShooterConstants.kTimeoutMs);
}


  @Override
  public void periodic() {

    //Insert a variable here for rpms value(Probably get the value from encoders?? Add if statement or something to see if desired value
    //testing is needed for such though)
    final double topRPMS = topMotor.getSelectedSensorVelocity();
    final double botRPMS = botMotor.getSelectedSensorVelocity();
    double currentVelocityTop = topRPMS * ShooterConstants.kSensorUnitsPerRotation / 600;
    double currentVelocityBot = botRPMS * ShooterConstants.kSensorUnitsPerRotation / 600;
    
    topPercent = SmartDashboard.getNumber("topMotorRPM", currentVelocityTop);
    botPercent = SmartDashboard.getNumber("botMotorRPM", currentVelocityBot);
    
  }
  //Charlie, change this. I am just using this as a placeholder until you're done
  public void sliderValueFunction() {
  
    topPercent = SmartDashboard.getNumber("topMotor%", 0);
    botPercent = SmartDashboard.getNumber("botMotor%", 0);
    topPID = SmartDashboard.getNumber("topPID", 0);
    botPID = SmartDashboard.getNumber("botPID", 0);
    topMotor.set(ControlMode.PercentOutput, -topPercent);
    botMotor.set(ControlMode.PercentOutput, botPercent);

  };


  public void ballMovingFunction(double top, double bot) {

    topMotor.set(ControlMode.PercentOutput, -top);
    botMotor.set(ControlMode.PercentOutput, bot);

  }

  public void ballMovingPID(double topRPM, double botRPM) {

//Top motor PID
      double topTargetVelocity_UnitsPer100ms = topRPM * ShooterConstants.kSensorUnitsPerRotation / 600;
          /* 500 RPM in either direction */
          topMotor.set(ControlMode.Velocity, topTargetVelocity_UnitsPer100ms);

//Bottom motor PID
      double botTargetVelocity_UnitsPer100ms = botRPM * ShooterConstants.kSensorUnitsPerRotation / 600;
       /* 500 RPM in either direction */
      botMotor.set(ControlMode.Velocity, botTargetVelocity_UnitsPer100ms);
  
  }
  //Charlie I need this done
  //More logic required for encoders
  public boolean speedReached(){
    return(true);
  }
}