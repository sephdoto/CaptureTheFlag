# CFP14
***

## Name
Capture the Flag Project Team 14

## Description
We are to build a Game Client and Game Engine which implements a made up game called Capture the Flag. The rules are set in the [parent project](https://swt-praktikum.informatik.uni-mannheim.de/kessel/cfp-service) from our professor. 

## Import and run the project
To import and run the project, the [server](https://github.com/sephdoto/CaptureTheFlagServer) project is needed, import both projects into your IDE and it should work.
Running CtfApplication.java from the CTFServer starts the server;
Running EntryPoint.java from ctf-ui starts the UI.
EntryPoint also tries starting a server, if no instance is running on port 8888, but it is recommended to start a seperate server instance to see the UI console outputs (at least for testing).

## Jar execution
! Jar files have to be uploaded, as the code is constantly changing no final version could be created yet !
The jars are built to be opened without JavaFX installed, having it installed can lead to issues (fixable with command line arguments)

## Authors 
Participants:
- Raffay Syed
- Simon Stumpf
- Manuel Krakowski
- Aaron Niemesch
- Yannick Siebenhaar

## Project status
* Server is ready
* UI is mostly bug free, the game is playable and everything works as intended.
* The UI is constantly being debugged, some bugs are hard to find or tedious to fix, but most bugs are already known and almost fixed.

## Roadmap
Most aspects of the game are completely finished, yet there are a few pending tasks:
* debugging (fixing old and new bugs)
* improve code (making it more readable, easier to understand, more efficient and dynamic)

## License
It's more or less open source
