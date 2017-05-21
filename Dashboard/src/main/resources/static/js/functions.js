var client;

var subscriptionTweets = null;
var subscriptionAttendees = null;

var subscriptionEndpoint = '/queue/search/';
var attendeesSubscriptionEndpoint = '/queue/attendee/';


var currentHackathon;



$(document).ready(function(){
	loadPage();	
	saveHackathon();
	editHackathon();
	loadEditHackathon();
	stompConnection();
	//registerSearch();
	//hackathonChange();
});


function loadPage() {
	loadSelect();
	loadAttendees();
	hackathonChange();
}



function loadSelect() {
	$.getJSON("hackathons", function(hackathons){	
		if(hackathons.length > 0) {
			
			//var data = hackathons.map(function(a) {return a.tweet;});

			console.log(hackathons);
			
			var data = hackathons.map(function(a) {return a.tag;});

			var template = '{{#.}}'
			+'<option>{{.}}</option>'
			+'{{/.}}';

			var html = Mustache.to_html(template, data);

			$("#HackathonSelect").empty().append(html);
			
			$('#HackathonSelect').change();
		}			
	});
}

function saveHackathon() {
	$("#saveHackathon").click(function(ev){
		event.preventDefault();
		$.post("/addHackathon", { "name":$('input#name').val(),
								"venue":$('input#venue').val(),
								"web":$('input#web').val(),
								"tag":$('input#tag').val(),
								"att-name":document.getElementById("att-name").checked,
								"att-size":document.getElementById("att-size").checked,
								"att-age":document.getElementById("att-age").checked,
								"att-mail":document.getElementById("att-mail").checked},
								function(data, status){
									alert("Data: " + data + "\nStatus: " + status);
									loadSelect();									
	    });
	});
}

function editHackathon() {
	$("#editHackathon").click(function(ev){
		event.preventDefault();
		//$.put("/editHackathon", { "name":$('input#name').val(),
		//						"venue":$('input#venue').val(),
		//						"web":$('input#web').val(),
		//						"tag":$('input#tag').val()}, function(data, status){		
		//							alert("Data: " + data + "\nStatus: " + status);
		//							loadSelect();									
	    //});
		$.ajax({
		    url: '/editHackathon',
		    type: 'PUT',
		    data: { "name":$('input#editName').val(),
				"venue":$('input#editVenue').val(),
				"web":$('input#editWeb').val(),
				"tag":$('input#editTag').val(),
				"att-name":document.getElementById("att-name-edit").checked,
				"att-size":document.getElementById("att-size-edit").checked,
				"att-age":document.getElementById("att-age-edit").checked,
				"att-mail":document.getElementById("att-mail-edit").checked},
		    success: function(data, status){		
				alert("Data: " + data + "\nStatus: " + status);
				loadSelect();									
	    	}
		});
	});
}


function loadEditHackathon() {
	$("#editButton").click(function(ev){
		event.preventDefault();
		$.getJSON("editHackathon", {query: currentHackathon }, function(hackathon, status, jqXHR){
			$('#editName').val(hackathon.nombre);
			$('#editVenue').val(hackathon.lugar);
			$('#editWeb').val(hackathon.web);
			$('#editTag').val(hackathon.tag);
			document.getElementById("att-name-edit").checked=hackathon.properties.name;
			document.getElementById("att-size-edit").checked=hackathon.properties.size;
			document.getElementById("att-age-edit").checked=hackathon.properties.age;
			document.getElementById("att-mail-edit").checked=hackathon.properties.mail;
	    });
	});
}

function stompConnection() {
	client = Stomp.over(new SockJS("/tweetsSocket"));
	
	var headers = {};
	var connectCallback = {};
	client.connect(headers, connectCallback);
}

function subscribeTwitter(query) {
	if (subscriptionTweets != null) subscriptionTweets.unsubscribe();
	
	client.send('/app/search', {}, JSON.stringify({'query' : query}));
	
	subscriptionTweets = client.subscribe(subscriptionEndpoint + query, function(tweet){		
		//console.log(tweet);		
		//console.log(tweet.id);		
		var template =
			'<div class="row panel panel-default">'
			+	    '<div class="panel-heading">'
			+	        '<a href="https://twitter.com/{{fromUser}}"'
			+	           'target="_blank"><b>@{{fromUser}}</b></a>'
			+	        '<div class="pull-right">'
			+	            '<a href="https://twitter.com/{{fromUser}}/status/{{idStr}}"'
			+ 				'target="_blank"><span class="glyphicon glyphicon-link"></span></a>'
			+	        '</div>'
			+	    '</div>'
			+	    '<div class="panel-body">{{{unmodifiedText}}}</div>'
			+'</div>';

		var data = JSON.parse(tweet.body);

		var html = Mustache.to_html(template, data);

		$("#resultsBlock").prepend(html);
	}, function(error){
		// Error connecting to the endpoint
		console.log('Error: ' + error);
	});

	//client.send('/app/search', {}, JSON.stringify({'query' : query}));
}

function registerSearch() {
	$("#search").submit(function(ev){
		event.preventDefault();
		$("#resultsBlock").empty();	
		// Creating WebSocket
		if (client == null) stompConnection();
		else subscribeTwitter($('input#q').val());
	});
}

function hackathonChange() {
	$('#HackathonSelect').change(function (event) {
		event.preventDefault();		
		var selectedText = $(this).find("option:selected").text();		
		// PETICIÓN DE LOS YA ALMACENADOS + SUSCRIPCIÓN PARA LOS NUEVOS		
		console.log(selectedText);		
		currentHackathon = selectedText;		
		var data = JSON.stringify({'query' : selectedText});		
		console.log(data);		
		$.getJSON("hack", {query: selectedText }, function(tweets, status, jqXHR){
			if(tweets.length > 0) {				
				var data = tweets.map(function(a) {return a.tweet;});

				var template = '{{#.}}'
				+'<div class="row panel panel-default">'
				+	    '<div class="panel-heading">'
				+	        '<a href="https://twitter.com/{{fromUser}}"'
				+	           'target="_blank"><b>@{{fromUser}}</b></a>'
				+	        '<div class="pull-right">'
				+	            '<a href="https://twitter.com/{{fromUser}}/status/{{idStr}}"'
				+	               'target="_blank"><span class="glyphicon glyphicon-link"></span></a>'
				+	        '</div>'
				+	    '</div>'
				+	    '<div class="panel-body">{{{unmodifiedText}}}</div>'
				+'</div>'
				+'{{/.}}';

				var html = Mustache.to_html(template, data);

				$("#resultsBlock").empty().append(html);
			}			
		});		

		$("#resultsBlock").empty();	
		// Creating WebSocket
		if (client == null) stompConnection();
		subscribeTwitter(selectedText);
		
		// Attendees retrieval
		$.getJSON("attendees", {query: selectedText }, function(attendees, status, jqXHR){
			if(attendees.length > 0) {
				//var data = attendees.map(function(a) {return a.tweet;});	
			
				var template = '{{#.}}'	
				+'<div class="row panel panel-default">'	
				+	    '<div class="panel-heading">'	
				+	        '<a href="https://twitter.com/{{fromUser}}"'	
				+	           'target="_blank"><b>{{mail}}</b></a>'	
				+	    '</div>'	
				+	    '<div class="panel-body">{{{name}}}</div>'
				+	    '<div class="panel-body">{{{teeSize}}}</div>'	
				+	    '<div class="panel-body">{{{age}}}</div>'	
				+'</div>'	
				+'{{/.}}';	
			
				var html = Mustache.to_html(template, attendees);	
			
				$("#attendeesBlock").empty().append(html);	
			}			
		});	
	
		$("#attendeesBlock").empty();	
		// Creating WebSocket
		if (client == null) stompConnection();
		else subscribeAttendees(selectedText);
	});
}

function loadHackathons() {
	$('#HackathonSelect').change(function (event) {
		event.preventDefault();
			
		var selectedText = $(this).find("option:selected").text();	
			
		currentHackathon = selectedText;		
		var data = JSON.stringify({'query' : selectedText});		
		console.log(data);		
		$.getJSON("hack", {query: selectedText }, function(tweets, status, jqXHR){
			if(tweets.length > 0) {				
				var data = tweets.map(function(a) {return a.tweet;});

				var template = '{{#.}}'
				+'<div class="row panel panel-default">'
				+	    '<div class="panel-heading">'
				+	        '<a href="https://twitter.com/{{fromUser}}"'
				+	           'target="_blank"><b>@{{fromUser}}</b></a>'
				+	        '<div class="pull-right">'
				+	            '<a href="https://twitter.com/{{fromUser}}/status/{{idStr}}"'
				+	               'target="_blank"><span class="glyphicon glyphicon-link"></span></a>'
				+	        '</div>'
				+	    '</div>'
				+	    '<div class="panel-body">{{{unmodifiedText}}}</div>'
				+'</div>'
				+'{{/.}}';

				var html = Mustache.to_html(template, data);

				$("#resultsBlock").empty().append(html);
			}			
		});		

		$("#resultsBlock").empty();	
		// Creating WebSocket
		if (client == null) stompConnection();
		subscribeTwitter(selectedText);
	});
}


function loadAttendees() {
	$('#HackathonSelect').change(function (event) {
		event.preventDefault();	
			
		var selectedText = $(this).find("option:selected").text();	
			
		// PETICIÓN DE LOS YA ALMACENADOS + SUSCRIPCIÓN PARA LOS NUEVOS	
			
		console.log(selectedText);	
			
		currentHackathon = selectedText;	
			
		var data = JSON.stringify({'query' : selectedText});	
			
		console.log(data);	
			
		$.getJSON("attendees", {query: selectedText }, function(attendees, status, jqXHR){
			if(attendees.length > 0) {
				//var data = attendees.map(function(a) {return a.tweet;});	
			
				var template = '{{#.}}'	
				+'<div class="row panel panel-default">'	
				+	    '<div class="panel-heading">'	
				+	        '<a href="https://twitter.com/{{fromUser}}"'	
				+	           'target="_blank"><b>{{mail}}</b></a>'	
				+	    '</div>'	
				+	    '<div class="panel-body">{{{name}}}</div>'
				+	    '<div class="panel-body">{{{teeSize}}}</div>'	
				+	    '<div class="panel-body">{{{age}}}</div>'	
				+'</div>'	
				+'{{/.}}';	
			
				var html = Mustache.to_html(template, attendees);	
			
				$("#attendeesBlock").empty().append(html);	
			}			
		});	
	
		$("#attendeesBlock").empty();	
		// Creating WebSocket
		if (client == null) stompConnection();
		else subscribeAttendees(selectedText);
	});
}

function subscribeAttendees(query) {
	if (subscriptionAttendees != null) subscriptionAttendees.unsubscribe();
	
	//client.send('/app/search', {}, JSON.stringify({'query' : query}));
	
	subscriptionAttendees = client.subscribe(attendeesSubscriptionEndpoint + query, function(attendee){		
		//console.log(tweet);		
		//console.log(tweet.id);		
		var template = '{{#.}}'	
			+'<div class="row panel panel-default">'	
			+	    '<div class="panel-heading">'	
			+	        '<a href="https://twitter.com/{{fromUser}}"'	
			+	           'target="_blank"><b>{{mail}}</b></a>'	
			+	    '</div>'	
			+	    '<div class="panel-body">{{{name}}}</div>'
			+	    '<div class="panel-body">{{{teeSize}}}</div>'	
			+	    '<div class="panel-body">{{{age}}}</div>'	
			+'</div>'	
			+'{{/.}}';

		var data = JSON.parse(attendee.body);

		var html = Mustache.to_html(template, data);

		$("#attendeesBlock").prepend(html);
	}, function(error){
		// Error connecting to the endpoint
		console.log('Error: ' + error);
	});

	//client.send('/app/search', {}, JSON.stringify({'query' : query}));
}

