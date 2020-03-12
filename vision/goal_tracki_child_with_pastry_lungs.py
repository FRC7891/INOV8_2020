# Single Color Code Tracking Example
#
# This example shows off single color code tracking using the OpenMV Cam.
#
# A color code is a blob composed of two or more colors. The example below will
# only track colored objects which have both the colors below in them.

import sensor, image, time, math,ustruct, pyb
from pyb import USB_VCP, Pin

DBG=True


usb = USB_VCP()
pin = Pin('P0', Pin.OUT_OD)

# Color Tracking Thresholds (L Min, L Max, A Min, A Max, B Min, B Max)
# The below thresholds track in general red/green things. You may wish to tune them...
  # generic_red_thresholds -> index is 0 so code == (1 << 0)
              # generic_green_thresholds -> index is 1 so code == (1 << 1)
# Codes are or'ed together when "merge=True" for "find_blobs".

sensor.reset()
sensor.set_pixformat(sensor.RGB565)
sensor.set_framesize(sensor.QVGA)
sensor.skip_frames(time = 2000)
sensor.set_auto_exposure(False,5000)
sensor.set_auto_gain(False,0,0) # must be turned off for color tracking
sensor.set_auto_whitebal(False,(0,0,0)) # must be turned off for color tracking
sensor.set_saturation(+3)
sensor.set_contrast(-3)

clock = time.clock()
#                 _
#                |  \
#                |  |\
#                |  | \
#     Goal/ _____| _|  \
#     Hole       |  |   \
#                |  |    \
#                |  |     \
#                |  |      \
#                |  |       \
#                |  |        \
#       Goal   _ |  |         \ <---- upward_distance
#       Hight    |  |          \
#                |  |           \
#                |  |            \
#                |  |             \
#                |  |              \
#                |  |              _\
#                |  |             /  \
#                |  |steep_angle>/ ___\
#                |_ |            |/    \<--- constant_angle
#                 _ |____________|______\ __________
#         Robot _|  |                    ||||||||||||<---- Robot
#         Hight  |_ |____________________||||||||||||
#                           ^
#                           |
#                       distance_away

#               hight_from_robot = (Goal Hight) - (Robot Hight)


# Only blobs that with more pixels than "pixel_threshold" and more area than "area_threshold" are
# returned by "find_blobs" below. Change "pixels_threshold" and "area_threshold" if you change the
# camera resolution. "merge=True" must be set to merge overlapping color blobs for color codes.

test = True
stream = True
goal_hight = 0
constant_angle = 0
def find_goal_angle():
    return( )


def find_distance():
    if(find_steep_angle() > 0):
        return( ( math.cos(find_steep_angle()) / math.sin(find_steep_angle()) ) * goal_hight )
    else:
        return(0)
       
def find_steep_angle():
  return((abs(  (blob.y()+blob.h()) - 240  )* (0.00404334625)) + constant_angle)


while(True):
    clock.tick()
    img = sensor.snapshot()
 
    for blob in img.find_blobs([((10, 90, -102, -30, 28, 89))], pixels_threshold=20, area_threshold=20, merge=True):
        img.draw_rectangle(blob.rect(), (255,255,255))
        # Note - the blob rotation is unique to 0-180 only.
        if(test == True):
            #print ( blob.h()/ blob.w() )
            #print (blob.density())
            print (find_distance() )
        ratio = blob.h()/blob.w()
        if ( ratio > 0.4 and ratio < 0.7 and blob.density() < 0.3):
            goal_distance = find_distance()
            #offset_angle = find_offset_angle() *  180/math.pi
        if (stream == True):
                img.draw_rectangle(blob.rect(), (0,0,255))
        


# \/ \/ USB CODE \/ \/ AND THE LLLL's are attacking!
ticks = time.ticks()
if stream:
    img.copy(x_scale=.25,y_scale=.25,copy_to_fb=True)
    img.compress(88) #90

cmd = usb.recv(2, timeout=1000) # Change this to match the number of commands received
if not cmd:
    continue
if cmd[0] == b'1'[0] and stream:
    usb.send(ustruct.pack(">llll", use_the_force_luke , goal_angle , goal_distance , img.size()))
    usb.send(img)
else:
    usb.send(ustruct.pack(">llll", use_the_force_luke , goal_angle , goal_distance , 0 ))
    if cmd[0] == b'1'[0]:
        stream = True
    else:
        stream = False


if cmd[1] == b'r'[0]:
    pyb.LED(1).toggle()
if cmd[1] == b'g'[0]:
    pyb.LED(2).toggle()
if cmd[1] == b'1'[0]:
    pin.value(True)
else:
    pin.value(False)
       # /\ /\ USB CODE /\ /\
# /\ /\ USB CODE /\ /\
