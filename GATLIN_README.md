# Performance testing:

## Notice
- Project is overriding embedded versions of Tomcat, as those used by spring are buggy and will crash the app
https://github.com/spring-projects/spring-boot/issues/8260
- Vaadin spring implementation has to be compiled from the github, or wait until 2.0.2
https://github.com/vaadin/spring/issues/203

## Record test
- In application.properties file, set vaadin.gatling.mode true (needed to allow recording to work as wanted)
- Compile project
- Start SpringBoot application
- Download gatlin: http://gatling.io/download/
- Extract it to suitable folder
- Under gatlin's folder, run bin/recorder.sh (or .bat)
- Select Save preferences (to avoid filling all these values again later)
- Select Strategy: Blacklist First
- Add these to blacklist
```
.*\.woff
.*\.(t|o)tf
.*\.png
.*\.js?(.*)
.*\.css?(.*)
.*\.txt
```
- Unselect "Infer html resource"
- Check the listening port, and add it as proxy port to your browser
- Close all other browsers
- Start recording (Start! button)
- Go to http://localhost:8080 with your browser
- Click your test case
- Stop recording
- Open recording saved as scala file with editor. File is found under gatlin installation folder user-files/simulations/RecordedSimulation.scala
- Check that all calls look valid (no need to add clientId magic)
- Modify last row to be something like
```scala
setUp(scn.inject(rampUsers(4000) over (5 minute))).protocols(httpProtocol)
```
Where you can adjust the amount of user or time test should take. More user and/or less time will make test heavier.


## Run test
- You can modify .scala recording file also at this point, calling gatlin.sh will recompile scala code
- Start SpringBoot application from command line (IDE running slows it), and give as much memory as you have (at your project folder)
```
java -Xmx12G -jar ui/target/patient-portal-vaadin-ui-0.0.1-SNAPSHOT.jar
```
- Open VisualVM, and connect to SpringBoot application process
- Open another console at gatlin's bin folder
- Run in gatlin console: ./gatlin.sh (or .bat), this should compile scale code, and you should see your recording as option 0, select it, and hit enter twice (or fill optional fields)
- Follow the progress at VisualVM
- If test did not fail, recording can be found at gatlin folder results (see the output of the gatlin.sh at the end)
