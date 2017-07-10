# Quadris
Self-Playing Tetris engine with Genetic Algorithm (also human-playable)

The learning algorithm is a genetically inspired adaptation of the traditional approach, insead of performing corssing over in one spot of a bianry vector, this implementation tests if each index should cross over independently as we store more data at each index of the solution vector. It also supports minimized inbreading by requiring a certain degree of difference before allowing species to breed, thereby minimizing premature convergence.

Video Demonstration of self-playing mode: https://youtu.be/HdH3w9qPyoA

## Requirements
JavaFX, for the GUI (Java 8 should fullfull this)

## Running
Download the repo

The Game Clients are in `Quadris/Quadris/src/clients`, pick one and run it

  UserGameClient is for a human controlled game
  
  AutoplayClient lets you watch the computer play
