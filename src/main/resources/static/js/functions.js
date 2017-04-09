var client;
var subscription = null;

var subscriptionEndpoint = '/queue/search/'



$(document).ready(function() {
	loadSelect();
	saveHackathon();
	stompConnection();
	//registerSearch();
	hackathonChange();
	
	
	
});

function loadSelect() {
	$.getJSON("hackathons", function(hackathons){	
		if(hackathons.length > 0) {
			
			//var data = hackathons.map(function(a) {return a.tweet;});

			console.log(hackathons);
			
			var data = hackathons.map(function(a) {return a.nombre;});

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
								"tag":$('input#tag').val()}, function(data, status){		
									alert("Data: " + data + "\nStatus: " + status);
									loadSelect();									
	    });
	});
}

function stompConnection() {
	client = Stomp.over(new SockJS("/twitter"));
	
	var headers = {};
	var connectCallback = {};
	client.connect(headers, connectCallback);
}

function subscribeTwitter(query) {
	if (subscription != null) subscription.unsubscribe();
	
	client.send('/app/search', {}, JSON.stringify({'query' : query}));
	
	subscription = client.subscribe(subscriptionEndpoint + query, function(tweet){		
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
		// Descomentar esto cuando estés preparado
		
		$("#resultsBlock").empty();	
		// Creating WebSocket
		if (client == null) stompConnection();
		else subscribeTwitter(selectedText);
	});
}

