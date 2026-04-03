import { getAccessToken } from "./auth";

const BASE_URL = process.env.NEXT_PUBLIC_API_URL;

//  types 

export type Role = "ROLE_ADMIN" | "ROLE_MANAGER";

export interface LoginRequest {

    username: string;
    password: string;
}

export interface RegisterRequest {
    fullName: string;
    username: string;
    password: string;
    role: Role;
}

export interface EmployeeRequest {

    name: string;
    email: string;
    phone: string;
    department: string;
}

export interface EmployeeUpdateRequest {
    name?: string;
    email?: string;
    phone?: string;
    department?: string;
}

export interface EmployeeResponse {
    id: number;
    name: string;
    email: string;
    phone: string;
    department: string;
}

                 //  Auth Helpers 

// adds Authorization header to every request auto
function authHeaders(): HeadersInit {
    const token = getAccessToken();
    return {
        "Content-Type": "application/json",
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
    };
}

//  auth api 
export async function loginApi(data: LoginRequest) {
    const res = await fetch(`${BASE_URL}/api/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
    });
    if (!res.ok) throw new Error("Login failed");
    return res.json();   // expects { accessToken, refreshToken } from your backend
}

export async function registerApi(data: RegisterRequest) {
    const res = await fetch(`${BASE_URL}/api/auth/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
    });
    if (!res.ok) throw new Error("Registration failed");
    // return res.json();
    return res.text();
}

//  Employee api 

export async function getAllEmployees(): Promise<EmployeeResponse[]> {
    const res = await fetch(`${BASE_URL}/api/employees`, {
        headers: authHeaders(),
    });
    if (!res.ok) throw new Error("Failed to fetch employees");
    return res.json();
}

export async function createEmployee(data: EmployeeRequest): Promise<EmployeeResponse> {
    const res = await fetch(`${BASE_URL}/api/employee`, {
        method: "POST",
        headers: authHeaders(),
        body: JSON.stringify(data),
    });
    if (!res.ok) throw new Error("Failed to create employee");
    return res.json();
}

export async function deleteEmployee(id: number): Promise<void> {
    const res = await fetch(`${BASE_URL}/api/employee/${id}`, {
        method: "DELETE",
        headers: authHeaders(),
    });
    if (!res.ok) throw new Error("Failed to delete employee");
}

export async function updateEmployee(id: number, data: EmployeeUpdateRequest): Promise<EmployeeResponse> {
    const res = await fetch(`${BASE_URL}/api/employee/${id}`, {
        method: "PATCH",
        headers: authHeaders(),
        body: JSON.stringify(data),
    });
    if (!res.ok) throw new Error("Failed to update employee");
    return res.json();
}