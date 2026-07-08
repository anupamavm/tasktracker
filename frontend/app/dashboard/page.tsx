"use client";

import { useCallback, useEffect, useRef, useState } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/context/AuthContext";
import {
	fetchTasks as apiFetchTasks,
	saveTask as apiSaveTask,
	deleteTask as apiDeleteTask,
} from "@/api/tasks";
import Navbar from "@/components/Navbar";
import TaskCard from "@/components/TaskCard";
import TaskModal from "@/components/TaskModal";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

export default function Dashboard() {
	const router = useRouter();
	const { token, isAuthenticated } = useAuth();
	const [tasks, setTasks] = useState<any[]>([]);
	const [statusFilter, setStatusFilter] = useState("");
	const [isModalOpen, setIsModalOpen] = useState(false);
	const [editingTask, setEditingTask] = useState<any>(null);
	const stompClientRef = useRef<Client | null>(null);

	const fetchTasks = useCallback(async () => {
		const data = await apiFetchTasks(token, statusFilter);
		setTasks(data || []);
	}, [token, statusFilter]);

	useEffect(() => {
		if (!isAuthenticated || !token) return;

		void fetchTasks();

		const client = new Client({
			webSocketFactory: () =>
				new SockJS(
					`${process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080"}/ws`,
				),
			debug: () => {},
			onConnect: () => {
				client.subscribe("/topic/tasks", () => {
					void fetchTasks();
				});
			},
			onStompError: (frame) => {
				console.error("STOMP error:", frame.headers["message"]);
			},
		});

		client.activate();
		stompClientRef.current = client;

		return () => {
			client.deactivate();
			stompClientRef.current = null;
		};
	}, [isAuthenticated, token, fetchTasks]);

	const handleSaveTask = async (taskData: any) => {
		try {
			await apiSaveTask(token, taskData, editingTask?.id);
			setIsModalOpen(false);
			setEditingTask(null);
			fetchTasks();
		} catch (err) {
			console.error("Error saving task:", err);
		}
	};

	const handleDeleteTask = async (id: number) => {
		if (!confirm("Are you sure you want to delete this task?")) return;
		try {
			await apiDeleteTask(token, id);
			fetchTasks();
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
