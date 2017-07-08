FitReader
======================
FitReader is a library to read ANT/Garmin .FIT files. .FIT files are converted to a FitRecordRepository, consisting of FitRecords for each .FIT defined FIT Message.
The main function is FitReader/readFile(), which does the reading and conversion to the FitRecordRepository. The FitRecordRepository can be used to obtain the message contents. 

Messages (message and field names) are defined in /src/main/resources/fit_messagefields.csv (this file originates from the .FIT SDK (20.30) that can be downloaded at the ANT site (https://www.thisisant.com/resources/fit); this SDK also contains PDFs describing the .FIT format). However, implements like Garmin appear to have added their own proprietary fields which are not defined in the csv.

Building
========
Use Maven to compile the source files into /target. The project is recognized by Netbeans as Maven project and can be imported.

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