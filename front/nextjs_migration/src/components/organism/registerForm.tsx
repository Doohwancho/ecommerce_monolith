"use client";

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";

import { Button } from "@/components/atom/button";
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/molecule/form";
import { Input } from "@/components/atom/input";
import { RegisterRequestDTO, RegisterResponseDTO } from '../../../models';

const formSchema = z.object({
  username: z.string().min(2, {
    message: "Username must be at least 2 characters.",
  }),
  password: z.string().min(6, {
    message: "Password must be at least 6 characters.",
  }),
});

export function RegisterForm() {
  const router = useRouter();
  const [error, setError] = useState<string | null>(null);

  // 1. Define your form.
  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      username: "",
      password: "",
    },
  });

  // 2. Define a submit handler.
  async function onSubmit(values: z.infer<typeof formSchema>) {
    try {
      const registerData: RegisterRequestDTO = {
        username: values.username,
        password: values.password,
        email: values.username+'@gmail.com', 
        name: 'name-'+values.username, 
        address: {
          street: 'empty',
          city: 'empty',
          state: 'empty',
          country: 'empty',
          zipCode: '12345'
        }
      };

      const BASE_URL = process.env.NEXT_PUBLIC_API_URL;
      const endpoint = `/register`;
      const fullUrl = BASE_URL + endpoint;

      const response = await fetch(fullUrl, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(registerData),
      });

      if (!response.ok) {
        throw new Error('Registration failed');
      }

      const data: RegisterResponseDTO = await response.json();
      console.log(data.message);

      // Redirect to home page
      router.push('/');
    } catch (err) {
      setError('Registration failed. Please try again.');
      console.error(err);
    }
  }

  return (
    <div className="flex py-8 justify-center min-h-screen">
      <Form {...form}>
        <form
          onSubmit={form.handleSubmit(onSubmit)}
          className="space-y-8 bg-white p-4 rounded-md w-96"
        >
          <h2 className="text-2xl font-bold">Welcome to Nike</h2>
          <FormField
            control={form.control}
            name="username"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Username</FormLabel>
                <FormControl>
                  <Input placeholder="Enter your username" {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          <FormField
            control={form.control}
            name="password"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Password</FormLabel>
                <FormControl>
                  <Input type="password" placeholder="Enter your password" {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          {error && <p className="text-red-500">{error}</p>}
          <Button type="submit" className="w-full">
            Register
          </Button>
        </form>
      </Form>
    </div>
  );
}
