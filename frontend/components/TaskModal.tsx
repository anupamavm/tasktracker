"use client";

import { useState, useEffect } from "react";

interface Task {
	id: number;
	title: string;
	description: string;
	status: "PENDING" | "IN_PROGRESS" | "COMPLETED";
	dueDate: string;
}

interface TaskModalProps {
	isOpen: boolean;
	onClose: () => void;
	onSave: (taskData: any) => void;
	task?: Task | null;
}

export default function TaskModal({
	isOpen,
	onClose,
	onSave,
	task,
}: TaskModalProps) {
	const [title, setTitle] = useState("");
	const [description, setDescription] = useState("");
	const [status, setStatus] = useState<"PENDING" | "IN_PROGRESS" | "COMPLETED">(
		"PENDING",
	);
	const [dueDate, setDueDate] = useState("");

	useEffect(() => {
		if (task) {
			setTitle(task.title);
			setDescription(task.description);
			setStatus(task.status);
			setDueDate(task.dueDate || "");
		} else {
			setTitle("");
			setDescription("");
			setStatus("PENDING");
			setDueDate("");
		}
	}, [task, isOpen]);

	if (!isOpen) return null;

	const handleSubmit = (e: React.FormEvent) => {
		e.preventDefault();
		onSave({ title, description, status, dueDate });
	};

	return (
		<div className="fixed inset-0 z-50 flex items-center justify-center bg-zinc-900/40 backdrop-blur-sm">
			<div className="w-full max-w-md rounded-xl border border-zinc-200 bg-white p-6 shadow-xl animate-in fade-in zoom-in-95 duration-150">
				<h2 className="text-lg font-semibold text-zinc-900">
					{task ? "Edit Task" : "Create New Task"}
				</h2>
				<form
					onSubmit={handleSubmit}
					className="mt-4 space-y-4">
					<div>
						<label className="text-xs font-medium text-zinc-500 uppercase tracking-wider">
							Title
						</label>
						<input
							type="text"
							required
							value={title}
							onChange={(e) => setTitle(e.target.value)}
							className="mt-1 w-full rounded-md border border-zinc-200 bg-zinc-50 px-3 py-2 text-sm text-zinc-900 outline-none transition-all focus:border-zinc-900 focus:bg-white"
						/>
					</div>
					<div>
						<label className="text-xs font-medium text-zinc-500 uppercase tracking-wider">
							Description
						</label>
						<textarea
							value={description}
							onChange={(e) => setDescription(e.target.value)}
							className="mt-1 w-full h-24 rounded-md border border-zinc-200 bg-zinc-50 px-3 py-2 text-sm text-zinc-900 outline-none transition-all focus:border-zinc-900 focus:bg-white resize-none"
						/>
					</div>
					<div className="grid grid-cols-2 gap-4">
						<div>
							<label className="text-xs font-medium text-zinc-500 uppercase tracking-wider">
								Status
							</label>
							<select
								value={status}
								onChange={(e) => setStatus(e.target.value as any)}
								className="mt-1 w-full rounded-md border border-zinc-200 bg-zinc-50 px-3 py-2 text-sm text-zinc-900 outline-none transition-all focus:border-zinc-900 focus:bg-white">
								<option value="PENDING">Pending</option>
								<option value="IN_PROGRESS">In Progress</option>
								<option value="COMPLETED">Completed</option>
							</select>
						</div>
						<div>
							<label className="text-xs font-medium text-zinc-500 uppercase tracking-wider">
								Due Date
							</label>
							<input
								type="date"
								value={dueDate}
								onChange={(e) => setDueDate(e.target.value)}
								className="mt-1 w-full rounded-md border border-zinc-200 bg-zinc-50 px-3 py-2 text-sm text-zinc-900 outline-none transition-all focus:border-zinc-900 focus:bg-white"
							/>
						</div>
					</div>
					<div className="mt-6 flex justify-end gap-3 pt-4 border-t border-zinc-100">
						<button
							type="button"
							onClick={onClose}
							className="rounded-md px-4 py-2 text-sm font-medium text-zinc-500 hover:bg-zinc-50 hover:text-zinc-900 transition-all">
							Cancel
						</button>
						<button
							type="submit"
							className="rounded-md bg-zinc-900 px-4 py-2 text-sm font-medium text-white hover:bg-zinc-800 transition-all shadow-sm">
							Save
						</button>
					</div>
				</form>
			</div>
		</div>
	);
}
