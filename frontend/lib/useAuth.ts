import { useEffect, useState } from "react";

type User = {
    email: string;
    name: string;
    picture: string;
};

export function useAuth() {
    const [user, setUser] = useState<User | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetch(`${process.env.NEXT_PUBLIC_API_BASE_URL}/auth/me`, {
            credentials: "include", // 🔑 REQUIRED for HttpOnly cookie
        })
            .then((res) => {
                if (!res.ok) throw new Error("Not authenticated");
                return res.json();
            })
            .then((data) => {
                setUser(data);
            })
            .catch(() => {
                setUser(null);
            })
            .finally(() => {
                setLoading(false);
            });
    }, []);

    const logout = () => {
        window.location.href = `${process.env.NEXT_PUBLIC_API_BASE_URL}/auth/logout`;
    };

    return {
        user,
        loading,
        isAuthenticated: !!user,
        logout,
    };
}
