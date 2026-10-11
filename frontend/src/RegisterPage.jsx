import { useState } from "react";
import {Link, useNavigate} from "react-router-dom";
import "./AuthPage.css";

function RegisterPage() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [name, setName] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();

    const handleRegister = async (e) => {
        e.preventDefault();
        try {
            const response = await fetch("http://localhost:8080/api/auth/register", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email, password, name }),
            });

            if (response.ok) {
                const data = await response.json();
                localStorage.setItem("token", data.token);
                navigate("/tasks");
            } else if (response.status === 409){
                setError("An account with this email already exists");
            } else if (response.status === 400) {
                setError("Please check your details");
            } else {
                setError("Something went wrong");
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
                    <h1>Create an account</h1>
                    <p>Log in to see your tasks.</p>
                </header>

                {error && <p className="login-error" role="alert">{error}</p>}

                <form onSubmit={handleRegister} className="login-form">
                    <label className="tm-field">
                        <span>Name</span>
                        <input
                            type="name"
                            className="tm-input"
                            required
                            autoComplete="name"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                        />
                    </label>
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
                            autoComplete="new-password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                        />
                    </label>
                    <button type="submit" className="tm-btn tm-btn--primary login-submit">
                        Sign up
                    </button>
                </form>
                <p className="auth-switch">
                    Already have an account? <Link to="/login">Log in</Link>
                </p>
            </div>
        </div>
    );
}

export default RegisterPage;