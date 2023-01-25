# SmartChat

A very simple distributed messenger app for Android.

Authors; [Giannis Mparous](https://github.com/giannismparous "Giannis Mparous"), [Dimitris Milios](https://github.com/DimMil24 "Dimitris Milios")

## Introduction

The main objective of this assignment was to develop a communication system that supports multimedia files in Java. Text, photo or video content is published by one or more users and is delivered to a group of subscribers. Due to the large amount of users that we want to serve, we implemented a clever system that is capable of content delivery to the correct receivers. 

In order to distribute the content, we need to know: 
- **who is interested (_subscribers_)**
- **how can they receive it (_by subscribing to topics_)**

## Architecture

The system is divided in two parts:
- Multimedia Streaming Framework
- Front End Android App

## Event Delivery System

The Event Delivery System is a programming framework that allows sending and receiving data that fulfill specific criteria. The advantage of the Event Delivery System is the immediate forwarding of data in real time via two fundamental functions; `push` and `pull`. These two functions are independent of each other. 

During each `push` call, the intermediate system node (_broker_) should;
- **be able to handle data incoming from different _publishers_ concurrently** (in our case users) and
- **be able to deliver the results to the _subscribers_** (also called _consumers_ as they "consume" the data)

Concurrency is inevitable because the system is required to offer simultaneous data delivery from _publishers_ to _brokers_ and from _brokers_ to _subscribers_. All subscribed users must simultaneously receive the same content.

The aforementioned model is backed backed by the two core functions that can be described by the following figure;

<p align="center">
  <img src="https://github.com/giannismparous/SmartChatters/blob/main/framework_prototype.png" />
</p>

<p align="center">
  <i>The basic prototype of the system.</i> 
</p>

Main functions of the users.

### - `push`

The sole role of the `push` function is to forward to a _broker_ a value which is stored in a data structure (e.g. queue), so that the value can be delivered upon requested. This intermediate data structure plays the role of the topic's _chat history_. As a result, once a new user subscribes to the topic, they will be able to see the previous messages. In our case, `push` takes as input the information required for the immaculate delivery of the content (ex. _username, topic name/id, video data, etc._). 

A significant software requirement that needs to be addressed for better comprehension of the model's functionality is **multimedia chunking**; a photo or a video streamed to and from the framework is *never sent wholly*. On the contrary, multimedia content is cut down to smaller, equal in size, fragments (_chunks_) in order to achieve higher communication efficiency[^1].

### - `pull`

The role of `pull` is to deliver all the data of an intermediate node that concern the user (_subscriber_) that calls the function. Values from each topic are collected and delivered to the _subscriber_ that issued the request. 

## Setting up the brokers and the app

Setting up:
- In order to start the brokers (the servers that receive and promote packages to users), you need to open the DS folder as an Eclipse project. You will have to set up the desired IPs and ports for the brokers in the conf.txt file. Upon doing that, you have to start the brokers. There is one java main class for all 3 premade brokers.-

- In order for the app to connect to the correct server you have to setup the ips and ports of the desired brokers (servers) as well. To do that, open the whole repository as an Android Studio project. Then you will have to setup the confuser.txt file that's in the raw resources folder. You will have to put the ips and ports of the servers there.

Debugging without the app:
- There is a User1.java that creates instances of Usernodes (in the Eclipse project). But to make it connect to the right servers you will have to add the ips and ports of the servers in the confUser.txt file.

## Class explanations
- **MultimediaFile** -> It is a chunk of a file that needs to be send to another location.
- **Usernode** -> It is the basic user. Upon instantiation it connects randomly to one of the available servers.
- **Publisher** -> Basically a thread that will run when a user decides to publish a file. It will send all the file chunks (MultimediaFile objects) to the server so that the server can save them to it's history. Once the uploading process is completed the thread terminates.
- **Consumer** -> Basically a thread that is always running and receiving new file chunks. This is possible by keeping an internal index of the files that it has received from a topic and comparing with the chunk history size of the actual topic. If the Consumer has less chunks than the topic, it will start pulling new chunks from it. There is 1 consumer thread running for each topic the user is subscribed to.

