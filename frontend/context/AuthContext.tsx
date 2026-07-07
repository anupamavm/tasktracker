"use client";

import React, { createContext, useContext, useState, useEffect } from "react";
import { useRouter } from "next/navigation";

interface AuthContextType {
	token: string | null;
	username: string | null;
	role: string | null;
	login: (token: string, username: string, role: string) => void;
	logout: () => void;
	isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
	const [token, setToken] = useState<string | null>(null);
	const [username, setUsername] = useState<string | null>(null);
	const [role, setRole] = useState<string | null>(null);
	const router = useRouter();

	useEffect(() => {
		// Sync state with local storage on mount
		const storedToken = localStorage.getItem("token");
		const storedUser = localStorage.getItem("username");
		const storedRole = localStorage.getItem("role");

		if (storedToken && storedUser && storedRole) {
			setToken(storedToken);
			setUsername(storedUser);
			setRole(storedRole);
		}
	}, []);

	const login = (token: string, username: string, role: string) => {
		localStorage.setItem("token", token);
		localStorage.setItem("username", username);
		localStorage.setItem("role", role);
		setToken(token);
		setUsername(username);
		setRole(role);
		router.push("/dashboard");
	};

	const logout = () => {
		localStorage.clear();
		setToken(null);
		setUsername(null);
		setRole(null);
		router.push("/login");
	};

	return (
		<AuthContext.Provider
			value={{
				token,
				username,
				role,
				login,
				logout,
				isAuthenticated: !!token,
			}}>
			{children}
		</AuthContext.Provider>
	);
};

export const useAuth = () => {
	const context = useContext(AuthContext);
	if (!context) throw new Error("useAuth must be used within an AuthProvider");
	return context;
};
