"use client";

import React, { useState } from "react";
import Link from "next/link";
import { useAuth } from "@/context/AuthContext";
import { register as apiRegister } from "@/api/user";

export default function RegisterPage() {
	const [username, setUsername] = useState("");
	const [email, setEmail] = useState("");
	const [password, setPassword] = useState("");
	const [error, setError] = useState("");
	const { login } = useAuth();

	const handleSubmit = async (e: React.FormEvent) => {
		e.preventDefault();
		setError("");
		try {
			const data = await apiRegister(username, email, password);
			login(data.token, data.username, data.role);
		} catch (err: any) {
			setError(err.message || "Registration failed");
		}
	};

	return (
		<div className="flex min-h-screen items-center justify-center bg-zinc-50 px-4">
			<div className="w-full max-w-sm rounded-xl border border-zinc-200 bg-white p-8 shadow-sm">
				<h2 className="text-xl font-semibold tracking-tight text-zinc-900">
					Create account
				</h2>
				<p className="mt-1.5 text-xs text-zinc-400">
					Get started with a clean task layout.
				</p>

				{error && (
					<div className="mt-4 rounded bg-red-50 p-2.5 text-xs font-medium text-red-600 border border-red-100">
						{error}
					</div>
				)}

				<form
					onSubmit={handleSubmit}
					className="mt-6 space-y-4">
					<div>
						<label className="text-xs font-medium text-zinc-500 uppercase tracking-wider">
							Username
						</label>
						<input
							type="text"
							required
							value={username}
							onChange={(e) => setUsername(e.target.value)}
							className="mt-1 w-full rounded-md border border-zinc-200 bg-zinc-50 px-3 py-2 text-sm text-zinc-900 outline-none transition-all focus:border-zinc-900 focus:bg-white"
						/>
					</div>
					<div>
						<label className="text-xs font-medium text-zinc-500 uppercase tracking-wider">
							Email
						</label>
						<input
							type="email"
							required
							value={email}
							onChange={(e) => setEmail(e.target.value)}
							className="mt-1 w-full rounded-md border border-zinc-200 bg-zinc-50 px-3 py-2 text-sm text-zinc-900 outline-none transition-all focus:border-zinc-900 focus:bg-white"
						/>
					</div>
					<div>
						<label className="text-xs font-medium text-zinc-500 uppercase tracking-wider">
							Password
						</label>
						<input
							type="password"
							required
							value={password}
							onChange={(e) => setPassword(e.target.value)}
							className="mt-1 w-full rounded-md border border-zinc-200 bg-zinc-50 px-3 py-2 text-sm text-zinc-900 outline-none transition-all focus:border-zinc-900 focus:bg-white"
						/>
					</div>
					<button
						type="submit"
						className="mt-2 w-full rounded-md bg-zinc-900 py-2.5 text-sm font-medium text-white hover:bg-zinc-800 transition-all shadow-sm">
						Register
					</button>
				</form>
				<p className="mt-6 text-center text-xs text-zinc-400">
					Already have an account?{" "}
					<Link
						href="/login"
						className="font-medium text-zinc-900 hover:underline">
						Sign in
					</Link>
				</p>
			</div>
		</div>
	);
}
