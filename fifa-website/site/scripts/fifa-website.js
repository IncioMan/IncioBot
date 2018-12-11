$(document).ready(function(){
	$("#down-button").click(function() {
		console.log("here");
		$('html,body').delay(500).animate({
			scrollTop: $("#first").offset().top},
			900);
	});
	$("#gitlab-logo-id").click(function() {
		window.location.href = 'https://gitlab.com/IncioMan/incio-bot';
	});
	$("#telegram-logo-id").click(function() {
		window.location.href = "https://t.me/EueiFifaBot";
	});
})