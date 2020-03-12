# Single Color RGB565 Blob Tracking Example
#
# This example shows off single color RGB565 tracking using the OpenMV Cam.

import sensor, image, time, math

# Color Tracking Thresholds (L Min, L Max, A Min, A Max, B Min, B Max)
# The below thresholds track in general red/green/blue things. You may wish to tune them...

screen = 400 #450
sensor.reset()
sensor.set_pixformat(sensor.RGB565)
sensor.set_framesize(sensor.VGA)
sensor.set_windowing((int((640-screen)/2),int((480-screen)/2),screen,screen))
sensor.set_auto_exposure(False, 5000)
sensor.set_auto_gain(False) # must be turned off for color tracking
sensor.set_auto_whitebal(False) # must be turned off for color tracking
clock = time.clock()

# Only blobs that with more pixels than "pixel_threshold" and more area than "area_threshold" are
# returned by "find_blobs" below. Change "pixels_threshold" and "area_threshold" if you change the
# camera resolution. "merge=True" merges all overlapping blobs in the image.

while(True):
    clock.tick()
    img = sensor.snapshot()

    blobs = img.find_blobs([(15, 94, -57, -12, -48, -10)], pixels_threshold=200, area_threshold=200, merge=True)

    img.draw_cross(225,225)
    for blob in blobs:
        # These values are stable all the time.
        img.draw_rectangle(blob.rect())
        img.draw_cross(blob.cx(), blob.cy())
        print(blob.density())
    #print(int(clock.fps()))
    #img.copy(x_scale=.2,y_scale=.2,copy_to_fb=True)
