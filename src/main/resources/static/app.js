const API_URL = "http://localhost:8080/api/students";
const API_AUTH = "http://localhost:8080/api/auth";

/**
 * Sends a POST request to create a new student record in the backend.
 * Resets the form and refreshes the student table upon success.
 *
 * @async
 * @function createStudent
 * @param {Object} studentData - The data object for the new student.
 * @param {string} studentData.firstName - The student's first name.
 * @param {string} studentData.lastName - The student's last name.
 * @param {string} studentData.email - The student's email address.
 * @returns {Promise<void>}
 */
async function createStudent(studentData) {
  try {
    const response = await fetch(API_URL, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(studentData),
      credentials: "include",
    });

    if (response.ok) {
      alert("Student added successfully!");
      resetForm();
      getAllStudents();
    } else {
      alert("Error adding student.");
    }
  } catch (error) {
    console.error("Error creating student:", error);
  }
}

/**
 * Handles the student form submission event. Validates the form and routes
 * the payload to either `updateStudent` or `createStudent` depending on
 * whether a hidden student ID is present.
 *
 * @param {SubmitEvent} e - The form submission event.
 * @returns {void}
 */
document.getElementById("studentForm").addEventListener("submit",
    function (e) {
  e.preventDefault();

  if (!validateStudentForm()) {
    return;
  }

  const id = document.getElementById("studentId").value;
  const studentData = {
    firstName: document.getElementById("firstName").value.trim(),
    lastName: document.getElementById("lastName").value.trim(),
    email: document.getElementById("email").value.trim(),
  };
  if (id) {
    updateStudent(id, studentData);
  } else {
    createStudent(studentData);
  }
});

/**
 * Resets the student input form, clears the hidden student ID field,
 * and restores the submit button to its default "Add Student" state.
 *
 * @function resetForm
 * @returns {void}
 */
function resetForm() {
  document.getElementById("studentId").value = "";
  document.getElementById("studentForm").reset();

  const submitBtn = document.getElementById("submitBtn");
  submitBtn.textContent = "Add Student";
  submitBtn.style.backgroundColor = "#28a745";
}

/**
 * Fetches all student records from the backend via a GET request and
 * populates the HTML table body with rows containing Edit and Delete buttons.
 *
 * @async
 * @function getAllStudents
 * @returns {Promise<void>}
 */
async function getAllStudents() {
  try {
    const response = await fetch(API_URL, {
      credentials: "include",
    });
    if (!response.ok) {
      throw new Error("Network response was not ok");
    }

    const students = await response.json();
    const tableBody = document.getElementById("studentTableBody");
    tableBody.innerHTML = "";

    students.forEach((student) => {
      const row = `
                <tr>
                    <td>${student.id}</td>
                    <td>${student.firstName}</td>
                    <td>${student.lastName}</td>
                    <td>${student.email}</td>
                    <td>
                        <button
                            class="btn-edit"
                            onclick="initializeStudentFormById(${student.id})"
                        >
                            Edit
                        </button>
                        <button
                            class="btn-delete"
                            onclick="deleteStudent(${student.id})"
                        >
                            Delete
                        </button>
                    </td>
                </tr>
            `;
      tableBody.innerHTML += row;
    });
  } catch (error) {
    console.error("Error fetching students:", error);
    alert("Failed to fetch students. Is your Spring Boot backend running?");
  }
}

/**
 * Fetches a single student's details by ID and populates the form fields
 * to prepare for editing. Updates the submit button UI and clears old
 * validation error styles.
 *
 * @async
 * @function initializeStudentFormById
 * @param {number|string} id - The unique identifier of the student to edit.
 * @returns {Promise<void>}
 */
async function initializeStudentFormById(id) {
  try {
    const response = await fetch(`${API_URL}/${id}`, {
      credentials: "include",
    });
    if (!response.ok) {
      throw new Error("Could not fetch student details");
    }

    const student = await response.json();

    document.getElementById("studentId").value = student.id;
    document.getElementById("firstName").value = student.firstName;
    document.getElementById("lastName").value = student.lastName;
    document.getElementById("email").value = student.email;

    // button changes when edit button is triggered
    const submitBtn = document.getElementById("submitBtn");
    submitBtn.textContent = "Update";
    submitBtn.style.backgroundColor = "#007bff";

    // clear old error highlights when loading data into form
    clearValidationStyles();
  } catch (error) {
    console.error("Error fetching student details:", error);
  }
}

/**
 * Sends a PUT request to update an existing student record in the backend.
 * Resets the form and refreshes the student table upon success.
 *
 * @async
 * @function updateStudent
 * @param {number|string} id - The ID of the student to update.
 * @param {Object} studentData - The updated student data payload.
 * @param {string} studentData.firstName - The updated first name.
 * @param {string} studentData.lastName - The updated last name.
 * @param {string} studentData.email - The updated email address.
 * @returns {Promise<void>}
 */
async function updateStudent(id, studentData) {
  try {
    const response = await fetch(`${API_URL}/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(studentData),
      credentials: "include",
    });

    if (response.ok) {
      alert("Student updated successfully!");
      resetForm();
      getAllStudents();
    } else {
      alert("Error updating student.");
    }
  } catch (error) {
    console.error("Error updating student:", error);
  }
}

/**
 * Prompts the user for confirmation and sends a DELETE request to remove
 * the specified student record from the backend. Refreshes the table on
 * success.
 *
 * @async
 * @function deleteStudent
 * @param {number|string} id - The unique identifier of the student to delete.
 * @returns {Promise<void>}
 */
async function deleteStudent(id) {
  if (confirm("Are you sure you want to delete this student?")) {
    try {
      const response = await fetch(`${API_URL}/${id}`, {
        method: "DELETE",
        credentials: "include",
      });

      if (response.ok) {
        const message = await response.text();
        alert(message);
        getAllStudents();
      } else {
        alert("Error deleting student.");
      }
    } catch (error) {
      console.error("Error deleting student:", error);
    }
  }
}

/**
 * Authenticates a user by sending login credentials to the auth endpoint.
 * Redirects to the main index template upon successful login.
 *
 * @async
 * @function loginUser
 * @param {Object} loginDto - The login data transfer object.
 * @param {string} loginDto.username - The user's username or email.
 * @param {string} loginDto.password - The user's password.
 * @returns {Promise<void>}
 */
async function loginUser(loginDto) {
  try {
    const response = await fetch(`${API_AUTH}/auth_login_session`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(loginDto),
      credentials: "include",
    });

    if (response.ok) {
      alert("Login successful!");
      window.location.href = "../templates/index.html";
    } else {
      alert("Invalid credentials.");
    }
  } catch (error) {
    console.error("Login error:", error);
  }
}

/**
 * Logs out the current user by invalidating the session on the backend,
 * clearing the student table, resetting the form, attempting to flush
 * browser Basic Auth credentials, and reloading the page.
 *
 * @async
 * @function logoutUser
 * @returns {Promise<void>}
 */
async function logoutUser() {
  try {
    await fetch(`${API_AUTH}/logout`, {
      method: "POST",
      credentials: "include",
    });
    document.getElementById("studentTableBody").innerHTML = "";
    resetForm();
    alert("Logged out successfully!");

    try {
      await fetch(API_URL, {
        method: "GET",
        headers: {
          Authorization: "Basic " + btoa("invalid_user:wrong_password"),
        },
      });
    } catch (err) {
      console.log("Browser credential cache cleared.");
    }
    window.location.reload();
  } catch (e) {
    console.error("Logout transmission crash:", e);
  }
}

/**
 * Initializes the student table once the initial HTML document has been
 * completely loaded and parsed.
 */
document.addEventListener("DOMContentLoaded", getAllStudents);
