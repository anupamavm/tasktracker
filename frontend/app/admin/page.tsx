"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/context/AuthContext";
import Navbar from "@/components/Navbar";
import { updateUserRole } from "@/api/admin";

export default function SuperAdminPage() {
	const router = useRouter();
	const { token, role, isAuthenticated } = useAuth();
	const [username, setUsername] = useState("");
	const [selectedRole, setSelectedRole] = useState("USER");
	const [message, setMessage] = useState("");
	const [isUpdating, setIsUpdating] = useState(false);

	if (!isAuthenticated || role?.toUpperCase() !== "SUPERADMIN") {
		return null;
	}

	const handleSubmit = async (e: React.FormEvent) => {
		e.preventDefault();
		setIsUpdating(true);
		setMessage("");

		try {
			await updateUserRole(token!, username, selectedRole);
			setMessage(`Role updated successfully for ${username}.`);
			setUsername("");
		} catch (err: any) {
			setMessage(err.message || "Failed to update role");
		} finally {
			setIsUpdating(false);
		}
	};

	return (
		<div className="min-h-screen bg-zinc-50">
			<Navbar />
			<main className="mx-auto max-w-3xl px-6 py-10">
				<div className="rounded-2xl border border-zinc-200 bg-white p-8 shadow-sm">
					<div className="mb-6 flex items-center justify-between gap-4">
						<h1 className="text-xl font-semibold text-zinc-900">
							Super Admin Controls
						</h1>

						<button
							onClick={() => router.push("/dashboard")}
							className="rounded-md bg-green-900 px-4 py-2 text-sm font-medium text-white hover:bg-zinc-800 transition-all shadow-sm">
							Back to Dashboard
						</button>
					</div>

					<form
						onSubmit={handleSubmit}
						className="mt-8 space-y-4">
						<div>
							<label
								htmlFor="username"
								className="mb-2 block text-sm font-medium text-zinc-700">
								Username
							</label>
							<input
								id="username"
								value={username}
								onChange={(e) => setUsername(e.target.value)}
								required
								className="w-full rounded-md border border-zinc-300 px-3 py-2 text-sm outline-none focus:border-zinc-900"
								placeholder="Enter username"
							/>
						</div>

						<div>
							<label
								htmlFor="role"
								className="mb-2 block text-sm font-medium text-zinc-700">
								Role
							</label>
							<select
								id="role"
								value={selectedRole}
								onChange={(e) => setSelectedRole(e.target.value)}
								className="w-full rounded-md border border-zinc-300 px-3 py-2 text-sm outline-none focus:border-zinc-900">
								<option value="USER">USER</option>
								<option value="ADMIN">ADMIN</option>
							</select>
						</div>

						<button
							type="submit"
							disabled={isUpdating}
							className="rounded-md bg-zinc-900 px-4 py-2 text-sm font-medium text-white transition-all hover:bg-zinc-800 disabled:opacity-60">
							{isUpdating ? "Updating..." : "Update Role"}
						</button>
					</form>

					{message ? (
						<p className="mt-4 text-sm text-zinc-600">{message}</p>
					) : null}
				</div>
			</main>
		</div>
	);
}
