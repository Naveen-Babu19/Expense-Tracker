function login(){
	
	//getting properties from html
	let email = document.getElementById("login-email").value;
	let password = document.getElementById("login-password").value;
	let xhr = new XMLHttpRequest();
	console.log(email);
	xhr.onreadystatechange = function(){
		
		// If everything is correct this will execute
		if(this.readyState == 4 && this.status == 200){
			let json = JSON.parse(this.responseText);
			console.log(this.responseText);
			if(json["username"] != ""){
				lStorage(json["username"]);
				//window.location.href = "mainPage.html";
			}
		}
	}
	xhr.open("POST","http://localhost:8080/ExpenseTracker/Login",true);
	xhr.setRequestHeader("Content-Header","application/x-www-form-urlencoded");
	xhr.send("email="+email+"&password="+password);
}

function signUp(){
	
	// getting details from html
	let username = document.getElementById("signup-username").value;
	let email = document.getElementById("signup-email").value;
	let password = document.getElementById("signup-password").value;
	let salary = document.getElementById("signup-salary").value;
	let xhr = new XMLHttpRequest();
	
	let details = {
		"username": username,
		"email": email,
		"password": password,
		"salary": salary
	}
	xhr.onreadystatechange = function(){
		if(this.readyState == 4 && this.status == 200){
			lStorage(username);
			window.location.href = "mainPage.html";
		}
	}
	
	//Before sending checks
	
	//email check
	if(emailCheck(email)){
		//password check
		if(passwordCheck(password)){
			//salary check
			if(salary > 0){
				xhr.open("POST","http://localhost:8080/ExpenseTracker/Signup",true);
				xhr.setRequestHeader("Content-type", "application/json");
				xhr.send(JSON.stringify(details));
			}else{
				
				//Printing in html that incorrect salary
				document.getElementById("salaryCorrection").innerText = "Salary must be greater than zero";
			}
		}else{
			//Printing in html that incorrect password
			let passRules = document.getElementById("signup-passwordCorrection").innerText;
			document.getElementById("signup-passwordCorrection").innerText = "Incorrect password\n"+passRules;
		}
	}else{
		//Printing in html that incorrect email
		document.getElementById("signup-emailCorrection").innerText = "Invalid email address";
	}
}

function passwordCheck(password){
	
	// checking for special characters and space.
	let specialCharCheck = password.search(/[^\w\d]/i);
	if(specialCharCheck != -1 || password.length < 8 || password.length > 16){
		return false;
	}
	specialCharCheck = password.search(/[a-z]/);
	if(specialCharCheck == -1){
		return false;
	}
	specialCharCheck = password.search(/[A-Z]/);
	if(specialCharCheck == -1){
		return false;
	}
	specialCharCheck = password.search(/[0-9]/);
	if(specialCharCheck == -1){
		return false;
	}
	return true;
}

function emailCheck(email){
	
	// checking for space
	let spaceCheck = email.search(/[\s]/i);
	if(spaceCheck != -1){
		return false;
	}	
	
	// checking for right email address
	let emailCheck = email.search(/^[\w\._%\+-]+@[\w\.-]+\.[a-zA-Z]{2,}$/);
	if(emailCheck != -1){
		return true;
	}else{
		return false;
	} 
}

function changeNext(){
    document.getElementById("login").classList.toggle("disp-none");
    document.getElementById("signup").classList.toggle("disp-none");
}

function lStorage(username){
	localStorage.setItem("username",username);
}