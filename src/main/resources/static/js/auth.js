// Check authentication status and update navbar
document.addEventListener('DOMContentLoaded', function() {
    // Check if there's a token in the URL (from OAuth2 redirect)
    const urlParams = new URLSearchParams(window.location.search);
    const tokenFromUrl = urlParams.get('token');
    
    if (tokenFromUrl) {
        // Store the token and fetch user info
        localStorage.setItem('token', tokenFromUrl);
        
        // Fetch user info using the token
        fetch('/api/auth/me', {
            headers: {
                'Authorization': 'Bearer ' + tokenFromUrl
            }
        })
        .then(response => response.json())
        .then(data => {
            localStorage.setItem('userId', data.id);
            localStorage.setItem('username', data.username);
            localStorage.setItem('email', data.email);
            localStorage.setItem('name', data.name);
            // Remove token from URL and reload
            window.history.replaceState({}, document.title, window.location.pathname);
            window.location.reload();
        })
        .catch(error => {
            console.error('Error fetching user info:', error);
            // Remove token from URL anyway
            window.history.replaceState({}, document.title, window.location.pathname);
        });
        return;
    }
    
    const token = localStorage.getItem('token');
    const userName = localStorage.getItem('name');
    
    const loginLink = document.getElementById('loginLink');
    const userGreeting = document.getElementById('userGreeting');
    const userNameSpan = document.getElementById('userName');
    const logoutLink = document.getElementById('logoutLink');
    
    if (token && userName && loginLink && userGreeting && userNameSpan) {
        loginLink.style.display = 'none';
        userGreeting.style.display = 'inline';
        userNameSpan.textContent = userName;
        
        // Logout handler
        if (logoutLink) {
            logoutLink.addEventListener('click', function(e) {
                e.preventDefault();
                localStorage.removeItem('token');
                localStorage.removeItem('username');
                localStorage.removeItem('email');
                localStorage.removeItem('name');
                localStorage.removeItem('userId');
                window.location.href = '/login';
            });
        }
    }
});
