import { useAuth } from "@/context/AuthContext";

interface Task {
	id: number;
	title: string;
	description: string;
	status: "PENDING" | "IN_PROGRESS" | "COMPLETED";
	dueDate: string;
	ownerUsername: string;
}

interface TaskCardProps {
	readonly task: Task;
	readonly onEdit: (task: Task) => void;
	readonly onDelete: (id: number) => void;
}

export default function TaskCard({ task, onEdit, onDelete }: TaskCardProps) {
	const { role } = useAuth();

	const statusColors = {
		PENDING: "bg-zinc-100 text-zinc-700 border-zinc-200",
		IN_PROGRESS: "bg-amber-50 text-amber-700 border-amber-200",
		COMPLETED: "bg-emerald-50 text-emerald-700 border-emerald-200",
	};

	return (
		<div className="rounded-lg border border-zinc-200 bg-white p-5 shadow-sm transition-all hover:shadow-md">
			<div className="flex items-start justify-between gap-4">
				<h3 className="font-medium text-zinc-900">{task.title}</h3>
				<span
					className={`rounded-full border px-2.5 py-0.5 text-xs font-medium ${statusColors[task.status]}`}>
					{task.status.replace("_", " ")}
				</span>
			</div>

			<p className="mt-2 text-sm text-zinc-500 line-clamp-2">
				{task.description || "No description provided."}
			</p>

			<div className="mt-4 pt-4 border-t border-zinc-100 flex items-center justify-between text-xs text-zinc-400">
				<div>
					<p>
						Due:{" "}
						<span className="font-medium text-zinc-600">
							{task.dueDate || "No date"}
						</span>
					</p>
					{role === "ADMIN" ||
						(role === "SUPERADMIN" && (
							<p className="mt-0.5">
								Owner:{" "}
								<span className="font-medium text-zinc-600">
									{task.ownerUsername}
								</span>
							</p>
						))}
				</div>
				<div className="flex gap-3">
					<button
						onClick={() => onEdit(task)}
						className="font-medium text-zinc-600 hover:text-zinc-900 transition-colors">
						Edit
					</button>
					<button
						onClick={() => onDelete(task.id)}
						className="font-medium text-red-600 hover:text-red-700 transition-colors">
						Delete
					</button>
				</div>
			</div>
		</div>
	);
}
