// app/login/page.tsx
"use client";                            // this makes it a Client Component (can use useState, events)

import { useState } from "react";
import {useRouter, useSearchParams} from "next/navigation";
import Link from "next/link";
import { loginApi } from "@/lib/api";
import { saveTokens } from "@/lib/auth";

export default function LoginPage() {
    const router = useRouter();

    const searchParams = useSearchParams();
    const justRegistered = searchParams.get("registered");

    // Form state
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();                  // prevent page reload
        setError("");
        setLoading(true);

        try {
            const data = await loginApi({ username, password });

            // Save tokens to localStorage
            saveTokens(data.accessToken, data.refreshToken);

            // Also save to cookie so middleware can read it
            document.cookie = `accessToken=${data.accessToken}; path=/`;

            router.push("/dashboard");         // redirect to dashboard
        } catch (err) {
            setError("Invalid username or password.");
        } finally {
            setLoading(false);
        }
    }





    return (
        <div className="min-h-screen flex flex-col items-center justify-center bg-gradient-to-br from-blue-600 via-indigo-600 to-purple-600 px-4">
            
            {/* App Title */}
            <h1 className="text-4xl font-extrabold text-white mb-8 tracking-wide text-center">
            Employee Management System
            </h1>

            {/* Card */}
            <div className="bg-white/90 backdrop-blur-md p-8 rounded-2xl shadow-2xl w-full max-w-sm">
            
            <h2 className="text-2xl font-bold mb-6 text-center text-gray-800">
                Sign In
            </h2>

            {/* Success banner */}
            {justRegistered && (
                <div className="bg-green-100 text-green-700 p-3 rounded-lg mb-4 text-sm border border-green-200">
                Registration successful! Please sign in.
                </div>
            )}

            {/* Error */}
            {error && (
                <div className="bg-red-100 text-red-700 p-3 rounded-lg mb-4 text-sm border border-red-200">
                {error}
                </div>
            )}

            <form onSubmit={handleSubmit} className="space-y-5" id="loginForm">
                
                <div>
                <label className="block text-sm font-medium text-gray-600 mb-1">
                    Username
                </label>
                <input
                    type="text"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none transition"
                    required
                />
                </div>

                <div>
                <label className="block text-sm font-medium text-gray-600 mb-1">
                    Password
                </label>
                <input
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none transition"
                    required
                />
                </div>

                <button
                type="submit"
                disabled={loading}
                className="w-full bg-gradient-to-r from-blue-600 to-indigo-600 text-white py-2 rounded-lg hover:opacity-90 disabled:opacity-50 text-sm font-semibold transition"
                >
                {loading ? "Signing in..." : "Sign In"}
                </button>
            </form>

            <p className="text-center text-sm text-gray-500 mt-5">
                Don't have an account?{" "}
                <Link href="/register" className="text-blue-600 font-medium hover:underline">
                Register
                </Link>
            </p>
            </div>
        </div>
);



}