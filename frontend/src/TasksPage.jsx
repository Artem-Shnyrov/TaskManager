import { useState, useEffect, useMemo } from "react";
import "./TasksPage.css";
import { useNavigate } from "react-router-dom";

const STATUS_LABELS = { TODO: "To do", IN_PROGRESS: "In progress", DONE: "Completed" };

function TasksPage() {
    const [tasks, setTasks] = useState([]);
    const [title, setTitle] = useState("");
    const [description, setDescription] = useState("");
    const [priority, setPriority] = useState("MEDIUM");
    const [dueDate, setDueDate] = useState("");
    const [search, setSearch] = useState("");
    const [statusFilter, setStatusFilter] = useState("All");
    const navigate = useNavigate();
    const API_URL = "http://localhost:8080/api/tasks";

    const token = () => localStorage.getItem("token");

    const apiFetch = async (url, options = {}) => {
        const response = await fetch(url, {
            ...options,
            headers: {
                ...options.headers,
                Authorization: "Bearer " + localStorage.getItem("token"),
            },
        });

        if (response.status === 401) {
            localStorage.removeItem("token");
            navigate("/login", { replace: true });
        }

        return response;
    };

    const fetchTasks = async () => {
        const response = await apiFetch(API_URL);
        if (response.ok) setTasks(await response.json());
    };

    const handleCreate = async (e) => {
        e.preventDefault();
        const response = await apiFetch(API_URL, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                title,
                description: description || null,
                priority,
                dueDate: dueDate || null,
            }),
        });
        if (response.ok) {
            setTitle("");
            setDescription("");
            setPriority("MEDIUM");
            setDueDate("");
            await fetchTasks();
        }
    };

    const handleStatusChange = async (id, newStatus) => {
        const response = await apiFetch(`${API_URL}/${id}`, {
            method: "PATCH",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ status: newStatus }),
        });
        if (response.ok) fetchTasks();
    };

    const handleDelete = async (id) => {
        const response = await apiFetch(`${API_URL}/${id}`, { method: "DELETE" });
        if (response.ok) fetchTasks();
    };

    const handleLogout = () => {
        localStorage.removeItem("token");
        navigate("/login", { replace: true });
    };

    useEffect(() => {
        fetchTasks();
    }, []);

    const counts = useMemo(() => ({
        total: tasks.length,
        inProgress: tasks.filter((t) => t.status === "IN_PROGRESS").length,
        completed: tasks.filter((t) => t.status === "DONE").length,
    }), [tasks]);

    const visibleTasks = useMemo(() => {
        const q = search.trim().toLowerCase();
        return tasks.filter((t) => {
            const statusOk = statusFilter === "All" || t.status === statusFilter;
            const searchOk = q === "" ||
                t.title.toLowerCase().includes(q) ||
                (t.description || "").toLowerCase().includes(q);
            return statusOk && searchOk;
        });
    }, [tasks, search, statusFilter]);

    return (
        <div className="tm-page">
            <div className="tm-layout">

                <aside className="tm-sidebar">
                    <div className="tm-brand">
                        <div className="tm-logo" />
                        TaskManager
                    </div>
                    <p className="tm-section-label">Workspace</p>
                    <nav className="tm-nav">
                        {["All", "IN_PROGRESS", "DONE"].map((f) => (
                            <button
                                key={f}
                                type="button"
                                className={`tm-nav-btn ${statusFilter === f ? "is-active" : ""}`}
                                onClick={() => setStatusFilter(f)}
                            >
                                {f === "All" ? "All tasks" : STATUS_LABELS[f]}
                            </button>
                        ))}
                    </nav>
                    <button type="button" className="tm-nav-btn tm-logout" onClick={handleLogout}>
                        Log out
                    </button>
                </aside>

                <main className="tm-main">
                    <header className="tm-header">
                        <h1>My Tasks</h1>
                        <p>Stay focused with your task workflow.</p>
                    </header>

                    <div className="tm-stats">
                        {[
                            { label: "Total", value: counts.total },
                            { label: "In progress", value: counts.inProgress },
                            { label: "Completed", value: counts.completed },
                        ].map((c) => (
                            <div key={c.label} className="tm-panel tm-stat">
                                <div className="tm-stat-label">{c.label}</div>
                                <div className="tm-stat-value">{c.value}</div>
                            </div>
                        ))}
                    </div>

                    <div className="tm-workspace">

                        <section className="tm-panel">
                            <div className="tm-panel-header">
                                <h2>Tasks</h2>
                                <span className="tm-count">{visibleTasks.length} shown</span>
                            </div>
                            <input
                                type="search"
                                className="tm-input tm-search"
                                value={search}
                                onChange={(e) => setSearch(e.target.value)}
                                placeholder="Search tasks..."
                            />

                            {visibleTasks.length === 0 ? (
                                <p className="tm-empty">
                                    {tasks.length === 0
                                        ? "No tasks yet. Create one in the panel."
                                        : "No tasks match your search."}
                                </p>
                            ) : (
                                <div className="tm-task-grid">
                                    {visibleTasks.map((task) => (
                                        <article
                                            key={task.id}
                                            className={`tm-panel tm-task ${task.status === "DONE" ? "is-done" : ""}`}
                                        >
                                            <div className="tm-task-top">
                                                <span className={`tm-chip tm-chip--${task.status}`}>
                                                    {STATUS_LABELS[task.status]}
                                                </span>
                                                <span className="tm-priority">
                                                    <span className={`tm-dot tm-dot--${task.priority}`} />
                                                    {task.priority}
                                                </span>
                                            </div>
                                            <h3 className="tm-task-title">{task.title}</h3>
                                            {task.description && <p className="tm-task-desc">{task.description}</p>}
                                            {task.dueDate && <p className="tm-task-due">Due {task.dueDate}</p>}
                                            <div className="tm-task-actions">
                                                {task.status === "TODO" && (
                                                    <button type="button" className="tm-btn"
                                                            onClick={() => handleStatusChange(task.id, "IN_PROGRESS")}>
                                                        Start
                                                    </button>
                                                )}
                                                {task.status !== "DONE" && (
                                                    <button type="button" className="tm-btn"
                                                            onClick={() => handleStatusChange(task.id, "DONE")}>
                                                        Mark done
                                                    </button>
                                                )}
                                                {task.status === "DONE" && (
                                                    <button type="button" className="tm-btn"
                                                            onClick={() => handleStatusChange(task.id, "TODO")}>
                                                        Reopen
                                                    </button>
                                                )}
                                                <button type="button" className="tm-btn tm-btn--danger"
                                                        onClick={() => handleDelete(task.id)}>
                                                    Delete
                                                </button>
                                            </div>
                                        </article>
                                    ))}
                                </div>
                            )}
                        </section>

                        <section className="tm-panel tm-form">
                            <h2>New task</h2>
                            <form onSubmit={handleCreate} className="tm-form-fields">
                                <label className="tm-field">
                                    <span>Title *</span>
                                    <input type="text" className="tm-input" required value={title}
                                           onChange={(e) => setTitle(e.target.value)} />
                                </label>
                                <label className="tm-field">
                                    <span>Description</span>
                                    <textarea className="tm-input" rows={3} value={description}
                                              onChange={(e) => setDescription(e.target.value)} />
                                </label>
                                <label className="tm-field">
                                    <span>Priority</span>
                                    <select className="tm-input" value={priority}
                                            onChange={(e) => setPriority(e.target.value)}>
                                        <option value="LOW">Low</option>
                                        <option value="MEDIUM">Medium</option>
                                        <option value="HIGH">High</option>
                                        <option value="URGENT">Urgent</option>
                                    </select>
                                </label>
                                <label className="tm-field">
                                    <span>Due date</span>
                                    <input type="date" className="tm-input" value={dueDate}
                                           onChange={(e) => setDueDate(e.target.value)} />
                                </label>
                                <button type="submit" className="tm-btn tm-btn--primary">
                                    Create task
                                </button>
                            </form>
                        </section>

                    </div>
                </main>
            </div>
        </div>
    );
}

export default TasksPage;