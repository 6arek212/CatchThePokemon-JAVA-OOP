# Catch The Pokemon :D

![Builds](https://github.com/project-chip/connectedhomeip/workflows/Builds/badge.svg)

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

    java -jar .\Ex4.jar

**The jar must be placed in the java folder ! & make sure you have java 11 or greater**

![](images/info.png)

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

**Game Algorithm**

- Every agent will be assigned to the closest pokemon to him (a pokemon will no be assigned for more than one agent) 



**Graph Algorithms**
- `Dijkstra Algorithm` for the shortest path between two nodes O(|V|*|V|)
- `Strongly connected components` to check weither the graph is connected there must be only **One** strongly connected
  component O(|V|+|E|)
- `center` algorithm using the shortest path O(|V| * |V| * |V|)
- `TSP` back-tracking algorithm N*(E+V*Log(V))

</br>


   




## Authors

* **Tarik Husin**  - linkedin -> https://www.linkedin.com/in/tarik-husin-706754184/
* **Wisam Kabha**  - github -> https://github.com/Wissam111

</br>

## References

https://en.wikipedia.org/wiki/Graph_center

https://en.wikipedia.org/wiki/Travelling_salesman_problem

https://www.youtube.com/watch?v=XB4MIexjvY0&t=484s

https://www.youtube.com/watch?v=XaXsJJh-Q5Y&t=600s
