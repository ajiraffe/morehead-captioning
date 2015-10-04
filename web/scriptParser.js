var fs = require('fs');

//remove nulls
function cleanArray(actual) {
    var newArray = [];
    for (var i = 0; i < actual.length; i++) {
        if (actual[i]) {
            newArray.push(actual[i]);
        }
    }
    return newArray;
}

//read in a string, and try and find & fix the time
function getTime(timeStr) {
    //strip beginning spaces
    var time = timeStr.replace(/^\s*/, "").split(" ")[0];
    //only get the digits and
    time = time.replace(/[^0-9;,:]/g, "");
    //standardize the time seperator to :
    time = time.replace(/[;,]/, ":");

    //check if time is valid length
    if (!isNaN(parseFloat(time))) {
        var timeData = time.split(":");
        if (timeData.length > 2) {
            return time;
        }
    }

    //not a vaild time, return null
    return null;
}

//function to do the actual reading and parsing
function scriptJSON(file){
    fs.readFile(file, 'utf8', function(err, data) {
            if (err) {
                return console.log(err);
            }

            var transcript = [];

            var lines = data.split("\n");
            lines = cleanArray(lines);

            //loop thru all lines, pushing json objects containing times and utterances
            var utteranceObj = {};
            for (var i = 0; i < lines.length - 2; i++) {
                var time = getTime(lines[i]);
                if (time) {
                    //if we already have an uttObj, then push it and continue
                    if (Object.keys(utteranceObj).length !== 0) {
                        transcript.push(utteranceObj);
                        utteranceObj = {};
                    }

                    //make a new uttObj
                    utteranceObj.time = time;

                    //loop thru proceeding lines to get message
                    i++;
                    var utterance = lines[i];
                    //while the lines aren't times, append to the uttObj message
                    while (utterance && !getTime(utterance)) {
                        var append;
                        if (utteranceObj.utterance) {
                            utteranceObj.utterance = utteranceObj.utterance +
                                utterance + "\n";
                        } else {
                            utteranceObj.utterance = utterance + "\n";
                        }

                        i++;
                        utterance = lines[i];
                    }

                    if (i == lines.length) {
                        transcript.push(utteranceObj);
                    }

                    //finally, i will always point to a time, so go back one
                    i--;
                }
            }

            console.log(JSON.stringify(transcript[transcript.length -
                1]));
        }
    );

    //finally, do the parsing
    console.log(scriptJSON('./MPSCSSRev_13_ShootingScript_TIME_CODE_reformat.txt'));
}
