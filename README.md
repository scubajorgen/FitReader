# FitReader
## FIT
FitReader is a library to read ANT/Garmin _.FIT_ files. FIT stands for _Flexible and Interoperable Data Transfer_ and is a Garmin protocol widely used for storing and transfering sports and activity related data. FitReader can be used to read and decode the FIT content for further processing.

The [FIT protocol](https://developer.garmin.com/fit/overview/) is based on Messages, each consisting of Fields of a certain data type. Possible Messages, Fields and Types are defined in the _Global FIT Profile_ which is part of the [Garmin FIT SDK](https://developer.garmin.com/fit/download/), in the form of an excel sheet Profile.xlsx.

A device, for example my Edge830 cycle computer, support a certain subset of the Global FIT Profile. This subset is called the Device Profile.

---
Note: Apparently with updates of the Global FIT Profile, messages are added. However, sometimes Message definitions are _removed_. An example is the Waypoints message (ID 29). However, this message is used by devices like the Edge 810 and Edge 830. Being not present in the Global FIT Profile means this message is no longer recognized in the FIT data.

---


## Working of the software
.FIT files are converted to a **FitRecordRepository**, consisting of **FitRecord**s for each .FIT defined FIT Message.
The main function is **FitReader:readFile()**, which does the reading and conversion to the **FitRecordRepository**. The **FitRecordRepository** can be used to obtain the message content definition and data. 

Messages (message and field names) are defined in /src/main/resources/Profile.xlsx (this file originates from the .FIT SDK that can be downloaded at the [ANT site](https://www.thisisant.com/resources/fit); this SDK also contains PDFs describing the .FIT format). However, implementers like Garmin appear to have added their own proprietary fields which are not defined in the Excel sheet.

# Building
Use Maven to compile the source files into /target. The project is recognized by Netbeans as Maven project and can be imported.

Usage is illustrated in the test files.


# Dependencies
The software uses 
- hirondelle-date4j-1.5.1.jar


# Information
Javadoc: Refer to the Javadoc in the /target file.
Blog:    http://blog.studioblueplanet.net/?page_id=468


Garmin Track Converter
The Garmin track converter uses this library to convert .FIT tracks to .GPX format

Source: https://github.com/scubajorgen/GarminTrackConverter