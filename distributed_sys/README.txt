-------------------------------------------
- 0. ADDITIONAL (unimportant) INFORMATION -
-------------------------------------------

- This code was written by Michaël Adriaensen and Luna Dierckx for the subject Distributed Systems at the University of Antwerp during the schoolyear of 2016-2017.
- We are not native english speakers, so excuse us for spelling and or grammatical mistakes made in any of the files
- All code was written in the Eclipse IDE for Java Developers (newest version at that time: Neon.1)

---------------------------
- 1. HOW TO USE THE CODE: -
---------------------------

Load the code into Eclipse as follows:
- File -> Open Projects from File System... -> Archive...
- Select the archive from our submission
- Click Finish
The code should now be loaded into eclipse. 

Next, you'll have to set up the command line arguments:
- Run -> Run Configurations...
- Make a new Java Application for every .java file (Fridge.java, Light.java, TemperatureSensor.java, User.java and Controller.java)
- Give the applications an easy name (for example, use "Fridge" for Fridge.java)
- in the Main class, search for the correct file (for example, the Fridge.java file is called "Fridge - avro.client")
- Switch to the Arguments tab
- Within "Program arguments", set up the arguments as given in the table below:

* Controller:        "<local ip>"
* Fridge:            "<controller ip>" "<local ip>"
* Light:             "<controller ip>" "<local ip>"
* TemperatureSensor: "<controller ip>" "<local ip>"
* User:              "<controller ip>" "<local ip>"

- In this table, <local ip> is the ip used to set up the connection on the local computer , and <controller ip> is the ip used to set up the connection on the computer where you'll run the (original) controller. (For example: computer 1 has ip 10.0.0.5 and computer 2 has ip 10.0.0.6. If you run the controller on computer 1, then <controller ip> is 10.0.0.5 on BOTH computers)

- Feel free to start playing around with all functionality. Make sure to FIRST run a controller. Once that's done, you can run as many fridges, lights, temperaturesensors and users as you like.
- To make it so the console doesn't always jump to the temperature sensors when they send a new measurement, just deactivate "Show console when standard out changes" and "Show console when standard error changes"
- To get a list of available commands for the users, type "?list" in the user console. Below is a table which a short explanation of what the commands do, as well as what the arguments for the commands are:

* pc:  prints a list of all currently connected clients (no arguments)
* pl:  prints a list of all lights and their status (no arguments)
* pfi: prints all the items currently in the fridge (1 argument: integer: id of the fridge you want to print the items from)
* pct: prints the current temperature (no arguments)
* pth: prints the temperature history (no arguments)
* of:  opens a fridge (1 argument: integer: the id of the fridge you want to open)
* aif: add an item to the fridge (1 argument: string: item to be added)
* rif: removes an item from the fridge (arguments: string: item to be removed) - only works when connected to a fridge)
* cf:  closes the fridge (no arguments)
* chs: changes the home status from the current user (no arguments)
* cl:  changes the status of a light (1 argument: integer: id of the light you want to change)

- To simulate a crash from anything, just hit the terminate button

---------------------------------------
- 2. DESCRIPTION OF THE ARCHITECTURE: -
---------------------------------------

- Fridge.java: implements all functionality of the fridges
- Light.java: implements all functionality of the lights
- TemperatureSensor.java: implements all functionality of the temperature sensors
- User.java: implements all functionality of the users
- Controller.java: implements all functionality of the controller

---------------------------------------
- 3. USED TECHNOLOGIES AND LIBRARIES: -
---------------------------------------

- Cliche, to use as interface for the users
- Avro, cause we had to