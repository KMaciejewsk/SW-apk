from MotorShield import PiMotor
import time
import RPi.GPIO as GPIO
import socket

#Name of Individual MOTORS 
m1 = PiMotor.Motor("MOTOR1",1)
m2 = PiMotor.Motor("MOTOR2",1)
m3 = PiMotor.Motor("MOTOR3",1)
m4 = PiMotor.Motor("MOTOR4",1)

#To drive all motors together
motorAll = PiMotor.LinkedMotors(m1,m2,m3,m4)

#Names for Individual Arrows
ab = PiMotor.Arrow(1)
al = PiMotor.Arrow(2)
af = PiMotor.Arrow(3) 
ar = PiMotor.Arrow(4)

##This segment drives the motors in the direction listed below:
## forward and reverse takes speed in percentage(0-100)

#moves_history = [[0.1,0.1],[0.5,0.5],[0.7,0.7],[1.0,1.0],[1,1],[1,1],[0.3,1],[0.3,1],[0.3,1],[0.3,1],[0.3,1],[0.3,1],[0.3,1],[0.3,1],[0.3,1],[0,0],[0,0],[0,0],[-1,-1],[-1,-1],[0,-1]]
moves_history = []

def processVector(vec):
    if vec[0] == 0 and vec[1]==0:
        al.off()
        af.off()
        ar.off()
        ab.off()
        motorAll.stop()
    elif vec[0] > 0 or vec[1] > 0:
        af.on()

        m1.forward(vec[0]*100)
        m2.forward(vec[0]*100)

        m3.forward(vec[1]*100)
        m4.forward(vec[1]*100)
        time.sleep(0.1)
    elif vec[0] < 0 or vec[1] < 0:
        ab.on()

        m1.reverse(abs(vec[0])*90)
        m2.reverse(abs(vec[0])*90)

        m3.reverse(abs(vec[1])*90)
        m4.reverse(abs(vec[1])*90)
        time.sleep(0.1)

def createReversedHistory(hist):
    reversed_history = []
    hist.reverse()
    for move in hist:
        reversed_history.append([-move[0],-move[1],move[2]])
    return reversed_history

def runBack(hist):
    reversed_history = createReversedHistory(hist)
    timeend = 0
    timestart = 0
    for move in reversed_history:
        if move[0] == 0 and move[1] == 0:
            timeend = move[2]
        else:
            timestart = move[2]
            deltatime = int(timeend) - int(timestart)
            processVector([move[0],move[1]])
            print ('Vector is: ', move[0], move[1], 'Time is: ', deltatime)
            time.sleep(abs(deltatime)/1000.0)
        
    motorAll.stop()
    moves_history.clear()
    reversed_history.clear()

sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
server_address = ('0', 10000)
sock.bind(server_address)
sock.listen(1)

def connect():
    print('waiting for a connection')
    connection, client_address = sock.accept()
    return connection, client_address

while True:
    connection, client_address = connect()
    try:
        while True:
            print('waiting for input')
            data = connection.recv(16)
            if not data:
                break
            if data:
                data = data.decode()
                print(data)
                if data == "back":
                        runBack(moves_history)
                        continue
                d = data.split('>')
                command = d[0]
                time_of_command = d[1]
                print(command, time_of_command)

                if d[0]:
                    if d[0] == "F":
                        processVector([1,1])
                        moves_history.append([1,1,d[1]])
                    elif d[0] == "B":
                        processVector([-1,-1])
                        moves_history.append([-1,-1,d[1]])
                    elif d[0] == "L":
                        processVector([0,1])
                        moves_history.append([0,1,d[1]])
                    elif d[0] == "R":
                        processVector([1,0])
                        moves_history.append([1,0,d[1]])
                    elif d[0] == "S":
                        processVector([0,0,d[1]])
                        moves_history.append([0,0,d[1]])
                    #motorAll.stop()

                else:
                    break
    # if ctrl + c is pressed or client connection is closed
    except KeyboardInterrupt:
        connection.close()
        sock.close()
        GPIO.cleanup()
        print('Exiting')
