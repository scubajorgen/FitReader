FitReader
======================
FitReader is a library to read ANT/Garmin .FIT files. .FIT files are converted to a FitRecordRepository, consisting of FitRecords 
for each .FIT defined FIT Message


Building
========
Use Maven to compile the source files into /target. The project is recognized by Netbeans as Maven project and 
can be imported.

Usage is illustrated in the test files.


Dependencies
============
The software uses 
- hirondelle-date4j-1.5.1.jar


Information
===========
Blog:   http://blog.studioblueplanet.net/?page_id=468


Garmin Track COnverter
======================
The Garmin track converter uses this library to convert .FIT tracks to .GPX format

Source: https://github.com/scubajorgen/GarminTrackConverter