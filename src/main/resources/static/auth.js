
// ==========================================
// BOOK YOUR SEAT - AUTHENTICATION SYSTEM
// ==========================================


// ==========================================
// 1. CUSTOM NOTIFICATION SYSTEM
// ==========================================

function showToast(type, title, message) {

    let container = document.querySelector(".toast-container");

    if (!container) {
        container = document.createElement("div");
        container.className = "toast-container";
        document.body.appendChild(container);
    }

    const icons = {
        success: "✓",
        error: "✕",
        warning: "!",
        info: "i"
    };

    const toast = document.createElement("div");
    toast.className = `toast ${type}`;

    const icon = document.createElement("div");
    icon.className = "toast-icon";
    icon.textContent = icons[type] || "i";

    const content = document.createElement("div");
    content.className = "toast-content";

    const heading = document.createElement("div");
    heading.className = "toast-title";
    heading.textContent = title;

    const description = document.createElement("div");
    description.className = "toast-message";
    description.textContent = message;

    const close = document.createElement("button");
    close.className = "toast-close";
    close.type = "button";
    close.textContent = "×";
    close.setAttribute("aria-label", "Close notification");

    const progress = document.createElement("div");
    progress.className = "toast-progress";

    content.append(heading, description);
    toast.append(icon, content, close, progress);
    container.appendChild(toast);

    let timer;

    function removeToast() {

        if (!toast.isConnected) return;

        clearTimeout(timer);

        toast.style.animation = "toastOut .3s ease forwards";

        setTimeout(() => toast.remove(), 300);
    }

    close.addEventListener("click", removeToast);

    timer = setTimeout(removeToast, 4000);
}


// ==========================================
// 2. API REQUEST FUNCTION
// ==========================================

async function sendAuthRequest(url, payload) {

    const response = await fetch(url, {

        method: "POST",

        headers: {
            "Content-Type": "application/json"
        },

        credentials: "same-origin",

        body: JSON.stringify(payload)

    });

    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || "Something went wrong!");
    }

    return data;
}


// ==========================================
// 3. LOGIN SYSTEM
// ==========================================

document.addEventListener("DOMContentLoaded", function () {

    const loginForm = document.getElementById("loginForm");

    if (loginForm) {

        loginForm.addEventListener("submit", async function (event) {

            event.preventDefault();

            const email = document.getElementById("email").value.trim();

            const password = document.getElementById("password").value;

            // Validate login fields

            if (!email || !password) {

                showToast(
                    "warning",
                    "Missing Information",
                    "Please enter your email and password."
                );

                return;
            }

            const button = loginForm.querySelector('button[type="submit"]');

            button.disabled = true;
            button.textContent = "Logging in...";

            try {

                // Send login request to Spring Boot

                const data = await sendAuthRequest(
                    "/api/auth/login",
                    {
                        email: email,
                        password: password
                    }
                );

                showToast(
                    "success",
                    "Login Successful",
                    "Welcome back to BOOK YOUR SEAT!"
                );

                // Redirect to homepage

                setTimeout(() => {
                    window.location.href = "/";
                }, 600);

            } catch (error) {

                showToast(
                    "error",
                    "Login Failed",
                    error.message
                );

                button.disabled = false;
                button.textContent = "Login";
            }

        });
    }


    // ==========================================
    // 4. SIGNUP SYSTEM
    // ==========================================

    const signupForm = document.getElementById("signupForm");

    if (signupForm) {

        signupForm.addEventListener("submit", async function (event) {

            event.preventDefault();

            const fullName = document.getElementById("fullName").value.trim();

            const email = document.getElementById("signupEmail").value.trim();

            const phone = document.getElementById("phone").value.trim();

            const password = document.getElementById("signupPassword").value;

            const confirmPassword = document.getElementById("confirmPassword").value;

            const terms = document.getElementById("terms").checked;


            // Validate required fields

            if (
                !fullName ||
                !email ||
                !phone ||
                !password ||
                !confirmPassword
            ) {

                showToast(
                    "warning",
                    "Missing Information",
                    "Please complete all required fields."
                );

                return;
            }


            // Validate phone number

            if (!/^[0-9]{10}$/.test(phone)) {

                showToast(
                    "error",
                    "Invalid Phone Number",
                    "Please enter a valid 10-digit phone number."
                );

                return;
            }


            // Validate password length

            if (password.length < 8) {

                showToast(
                    "warning",
                    "Weak Password",
                    "Password must contain at least 8 characters."
                );

                return;
            }


            // Confirm password

            if (password !== confirmPassword) {

                showToast(
                    "error",
                    "Password Mismatch",
                    "Your passwords do not match."
                );

                return;
            }


            // Terms and conditions

            if (!terms) {

                showToast(
                    "warning",
                    "Terms Required",
                    "Please accept the Terms and Conditions."
                );

                return;
            }


            const button = signupForm.querySelector('button[type="submit"]');

            button.disabled = true;
            button.textContent = "Creating Account...";


            try {

                // Send registration details to Spring Boot

                const data = await sendAuthRequest(
                    "/api/auth/signup",
                    {
                        fullName: fullName,
                        email: email,
                        phone: phone,
                        password: password,
                        gender: ""
                    }
                );


                showToast(
                    "success",
                    "Account Created",
                    "Welcome to BOOK YOUR SEAT!"
                );


                // Redirect after successful registration

                setTimeout(() => {
                    window.location.href = "/";
                }, 600);


            } catch (error) {

                showToast(
                    "error",
                    "Signup Failed",
                    error.message
                );

                button.disabled = false;
                button.textContent = "Create Account";
            }

        });
    }


    // ==========================================
    // 5. FORGOT PASSWORD
    // ==========================================

    const forgotPassword = document.getElementById("forgotPassword");

    if (forgotPassword) {

        forgotPassword.addEventListener("click", function (event) {

            event.preventDefault();

            showToast(
                "info",
                "Password Recovery",
                "Password recovery will be available soon."
            );

        });
    }

});
