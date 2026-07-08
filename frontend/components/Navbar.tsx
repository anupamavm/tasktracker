"use client";

import { useAuth } from "@/context/AuthContext";

export default function Navbar() {
	const { username, role, logout, isAuthenticated } = useAuth();

	if (!isAuthenticated) return null;

	return (
		<nav className="border-b border-zinc-200 bg-white px-6 py-4">
			<div className="mx-auto flex max-w-7xl items-center justify-between">
				<div className="flex items-center gap-3">
					<span className="text-lg font-semibold tracking-tight text-zinc-900">
						TaskTracker
					</span>
					<span className="rounded bg-zinc-100 px-2 py-0.5 text-xs font-medium text-zinc-600 uppercase tracking-wider">
						{role}
					</span>
				</div>
				<div className="flex items-center gap-4">
					{role === "SUPERADMIN" ? (
						<a
							href="/admin"
							className="text-sm font-medium text-zinc-600 hover:text-zinc-900 transition-colors">
							Super Admin Panel
						</a>
					) : null}
					<span className="text-sm text-zinc-500">
						<span className="font-medium text-red-800">{username}</span>
					</span>
					<button
						onClick={logout}
						className="text-sm font-medium text-zinc-500 hover:text-zinc-900 transition-colors">
						Sign out
					</button>
				</div>
			</div>
		</nav>
	);
}
