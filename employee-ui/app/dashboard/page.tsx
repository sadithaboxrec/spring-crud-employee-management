"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import {
    getAllEmployees,
    createEmployee,
    deleteEmployee,
    updateEmployee,
    EmployeeResponse,
    EmployeeRequest,
} from "@/lib/api";

import { clearTokens, getUsername, decodeToken, getAccessToken } from "@/lib/auth";

export default function DashboardPage() {
    const router = useRouter();

    const [employees, setEmployees] = useState<EmployeeResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    // Role-based access from token
    const [isAdmin, setIsAdmin] = useState(false);
    const [username, setUsername] = useState("");

    // Modal state to creating/editing
    const [showModal, setShowModal] = useState(false);
    const [editingEmployee, setEditingEmployee] = useState<EmployeeResponse | null>(null);
    const [form, setForm] = useState<EmployeeRequest>({ name: "", email: "", phone: "", department: "" });

    // On page load 
    useEffect(() => {
        const token = getAccessToken();
        if (!token) { router.push("/login"); return; }

        // Decode token to get role
        const decoded = decodeToken(token) as any;
        const role: string = decoded?.role ?? "";          // adjust field name to match your backend
        setIsAdmin(role === "ROLE_ADMIN");
        setUsername(decoded?.sub ?? "");

        fetchEmployees();
    }, []);

    async function fetchEmployees() {
        try {
            const data = await getAllEmployees();
            setEmployees(data);
        } catch {
            setError("Failed to load employees.");
        } finally {
            setLoading(false);
        }
    }

    //  Logout 
    function handleLogout() {
        clearTokens();
        document.cookie = "accessToken=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT"; // clear cookie
        router.push("/login");
    }

    //  Open modal for create 
    function openCreateModal() {
        setEditingEmployee(null);
        setForm({ name: "", email: "", phone: "", department: "" });
        setShowModal(true);
    }

    //   modal for edit 
    function openEditModal(emp: EmployeeResponse) {
        setEditingEmployee(emp);
        setForm({ name: emp.name, email: emp.email, phone: emp.phone, department: emp.department });
        setShowModal(true);
    }

    //  Handle form input changes 
    function handleFormChange(e: React.ChangeEvent<HTMLInputElement>) {
        setForm({ ...form, [e.target.name]: e.target.value });
    }

        //  Validation helpers 
    function isValidEmail(email: string) {
        return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
    }

    function isValidPhone(phone: string) {
        return /^\+94\s\d{2}\s\d{2}\s\d{2}\s\d{3}$/.test(phone);
    }

    //  Submit create or edit 
    async function handleFormSubmit(e: React.FormEvent) {
        e.preventDefault();

        if (!isValidEmail(form.email)) {
            alert("Please enter a valid email address.");
            return;
        }

        if (form.phone && !isValidPhone(form.phone)) {
            alert("Phone must be in format: +94 77 77 77 777");
            return;
        }

        try {
            if (editingEmployee) {
                // Edit mode
                const updated = await updateEmployee(editingEmployee.id, form);
                setEmployees(employees.map((emp) => (emp.id === updated.id ? updated : emp)));
            } else {
                // Create mode
                const created = await createEmployee(form);
                setEmployees([...employees, created]);
            }
            setShowModal(false);
        } catch {
            alert("Operation failed.");
        }
    }

    //  Delete 
    async function handleDelete(id: number) {
        if (!confirm("Delete this employee?")) return;
        try {
            await deleteEmployee(id);
            setEmployees(employees.filter((emp) => emp.id !== id));
        } catch {
            alert("Delete failed.");
        }
    }


    return (
        <div className="min-h-screen bg-gradient-to-br from-gray-100 to-gray-200">

            {/* Navbar */}
            <nav className="bg-gradient-to-r from-blue-600 to-indigo-600 shadow-lg px-6 py-4 flex justify-between items-center">
            <h1 className="text-2xl font-bold text-white tracking-wide">
                Employee Management System
            </h1>

            <div className="flex items-center gap-4">
                <span className="text-sm text-blue-100">
                {username} —{" "}
                <span className="font-semibold">
                    {isAdmin ? "Admin" : "Manager"}
                </span>
                </span>

                <button
                onClick={handleLogout}
                className="text-sm bg-white/20 px-3 py-1 rounded text-white hover:bg-white/30 transition"
                >
                Logout
                </button>
            </div>
            </nav>

            <main className="max-w-6xl mx-auto px-4 py-10">

            {/* Header row */}
            <div className="flex justify-between items-center mb-8">
                <h2 className="text-2xl font-bold text-gray-700">Employees</h2>

                {isAdmin && (
                <button
                    onClick={openCreateModal}
                    className="bg-blue-600 text-white px-5 py-2 rounded-lg text-sm hover:bg-blue-700 transition shadow"
                >
                    + Add Employee
                </button>
                )}
            </div>

            {/* Error */}
            {error && (
                <div className="bg-red-100 text-red-700 px-4 py-2 rounded mb-4 text-sm">
                {error}
                </div>
            )}

            {/* Loading */}
            {loading ? (
                <p className="text-gray-500 text-sm">Loading...</p>
            ) : (
                <div className="bg-white rounded-xl shadow-lg overflow-x-auto">
                <table className="w-full text-sm">

                    <thead className="bg-gray-100 text-gray-600 uppercase text-xs">
                    <tr>
                        <th className="px-4 py-3 text-left">Name</th>
                        <th className="px-4 py-3 text-left">Email</th>
                        <th className="px-4 py-3 text-left">Phone</th>
                        <th className="px-4 py-3 text-left">Department</th>
                        <th className="px-4 py-3 text-left">Actions</th>
                    </tr>
                    </thead>

                    <tbody>
                    {employees.length === 0 ? (
                        <tr>
                        <td colSpan={6} className="px-4 py-6 text-center text-gray-400">
                            No employees found.
                        </td>
                        </tr>
                    ) : (
                        employees.map((emp) => (
                        <tr key={emp.id} className="border-t hover:bg-gray-50 transition">


                            <td className="px-4 py-3 font-medium text-gray-700">
                            {emp.name}
                            </td>

                            <td className="px-4 py-3">{emp.email}</td>
                            <td className="px-4 py-3">{emp.phone}</td>

                            <td className="px-4 py-3">
                            <span className="bg-blue-100 text-blue-600 px-2 py-1 rounded text-xs">
                                {emp.department}
                            </span>
                            </td>

                            <td className="px-4 py-3 flex gap-3">
                            <button
                                onClick={() => openEditModal(emp)}
                                className="text-blue-600 hover:text-blue-800 font-medium"
                            >
                                Edit
                            </button>

                            {isAdmin && (
                                <button
                                onClick={() => handleDelete(emp.id)}
                                className="text-red-500 hover:text-red-700 font-medium"
                                >
                                Delete
                                </button>
                            )}
                            </td>
                        </tr>
                        ))
                    )}
                    </tbody>

                </table>
                </div>
            )}
            </main>

            {/* Modal */}
            {showModal && (
            <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
                <div className="bg-white rounded-xl shadow-xl p-6 w-full max-w-md">

                <h3 className="text-xl font-bold mb-4 text-gray-700">
                    {editingEmployee ? "Edit Employee" : "Add Employee"}
                </h3>

                <form onSubmit={handleFormSubmit} className="space-y-4">
                    {(["name", "email", "phone", "department"] as const).map((field) => (
                    <div key={field}>
                        <label className="block text-sm font-medium text-gray-700 mb-1 capitalize">
                        {field}
                        </label>

                        <input
                        name={field}
                        value={form[field]}
                        onChange={handleFormChange}
                        className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                        required={field !== "phone"}
                        />
                    </div>
                    ))}

                    <div className="flex justify-end gap-2 pt-2">
                    <button
                        type="button"
                        onClick={() => setShowModal(false)}
                        className="px-4 py-2 text-sm text-gray-600 border rounded hover:bg-gray-100"
                    >
                        Cancel
                    </button>

                    <button
                        type="submit"
                        className="px-4 py-2 text-sm bg-blue-600 text-white rounded hover:bg-blue-700"
                    >
                        {editingEmployee ? "Save Changes" : "Create"}
                    </button>
                    </div>
                </form>

                </div>
            </div>
            )}
        </div>
);

}