

# Catch The Pokemon:<img hight="300" width="700" alt="GIF" align="center" src="https://github.com/6arek212/CatchThePokemon-OOP-Assignment-4-JAVA/blob/wissam-v1/src/main/java/GameGui/tools/ball.gif">

![Builds](https://github.com/project-chip/connectedhomeip/workflows/Builds/badge.svg)

![head](https://github.com/6arek212/CatchThePokemon-OOP-Assignment-4-JAVA/blob/wissam-v1/src/main/java/GameGui/tools/intro.gif)


#### A part of a university assignment

</br>

## Project Overview

Catch the pokemon game , The objective is to catch as many pokemons as possible ,
without exceeding the number of moves allowed 




</br>



![](imgs/game_running.gif)




## How To Run

`1 - From the terminal run the server: `
    
    java -jar Ex4_Server_v0.0.jar <Case Number>

`2 - From the terminal run the game: `

    java -jar Ex4.jar <ID>

**The jar must be placed in the java folder ! & make sure you have java 11 or greater**


</br>



## What has been done ?
- ### Game
    
    - Move agents 
    - Choose agents next destination
    - Display the game
    - Load json data from the server
    - Time to get to a destination


- ### Graph

    - Add node
    - Delete node
    - Add edge
    - Delete edge
    - iterate through all the nodes
    - iterate through all the edges


- ### Graph Algorithms

    - Shortest path between two nodes
    - The center node
    - Travelling salesman problem
    - Strongly connected



</br>

## Algorithms Implementation

**Game Algorithm Idea**

- Calculate the distance between each pokemon and agent and put them on priority queue
- Take the best couple and attach them together, by attaching the pokemon to the agent no other agent can take it
- Find the shortest path from the agent to his assigned pokemon using Dijkstra algorithm and let the agent follow the path
- Calculate the estimated time for each agent to reach his pokemon and take the minimum time, doing that by using motion time equation



**Graph Algorithms**
- `Dijkstra Algorithm` for the shortest path between two nodes O(|V|*|V|)
- `Strongly connected components` to check weither the graph is connected there must be only **One** strongly connected
  component O(|V|+|E|)
- `center` algorithm using the shortest path O(|V| * |V| * |V|)
- `TSP` back-tracking algorithm N*(E+V*Log(V))

</br>


   




## Authors

* **Tarik Husin**  - linkedin -> https://www.linkedin.com/in/tarik-husin-706754184/
* **Wissam Kabha**  - github -> https://github.com/Wissam111

</br>

## References

https://en.wikipedia.org/wiki/Graph_center

https://en.wikipedia.org/wiki/Travelling_salesman_problem

https://www.youtube.com/watch?v=XB4MIexjvY0&t=484s

https://www.youtube.com/watch?v=XaXsJJh-Q5Y&t=600s

https://www.khanacademy.org/science/physics/one-dimensional-motion/displacement-velocity-time/v/solving-for-time
