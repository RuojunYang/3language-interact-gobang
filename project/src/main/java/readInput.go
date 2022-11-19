// go run zmq_request.go
package main

import (

    "strings"

	"encoding/json"
	"fmt"
	zmq "github.com/pebbe/zmq4"
)

func main() {
	requester, _ := zmq.NewSocket(zmq.REQ)
	defer requester.Close()
	requester.Connect("tcp://localhost:5555")

	arg := "ready"
	request, _ := json.Marshal(map[string]string{"string": arg})
	requester.SendBytes(request, 0)
	reply, _ := requester.RecvBytes(0)
	var response map[string]string
	json.Unmarshal(reply, &response)
	//fmt.Printf("%v\n", response)
    
    _, ok := response["Game Start"]
    if ok {
        fmt.Printf("Gobang Game Ready\n")
        fmt.Printf("Type Normal or Easy AI difficult\n")

        var text string
        fmt.Scan(&text)

        text = strings.ToLower(text)
        text = strings.Trim(text, "")
        for text != "normal" && text != "easy"{
            fmt.Printf("Incorrect word, Please Type again\nType Normal or Easy AI difficult\n")
            fmt.Scan(&text)
            text = strings.ToLower(text)
            text = strings.Trim(text, "")
        }
        arg = "easy"
        difficult, _ := json.Marshal(map[string]string{"difficult": arg})
        if text == "normal" {
            arg = "normal"
            difficult, _ = json.Marshal(map[string]string{"difficult": arg})
        }
        requester.SendBytes(difficult, 0)
        reply, _ = requester.RecvBytes(0)
        json.Unmarshal(reply, &response)
        _, ok1 := response["difficult set"]
        if ok1{
            fmt.Printf("Type Black(first) or White(second) for order\n")
            fmt.Scan(&text)
            text = strings.ToLower(text)
            text = strings.Trim(text, "")
            for text != "white" && text != "black"{
                fmt.Printf("Incorrect word, Please Type again\nType Normal or Easy AI difficult\n")
                fmt.Scan(&text)
                text = strings.ToLower(text)
                text = strings.Trim(text, "")
            }
            arg = "white"
            difficult, _ := json.Marshal(map[string]string{"order": arg})
            if text == "black" {
                arg = "black"
                difficult, _ = json.Marshal(map[string]string{"order": arg})
            }
            requester.SendBytes(difficult, 0)
            reply, _ = requester.RecvBytes(0)
            json.Unmarshal(reply, &response)
            _, ok2 := response["game continue"]
            for ok2{
                fmt.Printf("Type row want to pick in number of (0 - 14)\n")
                fmt.Scan(&text)
                text = strings.ToLower(text)
                text = strings.Trim(text, "")
                temp := text
                
                fmt.Printf("Type col want to pick in number of (0 - 14)\n")
                fmt.Scan(&text)
                text = strings.ToLower(text)
                text = strings.Trim(text, "")

                text = temp + "," + text
                location, _ := json.Marshal(map[string]string{"location": text})
                requester.SendBytes(location, 0)
                reply, _ = requester.RecvBytes(0)
                json.Unmarshal(reply, &response)
                
                _, formatErr := response["wrong format"]
                _, numberErr := response["not Integer"]
                _, repeatErr := response["repeat"]
                _, outOfRangeErr := response["location out of range"]
                _, end := response["game end"]
                if formatErr{
                    fmt.Printf("ERROR: format error\n")
                }else if numberErr{
                    fmt.Printf("ERROR: not Integer\n")
                }else if repeatErr{
                    fmt.Printf("ERROR: repeat location\n")
                }else if outOfRangeErr{
                    fmt.Printf("ERROR: location out of range\n")
                }else if end{
                    fmt.Printf("Game end\n")
                    return
                }else{
                    fmt.Printf("Move success\n")
                }

            }

        }
        
    }
}

