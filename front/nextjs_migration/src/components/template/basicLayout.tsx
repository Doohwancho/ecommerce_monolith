import React from 'react';

interface LayoutProps {
  navbar: React.ReactNode;
  main: React.ReactNode;
  footer: React.ReactNode;
}

const BasicLayout: React.FC<LayoutProps> = ({ navbar, main, footer }) => {
  return (
    <div className="min-h-[100vh] grid grid-rows-[auto_1fr_auto]">
      {/* <div className="bg-gray-200">{navbar}</div> */}
      <main className="bg-white">{main}</main>
      <footer className="bg-gray-200">{footer}</footer>
    </div>
  );
};

export default BasicLayout;