'use client';

import { Button } from "@/components/atom/button";
import { useRouter } from 'next/navigation';

export function CompletionStep() {
  const router = useRouter();

  return (
    <div className="text-center space-y-6">
      <h3 className="text-2xl font-bold">회원가입 완료</h3>
      <p className="text-gray-600">
        회원가입이 성공적으로 완료되었습니다!
      </p>
      <Button
        onClick={() => router.push('/login')}
        className="w-full"
      >
        시작하기
      </Button>
    </div>
  );
}