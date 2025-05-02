// src/lib/auth/auth.ts
import NextAuth from "next-auth"
import Credentials from "next-auth/providers/credentials"
import type { Session } from "next-auth"

export type AuthConfig = {
  pages: {
    signIn: string
  }
  callbacks: {
    authorized: (params: { auth: any; request: Request }) => Promise<boolean>
    session: (params: { session: Session, token: any }) => Promise<Session>
  }
  providers: any[]
}

export const authConfig: AuthConfig = {
  pages: {
    signIn: '/login',
  },
  callbacks: {
    async authorized({ auth, request: { nextUrl } }) {
      const isLoggedIn = !!auth?.user
      const isProtectedRoute = nextUrl.pathname.startsWith('/protected')
      
      if (isProtectedRoute) {
        if (isLoggedIn) return true
        return false
      }
      return true
    },
    // We only need minimal session handling since Spring handles the real session
    async session({ session }) {
      return session
    }
  },
  providers: [
    Credentials({
      name: 'credentials',
      credentials: {
        username: { label: "Username", type: "text" },
        password: { label: "Password", type: "password" }
      },
      async authorize(credentials) {
        if (!credentials?.username || !credentials?.password) {
          return null
        }

        try {
          const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/login`, {
            method: 'POST',
            headers: {
              'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams({
              username: credentials.username,
              password: credentials.password,
            }),
            credentials: 'include', // Important for JSESSIONID cookie
          })

          if (!response.ok) {
            return null
          }

          // Just return basic user info - Spring Security handles the real session
          return {
            id: credentials.username,
            name: credentials.username,
          }
        } catch (error) {
          console.error('Auth error:', error)
          return null
        }
      }
    })
  ]
}

export const { handlers, auth, signIn, signOut } = NextAuth(authConfig)

// // src/lib/auth/auth.ts
// import NextAuth from "next-auth"
// import CredentialsProvider from "next-auth/providers/credentials"
// import type { NextAuthConfig } from "next-auth"

// export const authConfig: NextAuthConfig = {
//   providers: [
//     CredentialsProvider({
//       name: 'Credentials',
//       credentials: {
//         username: { label: "Username", type: "text" },
//         password: { label: "Password", type: "password" }
//       },
//       async authorize(credentials) {
//         if (!credentials?.username || !credentials?.password) {
//           return null
//         }

//         try {
//           const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/login`, {
//             method: "POST",
//             headers: { "Content-Type": "application/x-www-form-urlencoded" },
//             body: new URLSearchParams({
//               username: credentials.username,
//               password: credentials.password,
//             }),
//             credentials: 'include',
//           })

//           if (!response.ok) {
//             return null
//           }

//           return {
//             id: credentials.username,
//             name: credentials.username,
//           }
//         } catch (error) {
//           console.error('Auth error:', error)
//           return null
//         }
//       }
//     })
//   ],
//   pages: {
//     signIn: '/auth/login',
//   }
// }

// export const { handlers, auth, signIn, signOut } = NextAuth(authConfig)