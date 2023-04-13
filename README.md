[![Codacy Badge](https://api.codacy.com/project/badge/Grade/3f9ba45b5c5449179150010659311f57)](https://www.codacy.com/manual/kai-morich/SimpleBluetoothLeTerminal?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=kai-morich/SimpleBluetoothLeTerminal&amp;utm_campaign=Badge_Grade)

# SimpleBluetoothLeTerminalUpdated

This is a fork of the simple bluetooth terminal app by kai morich we have updated the application to use with the adafruit circuit playground bluefruit microcontroller for the purpose of data collection and further development.  

The app is designed to facilitate data collection for the two projects that is working with behavioral authentication. It designed such a way that due to the similarities in our project the data collection would take much more time if we are going to do it separately.

 1.Authentication using Motion Sensors.
 2.Authentication using IR proximity sense

The apps functionalities in general sense include
 1.Connecting to a BLE device and searching for various compatible ones.
 2.ability to send and receive UART packets
 3.It will be able to show the output in a general list form
Added functionalities
 1.A few depreciated libraries have been improved to give support till android 13.
 2.It will be able to manage and save them into the data folder of the app in a shared folder according to the users choices.
 3.it will be able to save and separate UART data packets and save them as individual csv formats.

The master branch only has the fixes to fit android 13 The other branches are used for active development, will be updated only after the testing.

The Circuit_Python code folder manages the microcontroller code 
 circuit python is a extended distribution of python intended for microcontrollers.
 Both team uses Adafruit's circuitplayground Bluefruit microcontroller board. 

