j2seObexDemo is a J2SE program that uses the J2SE OBEX API.

The base of this program functioning described in its
j2me analog, ObexDemo README file.
(see <<WTK_HOME>>/JSR082/src/examples/ObexDemo/lib/README.txt)

j2seObexDemo have the following structure:

./src/  - Sources of J2SE OBEX Demo
./src/ObexDemoMain.java     - provide main method to run a program
                              and main window to choose what should be run
                              sender or receiver. 

./bin/  - Scripts to build the program and run demo.
./bin/build.sh              - script which provide build the program under
                              solaris and linux,
./bin/build.bat             - build script for windows,
./bin/run.sh                - script for running demo under solaris and linux,
./bin/run.bat               - run script for windows.            

./res/  - Resources required for program functioning.
./res/imagenames.properties - property file containing list of image names.

Additionally the program uses resources of its j2me analog:
../res/images               - images for demo.

You can use the j2seObexDemo class as an 
example for how to integrate your own J2SE programs into the
Wireless Toolkit's OBEX environment.
