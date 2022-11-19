# Goal:
A gobang game(five in a row) let player to against with AI. It have two AI algorithms, easy mode is use modified version of Monte Carlo algorithm which I learned in CMPT 310, and the normal mode is use Alpha-Beta search.

# Languages:
It used Java, Python, and Go
Java: Provide all modules of gobang game.
Python: Call the modules from Java to build the game logic and receive input from Go, and return the input status.
Go: read user input and send to Python, able to know the input is legal of what kind of error based on the information they are passing.

# Method for connection:
py4j: This allow python program to use the current running java program's function
zmq: This allow to send message between Go and Python

# How to run it (Have to be step by step): 
1. Open terminal 1 and located on my project folder and **vagrant up** then **vagrant ssh**
2. On terminal 1 type **sudo apt-get install libzmq3-dev** (may need to type **y** to continue the install)
3. On terminal 1 type **go get github.com/pebbe/zmq4**
4. On terminal 1 type **pip install py4j**
5. Open terminal 2 and located on my project folder and **vagrant up** then **vagrant ssh**
6. Open terminal 3 and located on my project folder and **vagrant up** then **vagrant ssh**
7. On all three terminals type **cd project/project**
8. On terminal 1 type **java -jar 383project.jar**
9. On terminal 2 type **python3 MainGame.py**
10. On terminal 3 type **go run readInput.go**
Now the Game is able to Play
11. On terminal 3 type easy or normal to pick AI difficult (case insensitive)
12. On terminal 3 type black or white to pick who play first (case insensitive)
13. On terminal 3 type the number between 0 to 14 to choose row number
14. On terminal 3 type the number between 0 to 14 to choose column number
//It will repeatly ask you above two questions untill game end.
//When game end, terminal 2 will tells who wins or a tie game.
//On terminal 2 it will print the current board, tells you which move you made
//  which move AI made, when the game finish the Python and Go program will all exit.
//Terminal 3 will return the input status about input success or some kind of error

# Features
* The Alpha-Beta Search
* Monte Carlo algorithm

# Problems I met:
* The score function I wrote is not very good, sometime the AI will make some decision which I think is not the best. Score funciton have room to improve.
* I directly used a .jar file because there is some issues with my xml file, which does not allow me to make the runnable file by using mvn compile & mvn package. I used the .jar which export from IDEA, the source code of .jar file is same as the Main.java file
* When I set use the zmq for Go part, there will always cannot install successfuly. So I find a way to use command line to install inside the VM. (Step 2 & 3)
