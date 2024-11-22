'use client';
import React, { useEffect, useContext } from 'react';
import { useRouter } from 'next/navigation';
import { AuthContext } from '../context/AuthContext';

export default function HomePage() {
  const auth = useContext(AuthContext);
  const router = useRouter();

  useEffect(() => {
    if (auth?.user) {
      if (auth.user.role === 'admin') {
        router.push('/admin');
      } else {
        router.push('/user');
      }
    } else {
      router.push('/login');
    }
  }, [auth, router]);

  return null; // Or a loading indicator
}
