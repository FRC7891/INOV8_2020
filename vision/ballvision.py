# Single Color RGB565 Blob Tracking Example
#BALL SLURPER
# This example shows off single color RGB565 tracking using the OpenMV Cam.

import sensor, pin, image, time, math, ustruct, pyb
from pyb import USB_VCP

DBG=True


usb = USB_VCP()
pin = Pin('P0', Pin.OUT_OD)

# Color Tracking Thresholds (L Min, L Max, A Min, A Max, B Min, B Max)
# The below thresholds track in general red/green/blue things. You may wish to tune them...

sensor.reset()

sensor.set_pixformat(sensor.RGB565)
sensor.set_framesize(sensor.QVGA)
sensor.set_auto_exposure(False, 5000)
sensor.set_auto_gain(False) # must be turned off for color tracking
sensor.set_auto_whitebal(False) # must be turned off for color tracking
sensor.skip_frames(time = 2000)
clock = time.clock()

# Only blobs that with more pixels than "pixel_threshold" and more area than "area_threshold" are
# returned by "find_blobs" below. Change "pixels_threshold" and "area_threshold" if you change the
# camera resolution. "merge=True" merges all overlapping blobs in the image.
def angle(ball):
    if (ball == None):
        return  0
    else:
        px = ball.cx() - 160
        return px * .22125


stream = False

while(True):
    clock.tick()
    img = sensor.snapshot()
    ball_angle = 0
    ball_dist = 0
    tracking_ball = False
    ball = None

    for blob in img.find_blobs([(30, 100, -50, 20, 35, 75)], pixels_threshold=100, area_threshold=100, merge=True):
        # These values depend on the blob not being circular - otherwise they will be shaky.
        if (ball == None ):
            ball = blob

        elif( blob.cy() > ball.cy() ):
            ball = blob
        # These values are stable all the time.
        img.draw_rectangle(blob.rect())
        img.draw_cross(blob.cx(), blob.cy())
    # Note - the blob rotation is unique to 0-180 only.
    if ( ball != None ):
        img.draw_rectangle(ball.rect(), (0,0,255))
        ball_angle = (angle(ball))
        tracking_ball = True
    print( tracking_ball)


    # \/ \/ USB CODE \/ \/
    ticks = time.ticks()
    if stream:
        img.copy(x_scale=.25,y_scale=.25,copy_to_fb=True)
        img.compress(88) #90

    cmd = usb.recv(2, timeout=1000) # Change this to match the number of commands received
    if not cmd:
        continue
    if cmd[0] == b's'[0] and stream:
        usb.send(ustruct.pack(">llll", tracking_ball , ball_angle , ball_dist, img.size()))
        usb.send(img)
    else:
        usb.send(ustruct.pack(">llll", tracking_ball , ball_angle , ball_dist, 0))
        if cmd[0] == b's'[0]:
            stream = True
        else:
            stream = False


    if cmd[1] == b'r'[0]:
        pyb.LED(1).toggle()
    if cmd[1] == b'g'[0]:
        pyb.LED(2).toggle()
    if cmd[1] == b'w'[0]:
        pin.value(False)
    else:
        pin.value(True)
    # /\ /\ USB CODE /\ /\
