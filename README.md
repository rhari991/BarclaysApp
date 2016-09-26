This is an application built during the Rise Hackathon 2016 in Mumbai during September 24 and 25 2016. It 
allows the user to check his account balance, make a transaction (using the Barclays API stubs) and send a 
tweet using voice commands.

#Build Instructions

1) Signup for a [Moxtra account](https://developer.moxtra.com/) and add them to the app\src\main\res\values\keys.xml file.
2) Create a [Firebase project](https://firebase.google.com/) and add the obtained google-services.json file to the app directory.
3) Host the provided server files on a server and add the server address and port number to the app\src\main\res\values\keys.xml file.
4) Install the Lombok plugin for Android Studio :

* Close the Project *(required to enable annotation processing)*
* Install Lombok Plugin (`Configure > Plugins > Browse Repositories > Search 'Lombok Plugin' > Install`)
* Restart Android Studio
* Enable Annotation Processing (`Configure > Settings > Build, Execution, Deployment > Compiler > Annotation Processors`)