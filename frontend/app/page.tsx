"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/context/AuthContext";

export default function Home() {
	const { isAuthenticated } = useAuth();
	const router = useRouter();

	useEffect(() => {
		if (isAuthenticated) {
			router.push("/dashboard");
		} else {
			router.push("/login");
		}
	}, [isAuthenticated, router]);

	return (
		<div className="flex h-screen w-screen items-center justify-center bg-zinc-50 text-sm text-zinc-400">
			Loading workspace context...
		</div>
	);
}
