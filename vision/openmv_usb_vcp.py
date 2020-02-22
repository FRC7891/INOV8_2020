# USB VCP example.
# This example shows how to use the USB VCP class to send an image to PC on demand.
#
# WARNING:
# This script should NOT be run from the IDE or command line, it should be saved as main.py
# Note the following commented script shows how to receive the image from the host side.
#
# #!/usr/bin/env python2.7
# import sys, serial, struct
# port = '/dev/ttyACM0'
# sp = serial.Serial(port, baudrate=115200, bytesize=serial.EIGHTBITS, parity=serial.PARITY_NONE,
#             xonxoff=False, rtscts=False, stopbits=serial.STOPBITS_ONE, timeout=None, dsrdtr=True)
# sp.setDTR(True) # dsrdtr is ignored on Windows.
# sp.write("snap")
# sp.flush()
# size = struct.unpack('<L', sp.read(4))[0]
# img = sp.read(size)
# sp.close()
#
# with open("img.jpg", "w") as f:
#     f.write(img)

import sensor, image, time, ustruct, pyb
from pyb import USB_VCP

DBG=True


usb = USB_VCP()
sensor.reset()                      # Reset and initialize the sensor.
sensor.set_pixformat(sensor.RGB565) # Set pixel format to RGB565 (or GRAYSCALE)
sensor.set_framesize(sensor.QVGA)   # Set frame size to QVGA (320x240)
sensor.set_auto_exposure(False, 5000)
sensor.set_auto_gain(False)
sensor.set_auto_whitebal(False)
#sensor.set_contrast(   -3 ) #-3 +3
#sensor.set_brightness( +3 ) #-3 +3
sensor.set_saturation( +3 ) #-3 +3
sensor.skip_frames(time = 1000)


imgMs = 0
usbMs = 0
stream = True


while (True):
    ticks = time.ticks()
    pyb.LED(3).toggle()
    img = sensor.snapshot()
    cnt = 0
    for blob in img.find_blobs([(10,100, 50,70, 40,70)], pixels_threshold=120, area_threshold=100, merge=True):
        cnt += 1
        if DBG and stream:
            img.draw_rectangle( blob.rect(), thickness=4)
            img.draw_string( blob.x(), blob.y()-10, '{0:1d}'.format(cnt), scale=4)
            img.draw_string(0,0,  '{0:d}'.format(time.ticks()), scale=4)
            img.draw_string(0,25, '{0:2d} {1:2d}'.format(imgMs, usbMs), scale=4)

    imgMs = time.ticks() - ticks

    # \/ \/ USB CODE \/ \/
    ticks = time.ticks()
    if stream:
        img.copy(x_scale=.25,y_scale=.25,copy_to_fb=True)
        img.compress(88) #90

    cmd = usb.recv(2, timeout=1000) # Change this to match the number of commands received
    if not cmd:
        continue
    if cmd[0] == b's'[0] and stream:
        usb.send(ustruct.pack(">LLL", imgMs, usbMs, img.size()))
        usb.send(img)
    else:
        usb.send(ustruct.pack(">LLL", imgMs, usbMs, 0))
        if cmd[0] == b's'[0]:
            stream = True
        else:
            stream = False
    usbMs = time.ticks() - ticks

    if cmd[1] == b'r'[0]:
        pyb.LED(1).toggle()
    if cmd[1] == b'g'[0]:
        pyb.LED(2).toggle()
    # /\ /\ USB CODE /\ /\

    #print('{} {}'.format(imgMs,usbMs))

