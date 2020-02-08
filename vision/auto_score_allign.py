# Untitled - By: 7891 Elon Musk - Sat Feb 8 2020

import sensor, image, time

sensor.reset()
sensor.set_pixformat(sensor.RGB565)
sensor.set_framesize(sensor.QVGA)
sensor.skip_frames(time = 2000)

clock = time.clock()

def auto_allign():
    distance_away = camera_angle sin((90-camera_angle)) (1/sin(camera_angle))

while(True):
    clock.tick()
    img = sensor.snapshot()
    print(clock.fps())