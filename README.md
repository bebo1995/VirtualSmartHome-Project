# Virtual Smart Home project

## Introduction

Smart Home Automation is the practice of using Internet-enabled devices to remotely and automatically control home appliances, lighting, heating systems, and safety measures in and around a home. Smart home management systems have a common architectural style where a residential gateway (or hub) works as a mediator between a web or cloud application of a utility company and the smart home devices/appliances. Furthermore, the gateway can send commands to the smart devices acting a local scheduler or regulator to manage the control operations of the devices. There is the possibility for the user (human-in-the-loop model) to control the system remotely through a simple UI application for smart-phones and tablets.

The goal of this project is to create a Virtual Smart Home managed by a self-adaptive software controller (the smart home gateway) consisting of two software layers: the managed layer, for basic control operations, and a managing layer realizing self-adaptation mechanisms for more advanced automation processes. In this project we have implemented both the managed and the managing subsystems using OpenHAB, an open source software platform (written in pure Java with an OSGi architecture for modularization) for Home Automation (https://www.openhab.org/).

Assuming the device abstraction provided by OpenHAB and its rule engine, the managed subsystem implements a basic control, while the managing subsystem allows to:
- maximize the heating comfort;
- maximize the air quality indoor;
- guarantee fire detection.

 Also the simulation of the behavior of our virtual smarthome environemnt has been realized with the OpenHAB rules system. 

![alt text](https://github.com/bebo1995/VirtualSmartHome-Project/blob/master/images/management_system.JPG?raw=true)

A secondary goal of this project is about the creation of a broker component in Java, OpenhabBroker, that allows the communication between the OpenHab system and a third part Java-like application by method invocation. This component implements a polling service and offers some get methods, with whom the third part application can access to the data collected by polling, a setStatus method and a sendCommand method to modify the status of an openhab item. This component communicates with Openhab through RestAPI, while a third part application can use this service by simple method invocation to interact indirectly with the OpenHAB event bus.

### Deployment Diagram
The deployment diagram of our application is:

![alt text](https://github.com/bebo1995/VirtualSmartHome-Project/blob/master/images/new_IT1_Deployment%20Diagram.png?raw=true)

### Project Collaborators
UniBG students: Arrigoni Alberto, Bosc Daniele, Pedercini Rita
