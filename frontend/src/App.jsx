import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import LoginPage from "./LoginPage";
import RegisterPage from "./RegisterPage";
import TasksPage from "./TasksPage";

// Renders the page only if a token exists; otherwise redirects to login.
function ProtectedRoute({ children }) {
    const token = localStorage.getItem("token");
    return token ? children : <Navigate to="/login" replace />;
}

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />
                <Route
                    path="/tasks"
                    element={
                        <ProtectedRoute>
                            <TasksPage />
                        </ProtectedRoute>
                    }
                />
                <Route path="*" element={<Navigate to="/tasks" replace />} />
            </Routes>
        </BrowserRouter>
    );
}

export default App;