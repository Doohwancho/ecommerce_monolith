import React from 'react';

const HomeBanner: React.FC = () => {
  return (
    <div className="bg-white py-28">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <h2 className="flex justify-center text-3xl font-extrabold text-gray-900 sm:text-4xl">
          가능성은 지금부터
        </h2>
        <p className="flex justify-center items-center text-center mt-4 text-lg text-gray-500">
          매일 공개되는 새로운 미션에 도전하고 
          <br />
          더 성장한 나를 만들어 줄 다양한 리워드와 제품도 함께 만나보세요
        </p>
        <div className="mt-8 flex justify-center space-x-4">
          <button className="px-6 py-3 border border-transparent text-base font-medium rounded-md text-white bg-gray-800 hover:bg-gray-700">
            미션 참여하기
          </button>
          <button className="px-6 py-3 border border-transparent text-base font-medium rounded-md text-gray-800 bg-gray-200 hover:bg-gray-400">
            혜택성 구매하기
          </button>
        </div>
      </div>
    </div>
  );
};

export default HomeBanner;