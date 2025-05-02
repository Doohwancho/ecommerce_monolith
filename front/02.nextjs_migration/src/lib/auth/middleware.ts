// src/middleware.ts
import { NextResponse } from 'next/server'
import type { NextRequest } from 'next/server'

export function middleware(request: NextRequest) {
  // Get JSESSIONID from cookies
  const sessionCookie = request.cookies.get('JSESSIONID')

  // Check if trying to access protected routes
  if (request.nextUrl.pathname.startsWith('/protected')) {
    if (!sessionCookie) {
      // Redirect to login if no session
      return NextResponse.redirect(new URL('/login', request.url))
    }
  }

  return NextResponse.next()
}

export const config = {
  matcher: [
    // Add routes that require authentication
    '/protected/:path*',
    '/dashboard/:path*',
    // Exclude public routes
    '/((?!api|_next/static|_next/image|favicon.ico|login|register|products|categories).*)'
  ],
}