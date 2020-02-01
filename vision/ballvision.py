# Single Color RGB565 Blob Tracking Example
#
# This example shows off single color RGB565 tracking using the OpenMV Cam.

import sensor, image, time, math

threshold_index = 0 # 0 for red, 1 for green, 2 for blue

# Color Tracking Thresholds (L Min, L Max, A Min, A Max, B Min, B Max)
# The below thresholds track in general red/green/blue things. You may wish to tune them...
thresholds = [] #

sensor.reset()
sensor.set_auto_exposure(False)
sensor.set_pixformat(sensor.RGB565)
sensor.set_framesize(sensor.QVGA)
sensor.skip_frames(time = 2000)
sensor.set_auto_gain(False) # must be turned off for color tracking
sensor.set_auto_whitebal(False) # must be turned off for color tracking
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
    
while(True):
    clock.tick()
    img = sensor.snapshot()
    ball = None

    for blob in img.find_blobs([(65, 100, -20, 20, 50, 95)], pixels_threshold=100, area_threshold=100, merge=True):
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
        print (angle(ball))
        


