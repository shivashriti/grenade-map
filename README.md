## Grenade Map


### Problem Statement
Design a client-server application that fires "blind" grenades on a grid map to target a Person.
Assuming you have a grid of 9x9, sending a coordinate from client would look like this:


|Client|Server|
|---|---|
|Fires a grenade based on coordinates (can be automated and/or sending random coordinates)|Receives the coordinates and creates a blast area that propagates until the "end of the world".|
| | |
|Coordinates: [X, Y, Blast Radius] Person: [X, Y, -1]|Responds based on whether a Person killed or if a Person already exists on a coordinate.|

Server should print every step of the blast (for every blast radius is 1 ... N, server should show the progression of each step) and the location of the Person.

Sending [X,Y,-1] to a coordinate with an existing Person will not spawn a new Person and Server will respond to client with a "Unable to spawn Person in that location".

### Solution


### Design

**Actors:**
- MainActor: receives targets from client, interprets them and tells BlastActor to handle accordingly
- BlastActor: takes care of the important tasks of server like initiate blast, spawn person, generate next step, detect persons died, build server responses and prepares app state accordingly.
- StateActor: maintains the state of the application. It is responsible for providing current step of the application and updating it to a new state
- Client: generates blind targets

**Models:**
- Server: initializes all necessary actors and schedules them as necessary. It provides the current step's view to update the UI regularly.
- RandomizedTimer: Generic Custom Timer that schedules an event at regular intervals based on minimum and maximum duration limits.

**Utils:**
- BlastUtil: Contains utility methods for BlastActor's necessary small tasks.
- UIUtil: Contains necessary constants/methods to create UI of application.

**UI**
- AppView: Creates UI of the application. It is responsible to draw the grid of blocks, blast areas, persons, client messages and server responses and keep drawing the updated view at all steps.


### Assumptions and Configurations
- Client is automated to send blast targets at *random intervals*. Client also randomly decides to send person co-ordinates. Currently the probability of sending a person target is set as 0.25
- The grid map size is set as 60*60. This is set so for better view of the application. 
- The grenade radius is randomly decided by client and is limited by a pre-configured number 15.
- Person can jump to any block in the grid randomly. Restricting the jumps by specific radius is mentioned as TODO item.
- Server detects if a person dies at current step, and notifies so. This is visible in the UI as both blasts and persons are drawn with separate colors. The dead person is removed from the grid in next step. 

**Note**
- Server configurations (time intervals, grid/block size details, grenade radius limit, error messages etc) are mentioned [here](src/main/scala/com/shriti/grenademap/models/package.scala).
- UI configurations (colors, panel size, message areas) are mentioned [here](src/main/scala/com/shriti/grenademap/util/UIUtil.scala)

    *These can be easily changed and experimented with.*

### Testing
Unit test cases have been added to check that
- Server starts new blast when asked
- Server spawns new person when asked
- Server propagates blast within grid
- Server changes person's position on random jump  
- Server does not move a person until grenade (that was targeted at same time as this person) finishes propagation  
- Server detects when person dies in a blast and notify  
- Server removes the person that died in last step
- Server detects when unable to spawn person and notify
- Server detects when all persons are dead and notify END


### Build and Execute Instructions
**Execute App with jar**

- Go to the root of the application. Run the jar file "grenade-map.jar" either by clicking it and through following command.
`java –jar grenade-map.jar`

*This will execute application till the "End of the world"*

**Execute App with sbt**

- On the root of the project, run `sbt compile` to compile the app.

    `~/myWorkspace/grenade-map 👉 $sbt compile`

- On successful compilation, run `sbt run` to execute the app

    `~/myWorkspace/grenade-map 👉 $sbt run`


**Test**

- To run unit tests, run `sbt test`

    `~/myWorkspace/grenade-map 👉 $sbt test`


### Packaging Instructions
- To create a jar for the app, assembly plugin has been used. Simply run `sbt assembly` on the root of the project. It uses main class *AppView* to launch the application's view.

`~/myWorkspace/grenade-map 👉 $sbt assembly`

*This will create grenade-map.jar in /grenade-map/target/scala-2.12/*


### Further Improvements and TODOs
- Provide support for quit, pause and restart of application on key-press.
- Gracefully close the application after a pause at end of app or ask user's permission to close the app.
- Restrict a person's random jump to a limited area surrounding him.


### Screenshots
Here are few screenshots of running application taken at **random** steps:
- **Client sends target**

![image1](images/Client%20sends%20blast%20target.png)

- **Server responds**

![image2](images/Server%20response.png)

- **End of the world**

![image3](images/End%20of%20the%20world.png)