document.addEventListener(onbeforeunload, function(){
	let xhr = new XMLHttpRequest();
	xhr.open("POST","http://localhost:8080/ExpenseTracker/app/Logout");
	xhr.send();
})