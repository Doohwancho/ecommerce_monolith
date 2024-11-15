import { z } from "zod";

// Schema for step 1 - Basic Info
export const basicInfoSchema = z.object({
  username: z.string().min(2, "Username must be at least 2 characters."),
  password: z.string().min(6, "Password must be at least 6 characters."),
  email: z.string().email("Invalid email address."),
  name: z.string().min(2, "Name must be at least 2 characters."),
});

// Schema for step 2 - Address
export const addressSchema = z.object({
  street: z.string().min(1, "Street is required"),
  city: z.string().min(1, "City is required"),
  state: z.string().min(1, "State is required"),
  country: z.string().min(1, "Country is required"),
  zipCode: z.string().min(1, "ZIP code is required"),
});

export type BasicInfoFormData = z.infer<typeof basicInfoSchema>;
export type AddressFormData = z.infer<typeof addressSchema>;