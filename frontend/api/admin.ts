export async function updateUserRole(
	token: string,
	username: string,
	role: string,
) {
	if (!token) throw new Error("Not authenticated");

	const res = await fetch("/api/admin/users/role", {
		method: "PUT",
		headers: {
			"Content-Type": "application/json",
			Authorization: `Bearer ${token}`,
		},
		body: JSON.stringify({ username, role }),
	});

	if (!res.ok) {
		let msg = res.statusText;
		try {
			const body = await res.json();
			msg = body.message || JSON.stringify(body);
		} catch {
			// ignore
		}
		throw new Error(msg || `Request failed: ${res.status}`);
	}

	return res.text();
}
