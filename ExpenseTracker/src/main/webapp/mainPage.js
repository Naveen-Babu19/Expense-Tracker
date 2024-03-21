function getDetails(){
	let xhr = new XMLHttpRequest();
	xhr.onreadystatechange = function(){
		if(this.readyState == 4 && this.status == 200){
			console.log(this.responseText);
			let valueGot = JSON.parse(this.responseText);
			if(valueGot["Salary"] != undefined){
				document.getElementById("income").innerText += " "+ valueGot["Salary"];
			}else if(valueGot["status"] == "LoggedOut"){
				alert("Your session has been closed");
				window.location.href = "loginPage.html";
			}
		}
	}
	xhr.open("POST","http://localhost:8080/ExpenseTracker/app/Salary");
	xhr.send();
	getSpent();
}

function getSpent(){
	let xhr = new XMLHttpRequest();
	xhr.onreadystatechange = function(){
		if(this.readyState == 4 && this.status == 200){
			console.log(this.responseText);
			let valueGot = JSON.parse(this.responseText);
			if(valueGot["totalAmount"] != undefined){
				document.getElementById("spent").innerText += " "+valueGot["totalAmount"];
			}else if(valueGot["status"] == "LoggedOut"){
				alert("Your session has been closed");
				window.location.href = "loginPage.html";
			}
		}
	}
	xhr.open("POST","http://localhost:8080/ExpenseTracker/app/TotalAmountSpentThisMonth");
	xhr.send();
	getBalance();
}

function getBalance(){
	let spent = parseInt(document.getElementById("spent").innerText);
	let income = parseInt(document.getElementById("income").innerText);
	document.getElementById("balance").innerText += !isNaN(income - spent)? (income - spent) : " 00";
	getUsername();
}

function getUsername(){
	console.log(localStorage.getItem("username"));
	document.getElementById("Greeting").innerText = "Hello, "+JSON.stringify(localStorage.getItem("username"));
}