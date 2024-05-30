/**
 * v0 by Vercel.
 * @see https://v0.dev/t/QuMaNFAV1QA
 * Documentation: https://v0.dev/docs#integrating-generated-code-into-your-nextjs-app
 */
import Link from "next/link";
import { Input } from "@/components/atom/input";
import { Button } from "@/components/atom/button";
import { SiNike } from "react-icons/si";

export default function Footer() {
  return (
    <footer className="bg-black text-white w-full">
      <div className="container mx-auto py-12 px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 gap-8 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4">
          <div>
            <Link className="flex items-center" href="#">
              <SiNike className="size-8" />
              <span className="ml-2 text-xl font-bold">Nike</span>
            </Link>
          </div>
          <nav>
            <h4 className="text-lg font-bold mb-4">Navigation</h4>
            <ul className="space-y-2">
              <li>
                <Link className="hover:underline" href="#">
                  Home
                </Link>
              </li>
              <li>
                <Link className="hover:underline" href="#">
                  Shop
                </Link>
              </li>
              <li>
                <Link className="hover:underline" href="#">
                  About
                </Link>
              </li>
              <li>
                <Link className="hover:underline" href="#">
                  Contact
                </Link>
              </li>
            </ul>
          </nav>
          <div>
            <h4 className="text-lg font-bold mb-4">Newsletter</h4>
            <p className="mb-4">
              Sign up for our up-to-date latest products and offers.
            </p>
            <form className="flex">
              <Input
                className="flex-1 bg-gray-800 border-none focus:ring-0 focus:border-none"
                placeholder="Enter your email"
                type="email"
              />
              <Button
                className="bg-blue-500 hover:bg-blue-600 text-white"
                type="submit"
              >
                Subscribe
              </Button>
            </form>
          </div>
          <div>
            <h4 className="text-lg font-bold mb-4">Follow Us</h4>
            <div className="flex space-x-4">
              <Link className="text-gray-400 hover:text-white" href="#">
                <TwitterIcon className="h-6 w-6" />
              </Link>
              <Link className="text-gray-400 hover:text-white" href="#">
                <FacebookIcon className="h-6 w-6" />
              </Link>
              <Link className="text-gray-400 hover:text-white" href="#">
                <InstagramIcon className="h-6 w-6" />
              </Link>
            </div>
          </div>
        </div>
        <div className="mt-8 border-t border-gray-700 pt-8 text-sm text-gray-400">
          <p>© 2024 Acme Store. All rights reserved.</p>
        </div>
      </div>
    </footer>
  );
}

function FacebookIcon(props) {
  return (
    <svg
      {...props}
      xmlns="http://www.w3.org/2000/svg"
      width="24"
      height="24"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    >
      <path d="M18 2h-3a5 5 0 0 0-5 5v3H7v4h3v8h4v-8h3l1-4h-4V7a1 1 0 0 1 1-1h3z" />
    </svg>
  );
}

function InstagramIcon(props) {
  return (
    <svg
      {...props}
      xmlns="http://www.w3.org/2000/svg"
      width="24"
      height="24"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    >
      <rect width="20" height="20" x="2" y="2" rx="5" ry="5" />
      <path d="M16 11.37A4 4 0 1 1 12.63 8 4 4 0 0 1 16 11.37z" />
      <line x1="17.5" x2="17.51" y1="6.5" y2="6.5" />
    </svg>
  );
}

function MountainIcon(props) {
  return (
    <svg
      {...props}
      xmlns="http://www.w3.org/2000/svg"
      width="24"
      height="24"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    >
      <path d="m8 3 4 8 5-5 5 15H2L8 3z" />
    </svg>
  );
}

function TwitterIcon(props) {
  return (
    <svg
      {...props}
      xmlns="http://www.w3.org/2000/svg"
      width="24"
      height="24"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    >
      <path d="M22 4s-.7 2.1-2 3.4c1.6 10-9.4 17.3-18 11.6 2.2.1 4.4-.6 6-2C3 15.5.5 9.6 3 5c2.2 2.6 5.6 4.1 9 4-.9-4.2 4-6.6 7-3.8 1.1 0 3-1.2 3-1.2z" />
    </svg>
  );
}
