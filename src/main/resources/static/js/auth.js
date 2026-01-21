document.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);
    const tokenFromUrl = urlParams.get('token');
    
    if (tokenFromUrl) {
        localStorage.setItem('token', tokenFromUrl);
        
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
            window.history.replaceState({}, document.title, window.location.pathname);
            window.location.reload();
        })
        .catch(error => {
            console.error('Error fetching user info:', error);
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
