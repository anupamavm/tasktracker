const handleResponse = async (res: Response) => {
	if (!res.ok) {
		let msg = res.statusText;
		const body = await res.json();
		msg = body.message || JSON.stringify(body);
		throw new Error(msg || `Request failed: ${res.status}`);
	}
	return res.json();
};
export async function fetchTasks(token: string | null, status?: string) {
	if (!token) return [];
	const url = status
		? `/api/tasks?status=${encodeURIComponent(status)}`
		: "/api/tasks";
	const res = await fetch(url, {
		headers: { Authorization: `Bearer ${token}` },
	});
	const data = await handleResponse(res);
	return data.content || [];
}

export async function saveTask(
	token: string | null,
	taskData: any,
	id?: number,
) {
	if (!token) throw new Error("Not authenticated");
	const method = id ? "PUT" : "POST";
	const url = id ? `/api/tasks/${id}` : "/api/tasks";
	const res = await fetch(url, {
		method,
		headers: {
			"Content-Type": "application/json",
			Authorization: `Bearer ${token}`,
		},
		body: JSON.stringify(taskData),
	});
	return handleResponse(res);
}

export async function deleteTask(token: string | null, id: number) {
	if (!token) throw new Error("Not authenticated");
	const res = await fetch(`/api/tasks/${id}`, {
		method: "DELETE",
		headers: { Authorization: `Bearer ${token}` },
	});
	return handleResponse(res);
}
