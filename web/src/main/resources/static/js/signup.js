let elements = document.querySelectorAll('.validation');
let paragraphs = document.querySelectorAll('.errorMessage');
for(let index = 0; index< elements.length; index++) {
	['blur', 'keydown'].forEach(type => {
		if (type === 'blur') {
			elements[index].addEventListener(type, () => {
				if(!elements[index].value) {
					elements[index].classList.add("error");
					//paragraphs[index].innerHTML = "Camp obligatoriu";
				}
			});
		}
	        else if (type === 'keydown') {
	        	elements[index].addEventListener(type, (event) => {
	        		const key = event.key;
	        		if((key === "Backspace" || key === "Delete") && (elements[index].value.length === 0 || elements[index].value.length === 1)) {
	        			elements[index].classList.add("error");
	        			//paragraphs[index].innerHTML = "Camp obligatoriu";
	        		} else {
	        			elements[index].classList.remove("error");
	        		}
	        	});
	        }
	});
}