
// ==========================================
// BOOK YOUR SEAT - MAIN JAVASCRIPT
// ==========================================


// 1. SALON SEARCH FUNCTIONALITY

function searchSalons() {
    const searchInput = document.getElementById("searchInput");
    const salonContainer = document.getElementById("salonContainer");
    const noResults = document.getElementById("noResults");

    if (!searchInput || !salonContainer) {
        return;
    }

    const searchValue = searchInput.value.toLowerCase().trim();

    const salonCards = salonContainer.querySelectorAll(".salon-card");

    let visibleCount = 0;

    salonCards.forEach(function (card) {

        const salonName = card.innerText.toLowerCase();

        if (salonName.includes(searchValue)) {
            card.style.display = "";
            visibleCount++;
        } else {
            card.style.display = "none";
        }

    });

    if (noResults) {
        noResults.style.display = visibleCount === 0 ? "block" : "none";
    }
}


// 2. OPEN SALON DETAILS PAGE

function openSalonDetails(salonName) {

    if (!salonName) {
        alert("Salon information is missing!");
        return;
    }

    const selectedSalon = encodeURIComponent(salonName);

    window.location.href =
        "/salon-details.html?salon=" + selectedSalon;
}


// 3. CHECK LOGIN SESSION

async function checkLoginSession() {

    const navButtons = document.querySelector(".nav-buttons");

    // If navigation buttons are not present,
    // skip the login navigation update.

    if (!navButtons) {
        return;
    }

    try {

        const response = await fetch("/api/auth/me", {
            method: "GET",
            credentials: "same-origin"
        });

        if (!response.ok) {
            return;
        }

        const user = await response.json();

        if (!user.userName) {
            return;
        }

        // Clear existing login/signup buttons.

        navButtons.innerHTML = "";

        // Create welcome message.

        const welcomeMessage = document.createElement("span");

        welcomeMessage.className = "welcome-user";
        welcomeMessage.textContent = "Welcome, " + user.userName;

        // Create logout button.

        const logoutButton = document.createElement("button");

        logoutButton.type = "button";
        logoutButton.className = "logout-btn";
        logoutButton.textContent = "Logout";

        logoutButton.addEventListener("click", logoutUser);

        navButtons.appendChild(welcomeMessage);
        navButtons.appendChild(logoutButton);

    } catch (error) {

        console.error("Login session error:", error);

    }
}


// 4. LOGOUT FUNCTIONALITY

async function logoutUser() {

    try {

        const response = await fetch("/api/auth/logout", {
            method: "POST",
            credentials: "same-origin"
        });

        if (response.ok) {
            alert("You have been logged out successfully.");

            window.location.href = "/login.html";
        } else {
            alert("Unable to logout. Please try again.");
        }

    } catch (error) {

        console.error("Logout error:", error);

        alert("Something went wrong while logging out.");

    }
}


// 5. PAGE INITIALIZATION

document.addEventListener("DOMContentLoaded", function () {

    // Check whether a user is logged in.

    checkLoginSession();

});
