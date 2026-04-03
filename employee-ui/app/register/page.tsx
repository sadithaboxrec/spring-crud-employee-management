"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { registerApi, Role } from "@/lib/api";

export default function RegisterPage() {
    const router = useRouter();

    const [form, setForm] = useState({
        fullName: "",
        username: "",
        password: "",
        role: "ROLE_MANAGER" as Role,        
    });
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    // Generic handler: updates whichever field changed
    function handleChange(e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) {
        setForm({ ...form, [e.target.name]: e.target.value });
    }

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();
        setError("");
        setLoading(true);

        try {
            await registerApi(form);
         //   router.push("/login");
            router.push("/login?registered=true");// go to login after registration
        } catch (err) {
            setError("Registration failed. Please try again.");
        } finally {
            setLoading(false);
        }
    }


return (
        <div className="min-h-screen flex flex-col items-center justify-center bg-gradient-to-br from-blue-600 via-indigo-600 to-purple-600 px-4">


            <h1 className="text-4xl font-extrabold text-white mb-8 tracking-wide text-center">
            Employee Management System
            </h1>

  
            <div className="bg-white p-8 rounded-2xl shadow-xl w-full max-w-sm">

            <h1 className="text-2xl font-bold mb-6 text-center text-gray-800">
                Register
            </h1>

            {error && (
                <div className="bg-red-100 text-red-700 p-3 rounded mb-4 text-sm">
                {error}
                </div>
            )}

            <form onSubmit={handleSubmit} className="space-y-4" id="registerForm">

                
                <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                    Full Name
                </label>
                <input
                    name="fullName"
                    type="text"
                    value={form.fullName}
                    onChange={handleChange}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                    required
                />
                </div>

                {/* Username */}
                <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                    Username
                </label>
                <input
                    name="username"
                    type="text"
                    value={form.username}
                    onChange={handleChange}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                    required
                />
                </div>

                
                <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                    Password
                </label>
                <input
                    name="password"
                    type="password"
                    value={form.password}
                    onChange={handleChange}  
                    className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                    required
                />
                </div>

                
                <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                    Role
                </label>
                <select
                    name="role"
                    value={form.role}
                    onChange={handleChange}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                    <option value="ROLE_ADMIN">Admin</option>
                    <option value="ROLE_MANAGER">Manager</option>
                </select>
                </div>

                <button
                type="submit"
                disabled={loading}
                className="w-full bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 disabled:opacity-50 text-sm font-medium transition"
                >
                {loading ? "Registering..." : "Register"}
                </button>
            </form>

            <p className="text-center text-sm text-gray-500 mt-4">
                Already have an account?{" "}
                <Link href="/login" className="text-blue-600 hover:underline">
                Sign In
                </Link>
            </p>
            </div>
        </div>
);


    
}