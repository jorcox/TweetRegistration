var client;
var subscription = null;

var subscriptionEndpoint = '/queue/search/'



$(document).ready(function() {
	stompConnection();
	//registerSearch();
	hackathonChange();
	
	
});

function stompConnection() {
	client = Stomp.over(new SockJS("/twitter"));
	
	var headers = {};
	var connectCallback = {};
	client.connect(headers, connectCallback);
}

function subscribeTwitter(query) {

	if (subscription != null) subscription.unsubscribe();

	subscription = client.subscribe(subscriptionEndpoint + query, function(tweet){

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

	client.send('/app/search', {}, JSON.stringify({'query' : query}));
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
	$('#HackathonSelect').change(function () {
		event.preventDefault();
		
		var selectedText = $(this).find("option:selected").text();
		
		// PETICIÓN DE LOS YA ALMACENADOS + SUSCRIPCIÓN PARA LOS NUEVOS
		
		console.log(selectedText);
		
		
		// Petición de los datos antiguos.
		$.ajax({
			type : "GET",
			url : "/hack-info/old",
			data : $("#shortener")
					.serialize(),
			success : function(result) {
				console.log(result);
				//$("#loading").hide();
				//$("#no").hide();
				//$("#yes").show();
				//$("#validation")
				//		.removeClass(
				//				"has-error")
				//		.addClass(
				//				"has-success");
			},
			error : function(msg, error, status) {
				console.log('Error');
				
			}
		});
		
		// Descomentar esto cuando estés preparado
		
		$("#resultsBlock").empty();	
		// Creating WebSocket
		if (client == null) stompConnection();
		else subscribeTwitter(selectedText);
	});
}

