# MyShelfie but better

## Team
Jacopo Piazzalunga [@Jacopopiazza](https://github.com/Jacopopiazza)<br>
Mattia Piccinato [@peetceenatoo](https://github.com/peetceenatoo)<br>
Francesco Rita [@FraRita](https://github.com/FraRita)<br>
Simone Romanò [@Simone1602](https://github.com/Simone1602)<br>

## Progress

| Functionality | State |
|:-----------------------|:------------------------------------:|
| Basic rules | ![#c5f015](https://placehold.it/15/44bb44/44bb44) |
| Complete rules | ![#c5f015](https://placehold.it/15/44bb44/44bb44) |
| Socket | ![#c5f015](https://placehold.it/15/44bb44/44bb44) |
| RMI | ![#c5f015](https://placehold.it/15/44bb44/44bb44) |
| GUI | ![#c5f015](https://placehold.it/15/44bb44/44bb44) |
| CLI | ![#c5f015](https://placehold.it/15/44bb44/44bb44)|
| Multiple games | ![#c5f015](https://placehold.it/15/44bb44/44bb44) |
| Persistence | ![#c5f015](https://placehold.it/15/f03c15/f03c15) |
| Chat | ![#c5f015](https://placehold.it/15/f03c15/f03c15) |
| Disconnections | ![#c5f015](https://placehold.it/15/44bb44/44bb44) |

<!--
[![RED](https://placehold.it/15/f03c15/f03c15)](#)
[![YELLOW](https://placehold.it/15/ffdd00/ffdd00)](#)
[![GREEN](https://placehold.it/15/44bb44/44bb44)](#)
-->


## Setup

- In the [Deliverables](deliverables) folder there are two multi-platform jar files, one to set the Server up, and the other one to start the Client.
- The Server can be run with the following command, the RMI port is 1099, the socket port is 1234:
    ```shell
    > java -jar MyShelfie_Server.jar
    ```
  This command can be followed by these arguments:
    - **-hostname**: followed by the desired hostname for the RMI server as argument (usually the IP of the network interface);

- The Client can be run with the following command:
    ```shell
    > java -jar MyShelfie_Client.jar
    ```
    - This command sets the Client on Graphical User Interface (GUI) mode, but it can be followed by **-cli** if the Command Line Interface (CLI) is preferred.
    - The Server's IP to connect to can be specified during the execution.


## Utilized Software

* [Draw.io - Diagrams.net](https://app.diagrams.net/): UML and sequence diagrams.
* [IntelliJ](https://www.jetbrains.com/idea/): Main IDE for project development.
* [JavaFX](https://openjfx.io/): Windows application design.
* [Maven](https://maven.apache.org/): Package and dependency management.

## License

[**MyShelfie**](https://www.craniocreations.it/prodotto/my-shelfie) is property of [_Cranio Creations_] and all of the copyrighted graphical assets used in this project were supplied by [**Politecnico di Milano**] in collaboration with their rights' holders.

[_Cranio Creations_]: https://www.craniocreations.it/
[**Politecnico di Milano**]: https://www.polimi.it/
