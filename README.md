# [DCC Controller](https://github.com/constantin-p/dcc-controller)
###### A DCC controller application for macOS, Windows, and Linux.

##### Table of Contents
* [Introduction](#introduction)
* [Installation (DCC Controller)](#installation-dcc-controller)
* [Usage](#usage)
* [Configurations](#configurations)
* [Screenshots](#screenshots)
* [License](#license)

### Introduction

DCC Controller allows users to operate model railway systems (which support the [DCC](https://www.nmra.org/dcc-rps-standards) protocol) through a desktop GUI application.

The described functionality is achieved by using a 2 part system:
- Java desktop application (DCC Controller)
- Arduino control code [arduino/sketch_dcc_controller_v1](arduino/sketch_dcc_controller_v1).


### Installation (DCC Controller)

[Download 1.0 Release (DCC_Controller.jar)](https://github.com/constantin-p/dcc-controller/releases/tag/v1.0.0)

### Usage
1. Install the device control code for Arduino ([arduino/sketch_dcc_controller_v1](arduino/sketch_dcc_controller_v1))
2. Open DCC Controller
3. Create a new device (`File` → `New Device` or use the `Add Device button`)
4. Open the device detail window (`Open` button from the device list)
5. Connect the arduino board to your computer and select the corresponding port from DCC Controller (`Tools` → `Port`).

###### You should now be able to control the speed and direction of the device. For more functionality use [configurations](#configurations).

### Configurations

You can extend the functionality of the application by using configuration files.
A configuration file is a comma-separated values (CSV) file with 2 columns: `command_display_name` & `command`.

In order to import your configuration files, go to `File` → `Import` configuration or use the `Add configuration` button (device detail window).

###### For an example configuration file check [configuration/default_train.csv](configuration/default_train.csv).

| Column                      | Description                                                 |
|-----------------------------|-------------------------------------------------------------| 
| **`command_display_name`**  | The string that will describe the command in the GUI.       | 
| **`command`**               | The string that will be interpreted by the Arduino code. \* |

_**\* This will be the suffix of the command string. The command structure is the following:**_

DCC_CTRL:**`<address>`**:`<command>`

* **`<address>`**: (`number`) The device address (starts from 0, up to 127). This is set when you create the device.


For the _**REVERSE**_ & _**FORWARD**_ commands the following structure is used:

DCC_CTRL:`<address>`:`<command>`:**`<arg>`**

* **`<arg>`**: (`number`) The speed value (starts from 0, up to 15). This is set by the GUI slider.


###### Commands are ended by a newline character `\n`.
---

### Screenshots

1. Device List Overview
<p align="center">
  <img src="https://github.com/constantin-p/dcc-controller/blob/master/screenshots/device-list.png?raw=true" alt="Device List Overview"/>
</p>

2. Create Device Screen
<p align="center">
  <img src="https://github.com/constantin-p/dcc-controller/blob/master/screenshots/create-device.png?raw=true" alt="Create Device Screen"/>
</p>

3. Default Device Screen
<p align="center">
  <img src="https://github.com/constantin-p/dcc-controller/blob/master/screenshots/device-basic.png?raw=true" alt="Default Device Screen"/>
</p>

4. Configured Device
<p align="center">
  <img src="https://github.com/constantin-p/dcc-controller/blob/master/screenshots/device-config.png?raw=true" alt="Configured Device"/>
</p>


## License

This project is MIT licensed.
Please see the [LICENSE](LICENSE) file for more information.
