import serial
import serial.tools.list_ports
from pykeyboard import PyKeyboard
from typing import Optional
import time
import sys

SERIAL_ID = "USB VID:PID=10C4:EA60"
serial_conneciton = None

def press_button(k, string):
 
 if string is "R":
  k.tap_key('1')
 elif string is "B":
  k.tap_key('2')
 elif string is "Y":
  k.tap_key('3')
 elif string is "G":
  k.tap_key('4')
 

def listen_for_port():
 ports = serial.tools.list_ports.comports()
 devices = {}

 serial_port: Optional[str] = None

 for port, _, hwid in sorted(ports):
  devices[hwid] = port

 for hwid in devices.keys():
  if SERIAL_ID in hwid:
   return devices[hwid]

def connect(serial_port):
 print("Connecting to: " + serial_port)
 return serial.Serial(serial_port, 115200)

def connect_loop():
 while True:
  serial_port = listen_for_port()
  if serial_port != None:
   print("Found device! Connecting...")
   return connect(serial_port)
  print("Retry in 2 seconds")
  time.sleep(2)

def listen(ser):
 print("Starting loop")
 k = PyKeyboard()
 while True:
  try:
   try:
    if ser.in_waiting > 0:
     string = str(ser.readline().decode('ascii')).strip()
     press_button(k, string)
     print(string)
    time.sleep(0.005)
   except KeyboardInterrupt:
    ser.close()
    print("Exiting...")
    sys.exit(0)
   except:
    print("some error")
    try:
     ser.close()
    except:
     pass
    ser = connect_loop()
  except KeyboardInterrupt:
   ser.close()
   print("Exiting...")
   sys.exit(0)

serial_conneciton = connect_loop()
print("Listening...")
listen(serial_conneciton)
