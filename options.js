$(function(){
	console.log(window.location.href);
	chrome.storage.sync.get(null, function (data) { console.info(data) });
	chrome.storage.sync.get('user',function(usr){
		console.log(usr.user);
		if(usr.user == ""){
			$('#logout').addClass("hidden");
		}
		else{
			$('#login').addClass("hidden");
		}
	});
	$('#in').click(function(){
		var username = $('#email').val();
		var password = $('#password').val();
		// console.log(password);
		// chrome.storage.sync.get('user',function(usr){
		// 	console.log(usr.user);
		// });
		$.post( "http://7019cfd3.ngrok.io/api/login", { email: username ,pass: password} )
		  .done(function( data ) {
		    console.log( "Data Loaded: " + data.success );
		    if(data.success){
				chrome.storage.sync.set({'user': username});
				$('#logout').removeClass("hidden");
				$('#login').addClass("hidden");
				$('#msg').addClass("hidden");

	    	    var sites = data.sites;
				chrome.storage.sync.set({'sites': sites});
				chrome.storage.sync.get('sites',
					function(sitearray){
						var array = sitearray.sites;
						console.log(array);
						// $.each( array , function( key, value ) {
						// console.log( key + ": " + value );
						// });
					});
		    }
		    else{
		    	$('#msg').removeClass("hidden");
		    	$('#email').val("");
				$('#password').val("");
		    }
		});
	});
	$('#out').click(function(){
		console.log(1);
		var username="";
		chrome.storage.sync.set({'user': username});
		var a = [];
		chrome.storage.sync.set({'sites': a});
		$('#login').removeClass("hidden");
		$('#logout').addClass("hidden");

	});

});