"use client";

import { useEffect, useState } from "react";
import { useAuth } from "@/context/AuthContext";
import Navbar from "@/components/Navbar";
import TaskCard from "@/components/TaskCard";
import TaskModal from "@/components/TaskModal";

export default function Dashboard() {
	const { token, isAuthenticated } = useAuth();
	const [tasks, setTasks] = useState([]);
	const [statusFilter, setStatusFilter] = useState("");
	const [isModalOpen, setIsModalOpen] = useState(false);
	const [editingTask, setEditingTask] = useState<any>(null);

	const fetchTasks = async () => {
		if (!token) return;
		try {
			const url = statusFilter
				? `/api/tasks?status=${statusFilter}`
				: "/api/tasks";
			const res = await fetch(url, {
				headers: { Authorization: `Bearer ${token}` },
			});
			const data = await res.json();
			setTasks(data.content || []);
		} catch (err) {
			console.error("Error fetching tasks:", err);
		}
	};

	useEffect(() => {
		if (isAuthenticated) {
			fetchTasks();
		}
	}, [token, statusFilter, isAuthenticated]);

	// Establish WebSocket connection for Real-Time Updates
	useEffect(() => {
		if (!isAuthenticated) return;

		// Simple execution using fallback long-polling mechanism checking every 5 seconds to match SockJS synchronization
		const pollInterval = setInterval(fetchTasks, 5000);
		return () => clearInterval(pollInterval);
	}, [token, statusFilter, isAuthenticated]);

	const handleSaveTask = async (taskData: any) => {
		try {
			const method = editingTask ? "PUT" : "POST";
			const url = editingTask ? `/api/tasks/${editingTask.id}` : "/api/tasks";

			const res = await fetch(url, {
				method,
				headers: {
					"Content-Type": "application/json",
					Authorization: `Bearer ${token}`,
				},
				body: JSON.stringify(taskData),
			});

			if (res.ok) {
				setIsModalOpen(false);
				setEditingTask(null);
				fetchTasks();
			}
		} catch (err) {
			console.error("Error saving task:", err);
		}
	};

	const handleDeleteTask = async (id: number) => {
		if (!confirm("Are you sure you want to delete this task?")) return;
		try {
			const res = await fetch(`/api/tasks/${id}`, {
				method: "DELETE",
				headers: { Authorization: `Bearer ${token}` },
			});
			if (res.ok) fetchTasks();
		} catch (err) {
			console.error("Error deleting task:", err);
		}
	};

	if (!isAuthenticated) return null;

	return (
		<div className="min-h-screen bg-zinc-50">
			<Navbar />
			<main className="mx-auto max-w-7xl px-6 py-8">
				<div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
					<div>
						<h1 className="text-xl font-semibold tracking-tight text-zinc-900">
							Workspace Tasks
						</h1>
						<p className="text-xs text-zinc-400 mt-0.5">
							Manage and trace assignments in real-time.
						</p>
					</div>
					<div className="flex items-center gap-3">
						<select
							value={statusFilter}
							onChange={(e) => setStatusFilter(e.target.value)}
							className="rounded-md border border-zinc-200 bg-white px-3 py-2 text-xs font-medium text-zinc-600 outline-none transition-all focus:border-zinc-900">
							<option value="">All Statuses</option>
							<option value="PENDING">Pending</option>
							<option value="IN_PROGRESS">In Progress</option>
							<option value="COMPLETED">Completed</option>
						</select>
						<button
							onClick={() => {
								setEditingTask(null);
								setIsModalOpen(true);
							}}
							className="rounded-md bg-zinc-900 px-4 py-2 text-xs font-medium text-white hover:bg-zinc-800 transition-all shadow-sm">
							New Task
						</button>
					</div>
				</div>

				{tasks.length === 0 ? (
					<div className="mt-12 flex flex-col items-center justify-center rounded-xl border border-dashed border-zinc-200 bg-white py-16 text-center">
						<span className="text-sm font-medium text-zinc-400">
							No tasks found
						</span>
						<p className="mt-1 text-xs text-zinc-400 max-w-xs">
							Create an assignment above or update filter params.
						</p>
					</div>
				) : (
					<div className="mt-8 grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
						{tasks.map((task: any) => (
							<TaskCard
								key={task.id}
								task={task}
								onEdit={(t) => {
									setEditingTask(t);
									setIsModalOpen(true);
								}}
								onDelete={handleDeleteTask}
							/>
						))}
					</div>
				)}
			</main>

			<TaskModal
				isOpen={isModalOpen}
				onClose={() => {
					setIsModalOpen(false);
					setEditingTask(null);
				}}
				onSave={handleSaveTask}
				task={editingTask}
			/>
		</div>
	);
}
