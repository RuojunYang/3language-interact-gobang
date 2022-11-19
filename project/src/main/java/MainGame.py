from py4j.java_gateway import JavaGateway
import json
import zmq
import sys

def printBoard(board):
    s = ""
    for i in range(0, 15):
        if(i == 0):
            s += "  |"
            for j in range(0, 15):
                s += str(j)
                s += "|"
            s += "\n"
            for j in range(0, 33):
                s += "-"
            s += "\n"
        for j in range(0, 15):
            if(j == 0):
                if i < 10:
                    s += " " + str(i) + "|"
                else:
                    s += str(i) + "|"
            if board[15 * i + j] == "e":
                s += " "
            elif board[15 * i + j] == "b":
                s += "x"
            else:
                s += "o"
            s += "|"
        s += "\n"
        for j in range(0, 33):
            s += "-"
        s += "\n"
    print(s)
    
context = zmq.Context()
socket = context.socket(zmq.REP)
socket.bind("tcp://*:5555")

gateway = JavaGateway()
gobang = gateway.entry_point
prevBoard = gobang.initial()

gameEnd = False;
player = 0;
AI = 1;
mode = "easy"

while not gameEnd:
    request = json.loads(socket.recv().decode('utf-8'))
    if request == {'string': 'ready'}:
        response = {'Game Start': ''}
        gobang.initial()
    elif request == {'difficult':'easy'}:
        response = {'difficult set': ''}
        mode = "easy"
    elif request == {'difficult':'normal'}:
        response = {'difficult set': ''}
        mode = "normal"
    elif request == {'order':'black'}:
        response = {'game continue': ''}
        player = 0
        AI = 1
    elif request == {'order':'white'}:
        response = {'game continue': ''}
        player = 1
        AI = 0
        if mode == "normal" :
            AIMove = gobang.ABAIMove(AI)
        else:
            AIMove = gobang.MCSTAIMove(AI)
        row = int(AIMove.split(",")[0])
        col = int(AIMove.split(",")[1])
        print("AI choose row: ", row)
        print("AI choose column: ", col)
        prevBoard = gobang.makeMove(int(row), int(col), AI)
        result = gobang.check(int(row), int(col));
        printBoard(gobang.getBoard())
        if result == True:
            print("\nAI Win\n");
            response = {"game end" : ""}
            socket.send(json.dumps(response).encode('utf-8'))
            break
        if gobang.checkTie() == True:
            print("\nTie Game\n")
            response = {"game end" : ""}
            socket.send(json.dumps(response).encode('utf-8'))
            break
    elif 'location' in request.keys():
        location = request.get('location').split(",");
        if len(location) != 2:
            response = {"wrong format" : ""}

        else:
            row = location[0].strip()
            col = location[1].strip()
            if row.isnumeric() & col.isnumeric():
                if int(row) < 0 or int (row) > 14 or int(col) < 0 or int (col) > 14:
                    response = {"location out of range" : ""}
                else:
                    curBoard = gobang.makeMove(int(row), int(col), player)
                    if prevBoard == curBoard:
                        response = {"repeat" : ""}
                    else:
                        print("Player choose row: ", row)
                        print("Player choose column: ", col)
                        prevBoard = curBoard
                        result = gobang.check(int(row), int(col));
                        printBoard(gobang.getBoard())
                        if result == True:
                            print("\nPlayer Win\n");
                            response = {"game end" : ""}
                            socket.send(json.dumps(response).encode('utf-8'))
                            break
                        if gobang.checkTie() == True:
                            print("\nTie Game\n")
                            response = {"game end" : ""}
                            socket.send(json.dumps(response).encode('utf-8'))
                            break
                        else:
                            if mode == "normal" :
                                AIMove = gobang.ABAIMove(AI)
                            else:
                                AIMove = gobang.MCSTAIMove(AI)
                            row = int(AIMove.split(",")[0])
                            col = int(AIMove.split(",")[1])
                            print("AI choose row: ", row)
                            print("AI choose column: ", col)
                            prevBoard = gobang.makeMove(int(row), int(col), AI)
                            result = gobang.check(int(row), int(col));
                            printBoard(gobang.getBoard())
                            if result == True:
                                print("\nAI Win\n");
                                response = {"game end" : ""}
                                socket.send(json.dumps(response).encode('utf-8'))
                                sys.exit(0)
                            if gobang.checkTie() == True:
                                print("\nTie Game\n")
                                response = {"game end" : ""}
                                socket.send(json.dumps(response).encode('utf-8'))
                                sys.exit(0)
                            else:
                                response = {"game continue" : ""}
            else:
               response = {"not Integer" : ""}
    socket.send(json.dumps(response).encode('utf-8'))
