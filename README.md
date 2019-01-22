# Virtual Smart Home project

## Introduction

This project is part of a main project with the goal of create a Virtual Smart home to apply an adaptative control.
It is based on the Smart Home automation, the practice of using Internet-enabled devices to remotely and automatically control home appliances, lighting, heating systems, and safety measures in and around a home.  

This project includes the creation of a virtual home, simulating the behaviour of its virtual devices.
It provides also a management system, composed by a managed and a managing system.
Smart home management systems have a common architectural style where a residential gateway (or hub) works as a mediator between a web or cloud application of a utility company and the smart home devices/appliances. 
Furthermore, the gateway can send commands to the smart devices acting a local scheduler or regulator to manage the control operations of the devices. 
There is the possibility for the user (human-in-the-loop model) to control the system remotely through a simple UI application for smart-phones and tablets.

In this project we have implemented both managed and managing systems using OpenHAB, an open source software platform (written in pure Java with an OSGi architecture for modularization) for home Automation (https://www.openhab.org/).
Also the simulation of the behavoiur of our virtual smarthome has been realized with the OpenHAB system. 

The managed system implements a basic control, while the managing system allows to reach 3 main goals:
- maximize the heating comfort;
- maximize the air quality indoor;
- realize a complete fire detector control.

The control system realized is composed by two layers: the managed layer, for basic control functions, and the managing layer, for advanced control function.

![alt text](https://github.com/bebo1995/VirtualSmartHome-Project/blob/master/images/management_system.JPG?raw=true)

The last part of this project is about the creation of an OpenhabBroker, a Java component that allows the communication between the OpenHab system and a third part application. This component implements a polling service and offer some get methods, with whom the third part application can access to the data collected by polling, a setStatus method and a sendCommand method to modify the status of an openhab item. 
This component communicates with Openhab through RestAPI, while a third part application can use this service by simple method invocation.

### Deployment Diagram
The deployment diagram of our application is:

![alt text](https://github.com/bebo1995/VirtualSmartHome-Project/blob/master/images/IT1_Deployment%20Diagram.png?raw=true)

### Project Collaborators
Arrigoni Alberto, Bosc Daniele, Pedercini Rita
