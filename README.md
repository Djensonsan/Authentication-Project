# Authentication-Project
This project was part of the course "Data Security" taught at DTU (Technical University of Denmark), Copenhagen.
The goal was to: "give students an introduction to fundamental concepts in computer security and introduce central theories and techniques for the development and analysis of secure IT systems".

## Lab Work

The first task is to write a simple client/server application using RMI. The example used in this lab is a mock-up of a simple authenticated print server, such as a print server installed in a small company. 
The print server must support the following operations:
print(String filename, String printer);   // prints file filename on the specified printer
queue(String printer);   // lists the print queue for a given printer on the user's display in lines of the form <job number>   <file name>
topQueue(String printer, int job);   // moves job to the top of the queue
start();   // starts the print server
stop();   // stops the print server
restart();   // stops the print server, clears the print queue and starts the print server again
status(String printer);  // prints status of printer on the user's display
readConfig(String parameter);   // prints the value of the parameter on the user's display
setConfig(String parameter, String value);   // sets the parameter to value
These operations define the interface of the print server, but it is unnecessary to implement any printing capabilities for this lab, i.e. it is sufficient that the print server records the invocation of a particular operation in a logfile or prints it on the console. It must be possible to invoke all the print server operations defined in the interface from the client program.
This lab will design and implement a password based authentication mechanism for the print server, i.e. the print server must authenticate all requests from the client. For the purpose of this lab, it is not necessary to consider enrolment of users, i.e. authentication data structures can be populated by hand. The design and implementation of the print server must, however, consider the problems of password storage, password transport and password verification.
  
## Password Storage
In relation to password storage, three possible solution must be considered: passwords stored in a "system" file, passwords stored in a "public" file, where cryptography is used to ensure confidentiality and integrity of the stored data; and passwords stored in a data base management system; these options are outlined below.
System File Storing passwords in a system file relies on the operating system/file system protection mechanisms are used to ensure confidentiality and integrity of the stored data. This password file is normally not accessible to all users, so some mechanism (e.g. the SetUID mechanism in Unix) or system service is required to provide controlled access to the data stored in the file (similar to the DBMS storage described below.)
Public File Storing passwords in a public file that can be read (but not necessarily written) by all users is the traditional way to store passwords on Unix systems. Confidentiality of the passwords is normally protected by cryptographic means, whereas integrity (e.g. binding users and passwords) may either be protected by the operating system/file system protection mechanism, i.e. normal users have no write access to the file (how are passwords then updated?) or by cryptographic means.

DBMS Storing passwords in a database (often unencrypted) relies on the security architecture, e.g. the access control mechanism, implemented in the DBMS to provide confidentiality and integrity of the stored passwords.
The analysis must briefly discuss how each of these three solutions may be implemented in the given context and compare and contrast the security offered by each of the proposed implementations (these considerations must be documented in the lab report outlined below). Other possible solutions may also be included in the analysis if they are considered relevant. Based on the discussions above, a solution should be selected and the reasons behind documented.

## Password Transport
An analysis of the password transport problem must include a discussion of how to implement both individual request authentication and authenticated sessions, where the initial authentication is used to define an authenticated session (e.g. a channel in BAN-logic), which implicitly authenticates messages exchanged. If authentication of invocation requires transfer of any additional parameters between client and server, these should simply be added to the interface defined above.

The password verification mechanism depends on the choice of password storage and must be explained in the report.
For the purpose of this lab, it is acceptable to assume that secure communication between client and server is ensured by other means. It must, however, be explicitly stated if this assumption is made and the specific guarantees required by the channel (e.g. confidentiality, integrity and/or availability) must be specified. It is also possible to protect communications by cryptographic means, in this case the relevant techniques and technologies must be identified and discussed and it must be implemented correctly in the application. A complete and correct implementation of secure communication will count positively in the assessment of the report, but only if everything else is well implemented and documented.
