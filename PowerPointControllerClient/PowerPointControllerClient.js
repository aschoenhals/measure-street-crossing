var express = require('express');
var app = express();


app.use('/', express.static('.'));


app.get('/pagedown', function (req, res) {
	var robot = require("robotjs");
	
	var date = new Date();
	var timestamp = date.toISOString();

	console.log(timestamp + " pagedown pressed");
	robot.keyTap("pagedown");
	res.send(timestamp + " pageDown pressed");
});

app.get('/pageup', function (req, res) {
	var robot = require("robotjs");
	var date = new Date();
	var timestamp = date.toISOString();
	
	console.log(timestamp + " pageup pressed");
	robot.keyTap("pageup");
	res.send(Date.now() + " pageUp pressed");
});


app.listen(8080, function () {
  console.log("PowerPointController started http://localhost:8080");
});
