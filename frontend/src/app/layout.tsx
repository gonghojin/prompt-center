import type { Metadata } from 'next'
import { Inter } from 'next/font/google'
import './globals.css'

const inter = Inter({ subsets: ['latin'] })

export const metadata: Metadata = {
  title: 'Prompt Center',
  description: '프롬프트 템플릿 중앙화 서버',
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="ko" className="h-full">
      <body className={`${inter.className} h-full`}>
        <div className="min-h-full">
          {children}
        </div>
      </body>
    </html>
  )
} 