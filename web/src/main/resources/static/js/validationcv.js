function isValid(value) {
	if (value.length > 3) return true;
	else return false;
}

function justLetters(name, event, number) {
	if (!/^[a-zA-Z]*$/g.test(name) || name.length < 3) {
		$(".showErrorNameCreate" + number + 0).html(
			"Numele institutiei este scris gresit"
		);
		return false;
	} else {
		$(".showErrorNameCreate" + number + 0).text("");
		return true;
	}
}

function justLetters2(name, event, number) {
	if (!/^[a-zA-Z]*$/g.test(name) || name.length < 3) {
		$(".showErrorNameEdit" + number + 1).html(
			"Numele institutiei este scris gresit"
		);
		return false;
	} else {
		$(".showErrorNameEdit" + number + 1).text("");
		return true;
	}
}

function justLetters3(name) {
	if (!/^[a-zA-Z]*$/g.test(name)) return false;
	else return true;
}

let select = 50;

function selecteazaEducatia(value) {
	select = value;
}

function year(input, year, event, number) {
	let yearType = input.name;
	var text = /^[0-9]+$/;
	if (year != 0) {
		
		if(yearType === "start") {
			
			if (year.length > 4) {
				$(".showErrorStartYearCreate" + number + 2).html(
					"Anul nu este potrivit. Verifica din nou"
				);
				return false;
			} else {
				$(".showErrorStartYearCreate" + number + 2).text("");
			}

			if (year != "" && !text.test(year)) {
				$(".showErrorStartYearCreate" + number + 1).html(
					"Sunt permise doar valori numerice."
				);
				return false;
			} else {
				$(".showErrorStartYearCreate" + number + 1).text("");
			}

			var maxYear = 2019;
			if (
				(year < 1945 && year.length == 4) ||
				(year > maxYear && year.length == 4)
			) {
				$(".showErrorStartYearCreate" + number + 3).html(
					"Anul trebuie sa fie intre 1945 si 2019"
				);
				return false;
			} else if (year > 1945 || year < maxYear) {
				$(".showErrorStartYearCreate" + number + 3).text("");
			}
			return true;
			
		} else if(yearType === "end") {
			if (year.length > 4) {
				$(".showErrorEndYearCreate" + number + 2).html(
					"Anul nu este potrivit. Verifica din nou"
				);
				return false;
			} else {
				$(".showErrorEndYearCreate" + number + 2).text("");
			}

			if (year != "" && !text.test(year)) {
				$(".showErrorEndYearCreate" + number + 1).html(
					"Sunt permise doar valori numerice."
				);
				return false;
			} else {
				$(".showErrorEndYearCreate" + number + 1).text("");
			}

			var current_year = new Date().getFullYear();
			if (
				(year < 1945 && year.length == 4) ||
				(year > current_year && year.length == 4)
			) {
				$(".showErrorEndYearCreate" + number + 3).html(
					"Anul trebuie sa fie intre 1945 si anul curent"
				);
				return false;
			} else if (year > 1945 || year < current_year) {
				$(".showErrorEndYearCreate" + number + 3).text("");
			}
			return true;
		}
	}
}

function yearValidation(input, year, event, number) {
	let yearType = input.name;
	var text = /^[0-9]+$/;
	if (year != 0) {
		
		if(yearType === "editStart") {
			console.log("Sunt pe start");
			if (year.length > 4) {
				$(".showErrorStartYearEdit" + number + 2 + select).html(
					"Anul nu este potrivit. Verifica din nou"
				);
				return false;
			} else {
				$(".showErrorStartYearEdit" + number + 2 + select).text("");
			}

			if (year != "" && !text.test(year)) {
				$(".showErrorStartYearEdit" + number + 1 + select).html(
					"Sunt permise doar valori numerice."
				);
				return false;
			} else {
				$(".showErrorStartYearEdit" + number + 1 + select).text("");
			}

			var maxYear = 2019;
			if (
				(year < 1945 && year.length == 4) ||
				(year > maxYear && year.length == 4)
			) {
				$(".showErrorStartYearEdit" + number + 3 + select).html(
					"Anul trebuie sa fie intre 1945 si 2019"
				);
				return false;
			} else if (year > 1945 || year < maxYear) {
				$(".showErrorStartYearEdit" + number + 3 + select).text("");
			}
			return true;
		} else if(yearType === "editEnd") {
			console.log("Sunt pe end");
			if (year.length > 4) {
				$(".showErrorEndYearEdit" + number + 2 + select).html(
					"Anul nu este potrivit. Verifica din nou"
				);
				return false;
			} else {
				$(".showErrorEndYearEdit" + number + 2 + select).text("");
			}

			if (year != "" && !text.test(year)) {
				$(".showErrorEndYearEdit" + number + 1 + select).html(
					"Sunt permise doar valori numerice."
				);
				return false;
			} else {
				$(".showErrorEndYearEdit" + number + 1 + select).text("");
			}

			var current_year = new Date().getFullYear();
			if (
				(year < 1945 && year.length == 4) ||
				(year > current_year && year.length == 4)
			) {
				$(".showErrorEndYearEdit" + number + 3 + select).html(
					"Anul trebuie sa fie intre 1945 si anul curent"
				);
				return false;
			} else if (year > 1945 || year < current_year) {
				$(".showErrorEndYearEdit" + number + 3 + select).text("");
			}
			return true;
		}
		
		
	}
}

function yearValidation2(year, yearType) {
	var text = /^[0-9]+$/;
	if (year != 0) {
		
		if(yearType === "start") {
			if (year.length > 4) {
				return false;
			}

			if (year != "" && !text.test(year)) {
				return false;
			}

			var maxYear = 2019;
			if (
				(year < 1945 && year.length == 4) ||
				(year > maxYear && year.length == 4)
			) {
				return false;
			}
			return true;
		} else if(yearType === "end") {
			if (year.length > 4) {
				return false;
			}

			if (year != "" && !text.test(year)) {
				return false;
			}

			var current_year = new Date().getFullYear();
			if (
				(year < 1945 && year.length == 4) ||
				(year > current_year && year.length == 4)
			) {
				return false;
			}
			return true;
		}
		
		
	}
}

$(".experience").submit(function () {
	let name = document.getElementById("expName").value;
	let start = document.getElementById("expStart").value;
	let end = document.getElementById("expEnd").value;
	let city = document.getElementById("expCity").value;
	if ($(this).data("valid")) {
		//you've already validated, allow the form to submit
		return true;
	} else {
		//send an ajax request and wait for the response to really submit
		$.ajax({
			url: "/view_profile",
			success: function (data) {
				if (
					justLetters3(name) &&
					yearValidation2(start, "start") &&
					yearValidation2(end, "end") &&
					city.length > 3,
					end - start >= 1
				) {
					//submit the form again, but set valid data so you don't do another Ajax request
					$(".experience").data("valid", true);
					$(".experience").submit();
				} else {
					$(".minimDuration").html("Durata minima este de un an");
				}
			}
		});
		return false;
	}

	//clear the validation flat
	$(this).data("valid", false);
});

$("#editExperience").submit(function () {
	let name = document.getElementById("expEditName").value;
	let start = document.getElementById("expEditStart").value;
	let end = document.getElementById("expEditEnd").value;
	let city = document.getElementById("expEditCity").value;
	if ($(this).data("valid")) {
		return true;
	} else {
		$.ajax({
			url: "/view_profile",
			type: "get",
			success: function (valid) {
				if (
					justLetters3(name) &&
					yearValidation2(start, "start") &&
					yearValidation2(end, "end") &&
					city.length > 3,
					end - start >= 1
				) {
					$("#editExperience").data("valid", true);
					$("#editExperience").submit();
				} else {
					$(".minimDuration").html("Durata minima este de un an");
				}
			}
		});
		return false;
	}

	$(this).data("valid", false);
});

$(".education").submit(function () {
	let name = document.getElementById("name").value;
	let start = document.getElementById("start").value;
	let end = document.getElementById("end").value;
	let city = document.getElementById("city").value;
	if ($(this).data("valid")) {
		//you've already validated, allow the form to submit
		return true;
	} else {
		//send an ajax request and wait for the response to really submit
		$.ajax({
			url: "/view_profile",
			success: function (data) {
				if (
					justLetters3(name) &&
					yearValidation2(start, "start") &&
					yearValidation2(end, "end") &&
					city.length > 3,
					end - start >= 1
				) {
					//submit the form again, but set valid data so you don't do another Ajax request
					$(".education").data("valid", true);
					$(".education").submit();
				} else {
					$(".minimDuration").html("Durata minima este de un an");
				}
			}
		});
		return false;
	}

	//clear the validation flat
	$(this).data("valid", false);
});

$("#editEducation").submit(function () {
	let name = document.getElementById("editName").value;
	let start = document.getElementById("editStart").value;
	let end = document.getElementById("editEnd").value;
	let city = document.getElementById("editCity").value;
	if ($(this).data("valid")) {
		return true;
	} else {
		$.ajax({
			url: "/view_profile",
			type: "get",
			success: function (valid) {
				if (
					justLetters3(name) &&
					yearValidation2(start, "start") &&
					yearValidation2(end, "end") &&
					city.length > 3,
					end - start >= 1
				) {
					$("#editEducation").data("valid", true);
					$("#editEducation").submit();
				} else {
					$(".minimDuration").html("Durata minima este de un an");
				}
			}
		});
		return false;
	}

	$(this).data("valid", false);
});

$("#myForm").submit(function () {
	let description = document.getElementById("description").value;
	if ($(this).data("valid")) {
		//you've already validated, allow the form to submit
		return true;
	} else {
		//send an ajax request and wait for the response to really submit
		$.ajax({
			url: "/view_profile",
			success: function (data) {
				if (isValid(description)) {
					//submit the form again, but set valid data so you don't do another Ajax request
					$("#myForm").data("valid", true);
					$("#myForm").submit();
				} else {
					$("#showError").html(
						"Dimensiunea campului este de minim 3 caractere"
					);
				}
			}
		});
		return false;
	}

	//clear the validation flat
	$(this).data("valid", false);
});


console.log($("#editSkill"));
console.log(document.getElementById("description2").value);
$("#editSkill").submit(function (ev) {
	let description = document.getElementById("description2").value;
	if ($(this).data("valid")) {
		return true;
	} else {
		$.ajax({
			url: "/view_profile",
			type: "get",
			success: function (valid) {
				if (isValid(description)) {
					$("#editSkill").data("valid", true);
					$("#editSkill").submit();
				} else {
					ev.preventDefault();
					$("#showError2").html(
						"Dimensiunea campului este de minim 3 caractere"
					);
				}
			}
		});
		return false;
	}

	$(this).data("valid", false);
});

$("#aboutMe").submit(function (ev) {
	let description = document.getElementById("descriptionAccountInformation").value;
	if ($(this).data("valid")) {
		return true;
	} else {
		$.ajax({
			url: "/view_profile",
			type: "get",
			success: function (valid) {
				if (isValid(description)) {
					$("#aboutMe").data("valid", true);
					$("#aboutMe").submit();
				} else {
					ev.preventDefault();
					$("#showErrorAboutMe").html(
						"Dimensiunea campului este de minim 3 caractere"
					);
				}
			}
		});
		return false;
	}

	$(this).data("valid", false);
});

