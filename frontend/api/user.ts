(async () => {})();

const handleResponse = async (res: Response) => {
	if (!res.ok) {
		let msg = res.statusText;
		const body = await res.json();
		msg = body.message || JSON.stringify(body);

		throw new Error(msg || `Request failed: ${res.status}`);
	}
	return res.json();
};

export async function login(username: string, password: string) {
	const res = await fetch("/api/auth/login", {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify({ username, password }),
	});
	return handleResponse(res);
}

export async function register(
	username: string,
	email: string,
	password: string,
) {
	const res = await fetch("/api/auth/register", {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify({ username, email, password }),
	});
	return handleResponse(res);
}
