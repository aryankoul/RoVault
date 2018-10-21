// chrome.browserAction.onClicked.addListener(buttonClicked);
// // chrome.storage.sync.get(null, function (data) { console.info(data) });
// function buttonClicked(tab){
// 	var url = tab.url;
// 	console.log(url);
// 	chrome.storage.sync.get('user',function(usr){
// 		console.log(usr.user);
// 		if(usr.user == ""){
// 			// if not logged in
// 			$('#logout').addClass("hidden");
// 		}
// 		else{
// 			$('#login').addClass("hidden");
// 		}
// 	});
// }
chrome.browserAction.onClicked.addListener(function (tab) {
	// for the current tab, inject the "inject.js" file & execute it
	chrome.tabs.executeScript(tab.ib, {
		file: 'inject.js'
	});
});
