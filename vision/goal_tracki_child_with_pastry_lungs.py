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
#                |  |               \
#                |  |                \
#                |  |                 \
#                |_ |                  \
#                 _ |___________________\ __________
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

while(True):
    clock.tick()
    img = sensor.snapshot()
    hight_from_robot = 0
    for blob in img.find_blobs([((10, 90, -102, -30, 28, 89))], pixels_threshold=20, area_threshold=20, merge=True):
        img.draw_rectangle(blob.rect(), (255,255,255))
        # Note - the blob rotation is unique to 0-180 only.
        if(test == True):
            #print ( blob.h()/ blob.w() )
            print (blob.density())
        ratio = blob.h()/blob.w()
        if ( ratio > 0.4 and ratio < 0.7 and blob.density() < 0.3):
            img.draw_rectangle(blob.rect(), (0,0,255))

