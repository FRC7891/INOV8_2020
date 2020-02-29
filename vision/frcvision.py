#!/usr/bin/env python3
#----------------------------------------------------------------------------
# Copyright (c) 2018 FIRST. All Rights Reserved.
# Open Source Software - may be modified and shared by FRC teams. The code
# must be accompanied by the FIRST BSD license file in the root directory of
# the project.
#----------------------------------------------------------------------------

import json
import time
import sys
import numpy as np
import struct
import threading
import serial
import cv2 as cv

from cscore import CameraServer, VideoSource, UsbCamera, MjpegServer
from networktables import NetworkTablesInstance
import ntcore

#   JSON format:
#   {
#       "team": <team number>,
#       "ntmode": <"client" or "server", "client" if unspecified>
#       "cameras": [
#           {
#               "name": <camera name>
#               "path": <path, e.g. "/dev/video0">
#               "pixel format": <"MJPEG", "YUYV", etc>   // optional
#               "width": <video mode width>              // optional
#               "height": <video mode height>            // optional
#               "Ms": <video mode Ms>                  // optional
#               "brightness": <percentage brightness>    // optional
#               "white balance": <"auto", "hold", value> // optional
#               "exposure": <"auto", "hold", value>      // optional
#               "properties": [                          // optional
#                   {
#                       "name": <property name>
#                       "value": <property value>
#                   }
#               ],
#               "stream": {                              // optional
#                   "properties": [
#                       {
#                           "name": <stream property name>
#                           "value": <stream property value>
#                       }
#                   ]
#               }
#           }
#       ]
#       "switched cameras": [
#           {
#               "name": <virtual camera name>
#               "key": <network table key used for selection>
#               // if NT value is a string, it's treated as a name
#               // if NT value is a double, it's treated as an integer index
#           }
#       ]
#   }

configFile = "/boot/frc.json"

class CameraConfig: pass

team = 7891
server = True
cameraConfigs = []
switchedCameraConfigs = []
cameras = []

def parseError(str):
    """Report parse error."""
    print("config error in '" + configFile + "': " + str, file=sys.stderr)

def readCameraConfig(config):
    """Read single camera configuration."""
    cam = CameraConfig()

    # name
    try:
        cam.name = config["name"]
    except KeyError:
        parseError("could not read camera name")
        return False

    # path
    try:
        cam.path = config["path"]
    except KeyError:
        parseError("camera '{}': could not read path".format(cam.name))
        return False

    # stream properties
    cam.streamConfig = config.get("stream")

    cam.config = config

    cameraConfigs.append(cam)
    return True

def readSwitchedCameraConfig(config):
    """Read single switched camera configuration."""
    cam = CameraConfig()

    # name
    try:
        cam.name = config["name"]
    except KeyError:
        parseError("could not read switched camera name")
        return False

    # path
    try:
        cam.key = config["key"]
    except KeyError:
        parseError("switched camera '{}': could not read key".format(cam.name))
        return False

    switchedCameraConfigs.append(cam)
    return True

def readConfig():
    """Read configuration file."""
    global team
    global server

    # parse file
    try:
        with open(configFile, "rt", encoding="utf-8") as f:
            j = json.load(f)
    except OSError as err:
        print("could not open '{}': {}".format(configFile, err), file=sys.stderr)
        return False

    # top level must be an object
    if not isinstance(j, dict):
        parseError("must be JSON object")
        return False

    # team number
    try:
        team = j["team"]
    except KeyError:
        parseError("could not read team number")
        return False

    # ntmode (optional)
    if "ntmode" in j:
        str = j["ntmode"]
        if str.lower() == "client":
            server = False
        elif str.lower() == "server":
            server = True
        else:
            parseError("could not understand ntmode value '{}'".format(str))

    # cameras
    try:
        cameras = j["cameras"]
    except KeyError:
        parseError("could not read cameras")
        return False
    for camera in cameras:
        if not readCameraConfig(camera):
            return False

    # switched cameras
    if "switched cameras" in j:
        for camera in j["switched cameras"]:
            if not readSwitchedCameraConfig(camera):
                return False

    return True

def startCamera(config):
    """Start running the camera."""
    print("Starting camera '{}' on {}".format(config.name, config.path))
    inst = CameraServer.getInstance()
    camera = UsbCamera(config.name, config.path)
    server = inst.startAutomaticCapture(camera=camera, return_server=True)

    camera.setConfigJson(json.dumps(config.config))
    camera.setConnectionStrategy(VideoSource.ConnectionStrategy.kKeepOpen)

    if config.streamConfig is not None:
        server.setConfigJson(json.dumps(config.streamConfig))

    return camera

def startSwitchedCamera(config):
    """Start running the switched camera."""
    print("Starting switched camera '{}' on {}".format(config.name, config.key))
    server = CameraServer.getInstance().addSwitchedCamera(config.name)

    def listener(fromobj, key, value, isNew):
        if isinstance(value, float):
            i = int(value)
            if i >= 0 and i < len(cameras):
                server.setSource(cameras[i])
        elif isinstance(value, str):
            for i in range(len(cameraConfigs)):
                if value == cameraConfigs[i].name:
                    server.setSource(cameras[i])
                    break

    NetworkTablesInstance.getDefault().getEntry(config.key).addListener(
        listener,
        ntcore.constants.NT_NOTIFY_IMMEDIATE |
        ntcore.constants.NT_NOTIFY_NEW |
        ntcore.constants.NT_NOTIFY_UPDATE)

    return server

##
# prefix - prefix used for all the entries in the NetworkTable
# arrIn -  ['Img','Light'] - NetworkTable bools to be packed and passed to the camera. Up to 7 to fill a byte.
# arrOut - ['JpegSize','ImgMs','Tracking','Turn','Dist'] - Received data to be unpacked and assigned to NetworkTable entries
#            JpegSize is special in that when this string is matched, that many bytes are then read to build and send an image.
# camera - path to the OpenMV Camera to use
# cs     - the shared cameraserver to use for all the cameras
def OpenMV(prefix, arrIn, arrOut, cs, camera):
    serialOpen = False
    ser = None

    for i, v in enumerate(arrIn):
        NetworkTablesInstance.getDefault().getTable(prefix).setDefaultString(v, '!')

    # loop forever
    while True:
        jpegSize = 0

        if serialOpen == False:
            try:
                time.sleep(2)
                ser = serial.Serial(camera, 11999999, timeout=1)
            except:
                print("Unexpected error(1):", sys.exc_info())
                continue
            print(ser.name + ' connected!')
            serialOpen = True

        cmds = ''
        for i, v in enumerate(arrIn):
            s = NetworkTablesInstance.getDefault().getTable(prefix).getString(v, '!')
            if s == '':
                s = '!'
            cmds += s[0]

        arr = bytearray()
        try:
            ser.flush()
            ser.write(cmds.encode())
            cnt = 1000
            while (len(arr) != (len(arrOut) * 4) and cnt != 0):
                cnt -= 1
                arr.extend(bytearray(ser.read((len(arrOut) * 4) - len(arr))))
        except:
            print("Unexpected error(2):", sys.exc_info())
            print(ser.name + ' disconnected!')
            serialOpen = False
            ser.close()
            continue

        for i, v in enumerate(struct.unpack('>' + 'l' * len(arrOut), arr)):
            if arrOut[i] == 'JpegSize':
                jpegSize = v
            NetworkTablesInstance.getDefault().getTable(prefix).putNumber(arrOut[i], v)

        if jpegSize > 0x8000:
            continue

        if jpegSize != 0:
            arr = bytearray()
            try:
                cnt = 1000
                while (len(arr) != jpegSize and cnt != 0):
                    cnt -= 1
                    arr.extend(bytearray(ser.read(jpegSize - len(arr))))
            except:
                print("Unexpected error(3):", sys.exc_info())
                print(ser.name + ' disconnected!')
                serialOpen = False
                ser.close()
                continue

            img = cv.imdecode(np.frombuffer(arr, dtype=np.uint8), 1)
            cs.putFrame(img)
        #else:
        #    img = np.zeros((240, 320, 3), np.uint8)
        #    cv.putText(img, '{}'.format(imgMs), (30,30), cv.FONT_HERSHEY_SIMPLEX,0.5,(255,255,255),1,cv.LINE_AA)
        #    cs.putFrame(img)

    ser.close()  # close port


if __name__ == "__main__":

    if len(sys.argv) >= 2:
        configFile = sys.argv[1]

    # read configuration
    if not readConfig():
        sys.exit(1)

    # start NetworkTables
    ntinst = NetworkTablesInstance.getDefault()
    if server:
        print("Setting up NetworkTables server")
        ntinst.startServer()
    else:
        print("Setting up NetworkTables client for team {}".format(team))
        ntinst.startClientTeam(team)

    # start cameras
    for config in cameraConfigs:
        cameras.append(startCamera(config))

    # start switched cameras
    for config in switchedCameraConfigs:
        startSwitchedCamera(config)

    # start 'Camera' camera server for OpenMv cameras
    cs = CameraServer.getInstance().putVideo('Camera', 320, 240)

#    threading.Thread(
#        target=OpenMV,
#        args=
#        ('Ball',
#         ['Img', 'Light'],
#         ['ImgMs', 'UsbMs', 'JpegSize'],
#         cs, '/dev/serial/by-path/platform-fd500000.pcie-pci-0000:01:00.0-usb-0:1.3:1.0'
#         ),  #'/dev/ttyACM0'
#        daemon=True).start()

    threading.Thread(
        target=OpenMV,
        args=
        ('Ball',
         ['Img', 'Light'],
         ['Tracking', 'Angle', 'Distance', 'JpegSize'],
         cs, '/dev/serial/by-path/platform-fd500000.pcie-pci-0000:01:00.0-usb-0:1.3:1.0'
         ),  #'/dev/ttyACM0'
        daemon=True).start()

    # loop forever
    while True:
        time.sleep(10)
