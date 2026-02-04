"use client";

import Link from "next/link";
import { useAuth } from "@/lib/useAuth";
import { Button } from "@/components/ui/button";
import { motion, AnimatePresence } from "framer-motion";
import { User, LogIn, CloudLightning, LogOut, ChevronDown } from "lucide-react";
import { useState, useRef, useEffect } from "react";

export function Navbar() {
    const { user, isAuthenticated, loading, logout } = useAuth();
    const [showMenu, setShowMenu] = useState(false);
    const menuRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
                setShowMenu(false);
            }
        };

        if (showMenu) {
            document.addEventListener("mousedown", handleClickOutside);
        }
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, [showMenu]);

    return (
        <nav className="fixed top-0 left-0 right-0 z-50 px-6 py-4">
            <div className="max-w-7xl mx-auto flex items-center justify-between glass border-white/20 px-6 py-3 rounded-2xl shadow-xl">
                {/* Logo */}
                <Link href="/" className="flex items-center gap-2 group">
                    <div className="p-2 rounded-xl bg-primary/20 text-primary group-hover:bg-primary group-hover:text-white transition-all duration-300">
                        <CloudLightning className="h-6 w-6" />
                    </div>
                    <span className="text-xl font-black crystal-text tracking-tighter">
                        Crystal Weather
                    </span>
                </Link>

                {/* Auth Section */}
                <div className="flex items-center gap-4">
                    <AnimatePresence mode="wait">
                        {loading ? (
                            <motion.div
                                key="loading"
                                initial={{ opacity: 0 }}
                                animate={{ opacity: 1 }}
                                exit={{ opacity: 0 }}
                                className="h-10 w-24 bg-white/10 animate-pulse rounded-xl"
                            />
                        ) : isAuthenticated && user ? (
                            <div className="relative" ref={menuRef}>
                                <motion.div
                                    key="user"
                                    initial={{ opacity: 0, x: 10 }}
                                    animate={{ opacity: 1, x: 0 }}
                                    exit={{ opacity: 0, x: 10 }}
                                    onClick={() => setShowMenu(!showMenu)}
                                    className={`flex items-center gap-3 pl-3 pr-2 py-1 rounded-full border transition-all cursor-pointer group ${showMenu ? 'bg-white/20 border-white/30' : 'bg-white/10 border-white/10 hover:bg-white/15'}`}
                                >
                                    <div className="text-right hidden sm:block">
                                        <p className="text-xs font-black truncate max-w-[120px]">
                                            {user.name}
                                        </p>
                                        <p className="text-[10px] text-foreground/40 font-bold truncate max-w-[120px]">
                                            {user.email}
                                        </p>
                                    </div>
                                    <div className="relative">
                                        {user.picture ? (
                                            <img
                                                src={user.picture}
                                                alt={user.name}
                                                className="h-8 w-8 rounded-full border border-white/20 shadow-lg object-cover"
                                            />
                                        ) : (
                                            <div className="h-8 w-8 rounded-full bg-primary/20 flex items-center justify-center border border-white/20">
                                                <User className="h-4 w-4 text-primary" />
                                            </div>
                                        )}
                                        <div className={`absolute -bottom-1 -right-1 bg-white/20 rounded-full border border-white/40 p-0.5 transition-transform duration-300 ${showMenu ? 'rotate-180' : ''}`}>
                                            <ChevronDown className="h-2 w-2" />
                                        </div>
                                    </div>
                                </motion.div>

                                <AnimatePresence>
                                    {showMenu && (
                                        <motion.div
                                            initial={{ opacity: 0, y: 10, scale: 0.95 }}
                                            animate={{ opacity: 1, y: 0, scale: 1 }}
                                            exit={{ opacity: 0, y: 10, scale: 0.95 }}
                                            className="absolute right-0 mt-2 w-56 glass border-white/20 rounded-2xl shadow-2xl overflow-hidden p-2 bg-white/10 backdrop-blur-xl z-[60]"
                                        >
                                            <div className="p-3 text-center border-b border-white/10 mb-2">
                                                <p className="text-sm font-black">{user.name}</p>
                                                <p className="text-[10px] text-foreground/40 font-bold truncate">{user.email}</p>
                                            </div>
                                            <button
                                                onClick={logout}
                                                className="w-full flex items-center gap-3 px-4 py-3 text-sm font-bold text-red-400 hover:bg-red-400/10 rounded-xl transition-all group"
                                            >
                                                <div className="p-1.5 rounded-lg bg-red-400/10 group-hover:bg-red-400 group-hover:text-white transition-all">
                                                    <LogOut className="h-4 w-4" />
                                                </div>
                                                Logout
                                            </button>
                                        </motion.div>
                                    )}
                                </AnimatePresence>
                            </div>
                        ) : (
                            <motion.div
                                key="login"
                                initial={{ opacity: 0, scale: 0.9 }}
                                animate={{ opacity: 1, scale: 1 }}
                                exit={{ opacity: 0, scale: 0.9 }}
                            >
                                <Button
                                    asChild
                                    variant="ghost"
                                    className="h-10 px-6 rounded-xl font-bold bg-primary/10 hover:bg-primary/20 text-primary gap-2 transition-all active:scale-95 border border-primary/20"
                                >
                                    <Link href="/login">
                                        <LogIn className="h-4 w-4" />
                                        Login
                                    </Link>
                                </Button>
                            </motion.div>
                        )}
                    </AnimatePresence>
                </div>
            </div>
        </nav>
    );
}
