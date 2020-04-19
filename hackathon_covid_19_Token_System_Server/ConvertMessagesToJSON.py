# this class converts message to be sent to the server to JSON format

import json

def toJsonFormat(message):
    modifiedMessage = '{"message": "' + str(message) + '"}'

    # converting to JSON
    jsonMessage = json.loads(modifiedMessage)
    #jsonMessage = eval(modifiedMessage)
    return jsonMessage


print(toJsonFormat(False))
