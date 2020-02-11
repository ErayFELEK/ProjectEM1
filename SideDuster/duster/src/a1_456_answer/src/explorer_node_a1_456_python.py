#!/usr/bin/env python
## AK
## explorer_node_py.py
##
## BLG456E Assignment 1 skeleton
##
## Instructions: Change the laser_callback function to make the robot explore more
## intelligently, using its sensory data (the laser range array).
##
## Advanced: If you want to make use of the robot's mapping subsystem then you can
## make use of the map in the mapping_callback function.
##
## 

from __future__ import print_function
import rospy
import cv2
from sensor_msgs.msg import Image
from cv_bridge import CvBridge, CvBridgeError
import sys
import math

## This is needed for the data structure containing the motor command.
from geometry_msgs.msg import Twist
## This is needed for the data structure containing the laser scan
from sensor_msgs.msg import LaserScan
## This is needed for the data structure containing the map (which you may not use).
from nav_msgs.msg import OccupancyGrid


motor_command = Twist()
    ## For now let us just set it up to drive forward ...
motor_command.linear.y = 0
motor_command.linear.x = 0.0
    ## And left a little bit
motor_command.angular.z = 0

count = 10
z_angular_turn = 1.2
already = 0
last_leftmost = 0
last_rightmost = 0


#close the edges where laserscan can see
my_leftmost = 0
my_rightmost = 0

photo_found = 0
#To use while determining the area where robot can pass through
last_left = 0.0
last_right = 0.0
last_middle = 10.0
color = ''

input_taken = 0
#Bot slows down when the distance is smaller then 2 stop when 1.2 for pass through paremeters
threshold = 2
stopping_threshold = 1.2

#normal speed
x_velocity = 0
#slow speed
x_velocity_slow = 0

z_angular = 0.3

#not used
epsilon = 0.7

#State keeps track if every distance for pass through is bigger than threshold
changing_state = 0

#pass through distances
in_left = 464
in_right = 216
in_middle = 320

#Will be used to decide when turning in small spaces or there is a empty space on the right or left
in_leftmost = -1
in_rightmost = 0


# https://github.com/markwsilliman/turtlebot/
class TakePhoto:
    def __init__(self):

        self.bridge = CvBridge()
        self.image_received = False

        # Connect image topic
        img_topic = "/camera/rgb/image_raw"
        self.image_sub = rospy.Subscriber(img_topic, Image, self.callback)

        # Allow up to one second to connection
        rospy.sleep(1)

    def callback(self, data):

        # Convert image to OpenCV format
        try:
            cv_image = self.bridge.imgmsg_to_cv2(data, "bgr8")
        except CvBridgeError as e:
            print(e)

        self.image_received = True
        self.image = cv_image

    def take_picture(self, img_title):
        if self.image_received:
            # Save an image
            cv2.imwrite(img_title, self.image)
            return True
        else:
            return False

def laser_callback(data):
    
    
    
    
    
    
    
    
    
    
    
    
    global count
    global x_velocity_back
    global in_left
    global in_right
    global in_middle   
    global in_leftmost
    global in_rightmost
    global z_angular_turn
    global already
    global last_leftmost
    global right_leftmost
    global changing_state  
    global last_rightmost
    global photo_found
    global input_taken
    global x_velocity_slow
    global x_velocity
    global y_velocity
    global z_angular
    global color
    
    global last_left
    global last_right
    global last_middle
    
    global my_leftmost
    global my_rightmost
    
    #preprocessing the data     
    if( math.isnan(data.ranges[in_leftmost]) ):
        if(my_leftmost != 0):
            my_leftmost = 10
    else:
        if( data.ranges[in_leftmost] < 0.6 ):
            my_leftmost = 0
        else:
            my_leftmost = data.ranges[in_leftmost]
            
    if( math.isnan(data.ranges[in_rightmost]) ):
        if(my_rightmost != 0):
            my_rightmost = 10
    else:
        if( data.ranges[in_rightmost] < 0.6 ):
            my_rightmost = 0
        else:
            my_rightmost = data.ranges[in_rightmost]
    
    if( math.isnan(data.ranges[in_left]) ):
        if(last_left != 0):
            last_left = 10
    else:
        if( data.ranges[in_left] < 0.6 ):
            last_left = 0
        else:
            last_left = data.ranges[in_left]
        
    if( math.isnan(data.ranges[in_right]) ):
        if(last_right != 0):
            last_right = 10
    else:
        if( data.ranges[in_right] < 0.6 ):
            last_right = 0
        else:
            last_right = data.ranges[in_right]
        
    if( math.isnan(data.ranges[in_middle]) ):
        if(last_middle != 0):
            last_middle = 10
    else:
        if( data.ranges[in_middle] < 0.6 ):
            last_middle = 0
        else:
            last_middle = data.ranges[in_middle]
        
    #Cases are there to keep track of every possibility.
    #When pass through distances are bigger than threshold, move without angular velocity.
    #When any of the pass through distances becomes smaller than threshold, see if they are smaller...
    #Than the stopping threshold. Changing state is checked before moving on with the rest parts.
    #Changing state is there to avoid confusion the bot encounters when there are smilar distances...
    #on the right and the left. In that case keep turning untill every pass through distance is
    #bigger than the threshold.
    #In order to decide which side to turn to use my_last.. paremeters.
    
                
            

        
        
    
 

    ## Lets fill a twist message for motor command
    #motor_command = Twist()
    ## For now let us just set it up to drive forward ...
    #motor_command.linear.y = 0.6
    #motor_command.linear.x = -0.3
    ## And left a little bit
    #motor_command.angular.z = 0.1 
    ## Lets publish that command so that the robot follows it
    
        
    
        
        
    #Changing state first before coming cloese to 0.8
    #if 0.8 go to changing state 1 where we can turn 90 degrees.
    #Count is there to publish consistently untill 90 degrees
    #after making a turn right at the first the we always turn left so already is there to keep track of that


    if(photo_found == 0):
        if(input_taken == 0):
            color = raw_input("Enter color: ")
            input_taken = 1
        if(photo_and_center(color)):
            motor_command.angular.z = 0
            photo_found = 1
        else:
            motor_command.angular.z = 0.25
            motor_command_publisher.publish(motor_command)
			
    else:
        if(changing_state == 0):
            print("state0")
            if(last_middle > 0.8):
                x_velocity = 0.2
            else:
                x_velocity = 0
                changing_state = 1
        elif(changing_state == 1):
            print("state1")
            if(count > 0):
                motor_command.angular.z = z_angular_turn
                count = count - 1
            else:
                changing_state = 2
                count = 10
                motor_command.angular.z = 0
        elif(changing_state == 2):
            if(already == 0):
                z_angular_turn = - z_angular_turn
                already = 1
        
                
            print("state2")
            if(z_angular_turn > 0):
                if(my_leftmost < 1.3):
                    x_velocity = 0.2
                    if(last_leftmost > my_leftmost):
                        motor_command.angular.z  = - z_angular_turn*(0.2)
                    else:
                        motor_command.angular.z = 0
                else:
                    if(count > 0):
                        x_velocity = 0.85
                        count = count -1 
                    else:
                        x_velocity = 0
                        count = 10
                        changing_state = 1
                        z_angular_turn = z_angular_turn*(0.9)
                        print('z anguler turn is : ' , z_angular_turn)
            else:
                if(my_rightmost < 1.3):
                    x_velocity = 0.2
                    if(last_rightmost > my_rightmost):
                        motor_command.angular.z  = - z_angular_turn*(0.2)
                    else:
                        motor_command.angular.z = 0
                else:
                    if(count > 0):
                        x_velocity = 0.85
                        count = count -1 
                    else:
                        x_velocity = 0
                        count = 10
                        changing_state = 1
                        z_angular_turn = z_angular_turn*(0.9)
                        print('z anguler turn is : ', z_angular_turn)
        
        motor_command.linear.x = x_velocity
        motor_command_publisher.publish(motor_command)
        
        last_leftmost = my_leftmost
        last_rightmost = my_rightmost
        #print("This oneeee")
        #print("This oneeee")
        print('x vel: ' , motor_command.linear.x)
        print('y vel: ' , motor_command.linear.y)
        print('z vel: ' , motor_command.angular.z)           
        # motor_command_publisher.publish(motor_command)
        
        ## Alternatively we could have looked at the laser scan BEFORE we made this decision
        ## Well Lets see how we might use a laser scan
        ## Laser scan is an array of distances
        
        
        #data.ranges[data.ranges==np.nan] =10
        print('my defined left dar: ', data.ranges[in_left])
        print('my defined right dar: ', data.ranges[in_right])
        print('my defined middle dar: ', last_middle)
        #print 'last left: ', last_left
        #print 'last right: ', last_right
        #print 'last middle: ', last_middle
        
        print('rightmosh: ', my_rightmost)
        print('leftmosh: ', my_leftmost)
        
        print('Number of points in laser scan is: ', len(data.ranges))
        print('The distance to the rightmost scanned point is: ', data.ranges[0])
        print('The distance to the leftmost scanned point is: ', data.ranges[-1])
        
        print('The distance to the middle scanned point is: ', data.ranges[len(data.ranges)/2])
        ## You can use basic trigonometry with the above scan array and the following information to find out exactly where the laser scan found something
        #print 'The minimum angle scanned by the laser is: ', data.angle_min
        #print 'The maximum angle scanned by the laser is: ', data.angle_max
        #print 'The increment in the angles scanned by the laser is: ', data.angle_increment
        ## angle_max = angle_min+angle_increment*len(data.ranges)
        #print 'The minimum range (distance) the laser can perceive is: ', data.range_min
        #print 'The maximum range (distance) the laser can perceive is: ', data.range_max
    
    
    

    
    
## You can also make use of the map which is being built by the "gslam_mapping" subsystem
## There is some code here to help but you can understand the API also by looking up the OccupancyGrid message and its members (this is the API for the message)
## If you want me to explain the data structure, I will - just ask me in advance of class
def map_callback(data):
    chatty_map = False
    if chatty_map:
        print("-------MAP---------")
        ## Here x and y has been incremented with five to make it fit in the terminal
        ## Note that we have lost some map information by shrinking the data
        for x in range(0,data.info.width-1,5):
            for y in range(0,data.info.height-1,5):
                index = x+y*data.info.width
                if data.data[index] > 50:
                    ## This square is occupied
                    sys.stdout.write('X')
                elif data.data[index] >= 0:
                    ## This square is unoccupied
                    sys.stdout.write(' ')
                else:
                    sys.stdout.write('?')
            sys.stdout.write('\n')
        sys.stdout.flush()
        print("-------------------")
    
## This is the method we initilize everything
def explorer_node():
    ## We must always do this when starting a ROS node - and it should be the first thing to happen
    rospy.init_node('amble')
    
    ## Here we declare that we are going to publish "Twist" messages to the topic /cmd_vel_mux/navi. It is defined as global because we are going to use this publisher in the laser_callback.
    global motor_command_publisher
    motor_command_publisher = rospy.Publisher('/cmd_vel_mux/input/navi', Twist, queue_size = 10)
    
    ## Here we set the function laser_callback to recieve new laser messages when they arrive
    rospy.Subscriber("/scan", LaserScan, laser_callback, queue_size = 1000)
    
    ## Here we set the function map_callback to recieve new map messages when they arrive from the mapping subsystem
    rospy.Subscriber("/map", OccupancyGrid, map_callback, queue_size = 1000)
    
    ## spin is an infinite loop but it lets callbacks to be called when a new data available. That means spin keeps this node not terminated and run the callback when nessessary. 
    rospy.spin()

def photo_and_center(color):
    is_centered = False
    camera = TakePhoto()

    img_title = rospy.get_param('~image_title', 'photo.jpg')
    print(img_title)

    if camera.take_picture(img_title):
        rospy.loginfo("Saved image " + img_title)
        image = cv2.imread(img_title)
        print(image.shape)
        left_count = 0
        right_count = 0
        print(int(image[240,0,0])+int(image[240,0,1]))
        if(color == "red"):
			for i in range(len(image[240])):
				if(int(image[240,i,2])>(int(image[240,i,0])+int(image[240,i,1]))):
					left_count = i
					break
			for c in range(len(image[240])):
				if(int(image[240,639-c,2])>(int(image[240,639-c,0])+int(image[240,639-c,1]))):
					right_count = c
					break
        if(color == "green"):
            for i in range(len(image[240])):
                if(int(image[240,i,1])>(int(image[240,i,2])+int(image[240,i,0]))):
                    left_count = i
                    break
            for c in range(len(image[240])):
                if(int(image[240,639-c,1])>(int(image[240,639-c,2])+int(image[240,639-c,0]))):
                    right_count = c
                    break
        print(image[240,639])
        print("left is: ",left_count,"right is: ",right_count)
        if(abs(left_count-right_count)<100):
            is_centered = True
        if(left_count == 0 and right_count == 0):
            is_centered = False
        print(is_centered)
				
        #cropped_image = image[150:250,:]
        #cv2.imwrite("cropped_image.jpg",cropped_image)
    else:
        rospy.loginfo("No images received")
    
    return is_centered


if __name__ == '__main__':
	explorer_node()
    


