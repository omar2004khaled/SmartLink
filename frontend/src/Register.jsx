import React from "react";

function Register() {

  const registerUser = async () => {
    try {
      const response = await fetch("http://localhost:8080/auth/register", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          fullName: "Omar Khaled",
          email: "omar@test.com",
          password: "Password123!",
          confirmPassword: "Password123!",
          phoneNumber: "01012345678",
          birthDate: "2000-05-10",
          gender: "MALE"
        })
      });

      const data = await response.text();
      console.log("Server response:", data);
      alert("Registration request sent ✅");
    } catch (error) {
      console.error("Error:", error);
    }
  };

  return (
    <div>
      <h2>Test Registration</h2>
      <button onClick={registerUser}>Register Test User</button>
    </div>
  );
}

export default Register;
