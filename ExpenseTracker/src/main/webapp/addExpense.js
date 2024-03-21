function sendExpenseToJava(){
	let amount = parseInt(document.getElementById("amount").value);
	let description = document.getElementById("Description").value;
	let category = document.getElementById("category").value;
	let expenseDetails = {
		username:"saran"
	};
	let jsonGot;
	expenseDetails["category"] = category;
	expenseDetails["amount"] = amount;
	expenseDetails["description"] = description;
	let xhr = new XMLHttpRequest();
	xhr.onreadystatechange = function () {
		if(this.status == 200 && this.readyState == 4){
			let jsonGot = JSON.parse(this.responseText);
			console.log(this.responseText);
			if(jsonGot["1"] == "true"){
				document.getElementById("output").innerText = "Enter successful";
			}
		}
	}
	console.log("json text: "+JSON.stringify(expenseDetails));
	xhr.open("POST","http://localhost:8080/ExpenseTracker/app/AddExpenseServlet");
	xhr.setRequestHeader("Content-type","application/json");
	xhr.send(JSON.stringify(expenseDetails));
}

function getCategories(){
	let xhr = new XMLHttpRequest();
	xhr.onreadystatechange = function () {
		if(this.status == 200 && this.readyState == 4){
			console.log(this.responseText);
			let json = JSON.parse(this.responseText);
			createCategories(json);
		}
	}
	xhr.open("GET","http://localhost:8080/ExpenseTracker/app/UserCategory");
	xhr.send();
}

function createCategories(json){
	let values = Object.values(json);
	for(let index = 0; index < values.length; index++){
		createHtml(values[index]);
	}
}

function createHtml(text){
	let category = document.createElement("Option");
	let categoryName = document.createTextNode(text);
	let dropdown = document.getElementById("categories");
	category.setAttribute("name",text);
	category.appendChild(categoryName);
	dropdown.appendChild(category);
}

function checkCategory(){
	let category = document.getElementById("categories").value;
	console.log(category);
	if(category == "Others"){
		document.getElementById("categoryAdder").classList.toggle("none");
	}
}

function addCategory(){
	let categoryName = document.getElementById("categoryName").value;
	let budget = document.getElementById("budget").value;
	if(categoryName == "" || categoryName == undefined){
		document.getElementById("categoryName").classList.toggle("wrong-input");
		return;
	}
	if(isNaN(budget) || budget == ""){
		budget = 0;
	}
	let xhr = new XMLHttpRequest();
	xhr.onreadystatechange = function(){
		if(this.status == 200 && this.readyState == 4){
			let json = JSON.parse(this.responseText);
			if(json["status"] == "done"){
				createHtml("categoryName");
			}
		}
	}
	let details = {
		"name": categoryName,
		"budget": budget
	}
	xhr.open("POST","http://localhost:8080/ExpenseTracker/app/AddCategoryServlet");
	xhr.setRequestHeader("Content-type","application/json");
	xhr.send(JSON.stringify(details));
}

function closeContainer(){
	document.getElementById("categoryAdder").classList.toggle("none");
}