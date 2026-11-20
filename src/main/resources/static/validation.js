/**
 * Validates the student input form fields (first name, last name, and email).
 * Clears previous validation styles, displays inline error messages, and
 * marks invalid inputs with the "invalid" CSS class.
 *
 * @function validateStudentForm
 * @returns {boolean} True if all form inputs are valid, otherwise false.
 */
function validateStudentForm() {
  let isValid = true;

  const firstName = document.getElementById("firstName");
  const lastName = document.getElementById("lastName");
  const email = document.getElementById("email");

  const firstNameError = document.getElementById("firstNameError");
  const lastNameError = document.getElementById("lastNameError");
  const emailError = document.getElementById("emailError");

  clearValidationStyles();

  if (firstName.value.trim() === "") {
    firstNameError.textContent = "First name is required.";
    firstName.classList.add("invalid");
    isValid = false;
  } else if (firstName.value.trim().length < 2) {
    firstNameError.textContent =
      "First name must be at least 2 characters long.";
    firstName.classList.add("invalid");
    isValid = false;
  }

  if (lastName.value.trim() === "") {
    lastNameError.textContent = "Last name is required.";
    lastName.classList.add("invalid");
    isValid = false;
  } else if (lastName.value.trim().length < 2) {
    lastNameError.textContent = 
    "Last name must be at least 2 characters long.";
    lastName.classList.add("invalid");
    isValid = false;
  }

  const emailValue = email.value.trim();
  const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

  if (emailValue === "") {
    emailError.textContent = "Email address is required.";
    email.classList.add("invalid");
    isValid = false;
  } else if (!emailRegex.test(emailValue)) {
    emailError.textContent =
      "Please enter a valid email address (e.g., student@example.com).";
    email.classList.add("invalid");
    isValid = false;
  }
  return isValid;
}

/**
 * Removes the "invalid" CSS class from all student form inputs and clears
 * any displayed validation error text from their corresponding error spans.
 *
 * @function clearValidationStyles
 * @returns {void}
 */
function clearValidationStyles() {
  const fields = ["firstName", "lastName", "email"];
  fields.forEach((fieldId) => {
    const element = document.getElementById(fieldId);
    const errorSpan = document.getElementById(`${fieldId}Error`);
    if (element) element.classList.remove("invalid");
    if (errorSpan) errorSpan.textContent = "";
  });
}
