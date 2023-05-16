// Get the form element
const form = document.getElementById('registrationForm');

// Add an event listener to the form's submit event
form.addEventListener('submit', function(event) {
  event.preventDefault(); // Prevent form submission

  // Get the input field values
  const firstName = document.getElementById('firstName').value;
  const lastName = document.getElementById('lastName').value;
  const email = document.getElementById('email').value;
  const password = document.getElementById('password').value;
  const phoneNumber = document.getElementById('phoneNumber').value;
  const gender = document.getElementById('gender').value;

  // Perform validation (you can add additional checks as needed)
  if (firstName.trim() === '') {
    alert('Förnamn');
    return;
  }
  if (lastName.trim() === '') {
      alert('Efternamn');
      return;
  }
  if (email.trim() === '') {
    alert('Email adress');
    return;
  }

  if (password.trim() === '') {
    alert('Password');
    return;
  }
  if (phoneNumber.trim() === '') {
      alert('Telefon nummer');
      return;
    }
    if (gender.trim() === '') {
        alert('Kön');
        return;
      }

  // Registration successful
  alert('Registration successful!');
  // You can perform additional actions here, such as sending the data to a server using AJAX or redirecting to another page.
});
