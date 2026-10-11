import { useState } from "react";
import {Link, useNavigate} from "react-router-dom";
import "./AuthPage.css";

function LoginPage() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            const response = await fetch("http://localhost:8080/api/auth/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email, password }),
            });

            if (response.ok) {
                const data = await response.json();
                localStorage.setItem("token", data.token);
                navigate("/tasks");
            } else {
                setError("Invalid email or password");
            }
        } catch (err) {
            setError("Something went wrong");
        }
    };

    return (
        <div className="login-page">
            <div className="login-card">
                <div className="login-brand">
                    <div className="login-logo" />
                    TaskManager
                </div>

                <header className="login-header">
                    <h1>Welcome back</h1>
                    <p>Log in to see your tasks.</p>
                </header>

                {error && <p className="login-error" role="alert">{error}</p>}

                <form onSubmit={handleLogin} className="login-form">
                    <label className="tm-field">
                        <span>Email</span>
                        <input
                            type="email"
                            className="tm-input"
                            required
                            autoComplete="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                        />
                    </label>
                    <label className="tm-field">
                        <span>Password</span>
                        <input
                            type="password"
                            className="tm-input"
                            required
                            autoComplete="current-password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                        />
                    </label>
                    <button type="submit" className="tm-btn tm-btn--primary login-submit">
                        Log in
                    </button>
                </form>
                <p className="auth-switch">
                    Don't have an account? <Link to="/register">Sign up</Link>
                </p>
            </div>
        </div>
    );
}

export default LoginPage;